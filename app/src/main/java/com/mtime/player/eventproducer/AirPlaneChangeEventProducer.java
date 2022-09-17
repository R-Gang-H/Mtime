package com.mtime.player.eventproducer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.kk.taurus.playerbase.extension.BaseEventProducer;
import com.kk.taurus.playerbase.extension.ReceiverEventSender;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.mtime.player.DataInter;

import java.lang.ref.WeakReference;

/**
 * Created by JiaJunHui on 2018/6/19.
 */
public class AirPlaneChangeEventProducer extends BaseEventProducer {

    private static final int MSG_CODE_AIRPLANE_STATE_CHANGE = 10;

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_CODE_AIRPLANE_STATE_CHANGE:
                    ReceiverEventSender sender = getSender();
                    if (sender != null) {
                        sender.sendEvent(
                                DataInter.ProducerEvent.EVENT_AIRPLANE_STATE_CHANGE,
                                null,
                                onReceiverFilter);
                    }
                    break;
            }
        }
    };

    private final Context mAppContext;
    private AirPlaneBroadcastReceiver mBroadcastReceiver;

    public AirPlaneChangeEventProducer(Context context) {
        this.mAppContext = context.getApplicationContext();
    }

    private final IReceiverGroup.OnReceiverFilter onReceiverFilter =
            new IReceiverGroup.OnReceiverFilter() {
                @Override
                public boolean filter(IReceiver iReceiver) {
                    return DataInter.ReceiverKey.KEY_ERROR_COVER.equals(iReceiver.getKey());
                }
            };

    @Override
    public void onAdded() {
        try {
            mBroadcastReceiver = new AirPlaneBroadcastReceiver(mHandler);
            IntentFilter filter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
            mAppContext.registerReceiver(mBroadcastReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRemoved() {
        unregisterReceiver();
        cleanHandler();
    }

    private void unregisterReceiver() {
        try {
            if (mBroadcastReceiver != null) {
                mAppContext.unregisterReceiver(mBroadcastReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        unregisterReceiver();
        cleanHandler();
    }

    private void cleanHandler() {
        mHandler.removeMessages(MSG_CODE_AIRPLANE_STATE_CHANGE);
    }

    public static class AirPlaneBroadcastReceiver extends BroadcastReceiver {

        private final WeakReference<Handler> mHandlerRefer;

        public AirPlaneBroadcastReceiver(Handler handler) {
            mHandlerRefer = new WeakReference<>(handler);
        }

        Handler getHandler() {
            if (mHandlerRefer != null)
                return mHandlerRefer.get();
            return null;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
                Handler postHandler = getHandler();
                if (postHandler != null) {
                    postHandler.sendEmptyMessage(MSG_CODE_AIRPLANE_STATE_CHANGE);
                }
            }
        }
    }
}
