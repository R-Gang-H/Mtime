package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnIRefreshListener;
import com.aspsine.irecyclerview.OnIScrollListener;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.mine.adapter.MyCommentAdapter;
import com.mtime.bussiness.mine.bean.CommentAndReplyBean;
import com.mtime.bussiness.mine.bean.CommentAndReplyListBean;
import com.mtime.bussiness.common.widget.MoveLayout;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的电影短评和回复
 * Created by ZhangCong on 16/4/5.
 */
public class CommentAndReplyActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private IRecyclerView commentaryListView;
    private LoadMoreFooterView loadMoreFooterView;
    private LinearLayoutManager linearLayoutManager;
    private MyCommentAdapter commentAdapter;
    private RequestCallback listCallback;
    private final int type = 1;// 因为原定是有回复和评论，所有有type
    private int commentPageIndex = 1;
    private List<CommentAndReplyListBean> commentBean;
    // 时间戳得除以1000
    private final int currentYear = MTimeUtils.getYear(new Date(System.currentTimeMillis()));
    private MoveLayout move_board;
    private TextView move_name;
    private int move_height;

    @Override
    protected void onInitEvent() {
        commentaryListView.setOnRefreshListener(this);
        commentaryListView.setOnLoadMoreListener(this);

        // 为了实现月份悬停效果
        commentaryListView.setOnIScrollListener(new OnIScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                {
                    View mView = recyclerView.getChildAt(1);
                    int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition() - 2;
                    if (commentAdapter != null && firstVisibleItem + 1 < commentAdapter.getCount()) {
                        if (firstVisibleItem >= 0) {
                            CommentAndReplyListBean bean = (CommentAndReplyListBean) commentAdapter.getItem(firstVisibleItem);
                            int year = MTimeUtils.getYear(new Date(bean.getTime() * 1000));
                            int month = MTimeUtils.getMonth(new Date(bean.getTime() * 1000));
                            move_board.setVisibility(View.VISIBLE);
                            int vY = mView.getTop();
                            if (move_height >= vY && commentAdapter.isHeader(firstVisibleItem + 1)) {
                                move_board.move(vY - move_height);
                            } else {
                                move_board.move(0);
                            }

                            if (currentYear == year) {
                                move_name.setText(String.format(getResources().getString(R.string.my_comment_and_reply_month), month));
                            } else {
                                move_name.setText(String.format(getResources().getString(R.string.my_comment_and_reply_year), year, month));
                            }
                        } else {
                            move_board.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        commentaryListView.setOnIRefreshListener(new OnIRefreshListener() {
            @Override
            public void onRefreshStart() {
                move_board.setVisibility(View.GONE);
            }
        });

        listCallback = new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                commentaryListView.setRefreshing(false);
            }

            @Override
            public void onSuccess(Object o) {
                CommentAndReplyBean bean = (CommentAndReplyBean) o;
                UIUtil.dismissLoadingDialog();
                loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                commentaryListView.setRefreshing(false);
                if (commentPageIndex == 1) {
                    commentBean = bean.getUserCommtentList();
                    commentAdapter = new MyCommentAdapter(CommentAndReplyActivity.this, commentBean);
                    commentaryListView.setIAdapter(commentAdapter);
                } else {
                    commentBean.addAll(bean.getUserCommtentList());
                    commentAdapter.notifyDataSetChanged();
                }

                if (commentPageIndex >= bean.getCount()) {//没有更多了
                    loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                }
            }
        };
    }

    @Override
    protected void onInitVariable() {
        setPageLabel("myComment");
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_comment_reply);
        View navBar = findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, this.getResources().getString(
                R.string.str_my_comment), null);

        commentaryListView = findViewById(R.id.comment_list);
        linearLayoutManager = new LinearLayoutManager(this);
        commentaryListView.setLayoutManager(linearLayoutManager);
        loadMoreFooterView = (LoadMoreFooterView) commentaryListView.getLoadMoreFooterView();

        move_board = findViewById(R.id.move_board);
        move_name = move_board.findViewById(R.id.move_name);
        move_board.setVisibility(View.GONE);
        move_height = move_board.getLayoutParams().height;
    }

    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);
        requsetInfo(commentPageIndex);
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    private void requsetInfo(int pageIndex) {
        // User/UserComments.api?type={0}&pageIndex={1}
        Map<String, String> param = new HashMap<>(2);
        param.put("type", String.valueOf(type));
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_MY_COMMENT_REPLY, param, CommentAndReplyBean.class, listCallback);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (loadMoreFooterView.canLoadMore()) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            commentPageIndex++;
            requsetInfo(commentPageIndex);
        }
    }

    @Override
    public void onRefresh() {
        commentPageIndex = 1;
        requsetInfo(commentPageIndex);
    }

    /**
     * 自己定义refer
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        Intent launcher = new Intent(context, CommentAndReplyActivity.class);
        dealRefer(context,refer, launcher);
        context.startActivity(launcher);
    }
}