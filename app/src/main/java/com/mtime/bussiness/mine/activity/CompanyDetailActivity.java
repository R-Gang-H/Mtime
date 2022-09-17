package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.adapter.CompanyDetailAdapter;
import com.mtime.bussiness.mine.bean.CompanyDetailBean;
import com.mtime.bussiness.mine.bean.NavigationItem;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.movie.widget.NavigationAdapter;
import com.mtime.bussiness.ticket.movie.widget.NavigationHorizontalScrollView;
import com.mtime.frame.BaseActivity;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.mtmovie.widgets.pullrefresh.OnItemClickListener;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.JumpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ZhangCong on 15/10/26.
 * 公司详情
 */
public class CompanyDetailActivity extends BaseActivity {
    public int currentPos = 0;

    // 不是接口参数枚举，是Tab位置枚举
    public static final int TYPE_COMPANY_MAKE = 0; // 制作
    public static final int TYPE_COMPANY_ISSUE = 1; // 发行
    public static final int TYPE_COMPANY_OTHER = 2; // 其他

    // 接口参数值  制作发行类型: 0其他 1 制作 2发行
    private static final int PARAM_TYPE_OTHER = 0;
    private static final int PARAM_TYPE_MAKE = 1;
    private static final int PARAM_TYPE_ISSUE = 2;

    private int pageIndexMake = 1;
    private int pageIndexIssue = 1;
    private int pageIndexOther = 1;

    private IRecyclerView makeListView;
    private IRecyclerView issueListView;
    private IRecyclerView otherListView;
    private LoadMoreFooterView makeLoadMoreFooterView;
    private LoadMoreFooterView issueLoadMoreFooterView;
    private LoadMoreFooterView otherLoadMoreFooterView;

    private TextView makeEmpty;
    private TextView issueEmpty;
    private TextView otherEmpty;

    private CompanyDetailAdapter makeDetailAdapter;
    private CompanyDetailAdapter issueDetailAdapter;
    private CompanyDetailAdapter otherDetailAdapter;

    private CompanyDetailBean makeDetailBean;
    private CompanyDetailBean issueDetailBean;
    private CompanyDetailBean otherDetailBean;

    private NetworkManager.NetworkListener<CompanyDetailBean> getMakeDetailCallback = null;
    private NetworkManager.NetworkListener<CompanyDetailBean> getIssueDetailCallback = null;
    private NetworkManager.NetworkListener<CompanyDetailBean> getOtherDetailCallback = null;

    private OnItemClickListener makeItemClickListener = null;
    private OnItemClickListener issueItemClickListener = null;
    private OnItemClickListener otherItemClickListener = null;

    private ViewPager viewPager;
    private List<View> pagerViews;
    private View viewMake;
    private View viewIssue;
    private View viewOther;
    private ViewPager.OnPageChangeListener pagerChangeListener;

    private NavigationHorizontalScrollView titleView; // 导航栏

    private List<NavigationItem> navigationItems;

    private View[] listViews;

    private int productionTotalCount;
    private int distributorTotalCount;
    private int otherTotalCount;

    private boolean isMakeLoadData = true;
    private boolean isIssueLoadData = true;
    private boolean isOtherLoadData = true;

    private TicketApi mTicketApi;

    public String companyId;
    public String companyName;
    public int type = PARAM_TYPE_MAKE; // 1 制作、2发行、0其他。默认制作

    public static final String KEY_COMPANY_ID = "company_id";
    public static final String KEY_COMPANY_NAME = "company_name";

    @Override
    protected void onInitVariable() {
        companyId = getIntent().getStringExtra(KEY_COMPANY_ID);
        companyName = getIntent().getStringExtra(KEY_COMPANY_NAME);
        productionTotalCount = distributorTotalCount = otherTotalCount = 0;
        setPageLabel("productionRelease");
        mTicketApi = new TicketApi();
    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_company_detail);
        View navBar = findViewById(R.id.navigationbar);

        new TitleOfNormalView(this, navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE, companyName, null);

        titleView = findViewById(R.id.horizontal_scrollview);
        titleView.setImageView(findViewById(R.id.iv_pre),
                findViewById(R.id.iv_next));

        viewMake = View.inflate(this, R.layout.company_pager_item, null);
        viewIssue = View.inflate(this, R.layout.company_pager_item, null);
        viewOther = View.inflate(this, R.layout.company_pager_item, null);

        makeListView = viewMake.findViewById(R.id.company_type);
        issueListView = viewIssue.findViewById(R.id.company_type);
        otherListView = viewOther.findViewById(R.id.company_type);

        makeListView.setLayoutManager(new LinearLayoutManager(this));
        issueListView.setLayoutManager(new LinearLayoutManager(this));
        otherListView.setLayoutManager(new LinearLayoutManager(this));

