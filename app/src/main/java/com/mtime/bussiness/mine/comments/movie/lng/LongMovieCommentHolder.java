package com.mtime.bussiness.mine.comments.movie.lng;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnIRefreshListener;
import com.aspsine.irecyclerview.OnIScrollListener;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.bussiness.common.widget.MoveLayout;
import com.mtime.bussiness.ticket.movie.comment.bean.MineLongMovieCommentBean;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
class LongMovieCommentHolder extends ContentHolder<Void> implements OnRefreshListener, OnLoadMoreListener {

    LongMovieCommentHolder(Context context, Callback c) {
        super(context);
        mCallback = c;
    }

    void setNoMore() {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
    }

    void stopRefreshing() {
        commentaryListView.setRefreshing(false);
    }

    void finishLoadMore() {
        loadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
    }

    void addComments(List<MineLongMovieCommentBean> commtentList) {
        if (commtentList != null) {
            mComments.addAll(commtentList);
            mCommentAdapter.notifyDataSetChanged();
        }
    }

    void onDeleteComment(int position) {
        mComments.remove(position);
        mCommentAdapter.notifyDataSetChanged();
        if (mComments.isEmpty()) {
            showEmpty();
        }
    }

    void clearData() {
        mComments.clear();
    }

    void deleteComment(long commentId) {
        Iterator<MineLongMovieCommentBean> iterator = mComments.iterator();
        for (; iterator.hasNext(); ) {
            MineLongMovieCommentBean bean = iterator.next();
            if (bean.commentId == commentId) {
                iterator.remove();
                mCommentAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    void doRefresh() {
        commentaryListView.setRefreshing(true);
    }


    @Override
    public void onCreate() {
    }

    interface Callback extends MyLongMovieCommentAdapter.ClickCallback {

        void onLoadMore();

        void onRefresh();
    }

    void showEmpty() {
        mEmptyView.setVisibility(View.VISIBLE);
    }

    private final Callback mCallback;

    @BindView(R.id.comment_list)
    IRecyclerView commentaryListView;
    private LoadMoreFooterView loadMoreFooterView;
    private LinearLayoutManager linearLayoutManager;
    // ??????????????????1000
    private final int currentYear = MTimeUtils.getYear(new Date(System.currentTimeMillis()));

    @BindView(R.id.move_board)
    MoveLayout move_board;
    @BindView(R.id.move_name)
    TextView move_name;
    @BindView(R.id.empty_layout)
    View mEmptyView;

    private int move_height;

    private MyLongMovieCommentAdapter mCommentAdapter;

    private final List<MineLongMovieCommentBean> mComments = new ArrayList<>();

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.frag_my_short_and_long_movie_comment);

        ButterKnife.bind(this, mRootView);

        initThings();
    }

    private void initThings() {
        linearLayoutManager = new LinearLayoutManager(mContext);
        commentaryListView.setLayoutManager(linearLayoutManager);
        loadMoreFooterView = (LoadMoreFooterView) commentaryListView.getLoadMoreFooterView();
        commentaryListView.setOnRefreshListener(this);
        commentaryListView.setOnLoadMoreListener(this);

        move_board.setVisibility(View.GONE);
        move_height = move_board.getLayoutParams().height;

        mCommentAdapter = new MyLongMovieCommentAdapter(mContext, mComments, mCallback);
        commentaryListView.setIAdapter(mCommentAdapter);

        // ??????????????????????????????
        commentaryListView.setOnIScrollListener(new OnIScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                View mView = recyclerView.getChildAt(1);
                int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition() - 2;
                if (firstVisibleItem + 1 < mCommentAdapter.getItemCount()) {
                    if (firstVisibleItem >= 0) {
                        MineLongMovieCommentBean bean = mComments.get(firstVisibleItem);
                        int year = MTimeUtils.getYear(new Date(bean.time * 1000));
                        int month = MTimeUtils.getMonth(new Date(bean.time * 1000));
                        move_board.setVisibility(View.VISIBLE);
                        int vY = mView.getTop();
                        if (move_height >= vY && mCommentAdapter.isHeader(firstVisibleItem + 1)) {
                            move_board.move(vY - move_height);
                        } else {
                            move_board.move(0);
                        }

                        if (currentYear == year) {
                            move_name.setText(String.format(getString(R.string.my_comment_and_reply_month), month));
                        } else {
                            move_name.setText(String.format(getString(R.string.my_comment_and_reply_year), year, month));
                        }
                    } else {
                        move_board.setVisibility(View.GONE);
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
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (loadMoreFooterView.canLoadMore()) {
            loadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            mCallback.onLoadMore();
        }
    }

    @Override
    public void onRefresh() {
        mCallback.onRefresh();
    }
}
