package com.mtime.bussiness.ticket.movie.fragment;


import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.router.provider.video.IVideoProvider;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.router.ext.ProviderExtKt;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.common.bean.CommonRegionPusblish;
import com.mtime.bussiness.ticket.TicketFragment;
import com.mtime.bussiness.ticket.adapter.TabTicketVideoAdapter;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.event.entity.PosterFilterEvent;
import com.mtime.frame.App;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.beans.ADDetailBean;
import com.mtime.beans.ADTotalBean;
import com.mtime.frame.BaseFragment;
import com.mtime.bussiness.ticket.movie.adapter.AttentionFilmAdapter;
import com.mtime.bussiness.ticket.movie.adapter.AttentionFilmTitleAdatper;
import com.mtime.bussiness.ticket.movie.adapter.IncomingFilmAdapter;
import com.mtime.bussiness.ticket.movie.bean.IncomingFilmBean;
import com.mtime.bussiness.ticket.movie.bean.WantSeeFilmBean;
import com.mtime.bussiness.common.widget.MoveLayout;
import com.mtime.mtmovie.widgets.ADWebView;
import com.mtime.mtmovie.widgets.pullrefresh.PullRefreshHeaderView;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.HttpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.util.UIUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 购票tab 电影（即将上映）
 */
public class TicketMoviesInComingFragment extends BaseFragment {

    private TabTicketVideoAdapter mTabTicketVideoAdapter;
    private AttentionFilmTitleAdatper attentionFilmTitleAdatper;
    private IncomingFilmAdapter incomingFilmAdapter;
    private AttentionFilmAdapter attentionFilmAdapter;
    private View incomingView;
    private LinearLayout headerView;
    private RecyclerView recyclerView;
    private TextView mVideoTitleTv;
    private RecyclerView mVideoRecyclerView;
    private RecyclerView attentionTitleRecyclerView;
    private ADWebView ad1;
    private AttentionFilmTitleAdatper.OnItemSelectListener onItemSelectListener;
    private List<IncomingFilmBean.RecommendsBean> attentionsBean;
    private RequestCallback incomingCallback;
    private boolean loadingFailedIncomingMovie = false;
    private List<IncomingFilmBean.MoviecomingsBean> incomingBean;//即将上映列表
    private final ArrayList<IncomingFilmBean.MoviecomingsBean> newList = new ArrayList<IncomingFilmBean.MoviecomingsBean>();
    private IRecyclerView lvIncoming;//即将上映
    private PullRefreshHeaderView refreshHeaderView;
    private View layout_failed_holder;
    private TextView tv_failed;
    private LinearLayout attention_num_layout;
    private TextView name;
    private MoveLayout board, move_board;
    private String cityId;
    private RequestCallback wantMovieIdsCallback;
    private ArrayList<Integer> wantFilmIds = new ArrayList<>();
    private TicketApi mTicketApi;
    public static String removeRemindMovieId = null;
    public static String addRemindMovieId = null;

    public void setCityId(final String cityId) {
        this.cityId = cityId;
    }

    @Override
    protected View setContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ticket_moviesincoming_view, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBaseStatisticHelper.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
        mBaseStatisticHelper.setSubmit(true);

        if(null == mTicketApi) {
            mTicketApi = new TicketApi();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        /**更新该影片想看状态*/
        updateRemindData();
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        layout_failed_holder = getView().findViewById(R.id.loading_failed_layout);
        tv_failed = getView().findViewById(R.id.retryErrorTv);

        attentionFilmTitleAdatper = new AttentionFilmTitleAdatper(context, new ArrayList<String>());
        incomingFilmAdapter = new IncomingFilmAdapter(context, new ArrayList<IncomingFilmBean.MoviecomingsBean>());
        attentionFilmAdapter = new AttentionFilmAdapter(context, new ArrayList<IncomingFilmBean.MoviecomingsBean>());
        incomingFilmAdapter.setAttentionFilmAdapter(attentionFilmAdapter);
        attentionFilmAdapter.setIncomingFilmAdapter(incomingFilmAdapter);

        incomingView = getView().findViewById(R.id.list_movie_incoming);
        initIncomingListView();
    }

