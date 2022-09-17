package com.mtime.bussiness.ticket.movie.details.holder;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.ticket.movie.adapter.MediaReviewPagerAdapter;
import com.mtime.bussiness.ticket.movie.bean.MediaReviewBean;
import com.mtime.bussiness.ticket.movie.bean.Medias;
import com.mtime.frame.App;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author vivian.wei
 * @date 2019/5/24
 * @desc 影片媒体评论Holder
 */
public class MovieMediaReviewHolder extends ContentHolder<MediaReviewBean> implements ViewPager.OnPageChangeListener {

    @BindView(R.id.layout_movie_sub_page_back_iv)
    ImageView mBackIv;
    @BindView(R.id.layout_movie_sub_page_title_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_movie_media_review_smarttablayout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.activity_movie_media_review_viewpager)
    ViewPager mViewPager;

    private Unbinder mUnbinder;
    private PagerAdapter mPageAdapter;
    private TabLayoutHelper mTabLayoutHelper;
    private final ArrayList<View> mPageViews = new ArrayList<>();

    public MovieMediaReviewHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_media_review);
        initView();
        initListener();
    }

    private void initView() {
        mUnbinder = ButterKnife.bind(this, mRootView);
        mTitleTv.setText(getResource().getString(R.string.st_media));
        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, TabLayoutHelper.TYPE_OF_NORMAL);
    }

    private void initListener() {
        mBackIv.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void refreshView() {
        super.refreshView();

        if(mData == null || CollectionUtils.isEmpty(mData.getMedias())) {
            return;
        }

        List<Medias> medias = mData.getMedias();
        ArrayList<String> tabTitles = new ArrayList<>();
        Medias media;
        View pageView;
        TextView summaryTv;
        for(int i = 0, size = medias.size(); i < size; i++) {
            media = medias.get(i);
            tabTitles.add(media.getName());
            pageView = View.inflate(mContext, R.layout.item_movie_media_review, null);
            summaryTv = pageView.findViewById(R.id.item_movie_media_review_summary_tv);
            if(CollectionUtils.isNotEmpty(media.getComments())) {
                // 只显示第一条
                summaryTv.setText(media.getComments().get(0).getSummary());
                summaryTv.setMovementMethod(ScrollingMovementMethod.getInstance());
            }
            if(i > 0) {
                pageView.setVisibility(View.GONE);
            }
            mPageViews.add(pageView);
        }
        mPageAdapter = new MediaReviewPagerAdapter(mPageViews, tabTitles);
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setViewPager(mViewPager);
        mTabLayoutHelper.initDefaultFocus();
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPageViews.get(position).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
