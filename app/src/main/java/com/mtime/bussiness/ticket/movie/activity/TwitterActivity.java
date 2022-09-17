package com.mtime.bussiness.ticket.movie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.ticket.bean.TweetReply;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.ticket.movie.adapter.TwitterAdapter;
import com.mtime.bussiness.ticket.movie.adapter.TwitterAdapter.ITwitterAdapterListener;
import com.mtime.bussiness.ticket.movie.bean.TwitterAllBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.BottomViewActionType;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.IBottomViewActListener;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageURLManager;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.HashMap;
import java.util.Map;

/**
 * 微评 （影片,影人资料）
 */
public class TwitterActivity extends BaseActivity implements IBottomViewActListener, OnLoadMoreListener {
    private int twitterType;
    public static final int TWITTERTYPE_MOIVE = 0;
    public static final int TWITTERTYPE_ACTOR = 1;
    public static final int ACTIVITY_RESULT_CODE_MOVIECOMMENT = 6234;
    public static final int ACTIVITY_RESULT_CODE_ACTORCOMMENT = 6235;
    private String tweetId;
    public String tweetReplyId = "";
    private IRecyclerView listView;
    private LoadMoreFooterView loadMoreFooterView;
    private TwitterAdapter adapter;
    private int pageIndex = 1;
    private boolean queryFinished;

    private int reply_userid=0;
    private String titleStr;
    int mTotalPraise;
    int mTotalComment;
    boolean isPraise;

    private BottomOfMovieCommentsView bottomComments;

    private ImageView userHeader;
    private ImageView commentImg;
    private TextView userName;
    private TextView commentTime;
    private TextView userRate;
    private TextView commentContents;
    private TextView replyValue;
    private TextView commentCountName;

    private TextView praiseValue;
    private ImageView praiseIcon;
    private ImageView praiseAnimIcon;

    private Animation animation;
    private boolean hasInitialized;
    private TitleOfNormalView titleView;

    private RequestCallback getTwitterCallback;
    private RequestCallback sendCommentsCallback;
    private RequestCallback praiseCallback;
    private boolean showCommentInput = false;
    private boolean isNotifyList = false; // 是不是通知列表条转过来的
    private String mCeimg;

    public static final String EXTRAS_KEY_SHOWINPUT = "showCommentInput";
    private static final String KEY_TWEET_ID = "tweetId";
    private static final String KEY_TWITTER_TYPE = "twitter_type";
    private static final String KEY_TITLE = "title";
    private static final String TITLE_DEFAULT = "短评";
    private static final int TITLE_SIZE = 8;

    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        tweetId = String.valueOf(getIntent().getExtras().getInt(KEY_TWEET_ID));
        titleStr = getIntent().getExtras().getString(KEY_TITLE, "");
        mTotalPraise = getIntent().getExtras().getInt("assist_num");
        mTotalComment = getIntent().getExtras().getInt("reply_num");
        isPraise = getIntent().getExtras().getBoolean("isassist");
        twitterType = getIntent().getExtras().getInt(KEY_TWITTER_TYPE);
        isNotifyList = getIntent().getExtras().getBoolean("notifyList");

        showCommentInput = getIntent().getExtras().getBoolean(EXTRAS_KEY_SHOWINPUT, false);
        queryFinished = false;
        hasInitialized = false;

