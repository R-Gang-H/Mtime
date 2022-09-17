package com.mtime.bussiness.daily.recommend.api;

import android.text.TextUtils;

import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MLogWriter;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.recommend.bean.HistoryMovieListBean;
import com.mtime.bussiness.daily.recommend.bean.HistoryRecommendBean;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-22
 */
public class RecmdApi extends BaseApi {

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    public void getDailyPopup(NetworkManager.NetworkListener<DailyRecommendBean> listener) {
        get(this, ConstantUrl.DAILY_MOVIE_POPUP, null, listener);
    }

    public void getHistoryRecmd(String month, NetworkManager.NetworkListener<HistoryRecommendBean> listener) {
        Map<String, String> param = null;
        if (!TextUtils.isEmpty(month)) {
            param = new HashMap<>();
            param.put("month", month);
        }
        get(this, ConstantUrl.HISTORY_RECMD_MOVIE, param, listener);
    }

    public void getDailyRecmd(NetworkManager.NetworkListener<HistoryMovieListBean> listener) {
        get(this, ConstantUrl.DAILY_RECMD_MOVIE, null, listener);
    }

    public void userSign() {
        post(this, ConstantUrl.USER_SIGN, null, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                MLogWriter.i("RecmdApi", result);
            }

            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                MLogWriter.e("RecmdApi", exception.getMessage());
            }
        });
    }
}
