package com.kotlin.chat_component.inner.interfaces

import com.hyphenate.EMChatRoomChangeListener

/**
 * 不需要实现的方法不再重写
 */
abstract class EaseChatRoomListener : EMChatRoomChangeListener {
    abstract override fun onChatRoomDestroyed(roomId: String, roomName: String)
    abstract override fun onRemovedFromChatRoom(
        reason: Int,
        roomId: String,
        roomName: String,
        participant: String
    )

    abstract override fun onMemberJoined(roomId: String, participant: String)
    abstract override fun onMemberExited(roomId: String, roomName: String, participant: String)

    override fun onMuteListAdded(chatRoomId: String, mutes: List<String>, expireTime: Long) {}
    override fun onMuteListRemoved(chatRoomId: String, mutes: List<String>) {}
    override fun onAdminAdded(chatRoomId: String, admin: String) {}
    override fun onAdminRemoved(chatRoomId: String, admin: String) {}
    override fun onOwnerChanged(chatRoomId: String, newOwner: String, oldOwner: String) {}
    override fun onAnnouncementChanged(chatroomId: String, announcement: String) {}
    override fun onWhiteListAdded(chatRoomId: String, whitelist: List<String>) {}
    override fun onWhiteListRemoved(chatRoomId: String, whitelist: List<String>) {}
    override fun onAllMemberMuteStateChanged(chatRoomId: String, isMuted: Boolean) {}
}