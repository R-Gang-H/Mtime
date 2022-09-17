package com.mtime.payment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.kotlin.android.film.JavaOpenSeatActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.activity.NewCinemaShowtimeActivity;
import com.mtime.bussiness.ticket.movie.activity.SeatSelectActivity;
import com.mtime.common.utils.LogWriter;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.UAUtils;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

/**
 * 选择银行卡支付，银行卡支付用的是支付宝的一项功能（并不是支付宝的网页版）
 * 银行卡支付合同已经到期了，所以WapPayActivity没有入口（目前是银联支付）
 */
public class WapPayActivity extends BaseActivity {
    private WebView      contentView = null;
    private String       url         = "";
    private ProgressBar  mProgressBar;
    private String       statusUrl;
    private boolean      isMovieCard = false;
    // TODO 待h5修改部署后再调整,直接改为:https://xxx即可。
    private final String URL_TITLE   = "https://m.mtime.cn/";
    private String       title;
    
    private class GetData extends AsyncTask<Object, Object, Object> {
        public GetData() {
            super();
        }
        
        @Override
        protected void onPostExecute(final Object result) {
            /*
             * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
             */
            contentView.loadUrl(url);
            super.onPostExecute(result);
        }
        
        @Override
        protected void onPreExecute() {
            /*
             * 执行预处理，它运行于UI线程，可以为后台任务做一些准备工作，比如绘制一个进度条控件
             */
            mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }
        
        @Override
        protected Object doInBackground(final Object... params) {
            return null;
        }
    }
    
