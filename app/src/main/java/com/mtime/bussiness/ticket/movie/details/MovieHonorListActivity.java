package com.mtime.bussiness.ticket.movie.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.BaseStateContainer;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBasic;
import com.mtime.bussiness.ticket.movie.details.holder.MovieHonorListHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;

/**
 * @author vivian.wei
 * @date 2019/6/17
 * @desc 电影获奖页
 */
public class MovieHonorListActivity extends BaseFrameUIActivity<MovieDetailsBasic.Award, MovieHonorListHolder> {

    // 数据在影片详情页赋值
    public static MovieDetailsBasic.Award sAward;

    // 页面跳转参数
    private static final String KEY_MOVIE_ID = "movie_id"; // 电影Id

    private String mMovieId;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieHonorListHolder(this);
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return super.onBindLoadingHolder();
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();

        mMovieId = getIntent().getStringExtra(KEY_MOVIE_ID);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("movieHonor");
        // 标题
        getUserContentHolder().setTitle(sAward.movieName);
    }

    @Override
    public void onResume() {
        super.onResume();

        // 显示数据
        setData(sAward);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);

        switch (eventCode) {
            case App.MOVIE_SUB_PAGE_EVENT_CODE_BACK:  // 返回
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

    public static void launch(Context context, String refer, String movieId) {
        Intent launcher = new Intent(context, MovieHonorListActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
