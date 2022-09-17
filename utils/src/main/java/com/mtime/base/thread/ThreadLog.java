package com.mtime.base.thread;

import android.util.Log;

/**
 * Created by JiaJunHui on 2018/10/21.
 */
public class ThreadLog {

    static void error(String tag, String message){
        Log.e(tag, message);
    }

    static void warning(String tag, String message){
        Log.w(tag, message);
    }

    static void info(String tag, String message){
        Log.i(tag, message);
    }

}
