package com.mtime.bussiness.ticket.movie.api;

import com.kotlin.android.app.data.entity.common.CommBizCodeResult;
import com.kotlin.android.app.data.entity.community.record.PostRecord;
import com.kotlin.android.app.data.entity.community.record.RecordId;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedListBean;
import com.mtime.bussiness.ticket.movie.bean.FilmographyListBean;
import com.mtime.bussiness.ticket.movie.bean.PersonDynamicBean;
import com.mtime.bussiness.ticket.movie.bean.PersonRatingResultBean;
import com.mtime.network.ConstantUrl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author vivian.wei
 * @date 2020/9/17
 * @desc 类描述
 */
public class PersonApi extends BaseApi {

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    @Override
    public void cancel() {
        NetworkManager.getInstance().cancel(this);
    }

    /**
     * 影人的关联新闻列表
     * @param personId
     * @param pageIndex
     * @param pageSize
     * @param listener
     */
    public void getPersonNewsList(String personId, int pageIndex, int pageSize, NetworkManager.NetworkListener<HomeOriginalFeedListBean> listener) {
        Map<String, String> parmas = new HashMap<>(3);
        parmas.put("personId", personId);
        parmas.put("pageIndex", String.valueOf(pageIndex));
        parmas.put("pageSize", String.valueOf(pageSize));
        get(this, ConstantUrl.GET_PERSON_NEWS_LIST, parmas, listener);
    }

    /**
     * 获取用户对影人的动态信息
     * @param personId
     * @param listener
     */
    public void getPersonDynamic(String personId, NetworkManager.NetworkListener<PersonDynamicBean> listener) {
        Map<String, String> parmas = new HashMap<>(1);
        parmas.put("personId", personId);
        get(this, ConstantUrl.GET_PERSON_DYNAMIC, parmas, listener);
    }

    /**
     * 影人评分：喜欢/不喜欢
     * @param personId 影人ID
     * @param rating   评分 喜欢：8， 一般：6，不喜欢：3
     */
    public void postPersonRating(String personId, int rating, NetworkManager.NetworkListener<PersonRatingResultBean> listener) {
        Map<String, String> parmas = new HashMap<>(2);
        parmas.put("personId", personId);
        parmas.put("rating", String.valueOf(rating));
        post(this, ConstantUrl.POST_PERSON_RATING, parmas, listener);
    }

    /**
     * 获取新记录IDv2，此ID供未发布内容-保存记录API中recId使用
     * @param type 内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     */
    public void postCommunityRecordId(Long type, NetworkManager.NetworkListener<RecordId> listener) {
        Map<String, String> parmas = new HashMap<>(1);
        parmas.put("type", String.valueOf(type));
        post(this, ConstantUrl.POST_COMMUNITY_RECORD_ID, parmas, listener);
    }

    /**
     * 未发布内容-保存记录
     * @param record
     * @param networkListener
     */
    public void postCommunityRecord(PostRecord record, NetworkManager.NetworkListener<CommBizCodeResult> networkListener) {
        postJson(this, ConstantUrl.POST_COMMUNITY_RECORD, record, null, null, networkListener);
    }

    /**
     * 获取影人作品列表
     * @param personId
     * @param pageIndex
     * @param orderId    排序类型（默认0， 1评分高到低，2年代从近到远 ，3年代从远到近，4按评分人数倒序排列，人数相同按评分倒序）
     * @param listener
     */
    public void getPersonMovies(String personId, int pageIndex, int orderId, NetworkManager.NetworkListener<FilmographyListBean> listener) {
        Map<String, String> parmas = new HashMap<>(1);
        parmas.put("personId", personId);
        parmas.put("pageIndex", String.valueOf(pageIndex));
        parmas.put("orderId", String.valueOf(orderId));
        get(this, ConstantUrl.GET_FILMOGRAPHIES, parmas, listener);
    }

}
