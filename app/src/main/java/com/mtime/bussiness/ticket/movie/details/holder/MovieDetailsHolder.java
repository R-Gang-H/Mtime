package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.router.liveevent.event.CollectionState;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.common.bean.MovieLatestReviewBean;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieDetailsTabAdapter;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsActorsBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsAdBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsArticleBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsAssociatedMovieBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsAwardBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsBoxOfficeBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsClassicLinesBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsEventsBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsExtendInfoBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsIntroBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsLongReviewBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsPicturesBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsShortReviewBinder;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsTrailersBinder;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsActors;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsArticle;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBoxOffice;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsDataBank;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsEvents;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsExtendBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotReviewsBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsIntro;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsTab;
import com.mtime.bussiness.ticket.movie.dialog.FilmSourceChoiceDialog;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.frame.BaseStatisticHelper;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.COLLECTION_OR_CANCEL;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-21
 */
public class MovieDetailsHolder extends ContentHolder<MovieDetailsBean> {
    public static final int EVENT_MOVIE_COLLECT = 110;
    public static final int EVENT_MOVIE_CANCEL_COLLECT = 111;
    public static final int EVENT_MOVIE_SHARE = 112;

    public static final int PAGE_REVIEW = 201;

    @BindView(R.id.activity_movie_details_app_bar_layout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.activity_movie_details_title_layout_fl)
    ViewGroup mTitleLayout;
    @BindView(R.id.activity_movie_details_title_name_tv)
    TextView mTitleNameTv;
    @BindView(R.id.activity_movie_details_title_star_iv)
    ImageView mTitleCollectIv;
    @BindView(R.id.activity_movie_details_title_share_iv)
    ImageView mTitleShareTv;
    @BindView(R.id.activity_movie_details_title_back_iv)
    ImageView mTitleBackTv;
    @BindView(R.id.activity_movie_details_tab_layout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.activity_movie_details_empty_view_pager)
    ViewPager mEmptyViewPager;
    @BindView(R.id.activity_movie_details_rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.activity_movie_details_bottom_btn_iv)
    ImageView mBottomBtnIv;

    private MovieDetailsBean mMovieDetailsBean;
    private MovieDetailsBasic mMovieDetailsBasic;
    private MovieDetailsExtendBean mMovieDetailsExtendBean;
//    private MovieDetailsHotTopicsBean mMovieDetailsHotTopicsBean;
    private MovieDetailsHotReviewsBean mMovieDetailsHotReviewsBean;
    private CategoryVideosBean mCategoryVideosBean;
    private CommonAdListBean mCommonAdListBean;

    private final MultiTypeAdapter mAdapter = new MultiTypeAdapter();
    private final Items mItems = new Items();
    private MovieDetailsHeadHelper mMovieDetailsHeadHelper;
    private TabLayoutHelper mTabLayoutHelper;
    private final List<MovieDetailsTab> mTabs = new ArrayList<>();
    private PagerAdapter mTabAdapter;
    private Unbinder mUnbinder;
    private MovieDetailsTrailersBinder mTrailersBinder;
    private final BaseStatisticHelper mBaseStatisticHelper;
    private final OnJumpPageCallback mOnJumpPageCallback;

    public MovieDetailsHolder(Context context, BaseStatisticHelper helper, OnJumpPageCallback listener) {
        super(context);
        mBaseStatisticHelper = helper;
        mOnJumpPageCallback = listener;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_movie_details);
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            final int statusBarColor = 0x00ffffff;
            final int height = MScreenUtils.dp2px(150);

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                final int scrollRange = appBarLayout.getTotalScrollRange();
                int pos = Math.abs(verticalOffset);

                //tab??????????????????????????????
                if (pos >= scrollRange) {
                    // ???tablayout?????????????????????
                    mTabLayout.setBackgroundColor(Color.WHITE);
                } else {
                    // ???tablayout??????????????????????????????
                    mTabLayout.setBackgroundResource(R.drawable.common_tab_bg);
                }

