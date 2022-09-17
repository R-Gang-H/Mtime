package com.mtime.bussiness.ticket.api;

import android.text.TextUtils;

import androidx.collection.ArrayMap;

import com.kotlin.android.app.data.entity.cinema.RcmdTicketShowtime;
import com.kotlin.android.app.data.entity.mine.CollectionCinema;
import com.kotlin.android.app.data.entity.mine.UserCollectQuery;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.ADTotalBean;
import com.mtime.bussiness.common.bean.CommonRegionPusblish;
import com.mtime.bussiness.mine.bean.CompanyDetailBean;
import com.mtime.bussiness.ticket.bean.CollectResultBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaDetailBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaScreeningBean;
import com.mtime.bussiness.ticket.cinema.bean.FavAndBeenCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.OnlineCityCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.RefoundTicketMsgBean;
import com.mtime.bussiness.ticket.movie.bean.DirectSellingOrderPrepareBean;
import com.mtime.bussiness.ticket.movie.bean.ExternalPlayInfosBean;
import com.mtime.bussiness.ticket.movie.bean.MovieShowTimeCinemaMainBean;
import com.mtime.bussiness.ticket.movie.bean.SeatInfoJsonBean;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeDataMainBean;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.movie.bean.TicketRealNameReservationBean;
import com.mtime.common.utils.LogWriter;
import com.mtime.network.ConstantUrl;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vivian.wei on 2017/10/16.
 * 购票Api
 */

public class TicketApi extends BaseApi {

    // 热门点击_业务类型
    public static final int SEARCH_POPULAR_CLICK_TYPE_MOVIE = 1;
    public static final int SEARCH_POPULAR_CLICK_TYPE_PERSON = 2;
    // 热门点击_子业务类型
    public static final int SEARCH_POPULAR_CLICK_SUB_TYPE_PERSON = -1;
    public static final int SEARCH_POPULAR_CLICK_SUB_TYPE_MOVIE = 0;
    public static final int SEARCH_POPULAR_CLICK_SUB_TYPE_TV = 1;

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }

    // 获取用户收藏影院列表、上次去过的1家影院
    public void getFavAndBeenCinemas(String locationId, NetworkManager.NetworkListener<FavAndBeenCinemaListBean> listener) {
        Map<String, String> params = new HashMap<>(1);
        params.put("locationId", locationId);
        get(this, ConstantUrl.GET_CINEMA_FAVORITE_LIST, params, listener);
    }

    // 影院列表_商圈和地铁筛选
    public void getCinemaScreening(String locationId, String movieId, String date, NetworkManager.NetworkListener<CinemaScreeningBean> listener) {
        Map<String, String> params = new HashMap<>();
        params.put("locationId", locationId);
        if (!TextUtils.isEmpty(movieId)) {
            params.put("movieId", movieId);
        }
        if (!TextUtils.isEmpty(date)) {
            params.put("date", date);
        }
        get(this, ConstantUrl.GET_CINEMA_SCREENING, params, listener);
    }

    // 广告-根据城市Id过滤
    public void getAdvertisements(String locationId, NetworkManager.NetworkListener<ADTotalBean> listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("locationId", locationId);
        get(this, ConstantUrl.AD_MOBILE_ADVERTISEMENT_INFO, param, 30 * 60 * 1000, listener);
    }

    // 某地区的在线售票影院列表
    public void getOnlineCinemas(String locationId, NetworkManager.NetworkListener<OnlineCityCinemaListBean> listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("locationId", locationId);
        get(this, ConstantUrl.GET_ONLINE_CINEMANS_BY_CITY, param, listener);
    }

    // 指定地区 + 指定电影 有影讯的日期列表
    public void getMovieShowtimeDates(String locationId, String movieId, NetworkManager.NetworkListener<ShowTimeDataMainBean> listener) {
        Map<String, String> param = new HashMap<>(2);
        param.put("locationId", locationId);
        param.put("movieId", movieId);
        get(this, ConstantUrl.GET_MOVIE_SHOWTIMEDATA, param, 180000, listener);
    }

    // 指定地区 + 指定电影 有影讯的影院列表
    public void getLocationMovieShowtimes(String locationId, String movieId, String date,
                                          String deviceToken, String jPushRegID, NetworkManager.NetworkListener<MovieShowTimeCinemaMainBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("locationId", locationId);
        param.put("movieId", movieId);
        param.put("date", date);
        param.put("needAllShowtimes", String.valueOf(true));
        if (!TextUtils.isEmpty(deviceToken)) {
            param.put("deviceToken", deviceToken);
        }
        if (!TextUtils.isEmpty(jPushRegID)) {
            param.put("jPushRegID", jPushRegID);
        }
        post(this, ConstantUrl.GET_MOVIE_LOCSHOWTIMECINEMA, param, listener);
    }

    /**
     * 购票成功后的广告请求
     *
     * @param locationId 城市ID
     * @param movieId    电影ID
     * @param listener
     */
