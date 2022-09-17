package com.mtime.bussiness.ticket.stills;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.beans.Photo;
import com.mtime.beans.PhotoAll;
import com.mtime.beans.PhotoType;
import com.mtime.bussiness.ticket.widget.TabSizeTextView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-26
 */
class MovieStillsHolder extends ContentHolder<Void> {

    @BindView(R.id.movie_title_tv)
    TextView movieTitleTv;
    @BindView(R.id.pager_tab)
    SmartTabLayout pagerTab;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private PhotoPagerAdapter mAdapter;

    private final List<Pair<String, MovieStillsFragment>> mFragments = new ArrayList<>();
    private int mType;

    MovieStillsHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        setContentView(R.layout.activity_movie_stills_layout);
        ButterKnife.bind(this, mRootView);

        String title = getIntent().getStringExtra(MovieStillsActivity.KEY_PHOTO_LIST_TITLE);
        movieTitleTv.setText(title);
        mType = getIntent().getIntExtra(MovieStillsActivity.KEY_PHOTO_LIST_TARGET_TYPE, -1);

        pagerTab.setCustomTabView((container, position, adapter) -> {
            TabSizeTextView tv = new TabSizeTextView(container.getContext());
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(mContext.getResources().getColorStateList(R.color.color_movie_still_tab_text));

            tv.setSelectedTextSize(17);
            tv.setDefaultTextSize(15);
            tv.setSelectedBold(true);

            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            if (position == 0) {
                tv.setPadding(
                        MScreenUtils.dp2px(15), 0,
                        MScreenUtils.dp2px(25),
                        MScreenUtils.dp2px(5));
            } else if (position == adapter.getCount() - 1) {
                tv.setPadding(
                        MScreenUtils.dp2px(25), 0,
                        MScreenUtils.dp2px(15),
                        MScreenUtils.dp2px(5));
            } else {
                tv.setPadding(
                        MScreenUtils.dp2px(25), 0,
                        MScreenUtils.dp2px(25),
                        MScreenUtils.dp2px(5));
            }

            tv.setText(adapter.getPageTitle(position));
            return tv;
        });

        LinearLayout tabLayout = (LinearLayout) pagerTab.getChildAt(0);
        tabLayout.setGravity(Gravity.BOTTOM);

        mAdapter = new PhotoPagerAdapter();
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Bundle args = new Bundle();
                args.putCharSequence("name", mAdapter.getPageTitle(position));
                onHolderEvent(0, args);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @OnClick(R.id.title_back)
    void onViewClicked() {
        finish();
    }

    void showImages(PhotoAll photos) {
        if(photos == null || CollectionUtils.isEmpty(photos.imageTypes)
                || CollectionUtils.isEmpty(photos.images)) {
            pagerTab.setVisibility(View.GONE);
            return;
        }

        List<PhotoType> types = photos.imageTypes;
        for (PhotoType type : types) {
            ArrayList<Photo> images = new ArrayList<>();
            if (type.type > 0) {
                // 具体类型图片
                for (Photo photo : photos.images) {
                    if (type.type == photo.type) {
                        images.add(photo);
                    }
                }
            } else {
                // 显示所有
                images.addAll(photos.images);
            }

            if(CollectionUtils.isNotEmpty(images)) {
                MovieStillsFragment fragment = new MovieStillsFragment();
                Bundle args = new Bundle();
                args.putParcelableArrayList("images", images);
                args.putInt("type", mType);
                fragment.setArguments(args);

                Pair<String, MovieStillsFragment> fragmentPair = Pair.create(type.typeName, fragment);
                mFragments.add(fragmentPair);
            }
        }

        mAdapter.notifyDataSetChanged();
        pagerTab.setViewPager(viewPager);

        if (types.size() == 1) {
            pagerTab.setVisibility(View.GONE);
        }
    }

    private class PhotoPagerAdapter extends FragmentPagerAdapter {

        private PhotoPagerAdapter() {
            super(getActivity().getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position).second;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).first;
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
