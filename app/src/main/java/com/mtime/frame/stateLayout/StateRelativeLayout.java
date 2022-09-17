package com.mtime.frame.stateLayout;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.mtime.frame.holder.ErrorHolder;
import com.mtime.frame.holder.LoadingHolder;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.BaseStateRelativeLayout;

/**
 * Created by mtime on 2017/11/30.
 */

public class StateRelativeLayout extends BaseStateRelativeLayout {

    public StateRelativeLayout(Context context) {
        super(context);
    }

    public StateRelativeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected BaseLoadingHolder getLoadingHolder() {
        return new LoadingHolder(getContext());
    }

    @Override
    protected BaseErrorHolder getErrorHolder() {
        return new ErrorHolder(getContext());
    }
}
