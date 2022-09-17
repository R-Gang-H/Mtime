package com.mtime.player;

import android.util.Log;

import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.bean.VideoAdBean;

import java.util.HashMap;
import java.util.List;

/**
 * Created by JiaJunHui on 2018/3/26.
 */

public class AdHelper {

    static final String TAG = "AdHelper";
    private static final MovieApi mMovieApi;
    private static final HashMap<String, List<VideoAdBean.AdItem>> mAdDataMap;

    static {
        mMovieApi = new MovieApi();
        mAdDataMap = new HashMap<>();
    }

    public static void updateAdList(String vid, int videoType){
        Video video = new Video(vid, videoType);
        String key = video.key();
        if(mAdDataMap.containsKey(key)){
            List<VideoAdBean.AdItem> adItems = mAdDataMap.get(key);
            if(adItems!=null){
                return;
            }
        }
        postLoad(video, null);
    }

    public static List<VideoAdBean.AdItem> getAdList(String vid, int videoType){
        Video video = new Video(vid, videoType);
        return mAdDataMap.get(video.key());
    }

    public static void getAdList(String vid, int videoType, OnAdDataListener onAdDataListener){
        Video video = new Video(vid, videoType);
        String key = video.key();
        if(mAdDataMap.containsKey(key)){
            List<VideoAdBean.AdItem> adItems = mAdDataMap.get(key);
            if(adItems!=null){
                onAdDataListener.onAdListReady(adItems);
                return;
            }
        }

        postLoad(video, onAdDataListener);

    }

    private static void postLoad(final Video video, final OnAdDataListener onAdDataListener) {
        mMovieApi.getAdInfo(TAG, video.vid, video.videoType, null, new NetworkManager.NetworkListener<VideoAdBean>() {
            @Override
            public void onSuccess(VideoAdBean result, String showMsg) {
                if(result!=null && result.getDaList()!=null){
                    List<VideoAdBean.AdItem> mAdItems = result.getDaList();
                    for(VideoAdBean.AdItem item: mAdItems){
                        Log.d(TAG,"<FOR> id = " + item.getaID() + " url = " + item.getImage() + " isShowTag = " + item.isShowTag() + " tag = " + item.getTagDesc() + " duration = " + item.getDuration() + "s");
                    }
                    mAdDataMap.put(video.key(), mAdItems);

                    if(onAdDataListener!=null)
                        onAdDataListener.onAdListReady(mAdItems);
                }
            }
            @Override
            public void onFailure(NetworkException<VideoAdBean> exception, String showMsg) {
                Log.d(TAG,showMsg);
            }
        });
    }

    public static void destroy(){
        mAdDataMap.clear();
    }

    public static class Video{
        public String vid;
        public int videoType;

        public Video() {
        }

        public Video(String vid, int videoType) {
            this.vid = vid;
            this.videoType = videoType;
        }

        public String key(){
            return vid + videoType;
        }
    }

    public interface OnAdDataListener{
        void onAdListReady(List<VideoAdBean.AdItem> items);
    }

}