        if (ACTIVITY_RESULT_CODE_MOVIECOMMENT == twitterType) {
            setPageLabel("commentDetail");
        } else {
            setPageLabel("startCommentDetail");
        }
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_twitter);
        animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        String title = "";
        if (TextUtils.isEmpty(titleStr)) {
            title = TITLE_DEFAULT;
        } else {
            if(titleStr.length() > TITLE_SIZE) {
                titleStr = titleStr.substring(0, TITLE_SIZE) + "...";
            }
            title = String.format(getResources().getString(R.string.twitter_title), titleStr);
        }

        View navBar = this.findViewById(R.id.navigationbar);
        titleView = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, title, null);
        listView = this.findViewById(R.id.comments_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        loadMoreFooterView = (LoadMoreFooterView) listView.getLoadMoreFooterView();
        View bottom = this.findViewById(R.id.bottom_comment_view);
        bottomComments = new BottomOfMovieCommentsView(this, bottom, null, this);
        bottomComments.setHideAfterSend(false);
        commentCountName = findViewById(R.id.review_count);

        View header = View.inflate(this, R.layout.movie_short_comments_list_header, null);
        init(header);
        listView.addHeaderView(header);
        listView.setVisibility(View.INVISIBLE);
        listView.setOnLoadMoreListener(this);

        adapter = new TwitterAdapter(this, null, new ITwitterAdapterListener() {

            @Override
            public void onEvent(Object obj) {
                if (!UserManager.Companion.getInstance().isLogin()) {
//                    startActivityForResult(LoginActivity.class, new Intent());
                    UserLoginKt.gotoLoginPage(TwitterActivity.this, null, 0);
                    return;
                }
                TweetReply reply = (TweetReply) obj;
                bottomComments.setFocus();
                String value = String.format("回复@%s:", reply.getNickname());
                bottomComments.setComments(value);
                reply_userid = reply.getUserId();
            }
        });

        listView.setIAdapter(adapter);
    }

    @Override
    protected void onInitEvent() {

        getTwitterCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {
                if (showCommentInput) {
                    bottomComments.setFocus();
                    showCommentInput = false;
                }
                listView.setVisibility(View.VISIBLE);
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                UIUtil.dismissLoadingDialog();

                final TwitterAllBean allBean = (TwitterAllBean) o;
                if(null==allBean){
                    return;
                }
                if (isNotifyList) {
                    commentCountName.setVisibility(View.VISIBLE);
                    commentCountName.setText(allBean.getTotalCommentCountName() + " >");
                    commentCountName.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            final Intent intent = new Intent();
//                            intent.putExtra(App.getInstance().KEY_MOVIE_ID, String.valueOf(allBean.getId()));
//                            intent.putExtra("MOVIE_NAME_CN",TextUtils.isEmpty(allBean.getName()) ? "" : allBean.getName());
//                            startActivity(MovieShortCommentsActivity.class, intent);
                            JumpUtil.startMovieShortCommentActivity(TwitterActivity.this,"", String.valueOf(allBean.getId())
                                    ,TextUtils.isEmpty(allBean.getName()) ? "" : allBean.getName(),"","",0);
                        }
                    });
                }

                String title = allBean.getName();
                if (TextUtils.isEmpty(title)) {
                    titleView.setTitleText(TITLE_DEFAULT);
                } else {
                    if(title.length() > TITLE_SIZE) {
                        title = title.substring(0, TITLE_SIZE) + "...";
                    }
                    title = String.format(getResources().getString(R.string.twitter_title), title);
                    titleView.setTitleText(title);
                }

                if (!TextUtils.isEmpty(allBean.getError())) {
                    queryFinished = true;
                    MToastUtils.showShortToast("已经加载完全部数据");
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    return;
                }
                if (allBean.getReplyTweets().size() < 20 || adapter.getCount() >= allBean.getReplyTotalCount()) {// fix
                    // bug13147
                    // 解决方法如果该次数据不够20条则说明数据后台没有数据了
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                }
                setHeaderValues(allBean);
                if (pageIndex <= 2) {
                    adapter.cleanDatas();
                }
                adapter.setDatas(allBean.getReplyTweets());
            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
            }
        };

        praiseCallback = new RequestCallback() {

            @Override
            public void onFail(Exception e) {
                // show the tips to user and change the status now
                isPraise = !isPraise;
                mTotalPraise += isPraise ? 1 : -1;


                if (twitterType == TWITTERTYPE_MOIVE) {
                    App.getInstance().isMoviePraised = !App.getInstance().isMoviePraised;
                    App.getInstance().totalMoviePraise += App.getInstance().isMoviePraised ? 1 : -1;
                    TwitterActivity.this.setResult(ACTIVITY_RESULT_CODE_MOVIECOMMENT);
                } else {
                    App.getInstance().isPraised = !App.getInstance().isPraised;
                    App.getInstance().totalPraise += App.getInstance().isPraised ? 1 : -1;
                    TwitterActivity.this.setResult(ACTIVITY_RESULT_CODE_ACTORCOMMENT);
                }
                updatePraise();
            }

            @Override
            public void onSuccess(Object o) {
                AddOrDelPraiseLogBean bean = (AddOrDelPraiseLogBean) o;
                if (bean.isSuccess()) {
                    mTotalPraise = bean.getTotalCount();
                    isPraise = bean.isAdd();
                    updatePraise();
                }
                if (twitterType == TWITTERTYPE_MOIVE) {
                    TwitterActivity.this.setResult(ACTIVITY_RESULT_CODE_MOVIECOMMENT);
                } else {
                    TwitterActivity.this.setResult(ACTIVITY_RESULT_CODE_ACTORCOMMENT);
                }
            }
        };

        sendCommentsCallback = new RequestCallback() {

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("评论发送失败:"+e.getLocalizedMessage());
            }

            @Override
            public void onSuccess(Object o) {

                SuccessBean bean = (SuccessBean) o;
                if (Boolean.valueOf(bean.getSuccess())) {
                    TwitterActivity.this.setResult(ACTIVITY_RESULT_CODE_MOVIECOMMENT);
                    pageIndex = 1;
                    queryFinished = false;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    // Weibo/TweetCommentReplies.api?tweetId={0}&pageIndex={1}
                    Map<String, String> param = new HashMap<>(2);
                    param.put("tweetId", tweetId);
                    param.put("pageIndex", String.valueOf(pageIndex++));
                    HttpUtil.get(ConstantUrl.GET_TWITTER, param, TwitterAllBean.class, getTwitterCallback);
                    MToastUtils.showShortToast("评论发送成功");
                } else {
                    UIUtil.dismissLoadingDialog();
                    MToastUtils.showShortToast(bean.getError());
                }

            }

        };

    }

    @Override
    protected void onLoadData() {
    }

    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);
        Map<String, String> param = new HashMap<>(2);
        param.put("tweetId", tweetId);
        param.put("pageIndex", String.valueOf(pageIndex++));
        HttpUtil.get(ConstantUrl.GET_TWITTER, param, TwitterAllBean.class, getTwitterCallback);
    }

    @Override
    protected void onUnloadData() {
    }

    @Override
    public void onEvent(BottomViewActionType type, String contents) {
        if (BottomViewActionType.TYPE_MOVIE_COMMENTS_HINT_CLICK == type) {
            return;
        }
        
        UIUtil.showLoadingDialog(this);

        Map<String, String> parameterList = new ArrayMap<String, String>(4);
        parameterList.put("content", contents);
        parameterList.put("tweetId", tweetId);
        if (tweetReplyId != null) {
            parameterList.put("reponseId", tweetReplyId);
        }
        if (reply_userid != 0) {
            parameterList.put("userId", String.valueOf(reply_userid));
        }
        HttpUtil.post(ConstantUrl.GET_TWITTER_REPLY, parameterList, SuccessBean.class, sendCommentsCallback);
    }

    private void init(final View root) {
        userHeader = root.findViewById(R.id.header);
        commentImg = root.findViewById(R.id.comment_img);
        this.userName = root.findViewById(R.id.name);
        this.commentTime = root.findViewById(R.id.time);
        this.userRate = root.findViewById(R.id.rate);
        this.commentContents = root.findViewById(R.id.content);
        this.praiseAnimIcon = root.findViewById(R.id.praise_icon_animation);
        this.praiseIcon = root.findViewById(R.id.praise_icon);
        this.praiseValue = root.findViewById(R.id.praise);
        this.replyValue = root.findViewById(R.id.reply);
        commentImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mCeimg)) {
//                    Intent intent = new Intent();
//                    intent.putExtra(CommentImageDetailActivity.KEY_IMAGE_PATH, ceimg);
//                    startActivity(CommentImageDetailActivity.class, intent);
                    JumpUtil.startCommentImageDetailActivity(TwitterActivity.this, mCeimg);
                }
            }
        });

        View praiseView = root.findViewById(R.id.praise_region);
        praiseView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!UserManager.Companion.getInstance().isLogin()) {
//                    Intent intent = new Intent();
//                    ((BaseActivity) (arg0.getContext())).startActivityForResult(LoginActivity.class, intent);
                    UserLoginKt.gotoLoginPage(arg0.getContext(), null, 0);
                    return;
                }

                isPraise = !isPraise;
                mTotalPraise += isPraise ? 1 : -1;

                if (twitterType == TWITTERTYPE_MOIVE) {
                    App.getInstance().isMoviePraised = !App.getInstance().isMoviePraised;
                    App.getInstance().totalMoviePraise += App.getInstance().isMoviePraised ? 1 : -1;
                } else {
                    App.getInstance().isPraised = !App.getInstance().isPraised;
                    App.getInstance().totalPraise += App.getInstance().isPraised ? 1 : -1;
                }

                updatePraise();
                praiseAnimIcon.startAnimation(animation);

                Map<String, String> parameterList = new ArrayMap<String, String>(2);
                parameterList.put("id", tweetId);
                parameterList.put("relatedObjType", String.valueOf(78));
                HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, praiseCallback);
            }
        });

    }

    private void setHeaderValues(final TwitterAllBean values) {
        if (hasInitialized && (!(TextUtils.isEmpty(this.commentContents.getText())) || !(TextUtils.isEmpty(this.userName.getText())) || !(TextUtils.isEmpty(this.commentTime.getText())))) {
            return;
        }

        hasInitialized = true;
        volleyImageLoader.displayImage(values.getParentTweet().getUserImage(), userHeader, R.drawable.profile_default_head_h90, R.drawable.profile_default_head_h90, ImageURLManager.ImageStyle.THUMB, null);
        if (!TextUtils.isEmpty(values.getParentTweet().getCeimg())) {
            mCeimg = values.getParentTweet().getCeimg();
            commentImg.setVisibility(View.VISIBLE);
            volleyImageLoader.displayImage(mCeimg, commentImg, R.drawable.default_image, R.drawable.default_image, 460, 270,null);
        } else {
            commentImg.setVisibility(View.GONE);
        }

        this.userName.setText(values.getParentTweet().getNickname());
        // change the time value here
        String value = null;

        // time stamp
        long sendTime = values.getParentTweet().getStampTime() - 8*60*60L;
        if (0 == values.getParentTweet().getTweetId()) {
            sendTime += 8*60*60L;
        }
        long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;
        if (replyTime < 0) {
            long totalSeconds = System.currentTimeMillis() / 1000;
            replyTime = (totalSeconds - values.getParentTweet().getStampTime()) / 60;
        }
        if (replyTime < 0) {
            value = DateUtil.getLongToDate(DateUtil.sdf6, values.getParentTweet().getStampTime());
        } else if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        } else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        } else {
            value = DateUtil.getLongToDate(DateUtil.sdf6, values.getParentTweet().getStampTime());
        }

        this.commentTime.setText(value);

        // rate
        float rate = values.getParentTweet().getRating();
        if (rate > 0) {
            this.userRate.setText(String.format("%.1f", rate));
            if (rate >= 10) {
                this.userRate.setText("10");
            }
        } else {
            this.userRate.setVisibility(View.INVISIBLE);
        }

        // reply count
        int count = values.getReplyTotalCount() < 1 ? 0 : values.getReplyTotalCount();
        this.replyValue.setText(String.valueOf(count));

        // contents
        this.commentContents.setText(values.getParentTweet().getContent());

        this.mTotalPraise = values.getParentTweet().getTotalPraise();
        this.isPraise = values.getParentTweet().isPraise();
        // praise view
        this.updatePraise();
    }

    private void updatePraise() {
        if (this.isPraise) {
            this.praiseAnimIcon.setImageResource(R.drawable.assist2);
            this.praiseIcon.setImageResource(R.drawable.assist2);
            this.praiseValue.setTextColor(getResources().getColor(R.color.orange));
        } else {
            this.praiseValue.setTextColor(getResources().getColor(R.color.color_777777));
            this.praiseAnimIcon.setImageResource(R.drawable.assist1);
            this.praiseIcon.setImageResource(R.drawable.assist1);
        }

        String value;
        if (mTotalPraise < 1) {
            value = "赞";
        } else if (mTotalPraise < 1000) {
            value = String.valueOf(mTotalPraise);
        } else {
            value = "999+";
        }
        this.praiseValue.setText(value);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (loadMoreFooterView.canLoadMore()) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            if (queryFinished) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                return;
            }

            // Weibo/TweetCommentReplies.api?tweetId={0}&pageIndex={1}
            Map<String, String> param = new HashMap<String, String>(2);
            param.put("tweetId", tweetId);
            param.put("pageIndex", String.valueOf(pageIndex++));
            HttpUtil.get(ConstantUrl.GET_TWITTER, param, TwitterAllBean.class, getTwitterCallback);
        }
    }

    /**
     * 自己定义refer
     *
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer, int tweetId, int type, String title, int requestcode ) {
        Intent launcher = new Intent(context, TwitterActivity.class);
        launcher.putExtra(KEY_TWEET_ID, tweetId);
        launcher.putExtra(KEY_TWITTER_TYPE, type);
        launcher.putExtra(KEY_TITLE, title);
        dealRefer(context, refer, launcher);
        if (context instanceof Activity) {
            ((Activity)context).startActivityForResult(launcher, requestcode);
        } else {
            context.startActivity(launcher);
        }
    }

    public static void launch(Activity context, String refer, int tweetId, int twittertypeMoive, String title, int praiseCount, int replyCount, boolean praise, boolean showCommentInput) {
        Intent launcher = new Intent(context, TwitterActivity.class);
        launcher.putExtra(KEY_TWEET_ID, tweetId);
        launcher.putExtra(KEY_TWITTER_TYPE, twittertypeMoive);
        launcher.putExtra(KEY_TITLE, title);
        launcher.putExtra("assist_num", praiseCount);
        launcher.putExtra("reply_num", replyCount);
        launcher.putExtra("isassist", praise);
        launcher.putExtra(TwitterActivity.EXTRAS_KEY_SHOWINPUT,showCommentInput);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }
}
