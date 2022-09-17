package com.mtime.bussiness.ticket;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.mtime.ktx.ext.MarginExtKt;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.search.ISearchProvider;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.main.MainCommunicational;
import com.mtime.bussiness.ticket.MovieAndCinemaSwitchView.IMovieAndCinemaSwitchViewListener;
import com.mtime.bussiness.ticket.adapter.TabTicketSubPagerAdapter;
import com.mtime.bussiness.ticket.cinema.TabTicketCinemaFragment;
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesFragment;
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesInComingFragment;
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesOnShowFragment;
import com.mtime.bussiness.ticket.widget.TitleOfHomeAndMovieView;
import com.mtime.event.entity.CityChangedEvent;
import com.mtime.frame.App;
import com.mtime.frame.BaseFragment;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.JumpUtil;
import com.mtime.widgets.BaseTitleView.ActionType;
import com.mtime.widgets.BaseTitleView.ITitleViewLActListener;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfSearchNewView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


/**
 * 购票tab Fragment
 */
public class TabTicketFragment extends BaseFragment implements MainCommunicational {


    public static TabTicketFragment newInstance() {
        TabTicketFragment fragment = new TabTicketFragment();
        fragment.openSubmit();
        return fragment;
    }

    public static final int TYPE_MOVIE_HOT = CommConstant.TYPE_TICKET_HOME_MOVIE_HOT;
    public static final int TYPE_MOVIE_INCOMING = CommConstant.TYPE_TICKET_HOME_MOVIE_INCOMING;
    public static final int TYPE_CINEMA = CommConstant.TYPE_TICKET_HOME_MOVIE_CINEMA;
    public static int type = TYPE_MOVIE_HOT;//当前要显示的界面
    public static boolean forceRefreshCinemaList = false;  // 强行更新影院列表， 比如从购票支付页点击顶部返回箭头回来

