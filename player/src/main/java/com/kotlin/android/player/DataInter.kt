package com.kotlin.android.player

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/15
 */
class DataInter {

    class Key {
        companion object {
            //boolean
            const val KEY_ERROR_SHOW_STATE = "error_show_state"

            //boolean
            var KEY_IS_FULL_SCREEN = "is_full_screen"

            //boolean
            var KEY_IS_CONTROLLER_SHOW = "is_controller_show"

            //controller timer update enable
            var KEY_CONTROLLER_TIMER_UPDATE_ENABLE = "timer_update_enable"

            //boolean
            var KEY_LIST_COMPLETE = "is_list_complete"

            //is need play next button
            var KEY_NEED_PLAY_NEXT = "is_need_play_next"

            //complete cover show or hidden
            var KEY_IS_COMPLETE_SHOW = "is_complete_show"

            //boolean type, whether show danmu edit button
            var KEY_DANMU_EDIT_ENABLE = "danmu_edit_enable"

            //whether need show danmu
            var KEY_DANMU_SHOW_STATE = "danmu_show_state"

            var KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE = "danmu_cover_in_edit_state"

            //recommend videos button show or hidden
            var KEY_NEED_RECOMMEND_LIST = "need_recommend_list"

            //definition button show or hidden
            var KEY_NEED_VIDEO_DEFINITION = "need_video_definition"

            //controller top container show or hidden
            var KEY_CONTROLLER_TOP_ENABLE = "controller_top_enable"

            //danmu switch button show or hidden
            var KEY_CONTROLLER_DANMU_SWITCH_ENABLE = "controller_danmu_switch_enable"

            //share button show or hidden
            var KEY_CONTROLLER_SHARE_ENABLE = "controller_share_enable"

            //是否显示全屏按钮
            var KEY_CONTROLLER_FULL_ENABLE = "controller_controller_full_enable"

            //show or hidden
            var KEY_USER_GUIDE_STATE = "user_guide_state"

            //video rate data
            var KEY_VIDEO_RATE_DATA = "video_rate_data"

            var KEY_VIDEO_RATE_NO_DATA = "video_rate_no_data"


            //video play urls info
            var KEY_PROVIDER_PLAY_URL_INFO = "provider_play_url_info"

            //current play definition
            var KEY_CURRENT_DEFINITION = "current_definition"

            //current play url
            var KEY_CURRENT_URL = "video_current_url"

            //string
            var KEY_CURRENT_VIDEO_ID = "video_current_id"

            //in landscape state, is in edit danmu state.
            //when pause if in edit state do not show pause ad cover.
            var KEY_IS_LANDSCAPE_EDIT_DANMU = "is_landscape_edit_danmu"

            //whether or not user pause
            var KEY_IS_USER_PAUSE = "is_user_pause"

            //play video info (vid, sourceType)
            var KEY_VIDEO_INFO = "video_info"

            //video statistics info
            var KEY_STATISTICS_INFO = "statistics_info"

            //danmu toggle button open state
            var KEY_DANMU_OPEN_STATE = "danmu_open_state"

            //whether recommend list is show
            var KEY_RECOMMEND_LIST_STATE = "recommend_list_state"

            //bottom progress bar
            var KEY_NEED_BOTTOM_PROGRESS_BAR = "need_bottom_progress_bar"

            //statistics pager refer info
            var KEY_STATISTICS_PAGE_REFER = "statistics_page_refer"

            //whether need error title
            var KEY_NEED_ERROR_TITLE = "need_error_title"

            //auto play flag
            var KEY_AUTO_PLAY_FLAG = "auto_play_flag"

            //直播-预约
            var KEY_LIVE_APPOINT = "live_appoint"

            //            直播弹幕开关
            var KEY_DANMU_TOGGLE = "live_danmu_toggle"

            //           直播全屏情况下编辑框内容
            var KEY_LIVE_CHAT_CONTENT = "live_chat_content"

            //            全屏状态下发送消息
            var KEY_LIVE_SEND_CHAT_CONTENT = "live_send_chat_content"

            //            直播收起机位提示
            var KEY_CAMERA_STAND_TIPS = "live_camera_stand_tips"

            //            直播弹幕
            var KEY_LIVE_DANMU_CONTENT = "live_danmu_content"

            //            是否是自动播放下一个视频
            var KEY_VIDEO_PLAY_NEXT_AUTO = "video_play_next_auto"

            //直播状态下横竖屏切换
            var KEY_LIVE_CAMERA_LIST_ORIENTATION = "live_camera_list_orientation"

            //直播状态下导播台内容
            var KEY_LIVE_DIRECTOR_UNITS = "live_director_units"

        }
    }

