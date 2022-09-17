package com.mtime.bussiness.ticket.movie.dialog;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.mtime.R;
import com.mtime.base.dialog.BaseDialogFragment;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MToastUtils;
import com.mtime.base.views.MDataErrorView;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.movie.adapter.FilmSourceAdapter;
import com.mtime.bussiness.ticket.movie.bean.ExternalPlayInfosBean;
import com.mtime.bussiness.ticket.movie.bean.ExternalPlayPlayListBean;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.h5.StatisticH5;
import com.mtime.util.JumpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;

/**
 * Created by zhuqiguang on 2018/6/20.
 * website www.zhuqiguang.cn
 * 选择片源dialog
 */
public class FilmSourceChoiceDialog extends BaseDialogFragment implements OnItemClickListener {
    private static final String TAG = "FilmSourceChoiceDialog";
    public static final String MOVIE_ID = "movie_id";
    @BindView(R.id.dialog_film_choice_close_iv)
    ImageView mCloseIv;
    @BindView(R.id.dialog_film_choice_state)
    MDataErrorView mStateView;
    @BindView(R.id.dialog_film_choice_resource_rv)
    RecyclerView mResourceRv;
    Unbinder unbinder;
    private TicketApi mApi;
    private FilmSourceAdapter mAdapter;
    private ExternalPlayInfosBean mPlayBean;
    private final List<ExternalPlayPlayListBean> mDatas = new ArrayList<>();
    private String mMovieId;
    private BaseFrameUIActivity mActivity;


    public static FilmSourceChoiceDialog show(String movieId, FragmentManager fragmentManager) {
        FilmSourceChoiceDialog dialog = new FilmSourceChoiceDialog();
        Bundle args = new Bundle();
        args.putString(MOVIE_ID, movieId);
        dialog.setArguments(args);
        dialog.showAllowingStateLoss(fragmentManager);
        return dialog;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dialog_film_source_choice;
    }

    @Override
    public void bindView(View view) {
        mActivity = (BaseFrameUIActivity) getContext();
        unbinder = ButterKnife.bind(this, view);
        Bundle arguments = getArguments();
        mMovieId = arguments.getString(MOVIE_ID);
//        mAdapter.setEmptyView(R.layout.layout_movie_resouce_empty, (ViewGroup) mResourceRv.getParent());
        mApi = new TicketApi();
        mStateView.setLoadingResource(true, R.drawable.loading_progress);
//        mStateView.getLoadingIv().setBackground(getResources().getDrawable(com.mtime.rankgame.R.drawable.loading_bg));
        mStateView.setEmptyDrawable(R.drawable.icon_movie_empty);
        mStateView.setEmptyTitle(getString(R.string.movie_empty_tips));
        getExternalPlayInfos();
    }

    private void getExternalPlayInfos() {
        mStateView.showLoading();
        mApi.getExternalPlayInfos(mMovieId, listener);
    }

    NetworkManager.NetworkListener listener = new NetworkManager.NetworkListener<ExternalPlayInfosBean>() {
        @Override
        public void onSuccess(ExternalPlayInfosBean bean, String s) {
            if (null != mStateView) {
                mStateView.hideLoading();
                if (bean == null) {
                    mStateView.showEmptyView();
                    return;
                }
                initView(bean);
            }
        }

        @Override
        public void onFailure(NetworkException<ExternalPlayInfosBean> networkException, String s) {
            if (null != mStateView) {
                mStateView.hideLoading();
                mStateView.showEmptyView();
                MToastUtils.showShortToast(s);
            }
        }
    };

    private void initView(ExternalPlayInfosBean bean) {
        mPlayBean = bean;
        mResourceRv.post(new Runnable() {
            @Override
            public void run() {
                mAdapter = new FilmSourceAdapter(mDatas);
                mResourceRv.setAdapter(mAdapter);
                mAdapter.setOnItemClickListener(FilmSourceChoiceDialog.this);
                mAdapter.addData(bean.getPlaylist());
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.ViewsBottomDialog;
    }

    @Override
    protected float getDimAmount() {
        return 0.12f;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        mApi.cancel();
    }

    @Optional
    @OnClick({R.id.dialog_film_choice_close_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_film_choice_close_iv:
                Map<String, String> businessParam = new HashMap<>();
                businessParam.put(StatisticConstant.MOVIE_ID, mMovieId);
                StatisticPageBean bean = mActivity.assemble("moviesourcelist", null, "cancel", null, null, null, businessParam);
                StatisticManager.getInstance().submit(bean);
                dismissAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        List<ExternalPlayPlayListBean> playlist = mPlayBean.getPlaylist();
        ExternalPlayPlayListBean playListBean = playlist.get(position);

        Map<String, String> businessParam = new HashMap<>();
        businessParam.put(StatisticConstant.MOVIE_ID, mMovieId);
        businessParam.put("sourceName", playListBean.getPlaySourceName());
        StatisticPageBean bean = mActivity.assemble("moviesourcelist", null, "playSource", null, "click", null, businessParam);
        StatisticManager.getInstance().submit(bean);

        JumpUtil.startCommonWebActivity(getContext(), playListBean.getPlayUrl(), StatisticH5.PN_H5, null,
                true, false, true, false, false, true, bean.toString());

        dismissAllowingStateLoss();
    }
}
