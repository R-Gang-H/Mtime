package com.mtime.player;

/**
 * Created by JiaJunHui on 2018/6/14.
 */
public interface DataInter {

    interface ProviderEvent {
        int EVENT_VIDEO_INFO_READY = 100;
        int EVENT_VIDEO_RATE_DATA = 101;
    }

    interface ReceiverEvent {

        int EVENT_DANMU_COVER_SEND_DANMU = 202;

        int EVENT_REQUEST_EDIT_DANMU = 204;

        int EVENT_DANMU_EDIT_TEXT_CHANGE = 205;

        int EVENT_CODE_ERROR_FEED_BACK = 206;

        int EVENT_REQUEST_SHOW_DEFINITION_LIST = 207;

        int EVENT_CODE_SHARE = 208;

        int EVENT_REQUEST_BACK = 209;

        int EVENT_REQUEST_NEXT_VIDEO = 210;

        //include data, show or hidden
        int EVENT_REQUEST_RECOMMEND_LIST_CHANGE = 211;

        int EVENT_REQUEST_TOGGLE_SCREEN_STATE = 212;

        int EVENT_CODE_UPDATE_SEEK = 214;

        //statistics
        int EVENT_CODE_DEFINITION_CHANGE = 215;

        //statistics
        int EVENT_CODE_SEEK_START_TRACING_TOUCH = 216;

        //statistics
        int EVENT_CODE_SEEK_END_TRACING_TOUCH = 217;

        int EVENT_CODE_ON_PAUSE_AD_SHOW = 218;
        int EVENT_CODE_ON_PAUSE_AD_CLICK = 219;

        //outer to danmu receiver to show danmu
        int EVENT_ADD_DANMU_TO_SHOW = 220;

    }

    interface ProducerEvent {
        int EVENT_AIRPLANE_STATE_CHANGE = 300;
    }

    interface CoverLevel {

        //low
        int COVER_LEVEL_DANMU = 0;
        int COVER_LEVEL_GESTURE = 2;
        int COVER_LEVEL_LOADING = 4;
        int COVER_LEVEL_PAUSE_AD = 6;
        int COVER_LEVEL_CONTROLLER = 8;

        //medium
        int COVER_LEVEL_DEFINITION = 0;
        int COVER_LEVEL_RECOMMEND_LIST = 2;

        //high
        int COVER_LEVEL_USER_GUIDE = 0;
        int COVER_LEVEL_ERROR = 4;
    }

    interface ReceiverKey {
        String KEY_LOADING_COVER = "loading_cover";
        String KEY_CONTROLLER_COVER = "controller_cover";
        String KEY_RECOMMEND_LIST_COVER = "recommend_list_cover";
        String KEY_ERROR_COVER = "error_cover";
        String KEY_DANMU_COVER = "danmu_cover";
        String KEY_PAUSE_AD_COVER = "pause_ad_cover";
        String KEY_DEFINITION_COVER = "definition_cover";
        String KEY_GESTURE_COVER = "gesture_cover";
        String KEY_USER_GUIDE_COVER = "user_guide_cover";

        String KEY_SILENCE_COVER = "silence_cover";

        //must use it
        String KEY_DATA_RECEIVER = "data_receiver";
        //must use it
        String KEY_LOG_RECEIVER = "log_receiver";
    }

    interface Key {
        //boolean
        String KEY_ERROR_SHOW_STATE = "error_show_state";

        //boolean
        String KEY_IS_FULL_SCREEN = "is_full_screen";

        //boolean
        String KEY_IS_CONTROLLER_SHOW = "is_controller_show";

        //controller timer update enable
        String KEY_CONTROLLER_TIMER_UPDATE_ENABLE = "timer_update_enable";

        //boolean
        String KEY_LIST_COMPLETE = "is_list_complete";

        //is need play next button
        String KEY_NEED_PLAY_NEXT = "is_need_play_next";

        //complete cover show or hidden
        String KEY_IS_COMPLETE_SHOW = "is_complete_show";

        //boolean type, whether show danmu edit button
        String KEY_DANMU_EDIT_ENABLE = "danmu_edit_enable";

        //whether need show danmu
        String KEY_DANMU_SHOW_STATE = "danmu_show_state";

        String KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE = "danmu_cover_in_edit_state";

        //recommend videos button show or hidden
        String KEY_NEED_RECOMMEND_LIST = "need_recommend_list";

        //definition button show or hidden
        String KEY_NEED_VIDEO_DEFINITION = "need_video_definition";

        //controller top container show or hidden
        String KEY_CONTROLLER_TOP_ENABLE = "controller_top_enable";

        //danmu switch button show or hidden
        String KEY_CONTROLLER_DANMU_SWITCH_ENABLE = "controller_danmu_switch_enable";

        //share button show or hidden
        String KEY_CONTROLLER_SHARE_ENABLE = "controller_share_enable";

        //show or hidden
        String KEY_USER_GUIDE_STATE = "user_guide_state";

        //video rate data
        String KEY_VIDEO_RATE_DATA = "video_rate_data";

        //video play urls info
        String KEY_PROVIDER_PLAY_URL_INFO = "provider_play_url_info";

        //current play definition
        String KEY_CURRENT_DEFINITION = "current_definition";

        //current play url
        String KEY_CURRENT_URL = "video_current_url";
        //string
        String KEY_CURRENT_VIDEO_ID = "video_current_id";

        //in landscape state, is in edit danmu state.
        //when pause if in edit state do not show pause ad cover.
        String KEY_IS_LANDSCAPE_EDIT_DANMU = "is_landscape_edit_danmu";

        //whether or not user pause
        String KEY_IS_USER_PAUSE = "is_user_pause";

        //play video info (vid, sourceType)
        String KEY_VIDEO_INFO = "video_info";

        //video statistics info
        String KEY_STATISTICS_INFO = "statistics_info";

        //danmu toggle button open state
        String KEY_DANMU_OPEN_STATE = "danmu_open_state";

        //whether recommend list is show
        String KEY_RECOMMEND_LIST_STATE = "recommend_list_state";

        //bottom progress bar
        String KEY_NEED_BOTTOM_PROGRESS_BAR = "need_bottom_progress_bar";

        //statistics pager refer info
        String KEY_STATISTICS_PAGE_REFER = "statistics_page_refer";

        //whether need error title
        String KEY_NEED_ERROR_TITLE = "need_error_title";

        //auto play flag
        String KEY_AUTO_PLAY_FLAG = "auto_play_flag";

    }

}
