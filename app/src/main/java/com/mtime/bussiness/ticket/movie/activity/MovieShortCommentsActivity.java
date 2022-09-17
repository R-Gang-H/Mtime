package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;

import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState;
import com.kotlin.android.app.data.entity.community.praisestate.PraiseStateList;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.provider.publish.IPublishProvider;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;
import com.mtime.bussiness.ticket.bean.TargetObjStatus;
import com.mtime.bussiness.ticket.movie.adapter.MovieShortCommentListAdapter;
import com.mtime.bussiness.ticket.movie.bean.MovieVoteBean;
import com.mtime.bussiness.ticket.movie.bean.MovieVoteResultBean;
import com.mtime.bussiness.ticket.movie.bean.RatingItemsBean;
import com.mtime.bussiness.ticket.movie.bean.SubmitVoteBean;
import com.mtime.bussiness.ticket.movie.bean.V2_MovieCommentAllBean;
import com.mtime.bussiness.ticket.movie.bean.V2_MovieCommentBean;
import com.mtime.bussiness.ticket.movie.bean.VoteDataOptionBean;
import com.mtime.bussiness.ticket.movie.widget.ShareCommentView;
import com.mtime.bussiness.ticket.movie.widget.VoteView;
import com.mtime.bussiness.video.api.PraiseCommentApi;
import com.mtime.common.utils.LogWriter;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kotlin.android.app.data.annotation.AnnotationExtKt.CONTENT_TYPE_FILM_COMMENT;

/**
 * Created by LEE on 15-10-29.
 * 电影短评页
 */
public class MovieShortCommentsActivity extends BaseActivity implements VoteView.OnOptionClickListener, OnLoadMoreListener {
    // TODO 这里主要是评价后如何更新资料页的界面？有些位置加了todo,有些没有。
    // TODO 评分，评论等代码会有重复，待资料页完事后再统一整理一下
    public static final int ACTIVITY_RESULT_CODE_MOVIESENDCOMMENT = 6232;
    private VoteView voteView;
    private int voteHight;
    public TargetObjStatus statusBean;
    private TitleOfNormalView title;
    private IRecyclerView listView;
    private LoadMoreFooterView loadMoreFooterView;
    private final String uploadTime = "uploadTime";

    private MovieShortCommentListAdapter adapter;

    private String movieid;
    private String movietitle;
    private String movieNameCN;
    private String movieNameEN;
    //    private float rating;
    private int commentcount;

    private int pageIndex = 1;// 用户评论页码

    private RequestCallback movieCommentsCallback;
//    private RequestCallback uploadImageCallback;
//    private RequestCallback ratingMovieCallback;
//    private RequestCallback relatedObjStatusCallback;
    //    private RequestCallback movieHotCommentsCallback;
    private ShareCommentView shareCommentView;

    public V2_MovieCommentBean twitterExtra;
    private RatingItemsBean ratingBean;
    private RatingItemsBean tRating;

    private Handler uploadImageHandler;
    private String uploadImageUrl;

    private double tRe;
    private String tContent;
    private boolean tDeploySubitem;
    private boolean tIsShare;

    private List<ImageBean> upLoadImages;
    public static int lastSelectedPos;

    public static final String KEY_MOVIE_ID = "movie_id";
    public static final String KEY_MOVIE_NAME_CN = "MOVIE_NAME_CN";
    public static final String KEY_MOVIE_NAME_EN = "MOVIE_NAME_EN";
    public static final String KEY_MOVIE_COMMENT_TITLE = "MOVIE_COMMENT_TITLE";
    public static final String KEY_MOVIE_COMMENT_COUNT = "COMMENTS_COUNT";
    public static final String KEY_MOVIE_RATING_VALUE = "RATING_VALUE";
    private static final String TAG_PRAISE = "movie_short_comment_praise";

    private boolean mWriteComment;
    private PraiseCommentApi mPraiseCommentApi;

    @Override
    protected void onInitVariable() {
        movieid = getIntent().getStringExtra(KEY_MOVIE_ID);
        movietitle = getIntent().getStringExtra(KEY_MOVIE_COMMENT_TITLE);
        movieNameCN = getIntent().getStringExtra(KEY_MOVIE_NAME_CN);
        movieNameEN = getIntent().getStringExtra(KEY_MOVIE_NAME_EN);
//        rating = getIntent().getFloatExtra("RATING_VALUE", 0);
        lastSelectedPos = -1;
        // TODO 临时使用一下，等光顺来定如何传递对象

        setPageLabel("movieCommentsList");

        mPraiseCommentApi = new PraiseCommentApi();
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.movie_short_comments);
        shareCommentView = findViewById(R.id.share_comment);
        shareCommentView.setActivity(this);
        shareCommentView.setBasicData(movieid, movieNameCN, movieNameEN);
        title = new TitleOfNormalView(this, findViewById(R.id.navigation), BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, null,
                null);
        commentcount = getIntent().getIntExtra("COMMENTS_COUNT", 0);
        title.setTitleText(String.format("短评(%d)", commentcount));

