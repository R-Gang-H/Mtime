package com.mtime.base.signal;

import android.os.HandlerThread;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2017-09-19
 * <p>
 * 切勿在 此线程 做耗时操作
 */

class LooperThread extends HandlerThread {

    private static LooperThread t;

    private LooperThread() {
        super("SocketLooperThread");
    }

    public static LooperThread get() {
        if (t == null) {
            synchronized (LooperThread.class) {
                if (t == null) {
                    t = new LooperThread();
                    t.start();
                }
            }
        }
        return t;
    }
}
