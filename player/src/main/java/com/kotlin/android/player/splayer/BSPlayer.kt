package com.kotlin.android.player.splayer

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import com.kk.taurus.playerbase.assist.RelationAssist
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.log.PLog
import com.kk.taurus.playerbase.player.IPlayer
import com.kk.taurus.playerbase.provider.IDataProvider
import com.kk.taurus.playerbase.receiver.GroupValue
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener

/**
 * create by lushan on 2020/9/1
 * description:
 */
abstract class BSPlayer(var ctx: Context) : ISPayer {
    protected var mRelationAssist: RelationAssist = RelationAssist(ctx)

    protected var mAppContext: Context = ctx
    private val mOnPlayerEventListeners: MutableList<OnPlayerEventListener> = mutableListOf()
    private val mOnErrorEventListeners: MutableList<OnErrorEventListener> = mutableListOf()
    private val mOnReceiverEventListeners: MutableList<OnReceiverEventListener> = mutableListOf()

    override fun addOnPlayerEventListener(onPlayerEventListener: OnPlayerEventListener?) {
        if (mOnPlayerEventListeners.contains(onPlayerEventListener)) return
        onPlayerEventListener?.apply {
            mOnPlayerEventListeners.add(this)
        }
    }

    override fun removePlayerEventListener(onPlayerEventListener: OnPlayerEventListener?): Boolean {
        return mOnPlayerEventListeners.remove(onPlayerEventListener)
    }

    override fun addOnErrorEventListener(onErrorEventListener: OnErrorEventListener?) {
        if (mOnErrorEventListeners.contains(onErrorEventListener)) return
        onErrorEventListener?.apply {
            mOnErrorEventListeners.add(this)
        }
    }

    override fun removeErrorEventListener(onErrorEventListener: OnErrorEventListener?): Boolean {
        return mOnErrorEventListeners.remove(onErrorEventListener)
    }

    override fun addOnReceiverEventListener(onReceiverEventListener: OnReceiverEventListener?) {
        if (mOnReceiverEventListeners.contains(onReceiverEventListener)) return
        onReceiverEventListener?.apply {
            mOnReceiverEventListeners.add(this)
        }
    }

    override fun removeReceiverEventListener(onReceiverEventListener: OnReceiverEventListener?): Boolean {
        return mOnReceiverEventListeners.remove(onReceiverEventListener)
    }

    private val mInternalPlayerEventListener = OnPlayerEventListener { eventCode, bundle ->
        onCallBackPlayerEvent(eventCode, bundle)
        callBackPlayerEventListeners(eventCode, bundle)
    }

    abstract fun onCallBackPlayerEvent(eventCode: Int, bundle: Bundle?)

    open fun callBackPlayerEventListeners(eventCode: Int, bundle: Bundle?) {
        mOnPlayerEventListeners.forEach { it.onPlayerEvent(eventCode, bundle) }
    }

    private val mInternalErrorEventListener = OnErrorEventListener { eventCode, bundle ->
        onCallBackErrorEvent(eventCode, bundle)
        callBackErrorEventListeners(eventCode, bundle)
    }

    protected abstract fun onCallBackErrorEvent(eventCode: Int, bundle: Bundle?)

    open fun callBackErrorEventListeners(eventCode: Int, bundle: Bundle?) {
        mOnErrorEventListeners.forEach { it.onErrorEvent(eventCode, bundle) }
    }

    private val mInternalReceiverEventListener = OnReceiverEventListener { eventCode, bundle ->
        onCallBackReceiverEvent(eventCode, bundle)
        callBackReceiverEventListeners(eventCode, bundle)
    }

    protected abstract fun onCallBackReceiverEvent(eventCode: Int, bundle: Bundle?)

    open fun callBackReceiverEventListeners(eventCode: Int, bundle: Bundle?) {
        mOnReceiverEventListeners.forEach { it.onReceiverEvent(eventCode, bundle) }
    }

    open fun attachListener() {
        mRelationAssist.apply {
            setOnPlayerEventListener(mInternalPlayerEventListener)
            setOnErrorEventListener(mInternalErrorEventListener)
            setOnReceiverEventListener(mInternalReceiverEventListener)
        }
    }

    override fun getGroupValue(): GroupValue? {
        val receiverGroup = getReceiverGroup()
        receiverGroup ?:return null
        return receiverGroup.groupValue
    }

    override fun getReceiverGroup(): IReceiverGroup? = mRelationAssist.receiverGroup

    override fun updateGroupValue(key: String?, value: Any?) {
        val groupValue = getGroupValue()
        groupValue?.putObject(key, value)
    }
    override fun registerOnGroupValueUpdateListener(onGroupValueUpdateListener: OnGroupValueUpdateListener?) {
        val groupValue = getGroupValue()
        groupValue?.registerOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener: OnGroupValueUpdateListener?) {
        val groupValue = getGroupValue()
        groupValue?.unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener)
    }

    override fun setReceiverGroup(receiverGroup: IReceiverGroup?) {
        mRelationAssist.receiverGroup = receiverGroup
    }
    override fun attachContainer(userContainer: ViewGroup?) {
        mRelationAssist.attachContainer(userContainer)
    }

    override fun play(dataSource: DataSource?) {
        play(dataSource, false)
    }

    override fun play(dataSource: DataSource?, updateRender: Boolean) {
        onSetDataSource(dataSource)
        attachListener()
        stop()
        mRelationAssist.setDataSource(dataSource)
        mRelationAssist.play(updateRender)
    }

    protected abstract fun onSetDataSource(dataSource: DataSource?)

    override fun setDataProvider(dataProvider: IDataProvider?) {
        mRelationAssist.setDataProvider(dataProvider)
    }

    override fun isInPlaybackState(): Boolean {
        val state = getState()
        PLog.d("AssistPlayer", "isInPlaybackState : state = $state")
        return state != IPlayer.STATE_END && state != IPlayer.STATE_ERROR && state != IPlayer.STATE_IDLE && state != IPlayer.STATE_INITIALIZED && state != IPlayer.STATE_PLAYBACK_COMPLETE && state != IPlayer.STATE_STOPPED
    }

    override fun isPlaying(): Boolean {
        return mRelationAssist.isPlaying
    }

    override fun getCurrentPosition(): Int {
        return mRelationAssist.currentPosition
    }

    override fun getState(): Int {
        return mRelationAssist.state
    }

    override fun pause() {
        mRelationAssist.pause()
    }

    override fun resume() {
        mRelationAssist.resume()
    }

    override fun stop() {
        mRelationAssist.stop()
    }

    override fun reset() {
        mRelationAssist.reset()
    }

    override fun rePlay(position: Int) {
        mRelationAssist.rePlay(position)
    }

    override fun destroy() {
        mOnPlayerEventListeners.clear()
        mOnErrorEventListeners.clear()
        mOnReceiverEventListeners.clear()
        val receiverGroup = getReceiverGroup()
        receiverGroup?.clearReceivers()
        mRelationAssist.destroy()
    }
}