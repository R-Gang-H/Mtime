package com.mtime.bussiness.ticket.movie.details.api;

import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.common.api.CommonApi;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsExtendBean;
import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotReviewsBean;
import com.mtime.bussiness.common.bean.MovieWantSeenResultBean;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-21
 */
public class MovieDetailsApi extends CommonApi {
    private final MovieApi mMovieApi = new MovieApi();

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
        mMovieApi.cancel();
    }

    /**
     * 影片详情
     *
     * @param movieId
     * @param listener
     */
    public void details(long movieId, NetworkManager.NetworkListener<MovieDetailsBean> listener) {
        Map<String, String> param = new HashMap<>(2);
        param.put("movieId", String.valueOf(movieId));
        param.put("locationId", GlobalDimensionExt.INSTANCE.getCurrentCityId());
        get(this, ConstantUrl.MOVIE_DETAIL_V2, param, listener);
    }

    /**
     * 影片扩展数据
     *
     * @param movieId
     * @param listener
     */
    public void extendDetail(long movieId, NetworkManager.NetworkListener<MovieDetailsExtendBean> listener) {
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", String.valueOf(movieId));
        get(this, ConstantUrl.MOVIE_EXTEND_DETAIL_V2, param, listener);
    }

    /**
     * 影片-设置看过
     *
     * @param movieId
     * @param listener
     */
    public void setHasSeen(long movieId, NetworkManager.NetworkListener<MovieWantSeenResultBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", String.valueOf(movieId));
        post(this, ConstantUrl.POST_MOVIE_SET_SEEN, parmas, listener);
    }

    /**
     * 影片的热门影评
     *
     * @param movieId       电影Id
     * @param minPageSize   短影评每页记录数
     * @param plusPageSize  长影评每页记录数
     * @param listener
     */
    public void hotReview(long movieId, int minPageSize, int plusPageSize, NetworkManager.NetworkListener<MovieDetailsHotReviewsBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", String.valueOf(movieId));
        parmas.put("minPageSize", String.valueOf(minPageSize));
        parmas.put("plusPageSize", String.valueOf(plusPageSize));
        get(this, ConstantUrl.GET_MOVIE_HOT_REVIEW, parmas, listener);
    }

    /**
     * 获取影片分类视频
     *
     * @param movieId
     * @param pageIndex
     * @param type
     * @param networkListener
     */
    public void getCategroyVideos(int movieId, int pageIndex, int type, NetworkManager.NetworkListener<CategoryVideosBean> networkListener) {
        mMovieApi.getCategroyVideos("CategoryVideosBean", movieId, pageIndex, type, networkListener);
    }

}