    /**
     * 关闭进度条
     */
    Handler mHandler = new Handler() {
                         @SuppressLint("SetJavaScriptEnabled")
                        @Override
                         public void handleMessage(final Message msg) {
                             switch (msg.what) {
                                 case 0:
                                     // 关闭进度条
                                     mProgressBar.setVisibility(View.GONE);
                                     break;
                                 
                                 case 1:
                                     contentView.setWebViewClient(new DownLoadWebViewClient());
                                     contentView.setWebChromeClient(new WebChromeClient() {

                                         @Override
                                         public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                                             result.confirm();
                                             return true;
                                         }

                                         @Override
                                         public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                                             result.confirm();
                                             return true;
                                         }
                                         
                                     });
                                     contentView.getSettings().setJavaScriptEnabled(true);
                                     contentView.getSettings().setSupportZoom(true);
                                     contentView.getSettings().setBuiltInZoomControls(true);
                                     // contentView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                                     new GetData().execute();
                                     break;
                             }
                             
                         }
    };
    
    public class DownLoadWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            if (ToolsUtils.isVisit(url)) {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (null != view && null != request && null != request.getUrl()) {
                    String url = request.getUrl().toString();
                    if (ToolsUtils.isVisit(url)) {
                        return super.shouldInterceptRequest(view, request);
                    }
                }
            }
            return null;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (null != view  && !TextUtils.isEmpty(url) && ToolsUtils.isVisit(url)) {
                return super.shouldInterceptRequest(view, url);
            }

            return null;
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            LogWriter.d("跳转url--->" + url);
            if (isMovieCard) {
                // http://m.mtime.cn/#!/onlineticket/result/
                if (url != null && url.length() > 0) {
                    if (url.startsWith(URL_TITLE)) {
                        String subUrl = url.substring(URL_TITLE.length());
                        if (subUrl != null && subUrl.length() > 0) {
                            if (subUrl.contains("/")) {
                                int index = subUrl.indexOf("/");
                                // http://m.mtime.cn/#!/onlineticket/result/
                                String subIndexUrl = subUrl.substring(index);
                                if (subIndexUrl.contains("onlineticket/result/")) {
                                    new Handler().postDelayed(new Runnable() {
                                        
                                        @Override
                                        public void run() {
                                            WapPayActivity.this.setResult(6);
                                            finish();
                                        }
                                    }, 200);
                                }
                                // http://m.mtime.cn/#!/my/account/membercard/
                                else if (subIndexUrl.contains("my/account/membercard/")) {
//                                    WapPayActivity.this.startActivity(MemberCardListActivity.class);
                                    JumpUtil.startMemberCardListActivity(WapPayActivity.this);
                                    finish();
                                }
                                // http://m.mtime.cn/#!/theater/290/1478/date/
                                else if (subIndexUrl.contains("/theater/")) {
                                    // 290/1478/date/
                                    String cinemaIdUrl = subIndexUrl.substring("/theater/".length());
                                    if (cinemaIdUrl != null && cinemaIdUrl.length() > 0) {
                                        int indexId = cinemaIdUrl.indexOf("/");
                                        if (indexId > 0) {
                                            // 1478/date/
                                            String subcinemaId = cinemaIdUrl.substring(indexId + 1);
                                            int subindex = subcinemaId.indexOf("/");
                                            if (subindex != 0) {
                                                String cinemaId = subcinemaId.substring(0, subindex);
                                                if (cinemaId != null && cinemaId.length() > 0) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra(App.getInstance().KEY_CINEMA_ID, cinemaId);
                                                    startActivity(NewCinemaShowtimeActivity.class, intent);
                                                    finish();
                                                }
                                                
                                            }
                                        }
                                        
                                    }
                                    
                                }
                                // http://m.mtime.cn/#!/onlineticket/pay/订单号/
                                else if (subIndexUrl.contains("onlineticket/pay/")) {
                                    finish();
                                }
                                else if (subIndexUrl.contains("/onlineticket/")) {
                                    String showTimeId = subIndexUrl.substring("/onlineticket/".length());
                                    
                                    int indexId = showTimeId.indexOf("/");
                                    if (indexId > 0) {
                                        String subSHowTimeId = showTimeId.substring(0, indexId);
                                        try {
                                            int id = Integer.parseInt(subSHowTimeId);
                                            if (id > 0) {
                                                Intent intent = getIntent();
                                                boolean isfromAccount=intent.getBooleanExtra(App.getInstance().KEY_ISFROM_ACCOUNT, false);
                                                if (isfromAccount) {
                                                    WapPayActivity.this.setResult(7);
                                                    finish();
                                                }else {
                                                    intent.putExtra(App.getInstance().KEY_SEATING_DID, subSHowTimeId);
//                                                    mDId = intent.getStringExtra(Constant.KEY_SEATING_DID);
//                                                    startActivity(SeatSelectActivity.class, intent);

                                                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(subSHowTimeId, null, null, null, null);
                                                 finish();
                                                }
                                              
                                            }
                                        }
                                        catch (NumberFormatException e) {
                                        }
                                    }
                                    
                                }
                            }
                        }
                        
                    }
                }
            }
            else {
                if (url.startsWith(App.getInstance().API_ADDRESS_ALIWAP_PAY)) {
                    statusUrl = url;
                    new Handler().postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra(App.getInstance().WAP_PAY_URL, statusUrl);
                            WapPayActivity.this.setResult(0, intent);
                            finish();
                        }
                    }, 200);
                }
                else if (url.startsWith(App.getInstance().API_ADDRESS_WAPPAY_RETURN)) {
                    statusUrl = url;
                    new Handler().postDelayed(new Runnable() {
                        
                        @Override
                        public void run() {
                            Intent intent = new Intent();
                            intent.putExtra(App.getInstance().WAP_PAY_URL, statusUrl);
                            WapPayActivity.this.setResult(0, intent);
                            finish();
                        }
                    }, 200);
                }
            }
            
            mHandler.sendEmptyMessage(0);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }
    }
    
    @Override
    protected void onInitEvent() {
    }
    
    @Override
    protected void onInitVariable() {
        title = getIntent().getStringExtra(App.getInstance().KEY_WEBVIEW_TITLE_NAME);
        url = getIntent().getStringExtra(App.getInstance().WAP_PAY_URL);
        isMovieCard = getIntent().getBooleanExtra(App.getInstance().MOVIE_CARD_PAY, false);
        setPageLabel("wapPay");
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        
        this.setContentView(R.layout.act_wappay);


        // 内容
        contentView = findViewById(R.id.wv_share_content);
        contentView.setInitialScale(60);
        UAUtils.changeWebViewUA(contentView);
        // 设置内容
        
        statusUrl = url;
        // 下载等待进度条
        mProgressBar = findViewById(R.id.pb_list);
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessageDelayed(1, 1000);
        
        String titleString = "";
        if ((title != null) && !"".equals(title)) {
            titleString = title;
        }
        
        View navbar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navbar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, titleString, new ITitleViewLActListener() {
            
            @Override
            public void onEvent(ActionType type, String content) {
                if (ActionType.TYPE_BACK == type) {
                    if (isMovieCard) {
                        WapPayActivity.this.setResult(6);
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(App.getInstance().WAP_PAY_URL, statusUrl);
                        WapPayActivity.this.setResult(0, intent);
                    }
                }
            }
        });
        
    }
    
    @Override
    protected void onLoadData() {
    }
    
    @Override
    protected void onRequestData() {
    }
    
    @Override
    protected void onUnloadData() {
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // if (contentView.canGoBack()) {
            // contentView.goBack();
            // return true;
            // }
            // else {
            if (isMovieCard) {
                WapPayActivity.this.setResult(6);
                finish();
            }
            else {
                Intent intent = new Intent();
                intent.putExtra(App.getInstance().WAP_PAY_URL, statusUrl);
                WapPayActivity.this.setResult(0, intent);
                finish();
            }
            return true;
            // }
        }
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (contentView != null) {
            ViewParent parent = contentView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(contentView);
            }
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            contentView.getSettings().setJavaScriptEnabled(false);
            contentView.getSettings().setSupportZoom(false);
            contentView.getSettings().setBuiltInZoomControls(false);
            contentView.clearHistory();
            contentView.removeAllViews();
            contentView.destroy();
            contentView = null;
        }
        super.onDestroy();
    }
}
