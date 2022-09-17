package com.mtime.bussiness.video.api;

import android.text.TextUtils;

import com.kotlin.android.app.data.entity.video.VideoPlayList;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.video.bean.BarrageBean;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.bean.PlayerMovieDetail;
import com.mtime.bussiness.video.bean.SendBarrageBean;
import com.mtime.bussiness.video.bean.VideoAdBean;
import com.mtime.bussiness.video.bean.VideoInfoApiRequestBean;
import com.mtime.bussiness.video.bean.VideoInfoBean;
import com.mtime.network.ConstantUrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mtime on 2017/10/11.
 */

public class MovieApi extends BaseApi {

    private final List<Object> tags = new ArrayList<>();

    private void addTag(Object tag) {
        tags.add(tag);
    }

    public void cancelAllTags() {
        if (tags != null) {
            for (Object tag : tags) {
                cancel(tag);
            }
            tags.clear();
        }
    }

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel(Object tag) {
        super.cancel(tag);
    }

    public void cancelRequest(Object tag) {
        cancel(tag);
    }

    /**
     * 获取影片视频详情
     *
     * @param tag
     * @param vid
     * @param locationId
     * @param networkListener
     */
    public void getMovieDetail(String tag, String vid, String locationId, NetworkManager.NetworkListener<PlayerMovieDetail> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("vId", vid);
        params.put("locationId", locationId);
        get(tag, ConstantUrl.GET_MOVIE_DETAIL, params, networkListener);
    }

    /**
     * 查询视频信息列表
     *
     * @param tag
     * @param requestBean
     * @param networkListener
     */
    public void getVideoInfo(String tag, VideoInfoApiRequestBean requestBean, NetworkManager.NetworkListener<VideoInfoBean> networkListener) {
        addTag(tag);
        postJson(tag, ConstantUrl.POST_PLAY_VIDEO_INFO, requestBean, null, null, networkListener);
    }

    /**
     * 发送弹幕
     *
     * @param tag
     * @param relatedObjId
     * @param relatedObjType
     * @param currentPlayPosition
     * @param content
     * @param networkListener
     */
    public void sendDanmu(String tag, String relatedObjId, String relatedObjType, long currentPlayPosition, String content, NetworkManager.NetworkListener<SendBarrageBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("relatedObjId", relatedObjId);//视频ID
        params.put("relatedObjType", relatedObjType);//视频类型 默认1媒资，2预告片
        params.put("pointSeconds", currentPlayPosition + "");//弹幕发送时间
        params.put("content", content);//弹幕内容
        post(tag, ConstantUrl.POST_BARRAGE_SUBJECT_SHOOT, params, networkListener);
    }

    public void getDanmuInfo(String tag, String vid, String relatedObjType, int startSecond, int endSecond, NetworkManager.NetworkListener<BarrageBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("relatedObjId", vid);//视频ID
        params.put("relatedObjType", relatedObjType);//视频类型 默认1媒资，2预告片
        params.put("startSeconds", String.valueOf(startSecond));//每隔10秒请求一次  每次请求间隔15秒的的弹幕信息  第一次在0秒时获取0-15秒 第二次在10秒时 获取10-25秒的弹幕 以此类推
        params.put("endSeconds", String.valueOf(endSecond));
        get(tag, ConstantUrl.GET_BARRAGE_SUBJECT_LIST, params, networkListener);
    }

    /**
     * 获取播放地址
     *
     * @param tag
     * @param videoId
     * @param source          videoSource
     * @param networkListener
     */
    public void getPlayUrl(String tag, String videoId, int source, String scheme, NetworkManager.NetworkListener<VideoPlayList> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("video_id", videoId);
        params.put("source", String.valueOf(source));
        params.put("scheme", scheme);
        get(tag, ConstantUrl.GET_PLAY_URL, params, networkListener);
    }

    /**
     * 获取视频的广告信息
     *
     * @param tag
     * @param videoId
     * @param videoType
     * @param movieId
     * @param networkListener
     */
    public void getAdInfo(String tag, String videoId, int videoType, String movieId, NetworkManager.NetworkListener<VideoAdBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("videoId", videoId);
        params.put("videoType", String.valueOf(videoType));
        if (!TextUtils.isEmpty(movieId)) {
            params.put("movieId", movieId);
        }
        get(tag, ConstantUrl.GET_VIDEO_AD_INFO, params, networkListener);
    }

    /**
     * 获取影片分类视频
     *
     * @param tag
     * @param movieId
     * @param pageIndex
     * @param type
     * @param networkListener
     */
    public void getCategroyVideos(String tag, int movieId, int pageIndex, int type, NetworkManager.NetworkListener<CategoryVideosBean> networkListener) {
        addTag(tag);
        Map<String, String> params = new HashMap<>();
        params.put("movieId", String.valueOf(movieId));
        params.put("pageIndex", String.valueOf(pageIndex));
        params.put("type", String.valueOf(type));
        get(tag, ConstantUrl.GET_CATEGORY_VIDEO, params, networkListener);
    }
}
