package com.mtime.statistic.large.dailyrecmd;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-14
 */
public interface StatisticDailyRecmd {

    // 今日推荐
    String DAILY_RECOMMEND_PN = "dailyRecommend";

    String CALENDAR = "calendar";

    String POSTER = "poster";

    String CLICK = "click";

    String SHOW = "show";

    String SHARE = "share";

    String DOWNLOAD = "download";

    String MOVIE_ID = "movieID";

    String DAILY_RCMD_MOVIE = "dailyRcmdMovie";

    // 历史推荐
    String HISTORY_RECOMMEND_PN = "historyrecommends";

    // 当日推荐( 今日推荐的不能左右滑版本)
    String ONE_DAY_RCMD = "onedayRcmd";

    // 弹框
    String DAILY_RCMD_POPUP = "dailyRcmdPopup";

    //推荐列表
    String HISTORY_RCMD_LIST = "historyRcmdList";
}
