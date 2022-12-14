package com.mtime.bussiness.ticket.holder;

import java.util.List;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.beans.ADDetailBean;
import com.mtime.beans.ADTotalBean;
import com.mtime.bussiness.ticket.cinema.adapter.MovieShowtimeCinemaShowtimeAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.TabTicketCinemaAdapter;
import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.bean.OnlineCinemasData;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBean;
import com.mtime.bussiness.ticket.cinema.util.CinemaListHelper;
import com.mtime.bussiness.ticket.cinema.widget.CinemaFilter;
import com.mtime.bussiness.ticket.cinema.widget.TitleOfMovieShowtimeView;
import com.mtime.bussiness.ticket.movie.bean.ShowtimeDate;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.mtmovie.widgets.ADWebView;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfSearchNewView;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by vivivan.wei on 2017/10/16.
 * Tab_??????_??????????????????Holder
 */

public class TabTicketCinemaHolder extends ContentHolder<OnlineCinemasData> {

    public enum PageEnum {
        CINEMA_LIST,   // ???????????????
        MOVIE_SHOWTIME // ???????????????
    }

    // ?????????
    @BindView(R.id.layout_cinema_list_main_rl)
    View mMainLayout;
    // ??????&??????
    @BindView(R.id.layout_cinema_list_filter_root_rl)
    View mfilterRoot;
    // ????????????
    @BindView(R.id.layout_cinema_list_irecyclerview)
    IRecyclerView mIRecyclerView;
    // ?????????
    @BindView(R.id.layout_cinema_list_empty_ll)
    View mEmptyLayout;
    // ???????????????
    @BindView(R.id.layout_cinema_list_location_bar_rr)
    View mLocationBarLayout;
    @BindView(R.id.layout_cinema_list_location_bar_tip_tv)
    TextView mLocationBarTipTv;
    @BindView(R.id.layout_cinema_list_location_bar_refresh_iv)
    ImageView mLocationBarRefreshIv;
    // ??????
    private ADWebView mAdWebView;
    private ImageView mAdSplitIv;
    // ????????????
    private View mLocationFailLayout;
    private View mLocationFailLineView;
    private View mLocationFailSplitView;
    // ??????????????????
    private TextView mNoticeNotOwnTv;

    // ??????_??????
    @BindView(R.id.layout_cinema_list_search_rl)
    View mSearchLayout;
    // ??????_????????????
    @BindView(R.id.layout_cinema_list_search_irecyclerview)
    IRecyclerView mSearchIRecyclerView;
    // ??????_?????????
    @BindView(R.id.layout_cinema_list_search_empty_ll)
    View mSearchEmptyLayout;

    // ???????????????_title
    @Nullable @BindView(R.id.act_movie_showtime_title)
    View mMovieShowtimeTitleRootView;
    // ???????????????_??????title
    @Nullable @BindView(R.id.act_movie_showtime_search_title)
    View mMovieShowtimeSearchTitleRootView;
    // ???????????????_????????????
    @Nullable @BindView(R.id.act_movie_showtime_date_sv)
    HorizontalScrollView mMovieShowtimeDateScrollView;
    @Nullable @BindView(R.id.act_movie_showtime_date_list_ll)
    LinearLayout mMovieShowtimeDateListLayout;

    // ???????????????
    private TitleOfMovieShowtimeView mMovieShowtimeTitleView;
    private TitleOfSearchNewView mMovieShowtimeSearchTitleView;

    private final Context mContext;
    private final PageEnum mPage;
    private Unbinder mUnbinder;
    private TabTicketCinemaAdapter mAdapter;
    private TabTicketCinemaAdapter mSearchAdapter;
    private MovieShowtimeCinemaShowtimeAdapter.OnDirectSaleShowtimeListener mDirectSaleShowtimeListener;
    // ??????????????????
    private CinemaFilter mCinemaFilter;
    // ?????????????????????
    private boolean mIsSubwayFilter = false;
    // ???????????????_?????????????????????
    private String mDate;

    public TabTicketCinemaHolder(Context context, PageEnum page ) {
        super(context);
        mContext = context;
        mPage = page;
    }

    @Override
    public void onCreate() {

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(mPage == TabTicketCinemaHolder.PageEnum.CINEMA_LIST ? R.layout.frag_cinema_list : R.layout.act_movie_showtime_new);
        initView();
    }

