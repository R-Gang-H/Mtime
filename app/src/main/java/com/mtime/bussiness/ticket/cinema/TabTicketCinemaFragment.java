package com.mtime.bussiness.ticket.cinema;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.app.data.entity.mine.CollectionCinema;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.beans.ADTotalBean;
import com.mtime.bussiness.ticket.TicketFragment;
import com.mtime.frame.BaseFragment;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaScreeningBean;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.bean.FavAndBeenCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.OnlineCinemasData;
import com.mtime.bussiness.ticket.cinema.bean.OnlineCityCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBean;
import com.mtime.bussiness.ticket.cinema.util.CinemaListHelper;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilter;
import com.mtime.bussiness.ticket.holder.TabTicketCinemaHolder;
import com.mtime.statistic.large.MapBuild;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vivian.wei on 2017/10/16.
 * Tab_购票_影院（列表）
 */

public class TabTicketCinemaFragment extends BaseFragment<OnlineCinemasData, TabTicketCinemaHolder>
        implements CinemaFilter.ICinemaFilterClickListener, View.OnClickListener, OnRefreshListener {

    // 用户收藏影院参数
    private static final Long FAV_CINEMA_PAGE_INDEX = 1L;
    private static final Long FAV_CINEMA_PAGE_SIZE = 10000L;  // 一次返回全部

    private TicketApi mTicketApi;

    private LocationInfo mLocationInfo;
    // 是否正在手动定位
    private boolean mIsLocation = false;

    // 当前排序类型
    private CinemaFilter.FilterEventType mSortType = CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE;
    // 当前筛选Id
    private int mDistrictId = 0;
    private int mBusinessId = 0;
    private int mSubwayId = 0;
    private int mStationId = 0;
    private int mFeatureIndex = 0;
    // 是否为地铁筛选
    private boolean mIsSubwayFilter = false;
    // 当前搜索关键词
    private boolean mFirstLoad = true;

    // 所有影院（未排序）
    private List<CinemaBaseInfo> mAllCinemas;
    // 距离排序影院列表
    private List<CinemaBaseInfo> mNearestCinemas = new ArrayList<>();
    // 最低票价排序影院列表
    private List<CinemaBaseInfo> mLowestPriceCinemas = new ArrayList<>();
    // 城区列表
    private List<DistrictBean> mDistricts = new ArrayList<>();
    // 地铁列表
    private List<SubwayBean> mSubways = new ArrayList<>();
    // 影厅特效
    private List<CinemaFeatureBean> mCinemaFeatures = new ArrayList<>();
    // 影院列表Data
    private OnlineCinemasData mCinemasData = new OnlineCinemasData();
    private String mCityId;
    private boolean mIsLocationSuccess;

    @Override
    public ContentHolder onBindContentHolder() {
        return new TabTicketCinemaHolder(mContext, TabTicketCinemaHolder.PageEnum.CINEMA_LIST);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        setPageLabel("cinemaList");

        if(null == mTicketApi) {
            mTicketApi = new TicketApi();
        }

        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();

        LocationHelper.location(getApplicationContext(), true, new OnLocationCallback() {
            @Override
            public void onStartLocation() {

            }

            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                mIsLocationSuccess = true;
                if(null != locationInfo) {
                    mLocationInfo = locationInfo.clone();
                    getUserContentHolder().updateLocationBar(LocationHelper.getLocationDescribe(mLocationInfo));
                } else {
                    mLocationInfo = LocationHelper.getDefaultLocationInfo();
                    getUserContentHolder().showLocationFail();
                    getUserContentHolder().updateLocationBarWithFail(-1);
                }
                mCityId = mLocationInfo.getCityId();
//                requestData();

                //上报
                mBaseStatisticHelper.assemble1("location", null, null, null, null, null,
                        new MapBuild().put("longtitude", String.valueOf(mLocationInfo.getLongitude()))
                                .put("latitude", String.valueOf(mLocationInfo.getLatitude()))
                                .put("cityID", String.valueOf(mCityId))
                                .put("localPermission", "1")
                                .build())
                        .submit();
            }

            @Override
            public void onLocationFailure(LocationException e) {
                mIsLocationSuccess = false;
                getUserContentHolder().showLocationFail();
                getUserContentHolder().updateLocationBarWithFail(null == e ? -1 : e.code);
                mLocationInfo = LocationHelper.getDefaultLocationInfo();
                mCityId = mLocationInfo.getCityId();
//                requestData();

                //上报
                mBaseStatisticHelper.assemble1("location", null, null, null, null, null,
                        new MapBuild().put("longtitude", String.valueOf(mLocationInfo.getLongitude()))
                                .put("latitude", String.valueOf(mLocationInfo.getLatitude()))
                                .put("cityID", String.valueOf(mCityId))
                                .put("localPermission", e.code == LocationException.CODE_PERMISSION_DENIED ? "0" : "1")
                                .build())
                        .submit();
            }
        });

        getUserContentHolder().setCinemaFilterListener(this);
        getUserContentHolder().setOnClickListener(this);
        getUserContentHolder().setOnRefreshListener(this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return super.onBindLoadingHolder();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mTicketApi) {
            mTicketApi.cancel();
        }
    }

    // 筛选事件处理
    @Override
    public void onEvent(CinemaFilter.FilterEventType filtertype, int id, int parentId) {
        if(filtertype == CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE) {  // 距离最近
            // 埋点
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_SORT, "",
                    "nearest", "", "", "", null);
            StatisticManager.getInstance().submit(bean);

            if(mSortType == CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE) {
                return;
            }
            mSortType = CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_SORT_PRICE) {  // 价格最低
            // 埋点
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_SORT, "",
                    "cheapest", "", "", "", null);
            StatisticManager.getInstance().submit(bean);

            if(mSortType == CinemaFilter.FilterEventType.TYPE_SORT_PRICE) {
                return;
            }
            mSortType = CinemaFilter.FilterEventType.TYPE_SORT_PRICE;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_FEATURE) { // 特效筛选
            // 埋点
            Map<String, String> businessParam = new HashMap<String, String>(1);
            businessParam.put(StatisticConstant.CINEMA_FEATURE_TYPE, String.valueOf(CinemaListHelper.getStatisticFeatureTypeString(id)));
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_CHARACTERISTIC_SERVICE, "",
                    "", "", "", "", businessParam);
            StatisticManager.getInstance().submit(bean);

            if(mFeatureIndex == id) {
                return;
            }
            mFeatureIndex = id;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_BUSINESS) { // 商圈
            // 埋点
            Map<String, String> businessParam = new HashMap<String, String>(2);
            businessParam.put(StatisticConstant.DISTRICT_ID, String.valueOf(parentId));
            businessParam.put(StatisticConstant.BUSINESS_ID, String.valueOf(id));
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_LOCAL, "",
                    "businessCircle", "", "", "", businessParam);
            StatisticManager.getInstance().submit(bean);

            // 商圈与地铁互斥
            mDistrictId = parentId;
            mBusinessId = id;
            mSubwayId = 0;
            mStationId = 0;
            mIsSubwayFilter = false;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_STATION) { // 地铁站
            // 埋点
            Map<String, String> businessParam = new HashMap<String, String>(2);
            businessParam.put(StatisticConstant.SUBWAY_ID, String.valueOf(parentId));
            businessParam.put(StatisticConstant.STATION_ID, String.valueOf(id));
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_LOCAL, "",
                    "subway", "", "", "", businessParam);
            StatisticManager.getInstance().submit(bean);

            // 商圈与地铁互斥
            mDistrictId = 0;
            mBusinessId = 0;
            mSubwayId = parentId;
            mStationId = id;
            mIsSubwayFilter = true;
            filterCinemaList();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_cinema_list_location_bar_rr: // 底部定位条
                refreshLatitudeAndLongitudeInfo();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(null != mLocationInfo && !mFirstLoad && !getUserContentHolder().isSearchState()) {
            // 影院列表页回来需要刷新收藏标签：如果是搜索列表，收藏后不刷新；如果不是搜索列表，收藏后刷新
            onRefresh();
        }
        mFirstLoad = false;
    }

    @Override
    protected void onErrorRetry() {
        // 刷新购票tab顶部banner
        refreshBanner();
        requestData();
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        // 刷新购票tab顶部banner
        refreshBanner();
        // 只请求某地区的在线售票影院列表（主接口），不更新排序筛选条件
        requestOnlineCinemas();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
    }

    // 请求数据
    private void requestData() {
        if (mIsLocation)
            return;

        // 某地区的在线售票影院列表
        requestOnlineCinemas();
        requestAds();
        // 不要删除，产品告知会加回来 2020-10-30 by wwl
//        requestCinemaScreening();
    }

    // 重置变量和组件
    private void resetVariable() {
        mSortType = CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE;
        mDistrictId = 0;
        mBusinessId = 0;
        mSubwayId = 0;
        mStationId = 0;
        mFeatureIndex = 0;
        mIsSubwayFilter = false;

        mAllCinemas = null;
        mNearestCinemas = new ArrayList<>();
        mLowestPriceCinemas = new ArrayList<>();
        mDistricts = new ArrayList<>();
        mSubways = new ArrayList<>();
        mCinemasData = new OnlineCinemasData();

        if(null != getUserContentHolder()) {
            getUserContentHolder().resetCinemaFilter();
        }
    }

    public void setCityId(String cityId) {
        if (!TextUtils.isEmpty(cityId)) {
            mCityId = cityId;
        }
    }

    // 某地区的在线售票影院列表（主接口）
    private void requestOnlineCinemas() {
        if(null != getUserContentHolder()) {
            if(((TicketFragment)getParentFragment()).getCurrentItem() == 1) {
                setPageState(BaseState.LOADING);
            }
            // 影院列表更新
            mTicketApi.getOnlineCinemas(mCityId, new NetworkManager.NetworkListener<OnlineCityCinemaListBean>() {
                @Override
                public void onSuccess(OnlineCityCinemaListBean result, String showMsg) {
                    getUserContentHolder().setRefreshState(false);
                    // 非自营提示
                    String noticeNotOwn = "";
                    if(null != result) {
                        noticeNotOwn = result.getNoticeNotOwn();
                        mAllCinemas = result.getCinemaList();
                    }
                    // 显示非自营提示条
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);

                    if (UserManager.Companion.getInstance().isLogin() && CollectionUtils.isNotEmpty(mAllCinemas)) {
                        // 获取用户收藏影院列表
                        requestFavCinemas();
                    } else {
                        setPageState(BaseState.SUCCESS);
                        // 更新影院列表
                        refreshCinemaList(null);
                    }
                }
        
                @Override
                public void onFailure(NetworkException<OnlineCityCinemaListBean> exception, String showMsg) {
                    setPageState(BaseState.ERROR);
                    getUserContentHolder().setRefreshState(false);
                }
            });
        }
    }

    /**
     * 获取用户收藏影院列表
     */
    private void requestFavCinemas() {
        mTicketApi.getCollectCinemaList(FAV_CINEMA_PAGE_INDEX, FAV_CINEMA_PAGE_SIZE, new NetworkManager.NetworkListener<CollectionCinema>() {
            @Override
            public void onSuccess(CollectionCinema result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                // 将2020新版用户收藏影院接口转换为原来用户收藏去过影院接口实体(这么处理代码修改最小）
                FavAndBeenCinemaListBean list = CinemaListHelper.convertFavCinema(result);
                // 更新影院列表
                refreshCinemaList(list);
            }

            @Override
            public void onFailure(NetworkException<CollectionCinema> exception, String showMsg) {
                // 主接口OnlineCinemasByCity.api获取成功即成功
                setPageState(BaseState.SUCCESS);
                // 更新影院列表
                refreshCinemaList(null);
            }
        });
    }

    // 更新影院列表
    private void refreshCinemaList(FavAndBeenCinemaListBean favAndBeenCinemas) {
        // 隐藏定位失败
        if(mIsLocationSuccess) {
            getUserContentHolder().hideLocationFail();
        }
        // 影院排序：得到排序好的mNearestCinemas和mLowestPriceCinemas
        CinemaListHelper.sortCinemas(mLocationInfo, mAllCinemas, favAndBeenCinemas, mNearestCinemas,
                mLowestPriceCinemas, mCinemaFeatures);
        // 设置影厅特效数据
        getUserContentHolder().setCinemaFeatureData(mCinemaFeatures, mFeatureIndex);
        // 显示影院列表
        filterCinemaList();
    }

    // 影院列表_商圈和地铁筛选数据
    private void requestCinemaScreening() {
        if(null != getUserContentHolder()) {
            mTicketApi.getCinemaScreening(mCityId, "", "", new NetworkManager.NetworkListener<CinemaScreeningBean>() {
                @Override
                public void onSuccess(CinemaScreeningBean result, String showMsg) {
                    // 商圈和地铁添加全部和数量
                    CinemaListHelper.parseDistrictSubwayData(getApplicationContext(), result, mDistricts, mSubways);
                    // 设置商圈和地铁筛选数据
                    getUserContentHolder().setCinemaScreeningData(mDistricts, mSubways);
                }
        
                @Override
                public void onFailure(NetworkException<CinemaScreeningBean> exception, String showMsg) {
                }
            });
        }
    }

    // 获取筛选栏下的广告
    private void requestAds() {
        if(null != getUserContentHolder()) {
            mTicketApi.getAdvertisements(mCityId, new NetworkManager.NetworkListener<ADTotalBean>() {
                @Override
                public void onSuccess(ADTotalBean result, String showMsg) {
                    // 显示广告
                    getUserContentHolder().showAd(result);
                }
        
                @Override
                public void onFailure(NetworkException<ADTotalBean> exception, String showMsg) {
                    // 隐藏广告
                    getUserContentHolder().setAdVisible(false);
                }
            });
        }
    }

    // 筛选影院列表
    private void filterCinemaList() {
        // 获取排序筛选后用于显示的影院列表
        List<CinemaBaseInfo> showCinemas = CinemaListHelper.getShowCinemas(mSortType, mFeatureIndex, 
                mDistrictId, mBusinessId, mSubwayId, mStationId, mNearestCinemas, mLowestPriceCinemas,
                mDistricts, mSubways);
        // 刷新页面数据
        getUserContentHolder().isSubwayFilter(mIsSubwayFilter);
        mCinemasData.setList(showCinemas);
        setData(mCinemasData);
    }

    // 开始定位
    private void refreshLatitudeAndLongitudeInfo() {
        if (mIsLocation) {
            return;
        }
        mIsLocation = true;

        getUserContentHolder().updateLocationBar();

        LocationHelper.refreshLatitudeAndLongitudeInfo(context.getApplicationContext(), new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                mIsLocation = false;
                if(null != locationInfo) {
                    mIsLocationSuccess = true;
                    mLocationInfo = locationInfo.clone();

                    getUserContentHolder().updateLocationBar(LocationHelper.getLocationDescribe(mLocationInfo));
                    updateDataAfterLocation();

                    //上报
                    mBaseStatisticHelper.assemble1("localCover", null, "localReflesh", null, null, null,
                            new MapBuild().put("longtitude", String.valueOf(mLocationInfo.getLongitude()))
                                    .put("latitude", String.valueOf(mLocationInfo.getLatitude()))
                                    .put("cityID", String.valueOf(mCityId))
                                    .put("localPermission", "1")
                                    .build())
                            .submit();
                }
            }

            @Override
            public void onLocationFailure(LocationException e) {
                mIsLocation = false;
                mIsLocationSuccess = false;
                getUserContentHolder().updateLocationBarWithFail(null == e ? -1 : e.code);

                //上报
                mBaseStatisticHelper.assemble1("localCover", null, "localReflesh", null, null, null,
                        new MapBuild().put("longtitude", String.valueOf(mLocationInfo.getLongitude()))
                                .put("latitude", String.valueOf(mLocationInfo.getLatitude()))
                                .put("cityID", String.valueOf(mCityId))
                                .put("localPermission", e.code == LocationException.CODE_PERMISSION_DENIED ? "0" : "1")
                                .build())
                        .submit();
            }
        });
    }

    // 定位成功后更新数据
    public void updateDataAfterLocation() {
        // 重置页面状态
        if(getPageState() == BaseState.ERROR || getPageState() == BaseState.LOADING) {
            getStateContainer().setState(BaseState.SUCCESS);
        }
        resetVariable();
        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();
        requestData();
    }

    // 显示搜索影院布局
    public void showSearchView() {
        getUserContentHolder().showSearchView();
    }

    // 搜索（不用加下拉刷新）
    public void search(final String content) {
        if (null == mNearestCinemas || mNearestCinemas.size() == 0) {
            getUserContentHolder().showSearchEmpty();
            return;
        }

        List<CinemaBaseInfo> showCinemas = CinemaListHelper.searchCinemas(content, mNearestCinemas, mDistricts);
        if (CollectionUtils.isEmpty(showCinemas)) {
            getUserContentHolder().showSearchEmpty();
            return;
        }

        // 显示搜索结果
        getUserContentHolder().showSearchResult(showCinemas);
    }

    // 取消搜索
    public void cancelSearch() {
        clearSearch();
        if(null != getUserContentHolder()) {
            getUserContentHolder().cancelSearch();
        }
    }

    // 清空搜索
    public void clearSearch() {
        // 清空搜索列表
        List<CinemaBaseInfo> showCinemas = new ArrayList<>();
        if(null != getUserContentHolder()) {
            getUserContentHolder().showSearchResult(showCinemas);
        }
    }

    // 是否需要请求数据
    public boolean needRequest(final LocationInfo info) {
        if (null == mLocationInfo || !TextUtils.equals(mCityId, info.getCityId())) {
            mLocationInfo = info.clone();
            mCityId = mLocationInfo.getCityId();
            return true;
        }

        return false;
    }

    // 设置为第一次加载
    public void setFirstLoad() {
        mFirstLoad = true;
    }

    /**
     * 刷新购票tab顶部banner
     */
    private void refreshBanner() {
        ((TicketFragment)getParentFragment()).requestBanner();
    }
}
