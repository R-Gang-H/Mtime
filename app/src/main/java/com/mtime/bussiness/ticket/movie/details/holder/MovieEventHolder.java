package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.bean.MovieSecretBean;
import com.mtime.bussiness.ticket.movie.details.adapter.MovieEventAdapter;
import com.mtime.frame.App;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/5/23
 * @desc 影片幕后揭秘Holder
 */
public class MovieEventHolder extends ContentHolder<MovieSecretBean> {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_event_recyclerview)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;
    private MovieEventAdapter mAdapter;

    public MovieEventHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_event);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mTitleTv.setText(getResource().getString(R.string.st_movie_event));

        mAdapter = new MovieEventAdapter(null);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        List<String> list = mData.getList();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        mAdapter.addData(list);
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