        makeLoadMoreFooterView = (LoadMoreFooterView) makeListView.getLoadMoreFooterView();
        issueLoadMoreFooterView = (LoadMoreFooterView) issueListView.getLoadMoreFooterView();
        otherLoadMoreFooterView = (LoadMoreFooterView) otherListView.getLoadMoreFooterView();

        makeEmpty = viewMake.findViewById(R.id.empty_info);
        issueEmpty = viewIssue.findViewById(R.id.empty_info);
        otherEmpty = viewOther.findViewById(R.id.empty_info);

        pagerViews = new ArrayList<View>();
        pagerViews.add(viewMake);
        pagerViews.add(viewIssue);
        pagerViews.add(viewOther);

        viewPager = findViewById(R.id.pager);
        viewPager.setAdapter(new ViewPagerAdapter());
        viewPager.setOffscreenPageLimit(2);

        listViews = new View[]{
                makeListView, issueListView, otherListView
        };
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return pagerViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pagerViews.get(arg1));
            return pagerViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

    }

    @Override
    protected void onInitEvent() {
        initGetMakeDetailCallback();
        initMakeItemClickListener();

        initGetIssueDetailCallback();
        initIssueItemClickListener();

        initGetOtherDetailCallback();
        initOtherItemClickListener();

        makeListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (makeLoadMoreFooterView.canLoadMore()) {
                    makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    pageIndexMake++;
                    type = PARAM_TYPE_MAKE;

                    mTicketApi.getCompanyMakeMovies(companyId, pageIndexMake, type, new NetworkManager.NetworkListener<CompanyDetailBean>() {
                        @Override
                        public void onSuccess(CompanyDetailBean bean, String showMsg) {
                            makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                            if (bean.getMovies() != null) {
                                makeDetailBean.getMovies().addAll(bean.getMovies());
                                makeDetailAdapter.notifyDataSetChanged();
                                if (makeDetailBean.getMovies().size() >= productionTotalCount) {
                                    makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                                }
                            }
                        }

                        @Override
                        public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                            MToastUtils.showShortToast("数据加载失败:"+showMsg);
                            makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                        }
                    });

                    // Company/MakeMovies.api?companyId={0}&pageIndex={1}&type={2}
//                    Map<String, String> param = new HashMap<>(3);
//                    param.put("companyId", companyId);
//                    param.put("pageIndex", String.valueOf(pageIndexMake));
//                    param.put("type", String.valueOf(type));
//                    HttpUtil.get(ConstantUrl.GET_COMPANY_DETAIL, param, CompanyDetailBean.class, getMakeDetailIndexCallback, 3600);
                }
            }
        });
        issueListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (issueLoadMoreFooterView.canLoadMore()) {
                    issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    pageIndexIssue++;
                    type = PARAM_TYPE_ISSUE;

                    mTicketApi.getCompanyMakeMovies(companyId, pageIndexIssue, type, new NetworkManager.NetworkListener<CompanyDetailBean>() {
                        @Override
                        public void onSuccess(CompanyDetailBean bean, String showMsg) {
                            issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                            if (bean.getMovies() != null) {
                                issueDetailBean.getMovies().addAll(bean.getMovies());
                                issueDetailAdapter.notifyDataSetChanged();
                                if (issueDetailBean.getMovies().size() >= distributorTotalCount) {
                                    issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                                }
                            }
                        }

                        @Override
                        public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                            MToastUtils.showShortToast("数据加载失败:"+showMsg);
                            issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                        }
                    });

                    // Company/MakeMovies.api?companyId={0}&pageIndex={1}&type={2}
