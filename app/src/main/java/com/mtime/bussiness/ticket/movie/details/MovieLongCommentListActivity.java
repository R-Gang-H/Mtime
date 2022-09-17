package com.mtime.bussiness.ticket.movie.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieHotLongCommentAllBean;
import com.mtime.bussiness.ticket.movie.bean.MovieHotLongCommentBean;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieLongCommentAdapter;
import com.mtime.bussiness.ticket.movie.details.api.MovieSubPageApi;
import com.mtime.bussiness.ticket.movie.details.holder.MovieLongCommentListHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/5/24
 * @desc 影片长影评列表页
 */
public class MovieLongCommentListActivity extends BaseFrameUIActivity<MovieHotLongCommentAllBean, MovieLongCommentListHolder>
        implements OnRefreshListener, OnLoadMoreListener,
        OnItemClickListener,
        OnItemChildClickListener,
        MovieLongCommentAdapter.OnMovieLongCommentAdapterListener {

    // 页面跳转参数
    private static final String KEY_MOVIE_ID = "movie_id";      // 电影Id
    private static final String KEY_MOVIE_NAME = "movie_name";  // 电影名称
    // 用于计算是否有下一页
    private static final int PAGE_SIZE = 20;

    private String mMovieId;
    private String mMovieName;
    private MovieSubPageApi mApi;
    private int mPageIndex = 1; // 用户评论页码
    private boolean mFirstLoad = true;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieLongCommentListHolder(this, this);
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
        mMovieName = getIntent().getStringExtra(KEY_MOVIE_NAME);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("relatedArticleList");
        mBaseStatisticHelper.putBaseParam("movieID", mMovieId);

        // 标题
        getUserContentHolder().setTitle(mMovieName);
        if(mApi == null) {
            mApi = new MovieSubPageApi();
        }
        // 刷新|加载更多
        getUserContentHolder().setRefreshLoadMoreListener(this, this);
        getUserContentHolder().setAdapterListener(this, this);
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
        mApi.getMovieHotLongComments(mMovieId, mPageIndex, new NetworkManager.NetworkListener<MovieHotLongCommentAllBean>() {
            @Override
            public void onSuccess(MovieHotLongCommentAllBean result, String showMsg) {
                getUserContentHolder().finishRefresh(true);
                if (null != result) {
                    mFirstLoad = false;
                    getUserContentHolder().setPageIndex(mPageIndex);
                    setData(result);

                    if (CollectionUtils.isNotEmpty(result.getComments()) && hasMore(result.getTotalCount())) {
                        getUserContentHolder().setEnableLoadMore(true);
                        mPageIndex++;
                    } else {
                        getUserContentHolder().setEnableLoadMore(false);
                    }
                }
                setPageState(BaseState.SUCCESS);
            }

            @Override
            public void onFailure(NetworkException<MovieHotLongCommentAllBean> exception, String showMsg) {
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

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<MovieHotLongCommentBean> comments = adapter.getData();
        MovieHotLongCommentBean bean = comments.get(position);

        // 埋点
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mapBuild.put("articleID", String.valueOf(bean.getId()));
        String refer = mBaseStatisticHelper.assemble1(
                "article", null,
                "click", null,
                null, null, mapBuild.build()).submit();

        // 影评详情页
        JumpUtil.startFindFilmReviewDetailActivity(this, refer, position, String.valueOf(bean.getId()), "comment_list");
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(view.getId() == R.id.item_movie_long_comment_comment_cl) {
            // 无埋点
            List<MovieHotLongCommentBean> comments = adapter.getData();
            MovieHotLongCommentBean bean = comments.get(position);
            // 影评评论列表页
            JumpUtil.startNewsCommentListActivity(this, "", "", String.valueOf(bean.getId()), bean.getCommentCount());
        }
    }

    @Override
    public void onShow(int position, MovieHotLongCommentBean bean) {
        // 埋点: 滑动显示上报
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mapBuild.put("articleID", String.valueOf(bean.getId()));
        String refer = mBaseStatisticHelper.assemble1(
                "article", null,
                "show", String.valueOf(position + 1),
                null, null, mapBuild.build()).submit();
    }

    // 是否有下一页
    private boolean hasMore(int totalCount) {
        // 老接口，api不好加字段，建议客户端自己计算
        int maxPageIndex = totalCount / PAGE_SIZE;
        if(totalCount % PAGE_SIZE > 0) {
            maxPageIndex++;
        }
        return mPageIndex < maxPageIndex;
    }

    // 页面跳转
    public static void launch(Context context, String refer, String movieId, String movieName) {
        Intent launcher = new Intent(context, MovieLongCommentListActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        launcher.putExtra(KEY_MOVIE_NAME, movieName);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

}
