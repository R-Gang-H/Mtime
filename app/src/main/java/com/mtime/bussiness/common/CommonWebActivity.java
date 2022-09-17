package com.mtime.bussiness.common;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.jssdk.JSCenter;
import com.jssdk.beans.CommonBean;
import com.jssdk.beans.HandleGoBackBean;
import com.jssdk.beans.HandleTelBean;
import com.jssdk.beans.NavBarStatusBean;
import com.jssdk.beans.NewShowShareBean;
import com.jssdk.beans.OpenAppLinkBean;
import com.jssdk.beans.OpenImageBrowser;
import com.jssdk.beans.ShowBirthdayLevelPopPageBean;
import com.jssdk.beans.ShowShareBean;
import com.jssdk.beans.ShowVideoPlayerBean;
import com.jssdk.beans.calljs.JSCallbackBean;
import com.jssdk.beans.calljs.NativeNetStatusBean;
import com.jssdk.listener.JSTotalListener;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.share.SharePlatform;
import com.kotlin.android.share.ShareType;
import com.kotlin.android.share.entity.ShareEntity;
import com.kotlin.android.share.ext.ShareExtKt;
import com.kotlin.android.share.ui.ShareFragment;
import com.mtime.applink.ApplinkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.widget.Browser;
import com.mtime.bussiness.mine.widget.MemberCenterPopup;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.OnShakeListener;
import com.mtime.common.utils.ShakeDetector;
import com.mtime.common.utils.Utils;
import com.mtime.share.ShareExtJava;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.article.StatisticArticle;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mylhyl.acp.Acp;
import com.mylhyl.acp.AcpListener;
import com.mylhyl.acp.AcpOptions;
import com.tencent.smtt.sdk.ValueCallback;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_IS_INTERCEPT_PRIVATE_URL;
import static com.kotlin.android.ktx.ext.KeyExtKt.KEY_WEB_OF_IS_INTERCEPT_URL;

/**
 * 公共的WebView H5页面；
 * 支持jssdk，applink, Web拦截(goto协议)
 * <p>
 * HorizontalWebActivity  WebViewActivity  AdvRecommendActivity  ShareDetailActivity
 */
@Route(path = RouterActivityPath.AppUser.PAGE_COMMON_WEBACTIVITY)
public class CommonWebActivity extends AbsWebViewActivity implements JSTotalListener {
    private static final String KEY_OF_IS_INTERCEPT_URL = KEY_WEB_OF_IS_INTERCEPT_URL;
    private static final String KEY_OF_IS_INTERCEPT_PRIVATE_URL = KEY_WEB_OF_IS_INTERCEPT_PRIVATE_URL;

    /**
     * @param context               上下文
     * @param url                   地址
     * @param pageLabel             页面标识（用于埋点统计）
     * @param adTag                 广告tag
     * @param isShowTitleBar        是否显示title
     * @param isShowBack            是否显示返回按钮
     * @param isShowClose           是否显示关闭按钮
     * @param isHorizontalScreen    是否是横屏模式
     * @param isInterceptUrl        是否拦截地址（后台配置的白名单）
     * @param isInterceptPrivateUrl 是否拦截私有协议的地址，如youku://
     * @param refer                 页面访问路径（用于埋点统计）
     */
    public static void launch(Context context, String url, String pageLabel, String adTag,
                              boolean isShowTitleBar, boolean isShowBack, boolean isShowClose,
                              boolean isHorizontalScreen, boolean isInterceptUrl,
                              boolean isInterceptPrivateUrl, String refer) {
        Intent intent = new Intent(context, CommonWebActivity.class);
        // load url 也在 父类处理，如果需要拦截 url ，则重写 setData() 或者 重写 onInterceptUrlLoading 并返回 ture
        intent.putExtra(KEY_OF_URL, url);
        intent.putExtra(KEY_OF_PAGE_LABEL, pageLabel);
        intent.putExtra(KEY_OF_AD_TAG, adTag);
        intent.putExtra(KEY_OF_SHOW_TITLE, isShowTitleBar);
        intent.putExtra(KEY_OF_SHOW_BACK, isShowBack);
        intent.putExtra(KEY_OF_SHOW_CLOSE, isShowClose);
        intent.putExtra(KEY_OF_IS_HORIZONTAL_SCREEN, isHorizontalScreen);
        intent.putExtra(KEY_OF_IS_INTERCEPT_URL, isInterceptUrl);
        intent.putExtra(KEY_OF_IS_INTERCEPT_PRIVATE_URL, isInterceptPrivateUrl);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }

