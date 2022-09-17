package com.mtime.widgets;


public abstract class BaseTitleView {
    public static final float MIN_ALPHA = 0.0f;

    public enum StructType {
        TYPE_HOME_SHOW_LOGO,
        TYPE_HOME_SHOW_MOVINGS,
        TYPE_SEARCH_SHOW_SEARCH_ONLY,
        TYPE_SEARCH_SHOW_TITLE_ONLY,
        TYPE_NORMAL_SHOW_ALL,
        TYPE_NORMAL_SHOW_BACK_TITLE,
        TYPE_NORMAL_SHOW_BACK_SHARE_FAVORITE,
        TYPE_NORMAL_SHOW_BACK_FAVORITE,
        TYPE_NORMAL_SHOW_BACK_TITLE_SHARE_FAVORITE,
        TYPE_NORMAL_SHOW_BACK_TITLE_FAVORITE,
        TYPE_NORMAL_SHOW_BACK_TITLE_SHARE,
        TYPE_NORMAL_SHOW_BACK_ONLY,
        TYPE_NORMAL_SHOW_MESSAGE_ONLY,
        TYPE_NORMAL_SHOW_TITLE_ONLY,
        TYPE_LOGIN_SHOW_LOGO_ONLY,
        TYPE_LOGIN_SHOW_LOGO_SKIP,
        TYPE_LOGIN_SHOW_TITLE_ONLY,
        TYPE_LOGIN_SHOW_TITLE_SKIP,
        TYPE_NORMAL_SHOW_BACK_TITLE_FEEDBACKLIST,
        TYPE_NORMAL_SHOW_BACK_TITLE_TIMER,
        TYPE_NORMAL_SHOW_BACK_TITLE_PACKET_RULE,
        TYPE_SEARCH_SHOW_TITLE_NO_AUTOSEARCH,
        TYPE_TITLE_DRAEABLELEFT_ATTENTION,
        TYPE_TITLE_SIGN_IN
    }

    public enum ActionType {
        TYPE_UNKNOWN,
        TYPE_BACK,
        TYPE_SCAN,
        TYPE_CANCEL,
        TYPE_SEARCH,
        TYPE_CONTENT_CHANGED,
        TYPE_SHARE,
        TYPE_MORE,
        TYPE_FAVORITE,
        TYPE_SKIP,
        TYPE_PACKET,
        TYPE_FEEDBACKLIST,
        TYPE_SETTING,
        TYPE_SETTING_MESSAGE,
        TYPE_GOODS_CHANGE,
        TYPE_GOODS_EDIT,
        TYPE_GOODS_EDIT_COMPLETE,
        TYPE_GOODS_SAVE,
        TYPE_GOODES_FILTER,
        TYPE_MOVIESHOWTIME_CLEAN_FILTER,
        TYPE_TITLE,
        TYPE_ATTENTION,
        TYPE_SIGN_IN

    }

    public interface ITitleViewLActListener {

        /**
         * @param type
         * @param content maybe null
         */
        void onEvent(final ActionType type, final String content);
    }

    public abstract void setAlpha(final float alpha);
}
