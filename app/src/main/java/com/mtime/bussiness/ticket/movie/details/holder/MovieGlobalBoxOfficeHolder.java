package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.frame.App;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/6/20
 * @desc 影片全球票房榜页Holder
 */
public class MovieGlobalBoxOfficeHolder extends ContentHolder<Void> {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;

    private Unbinder mUnbinder;

    public MovieGlobalBoxOfficeHolder(Context context) {
        super(context);

    }

    @Override
    public void onCreate() {
        setContentView(R.layout.activity_movie_global_boxoffice);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();

        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mTitleTv.setText(getResource().getString(R.string.movie_global_boxoffice_title));
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();
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

    public int getContainerId(){
        return R.id.activity_movie_global_boxoffice_fl;
    }

}
