package com.kotlin.android.router.liveevent

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 3:47 PM
 * 描述:
 */

/**一键登录授权登录状态变化*/
const val AUTH_LOGIN_STATE = "auth_login_state"

/**登录状态变化*/
const val LOGIN_STATE = "login_state"

/**城市信息变动*/
const val CITY_CHANGED = "city_changed"

/**删除相册内容*/
const val DELETE_ALBUM_IMAGE = "delete_album_image"

/**收藏或取消收藏影片、影人、影院、文章、帖子*/
const val COLLECTION_OR_CANCEL = "obj_collection_or_cancel"

/**弹出随机彩蛋弹框*/
const val POPUP_BONUS_SCENE = "popup_bonus_scene"

/**关闭随机彩蛋弹框*/
const val CLOSE_BONUS_SCENE = "close_bonus_scene"

/**直播详情页点击相关视频*/
const val LIVE_DETAIL_CLICK_VIDEO = "live_detail_video_click"

/**直播详情页播放下一个相关视频*/
const val LIVE_DETAIL_PLAY_NEXT_VIDEO = "live_detail_video_play_next"

/**直播详情页切换状态，需要通知详情页fragment*/
const val LIVE_DETAIL_CHANGE_STATUS = "live_detail_change_status"

/**搜索结果页从全部tab跳转各分类tab*/
const val SEARCH_RESULT_TYPE_TAB = "search_result_type_tab"

/**搜索结果页全部tab刷新数据*/
const val SEARCH_RESULT_TAB_ALL_REFRESH = "search_result_tab_all_refresh"

/**登录条款状态变化*/
const val LOGIN_AGREE_STATE = "login_agree_state"
