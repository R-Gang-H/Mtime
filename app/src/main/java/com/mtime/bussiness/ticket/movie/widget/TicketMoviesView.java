//package com.mtime.bussiness.ticket.movie.widget;
//
//import android.annotation.SuppressLint;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.view.PagerAdapter;
//import androidx.viewpager.widget.ViewPager;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.text.Spannable;
//import android.text.SpannableStringBuilder;
//import android.text.TextUtils;
//import android.text.style.ForegroundColorSpan;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.aspsine.irecyclerview.IRecyclerView;
//import com.aspsine.irecyclerview.OnRefreshListener;
//
//import com.mtime.frame.App;
//import com.mtime.frame.BaseActivity;
//import com.kekstudio.dachshundtablayout.DachshundTabLayout;
//import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;
//import com.mtime.R;
//import com.kotlin.android.user.UserManager;
//import com.mtime.base.statistic.StatisticConstant;
//import com.mtime.base.statistic.bean.StatisticPageBean;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.beans.ADDetailBean;
//import com.mtime.beans.ADTotalBean;
//import com.mtime.beans.QRGotoPage;
//import com.mtime.bussiness.mine.activity.MyVoucherListActivity;
//import com.mtime.bussiness.mine.login.activity.LoginActivity;
//import com.mtime.bussiness.ticket.TabPayTicketFragment;
//import com.mtime.bussiness.ticket.movie.adapter.AttentionFilmAdapter;
//import com.mtime.bussiness.ticket.movie.adapter.AttentionFilmTitleAdatper;
//import com.mtime.bussiness.ticket.movie.adapter.IncomingFilmAdapter;
//import com.mtime.bussiness.ticket.movie.adapter.Main_moviePlayingList_Adapter;
//import com.mtime.bussiness.ticket.movie.bean.CinemaMovieJsonBean;
//import com.mtime.bussiness.ticket.movie.bean.IncomingFilmBean;
//import com.mtime.bussiness.ticket.movie.bean.MovieAdBean;
//import com.mtime.bussiness.ticket.movie.bean.MovieBean;
//import com.mtime.bussiness.ticket.movie.bean.WantSeeFilmBean;
//import com.mtime.mtmovie.widgets.ADWebView;
//import com.mtime.mtmovie.widgets.pullrefresh.PullRefreshHeaderView;
//import com.mtime.network.ConstantUrl;
//import com.mtime.network.RequestCallback;
//import com.mtime.statistic.baidu.BaiduConstants;
//import com.mtime.statistic.large.StatisticManager;
//import com.mtime.statistic.large.ticket.StatisticTicket;
//import com.mtime.util.HttpUtil;
//import com.mtime.util.JumpToAdv;
//import com.mtime.util.ToolsUtils;
//import com.mtime.util.UIUtil;
//
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * 购票tab 电影（含 正在热映 即将上映）
// */
//@Deprecated
//@SuppressLint("InlinedApi")
//public class TicketMoviesView implements OnClickListener {
//
//    private static final String HOTPLAY_TOP_AD = "正在热映顶部文字链广告";
//
//    private BaseActivity context;
//
//    private IRecyclerView lvPlaying; // 正在热映
//    private IRecyclerView lvIncoming;//即将上映
//    private View incomingView;
//    private DachshundTabLayout mTabLayout;
//    private TextView couponNum;
//    private LinearLayout promoLayout;
//    private TextView iconTextTv;
//    private TextView titleTv;
//
//    public static final byte MOVIESTYPE_PLAYING = 1;
//    public static final byte MOVIESTYPE_INCOMING = 2;
//    public byte moviesType = MOVIESTYPE_PLAYING;
//
//    private Main_moviePlayingList_Adapter moviePlaying_Adapter;
//
//    private RequestCallback movieCallback = null;
//    private RequestCallback incomingCallback = null;
//    private RequestCallback wantMovieIdsCallback = null;
//    private String voucherMsg;
//    private String strClickkview;
//    private boolean hasPromo;
//    MovieAdBean promoBean;
//
//    private MoveLayout board, move_board;
//    private TextView name, move_name;
//
//    private List<MovieBean> listMovieData = new ArrayList<>();
//    List<IncomingFilmBean.RecommendsBean> attentionsBean;
//    List<String> remindMovieIDs = new ArrayList<String>();
//    private ArrayList<Integer> wantFilmIds = new ArrayList<>();
//    LinearLayout headerView, attention_num_layout;
//    ArrayList<IncomingFilmBean.MoviecomingsBean> newList = new ArrayList<IncomingFilmBean.MoviecomingsBean>();
//
//    private String cityId;
//
//    private ADWebView ad1;
//    private boolean loadingFailedHotMovie = false;
//    private boolean loadingFailedIncomingMovie = false;
//    public static String removeRemindMovieId = null;
//    public static String addRemindMovieId = null;
//    private RecyclerView recyclerView;
//    private boolean isNeedSHow = true;
//
//    private View layout_failed_holder;
//    private View layout_failed_holder1;
//    private ImageView imageview_failed;
//    private RecyclerView attentionTitleRecyclerView;//即将上映推荐标题
//    private AttentionFilmTitleAdatper attentionFilmTitleAdatper;//最受关注标签
//    private AttentionFilmTitleAdatper.OnItemSelectListener onItemSelectListener;
//    private ArrayList<IncomingFilmBean.RecommendsBean> recommendsFilmList = new ArrayList<>();
//    private IncomingFilmAdapter incomingFilmAdapter;//即将上映
//    private AttentionFilmAdapter attentionFilmAdapter;//最受关注
//    private List<IncomingFilmBean.MoviecomingsBean> incomingBean;//即将上映列表
//    private PullRefreshHeaderView refreshHeaderView;
//    private ViewPager viewPager;
//    private ArrayList<View> pageview;
//    private int page;
//    private long mStartTime;
//    private String referBean;
//
//    public String getReferBean() {
//        return referBean;
//    }
//
//    public void setLastPageRefer(String referBean) {
//        this.referBean = referBean;
//    }
//
//    public long getmStartTime() {
//        return mStartTime;
//    }
//
//
//    public List<MovieBean> getListMovieData() {
//        return listMovieData;
//    }
//
//    public TicketMoviesView(final BaseActivity context, final View root) {
//        this.context = context;
//        init(context, root);
//    }
//
//    public void setCityId(final String cityId) {
//        this.cityId = cityId;
//    }
//
//    public boolean needRequest(final String cityId) {
//        if (null == this.cityId || !this.cityId.equalsIgnoreCase(cityId)) {
//            this.cityId = cityId;
//            return true;
//        }
//
//        return false;
//    }
//
//    private void showPromo() {
//        if (promoBean != null) {
//            promoLayout.setVisibility(View.VISIBLE);
//            if (!promoBean.getIconText().equals("")) {
//                iconTextTv.setText(promoBean.getIconText());
//                iconTextTv.setVisibility(View.VISIBLE);
//            } else {
//                iconTextTv.setVisibility(View.GONE);
//            }
//            titleTv.setText(promoBean.getTitle());
//        } else {
//            promoLayout.setVisibility(View.GONE);
//        }
//    }
//
//    private void showVoucherMsg() {
//        if (!TextUtils.isEmpty(voucherMsg)) {
//            if (TextUtils.isEmpty(strClickkview)) {
//                strClickkview = context.getResources().getString(R.string.st_click_view);
//            }
//            String newVoucherMsg = voucherMsg + strClickkview;
//            if (!couponNum.getText().equals(newVoucherMsg)) {
//                SpannableStringBuilder spannable = new SpannableStringBuilder(newVoucherMsg);
//                spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_ff8600)), newVoucherMsg.indexOf(strClickkview), newVoucherMsg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                couponNum.setText(spannable);
//            }
//            couponNum.setVisibility(View.VISIBLE);
//        } else {
//            couponNum.setVisibility(View.GONE);
//        }
//    }
//
//    public void onInitEvent() {
//
//        /**
//         * 选中关注标签儿
//         */
//        onItemSelectListener = new AttentionFilmTitleAdatper.OnItemSelectListener() {
//            @Override
//            public void onSelect(int position, String title) {
//                if (attentionsBean != null && attentionsBean.size() > 0) {
//                    for (int i = 0; i < attentionsBean.size(); i++) {
//                        IncomingFilmBean.RecommendsBean recommendsBean = attentionsBean.get(i);
//                        if (recommendsBean != null) {
//                            String recommendTitle = recommendsBean.getRecommendTitle();
//                            if (title.equals(recommendTitle)) {
//                                attentionFilmAdapter.clear();
//                                attentionFilmAdapter.addAll(recommendsBean.getMovies());
//                                attentionFilmAdapter.setTagIndex(position);
//                                if (attentionFilmAdapter.getItemCount() > 0) {
//                                    recyclerView.scrollToPosition(0);
//                                }
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        };
//        movieCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                isNeedSHow = false;
//                UIUtil.dismissLoadingDialog();
//                loadingFailedHotMovie = false;
//                lvPlaying.setRefreshing(false);
//                lvPlaying.setVisibility(View.VISIBLE);
//                CinemaMovieJsonBean object = (CinemaMovieJsonBean) o;
//                voucherMsg = object.getVoucherMsg();
//                hasPromo = object.isHasPromo();
//                if (moviesType == MOVIESTYPE_PLAYING) {
//                    showVoucherMsg();
//                    if (hasPromo) {
//                        promoBean = object.getPromo();
//                        showPromo();
//                    }
//                }
//                if (listMovieData != null) {
//                    listMovieData.clear();
//                }
//                listMovieData = object.getMs();
//                moviePlaying_Adapter.clear();
//                moviePlaying_Adapter.addAll(listMovieData);
//                if (null == listMovieData || listMovieData.isEmpty()) {
//                    MToastUtils.showShortToast("本地没有数据");
//                }
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                loadingFailedHotMovie = true;
//                isNeedSHow = false;
//                UIUtil.dismissLoadingDialog();
//                lvPlaying.setRefreshing(false);
//                lvPlaying.setVisibility(View.GONE);
//                UIUtil.showLoadingFailedLayout(layout_failed_holder1, imageview_failed, new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        reLoadData();
//                    }
//                });
//
//            }
//        };
//
//
//        incomingCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                loadingFailedIncomingMovie = false;
//                try {
//                    IncomingFilmBean incomingBeanaAll = (IncomingFilmBean) o;
//                    // set attentions value
//                    attentionsBean = incomingBeanaAll.getRecommends();
//                    // init the attentions movies and incoming
//                    incomingBean = incomingBeanaAll.getMoviecomings();
//                    String old = null;
//                    newList.clear();
//                    if (incomingBean != null) {
//                        int lastYear = 0;
//                        int lastMonth = 0;
//                        int lastDay = 0;
//                        for (final IncomingFilmBean.MoviecomingsBean b : incomingBean) {
//                            old = calcDateString(old, b);
//                            if (b != null) {//判断是否是当前天的第一个
//                                int rYear = b.getRYear();
//                                int rMonth = b.getRMonth();
//                                int rDay = b.getRDay();
//                                if (lastDay != rDay || lastMonth != rMonth || lastYear != rYear) {//有一个不为相等的时候就是开头
//                                    b.setHeader(true);
//                                } else {
//                                    b.setHeader(false);
//                                }
//                                lastDay = rDay;
//                                lastMonth = rMonth;
//                                lastYear = rYear;
//                            }
//                        }
//                    } else {
//                    }
//
//                    requestWantMovieIds();
//                    incomingFilmAdapter.clear();
//                    incomingFilmAdapter.addAll(incomingBeanaAll.getMoviecomings());
//                    if (incomingFilmAdapter.getItemCount() > 0) {
//                        lvIncoming.scrollToPosition(0);
//                    }
//                    showAttentionsView(attentionsBean);
//                } catch (Exception e) {
//                    e.printStackTrace();
//
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                refreshHeaderView.onComplete();
//                lvIncoming.setRefreshing(false);
//                loadingFailedIncomingMovie = true;
//                UIUtil.showLoadingFailedLayout(layout_failed_holder, imageview_failed, new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        reLoadData();
//                    }
//                });
//            }
//        };
//
//        wantMovieIdsCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                WantSeeFilmBean wantSeeFilmBean = (WantSeeFilmBean) o;
//                if (wantSeeFilmBean != null) {
//                    List<Integer> movieIds = wantSeeFilmBean.getMovieIds();
//                    if (movieIds != null && movieIds.size() > 0) {
//                        wantFilmIds.clear();
//                        for (int i = 0; i < movieIds.size(); i++) {
//                            wantFilmIds.add(movieIds.get(i));
//                        }
//                    }
//                    if (attentionFilmAdapter != null) {
//                        attentionFilmAdapter.setWantMoviesList(wantFilmIds);
//                    }
//                    if (incomingFilmAdapter != null) {
//                        incomingFilmAdapter.setWantMoviesList(wantFilmIds);
//                    }
//                }
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//            }
//        };
//
//    }
//
//
//    public void onInitVariable() {
//        String type = context.getIntent().getStringExtra(App.getInstance().KEY_MOVIE_TYPE);
//        if (!TextUtils.isEmpty(type)) {
//            if (type.equals("hot_movie")) {
//                moviesType = MOVIESTYPE_PLAYING;
//            } else {
//                moviesType = MOVIESTYPE_INCOMING;
//            }
//        } else {
//            switch (TabPayTicketFragment.type) {
//                case TabPayTicketFragment.TYPE_MOVIE_INCOMING:
//                    moviesType = MOVIESTYPE_INCOMING;
//                    break;
//                default:
//                    moviesType = MOVIESTYPE_PLAYING;
//                    break;
//            }
//        }
//
//    }
//
//
//    public void loadView() {
//        switch (moviesType) {
//            case MOVIESTYPE_INCOMING:
//                context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                mStartTime = System.currentTimeMillis();
//                incomingFilmAdapter.setStartTime(mStartTime);
//                attentionFilmAdapter.setStartTime(mStartTime);
//                StatisticPageBean bean1 = context.assemble(StatisticConstant.OPEN, null, null, null, null, null, null);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean1.refer = referBean;
//                    incomingFilmAdapter.setReferBean(referBean);
//                    attentionFilmAdapter.setReferBean(referBean);
//                }
//                StatisticManager.getInstance().submit(bean1);
//                operatePage(2);
//                break;
//            default:
//                context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                mStartTime = System.currentTimeMillis();
//                moviePlaying_Adapter.setStartTime(mStartTime);
//                StatisticPageBean bean2 = context.assemble(StatisticConstant.OPEN, null, null, null, null, null, null);
//                if (!TextUtils.isEmpty(referBean)) {
//                    bean2.refer = referBean;
//                    moviePlaying_Adapter.setReferBean(referBean);
//                }
//                StatisticManager.getInstance().submit(bean2);
//                if (null == attentionsBean || loadingFailedIncomingMovie) {
//                    requestIncomingData();
//                }
//                operatePage(1);
//                break;
//        }
//    }
//
//    public void setMoviesType(byte type) {
//        if (type == MOVIESTYPE_INCOMING) {
//            moviesType = type;
//        } else {//防止输入错误，除了即将上映类型外，都强制转为正在热映
//            moviesType = MOVIESTYPE_PLAYING;
//        }
//        loadView();
//    }
//
//    public void onLoadData() {
//        // Showtime/LocationMovies.api?locationId={0}
//        UIUtil.showLoadingDialog(context);
//        Map<String, String> param = new HashMap<>(1);
//        param.put("locationId", cityId);
//        HttpUtil.get(ConstantUrl.GET_CITY_CINEMA_MOVIES, param, CinemaMovieJsonBean.class, movieCallback, Integer.MAX_VALUE, null, 0, true);
//        loadAdsData();
//    }
//
//    @Override
//    public void onClick(View arg0) {
//        switch (arg0.getId()) {
//            case R.id.coupon_num:
//                if (UserManager.Companion.getInstance().isLogin()) {
//                    context.startActivity(MyVoucherListActivity.class);
//                } else {
//                    // 跳转到登录页面
//                    // TODO 临时注释掉,等需求确定后再调整
////                    final Intent intent = new Intent();
////                    intent.putExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID, MyVoucherListActivity.class.getName());
//                    context.startActivity(LoginActivity.class);
//                }
//                break;
//            case R.id.ad_linear:
//                QRGotoPage gotoPage = promoBean.getGotoPage();
//                JumpToAdv jumpToAdv = new JumpToAdv();
//                String gotoType = gotoPage.getGotoType();
//                if (gotoType == null) {
//                    return;
//                }
//                context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_ADLINK, null, null, null, null, null, null);
//                StatisticManager.getInstance().submit(bean);
//
//                jumpToAdv.isOpenH5 = gotoPage.isGoH5();
//                jumpToAdv.Jump(context, gotoType, -1, null, -1, false, false, gotoPage, null, bean.toString());
//                break;
//            default:
//                break;
//        }
//    }
//
//    private void reLoadData() {
//        switch (moviesType) {
//            case MOVIESTYPE_INCOMING:
//                attentionsBean = null;
//                operatePage(2);
//                break;
//            default:
//                onLoadData();
//                operatePage(1);
//                break;
//        }
//    }
//
//    private void init(final BaseActivity context, final View root) {
//        this.layout_failed_holder = root.findViewById(R.id.loading_failed_layout);
//        this.imageview_failed = (ImageView) root.findViewById(R.id.load_failed);
//        mTabLayout = (DachshundTabLayout) root.findViewById(R.id.ticket_tabLayout);
//        viewPager = (ViewPager) root.findViewById(R.id.ticket_movie_viewpager);
//        LayoutInflater inflater = context.getLayoutInflater();
//        View view1 = inflater.inflate(R.layout.ticket_moviesonshow_view, null);
//        View view2 = inflater.inflate(R.layout.ticket_moviesincoming_view, null);
//
//        pageview = new ArrayList<View>();
//        //添加想要切换的界面
//        pageview.add(view1);
//        pageview.add(view2);
//
//        //数据适配器
//        PagerAdapter mPagerAdapter = new PagerAdapter() {
//
//            String[] titles = {"正在热映", "即将上映"};
//
//            @Override
//            //获取当前窗体界面数
//            public int getCount() {
//                // TODO Auto-generated method stub
//                return pageview.size();
//            }
//
//            @Override
//            //判断是否由对象生成界面
//            public boolean isViewFromObject(View arg0, Object arg1) {
//                // TODO Auto-generated method stub
//                return arg0 == arg1;
//            }
//
//            //返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
//            public Object instantiateItem(View arg0, int arg1) {
//                ((ViewPager) arg0).addView(pageview.get(arg1));
//                return pageview.get(arg1);
//            }
//
//            @Override
//            public CharSequence getPageTitle(int position) {
//                return titles[position];
//            }
//        };
//        //绑定适配器
//        viewPager.setAdapter(mPagerAdapter);
//        //添加切换界面的监听器
//        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
//
//        mTabLayout.setupWithViewPager(viewPager);
//        mTabLayout.setAnimatedIndicator(new DachshundIndicator(mTabLayout));
//        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.white));
//        mTabLayout.setTabTextColors(ContextCompat.getColor(context, R.color.color_777777), ContextCompat.getColor(context, R.color.white));
//
//        couponNum = (TextView) view1.findViewById(R.id.coupon_num);
//        couponNum.setOnClickListener(this);
//        couponNum.setVisibility(View.GONE);
//
//        promoLayout = (LinearLayout) view1.findViewById(R.id.ad_linear);
//        promoLayout.setOnClickListener(this);
//        promoLayout.setVisibility(View.GONE);
//        iconTextTv = (TextView) view1.findViewById(R.id.ad_icon_tv);
//        titleTv = (TextView) view1.findViewById(R.id.ad_txt_tv);
//
//        moviePlaying_Adapter = new Main_moviePlayingList_Adapter(context, new ArrayList<MovieBean>());
//        attentionFilmTitleAdatper = new AttentionFilmTitleAdatper(context, new ArrayList<String>());
//        incomingFilmAdapter = new IncomingFilmAdapter(context, new ArrayList<IncomingFilmBean.MoviecomingsBean>());
//        attentionFilmAdapter = new AttentionFilmAdapter(context, new ArrayList<IncomingFilmBean.MoviecomingsBean>());
//        incomingFilmAdapter.setAttentionFilmAdapter(attentionFilmAdapter);
//        attentionFilmAdapter.setIncomingFilmAdapter(incomingFilmAdapter);
//        lvPlaying = (IRecyclerView) view1.findViewById(R.id.list_movie_hot);
//        layout_failed_holder1 = view1.findViewById(R.id.loading_failed_layout1);
//        lvPlaying.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
//        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
//        lvPlaying.setLayoutManager(linearLayoutManager1);
//        lvPlaying.setIAdapter(moviePlaying_Adapter);
//        lvPlaying.setRefreshEnabled(true);
//        lvPlaying.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                onLoadData();
//            }
//        });
//        incomingView = view2.findViewById(R.id.list_movie_incoming);
//        initIncomingListView();
//    }
//
//    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//
//        @Override
//        public void onPageSelected(int arg0) {
//            changePager(arg0);
//        }
//
//        @Override
//        public void onPageScrolled(int arg0, float arg1, int arg2) {
//        }
//
//        @Override
//        public void onPageScrollStateChanged(int arg0) {
//        }
//    }
//
//    private void changePager(int arg) {
//        switch (arg) {
//            case 0:
//                TabPayTicketFragment.type = TabPayTicketFragment.TYPE_MOVIE_HOT;
//                if (moviesType == MOVIESTYPE_INCOMING) {
//                    context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                    StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean1.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean1);
//                    context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                    Map<String, String> params = new HashMap<>();
//                    params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                    StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean3.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean3);
//
//                    context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_SELECT_ON_SHOW, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean);
//                    referBean = bean.toString();
//
//                    context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                    mStartTime = System.currentTimeMillis();
//                    moviePlaying_Adapter.setStartTime(mStartTime);
//                    StatisticPageBean bean2 = context.assemble(StatisticConstant.OPEN, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean2.refer = referBean;
//                        moviePlaying_Adapter.setReferBean(referBean);
//                    }
//                    StatisticManager.getInstance().submit(bean2);
//
//                }
//                moviesType = MOVIESTYPE_PLAYING;
//                break;
//            case 1:
//                TabPayTicketFragment.type = TabPayTicketFragment.TYPE_MOVIE_INCOMING;
//                if (moviesType == MOVIESTYPE_PLAYING) {
//                    context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                    StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean1.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean1);
//
//                    context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                    Map<String, String> params = new HashMap<>();
//                    params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                    StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean3.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean3);
//
//                    context.mPageLabel = StatisticTicket.PN_ON_SHOW_LIST;
//                    StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_SELECT_FUTURE_SCHEDULE, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean.refer = referBean;
//                    }
//                    StatisticManager.getInstance().submit(bean);
//                    referBean = bean.toString();
//
//                    context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                    mStartTime = System.currentTimeMillis();
//                    incomingFilmAdapter.setStartTime(mStartTime);
//                    attentionFilmAdapter.setStartTime(mStartTime);
//                    StatisticPageBean bean2 = context.assemble(StatisticConstant.OPEN, null, null, null, null, null, null);
//                    if (!TextUtils.isEmpty(referBean)) {
//                        bean2.refer = referBean;
//                        incomingFilmAdapter.setReferBean(referBean);
//                        attentionFilmAdapter.setReferBean(referBean);
//                    }
//                    StatisticManager.getInstance().submit(bean2);
//                }
//                moviesType = MOVIESTYPE_INCOMING;
//                break;
//        }
//    }
//
//    /**
//     * 即将上映ListView
//     */
//    private void initIncomingListView() {
//        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.movie_incoming_list_header, null);
//        recyclerView = (RecyclerView) headerView.findViewById(R.id.attentions_view);
//        attentionTitleRecyclerView = ((RecyclerView) headerView.findViewById(R.id.recommend_title_recyclerview));
//        LinearLayoutManager recycleLinearLayoutManager = new LinearLayoutManager(context);
//        recycleLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        attentionTitleRecyclerView.setLayoutManager(recycleLinearLayoutManager);
//        attentionTitleRecyclerView.setAdapter(attentionFilmTitleAdatper);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        recyclerView.setLayoutManager(linearLayoutManager);
//        recyclerView.setAdapter(attentionFilmAdapter);
//        this.ad1 = (ADWebView) headerView.findViewById(R.id.ad);
//        attentionsBean = null;
//        attention_num_layout = (LinearLayout) headerView.findViewById(R.id.attention_num_layout);
//
//
//        board = (MoveLayout) incomingView.findViewById(R.id.board);
//        name = (TextView) board.findViewById(R.id.name);
//        board.setVisibility(View.GONE);
//
//        move_board = (MoveLayout) incomingView.findViewById(R.id.move_board);
//        move_name = (TextView) move_board.findViewById(R.id.move_name);
//        move_board.setVisibility(View.GONE);
//
//        lvIncoming = (IRecyclerView) incomingView.findViewById(R.id.movie_incoming_list);
//        refreshHeaderView = (PullRefreshHeaderView) lvIncoming.getRefreshHeaderView();
//
//        lvIncoming.setHasFixedSize(true);
//        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
//        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
//        lvIncoming.setLayoutManager(linearLayoutManager1);
//        lvIncoming.setIAdapter(incomingFilmAdapter);
//        lvIncoming.addHeaderView(headerView);
//        lvIncoming.setRefreshEnabled(true);
//        lvIncoming.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
//                if (firstVisibleItemPosition >= 1) {
//                    IncomingFilmBean.MoviecomingsBean moviecomingsBean = incomingFilmAdapter.getItem(firstVisibleItemPosition - 2);
//                    if (moviecomingsBean != null) {
//                        board.setVisibility(View.VISIBLE);
//                        name.setText(moviecomingsBean.getDateString());
//                    } else {
//                        board.setVisibility(View.GONE);
//                    }
//                } else {
//                    board.setVisibility(View.GONE);
//                }
//
//            }
//        });
//        lvIncoming.setOnRefreshListener(new OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                requestIncomingData();
//            }
//        });
//
//    }
//
//    /**
//     * 请求即将上映列表数据
//     */
//    public void requestIncomingData() {
//        if (getMoviesType() == MOVIESTYPE_INCOMING) {
//            UIUtil.showLoadingDialog(context);
//        }
//        Map<String, String> param = new HashMap<>(1);
//        param.put("locationId", String.valueOf(cityId));
//        HttpUtil.get(ConstantUrl.GET_INCOMING_RECOMMEND_LIST, param, IncomingFilmBean.class, incomingCallback, Integer.MAX_VALUE, null, 0, true);
//    }
//
//    // 影片类型的操作
//    private void operatePage(int flag) {
//        if (flag == 2) {
//            couponNum.setVisibility(View.GONE);
//            incomingView.setVisibility(View.VISIBLE);
//            lvPlaying.setVisibility(View.VISIBLE);
//            if (null == attentionsBean || loadingFailedIncomingMovie) {
//                requestIncomingData();
//            }
//            moviesType = MOVIESTYPE_INCOMING;
//            page = 1;
//        } else {
//            lvPlaying.setVisibility(View.VISIBLE);
//            incomingView.setVisibility(View.VISIBLE);
//            if (loadingFailedHotMovie) {
//                UIUtil.showLoadingFailedLayout(layout_failed_holder, imageview_failed, new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        reLoadData();
//                    }
//                });
//            } else if (null == listMovieData) {
//                onLoadData();
//            }
//            showVoucherMsg();
//            moviesType = MOVIESTYPE_PLAYING;
//            page = 0;
//        }
//        if (null == viewPager) {
//            return;
//        }
//
//        viewPager.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//                viewPager.setCurrentItem(page);
//            }
//        }, 100);
//    }
//
//
//
//    public void showAttentionsView(List<IncomingFilmBean.RecommendsBean> attentions) {
//        UIUtil.dismissLoadingDialog();
//        refreshHeaderView.onComplete();
//        lvIncoming.setRefreshing(false);
//        if (attentions == null || attentions.size() == 0) {
//            recyclerView.setVisibility(View.GONE);
//            attention_num_layout.setVisibility(View.GONE);
//            return;
//        }
//        attention_num_layout.setVisibility(View.VISIBLE);
//        recyclerView.setVisibility(View.VISIBLE);
//        if (attentions.size() > 0) {
//            attentionFilmAdapter.clear();
//            IncomingFilmBean.RecommendsBean recommendsBean = attentions.get(0);
//            if (recommendsBean != null) {
//                attentionFilmAdapter.addAll(recommendsBean.getMovies());
//                attentionFilmAdapter.setTagIndex(0);
//                if (attentionFilmAdapter.getItemCount() > 0) {
//                    recyclerView.scrollToPosition(0);
//                }
//
//            }
//            ArrayList<String> attentionsTitleList = new ArrayList<>();
//            for (int i = 0; i < attentions.size(); i++) {
//                IncomingFilmBean.RecommendsBean recommendsBean1 = attentions.get(i);
//                attentionsTitleList.add(recommendsBean1.getRecommendTitle());
//            }
//            attentionFilmTitleAdatper.clear();
//            attentionFilmTitleAdatper.addAll(attentionsTitleList);
//            attentionFilmTitleAdatper.setOnItemSelectListener(onItemSelectListener);
//            if (attentionFilmTitleAdatper.getItemCount() > 0) {
//                attentionTitleRecyclerView.scrollToPosition(0);
//            }
//        }
//    }
//
//
//    //只有刚从登录回来才走该方法
//    public void requestWantMovieIds() {
//        if (UserManager.Companion.getInstance().isLogin()) {
//            HttpUtil.get(ConstantUrl.GET_WANT_MOVIE_IDS, WantSeeFilmBean.class, wantMovieIdsCallback);
//        } else {
//            if (wantFilmIds == null) {
//                wantFilmIds = new ArrayList<>();
//            } else {
//                wantFilmIds.clear();
//                if (attentionFilmAdapter != null) {
//                    attentionFilmAdapter.setWantMoviesList(wantFilmIds);
//                }
//                if (incomingFilmAdapter != null) {
//                    incomingFilmAdapter.setWantMoviesList(wantFilmIds);
//                }
//            }
//        }
//    }
//
//    /**
//     * 计算即将上映影片该显示的日期
//     *
//     * @param old
//     * @param b
//     * @return
//     */
//    private String calcDateString(String old, final IncomingFilmBean.MoviecomingsBean b) {
//        String dateString = "";
//        if (b != null) {
//            int rYear = b.getRYear();
//            int rMonth = b.getRMonth();
//            int rDay = b.getRDay();
//            int year = Calendar.getInstance().get(Calendar.YEAR);
//            if (year == rYear) {
//                if (rMonth != 0) {
//                    if (rDay != 0) {
//                        dateString = String.format("%1$d月%2$d日", rMonth, rDay);
//                    } else {
//                        dateString = String.format("%1$d月待定", rMonth);
//                    }
//                } else {
//                    dateString = String.format("%1$d年待定", rYear);
//                }
//            } else {//年份不相等
//                if (rYear != 0) {
//                    if (rMonth != 0) {
//                        if (rDay != 0) {
//                            dateString = String.format("%1$d年%2$d月%3$d日", rYear, rMonth, rDay);
//                        } else {//没有日期
//                            dateString = String.format("%1$d年%2$d月待定", rYear, rMonth);
//                        }
//                    } else {
//                        dateString = String.format("%1$d年待定", rYear);
//                    }
//                } else {
//                    if (rMonth != 0) {
//                        if (rDay != 0) {
//                            dateString = String.format("%1$d月%2$d日", rMonth, rDay);
//                        } else {
//                            dateString = String.format("%1$d月待定", rMonth);
//                        }
//                    } else {
//
//                    }
//                }
//
//            }
//        }
//        b.setDateString(dateString);
//        return dateString;
//    }
//
//    private void loadAdsData() {
//        // ad1
//        // Advertisement/MobileAdvertisementInfo.api?locationId={0}
//        Map<String, String> param = new HashMap<>(1);
//        param.put("locationId", cityId);
//        HttpUtil.get(ConstantUrl.AD_MOBILE_ADVERTISEMENT_INFO, param, ADTotalBean.class, new RequestCallback() {
//
//            @Override
//            public void onFail(Exception e) {
//                ad1.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onSuccess(Object o) {
//                ADTotalBean bean = (ADTotalBean) o;
//                ADDetailBean item = ToolsUtils.getADBean(bean, App.getInstance().AD_MOVIE_INCOMING);
//                if (!ADWebView.show(item)) {
//                    ad1.setVisibility(View.GONE);
//                    return;
//                }
//                ad1.setVisibility(View.VISIBLE);
//                context.mPageLabel = StatisticTicket.PN_FUTURE_SCHEDULE;
//                ad1.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
//                    @Override
//                    public void onAdItemClick(ADDetailBean item, String url) {
//                        StatisticPageBean bean1 = context.assemble(StatisticTicket.TICKET_AD, null, null, null, null, null, null);
//                        StatisticManager.getInstance().submit(bean1);
//                    }
//                });
//                ad1.load(context, item);
//
//            }
//        }, 30 * 60 * 1000, null, 2000);
//
//    }
//
//    public void updateRemindData() {
//        if (null == incomingFilmAdapter) {
//            return;
//        }
//
//        if (TextUtils.isEmpty(addRemindMovieId) && TextUtils.isEmpty(removeRemindMovieId)) {
//            return;
//        }
//
//        if (UserManager.Companion.getInstance().isLogin()) {
//            //
//            if (!TextUtils.isEmpty(addRemindMovieId)) {
//
//                incomingFilmAdapter.modifyRemindIds(addRemindMovieId, true);
//            } else if (!TextUtils.isEmpty(removeRemindMovieId)) {
//                incomingFilmAdapter.modifyRemindIds(removeRemindMovieId, false);
//            }
//        }
//
//        incomingFilmAdapter.notifyDataSetChanged();
//        addRemindMovieId = null;
//        removeRemindMovieId = null;
//    }
//
//    public byte getMoviesType() {
//        return moviesType;
//    }
//}
