package com.mtime.player;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.provider.BaseDataProvider;
import com.kotlin.android.app.data.entity.video.VideoPlayList;
import com.kotlin.android.app.data.entity.video.VideoPlayUrl;
import com.kotlin.android.audio.floatview.component.aduiofloat.constant.AudioConstantKt;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.player.bean.SchemeType;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.bean.VideoInfoApiRequestBean;
import com.mtime.bussiness.video.bean.VideoInfoBean;
import com.mtime.player.bean.MTimeVideoData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtime on 2017/10/19.
 */

public class MTimeDataProvider extends BaseDataProvider {

    private final String TAG = "MTimeDataProvider";

    private final String API_TAG_GET_PLAY_URL = "player_get_play_url";
    private final String API_TAG_POST_PLAY_VIDEO_INFO = "player_get_play_video_info";
    private final MovieApi mMovieApi;
    private boolean cancelFlag;

    public MTimeDataProvider() {
        mMovieApi = new MovieApi();
    }

    @Override
    public void handleSourceData(DataSource data) {
        if (data == null)
            return;
        cancelFlag = false;
        AudioConstantKt.sendCloseEvent();
        if (!TextUtils.isEmpty(data.getData())) {
            Bundle obtain = BundlePool.obtain();
            obtain.putSerializable(EventKey.SERIALIZABLE_DATA, data);
            onProviderMediaDataSuccess(obtain);
            return;
        }
        if (data instanceof MTimeVideoData) {
            handleGetVideoInfo((MTimeVideoData) data);
        }
    }

    /**
     * 获取视频基础信息
     *
     * @param data
     */
    private void handleGetVideoInfo(MTimeVideoData data) {
        //callback start
        onProviderDataStart();
        handleUrlGet(data);
//        VideoInfoApiRequestBean videoInfoApiRequestBean = new VideoInfoApiRequestBean();
//        List<VideoInfoApiRequestBean.VideoInfoApiRequestItem> videoInfoApiRequestItems = new ArrayList<>();
//        VideoInfoApiRequestBean.VideoInfoApiRequestItem requestItem = new VideoInfoApiRequestBean.VideoInfoApiRequestItem();
//        requestItem.setVideoId(Long.parseLong(data.getVideoId()));
//        requestItem.setSource(data.getSource());
//        videoInfoApiRequestItems.add(requestItem);
//        videoInfoApiRequestBean.setScheme(SchemeType.HTTP.getType());
//        videoInfoApiRequestBean.setVideoInfoApiRequest(videoInfoApiRequestItems);
//        mMovieApi.cancelRequest(API_TAG_POST_PLAY_VIDEO_INFO);
//        mMovieApi.getVideoInfo(API_TAG_POST_PLAY_VIDEO_INFO, videoInfoApiRequestBean, new NetworkManager.NetworkListener<VideoInfoBean>() {
//            @Override
//            public void onSuccess(VideoInfoBean result, String showMsg) {
//                if (result != null) {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable(EventKey.SERIALIZABLE_DATA, result);
//                    onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_INFO_READY, bundle);
//                    if (result.isPlayable(data.getVideoId())) {
//                        handleUrlGet(data);
//                    } else {
//                        onProviderMediaDataError(null);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(NetworkException<VideoInfoBean> exception, String showMsg) {
//                Log.d(TAG, "showMsg = " + showMsg);
//                onProviderMediaDataError(null);
//            }
//        });
    }

    /**
     * 获取视频各清晰度的播放链接
     *
     * @param data
     */
    private void handleUrlGet(final MTimeVideoData data) {
        if (cancelFlag)
            return;
        mMovieApi.cancelRequest(API_TAG_GET_PLAY_URL);
        mMovieApi.getPlayUrl(API_TAG_GET_PLAY_URL, data.getVideoId(), data.getSource(), SchemeType.HTTP.getType(),
                new NetworkManager.NetworkListener<VideoPlayList>() {
                    @Override
                    public void onSuccess(VideoPlayList result, String showMsg) {
                        if (result != null && !cancelFlag) {
                            List<VideoPlayUrl> urlItems = result.getPlayUrlList();
                            if (urlItems != null && urlItems.size() > 0) {
                                handleGetUrlResponseData(data, urlItems);
                                //send get video info event
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(DataInter.Key.KEY_PROVIDER_PLAY_URL_INFO, result);
                                onProviderExtraDataSuccess(DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA, bundle);
                            }
                        }
                    }

                    @Override
                    public void onFailure(NetworkException<VideoPlayList> exception, String showMsg) {
                        onProviderMediaDataError(null);
                    }
                });
    }

    /**
     * 处理响应的链接数据
     *
     * @param data
     * @param urlItems
     */
    private void handleGetUrlResponseData(MTimeVideoData data, List<VideoPlayUrl> urlItems) {
        int size = urlItems.size();
        //默认使用最高清晰度起播。
        String recordDefinition = PlayerHelper.getHistoryDefinition();
        VideoPlayUrl playItem = PlayerHelper.getHighDefinition(urlItems);
        for (int i = 0; i < size; i++) {
            VideoPlayUrl item = urlItems.get(i);
            //如果本地有清晰度记录，就以记录清晰度起播
            if (String.valueOf(item.getResolutionType()).equalsIgnoreCase(recordDefinition)) {
                playItem = item;
            }
        }

        //处理最终的播放数据
        handleDataSourceReady(data, playItem);
    }

    /**
     * 处理最终播放数据，回调给播放器
     *
     * @param data
     * @param playItem
     */
    private void handleDataSourceReady(MTimeVideoData data,VideoPlayUrl playItem) {
        MTimeVideoData playItemData = new MTimeVideoData(playItem.getUrl());
        playItemData.setVideoId(data.getVideoId());
        playItemData.setSource(data.getSource());
        //set tag for definition
        playItemData.setTag(playItem.getName());
        playItemData.setFileSize(playItem.getFileSize());

        //将源数据的起播时间点设置上
        playItemData.setStartPos(data.getStartPos());
        onDataSourceReady(playItemData);
    }

    protected void onDataSourceReady(DataSource data) {
        Bundle bundle = BundlePool.obtain();
        bundle.putSerializable(EventKey.SERIALIZABLE_DATA, data);
        onProviderMediaDataSuccess(bundle);
    }

    @Override
    public void cancel() {
        cancelFlag = true;
        mMovieApi.cancelAllTags();
    }

    @Override
    public void destroy() {
        cancel();
    }
}
