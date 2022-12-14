package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.app.data.entity.mine.CollectionCinema;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.cinema.adapter.MovieShowtimeCinemaShowtimeAdapter;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaScreeningBean;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.bean.FavAndBeenCinemaListBean;
import com.mtime.bussiness.ticket.cinema.bean.OnlineCinemasData;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBean;
import com.mtime.bussiness.ticket.cinema.util.CinemaListHelper;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilter;
import com.mtime.bussiness.ticket.holder.TabTicketCinemaHolder;
import com.mtime.bussiness.ticket.movie.bean.DirectSellingOrderPrepareBean;
import com.mtime.bussiness.ticket.movie.bean.MovieBaseItem;
import com.mtime.bussiness.ticket.movie.bean.MovieShowTimeCinemaMainBean;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeDataMainBean;
import com.mtime.bussiness.ticket.movie.bean.ShowtimeDate;
import com.mtime.frame.BaseActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.JumpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.BaseTitleView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mtime.bussiness.ticket.movie.bean.DirectSellingOrderPrepareBean.BIZ_CODE_SUCCESS;

/**
 * Created by vivian.wei on 2017/11/27.
 * ???????????????
 */
@Route(path = RouterActivityPath.Ticket.PAGER_MOVIE_SHOWTIME)
public class MovieShowtimeActivity extends BaseActivity<OnlineCinemasData, TabTicketCinemaHolder>
        implements CinemaFilter.ICinemaFilterClickListener, View.OnClickListener, OnRefreshListener,
        MovieShowtimeCinemaShowtimeAdapter.OnDirectSaleShowtimeListener {
    
    public static final String KEY_MOVIE_ID = "movie_id";
    private static final String KEY_MOVIE_ENAME = "movie_e_name";
    private static final String KEY_MOVIE_TICKET = "movie_isticket";
    private static final String KEY_MOVIE_SHOWTIME_DATE = "key_movie_showtime_date";

    private static final String DATE_SPLIT = "-";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    // ????????????????????????
    private static final Long FAV_CINEMA_PAGE_INDEX = 1L;
    private static final Long FAV_CINEMA_PAGE_SIZE = 10000L;  // ??????????????????

    // ??????
    private String mMovieId;
    private String mMovieName = "";
    private String mShowDateString;

    // ??????2012???11-30
    private String mDate;
    // ?????????20121130
    private String mNewDate;
    // ??????tab??????  ??????dayType
    private int mDateIndex = 0;

    private TicketApi mTicketApi;
    private LocationInfo mLocationInfo;
    // ????????????????????????
    private boolean mIsLocation = false;
    private boolean mIsLocationSuccess = false;

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

    // ???????????????????????????
    private List<CinemaBaseInfo> mAllCinemas;
    // ????????????????????????
    private final List<CinemaBaseInfo> mNearestCinemas = new ArrayList<>();
    // ??????????????????????????????
    private final List<CinemaBaseInfo> mLowestPriceCinemas = new ArrayList<>();
    // ????????????
    private final List<DistrictBean> mDistricts = new ArrayList<>();
    // ????????????
    private final List<SubwayBean> mSubways = new ArrayList<>();
    // ????????????
    private List<CinemaFeatureBean> mCinemaFeatures = new ArrayList<>();
    // ????????????Data
    private final OnlineCinemasData mCinemasData = new OnlineCinemasData();

    // ??????????????????Bean
    private ShowTimeDataMainBean mDateListBean;

    private NetworkManager.NetworkListener<MovieShowTimeCinemaMainBean> mGetMovieShowtimesCallback;  // ????????????
    private View.OnClickListener mDateClick;
    private BaseTitleView.ITitleViewLActListener titleListener;
    private BaseTitleView.ITitleViewLActListener searchTitleListener;

    // ????????????content
    private String mSearchKey;
    private boolean isSearch;
    private boolean mFirstLoad = true;
    private String mCityId;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this,this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new TabTicketCinemaHolder(this, TabTicketCinemaHolder.PageEnum.MOVIE_SHOWTIME);
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mMovieId = String.valueOf(getIntent().getLongExtra(KEY_MOVIE_ID, 0));
        mMovieName = getIntent().getStringExtra(KEY_MOVIE_ENAME);
        mShowDateString = getIntent().getStringExtra(KEY_MOVIE_SHOWTIME_DATE);

        if (!TextUtils.isEmpty(mShowDateString) && mShowDateString.length() == 8) {
            StringBuffer sb = new StringBuffer(mShowDateString);
            mShowDateString = sb.insert(4, DATE_SPLIT).insert(7, DATE_SPLIT).toString();
        }
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // ??????
        setPageLabel(StatisticTicket.PN_MOVIE_TIME);
        putBaseStatisticParam(StatisticConstant.MOVIE_ID, mMovieId);

        // ?????????Event
        initEvent();

        // ???????????????
        getUserContentHolder().initMovieShowtimeTitle(mMovieName, titleListener, searchTitleListener);

        // ???????????????
        Date serverDate = MTimeUtils.getLastDiffServerDate();
        final SimpleDateFormat sFormat = new SimpleDateFormat(DATE_FORMAT);
        if (serverDate == null) {
            serverDate = new Date();
        }
        mDate = sFormat.format(serverDate);
        mNewDate = mDate.replace(DATE_SPLIT, "");

        if(null == mTicketApi) {
            mTicketApi = new TicketApi();
        }

        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();

        LocationHelper.location(getApplicationContext(), true, new OnLocationCallback() {
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
                requestData();
            }

            @Override
            public void onLocationFailure(LocationException e) {
                mIsLocationSuccess = false;
                getUserContentHolder().showLocationFail();
                getUserContentHolder().updateLocationBarWithFail(null == e ? -1 : e.code);
                mLocationInfo = LocationHelper.getDefaultLocationInfo();
                mCityId = mLocationInfo.getCityId();
                requestData();
            }
        });

        getUserContentHolder().setCinemaFilterListener(this);
        getUserContentHolder().setOnClickListener(this);
        getUserContentHolder().setOnRefreshListener(this);
        getUserContentHolder().setOnDirectSaleShowtimeListener(this);
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
    protected void onErrorRetry() {
        super.onErrorRetry();
        requestData();
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

    // ????????????
    @Override
    public void onRefresh() {
        // ????????????????????????????????????????????????????????????????????????????????????
        setPageState(BaseState.LOADING);
        requestDateCinemas();
    }

    // ?????????????????????
    @Override
    public void onBackPressed() {
        if(isSearch) {
            getUserContentHolder().movieShowtimeSearchBackPressed();
            // ??????????????????
            List<CinemaBaseInfo> showCinemas = new ArrayList<>();
            getUserContentHolder().showSearchResult(showCinemas);
            mSearchKey = "";
            isSearch = false;
        } else {
            super.onBackPressed();
        }
    }

    // webview?????????????????????H5?????????
    @Override
    public void gotoSeatSelect(long dsPlatformId, String govCinemaId, int cinemaId, String dsShowtimeId) {
        mTicketApi.getDirectSaleTicketDetail(dsPlatformId, govCinemaId, String.valueOf(cinemaId), dsShowtimeId,
                new NetworkManager.NetworkListener<DirectSellingOrderPrepareBean>() {
                    @Override
                    public void onSuccess(DirectSellingOrderPrepareBean bean, String s) {
                        if (bean != null) {
                            if(TextUtils.equals(bean.getBizCode(), BIZ_CODE_SUCCESS) && !TextUtils.isEmpty(bean.getJumpUrl())) {
//                                JumpUtil.startCooperationSeatSelectActivity(MovieShowtimeActivity.this, bean.getJumpUrl());
                                JumpUtil.startCommonWebActivity(MovieShowtimeActivity.this, bean.getJumpUrl(), StatisticH5.PN_H5, null,
                                        true, false, true, false, false, assemble().toString());
                            } else {
                                MToastUtils.showShortToast(bean.getBizMsg());
                            }
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<DirectSellingOrderPrepareBean> networkException, String msg) {
                        MToastUtils.showShortToast(msg);
                    }
                });
    }

    // ?????????Event
    private void initEvent() {
        // ??????title
        titleListener = new BaseTitleView.ITitleViewLActListener() {
            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if(type == BaseTitleView.ActionType.TYPE_BACK) {
                    // ??????????????????
                    onBackPressed();
                } else if(type == BaseTitleView.ActionType.TYPE_SEARCH) {
                    // ??????
                    StatisticPageBean bean = assemble(StatisticTicket.TICKET_TOP_NAVIGATION, "",
                            "search", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(bean);

                    isSearch = true;
                    // ???????????????????????????????????????
                    getUserContentHolder().showMovieShowtimeSearchLayout();
                }
            }
        };

        // ????????????title
        searchTitleListener = new BaseTitleView.ITitleViewLActListener() {

            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if(type == BaseTitleView.ActionType.TYPE_BACK) { // ??????????????????
                    // ??????
                    StatisticPageBean bean = assemble(StatisticTicket.TICKET_SEARCH_CANCEL, "",
                            "", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(bean);
                    // ??????
                    onBackPressed();
                    isSearch = false;
                    return;
                }

                if (BaseTitleView.ActionType.TYPE_CONTENT_CHANGED == type || BaseTitleView.ActionType.TYPE_SEARCH == type) {
                    if (TextUtils.isEmpty(content)) {
                        // ????????????
                        List<CinemaBaseInfo> showCinemas = new ArrayList<>();
                        getUserContentHolder().showSearchResult(showCinemas);
                        mSearchKey = "";
                    } else if(!content.equals(mSearchKey)){
                        // ??????
                        mSearchKey = content;
                        search(content);
                    }
                }

            }
        };

        // ??????????????????????????????????????????
        mGetMovieShowtimesCallback = new NetworkManager.NetworkListener<MovieShowTimeCinemaMainBean>() {
            @Override
            public void onSuccess(MovieShowTimeCinemaMainBean result, String showMsg) {
                getUserContentHolder().setRefreshState(false);
                MovieShowTimeCinemaMainBean mainBean = result;
                // ???????????????
                String noticeNotOwn = null == mainBean ? "" : mainBean.getNoticeNotOwn();
                // bean????????????????????????????????????????????????
                mAllCinemas = CinemaListHelper.parseToCinemaBaseInfos(mMovieId, mainBean);
                //
                String dateValue = "";
                if(null != mDateListBean
                        && CollectionUtils.isNotEmpty(mDateListBean.getShowtimeDate())
                        && mDateIndex < mDateListBean.getShowtimeDate().size()) {
                    dateValue = mDateListBean.getShowtimeDate().get(mDateIndex).getDateValue();
                }

                if(CollectionUtils.isNotEmpty(mAllCinemas)) {
                    // ????????????????????????
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);

                    if (UserManager.Companion.getInstance().isLogin()) {
                        // ??????????????????????????????
                        requestFavCinemas();
                    } else {
                        setPageState(BaseState.SUCCESS);
                        // ??????????????????
                        refreshCinemaList(null);
                    }
                } else if (!TextUtils.isEmpty(dateValue) && !mDate.equals(dateValue)) {
                    mDate = dateValue;
                    mNewDate = mDate.replace(DATE_SPLIT, "");
                    mTicketApi.getLocationMovieShowtimes(mCityId, mMovieId, mNewDate,
                            ToolsUtils.getToken(getApplicationContext()),
                            ToolsUtils.getJPushId(getApplicationContext()),
                            mGetMovieShowtimesCallback);
                } else {
                    setPageState(BaseState.SUCCESS);
                    // ????????????????????????
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);
                    // ??????????????????
                    refreshCinemaList(null);
                    MToastUtils.showShortToast("?????????????????????");
                }
            }

            @Override
            public void onFailure(NetworkException<MovieShowTimeCinemaMainBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
                getUserContentHolder().setRefreshState(false);
            }
        };

        // ?????????????????????????????????????????????????????????????????????
        mDateClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newDateIndex = (Integer) v.getTag();
                // ??????
                StatisticPageBean bean = assemble(StatisticTicket.TICKET_SELECT_TIME, String.valueOf(newDateIndex + 1),
                        "", "", "", "", getBaseStatisticParam());
                StatisticManager.getInstance().submit(bean);
                // ??????????????????????????????
                getUserContentHolder().updateMovieShowtimeDateView(mDateIndex, newDateIndex);
                // ????????????????????????
                mDateIndex = newDateIndex;
                if(null != mDateListBean
                        && CollectionUtils.isNotEmpty(mDateListBean.getShowtimeDate())
                        && mDateIndex < mDateListBean.getShowtimeDate().size()) {
                    mDate = mDateListBean.getShowtimeDate().get(mDateIndex).getDateValue();
                }
                if (!TextUtils.isEmpty(mDate)) {
                    mNewDate = mDate.replace(DATE_SPLIT, "");
                } else {
                    mNewDate = "";
                }
                // ????????????????????????????????????
                setPageState(BaseState.LOADING);
                requestDateCinemas();
            }
        };
    }

    // ????????????
    private void requestData() {
        if (mIsLocation)
            return;

        // ????????????+???????????? ????????????????????????
        requestMovieShowtimeDates();
        // ????????????_???????????????????????????
        // ??????????????????????????????????????? 2020-10-30 by wwl
//        requestCinemaScreening();
    }

    // ????????????+???????????? ???????????????????????????????????????
    private void requestMovieShowtimeDates() {
        setPageState(BaseState.LOADING);
        mTicketApi.getMovieShowtimeDates(mCityId, mMovieId, new NetworkManager.NetworkListener<ShowTimeDataMainBean>() {
            @Override
            public void onSuccess(ShowTimeDataMainBean result, String showMsg) {
                mDateListBean = result;
                if (null == mDateListBean || CollectionUtils.isEmpty(mDateListBean.getShowtimeDate())) {
                    MToastUtils.showShortToast("??????????????????????????????");
                    return;
                }

                // ??????title
                MovieBaseItem movieBean = mDateListBean.getMovie();
                if (null != movieBean) {
                    String title = TextUtils.isEmpty(movieBean.getNameCN()) ? movieBean.getNameEN() : movieBean.getNameCN();
                    if (!TextUtils.isEmpty(title)) {
                        getUserContentHolder().setMovieShowtimeTitle(title);
                    }
                }

                mDateIndex = 0;
                List<ShowtimeDate> dateListBean = mDateListBean.getShowtimeDate();
                ShowtimeDate dateBean;
                for (int i = 0; i < dateListBean.size(); i++) {
                    dateBean = dateListBean.get(i);
                    if (!TextUtils.isEmpty(dateBean.getDateValue()) && dateBean.getDateValue().equals(mShowDateString)) {
                        mDateIndex = i;
                        mDate = dateBean.getDateValue();
                        mNewDate = mDate.replace(DATE_SPLIT, "");
                        break;
                    }
                }
                // ??????????????????
                getUserContentHolder().showMovieShowtimeDateView(mDateIndex, dateListBean, mDateClick);
                // ????????????????????????????????????
                requestDateCinemas();
            }

            @Override
            public void onFailure(NetworkException<ShowTimeDataMainBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
            }
        });
    }

    // ???????????????????????????????????????????????????
    // ??????updateDate()
    private void requestDateCinemas() {
        mTicketApi.getLocationMovieShowtimes(mCityId, mMovieId, mNewDate, "", "",mGetMovieShowtimesCallback);
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
        // ?????????????????????????????????mNearestCinemas???mNearestCinemas
        CinemaListHelper.sortCinemas(mLocationInfo, mAllCinemas, favAndBeenCinemas, mNearestCinemas,
                mLowestPriceCinemas, mCinemaFeatures);
        // ????????????????????????
        getUserContentHolder().setCinemaFeatureData(mCinemaFeatures, mFeatureIndex);
        // ??????????????????
        filterCinemaList();
    }

    // ????????????_???????????????????????????
    private void requestCinemaScreening() {
        mTicketApi.getCinemaScreening(mCityId, "", "", new NetworkManager.NetworkListener<CinemaScreeningBean>() {
            @Override
            public void onSuccess(CinemaScreeningBean result, String showMsg) {
                // ????????????????????????????????????
                CinemaListHelper.parseDistrictSubwayData(MovieShowtimeActivity.this, result, mDistricts, mSubways);
                // ?????????????????????????????????
                getUserContentHolder().setCinemaScreeningData(mDistricts, mSubways);
            }

            @Override
            public void onFailure(NetworkException<CinemaScreeningBean> exception, String showMsg) {
            }
        });
    }

    // ??????????????????
    private void filterCinemaList() {
        // ????????????????????????????????????????????????
        List<CinemaBaseInfo> showCinemas = CinemaListHelper.getShowCinemas(mSortType, mFeatureIndex,
                mDistrictId, mBusinessId, mSubwayId, mStationId, mNearestCinemas, mLowestPriceCinemas,
                mDistricts, mSubways);
        // ??????????????????
        getUserContentHolder().setSelectDateString(mDate);
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

        LocationHelper.refreshLatitudeAndLongitudeInfo(getApplicationContext(), new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                mIsLocation = false;
                if(null != locationInfo) {
                    mIsLocationSuccess = true;
                    mLocationInfo = locationInfo.clone();
                    getUserContentHolder().updateLocationBar(LocationHelper.getLocationDescribe(mLocationInfo));
                    updateDataAfterLocation();
                }
            }

            @Override
            public void onLocationFailure(LocationException e) {
                mIsLocation = false;
                mIsLocationSuccess = false;
                getUserContentHolder().updateLocationBarWithFail(null == e ? -1 : e.code);
            }
        });
    }

    // ???????????????????????????
    private void updateDataAfterLocation() {
        resetVariable();
        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();
        requestData();
    }

    // ?????????????????????????????????
    private void search(final String content) {
        List<CinemaBaseInfo> showCinemas = new ArrayList<>();

        if (CollectionUtils.isEmpty(mNearestCinemas)) {
            getUserContentHolder().showSearchEmpty();
            return;
        }

        showCinemas = CinemaListHelper.searchCinemas(content, mNearestCinemas, mDistricts);

        if (CollectionUtils.isEmpty(showCinemas)) {
            getUserContentHolder().showSearchEmpty();
            return;
        }

        // ??????????????????
        getUserContentHolder().showSearchResult(showCinemas);
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
        getUserContentHolder().resetCinemaFilter();
    }

    public static void launch(Context context, String refer, String movieId, String movieEName, boolean movieTicket, String movidDate, int requestCode) {
        Intent launcher = new Intent(context, MovieShowtimeActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, Long.valueOf(movieId));
        launcher.putExtra(KEY_MOVIE_ENAME, movieEName);
        launcher.putExtra(KEY_MOVIE_TICKET, movieTicket);
        launcher.putExtra(KEY_MOVIE_SHOWTIME_DATE, movidDate);
        dealRefer(context, refer, launcher);
        if(context instanceof BaseActivity) {
            ((BaseActivity) context).startActivityForResult(launcher, requestCode);
        } else {
            context.startActivity(launcher);
        }
    }
}
