package com.mtime.bussiness.ticket.movie.details.api;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedListBean;
import com.mtime.bussiness.ticket.movie.bean.MediaReviewBean;
import com.mtime.bussiness.ticket.movie.bean.MovieHotLongCommentAllBean;
import com.mtime.bussiness.ticket.movie.bean.MovieSecretBean;
import com.mtime.bussiness.ticket.movie.bean.MovieStarsTotalBean;
import com.mtime.bussiness.ticket.movie.bean.RelatedMoviesBean;
import com.mtime.bussiness.ticket.movie.details.MovieOriginalListActivity;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vivian.wei
 * @date 2019/5/29
 * @desc 影片资料_二级页面Api
 */
public class MovieSubPageApi extends BaseApi {

    @Override
    protected String host() {
        return null;
    }

    /**
     * 演职员列表
     */
    public void getFullCredits(String movieId, NetworkManager.NetworkListener<MovieStarsTotalBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", movieId);
        get(this, ConstantUrl.GET_FULL_CREDITS, parmas, 180000, listener);
    }

    /**
     * 关联电影
     */
    public void getRelatedMovies(String movieId, NetworkManager.NetworkListener<RelatedMoviesBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("MovieId", movieId);
        get(this, ConstantUrl.GET_RELATED_MOVIES, parmas, 180000, listener);
    }

    /**
     * 幕后揭秘
     */
    public void getMovieEvents(String movieId, NetworkManager.NetworkListener<MovieSecretBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("MovieId", movieId);
        get(this, ConstantUrl.GET_MOVIE_SECRET, parmas, 3600, listener);
    }

    /**
     * 媒体评论
     */
    public void getMovieMediaReviews(String movieId, NetworkManager.NetworkListener<MediaReviewBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("MovieId", movieId);
        get(this, ConstantUrl.GET_MEDIA_REVIEW, parmas, 3600, listener);
    }

    /**
     * 影片热门长影评列表
     * 默认返回20条
     */
    public void getMovieHotLongComments(String movieId, int pageIndex, NetworkManager.NetworkListener<MovieHotLongCommentAllBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", movieId);
        parmas.put("pageIndex", String.valueOf(pageIndex));
        get(this, ConstantUrl.GET_MOVIE_HOTLONGCOMMENTS_V2, parmas, listener);
    }

    /**
     * 影片时光原创列表|时光对话列表
     * 默认返回10条
     */
    public void getMovieOriginalNewsList(String movieId, int pageIndex, int pageSize, String newsType, NetworkManager.NetworkListener<HomeOriginalFeedListBean> listener) {
        Map<String, String> parmas = new HashMap<>();
        parmas.put("movieId", movieId);
        parmas.put("pageIndex", String.valueOf(pageIndex));
        parmas.put("pageSize", String.valueOf(pageSize));
        get(this, newsType.equals(MovieOriginalListActivity.NEWS_TYPE_ORIGINAL) ? ConstantUrl.GET_MOVIE_ORIGINAL_NEWS_LIST : ConstantUrl.GET_MOVIE_TALK_NEWS_LIST, parmas, listener);
    }

}
