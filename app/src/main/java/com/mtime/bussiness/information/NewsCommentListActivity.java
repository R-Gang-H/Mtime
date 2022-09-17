package com.mtime.bussiness.information;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.collection.ArrayMap;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.frame.BaseActivity;
import com.google.gson.reflect.TypeToken;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.SuccessBean;
import com.mtime.bussiness.information.adapter.NewsCommentListAdapter;
import com.mtime.bussiness.information.bean.NewsCommentItemBean;
import com.mtime.bussiness.information.bean.NewsCommentItemReplyBean;
import com.mtime.bussiness.mine.login.activity.LoginActivity;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.BottomViewActionType;
import com.mtime.mtmovie.widgets.BottomOfMovieCommentsView.IBottomViewActListener;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.TipsDlg;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发现--新闻--评论列表页
 */
public class NewsCommentListActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener,
        NewsCommentListAdapter.OnRecyclerViewListener {
    private TitleOfNormalView navigationBar;
    private BottomOfMovieCommentsView bottom;
    private String commentString = "";
    private RequestCallback newsCallback;
    private int pageIndex = 1;
    private String newsId;            // 从新闻页传来新闻id
    private List<NewsCommentItemBean> newsMainBeans;
    private String reviewId;          // 影评页传来影评id
    private RequestCallback releseCallback;
    private SuccessBean successBean;
    private int commentSize;
    private TipsDlg tDlg;
    private View noinfo;
    private boolean isReply;

    private IRecyclerView ircyclerview;
    private NewsCommentListAdapter adapter;
    private LoadMoreFooterView loadMoreFooterView;
    private String commentId;

    public static final String KEY_NEWSID = "news_id";
    public static final String KEY_REVIEWID = "reviewid";
    public static final String KEY_COMMENT_SIZE = "comment_size";

    @Override
    protected void onInitVariable() {
        newsId = getIntent().getStringExtra(KEY_NEWSID);
        reviewId = getIntent().getStringExtra(KEY_REVIEWID);
        commentSize = getIntent().getIntExtra(KEY_COMMENT_SIZE, 0);

        setPageLabel("newsCommentsList");
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {

        this.setContentView(R.layout.act_news_discuss);
        View navBar = findViewById(R.id.navigationbar);
        navigationBar = new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "", null);
        View root = this.findViewById(R.id.comment_bottom);
        bottom = new BottomOfMovieCommentsView(this, root, null, new IBottomViewActListener() {

            @Override
            public void onEvent(BottomViewActionType type, String contents) {
                if (BottomViewActionType.TYPE_MOVIE_COMMENTS_SEND != type) {
                    return;
                }
                commentString = contents;
                commentRelease();
            }
        });

        bottom.setVisibility(View.VISIBLE);
        bottom.setHideAfterSend(false);

        ircyclerview = findViewById(R.id.comment_list);
        loadMoreFooterView = (LoadMoreFooterView) ircyclerview.getLoadMoreFooterView();
        ircyclerview.setItemAnimator(new DefaultItemAnimator());//默认的删除和添加的动画
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        ircyclerview.setLayoutManager(layoutManager);
        ircyclerview.setOnRefreshListener(this);
        ircyclerview.setOnLoadMoreListener(this);

        navigationBar.setTitleText(commentSize + "条评论");
        tDlg = new TipsDlg(NewsCommentListActivity.this);
        noinfo = findViewById(R.id.no_info_view);
    }

    @Override
    protected void onInitEvent() {

        newsCallback = new RequestCallback() {

            @SuppressWarnings("unchecked")
            @Override
            public void onSuccess(final Object o) {

                UIUtil.dismissLoadingDialog();
                final ArrayList<NewsCommentItemBean> nBeans = (ArrayList<NewsCommentItemBean>) o;

                if (pageIndex == 1) {
                    if (nBeans == null || nBeans.size() == 0) {
                        ircyclerview.setVisibility(View.GONE);
                        noinfo.setVisibility(View.VISIBLE);
                        navigationBar.setTitleText("评论");
                    }
                    ircyclerview.setRefreshing(false);
                    if (nBeans == null || nBeans.size() == 0) {
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    } else {
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    }
                    newsMainBeans = nBeans;
                    adapter = new NewsCommentListAdapter(NewsCommentListActivity.this, newsMainBeans, null, null);
                    adapter.setOnRecyclerViewListener(NewsCommentListActivity.this);
                    ircyclerview.setIAdapter(adapter);
                } else {
                    if (adapter != null) {
                        if (nBeans == null || nBeans.size() == 0) {
                            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                            return;
                        }
                        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                        int insertFromIndex = newsMainBeans.size();
                        newsMainBeans.addAll(nBeans);
                        adapter.notifyItemInserted(insertFromIndex);
                    }
                }

                for (int beanIndex = 0; beanIndex < nBeans.size(); beanIndex++) {
                    final NewsCommentItemBean currentNew = nBeans.get(beanIndex);
                    final String[] newsDate = currentNew.getDate().split(" ");

                    final List<NewsCommentItemReplyBean> newsCommentItemReplyBeanList = currentNew.getReplies();
                    for (int replyIndex = 0; replyIndex < newsCommentItemReplyBeanList.size(); replyIndex++) {
                        final NewsCommentItemReplyBean newsCommentItemReplyBean = newsCommentItemReplyBeanList.get(replyIndex);

                        final String[] replyDate = newsCommentItemReplyBean.getDate().split(" ");
                        if (replyDate[0].trim().equals(newsDate[0].trim())) {
                            final String[] replyTime = replyDate[1].split(":"); // 时间格式,
                            // 时:分:秒
                            newsCommentItemReplyBean.setDate(replyTime[0] + ":" + replyTime[1]); // 显示格式,时:分
                        } else {
                            newsCommentItemReplyBean.setDate(replyDate[0]);
                        }
                    }
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                if (pageIndex == 1) {
                    ircyclerview.setRefreshing(false);
                } else {
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                }
                ircyclerview.setVisibility(View.GONE);
                noinfo.setVisibility(View.GONE);
                navigationBar.setTitleText("评论");
                MToastUtils.showShortToast("数据加载失败:"+e.getLocalizedMessage());
            }
        };
        releseCallback = new RequestCallback() {

            @Override
            public void onSuccess(final Object o) {

                successBean = (SuccessBean) o;
                if (successBean != null && Boolean.valueOf(successBean.getSuccess())) {
                    tDlg.setText("发送成功");
                    tDlg.getProgressBar().setVisibility(View.GONE);
                    tDlg.getImg().setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            tDlg.dismiss();
                        }
                    }, 1000);
                    // 假数据添加，不刷新服务器，但是无法获取id
                    ircyclerview.setVisibility(View.VISIBLE);
                    noinfo.setVisibility(View.GONE);
                    if (isReply) {
                        isReply = false;
                        NewsCommentItemReplyBean newsCommentItemReplyBean = new NewsCommentItemReplyBean();
                        newsCommentItemReplyBean.setContent(commentString);
                        newsCommentItemReplyBean.setNickname(UserManager.Companion.getInstance().getNickname());
                        newsCommentItemReplyBean.setUserImage(UserManager.Companion.getInstance().getUserAvatar());
                        newsCommentItemReplyBean.setDate(MTimeUtils.format(MTimeUtils.YMD_HM, MTimeUtils.getLastDiffServerTime()));
                        newsCommentItemReplyBean.setTimestamp(MTimeUtils.getLastDiffServerTime() / 1000 + 3600 * 8);

                        int notifyIndex = 0;
                        for (int i = 0; i < newsMainBeans.size(); i++) {
                            if (String.valueOf(newsMainBeans.get(i).getId()).equals(commentId)) {
                                newsMainBeans.get(i).getReplies().add(0, newsCommentItemReplyBean);
                                notifyIndex = i;
                            }
                        }
                        isReply = false;
                        adapter.notifyItemChanged(notifyIndex);
                    } else {
                        final NewsCommentItemBean cBean = new NewsCommentItemBean();
                        cBean.setContent(commentString);
                        if (UserManager.Companion.getInstance().isLogin()) {
                            cBean.setNickname(UserManager.Companion.getInstance().getNickname());
                            cBean.setUserImage(UserManager.Companion.getInstance().getUserAvatar());
                        } else {
                            cBean.setNickname("");
                            cBean.setUserImage("");
                        }
                        cBean.setDate(MTimeUtils.format(MTimeUtils.YMD_HM, MTimeUtils.getLastDiffServerTime()));
                        cBean.setTimestamp(MTimeUtils.getLastDiffServerTime() / 1000 + 3600 * 8);
                        newsMainBeans.add(0, cBean);// 在顶部显示
                        adapter.notifyItemChanged(0);
                    }
                    commentSize++;
                    navigationBar.setTitleText(commentSize + "条评论");
                } else {
                    isReply = false;
                    if (successBean != null) {
                        tDlg.setText(successBean.getError());
                    }
                    tDlg.getProgressBar().setVisibility(View.GONE);
                    tDlg.getImg().setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            tDlg.dismiss();
                        }
                    }, 3000);

                }

            }

            @Override
            public void onFail(final Exception e) {
                isReply = false;
                tDlg.setText("发送失败");
                tDlg.getProgressBar().setVisibility(View.GONE);
                tDlg.getImg().setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        tDlg.dismiss();
                    }
                }, 1000);

            }
        };
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {

        UIUtil.showLoadingDialog(this);
        requestComment();
    }

    @Override
    protected void onUnloadData() {

    }

    // 请求评论列表
    private void requestComment() {

        if (!TextUtils.isEmpty(newsId)) {
            final Type typeToken = new TypeToken<List<NewsCommentItemBean>>() {
            }.getType();
            // News/Comment.api?newsId={0}&pageIndex={1}
            Map<String, String> param = new HashMap<>(2);
            param.put("newsId", newsId);
            param.put("pageIndex", String.valueOf(pageIndex));
            HttpUtil.get(ConstantUrl.GET_RECOMMEND_COMMENT_NEWS, param, NewsCommentItemBean.class, newsCallback, 180000, typeToken);
        } else if (!TextUtils.isEmpty(reviewId)) {

            final Type typeToken = new TypeToken<List<NewsCommentItemBean>>() {
            }.getType();

            //Review/Comment.api?reviewId={0}&pageIndex={1}
            Map<String, String> param = new HashMap<>(2);
            param.put("reviewId", reviewId);
            param.put("pageIndex", String.valueOf(pageIndex));
            HttpUtil.get(ConstantUrl.GET_RECOMMEND_COMMENT_REVIEW, param, NewsCommentItemBean.class, newsCallback, 180000, typeToken);
        }
    }

    // 发布评论
    private void commentRelease() {
        if (UserManager.Companion.getInstance().isLogin()) {
            tDlg.show();
            tDlg.setText("正在发送");
            tDlg.getProgressBar().setVisibility(View.VISIBLE);
            tDlg.getImg().setVisibility(View.GONE);

            if (!TextUtils.isEmpty(newsId)) {
                Map<String, String> parameterList = new ArrayMap<String, String>(3);
                parameterList.put("newsId", newsId);
                // 根据id来判断是发送到回复里还是整个评论里
                if (isReply) {
                    parameterList.put("commentId", commentId);
                } else {
                    parameterList.put("commentId", "0");
                }
                parameterList.put("content", commentString);
                HttpUtil.post(ConstantUrl.GET_RECOMMEND_COMMENT_DONEWS, parameterList, SuccessBean.class, releseCallback);

            } else if (!TextUtils.isEmpty(reviewId)) {
                Map<String, String> parameterList = new ArrayMap<String, String>(3);
                parameterList.put("reviewId", reviewId);
                if (isReply) {
                    parameterList.put("commentId", commentId);
                } else {
                    parameterList.put("commentId", "0");
                }
                parameterList.put("content", commentString);
                HttpUtil.post(ConstantUrl.GET_RECOMMEND_COMMENT_DOREVIEW, parameterList, SuccessBean.class, releseCallback);
            }
        } else {
            // TODO  临时注释掉,等需求完整后再调整
//            final Intent intent = getIntent();
//            intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, NewsCommentListActivity.class.getName());
//            this.startActivity(LoginActivity.class);
            UserLoginKt.gotoLoginPage(this, null, null);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (UserManager.Companion.getInstance().isLogin()) {
            final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
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
            pageIndex++;
            requestComment();
        }
    }

    @Override
    public void onRefresh() {
        pageIndex = 1;
        requestComment();
    }

    @Override
    public void onClickReply(String commentId) {
        this.commentId = commentId;
        isReply = true;
        if (!UserManager.Companion.getInstance().isLogin()) {
//            final Intent intent = getIntent();
//            startActivityForResult(LoginActivity.class, intent);
            UserLoginKt.gotoLoginPage(this, null, 0);
        } else {
            bottom.setFocus();
        }
    }

    public static void launch(Context context, String refer, String newsId, String reviewId, int commentSize){
        Intent launcher = new Intent(context, NewsCommentListActivity.class);
        if(!TextUtils.isEmpty(newsId)) {
            launcher.putExtra(KEY_NEWSID, newsId);
        }
        if(!TextUtils.isEmpty(reviewId)) {
            launcher.putExtra(KEY_REVIEWID, reviewId);
        }
        launcher.putExtra(KEY_COMMENT_SIZE,commentSize);
        dealRefer(context, refer, launcher);
        context.startActivity(launcher);
    }

}
