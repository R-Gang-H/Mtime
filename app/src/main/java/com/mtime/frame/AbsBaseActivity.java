package com.mtime.frame;

import android.os.Bundle;

import com.mtime.base.mvp.MvpBaseActivity;
import com.mtime.base.mvp.MvpBasePresenter;
import com.mtime.base.mvp.MvpBaseView;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-10
 *
 * 项目内部的base类
 *
 * base库中的mvp架构，考虑到切换问题，暂时不考虑使用此架构了
 */
/*public abstract class AbsBaseActivity<P extends MvpBasePresenter<V>, V extends MvpBaseView> extends MvpBaseActivity<P, V> {

    private BaseStatisticHelper mBaseStatisticHelper = new BaseStatisticHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseStatisticHelper.setLastPageRefer(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //页面基础埋点-open
        mBaseStatisticHelper.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //页面基础埋点-close/timing
        mBaseStatisticHelper.onPause(this);
    }

    @Override
    protected void initDatas() {

    }
}*/
