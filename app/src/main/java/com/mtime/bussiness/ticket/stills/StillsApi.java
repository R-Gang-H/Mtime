package com.mtime.bussiness.ticket.stills;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.beans.PhotoAll;
import com.mtime.bussiness.ticket.bean.CinemaPhotoList;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-26
 */
class StillsApi extends BaseApi {

    @Override
    protected String host() {
        return null;
    }

    void getActorStills(String personId, NetworkManager.NetworkListener<PhotoAll> l) {
        Map<String, String> param = new HashMap<>(1);
        param.put("personId", personId);
        get(this, ConstantUrl.GET_PERSON_IMAGES, param, l);
    }

    void getMovieStills(String movieId, NetworkManager.NetworkListener<PhotoAll> l) {
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", movieId);
        get(this, ConstantUrl.GET_MOVIE_IMAGES, param, l);
    }

    void getCinemaStills(String cinemaId, NetworkManager.NetworkListener<CinemaPhotoList> l) {
        Map<String, String> param = new HashMap<>(1);
        param.put("cinemaId", cinemaId);
        get(this, ConstantUrl.GET_CINEMA_IMAGES, param, l);
    }
}
