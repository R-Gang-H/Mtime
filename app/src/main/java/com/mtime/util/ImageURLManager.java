package com.mtime.util;

import android.os.Build;
import android.text.TextUtils;

import com.mtime.bussiness.splash.LoadManager;
import com.mtime.constant.FrameConstant;

/**
 * Created by LEE on 15-9-1.
 */
@Deprecated
public class ImageURLManager {
    public enum ImageStyle {
        LARGE, STANDARD, STANDARD_HOR, THUMB
    }

    private static final int LARGE_WIDTH = 392;
    private static final int LARGE_HEIGHT = 560;

    private static final int STANDARD_WIDTH = 240;
    private static final int STANDARD_HEIGHT = 366;

    private static final int STANDARD_HOR_WIDTH = 240;
    private static final int STANDARD_HOR_HEIGHT = 160;

    private static final int THUMB_WIDTH = 120;
    private static final int THUMB_HEIGHT = 120;

    // 等比缩放
    public static final int SCALE_TO_FIT = 0;
    // 定宽等比缩放，如果高大于需求则截取高为需求
    public static final int FIX_WIDTH_TRIM_HEIGHT = 1;
    // 定宽等比缩放，不限制高
    public static final int FIX_WIDTH = 2;
    /// 固定宽或者固定高
    /// 如果宽大于需求，高大于需求，宽>高，宽等于需求宽，高等比
    /// 如果宽大于需求，高大于需求，高>宽，高等于需求高，宽等比
    /// 如果宽大于需求，高小于需求，宽等于需求宽，高等比
    /// 如果宽小于需求，高大于需求，高等于需求高，宽等比
    /// 如果都小则不做处理
    public static final int FIX_WIDTH_OR_HEIGHT = 3;
    // 固定宽和高，如果小于则放大，宽截取中间的需求宽
    public static final int FIX_WIDTH_AND_HEIGHT = 4;

    public static String getRequestURL(final String url, final int w, final int h, final int clipType) {
        if (TextUtils.isEmpty(LoadManager.getImageProxy())) {
            return url;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(LoadManager.getImageProxy());
        sb.append(url);
        if (w > 0 && h >0) {
            sb.append("&width=");
            sb.append(w);
            sb.append("&height=");
            sb.append(h);
        }
        sb.append("&quality=");

        sb.append(FrameConstant.image_quality);
        sb.append("&clipType=");
        sb.append(clipType);

        // 是否支持Webp
        if (Build.VERSION.SDK_INT >= 16 && WebPUtils.isWebPSupported()) {
            sb.append("&iswebp=true");
        }

        return sb.toString();
    }

    public static String getRequestURL(final String url, final int w, final int h) {
        return getRequestURL(url, w, h, ImageURLManager.FIX_WIDTH_AND_HEIGHT);
    }

    public static String getRequestURL(final String url, final int clipType) {
        if (TextUtils.isEmpty(LoadManager.getImageProxy())) {
            return url;
        }

        return LoadManager.getImageProxy() + url + "&quality=" + FrameConstant.image_quality + "&clipType=" + clipType;
    }

    public static String getRequestURL(final String url, final ImageStyle style) {
        int width = 0;
        int height = 0;
        switch (style) {
            case LARGE:
                width = LARGE_WIDTH;
                height = LARGE_HEIGHT;
                break;
            case STANDARD:
                width = STANDARD_WIDTH;
                height = STANDARD_HEIGHT;
                break;
            case STANDARD_HOR:
                width = STANDARD_HOR_WIDTH;
                height = STANDARD_HOR_HEIGHT;
                break;
            case THUMB:
                width = THUMB_WIDTH;
                height = THUMB_HEIGHT;
                break;
        }

        return getRequestURL(url, width, height, ImageURLManager.FIX_WIDTH_AND_HEIGHT);
    }

    public static String getRequestURL(final String url, final ImageURLManager.ImageStyle style, final int clipType) {
        int width = 0;
        int height = 0;
        switch (style) {
            case LARGE:
                width = LARGE_WIDTH;
                height = LARGE_HEIGHT;
                break;
            case STANDARD:
                width = STANDARD_WIDTH;
                height = STANDARD_HEIGHT;
                break;
            case STANDARD_HOR:
                width = STANDARD_HOR_WIDTH;
                height = STANDARD_HOR_HEIGHT;
                break;
            case THUMB:
                width = THUMB_WIDTH;
                height = THUMB_HEIGHT;
                break;
        }

        return getRequestURL(url, width, height, clipType);
    }

    public static String getRequestURL(final String url) {
        if (TextUtils.isEmpty(LoadManager.getImageProxy())) {
            return url;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(LoadManager.getImageProxy());
        sb.append(url);

        return sb.toString();
    }

    public static int getWidth(final ImageStyle style) {
        int width = 0;
        switch (style) {
            case LARGE:
                width = LARGE_WIDTH;
                break;
            case STANDARD:
                width = STANDARD_WIDTH;
                break;
            case STANDARD_HOR:
                width = STANDARD_HOR_WIDTH;
                break;
            case THUMB:
                width = THUMB_WIDTH;
                break;
            default:break;
        }

        return width;
    }

    public static int getHeight(final ImageStyle style) {
        int height = 0;
        switch (style) {
            case LARGE:
                height = LARGE_HEIGHT;
                break;
            case STANDARD:
                height = STANDARD_HEIGHT;
                break;
            case STANDARD_HOR:
                height = STANDARD_HOR_HEIGHT;
                break;
            case THUMB:
                height = THUMB_HEIGHT;
                break;
            default:break;
        }

        return height;
    }
}
