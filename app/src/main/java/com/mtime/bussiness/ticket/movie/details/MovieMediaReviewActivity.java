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
import com.mtime.bussiness.ticket.movie.bean.MediaReviewBean;
import com.mtime.bussiness.ticket.movie.details.api.MovieSubPageApi;
import com.mtime.bussiness.ticket.movie.details.holder.MovieMediaReviewHolder;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;

/**
 * @author vivian.wei
 * @date 2019/5/24
 * @desc 影片媒体评论页
 */
public class MovieMediaReviewActivity extends BaseFrameUIActivity<MediaReviewBean, MovieMediaReviewHolder> {

    // 页面跳转参数
    private static final String KEY_MOVIE_ID = "movie_id";   // 电影Id

    private String mMovieId;
    private MovieSubPageApi mApi;

    @Override
    protected BaseStateContainer getStateContainer() {
        return new NoTitleBarContainer(this, this, this);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new MovieMediaReviewHolder(this);
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
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 埋点
        mBaseStatisticHelper.setPageLabel("mediaEvaluation");

        if(mApi == null) {
            mApi = new MovieSubPageApi();
        }
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
        mApi.getMovieMediaReviews(mMovieId, new NetworkManager.NetworkListener<MediaReviewBean>() {
            @Override
            public void onSuccess(MediaReviewBean result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                if (null != result) {
                    setData(result);
                }
            }

            @Override
            public void onFailure(NetworkException<MediaReviewBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
            }
        });
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

    public static void launch(Context context, String refer, String movieId) {
        Intent launcher = new Intent(context, MovieMediaReviewActivity.class);
        launcher.putExtra(KEY_MOVIE_ID, movieId);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

}
