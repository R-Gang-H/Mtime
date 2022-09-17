package com.mtime.statistic.large.video;

/**
 * Created by vivian.wei on 2017/9/11.
 * 视频数据上报
 * 文档：http://wiki.inc-mtime.com/pages/viewpage.action?pageId=80773254
 */

public interface StatisticVideo {

    // 页面名称
    String PN_VIDEO_TOPIC = "videoTopicDetail";           // 视频专题页pageLable
    String PN_VIDEO_TOPIC_COMMENT_LIST = "commentList";   // 视频专题_全部评论页pageLable
    String PN_VIDEO_TOPIC_COMMENT_DETAIL = "commentDetail";   //

    String PN_VIDEO_HOME = "video"; //main页面tab中的视频tab主页面

    String VIDEO_TOPIC_ID = "videoTopicID";            // 视频专题ID
    String VIDEO_ID = "videoId";                       // 视频ID
    String COMMENT_ID = "commentID";                   // 评论ID
    String TRAILER_ID = "trailerID";                   // 预告片ID
    String OPERATING_LISTS_ID = "listID";              // 榜单ID

    String VIDEO_FILM_TV = "filmTV"; //影视tab
    
    String VIDEO_TOP_NAV = "topNav"; //顶部导航
    String VIDEO_TOP_NAV_SEARCH = "search"; //搜索
    
    /**
     * 二级区域标识
     */
    String VIDEO_HOT_FILMS = "hotFilms";                //影视tab-全网热播
    String VIDEO_NEWEST_FILMS = "newestFilms";          //影视tab-最新上线
    String VIDEO_FREE_FILMS = "freeFilms";              //影视tab-免费电影
    String VIDEO_FILMS_COMING_SOON = "filmsComingSoon"; //影视tab-全网即将上线
    String VIDEO_TRAILERS = "trailers";                 //影视tab-新品预告
    String VIDEO_HOT_TELEPLAYS = "hotTeleplays";        //影视tab-热门电视剧
    String VIDEO_OPERATING_LISTS = "operatingLists";    //影视tab-运营榜单

    /**
     * 三级区域标识
     */
    String VIDEO_ITEM_CLICK = "click";                  //点击
    String VIDEO_ITEM_HOT_FILM_MORE = "hotFilmMore";  //全网热播——点击更多
    String VIDEO_ITEM_HOT_FILM_SLIDE = "hotFilmSlide";//全网热播——滑动
    String VIDEO_ITEM_NEWEST_FILM_MORE = "newestFilmMore";  //最新上线——点击更多
    String VIDEO_ITEM_NEWEST_FILM_SLIDE = "newestFilmSlide";//最新上线——滑动
    String VIDEO_ITEM_FREE_FILM_MORE = "freeFilmMore";  //免费电影——点击更多
    String VIDEO_ITEM_FREE_FILM_SLIDE = "freeFilmSlide";//免费电影——滑动
    String VIDEO_ITEM_FILMS_COMING_SOON_MORE = "filmsComingSoonMore";  //全网即将上线——点击更多
    String VIDEO_ITEM_FILMS_COMING_SOON_SLIDE = "filmsComingSoonSlide";//全网即将上线——滑动
    String VIDEO_ITEM_TRAILERS_MORE = "trailersMore";                  //新品预告——点击更多
    String VIDEO_ITEM_HOT_TELEPLAYS_MORE = "hotTeleplaysMore";  //热门电视剧——点击更多
    String VIDEO_ITEM_HOT_TELEPLAYS_SLIDE = "hotTeleplaysSlide";//热门电视剧——滑动
    String VIDEO_ITEM_OPERATING_LISTS_MORE = "operatingListsMore";  //运营榜单——点击更多
    String VIDEO_ITEM_OPERATING_LISTS_SLIDE = "operatingListsSlide";//运营榜单——滑动
}
