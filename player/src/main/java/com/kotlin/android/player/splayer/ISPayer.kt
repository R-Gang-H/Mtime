package com.kotlin.android.player.splayer

import android.view.ViewGroup
import com.kk.taurus.playerbase.entity.DataSource
import com.kk.taurus.playerbase.event.OnErrorEventListener
import com.kk.taurus.playerbase.event.OnPlayerEventListener
import com.kk.taurus.playerbase.provider.IDataProvider
import com.kk.taurus.playerbase.receiver.GroupValue
import com.kk.taurus.playerbase.receiver.IReceiverGroup
import com.kk.taurus.playerbase.receiver.IReceiverGroup.OnGroupValueUpdateListener
import com.kk.taurus.playerbase.receiver.OnReceiverEventListener

/**
 * create by lushan on 2020/9/1
 * description:
 */
interface ISPayer {
    companion object{
        val RECEIVER_GROUP_CONFIG_SILENCE_STATE = 1
        val RECEIVER_GROUP_CONFIG_LIST_STATE = 2
        val RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE = 3
        val RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE = 4
        val RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_WITH_OUT_TITLE_STATE = 5
        val RECEIVER_GROUP_CONFIG_FULL_SCREEN_WITH_OUT_TITLE_STATE = 6
    }




    fun addOnPlayerEventListener(onPlayerEventListener: OnPlayerEventListener?)

    fun removePlayerEventListener(onPlayerEventListener: OnPlayerEventListener?): Boolean

    fun addOnErrorEventListener(onErrorEventListener: OnErrorEventListener?)

    fun removeErrorEventListener(onErrorEventListener: OnErrorEventListener?): Boolean

    fun addOnReceiverEventListener(onReceiverEventListener: OnReceiverEventListener?)

    fun removeReceiverEventListener(onReceiverEventListener: OnReceiverEventListener?): Boolean

    fun setReceiverGroup(receiverGroup: IReceiverGroup?)

    fun getReceiverGroup(): IReceiverGroup?

    fun attachContainer(userContainer: ViewGroup?)

    fun play(dataSource: DataSource?)

    fun play(dataSource: DataSource?, updateRender: Boolean)

    fun setDataProvider(dataProvider: IDataProvider?)

    fun getGroupValue(): GroupValue?

    fun updateGroupValue(key: String?, value: Any?)

    fun registerOnGroupValueUpdateListener(onGroupValueUpdateListener: OnGroupValueUpdateListener?)

    fun unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener: OnGroupValueUpdateListener?)

    fun isInPlaybackState(): Boolean

    fun isPlaying(): Boolean

    fun getCurrentPosition(): Int

    fun getState(): Int

    fun pause()

    fun resume()

    fun stop()

    fun reset()

    fun rePlay(position: Int)

    fun destroy()

}