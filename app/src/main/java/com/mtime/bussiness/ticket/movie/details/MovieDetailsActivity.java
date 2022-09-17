package com.mtime.bussiness.ticket.movie.details;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.kotlin.android.core.statusbar.StatusBarUtils;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.ktx.ext.statusbar.StatusBarExtKt;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.app.router.liveevent.event.LoginState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.common.bean.MovieLatestReviewBean;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.bean.CollectResultBean;
import com.mtime.bussiness.ticket.movie.details.api.MovieDetailsApi;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsExtendBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotReviewsBean;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.share.ShareExtJava;
import com.mtime.statistic.large.MapBuild;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.statusbar.StatusBarUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import static com.kotlin.android.router.liveevent.EventKeyExtKt.LOGIN_STATE;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-15
 * <p>
 * 影片详情(影片资料)页
 */
@Route(path = RouterActivityPath.Ticket.PAGER_MOVIE_DETAIL)
public class MovieDetailsActivity extends BaseFrameUIActivity<MovieDetailsBean, MovieDetailsHolder> implements MovieDetailsHolder.OnJumpPageCallback {

    public static final String KEY_MOVIE_ID = "movie_id";
    // 收藏动作
    private static final int COLLECT_ACTION_ADD = 1;
    private static final int COLLECT_ACTION_CANCEL = 2;
    // 短影评页数
    private static final int SHORT_COMMENT_PAGE_SIZE = 3;
    // 长影评页数
    private static final int REVIEW_PAGE_SIZE = 3;

    public static void launch(Context context, String refer, String id, int requestCode) {
        Intent launcher = new Intent(context, MovieDetailsActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, Long.valueOf(id));
        dealRefer(context, refer, launcher);
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(launcher, requestCode);
        } else {
            context.startActivity(launcher);
        }
    }

    private final MovieDetailsApi mApi = new MovieDetailsApi();
    private long mMovieId;
    private int mCurJumpCode = -1;
    private TicketApi mTicketApi;

    @Override
    public MovieDetailsHolder onBindContentHolder() {
        return new MovieDetailsHolder(this, mBaseStatisticHelper, this);
    }

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        //        设置全屏
        StatusBarExtKt.handleArticleStatusBar(this,false);