//    public void getADBuyTicketSuccess(String locationId, String movieId, NetworkManager.NetworkListener<BuyTicketSuccessADBean> listener) {
//        Map<String, String> param = new HashMap<>();
//        param.put("movieId", movieId);
//        if (!TextUtils.isEmpty(locationId)) {
//            param.put("locationId", locationId);
//        }
//        get(this, ConstantUrl.POST_BUY_SUCCESS_AD, param, 180000, listener);
//    }

    /**
     * 直销影票详情
     *
     * @param tag
     * @param orderId
     * @param listener
     */
    public void getDirectSaleTicketDetail(String tag, String orderId, String serialNo, NetworkManager.NetworkListener<TicketDetailBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("orderId", orderId);
        param.put("serialNo", serialNo);
        get(tag, ConstantUrl.ONLINE_TICKET_DATEIL, param, 180000, listener);
    }

    /**
     * 获取影院是否可以退票及相关说明(cinema/refundTicket.api)
     *
     * @param cinemaId 影院ID
     */
    public void getCinemaRefoundMsg(String cinemaId, NetworkManager.NetworkListener<RefoundTicketMsgBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("cinemaId", cinemaId);
        get(this, ConstantUrl.REFOUND_TICKET_MSG, param, listener);
    }

    /**
     * 第三方直销跳转数据准备
     *
     * @param platformId
     * @param govCinemaId
     * @param mtimeCinemaId
     * @param showtimeId
     * @param listener
     */
    public void getDirectSaleTicketDetail(long platformId, String govCinemaId, String mtimeCinemaId, String showtimeId, NetworkManager.NetworkListener<DirectSellingOrderPrepareBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("platformId", String.valueOf(platformId));
        param.put("govCinemaId", govCinemaId);
        param.put("mtimeCinemaId", mtimeCinemaId);
        param.put("showtimeId", showtimeId);
        get(this, ConstantUrl.SELLING_ORDER_PREPARE, param, 180000, listener);
    }

    /**
     * 影片外部播放源信息接口
     */
    public void getExternalPlayInfos(String movieId, NetworkManager.NetworkListener<ExternalPlayInfosBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("movieId", movieId);
        get(this, ConstantUrl.GET_MOVIE_EXTERNAL_INFO, param, 180000, listener);
    }

    /**
     * 下载要保存到相册中的图片
     *
     * @param tag
     * @param orderId
     * @param filePath
     * @param listener
     */
    public void downloadTicketPhoto(String tag, String orderId, String filePath, NetworkManager.NetworkProgressListener<String> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("orderId", orderId);
        downloadFile(tag, urlGetParam(param, ConstantUrl.GET_TICKET_OR_ETICKET_IMAGE), filePath, listener);
    }

    private String urlGetParam(final Map<String, String> urlParam, final String url) {
        if (null == urlParam || urlParam.size() == 0 || TextUtils.isEmpty(url)) {
            return url;
        }
        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append(url);
        if (!url.endsWith("?")) {
            urlBuffer.append("?");
        }
        int index = 0;
        try {
            for (Map.Entry<String, String> entry : urlParam.entrySet()) {
                urlBuffer.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                urlBuffer.append("=");
                String value = null != entry.getValue() ? entry.getValue() : "";
                urlBuffer.append(URLEncoder.encode(value, "UTF-8"));
                if (++index >= urlParam.size()) {
                    break;
                }

                urlBuffer.append("&");
            }

            LogWriter.e("checkParam", urlBuffer.toString());
            return urlBuffer.toString();
        } catch (Exception e) {
            LogWriter.e(e.getLocalizedMessage());
        }
        return urlBuffer.toString();
    }

    /**
     * 下载华为需要传给SDK的文件流
     *
     * @param tag
     * @param orderId
     * @param filePath
     * @param listener
     */
    public void downloadHuaWeiData(String tag, String orderId, String filePath, NetworkManager.NetworkProgressListener<String> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("orderId", orderId);
        downloadFile(tag, urlGetParam(param, ConstantUrl.GET_HUA_WEI_WALLET_INFO), filePath, listener);
    }

    /**
     * 获取购票订单实名预约信息
     * @param orderId  订单id
     * @param listener
     */
    public void getRealNameReservationDetail(String orderId, NetworkManager.NetworkListener<TicketRealNameReservationBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("orderId", orderId);
        get(this, ConstantUrl.GET_REAL_NAME_RESERVATION_DETAIL, param, listener);
    }

    /**
     * 收藏/取消收藏(2020.09改版新增接口）
     * @param action    动作 1:收藏 2:取消收藏
     * @param objType   收藏主体类型 MOVIE(1, "电影"), PERSON(2, "影人"), CINEMA(3, "影院"), POST(4, "帖子"), ARTICLE(5, "文章");
     * @param objId     收藏主体ID
     * @param listener
     */
    public void postCollect(int action, Long objType, String objId, NetworkManager.NetworkListener<CollectResultBean> listener) {
        Map<String, String> param = new HashMap<>();
        param.put("action", String.valueOf(action));
        param.put("objType", String.valueOf(objType));
        param.put("objId", objId);
        post(this, ConstantUrl.POST_COMMUNITY_COLLECT, param, listener);
    }

    /**
     * 查询影院详情
     * @param cinemaId
     * @param listener
     */
    public void getCinemaDetail(String cinemaId, NetworkManager.NetworkListener<CinemaDetailBean> listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("cinemaId", cinemaId);
        get(this, ConstantUrl.GET_CINEMA_DETAIL, param, listener);
    }

    /**
     * 查询当前用户的收藏状态
     * @param objType   收藏主体类型 MOVIE(1, "电影"), PERSON(2, "影人"), CINEMA(3, "影院"), POST(4, "帖子"), ARTICLE(5, "文章");
     * @param objId     收藏主体ID
     * @param listener
     */
    public void userCollectQuery(Long objType, String objId, NetworkManager.NetworkListener<UserCollectQuery> listener) {
        Map<String, String> param = new HashMap<>(2);
        param.put("objType", String.valueOf(objType));
        param.put("objId", objId);
        get(this, ConstantUrl.USER_COLLECT_QUERY, param, listener);
    }
    /**
     * 分页查询用户收藏影院
     */
    public void getCollectCinemaList(Long pageIndex, Long pageSize, NetworkManager.NetworkListener<CollectionCinema> listener) {
        Map<String, String> param = new HashMap<>(2);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("pageSize", String.valueOf(pageSize));
        get(this, ConstantUrl.GET_COLLECT_CINEMA_LIST, param, listener);
    }

    /**
     * 获取场次实时座位信息
     * @param dId       场次id,必填
     * @param listener
     */
    public void getSeatInfo(String dId, NetworkManager.NetworkListener<SeatInfoJsonBean> listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("dId", String.valueOf(dId));
        post(this, ConstantUrl.GET_SEAT_INFO, param, listener);
    }

    /**
     * 公司制作发行影片列表
     * @param companyId     制作发行影片公司Id
     * @param pageIndex
     * @param type          制作发行类型，0其他 1制作(默认) 2发行
     * @param listener
     */
    public void getCompanyMakeMovies(String companyId, int pageIndex, int type, NetworkManager.NetworkListener<CompanyDetailBean> listener) {
        Map<String, String> param = new HashMap<>(3);
        param.put("companyId", companyId);
        param.put("pageIndex", String.valueOf(pageIndex));
        param.put("type", String.valueOf(type));
        get(this, ConstantUrl.GET_COMPANY_DETAIL, param, listener);
    }

    /**
     * 热门点击
     * 注：用于搜索统计，将来推送感兴趣的影片影人文章等
     * @param pType     业务类型: 影片 1 、  影人 2、  文章 3
     * @param sType     子业务类型: 影片 0 、 电视剧 1 、 文章、影人 -1
     * @param keyword   影片id 、影人id、文章标题
     */
    public void postSearchPoplarClick(int pType, int sType, String keyword) {
        Map<String, String> param = new ArrayMap<String, String>(3);
        param.put("pType", String.valueOf(pType));
        param.put("sType", String.valueOf(sType));
        param.put("keyword", keyword);
        post(this, ConstantUrl.POST_SEARCH_POPULAR_CLICK, param, null);
    }

     /**
      * 统一获取推荐位数据
     * @param codes
     * @param listener
     */
    public void getRcmdRegionPublishList(String codes, NetworkManager.NetworkListener<CommonRegionPusblish> listener)  {
        Map<String, String> param = new HashMap<>(1);
        param.put("codes", codes);
        get(this, ConstantUrl.GET_RCMD_REGION_PUBLISH, param, listener);
    }

    /**
     * 影院场次列表页公告推荐位
     * @param listener
     */
    public void getRcmdTicketShowtimeNotice(NetworkManager.NetworkListener<RcmdTicketShowtime> listener)  {
        get(this, ConstantUrl.GET_TICKET_SHOWTIME_NOTICE, null, listener);
    }

    /**
     * 影院场次列表页活动推荐位
     * @param listener
     */
    public void getRcmdTicketShowtimeActivity(NetworkManager.NetworkListener<RcmdTicketShowtime> listener)  {
        get(this, ConstantUrl.GET_TICKET_SHOWTIME_ACTIVITY, null, listener);
    }
}
