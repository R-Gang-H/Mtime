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
 * 影片排片页
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
    // 用户收藏影院参数
    private static final Long FAV_CINEMA_PAGE_INDEX = 1L;
    private static final Long FAV_CINEMA_PAGE_SIZE = 10000L;  // 一次返回全部

    // 参数
    private String mMovieId;
    private String mMovieName = "";
    private String mShowDateString;

    // 时间2012—11-30
    private String mDate;
    // 新时间20121130
    private String mNewDate;
    // 日期tab索引  旧：dayType
    private int mDateIndex = 0;

    private TicketApi mTicketApi;
    private LocationInfo mLocationInfo;
    // 是否正在手动定位
    private boolean mIsLocation = false;
    private boolean mIsLocationSuccess = false;

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

    // 所有影院（未排序）
    private List<CinemaBaseInfo> mAllCinemas;
    // 距离排序影院列表
    private final List<CinemaBaseInfo> mNearestCinemas = new ArrayList<>();
    // 最低票价排序影院列表
    private final List<CinemaBaseInfo> mLowestPriceCinemas = new ArrayList<>();
    // 城区列表
    private final List<DistrictBean> mDistricts = new ArrayList<>();
    // 地铁列表
    private final List<SubwayBean> mSubways = new ArrayList<>();
    // 影厅特效
    private List<CinemaFeatureBean> mCinemaFeatures = new ArrayList<>();
    // 影院列表Data
    private final OnlineCinemasData mCinemasData = new OnlineCinemasData();

    // 上映日期列表Bean
    private ShowTimeDataMainBean mDateListBean;

    private NetworkManager.NetworkListener<MovieShowTimeCinemaMainBean> mGetMovieShowtimesCallback;  // 全部影院
    private View.OnClickListener mDateClick;
    private BaseTitleView.ITitleViewLActListener titleListener;
    private BaseTitleView.ITitleViewLActListener searchTitleListener;

    // 当前搜索content
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

        // 埋点
        setPageLabel(StatisticTicket.PN_MOVIE_TIME);
        putBaseStatisticParam(StatisticConstant.MOVIE_ID, mMovieId);

        // 初始化Event
        initEvent();

        // 初始化标题
        getUserContentHolder().initMovieShowtimeTitle(mMovieName, titleListener, searchTitleListener);

        // 服务器日期
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

    // 下拉刷新
    @Override
    public void onRefresh() {
        // 只请求指定日期下的影院列表（主接口），不更新排序筛选条件
        setPageState(BaseState.LOADING);
        requestDateCinemas();
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        if(isSearch) {
            getUserContentHolder().movieShowtimeSearchBackPressed();
            // 清空搜索列表
            List<CinemaBaseInfo> showCinemas = new ArrayList<>();
            getUserContentHolder().showSearchResult(showCinemas);
            mSearchKey = "";
            isSearch = false;
        } else {
            super.onBackPressed();
        }
    }

    // webview打开合作影院的H5选座页
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

    // 初始化Event
    private void initEvent() {
        // 点击title
        titleListener = new BaseTitleView.ITitleViewLActListener() {
            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if(type == BaseTitleView.ActionType.TYPE_BACK) {
                    // 点击返回箭头
                    onBackPressed();
                } else if(type == BaseTitleView.ActionType.TYPE_SEARCH) {
                    // 埋点
                    StatisticPageBean bean = assemble(StatisticTicket.TICKET_TOP_NAVIGATION, "",
                            "search", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(bean);

                    isSearch = true;
                    // 点击搜索图标：显示搜索布局
                    getUserContentHolder().showMovieShowtimeSearchLayout();
                }
            }
        };

        // 点击搜索title
        searchTitleListener = new BaseTitleView.ITitleViewLActListener() {

            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if(type == BaseTitleView.ActionType.TYPE_BACK) { // 点击返回箭头
                    // 埋点
                    StatisticPageBean bean = assemble(StatisticTicket.TICKET_SEARCH_CANCEL, "",
                            "", "", "", "", getBaseStatisticParam());
                    StatisticManager.getInstance().submit(bean);
                    // 返回
                    onBackPressed();
                    isSearch = false;
                    return;
                }

                if (BaseTitleView.ActionType.TYPE_CONTENT_CHANGED == type || BaseTitleView.ActionType.TYPE_SEARCH == type) {
                    if (TextUtils.isEmpty(content)) {
                        // 清空列表
                        List<CinemaBaseInfo> showCinemas = new ArrayList<>();
                        getUserContentHolder().showSearchResult(showCinemas);
                        mSearchKey = "";
                    } else if(!content.equals(mSearchKey)){
                        // 搜索
                        mSearchKey = content;
                        search(content);
                    }
                }

            }
        };

        // 获取指定日期下的影院列表回调
        mGetMovieShowtimesCallback = new NetworkManager.NetworkListener<MovieShowTimeCinemaMainBean>() {
            @Override
            public void onSuccess(MovieShowTimeCinemaMainBean result, String showMsg) {
                getUserContentHolder().setRefreshState(false);
                MovieShowTimeCinemaMainBean mainBean = result;
                // 非自营提示
                String noticeNotOwn = null == mainBean ? "" : mainBean.getNoticeNotOwn();
                // bean转换（需要与影院列表页共用方法）
                mAllCinemas = CinemaListHelper.parseToCinemaBaseInfos(mMovieId, mainBean);
                //
                String dateValue = "";
                if(null != mDateListBean
                        && CollectionUtils.isNotEmpty(mDateListBean.getShowtimeDate())
                        && mDateIndex < mDateListBean.getShowtimeDate().size()) {
                    dateValue = mDateListBean.getShowtimeDate().get(mDateIndex).getDateValue();
                }

                if(CollectionUtils.isNotEmpty(mAllCinemas)) {
                    // 显示非自营提示条
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);

                    if (UserManager.Companion.getInstance().isLogin()) {
                        // 获取用户收藏影院列表
                        requestFavCinemas();
                    } else {
                        setPageState(BaseState.SUCCESS);
                        // 更新影院列表
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
                    // 显示非自营提示条
                    getUserContentHolder().showNoticeNotOwn(noticeNotOwn);
                    // 更新影院列表
                    refreshCinemaList(null);
                    MToastUtils.showShortToast("当日无剩余场次");
                }
            }

            @Override
            public void onFailure(NetworkException<MovieShowTimeCinemaMainBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
                getUserContentHolder().setRefreshState(false);
            }
        };

        // 点击日期列表（注：已经选中的排序筛选栏不重置）
        mDateClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int newDateIndex = (Integer) v.getTag();
                // 埋点
                StatisticPageBean bean = assemble(StatisticTicket.TICKET_SELECT_TIME, String.valueOf(newDateIndex + 1),
                        "", "", "", "", getBaseStatisticParam());
                StatisticManager.getInstance().submit(bean);
                // 更新日期列表选中状态
                getUserContentHolder().updateMovieShowtimeDateView(mDateIndex, newDateIndex);
                // 获取选中的新日期
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
                // 更新指定日期下的影院列表
                setPageState(BaseState.LOADING);
                requestDateCinemas();
            }
        };
    }

    // 请求数据
    private void requestData() {
        if (mIsLocation)
            return;

        // 指定地区+指定电影 有影讯的日期列表
        requestMovieShowtimeDates();
        // 影院列表_商圈和地铁筛选数据
        // 不要删除，产品告知会加回来 2020-10-30 by wwl
//        requestCinemaScreening();
    }

    // 指定地区+指定电影 有影讯的日期列表（主接口）
    private void requestMovieShowtimeDates() {
        setPageState(BaseState.LOADING);
        mTicketApi.getMovieShowtimeDates(mCityId, mMovieId, new NetworkManager.NetworkListener<ShowTimeDataMainBean>() {
            @Override
            public void onSuccess(ShowTimeDataMainBean result, String showMsg) {
                mDateListBean = result;
                if (null == mDateListBean || CollectionUtils.isEmpty(mDateListBean.getShowtimeDate())) {
                    MToastUtils.showShortToast("该影片无上映日期数据");
                    return;
                }

                // 更新title
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
                // 显示日期列表
                getUserContentHolder().showMovieShowtimeDateView(mDateIndex, dateListBean, mDateClick);
                // 更新选中日期下的影院列表
                requestDateCinemas();
            }

            @Override
            public void onFailure(NetworkException<ShowTimeDataMainBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
            }
        });
    }

    // 更新指定日期下的影院列表（主接口）
    // 旧：updateDate()
    private void requestDateCinemas() {
        mTicketApi.getLocationMovieShowtimes(mCityId, mMovieId, mNewDate, "", "",mGetMovieShowtimesCallback);
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
        // 影院排序：得到排序好的mNearestCinemas和mNearestCinemas
        CinemaListHelper.sortCinemas(mLocationInfo, mAllCinemas, favAndBeenCinemas, mNearestCinemas,
                mLowestPriceCinemas, mCinemaFeatures);
        // 设置影厅特效数据
        getUserContentHolder().setCinemaFeatureData(mCinemaFeatures, mFeatureIndex);
        // 显示影院列表
        filterCinemaList();
    }

    // 影院列表_商圈和地铁筛选数据
    private void requestCinemaScreening() {
        mTicketApi.getCinemaScreening(mCityId, "", "", new NetworkManager.NetworkListener<CinemaScreeningBean>() {
            @Override
            public void onSuccess(CinemaScreeningBean result, String showMsg) {
                // 商圈和地铁添加全部和数量
                CinemaListHelper.parseDistrictSubwayData(MovieShowtimeActivity.this, result, mDistricts, mSubways);
                // 设置商圈和地铁筛选数据
                getUserContentHolder().setCinemaScreeningData(mDistricts, mSubways);
            }

            @Override
            public void onFailure(NetworkException<CinemaScreeningBean> exception, String showMsg) {
            }
        });
    }

    // 筛选影院列表
    private void filterCinemaList() {
        // 获取排序筛选后用于显示的影院列表
        List<CinemaBaseInfo> showCinemas = CinemaListHelper.getShowCinemas(mSortType, mFeatureIndex,
                mDistrictId, mBusinessId, mSubwayId, mStationId, mNearestCinemas, mLowestPriceCinemas,
                mDistricts, mSubways);
        // 刷新页面数据
        getUserContentHolder().setSelectDateString(mDate);
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

    // 定位成功后更新数据
    private void updateDataAfterLocation() {
        resetVariable();
        mCinemaFeatures = CinemaListHelper.initCinemaFeatures();
        requestData();
    }

    // 搜索（不用加下拉刷新）
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

        // 显示搜索结果
        getUserContentHolder().showSearchResult(showCinemas);
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
