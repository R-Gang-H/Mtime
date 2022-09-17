package com.mtime.frame;


import com.mtime.base.mvp.MvpBaseFragment;
import com.mtime.base.mvp.MvpBaseFragmentPresenter;
import com.mtime.base.mvp.MvpBaseView;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-13
 *
 * base库中的mvp架构，考虑到切换问题，暂时不考虑使用此架构了
 */
/*public abstract class AbsBaseFragment<P extends MvpBaseFragmentPresenter<V>, V extends MvpBaseView> extends MvpBaseFragment<P, V> {

    private BaseStatisticHelper mBaseStatisticHelper = new BaseStatisticHelper(false);

    public void setLastPageRefer(String refer){
        mBaseStatisticHelper.setLastPageRefer(refer);
    }

    public String getRefer() {
        return mBaseStatisticHelper.getRefer();
    }

    public String getPageLabel() {
        return mBaseStatisticHelper.getPageLabel();
    }

    public void getParentFragmentPageLabelAndRefer() {
        //作用子页面，直接取父页面的PN和refer
        if(getParentFragment() instanceof BaseFrameUIFragment) {
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIFragment) getParentFragment()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIFragment) getParentFragment()).getRefer());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //页面基础埋点-open
        mBaseStatisticHelper.onResume(getContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        //页面基础埋点-close/timing
        mBaseStatisticHelper.onPause(getContext());
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }
}*/
