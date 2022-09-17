package com.kotlin.android.share

/**
 * 分享类型：图文，仅图片，音频，app，小程序，圈子，发表说说或图片，发表视频
 *
 * Created on 2020/6/23.
 *
 * @author o.s
 */
enum class ShareType {
    SHARE_IMAGE_TEXT,
    SHARE_TEXT,
    SHARE_IMAGE,
    SHARE_MULTI_IMAGE,
    SHARE_MUSIC,
    SHARE_VIDEO,
    SHARE_APP,//qq已废弃
    SHARE_MINI_PROGRAM,
    SHARE_TIMELINE,
    SHARE_MINI_PROGRAM_TO_TIMELINE,
    PUBLISH_IMAGE_TEXT,
    PUBLISH_VIDEO,
}

/**
const val ACTION_AUTHORIZING = 1
const val ACTION_GETTING_FRIEND_LIST = 2
const val ACTION_SENDING_DIRECT_MESSAGE = 5
const val ACTION_FOLLOWING_USER = 6
const val ACTION_TIMELINE = 7
const val ACTION_USER_INFOR = 8
const val ACTION_SHARE = 9
const val ACTION_GETTING_BILATERAL_LIST = 10
const val ACTION_GETTING_FOLLOWER_LIST = 11
const val ACTION_CUSTOMER = 655360
const val CUSTOMER_ACTION_MASK = 65535
const val SHARE_TEXT = 1
const val SHARE_IMAGE = 2
const val SHARE_WEBPAGE = 4
const val SHARE_MUSIC = 5
const val SHARE_VIDEO = 6
const val SHARE_APPS = 7
const val SHARE_FILE = 8
const val SHARE_EMOJI = 9
const val SHARE_ZHIFUBAO = 10
const val SHARE_WXMINIPROGRAM = 11
const val OPEN_WXMINIPROGRAM = 12
*/