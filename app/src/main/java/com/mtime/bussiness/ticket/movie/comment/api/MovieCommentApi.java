package com.mtime.bussiness.ticket.movie.comment.api;

import androidx.collection.ArrayMap;
import android.text.TextUtils;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.bean.MSyncBaseBean;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.ticket.movie.comment.bean.CommentScoreBean;
import com.mtime.bussiness.ticket.movie.comment.bean.DeleteCommentsResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.EmptyResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.MineLongMovieCommentsBean;
import com.mtime.bussiness.ticket.movie.comment.bean.PostCommentResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.RatingMovieResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.ShareMovieCommentBean;
import com.mtime.network.ConstantUrl;

import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-31
 */
public class MovieCommentApi extends BaseApi {

    private Subscription mSubscribe;

    @Override
    protected String host() {
        return null;
    }

    @Override
    public void cancel() {
        super.cancel();
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
        }
    }

    /**
     * 获取我的长影评列表
     */
    public void getMyLongComments(int pageIndex, int pageSize, NetworkManager.NetworkListener<MineLongMovieCommentsBean> l) {
        Map<String, String> params = new ArrayMap<>();
        params.put("pageIndex", String.valueOf(pageIndex));
        params.put("pageSize", String.valueOf(pageSize));
        get(this, ConstantUrl.GET_MY_LONG_MOVIE_COMMENTS, params, l);
    }

    /**
     * 获取分享影评内容
     */
    public void getShareMovieComment(long movieId, long commentId, int commentType, NetworkManager.NetworkListener<ShareMovieCommentBean> l) {
        Map<String, String> params = new ArrayMap<>();
        params.put("movieId", String.valueOf(movieId));
        if (commentId > 0) {
            params.put("commentId", String.valueOf(commentId));
            params.put("commentType", String.valueOf(commentType));
        }
        get(this, ConstantUrl.GET_SHARE_IMAGES, params, l);
    }

    /**
     * 发布短评
     */
    public void postShortComment(long movieId,
                                 String content, String imageId, NetworkManager.NetworkListener<PostCommentResultBean> listener) {
        Map<String, String> params = new ArrayMap<>();
        params.put("movieId", String.valueOf(movieId));
        params.put("c", content);
        if (!TextUtils.isEmpty(imageId)) {
            params.put("imageUrl", imageId);
        }
        post(this, ConstantUrl.POST_MOVIE_SHORT_COMMENT, params, listener);
    }

    /**
     * 影片评分
     */
    public void postRatingMovie(long movieId, CommentScoreBean scoreBean, NetworkManager.NetworkListener<RatingMovieResultBean> listener) {
        Map<String, String> params = new ArrayMap<>();
        params.put("movieId", String.valueOf(movieId));
        if (scoreBean.useWholeScore) {
            params.put("r", String.valueOf((int) scoreBean.wholeScore));
        } else {
            params.put("ir", String.valueOf((int) scoreBean.impressionsScore));
            params.put("str", String.valueOf((int) scoreBean.storyScore));
            params.put("shr", String.valueOf((int) scoreBean.actingScore));
            params.put("dr", String.valueOf((int) scoreBean.directorScore));
            params.put("pr", String.valueOf((int) scoreBean.pictureScore));
            params.put("mr", String.valueOf((int) scoreBean.musicScore));
        }
        post(this, ConstantUrl.POST_MOVIE_RATING, params, listener);
    }

    /**
     * 删除短影评
     */
    public void deleteShortComment(long commentId, NetworkManager.NetworkListener<EmptyResultBean> l) {
        //POST_MOVIE_DELETE_SHORT_COMMENT
        Map<String, String> params = new ArrayMap<>();
        params.put("commentId", String.valueOf(commentId));
        post(this, ConstantUrl.POST_MOVIE_DELETE_SHORT_COMMENT, params, l);
    }

    /**
     * 发布长影评
     *
     * @param blogOrigin 是否原创
     * @param isSpoiler  是否剧透
     */
    public void postLongComment(long movieId, String title, String body,
                                boolean blogOrigin, boolean isSpoiler,
                                NetworkManager.NetworkListener<PostCommentResultBean> l) {
        //POST_MOVIE_LONG_COMMENT
        Map<String, String> params = new ArrayMap<>();
        params.put("movieId", String.valueOf(movieId));

        params.put("title", title);
        params.put("body", body);

        params.put("blogOrigin", blogOrigin ? "0" : "1");
        params.put("isSpoiler", isSpoiler ? "1" : "0");
        post(this, ConstantUrl.POST_MOVIE_LONG_COMMENT, params, l);
    }

    public void deleteShortComment(long movieId, long shortId, DeleteCommentsCallback c) {
        mSubscribe = Observable.zip(deleteShort(shortId), deleteRating(movieId), (shortRes, ratingRes) -> {
            DeleteCommentsResultBean result = new DeleteCommentsResultBean();
            result.shortDeleted = shortRes != null && shortRes.code == 1;
            return result;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(c::onDeleteResult, throwable -> {
                    throwable.printStackTrace();
                    c.onDeleteResult(null);
                });
    }

    public void deleteLongComment(long longId, DeleteCommentsCallback c) {
        mSubscribe = deleteLong(longId).map(longRes -> {
            DeleteCommentsResultBean result = new DeleteCommentsResultBean();
            result.longDeleted = longRes != null && longRes.code == 1;
            return result;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(c::onDeleteResult, throwable -> {
                    throwable.printStackTrace();
                    c.onDeleteResult(null);
                });
    }

    private Observable<RatingMovieResultBean> deleteRating(long movieId) {
        return Observable.fromCallable(() -> {
            Map<String, String> params = new ArrayMap<>();
            params.put("movieId", String.valueOf(movieId));
            params.put("r", "0");
            params.put("ir", "0");
            params.put("str", "0");
            params.put("shr", "0");
            params.put("dr", "0");
            params.put("pr", "0");
            params.put("mr", "0");

            return syncPost(ConstantUrl.POST_MOVIE_RATING, params, RatingMovieResultBean.class);
        }).subscribeOn(Schedulers.io());
    }

    private Observable<DeleteShortRes> deleteShort(long commentId) {
        return Observable.fromCallable(() -> {
            if (commentId <= 0) {
                return new DeleteShortRes(1);
            }
            Map<String, String> params = new ArrayMap<>();
            params.put("commentId", String.valueOf(commentId));
            return syncPost(ConstantUrl.POST_MOVIE_DELETE_SHORT_COMMENT, params, DeleteShortRes.class);
        }).subscribeOn(Schedulers.io());
    }

    private Observable<DeleteLongRes> deleteLong(long commentId) {
        return Observable.fromCallable(() -> {
            if (commentId <= 0) {
                return new DeleteLongRes(1);
            }
            Map<String, String> params = new ArrayMap<>();
            params.put("commentId", String.valueOf(commentId));
            return syncPost(ConstantUrl.POST_MOVIE_DELETE_LONG_COMMENT, params, DeleteLongRes.class);
        }).subscribeOn(Schedulers.io());
    }

    public interface DeleteCommentsCallback {
        void onDeleteResult(DeleteCommentsResultBean result);
    }

    public static class DeleteShortRes extends MSyncBaseBean implements IObfuscateKeepAll {
        public DeleteShortRes() {
        }

        public DeleteShortRes(int code) {
            this.code = code;
        }
    }

    public static class DeleteLongRes extends MSyncBaseBean implements IObfuscateKeepAll {
        public DeleteLongRes() {
        }

        public DeleteLongRes(int code) {
            this.code = code;
        }
    }

}
