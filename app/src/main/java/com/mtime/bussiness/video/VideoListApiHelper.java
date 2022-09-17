package com.mtime.bussiness.video;

import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.video.api.MovieApi;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.bean.ReViewPariseByRelatedBean;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/3/22.
 */

public class VideoListApiHelper {

    final String relatedObjType = "120";
    private static VideoListApiHelper i;

    private final MovieApi mMovieApi;
    private final PraiseCommentApi mPraiseCommentApi;

    private VideoListApiHelper() {
        mMovieApi = new MovieApi();
        mPraiseCommentApi = new PraiseCommentApi();
    }

    public static VideoListApiHelper get() {
        if (null == i) {
            synchronized (VideoListApiHelper.class) {
                if (null == i) {
                    i = new VideoListApiHelper();
                }
            }
        }
        return i;
    }

    public void loadVideos(final String tag, int movieId, int pageIndex, int type, final OnApiLoadListener onApiLoadListener) {
        mMovieApi.getCategroyVideos(tag, movieId, pageIndex, type, new NetworkManager.NetworkListener<CategoryVideosBean>() {
            @Override
            public void onSuccess(final CategoryVideosBean result, final String showMsg) {
                if (result.hasListData()) {
                    String ids = getIDs(result.getVideoList());
//                    点赞信息加入到视频列表接口中了，不需要再次请求批量查询点赞信息接口
//                    loadCommentPraise(tag, ids, relatedObjType, result.getVideoList(), new OnPraiseUpdateListener() {
//                        @Override
//                        public void onPraiseUpdate() {
//                            if (onApiLoadListener != null)
//                                onApiLoadListener.onSuccess(result, showMsg);
//                        }
//                    });
                    if (onApiLoadListener != null)
                        onApiLoadListener.onSuccess(result, showMsg);
                } else {
                    if (onApiLoadListener != null)
                        onApiLoadListener.onSuccess(result, showMsg);
                }
            }

            @Override
            public void onFailure(NetworkException<CategoryVideosBean> exception, String showMsg) {
                if (onApiLoadListener != null)
                    onApiLoadListener.onFailure(exception, showMsg);
            }
        });
    }

    private void loadCommentPraise(String tag, String ids, String relatedObjType, final List<CategoryVideosBean.RecommendVideoItem> videoItems, final OnPraiseUpdateListener onPraiseUpdateListener) {
        mPraiseCommentApi.getArticlePraiseInfo(tag, ids, relatedObjType, new NetworkManager.NetworkListener<ReViewPariseByRelatedBean>() {
            @Override
            public void onSuccess(ReViewPariseByRelatedBean result, String showMsg) {
                if (result != null && result.getReviewParises() != null) {
                    updateVideoList(videoItems, result.getReviewParises());
                }
                onPraiseUpdateListener.onPraiseUpdate();
            }

            @Override
            public void onFailure(NetworkException<ReViewPariseByRelatedBean> exception, String showMsg) {
                onPraiseUpdateListener.onPraiseUpdate();
            }
        });
    }

    private String getIDs(List<CategoryVideosBean.RecommendVideoItem> videoItems) {
        String result = "";
        if (videoItems == null || videoItems.size() < 0)
            return result;
        int size = videoItems.size();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(videoItems.get(i).getvId()).append(i == size - 1 ? "" : ",");
        }
        result = sb.toString();
        return result;
    }

    private void updateVideoList(List<CategoryVideosBean.RecommendVideoItem> videoItems, List<ReViewPariseByRelatedBean.ReviewParisesBean> reviewBean) {
        CategoryVideosBean.RecommendVideoItem bean;
        for (int i = 0; i < reviewBean.size(); i++) {
            bean = videoItems.get(i);
            bean.setPraiseInfo(String.valueOf(reviewBean.get(i).getTotalPraise()));
            bean.setPraised(reviewBean.get(i).isIsPraise());
        }
    }

    public void cancelTag(String tag) {
        if (mMovieApi != null)
            mMovieApi.cancelRequest(tag);
        if (mPraiseCommentApi != null)
            mPraiseCommentApi.cancelRequest(tag);
    }

    public interface OnApiLoadListener {
        void onSuccess(CategoryVideosBean result, String showMsg);

        void onFailure(NetworkException<CategoryVideosBean> exception, String showMsg);
    }

    public interface OnPraiseUpdateListener {
        void onPraiseUpdate();
    }

}
