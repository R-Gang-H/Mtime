package com.mtime.bussiness.common.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;

import com.jssdk.JSCenter;
import com.jssdk.JSInterfaceNative;
import com.mtime.base.widget.X5WebView;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-05-25
 */
public class BrowserImpl extends X5WebView implements Browser {

    public BrowserImpl(Context context) {
        super(context);
    }

    public BrowserImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    public BrowserImpl(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }

    @Override
    public JSCenter enableJsSdk(String jsName) {
        JSInterfaceNative jsNative = new JSInterfaceNative(this, jsName);
        return jsNative.getJsCenter();
    }

    @Override
    public void callJs(String methodName, Object... args) {
        StringBuilder js = new StringBuilder("javascript:");
        js.append(methodName).append("(");
        if (args != null) {
            boolean addParam = false;
            for (Object arg : args) {
                if (arg == null) continue;
                boolean isNumber = arg instanceof Number;
                if (!isNumber) {
                    js.append("'");
                }
                js.append(arg);
                if (!isNumber) {
                    js.append("'");
                }
                js.append(",");
                addParam = true;
            }
            if (addParam) {
                js.deleteCharAt(js.length() - 1);
            }
        }
        js.append(")");
        loadUrl(js.toString());
    }

    @SuppressLint("JavascriptInterface")
    @Override
    public void addJsInterface(Object obj, String name) {
        addJavascriptInterface(obj, name);
    }

}