        BottomOfMovieCommentsView bottomBarView = new BottomOfMovieCommentsView(this, findViewById(R.id.movie_comment), null, new BottomOfMovieCommentsView.IBottomViewActListener() {

            @Override
            public void onEvent(BottomOfMovieCommentsView.BottomViewActionType type, String contents) {
                if (BottomOfMovieCommentsView.BottomViewActionType.TYPE_MOVIE_COMMENTS_HINT_CLICK == type) {
                    // 写短评
                    IPublishProvider provider = ProviderExtKt.getProvider(IPublishProvider.class);
                    provider.startEditorActivity(
                            CONTENT_TYPE_FILM_COMMENT,
                            null,
                            null,
                            Long.parseLong(movieid),
                            movieNameCN,
                            null,
                            null,
                            false,
                            false
                    );
                    mWriteComment = true;
                    return;
                }
                if (TextUtils.isEmpty(contents)) {
                    MToastUtils.showShortToast("请输入评论内容");
                    return;
                }
            }
        });
        bottomBarView.setMsgToInvoker(true);

        listView = findViewById(R.id.listview);
        listView.setLayoutManager(new LinearLayoutManager(this));
        loadMoreFooterView = (LoadMoreFooterView) listView.getLoadMoreFooterView();
        adapter = new MovieShortCommentListAdapter(this, movietitle);
        adapter.showTypeView(true);

        voteView = new VoteView(this);
        voteView.setOptionClickListener(this);
        listView.addHeaderView(voteView);
        listView.setIAdapter(adapter);
        initVoteView(null);
        listView.setOnLoadMoreListener(this);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    int tweetId = adapter.getCommentList().get(position).getTweetId();
                    if (0 == tweetId) {
                        return;
                    }

                    lastSelectedPos = position;
                    try {
                        App.getInstance().isMoviePraised = adapter.getCommentList().get(position).getIsPraise();
                        App.getInstance().totalMoviePraise = adapter.getCommentList().get(position).getTotalPraise();
                    } catch (Exception e) {

                    }
                    Intent intent = new Intent();
                    intent.putExtra("tweetId", tweetId);
                    intent.putExtra("title", movietitle);
                    intent.putExtra("assist_num", adapter.getCommentList().get(position)
                            .getTotalPraise());
                    intent.putExtra("reply_num", adapter.getCommentList().get(position)
                            .getCommentCount());
                    intent.putExtra("isassist", adapter.getCommentList().get(position).getIsPraise());
                    intent.putExtra("twitter_type", TwitterActivity.TWITTERTYPE_MOIVE);
                    startActivityForResult(TwitterActivity.class, intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        this.initRate();
    }

    @Override
    protected void onInitEvent() {
        movieCommentsCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                listView.setRefreshing(false);
//                MovieHotCommensBean bean = (MovieHotCommensBean) o;
                requestPariseInfosByRelatedIds((V2_MovieCommentAllBean) o);
//                if (null != bean && null != bean.getMini() && null != bean.getMini().getList() &&
//                        !bean.getMini().getList().isEmpty()) {
//                    adapter.setCommentCount(bean.getMini().getTotal());
//                    final List<MovieCommentsMiniItem> comments = bean.getMini().getList();
//                    if (bean.getMini().getTotal() <= 0) {
//                        pageIndex = 1;
//                        adapter.clearCommentList();
//                    }
//
//                    if ((comments.size() > 0)) {
//                        adapter.addCommentList(comments);
//                    }
//                    if (adapter.getItemCount() >= bean.getMini().getTotal()) {
//                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
//                    }
//                } else {
//                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
//                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                listView.setRefreshing(false);
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                --pageIndex;
                if (pageIndex < 1) {
                    pageIndex = 1;
                }
            }
        };
