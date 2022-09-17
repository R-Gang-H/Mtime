package com.kotlin.android.player

import android.content.Context
import android.os.Bundle
import com.kk.taurus.playerbase.receiver.GroupValue
import com.kk.taurus.playerbase.receiver.IReceiver
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnReceiverFilter
import com.kk.taurus.playerbase.receiver.ReceiverGroup

/**
 * create by lushan on 2020/8/31
 * description:
 */
abstract class BaseReceiverGroupConfig(var context: Context) {
    private var mReceiverGroup: ReceiverGroup? = null

    init {
        mReceiverGroup = onInitReceiverGroup(context)
        onReceiverGroupCreated(context)

    }

    protected open fun onReceiverGroupCreated(context: Context?) {

    }

    protected abstract fun onInitReceiverGroup(context: Context?): ReceiverGroup?

    open fun registerOnGroupValueUpdateListener(onGroupValueUpdateListener: OnGroupValueUpdateListener?) {
        val groupValue = getGroupValue()
        groupValue?.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    open fun getReceiverGroup(): ReceiverGroup? {
        return mReceiverGroup
    }

    open fun getGroupValue(): GroupValue? {
        return  mReceiverGroup?.groupValue
    }

    open fun updateGroupValue(key: String?, value: Any?) {
        val groupValue = getGroupValue()
        groupValue?.putObject(key, value)
    }

    open fun addReceiver(receiverKey: String?, receiver: IReceiver?) {
        mReceiverGroup?.addReceiver(receiverKey, receiver)
    }

    open fun getReceiver(receiverKey: String?): IReceiver? {
        return mReceiverGroup?.getReceiver(receiverKey)
    }

    open fun removeReceiver(receiverKey: String?) {
        mReceiverGroup?.removeReceiver(receiverKey)
    }

    open fun sendReceiverPrivateEvent(key: String?, eventCode: Int, bundle: Bundle?) {
        val receiver = mReceiverGroup?.getReceiver<IReceiver>(key)
        receiver?.onPrivateEvent(eventCode, bundle)
    }

    open fun sendReceiverEvent(eventCode: Int, bundle: Bundle?, receiverFilter: OnReceiverFilter?) {
        mReceiverGroup?.forEach(receiverFilter, { iReceiver -> iReceiver.onReceiverEvent(eventCode, bundle) })
    }

    open fun destroy() {
        mReceiverGroup?.clearReceivers()
    }

}