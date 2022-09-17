package com.mtime.frame;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kk.taurus.uiframe.manager.ActivityManager;
import com.mtime.base.application.AppForeBackListener;
import com.mtime.base.network.NetworkManager;
import com.mtime.statistic.large.StatisticManager;

public class SessionForeBackListener implements AppForeBackListener {
    private static final int DELAY_MILLIS = 1000 * 60 * 30; //30分钟
    private static final int MSG_CLEAR = 10101;
    private Handler mHandler = null;

    @Override
    public void onBecameForeground() {
        if(null != mHandler) {
            mHandler.removeMessages(MSG_CLEAR);
        }
    }

    @Override
    public void onBecameBackground() {
        if(null == mHandler) {
            mHandler = new SessionHandler(Looper.getMainLooper());
        }
        // 进入后台30分钟后，清除相关数据并关闭所有页面
        mHandler.sendEmptyMessageDelayed(MSG_CLEAR, DELAY_MILLIS);
    }

    private class SessionHandler extends Handler {

        public SessionHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            //清除相关数据并关闭所有页面
            if(msg.what == MSG_CLEAR) {
                // 停止所有网络请求
                NetworkManager.getInstance().clearRequest();
                // 清理统计的sessionID
                StatisticManager.getInstance().clear();
                // 关闭所有页面
                ActivityManager.getInstance().finishAll();
            }
        }
    }
}