    @Override
    public void refreshView() {
        super.refreshView();

        // ??????????????????
        if(null == mData || CollectionUtils.isEmpty(mData.getList())) {
            // ???????????????
            if(null != mEmptyLayout) {
                mEmptyLayout.setVisibility(View.VISIBLE);
            }
            if(null != mIRecyclerView) {
                mIRecyclerView.setVisibility(View.GONE);
            }
        } else {
            if(null != mEmptyLayout) {
                mEmptyLayout.setVisibility(View.GONE);
            }
            if(null != mIRecyclerView) {
                mIRecyclerView.setVisibility(View.VISIBLE);
                if (null == mAdapter) {
                    mAdapter = new TabTicketCinemaAdapter(mContext, mData.getList(), mDirectSaleShowtimeListener);
                    mAdapter.setSelectDateString(mDate);
                    mIRecyclerView.setIAdapter(mAdapter);
                } else {
                    mAdapter.isSubwayFilter(mIsSubwayFilter);
                    mAdapter.setList(mData.getList());
                    mAdapter.setSelectDateString(mDate);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mCinemaFilter) {
            mCinemaFilter.clear();
            mCinemaFilter = null;
        }
        if(null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

    // ?????????View?????????
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mIRecyclerView.setLayoutManager(layoutManager);
        View headerView = View.inflate(mContext, R.layout.layout_cinema_list_header, null);
        mIRecyclerView.addHeaderView(headerView);

        mSearchIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        // header
        mAdWebView = headerView.findViewById(R.id.layout_cinema_list_header_ad_webview);
        mAdSplitIv = headerView.findViewById(R.id.layout_cinema_list_header_ad_split_iv);
        mLocationFailLayout = headerView.findViewById(R.id.layout_cinema_list_header_location_fail_ll);
        mLocationFailLineView = headerView.findViewById(R.id.layout_cinema_list_header_location_fail_line_view);
        mLocationFailSplitView = headerView.findViewById(R.id.layout_cinema_list_header_location_fail_split_view);
        mNoticeNotOwnTv = headerView.findViewById(R.id.layout_cinema_list_header_not_own_tv);

        mCinemaFilter = new CinemaFilter(mContext, mfilterRoot, null);

        if(null != mMovieShowtimeDateScrollView) {
            mMovieShowtimeDateScrollView.setVisibility(View.GONE);
        }
        mfilterRoot.setVisibility(View.GONE);
        mEmptyLayout.setVisibility(View.GONE);
        mLocationFailLayout.setVisibility(View.GONE);
        // ?????????????????????????????????mNoticeNotOwnTv????????????
        mLocationFailLineView.setVisibility(View.GONE);
        mLocationFailSplitView.setVisibility(View.GONE);
        mNoticeNotOwnTv.setVisibility(View.GONE);

        mIRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            /*@Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(View.VISIBLE == mLocationFailLayout.getVisibility()) {
                    //????????????????????????????????????
                    int firstPosition = layoutManager.findFirstVisibleItemPosition();
                    if (firstPosition > 1) {
                        mLocationBarLayout.setVisibility(View.VISIBLE);
                    } else {
                        mLocationBarLayout.setVisibility(View.GONE);
                    }
                }
            }*/

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (null != mLocationBarLayout) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        mLocationBarLayout.setVisibility(View.VISIBLE);
                    } else {
                        mLocationBarLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        // ?????????????????????????????????????????????
        mSearchIRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideInput();
                if(null != mMovieShowtimeSearchTitleView) {
                    mMovieShowtimeSearchTitleView.clearFoucus();
                }
                return false;
            }
        });
    }

