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
 * Tab_??????_??????????????????
 */

public class TabTicketCinemaFragment extends BaseFragment<OnlineCinemasData, TabTicketCinemaHolder>
        implements CinemaFilter.ICinemaFilterClickListener, View.OnClickListener, OnRefreshListener {

    // ????????????????????????
    private static final Long FAV_CINEMA_PAGE_INDEX = 1L;
    private static final Long FAV_CINEMA_PAGE_SIZE = 10000L;  // ??????????????????

    private TicketApi mTicketApi;

    private LocationInfo mLocationInfo;
    // ????????????????????????
    private boolean mIsLocation = false;

    // ??????????????????
    private CinemaFilter.FilterEventType mSortType = CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE;
    // ????????????Id
    private int mDistrictId = 0;
    private int mBusinessId = 0;
    private int mSubwayId = 0;
    private int mStationId = 0;
    private int mFeatureIndex = 0;
    // ?????????????????????
    private boolean mIsSubwayFilter = false;
    // ?????????????????????
    private boolean mFirstLoad = true;

    // ???????????????????????????
    private List<CinemaBaseInfo> mAllCinemas;
    // ????????????????????????
    private List<CinemaBaseInfo> mNearestCinemas = new ArrayList<>();
    // ??????????????????????????????
    private List<CinemaBaseInfo> mLowestPriceCinemas = new ArrayList<>();
    // ????????????
    private List<DistrictBean> mDistricts = new ArrayList<>();
    // ????????????
    private List<SubwayBean> mSubways = new ArrayList<>();
    // ????????????
    private List<CinemaFeatureBean> mCinemaFeatures = new ArrayList<>();
    // ????????????Data
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

                //??????
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

                //??????
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

    // ??????????????????
    @Override
    public void onEvent(CinemaFilter.FilterEventType filtertype, int id, int parentId) {
        if(filtertype == CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE) {  // ????????????
            // ??????
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_SORT, "",
                    "nearest", "", "", "", null);
            StatisticManager.getInstance().submit(bean);

            if(mSortType == CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE) {
                return;
            }
            mSortType = CinemaFilter.FilterEventType.TYPE_SORT_DISTANCE;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_SORT_PRICE) {  // ????????????
            // ??????
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_SORT, "",
                    "cheapest", "", "", "", null);
            StatisticManager.getInstance().submit(bean);

            if(mSortType == CinemaFilter.FilterEventType.TYPE_SORT_PRICE) {
                return;
            }
            mSortType = CinemaFilter.FilterEventType.TYPE_SORT_PRICE;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_FEATURE) { // ????????????
            // ??????
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
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_BUSINESS) { // ??????
            // ??????
            Map<String, String> businessParam = new HashMap<String, String>(2);
            businessParam.put(StatisticConstant.DISTRICT_ID, String.valueOf(parentId));
            businessParam.put(StatisticConstant.BUSINESS_ID, String.valueOf(id));
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_LOCAL, "",
                    "businessCircle", "", "", "", businessParam);
            StatisticManager.getInstance().submit(bean);

            // ?????????????????????
            mDistrictId = parentId;
            mBusinessId = id;
            mSubwayId = 0;
            mStationId = 0;
            mIsSubwayFilter = false;
            filterCinemaList();
        } else if(filtertype == CinemaFilter.FilterEventType.TYPE_STATION) { // ?????????
            // ??????
            Map<String, String> businessParam = new HashMap<String, String>(2);
            businessParam.put(StatisticConstant.SUBWAY_ID, String.valueOf(parentId));
            businessParam.put(StatisticConstant.STATION_ID, String.valueOf(id));
            StatisticPageBean bean = assemble(StatisticTicket.TICKET_LOCAL, "",
                    "subway", "", "", "", businessParam);
            StatisticManager.getInstance().submit(bean);

            // ?????????????????????
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
            case R.id.layout_cinema_list_location_bar_rr: // ???????????????
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
            // ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            onRefresh();
        }
        mFirstLoad = false;
    }

    @Override
    protected void onErrorRetry() {
        // ????????????tab??????banner
        refreshBanner();
        requestData();
    }

    // ????????????
    @Override
    public void onRefresh() {
        // ????????????tab??????banner
        refreshBanner();
        // ??????????????????????????????????????????????????????????????????????????????????????????
        requestOnlineCinemas();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
    }

    // ????????????
    private void requestData() {
        if (mIsLocation)
            return;

        // ????????????????????????????????????
        requestOnlineCinemas();
        requestAds();
        // ??????????????????????????????????????? 2020-10-30 by wwl
//        requestCinemaScreening();
    }

    // ?????????????????????
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

    // ???????????????????????????????????????????????????
    private void requestOnlineCinemas() {
        if(null != getUserContentHolder()) {
            if(((TicketFragment)getParentFragment()).getCurrentItem() == 1) {
                setPageState(BaseState.LOADING);
            }
            // ??????????????????
            mTicketApi.getOnlineCinemas(mCityId, new NetworkManager.NetworkListener<OnlineCityCinemaListBean>() {
                @Override
                public void onSuccess(OnlineCityCinemaListBean result, String showMsg) {
                    getUserContentHolder().setRefreshState(false);
                    // ???????????????
                    String noticeNotOwn = "";
                    if(null != result) {
                        noticeNotOwn = result.getNoticeNotOwn();
                        mAllCinemas = result.getCinemaList();
                    }
                    // ????????????????????????
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);

                    if (UserManager.Companion.getInstance().isLogin() && CollectionUtils.isNotEmpty(mAllCinemas)) {
                        // ??????????????????????????????
                        requestFavCinemas();
                    } else {
                        setPageState(BaseState.SUCCESS);
                        // ??????????????????
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
     * ??????????????????????????????
     */
    private void requestFavCinemas() {
        mTicketApi.getCollectCinemaList(FAV_CINEMA_PAGE_INDEX, FAV_CINEMA_PAGE_SIZE, new NetworkManager.NetworkListener<CollectionCinema>() {
            @Override
            public void onSuccess(CollectionCinema result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                // ???2020?????????????????????????????????????????????????????????????????????????????????(?????????????????????????????????
                FavAndBeenCinemaListBean list = CinemaListHelper.convertFavCinema(result);
                // ??????????????????
                refreshCinemaList(list);
            }

            @Override
            public void onFailure(NetworkException<CollectionCinema> exception, String showMsg) {
                // ?????????OnlineCinemasByCity.api?????????????????????
                setPageState(BaseState.SUCCESS);
                // ??????????????????
                refreshCinemaList(null);
            }
        });
    }

    // ??????????????????
    private void refreshCinemaList(FavAndBeenCinemaListBean favAndBeenCinemas) {
        // ??????????????????
        if(mIsLocationSuccess) {
            getUserContentHolder().hideLocationFail();
        }
        // ?????????????????????????????????mNearestCinemas???mLowestPriceCinemas
        CinemaListHelper.sortCinemas(mLocationInfo, mAllCinemas, favAndBeenCinemas, mNearestCinemas,
                mLowestPriceCinemas, mCinemaFeatures);
        // ????????????????????????
        getUserContentHolder().setCinemaFeatureData(mCinemaFeatures, mFeatureIndex);
        // ??????????????????
        filterCinemaList();
    }

    // ????????????_???????????????????????????
    private void requestCinemaScreening() {
        if(null != getUserContentHolder()) {
            mTicketApi.getCinemaScreening(mCityId, "", "", new NetworkManager.NetworkListener<CinemaScreeningBean>() {
                @Override
                public void onSuccess(CinemaScreeningBean result, String showMsg) {
                    // ????????????????????????????????????
                    CinemaListHelper.parseDistrictSubwayData(getApplicationContext(), result, mDistricts, mSubways);
                    // ?????????????????????????????????
                    getUserContentHolder().setCinemaScreeningData(mDistricts, mSubways);
                }
        
                @Override
                public void onFailure(NetworkException<CinemaScreeningBean> exception, String showMsg) {
                }
            });
        }
    }

    // ???????????????????????????
    private void requestAds() {
        if(null != getUserContentHolder()) {
            mTicketApi.getAdvertisements(mCityId, new NetworkManager.NetworkListener<ADTotalBean>() {
                @Override
                public void onSuccess(ADTotalBean result, String showMsg) {
                    // ????????????
                    getUserContentHolder().showAd(result);
                }
        
                @Override
                public void onFailure(NetworkException<ADTotalBean> exception, String showMsg) {
                    // ????????????
                    getUserContentHolder().setAdVisible(false);
                }
            });
        }
    }

    // ??????????????????
    private void filterCinemaList() {
        // ????????????????????????????????????????????????
        List<CinemaBaseInfo> showCinemas = CinemaListHelper.getShowCinemas(mSortType, mFeatureIndex, 
                mDistrictId, mBusinessId, mSubwayId, mStationId, mNearestCinemas, mLowestPriceCinemas,
                mDistricts, mSubways);
        // ??????????????????
        getUserContentHolder().isSubwayFilter(mIsSubwayFilter);
        mCinemasData.setList(showCinemas);
        setData(mCinemasData);
    }

    // ????????????
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

                    //??????
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

                //??????
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

    // ???????????????????????????
    public void updateDataAfterLocation() {
        // ??????????????????
        if(getPageState() == BaseState.ERROR || getPageState() == BaseState.LOADING) {
            getStateContainer().setState(BaseState.SUCCESS);
        }
        resetVariable();
        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();
        requestData();
    }

    // ????????????????????????
    public void showSearchView() {
        getUserContentHolder().showSearchView();
    }

    // ?????????????????????????????????
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

        // ??????????????????
        getUserContentHolder().showSearchResult(showCinemas);
    }

    // ????????????
    public void cancelSearch() {
        clearSearch();
        if(null != getUserContentHolder()) {
            getUserContentHolder().cancelSearch();
        }
    }

    // ????????????
    public void clearSearch() {
        // ??????????????????
        List<CinemaBaseInfo> showCinemas = new ArrayList<>();
        if(null != getUserContentHolder()) {
            getUserContentHolder().showSearchResult(showCinemas);
        }
    }

    // ????????????????????????
    public boolean needRequest(final LocationInfo info) {
        if (null == mLocationInfo || !TextUtils.equals(mCityId, info.getCityId())) {
            mLocationInfo = info.clone();
            mCityId = mLocationInfo.getCityId();
            return true;
        }

        return false;
    }

    // ????????????????????????
    public void setFirstLoad() {
        mFirstLoad = true;
    }

    /**
     * ????????????tab??????banner
     */
    private void refreshBanner() {
        ((TicketFragment)getParentFragment()).requestBanner();
    }
}
