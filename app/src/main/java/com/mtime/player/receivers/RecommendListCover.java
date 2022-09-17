package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.mtime.R;
import com.mtime.bussiness.common.widget.TabLayoutHelper;
import com.mtime.bussiness.video.CategoryDataManager;
import com.mtime.bussiness.video.adapter.RecommendCategoryVideoTabAdapter;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.player.DataInter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by JiaJunHui on 2018/6/21.
 */
public class RecommendListCover extends BaseCover {

    @BindView(R.id.root_view)
    View mRootView;
    @BindView(R.id.tab_layout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private RecommendCategoryVideoTabAdapter mTabAdapter;
    private TabLayoutHelper mTabLayoutHelper;
    private Unbinder unbinder;

    private final FragmentManager mFragmentManager;

    public RecommendListCover(Context context) {
        super(context);
        mFragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_recommend_video_list_cover, null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        unbinder = ButterKnife.bind(this, getView());

        mTabLayoutHelper = new TabLayoutHelper(mTabLayout, TabLayoutHelper.TYPE_OF_NORMAL);

        mRootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecommendListState(false);
            }
        });

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    DataInter.Key.KEY_ERROR_SHOW_STATE
            };
        }

        @Override
        public void onValueUpdate(String key, Object value) {
            if(DataInter.Key.KEY_ERROR_SHOW_STATE.equals(key)){
                boolean error = (boolean) value;
                if(error){
                    setRecommendListState(false);
                }
            }
        }
    };

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        unbinder.unbind();
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        setRecommendListState(false);
    }

    private void setRecommendListState(boolean state){
        if(state && isDataAvailable()){
            setCoverVisibility(View.VISIBLE);
            onShow();
            getGroupValue().putBoolean(DataInter.Key.KEY_RECOMMEND_LIST_STATE, true);
        }else{
            setCoverVisibility(View.GONE);
            getGroupValue().putBoolean(DataInter.Key.KEY_RECOMMEND_LIST_STATE, false);
        }
    }

    private boolean isDataAvailable(){
        List<CategoryVideosBean.Category> categoryList = CategoryDataManager.get().getCategoryList();
        return categoryList!=null && categoryList.size()>0;
    }

    private void onShow() {
        final int currCategoryIndex = CategoryDataManager.get().getCurrentCategoryIndex();
        List<CategoryVideosBean.Category> categoryList = CategoryDataManager.get().getCategoryList();
        int movieId = CategoryDataManager.get().getMovieId();
        if(mTabAdapter==null){
            mTabAdapter = new RecommendCategoryVideoTabAdapter(mFragmentManager, categoryList, movieId);
            mViewPager.setAdapter(mTabAdapter);
            mTabLayout.setViewPager(mViewPager);
            mTabLayoutHelper.initDefaultFocus();
        }else{
            mTabAdapter.notifySelectIndex();
        }
        //scroll category page to current play category and index.
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(currCategoryIndex);
            }
        });
    }

    @Override
    public void onPlayerEvent(int i, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.ReceiverEvent.EVENT_REQUEST_RECOMMEND_LIST_CHANGE:
                boolean state = bundle != null && bundle.getBoolean(EventKey.BOOL_DATA);
                setRecommendListState(state);
                break;
        }
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(DataInter.CoverLevel.COVER_LEVEL_RECOMMEND_LIST);
    }
}
