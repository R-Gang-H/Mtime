package com.mtime.player;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.kotlin.android.app.data.entity.video.VideoPlayUrl;
import com.mtime.frame.App;
import com.mtime.common.utils.PrefsManager;

import java.util.List;

/**
 * Created by mtime on 2017/10/23.
 */

public class PlayerHelper {

    private static final String TAG = "PlayerHelper";

    public static final String KEY_VIDEO_DEFINITION = "video_definition";
    public static final String KEY_PLAYER_BRIGHTNESS = "player_brightness";

    public static int getScreenMinW(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static int getScreenW(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenMaxH(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.max(displayMetrics.heightPixels, displayMetrics.widthPixels);
    }

    public static int getScreenH(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    public static String getHistoryDefinition() {
        return PrefsManager.get(App.getInstance().getApplicationContext()).getString(KEY_VIDEO_DEFINITION);
    }

    public static float getHistoryBrightness() {
        return PrefsManager.get(App.getInstance().getApplicationContext()).getFloat(KEY_PLAYER_BRIGHTNESS);
    }

    public static void recordDefinition(String definition) {
        PrefsManager.get(App.getInstance().getApplicationContext()).putString(KEY_VIDEO_DEFINITION, definition);
    }

    public static void recordBrightness(float brightness) {
        PrefsManager.get(App.getInstance().getApplicationContext()).putFloat(KEY_PLAYER_BRIGHTNESS, brightness);
    }

    public static VideoPlayUrl getHighDefinition(List<VideoPlayUrl> urlItems) {
        if (urlItems == null || urlItems.size() <= 0)
            return null;
        int size = urlItems.size();
        VideoPlayUrl resultItem = urlItems.get(size - 1);
        long maxDefinitionCode = resultItem.getResolutionType();
        Log.d(TAG, "init max code = " + maxDefinitionCode);
        for (VideoPlayUrl item : urlItems) {
            long code = item.getResolutionType();
            if (code > maxDefinitionCode) {
                resultItem = item;
                maxDefinitionCode = code;
            }
        }
        Log.d(TAG, "result max code = " + maxDefinitionCode);
        return resultItem;
    }

    private static int getDefinitionCode(String definitionName) {
        String definition = definitionName.replaceAll("[a-zA-Z]+", "");
        try {
            return Integer.parseInt(definition);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isTopActivity(Activity activity) {
        return activity != null && isForeground(activity, activity.getClass().getName());
    }

    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            return className.equals(cpn.getClassName());
        }
        return false;
    }

    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = resources.getDimensionPixelSize(resourceId);
        }
        if (statusBarHeight <= 0) {
            try {
                Class<?> clazz = Class.forName("com.android.internal.R$dimen");
                Object object = clazz.newInstance();
                int height = Integer.parseInt(clazz.getField("status_bar_height")
                        .get(object).toString());
                statusBarHeight = resources.getDimensionPixelSize(height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public static void setSystemUIVisible(Activity context, boolean show) {
        if (show) {
            View decorView = context.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(option);
            if (Build.VERSION.SDK_INT >= 21) {
                context.getWindow().setStatusBarColor(Color.TRANSPARENT);
                return;
            }
            if (Build.VERSION.SDK_INT >= 19) {
                context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            context.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    public static void portraitMatchWidth_16_9(Context context, ViewGroup container, Params params) {
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = (getScreenW(context) * 9) / 16;
        if (params != null) {
            if (layoutParams instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) layoutParams)
                        .setMargins(params.getLeftMargin(), params.getTopMargin(), params.getRightMargin(), params.getBottomMargin());
            } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams)
                        .setMargins(params.getLeftMargin(), params.getTopMargin(), params.getRightMargin(), params.getBottomMargin());
            }
            container.setPadding(params.getLeftPadding(), params.getTopPadding(), params.getRightPadding(), params.getBottomPadding());
        }
        container.setLayoutParams(layoutParams);
    }

    public static void landscapeMatchWidthHeight(Context context, ViewGroup container, Params params) {
        ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = -1;
        if (params != null) {
            if (layoutParams instanceof RelativeLayout.LayoutParams) {
                ((RelativeLayout.LayoutParams) layoutParams)
                        .setMargins(params.getLeftMargin(), params.getTopMargin(), params.getRightMargin(), params.getBottomMargin());
            } else if (layoutParams instanceof FrameLayout.LayoutParams) {
                ((FrameLayout.LayoutParams) layoutParams)
                        .setMargins(params.getLeftMargin(), params.getTopMargin(), params.getRightMargin(), params.getBottomMargin());
            }
            container.setPadding(params.getLeftPadding(), params.getTopPadding(), params.getRightPadding(), params.getBottomPadding());
        }
        container.setLayoutParams(layoutParams);
    }

    public static class Params {
        private int leftMargin;
        private int topMargin;
        private int rightMargin;
        private int bottomMargin;

        private int leftPadding;
        private int topPadding;
        private int rightPadding;
        private int bottomPadding;

        public int getLeftMargin() {
            return leftMargin;
        }

        public Params setLeftMargin(int leftMargin) {
            this.leftMargin = leftMargin;
            return this;
        }

        public int getTopMargin() {
            return topMargin;
        }

        public Params setTopMargin(int topMargin) {
            this.topMargin = topMargin;
            return this;
        }

        public int getRightMargin() {
            return rightMargin;
        }

        public Params setRightMargin(int rightMargin) {
            this.rightMargin = rightMargin;
            return this;
        }

        public int getBottomMargin() {
            return bottomMargin;
        }

        public Params setBottomMargin(int bottomMargin) {
            this.bottomMargin = bottomMargin;
            return this;
        }

        public int getLeftPadding() {
            return leftPadding;
        }

        public Params setLeftPadding(int leftPadding) {
            this.leftPadding = leftPadding;
            return this;
        }

        public int getTopPadding() {
            return topPadding;
        }

        public Params setTopPadding(int topPadding) {
            this.topPadding = topPadding;
            return this;
        }

        public int getRightPadding() {
            return rightPadding;
        }

        public Params setRightPadding(int rightPadding) {
            this.rightPadding = rightPadding;
            return this;
        }

        public int getBottomPadding() {
            return bottomPadding;
        }

        public Params setBottomPadding(int bottomPadding) {
            this.bottomPadding = bottomPadding;
            return this;
        }
    }

}
