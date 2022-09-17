package com.mtime.bussiness.ticket.movie.boxoffice.fragment;

import java.util.List;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseErrorHolder;
import com.kk.taurus.uiframe.v.BaseLoadingHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.ticket.movie.boxoffice.api.HomeBoxOfficeApi;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListDetailBean;
import com.mtime.bussiness.ticket.movie.boxoffice.bean.HomeBoxOfficeTabListDetailMovieBean;
import com.mtime.bussiness.ticket.movie.boxoffice.holder.RecommendBoxOfficeHolder;
import com.mtime.event.entity.CityChangedEvent;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.frame.BaseFrameUIFragment;
import com.mtime.statistic.large.MapBuild;
import com.mtime.util.JumpUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

/**
 * @author vivian.wei
 * @date 2019/6/21
 * @desc 推荐票房榜Fragment
 */
public class RecommendBoxOfficeFragment extends BaseFrameUIFragment<HomeBoxOfficeTabListDetailBean, RecommendBoxOfficeHolder>
        implements OnRefreshListener, OnItemClickListener, OnItemChildClickListener {

    public static final String KEY_PAGE_SUB_AREA_ID = "page_sub_area_id";
    private static final String PAGE_SUB_AREA_ID_INLAND = "48266";         // 内地
    private static final String PAGE_SUB_AREA_ID_NORTH_AMERICA = "48267";  // 北美
    private static final String PAGE_SUB_AREA_ID_GLOBAL = "48268";         // 全球
    private static final int PAGE_INDEX = 1;

    // 参数_推荐位Id
    private String mPageSubAreaId = PAGE_SUB_AREA_ID_GLOBAL;
    private HomeBoxOfficeApi mApi;
    private String mCityId;
    private boolean mFirstLoad = true;

    @Override
    public ContentHolder onBindContentHolder() {
        return new RecommendBoxOfficeHolder(mContext, this, this);
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

        // 选中Tab的推荐位Id
        Bundle bundle = getArguments();
        mPageSubAreaId = bundle.getString(KEY_PAGE_SUB_AREA_ID);

        //父页面的PN和refer
        if(getParentFragment() instanceof BaseFrameUIFragment) {
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIFragment) getParentFragment()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIFragment) getParentFragment()).getRefer());
        } else if(getActivity() instanceof BaseFrameUIActivity){
            mBaseStatisticHelper.setPageLabel(((BaseFrameUIActivity) getActivity()).getPageLabel());
            mBaseStatisticHelper.setLastPageRefer(((BaseFrameUIActivity) getActivity()).getRefer());
        }

        if (null == mApi) {
            mApi = new HomeBoxOfficeApi();
        }

        initListener();

        setPageState(BaseState.LOADING);
        requestData();
    }

    private void initListener() {
        // 刷新|加载更多
        getUserContentHolder().setRefreshLoadMoreListener(this);
    }

    @Override
    protected void onErrorRetry() {
        super.onErrorRetry();
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

    // 下拉刷新
    @Override
    public void onRefresh(RefreshLayout var1) {
        requestData();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<HomeBoxOfficeTabListDetailMovieBean> movies = adapter.getData();
        HomeBoxOfficeTabListDetailMovieBean movieBean = movies.get(position);

        // 埋点
        MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
        mapBuild.put("movieID", String.valueOf(movieBean.getId()));
        String secRegion = getLogxSecRegion();
        String refer = mBaseStatisticHelper.assemble1(
                "boxOffice", null,
                secRegion, null,
                "detail", String.valueOf(position + 1), mapBuild.build()).submit();

        // TODO: 2019/6/24 requestCode 首页之前传-1 why?
        int requestCode = 0; // 首页 -1  影片二级页 0
        JumpUtil.startMovieInfoActivity(mContext, refer, String.valueOf(movieBean.getId()), requestCode);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        if(view.getId() == R.id.item_recommend_boxoffice_ticket_tv) {
            List<HomeBoxOfficeTabListDetailMovieBean> movies = adapter.getData();
            HomeBoxOfficeTabListDetailMovieBean movieBean = movies.get(position);
            if(movieBean.getIsTicket() || movieBean.getIsPresell()) {
                // 埋点
                MapBuild<String, String> mapBuild = new MapBuild<>(mBaseStatisticHelper.getBaseParam());
                mapBuild.put("movieID", String.valueOf(movieBean.getId()));
                String secRegion = getLogxSecRegion();
                String thrRegion =  movieBean.getIsTicket() ? "ticket" : "preSell";
                String refer = mBaseStatisticHelper.assemble1(
                        "boxOffice", null,
                        secRegion, null,
                        thrRegion, String.valueOf(position + 1), mapBuild.build()).submit();

                // TODO: 2019/6/24 requestCode 首页之前传-1 why?
                int requestCode = 0; // 首页 -1  影片二级页 0
                JumpUtil.startMovieShowtimeActivity(mContext, refer, String.valueOf(movieBean.getId()),
                        movieBean.getName(), movieBean.getIsTicket(), "", requestCode);
            }
        }
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    //城市变更
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChagedCity(CityChangedEvent event) {
        if (null != event && !TextUtils.equals(mCityId, event.newCityId)) {
            requestData();
        }
    }

    // 请求数据
    private void requestData() {
        mCityId = GlobalDimensionExt.INSTANCE.getCurrentCityId();
        mApi.loadTabListDetail(mCityId, PAGE_INDEX, mPageSubAreaId, new NetworkManager.NetworkListener<HomeBoxOfficeTabListDetailBean>() {
            @Override
            public void onSuccess(HomeBoxOfficeTabListDetailBean result, String showMsg) {
                getUserContentHolder().finishRefresh(true);
                if (null != result) {
                    mFirstLoad = false;
                    setData(result);
                }
                setPageState(BaseState.SUCCESS);
            }

            @Override
            public void onFailure(NetworkException<HomeBoxOfficeTabListDetailBean> exception, String showMsg) {
                //第一次加载
                if (mFirstLoad) {
                    setPageState(BaseState.ERROR);
                } else {
                    setPageState(BaseState.SUCCESS);
                }
                getUserContentHolder().finishRefresh(false);
            }
        });
    }

    // 获取埋点SecRegion
    private String getLogxSecRegion() {
        String secRegion = "";
        if (mPageSubAreaId.equals(PAGE_SUB_AREA_ID_INLAND)) {
            secRegion = "domestic";
        } else if (mPageSubAreaId.equals(PAGE_SUB_AREA_ID_NORTH_AMERICA)) {
            secRegion = "US";
        } else if (mPageSubAreaId.equals(PAGE_SUB_AREA_ID_GLOBAL)) {
            secRegion = "international";
        }
        return secRegion;
    }

}
