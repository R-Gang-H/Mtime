package com.mtime.bussiness.common.widget;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/4/23
 */
public class TabLayoutHelper implements ViewPager.OnPageChangeListener, SmartTabLayout.TabProvider {
    public static final int TYPE_OF_NORMAL = 0x0;
    public static final int TYPE_OF_WEIGHT = 0x1;

    @IntDef({TYPE_OF_NORMAL, TYPE_OF_WEIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TabLayoutType {
    }

    private final SmartTabLayout mTabLayout;
    private final int mType;
    private View mCurTabView;
    private final SparseArray<String> mSubTitleStr = new SparseArray<>();
    private final List<TextView> subTitleViews = new ArrayList<>();

    public TabLayoutHelper(@NonNull SmartTabLayout tabLayout, @TabLayoutType int type) {
        this.mTabLayout = tabLayout;
        this.mType = type;

        mTabLayout.setOnPageChangeListener(this);
        mTabLayout.setCustomTabView(this);
    }

    @Override
    public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
        int layoutId;
        switch (mType) {
            case TYPE_OF_WEIGHT:
                layoutId = R.layout.common_layout_custom_tab_with_weight_and_subtitle;
                break;

            default:
                layoutId = R.layout.common_layout_custom_tab;
                break;
        }
        View tabView = LayoutInflater.from(container.getContext()).inflate(layoutId, container, false);
        ((TextView) tabView.findViewById(R.id.common_layout_custom_tab_title_tv)).setText(adapter.getPageTitle(position));
        TextView subTitleView = tabView.findViewById(R.id.common_layout_custom_tab_subtitle_tv);
        if (null != subTitleView) {
            if(!TextUtils.isEmpty(mSubTitleStr.get(position))) {
                subTitleView.setVisibility(View.VISIBLE);
                subTitleView.setText(mSubTitleStr.get(position));
            }else {
                subTitleView.setVisibility(View.GONE);
            }
            subTitleViews.add(subTitleView);
        }
        return tabView;
    }

    /**
     * 子标题文本
     * @param position
     * @param str
     */
    public void addSubTitleStr(int position, String str) {
        mSubTitleStr.append(position, str);
    }

    /**
     * 设置子标题文本
     * @param strs
     */
    public void setSubTitleStrs(List<String> strs) {
        for (int i = 0; i < strs.size(); i++) {
            mSubTitleStr.append(i, strs.get(i));
        }
    }

    /**
     * 获取指定位置的副标题
     * @param position
     */
    public TextView getSubTitleView(int position) {
        if(CollectionUtils.isNotEmpty(subTitleViews)) {
            return subTitleViews.get(position);
        }
        return null;
    }

    /**
     * 设置完数据后调用此方法，手动使0tab也具有相关的选中效果
     * tab初始化选中0，但不会回调，所以这里需要手动回调下
     */
    public void initDefaultFocus() {
        mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                // tab初始化选中0，但不会回调，所以这里需要手动回调下
                onPageSelected(0);
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        View tabView = mTabLayout.getTabAt(position);
        if(null != tabView) {
            if (null != mCurTabView) {
                TextView titleView = mCurTabView.findViewById(R.id.common_layout_custom_tab_title_tv);
                titleView.setTextSize(15);
                titleView.getPaint().setFakeBoldText(false);
                /*View subTitleView = mCurTabView.findViewById(R.id.common_layout_custom_tab_subtitle_tv);
                if (null != subTitleView) {
                    ((TextView) subTitleView).setTextSize(9);
                    ((TextView) subTitleView).getPaint().setFakeBoldText(false);
                }*/
            }

            TextView titleView = tabView.findViewById(R.id.common_layout_custom_tab_title_tv);
            titleView.setTextSize(17);
            titleView.getPaint().setFakeBoldText(true);
            /*TextView subTitleView = tabView.findViewById(R.id.common_layout_custom_tab_subtitle_tv);
            if (null != subTitleView) {
                subTitleView.setTextSize(11);
                subTitleView.getPaint().setFakeBoldText(true);
            }*/

            mCurTabView = tabView;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
