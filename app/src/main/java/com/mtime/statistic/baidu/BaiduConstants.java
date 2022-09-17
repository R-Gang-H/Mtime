package com.mtime.statistic.baidu;

/**
 * Created by LiJiaZhi on 2017/9/7.
 *  百度统计
 */

public class BaiduConstants {
    // 影片详情页
    public final  static String BAIDU_EVENTID_MOVIEVIEW_LOADTIMEWIFI = "10002";// eventid影片详情页加载时间wifi
    public final  static String BAIDU_EVENTID_MOVIEVIEW_LOADTIME = "10001";// eventid影片详情页加载非wifi
    public final  static String BAIDU_EVENTID_SELECTION_LOAD = "10009";// eventid进入
    public final  static String BAIDU_EVENTID_SELECTION_LOADTIMEWIFI = "10008";// eventid精选页加载时间
    // wifi static 
    public final  static String BAIDU_EVENTID_SELECTION_LOADTIME = "10007";// eventid精选页加载时间
    // 非wifi static 
    public final  static String BAIDU_EVENTID_APP_OPEN = "10011";// eventid app启动
    public final  static String BAIDU_EVENTID_APP_OPENTIME = "10013";// eventid
    // app启动时间非wifi
    public final static String BAIDU_EVENTID_APP_OPENTIMEWIFI = "10012";// eventid
    // app启动时间wifi

    // tabs
    public final static String BAIDU_EVENTID_SWITCH_MYTAB = "10068";
    public final static String BAIDU_EVENTID_SWITCH_FINDTAB = "10067";
    public final static String BAIDU_EVENTID_SWITCH_SHOPTAB = "10066";
    public final static String BAIDU_EVENTID_SWITCH_TICKETTAB = "10065";
    public final static String BAIDU_EVENTID_SWITCH_HOMETAB = "10064";

    // register
    public final static String BAIDU_EVENTID_REGISTER_VIEW = "10050"; // 进入注册页
    public final static String BAIDU_EVENTID_REGISTER_PHONE_SUCCEEDED = "10051"; // 手机注册成功

    // seat and ticket
    public final static String BAIDU_EVENTID_SEATS_VIEW = "10052"; // 进入选座购票页
    public final static String BAIDU_EVENTID_ORDER_ENSURE_VIEW = "10053"; // 进入确认订单页
    public final static String BAIDU_EVENTID_BUY_TICKET_SUCCEEDED = "10055"; // 成功出票
    public final static String BAIDU_EVENTID_LOCK_SEATS_FAILED = "10056";
    public final static String BAIDU_EVENTID_BUY_TICKET_FAILED = "10058"; // 出票失败

    // view click
    public final static String BAIDU_EVENTID_HOME_CLICKED = "10059"; // 首页点击行为
    public final static String BAIDU_EVENTID_HOME_SCROLL = "10088"; // 首页滚动事件
    public final static String BAIDU_EVENTID_PAYOFF_CLICKED = "10060"; // 支付页点击
    // share type
    public final static String BAIDU_EVENTID_SHARE_TYPE = "10061"; // 分享方式
    public final static String BAIDU_EVENTID_SHARE_COMMENT_CLICKED = "10122"; // 分享评分评论页的点击
    public final static String BAIDU_EVENTID_SHARE = "10123"; // 评分页
    // login type
    public final static String BAIDU_EVENTID_LOGIN_TYPE = "10062"; // 登录页 -登录方式
    // bind result
    public final static String BAIDU_EVENTID_BIND_RESULT = "10063"; // 第三方登录后绑定账号

    // mall part
    public final static String BAIDU_EVENTID_MALL_PRODUCTS_LIST_VIEW = "10071"; // 进入 列表页
    public final static String BAIDU_EVENTID_MALL_GOODS_VIEW = "10072"; // 进入 商品详情页
    public final static String BAIDU_EVENTID_MALL_CART_VIEW = "10073"; // 进入 购物车
    public final static String BAIDU_EVENTID_MALL_ORDER_VIEW = "10074"; // 进入 订单填写
    public final static String BAIDU_EVENTID_MALL_PAY_VIEW = "10075"; // 进入 支付页
    public final static String BAIDU_EVENTID_MALL_PAY_SUCCED = "10076";// 支付成功
    public final static String BAIDU_EVENTID_MALL_HOME_TITLE_CLICK = "10078"; // 商城首页顶部栏点击行为
    public final static String BAIDU_EVENTID_MALL_GOODS_VIEW_TITLE_CLICK = "10077"; // 商品详情页顶部栏点击行为

    public final static String BAIDU_EVENTID_HOME_POPAD_CLICK = "10079"; // 首页弹窗广告点击行为
    public final static String BAIDU_EVENTID_HELP_PHONE_CLICK = "10080"; // 用户吊起打电话服务

    public final static String BAIDU_EVENTID_MALL_PAY_CLICK = "10099"; // 商品支付页

    // 购票填写订单页时放弃
    public final static String BAIDU_EVENTID_GIVEUP_WRITE_TICKET_ORDER = "10083";
    // 商品订单填写页时放弃
    public final static String BAIDU_EVENTID_GIVEUP_WRITE_GOODS_ORDER = "10084";
    // 购买商品时放弃
    public final static String BAIDU_EVENTID_GIVEUP_BUY_GOODS = "10082";
    // 卖票时放弃
    public final static String BAIDU_EVENTID_GIVEUP_BUY_TICKETS = "10081";

