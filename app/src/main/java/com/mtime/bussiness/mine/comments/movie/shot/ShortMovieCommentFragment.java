package com.mtime.bussiness.mine.comments.movie.shot;

import android.content.Intent;
import android.os.Bundle;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.widget.TwoButtonAlert;
import com.mtime.bussiness.mine.bean.CommentAndReplyBean;
import com.mtime.bussiness.mine.comments.movie.MovieCommentMoreDialog;
import com.mtime.bussiness.ticket.movie.activity.TwitterActivity;
import com.mtime.bussiness.ticket.movie.comment.api.MovieCommentApi;
import com.mtime.bussiness.ticket.movie.comment.bean.DeleteCommentsResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.MovieCommentDeleteEvent;
import com.mtime.bussiness.ticket.movie.comment.bean.PublishMovieCommentEvent;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class ShortMovieCommentFragment extends BaseFrameUIFragment<Void, ShortMovieCommentHolder>
        implements ShortMovieCommentHolder.Callback {

    private ShortMovieCommentHolder mHolder;
    private RequestCallback listCallback;
    private final MovieCommentApi mCommentApi = new MovieCommentApi();
    private boolean mRefresh = false;

    @Override
    public ContentHolder onBindContentHolder() {
        return (mHolder = new ShortMovieCommentHolder(mContext, this));
    }

    private int commentPageIndex = 1;

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        setPageState(BaseState.LOADING);

        listCallback = new RequestCallback() {
            @Override
            public void onFail(Exception e) {
                if (isUnsafe()) {
                    return;
                }
                if (commentPageIndex == 1) {
                    if (mRefresh) {
                        MToastUtils.showShortToast("加载失败");
                        mHolder.stopRefreshing();
                    } else {
                        setPageState(BaseState.ERROR);
                        mHolder.stopRefreshing();
                    }
                    mRefresh = false;
                } else {
                    MToastUtils.showShortToast("加载失败");
                    mHolder.finishLoadMore();
                }
            }

            @Override
            public void onSuccess(Object o) {
                if (isUnsafe()) {
                    return;
                }
                CommentAndReplyBean bean = (CommentAndReplyBean) o;
                if (commentPageIndex == 1) {
                    setPageState(BaseState.SUCCESS);
                    mHolder.clearData();
                    mHolder.stopRefreshing();
                    if (bean.getUserCommtentList() == null || bean.getUserCommtentList().isEmpty()) {
                        mHolder.showEmpty();
                    }
                } else {
                    mHolder.finishLoadMore();
                }
                mHolder.addComments(bean.getUserCommtentList());

                if (commentPageIndex >= bean.getCount()) {//没有更多了
                    mHolder.setNoMore();
                }
            }
        };

        requsetInfo(commentPageIndex);
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
        setPageState(BaseState.LOADING);
        requsetInfo(commentPageIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommentApi.cancel();
    }

    private void requsetInfo(int pageIndex) {
        // User/UserComments.api?type={0}&pageIndex={1}
        Map<String, String> param = new HashMap<>(2);
        // 因为原定是有回复和评论，所有有type
        param.put("type", "1");
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_MY_COMMENT_REPLY, param, CommentAndReplyBean.class, listCallback);
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentDeleted(MovieCommentDeleteEvent event) {
        if (isUnsafe()) {
            return;
        }
        if (!event.isLong) {
            mHolder.deleteComment(event.commentId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentPublished(PublishMovieCommentEvent event) {
        if (isUnsafe()) {
            return;
        }
        if (!event.isLong) {
            mHolder.doRefresh();
        }
    }

    @Override
    public void onLoadMore() {
        commentPageIndex++;
        requsetInfo(commentPageIndex);
    }

    @Override
    public void onRefresh() {
        mRefresh = true;
        commentPageIndex = 1;
        requsetInfo(commentPageIndex);
    }

    @Override
    public void gotoTwitter(int tweetId) {
        final Intent intent = new Intent(mContext, TwitterActivity.class);
        intent.putExtra("tweetId", tweetId);
        startActivity(intent);
    }

    @Override
    public void gotoMovieDetail(long relatedId) {
        JumpUtil.startMovieInfoActivity(mContext, assemble().toString(), String.valueOf(relatedId), 0);
    }

    @Override
    public void onMoreClick(int position, long relatedId, long tweetId) {
        MovieCommentMoreDialog moreDialog = new MovieCommentMoreDialog();
        moreDialog.setOnDeleteClickCallback(new MovieCommentMoreDialog.OnDeleteClickCallback() {
            @Override
            public void onDeleteClick() {
                showDeleteConfirm(position, relatedId, tweetId);
            }
        });

        moreDialog.show(getChildFragmentManager());
    }

    private void showDeleteConfirm(int position, long relatedId, long tweetId) {
        TwoButtonAlert alert = new TwoButtonAlert.Builder()
                .alert("删除影评？")
                .negative("取消")
                .positive("删除")
                .dimAmount(0.4f)
                .shouldCancel(false)
                .callback(new TwoButtonAlert.AlertCallback() {
                    @Override
                    public void onPositiveClick() {
                        deleteComment(position, relatedId, tweetId);
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).build();
        alert.show(getChildFragmentManager());
    }

    private void deleteComment(int position, long relatedId, long tweetId) {
        setPageState(BaseState.LOADING_WITH_CONTENT);
        mCommentApi.deleteShortComment(relatedId, tweetId, new MovieCommentApi.DeleteCommentsCallback() {
            @Override
            public void onDeleteResult(DeleteCommentsResultBean result) {
                if (isUnsafe()) {
                    return;
                }
                setPageState(BaseState.SUCCESS);
                if (result != null) {
                    if (result.shortDeleted) {
                        mHolder.onDeleteComment(position);
                    }
                }
            }
        });
    }
}