    @Override
    protected void onInitEvent() {
        /**
         * 选中关注标签儿
         */
        onItemSelectListener = new AttentionFilmTitleAdatper.OnItemSelectListener() {
            @Override
            public void onSelect(int position, String title) {
                if (attentionsBean != null && attentionsBean.size() > 0) {
                    for (int i = 0; i < attentionsBean.size(); i++) {
                        IncomingFilmBean.RecommendsBean recommendsBean = attentionsBean.get(i);
                        if (recommendsBean != null) {
                            String recommendTitle = recommendsBean.getRecommendTitle();
                            if (title.equals(recommendTitle)) {
                                attentionFilmAdapter.clear();
                                attentionFilmAdapter.addAll(recommendsBean.getMovies());
                                attentionFilmAdapter.setTagIndex(position);
                                if (attentionFilmAdapter.getItemCount() > 0) {
                                    recyclerView.scrollToPosition(0);
                                }
                                break;
                            }
                        }
                    }
                }
            }
        };

        incomingCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                loadingFailedIncomingMovie = false;
                lvIncoming.setVisibility(View.VISIBLE);
                try {
                    IncomingFilmBean incomingBeanaAll = (IncomingFilmBean) o;
                    // set attentions value
                    attentionsBean = incomingBeanaAll.getRecommends();
                    // init the attentions movies and incoming
                    incomingBean = incomingBeanaAll.getMoviecomings();
                    String old = null;
                    newList.clear();
                    if (incomingBean != null) {
                        int lastYear = 0;
                        int lastMonth = 0;
                        int lastDay = 0;
                        for (final IncomingFilmBean.MoviecomingsBean b : incomingBean) {
                            old = calcDateString(old, b);
                            if (b != null) {//判断是否是当前天的第一个
                                int rYear = b.getReleaseYear();
                                int rMonth = b.getReleaseMonth();
                                int rDay = b.getReleaseDay();
                                //有一个不为相等的时候就是开头
                                b.setHeader(lastDay != rDay || lastMonth != rMonth || lastYear != rYear);
                                lastDay = rDay;
                                lastMonth = rMonth;
                                lastYear = rYear;
                            }
                        }
                    } else {
                    }

                    requestWantMovieIds();
                    incomingFilmAdapter.clear();
                    incomingFilmAdapter.addAll(incomingBeanaAll.getMoviecomings());
                    if (incomingFilmAdapter.getItemCount() > 0) {
                        lvIncoming.scrollToPosition(0);
                    }
                    showAttentionsView(attentionsBean);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                refreshHeaderView.onComplete();
                lvIncoming.setRefreshing(false);
                lvIncoming.setVisibility(View.GONE);
                loadingFailedIncomingMovie = true;
                UIUtil.showLoadingFailedLayout(layout_failed_holder, tv_failed, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 刷新购票tab顶部banner
                        refreshBanner();
                        reLoadData();
                    }
                });
            }
        };

        wantMovieIdsCallback = new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                WantSeeFilmBean wantSeeFilmBean = (WantSeeFilmBean) o;
                if (wantSeeFilmBean != null) {
                    List<Integer> movieIds = wantSeeFilmBean.getMovieIds();
                    if (movieIds != null && movieIds.size() > 0) {
                        wantFilmIds.clear();
                        for (int i = 0; i < movieIds.size(); i++) {
                            wantFilmIds.add(movieIds.get(i));
                        }
                    }
                    if (attentionFilmAdapter != null) {
                        attentionFilmAdapter.setWantMoviesList(wantFilmIds);
                    }
                    if (incomingFilmAdapter != null) {
                        incomingFilmAdapter.setWantMoviesList(wantFilmIds);
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
            }
        };
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (getUserVisibleHint()) {
//            reLoadData();
//        }
//    }


    @Override
    protected void onRequestData() {
//        reLoadData();
    }

    @Override
    protected void onErrorRetry() {
        // 刷新购票tab顶部banner
        refreshBanner();
        reLoadData();
    }

    /**
     * 即将上映ListView
     */
    private void initIncomingListView() {
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.movie_incoming_list_header, null);
        recyclerView = headerView.findViewById(R.id.attentions_view);
        attentionTitleRecyclerView = headerView.findViewById(R.id.recommend_title_recyclerview);
        LinearLayoutManager recycleLinearLayoutManager = new LinearLayoutManager(context);
        recycleLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        attentionTitleRecyclerView.setLayoutManager(recycleLinearLayoutManager);
        attentionTitleRecyclerView.setAdapter(attentionFilmTitleAdatper);
        // 预告片
        mVideoTitleTv = headerView.findViewById(R.id.video_recommend_title);
        mVideoRecyclerView = headerView.findViewById(R.id.video_recommend_rv);
        mVideoRecyclerView.setLayoutManager(
                new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL,false)
        );
        mTabTicketVideoAdapter = new TabTicketVideoAdapter(null);
        mVideoRecyclerView.setAdapter(mTabTicketVideoAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(attentionFilmAdapter);
        ad1 = headerView.findViewById(R.id.ad);
        attentionsBean = null;
        attention_num_layout = headerView.findViewById(R.id.attention_num_layout);

        board = incomingView.findViewById(R.id.board);
        name = board.findViewById(R.id.name);
        board.setVisibility(View.GONE);

        move_board = incomingView.findViewById(R.id.move_board);
        move_board.setVisibility(View.GONE);

        lvIncoming = incomingView.findViewById(R.id.movie_incoming_list);
        refreshHeaderView = (PullRefreshHeaderView) lvIncoming.getRefreshHeaderView();

        lvIncoming.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(context);
        linearLayoutManager1.setOrientation(LinearLayoutManager.VERTICAL);
        lvIncoming.setLayoutManager(linearLayoutManager1);
        lvIncoming.setIAdapter(incomingFilmAdapter);
        lvIncoming.addHeaderView(headerView);
        lvIncoming.setRefreshEnabled(true);
        lvIncoming.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItemPosition >= 1) {
                    IncomingFilmBean.MoviecomingsBean moviecomingsBean = incomingFilmAdapter.getItem(firstVisibleItemPosition - 2);
                    if (moviecomingsBean != null) {
                        board.setVisibility(View.VISIBLE);
                        name.setText(moviecomingsBean.getDateString());
                    } else {
                        board.setVisibility(View.GONE);
                    }
                } else {
                    board.setVisibility(View.GONE);
                }
            }
        });
        lvIncoming.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 刷新购票tab顶部banner
                refreshBanner();
                requestIncomingData();
            }
        });

    }

    /**
     * 请求即将上映列表数据
     */
    public void requestIncomingData() {
        if(((TicketFragment)getParentFragment()).getCurrentItem() == 2) {
            UIUtil.showLoadingDialog(context);
        }
        requestVideoList();
        Map<String, String> param = new HashMap<>(1);
        param.put("locationId", cityId);
        HttpUtil.get(ConstantUrl.GET_INCOMING_RECOMMEND_LIST, param, IncomingFilmBean.class, incomingCallback);
        requestAds();
    }

    /**
     * 获取预告片推荐列表
     */
    private void requestVideoList() {
        // 统一获取推荐位数据
        mTicketApi.getRcmdRegionPublishList(CommConstant.RCMD_REGION_NEW_VIDEO, new NetworkManager.NetworkListener<CommonRegionPusblish>() {
            @Override
            public void onSuccess(CommonRegionPusblish result, String showMsg) {
                if(result != null
                        && result.regionList != null && !result.regionList.isEmpty()
                        && result.regionList.get(0).items != null && !result.regionList.get(0).items.isEmpty()) {
                    List<Map<String, String>> items = result.regionList.get(0).items;
                    mTabTicketVideoAdapter.getData().clear();
                    mTabTicketVideoAdapter.addData(items);
                    // 点击跳转
                    mTabTicketVideoAdapter.setOnItemClickListener((adapter, view, position) -> {
                        Map<String, String> bean = (Map<String, String>) adapter.getData().get(position);
                        if(bean != null) {
                            String idStr = bean.get("videoId");
                            if(!TextUtils.isEmpty(idStr)) {
                                Long videoId = Long.parseLong(idStr);
                                if(videoId > 0) {
                                    // 视频详情页
                                    ProviderExtKt.getProvider(IVideoProvider.class).startPreVideoActivity(videoId);
                                }
                            }
                        }
                    });
                    mVideoTitleTv.setVisibility(View.VISIBLE);
                    mVideoRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    mVideoTitleTv.setVisibility(View.GONE);
                    mVideoRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(NetworkException<CommonRegionPusblish> exception, String showMsg) {
                mVideoTitleTv.setVisibility(View.GONE);
                mVideoRecyclerView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 刷新购票tab顶部banner
     */
    private void refreshBanner() {
        ((TicketFragment)getParentFragment()).requestBanner();
    }

    private void requestAds() {
        Map<String, String> param = new HashMap<>(1);
        param.put("locationId", cityId);
        HttpUtil.get(ConstantUrl.AD_MOBILE_ADVERTISEMENT_INFO, param, ADTotalBean.class, new RequestCallback() {

            @Override
            public void onFail(Exception e) {
                ad1.setVisibility(View.GONE);
            }

            @Override
            public void onSuccess(Object o) {
                ADTotalBean bean = (ADTotalBean) o;
                ADDetailBean item = ToolsUtils.getADBean(bean, App.getInstance().AD_MOVIE_INCOMING);
                if (!ADWebView.show(item)) {
                    ad1.setVisibility(View.GONE);
                    return;
                }
                ad1.setVisibility(View.VISIBLE);
//                context.setPageLabel(StatisticTicket.PN_FUTURE_SCHEDULE);
                ad1.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
                    @Override
                    public void onAdItemClick(ADDetailBean item, String url) {
//                        StatisticPageBean bean1 = context.assemble(StatisticTicket.TICKET_AD, null, null, null, null, null, null);
//                        StatisticManager.getInstance().submit(bean1);
                    }
                });
                ad1.load(context, item);

            }
        }, 30 * 60 * 1000, null, 2000);

    }

    /**
     * 计算即将上映影片该显示的日期
     *
     * @param old
     * @param b
     * @return
     */
    private String calcDateString(String old, final IncomingFilmBean.MoviecomingsBean b) {
        String dateString = "";
        if (b != null) {
            int rYear = b.getReleaseYear();
            int rMonth = b.getReleaseMonth();
            int rDay = b.getReleaseDay();
            int year = Calendar.getInstance().get(Calendar.YEAR);
            if (year == rYear) {
                if (rMonth != 0) {
                    if (rDay != 0) {
                        dateString = String.format("%1$d月%2$d日", rMonth, rDay);
                    } else {
                        dateString = String.format("%1$d月待定", rMonth);
                    }
                } else {
                    dateString = String.format("%1$d年待定", rYear);
                }
            } else {//年份不相等
                if (rYear != 0) {
                    if (rMonth != 0) {
                        if (rDay != 0) {
                            dateString = String.format("%1$d年%2$d月%3$d日", rYear, rMonth, rDay);
                        } else {//没有日期
                            dateString = String.format("%1$d年%2$d月待定", rYear, rMonth);
                        }
                    } else {
                        dateString = String.format("%1$d年待定", rYear);
                    }
                } else {
                    if (rMonth != 0) {
                        if (rDay != 0) {
                            dateString = String.format("%1$d月%2$d日", rMonth, rDay);
                        } else {
                            dateString = String.format("%1$d月待定", rMonth);
                        }
                    } else {

                    }
                }

            }
        }
        if (b != null) {
            b.setDateString(dateString);
        }
        return dateString;
    }


    //只有刚从登录回来才走该方法
    public void requestWantMovieIds() {
        if (UserManager.Companion.getInstance().isLogin()) {
            HashMap<String, String> urlParam = new HashMap<>();
            urlParam.put("locationId", GlobalDimensionExt.INSTANCE.getCurrentCityId());
            HttpUtil.get(ConstantUrl.GET_WANT_MOVIE_IDS, urlParam, WantSeeFilmBean.class, wantMovieIdsCallback);
        } else {
            if (wantFilmIds == null) {
                wantFilmIds = new ArrayList<>();
            } else {
                wantFilmIds.clear();
                if (attentionFilmAdapter != null) {
                    attentionFilmAdapter.setWantMoviesList(wantFilmIds);
                }
                if (incomingFilmAdapter != null) {
                    incomingFilmAdapter.setWantMoviesList(wantFilmIds);
                }
            }
        }
    }

    public void showAttentionsView(List<IncomingFilmBean.RecommendsBean> attentions) {
        UIUtil.dismissLoadingDialog();
        refreshHeaderView.onComplete();
        lvIncoming.setRefreshing(false);
        if (attentions == null || attentions.size() == 0) {
            recyclerView.setVisibility(View.GONE);
            attention_num_layout.setVisibility(View.GONE);
            return;
        }
        attention_num_layout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        if (attentions.size() > 0) {
            attentionFilmAdapter.clear();
            IncomingFilmBean.RecommendsBean recommendsBean = attentions.get(0);
            if (recommendsBean != null) {
                attentionFilmAdapter.addAll(recommendsBean.getMovies());
                attentionFilmAdapter.setTagIndex(0);
                if (attentionFilmAdapter.getItemCount() > 0) {
                    recyclerView.scrollToPosition(0);
                }

            }
            ArrayList<String> attentionsTitleList = new ArrayList<>();
            for (int i = 0; i < attentions.size(); i++) {
                IncomingFilmBean.RecommendsBean recommendsBean1 = attentions.get(i);
                attentionsTitleList.add(recommendsBean1.getRecommendTitle());
            }
            attentionFilmTitleAdatper.clear();
            attentionFilmTitleAdatper.addAll(attentionsTitleList);
            attentionFilmTitleAdatper.setOnItemSelectListener(onItemSelectListener);
            if (attentionFilmTitleAdatper.getItemCount() > 0) {
                attentionTitleRecyclerView.scrollToPosition(0);
            }
        }
    }

    public void reLoadData() {
        if(layout_failed_holder.getVisibility() == View.VISIBLE) {
            layout_failed_holder.setVisibility(View.GONE);
        }
        attentionsBean = null;
        incomingView.setVisibility(View.VISIBLE);
        if (null == attentionsBean || loadingFailedIncomingMovie) {
            requestIncomingData();
        }
    }
    
    /**
     * 更新该影片想看状态
     */
    public void updateRemindData() {
        if (null == incomingFilmAdapter) {
            return;
        }

        if (TextUtils.isEmpty(addRemindMovieId) && TextUtils.isEmpty(removeRemindMovieId)) {
            return;
        }

        if (UserManager.Companion.getInstance().isLogin()) {
            if (!TextUtils.isEmpty(addRemindMovieId)) {
                incomingFilmAdapter.modifyRemindIds(addRemindMovieId, true);
            } else if (!TextUtils.isEmpty(removeRemindMovieId)) {
                incomingFilmAdapter.modifyRemindIds(removeRemindMovieId, false);
            }
        }
        incomingFilmAdapter.notifyDataSetChanged();
        addRemindMovieId = null;
        removeRemindMovieId = null;
    }

    public boolean needRequest(final String cityId) {
        if (null == this.cityId || !this.cityId.equalsIgnoreCase(cityId)) {
            this.cityId = cityId;
            return true;
        }
        return false;
    }

    public List<IncomingFilmBean.RecommendsBean> getAttentionsBean() {
        return attentionsBean;
    }

    @Override
    protected boolean openEventBus() {
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPosterFilter(PosterFilterEvent event) {
        if (null != incomingFilmAdapter) {
            incomingFilmAdapter.notifyDataSetChanged();
        }
    }
}
