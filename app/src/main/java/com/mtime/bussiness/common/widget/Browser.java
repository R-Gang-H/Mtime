package com.mtime.bussiness.common.widget;

import com.jssdk.JSCenter;
import com.tencent.smtt.sdk.WebSettings;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 */
public interface Browser {
    JSCenter enableJsSdk(String jsName);

    void callJs(String methodName, Object... args);

    void addJsInterface(Object obj, String name);

    WebSettings getSettings();

    void loadUrl(String url);

    boolean canGoBack();

    void reload();

    void stopLoading();

    void goBack();
}
