package com.mtime.bussiness.ticket.cinema.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.kotlin.android.app.data.entity.mine.CollectionCinema;
import com.mtime.R;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.ticket.cinema.bean.BusinessAreaNewBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaResultFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaScreeningBean;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.bean.FavAndBeenCinemaBean;
import com.mtime.bussiness.ticket.cinema.bean.FavAndBeenCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.StationBean;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBean;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilter;
import com.mtime.bussiness.ticket.movie.bean.MovieShowTimeCinemaBean;
import com.mtime.bussiness.ticket.movie.bean.MovieShowTimeCinemaMainBean;
import com.mtime.bussiness.ticket.movie.bean.V2_ShowTimeCinemaFeature;
import com.mtime.common.utils.DateUtil;
import com.mtime.util.MtimeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by vivian.wei on 2017/11/27.
 * 影院列表Helper
 * Tab_购票_影院（列表）和 影片排片页 公用方法抽取
 */

public class CinemaListHelper {

    // TODO: 2017/12/9 空检查

    // 3D、IMAX、VIP、4D、巨幕、4K、杜比、情侣座
    private static final String[] mFeatureArray = {"不限", "3D", "IMAX", "VIP", "4D", "巨幕", "4K", "杜比", "情侣座"};
    private static final String FEATURE_ALL = "全部";

    // 埋点_影厅特色汉字
    public static String getStatisticFeatureTypeString(int index) {
        return index == 0 ? FEATURE_ALL : mFeatureArray[index];
    }

    // 初始化影厅特效
    public static List<CinemaFeatureBean> initCinemaFeatures() {
        List<CinemaFeatureBean> cinemaFeatures = new ArrayList<>();
        CinemaFeatureBean item;
        for(int i = 0, length = mFeatureArray.length; i < length; i++) {
            item = new CinemaFeatureBean();
            item.setName(mFeatureArray[i]);
            item.setSupport(i == 0);
            cinemaFeatures.add(item);
        }
        return cinemaFeatures;
    }

    // 影片排片页_bean转换（需要与影院列表页共用方法）
    public static List<CinemaBaseInfo> parseToCinemaBaseInfos(String movieId, MovieShowTimeCinemaMainBean mainBean) {
        List<CinemaBaseInfo> infos = new ArrayList<>();
        if(null != mainBean && CollectionUtils.isNotEmpty(mainBean.getCs())) {
            List<MovieShowTimeCinemaBean> cinemaBeans = mainBean.getCs();
            MovieShowTimeCinemaBean cinemaBean;
            V2_ShowTimeCinemaFeature cinemaBeanFeature;
            CinemaBaseInfo info;
            CinemaResultFeatureBean feature;
            for(int i = 0, size = mainBean.getCs().size(); i < size; i++) {
                cinemaBean = cinemaBeans.get(i);
                info = new CinemaBaseInfo();
                info.setAddress(cinemaBean.getAddress());
                info.setCinemaId(cinemaBean.getCid());
                info.setCinameName(cinemaBean.getCn());
                info.setDistrictID(cinemaBean.getDistrictId());
                info.setTicket(cinemaBean.getIsTicket());
                info.setMinPrice(Integer.parseInt(MtimeUtils.formatPrice(cinemaBean.getMinPriceFen())));
                info.setLatitude(cinemaBean.getLatitude());
                info.setLongitude(cinemaBean.getLongitude());
                info.setBaiduLatitude(cinemaBean.getBaiduLatitude());
                info.setBaiduLongitude(cinemaBean.getBaiduLongitude());
                cinemaBeanFeature = cinemaBean.getFeature();
                if(null != cinemaBeanFeature) {
                    feature = new CinemaResultFeatureBean();
                    feature.setHas3D(cinemaBeanFeature.getHas3D());
                    feature.setHasFeature4D(cinemaBeanFeature.getHasFeature4D());
                    feature.setHasFeature4K(cinemaBeanFeature.getHasFeature4K());
                    feature.setHasFeatureDolby(cinemaBeanFeature.getHasFeatureDolby());
                    feature.setHasFeatureHuge(cinemaBeanFeature.getHasFeatureHuge());
                    feature.setHasIMAX(cinemaBeanFeature.getHasIMAX());
                    feature.setHasLoveseat(cinemaBeanFeature.getHasLoveseat());
                    feature.setHasPark(cinemaBeanFeature.getHasPark());
                    feature.setHasServiceTicket(cinemaBeanFeature.getHasServiceTicket());
                    feature.setHasVIP(cinemaBeanFeature.getHasVIP());
                    feature.setHasWifi(cinemaBeanFeature.getHasWifi());
                    feature.setHasSphereX(cinemaBeanFeature.getHasSphereX());
                    feature.setHasScreenX(cinemaBeanFeature.getHasScreenX());
                    info.setFeature(feature);
                }
                info.setCouponActivityList(cinemaBean.getCouponActivityList());
                info.setShowtimeList(cinemaBean.getShowtimeList());
                info.setMovieId(movieId);
                // 第三方直销平台
                info.setDirectSalesFlag(cinemaBean.getDirectSalesFlag());
                info.setDsPlatformId(cinemaBean.getDsPlatformId());
                info.setDsPlatformLabel(cinemaBean.getDsPlatformLabel());
                info.setGovCinemaId(cinemaBean.getGovCinemaId());
                infos.add(info);
            }
        }

        return infos;
    }