    private JSCenter mJSCenter;
    private NavBarStatusBean.Data.ShareContentBean mShareContentBean;
    private boolean mIsInterceptUrl;
    /**
     * 是否拦截私有的url协议，如youku://...
     */
    private boolean mIsInterceptPrivateUrl;

    //=====之前AdvRecommendActivity使用，可能用于富媒体广告，后期如果富媒体广告不使用可以干掉===============
    private ShakeDetector mShakeDetector; // 网页摇动监测
    private OnShakeListener mOnShakeListener;
    private final int REQUSET_CODE = 1;
    private final String CHARSET_DEFAULT = "UTF-8";
    private final int REQUEST_CODE_FILE_PICKER = 51426;
    private final String mUploadableFileTypes = "image/*";
    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    protected String mLanguageIso3;
    protected int mRequestCodeFilePicker = REQUEST_CODE_FILE_PICKER;
    //===========================================================================================


    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        //网页中的视频，上屏幕的时候，可能出现闪烁的情况
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        mIsInterceptUrl = getIntent().getBooleanExtra(KEY_OF_IS_INTERCEPT_URL, true);
        mIsInterceptPrivateUrl = getIntent().getBooleanExtra(KEY_OF_IS_INTERCEPT_PRIVATE_URL, false);
        mShakeDetector = new ShakeDetector(this);
        mOnShakeListener = new OnShakeListener() {
            @Override
            public void onShake() {
                if (null != mJSCenter) {
                    mJSCenter.callJS("javascript:shakeByHand()");
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mShakeDetector) {
            try {
                mShakeDetector.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mShakeDetector) {
            mShakeDetector.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mShakeDetector && null != mOnShakeListener) {
            mShakeDetector.unregisterOnShakeListener(mOnShakeListener);
        }
        mShakeDetector = null;
    }

    @Override
    public void onShareClicked() {
        if (null != mShareContentBean) {
            callOriginShare(mShareContentBean.getSharetype(), mShareContentBean.getPic(),
                    mShareContentBean.getTitle(), mShareContentBean.getSummary(), mShareContentBean.getUrl());
        }
    }

    /**
     * 此处检查了几种常用的通用协议 http、https、ftp、ws、wss，如果以后产生了新的通用协议，就在这个里面再加上吧。
     * <p>
     * 下面是对以上几种协议的简单注释：
     * http: 超文本传输协议
     * https: 安全的超文本传输协议，被传输的数据会在传输层加密
     * ftp: 文件传输协议
     * ws: websocket协议，最初产生于H5页面的即时通信，后来app的即时通信也会用到websocket
     * wss: 安全的websocket，相对于ws，wss会在传输数据时加密
     *
     * @param url 地址
     * @return boolean 是否需要拦截
     */
    private final String[] urlSchemes = {"http", "https", "ftp", "ws", "wss"};

    private boolean isPrivateUrl(String url) {
        for (String scheme : urlSchemes) {
            if (url.startsWith(scheme)) {
                return false;
            }
        }
        return true;
    }

    private boolean isInterceptPrivateUrl(String url) {
        return mIsInterceptPrivateUrl && isPrivateUrl(url);
    }

    @Override
    public boolean onInterceptRequest(Browser browser, String url) {
        return isInterceptPrivateUrl(url) || (mIsInterceptUrl && !ToolsUtils.isVisit(url));
    }

    @Override
    public boolean onInterceptUrlLoading(Browser browser, String url) {
        if (isInterceptPrivateUrl(url)) {
            return true;
        }

        if (!mIsInterceptUrl) {
            return false;
        }

        if (ToolsUtils.isVisit(url)) {
            if (url.contains("share.v.t.qq.com/index.php?c=share&a=jump&mobile=1")) {
                //此处是之前ShareDetailActivity页面的处理逻辑，不知道什么原因
                browser.loadUrl(mData);
                return true;
            }

            //之前AdvRecommendActivity里使用的
            if ((url.startsWith("https://m.mtime.cn/") || url.startsWith("http://m.mtime.cn/"))
                    && (url.endsWith("/share/") || url.endsWith("/share"))) {
                shareH5(1, url);
                return true;
            } else if ((url.startsWith("https://m.mtime.cn/") || url.startsWith("http://m.mtime.cn/"))
                    && (url.endsWith("/shareForQQAndWechat/") || url.endsWith("/shareForQQAndWechat"))) {
                shareH5(2, url);
                return true;
            }

            return false;
        }
        return true;
    }

    @Override
    public void onConfigBrowser(Browser browser) {
        //JSSDK
        mJSCenter = browser.enableJsSdk(JSNAME);
        mJSCenter.setJSTotalListener(this);
        //之前AdvRecommendActivity里配置
        browser.addJsInterface(new ShakeContract(), "shakeContract");
    }

    //=================之前AdvRecommendActivity里的逻辑====start======================================


    @Override
    protected void setData(String data) {
        if (!TextUtils.isEmpty(data)) {
            if ((data.startsWith("https://m.mtime.cn/") || data.startsWith("http://m.mtime.cn/"))
                    && (data.endsWith("/share/") || data.endsWith("/share"))) {
                shareH5(1, data);
                return;
            } else if ((data.startsWith("https://m.mtime.cn/") || data.startsWith("http://m.mtime.cn/"))
                    && (data.endsWith("/shareForQQAndWechat/") || data.endsWith("/shareForQQAndWechat"))) {
                shareH5(2, data);
                return;
            }
        }
        super.setData(data);
    }

    private final class ShakeContract {
        // Html调用此方法传递数据
        @JavascriptInterface
        public void notificationSupportShake() {
            if (null != mShakeDetector) {
                mShakeDetector.registerOnShakeListener(mOnShakeListener);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == mRequestCodeFilePicker) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(data.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[]{Uri.parse(data.getDataString())};
                        } catch (Exception e) {
                            dataUris = null;
                        }

                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
        else if (requestCode == 2 && resultCode == 4) {
            // 关闭所有广告页面
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 分享h5 url格式例子： url =
    // "http://m.mtime.cn/?title=小t&url=http://www.baidu.com&summary=小s&pic=小p#!/share/";
    private void shareH5(int shareType, final String url) {

        // 4.0埋点（分享按钮 ）
        Map<String, String> params = new HashMap<>();
        params.put(StatisticConstant.URL, mData);
        StatisticManager.getInstance().submit(
                this.assemble(StatisticArticle.FFIRST_REGION_TOP_NAV, null,
                        "shareBtn", null, null, null, params));

        try {
            String shareTitle = "";
            String shareSummary = "";
            String shareUrl = "";
            String sharePic = "";

            URL urlH5 = new URL(url);
            String queryString = urlH5.getQuery();
            String[] arr = queryString.split("&");
            for (int i = 0; i < arr.length; i++) {
                LogWriter.d("advertUrl--sub" + arr[i]);

                String[] subArr = arr[i].split("=");
                String key = subArr.length >= 1 ? ConvertHelper.toString(subArr[0]) : "";
                String value = subArr.length == 2 ? ConvertHelper.toString(subArr[1]) : "";

                if ("title".equals(key)) {
                    shareTitle = value;
                } else if ("url".equals(key)) {
                    shareUrl = value;
                } else if ("summary".equals(key)) {
                    shareSummary = value;
                } else if ("pic".equals(key)) {
                    sharePic = value;
                }
            }

            onShare(shareTitle, shareSummary, shareUrl, sharePic);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
            @Override
            public void onLocationFailure(LocationException e) {
                onLocationSuccess(LocationHelper.getDefaultLocationInfo());
            }

            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                String shareTitle = "";
                String shareSummary = "";
                String shareUrl = "";
                String sharePic = "";

                URL urlH5;
                try {
                    urlH5 = new URL(url);
                    String queryString = urlH5.getQuery();
                    String[] arr = queryString.split("&");
                    for (int i = 0; i < arr.length; i++) {
                        LogWriter.d("advertUrl--sub" + arr[i]);

                        String[] subArr = arr[i].split("=");
                        String key = subArr.length >= 1 ? ConvertHelper.toString(subArr[0]) : "";
                        String value = subArr.length == 2 ? ConvertHelper.toString(subArr[1]) : "";

                        if ("title".equals(key)) {
                            shareTitle = value;
                        } else if ("url".equals(key)) {
                            shareUrl = value;
                        } else if ("summary".equals(key)) {
                            shareSummary = value;
                        } else if ("pic".equals(key)) {
                            sharePic = value;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final ShareView view;
                if (shareType == 2) {
                    view = new ShareView(CommonWebActivity.this, 5);
                } else {
                    view = new ShareView(CommonWebActivity.this);
                }
                String cityId = null != locationInfo ? locationInfo.getCityId() : String.valueOf(GlobalDimensionExt.CITY_ID);
                view.setValues("0", ShareView.SHARE_TYPE_H5, cityId, null, null);
                view.setH5Values(shareTitle, shareSummary, shareUrl, sharePic);
                view.setStatistic(StatisticArticle.FFIRST_REGION_SHARE_DLG, "shareTo", "close", StatisticConstant.URL, mData);
                view.showActionSheet();
            }
        });*/
    }

    private void onShare(String shareTitle,
            String shareSummary,
            String shareUrl,
            String sharePic) {
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setTitle(shareTitle);
        shareEntity.setTargetUrl(shareUrl);
        shareEntity.setImageUrl(sharePic);
        shareEntity.setSummary(shareSummary);
        shareEntity.setWbDesc(shareSummary);
        ShareExtKt.showShareDialog(
                this,
                shareEntity,
                ShareFragment.LaunchMode.STANDARD,
                true,
                null);
    }

    @Override
    public boolean openFileInput(ValueCallback<Uri> fileUploadCallbackFirst, ValueCallback<Uri[]> fileUploadCallbackSecond) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType(mUploadableFileTypes);

        startActivityForResult(Intent.createChooser(i, getFileUploadPromptLabel()), mRequestCodeFilePicker);
        return true;
    }

    /**
     * Provides localizations for the 25 most widely spoken languages that have a ISO 639-2/T code
     */
    private String getFileUploadPromptLabel() {
        try {
            if (mLanguageIso3.equals("zho")) return decodeBase64("6YCJ5oup5LiA5Liq5paH5Lu2");
            else if (mLanguageIso3.equals("spa")) return decodeBase64("RWxpamEgdW4gYXJjaGl2bw==");
            else if (mLanguageIso3.equals("hin"))
                return decodeBase64("4KSP4KSVIOCkq+CkvOCkvuCkh+CksiDgpJrgpYHgpKjgpYfgpII=");
            else if (mLanguageIso3.equals("ben"))
                return decodeBase64("4KaP4KaV4Kaf4Ka/IOCmq+CmvuCmh+CmsiDgpqjgpr/gprDgp43gpqzgpr7gpprgpqg=");
            else if (mLanguageIso3.equals("ara"))
                return decodeBase64("2KfYrtiq2YrYp9ixINmF2YTZgSDZiNin2K3Yrw==");
            else if (mLanguageIso3.equals("por")) return decodeBase64("RXNjb2xoYSB1bSBhcnF1aXZv");
            else if (mLanguageIso3.equals("rus"))
                return decodeBase64("0JLRi9Cx0LXRgNC40YLQtSDQvtC00LjQvSDRhNCw0LnQuw==");
            else if (mLanguageIso3.equals("jpn"))
                return decodeBase64("MeODleOCoeOCpOODq+OCkumBuOaKnuOBl+OBpuOBj+OBoOOBleOBhA==");
            else if (mLanguageIso3.equals("pan"))
                return decodeBase64("4KiH4Kmx4KiVIOCoq+CovuCoh+CosiDgqJrgqYHgqKPgqYs=");
            else if (mLanguageIso3.equals("deu")) return decodeBase64("V8OkaGxlIGVpbmUgRGF0ZWk=");
            else if (mLanguageIso3.equals("jav")) return decodeBase64("UGlsaWggc2lqaSBiZXJrYXM=");
            else if (mLanguageIso3.equals("msa")) return decodeBase64("UGlsaWggc2F0dSBmYWls");
            else if (mLanguageIso3.equals("tel"))
                return decodeBase64("4LCS4LCVIOCwq+CxhuCxluCwsuCxjeCwqOCxgSDgsI7gsILgsJrgsYHgsJXgsYvgsILgsKHgsL8=");
            else if (mLanguageIso3.equals("vie"))
                return decodeBase64("Q2jhu41uIG3hu5l0IHThuq1wIHRpbg==");
            else if (mLanguageIso3.equals("kor"))
                return decodeBase64("7ZWY64KY7J2YIO2MjOydvOydhCDshKDtg50=");
            else if (mLanguageIso3.equals("fra"))
                return decodeBase64("Q2hvaXNpc3NleiB1biBmaWNoaWVy");
            else if (mLanguageIso3.equals("mar"))
                return decodeBase64("4KSr4KS+4KSH4KSyIOCkqOCkv+CkteCkoeCkvg==");
            else if (mLanguageIso3.equals("tam"))
                return decodeBase64("4K6S4K6w4K+BIOCuleCvh+CuvuCuquCvjeCuquCviCDgrqTgr4fgrrDgr43grrXgr4E=");
            else if (mLanguageIso3.equals("urd"))
                return decodeBase64("2KfbjNqpINmB2KfYptmEINmF24zauiDYs9uSINin2YbYqtiu2KfYqCDaqdix24zaug==");
            else if (mLanguageIso3.equals("fas"))
                return decodeBase64("2LHYpyDYp9mG2KrYrtin2Kgg2qnZhtuM2K8g24zaqSDZgdin24zZhA==");
            else if (mLanguageIso3.equals("tur")) return decodeBase64("QmlyIGRvc3lhIHNlw6dpbg==");
            else if (mLanguageIso3.equals("ita")) return decodeBase64("U2NlZ2xpIHVuIGZpbGU=");
            else if (mLanguageIso3.equals("tha"))
                return decodeBase64("4LmA4Lil4Li34Lit4LiB4LmE4Lif4Lil4LmM4Lir4LiZ4Li24LmI4LiH");
            else if (mLanguageIso3.equals("guj"))
                return decodeBase64("4KqP4KqVIOCqq+CqvuCqh+CqsuCqqOCrhyDgqqrgqrjgqoLgqqY=");
        } catch (Exception e) {
        }

        // return English translation by default
        return "Choose a file";
    }

    private String decodeBase64(final String base64) throws IllegalArgumentException, UnsupportedEncodingException {
        final byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        return new String(bytes, CHARSET_DEFAULT);
    }

    //=================之前AdvRecommendActivity里的逻辑====end======================================

    /**
     * jssdk-获取客户端网络状
     */
    @Override
    public void getNativeNetStatus(CommonBean commonBean) {
        if (null != mJSCenter && null != commonBean && !TextUtils.isEmpty(commonBean.getCallBackMethod())) {
            final String netStatus = Utils.getNetworkType(this);
            mJSCenter.getX5WebView().post(new Runnable() {
                @Override
                public void run() {
                    int status;
                    if ("nolink".equals(netStatus)) {
                        status = 0;
                    } else if ("wifi".equals(netStatus)) {
                        status = 1;
                    } else if ("2G".equals(netStatus)) {
                        status = 2;
                    } else if ("3G".equals(netStatus)) {
                        status = 3;
                    } else if ("4G".equals(netStatus)) {
                        status = 4;
                    } else {
                        status = 99;
                    }
                    NativeNetStatusBean data = new NativeNetStatusBean();
                    data.nativeNetStatus = String.valueOf(status);
                    JSCallbackBean<NativeNetStatusBean> callbean = new JSCallbackBean<>();
                    callbean.data = data;
                    callbean.code = 0;
                    callbean.success = true;
                    mJSCenter.callJS(commonBean.getCallBackMethod(), new Gson().toJson(callbean));
                }
            });
        }
    }

    /**
     * jssdk-拨打电话
     */
    @Override
    public void handleDialTel(HandleTelBean bean) {
        if (null != bean && null != bean.getData() && !TextUtils.isEmpty(bean.getData().phoneNumber)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Acp.getInstance(CommonWebActivity.this).request(new AcpOptions.Builder().setPermissions(
                            Manifest.permission.CALL_PHONE).build(), new AcpListener() {
                        @Override
                        public void onGranted() {
                            Acp.getInstance(getApplicationContext()).onDestroy();
                            // 直接拨打电话
                            final Intent intent = new Intent();
                            intent.setData(Uri.parse("tel:" + bean.getData().phoneNumber));
                            intent.setAction(Intent.ACTION_CALL);
                            startActivity(intent);
                        }

                        @Override
                        public void onDenied(List<String> permissions) {
                            Acp.getInstance(getApplicationContext()).onDestroy();
                            MToastUtils.showShortToast(permissions.toString() + "权限拒绝");
                        }
                    });
                }
            });
        }
    }

    /**
     * jssdk-回到上一级原生页面（有可能刷新）
     */
    @Override
    public void handleGoBack(HandleGoBackBean backBean) {
        if (null != backBean && null != backBean.getData()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (backBean.getData().isCloseWindow()) {//关闭当前界面
                        CommonWebActivity.this.finish();
                    } else {
                        if (null != mJSCenter && null != mJSCenter.getX5WebView() && mJSCenter.getX5WebView().canGoBack()) {
                            mJSCenter.getX5WebView().goBack();
                        } else {
                            CommonWebActivity.this.finish();
                        }
                    }
                }
            });
        }
    }

    /**
     * jssdk-设置导航栏
     */
    @Override
    public void setNavBarStatus(NavBarStatusBean navBarStatus) {
        if (null != navBarStatus && null != navBarStatus.getData()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!navBarStatus.getData().isIsShowNavBar()) {
                        if (null != mTitleBar) {
                            mTitleBar.setTitleBarShow(false);
                        }
                    } else {
                        if (null != mTitleBar) {
                            mTitleBar.setTitleBarShow(true);
                            mTitleBar.setBackShow(true);
                            mTitleBar.setCloseShow(true);
                            mTitleBar.setShareShow(false);
                            if (!TextUtils.isEmpty(navBarStatus.getData().getNavBarMainTitle())) {
                                mTitleBar.setTitle(navBarStatus.getData().getNavBarMainTitle());
                            }
                            if (null != navBarStatus.getData().getShareContent() && navBarStatus.getData().isIsShowRightShareBtn()) {
                                mTitleBar.setShareShow(true);
                                mShareContentBean = navBarStatus.getData().getShareContent();
                            }
                        }
                    }
                }
            });
        }
    }

    /**
     * jssdk-applink跳转
     */
    @Override
    public void openAppLinkClient(OpenAppLinkBean bean) {
        if (null != bean && null != bean.getData()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ApplinkManager.jump4H5(CommonWebActivity.this, bean.getData().getApplinkData(),
                            StatisticManager.getH5Refer(bean.getData().originUrl));
                }
            });
        }
    }

    /**
     * jssdk-打开图片浏览
     */
    @Override
    public void openImageBrowser(OpenImageBrowser bean) {
        if (null != bean && null != bean.getData()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String refer = StatisticManager.getH5Refer(bean.getData().originUrl);
                    JumpUtil.startNewsPhotoDetailActivity(CommonWebActivity.this, refer,
                            bean.getData().getPhotoImageUrls(), "",
                            bean.getData().getCurrentImageIndex(), 1, false);
                }
            });
        }
    }

    /**
     * jssdk-显示生日/等级礼包弹窗
     */
    @Override
    public void showBirthdayLevelPopPage(ShowBirthdayLevelPopPageBean bean) {
        if (null == bean || null == bean.getData())
            return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // showBirthdayLevelPopPageBean: 0代表生日弹窗，1表示等级弹窗
                if ("0".equals(bean.getData().getType()) || "1".equals(bean.getData().getType())) {
                    int type = "0".equals(bean.getData().getType()) ? 2 : 1;
                    MemberCenterPopup popup = new MemberCenterPopup(CommonWebActivity.this, false, new MemberCenterPopup.OnDismantleSuccessListener() {
                        @Override
                        public void onSuccess() {
                            // 拆礼包成功后回调
                            if (null != mJSCenter) {
                                mJSCenter.callJS(JSCallbackBean.CALL_JS_GIFT);
                            }
                        }
                    });
                    popup.giftDismantle(type);
                }
            }
        });
    }

    /**
     * jssdk-显示分享弹框
     */
    @Override
    public void showShare(ShowShareBean bean) {
        if (null != bean && null != bean.getData()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    callOriginShare(bean.getData().getSharetype(), bean.getData().getPic(),
                            bean.getData().getTitle(), bean.getData().getSummary(), bean.getData().getUrl());
                }
            });
        }
    }

    /**
     * 调用分享的弹窗
     */
    private void callOriginShare(final String shareType, final String imageUrl, final String h5Title, final String h5Content, final String h5Link) {
        ShareEntity shareEntity = new ShareEntity();
        shareEntity.setShareType(ShareType.SHARE_IMAGE_TEXT);
        shareEntity.setTitle(h5Title);
        shareEntity.setImageUrl(imageUrl);
        shareEntity.setSummary(h5Content);
        shareEntity.setWbDesc(h5Content);
        shareEntity.setTargetUrl(h5Link);
        ShareExtKt.showShareDialog(
                this,
                shareEntity,
                ShareFragment.LaunchMode.STANDARD,
                true,
                (SharePlatform sp) -> {
                    int result = 1;
                    mJSCenter.callJS(result == 1 ? JSCallbackBean.CALL_JS_SHARE_SUCCESS : JSCallbackBean.CALL_JS_SHARE_FAILURE);
                    return null;
                });
    }

    /**
     * jssdk-显示播放器控件
     */
    @Override
    public void showVideoPlayer(ShowVideoPlayerBean bean) {
        //这个方法实际是在文章详情页面使用的
    }

    @Override
    public void newShowShare(NewShowShareBean bean) {
        ShareExtJava.showShareDialog(
                this,
                bean.getData().sharetype,
                bean.getData().relateId,
                bean.getData().secondRelateId
        );
    }
}
