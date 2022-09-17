package com.mtime.frame.container;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kk.taurus.uiframe.d.BaseTitleBarParams;
import com.kk.taurus.uiframe.i.IUserHolder;
import com.kk.taurus.uiframe.listener.OnHolderListener;
import com.kk.taurus.uiframe.v.NormalTitleBarContainer;

/**
 * Created by mtime on 2017/11/6.
 */

public class TransTitleBarContainer extends NormalTitleBarContainer {

    public TransTitleBarContainer(Context context, IUserHolder userHolder) {
        super(context, userHolder);
    }

    public TransTitleBarContainer(Context context, IUserHolder mUserHolder, OnHolderListener onHolderListener) {
        super(context, mUserHolder, onHolderListener);
    }

    @Override
    protected void onLayoutLogic() {

        //add content
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentContainer = new FrameLayout(mContext);
        if(mUserHolder.contentHolder==null)
            return;
        mContentContainer.addView(mUserHolder.contentHolder.getHolderView()
                ,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mRootContainer.addView(mContentContainer,layoutParams);

        //add title bar
        if(mUserHolder.titleBarHolder==null)
            return;
        BaseTitleBarParams baseTitleBarParams = mUserHolder.titleBarHolder.getTitleBarParams();
        if(baseTitleBarParams == null)
            return;
        int height = baseTitleBarParams.titleBarHeight;
        if(height <= 0)
            return;
        mRootContainer.addView(mUserHolder.titleBarHolder.getHolderView()
                ,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height));

    }

}