                //??????????????????title???????????????title????????????????????????
                if (pos < 0) {
                    pos = 0;
                } else if (pos > height) {
                    pos = height;
                }
                if (pos == height) {
                    if (null != mMovieDetailsBasic) {
                        mTitleNameTv.setText(mMovieDetailsBasic.name);
                    }
                    mTitleShareTv.setImageResource(R.drawable.common_title_bar_share);
                    mTitleBackTv.setImageResource(R.drawable.common_icon_title_back);
                    mTitleCollectIv.setImageResource(R.drawable.selector_common_star2);
                } else {
                    mTitleNameTv.setText("");
                    mTitleShareTv.setImageResource(R.drawable.movie_details_ic_title_share);
                    mTitleBackTv.setImageResource(R.drawable.movie_details_ic_title_back);
                    mTitleCollectIv.setImageResource(R.drawable.selector_common_star);
                }
                float ratio = pos / (float)height;
                int alpha = (int) (255 * ratio);
                int color = statusBarColor & 0x00ffffff | (alpha << 24);
                mTitleLayout.setBackgroundColor(color);
            }
        });

        mMovieDetailsHeadHelper = new MovieDetailsHeadHelper(getViewById(R.id.activity_movie_details_head_layout), mOnJumpPageCallback, mBaseStatisticHelper);
        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, TabLayoutHelper.TYPE_OF_WEIGHT);
        mTabAdapter = new MovieDetailsTabAdapter(mTabs);

        mTabLayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                MovieDetailsTab tab = mTabs.get(position);
                if (null != tab) {
                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPositionWithOffset(tab.startPos, 0);

                    // ????????????
                    MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                    mBaseStatisticHelper.assemble1(
                            "quickGo", null,
                            tab.secRegion, null,
                            null, null, mapBuild.build()).submit();
                }
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onSelectedTab();
            }
        });

        mAdapter.register(MovieDetailsActors.class, new MovieDetailsActorsBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsArticle.class, new MovieDetailsArticleBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsExtendBean.AssociatedMovies.class, new MovieDetailsAssociatedMovieBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsBasic.Award.class, new MovieDetailsAwardBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsBoxOffice.class, new MovieDetailsBoxOfficeBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsDataBank.ClassicLines.class, new MovieDetailsClassicLinesBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsEvents.class, new MovieDetailsEventsBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsDataBank.class, new MovieDetailsExtendInfoBinder(mOnJumpPageCallback, mBaseStatisticHelper));