    public TitleOfHomeAndMovieView titleCity;
    private TitleOfSearchNewView titleSearch;
    public IMovieAndCinemaSwitchViewListener listener;
    private String mCityId;
    private String mCityName;
    // 定位信息
    private LocationInfo mLocationInfo;
    private ArrayList<Fragment> fragments;
    private ViewPager mViewPager;
    private TabTicketSubPagerAdapter mTabTicketSubPagerAdapter;
    private Fragment currentFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        openSubmit();
    }

    public void openSubmit() {
        mBaseStatisticHelper.setPageLabel(StatisticTicket.PN_TICKET);
        mBaseStatisticHelper.setSubmit(true);

    }

    @Override
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buyticket_main, container, false);
    }

    @Override
    protected void onInitVariable() {
        handleTopMargin();
        LocationHelper.location(getContext(), new OnLocationCallback() {

            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if (null != locationInfo) {
                    mLocationInfo = locationInfo.clone();
                } else {
                    mLocationInfo = LocationHelper.getDefaultLocationInfo();
                }
                mCityId = mLocationInfo.getCityId();
                mCityName = mLocationInfo.getCityName();
                initVariable();
                initView();
            }

            @Override
            public void onLocationFailure(LocationException e) {
                onLocationSuccess(LocationHelper.getDefaultLocationInfo());
            }
        });
    }

    private void initVariable() {
        // 切换顶部电影/影院Tab
        listener = new IMovieAndCinemaSwitchViewListener() {

            @Override
            public void onEvent(boolean leftOn) {
                if (leftOn) {
                    titleSearch.setEditHint(context.getResources().getString(R.string.str_title_search_hint_content));
                    titleCity.setSearchType(false);
                    if (null != mViewPager) {
                        mViewPager.setCurrentItem(0);
                    }

                    if (currentFragment instanceof TicketMoviesFragment) {
                        if (type == TYPE_MOVIE_HOT) {
                            ((TicketMoviesFragment) currentFragment).viewPager.setCurrentItem(0);
                        } else {
                            ((TicketMoviesFragment) currentFragment).viewPager.setCurrentItem(1);
                        }

                        Fragment fragment = ((TicketMoviesFragment.MyPagerAdapter) ((TicketMoviesFragment) currentFragment).viewPager.getAdapter()).getCurrentFragment();
                        if (fragment != null && fragment instanceof TicketMoviesOnShowFragment) {
                            if (((TicketMoviesOnShowFragment) fragment).needRequest(mCityId) || ((TicketMoviesOnShowFragment) fragment).getListMovieData() == null || ((TicketMoviesOnShowFragment) fragment).getListMovieData().size() == 0) {
                                ((TicketMoviesOnShowFragment) fragment).onLoadData();
                            }
                            type = TYPE_MOVIE_HOT;
                        } else {
                            if (fragment != null && fragment instanceof TicketMoviesInComingFragment) {
                                if (((TicketMoviesInComingFragment) fragment).needRequest(mCityId) || ((TicketMoviesInComingFragment) fragment).getAttentionsBean() == null || ((TicketMoviesInComingFragment) fragment).getAttentionsBean().size() == 0) {
                                    ((TicketMoviesInComingFragment) fragment).reLoadData();
                                }
                            }
                            type = TYPE_MOVIE_INCOMING;
                        }
                    }
                } else {
                    // 正在热映|即将上映页中点击"影院"
                    titleSearch.setEditHint(context.getResources().getString(R.string.str_title_search_hint_cinemacontent));
                    titleCity.setSearchType(true);
                    type = TYPE_CINEMA;
                    mViewPager.setCurrentItem(1);
                    if (currentFragment != null && currentFragment instanceof TabTicketCinemaFragment &&
                            (((TabTicketCinemaFragment) currentFragment).needRequest(mLocationInfo) || forceRefreshCinemaList)) {
                        // 刷新影院列表页
                        refreshCinemaList();
                    }
                }
            }
        };
    }

    // 刷新影院列表页
    private void refreshCinemaList() {
        // title
        titleSearch.setVisibile(View.INVISIBLE);
        titleSearch.hideInput();
        titleCity.setVisibile(View.VISIBLE);

        ((TabTicketCinemaFragment) currentFragment).setCityId(mCityId);
        ((TabTicketCinemaFragment) currentFragment).setFirstLoad();
        ((TabTicketCinemaFragment) currentFragment).cancelSearch();
        ((TabTicketCinemaFragment) currentFragment).updateDataAfterLocation();
        //
        forceRefreshCinemaList = false;
    }

    // 刷新正在热映或即将上映
    private void refreshMovies() {
        if (!(currentFragment instanceof TicketMoviesFragment) || null == ((TicketMoviesFragment) currentFragment).viewPager) {
            return;
        }

        ((TicketMoviesFragment) currentFragment).setCityId(mCityId);

        Fragment fragment = ((TicketMoviesFragment.MyPagerAdapter) ((TicketMoviesFragment) currentFragment).viewPager.getAdapter()).getCurrentFragment();
        if (null != fragment) {
            if (fragment instanceof TicketMoviesOnShowFragment && ((TicketMoviesOnShowFragment) fragment).needRequest(mCityId)) {
                // 刷新正在热映
                ((TicketMoviesOnShowFragment) fragment).onLoadData();
            } else if (fragment instanceof TicketMoviesInComingFragment && ((TicketMoviesInComingFragment) fragment).needRequest(mCityId)) {
                // 刷新即将上映
                ((TicketMoviesInComingFragment) fragment).reLoadData();
            }
        }
    }

    private void initView() {
        TicketMoviesFragment ticketMoviesFragment = new TicketMoviesFragment();
        ticketMoviesFragment.setCheckSwitchCityListener(new TicketMoviesFragment.CheckSwitchCityListener() {
            @Override
            public void onCheckSwitchCity() {
                if (!TextUtils.isEmpty(mCityId)) {
                    LocationHelper.location(getContext().getApplicationContext(), new OnLocationCallback() {
                        @Override
                        public void onLocationSuccess(LocationInfo locationInfo) {
                            if (null != locationInfo && !TextUtils.equals(mCityId, locationInfo.getCityId())) {
                                mCityId = locationInfo.getCityId();
                                mCityName = locationInfo.getCityName();
                                titleCity.update(mCityName);
                                mLocationInfo = locationInfo.clone();
                                refreshMovies();
                            } else {
                                refreshMovies();
                            }
                        }

                        @Override
                        public void onLocationFailure(LocationException e) {
                            onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                        }
                    });
                }
            }
        });
        TabTicketCinemaFragment ticketCinemaFragment = new TabTicketCinemaFragment();
        ticketMoviesFragment.setCityId(mCityId);
        fragments = new ArrayList<>();
        fragments.add(ticketMoviesFragment);
        fragments.add(ticketCinemaFragment);
        if (null != getView()) {
            mViewPager = getView().findViewById(R.id.ticket_buyticket_viewpager);

            //绑定适配器 bugly 43182
//        if(this.isAdded()) {
            mTabTicketSubPagerAdapter = new TabTicketSubPagerAdapter(getChildFragmentManager(), fragments);
//        }
            mViewPager.setAdapter(mTabTicketSubPagerAdapter);
            //添加切换界面的监听器
            mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
            // 默认选中影片
            if (null == currentFragment) {
                currentFragment = ticketMoviesFragment;
            }

            View navBar = getView().findViewById(R.id.navigationbar);
            titleCity = new TitleOfHomeAndMovieView(context, navBar, mCityName, StructType.TYPE_HOME_SHOW_MOVINGS, listener, new ITitleViewLActListener() {

                @Override
                public void onEvent(ActionType type, String content) {
                    if (!titleCity.getSearchType()) {
                        return;
                    }
                    titleCity.setVisibile(View.INVISIBLE);
                    titleSearch.setVisibile(View.VISIBLE);
                    titleSearch.setFocus();

                }
            }, new TitleOfHomeAndMovieView.ILogXListener() {
                @Override
                public void onEvent(ActionType type) {
                    if (type == ActionType.TYPE_CITY_CLICK) {
                        if (mViewPager.getCurrentItem() == 0) {
//                            context.setPageLabel((TabTicketFragment.type == TYPE_MOVIE_INCOMING) ? StatisticTicket.PN_FUTURE_SCHEDULE : StatisticTicket.PN_ON_SHOW_LIST);
                        }
                        if (mViewPager.getCurrentItem() == 1) {
//                            context.setPageLabel(StatisticTicket.PN_CINEMA_LIST);
                        }
//                        StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_TOP_NAVIGATION, null, "selectCity", null, null, null, null);
//                        StatisticManager.getInstance().submit(bean);
                        JumpUtil.startCityChangeActivityForResult(context, null, 0);

                    } else if (type == ActionType.TYPE_SEARCH_CLICK) {
                        if (mViewPager.getCurrentItem() == 0) {
//                            context.setPageLabel((TabTicketFragment.type == TYPE_MOVIE_INCOMING) ? StatisticTicket.PN_FUTURE_SCHEDULE : StatisticTicket.PN_ON_SHOW_LIST);
//                            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_TOP_NAVIGATION, null, "search", null, null, null, null);
//                            StatisticManager.getInstance().submit(bean);
                            // 跳转搜索页
                            ISearchProvider provider = ProviderExtKt.getProvider(ISearchProvider.class);
                            provider.startSearchActivity(mContext);
                        }
                        if (mViewPager.getCurrentItem() == 1) {
//                            context.setPageLabel(StatisticTicket.PN_CINEMA_LIST);
//                            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_TOP_NAVIGATION, null, "search", null, null, null, null);
//                            StatisticManager.getInstance().submit(bean);
                            // 显示搜索影院布局
                            if (currentFragment != null && currentFragment instanceof TabTicketCinemaFragment) {
                                ((TabTicketCinemaFragment) currentFragment).showSearchView();
                                if (null != titleSearch) {
                                    titleSearch.setEditTextConent("");
                                    titleSearch.hideClearIcon();
                                }
                            }
                        }
                    }
                }
            });

            View search = getView().findViewById(R.id.search_title);
            titleSearch = new TitleOfSearchNewView(context, search, new ITitleViewLActListener() {

                @Override
                public void onEvent(ActionType type, String content) {
                    if (!titleSearch.isVisibile()) {
                        return;
                    }

                    // 点击"取消"
                    if (ActionType.TYPE_BACK == type) {
                        titleSearch.setVisibile(View.INVISIBLE);
                        titleCity.setVisibile(View.VISIBLE);
                        titleSearch.hideInput();

                        if (currentFragment != null && currentFragment instanceof TabTicketCinemaFragment) {
                            // 埋点
//                            context.setPageLabel(StatisticTicket.PN_CINEMA_LIST);
//                            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_SEARCH_CANCEL, null, null, null, null, null, null);
//                            StatisticManager.getInstance().submit(bean);
                            // 取消搜索
                            ((TabTicketCinemaFragment) currentFragment).cancelSearch();
                        }
                        return;
                    }

                    // 搜索影院
                    if (ActionType.TYPE_CONTENT_CHANGED == type || ActionType.TYPE_SEARCH == type) {
                        if (currentFragment != null && currentFragment instanceof TabTicketCinemaFragment) {
                            if (TextUtils.isEmpty(content)) {
                                // 清空搜索
                                ((TabTicketCinemaFragment) currentFragment).clearSearch();
                            } else {
                                // 点击"搜索"：搜索影院
                                ((TabTicketCinemaFragment) currentFragment).search(content);
                            }
                        }
                    }
                }
            });
            titleSearch.setCloseParent(false);

            if (type == TYPE_CINEMA) {
                mViewPager.setCurrentItem(1);
                if (null == currentFragment) {
                    currentFragment = ticketCinemaFragment;
                }
            }

        }
    }

    private void handleTopMargin() {
        if (getView() == null) {
            return;
        }
        MarginExtKt.topStatusMargin(getView().findViewById(R.id.ticketRootView));
    }

    //城市变更更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCityDatas(CityChangedEvent event) {
        if (null != event && !TextUtils.equals(mCityId, event.newCityId)) {
            mCityId = event.newCityId;
            mCityName = event.newCityName;
            titleCity.update(mCityName);
//            if (currentFragment instanceof  TicketMoviesFragment) {
//                ((TicketMoviesFragment)currentFragment).setCityId(mCityId);
//            }else {
//                ((TabTicketCinemaFragment)currentFragment).setCityId(mCityId);
//            }
            refreshMovies();
            // 刷新影院列表页
            refreshCinemaList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        switch (type) {
            case TYPE_MOVIE_HOT://正在热映
            case TYPE_MOVIE_INCOMING://即将上映
                /*if (listener != null) {
                    listener.onEvent(true);
                }
                if (titleCity != null) {
                    titleCity.setCinemaViewOn(context, false);
                }*/
                if (null != mViewPager) {
                    mViewPager.setCurrentItem(0);
                }
                break;
            case TYPE_CINEMA: //影院列表
                /*if (listener != null) {
                    listener.onEvent(false);
                }
                if (titleCity != null) {
                    titleCity.setCinemaViewOn(context, true);
                }*/
                if (null != mViewPager) {
                    mViewPager.setCurrentItem(1);
                }
                break;
            default:
                break;

        }
    }

    /*@Override
    public void onStart() {
        // 检查是否切换了城市
        if (!TextUtils.isEmpty(mCityId)) {
            LocationHelper.location(getContext().getApplicationContext(), new OnLocationCallback() {
                @Override
                public void onLocationFailure(LocationException e) {
                    onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                }

                @Override
                public void onLocationSuccess(LocationInfo locationInfo) {
                    if (null != locationInfo && !TextUtils.equals(mCityId, locationInfo.getCityId())) {
                        mCityId = locationInfo.getCityId();
                        mCityName = locationInfo.getCityName();
                        titleCity.update(mCityName);
                        mLocationInfo = locationInfo.clone();
                        if (null != currentFragment) {
                            if (currentFragment instanceof TabTicketCinemaFragment && ((TabTicketCinemaFragment) currentFragment).needRequest(mLocationInfo)) {
                                // 刷新影院列表
                                refreshCinemaList();
                            } else if (currentFragment instanceof TicketMoviesFragment) {
                                // 影片标签
                                Fragment fragment = ((TicketMoviesFragment.MyPagerAdapter) ((TicketMoviesFragment) currentFragment).viewPager.getAdapter()).getCurrentFragment();
                                if (null != fragment) {
                                    if (fragment instanceof TicketMoviesOnShowFragment && ((TicketMoviesOnShowFragment) fragment).needRequest(mCityId)) {
                                        // 刷新正在热映
                                        ((TicketMoviesOnShowFragment) fragment).onLoadData();
                                    } else if (fragment instanceof TicketMoviesInComingFragment && ((TicketMoviesInComingFragment) fragment).needRequest(mCityId)) {
                                        // 刷新即将上映
                                        ((TicketMoviesInComingFragment) fragment).reLoadData();
                                    }
                                }
                            }
                        }
                    } else if (currentFragment != null && currentFragment instanceof TabTicketCinemaFragment && forceRefreshCinemaList) {
                        // 刷新影院列表
                        refreshCinemaList();
                    }
                }
            });
        }

        super.onStart();
    }*/

    /**
     * 切换城市
     * public void switchCity() {
     * onStart();
     * }
     */

    @Override
    public boolean onHandleMainEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case MainCommunicational.EVENT_ON_PARSE_INTENT:
                if (null != bundle && bundle.containsKey(App.getInstance().MAIN_TAB_BUYTICKET_TYPE)) {
                    TabTicketFragment.type = bundle.getInt(App.getInstance().MAIN_TAB_BUYTICKET_TYPE);
                    TabTicketFragment.forceRefreshCinemaList = bundle.getBoolean(App.getInstance().MAIN_TAB_BUYTICKET_CINEMA_LIST_REFRESH, false);
                    if (null != mViewPager) {
                        mViewPager.postDelayed(() -> listener.onEvent(type != TYPE_CINEMA), 500);
                    }
                }
                break;

            default:
                break;
        }
        return false;
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(int arg0) {
            if (null != fragments && arg0 >= 0 && arg0 < fragments.size()) {
                currentFragment = fragments.get(arg0);
            }

            //与顶部电影 影院联动
            titleCity.setCinemaViewOn(context, arg0 != 0);
            if (arg0 == 1) {
                type = TYPE_CINEMA;
            } else {
                if (currentFragment instanceof TicketMoviesFragment && null != ((TicketMoviesFragment) currentFragment).viewPager) {
                    PagerAdapter adapter = ((TicketMoviesFragment) currentFragment).viewPager.getAdapter();
                    if (null != adapter) {
                        Fragment fragment = ((TicketMoviesFragment.MyPagerAdapter) adapter).getCurrentFragment();
                        if (fragment instanceof TicketMoviesOnShowFragment) {
                            type = TYPE_MOVIE_HOT;
                        } else {
                            type = TYPE_MOVIE_INCOMING;
                        }
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