//        修改字体颜色
        if (StatusBarUtils.INSTANCE.canControlStatusBarTextColor()) {
            StatusBarUtils.INSTANCE.translucentStatusBar(this, true, true);
        }
        mMovieId = getIntent().getLongExtra(KEY_MOVIE_ID, 0);

        mTicketApi = new TicketApi();

        //埋点
        setPageLabel(StatisticTicket.PN_MOVIE_DETAIL);
        putBaseStatisticParam(StatisticConstant.MOVIE_ID, String.valueOf(mMovieId));

        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        loginEventObserve();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 从部分页面返回后，需要刷新数据
        switch (mCurJumpCode) {
            case MovieDetailsHolder.PAGE_REVIEW:
                loadLatestReview();
                loadHotReview();
                break;
        }
        mCurJumpCode = -1;
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        initDatas();
    }

    @Override
    protected void onErrorRetry() {
        onLoadState();
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(LoginEvent event) {
//        onLoadState();
//    }

    private void loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, LoginState.class).observe(this, loginState -> {
            if (loginState.isLogin()) {//登录成功
                onLoadState();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApi.cancel();
        if(mTicketApi != null) {
            mTicketApi = null;
        }
    }

    // 用户对本影片的最新一条影评
    private void loadLatestReview() {
        mApi.movieLatestReview(String.valueOf(mMovieId), new NetworkManager.NetworkListener<MovieLatestReviewBean>() {
            @Override
            public void onSuccess(MovieLatestReviewBean result, String showMsg) {
                // 显示最新一条影评
                getUserContentHolder().setMovieLatestReviewBean(result);
            }

            @Override
            public void onFailure(NetworkException<MovieLatestReviewBean> exception, String showMsg) {

            }
        });
    }

    /**
     * 保存浏览历史记录
     * 用于搜索页面显示
     *
     * @param movieId
     * @param movieName
     */
    private void saveSearchBrowseHistories(long movieId, String movieName) {
//        SearchUtil.saveSearchBrowseHistories(String.valueOf(movieId), movieName, SearchUtil.SEARCH_TYPE_MOVIE);
    }

    private void initDatas() {
        // 详情数据
        mApi.details(mMovieId, new NetworkManager.NetworkListener<MovieDetailsBean>() {
            @Override
            public void onSuccess(MovieDetailsBean result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                getUserContentHolder().setMovieDetailsBean(result);
                if (null != result && result.hasData()) {
                    // 保存浏览历史记录
                    saveSearchBrowseHistories(result.basic.movieId, result.basic.name);

                    if (result.basic.attitude == 0) {
                        // 初始状态为看过，所以需要调用 最新一条影评接口
                        loadLatestReview();
                    }

                    // 热门点击
                    int sType = result.basic.movieType == MovieDetailsBasic.MOVIE_TYPE_MOVIE ?
                            TicketApi.SEARCH_POPULAR_CLICK_SUB_TYPE_MOVIE : TicketApi.SEARCH_POPULAR_CLICK_SUB_TYPE_TV;
                    mTicketApi.postSearchPoplarClick(TicketApi.SEARCH_POPULAR_CLICK_TYPE_MOVIE,
                            sType, String.valueOf(mMovieId));
                }
            }

            @Override
            public void onFailure(NetworkException<MovieDetailsBean> exception, String showMsg) {
                MToastUtils.showShortToast(R.string.str_load_error);
                setPageState(BaseState.ERROR);
            }
        });

        // 预告片与花絮
        mApi.getCategroyVideos((int) mMovieId, 1, CategoryVideosBean.Category.TYPE_RECOMMEND, new NetworkManager.NetworkListener<CategoryVideosBean>() {
            @Override
            public void onSuccess(CategoryVideosBean result, String showMsg) {
                getUserContentHolder().setCategoryVideosBean(result);
            }

            @Override
            public void onFailure(NetworkException<CategoryVideosBean> exception, String showMsg) {

            }
        });

        // 扩展详情数据
        mApi.extendDetail(mMovieId, new NetworkManager.NetworkListener<MovieDetailsExtendBean>() {
            @Override
            public void onSuccess(MovieDetailsExtendBean result, String showMsg) {
                getUserContentHolder().setMovieDetailsExtendBean(result);
            }

            @Override
            public void onFailure(NetworkException<MovieDetailsExtendBean> exception, String showMsg) {
            }
        });

        // 热门话题
//        mApi.hotTopicList(mMovieId, new NetworkManager.NetworkListener<MovieDetailsHotTopicsBean>() {
//            @Override
//            public void onSuccess(MovieDetailsHotTopicsBean result, String showMsg) {
//                getUserContentHolder().setMovieDetailsHotTopicsBean(result);
//            }
//
//            @Override
//            public void onFailure(NetworkException<MovieDetailsHotTopicsBean> exception, String showMsg) {
//            }
//        });

        // 广告
        mApi.getAdInfo(GlobalDimensionExt.INSTANCE.getCurrentCityId(), CommonAdListBean.AD_POSITION_CODE_FILM_DETAIL,
                new NetworkManager.NetworkListener<CommonAdListBean>() {
            @Override
            public void onSuccess(CommonAdListBean result, String showMsg) {
                getUserContentHolder().setAdBean(result);
            }

            @Override
            public void onFailure(NetworkException<CommonAdListBean> exception, String showMsg) {
            }
        });

        // 热门影评
        loadHotReview();
    }

    // 加载热门影评
    private void loadHotReview() {
        mApi.hotReview(mMovieId, SHORT_COMMENT_PAGE_SIZE, REVIEW_PAGE_SIZE,
                new NetworkManager.NetworkListener<MovieDetailsHotReviewsBean>() {
            @Override
            public void onSuccess(MovieDetailsHotReviewsBean result, String showMsg) {
                getUserContentHolder().setMovieDetailsHotReviewsBean(result);
            }

            @Override
            public void onFailure(NetworkException<MovieDetailsHotReviewsBean> exception, String showMsg) {
            }
        });
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode) {
            case MovieDetailsHolder.EVENT_MOVIE_CANCEL_COLLECT:
                collect(false);
                break;

            case MovieDetailsHolder.EVENT_MOVIE_COLLECT:
                collect(true);
                break;

            case MovieDetailsHolder.EVENT_MOVIE_SHARE:
                onShare();
                break;
        }
    }

    // 分享
    private void onShare() {
        // 埋点上报
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mBaseStatisticHelper.assemble1(StatisticTicket.TICKET_TOP_NAVIGATION, null,
                "shareBtn", null, null, null, mapBuild.build()).submit();

        ShareExtJava.showShareDialog(
                this,
                String.valueOf(CommConstant.SHARE_TYPE_FILM),
                String.valueOf(mMovieId),
                null
        );
    }

    /**
     * 收藏/取消收藏
     */
    private void collect(final boolean isAdd) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            JumpUtil.startLoginActivity(this, mBaseStatisticHelper.assemble().toString());
            return;
        }

        // 埋点上报
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
                .put(StatisticConstant.COLLECT_STATE,
                     isAdd ? StatisticEnum.EnumCollectState.COLLECT.getValue() : StatisticEnum.EnumCollectState.CANCEL.getValue());
        mBaseStatisticHelper.assemble1(StatisticTicket.TICKET_TOP_NAVIGATION, null,
                "collect", null, null, null,
                mapBuild.build()).submit();

        UIUtil.showLoadingDialog(this);
        mTicketApi.postCollect(isAdd ? COLLECT_ACTION_ADD : COLLECT_ACTION_CANCEL,
                CommConstant.COLLECTION_OBJ_TYPE_FILM, String.valueOf(mMovieId),
                new NetworkManager.NetworkListener<CollectResultBean>() {
                    @Override
                    public void onSuccess(CollectResultBean result, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        if (result.getBizCode() == CollectResultBean.SUCCESS) {
                            // 成功
                            getUserContentHolder().setTitleCollect(isAdd);
                            MToastUtils.showShortToast(isAdd ? R.string.common_collect_success : R.string.common_cancel_collect_success);
                            //收藏动作联动
                            setResult(App.STATUS_MOVIE_REFRESH);
                        } else {
                            // 失败
                            MToastUtils.showShortToast(isAdd ? R.string.common_collect_failed : R.string.common_cancel_collect_failed);
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<CollectResultBean> exception, String showMsg) {
                        UIUtil.dismissLoadingDialog();
                        MToastUtils.showShortToast(isAdd ? R.string.common_collect_failed : R.string.common_cancel_collect_failed);
                    }
                });
    }

    // 取消收藏
//    private void onCancelCollect() {
//        if (!UserManager.Companion.getInstance().isLogin()) {
//            JumpUtil.startLoginActivity(this, mBaseStatisticHelper.assemble().toString());
//            return;
//        }
//
//        // 埋点上报
//        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
//                .put(StatisticConstant.COLLECT_STATE, StatisticEnum.EnumCollectState.CANCEL.getValue());
//        mBaseStatisticHelper.assemble1(StatisticTicket.TICKET_TOP_NAVIGATION, null,
//                "collect", null, null, null, mapBuild.build()).submit();
//
//        mApi.favoriteCancel(String.valueOf(mMovieId), 1, new NetworkManager.NetworkListener<CommResultBean>() {
//            @Override
//            public void onSuccess(CommResultBean result, String showMsg) {
//                if (null != result && result.isSuccess() && null != getUserContentHolder()) {
//                    getUserContentHolder().setTitleCollect(false);
//                    MToastUtils.showShortToast(R.string.common_cancel_collect_success);
//                    //收藏动作联动
//                    setResult(App.STATUS_MOVIE_REFRESH);
//                } else {
//                    MToastUtils.showShortToast(R.string.common_cancel_collect_failed);
//                }
//            }
//
//            @Override
//            public void onFailure(NetworkException<CommResultBean> exception, String showMsg) {
//                MToastUtils.showShortToast(R.string.common_cancel_collect_failed);
//            }
//        });
//    }

    // 收藏
//    private void onCollect() {
//        if (!UserManager.Companion.getInstance().isLogin()) {
//            JumpUtil.startLoginActivity(this, mBaseStatisticHelper.assemble().toString());
//            return;
//        }
//
//        // 埋点上报
//        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam())
//                .put(StatisticConstant.COLLECT_STATE, StatisticEnum.EnumCollectState.COLLECT.getValue());
//        mBaseStatisticHelper.assemble1(StatisticTicket.TICKET_TOP_NAVIGATION, null,
//                "collect", null, null, null, mapBuild.build()).submit();
//
//        mApi.favoriteAdd(String.valueOf(mMovieId), 1, new NetworkManager.NetworkListener<CommResultBean>() {
//            @Override
//            public void onSuccess(CommResultBean result, String showMsg) {
//                if (null != result && result.isSuccess() && null != getUserContentHolder()) {
//                    getUserContentHolder().setTitleCollect(true);
//                    MToastUtils.showShortToast(R.string.common_collect_success);
//                    //收藏动作联动
//                    setResult(App.STATUS_MOVIE_REFRESH);
//                } else {
//                    MToastUtils.showShortToast(R.string.common_collect_failed);
//                }
//            }
//
//            @Override
//            public void onFailure(NetworkException<CommResultBean> exception, String showMsg) {
//                MToastUtils.showShortToast(R.string.common_collect_failed);
//            }
//        });
//    }


    @Override
    public void onJumpPageCallback(int code) {
        mCurJumpCode = code;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getSupportFragmentManager().getFragments().size() > 0) {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment mFragment : fragments) {
                mFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