//        movieHotCommentsCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                V2_MovieCommentAllBean hotCommentBean=(V2_MovieCommentAllBean) o;
//                if(hotCommentBean!=null&&hotCommentBean.getCts().size()>0){
//                    adapter.addHotCommentList(hotCommentBean.getCts());
//                }
//                pageIndex = 1;
//                requestMovieComments(pageIndex);
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                pageIndex = 1;
//                requestMovieComments(pageIndex);
//            }
//        };
//        relatedObjStatusCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//
//                statusBean = (TargetObjStatus) o;
//                if (statusBean == null) {
//                    return;
//                }
//                ratingBean = statusBean.getRatingItems();
//                double rating = (double) statusBean.getRating();
//                if (ratingBean != null) {
//                    rateView.setValues(ratingBean.getrOther(), ratingBean.getrPicture(),
//                            ratingBean.getrDirector(), ratingBean.getrStory(),
//                            ratingBean.getrShow(), ratingBean.getrTotal(), rating);
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//            }
//        };
//        ratingMovieCallback = new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                rateView.setRateGridImgs(null);
//                CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
//                MToastUtils.showShortToast("评分发送失败:" + e.getLocalizedMessage());
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                RatingResultJsonBean result = (RatingResultJsonBean) o;
//                if (null != result && null != result.getError() && !result.getError().trim().equals("")) {
//                    MToastUtils.showShortToast(result.getError());
//                    return;
//                }
//                MToastUtils.showShortToast("评分提交成功");
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MovieShortCommentsActivity.this);
//                prefs.edit().putLong(uploadTime, System.currentTimeMillis() / 1000).apply();
//                setResult(ACTIVITY_RESULT_CODE_MOVIESENDCOMMENT);
//                CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
//                List<ImageBean> tImages = rateView.getImgs();//评论发送成功后用于假写评论的图片
//                rateView.setRateGridImgs(null);
//
//                // TODO 如何通知资料页去更新界面？
//                V2_MovieCommentBean bean = new V2_MovieCommentBean();
//                bean.setTweetId(0);
//                bean.setCa(AccountManager.getUserNickName());
//                bean.setCaimg(AccountManager.getUserAvatar());
////                bean.setCommentDate((int) (System.currentTimeMillis() / 1000));
//                bean.setCd(System.currentTimeMillis() / 1000);
//                bean.setCe(tContent);
//                bean.setCr(String.valueOf(tRe));
//                if (adapter != null) {
//                    adapter.addCommentToFirst(bean, tImages);
//                }
//                if (null != tRating) {
//                    rateView.setValues(tRating.getrOther(), tRating.getrPicture(), tRating.getrDirector(),
//                            tRating.getrStory(), tRating.getrShow(), tRating.getrTotal()
//                            , tRe
//                    );
//                    ratingBean = tRating;
//                    tRating = null;//临时使命完成
//                }
//                if (statusBean != null) {
//                    statusBean.setRating((float) tRe);
//                }
//
//                // 在这里更新数量, 直接加是否合适呢？
//                title.setTitleText(String.format("短评(%d)", ++commentcount));
//
//                // TODO  如何通知资料页评分？还是回去后更新整个页面？
//
//                if (TextUtils.isEmpty(tContent) && null != rateView) {
//                    shareCommentView.setCommentData((float) tRe, rateView.getDefContent());
//                } else {
//                    shareCommentView.setCommentData((float) tRe, tContent);
//                }
//                shareCommentView.setVisibility(View.VISIBLE);
//
//            }
//        };


//        uploadImageCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                final UploadImageURLBean bean = (UploadImageURLBean) o;
//                if (TextUtils.isEmpty(bean.getUploadImageUrl())) {
//                    MToastUtils.showShortToast(getString(R.string.st_upload_comment_photo_failed));
//                    requestRatingMovie(null);
//                    return;
//                }
//
//                Message msg = uploadImageHandler.obtainMessage();
//                msg.what = 1;
//                uploadImageUrl = bean.getUploadImageUrl();
//                uploadImageHandler.sendMessage(msg);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                requestRatingMovie(null);
//                MToastUtils.showShortToast(getString(R.string.st_upload_comment_photo_failed));
//            }
//        };

        // 这上传图片逻辑垃圾
