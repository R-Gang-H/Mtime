package com.mtime.bussiness.video;

import android.content.Context;
import android.os.Bundle;

import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

/**
 * Created by JiaJunHui on 2018/6/27.
 */
public abstract class BaseReceiverGroupConfig {

    private final Context mContext;
    private final ReceiverGroup mReceiverGroup;

    public BaseReceiverGroupConfig(Context context) {
        this.mContext = context;
        this.mReceiverGroup = onInitReceiverGroup(context);
        onReceiverGroupCreated(context);
    }

    protected void onReceiverGroupCreated(Context context) {

    }

    protected abstract ReceiverGroup onInitReceiverGroup(Context context);

    protected Context getContext() {
        return mContext;
    }

    public void registerOnGroupValueUpdateListener(IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null)
            groupValue.registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    public ReceiverGroup getReceiverGroup() {
        return mReceiverGroup;
    }

    public GroupValue getGroupValue() {
        if (mReceiverGroup != null)
            return mReceiverGroup.getGroupValue();
        return null;
    }

    public void updateGroupValue(String key, Object value) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null)
            groupValue.putObject(key, value);
    }

    public void addReceiver(String receiverKey, IReceiver receiver) {
        if (mReceiverGroup != null)
            mReceiverGroup.addReceiver(receiverKey, receiver);
    }

    public IReceiver getReceiver(String receiverKey) {
        if (mReceiverGroup != null)
            return mReceiverGroup.getReceiver(receiverKey);
        return null;
    }

    public void removeReceiver(String receiverKey) {
        if (mReceiverGroup != null)
            mReceiverGroup.removeReceiver(receiverKey);
    }

    public void sendReceiverPrivateEvent(String key, int eventCode, Bundle bundle) {
        if (mReceiverGroup == null)
            return;
        IReceiver receiver = mReceiverGroup.getReceiver(key);
        if (receiver != null)
            receiver.onPrivateEvent(eventCode, bundle);
    }

    public void sendReceiverEvent(int eventCode, Bundle bundle, IReceiverGroup.OnReceiverFilter receiverFilter) {
        if (mReceiverGroup == null)
            return;
        mReceiverGroup.forEach(receiverFilter, new IReceiverGroup.OnLoopListener() {
            @Override
            public void onEach(IReceiver iReceiver) {
                iReceiver.onReceiverEvent(eventCode, bundle);
            }
        });
    }

    public void destroy() {
        if (mReceiverGroup != null)
            mReceiverGroup.clearReceivers();
    }

}
