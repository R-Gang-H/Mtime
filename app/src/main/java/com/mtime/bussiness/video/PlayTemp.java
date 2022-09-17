package com.mtime.bussiness.video;

import android.app.Activity;
import android.text.TextUtils;

import java.util.Stack;

/**
 * Created by JiaJunHui on 2018/4/16.
 */
public class PlayTemp {

    private static String danmuTextCache;
    private static final Stack<Activity> mPreviewPlayActivityStack;

    static {
        mPreviewPlayActivityStack = new Stack<>();
    }

    public static void push(Activity activity) {
        mPreviewPlayActivityStack.push(activity);
    }

    /**
     * pop activity
     *
     * @return true need destroy player, false not;
     */
    public static boolean pop() {
        if (mPreviewPlayActivityStack.isEmpty()) {
            return true;
        }
        mPreviewPlayActivityStack.pop();
        return mPreviewPlayActivityStack.isEmpty();
    }

    public static void saveDanmuTextCache(String text) {
        if (TextUtils.isEmpty(text))
            return;
        danmuTextCache = text;
    }

    public static String getDanmuTextCache() {
        if (TextUtils.isEmpty(danmuTextCache))
            danmuTextCache = "";
        return danmuTextCache;
    }

    public static void cleanDanmuTextCache() {
        danmuTextCache = null;
    }

}
