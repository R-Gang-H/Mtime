package com.mtime.bussiness.mine.comments.movie;

import android.view.View;

import com.mtime.R;
import com.mtime.base.views.BaseBottomFragment;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class MovieCommentMoreDialog extends BaseBottomFragment {

    @Override
    protected float getDimAmount() {
        return 0.4f;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_movie_comment_more_layout;
    }

    @Override
    public void bindView(View v) {
        ButterKnife.bind(this, v);
    }

    @OnClick(R.id.delete_tv)
    void onDeleteClick() {
        dismiss();
        if (mOnDeleteClickCallback != null) {
            mOnDeleteClickCallback.onDeleteClick();
        }
    }

    @OnClick(R.id.cancel_tv)
    void onCancelClick() {
        dismiss();
    }

    private OnDeleteClickCallback mOnDeleteClickCallback;

    public void setOnDeleteClickCallback(OnDeleteClickCallback onDeleteClickCallback) {
        mOnDeleteClickCallback = onDeleteClickCallback;
    }

    public interface OnDeleteClickCallback {
        void onDeleteClick();
    }
}
