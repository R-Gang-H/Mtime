//package com.mtime.bussiness.mine.activity;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.core.content.ContextCompat;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.SparseBooleanArray;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.aspsine.irecyclerview.IRecyclerView;
//import com.aspsine.irecyclerview.OnRefreshListener;
//import com.mtime.base.imageload.ImageHelper;
//import com.mtime.base.imageload.ImageProxyUrl;
//import com.mtime.base.utils.MScreenUtils;
//import com.mtime.frame.App;
//import com.mtime.frame.BaseActivity;
//import com.mtime.R;
//import com.kotlin.android.user.UserManager;
//import com.mtime.base.statistic.StatisticConstant;
//import com.mtime.base.statistic.bean.StatisticPageBean;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.beans.ArticleInfoBean;
//import com.mtime.bussiness.mine.adapter.OfficialAccountsRecyclerAdapter;
//import com.mtime.bussiness.mine.bean.OfficialAccountInfoBean;
//import com.mtime.bussiness.mine.bean.OfficialAccountListBean;
//import com.mtime.bussiness.mine.bean.PublicAddFollowBean;
//import com.mtime.bussiness.mine.bean.PublicCancelFollowBean;
//import com.mtime.bussiness.mine.login.activity.LoginActivity;
//import com.mtime.bussiness.video.event.FollowStateEditEvent;
//import com.mtime.common.utils.Utils;
//import com.mtime.mtmovie.widgets.ActionSheetDialog;
//import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
//import com.mtime.network.ConstantUrl;
//import com.mtime.network.RequestCallback;
//import com.mtime.statistic.large.StatisticManager;
//import com.mtime.statistic.large.mine.StatisticMine;
//import com.mtime.util.HttpUtil;
//import com.mtime.util.ImageURLManager;
//import com.mtime.util.UIUtil;
//import com.mtime.widgets.ExpandableTextView;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * ???????????????
// * Created by wangdaban on 2017/5/23.
// */
//
//public class OfficialAccountActivity extends BaseActivity implements View.OnClickListener {
//
//    private RequestCallback officialAccountInfoCallback;
//    private RequestCallback articleCallback;
//    private long mMediaId;//?????????ID
//    private OfficialAccountInfoBean infoBean;
//    private OfficialAccountListBean listBean;
//    private IRecyclerView iRecyclerView;
//    private OfficialAccountsRecyclerAdapter adapter;
//    private LinearLayout topSuspenstionLayout;//????????????layout
//    private int tabBottomOffest = 0;
//    private RelativeLayout layout_toolbar_common;//???????????????layout
//    private final int cureSelectTab = 1;// 1:??????  2:??????
//    private boolean isTabInList = true;//tab???????????????????????????????????????,??????true
//    //title
//    private RelativeLayout titlebar_middle;//titlebar ???????????????????????????
//    private ImageView ivTitleBarHead;//???????????????
//    private TextView tvTitleBarName;//???????????????
//
//    //header  start
//    private RelativeLayout header_continer;//header continer
//    private View headerView;
//    private ExpandableTextView textIntroduce;//???????????????
//    private TextView tvName;//???????????????
//    private TextView tvContentCount;//?????????????????????
//    private TextView tvAttentionCount;//?????????????????????
//    private ImageView ivHead;//???????????????
//    private RelativeLayout layoutContentCount;//?????????????????????layout
//    private RelativeLayout layoutAttentionCount;//?????????????????????layout
//    private ImageView headerBgView;//?????????????????????
//    private LinearLayout layout_tabs_suspenstion;//header???tab?????????layout
//    private LinearLayout layout_tab;//header???tab??????layout
////    private TextView tv_tabs_all;//tab??????
////    private TextView tv_tabs_hot;//tab??????
////    private View line_tabs_all;//tab line??????
////    private View line_tabs_hot;//tab line??????
////    private int offsetHeaderBg = 0;
//    //header end
//
//    private boolean isConcerned;//???????????????
//    private long followNum;
//    private TextView tvfollow;
//
//    //footer start
//    //?????????????????????????????????????????????????????????????????????
//    private boolean isSlidingToLast = false;
//    private LoadMoreFooterView loadMoreView;
//    private int pageIndex = 1;
//    private RequestCallback officialAccountListCallback;
//    public final String logxParamType = "articleID";
//
//
//    @Override
//    protected void onInitVariable() {
//        Intent intent = getIntent();
//        mMediaId = intent.getLongExtra(App.getInstance().OFFICIAL_ACCOUNT_ID, 0);
//        setPageLabel(StatisticMine.PN_OFFICIAL_ACCOUNT);
//        putBaseStatisticParam(StatisticConstant.MEDIA_ID, String.valueOf(mMediaId));
//    }
//
//
//    @Override
//    protected void onInitView(Bundle savedInstanceState) {
//        this.setContentView(R.layout.activity_officalaccount);
//        //?????????????????????????????????
//        findViewById(R.id.iv_titlebar_right).setVisibility(View.VISIBLE);
//        layout_toolbar_common = findViewById(R.id.layout_toolbar_common);
//        topSuspenstionLayout = findViewById(R.id.lyaout_top_suspension);
//        titlebar_middle = findViewById(R.id.titlebar_middle);
//        ivTitleBarHead = findViewById(R.id.iv_titlebar_headImg);
//        tvTitleBarName = findViewById(R.id.tv_titlebar_name);
//        titlebar_middle.setAlpha(0);
//
//
//        //???????????????
//        tvfollow = findViewById(R.id.btn_add_concern);
//        tvfollow.setOnClickListener(this);
//        //header
//        headerView = getLayoutInflater().inflate(R.layout.act_official_accounts_recycler_header, null);
//        textIntroduce = headerView.findViewById(R.id.tv_header_head_introduce);
//        tvName = headerView.findViewById(R.id.tv_head_title);
//        tvContentCount = headerView.findViewById(R.id.tv_contentCount);
//        tvAttentionCount = headerView.findViewById(R.id.tv_attentionCount);
//        ivHead = headerView.findViewById(R.id.iv_header_head);
//        layoutContentCount = headerView.findViewById(R.id.layout_header_content);
//        layoutAttentionCount = headerView.findViewById(R.id.layout_header_attention);
//        headerBgView = headerView.findViewById(R.id.iv_header_bg);
//        header_continer = headerView.findViewById(R.id.header_continer);
//        layout_tabs_suspenstion = headerView.findViewById(R.id.layout_tabs_suspenstion);
//        layout_tab = headerView.findViewById(R.id.layout_tab);
////        tv_tabs_all = (TextView) headerView.findViewById(R.id.tv_tabs_all);
////        tv_tabs_hot = (TextView) headerView.findViewById(R.id.tv_tabs_hot);
////        line_tabs_all = headerView.findViewById(R.id.line_tabs_all);
////        line_tabs_hot = headerView.findViewById(R.id.line_tabs_hot);
//
//        iRecyclerView = findViewById(R.id.iRecyclerView);
//        loadMoreView = (LoadMoreFooterView) iRecyclerView.getLoadMoreFooterView();
//        loadMoreView.setIsShowTheEnd(true);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        iRecyclerView.setLayoutManager(layoutManager);
//        iRecyclerView.setHasFixedSize(true);
//        iRecyclerView.setRefreshEnabled(true);
//        iRecyclerView.setLoadMoreEnabled(true);
//        iRecyclerView.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                onListRefresh();
//            }
//        });
//
//        findViewById(R.id.titlebar_middle).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                iRecyclerView.scrollToPosition(0);
//            }
//        });
//    }
//
//    @Override
//    protected void onInitEvent() {
//
//        officialAccountInfoCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//                infoBean = (OfficialAccountInfoBean) o;
//                isConcerned = infoBean.isHasAttention();
//                if (!isConcerned) {
//                    tvfollow.setVisibility(View.VISIBLE);
//                } else {
//                    tvfollow.setVisibility(View.GONE);
//                }
//                followNum = infoBean.getAttentionCount();
//                if (null == infoBean) {
//                    UIUtil.dismissLoadingDialog();
//                    return;
//                }
//                UIUtil.dismissLoadingDialog();
//                setBasicInfo();
//                requestListData();
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast(e.getLocalizedMessage());
//            }
//        };
//
//
//        officialAccountListCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                UIUtil.dismissLoadingDialog();
//                OfficialAccountListBean itemBean = (OfficialAccountListBean) o;
//                if (pageIndex == 1) {
//                    iRecyclerView.setRefreshing(false);
//                    loadMoreView.setStatus(LoadMoreFooterView.Status.GONE);
//                    listBean = itemBean;
//                    if (null == listBean) {
//                        UIUtil.dismissLoadingDialog();
//                        return;
//                    }
//                    UIUtil.dismissLoadingDialog();
//                    adapter = new OfficialAccountsRecyclerAdapter(OfficialAccountActivity.this, listBean.getList(), headerView, null);
//                    adapter.setMediaID(mMediaId);
//                    adapter.setPublicName(infoBean.getName());
//                    iRecyclerView.setIAdapter(adapter);
//                    //????????????ID
//                    String articleIds = getArticleIds(itemBean.getList());
//                    //?????????????????? ?????????
//                    requestCount(articleIds);
//                } else {
//                    if (itemBean.getList() == null || itemBean.getList().size() == 0) {
//                        loadMoreView.setStatus(LoadMoreFooterView.Status.THE_END);
//                        return;
//                    }
//                    loadMoreView.setStatus(LoadMoreFooterView.Status.GONE);
//                    if (listBean != null && listBean.getList() != null && itemBean != null && itemBean.getList() != null && adapter != null) {
//                        listBean.getList().addAll(itemBean.getList());
//                        //????????????ID
//                        String articleIds = getArticleIds(itemBean.getList());
//                        //?????????????????? ?????????
//                        requestCount(articleIds);
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                if (pageIndex == 1) {
//                    iRecyclerView.setRefreshing(false);
//                    MToastUtils.showShortToast(e.getLocalizedMessage());
//                } else {
//                    loadMoreView.setStatus(LoadMoreFooterView.Status.ERROR);
//                }
//            }
//        };
//
//
//        articleCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                final ArticleInfoBean articleBean = (ArticleInfoBean) o;
//                if (articleBean != null && articleBean.getList() != null) {
//                    List<ArticleInfoBean.ListBean> beans = articleBean.getList();
//                    for (int j = 0; j < beans.size(); j++) {
//                        for (int i = 0; i < listBean.getList().size(); i++) {
//                            if (beans.get(j).getArticleId() == listBean.getList().get(i).getArticleId()) {
//                                listBean.getList().get(i).setListBean(beans.get(j));
//                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//
//            }
//        };
//
//
//        iRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                int visibleItemCount = layoutManager.getChildCount();
//                boolean triggerCondition = visibleItemCount > 0 && newState == RecyclerView.SCROLL_STATE_IDLE && canTriggerLoadMore(recyclerView);
//                if (triggerCondition) {
//                    onLoadMoreItem();
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                //dx?????????????????????????????????dy??????????????????????????????
//                //??????0???????????????????????????
//                //????????????0 ???????????????????????????
//                isSlidingToLast = dy > 0;
//                if (tabBottomOffest == 0) {
//                    tabBottomOffest = headerView.getBottom();
//                }
//                if (headerView.getBottom() < headerView.getHeight()) {
//                    int minShowHeight = layout_toolbar_common.getHeight() + layout_tabs_suspenstion.getHeight();//titlebar?????????255??????headerView.getBottom()????????????
//                    if (headerView.getBottom() <= minShowHeight) {//??????
//                        if (layout_tab.getParent() != topSuspenstionLayout) {//??????
//                            layout_toolbar_common.getBackground().setAlpha(255);
//                            titlebar_middle.setAlpha(1.0f);
//                            isTabInList = false;
//                            layout_tabs_suspenstion.removeView(layout_tab);
//                            topSuspenstionLayout.addView(layout_tab);
//                            layout_tab.setBackgroundColor(ContextCompat.getColor(OfficialAccountActivity.this, R.color.color_1c2635));
//                            //changeTabSelectColor();
//                        }
//                    } else {//???????????????
//                        if (layout_tab.getParent() != layout_tabs_suspenstion) {//???????????????
//                            isTabInList = true;
//                            topSuspenstionLayout.removeView(layout_tab);
//                            layout_tabs_suspenstion.addView(layout_tab);
//                            layout_tab.setBackgroundColor(ContextCompat.getColor(OfficialAccountActivity.this, R.color.white));
//                            //changeTabSelectColor();
//                        }
//
//                        float alpha = (float) (tabBottomOffest - headerView.getBottom() + layout_tabs_suspenstion.getHeight()) / tabBottomOffest;
//                        alpha = alpha < 0 ? 0 : (alpha > 1 ? 1f : alpha);
//                        layout_toolbar_common.getBackground().setAlpha((int) (alpha * 255));
//                        titlebar_middle.setAlpha(alpha);
//                    }
//                } else {
//                    initColor();
//                }
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        initColor();
//        requestData();//??????
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        recoverColor();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        recoverColor();
//    }
//
//    private void recoverColor() {
//        layout_toolbar_common.getBackground().setAlpha(255);
//        titlebar_middle.setAlpha(1.0f);
//    }
//
//    private void initColor() {
//        layout_toolbar_common.getBackground().setAlpha(30);
//        titlebar_middle.setAlpha(0);
//    }
//
//
//    public boolean canTriggerLoadMore(RecyclerView recyclerView) {
//        View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
//        int position = recyclerView.getChildLayoutPosition(lastChild);
//        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        int totalItemCount = layoutManager.getItemCount();
//        return totalItemCount - 1 == position;
//    }
//
//    //???????????????????????????
//    private void requestListData() {
//        Map<String, String> param = new HashMap<>(1);
//        param.put("number", String.valueOf(mMediaId));
//        param.put("tag", String.valueOf(0));
//        param.put("pageIndex", String.valueOf(pageIndex));
//        HttpUtil.get(ConstantUrl.OFFICIAL_ACCOUNT_LIST, param, OfficialAccountListBean.class, officialAccountListCallback);
//
//    }
//
//
//    @Override
//    protected void onRequestData() {
//    }
//
//    private void requestData() {
//        UIUtil.showLoadingDialog(this);
//        Map<String, String> param = new HashMap<>(1);
//        param.put("number", String.valueOf(mMediaId));
//        HttpUtil.get(ConstantUrl.OFFICIAL_ACCOUNT_INFO, param, OfficialAccountInfoBean.class, officialAccountInfoCallback);
//    }
//
//    private void onListRefresh() {
//        pageIndex = 1;
//        Map<String, String> param = new HashMap<>(1);
//        param.put("number", String.valueOf(mMediaId));
//        param.put("tag", String.valueOf(0));
//        param.put("pageIndex", String.valueOf(pageIndex));
//        HttpUtil.get(ConstantUrl.OFFICIAL_ACCOUNT_LIST, param, OfficialAccountListBean.class, officialAccountListCallback);
//    }
//
//    private void onLoadMoreItem() {
//        if (loadMoreView.canLoadMore()) {
//            loadMoreView.setStatus(LoadMoreFooterView.Status.LOADING);
//            pageIndex++;
//            Map<String, String> param = new HashMap<>(1);
//            param.put("number", String.valueOf(mMediaId));
//            param.put("tag", String.valueOf(0));
//            param.put("pageIndex", String.valueOf(pageIndex));
//            HttpUtil.get(ConstantUrl.OFFICIAL_ACCOUNT_LIST, param, OfficialAccountListBean.class, officialAccountListCallback);
//        }
//    }
//
//
//    @Override
//    protected void onLoadData() {
//
//    }
//
//    @Override
//    protected void onUnloadData() {
//
//    }
//
//    /**
//     * ???????????????????????????UI
//     */
//    private void setBasicInfo() {
//        if (!TextUtils.isEmpty(infoBean.getName())) {
//            tvName.setText(infoBean.getName());
//            tvTitleBarName.setText(infoBean.getName());
//        } else {
//            tvName.setVisibility(View.GONE);
//        }
//        if (!TextUtils.isEmpty(infoBean.getDesc())) {
//            textIntroduce.setVisibility(View.VISIBLE);
//            textIntroduce.setConvertText(new SparseBooleanArray(), 0, infoBean.getDesc());
//        } else {
//            textIntroduce.setVisibility(View.GONE);
//        }
//        if (infoBean.getContentCount() <= 0) {
//            layoutContentCount.setVisibility(View.GONE);
//        } else if (infoBean.getContentCount() > 999999999) {
//            tvContentCount.setText("999999+");
//            layoutContentCount.setVisibility(View.VISIBLE);
//        } else {
//            layoutContentCount.setVisibility(View.VISIBLE);
//            tvContentCount.setText(String.valueOf(infoBean.getContentCount()));
//        }
//        if (infoBean.getContentCount() <= 0) {
//            layoutAttentionCount.setVisibility(View.GONE);
//        } else if (infoBean.getContentCount() > 999999999) {
//            tvAttentionCount.setText("999999+");
//            layoutAttentionCount.setVisibility(View.VISIBLE);
//        } else {
//            layoutAttentionCount.setVisibility(View.VISIBLE);
//            tvAttentionCount.setText(String.valueOf(infoBean.getAttentionCount()));
//        }
//        volleyImageLoader.displayImage(infoBean.getAvatar(), ivHead, R.drawable.my_home_logout_head, R.drawable.my_home_logout_head,
//                Utils.dip2px(this, 75), Utils.dip2px(this, 75), ImageURLManager.SCALE_TO_FIT, null);
//        volleyImageLoader.displayImage(infoBean.getAvatar(), ivTitleBarHead, R.drawable.my_home_logout_head, R.drawable.my_home_logout_head,
//                Utils.dip2px(this, 20), Utils.dip2px(this, 20), ImageURLManager.SCALE_TO_FIT, null);
//        setHeaderBgWithRadius();
//    }
//
//    private void setHeaderBgWithRadius() {
//        ImageHelper.with(this, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
//                .override(MScreenUtils.getScreenWidth(), MScreenUtils.dp2px(150))
//                .load(infoBean.getAvatar())
//                .view(headerBgView)
//                .blur(7, 10)
//                .placeholder(R.drawable.default_image)
//                .showload();
//    }
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.back://??????
//                recoverColor();
//                finish();
//                break;
//            case R.id.btn_add_concern://???????????????
//                Map<String, String> businessParam = new HashMap<String, String>();
//                businessParam.put(StatisticConstant.MEDIA_ID, String.valueOf(mMediaId));
//                StatisticPageBean bean = this.assemble(StatisticMine.OFFICIAL_FOLLOWBAR, null, "followBtn", null, null, null, businessParam);
//                StatisticManager.getInstance().submit(bean);
//                if (!UserManager.Companion.getInstance().isLogin()) {
//                    startActivityForResult(LoginActivity.class, 1);
//                } else {
//                    if (!isConcerned) {
//                        // ????????????
//                        Map<String, String> param = new HashMap<>(1);
//                        param.put("publicId", String.valueOf(mMediaId));
//                        HttpUtil.get(ConstantUrl.GET_PUBLIC_ADD_FOLLOW, param, PublicAddFollowBean.class, new RequestCallback() {
//
//                            @Override
//                            public void onSuccess(Object o) {
//                                UIUtil.dismissLoadingDialog();
//                                PublicAddFollowBean addFollowBean = (PublicAddFollowBean) o;
//                                if (1 == addFollowBean.getBizCode()) {
//                                    infoBean.setHasAttention(true);
//                                    isConcerned = !isConcerned;
//                                    tvAttentionCount.setText(String.valueOf(followNum + 1));
//                                    followNum = followNum + 1;
//                                    EventBus.getDefault().post(new FollowStateEditEvent(mMediaId, true));
//                                    MToastUtils.showShortToast("?????????");
//                                    tvfollow.setVisibility(View.GONE);
//                                } else if (2 == addFollowBean.getBizCode()) {
//                                    MToastUtils.showShortToast("???????????????2000??????????????????????????????");
//                                    tvfollow.setVisibility(View.GONE);
//                                } else {
//                                    MToastUtils.showShortToast(addFollowBean.getBizMsg());
//                                }
//                            }
//
//                            @Override
//                            public void onFail(Exception e) {
//                                UIUtil.dismissLoadingDialog();
//                                MToastUtils.showShortToast("???????????????" + e.getLocalizedMessage());
//                            }
//
//                        });
//                    }
//                }
//                break;
//            case R.id.titlebar_right://??????
//                String text;
//                if (!isConcerned) {
//                    text = "??????";
//                } else {
//                    text = "????????????";
//                }
//                ActionSheetDialog actionSheetDialog = new ActionSheetDialog(this).builder();
//                actionSheetDialog.addSheetItem(text, null,
//                        new ActionSheetDialog.OnSheetItemClickListener() {
//                            @Override
//                            public void onClick(int which) {
//                                if (!UserManager.Companion.getInstance().isLogin()) {
//                                    startActivityForResult(LoginActivity.class, 1);
//                                } else {
//                                    if (!isConcerned) {//??????
//                                        // ????????????
//                                        Map<String, String> businessParam = new HashMap<String, String>();
//                                        businessParam.put(StatisticConstant.MEDIA_ID, String.valueOf(mMediaId));
//                                        StatisticPageBean bean = OfficialAccountActivity.this.assemble(StatisticMine.OFFICIAL_TOP_NAVIGATION, null, "followBtn", null, "follow", null, businessParam);
//                                        StatisticManager.getInstance().submit(bean);
//                                        Map<String, String> param = new HashMap<>(1);
//                                        param.put("publicId", String.valueOf(mMediaId));
//                                        HttpUtil.get(ConstantUrl.GET_PUBLIC_ADD_FOLLOW, param, PublicAddFollowBean.class, new RequestCallback() {
//
//                                            @Override
//                                            public void onSuccess(Object o) {
//                                                UIUtil.dismissLoadingDialog();
//                                                PublicAddFollowBean addFollowBean = (PublicAddFollowBean) o;
//                                                if (1 == addFollowBean.getBizCode()) {
//                                                    EventBus.getDefault().post(new FollowStateEditEvent(mMediaId, true));
//                                                    infoBean.setHasAttention(true);
//                                                    isConcerned = !isConcerned;
//                                                    layoutAttentionCount.setVisibility(View.VISIBLE);
//                                                    tvAttentionCount.setText(String.valueOf(followNum + 1));
//                                                    followNum = followNum + 1;
//                                                    tvfollow.setVisibility(View.GONE);
//                                                    MToastUtils.showShortToast("?????????");
//                                                } else if (2 == addFollowBean.getBizCode()) {
//                                                    MToastUtils.showShortToast("???????????????2000??????????????????????????????");
//                                                } else {
//                                                    MToastUtils.showShortToast(addFollowBean.getBizMsg());
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFail(Exception e) {
//                                                UIUtil.dismissLoadingDialog();
//                                                MToastUtils.showShortToast("???????????????" + e.getLocalizedMessage());
//                                            }
//
//                                        });
//
//                                    } else {//????????????
//                                        Map<String, String> businessParam = new HashMap<String, String>();
//                                        businessParam.put(StatisticConstant.MEDIA_ID, String.valueOf(mMediaId));
//                                        StatisticPageBean bean = OfficialAccountActivity.this.assemble(StatisticMine.OFFICIAL_TOP_NAVIGATION, null, "followBtn", null, "unfollow", null, businessParam);
//                                        StatisticManager.getInstance().submit(bean);
//                                        Map<String, String> param = new HashMap<>(1);
//                                        param.put("publicId", String.valueOf(mMediaId));
//                                        HttpUtil.get(ConstantUrl.GET_PUBLIC_CANCEL_FOLLOW, param, PublicCancelFollowBean.class, new RequestCallback() {
//
//                                            @Override
//                                            public void onSuccess(Object o) {
//                                                UIUtil.dismissLoadingDialog();
//                                                PublicCancelFollowBean cancelFollowBean = (PublicCancelFollowBean) o;
//                                                if (1 == cancelFollowBean.getBizCode()) {
//                                                    infoBean.setHasAttention(false);
//                                                    isConcerned = !isConcerned;
//                                                    tvAttentionCount.setText(String.valueOf(followNum - 1));
//                                                    if (followNum - 1 <= 0) {
//                                                        layoutAttentionCount.setVisibility(View.GONE);
//                                                    }
//                                                    followNum = followNum - 1;
//                                                    tvfollow.setVisibility(View.VISIBLE);
//                                                    EventBus.getDefault().post(new FollowStateEditEvent(mMediaId, false));
//                                                    MToastUtils.showShortToast("???????????????");
//                                                } else {
//                                                    MToastUtils.showShortToast(cancelFollowBean.getBizMsg());
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onFail(Exception e) {
//                                                UIUtil.dismissLoadingDialog();
//                                                MToastUtils.showShortToast("?????????????????????" + e.getLocalizedMessage());
//                                            }
//
//                                        });
//                                    }
//                                }
//                            }
//                        }).show();
//                break;
////            case R.id.layout_tabs_all://??????????????????
////                cureSelectTab = 1;
////                //changeTabSelectColor();
////                // TODO: 16/10/21 ????????????adapter?????????
////                break;
////            case R.id.layout_tabs_hot://??????????????????
////                cureSelectTab = 2;
////                //changeTabSelectColor();
////                // TODO: 16/10/21 ????????????adapter?????????
////                break;
//        }
//    }
//
//
////    private void changeTabSelectColor() {
////        int selectTextColorId;
////        int selectLineColorId;
////        int unSelectLineColorId;
////        if (isTabInList) {
////            selectTextColorId = R.color.color_ff8600;
////            selectLineColorId = R.color.color_ff8600;
////            unSelectLineColorId = R.color.white;
////        } else {
////            selectTextColorId = R.color.white;
////            selectLineColorId = R.color.white;
////            unSelectLineColorId = R.color.color_1c2635;
////        }
////        if (cureSelectTab == 1) {
////            tv_tabs_all.setTextColor(getResources().getColor(selectTextColorId));
////            line_tabs_all.setBackgroundColor(getResources().getColor(selectLineColorId));
////            tv_tabs_hot.setTextColor(getResources().getColor(R.color.color_777777));
////            line_tabs_hot.setBackgroundColor(getResources().getColor(unSelectLineColorId));
////        } else if (cureSelectTab == 2) {
////            tv_tabs_all.setTextColor(getResources().getColor(R.color.color_777777));
////            line_tabs_all.setBackgroundColor(getResources().getColor(unSelectLineColorId));
////            tv_tabs_hot.setTextColor(getResources().getColor(selectTextColorId));
////            line_tabs_hot.setBackgroundColor(getResources().getColor(selectLineColorId));
////        }
////    }
//
//
//    private String getArticleIds(List<OfficialAccountListBean.ListBeanX> list) {
//        StringBuilder builder = new StringBuilder();
//        if (null != list && !list.isEmpty()) {
//            for (OfficialAccountListBean.ListBeanX bean : list) {
//                builder.append(bean.getArticleId()).append("|");
//            }
//        }
//        return String.valueOf(builder);
//    }
//
//    private void requestCount(String articleIds) {
//        Map<String, String> param = new HashMap<>(1);
//        param.put("articleIds", articleIds);
//        HttpUtil.get(ConstantUrl.GET_ARTICLES_INFO, param, ArticleInfoBean.class, articleCallback, 0);
//    }
//
//    public static void launch(Context context, String refer, long id) {
//        Intent intent = new Intent(context, OfficialAccountActivity.class);
//        intent.putExtra(App.getInstance().OFFICIAL_ACCOUNT_ID, id);
//        dealRefer(context, refer, intent);
//        context.startActivity(intent);
//    }
//}