//                    Map<String, String> param = new HashMap<String, String>(3);
//                    param.put("companyId", companyId);
//                    param.put("pageIndex", String.valueOf(pageIndexIssue));
//                    param.put("type", String.valueOf(type));
//                    HttpUtil.get(ConstantUrl.GET_COMPANY_DETAIL, param, CompanyDetailBean.class, getIssueDetailIndexCallback, 3600);
                }
            }
        });
        otherListView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(View loadMoreView) {
                if (otherLoadMoreFooterView.canLoadMore()) {
                    otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
                    pageIndexOther++;
                    type = PARAM_TYPE_OTHER;

                    mTicketApi.getCompanyMakeMovies(companyId, pageIndexOther, type, new NetworkManager.NetworkListener<CompanyDetailBean>() {
                        @Override
                        public void onSuccess(CompanyDetailBean bean, String showMsg) {
                            otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                            if (bean.getMovies() != null) {
                                otherDetailBean.getMovies().addAll(bean.getMovies());
                                otherDetailAdapter.notifyDataSetChanged();
                                if (otherDetailBean.getMovies().size() >= otherTotalCount) {
                                    otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                                }
                            }
                        }

                        @Override
                        public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                            MToastUtils.showShortToast("数据加载失败:"+showMsg);
                            otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                        }
                    });

                    // Company/MakeMovies.api?companyId={0}&pageIndex={1}&type={2}
//                    Map<String, String> param = new HashMap<String, String>(3);
//                    param.put("companyId", companyId);
//                    param.put("pageIndex", String.valueOf(pageIndexOther));
//                    param.put("type", String.valueOf(type));
//                    HttpUtil.get(ConstantUrl.GET_COMPANY_DETAIL, param, CompanyDetailBean.class, getOtherDetailIndexCallback, 3600);

                }
            }
        });

        pagerChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int position) {

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPos = position;

                titleView.selectItem(position);
                toggleListViews(position);
            }
        };

        titleView.setOnItemClickListener(new NavigationHorizontalScrollView.OnItemClickListener() {
            @Override
            public void click(int position) {
                viewPager.setCurrentItem(position);
            }
        });

        viewPager.setOnPageChangeListener(pagerChangeListener);
        titleView.selectItem(0);
    }

    private void toggleListViews(int position) {
        if (position == CompanyDetailActivity.TYPE_COMPANY_MAKE) {
            if(isMakeLoadData){
                UIUtil.showLoadingDialog(CompanyDetailActivity.this);
                mTicketApi.getCompanyMakeMovies(companyId, 1, PARAM_TYPE_MAKE, getMakeDetailCallback);
            }
            toggleListViewsVisibility(makeListView);
        } else if (position == CompanyDetailActivity.TYPE_COMPANY_ISSUE) {
            if(isIssueLoadData) {
                UIUtil.showLoadingDialog(CompanyDetailActivity.this);
                mTicketApi.getCompanyMakeMovies(companyId, 1, PARAM_TYPE_ISSUE, getIssueDetailCallback);
            }
            toggleListViewsVisibility(issueListView);
        } else if (position == CompanyDetailActivity.TYPE_COMPANY_OTHER) {
            if(isOtherLoadData) {
                UIUtil.showLoadingDialog(CompanyDetailActivity.this);
                mTicketApi.getCompanyMakeMovies(companyId, 1, PARAM_TYPE_OTHER, getOtherDetailCallback);
            }
            toggleListViewsVisibility(otherListView);
        }
    }

    private void toggleListViewsVisibility(View showView) {
        if (listViews != null && listViews.length > 0 && showView != null) {
            for (int index = 0; index < listViews.length; index++) {
                View currentView = listViews[index];
                boolean isGone = showView.getId() != currentView.getId();

                int visibility = isGone ? View.GONE : View.VISIBLE;
                currentView.setVisibility(visibility);
            }
        }
    }

    private void initGetMakeDetailCallback() {
        getMakeDetailCallback = new NetworkManager.NetworkListener<CompanyDetailBean>() {
            @Override
            public void onSuccess(CompanyDetailBean bean, String showMsg) {
                UIUtil.dismissLoadingDialog();

                makeDetailBean = bean;
                productionTotalCount = makeDetailBean.getProductionTotalCount();
                distributorTotalCount = makeDetailBean.getDistributorTotalCount();
                otherTotalCount = makeDetailBean.getOtherTotalCount();

                isMakeLoadData = false;
                initNavigationItems();
                titleView.setAdapter(new NavigationAdapter(CompanyDetailActivity.this, navigationItems));

                titleView.selectItem(0);

                if (makeDetailBean.getMovies() == null || makeDetailBean.getMovies().size() == 0) {
                    makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    makeEmpty.setVisibility(View.VISIBLE);
                    return;
                } else {
                    makeEmpty.setVisibility(View.GONE);
                    if (makeDetailBean.getMovies().size() < productionTotalCount) {
                        makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    } else {
                        makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    }
                }

                makeDetailAdapter = new CompanyDetailAdapter(CompanyDetailActivity.this, makeDetailBean.getMovies());
                makeListView.setIAdapter(makeDetailAdapter);
                makeDetailAdapter.setOnItemClickListener(makeItemClickListener);

            }

            @Override
            public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("数据加载失败:"+showMsg);
                makeLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);
                titleView.selectItem(0);
            }
        };
    }

    private void initMakeItemClickListener() {
        makeItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (makeDetailBean != null && makeDetailBean.getMovies() != null) {
                    if (position <= makeDetailBean.getMovies().size()) {
                        JumpUtil.startMovieInfoActivity(CompanyDetailActivity.this, assemble().toString(),
                                String.valueOf(makeDetailBean.getMovies().get(position).getId()), 0);
                    }
                }
            }

        };
    }

    private void initGetIssueDetailCallback() {
        getIssueDetailCallback = new NetworkManager.NetworkListener<CompanyDetailBean>() {
            @Override
            public void onSuccess(CompanyDetailBean bean, String showMsg) {
                UIUtil.dismissLoadingDialog();
                issueDetailBean = bean;
                isIssueLoadData = false;
                issueDetailAdapter = new CompanyDetailAdapter(CompanyDetailActivity.this, issueDetailBean.getMovies());
                issueListView.setIAdapter(issueDetailAdapter);
                issueDetailAdapter.setOnItemClickListener(issueItemClickListener);

                if (issueDetailBean.getMovies() == null || issueDetailBean.getMovies().size() == 0) {
                    issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    issueEmpty.setVisibility(View.VISIBLE);
                    return;
                } else {
                    issueEmpty.setVisibility(View.GONE);
                    if (issueDetailBean.getMovies().size() < distributorTotalCount) {
                        issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    } else {
                        issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("数据加载失败:"+showMsg);
                issueLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);

            }
        };
    }

    private void initIssueItemClickListener() {
        issueItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (issueDetailBean != null && issueDetailBean.getMovies() != null) {
                    if (position <= issueDetailBean.getMovies().size()) {
                        JumpUtil.startMovieInfoActivity(CompanyDetailActivity.this, assemble().toString(),
                                String.valueOf(issueDetailBean.getMovies().get(position).getId()), 0);
                    }
                }
            }

        };
    }

    private void initGetOtherDetailCallback() {
        getOtherDetailCallback = new NetworkManager.NetworkListener<CompanyDetailBean>() {
            @Override
            public void onSuccess(CompanyDetailBean bean, String showMsg) {
                UIUtil.dismissLoadingDialog();
                otherDetailBean = bean;
                isOtherLoadData = false;
                otherDetailAdapter = new CompanyDetailAdapter(CompanyDetailActivity.this, otherDetailBean.getMovies());
                otherListView.setIAdapter(otherDetailAdapter);
                otherDetailAdapter.setOnItemClickListener(otherItemClickListener);

                if (otherDetailBean.getMovies() == null || otherDetailBean.getMovies().size() == 0) {
                    otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    otherEmpty.setVisibility(View.VISIBLE);
                    return;
                } else {
                    otherEmpty.setVisibility(View.GONE);
                    if (otherDetailBean.getMovies().size() < otherTotalCount) {
                        otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                    } else {
                        otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                    }
                }
            }

            @Override
            public void onFailure(NetworkException<CompanyDetailBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("数据加载失败:"+showMsg);
                otherLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.ERROR);

            }
        };
    }

    public void initOtherItemClickListener() {
        otherItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (otherDetailBean != null && otherDetailBean.getMovies() != null) {
                    if (position <= otherDetailBean.getMovies().size()) {
                        JumpUtil.startMovieInfoActivity(CompanyDetailActivity.this, assemble().toString(),
                                String.valueOf(otherDetailBean.getMovies().get(position).getId()), 0);
                    }
                }
            }

        };
    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onRequestData() {
        UIUtil.showLoadingDialog(this);

        mTicketApi.getCompanyMakeMovies(companyId, pageIndexMake, type, getMakeDetailCallback);

        // Company/MakeMovies.api?companyId={0}&pageIndex={1}&type={2}
//        Map<String, String> param = new HashMap<>(3);
//        param.put("companyId", companyId);
//        param.put("pageIndex",String.valueOf(pageIndexMake) );
//        param.put("type", String.valueOf(type));
//        HttpUtil.get(ConstantUrl.GET_COMPANY_DETAIL, param, CompanyDetailBean.class, getMakeDetailCallback, 3600);
    }

    @Override
    protected void onDestroy() {
        listViews = null;
        super.onDestroy();
        if(mTicketApi != null) {
            mTicketApi = null;
        }
    }

    /*
    初始顶部导航
     */
    private void initNavigationItems() {
        navigationItems = new ArrayList<NavigationItem>();
        navigationItems.add(new NavigationItem(CompanyDetailActivity.TYPE_COMPANY_MAKE, String.format("制作(%d)", productionTotalCount)));
        navigationItems.add(new NavigationItem(CompanyDetailActivity.TYPE_COMPANY_ISSUE, String.format("发行(%d)", distributorTotalCount)));
        navigationItems.add(new NavigationItem(CompanyDetailActivity.TYPE_COMPANY_OTHER, String.format("其他(%d)", otherTotalCount)));
    }

    public static void launch(Context context, String companyId, String companyName){
        Intent launcher = new Intent();
        launcher.putExtra(KEY_COMPANY_ID,companyId);
        launcher.putExtra(KEY_COMPANY_NAME,companyName);
        ((BaseActivity)context).startActivity(CompanyDetailActivity.class, launcher);
    }

}
