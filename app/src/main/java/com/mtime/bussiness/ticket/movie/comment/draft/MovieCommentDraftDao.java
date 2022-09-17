//package com.mtime.bussiness.ticket.movie.comment.draft;
//
//import androidx.core.util.ObjectsCompat;
//
//import com.kotlin.android.user.UserManager;
//import com.mtime.base.utils.CollectionUtils;
//import com.mtime.bussiness.draft.bean.DraftItemBean;
//import com.mtime.bussiness.draft.iinterface.BaseDraftDao;
//
//import org.litepal.LitePal;
//
//import java.util.List;
//
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-05-30
// * <p>
// * 长短影评草稿
// */
//public class MovieCommentDraftDao extends BaseDraftDao {
//
//    private static final String COLUMN_MOVIE_ID = "movieid";
//    private static final String COLUMN_COMMENT_TYPE = "longcomment";
//
//    private static final String COLUMN_MOVIE_NAME = "moviename";
//
//    public long movieId; // 电影id
//
//    public String movieName; // 影片名
//
//    public boolean longComment = false;
//
//    public float wholeScore; // 整体评分
//
//    public float musicScore; // 音乐评分
//    public float pictureScore; // 画面评分
//    public float directorScore; // 导演评分
//    public float storyScore; // 故事评分
//    public float actingScore; // 表演评分
//    public float impressionsScore; // 印象评分
//
//    public String longCommentTitle; // 长影评标题
//
//    public boolean isOriginal = true; // 是否原创
//    public boolean containsMovieContent; // 是否剧透
//
//    public long shortCommentId; // 已经存在的短评
//
//    public boolean hasLongComment; // 是否有长评
//
//    public boolean useWholeScore;
//
//    public String getTitle() {
//        return movieName;
//    }
//
//    @Override
//    public int getType() {
//        return DraftItemBean.TYPE_MOVIE_COMMENT;
//    }
//
//    @Override
//    protected void onSaveDraft() {
//        saveOrUpdate(COLUMN_USER_ID + " = ? and " +
//                        COLUMN_MOVIE_ID + " = ? and " +
//                        COLUMN_COMMENT_TYPE + " = ?",
//                String.valueOf(userId), String.valueOf(movieId), String.valueOf(longComment ? 1 : 0));
//    }
//
//    @Override
//    protected void onDeleteDraft() {
////            if (isSaved()) {
//        delete();
////            }
//    }
//
//    public void deleteDraftTemp() {
//        Observable.fromCallable(() -> {
////            if (isSaved()) {
//            delete();
////            }
//            return "";
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(s -> {
//                }, Throwable::printStackTrace);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        MovieCommentDraftDao that = (MovieCommentDraftDao) o;
//        return userId == that.userId &&
//                movieId == that.movieId &&
//                ObjectsCompat.equals(id, that.id);
//    }
//
//    @Override
//    public int hashCode() {
//        return ObjectsCompat.hash(id, userId, movieId);
//    }
//
//    public static void getByMovieId(long movieId, boolean longComment, FindOneDraftCallback c) {
//        Observable.fromCallable(() -> {
//            long userId = UserManager.Companion.getInstance().getUserId();
//            List<MovieCommentDraftDao> drafts = LitePal.where(COLUMN_USER_ID + " = ? and " +
//                            COLUMN_MOVIE_ID + " = ? and " + COLUMN_COMMENT_TYPE + " = ? ",
//                    String.valueOf(userId), String.valueOf(movieId), String.valueOf(longComment ? 1 : 0))
//                    .find(MovieCommentDraftDao.class);
//            handleDrafts(drafts);
//            return drafts;
//        }).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(drafts -> {
//                    if (c == null) {
//                        return;
//                    }
//                    if (drafts.isEmpty()) {
//                        c.onGetDraft(null);
//                    } else {
//                        c.onGetDraft(drafts.get(0));
//                    }
//                }, throwable -> c.onGetDraft(null));
//    }
//
//    private static void handleDrafts(List<MovieCommentDraftDao> drafts) {
//        if (CollectionUtils.isNotEmpty(drafts)) {
//            for (MovieCommentDraftDao draft : drafts) {
//                handleContent(draft);
//            }
//        }
//    }
//
//}