//        uploadImageHandler = new Handler() {
//            public void handleMessage(final Message msg) {
//
//                switch (msg.what) {
//                    case 1:
//                        CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
//                        String filePath = "";
//                        if (upLoadImages != null && upLoadImages.size() > 0) {
//                            filePath = upLoadImages.get(0).path;
//                            File file = new File(upLoadImages.get(0).path);
//                            if (file.exists()) {//保证文件存在才上传
//                                if (file.length() > 512000) {//如果文件大于500k，就取几兆的2倍。比如该图片是2.5M，那么就是(2+1)*2=4,产品出的垃圾需求
//                                    int mSize = (((int) (file.length() / 1024 / 1024)) + 1) << 1;
//                                    Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(
//                                            filePath, mSize, new NativeImageLoader.ImageLoaderCallBack() {
//
//                                                @Override
//                                                public void onImageLoader(Bitmap bitmap, String path) {//如果返回的bitmap对象为空才执行
//                                                    File file = null;
//                                                    String tmpFilePath = "";
//                                                    try {
//                                                        file = saveFile(bitmap);
//                                                    } catch (Exception e) {
//
//                                                    }
//                                                    if (file != null && file.exists()) {
//                                                        tmpFilePath = file.getPath();
//                                                    } else {
//                                                        tmpFilePath = upLoadImages.get(0).path;
//                                                    }
//                                                    setUpLoadImageHandler(tmpFilePath);
//                                                }
//                                            });
//                                    if (bitmap != null) {//不执行ImageLoaderCallBack
//                                        File fl = null;
//                                        try {
//                                            fl = saveFile(bitmap);
//                                        } catch (Exception e) {
//
//                                        }
//                                        if (fl != null && fl.exists()) {
//                                            filePath = fl.getPath();
//                                        } else {
//                                            filePath = upLoadImages.get(0).path;
//                                        }
//                                        setUpLoadImageHandler(filePath);
//                                    }
//                                } else {
//                                    setUpLoadImageHandler(filePath);
//                                }
//
//                            } else {//没有有效的图片，直接上传评分
//                                requestRatingMovie(null);
//                            }
//                        } else {//没有有效的图片，直接上传评分
//                            requestRatingMovie(null);
//                        }
//                        break;
//                    case 2:
//                        String headerImagePath = (String) msg.obj;
//                        if (TextUtils.isEmpty(headerImagePath)) {
//                            MToastUtils.showShortToast(getString(R.string.st_upload_comment_photo_failed));
//                            requestRatingMovie(null);
//                            return;
//                        }
//                        int pos = headerImagePath.indexOf("List");
//                        if (pos > 0) {
//                            headerImagePath = headerImagePath.replace("List", "resultList");
//                        }
//
//                        UploadResultBean b = (UploadResultBean) Utils.handle(headerImagePath, UploadResultBean.class);
//                        if (b == null) {
//                            MToastUtils.showShortToast(getString(R.string.st_upload_comment_photo_failed));
//                            requestRatingMovie(null);
//                        }
//                        List<ResultList> listData = b.getResult();
//                        if (listData != null && listData.size() > 0 && null != listData.get(0)) {
//                            String uploadID = listData.get(0).getUploadId();
//                            requestRatingMovie(uploadID);
//                            return;
//                        } else {
//                            MToastUtils.showShortToast(getString(R.string.st_upload_comment_photo_failed));
//                            requestRatingMovie(null);
//                        }
//
//                        break;
//
//                    default:
//                        break;
//                }
//            }
//        };
    }

    private void requestPariseInfosByRelatedIds(final V2_MovieCommentAllBean beans) {
        if (beans == null || beans.getCts() == null) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
            return;
        }
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
        title.setTitleText(String.format("短评(%d)", beans.getTotalCount()));
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < beans.getCts().size(); i++) {
            if (beans.getCts().get(i) != null) {
                sb.append(beans.getCts().get(i).getTweetId());
            }
            if (i < beans.getCts().size() - 1) {
                sb.append(",");
            }

        }

        String ids = sb.toString();
        if (ids.length() < 1 && adapter != null) {
            adapter.addCommentList(beans.getCts());
            return;
        }
        // 批量查询点赞点踩状态
        mPraiseCommentApi.getPraiseStatList(TAG_PRAISE, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                ids, new NetworkManager.NetworkListener<PraiseStateList>() {
            @Override
            public void onSuccess(PraiseStateList result, String showMsg) {
                if(result == null || CollectionUtils.isEmpty(result.getList())) {
                    setCommentsPraisedFail(beans);
                    return;
                }
                setCommentsPraisedSuccess(beans, result.getList());
            }

            @Override
            public void onFailure(NetworkException<PraiseStateList> exception, String showMsg) {

            }
        });

        // TODO: 2020/10/21 页面数据由于迁移接口字段没有兼容，显示不出来，还未联调，联调后再删除旧代码 