    // 影片排片页_日期显示格式
    public static String getStringDate(String dateString) {
        final long time = DateUtil.getDateToLong(DateUtil.sdf1, dateString);
        if (time == 0) {
            return "";
        }
        int gapCount = DateUtil.getGapCount(MTimeUtils.getLastDiffServerDate(), DateUtil.getDateFromStr(dateString));
        if (gapCount == 0) {
            dateString = DateUtil.sdf4.format(new Date(time));
            return String.format("今天(%s)", dateString);
        } else if (gapCount == 1) {
            dateString = DateUtil.sdf4.format(new Date(time));
            return String.format("明天(%s)", dateString);
        } else if (gapCount == 2) {
            dateString = DateUtil.sdf4.format(new Date(time));
            return String.format("后天(%s)", dateString);
        } else if (gapCount >= 3) {
            dateString = DateUtil.sdf11.format(new Date(time));
            return dateString;
        }
        return DateUtil.sdf11.format(new Date(time));
    }

    // 是否次日
    @SuppressLint("SimpleDateFormat")
    public static boolean isTomorrow(final long longTime, String selectDate) {
        if(longTime <= 0 || TextUtils.isEmpty(selectDate)) {
            return false;
        }
        Date date = new Date(0);
        try {
            if (selectDate.contains("-")) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(selectDate);
            } else {
                date = new SimpleDateFormat("yyyyMMdd").parse(selectDate);
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return DateUtil.isTomorrow(new Date(longTime), date);
    }

    /**
     * 将2020新版用户收藏影院接口转换为原来用户收藏去过影院接口实体
     * @param cinemas
     * @return
     */
    public static FavAndBeenCinemaListBean convertFavCinema(CollectionCinema cinemas) {
        if(cinemas == null || CollectionUtils.isEmpty(cinemas.getItems())) {
            return null;
        }
        List<CollectionCinema.Item> items = cinemas.getItems();
        CollectionCinema.Item item;

        FavAndBeenCinemaListBean listBean = new FavAndBeenCinemaListBean();
        List<FavAndBeenCinemaBean> favList = new ArrayList<>(); // 收藏影院列表
        for(int i = 0, size = items.size(); i < size; i++) {
            item = items.get(i);
            FavAndBeenCinemaBean favBean = new FavAndBeenCinemaBean();
            if(item.getObj() == null || item.getObj().getId() <= 0) {
                continue;
            }
            String id = String.valueOf(item.getObj().getId());
            favBean.setCinemaId(Integer.parseInt(id));
            favBean.setName(item.getObj().getName() == null ? "" : item.getObj().getName());
            favList.add(favBean);
        }
        if(CollectionUtils.isNotEmpty(favList)) {
            listBean.setFavoriteList(favList);
        }
        return listBean;
    }

    // 影院排序：此方法可以得到排序好的mNearestCinemas和mNearestCinemas
    public static void sortCinemas(LocationInfo locationInfo,
                                   List<CinemaBaseInfo> allCinemas, FavAndBeenCinemaListBean favAndBeenCinemas,
                                   List<CinemaBaseInfo> nearestCinemas, List<CinemaBaseInfo> lowestPriceCinemas,
                                   List<CinemaFeatureBean> cinemaFeatures) {
        /*
        首位为“我的收藏”影院，取所有收藏的影院，按时间由近及远排。（用户登录状态才支持）
        其次为用户“上次去的”的影院。（用户登录状态才支持）
        其余影院按照距离排序
        */

        if(CollectionUtils.isEmpty(allCinemas)) {
            return;
        }
        if(null != nearestCinemas) {
            nearestCinemas.clear();
        }
        if(null != lowestPriceCinemas) {
            lowestPriceCinemas.clear();
        }
        List<CinemaBaseInfo> favCinemaInfos = new ArrayList<>();
        List<CinemaBaseInfo> otherCinemaInfos = new ArrayList<>();
        CinemaBaseInfo beenInfo = null;

        List<FavAndBeenCinemaBean> favCinemas = null;
        FavAndBeenCinemaBean beenCinema = null;
        if(null != favAndBeenCinemas) {
            favCinemas = favAndBeenCinemas.getFavoriteList();
            beenCinema = favAndBeenCinemas.getBeenCinema();
        }

        CinemaBaseInfo info;
        FavAndBeenCinemaBean fav;
        double distance;
        CinemaResultFeatureBean feature;
        for(int i = 0, size = allCinemas.size(); i < size; i++) {
            info = allCinemas.get(i);
            if(null != locationInfo && !locationInfo.isLose()) {
                distance = MtimeUtils.gps2m(locationInfo.getLatitude(), locationInfo.getLongitude(),
                        info.getBaiduLatitude(), info.getBaiduLongitude());
                info.setDistance(distance);
            }
            info.setFavorit(false);
            info.setBeen(false);

            feature = info.getFeature();
            // 设置筛选组件支持的影厅特效
            setSupportFeatures(feature, cinemaFeatures);

            if(null != lowestPriceCinemas && info.isTicket() && info.getMinPrice() > 0) {
                lowestPriceCinemas.add(info);
            }
            // 收藏
            if(CollectionUtils.isNotEmpty(favCinemas)) {
                boolean addFav = false;
                for (int j = 0, favSize = favCinemas.size(); j < favSize; j++) {
                    fav = favCinemas.get(j);
                    if(info.getCinemaId() == fav.getCinemaId()) {
                        info.setFavorit(true);
                        favCinemaInfos.add(info);
                        addFav = true;
                        break;
                    }
                }
                if(addFav) {
                    continue;
                }
            }
            // 去过
            if(null == beenInfo && null != beenCinema && info.getCinemaId() == beenCinema.getCinemaId()) {
                info.setBeen(true);
                beenInfo = info;
                continue;
            }
            // 其他
            otherCinemaInfos.add(info);
        }

        // 收藏影院favCinemaInfos按favCinemas顺序排序(api已经按距离排好）
        if(CollectionUtils.isNotEmpty(favCinemas) && CollectionUtils.isNotEmpty(favCinemaInfos)) {
            for (int i = 0, favSize = favCinemas.size(); i < favSize; i++) {
                fav = favCinemas.get(i);
                for (int j = 0, topSize = favCinemaInfos.size(); j < topSize; j++) {
                    info = favCinemaInfos.get(j);
                    if (fav.getCinemaId() == info.getCinemaId()) {
                        nearestCinemas.add(info);
                        break;
                    }
                }
            }
        }
        // 添加去过
        if(null != beenInfo) {
            nearestCinemas.add(beenInfo);
        }
        // 添加按距离排序的影院
        if(null != locationInfo && !locationInfo.isLose()) {
            Comparator sort1 = new DistanceSort();
            Collections.sort(otherCinemaInfos, sort1);
        }
        nearestCinemas.addAll(otherCinemaInfos);

        // 最低票价影院列表排序
        Comparator sort2 = new PriceSort();
        Collections.sort(lowestPriceCinemas, sort2);
    }

    // 获取排序筛选后用于显示的影院列表
    public static List<CinemaBaseInfo> getShowCinemas(CinemaFilter.FilterEventType sortType, int featureIndex,
                                                      int districtId, int businessId,
                                                      int subwayId, int stationId,
                                                      List<CinemaBaseInfo> nearestCinemas,
                                                      List<CinemaBaseInfo> lowestPriceCinemas,
                                                      List<DistrictBean> districts, List<SubwayBean> subways) {
        // 排序
        List<CinemaBaseInfo> showCinemas =
                sortType == CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE ? nearestCinemas : lowestPriceCinemas;
        // 特效
        if(featureIndex > 0) {
            showCinemas = calFeatureCinemas(featureIndex, showCinemas);
        }
        // 商圈与地铁互斥
        if(districtId > 0) {
            if(businessId > 0) {
                showCinemas = calBusinessCinemas(businessId, districtId, showCinemas, districts);
            } else {
                showCinemas = calDistrictCinemas(districtId, showCinemas, districts);
            }
        } else if(subwayId > 0) {
            if(stationId > 0) {
                showCinemas = calStationCinemas(stationId, subwayId, showCinemas, subways);
            } else {
                showCinemas = calSubwayCinemas(subwayId, showCinemas, subways);
            }
        }
        return showCinemas;
    }

    // 商圈和地铁添加全部和数量
    public static void parseDistrictSubwayData(Context context, CinemaScreeningBean result,
                                               List<DistrictBean> districts, List<SubwayBean> subways) {
        if (null == context || null == districts || null == subways)
            return;
        // 商圈: 添加全部及其数量
        districts.clear();
        String all = context.getResources().getString(R.string.s_all);
        DistrictBean districtBean = new DistrictBean();
        districtBean.setName(all);
        districtBean.setDistrictId(0);
        districts.add(districtBean);
        if(CollectionUtils.isNotEmpty(result.getDistricts())) {
            for(int i = 0, size = result.getDistricts().size(); i < size; i++) {
                districtBean = result.getDistricts().get(i);
                BusinessAreaNewBean areaBean = new BusinessAreaNewBean();
                areaBean.setName(all);
                areaBean.setBusinessId(0);
                areaBean.setCinemaCount(districtBean.getCinemaCount());
                districtBean.getBusinessAreas().add(0, areaBean);
                districts.add(districtBean);
            }
        }
        // 地铁: 添加全部及其数量
        subways.clear();
        SubwayBean subwayBean = new SubwayBean();
        subwayBean.setName(all);
        subwayBean.setSubwayId(0);
        subways.add(subwayBean);
        if(CollectionUtils.isNotEmpty(result.getSubways())) {
            for(int i = 0, size = result.getSubways().size(); i < size; i++) {
                subwayBean = result.getSubways().get(i);
                StationBean stationBean = new StationBean();
                stationBean.setStName(all);
                stationBean.setStId(0);
                stationBean.setCinemaCount(subwayBean.getCinemaCount());
                subwayBean.getStations().add(0, stationBean);
                subways.add(subwayBean);
            }
        }
    }


    // 搜索影院
    public static List<CinemaBaseInfo> searchCinemas(String content, List<CinemaBaseInfo> nearestCinemas, List<DistrictBean> districts) {
        List<CinemaBaseInfo> showCinemas = new ArrayList<>();

        // 结果唯一性，同时顺序为：影院名称,商圈以及地址
        String searchKey = content.toLowerCase();
        List<Integer> uniqCinemaIds = new ArrayList<>();

        // 优先名字
        CinemaBaseInfo info;
        // 按地址匹配的影院，要排到最后
        List<Integer> matchAddressCIds = new ArrayList<>();
        for (int i = 0; i < nearestCinemas.size(); i++) {
            info = nearestCinemas.get(i);
            if (!TextUtils.isEmpty(info.getCinameName())
                    && info.getCinameName().toLowerCase().contains(searchKey)) {
                uniqCinemaIds.add(info.getCinemaId());
            }
            if (!TextUtils.isEmpty(info.getAddress())
                    && info.getAddress().toLowerCase().contains(searchKey)) {
                matchAddressCIds.add(info.getCinemaId());
            }
        }
        // 商圈
        if (CollectionUtils.isNotEmpty(districts)) {
            DistrictBean district;
            List<BusinessAreaNewBean> businessAreas;
            BusinessAreaNewBean business;
            String businessName;
            for (int i = 0; i < districts.size(); i++) {
                district = districts.get(i);
                businessAreas = district.getBusinessAreas();
                if (null != businessAreas && businessAreas.size() > 0) {
                    for (int j = 0; j < businessAreas.size(); j++) {
                        business = businessAreas.get(j);
                        businessName = business.getName();
                        if (!TextUtils.isEmpty(businessName) && businessName.toLowerCase().contains(searchKey)) {
                            addUniqCinemaIds(business.getCinemaIds(), uniqCinemaIds);
                        }
                    }
                }
            }
        }
        // 地址
        if (CollectionUtils.isNotEmpty(matchAddressCIds)) {
            addUniqCinemaIds(matchAddressCIds, uniqCinemaIds);
        }

        if (CollectionUtils.isEmpty(uniqCinemaIds)) {
            return showCinemas;
        }

        // 按uniqCinemaIds顺序显示
        for (int i = 0; i < uniqCinemaIds.size(); i++) {
            for (int j = 0; j < nearestCinemas.size(); j++) {
                info = nearestCinemas.get(j);
                if(uniqCinemaIds.get(i) == info.getCinemaId()) {
                    showCinemas.add(info);
                }
            }
        }
        return showCinemas;
    }

    // 设置筛选组件支持的影厅特效
    private static void setSupportFeatures(CinemaResultFeatureBean feature, List<CinemaFeatureBean> cinemaFeatures) {
        if(null == feature || null == cinemaFeatures) {
            return;
        }

        // 顺序：不限、3D、IMAX、VIP、4D、巨幕、4K、杜比、情侣座
        if (1 == feature.getHas3D()) {
            cinemaFeatures.get(1).setSupport(true);
        }
        if (1 == feature.getHasIMAX()) {
            cinemaFeatures.get(2).setSupport(true);
        }
        if (1 == feature.getHasVIP()) {
            cinemaFeatures.get(3).setSupport(true);
        }
        if (1 == feature.getHasFeature4D()) {
            cinemaFeatures.get(4).setSupport(true);
        }
        if (1 == feature.getHasFeatureHuge()) {
            cinemaFeatures.get(5).setSupport(true);
        }
        if (1 == feature.getHasFeature4K()) {
            cinemaFeatures.get(6).setSupport(true);
        }
        if (1 == feature.getHasFeatureDolby()) {
            cinemaFeatures.get(7).setSupport(true);
        }
        if (1 == feature.getHasLoveseat()) {
            cinemaFeatures.get(8).setSupport(true);
        }
    }

    // 筛选出特色影院
    private static List<CinemaBaseInfo> calFeatureCinemas(int index, List<CinemaBaseInfo> list) {
        if(null == list || 0 == list.size()) {
            return null;
        }

        List<CinemaBaseInfo> cinemas = new ArrayList<>();
        CinemaBaseInfo info;
        CinemaResultFeatureBean featureBean;
        for (int i = 0; i < list.size(); i++) {
            info = list.get(i);
            featureBean = info.getFeature();
            if(null == featureBean) {
                continue;
            }
            boolean find = false;
            switch (index) {
                case 1:
                    if (1 == featureBean.getHas3D()) {
                        find = true;
                    }
                    break;
                case 2:
                    if (1 == featureBean.getHasIMAX()) {
                        find = true;
                    }
                    break;
                case 3:
                    if (1 == featureBean.getHasVIP()) {
                        find = true;
                    }
                    break;
                case 4:
                    if (1 == featureBean.getHasFeature4D()) {
                        find = true;
                    }
                    break;
                case 5:
                    if (1 == featureBean.getHasFeatureHuge()) {
                        find = true;
                    }
                    break;
                case 6:
                    if (1 == featureBean.getHasFeature4K()) {
                        find = true;
                    }
                    break;
                case 7:
                    if (1 == featureBean.getHasFeatureDolby()) {
                        find = true;
                    }
                    break;
                case 8:
                    if (1 == featureBean.getHasLoveseat()) {
                        find = true;
                    }
                    break;
                default:
                    break;
            }
            if (find) {
                cinemas.add(info);
            }
        }
        return cinemas;
    }

    // 筛选出指定城区影院
    private static List<CinemaBaseInfo> calDistrictCinemas(int districtId, List<CinemaBaseInfo> sortList, List<DistrictBean> districts) {
        if(null == sortList || 0 == sortList.size() || null == districts || 0 == districts.size()) {
            return null;
        }

        // 筛出指定城区影院Id列表
        List<Integer> cinemaIds = new ArrayList<>();
        DistrictBean districtBean;
        for (int i = 0; i < districts.size(); i++) {
            districtBean = districts.get(i);
            if(districtBean.getDistrictId() == districtId) {
                cinemaIds = districtBean.getCinemaIds();
                break;
            }
        }

        if(null == cinemaIds || 0 == cinemaIds.size()) {
            return null;
        }

        return getCinemasByIds(cinemaIds, sortList);
    }

    // 筛选出指定商圈影院
    private static List<CinemaBaseInfo> calBusinessCinemas(int businessId, int districtId, List<CinemaBaseInfo> sortList, List<DistrictBean> districts) {
        if(null == sortList || 0 == sortList.size() || null == districts || 0 == districts.size()) {
            return null;
        }

        DistrictBean districtBean = null;
        for (int i = 0; i < districts.size(); i++) {
            districtBean = districts.get(i);
            if(districtBean.getDistrictId() == districtId) {
                break;
            }
        }
        if(null == districtBean || null == districtBean.getBusinessAreas() || 0 == districtBean.getBusinessAreas().size()) {
            return null;
        }

        List<BusinessAreaNewBean> businessList = districtBean.getBusinessAreas();
        List<Integer> cinemaIds = new ArrayList<>();
        BusinessAreaNewBean businessBean;
        for (int i = 0; i < businessList.size(); i++) {
            businessBean = businessList.get(i);
            if(businessBean.getBusinessId() == businessId) {
                cinemaIds = businessBean.getCinemaIds();
                break;
            }
        }

        return getCinemasByIds(cinemaIds, sortList);
    }

    // 筛选出指定地铁线影院
    private static List<CinemaBaseInfo> calSubwayCinemas(int subwayId, List<CinemaBaseInfo> sortList, List<SubwayBean> subways) {
        if(null == sortList || 0 == sortList.size() || null == subways || 0 == subways.size()) {
            return null;
        }

        // 筛出指定地铁线影院Id列表
        List<Integer> cinemaIds = new ArrayList<>();
        SubwayBean bean;
        for (int i = 0; i < subways.size(); i++) {
            bean = subways.get(i);
            if(bean.getSubwayId() == subwayId) {
                cinemaIds = bean.getCinemaIds();
                break;
            }
        }

        if(null == cinemaIds || 0 == cinemaIds.size()) {
            return null;
        }

        return getCinemasByIds(cinemaIds, sortList);
    }

    // 筛选出指定地铁站影院
    private static List<CinemaBaseInfo> calStationCinemas(int stationId, int subwayId, List<CinemaBaseInfo> sortList, List<SubwayBean> subways) {
        if(null == sortList || 0 == sortList.size() || null == subways || 0 == subways.size()) {
            return null;
        }

        SubwayBean subwayBean = null;
        for (int i = 0; i < subways.size(); i++) {
            subwayBean = subways.get(i);
            if(subwayBean.getSubwayId() == subwayId) {
                break;
            }
        }
        if(null == subwayBean || null == subwayBean.getStations() || 0 == subwayBean.getStations().size()) {
            return null;
        }

        List<StationBean> stationList = subwayBean.getStations();
        List<Integer> cinemaIds = new ArrayList<>();
        StationBean stationBean;
        for (int i = 0; i < stationList.size(); i++) {
            stationBean = stationList.get(i);
            if(stationBean.getStId() == stationId) {
                cinemaIds = stationBean.getCinemaIds();
                break;
            }
        }

        return getCinemasByIds(cinemaIds, sortList);
    }

    // 根据影院Id串获取影院具体信息：按sortList顺序返回
    private static List<CinemaBaseInfo> getCinemasByIds(List<Integer> cinemaIds, List<CinemaBaseInfo> sortList) {
        if(null == cinemaIds || 0 == cinemaIds.size() || null == sortList || 0 == sortList.size()) {
            return null;
        }

        List<CinemaBaseInfo> cinemas = new ArrayList<>();
        CinemaBaseInfo info;
        for(int i = 0, size1 = sortList.size(); i < size1; i++) {
            info = sortList.get(i);
            for (int j = 0, size2 = cinemaIds.size(); j < size2; j++) {
                if(info.getCinemaId() == cinemaIds.get(j)) {
                    cinemas.add(info);
                    break;
                }
            }
        }

        return cinemas;
    }

    // 添加不重复的影院Id
    private static void addUniqCinemaIds(List<Integer> fromCinemaIds, List<Integer> toCinemaIds) {
        if (null == fromCinemaIds || fromCinemaIds.size() == 0) {
            return;
        }

        for (int i = 0; i < fromCinemaIds.size(); i++) {
            if(toCinemaIds.contains(fromCinemaIds.get(i))) {
                continue;
            }
            toCinemaIds.add(fromCinemaIds.get(i));
        }
    }

    // 距离升序排
    private static class DistanceSort implements Comparator {

        @Override
        public int compare(Object arg0, Object arg1) {
            CinemaBaseInfo info1 = (CinemaBaseInfo) arg0;
            CinemaBaseInfo info2 = (CinemaBaseInfo) arg1;

            if (info1.getDistance() == info2.getDistance()) {
                return 0;
            } else if (info1.getDistance() > info2.getDistance()) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    // 价格升序排
    private static class PriceSort implements Comparator {

        @Override
        public int compare(Object arg0, Object arg1) {
            CinemaBaseInfo info1 = (CinemaBaseInfo) arg0;
            CinemaBaseInfo info2 = (CinemaBaseInfo) arg1;
            if (info1.getMinPrice() == info2.getMinPrice()) {
                return 0;
            } else if (info1.getMinPrice() > info2.getMinPrice()) {
                return 1;
            } else {
                return -1;
            }
        }

    }
}
