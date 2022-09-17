package com.mtime.base.dialog;

import android.os.Bundle;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

import com.mtime.base.recyclerview.CommonViewHolder;

public class MDialog extends BaseMDialog {
    private ViewConvertListener mConvertListener;

    public static MDialog with() {
        return new MDialog();
    }

    @Override
    public int intLayoutId() {
        return mLayoutId;
    }

    @Override
    public void convertView(CommonViewHolder holder, BaseMDialog dialog) {
        if (mConvertListener != null) {
            mConvertListener.convertView(holder, dialog);
        }
    }

    public MDialog setLayoutId(@LayoutRes int layoutId) {
        this.mLayoutId = layoutId;
        return this;
    }

    public MDialog setConvertListener(ViewConvertListener convertListener) {
        this.mConvertListener = convertListener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mConvertListener = savedInstanceState.getParcelable("listener");
        }
    }

    /**
     * 保存接口
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("listener", mConvertListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mConvertListener = null;
    }
}
