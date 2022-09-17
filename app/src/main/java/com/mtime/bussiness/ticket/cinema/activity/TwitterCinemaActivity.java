package com.mtime.bussiness.ticket.cinema.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.bussiness.ticket.cinema.adapter.TwitterCinemaAdapter;
import com.mtime.bussiness.common.bean.AddOrDelPraiseLogBean;
import com.mtime.bussiness.ticket.cinema.bean.TopicAllBean;
import com.mtime.bussiness.ticket.cinema.bean.TopicParent;
import com.mtime.bussiness.ticket.cinema.bean.TopicReply;
import com.mtime.bussiness.ticket.movie.adapter.TwitterAdapter.ITwitterAdapterListener;
import com.mtime.common.utils.DateUtil;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.BottomViewActionType;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.IBottomViewActListener;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.ImageURLManager;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微评 （影院资料）
 * 
 */
public class TwitterCinemaActivity extends BaseActivity implements IBottomViewActListener, OnLoadMoreListener {
    
    private BottomOfMovieCommentsView bottomComments;
    private IRecyclerView             listView;
    private LoadMoreFooterView        loadMoreFooterView;
    
    private ImageView                 userHeader;
    private TextView                  userName;
    private TextView                  commentTime;
    private TextView                  userRate;
    private TextView                  commentContents;
    private TextView                  replyValue;
    
    private TextView                  praiseValue;
    private ImageView                 praiseIcon;
    private ImageView                 praiseAnimIcon;
    
    private String                    titleStr;

    private boolean                   queryFinished;
    private int                       pageIndex = 1;
    private String                    topicId;
    
    private Animation                 animation;
    private boolean                   hasInitialized;
    
    private RequestCallback twittersCallback;
    
    private TwitterCinemaAdapter      adapter;
    
    private String                    userContents;
    
    private List<TopicReply>          replies;
    private RequestCallback           releseCallback;

    private int replyCount;

    public static final String KEY_TOPIC_ID = "topicId";
    public static final String KEY_TITLE = "title";

    @Override
    protected void onInitVariable() {
        topicId = String.valueOf(getIntent().getExtras().getInt(KEY_TOPIC_ID));
        titleStr = getIntent().getExtras().getString(KEY_TITLE);
        
        queryFinished = false;
        hasInitialized = false;
        
        animation = AnimationUtils.loadAnimation(this, R.anim.zoomin);

        setPageLabel("cinemaCommentDetail");
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_twitter);
        
