package com.mtime.base.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-09-29
 */
public class ViewFactory implements LayoutInflater.Factory {

    private static final String FOREGROUND_IMAGE_VIEW = "com.mtime.base.views.ForegroundImageView";

    @Override
    public final View onCreateView(String name, Context context, AttributeSet attrs) {
        return handleCreateView(name, context, attrs);
    }

    private View handleCreateView(String name, Context context, AttributeSet attrs) {
        switch (name) {
            case FOREGROUND_IMAGE_VIEW:
            case "ForegroundImageView":
            case "ImageView":
                return new ForegroundImageView(context, attrs);
            default:
                return createView(name, context, attrs);
        }
    }

    protected View createView(String name, Context context, AttributeSet attrs) {
        return null;
    }
}
