package com.mtime.base.utils;

import android.content.Context;

import com.mtime.base.utils.recorder.RecorderManager;

public class UtilsManager {
    public static void initUtils(Context context) {
        RecorderManager.init(context.getApplicationContext());
        MToastUtils.initToast(context.getApplicationContext());
        MScreenUtils.init(context.getApplicationContext());
    }
}
