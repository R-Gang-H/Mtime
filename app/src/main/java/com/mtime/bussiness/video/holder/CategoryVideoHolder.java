package com.mtime.bussiness.video.holder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.OrientationHelper;
import com.mtime.bussiness.video.adapter.CategoryVideoTabAdapter;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.event.NextCategoryEvent;
import com.mtime.bussiness.video.event.PlayEvent;
import com.mtime.bussiness.video.fragment.NewVideoListFragment;
import com.mtime.bussiness.video.view.ScrollSettingViewPager;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;
import com.mtime.player.bean.MTimeVideoData;
import com.mtime.player.receivers.RecommendListCover;
import com.mtime.bussiness.video.PreviewVideoPlayer;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class CategoryVideoHolder extends ContentHolder<CategoryVideosBean> {

    @BindView(R.id.act_category_video_list_tablayout)
    SmartTabLayout mTablayout;
    @BindView(R.id.act_category_video_list_view_pager)
    ScrollSettingViewPager mViewPager;
    @BindView(R.id.act_category_video_list_title_tv)
    TextView mTitleTv;
    @BindView(R.id.act_category_video_list_back_iv)
    ImageView mBackIcon;
    @BindView(R.id.act_category_video_list_top_rl)
    RelativeLayout mTitleLayout;
    @BindView(R.id.act_category_video_list_full_screen_player_container)
    FrameLayout mFullScreenContainer;

    private Unbinder mUnbinder;

    private FragmentManager mFragmentManager;

    private List<CategoryVideosBean.Category> mCategoryList = new ArrayList<>();

    private CategoryVideoTabAdapter mTabAdapter;

    private int mMovieId;

    private OnCategoryVideoHolderListener mOnCategoryVideoHolderListener;

    private boolean mScrollChange;

    private OrientationHelper orientationHelper;
    private boolean isLandScape;
    private TabLayoutHelper mTabLayoutHelper;
    private RecommendListCover mRecommendListCover;

    public CategoryVideoHolder(Context context) {
        super(context);
    }

    public CategoryVideoHolder(Context context, OnCategoryVideoHolderListener onCategoryVideoHolderListener, FragmentManager fragmentManager) {
        super(context);
        this.mOnCategoryVideoHolderListener = onCategoryVideoHolderListener;
        this.mFragmentManager = fragmentManager;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_category_video);
        EventBus.getDefault().register(this);
        mUnbinder = ButterKnife.bind(this, mRootView);

        orientationHelper = new OrientationHelper((Activity) mContext);
        mTabLayoutHelper = new TabLayoutHelper(mTablayout, TabLayoutHelper.TYPE_OF_NORMAL);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        mRecommendListCover = new RecommendListCover(mContext);

        mTablayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_UP:
                        if(mScrollChange){
                            mOnCategoryVideoHolderListener.onTabScrolled();
                            mScrollChange = false;
                        }
                        return true;
                }
                return false;
            }
        });

        mTablayout.setOnScrollChangeListener(new SmartTabLayout.OnScrollChangeListener() {
            @Override
            public void onScrollChanged(int scrollX, int oldScrollX) {
                mScrollChange = true;
            }
        });

        mTablayout.setOnTabClickListener(new SmartTabLayout.OnTabClickListener() {
            @Override
            public void onTabClicked(int position) {
                mOnCategoryVideoHolderListener.onTabClicked(position, mTabAdapter.getTabName(position));
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(mTabAdapter!=null){
                    CategoryVideosBean.Category category = mCategoryList.get(position);
                    CategoryDataManager.get().updateCurrentCategoryType(category.getType());
                    mViewPager.post(new Runnable() {
                        @Override
                        public void run() {
                            mTabAdapter.onSelectChange(position);
                        }
                    });
                }
                mOnCategoryVideoHolderListener.onPageSelected(position, mTabAdapter.getTabName(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        PreviewVideoPlayer.get().setOnNextPlayListener(new PreviewVideoPlayer.OnNextPlayListener() {
            @Override
            public void onNextPlay(CategoryVideosBean.RecommendVideoItem item, int type, int index) {
                if (null != CategoryDataManager.get()) {
                    int categoryTypeIndex = CategoryDataManager.get().getCategoryTypeIndex(type);
                    if (!isLandScape && PlayerHelper.isTopActivity((Activity) mContext)) {
                        if (categoryTypeIndex == mViewPager.getCurrentItem()) {
                            NewVideoListFragment fragmentItem = mTabAdapter.getItem(categoryTypeIndex);
                            if (fragmentItem != null) {
                                fragmentItem.onNextPlayAttach(index);
                            }
                        } else {
                            mViewPager.setCurrentItem(categoryTypeIndex);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mViewPager.setScrollEnable(false);
            mFullScreenContainer.setVisibility(View.VISIBLE);
            PreviewVideoPlayer.get().attachContainer(mFullScreenContainer);
            PreviewVideoPlayer.get().setReceiverGroupFullScreenState(mContext, mRecommendListCover);
        }else if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            mViewPager.setScrollEnable(true);
            mFullScreenContainer.setVisibility(View.GONE);
        }
    }

    public void sendHiddenRecommendListCover(){
        //send hidden event for recommend list
        Bundle bundle = BundlePool.obtain();
        bundle.putBoolean(EventKey.BOOL_DATA, false);
        PreviewVideoPlayer.get().sendReceiverEvent(DataInter.ReceiverEvent.EVENT_REQUEST_RECOMMEND_LIST_CHANGE, bundle, null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRecommendListPlayEvent(PlayEvent event){
        CategoryVideosBean.RecommendVideoItem item = CategoryDataManager.get().getItem(event.categoryType, event.index);
        if(item!=null){
            sendHiddenRecommendListCover();
            MTimeVideoData data = new MTimeVideoData(String.valueOf(item.getvId()), item.getVideoSource());
            PreviewVideoPlayer.get().play(data);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onChangeNextTab(NextCategoryEvent event){
        if(mTabAdapter==null) {
            return;
        }
        int count = mTabAdapter.getCount();
        int currentItem = mViewPager.getCurrentItem();
        if(currentItem < count-1){
            currentItem++;
            mViewPager.setCurrentItem(currentItem);
        }
    }

    public void portraitUpdateChangeItem(int index){
        mViewPager.setCurrentItem(index);
    }

    public void portraitAttachCurrentItem(){
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                NewVideoListFragment item = mTabAdapter.getItem(mViewPager.getCurrentItem());
                if(item!=null){
                    item.onPortraitCurrentAttach();
                }
            }
        });
    }

    public int getCurrItemIndex(){
        return mViewPager.getCurrentItem();
    }

    @Override
    public void onDataChanged(CategoryVideosBean data) {
        super.onDataChanged(data);
        if (mCategoryList == null) {
            mCategoryList = new ArrayList<>();
        }
        if (null != mViewPager) {
            mCategoryList.addAll(mData.getCategory());
            mTabAdapter = new CategoryVideoTabAdapter(mFragmentManager, mCategoryList, mMovieId);
            mViewPager.setAdapter(mTabAdapter);
            if (null != mTablayout) {
                mTablayout.setViewPager(mViewPager);
            }
            if (null != mTabLayoutHelper) {
                mTabLayoutHelper.initDefaultFocus();
            }

            mViewPager.post(new Runnable() {
                @Override
                public void run() {
                    mTabAdapter.onSelectChange(0);
                }
            });
        }
    }

    @Override
    public void refreshView() {
        super.refreshView();
        if (!TextUtils.isEmpty(mData.getMovieTitle()) && mTitleTv!=null) {
            mTitleTv.setText(mData.getMovieTitle());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        orientationHelper.enable();
    }

    @Override
    public void onStop() {
        super.onStop();
        orientationHelper.disable();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mViewPager==null) {
            return;
        }
        int currentItem = mViewPager.getCurrentItem();
        int currentCategoryIndex = CategoryDataManager.get().getCurrentCategoryIndex();
        if(currentItem!=currentCategoryIndex && currentCategoryIndex!=-1){
            mViewPager.setCurrentItem(currentCategoryIndex);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orientationHelper.destroy();
        EventBus.getDefault().unregister(this);
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /*@Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_category_video_list_custom_tab, container, false);
        TextView titleView = view.findViewById(R.id.layout_category_video_list_custom_tab_title_tv);
        CategoryVideosBean.Category category = mCategoryList.get(position);
        if (!TextUtils.isEmpty(category.getName())) {
            titleView.setText(category.getName());
        }
        return view;
    }*/

    @OnClick(R.id.act_category_video_list_back_iv)
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.act_category_video_list_back_iv:
                finish();
                break;
        }
    }

    public interface OnCategoryVideoHolderListener{
        void onTabScrolled();
        void onTabClicked(int position, String tabName);
        void onPageSelected(int position, String tabName);
    }

    public void setMovieId(int movieId) {
        mMovieId = movieId;
    }

}
