//package com.mtime.bussiness.main;
//
//import android.content.Context;
//
//import com.google.android.material.tabs.TabLayout;
//import android.view.ViewGroup;
//
//import com.kk.taurus.uiframe.v.ContentHolder;
//import com.mtime.R;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.Unbinder;
//
///**
// * @author ZhouSuQiang
// * @date 2018/8/29
// */
//public class MainHolder extends ContentHolder<String> {
//    private static final String SP_KEY_NEWBIE_GUIDE_MAIN_LABEL = "main_guide";
//
//    @BindView(R.id.activity_main_tab_layout)
//    TabLayout mTabLayout;
//    @BindView(R.id.activity_main_content_fl)
//    ViewGroup mContentLayout;
//
//    private Unbinder mUnbinder;
//
//    public MainHolder(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void onCreate() {
//
//    }
//
//    @Override
//    public void onHolderCreated() {
//        setContentView(R.layout.activity_main);
//        mUnbinder = ButterKnife.bind(this, mRootView);
//
////        showNewbieGuide(); //2019-8月版本去掉引导层
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        if(null != mUnbinder) {
//            mUnbinder.unbind();
//        }
//    }
//
//    /**
//     *
//     * @param index
//     */
//    public void selectedTab(int index) {
//        if(null != mTabLayout) {
//            mTabLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    if (null != mTabLayout) {
//                        TabLayout.Tab tab = mTabLayout.getTabAt(index);
//                        if (null != tab) {
//                            tab.select();
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    /** 新手引导是否正在显示 */
//    public boolean hasNewbieGuide() {
//        return false;
//        //2019-8月版本去掉引导层
////        return !getActivity().getSharedPreferences("NewbieGuide", 0).contains(SP_KEY_NEWBIE_GUIDE_MAIN_LABEL);
//    }
//
//    /**
//     * 显示新手引导提示
//     */
//    /*private void showNewbieGuide() {
//        //2019-8月版本去掉引导层
//        mTabLayout.post(new Runnable() {
//            boolean isRemoved;
//
//            @Override
//            public void run() {
//                TabLayout.Tab tab = mTabLayout.getTabAt(2);
//                if (null != tab && null != tab.getCustomView()) {
//                    View tabView = tab.getCustomView();
//                    Rect rect = ViewUtils.getLocationInView(getActivity().findViewById(android.R.id.content), tabView);
//                    RectF rectF = new RectF(rect);
//                    rectF.right += tabView.getWidth();
//                    rectF.bottom -= MScreenUtils.dp2px(6);
//                    rectF.top += MScreenUtils.dp2px(4);
//
//                    HighlightOptions options = new HighlightOptions.Builder()
//                            .setOnHighlightDrewListener(new OnHighlightDrewListener() {
//                                @Override
//                                public void onHighlightDrew(Canvas canvas, RectF rectF) {
//                                    Paint paint = new Paint();
//                                    paint.setColor(Color.WHITE);
//                                    paint.setStyle(Paint.Style.STROKE);
//                                    paint.setStrokeWidth(5);
//                                    paint.setPathEffect(new DashPathEffect(new float[]{9, 9}, 0));
//                                    RectF rf = new RectF(rectF);
//                                    rf.inset(-8, -8);
//                                    canvas.drawRoundRect(rf, 90, 90, paint);
//                                }
//                            })
//                            .build();
//
//                    NewbieGuide.with(getActivity())
//                            .setLabel(SP_KEY_NEWBIE_GUIDE_MAIN_LABEL)
//                            .setOnGuideChangedListener(new OnGuideChangedListener() {
//                                @Override
//                                public void onShowed(Controller controller) {
//                                    // 用户无操作展示3秒后自动消失
//                                    mTabLayout.postDelayed(() -> {
//                                        if (!isRemoved) {
//                                            controller.remove();
//                                        }
//                                    }, 3000);
//                                }
//
//                                @Override
//                                public void onRemoved(Controller controller) {
//                                    isRemoved = true;
//                                }
//                            })
//                            .addGuidePage(
//                                    GuidePage.newInstance()
//                                            .addHighLightWithOptions(rectF, HighLight.Shape.ROUND_RECTANGLE, 90, options)
//                                            .setBackgroundColor(getActivity().getResources().getColor(R.color.color_66000000))
//                                            //点击蒙版区域任一位置蒙层都消失
//                                            .setEverywhereCancelable(true)
//                                            .setLayoutRes(R.layout.layout_main_newbie_guide, R.id.layout_main_newbie_guide_iknow_iv)
//                            ).show();
//                }
//            }
//        });
//    }*/
//}