        if (titleStr != null && titleStr.length() > 6) {
            titleStr = titleStr.substring(0, 6) + "...";
        }
        
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, titleStr + "-短评", null);
        
        View bottom = this.findViewById(R.id.bottom_comment_view);
        bottomComments = new BottomOfMovieCommentsView(this, bottom, null, this);
        bottomComments.setHideAfterSend(false);
        
        View header = View.inflate(this, R.layout.movie_short_comments_list_header, null);
        init(header);
        
        listView = findViewById(R.id.comments_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        loadMoreFooterView = (LoadMoreFooterView) listView.getLoadMoreFooterView();

        listView.addHeaderView(header);
        listView.setOnLoadMoreListener(this);

    }
    
    @Override
    protected void onInitEvent() {
        twittersCallback = new RequestCallback() {
            
            @Override
            public void onSuccess(Object o) {
                listView.setVisibility(View.VISIBLE);
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                UIUtil.dismissLoadingDialog();
                TopicAllBean allBean = (TopicAllBean) o;
                if (!TextUtils.isEmpty(allBean.getError())) {
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    MToastUtils.showShortToast("数据加载完成");
                    return;
                }
                List<TopicReply> replyBeansList = allBean.getReplyTopic();
                if(null == replyBeansList || replyBeansList.size() < 20 || adapter.getCount() >= allBean.getReplyTotalCount())
                {
                    queryFinished = true;
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                }

                if (pageIndex == 1) {
                    setHeaderValues(allBean.getParentTopic());
                    replies = replyBeansList;
                    if (null == replies) {
                        replies = new ArrayList<>();
                    }
                    if (0 == replies.size()) {
                        // 添加一个默认的进来，给沙发用
                        TopicReply reply = new TopicReply();
                        reply.setReplyId(-1);
                        replies.add(reply);
                    }

                    adapter = new TwitterCinemaAdapter(TwitterCinemaActivity.this, replies, new ITwitterAdapterListener() {

                        @Override
                        public void onEvent(Object obj) {
                            if (!UserManager.Companion.getInstance().isLogin()) {
//                                startActivityForResult(LoginActivity.class, new Intent());
                                UserLoginKt.gotoLoginPage(TwitterCinemaActivity.this, null, 0);
                                return;
                            }

                            TopicReply reply = (TopicReply) obj;
                            bottomComments.setFocus();
                            String value = String.format("回复@%s:", reply.getNickname());
                            bottomComments.setComments(value);
                            
                        }});
                    listView.setIAdapter(adapter);
                }
                else {
                    replies.addAll(replyBeansList);
                    adapter.notifyDataSetChanged();
                }
            }
            
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
            }
        };
        
        releseCallback = new RequestCallback() {
            
            @Override
            public void onFail(Exception e) {
                MToastUtils.showShortToast("发送失败");
            }
            
            @Override
            public void onSuccess(Object o) {
                SuccessBean bean = (SuccessBean) o;
                if (Boolean.valueOf(bean.getSuccess())) {
                    MToastUtils.showShortToast("发送成功");

                    // 假数据添加，不刷新服务器，但是无法获取id和reponseId
                    TopicReply reply = new TopicReply();
                    reply.setReplyId(0);
                    reply.setContent(userContents);
                    reply.setNickname(UserManager.Companion.getInstance().getNickname());
                    Date date = new Date(MTimeUtils.getLastDiffServerTime());
                    reply.setEnterTime(date.getTime() / 1000);
                    reply.setUserImage(UserManager.Companion.getInstance().getUserAvatar());

                    // 如果只有一个代表沙发的，则直接移除
                    if (replies.size() == 1 && replies.get(0).getReplyId() == -1) {
                        replies.remove(0);
                    }
                    replies.add(0, reply);
                    // 这里更新下评论总数
                    replyCount ++;
                    replyValue.setText(String.valueOf(replyCount));
                    adapter.notifyDataSetChanged();
                }
                else {
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
        // Cinema/CinemaCommentReplies.api?topicId={0}&pageIndex={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("topicId", topicId);
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_TWITTER_CINEMA, param, TopicAllBean.class, twittersCallback);
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
    @Override
    public void onEvent(BottomViewActionType type, String contents) {
        if (type != BottomViewActionType.TYPE_MOVIE_COMMENTS_SEND) {
            return;
        }
        if (null == replies || replies.size() < 1) {
            MToastUtils.showShortToast("暂时无法评论自己的评论");
            return;
        }

        this.userContents = contents;
        Map<String, String> parameterList = new ArrayMap<String, String>(2);
        parameterList.put("content", contents);
        parameterList.put("topicId", topicId);
        HttpUtil.post(ConstantUrl.GET_TWITTER_CINEMA_REPLY, parameterList, SuccessBean.class, releseCallback);
    }
    
    private void init(final View root) {
        this.userHeader = root.findViewById(R.id.header);
        this.userName = root.findViewById(R.id.name);
        this.commentTime = root.findViewById(R.id.time);
        this.userRate = root.findViewById(R.id.rate);
        this.commentContents = root.findViewById(R.id.content);
        this.praiseAnimIcon = root.findViewById(R.id.praise_icon_animation);
        this.praiseIcon = root.findViewById(R.id.praise_icon);
        this.praiseValue = root.findViewById(R.id.praise);
        this.replyValue = root.findViewById(R.id.reply);
        
        View praiseView = root.findViewById(R.id.praise_region);
        praiseView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if (!UserManager.Companion.getInstance().isLogin()) {
//                    startActivityForResult(LoginActivity.class, new Intent());
                    UserLoginKt.gotoLoginPage(TwitterCinemaActivity.this, null, 0);
                    return;
                }
                
                CinemaViewActivity.isPraised = !CinemaViewActivity.isPraised;
                CinemaViewActivity.totalPraise += CinemaViewActivity.isPraised ? 1 : -1;
                
                updatePraise();
                praiseAnimIcon.startAnimation(animation);
                
                Map<String, String> parameterList = new ArrayMap<String, String>(2);
                parameterList.put("id", topicId);
                parameterList.put("relatedObjType", String.valueOf(86));
                HttpUtil.post(ConstantUrl.ADD_OR_DEL_PRAISELOG, parameterList, AddOrDelPraiseLogBean.class, null);
            }
        });
        
    }
    
    private void updatePraise() {
        if (CinemaViewActivity.isPraised) {
            this.praiseAnimIcon.setImageResource(R.drawable.assist2);
            this.praiseIcon.setImageResource(R.drawable.assist2);
            this.praiseValue.setTextColor(getResources().getColor(R.color.orange));
        }
        else {
            this.praiseValue.setTextColor(getResources().getColor(R.color.color_777777));
            this.praiseAnimIcon.setImageResource(R.drawable.assist1);
            this.praiseIcon.setImageResource(R.drawable.assist1);
        }
        
        String value;
        if (CinemaViewActivity.totalPraise < 1) {
            value = "赞";
        }
        else if (CinemaViewActivity.totalPraise < 1000) {
            value = String.valueOf(CinemaViewActivity.totalPraise);
        }
        else {
            value = "999+";
        }
        this.praiseValue.setText(value);
    }
    
    private void setHeaderValues(final TopicParent values) {
        if (hasInitialized) {
            return;
        }
        
        hasInitialized = true;
        
        volleyImageLoader.displayImage(values.getUserImage(), this.userHeader, ImageURLManager.ImageStyle.STANDARD, null);
        this.userName.setText(values.getNickname());
        // change the time value here
        String value = null;
        
        // time stamp
        long sendTime = values.getEnterTime() - 8*60*60L;
        if (0 == values.getTopicId()) {
            sendTime += 8*60*60L;
        }
        long replyTime = (System.currentTimeMillis() / 1000 - sendTime) / 60;
        if (replyTime < 0) {
            replyTime = 1;
        }
        if (replyTime < 60) {
            value = String.format("%d分钟前", replyTime);
        }
        else if (replyTime < (24 * 60)) {
            value = String.format("%d小时前", replyTime / 60);
        }
        else {
            value = DateUtil.getLongToDate(DateUtil.sdf5, values.getEnterTime());
        }
        this.commentTime.setText(value);
        
        // rate
        float rate = values.getRating();
        if (rate > 0) {
            this.userRate.setText(String.format("%.1f", rate));
        }
        else {
            this.userRate.setVisibility(View.INVISIBLE);
        }
        
        // reply count
        int count = values.getReplyCount() < 1 ? 0 : values.getReplyCount();
        replyCount = count;
        this.replyValue.setText(String.valueOf(count));
        
        // contents
        this.commentContents.setText(values.getContent());
        
        // praise view
        this.updatePraise();
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (loadMoreFooterView.canLoadMore() && adapter != null && adapter.getRealCount() > 0) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            if (queryFinished) {
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                return;
            }

            // Cinema/CinemaCommentReplies.api?topicId={0}&pageIndex={1}
            Map<String, String> param = new HashMap<String, String>(2);
            param.put("topicId", topicId);
            param.put("pageIndex", String.valueOf(pageIndex++));
            HttpUtil.get(ConstantUrl.GET_TWITTER_CINEMA, param, TopicAllBean.class, twittersCallback);
        }
    }

    public static void launch(Context context, int topicId, String title, int requestCode){
        Intent launcher = new Intent();
        launcher.putExtra(KEY_TOPIC_ID,topicId);
        launcher.putExtra(KEY_TITLE,title);
        ((BaseActivity)context).startActivityForResult(TwitterCinemaActivity.class,launcher,requestCode);
    }

}
