/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kotlin.chat_component.inner.constants

interface EaseConstant {
    companion object {
        const val MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call"
        const val MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call"
        const val MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression"
        const val MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id"
        const val MESSAGE_ATTR_AT_MSG = "em_at_list"
        const val MESSAGE_ATTR_VALUE_AT_MSG_ALL = "ALL"
        const val FORWARD_MSG_ID = "forward_msg_id"
        const val HISTORY_MSG_ID = "history_msg_id"
        const val CHATTYPE_SINGLE = 1
        const val CHATTYPE_GROUP = 2
        const val CHATTYPE_CHATROOM = 3
        const val EXTRA_CHAT_TYPE = "chatType"
        const val EXTRA_CONVERSATION_ID = "conversationId"
        const val EXTRA_IS_ROAM = "isRoaming"
        const val MESSAGE_TYPE_TXT = "txt"
        const val MESSAGE_TYPE_EXPRESSION = "expression"
        const val MESSAGE_TYPE_IMAGE = "image"
        const val MESSAGE_TYPE_VIDEO = "video"
        const val MESSAGE_TYPE_LOCATION = "location"
        const val MESSAGE_TYPE_VOICE = "voice"
        const val MESSAGE_TYPE_FILE = "file"
        const val MESSAGE_TYPE_CMD = "cmd"
        const val MESSAGE_TYPE_RECALL = "message_recall"
        const val MESSAGE_TYPE_VOICE_CALL = "voice_call"
        const val MESSAGE_TYPE_VIDEO_CALL = "video_call"
        const val MESSAGE_TYPE_CONFERENCE_INVITE = "conference_invite"
        const val MESSAGE_TYPE_LIVE_INVITE = "live_invite"
        const val MESSAGE_FORWARD = "message_forward"
        const val MESSAGE_CHANGE_RECEIVE = "message_receive"
        const val MESSAGE_CHANGE_CMD_RECEIVE = "message_cmd_receive"
        const val MESSAGE_CHANGE_SEND_SUCCESS = "message_success"
        const val MESSAGE_CHANGE_SEND_ERROR = "message_error"
        const val MESSAGE_CHANGE_SEND_PROGRESS = "message_progress"
        const val MESSAGE_CHANGE_RECALL = "message_recall"
        const val MESSAGE_CHANGE_CHANGE = "message_change"
        const val MESSAGE_CHANGE_DELETE = "message_delete"
        const val MESSAGE_CALL_SAVE = "message_call_save"
        const val CONVERSATION_DELETE = "conversation_delete"
        const val CONVERSATION_READ = "conversation_read"
        const val GROUP_LEAVE = "group_leave"
        const val DEFAULT_SYSTEM_MESSAGE_ID = "em_system"
        const val DEFAULT_SYSTEM_MESSAGE_TYPE = "em_system_type"
        const val USER_CARD_EVENT = "userCard"

        //时光对方用户信息
        const val KEY_CHAT_OTHER_MTIME_ID = "message_key_other_mtime_id"
        const val KEY_CHAT_OTHER_NICK_NAME = "message_key_other_nick_name"
        const val KEY_CHAT_OTHER_HEAD = "message_key_other_head"
        const val KEY_CHAT_OTHER_AUTH_TYPE = "message_key_other_auth_type"
        const val KEY_CHAT_OTHER_AUTH_ROLE = "message_key_other_auth_role"
    }
}