//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("ids", ids);
//        parameterList.put("relatedObjType", "78");
//        HttpUtil.post(ConstantUrl.PARISE_INFOS_BY_RELATEDIDS, parameterList, PariseInfosByRelatedIdsBean.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                setCommentsPraisedFail(beans);
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                PariseInfosByRelatedIdsBean bean = (PariseInfosByRelatedIdsBean) o;
//                List<ReviewParis> paris = bean.getReviewParises();
//                if (null == paris) {
//                    setCommentsPraisedFail(beans);
//                    return;
//                }
//                for (int i = 0; i < paris.size(); i++) {
//                    beans.getCts().get(i).setIsPraise(paris.get(i).getIsPraise());
//                    beans.getCts().get(i).setTotalPraise(paris.get(i).getTotalPraise());
//                }
//                setCommentsPraisedSuccess(beans, paris);
//            }
//        });
    }

    private void setCommentsPraisedFail(final V2_MovieCommentAllBean beans) {
        adapter.setCommentCount(beans.getTotalCount());
        final List<V2_MovieCommentBean> comments = beans.getCts();
        if (beans.getTotalCount() <= 0) {
            pageIndex = 1;
            adapter.clearCommentList();
        }

        if ((comments.size() > 0)) {
            adapter.addCommentList(comments);
        }

    }

    private void setCommentsPraisedSuccess(final V2_MovieCommentAllBean beans, final List<PraiseState> paris) {
        if (beans.getCts() == null) {
            setCommentsPraisedFail(beans);
            return;
        }

        if (beans.getTotalCount() <= 0) {
            pageIndex = 1;
            adapter.clearCommentList();
        }
        for (int i = 0; i < paris.size(); i++) {
            // 当前用户点赞、点踩状态：1.点赞 2.点踩 null.无  -1.当前用户未登录
            boolean isPraise = paris.get(i).getCurrentUserPraise() != null
                    && paris.get(i).getCurrentUserPraise() == PraiseState.CURRENT_USER_PRAISE_STAT_PRAISE;
            int totalPraise = Integer.parseInt(String.valueOf(paris.get(i).getUpCount()));
            beans.getCts().get(i).setIsPraise(isPraise);
            beans.getCts().get(i).setTotalPraise(totalPraise);
        }
        adapter.setCommentCount(beans.getTotalCount());
        adapter.addCommentList(beans.getCts());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWriteComment){
            mWriteComment = false;
            adapter.clearCommentList();
            onRequestData();
        }
    }

    @Override
    protected void onRequestData() {
        pageIndex = 1;

        // Movie/VoteUrl.api 没有对应的迁移接口，ios线上也没有这个接口的请求，所以直接不显示
        initVoteView(null);
//        requestVotes();

        UIUtil.showLoadingDialog(this);
        requestMovieComments(pageIndex);
//        requestMovieHotComments(1);
//        requestRelatedObjStatus();
    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPraiseCommentApi != null) {
            mPraiseCommentApi.cancelAllTags();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == TwitterActivity.ACTIVITY_RESULT_CODE_MOVIECOMMENT && lastSelectedPos != -1) {
            adapter.getCommentList().get(lastSelectedPos).setIsPraise(App.getInstance().isMoviePraised);
            adapter.getCommentList().get(lastSelectedPos).setTotalPraise(App.getInstance().totalMoviePraise);
            adapter.notifyDataSetChanged();

        }
        if (requestCode == 3 && UserManager.Companion.getInstance().isLogin()) {
            if (twitterExtra != null) {
                if (twitterExtra.getTweetId() == 0) {
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("tweetId", twitterExtra.getTweetId());
                intent.putExtra("title", movietitle);
                intent.putExtra("assist_num", twitterExtra.getTotalPraise());
                intent.putExtra("reply_num", twitterExtra.getCommentCount());
                intent.putExtra("isassist", twitterExtra.getIsPraise());
                intent.putExtra("twitter_type", TwitterActivity.TWITTERTYPE_MOIVE);
                intent.putExtra(TwitterActivity.EXTRAS_KEY_SHOWINPUT, twitterExtra.getCommentCount() == 0);
                startActivityForResult(TwitterActivity.class, intent);
            }
        }/* else if (resultCode == PictureSelectActivity.ACTIVITY_RESULT_CODE_PICTURESELECT && rateView != null && data != null) {
            NativeImageLoader.getInstance().cleanBitmap();
            List<ImageBean> images = (List<ImageBean>) data
                    .getSerializableExtra(PictureSelectActivity.IMAGESEXTRA);
            rateView.setRateGridImgs(images);
        }*/

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initRate() {

//        View root = this.findViewById(R.id.movie_rate);
//        root.setFocusable(true);
//        root.setEnabled(true);
//        root.setClickable(true);
        setSwipeBack(true);
//        rateView = new MovieRateView(this, root, movieid, new MovieRateView.IMovieRateViewListener() {
//
//            @Override
//            public void onEvent(MovieRateView.MovieRateViewEventType type, List<ImageBean> images, final double re, final int rateChangedMusic,
//                                final int rateChangedGeneral, final int rateChangedDirector, final int rateChangedStory, final int rateChangedPerform,
//                                final int rateChangedImpressions, final String content, final boolean deploySubitem, final boolean share) {
//                if (MovieRateView.MovieRateViewEventType.TYPE_OK == type) {
//                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MovieShortCommentsActivity.this);
//                    long lastUploadTime = prefs.getLong(uploadTime, 0L);
//                    if (0 != lastUploadTime && (System.currentTimeMillis() / 1000) - lastUploadTime < 20) {
//                        MToastUtils.showShortToast("您发布的速度太快了，休息一下吧");
//                        return;
//                    }
//                    UIUtil.showLoadingDialog(MovieShortCommentsActivity.this, false);//发送评论过程中不让返回
//                    upLoadImages = images;
//                    tRe = re;
//
//                    tRating = new RatingItemsBean();
//                    tRating.setrDirector(rateChangedDirector);
//                    tRating.setrOther(rateChangedMusic);
//                    tRating.setrPicture(rateChangedGeneral);
//                    tRating.setrShow(rateChangedPerform);
//                    tRating.setrStory(rateChangedStory);
//                    tRating.setrTotal(rateChangedImpressions);
//
//                    tContent = content;
//                    tDeploySubitem = deploySubitem;
//                    tIsShare = share;
//                    uploadImageUrl = "";
//                    setSwipeBack(true);
//                    HttpUtil.get(ConstantUrl.UPLOAD_IAMGE_URL, UploadImageURLBean.class, uploadImageCallback);// 这上传图片流程垃圾
//                }
//                if (MovieRateView.MovieRateViewEventType.TYPE_CLOSE == type) {
//                    if (ratingBean != null && statusBean != null) {
//                        rateView.setValues((int) ratingBean.getrOther(), (int) ratingBean.getrPicture(),
//                                (int) ratingBean.getrDirector(), (int) ratingBean.getrStory(),
//                                (int) ratingBean.getrShow(), (int) ratingBean.getrTotal(), statusBean.getRating());
//                        rateView.setRateGridImgs(null);
//                    }
//                    setSwipeBack(true);
//                }
//            }
//        });


//        rateView.setVisibility(View.INVISIBLE);
    }

    private void requestMovieComments(int pageIndex) {
        // Showtime/HotMovieComments.api?movieId={0}&pageIndex={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("movieId", movieid);
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_MOVIE_COMMENTS, param, V2_MovieCommentAllBean.class, movieCommentsCallback);
    }

    private void requestVotes() {
        // 执行投票页面的请求
        if (TextUtils.isEmpty(this.movieid)) {
            return;
        }

        // Movie/VoteUrl.api?movieId={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("movieId", movieid);
        HttpUtil.get(ConstantUrl.GET_MOVIE_VOTE, param, null, new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                LogWriter.d("checkVote", "request vote failed:" + e.getLocalizedMessage());
                initVoteView(null);
            }

            @Override
            public void onSuccess(Object o) {
                LogWriter.d("checkVote", "request success");
                initVoteView((MovieVoteResultBean) o);

            }
        }, 0, MovieVoteResultBean.class);
    }

    public void initVoteView(MovieVoteResultBean bean) {
        boolean isShowVoteView = false;
        if (null != voteView) {
            if (0 == voteView.getMeasuredHeight()) {
                voteView.measure(View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0,
                        View.MeasureSpec.UNSPECIFIED));
                if (voteView.getMeasuredHeight() != 0) {
                    voteHight = voteView.getMeasuredHeight();
                }
            }
            if (null != bean && null != bean.getData() && null != bean.getData().getList() && bean.getData().getList().size() > 0) {
                List<MovieVoteBean> objs = bean.getData().getList();

                if (objs != null && objs.size() > 0) {
                    for (MovieVoteBean item : objs) {
                        if (2 == item.getType() && null != item.getOptions() && item.getOptions().size() > 0) {
                            // TODO 显示出来投票页面
                            LogWriter.d("checkVote", "init vote view");
                            voteView.addVoteData(item.getOptions().get(0));
                            voteView.setLabel(item.getTag(), true);
                            isShowVoteView = true;
                            break;
                        }
                    }

                }

            }
        }
        if (isShowVoteView) {
            voteView.setVisibility(View.VISIBLE);
            voteView.setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.offset_pxtodx_60));
        } else {
            voteView.setVisibility(View.GONE);
            voteView.setPadding(0, -voteHight, 0, 0);
        }
    }

