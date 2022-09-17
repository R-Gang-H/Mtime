package com.mtime.bussiness.video.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.i.IUserHolder;
import com.kk.taurus.uiframe.listener.OnHolderListener;
import com.kk.taurus.uiframe.v.NoTitleBarContainer;

/**
 * Created by Taurus on 2017/9/30.
 */

public class TransBgNoTitleBarContainer extends NoTitleBarContainer {

    public TransBgNoTitleBarContainer(Context context, IUserHolder userHolder) {
        super(context, userHolder);
    }

    public TransBgNoTitleBarContainer(Context context, IUserHolder mUserHolder, OnHolderListener onHolderListener) {
        super(context, mUserHolder, onHolderListener);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRootContainer.setBackgroundColor(Color.parseColor("#1C2635"));
    }

}
