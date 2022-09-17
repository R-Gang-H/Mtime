package com.mtime.bussiness.mine.comments.movie;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.mine.comments.movie.lng.LongMovieCommentFragment;
import com.mtime.bussiness.mine.comments.movie.shot.ShortMovieCommentFragment;
import com.mtime.bussiness.ticket.widget.TabSizeTextView;
import com.mtime.frame.BaseActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-09
 * <p>
 * 我的影评页面
 */
public class MyMovieCommentsActivity extends BaseActivity {

    public static void start(Context context, String refer) {
        Intent starter = new Intent(context, MyMovieCommentsActivity.class);
        dealRefer(context, refer, starter);
        context.startActivity(starter);
    }

    @BindView(R.id.tab_layout)
    SmartTabLayout mTabLayout;
    @BindView(R.id.vp)
    ViewPager mViewPager;

    private final List<Fragment> mFragments = new ArrayList<>();
    private final List<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_my_long_short_movie_comments);

        ButterKnife.bind(this);

        mTabLayout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup c, int position, PagerAdapter adapter) {
                TabSizeTextView tv = new TabSizeTextView(c.getContext());
                tv.setText(adapter.getPageTitle(position));
                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(getResources().getColorStateList(R.color.color_movie_still_tab_text));

                tv.setSelectedTextSize(17);
                tv.setDefaultTextSize(15);
                tv.setSelectedBold(true);

                tv.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                tv.setPadding(0, 0, 0,
                        MScreenUtils.dp2px(5));
                return tv;
            }
        });

        LinearLayout tabLayout = (LinearLayout) mTabLayout.getChildAt(0);
        tabLayout.setGravity(Gravity.BOTTOM);

        mFragments.add(new ShortMovieCommentFragment());
        mFragments.add(new LongMovieCommentFragment());

        mTitles.add("短评");
        mTitles.add("长评");

        mViewPager.setAdapter(new CommentAdapter(getSupportFragmentManager()));

        mTabLayout.setViewPager(mViewPager);
    }

    private class CommentAdapter extends FragmentPagerAdapter {

        CommentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }
    }

    @OnClick(R.id.back_iv)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