//    private void setCommentsPraisedFail(final V2_MovieCommentAllBean beans) {
//        adapter.setCommentCount(beans.getTotalCount());
//        final List<MovieCommentsMiniItem> comments = beans.getCts();
//        if (beans.getTotalCount() <= 0) {
//            pageIndex = 1;
//            adapter.clearCommentList();
//        }
//
//        if ((comments.size() > 0)) {
//            adapter.addCommentList(comments);
//        }
//
//    }

//    private void setCommentsPraisedSuccess(final V2_MovieCommentAllBean beans, final List<ReviewParis> paris) {
//        if (paris == null || beans.getCts() == null) {
//            setCommentsPraisedFail(beans);
//            return;
//        }
//
//        if (beans.getTotalCount() <= 0) {
//            pageIndex = 1;
//            adapter.clearCommentList();
//        }
//
//        for (int i = 0; i < paris.size(); i++) {
//            beans.getCts().get(i).setIsPraise(paris.get(i).getIsPraise());
//            beans.getCts().get(i).setTotalPraise(paris.get(i).getTotalPraise());
//        }
//        adapter.setCommentCount(beans.getTotalCount());
//        adapter.addCommentList(beans.getCts());
//    }

    //上传图片部分，垃圾垃圾，流程超级垃圾
//    private void setUpLoadImageHandler(final String imgPath) {
//        if (TextUtils.isEmpty(imgPath) || TextUtils.isEmpty(uploadImageUrl)) {
//            requestRatingMovie(null);
//            return;
//        }
//        new Thread() {//图片上传单起一个线程
//            @Override
//            public void run() {
//                String headImagePath = "";
//                try {
//                    headImagePath = uploadImages(imgPath, "UploadImage", uploadImageUrl);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (TextUtils.isEmpty(headImagePath)) {
//                    requestRatingMovie(null);
//                    return;
//                }
//                Message msg = uploadImageHandler.obtainMessage();
//                msg.what = 2;
//                msg.obj = headImagePath;
//                uploadImageHandler.sendMessage(msg);
//            }
//
//        }.start();
//
//    }

