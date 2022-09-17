package com.kotlin.android.live.component.constant

/**
 * create by lushan on 2021/3/3
 * description:直播相关常量
 */

const val LIVE_STATUS_APPOINT = 1L//直播前状态
const val LIVE_STATUS_LIVING = 2L//直播中状态
const val LIVE_STATUS_END = 3L//直播结束状态
const val LIVE_STATUS_REVIEW = 4L//直播精彩回顾


const val LIVE_UN_APPOINT = 0L//未预约
const val LIVE_HAD_APPOINT = 1L//已预约

/**
 * 是否支持购票 0不支持购票 1购票  2预售
 */
const val LIVE_MOVIE_TICKET_STATUS_NO = 0L
const val LIVE_MOVIE_TICKET_STATUS_TICKET = 1L
const val LIVE_MOVIE_TICKET_STATUS_PRESELL = 2L

const val SOCKET_CMD_CODE_1 = 1L//聊天室踢人
const val SOCKET_CMD_CODE_2 = 2L//聊天室禁言
const val SOCKET_CMD_CODE_101 = 101L//机位上下架通知
const val SOCKET_CMD_CODE_102 = 102L//流地址切换通知
const val SOCKET_CMD_CODE_103 = 103L//流状态变更
const val SOCKET_CMD_CODE_104 = 104L//直播状态变更：1.直播前 2.直播中 3.直播结束 4.直播后
const val SOCKET_CMD_CODE_105 = 105L//观看直播人数统计通知
const val SOCKET_CMD_CODE_106 = 106L//机位变更
const val SOCKET_CMD_CODE_1001 = 1001L//口令红包
const val SOCKET_CMD_CODE_1002 = 1002L//点赞