    // ???????????????
    private void hideInput() {
        if (mContext == null
                || !(mContext instanceof BaseFrameUIActivity)
                || null == ((BaseFrameUIActivity)mContext).getCurrentFocus()
        ) {
            return;
        }
        InputMethodManager im = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im == null) {
            return;
        }
        im.hideSoftInputFromWindow(((BaseFrameUIActivity)mContext).getCurrentFocus().getApplicationWindowToken(), 0);
    }

    // ?????????????????????????????????
    public void showAd(ADTotalBean bean) {
        if(null == bean) {
            setAdVisible(false);
            return;
        }

        ADDetailBean item = ToolsUtils.getADBean(bean, App.getInstance().AD_CINEMA_LIST);
        if (!ADWebView.show(item)) {
            setAdVisible(false);
            return;
        }

        setAdVisible(true);

        if(null != mAdWebView) {
            mAdWebView.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
                @Override
                public void onAdItemClick(ADDetailBean item, String url) {
                    // ??????
                    StatisticPageBean statisticBean = ((BaseFrameUIActivity) mContext).assemble(StatisticTicket.TICKET_AD, null, null, null, null, null, null);
                    StatisticManager.getInstance().submit(statisticBean);
                    mAdWebView.setAdReferer(statisticBean.toString());
                }
            });

            mAdWebView.load(mContext, item);
        }
    }

    // ????????????????????????
    public void setAdVisible(boolean visible) {
        if(null != mAdWebView) {
            mAdWebView.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        if(null != mAdSplitIv) {
            mAdSplitIv.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    // ??????????????????
    public void showSearchView() {
        if(null != mMainLayout) {
            mMainLayout.setVisibility(View.GONE);
        }
        if(null != mSearchLayout) {
            mSearchLayout.setVisibility(View.VISIBLE);
        }
        if(null != mSearchIRecyclerView) {
            mSearchIRecyclerView.setVisibility(View.VISIBLE);
        }
        if(null != mSearchEmptyLayout) {
            mSearchEmptyLayout.setVisibility(View.GONE);
        }
    }

    // ????????????????????????
    public boolean isSearchState() {
        return null != mSearchLayout && mSearchLayout.getVisibility() == View.VISIBLE;
    }

    // ????????????????????????
    public void showSearchResult(List<CinemaBaseInfo> showCinemas) {
        if(null != mSearchIRecyclerView) {
            mSearchIRecyclerView.setVisibility(View.VISIBLE);
        }
        if(null != mSearchEmptyLayout) {
            mSearchEmptyLayout.setVisibility(View.GONE);
        }
        if (null == mSearchAdapter) {
            mSearchAdapter = new TabTicketCinemaAdapter(mContext, showCinemas, mDirectSaleShowtimeListener);
            mSearchAdapter.setSelectDateString(mDate);
            if(null != mSearchIRecyclerView) {
                mSearchIRecyclerView.setIAdapter(mSearchAdapter);
            }
        } else {
            mSearchAdapter.setSelectDateString(mDate);
            mSearchAdapter.setList(showCinemas);
            mSearchAdapter.notifyDataSetChanged();
        }
    }

    // ?????????????????????
    public void showSearchEmpty() {
        if(null != mSearchIRecyclerView) {
            mSearchIRecyclerView.setVisibility(View.GONE);
        }
        if(null != mSearchEmptyLayout) {
            mSearchEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    // ????????????
    public void cancelSearch() {
        if(null != mMainLayout) {
            mMainLayout.setVisibility(View.VISIBLE);
        }
        if(null != mSearchLayout) {
            mSearchLayout.setVisibility(View.GONE);
        }
        if(null != mSearchIRecyclerView) {
            mSearchIRecyclerView.setVisibility(View.GONE);
        }
        if(null != mSearchEmptyLayout) {
            mSearchEmptyLayout.setVisibility(View.GONE);
        }
    }

    // ?????????????????????
    public void isSubwayFilter(boolean subwayFilter) {
        mIsSubwayFilter = subwayFilter;
    }

    // ??????????????????
    public void setCinemaFilterListener(CinemaFilter.ICinemaFilterClickListener listener) {
        if(null != mCinemaFilter) {
            mCinemaFilter.setFilterListener(listener);
        }
    }

    // ????????????????????????
    public void setCinemaFeatureData(List<CinemaFeatureBean> cinemaFeatures, int featureIndex) {
        if(null != mfilterRoot && mfilterRoot.getVisibility() == View.GONE) {
            mfilterRoot.setVisibility(View.VISIBLE);
        }
        if(null != mCinemaFilter) {
            mCinemaFilter.setCinemaFeatureData(cinemaFeatures, featureIndex);
        }
    }

    // ?????????????????????????????????
    public void setCinemaScreeningData(List<DistrictBean> districts, List<SubwayBean> subways) {
        if(null != mCinemaFilter) {
            mCinemaFilter.setCinemaScreeningData(districts, subways);
        }
    }

    // ??????????????????
    public void resetCinemaFilter() {
        if(null != mCinemaFilter) {
            mCinemaFilter.reset();
        }
    }

    // ??????????????????
    public void showLocationFail() {
        if(null != mLocationFailLayout) {
            mLocationFailLayout.setVisibility(View.VISIBLE);
        }
    }

    // ??????????????????
    public void hideLocationFail() {
        if(null != mLocationFailLayout) {
            mLocationFailLayout.setVisibility(View.GONE);
        }
    }

    // ?????????????????????
    public void updateLocationBar() {
        if(null != mLocationBarTipTv) {
            mLocationBarTipTv.setText(R.string.cinema_list_location_bar_locating);
        }
        if(null != mLocationBarRefreshIv) {
            mLocationBarRefreshIv.setVisibility(View.GONE);
        }
    }

    public void updateLocationBarWithFail(int errorCode) {
        if (null != mLocationBarTipTv) {
            mLocationBarTipTv.setText(
                    errorCode == LocationException.CODE_PERMISSION_DENIED ?
                            R.string.cinema_list_location_fail
                            : R.string.cinema_list_location_bar_fail);
        }
        if(null != mLocationBarRefreshIv) {
            mLocationBarRefreshIv.setVisibility(View.VISIBLE);
        }
    }

    public void updateLocationBar(String text) {
        if (null != mLocationBarTipTv) {
            mLocationBarTipTv.setText("?????????" + text);
        }
        if(null != mLocationBarRefreshIv) {
            mLocationBarRefreshIv.setVisibility(View.VISIBLE);
        }
    }

    // ????????????????????????
    public void showNoticeNotOwn(String noticeNotOwn) {
        // ?????????????????????????????????mNoticeNotOwnTv????????????
        boolean hideTip = TextUtils.isEmpty(noticeNotOwn);
        if(null != mLocationFailLineView) {
            mLocationFailLineView.setVisibility(hideTip ? View.VISIBLE : View.GONE);
        }
        if(null != mLocationFailSplitView) {
            mLocationFailSplitView.setVisibility(hideTip ? View.VISIBLE : View.GONE);
        }
        if(null != mNoticeNotOwnTv) {
            mNoticeNotOwnTv.setVisibility(!hideTip ? View.VISIBLE : View.GONE);
            mNoticeNotOwnTv.setText(noticeNotOwn);
        }
    }

    // ??????????????????
    public void setOnClickListener(View.OnClickListener listener) {
        if(null != mLocationBarLayout) {
            mLocationBarLayout.setOnClickListener(listener);
        }
    }

    // ??????????????????
    public void setOnRefreshListener(OnRefreshListener listener) {
        if(null != mIRecyclerView) {
            mIRecyclerView.setOnRefreshListener(listener);
        }
    }

    // ????????????????????????
    public void setRefreshState(boolean refreshState) {
        if(null != mIRecyclerView) {
            mIRecyclerView.setRefreshing(false);
        }
    }

    // ***************** ??????????????? begin *****************

    // ?????????title
    public void initMovieShowtimeTitle(String title,
                                       BaseTitleView.ITitleViewLActListener titleListener,
                                       BaseTitleView.ITitleViewLActListener searchTitleListener) {
        // title
        if(null != mMovieShowtimeTitleRootView) {
            mMovieShowtimeTitleView = new TitleOfMovieShowtimeView(mContext, mMovieShowtimeTitleRootView, titleListener);
            setMovieShowtimeTitle(title);
        }

        // ??????title
        if(null != mMovieShowtimeSearchTitleRootView) {
            mMovieShowtimeSearchTitleView = new TitleOfSearchNewView((BaseFrameUIActivity) mContext, mMovieShowtimeSearchTitleRootView,
                    searchTitleListener);
            mMovieShowtimeSearchTitleView.setCloseParent(false);
            mMovieShowtimeSearchTitleView.setEditHint(mContext.getResources().getString(R.string.str_title_search_hint_cinemacontent));
        }
    }

    // ??????title
    public void setMovieShowtimeTitle(String title) {
        if(null != mMovieShowtimeTitleView) {
            mMovieShowtimeTitleView.setTitle(title);
        }
    }

    // ??????????????????
    public void showMovieShowtimeSearchLayout() {
        // ????????????
        if(null != mMovieShowtimeTitleRootView) {
            mMovieShowtimeTitleRootView.setVisibility(View.INVISIBLE);
        }
        if(null != mMovieShowtimeSearchTitleRootView) {
            mMovieShowtimeSearchTitleRootView.setVisibility(View.VISIBLE);
        }
        if(null != mMovieShowtimeDateScrollView) {
            mMovieShowtimeDateScrollView.setVisibility(View.GONE);
        }
        showSearchView();
        if(null != mMovieShowtimeSearchTitleView) {
            mMovieShowtimeSearchTitleView.setFocus();
            mMovieShowtimeSearchTitleView.setEditTextConent("");
            mMovieShowtimeSearchTitleView.hideClearIcon();
        }
    }

    // ????????????????????????
    public void movieShowtimeSearchBackPressed() {
        if(null != mMovieShowtimeTitleRootView) {
            mMovieShowtimeTitleRootView.setVisibility(View.VISIBLE);
        }
        // ????????????
        if(null != mMovieShowtimeSearchTitleRootView) {
            mMovieShowtimeSearchTitleRootView.setVisibility(View.INVISIBLE);
        }
        if(null != mMovieShowtimeDateScrollView) {
            mMovieShowtimeDateScrollView.setVisibility(View.VISIBLE);
        }
        if(null != mMovieShowtimeSearchTitleView) {
            mMovieShowtimeSearchTitleView.hideInput();
            mMovieShowtimeSearchTitleView.clearFoucus();
        }
        cancelSearch();
    }

    // ?????????????????? ??????showData()
    public void showMovieShowtimeDateView(int selectIndex, List<ShowtimeDate> dateListBean, View.OnClickListener dateClick) {
        if(null == mMovieShowtimeDateListLayout) {
            return;
        }
        if(null != mMovieShowtimeDateScrollView && mMovieShowtimeDateScrollView.getVisibility() == View.GONE) {
            mMovieShowtimeDateScrollView.setVisibility(View.VISIBLE);
        }

        mMovieShowtimeDateListLayout.removeAllViews();
        View itemView;
        TextView textTv;
        View lineView;
        for (int i = 0; i < dateListBean.size(); i++) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.item_movie_showtime_date, null);
            textTv = itemView.findViewById(R.id.item_movie_showtime_date_tv);
            lineView = itemView.findViewById(R.id.item_movie_showtime_date_line_view);
            String date = CinemaListHelper.getStringDate(dateListBean.get(i).getDateValue());
            textTv.setText(date);
            setMovieShowtimeDateSelect(i == selectIndex, textTv, lineView);
            itemView.setTag(i);
            itemView.setOnClickListener(dateClick);
            mMovieShowtimeDateListLayout.addView(itemView);
        }
    }

    // ??????????????????????????????
    public void updateMovieShowtimeDateView(int preIndex, int newIndex) {
        setMovieShowtimeDateView(preIndex, false);
        setMovieShowtimeDateView(newIndex, true);
    }

    // ?????????????????????String
    public void setSelectDateString(String date) {
        mDate = date;
    }

    // ??????????????????/?????????????????????????????????
    public void setOnDirectSaleShowtimeListener(MovieShowtimeCinemaShowtimeAdapter.OnDirectSaleShowtimeListener listener) {
        mDirectSaleShowtimeListener = listener;
    }

    // ????????????????????????????????????????????????
    private void setMovieShowtimeDateView(int index, boolean isSelect) {
        if(null == mMovieShowtimeDateListLayout || index >= mMovieShowtimeDateListLayout.getChildCount()) {
            return;
        }

        View itemView = mMovieShowtimeDateListLayout.getChildAt(index);
        if(null != itemView) {
            TextView textTv = itemView.findViewById(R.id.item_movie_showtime_date_tv);
            View lineView = itemView.findViewById(R.id.item_movie_showtime_date_line_view);
            setMovieShowtimeDateSelect(isSelect, textTv, lineView);
        }
    }

    // ??????/??????????????????
    private void setMovieShowtimeDateSelect(boolean isSelect, TextView textTv, View lineView) {
        if(isSelect) {
            textTv.setTextColor(ContextCompat.getColor(mContext,R.color.color_333333));
            lineView.setVisibility(View.VISIBLE);
        } else {
            textTv.setTextColor(ContextCompat.getColor(mContext,R.color.color_bbbbbb));
            // ?????????INVISIBLE??????????????????????????????????????????
            lineView.setVisibility(View.INVISIBLE);
        }
    }

    // ***************** ??????????????? end *******************
}