//    /**
//     * 将输入流解析为String
//     *
//     * @param is
//     * @return
//     * @throws Exception
//     */
//    private String convertStreamToString(InputStream is) throws Exception {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//        StringBuilder sb = new StringBuilder();
//        String line = null;
//        try {
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//                line = null;
//            }
//        } finally {
//            try {
//                if (reader != null) {
//                    reader.close();
//                    reader = null;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        line = sb.toString();
//        sb = null;
//        return line;
//    }

//    private String uploadImages(String file, String type, String uploadURL) throws Exception {
//        Map<String, String> params = new ArrayMap<String, String>(3);
//        params.put("UploadType", String.valueOf(1));
//        params.put("imageClipType", String.valueOf(2));
//        params.put("ImageFileType", type);
//        UploadPicture.FormFileBean fie = new UploadPicture.FormFileBean("temp", TextUtil.getFileContent(file), "image", "multipart/form-data");
//        String str = uploadImage(uploadURL, params, fie);
//        return str;
//    }

//    /**
//     * 上传图片
//     *
//     * @param actionUrl 上传路径
//     * @param params    请求参数 key为参数名,value为参数值
//     * @param file      上传文件
//     */
//    private String uploadImage(String actionUrl, Map<String, String> params, UploadPicture.FormFileBean file) throws Exception {
//
//        final String BOUNDARY = "---------7d4a6d158c9"; // 数据分隔线
//        URL url = new URL(actionUrl);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        // 上传的表单参数部分
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
//            sb.append("--");
//            sb.append(BOUNDARY);
//            sb.append("\r\n");
//            sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n\r\n");
//            sb.append(entry.getValue());
//            sb.append("\r\n");
//        }
//        conn.setDoInput(true);// 允许输入
//        conn.setDoOutput(true);// 允许输出
//        conn.setUseCaches(false);// 不使用Cache
//        conn.setRequestMethod("POST");
////        conn.setRequestProperty("Connection", "Keep-Alive");
//        conn.setInstanceFollowRedirects(true);
//        conn.setRequestProperty("Content-Type", file.getContentType() + "; boundary=" + BOUNDARY);
//        conn.setRequestProperty("Accept-Charset", "UTF-8,*");
//        conn.setRequestProperty("Accept-Language", "zh-cn");
//        conn.setRequestProperty("User-Agent", FrameConstant.UA_STR);
//        conn.setRequestProperty("Cache-Control", "no-cache");
//        // 设置连接主机超市（单位：毫秒）
//        conn.setConnectTimeout(5000);
//        // 设置从主机读取数据超时（单位：毫秒）
//        conn.setReadTimeout(5000);
//        // Map<String, List<String>> map1=conn.getRequestProperties();
//        conn.connect();
//        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
//        outStream.write(sb.toString().getBytes());// 发送表单字段数据
//        // 上传的图片部分
//        StringBuilder split = new StringBuilder();
//        split.append("--");
//        split.append(BOUNDARY);
//        split.append("\r\n");
//        split.append("Content-Disposition: form-data;name=\"").append(file.getFormname()).append("\";filename=\"")
//                .append(file.getFilname()).append("\"\r\n");
//        split.append("Content-Type: ").append(file.getContentType()).append("\r\n\r\n");
//        outStream.write(split.toString().getBytes());
//        outStream.write(file.getData(), 0, file.getData().length);
//        outStream.write("\r\n".getBytes());
//
//        byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
//        outStream.write(end_data);
//        outStream.flush();
//        int cah = conn.getResponseCode();
//        if (cah != HttpURLConnection.HTTP_OK) {
//            throw new Exception("Mtime:POST request not data :" + actionUrl);
//        }
//        InputStream is = conn.getInputStream();
//        String jsonVale = convertStreamToString(is);
//        outStream.close();
//        conn.disconnect();
//        return jsonVale;
//    }

