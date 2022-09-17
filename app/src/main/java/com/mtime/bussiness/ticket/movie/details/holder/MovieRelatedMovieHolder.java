package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.ticket.movie.bean.RelatedMovieBean;
import com.mtime.bussiness.ticket.movie.bean.RelatedMoviesBean;
import com.mtime.bussiness.ticket.movie.bean.RelatedTypeMoviesBean;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieRelatedMovieAdapter;
import com.mtime.bussiness.ticket.movie.details.widget.MovieActorFloatingItemDecoration;
import com.mtime.frame.App;
import com.mtime.util.JumpUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/5/22
 * @desc 影片关联电影Holder
 */
public class MovieRelatedMovieHolder extends ContentHolder<RelatedMoviesBean> implements OnItemClickListener {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_related_movie_recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private MovieRelatedMovieAdapter mAdapter;
    private MovieActorFloatingItemDecoration mFloatingItemDecoration; // 与演职员表共用float title

    public MovieRelatedMovieHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_related_movie);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mTitleTv.setText(getResource().getString(R.string.st_related_movie));

        mAdapter = new MovieRelatedMovieAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        // 列表分组日期
        mFloatingItemDecoration = new MovieActorFloatingItemDecoration(mContext);
        mFloatingItemDecoration.setTitleHeight(MScreenUtils.dp2px(30));
        mFloatingItemDecoration.setTitleViewmTitleViewPaddingLeft(MScreenUtils.dp2px(15));
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(mContext, R.color.color_F2F3F6_alpha_92));
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(mContext, R.color.color_8798AF));
        mFloatingItemDecoration.setTitleTextSize(MScreenUtils.sp2px( 12));
        mRecyclerView.addItemDecoration(mFloatingItemDecoration);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        List<RelatedTypeMoviesBean> types = mData.getList();
        if (CollectionUtils.isEmpty(types)) {
            return;
        }

        // 数据分组
        RelatedTypeMoviesBean type;
        int titleKey = 0;
        for (int i = 0, size = types.size(); i < size; i++) {
            type = types.get(i);
            if(type!= null && CollectionUtils.isNotEmpty(type.getMovies())) {
                titleKey = mAdapter.getItemCount();
                if(null != mFloatingItemDecoration) {
                    mFloatingItemDecoration.appendTitles(titleKey, type.getTypeName());
                }
                int movieSize = type.getMovies().size();
                type.getMovies().get(movieSize - 1).setGroupEnd(true);
                mAdapter.addData(type.getMovies());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_movie_sub_page_back_iv:
                onHolderEvent(App.MOVIE_SUB_PAGE_EVENT_CODE_BACK, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // 无埋点
        List<RelatedMovieBean> movieBeans = adapter.getData();
        RelatedMovieBean movieBean = movieBeans.get(position);
        JumpUtil.startMovieInfoActivity(mContext, "", String.valueOf(movieBean.getMovieID()), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (null != mUnbinder) {
            mUnbinder.unbind();
        }
    }

}
