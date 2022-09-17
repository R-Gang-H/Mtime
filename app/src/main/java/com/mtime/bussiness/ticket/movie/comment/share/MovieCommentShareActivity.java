//package com.mtime.bussiness.ticket.movie.comment.share;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.alibaba.android.arouter.facade.annotation.Route;
//import com.kk.taurus.uiframe.d.BaseState;
//import com.kk.taurus.uiframe.v.BaseStateContainer;
//import com.kk.taurus.uiframe.v.ContentHolder;
//import com.kk.taurus.uiframe.v.NoTitleBarContainer;
//import com.kotlin.android.app.router.path.RouterActivityPath;
//import com.mtime.base.network.NetworkException;
//import com.mtime.base.network.NetworkManager;
//import com.mtime.bussiness.ticket.movie.comment.api.MovieCommentApi;
//import com.mtime.bussiness.ticket.movie.comment.bean.ShareMovieCommentBean;
//import com.mtime.frame.BaseFrameUIActivity;
//import com.mtime.statusbar.StatusBarUtil;
//import com.mtime.util.MtimeUtils;
//
//import java.text.DecimalFormat;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
// * <p>
// * On 2019-06-24
// */
//@Route(path = RouterActivityPath.Ticket.PAGER_MOVIE_COMMENT_SHARE_ACTIVITY)
//public class MovieCommentShareActivity extends BaseFrameUIActivity<Void, MovieCommentShareHolder> implements MovieCommentShareHolder.Callback {
//    public static String KEY_MOVIE_ID = "movieId";
//    public static String KEY_COMMENT_ID  ="commentId";
//    public static String KEY_LONG_COMMENT = "longComment";
//    public static String KEY_MOVIE_SCORE = "movieScore";
//
//    public static void start(Context context, long movieId, long commentId, boolean longComment, float movieScore, String refer) {
//        Intent starter = new Intent(context, MovieCommentShareActivity.class);
//        starter.putExtra(KEY_MOVIE_ID, movieId);
//        starter.putExtra(KEY_COMMENT_ID, commentId);
//        starter.putExtra(KEY_LONG_COMMENT, longComment);
//        starter.putExtra(KEY_MOVIE_SCORE, movieScore);
//        dealRefer(context, refer, starter);
//        context.startActivity(starter);
//    }
//
//    private MovieCommentShareHolder mHolder;
//    private long mMovieId;
//    private long mCommentId;
//    private int mCommentType = 2;
//    private float mMovieScore;
//
//    private final MovieCommentApi mApi = new MovieCommentApi();
//    private final DecimalFormat format = new DecimalFormat("0.0");
//    private final Map<String, String> mMovieIdMap = new HashMap<>();
//
//    @Override
//    public ContentHolder onBindContentHolder() {
//        return (mHolder = new MovieCommentShareHolder(this, this));
//    }
//
//    @Override
//    protected BaseStateContainer getStateContainer() {
//        return new NoTitleBarContainer(this, this, this);
//    }
//
//    @Override
//    protected void onInit(Bundle savedInstanceState) {
//        super.onInit(savedInstanceState);
//        mMovieId = getIntent().getLongExtra(KEY_MOVIE_ID, 0);
//        mCommentId = getIntent().getLongExtra(KEY_COMMENT_ID, 0);
//        boolean longComment = getIntent().getBooleanExtra(KEY_LONG_COMMENT, false);
//        mCommentType = longComment ? 1 : 2;
//        mMovieScore = getIntent().getFloatExtra(KEY_MOVIE_SCORE, 0);
//
//        //设置状态栏透明
//        StatusBarUtil.setTranslucentStatus(this);
//        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//            //这样半透明+白=灰, 状态栏的文字能看得清
//            StatusBarUtil.setStatusBarColor(this, 0x55000000);
//        }
//
//        mMovieIdMap.put("movieID", String.valueOf(mMovieId));
//
//        setPageLabel("shareCard");
//        putBaseStatisticParam("movieID", String.valueOf(mMovieId));
//    }
//
//    @Override
//    protected void onErrorRetry() {
//        onLoadState();
//    }
//
//    @Override
//    protected void onLoadState() {
//        setPageState(BaseState.LOADING);
//        mApi.getShareMovieComment(mMovieId, mCommentId, mCommentType, new NetworkManager.NetworkListener<ShareMovieCommentBean>() {
//            @Override
//            public void onSuccess(ShareMovieCommentBean result, String showMsg) {
//                if (result == null) {
//                    setPageState(BaseState.ERROR);
//                    return;
//                }
//                handleShareResult(result);
//            }
//
//            @Override
//            public void onFailure(NetworkException<ShareMovieCommentBean> exception, String showMsg) {
//                setPageState(BaseState.ERROR);
//            }
//        });
//    }
//
//    private void handleShareResult(ShareMovieCommentBean shareBean) {
//        if (mCommentId <= 0) {
//            shareBean.userComment = getString(MtimeUtils.getDefaultScoreContent(mMovieScore));
//            if (mMovieScore >= 10) {
//                shareBean.userRating = "10 ";
//            } else {
//                shareBean.userRating = format.format(mMovieScore) + " ";
//            }
//        }
//        mHolder.showShare(shareBean);
//        setPageState(BaseState.SUCCESS);
//    }
//
//    @Override
//    public void onHolderEvent(int eventCode, Bundle bundle) {
//        super.onHolderEvent(eventCode, bundle);
//        if (eventCode == 1) {
//            onBackPressed();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Map<String, String> params = new HashMap<>(mMovieIdMap);
//        params.put("picID", stillUrl);
//        params.put("picOrder", String.valueOf(pos));
//        params.put("slideCount", String.valueOf(mStillScrollCount));
//        params.put("chooseCount", String.valueOf(chooseCount));
//        params.put("saveCount", String.valueOf(saveCount));
//        params.put("shareCount", String.valueOf(shareCount));
//        assembleOnlyRegion(params, "back").submit();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        mApi.cancel();
//    }
//
//    private String stillUrl;
//    private int pos;
//    private int chooseCount;
//    private int saveCount;
//    private int shareCount;
//
//    private int mStillScrollCount = 0;
//    private boolean anonyState = false;
//
//    @Override
//    public void onStillChanged(String url, int pos) {
//        stillUrl = url;
//        this.pos = pos;
//        chooseCount++;
//        assembleOnlyRegion(null, "shareCard", "choosePic").submit();
//    }
//
//    @Override
//    public void onStillScrolled() {
//        mStillScrollCount++;
//        assembleOnlyRegion(null, "shareCard", "choosePic", "slidePic").submit();
//    }
//
//    @Override
//    public void sharePic(int channel) {
//        shareCount++;
//        Map<String, String> params = new HashMap<>(mMovieIdMap);
//        params.put("picID", stillUrl);
//        params.put("picOrder", String.valueOf(pos));
//        params.put("anonyState", anonyState ? "1" : "0");
//        switch (channel) {
//            case DirectShare.CHANNEL_WECHAT:
//                params.put("shareTo", "WeiXinSession");
//                break;
//            case DirectShare.CHANNEL_MOMENTS:
//                params.put("shareTo", "WeiXinTimeline");
//                break;
//            case DirectShare.CHANNEL_SINA:
//                params.put("shareTo", "WeiBo");
//                break;
//            case DirectShare.CHANNEL_QQ:
//                params.put("shareTo", "QQ");
//                break;
//        }
//        assembleOnlyRegion(params, "shareCard", "sharePic").submit();
//    }
//
//    @Override
//    public void savePic() {
//        saveCount++;
//        Map<String, String> params = new HashMap<>(mMovieIdMap);
//        params.put("picID", stillUrl);
//        params.put("anonyState", anonyState ? "1" : "0");
//        params.put("picOrder", String.valueOf(pos));
//        assembleOnlyRegion(params, "shareCard", "savePic").submit();
//    }
//
//    @Override
//    public void onPicConfirm() {
//        assembleOnlyRegion(null, "shareCard", "choosePic", "picConfirm").submit();
//    }
//
//    @Override
//    public void onNnonyStateChanged(boolean isChecked) {
//        anonyState = isChecked;
//        assembleOnlyRegion(null, "shareCard", "anonymous").submit();
//    }
//}