    // 商城首页页面点击行为
    public final static String BAIDU_EVENTID_MALL_HOME_VIEW_CLICK = "10085";
    // 商城首页滑动行为
    public final static String BAIDU_EVENTID_MALL_HOME_VIEW_SCROLL = "10086";
    // 设置页海报屏蔽行为
    public final static String BAIDU_EVENTID_SET_MOVIECOVER_FILTER = "10087";
    // 购物车点击形象
    public final static String BAIDU_EVENTID_CART_CLICK = "10090";
    // 影片短评点击行为
    public final static String BAIDU_EVENTID_MOVIE_SHORT_COMMENTS_CLICK = "10089";
    // 全球票房榜
    public final static String BAIDU_EVENTID_GLOBAL_BOX_OFFICE_CLICK = "10091";
    // 时光热度榜
    public final static String BAIDU_EVENTID_MTIME_TOP_LIST_CLICK = "10092";
    // 影片资料页点击事件
    // public final static String BAIDU_EVENTID_MOVIE_VIEW_CLICK = "10093";
    // 正在热映/即将上映 影片详情页 点击行为
    public final static String BAIDU_EVENTID_PLAYING_MOVIE_VIEW_CLICK = "10094";
    // 非 正在热映/即将上映 影片详情页 点击行为
    // public final static String BAIDU_EVENTID_PLAYED_MOVIE_VIEW_CLICK = "10095";
    // 10096 商品评价页面点击行为
    public final static String BAIDU_EVENTID_GOODS_COMMENTS_VIEW_CLICK = "10096";
    // 商品评价页面展示
    public final static String BAIDU_EVENTID_GOODS_COMMENTS_VIEW = "10097";
    // 首页观影后评论引导条曝光
    public final static String BAIDU_EVENTID_HOME_REVIEW_GUIDE_SHOW = "10098";
    // 发现 点击_滑动行为
    public final static String BAIDU_EVENTID_FIND_CLICK_OR_SCROLL = "10100";
    // 首页启动倒计时广告点击
    public final static String BAIDU_EVENTID_LAUNCHER_AD_CLICK = "10101";
    // 新闻详情页下方点击事件
    public final static String BAIDU_EVENTID_NEWS_CLICK = "10102";
    // 搜索页点击事件
    public final static String BAIDU_EVENTID_SEARCH_CLICK = "10104";
    public final static String BAIDU_EVENTID_ORDER_PAY_ACTIVITY_CLICK = "10109";
    public final static String BAIDU_EVENTID_ORDER_PAY_ACTIVITY_BINDPHONE = "10111";

    // 选电影事件
    public final static String BAIDU_EVENTID_CHOOSEMOVIE_SOURCE = "10105"; // 选电影入口来源
    public final static String BAIDU_EVENTID_CHOOSEMOVIE_SHOW = "10106"; // 选电影页展示
    public final static String BAIDU_EVENTID_CHOOSEMOVIE_CLICK = "10107"; // 选电影页点击
    public final static String BAIDU_EVENTID_CHOOSEMOVIE_SORT_CLICK = "10108"; // 选电影页排序点击

    // 直播相关
    public final static String BAIDU_EVENTID_LIVE_MOVIE_LIST_CLICK = "10120";
    public final static String BAIDU_EVENTID_LIVE_MY_LIST_CLICK = "10113";
    public final static String BAIDU_EVENTID_LIVE_DETAIL_CLICK = "10119";

    // 新版即将上映相关
    public final static String BAIDU_EVENTID_INCOMING_RECOMMENT = "10200";
    public final static String BAIDU_EVENTID_INCOMING_LIST = "10201";
    // 预告片儿点击量
    public final static String BAIDU_EVENTID_TRAILER_CLICK = "10204";
    // 预告片下拉刷新次数
    public final static String BAIDU_EVENTID_TRAILER_REFRESH = "10205";

    // 影评列表点击量
    public final static String BAIDU_EVENTID_FILM_REVIEW_LIST = "10202";
    // 影评列表下拉刷新次数
    public final static String BAIDU_EVENTID_FILM_REVIEW_LIST_PULLDOWN = "10203";
    // 资讯列表点击量
    public final static String BAIDU_EVENTID_NEWS_LIST = "10206";
    // 影评列表下拉刷新次数
    public final static String BAIDU_EVENTID_NEWS_LIST_PULLDOWN = "10207";

    // 影片评分事件
    public final static String BAIDU_EVENTID_CR = "10110";
    // 扫描二维码页点击事件
    public final static String BAIDU_EVENTID_QR_CLICK = "10121";
    // 首页选电影热门分类
    public final static String BAIDU_EVENTID_SELECT_MOVIE_GENERY = "10300";
    // 首页选电影热点专题
    public final static String BAIDU_EVENTID_SELECT_MOVIE_HOTTOPIC = "10301";
    // 首页选电影每日佳片
    public final static String BAIDU_EVENTID_SELECT_MOVIE_GOOD_MOVIE = "10302";
    // 首页选电影榜单
    public final static String BAIDU_EVENTID_SELECT_MOVIE_RECOMEND_TOP = "10303";
    // 正在热映顶部文字链广告
    public final static String BAIDU_EVENTID_HOTPLAY_TOP = "10220";

}
