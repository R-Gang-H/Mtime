package com.mtime.statistic.large.home;

import com.mtime.statistic.large.mine.StatisticMine;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.statistic.large.video.StatisticVideo;

/**
 * Created by vivian.wei on 2017/9/11.
 * 首页数据上报
 * 文档：http://wiki.inc-mtime.com/pages/viewpage.action?pageId=80773254
 */

public class StatisticHome {

    // 页面名称
    public static final String PN_SPLASH = "splash";    // 启动页
    public static final String PN_INTRO = "intro";      // 引导页
    public static final String PN_HOME = "home";        // 首页
    public static final String PN_SEARCH_PRE = "searchPre";     // 搜索前默认页
    public static final String PN_FILM_FILTER = "filmFilter";   // 影片分类筛选页

    //一级界面标示名称
    public static final String TAB_BAR_HOME = PN_HOME;//底部首页
    public static final String TAB_BAR_TICKET = StatisticTicket.PN_TICKET;//底部购票
    public static final String TAB_BAR_GAME = "game";//底部游戏
    public static final String TAB_BAR_MY = StatisticMine.PN_MY;//底部我的
    public static final String TAB_BAR_VIDEO = StatisticVideo.PN_VIDEO_HOME;//底部视频


    // 一级区域标识
    public static final String HOME_TOP_NAV = "topNav";//顶部导航
    public static final String HOME_TOP = "top";//头图
    public static final String HOME_CATEGORY_ENTRY = "categoryEntry";//分类入口
    public static final String HOME_SELECTED = "selected";//精选
    public static final String HOME_FEEDS = "feeds";//咨询列表
    public static final String HOME_TICKET = "ticket";//购票
    public static final String HOME_AD = "ad";//购票

    // 搜索_一级区域标识
    public static final String HOME_SEARCH = "search";//搜索
    public static final String HOME_SEARCH_BACK = "back";//首页搜索界面的返回
    public static final String HOME_SEARCH_SCANQR = "scanQR";//首页搜索界面的扫一扫
    public static final String HOME_SEARCH_AD = "ad";//首页搜索界面的广告
    public static final String HOME_FILM_FILTER = "filmFilter";//首页搜索界面的影片分类
    public static final String HOME_POPULAR_SEARCH = "popularSearch";//首页搜索界面的热门搜索区
    public static final String HOME_SEARCH_HISTORY = "searchHistory";//首页搜索界面的搜索记录
    public static final String HOME_SEARCH_CLEAR_HISTORY = "clearSearchHistory";//首页搜索界面的搜索记录
    public static final String HOME_SEARCH_FILM = "film";//首页搜索界面的搜索影片结果
    public static final String HOME_SEARCH_PEOPLE = "people";//首页搜索界面的搜索影人结果
    public static final String HOME_SEARCH_THEATER = "theater";//首页搜索界面的搜索影院结果
    public static final String HOME_SEARCH_PRODUCT = "product";//首页搜索界面的搜商品结果
    public static final String HOME_SEARCH_PRODUCT_MORE = "productMore";//首页搜索界面的搜商品结果更多商品
    public static final String HOME_SEARCH_PRE_TAB = "tab";                 // 频道分类

    // 影片分类筛选页_一级区域标识
    public static final String HOME_FILM_FILTER_BACK = "back";              // 返回按钮
    public static final String HOME_FILM_FILTER_TAB = "tab";                // 电影分类
    public static final String HOME_FILM_FILTER_FILM = "film";              // 影片信息体

    //add 2017/11/29
    public static final String HOME_RECOMMEND = "recommend";//首页推荐
    public static final String HOME_BOX_OFFICE = "boxOffice";//首页票房
    public static final String HOME_ORIGINAL = "original";//首页原创
    public static final String HOME_MTIME_MEDIA = "mtimeMedia";//首页时光号
    public static final String HOME_TRAILER = "trailer";        // 首页预告片

    //二级标示
    public static final String HOME_TOP_NAV_SELECT_CITY = "selectCity";//切换城市
    public static final String HOME_TOP_NAV_SEARCH = "search";//搜索
    public static final String HOME_TOP_NAV_SCAN_QR = "scanQR";//扫一扫
    public static final String HOME_CATEGORY_ENTRY_SELECTED = "selected";//分类入口-精选
    public static final String HOME_CATEGORY_ENTRY_NEW_LIST = "newsList";//分类入口-资讯
    public static final String HOME_CATEGORY_ENTRY_FILM_SELECTION = "filmSelection";//分类入口-选电影
    public static final String HOME_CATEGORY_ENTRY_TRAILER = "trailer";//分类入口-预告片
    public static final String HOME_CATEGORY_ENTRY_ARTICLE = "article";//分类入口-影评
    public static final String HOME_SELECTED_TICKET = "ticket";//精选-购票

    public static final String HOME_BOX_OFFICE_DOMESTIC = "domestic";//首页票房内地
    public static final String HOME_BOX_OFFICE_US = "US";//首页票房北美
    public static final String HOME_BOX_OFFICE_INTERNATIONAL = "international";//首页票房全球
    public static final String HOME_MTIME_MEDIA_FEED = "feeds";//首页时光号Feed流
    public static final String HOME_MTIME_MEDIA_MEDIA_RECOMMENDATION = "mediaRecommendation";//首页时光号账号推荐
    public static final String TAB_ADS_BANNDER = "adsBanner";//购票成功广告
    public static final String TAB_TOP_REC = "topRec";//首页广告

    //三级标示
    public static final String HOME_BOX_OFFICE_CLICK = "click";//首页票房点击

    public static final String HOME_RECOMMEND_HOT_TICKET_STATE_IN_SALE = "Buy";//首页热映投票点击购票
    public static final String HOME_RECOMMEND_HOT_TICKET_STATE_PRE_SALE = "preBuy";//首页热映投票点击预售
    public static final String HOME_RECOMMEND_HOT_TICKET_STATE_SHOW_TIMES = "showTimes";//首页热映投票点击查影讯


}
