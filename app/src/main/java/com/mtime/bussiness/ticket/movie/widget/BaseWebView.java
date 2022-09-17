package com.mtime.bussiness.ticket.movie.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewConfiguration;
import android.webkit.WebView;

/**
 * Created by LEE on 2/8/17.
 * // 尝试解决 java.lang.IllegalArgumentException: Receiver not registered: android.widget.ZoomButtonsController$1@1
 */
@Deprecated
public class BaseWebView extends WebView {

    private static final long ZOOM_CONTROLS_TIMEOUT =
            ViewConfiguration.getZoomControlsTimeout();

    public BaseWebView(Context context) {
        super(context);
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
