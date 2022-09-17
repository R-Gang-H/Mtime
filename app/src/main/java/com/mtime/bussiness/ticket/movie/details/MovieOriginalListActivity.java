package com.mtime.bussiness.ticket.movie.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.widget.layout.OnVisibilityCallback;
import com.mtime.bussiness.home.bean.HomeFeedItemBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedItemBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedListBean;
import com.mtime.bussiness.home.original.widget.HomeOriginalFeedView;
import com.mtime.bussiness.home.recommend.bean.HomeRecommendFeedItemBean;
import com.mtime.bussiness.home.recommend.bean.HomeRecommendFeedLogxBean;
import com.mtime.bussiness.mine.history.ReadHistoryUtil;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieOriginalAdapter;
import com.mtime.bussiness.ticket.movie.details.api.MovieSubPageApi;
import com.mtime.bussiness.ticket.movie.details.holder.MovieOriginalListHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

/**
 * @author vivian.wei
 * @date 2019/6/10
 * @desc 影片时光原创列表页
 *
 * 备注：之前在影片详情中用到，现在影片详情里面没有"全部"了，所以暂时用不到这个列表，
 *      但是产品说暂时去掉"全部"，也不能确定后期会不会加回来，所以暂时先不删除相关代码
 */
public class MovieOriginalListActivity  extends BaseFrameUIActivity<HomeOriginalFeedListBean, MovieOriginalListHolder>
        implements OnRefreshListener, OnLoadMoreListener,
        HomeOriginalFeedView.OnFeedItemClickListener,
        MovieOriginalAdapter.OnMovieOriginalAdapterListener {

    // 页面跳转参数
    private static final String KEY_MOVIE_ID = "movie_id";      // 电影Id
    private static final String KEY_NEWS_TYPE = "news_type";    // 新闻类型
    // 新闻类型值
    public static final String NEWS_TYPE_ORIGINAL = "original";          // 时光原创
    public static final String NEWS_TYPE_CONVERSATION = "conversation";  // 时光对话

    private static final int PAGE_SIZE = 10;

    private String mMovieId;
    private String mNewsType = NEWS_TYPE_ORIGINAL;
    private MovieSubPageApi mApi;
    private int mPageIndex = 1;
    private boolean mFirstLoad = true;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieOriginalListHolder(this, this, this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();

        onLoadState();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mMovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
        mNewsType = getIntent().getStringExtra(KEY_NEWS_TYPE);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel(mNewsType.equals(NEWS_TYPE_ORIGINAL) ? "mtimeOriginalList" : "mtimeDiaList");
        mBaseStatisticHelper.putBaseParam("movieID", mMovieId);

        // 标题
        getUserContentHolder().setTitle(mNewsType);
        if(mApi == null) {
            mApi = new MovieSubPageApi();
        }
        // 刷新|加载更多
        getUserContentHolder().setRefreshLoadMoreListener(this, this);
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mApi) {
            mApi.cancel();
        }
    }

    private void requestData() {
        mApi.getMovieOriginalNewsList(mMovieId, mPageIndex, PAGE_SIZE, mNewsType, new NetworkManager.NetworkListener<HomeOriginalFeedListBean>() {
            @Override
            public void onSuccess(HomeOriginalFeedListBean result, String showMsg) {
                getUserContentHolder().finishRefresh(true);
                if (null != result) {
                    mFirstLoad = false;
                    getUserContentHolder().setPageIndex(mPageIndex);
                    setData(result);

                    if (CollectionUtils.isNotEmpty(result.getList()) && result.isHasMore()) {
                        getUserContentHolder().setEnableLoadMore(true);
                        mPageIndex++;
                    } else {
                        getUserContentHolder().setEnableLoadMore(false);
                    }
                }
                setPageState(BaseState.SUCCESS);
            }

            @Override
            public void onFailure(NetworkException<HomeOriginalFeedListBean> exception, String showMsg) {
                //第一次加载
                if (mFirstLoad) {
                    setPageState(BaseState.ERROR);
                } else {
                    setPageState(BaseState.SUCCESS);
                }
                getUserContentHolder().finishRefresh(false);
            }
        });
    }

    // 下拉刷新
    @Override
    public void onRefresh(RefreshLayout var1) {
        mPageIndex = 1;
        requestData();
    }

    // 上拉加载
    @Override
    public void onLoadMore(RefreshLayout refreshLayout) {
        requestData();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case App.MOVIE_SUB_PAGE_EVENT_CODE_BACK:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    // 兼容物理返回键
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // 跳转到文章详情页面
    @Override
    public void onFeedItemClick2Articles(HomeFeedItemBean bean, int position) {
        HomeOriginalFeedItemBean item = (HomeOriginalFeedItemBean)bean;
        // 埋点上报
        String refer = logx(item);
        // 保存阅读历史
        ReadHistoryUtil.saveReadHistory2Local(item.getContentType(), item.getRelatedId(), item.getTitle(),
                (null == item.getPublicInfo() ? "" : item.getPublicInfo().getName()), item.getImgUrl(0),
                item.isShowPlayIcon(0), item.getVideoId(), item.getVideoSourceType());

        String articleType;
        articleType = item.getContentType() == 4 ?  "2"  : "1";
        JumpUtil.startArticleActivity(this, refer, String.valueOf(item.relatedId), articleType, "", "");
    }

    // 跳转到视频内容播放页
    @Override
    public void onFeedItemClick2Video(HomeFeedItemBean bean, int position) {
        HomeOriginalFeedItemBean item = (HomeOriginalFeedItemBean)bean;
        // 埋点上报
        String refer = logx(item);
        // 保存阅读历史
        ReadHistoryUtil.saveReadHistory2Local(item.getContentType(), item.getRelatedId(), item.getTitle(),
                (null == item.getPublicInfo() ? "" : item.getPublicInfo().getName()), item.getImgUrl(0),
                true, item.getVideoId(), item.getVideoSourceType());

        JumpUtil.startMediaVideoDetailActivity(this,
                String.valueOf(item.relatedId), item.videoId, item.videoSourceType,
                "", "", refer);
    }

    @Override
    public void onFeedItemClick2Ad(HomeFeedItemBean item, int position) {

    }

    @Override
    public void onFeedItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag) {

    }

    @Override
    public void onDeleteDislikeRcmd(HomeRecommendFeedItemBean item, HomeRecommendFeedLogxBean reasonBean, int position) {

    }

    @Override
    public void onShow(int position, HomeOriginalFeedItemBean bean) {
        // 埋点: 滑动显示上报
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mapBuild.put("articleID", String.valueOf(bean.getRelatedId()));
        String refer = mBaseStatisticHelper.assemble1(
                "content", null,
                "show", String.valueOf(position + 1),
                null, null, mapBuild.build()).submit();
    }

    // 埋点：点击上报
    private String logx(HomeOriginalFeedItemBean item) {
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mapBuild.put("articleID", String.valueOf(item.getRelatedId()));
        String refer = mBaseStatisticHelper.assemble1(
                "content", null,
                "click", null,
                null, null, mapBuild.build()).submit();
        return refer;
    }

    // 页面跳转
    public static void launch(Context context, String refer, String movieId, String newsType) {
        Intent launcher = new Intent(context, MovieOriginalListActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        launcher.putExtra(KEY_NEWS_TYPE, newsType);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

}