    class CoverLevel {
        companion object {
            //cover级别
            //low
            const val COVER_LEVEL_DANMU: Int = 0
            const val COVER_DIRECTOR_UNITS:Int = 1//导播台
            const val COVER_LEVEL_GESTURE = 2
            const val COVER_LEVEL_LIVE_ERROR = 3//直播错误页面
            const val COVER_LEVEL_LOADING = 4
            const val COVER_LEVEL_PAUSE_AD = 6
            const val COVER_LEVEL_CONTROLLER = 8
            const val COVER_LEVEL_APPOINT = 10//直播预约
            const val COVER_LEVEL_LIVE = 12 //直播

            //medium
            const val COVER_LEVEL_DEFINITION = 0
            const val COVER_LEVEL_RECOMMEND_LIST = 2

            //high
            const val COVER_LEVEL_USER_GUIDE = 0
            const val COVER_LEVEL_ERROR = 4
        }
    }

    class ReceiverKey {
        companion object {
            const val KEY_LOADING_COVER = "loading_cover"
            const val KEY_CONTROLLER_COVER = "controller_cover"
            const val KEY_RECOMMEND_LIST_COVER = "recommend_list_cover"
            const val KEY_ERROR_COVER = "error_cover"
            const val KEY_DANMU_COVER = "danmu_cover"
            const val KEY_PAUSE_AD_COVER = "pause_ad_cover"
            const val KEY_DEFINITION_COVER = "definition_cover"
            const val KEY_GESTURE_COVER = "gesture_cover"
            const val KEY_USER_GUIDE_COVER = "user_guide_cover"
            const val KEY_TOUCH_GESTURE_COVER = "touch_gesture_cover"

            const val KEY_SILENCE_COVER = "silence_cover"

            //must use it
            const val KEY_DATA_RECEIVER = "data_receiver"

            //must use it
            const val KEY_LOG_RECEIVER = "log_receiver"
            //导播台
            const val KEY_DIRECTOR_UNITS_COVER = "director_units_cover"
        }
    }

    class ProducerEvent {
        companion object {
            var EVENT_AIRPLANE_STATE_CHANGE = 300
        }
    }

    class ProviderEvent {
        companion object {
            var EVENT_VIDEO_INFO_READY = 100
            var EVENT_VIDEO_RATE_DATA = 101
            var EVENT_LIVE_CAMERA_LIST = 102//直播机位列表
            var EVENT_LIVE_SEND_CHAT_CONTENT = 103//发送聊天内容
            var EVENT_LIVE_DANMU = 104//接收弹幕
            var EVENT_LIVE_CAMERA_LIST_ORIENTATION = 105//机位横竖屏切换
            var EVENT_VIDEO_NO_RATE_DATA = 106//视频没有清晰度
            var EVENT_LIVE_DIRECTOR_UNITS = 107//导播台
        }
    }

    class ReceiverEvent {
        companion object {
            var EVENT_DANMU_COVER_SEND_DANMU = 202

            var EVENT_REQUEST_EDIT_DANMU = 204

            var EVENT_DANMU_EDIT_TEXT_CHANGE = 205

            var EVENT_CODE_ERROR_FEED_BACK = 206

            var EVENT_REQUEST_SHOW_DEFINITION_LIST = 207

            var EVENT_CODE_SHARE = 208

            var EVENT_REQUEST_BACK = 209

            var EVENT_REQUEST_NEXT_VIDEO = 210

            //include data, show or hidden
            var EVENT_REQUEST_RECOMMEND_LIST_CHANGE = 211

            var EVENT_REQUEST_TOGGLE_SCREEN_STATE = 212

            var EVENT_CODE_UPDATE_SEEK = 214

            //statistics
            var EVENT_CODE_DEFINITION_CHANGE = 215

            //statistics
            var EVENT_CODE_SEEK_START_TRACING_TOUCH = 216

            //statistics
            var EVENT_CODE_SEEK_END_TRACING_TOUCH = 217

            var EVENT_CODE_ON_PAUSE_AD_SHOW = 218
            var EVENT_CODE_ON_PAUSE_AD_CLICK = 219

            //outer to danmu receiver to show danmu
            var EVENT_ADD_DANMU_TO_SHOW = 220

            var EVENT_LIVE_APPOINT_TO_SHOW = 221//直播预约是否显示
            var EVENT_LIVE_CAMERA_VIDEO = 222//直播切换机位
            var EVENT_LIVE_CHAT = 223//直播过程中点击编辑框
            var EVENT_LIVE_DANMU = 224//直播过程弹幕
            var EVENT_VIDEO_COMPLETE = 225//播放结束
            var EVENT_VIDEO_SIZE = 226//视频宽高
        }
    }
}