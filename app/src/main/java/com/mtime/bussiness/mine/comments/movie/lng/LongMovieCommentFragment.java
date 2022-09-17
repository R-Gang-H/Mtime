package com.mtime.bussiness.mine.comments.movie.lng;

import android.os.Bundle;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.common.widget.TwoButtonAlert;
import com.mtime.bussiness.mine.comments.movie.MovieCommentMoreDialog;
import com.mtime.bussiness.ticket.movie.comment.api.MovieCommentApi;
import com.mtime.bussiness.ticket.movie.comment.bean.DeleteCommentsResultBean;
import com.mtime.bussiness.ticket.movie.comment.bean.MineLongMovieCommentsBean;
import com.mtime.bussiness.ticket.movie.comment.bean.MovieCommentDeleteEvent;
import com.mtime.bussiness.ticket.movie.comment.bean.PublishMovieCommentEvent;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.util.JumpUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class LongMovieCommentFragment extends BaseFrameUIFragment<Void, LongMovieCommentHolder>
        implements LongMovieCommentHolder.Callback {

    private LongMovieCommentHolder mHolder;
    private final MovieCommentApi mCommentApi = new MovieCommentApi();

    private int commentPageIndex = 1;
    private boolean mRefresh = false;

    private NetworkManager.NetworkListener<MineLongMovieCommentsBean> mListener;

    @Override
    public ContentHolder onBindContentHolder() {
        return (mHolder = new LongMovieCommentHolder(mContext, this));
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        mListener = new NetworkManager.NetworkListener<MineLongMovieCommentsBean>() {
            @Override
            public void onSuccess(MineLongMovieCommentsBean bean, String showMsg) {
                if (isUnsafe()) {
                    return;
                }
                if (commentPageIndex == 1) {
                    setPageState(BaseState.SUCCESS);
                    mHolder.clearData();
                    mHolder.stopRefreshing();
                    if (bean.userCommtentList == null || bean.userCommtentList.isEmpty()) {
                        mHolder.showEmpty();
                    }
                } else {
                    mHolder.finishLoadMore();
                }
                mHolder.addComments(bean.userCommtentList);

                if (commentPageIndex >= bean.pageCount) { //没有更多了
                    mHolder.setNoMore();
                }
            }

            @Override
            public void onFailure(NetworkException<MineLongMovieCommentsBean> exception, String showMsg) {
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
        };

        setPageState(BaseState.LOADING);
        loadPage();
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
        setPageState(BaseState.LOADING);
        loadPage();
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
        if (event.isLong) {
            mHolder.deleteComment(event.commentId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentPublished(PublishMovieCommentEvent event) {
        if (isUnsafe()) {
            return;
        }
        if (event.isLong) {
            mHolder.doRefresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCommentApi.cancel();
    }

    private void loadPage() {
        mCommentApi.getMyLongComments(commentPageIndex, 20, mListener);
    }

    @Override
    public void onLoadMore() {
        commentPageIndex++;
        loadPage();
    }

    @Override
    public void onRefresh() {
        mRefresh = true;
        commentPageIndex = 1;
        loadPage();
    }

    @Override
    public void gotoCommentDetail(long commentId) {
        JumpUtil.startFindFilmReviewDetailActivity(mContext, assemble().toString(),
                0, String.valueOf(commentId), "");
    }

    @Override
    public void gotoMovieDetail(long relatedId) {
        JumpUtil.startMovieInfoActivity(mContext, assemble().toString(), String.valueOf(relatedId), 0);
    }

    @Override
    public void onMoreClick(int position, long commentId) {
        MovieCommentMoreDialog moreDialog = new MovieCommentMoreDialog();
        moreDialog.setOnDeleteClickCallback(new MovieCommentMoreDialog.OnDeleteClickCallback() {
            @Override
            public void onDeleteClick() {
                showDeleteConfirm(position, commentId);
            }
        });

        moreDialog.show(getChildFragmentManager());
    }

    private void showDeleteConfirm(int position, long commentId) {
        TwoButtonAlert alert = new TwoButtonAlert.Builder()
                .alert("删除影评？")
                .negative("取消")
                .positive("删除")
                .dimAmount(0.4f)
                .shouldCancel(false)
                .callback(new TwoButtonAlert.AlertCallback() {
                    @Override
                    public void onPositiveClick() {
                        deleteComment(position, commentId);
                    }

                    @Override
                    public void onNegativeClick() {
                    }
                }).build();
        alert.show(getChildFragmentManager());
    }

    private void deleteComment(int position, long commentId) {
        setPageState(BaseState.LOADING_WITH_CONTENT);

        mCommentApi.deleteLongComment(commentId, new MovieCommentApi.DeleteCommentsCallback() {
            @Override
            public void onDeleteResult(DeleteCommentsResultBean result) {
                if (isUnsafe()) {
                    return;
                }
                setPageState(BaseState.SUCCESS);
                if (result != null) {
                    if (result.longDeleted) {
                        mHolder.onDeleteComment(position);
                    }
                }
            }
        });
    }
}
