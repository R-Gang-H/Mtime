package com.mtime.mtmovie.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.applink.ApplinkManager;
import com.mtime.beans.ADDetailBean;
import com.mtime.frame.App;
import com.mtime.network.UAUtils;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.util.MTWebChromeClient;
import com.mtime.util.ToolsUtils;

import java.util.Calendar;
import java.util.TimeZone;

@SuppressLint("SetJavaScriptEnabled")
public class ADWebView extends WebView {

    //之前goto使用，改为applink后将不再使用
    /*public enum OpenType {
        TYPE_H5, TYPE_NATIVE
    }*/

    private static final long ZOOM_CONTROLS_TIMEOUT =
            ViewConfiguration.getZoomControlsTimeout();

    private AbsoluteLayout.LayoutParams params;
    private TextView tagView;
    private String tagValue;
    private final Context context;
    private int height;
    private int tagHeight;
    private String mReferer;
    private OnAdItemClickListenner mOnAdItemClickListenner; //广告点击事件监听

    public ADWebView(Context context) {
        super(context);
        this.context = context;
        initTagView();
    }

    public void setHeight(int height) {
///        this.height = height;
    }

    private void initTagView() {
        tagView = new TextView(context);
        tagView.setPadding(6, 3, 6, 2);
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        tagView.setBackgroundResource(R.drawable.ad_tag);
        tagView.setShadowLayer(5, 0, 0, ContextCompat.getColor(context, R.color.black_color));
        tagView.setTextColor(ContextCompat.getColor(context, R.color.white));
        params = new AbsoluteLayout.LayoutParams(
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                AbsoluteLayout.LayoutParams.WRAP_CONTENT,
                6, getMeasuredHeight() - tagView.getMeasuredHeight() - 6);
        addView(tagView, params);

        tagView.setVisibility(GONE);
    }

    private void drawTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            tagValue = tag;
        }
        if (TextUtils.isEmpty(tagValue)) {
            tagView.setVisibility(GONE);
            return;
        }
        height = getMeasuredHeight();
        tagHeight = tagView.getMeasuredHeight();
        tagView.setVisibility(VISIBLE);
        params.x = 6;
        params.y = height - tagHeight - 10;
        tagView.setText(tagValue);
    }

    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            drawTag(null);
        }
    }


    public ADWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initTagView();
    }

    public void load(final Context activity, final ADDetailBean item) {
        final String url = item.getUrl();
        getSettings().setJavaScriptEnabled(true);
        UAUtils.changeWebViewUA(this);

        this.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //https://applink.mtime.cn/?applinkData=XXXXXX
                if (!TextUtils.isEmpty(url) && url.startsWith("https://applink.mtime.cn/?applinkData=")) {
                    try {
                        String applinkData = url.substring(url.indexOf('=') + 1);
                        if (null != mOnAdItemClickListenner) {
                            mOnAdItemClickListenner.onAdItemClick(item, url);
                        }
                        String referer = TextUtils.isEmpty(mReferer) ? StatisticManager.getH5Refer(view.getUrl()) : mReferer;
                        ApplinkManager.jump4H5(activity, applinkData, referer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                if (null != view && !TextUtils.isEmpty(url) && ToolsUtils.isVisit(url)) {
                    return super.shouldInterceptRequest(view, url);
                }

                return null;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        MTWebChromeClient mtWebChromeClient = new MTWebChromeClient(activity);
        this.setWebChromeClient(mtWebChromeClient);

        this.loadUrl(url);
        if (0 == tagHeight) {
            ViewTreeObserver vto = tagView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    drawTag(item.getAdvTag());
                    if (tagHeight != 0) {
                        tagView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        } else {
            drawTag(item.getAdvTag());
        }

    }

    public void setAdReferer(String referer) {
        mReferer = referer;
    }

    public static boolean show(ADDetailBean item) {
        if (null == item || item.isEmpty()) {
            return false;
        }

        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        long totalMilliseconds = System.currentTimeMillis() + timeZone.getRawOffset();

        if (item.getType().equalsIgnoreCase(App.getInstance().AD_START_APP)) {
            totalMilliseconds -= timeZone.getRawOffset();
        }

        long time = totalMilliseconds / 1000;
        int start = item.getStartDate();
        int end = item.getEndDate();
        return time <= end && time >= start && !TextUtils.isEmpty(item.getUrl());
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    /**
     * 设置广告点击事件监听
     *
     * @param onAdItemClickListenner
     */
    public void setOnAdItemClickListenner(OnAdItemClickListenner onAdItemClickListenner) {
        mOnAdItemClickListenner = onAdItemClickListenner;
    }

    public interface OnAdItemClickListenner {
        void onAdItemClick(ADDetailBean item, String url);
    }
}
