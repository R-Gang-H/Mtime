package com.mtime.base.views;

import androidx.fragment.app.FragmentManager;

import com.mtime.base.dialog.BaseDialogFragment;

/**
 * Created by yuhengyi on 16/10/19.
 * <p>
 * 底部弹出的fragment 相对于{@link MBottomDlg}可自定义布局
 */

public abstract class BaseBottomFragment extends BaseDialogFragment {

    public boolean getCancelOutside() {
        return true;
    }

    @Override
    public int getTheme() {
        return R.style.ViewsBottomDialog;
    }

    public void show(FragmentManager fragmentManager) {
        showAllowingStateLoss(fragmentManager);
    }

    public void hide() {
        dismissAllowingStateLoss();
    }

    @Override
    public boolean shouldCancelOnTouchOutside() {
        return getCancelOutside();
    }

}