//    private File saveFile(Bitmap bm) throws Exception {
//        CacheManager.getInstance().getFileCache().clearSpeFolder(FileCache.CACHE_TEMP_PIC_PATH);
//        File tempFile = new File(FileCache.CACHE_TEMP_PIC_PATH);
//        if (!tempFile.exists()) {
//            tempFile.mkdirs();
//        }
//        File saveFile = new File(tempFile, "sendTempFile.jpg");
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(saveFile));
//        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//        bos.flush();
//        bos.close();
//        return saveFile;
//    }

    // 发送评分
//    private void requestRatingMovie(final String uploadID) {
//        if (tRating == null) {
//            UIUtil.dismissLoadingDialog();
//            return;
//        }
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(10);
//        parameterList.put("movieid", movieid);
//        parameterList.put("r", tDeploySubitem ? "0" : String.valueOf((int) tRe));
//        parameterList.put("ir", String.valueOf(tRating.getrTotal()));
//        parameterList.put("str", String.valueOf(tRating.getrStory()));
//        parameterList.put("shr", String.valueOf(tRating.getrShow()));
//        parameterList.put("dr", String.valueOf(tRating.getrDirector()));
//        parameterList.put("pr", String.valueOf(tRating.getrPicture()));
//        parameterList.put("mr", String.valueOf(tRating.getrOther()));
//        parameterList.put("c", tContent);
//        if (!TextUtils.isEmpty(uploadID)) {
//            parameterList.put("ip", uploadID);
//        }
//        HttpUtil.post(ConstantUrl.RATING_MOVIE, parameterList, RatingResultJsonBean.class, ratingMovieCallback);
//    }

//    private void requestRelatedObjStatus() {
//        // Showtime/GetRelatedObjStatus.api?relateType={0}&relateId={1}
//        Map<String, String> param = new HashMap<>(2);
//        param.put("relateType", String.valueOf(App.getInstance().TARGET_OBJ_TYPE_MOVIE));
//        param.put("relateId", movieid);
//        HttpUtil.get(ConstantUrl.GET_RELATED_OBJ_STATUS, param, TargetObjStatus.class, relatedObjStatusCallback);
//    }


    @Override
    public void onOptionClick(int topicId, VoteDataOptionBean optionBean) {
        if (!UserManager.Companion.getInstance().isLogin()) {
//            MovieShortCommentsActivity.this.startActivityForResult(LoginActivity.class, new Intent());
            UserLoginKt.gotoLoginPage(MovieShortCommentsActivity.this, null, 0);
            return;
        }
        submitVote(topicId, optionBean);
    }

    private void submitVote(int topicId, final VoteDataOptionBean optionBean) {
        if (optionBean.getIsSelf()) {//如果已经投过票了，就不请求接口
            return;
        }
        UIUtil.showLoadingDialog(this);
        // utility/vote.api?qId={0}&answer={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("qId", String.valueOf(topicId));
        param.put("answer", String.valueOf(optionBean.getTopicOptionId()));
        HttpUtil.get(ConstantUrl.GET_SUBMIT_VOTE, param, SubmitVoteBean.class, new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("提交投票失败:" + e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                SubmitVoteBean voteBean = (SubmitVoteBean) o;
                if (null == voteBean || voteBean.getCode() != 1) {
                    if (null != voteBean && !TextUtils.isEmpty(voteBean.getShowMsg())) {
                        MToastUtils.showShortToast(voteBean.getShowMsg());
                    }
                    return;
                }
                if (null != voteBean.getData()) {
                    switch (voteBean.getData().getBzCode()) {
                        case 1://表示成功
                            requestVotes();
                            break;
                        case 2://必须登陆
                            MToastUtils.showShortToast("未登录");
                            break;
                        case 3://重复投票
                            break;
                        case 0://其他错误
                            break;
                    }
                }

            }
        }, 180000);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (loadMoreFooterView.canLoadMore()) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            ++pageIndex;
            requestMovieComments(pageIndex);
        }
    }

    public static void launch(Context context, String refer, String movieId, String name, String nameEn, String title, int commentCount) {
        Intent intent = new Intent(context, MovieShortCommentsActivity.class);
        intent.putExtra(KEY_MOVIE_ID, movieId);
        intent.putExtra(KEY_MOVIE_NAME_CN, name);
        intent.putExtra(KEY_MOVIE_NAME_EN, nameEn);
        intent.putExtra(KEY_MOVIE_COMMENT_TITLE, title);
        intent.putExtra(KEY_MOVIE_COMMENT_COUNT, commentCount);
        dealRefer(context, refer, intent);
        context.startActivity(intent);
    }


}
