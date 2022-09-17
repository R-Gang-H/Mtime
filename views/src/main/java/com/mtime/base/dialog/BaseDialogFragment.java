package com.mtime.base.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mtime.base.views.R;


/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2018-02-08
 * <p>
 * 默认主题是 R.style.BaseDialogFragment
 * <p>
 * 提供安全的 showAllowingStateLoss
 */

public abstract class BaseDialogFragment extends DialogFragment implements CancelableDialog.PreCancelCallback,
        CancelableDialog.ShouldCancelCallback, DialogInterface.OnShowListener {

    private static final float DEFAULT_DIM = 0.8f;

    private DismissListener dismissListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, getTheme());
    }

    /**
     * 如果需要设置主题，请重写这个函数，并返回一个主题
     */
    @Override
    public int getTheme() {
        return R.style.ViewsBaseDialogFragment;
    }

    /**
     * 最好不要重写这个函数，如果必须重写，那最好不要自己 new Dialog() 否则这个类就失去了意义
     */
    @CallSuper
    @NonNull
    @Override
    public final Dialog onCreateDialog(Bundle savedInstanceState) {
        CancelableDialog dialog = new CancelableDialog(getActivity(), getTheme());
        Window window = dialog.getWindow();
        window.setDimAmount(getDimAmount());
        dialog.setPreCancelCallback(this);
        dialog.setShouldCancelCallback(this);
        dialog.setOnShowListener(this);
        return dialog;
    }

    /**
     * dialog 非内容区域的黑色透明度，默认 0.8
     */
    protected float getDimAmount() {
        return DEFAULT_DIM;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutRes(), container, false);
        bindView(v);
        return v;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Deprecated
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    /**
     * 安全的显示 dialog
     */
    public void showAllowingStateLoss(FragmentManager fmgr) {
        show(fmgr, getFragmentTag());
    }

    /**
     * 安全的显示 dialog
     */
    public void show(FragmentManager fmgr) {
        show(fmgr, getFragmentTag());
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public abstract void bindView(View v);

    /**
     * 点击返回按钮， dialog 是否应该消失
     */
    @Override
    public boolean shouldCancelOnBackPressed() {
        return true;
    }

    /**
     * 点击 dialog 的 外围 是否应该消失
     */
    @Override
    public boolean shouldCancelOnTouchOutside() {
        return true;
    }

    /**
     * cancel 之前的回调
     */
    @Override
    public void onPreCancel() {
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    public void setOnDismissListener(DismissListener listener) {
        dismissListener = listener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != dismissListener) {
            dismissListener.onDismiss();
        }
    }

    @Override
    public void onShow(DialogInterface dialog) {
    }

    public interface DismissListener {
        void onDismiss();
    }

    public String getFragmentTag() {
        return "dialog_" + getIdentityString();
    }

    protected String getIdentityString() {
        return Integer.toHexString(System.identityHashCode(this));
    }
}
