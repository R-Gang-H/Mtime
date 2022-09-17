package com.mtime.bussiness.ticket.movie.boxoffice.fragment;

import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;


import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.boxoffice.api.HomeBoxOfficeMainApi;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeMainBean;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListBean;
import com.mtime.bussiness.ticket.movie.boxoffice.holder.BoxOfficeHolder;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.statistic.baidu.BaiduConstants;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/6/21
 * @desc 票房Fragment
 */
public class BoxOfficeFragment extends BaseFrameUIFragment<HomeBoxOfficeTabListBean, BoxOfficeHolder> implements ViewPager.OnPageChangeListener {

    public static final String KEY_FROM_TYPE = "fromType";
    // fromTy值
    public static final int FROM_TYPE_GLOBAL = 0;
    public static final int FROM_TYPE_INLAND = 1;
    // Tab title
    private static final String TAB_TITLE_INLAND = "内地";
    private static final String TAB_TITLE_NORTH_AMERICA = "北美";
    public static final String TAB_TITLE_GLOBAL = "全球";

    private int mFromType;   //0:默认，设置为全球页签  1：点击内地票房榜。第一个tab

    private HomeBoxOfficeMainApi mApi;

    public static BoxOfficeFragment newInstance(int fromType) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FROM_TYPE, fromType);
        BoxOfficeFragment fragment = new BoxOfficeFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new BoxOfficeHolder(mContext, getChildFragmentManager(), this);
    }

    @Override
    public BaseErrorHolder onBindErrorHolder() {
        return super.onBindErrorHolder();
    }

    @Override
    public BaseLoadingHolder onBindLoadingHolder() {
        return super.onBindLoadingHolder();
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);

        // 获取从Activity传来的数据
        Bundle bundle = getArguments();
        if(bundle != null) {
            mFromType = bundle.getInt(KEY_FROM_TYPE, FROM_TYPE_GLOBAL);
        }

        //父页面的PN和refer
        if(getParentFragment() instanceof BaseFrameUIFragment) {
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIFragment) getParentFragment()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIFragment) getParentFragment()).getRefer());
        } else if(getActivity() instanceof BaseFrameUIActivity){
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIActivity) getActivity()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIActivity) getActivity()).getRefer());
        }

        if (null == mApi) {
            mApi = new HomeBoxOfficeMainApi();
        }

        getUserContentHolder().setFromType(mFromType);
        initListener();
    }

    private void initListener() {

    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
        onLoadState();
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mApi) {
            mApi.cancel();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        String tabTitle = getUserContentHolder().getTabTitle(position);
        // 埋点
        String secRegion = "";
        if(!TextUtils.isEmpty(tabTitle)) {
            if (tabTitle.equals(TAB_TITLE_INLAND)) {
                secRegion = "domestic";
            } else if (tabTitle.equals(TAB_TITLE_NORTH_AMERICA)) {
                secRegion = "US";
            } else if (tabTitle.equals(TAB_TITLE_GLOBAL)) {
                secRegion = "international";
            }
        }
        mBaseStatisticHelper.assemble1(
                "boxOffice", null,
                secRegion, null,
                "click", null, null).submit();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    // 请求数据
    private void requestData() {
        // 获取Tab列表
        mApi.loadTabList(new NetworkManager.NetworkListener<HomeBoxOfficeMainBean>() {
            @Override
            public void onSuccess(HomeBoxOfficeMainBean result, String showMsg) {
                setPageState(BaseState.SUCCESS);
                if (null != result) {
                    List<HomeBoxOfficeTabListBean> topList = result.getTopList();
                    HomeBoxOfficeTabListBean top;
                    if(CollectionUtils.isNotEmpty(topList)) {
                        for(int i = 0, size = topList.size(); i < size; i++) {
                            top = topList.get(i);
                            if(top.getTitle().equals(getResources().getString(R.string.movie_global_boxoffice_title))) {
                                setData(top);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<HomeBoxOfficeMainBean> exception, String showMsg) {
                setPageState(BaseState.ERROR);
            }
        });
    }

}
