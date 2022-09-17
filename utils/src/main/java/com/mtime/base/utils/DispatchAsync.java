package com.mtime.base.utils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by LiJiaZhi on 17/3/24.
 *
 * 简单的异步执行分发
 */
public class DispatchAsync {
    
    private static Handler mHandler = null;

    public interface DispatchRunnable {
        /**
         * 后台执行操作
         */
        void runInBackground();

        /**
         * 主线程执行操作
         */
        void runInMain();
    }

    public static void dispatchAsyncDelayed(Runnable runnable, long delayMillis) {
        if(null == mHandler) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        mHandler.postDelayed(runnable, delayMillis);
    }

    public static void dispatchAsync(DispatchRunnable runnable) {
        new DispatchAsyncTask().execute(runnable);
    }

    private final static class DispatchAsyncTask extends AsyncTask<DispatchRunnable, Void, DispatchRunnable> {

        @Override
        protected DispatchRunnable doInBackground(DispatchRunnable... params) {
            if (params == null || params.length == 0)
                return null;
            DispatchRunnable runnable = params[0];

            runnable.runInBackground();

            return runnable;
        }

        @Override
        protected void onPostExecute(DispatchRunnable disPatchRunnable) {
            if (disPatchRunnable != null) {
                disPatchRunnable.runInMain();
            }
        }
    }
}
