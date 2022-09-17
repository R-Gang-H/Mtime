//package com.mtime.bussiness.ticket.movie.api;
//
//import com.mtime.base.network.BaseApi;
//import com.mtime.base.network.NetworkManager;
//import com.mtime.bussiness.ticket.movie.details.bean.MovieDetailsHotTopicsBean;
//import com.mtime.network.ConstantUrl;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author ZhouSuQiang
// * @email zhousuqiang@126.com
// * @date 2019/4/1
// */
//public class MovieInfoApi extends BaseApi {
//    @Override
//    protected String host() {
//        return null;
//    }
//
//    /**
//     * 影片资料页 - 话题列表 (/topic/movieTopicList.api)
//     * @param movieId
//     * @param listener
//     */
//    public void getMovieHotTopicList(String movieId, NetworkManager.NetworkListener<MovieDetailsHotTopicsBean> listener) {
//        Map<String, String> parmas = new HashMap<>();
//        parmas.put("movieId", movieId);
//        get(this, ConstantUrl.GET_MOVIE_HOT_TOPICS, parmas, listener);
//    }
//
//}
