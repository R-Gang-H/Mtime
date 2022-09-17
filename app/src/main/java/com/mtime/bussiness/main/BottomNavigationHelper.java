//package com.mtime.bussiness.main;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.graphics.drawable.StateListDrawable;
//import android.text.TextUtils;
//import android.view.View;
//import android.view.animation.AccelerateInterpolator;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.google.android.material.tabs.TabLayout;
//import com.kotlin.android.community.ui.home.CommunityFragment;
//import com.kotlin.android.home.ui.home.HomeFragment;
//import com.kotlin.android.mine.ui.home.MineVMFragment;
//import com.mtime.R;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.imageload.ImageProxyUrl;
//import com.mtime.base.imageload.ImageShowLoadCallback;
//import com.mtime.base.statistic.bean.StatisticPageBean;
//import com.mtime.base.utils.MTimeUtils;
//import com.mtime.bussiness.common.utils.MSharePreferenceHelper;
//import com.mtime.bussiness.ticket.TabTicketFragment;
//import com.mtime.constant.SpManager;
//import com.mtime.frame.App;
//
//import java.util.Date;
//
//import androidx.annotation.DrawableRes;
//import androidx.annotation.NonNull;
//import androidx.appcompat.content.res.AppCompatResources;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
///**
// * @author ZhouSuQiang
// * @date 2018/8/28
// *
// * 主页面底部导航辅助类
// */
//public class BottomNavigationHelper implements TabLayout.OnTabSelectedListener {
//    static final String SP_KEY_TAB_COMMUNITY_CLICK_TIME = "tab_community_click_time";
//
//    public static final int TYPE_HOME = 1;      //首页
//    public static final int TYPE_TICKET = 2;    //购票
//    public static final int TYPE_MALL = 3;      //商城
//    public static final int TYPE_MINE = 5;      //我的
////    public static final int TYPE_VIDEO = 7;     //视频
//    public static final int TYPE_GAME = 8;      //时光猜电影
//    public static final int TYPE_COMMUNITY = 9; //社区
//
//    private final int[][] drawableRes = {
//            {TYPE_HOME, R.string.str_tabname_home, R.drawable.ic_home_tab_normal, R.drawable.ic_home_tab_selected},
//            {TYPE_TICKET, R.string.str_tabname_payticket, R.drawable.ic_ticket_tab_normal, R.drawable.ic_ticket_tab_selected},
//            {TYPE_COMMUNITY, R.string.str_tabname_community, R.drawable.ic_forum_tab_normal, R.drawable.ic_forum_tab_selected},
////            {TYPE_VIDEO, R.string.str_tabname_video, R.drawable.tab_video01, R.drawable.tab_video02},
//            {TYPE_MINE, R.string.str_tabname_user, R.drawable.ic_profile_tab_normal, R.drawable.ic_profile_tab_selected}
//    };
//
//    private final String[][] netRes = {
//            {App.getInstance().HOME_NAME, App.getInstance().HOME_ICON_UNSELECTED, App.getInstance().HOME_ICON_SELECTED},
//            {App.getInstance().TICKET_NAME, App.getInstance().TICKET_ICON_UNSELECTED, App.getInstance().TICKET_ICON_SELECTED},
//            {App.getInstance().COMMUNITY_NAME, App.getInstance().COMMUNITY_ICON_UNSELECTED, App.getInstance().COMMUNITY_ICON_SELECTED},
////            {App.getInstance().VIDEO_NAME, App.getInstance().VIDEO_ICON_UNSELECTED, App.getInstance().VIDEO_ICON_SELECTED},
//            {App.getInstance().MINE_NAME, App.getInstance().MINE_ICON_UNSELECTED, App.getInstance().MINE_ICON_SELECTED}
//    };
//
//    private final static String TAG = "bottom_navigation_tag_";
//    private TabLayout mTabLayout;
//    private FragmentManager mFragmentManager;
//    private Fragment mCurFragment;
//    private StatisticPageBean mStatisticPageBean;
//
//    /**
//     * 设置Selector。
//     */
//    private StateListDrawable newSelector(@NonNull Context context, Bitmap normal, Bitmap selected) {
//        StateListDrawable bg = new StateListDrawable();
//        Drawable normalDrawable = new BitmapDrawable(context.getResources(), normal);
//        Drawable selectedDrawable = new BitmapDrawable(context.getResources(), selected);
//        bg.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
//        bg.addState(new int[]{}, normalDrawable);
//        return bg;
//    }
//
//    /**
//     * 设置Selector。
//     */
//    private StateListDrawable newSelector(@NonNull Context context, @DrawableRes int normal, @DrawableRes int selected) {
//        StateListDrawable bg = new StateListDrawable();
//        Drawable normalDrawable = AppCompatResources.getDrawable(context, normal);
//        Drawable selectedDrawable = AppCompatResources.getDrawable(context, selected);
//        bg.addState(new int[]{android.R.attr.state_selected}, selectedDrawable);
//        bg.addState(new int[]{}, normalDrawable);
//        return bg;
//    }
//
//    private void setNetIcon(@NonNull TabLayout.Tab tab, Bitmap normal, Bitmap selected) {
//        if(null != tab.getCustomView()) {
//            ImageView icon = tab.getCustomView().findViewById(R.id.tab_widget_icon);
//            icon.setImageDrawable(newSelector(tab.getCustomView().getContext(), normal, selected));
//        }
//    }
//
//    private void loadNetIcon(@NonNull TabLayout.Tab tab) {
//        final String localTabName = netRes[tab.getPosition()][0];
//        final String localUnSelectedName = netRes[tab.getPosition()][1];
//        final String localSelectedName = netRes[tab.getPosition()][2];
//        final String[] iconUrls = {
//                SpManager.get().getString(localUnSelectedName),
//                SpManager.get().getString(localSelectedName)
//        };
//        final boolean hasCacheTabInfo = !TextUtils.isEmpty(iconUrls[0]) && !TextUtils.isEmpty(iconUrls[1]);
//        if (hasCacheTabInfo) {
//            if(null != tab.getCustomView()) {
//                TextView text = tab.getCustomView().findViewById(R.id.tab_widget_content);
//                text.setText(SpManager.get().getString(localTabName));
//            }
//
//            final Bitmap[] bitmaps = new Bitmap[2];
//            for (int i = 0; i < iconUrls.length; i++) {
//                final int pos = i;
//                ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                        .load(iconUrls[pos])
//                        .callback(new ImageShowLoadCallback() {
//                            @Override
//                            public void onLoadCompleted(Bitmap bitmap) {
//                                bitmaps[pos] = bitmap;
//                                if (bitmaps[0] != null && bitmaps[1] != null) {
//                                    setNetIcon(tab, bitmaps[0], bitmaps[1]);
//                                }
//                            }
//
//                            @Override
//                            public void onLoadCompleted(Drawable resource) {
//                                if (resource instanceof BitmapDrawable) {
//                                    bitmaps[pos] = ((BitmapDrawable) resource).getBitmap();
//                                    if (bitmaps[0] != null && bitmaps[1] != null) {
//                                        setNetIcon(tab, bitmaps[0], bitmaps[1]);
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onLoadFailed() {
//                            }
//                        })
//                        .showload();
//            }
//        }
//    }
//
//    private void submit(Fragment fragment) {
////        if(fragment instanceof BaseFrameUIFragment) {
////            BaseFrameUIFragment uiFragment = (BaseFrameUIFragment) fragment;
////            String pageName = uiFragment.getPageLabel();
////            mStatisticPageBean = StatisticDataBuild.assemble(null != mStatisticPageBean ? mStatisticPageBean.toString() : null,
////                    "tabbar",  pageName, "", "", "", null);
////            StatisticManager.getInstance().submit(mStatisticPageBean);
////            uiFragment.setRefer(mStatisticPageBean.toString());
////
//////            String type = ToolsUtils.getNetworkType(mTabLayout.getContext());
////        }
//    }
//
//    @Override
//    public void onTabSelected(TabLayout.Tab tab) {
//        if(null == mFragmentManager || null == mTabLayout || null == tab.getTag()) {
//            return;
//        }
//        final String tag = TAG + tab.getTag();
//        mCurFragment = mFragmentManager.findFragmentByTag(tag);
//        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
//        switch ((int)tab.getTag()) {
//            case TYPE_HOME:
//                if(null == mCurFragment) {
//                    mCurFragment = HomeFragment.Companion.newInstance();
//                }
//                break;
//            case TYPE_TICKET:
//                if(null == mCurFragment) {
//                    mCurFragment = TabTicketFragment.newInstance();
//                }
//                break;
//            case TYPE_MINE:
//                if(null == mCurFragment) {
//                    mCurFragment = MineVMFragment.Companion.newInstance();
//                }
//                break;
//            case TYPE_COMMUNITY:
//                if(null == mCurFragment) {
//                    mCurFragment = CommunityFragment.Companion.newInstance();
//                }
//                if(null != tab.getCustomView()) {
//                    View pointView = tab.getCustomView().findViewById(R.id.tab_widget_redpoint);
//                    if(pointView.getVisibility() == View.VISIBLE) {
//                        pointView.setVisibility(View.GONE);
//                        MSharePreferenceHelper.get().putLong(SP_KEY_TAB_COMMUNITY_CLICK_TIME, System.currentTimeMillis());
//                    }
//                }
//            default:
//                break;
//        }
//
//        if (null != mCurFragment) {
//            if (mCurFragment.isAdded()) {
//                fragmentTransaction.show(mCurFragment);
//            } else {
//                fragmentTransaction.add(R.id.activity_main_content_fl, mCurFragment, tag);
//            }
//        }
//
//        submit(mCurFragment);
//        fragmentTransaction.commitAllowingStateLoss();
//    }
//
//    @Override
//    public void onTabUnselected(TabLayout.Tab tab) {
//        if(null != mFragmentManager) {
//            Fragment fragment = mFragmentManager.findFragmentByTag(TAG + tab.getTag());
//            if (null != fragment) {
//                mFragmentManager.beginTransaction().hide(fragment).commitAllowingStateLoss();
//            }
//        }
//    }
//
//    @Override
//    public void onTabReselected(TabLayout.Tab tab) {
//        if(mCurFragment instanceof MainCommunicational) {
//            ((MainCommunicational)mCurFragment).onHandleMainEvent(MainCommunicational.EVENT_ON_TAB_RESELECTED, null);
//        }
//    }
//
//    public Fragment getCurFragment() {
//        return mCurFragment;
//    }
//
//    Animation in;
//    Animation out;
//
//    public void setup(@NonNull TabLayout tabLayout, FragmentManager fragmentManager) {
//        this.mTabLayout = tabLayout;
//        this.mFragmentManager = fragmentManager;
//
//        mTabLayout.addOnTabSelectedListener(this);
//
//        TabLayout.Tab tab;
//        ImageView icon;
//        TextView text;
//        for (int[] res : drawableRes) {
//            tab = tabLayout.newTab().setCustomView(R.layout.activity_home_tab_view);
//            if(null != tab.getCustomView()) {
//                icon = tab.getCustomView().findViewById(R.id.tab_widget_icon);
//                text = tab.getCustomView().findViewById(R.id.tab_widget_content);
//                icon.setImageDrawable(newSelector(tabLayout.getContext(), res[2], res[3]));
//                text.setText(res[1]);
//                showRedPoint(res[0], tab.getCustomView().findViewById(R.id.tab_widget_redpoint));
//            }
//            tab.setTag(res[0]);
//            tabLayout.addTab(tab);
//            loadNetIcon(tab);
//        }
//
//        in = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
//                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f);
//        in.setDuration(300);
//        in.setInterpolator(new AccelerateInterpolator());
//        in.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//                TabLayout.Tab tab = mTabLayout.getTabAt(0);
//                if(null != tab) {
//                    View view = tab.getCustomView();
//                    if (null != view) {
//                        // 这里给tab_widget_content加白色背景是为了让动画效果显示是从文字上方开始移动的
//                        view.findViewById(R.id.tab_widget_content).setBackgroundColor(Color.WHITE);
//                    }
//                }
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                TabLayout.Tab tab = mTabLayout.getTabAt(0);
//                if(null != tab) {
//                    View view = tab.getCustomView();
//                    if (null != view) {
//                        // 这里给tab_widget_content加白色背景是为了让动画效果显示是从文字上方开始移动的
//                        view.findViewById(R.id.tab_widget_content).setBackgroundColor(Color.TRANSPARENT);
//                    }
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        out = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
//                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f);
//        out.setDuration(300);
//        out.setInterpolator(new AccelerateInterpolator());
//    }
//
//    private void showRedPoint(int tabType, View pointView) {
//        switch (tabType) {
//            case TYPE_COMMUNITY: //社区
//                // 社区显示红点的规则：时隔1个自然日并且大于6个小时则显示出红点
//                long preClickTime = MSharePreferenceHelper.get().getLongValue(SP_KEY_TAB_COMMUNITY_CLICK_TIME, 0);
//                boolean diff = (System.currentTimeMillis() - preClickTime) > (6 * 3600000);
//                if(!MTimeUtils.isToday(new Date(preClickTime)) && diff) {
//                    pointView.setVisibility(View.VISIBLE);
//                }
//                break;
//        }
//    }
//
//    public void destroy() {
//        mTabLayout = null;
//        mFragmentManager = null;
//        mCurFragment = null;
//    }
//}