//        mAdapter.register(MovieDetailsRelatedGoods.class, new MovieDetailsGoodsBinder(mOnJumpPageCallback, mBaseStatisticHelper));
//        mAdapter.register(MovieDetailsHotTopicsBean.class, new MovieDetailsHotTopicsBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsIntro.class, new MovieDetailsIntroBinder(mOnJumpPageCallback, mBaseStatisticHelper));
//        mAdapter.register(MovieDetailsLive.class, new MovieDetailsLiveBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        // ?????????
        mAdapter.register(MovieDetailsHotReviewsBean.LongReviewList.class, new MovieDetailsLongReviewBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(MovieDetailsBasic.StageImg.class, new MovieDetailsPicturesBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        // ?????????
        mAdapter.register(MovieDetailsHotReviewsBean.ShortReviewList.class, new MovieDetailsShortReviewBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mAdapter.register(CommonAdListBean.class, new MovieDetailsAdBinder(mOnJumpPageCallback, mBaseStatisticHelper));
        mTrailersBinder = new MovieDetailsTrailersBinder(mOnJumpPageCallback, mBaseStatisticHelper);
        mAdapter.register(CategoryVideosBean.class, mTrailersBinder);
        mAdapter.setItems(mItems);
        mRecyclerView.setAdapter(mAdapter);
    }

    //????????????????????????????????????tab
    private void onSelectedTab() {
        int position = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        if (position >= 0) {
            int lastPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (position != 0 && lastPosition == mAdapter.getItemCount() - 1) {
                MovieDetailsTab tab = mTabs.get(mTabs.size() - 1);
                if (selectedTab(tab)) {
                    return;
                }
            }

            for (MovieDetailsTab tab : mTabs) {
                if (position >= tab.startPos && position <= tab.endPos) {
                    if (selectedTab(tab)) {
                        return;
                    }
                }
            }
        } else {
            //?????????tab??????
            mTabLayoutHelper.initDefaultFocus();
        }
    }

    private boolean selectedTab(MovieDetailsTab tab) {
        if (null != tab) {
            int tabIndex = mTabs.indexOf(tab);
            if (tabIndex != mEmptyViewPager.getCurrentItem()) {
                mEmptyViewPager.setCurrentItem(mTabs.indexOf(tab), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
        mMovieDetailsHeadHelper.release();
    }


    @OnClick({R.id.activity_movie_details_title_back_iv,
            R.id.activity_movie_details_title_star_iv,
            R.id.activity_movie_details_title_share_iv,
            R.id.activity_movie_details_head_online_play_fl,
            R.id.activity_movie_details_bottom_btn_iv
    })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_movie_details_title_back_iv:
                finish();
                break;
            case R.id.activity_movie_details_title_star_iv:
                // Title??????
                if (null != mMovieDetailsBasic) {
                    if (mMovieDetailsBasic.isFavorite == 0) {
                        sendHolderEvent(EVENT_MOVIE_COLLECT, null);
                    } else {
                        sendHolderEvent(EVENT_MOVIE_CANCEL_COLLECT, null);
                    }
                    LiveEventBus.get(COLLECTION_OR_CANCEL).post(new CollectionState(CommConstant.COLLECTION_TYPE_MOVIE));
                }
                break;

            case R.id.activity_movie_details_title_share_iv:
                // Title??????
                if (null != mMovieDetailsBasic) {
                    sendHolderEvent(EVENT_MOVIE_SHARE, null);
                }
                break;

            case R.id.activity_movie_details_head_online_play_fl:
                // ?????????????????????
                onOnlinePlay(true);
                break;

            case R.id.activity_movie_details_bottom_btn_iv:
                onBottomBtnClick();
                break;
        }
    }

    private void onBottomBtnClick() {
        if (null != mMovieDetailsBean) {
            if (mMovieDetailsBean.playState == 2) {
                // ????????????
                onOnlinePlay(false);
            } else if (mMovieDetailsBean.playState == 1
                    || mMovieDetailsBean.playState == 3) {
                // ????????????
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                        .put("buyTicketState", mMovieDetailsBean.playState == 1 ? "Buy" : "preBuy");
                String refer = mBaseStatisticHelper.assemble1(
                        "bottomNav", null,
                        "ticket", null,
                        null, null, mapBuild.build()).submit();

                // ???????????????
                if (null != mMovieDetailsBasic) {
                    JumpUtil.startMovieShowtimeActivity(getActivity(), refer,
                            String.valueOf(mMovieDetailsBasic.movieId), mMovieDetailsBasic.name,
                            mMovieDetailsBasic.isTicket, null, 0);
                }
            }
        }
    }

    private void onOnlinePlay(boolean isHead) {
        if (null != getActivity()) {
            if (isHead) {
                // ????????????
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                mBaseStatisticHelper.assemble1(
                        "head", null,
                        "play", null,
                        null, null, mapBuild.build()).submit();
            } else {
                // ????????????
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                mBaseStatisticHelper.assemble1(
                        "bottomNav", null,
                        "play", null,
                        null, null, mapBuild.build()).submit();
            }
            if (null != mMovieDetailsBasic) {
                FilmSourceChoiceDialog.show(String.valueOf(mMovieDetailsBasic.movieId), getActivity().getSupportFragmentManager());
            }
        }
    }

    public void setTitleCollect(boolean isAdd) {
        if (null != mTitleCollectIv) {
            mTitleCollectIv.setSelected(isAdd);
        }
        if (null != mMovieDetailsBasic) {
            mMovieDetailsBasic.isFavorite = isAdd ? 1 : 0;
        }
    }

    private void addTab(MovieDetailsTab tab) {
        if (tab.startPos >=0 && tab.endPos >= tab.startPos) {
            mTabs.add(tab);
        }
        if (TextUtils.equals(tab.subTitle, "0")) {
            tab.subTitle = "";
        }
        mTabLayoutHelper.addSubTitleStr(mTabs.size()-1, tab.subTitle);
    }

    private void showData() {
        if (null == mMovieDetailsBasic) {
            return;
        }

        if (mTitleCollectIv != null) {
            mTitleCollectIv.setSelected(mMovieDetailsBasic.isFavorite == 1);
        }
        mMovieDetailsHeadHelper.setBasicData(mMovieDetailsBasic);
        if (mMovieDetailsBean != null) {
            mBottomBtnIv.setImageLevel(mMovieDetailsBean.playState);
            mMovieDetailsHeadHelper.setOnlinePlayData(mMovieDetailsBean.playlist);
        }
        mTrailersBinder.setMovieId(mMovieDetailsBasic.movieId);
        if (null != mMovieDetailsExtendBean) {
            mMovieDetailsExtendBean.movieId = mMovieDetailsBasic.movieId;
            mMovieDetailsExtendBean.movieName = mMovieDetailsBasic.name;
        }

        mItems.clear();
        mTabs.clear();

        MovieDetailsTab tab1 = new MovieDetailsTab();
        tab1.startPos = 0;
        // ???UI??????????????????Item??????
        // ??????
        if (mMovieDetailsBasic.hasIntro()) {
            mItems.add(mMovieDetailsBasic.getIntroBean());
        }
        // ????????????
        if (mMovieDetailsBasic.hasActorsBean()) {
            mItems.add(mMovieDetailsBasic.getActorsBean());
        }
        // ????????????
        if (null != mMovieDetailsExtendBean && mMovieDetailsExtendBean.hasTimeTalks()) {
            mItems.add(mMovieDetailsExtendBean.getTimeTalks());
        }
        // ??????????????????
        if (null != mCategoryVideosBean && !CollectionUtils.isEmpty(mCategoryVideosBean.getVideoList())) {
            mItems.add(mCategoryVideosBean);
        }
        // ??????
        if (mMovieDetailsBasic.hasStageImg()) {
            mItems.add(mMovieDetailsBasic.getStageImg());
        }
        tab1.endPos = mItems.size() - 1;
        tab1.titleResid = R.string.movie_details_tab_intro;
        tab1.secRegion = "introduce"; //?????????????????????
        addTab(tab1);

        if (null != mMovieDetailsHotReviewsBean) {
            mMovieDetailsHotReviewsBean.movieId = mMovieDetailsBasic.movieId;
            if(mMovieDetailsBasic != null) {
                mMovieDetailsHotReviewsBean.movieName = TextUtils.isEmpty(mMovieDetailsBasic.name) ?
                        mMovieDetailsBasic.nameEn : mMovieDetailsBasic.name;
            }

            MovieDetailsTab tab2 = new MovieDetailsTab();
            tab2.startPos = mItems.size();
            // ??????
            if (mMovieDetailsHotReviewsBean.hasShortReview()) {
                mItems.add(mMovieDetailsHotReviewsBean.getShortReview());
            }
            // ??????
            if (mMovieDetailsHotReviewsBean.hasLongReview()) {
                mItems.add(mMovieDetailsHotReviewsBean.getLongReview());
            }
            tab2.endPos = mItems.size() -1;
            tab2.titleResid = R.string.movie_details_tab_review;
            tab2.secRegion = "reviews"; //?????????????????????
            tab2.subTitle = mMovieDetailsHotReviewsBean.commentTotalCountShow;
            addTab(tab2);
        }

        MovieDetailsTab tab4 = new MovieDetailsTab();
        tab4.startPos = mItems.size();
        if (null != mMovieDetailsExtendBean) {
            // ????????????
            if (mMovieDetailsExtendBean.hasTimeOriginal()) {
                mItems.add(mMovieDetailsExtendBean.getTimeOriginal());
            }
            // ?????????????????????XX??????
            if (mMovieDetailsExtendBean.hasEvents()) {
                mItems.add(mMovieDetailsExtendBean.getEvents());
            }
            // ????????????
            if (mMovieDetailsExtendBean.hasClassicLines()) {
                mItems.add(mMovieDetailsExtendBean.getClassicLines());
            }
        }
        // ??????
        if (mMovieDetailsBean.hasBoxOffice()) {
            mItems.add(mMovieDetailsBean.boxOffice);
        }
        // ??????
        if (mMovieDetailsBasic.hasAward()) {
            mItems.add(mMovieDetailsBasic.getAward());
        }
        // ????????????
        if (null != mMovieDetailsExtendBean && mMovieDetailsExtendBean.hasDataBankEntry()) {
            mItems.add(mMovieDetailsExtendBean.getDataBankEntry());
        }
        // ???????????????????????????????????????????????????
//        if (null != mMovieDetailsExtendBean) {
//            // ????????????
//            if (mMovieDetailsExtendBean.hasAssociatedMovies()) {
//                mItems.add(mMovieDetailsExtendBean.getAssociatedMovies());
//            }
//        }
        // ??????
        if (null != mCommonAdListBean && mCommonAdListBean.hasDatas()
                && mCommonAdListBean.getAdList().get(0).hasAdData()) {
            mItems.add(mCommonAdListBean);
        }
        tab4.endPos = mItems.size() - 1;
        tab4.titleResid = R.string.movie_details_tab_more;
        tab4.secRegion = "more"; //?????????????????????
        addTab(tab4);

        mAdapter.notifyDataSetChanged();

        // ?????????viewpager??????????????????TabLayout, ??????????????????????????????
        mEmptyViewPager.setAdapter(mTabAdapter);
        mTabLayout.setViewPager(mEmptyViewPager);
        onSelectedTab();

        // head??????????????????????????????????????????
        View mAppBarChildAt = mAppBarLayout.getChildAt(0);
        if (null != mAppBarChildAt) {
            AppBarLayout.LayoutParams mAppBarParams = (AppBarLayout.LayoutParams) mAppBarChildAt.getLayoutParams();
            if (mTabs.isEmpty()) {
                //AppBarLayout???????????????
                mAppBarParams.setScrollFlags(0);
            } else {
                mAppBarParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
            }
        }
    }

    public void setMovieLatestReviewBean(MovieLatestReviewBean latestReviewBean) {
        if (null != latestReviewBean) {
            // ?????????????????????????????????
            mMovieDetailsBean.basic.attitude = latestReviewBean.getAttitude();
            mMovieDetailsHeadHelper.setLatestReview(latestReviewBean);
        }
    }

    public void setMovieDetailsBean(MovieDetailsBean movieDetailsBean) {
        if (movieDetailsBean == null)
            return;
        mMovieDetailsBean = movieDetailsBean;
        mMovieDetailsBasic = movieDetailsBean.basic;
        showData();
    }

    public void setCategoryVideosBean(CategoryVideosBean categoryVideosBean) {
        mCategoryVideosBean = categoryVideosBean;
        showData();
    }

    public void setMovieDetailsHotReviewsBean(MovieDetailsHotReviewsBean movieDetailsHotReviewsBean) {
        mMovieDetailsHotReviewsBean = movieDetailsHotReviewsBean;
        showData();
    }

    public void setMovieDetailsExtendBean(MovieDetailsExtendBean movieDetailsExtendBean) {
        mMovieDetailsExtendBean = movieDetailsExtendBean;
        showData();
    }

    public void setAdBean(CommonAdListBean adListBean) {
        mCommonAdListBean = adListBean;
        showData();
    }

    /**
     * ?????????????????????????????????????????????activity????????????????????????????????????onResume????????????????????????
     */
    public interface OnJumpPageCallback {
        void onJumpPageCallback(int code);
    }
}
