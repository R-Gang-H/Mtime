package com.mtime.bussiness.ticket.cinema.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.kotlin.android.app.data.constant.CommConstant;
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity;
import com.kotlin.android.app.data.entity.mine.UserCollectQuery;
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider;
import com.kotlin.android.film.JavaOpenSeatActivity;
import com.kotlin.android.image.coil.ext.CoilCompat;
import com.kotlin.android.ktx.ext.dimension.DimensionExtKt;
import com.kotlin.android.ktx.ext.time.TimeExt;
import com.kotlin.android.router.ext.ProviderExtKt;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.widget.marquee.MarqueeTextView;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.bean.CollectResultBean;
import com.mtime.bussiness.ticket.bean.Provider;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaShowtimeMovieAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaShowtimeViewPagerAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CouponActivityListAdapter;
import com.mtime.bussiness.ticket.cinema.bean.CinemaMoviesCouponActivityItem;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfCinemaBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowtimeUPHalfMovieBean;
import com.mtime.bussiness.ticket.cinema.bean.GeniueSurroundingBean;
import com.mtime.bussiness.ticket.cinema.bean.MovieTimeChildMainBean;
import com.mtime.bussiness.ticket.cinema.bean.ShowtimeJsonBean;
import com.mtime.bussiness.ticket.cinema.widget.ScrollViewWithViewPager;
import com.mtime.bussiness.ticket.widget.CouponLayout;
import com.mtime.common.utils.ConvertHelper;
import com.mtime.common.utils.DateUtil;
import com.mtime.event.entity.PosterFilterEvent;
import com.mtime.frame.App;
import com.mtime.frame.BaseFrameUIActivity;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.ticket.StatisticTicket;
import com.mtime.util.ImageLoader;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.SaveOffenGo;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.PagerSlidingTabStrip;
import com.mtime.widgets.ScrollListView;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.widgets.recyclerview.CoverFlowRecyclerView;
import com.mtime.widgets.recyclerview.RecyclerItemClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * Created by zhuqiguang on 2018/5/22.
 * website www.zhuqiguang.cn
 */
public class CinemaShowtimeContentHolder extends ContentHolder<GeniueSurroundingBean> implements CoverFlowRecyclerView.CoverFlowItemListener {

    public static final int EVENT_CODE_START = 101;
    public static final int RESULT_CODE = 0;
    public static final int EVENT_CODE_ITEM_CHANGED = 102;
    // 收藏动作
    private static final int COLLECT_ACTION_ADD = 1;
    private static final int COLLECT_ACTION_CANCEL = 2;

    private final OnShowtimeListener mListener;
    private final FragmentManager mFragmentManager;
    private final BaseFrameUIActivity mActivity;
    @BindView(R.id.navigationbar)
    View mNavigationBar;
    @BindView(R.id.cinemashowtime_cinemaname_textview)
    TextView mCinemaNameTv;
    @BindView(R.id.cinemashowtime_cinemaaddress_textview)
    TextView mCinemaAddressTv;
    @BindView(R.id.layout_features_cinema)
    CouponLayout mFeaturesCinemaCl;
    @BindView(R.id.layout_goto_cinemadetail)
    LinearLayout mCinemaDetailLl;
    @BindView(R.id.couponactivity_listview)
    ScrollListView mCouponactivityLv;
    @BindView(R.id.couponactivity_layout)
    LinearLayout mCouponactivityLayout;
    @BindView(R.id.view_recyclerview_bottom)
    ImageView mRecyclerviewBottom;
    @BindView(R.id.cinemashowtime_movies_recycleview)
    CoverFlowRecyclerView mCoverRv;
    @BindView(R.id.cinema_showtime_movietitle)
    TextView cinema_showtime_movietitle;
    @BindView(R.id.cinema_showtime_moviedate)
    TextView cinema_showtime_moviedate;
    @BindView(R.id.cinema_showtime_movielength)
    TextView cinema_showtime_movielength;
    @BindView(R.id.layout_movieinfo)
    RelativeLayout mMovieInfoRl;
    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;
    @BindView(R.id.pager)
    ViewPager pager;
    @BindView(R.id.cinema_showtime_child_layout)
    LinearLayout cinema_showtime_child_layout;
    @BindView(R.id.cinema_showtime_child_layout_line)
    View mGeniueSurroundLine;
    @BindView(R.id.cinema_showtime_tv)
    TextView cinemaShowtimeTv;
    @BindView(R.id.cinema_showtime_more_tv)
    TextView cinemaShowtimeMoreTv;
    @BindView(R.id.cinema_showtime_genuine_surrounding_layout)
    LinearLayout mGenuineSurroundingLayout;
    @BindView(R.id.cinema_showtime_lla_genuine_surrounding_root)
    RelativeLayout mGenuineSurroundingRoot;
    @BindView(R.id.cinema_showtime_child_loading_layout)
    RelativeLayout childLoadingLayout;
    @BindView(R.id.layout_cinema_showtime_no)
    LinearLayout mCinemaShowtimeNoLl;
    @BindView(R.id.layout_cinema_showtime)
    LinearLayout mCinemaShowtimeLl;
    @BindView(R.id.act_cinema_direct_selling_tv)
    TextView mDirectSellingTv;
    @BindView(R.id.scrollview)
    ScrollViewWithViewPager mScrollview;
    @BindView(R.id.guide_iknow_btn)
    ImageView mGuideIknowBtn;
    @BindView(R.id.cover_holder)
    RelativeLayout mCoverHolderRl;
    @BindView(R.id.noticeCard)
    CardView mNoticeCard;
    @BindView(R.id.noticeMarqueeTv)
    MarqueeTextView mNoticeMarqueeTv;
    @BindView(R.id.activityMarqueeTv)
    MarqueeTextView mActivityMarqueeTv;
    @BindView(R.id.activityLineView)
    View mActivityLineView;

    private Unbinder mUnbinder;
    private final String mCinemaId;
    private final String mMovieId;
    private String mShowDateString;
    private TitleOfNormalView navigationBar;
    private int mPosition;
    //    private List<GeniueSurroundingBean.DetailRelatedsBean> mGeniueSurroundingList = new ArrayList<>();
    private CinemaShowtimeMovieAdapter movieAdapter;
    private CinemaShowtimeUPHalfBean mUpHalfBean;
    private int tId;
    private int selectedMovieId;

    // 图片加载器
    public ImageLoader volleyImageLoader = new ImageLoader();
    private final Handler mHandler = new Handler();
    private CinemaShowtimeViewPagerAdapter pagerAdapter;
    private boolean isNotFirstShowDateString;
    private String passDateString;
    private boolean isFrist = true;
    private TicketApi mTicketApi;

    public boolean getFavoriate() {
        return navigationBar.getFavoriate();
    }

    public CinemaShowtimeContentHolder(Context context, FragmentManager fragmentManager, String cinemaId, String movieId, String showDateString, OnShowtimeListener listener) {
        super(context);
        mActivity = (BaseFrameUIActivity) context;
        mCinemaId = cinemaId;
        mMovieId = movieId;
        mShowDateString = showDateString;
        mFragmentManager = fragmentManager;
        mListener = listener;
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_cinema_showtime);
        mUnbinder = ButterKnife.bind(this, mRootView);
    }

    @Override
    public void onHolderCreated() {
        super.onHolderCreated();
        mTicketApi = new TicketApi();
        EventBus.getDefault().register(this);
        navigationBar = new TitleOfNormalView(mActivity, mNavigationBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_FAVORITE, "",
                (type, content) -> {
                    switch (type) {
                        case TYPE_FAVORITE:
                            Map<String, String> businessParam1 = new HashMap<String, String>();
                            businessParam1.put(StatisticConstant.CINEMA_ID, String.valueOf(mCinemaId));
                            businessParam1.put(StatisticConstant.COLLECT_STATE, navigationBar.getFavoriate() ? "collect" : "cancel");
                            StatisticPageBean bean1 = mActivity.assemble(StatisticTicket.TICKET_TOP_NAVIGATION, null, "collect", null, null, null, businessParam1);
                            StatisticManager.getInstance().submit(bean1);
                            NavigationBarFavoriteClick(content);
                            break;
                        case TYPE_BACK:
                            Intent intent = new Intent();
                            intent.putExtra("cinemaId", TextUtils.isEmpty(mCinemaId) ? "" : mCinemaId);
                            intent.putExtra("isFavoriate", navigationBar.getFavoriate());
                            if (mListener != null) {
                                mListener.setResultForActivity(RESULT_CODE, intent);
                            }
                            break;
                        default:
                            break;
                    }

                });

        mScrollview.post(() -> {
            try {
                mScrollview.smoothScrollTo(0, 0);
            } catch (Exception ignored) {

            }

        });
//        cinemaShowtimeMoreTv.setOnClickListener(this::jumpGoodsList);
        mCinemaDetailLl.setOnClickListener(this::gotoCinemaDetail);
        mMovieInfoRl.setOnClickListener(this::gotoMovieView);
        mCoverRv.setCoverFlowListener(this);
        mCoverRv.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mCoverRv.scrollToCenter(position);
            }
        }));


        // 这里判断是否需要显示遮罩提醒
        boolean show = App.getInstance().getPrefsManager().getBoolean("show_cimean_movies_cover", true);
        if (show) {
            final View cover = mCoverHolderRl;
            cover.setVisibility(View.VISIBLE);
            ImageView btn = mGuideIknowBtn;
            btn.setOnClickListener(v -> cover.setVisibility(View.GONE));
            App.getInstance().getPrefsManager().putBoolean("show_cimean_movies_cover", false);
        }
    }

    private void gotoCinemaDetail(View view) {
        if (mListener != null) mListener.gotoCinemaDetail();
    }

    private void NavigationBarFavoriteClick(String content) {
        if (!UserManager.Companion.getInstance().isLogin()) {
            navigationBar.setIsFavorited();
            if (mListener != null && mUpHalfBean != null && mUpHalfBean.getCinema() != null) {
                mListener.gotoLogin(mUpHalfBean.getCinema().getDirectSalesFlag());
            }
            return;
        }
        // 收藏/取消收藏
        collect(Boolean.valueOf(content) ? COLLECT_ACTION_ADD : COLLECT_ACTION_CANCEL);
    }

    /**
     * 收藏/取消收藏
     */
    private void collect(int action) {
        UIUtil.showLoadingDialog(mContext);
        mTicketApi.postCollect(action, CommConstant.COLLECTION_OBJ_TYPE_CINEMA, mCinemaId, new NetworkManager.NetworkListener<CollectResultBean>() {
            @Override
            public void onSuccess(CollectResultBean result, String showMsg) {
                UIUtil.dismissLoadingDialog();
                if (result.getBizCode() == CollectResultBean.SUCCESS) {
                    // 成功
                    MToastUtils.showShortToast(action == COLLECT_ACTION_ADD ? "已添加到我的收藏" : "已取消收藏");
                    navigationBar.setFavoriate(action == COLLECT_ACTION_ADD);
                } else {
                    // 失败
                    MToastUtils.showShortToast((action == COLLECT_ACTION_ADD ? "收藏失败" : "取消收藏失败:") + showMsg);
                    navigationBar.setFavoriate(action == COLLECT_ACTION_CANCEL);
                }
            }

            @Override
            public void onFailure(NetworkException<CollectResultBean> exception, String showMsg) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast((action == COLLECT_ACTION_ADD ? "收藏失败" : "取消收藏失败:") + showMsg);
                navigationBar.setFavoriate(action == COLLECT_ACTION_CANCEL);
            }
        });
    }

    public void gotoMovieView(View v) {
        Map<String, String> businessParam = new HashMap<String, String>();
        businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(selectedMovieId));
        StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_POSTER, String.valueOf(tId), null, null, null, null, businessParam);
        StatisticManager.getInstance().submit(bean);
        JumpUtil.startMovieInfoActivity(mContext, bean.toString(), String.valueOf(selectedMovieId), 0);
    }

    public void updateCinemaShowtime(CinemaShowtimeUPHalfBean upHalfBean) {
        mUpHalfBean = upHalfBean;
        final CinemaShowtimeUPHalfCinemaBean upHalfCinemaBean = upHalfBean.getCinema();
        if (upHalfCinemaBean != null) {
            // 当前用户收藏影院状态
            requestCollect();

            mCinemaNameTv.setText(upHalfCinemaBean.getName());
            if (!TextUtils.isEmpty(upHalfCinemaBean.getAddress())) {
                mCinemaAddressTv.setVisibility(View.VISIBLE);
                mCinemaAddressTv.setText(upHalfCinemaBean.getAddress());
            }
            if (upHalfCinemaBean.getDirectSalesFlag() == CinemaShowtimeUPHalfCinemaBean.THREE_PARTY_SALES_FALG) {
                mDirectSellingTv.setVisibility(View.VISIBLE);
            }
            // TODO: 16/8/29 因为接口不支持返回文案，所以先在本地判断实现显示文案
//                        List<String> features = upHalfCinemaBean.getFeatures();
            List<String> features = new ArrayList<>();
            CinemaShowtimeUPHalfFeatureBean featureBean = upHalfCinemaBean.getFeature();
            if (featureBean.getHas3D() == 1) {
                features.add("3D");
            }
            if (featureBean.getHasIMAX() == 1) {
                features.add("IMAX");
            }
            if (featureBean.getHasVIP() == 1) {
                features.add("VIP");
            }
            if (featureBean.getHas4D() == 1) {
                features.add("4D");
            }
            if (featureBean.getHasFeatureHuge() == 1) {
                features.add("巨幕");
            }
            if (featureBean.getHasFeature4K() == 1) {
                features.add("4K");
            }
            if (featureBean.getHasFeatureDolby() == 1) {
                features.add("杜比");
            }
            if (featureBean.getHasSphereX() == 1) {
                features.add("SphereX");
            }
            if (featureBean.getHasScreenX() == 1) {
                features.add("ScreenX");
            }
            if (featureBean.getHasLoveseat() == 1) {
                features.add("情侣座");
            }
            if (featureBean.getHasPark() == 1) {
                features.add("可停车");
            }
//            if (featureBean.getHasServiceTicket() == 1) {
//                features.add("自助取票");
//            }
//            if (featureBean.getHasWifi() == 1) {
//                features.add("WiFi");
//            }

            if (null != features && features.size() > 0) {
                mFeaturesCinemaCl.setVisibility(View.VISIBLE);
                for (int i = 0; i < features.size(); i++) {
                    LayoutInflater inflater = getLayoutInflater();
                    View tvView = inflater.inflate(R.layout.tv_color_6d9297_border, null);
                    TextView textView = tvView.findViewById(R.id.tv_color_6d8297);
                    textView.setText(features.get(i));
                    mFeaturesCinemaCl.addView(tvView);
                }
                TextView textView = new TextView(mContext);
                mFeaturesCinemaCl.addView(textView);
            } else {
                mFeaturesCinemaCl.setVisibility(View.GONE);
            }

            List<CinemaMoviesCouponActivityItem> couponActivityList = upHalfCinemaBean.getActivityList();
            if (couponActivityList != null && couponActivityList.size() > 0) {
                mCouponactivityLayout.setVisibility(View.VISIBLE);
                CouponActivityListAdapter couponActivityListAdapter = new CouponActivityListAdapter(mActivity, couponActivityList);
                mCouponactivityLv.setAdapter(couponActivityListAdapter);
            } else {
                mCouponactivityLayout.setVisibility(View.GONE);
            }

        }
        final List<CinemaShowtimeUPHalfMovieBean> upHalfMovieBeans = upHalfBean.getMovies();
        mCinemaShowtimeLl.setVisibility(View.VISIBLE);
        if (upHalfMovieBeans != null && upHalfMovieBeans.size() > 0) {
            // 请求正规周边详情
//            requestGenuineSurrounding(getMovieIds(upHalfMovieBeans));
            mGeniueSurroundLine.setVisibility(View.GONE);
            mGenuineSurroundingRoot.setVisibility(View.GONE);

            mCinemaShowtimeNoLl.setVisibility(View.GONE);
            for (int i = 0; i < 2; i++) {
                upHalfMovieBeans.add(0, new CinemaShowtimeUPHalfMovieBean(true));
                upHalfMovieBeans.add(new CinemaShowtimeUPHalfMovieBean(true));
            }
            movieAdapter = new CinemaShowtimeMovieAdapter(mContext, upHalfMovieBeans);
            mCoverRv.setAdapter(movieAdapter);

            int selId = 2;
            for (int j = 0; j < upHalfMovieBeans.size(); j++) {
                if (!upHalfMovieBeans.get(j).isBorder()) {
                    if (mMovieId != null && !"0".equals(mMovieId)
                            && mMovieId.equals(String.valueOf(upHalfMovieBeans.get(j).getMovieId()))) {
                        selId = j;
                        break;
                    }
                }
            }
            if (selId != 2) {
                mCoverRv.getLayoutManager().scrollToPosition(selId - 2);
            }
            if (selId > upHalfMovieBeans.size() || selId < 0) {
                selId = 2;
            }

            final Date nowDate = MTimeUtils.getLastDiffServerDate();
            final long nowTime = nowDate.getTime();
            List<MovieTimeChildMainBean> showtimeJsonBeans = upHalfBean.getShowtimes();
            // 倒过来遍历list，正着遍历，就会出现错误--之前的注释。不理解要倒着的原因
            //因为服务器不做垃圾数据的筛选，只能这么多层for循环去处理了
            for (int i = showtimeJsonBeans.size() - 1; i >= 0; i--) {
                List<ShowtimeJsonBean> list = showtimeJsonBeans.get(i).getList();
                for (int j = list.size() - 1; j >= 0; j--) {
                    if (((list.get(j).getShowDay())) < nowTime) {
                        showtimeJsonBeans.get(i).getList().remove(j);
                    }
                }
                if (list.size() == 0) {
                    String movieKey = showtimeJsonBeans.get(i).getMoviekey();
                    String[] strs = movieKey.split("_");
                    if (strs.length == 2) {//删除掉movie数组里面的日期
                        for (int k = 0; k < upHalfMovieBeans.size(); k++) {
                            if (!upHalfMovieBeans.get(k).isBorder() &&
                                    Integer.parseInt(strs[0]) == upHalfMovieBeans.get(k).getMovieId()) {
                                List<String> showDatas = upHalfMovieBeans.get(k).getShowDates();
                                for (int m = 0; m < showDatas.size(); m++) {
                                    String dateString = showDatas.get(m);
                                    int gapCount = DateUtil.getGapCount(MTimeUtils.getLastDiffServerDate(),
                                            DateUtil.getDateFromStr(dateString));
                                    //gapCount == 0//今天。保留今天的页签。删除掉除今天之前的无效页签日期,因为产品要显示今天的页签，以免页面显示空
                                    if (gapCount != 0 && strs[1].equals(dateString)) {
                                        upHalfMovieBeans.get(k).getShowDates().remove(m);
                                    }
                                }
                            }
                        }
                        int gapCount = DateUtil.getGapCount(MTimeUtils.getLastDiffServerDate(),
                                DateUtil.getDateFromStr(strs[1]));
                        if (gapCount != 0) {
                            showtimeJsonBeans.remove(i);//删掉拍片里面的数据
                        }
                    }
                }
            }
            updateMoviesSelectStatus(upHalfMovieBeans, selId);
        } else {
            mCinemaShowtimeNoLl.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        // 当前用户收藏影院状态
        requestCollect();
    }

    /**
     * 当前用户收藏影院状态
     */
    private void requestCollect() {
        if (UserManager.Companion.getInstance().isLogin()) {
            if (null != mTicketApi) {
                mTicketApi.userCollectQuery(CommConstant.COLLECTION_OBJ_TYPE_CINEMA, mCinemaId, new NetworkManager.NetworkListener<UserCollectQuery>() {
                    @Override
                    public void onSuccess(UserCollectQuery result, String showMsg) {
                        // 收藏
                        boolean isCollect = result != null && result.isCollect() != null && result.isCollect();
                        navigationBar.setFavoriate(isCollect);
                    }

                    @Override
                    public void onFailure(NetworkException<UserCollectQuery> exception, String showMsg) {

                    }
                });
            }
        } else {
            navigationBar.setFavoriate(SaveOffenGo.getInstance().contains(mCinemaId));
        }
    }

    /**
     * 请求正规周边详情
     */
//    private void requestGenuineSurrounding(String movieIds) {
//        // movieIds="31079,11675";
//        if (!TextUtils.isEmpty(movieIds)) {
//            Map<String, String> param = new HashMap<>();
//            param.put("movieIds", movieIds);
//            HttpUtil.get(ConstantUrl.GET_MALL_MOVIE_RELEATED_GOODS, param, GeniueSurroundingBean.class, new RequestCallback() {
//                @Override
//                public void onSuccess(Object o) {
//                    GeniueSurroundingBean beans = (GeniueSurroundingBean) o;
//                    if (null != beans && null != beans.getDetailRelateds() && beans.getDetailRelateds().size() > 0) {
//                        mGeniueSurroundingList = beans.getDetailRelateds();
//                        fillData(mPosition);
//                    }
//                }
//
//                @Override
//                public void onFail(Exception e) {
//
//                }
//            });
//        }
//    }
    private void updateMoviesSelectStatus(final List<CinemaShowtimeUPHalfMovieBean> upHalfMovieBeans, int selId) {
        if (upHalfMovieBeans == null || upHalfMovieBeans.size() == 0) {
            mCinemaShowtimeNoLl.setVisibility(View.VISIBLE);
            return;
        }
        Map<String, String> businessParam = new HashMap<String, String>();
        businessParam.put(StatisticConstant.MOVIE_ID, String.valueOf(mUpHalfBean.getMovies().get(selId).getMovieId()));
        StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_SELECT_MOVIE, String.valueOf(selId), null, null, null, null, businessParam);
        StatisticManager.getInstance().submit(bean);

        if (null != getActivity() && !getActivity().isDestroyed()) {
            CoilCompat.INSTANCE.loadBlurImage(
                    mRecyclerviewBottom,
                    upHalfMovieBeans.get(selId).getImg(),
                    DimensionExtKt.getScreenWidth(),
                    DimensionExtKt.getDp(135),
                    true,
                    R.drawable.default_image,
                    20,
                    4
            );
        }

        tId = selId;
        mPosition = selId - 2;
        cinema_showtime_child_layout.removeAllViews();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final CinemaShowtimeUPHalfMovieBean bean = upHalfMovieBeans.get(tId);
                selectedMovieId = bean.getMovieId();
                if (!"".equals(bean.getTitle())) {
                    cinema_showtime_movietitle.setText(bean.getTitle());
                } else if (!"".equals(bean.getTitleEn())) {
                    cinema_showtime_movietitle.setText(bean.getTitleEn());
                }

                /**
                 * 处理得到当前影片下的所有日期的排片数据
                 */
                List<String> showDates = upHalfMovieBeans.get(tId).getShowDates();
                if (showDates == null || showDates.size() == 0) {
                    return;
                }
                final List<MovieTimeChildMainBean> showTimeList = new ArrayList<>();
                String showTimeKey;
                for (int i = 0; i < showDates.size(); i++) {
                    showTimeKey = new StringBuffer().append(selectedMovieId).append("_").append(showDates.get(i)).toString();
                    for (int j = 0; j < mUpHalfBean.getShowtimes().size(); j++) {
                        if (showTimeKey.equals(mUpHalfBean.getShowtimes().get(j).getMoviekey())) {
                            showTimeList.add(mUpHalfBean.getShowtimes().get(j));
                        }
                    }
                }
                StringBuilder sb = new StringBuilder();
                //todo canShowDlg
                if (showTimeList.size() > 0) {
                    childLoadingLayout.setVisibility(View.GONE);
                    //  此处切换影片，还要再筛选场次时间
                    final Date nowDate = MTimeUtils.getLastDiffServerDate();
                    final long nowTime = nowDate.getTime();
                    for (int i = showTimeList.size() - 1; i >= 0; i--) {
                        List<ShowtimeJsonBean> list = showTimeList.get(i).getList();
                        for (int j = list.size() - 1; j >= 0; j--) {
                            if (((list.get(j).getShowDay())) < nowTime) {
                                showTimeList.get(i).getList().remove(j);
                            }
                        }
                    }
                    pagerAdapter = new CinemaShowtimeViewPagerAdapter(mFragmentManager, showTimeList, mCinemaId, pager);
                    pager.setAdapter(pagerAdapter);
                    tabs.setShouldExpand(true);
                    tabs.setViewPager(pager);
//                    tabs.setTabPaddingLeftRight(20);
                    pagerAdapter.notifyDataSetChanged();

                    if (!TextUtils.isEmpty(mShowDateString) && !isNotFirstShowDateString) {
                        for (int j = 0; j < bean.getShowDates().size(); j++) {
                            if (mShowDateString.equals(bean.getShowDates().get(j))) {
                                pager.setCurrentItem(j);
                                break;
                            }
                        }
                    } else {
                        mShowDateString = bean.getShowDates().get(0);
                    }

                    isNotFirstShowDateString = true;
                    passDateString = mShowDateString;

                    int curPageIndex = 0;
                    for (int k = 0; k < showTimeList.size(); k++) {
                        String[] moviekey = showTimeList.get(k).getMoviekey().split("_");
                        if (moviekey.length == 2) {
                            if (moviekey[1].equals(mShowDateString)) {
                                curPageIndex = k;
                            }
                        }
                    }
                    initChildList(showTimeList.get(curPageIndex).getList());
                    tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                        @Override
                        public void onPageSelected(int position) {
                            cinema_showtime_child_layout.removeAllViews();
//                            childLoadingLayout.setVisibility(View.VISIBLE);
                            pager.setCurrentItem(position);
                            List<ShowtimeJsonBean> list = showTimeList.get(position).getList();
                            //这里在viewpager的tab切换时还是要重新整理数据，因为可能在切换已经有时间过期的了
                            final Date nowDate = MTimeUtils.getLastDiffServerDate();
                            final long nowTime = nowDate.getTime();
                            // 倒过来遍历list，正着遍历，就会出现错误
                            for (int i = list.size() - 1; i >= 0; i--) {
                                if (((list.get(i).getShowDay())) < nowTime) {
                                    list.remove(i);
                                }
                            }
                            passDateString = String.valueOf(bean.getShowDates().get(position));
                            StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_TIME, null, "selectDate", String.valueOf(position), null, null, null);
                            StatisticManager.getInstance().submit(bean);
                            initChildList(list);
                        }

                        @Override
                        public void onPageScrolled(int arg0, float arg1, int arg2) {

                        }

                        @Override
                        public void onPageScrollStateChanged(int arg0) {

                        }
                    });
                    final long nowTimes = MTimeUtils.getLastDiffServerTime();
                    final long time = DateUtil.getDateToLong(DateUtil.sdf1, bean.getShowDates().get(0));
                    int gapCount = DateUtil.getGapCount(MTimeUtils.getLastDiffServerDate(), DateUtil.getDateFromStr(bean.getShowDates().get(0)));
                    if (nowTimes > time) {
                        cinema_showtime_moviedate.setVisibility(View.GONE);
                    } else {
                        sb.append("-");
                        cinema_showtime_moviedate.setVisibility(View.VISIBLE);
                        if (gapCount == 1) {
                            cinema_showtime_moviedate.setText("明天上映");
                        } else if (gapCount == 2) {
                            cinema_showtime_moviedate.setText("后天上映");
                        } else if (gapCount >= 3) {
                            cinema_showtime_moviedate.setText(gapCount + "天后上映");
                        }

                    }
                } else {
                    childLoadingLayout.setVisibility(View.VISIBLE);
                }
                if (bean.getLength() != null && !"".equals(bean.getLength()) && !"0分钟".equals(bean.getLength())) {
                    sb.append(bean.getLength());
                    if (bean.getType() != null && !"".equals(bean.getType())) {
                        sb.append("-");
                    }
                }
                if (bean.getType() != null && !"".equals(bean.getType())) {
                    sb.append(bean.getType());
                }
                cinema_showtime_movielength.setText(sb);
            }
        }, 300);

        if (movieAdapter != null) {
            movieAdapter.notifyDataSetChanged();
//            fillData(mPosition);
        }
    }

    private void initChildList(List<ShowtimeJsonBean> beans) {
        // TODO: 16/8/12 知道这里为什么已经做了viewpager的fragment还要做这样的动态add的方式了。因为viewpager的高度计算与scrollview冲突，需要重新计算高度，每次切换viewpager都要重新计算。暂时先用这个，需要花费时间重构
        cinema_showtime_child_layout.removeAllViews();
        if (beans == null) {
            beans = new ArrayList<>();
        }
        if (beans.size() == 0) {
            childLoadingLayout.setVisibility(View.VISIBLE);
            return;
        } else {
            childLoadingLayout.setVisibility(View.GONE);
        }
        for (int j = 0; j < beans.size(); j++) {
            View convertView = getLayoutInflater().inflate(R.layout.cinema_showtime_listview_adapter_item, null);
            TextView childtextTime = convertView.findViewById(R.id.movie_child_tv_time);
            TextView childtextTomorrow = convertView.findViewById(R.id.movie_child_tv_tomorrow);
            TextView childtextInfo = convertView.findViewById(R.id.movie_child_tv_info);
            TextView childtextMonny = convertView.findViewById(R.id.movie_child_tv_monny);
            TextView childtextPlace = convertView.findViewById(R.id.movie_child_tv_place);
            TextView childbtnBuy = convertView.findViewById(R.id.movie_child_btn_buy);
            TextView tv_nextday = convertView.findViewById(R.id.movie_child_tv_nextday);
            TextView movie_child_tv_special = convertView.findViewById(R.id.movie_child_tv_special);//特效
            TextView movie_child_tv_hallsize = convertView.findViewById(R.id.movie_child_tv_hallsize);//场次大小
            ShowtimeJsonBean bean = beans.get(j);
            // TODO: 16/8/20 新加了接口返回按钮文案的逻辑。但是此版本接口可能不支持，所以先按以前的逻辑本地判断显示
            if (bean.isTicket()) {
                childbtnBuy.setVisibility(View.VISIBLE);
                if (bean.isVaildTicket()) {
                    //满座/购票。文案色值正常
                    childtextTime.setTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
                    childtextTomorrow.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
                    childtextInfo.setTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
                    childtextMonny.setTextColor(ContextCompat.getColor(mContext, R.color.orange_color));
                    childtextPlace.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
                    tv_nextday.setTextColor(ContextCompat.getColor(mContext, R.color.color_333333));
                    movie_child_tv_special.setTextColor(ContextCompat.getColor(mContext, R.color.color_6D8297));
                    movie_child_tv_hallsize.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
                    if ("售罄".equals(bean.getSeatSalesTip())) {//售罄/满座
                        childbtnBuy.setEnabled(false);
                        childbtnBuy.setText("满座");
                        childbtnBuy.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                        childbtnBuy.setBackgroundResource(R.drawable.bg_stroke_bbbbbb_frame);
                    } else {
                        childbtnBuy.setEnabled(true);
                        childbtnBuy.setText("购票");
                        childbtnBuy.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
                        childbtnBuy.setBackgroundResource(R.drawable.bg_stroke_f97d3f_frame);
                    }
                } else {
                    childbtnBuy.setEnabled(false);
                    childbtnBuy.setText("停售");
                    childbtnBuy.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    childbtnBuy.setBackgroundResource(R.drawable.bg_stroke_bbbbbb_frame);
                    //停售时，全部文案色值设置灰色
                    childtextTime.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    childtextTomorrow.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    childtextInfo.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    childtextMonny.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    childtextPlace.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    tv_nextday.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    movie_child_tv_special.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                    movie_child_tv_hallsize.setTextColor(ContextCompat.getColor(mContext, R.color.color_bbbbbb));
                }

                StringBuffer priceBuffer = new StringBuffer();
                if (bean.isCoupon()) {//特惠
                    priceBuffer.append("特惠");
                }
                priceBuffer.append(MtimeUtils.formatPrice(bean.getSalePrice())).append("元起");
                SpannableString styledText = new SpannableString(priceBuffer.toString());
                int styleIdSmall;
                int styleIdLarge;
                if (bean.isVaildTicket()) {
                    styleIdSmall = R.style.tvstyle_cinema_showtime_price_small;
                    styleIdLarge = R.style.tvstyle_cinema_showtime_price_large;
                } else {//停售
                    styleIdSmall = R.style.tvstyle_cinema_showtime_price_gray_small;
                    styleIdLarge = R.style.tvstyle_cinema_showtime_price_gray_large;
                }
                if (bean.isCoupon()) {
                    styledText.setSpan(new TextAppearanceSpan(mContext, styleIdSmall), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(mContext, styleIdLarge), 2, priceBuffer.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(mContext, styleIdSmall), priceBuffer.toString().length() - 2, priceBuffer.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    styledText.setSpan(new TextAppearanceSpan(mContext, styleIdLarge), 0, priceBuffer.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(mContext, styleIdSmall), priceBuffer.toString().length() - 2, priceBuffer.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                childtextMonny.setText(styledText, TextView.BufferType.SPANNABLE);
            } else {
                childbtnBuy.setVisibility(View.GONE);
                if (bean.getPrice().equals("0") || bean.getPrice().equals("")) {
                    childtextMonny.setVisibility(View.GONE);
                } else {
                    childtextMonny.setVisibility(View.VISIBLE);
                    StringBuffer priceBuffer = new StringBuffer();
                    priceBuffer.append(MtimeUtils.formatPrice(bean.getPrice())).append("元起");
                    SpannableString styledText = new SpannableString(priceBuffer.toString());
                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.tvstyle_cinema_showtime_price_gray_large), 0, priceBuffer.toString().length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.tvstyle_cinema_showtime_price_gray_small), priceBuffer.toString().length() - 2, priceBuffer.toString().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    childtextMonny.setText(styledText, TextView.BufferType.SPANNABLE);
                }
            }

            String date = TimeExt.INSTANCE.millis2String(bean.getShowDay(), DateUtil.sdf2);
            childtextTime.setText(date);
            final ShowtimeJsonBean sjBean = bean;
            final boolean isTomorrow = isTomorrow((bean.getShowDay()), passDateString);
            if (isTomorrow) {
                tv_nextday.setVisibility(View.VISIBLE);
                childtextTime.setTextSize(13);
            } else {
                tv_nextday.setVisibility(View.GONE);
                childtextTime.setTextSize(22);
            }
            if (isTomorrow && bean.isMovies()) {
                childtextTomorrow.setText("次日连映");
                childtextTomorrow.setVisibility(View.VISIBLE);
            } else if (bean.isMovies()) {
                childtextTomorrow.setText("连映");
                childtextTomorrow.setVisibility(View.VISIBLE);
            } else if (sjBean.getLength() > 0) {
                String date1 = TimeExt.INSTANCE.millis2String(bean.getShowDay() + sjBean.getLength() * 60 * 1000, DateUtil.sdf2);
                childtextTomorrow.setText(date1 + "散场");
                childtextTomorrow.setVisibility(View.VISIBLE);
            } else {
                childtextTomorrow.setVisibility(View.GONE);
            }
            final String hall = ConvertHelper.toString(sjBean.getHall());
            final String language = ConvertHelper.toString(sjBean.getLanguage());
            childtextInfo.setText(language.equals("") ? sjBean.getVersionDesc() : language + "  " + sjBean.getVersionDesc());
            childtextPlace.setText(hall);
            movie_child_tv_hallsize.setText(bean.getCapacity());

            Bundle bundle = new Bundle();
            bundle.putSerializable("bean", bean);
            bundle.putInt("position", j);
            childbtnBuy.setTag(bundle);

            childbtnBuy.setOnClickListener(this);
            if (!TextUtils.isEmpty(bean.getEffect())) {//是否显示特效
                movie_child_tv_special.setVisibility(View.VISIBLE);
                movie_child_tv_special.setText(bean.getEffect());
            } else {
                movie_child_tv_special.setVisibility(View.GONE);
            }
            cinema_showtime_child_layout.addView(convertView);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.movie_child_btn_buy:
                if (MtimeUtils.isDoubleClick(1000)) return;
                Bundle bundle1 = (Bundle) v.getTag();
                int pos = bundle1.getInt("position");
                final ShowtimeJsonBean mChildBean = (ShowtimeJsonBean) bundle1.getSerializable("bean");
//                    final ShowtimeJsonBean mChildBean = (ShowtimeJsonBean) v.getTag();
                if (mChildBean == null) {
                    return;
                }
                if (!mChildBean.isVaildTicket()) {
                    return;
                }
                final List<Provider> providers = mChildBean.getProviders();
                if ((providers == null) || (providers.size() <= 0)) {
                    return;
                }

                final Provider provider1 = providers.get(0);
                final String dId = String.valueOf(provider1.getdId()); // 取第一个provider的dId即可（服务端的说明）

                Map<String, String> businessParam = new HashMap<String, String>();
                businessParam.put("showTimeID", dId);
                StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_TIME, null, "playPlan", String.valueOf(pos), null, null, businessParam);
                StatisticManager.getInstance().submit(bean);
                CinemaShowtimeUPHalfCinemaBean cinema = mUpHalfBean.getCinema();
                if (provider1.getDirectSalesFlag() == CinemaShowtimeUPHalfCinemaBean.MTIME_SALES_FALG) {
//                    JumpUtil.startSeatSelectActivity(mContext, bean.toString(), dId, mCinemaId, String.valueOf(selectedMovieId), passDateString, "影院排片表");

                    JavaOpenSeatActivity.INSTANCE.openSeatActivity(dId, null, String.valueOf(selectedMovieId), mCinemaId, passDateString);
                } else {
                    if (!UserManager.Companion.getInstance().isLogin()) {
                        if (mListener != null) {
                            mListener.gotoLogin(mUpHalfBean.getCinema().getDirectSalesFlag());
                        }
                    } else if (!UserManager.Companion.getInstance().getHasBindMobile()) {
                        if (mListener != null) {
                            mListener.gotoBindPhone(mUpHalfBean.getCinema().getDirectSalesFlag());
                        }
                    } else {
                        //webview打开合作影院的H5选座页
                        if (mListener != null) {
                            mListener.gotoSeatSelect(cinema.getDsPlatformId(), cinema.getGovCinemaId(), cinema.getCinemaId(), provider1.getDsShowtimeId());
                        }
                    }
                }
                break;
        }
    }

    @SuppressLint("SimpleDateFormat")
    private boolean isTomorrow(final long longTime, String mDate) {
        Date date = new Date(0);
        try {
            if (mDate.contains("-")) {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(mDate);
            } else {
                date = new SimpleDateFormat("yyyyMMdd").parse(mDate);
            }
        } catch (final ParseException e) {
            e.printStackTrace();
        }
        return DateUtil.isTomorrow(new Date(longTime), date);
    }

    /**
     * 填充数据 正规周边
     */
//    private void fillData(int position) {
//        mGenuineSurroundingLayout.removeAllViews();
//        if (position > mGeniueSurroundingList.size() - 1) {
//            mGeniueSurroundLine.setVisibility(View.GONE);
//            mGenuineSurroundingRoot.setVisibility(View.GONE);
//            return;
//        }
//        final GeniueSurroundingBean.DetailRelatedsBean detailRelatedsBean = mGeniueSurroundingList.get(position);
//        if (null != detailRelatedsBean && detailRelatedsBean.getGoodsList().size() > 0) {
//            for (int i = 0; i < detailRelatedsBean.getGoodsList().size(); i++) {
//                final int ii = i + 1;
//                View goodsView = LayoutInflater.from(mContext).inflate(R.layout.item_genuine_surrounding, null);
//                if (i > 7) {
//
//                    return;
//                }
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.offset_10dp);
//                mGenuineSurroundingLayout.addView(goodsView, params);
//                final GoodsListBean singleBean = detailRelatedsBean.getGoodsList().get(i);
//                TextView goodNameTextView = goodsView.findViewById(R.id.item_genuine_surrounding_name_tv);
//                TextView priceTextView = goodsView.findViewById(R.id.item_genuine_surrounding_price_tv);
//                priceTextView.setText(String.valueOf(singleBean.getMinSalePriceFormat()));
//                ImageView imageView = goodsView.findViewById(R.id.item_genuine_surrounding_big_iv);
//                int w = mContext.getResources().getDimensionPixelSize(R.dimen.offset_210px);
//                volleyImageLoader.displayImage(singleBean.getImage(), imageView, R.drawable.default_image, R.drawable.default_image, w, w, null);
//                goodsView.setOnClickListener(v -> {
//                    HashMap<String, String> params1 = new HashMap<>();
//                    params1.put("movieID", String.valueOf(detailRelatedsBean.getMovieId()));
//                    params1.put("goodsID", String.valueOf(singleBean.getGoodsId()));
//                    StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_REC_ITEM, "", "itemList", String.valueOf(ii), "", "", params1);
//                    StatisticManager.getInstance().submit(bean);
////                    JumpUtil.startProductViewActivity(mContext, bean.toString(), singleBean.getGoodsUrl());
//                });
//            }
//            if (mGenuineSurroundingLayout.getChildCount() > 7) {
//                View goodsMoreView = LayoutInflater.from(mContext).inflate(R.layout.item_genuine_surrounding_more, null);
//                goodsMoreView.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        HashMap<String, String> params = new HashMap<>();
//                        params.put("movieID", String.valueOf(detailRelatedsBean.getMovieId()));
//                        StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_REC_ITEM, "", "viewMore", "", "", "", params);
//                        StatisticManager.getInstance().submit(bean);
////                        JumpUtil.startProductListActivity(mContext, bean.toString(), TextUtils.isEmpty(detailRelatedsBean.getRelatedUrl()) ? "" : detailRelatedsBean.getRelatedUrl(), null);
//                    }
//                });
//                int w = mContext.getResources().getDimensionPixelSize(R.dimen.offset_214px);
//                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(w, w);
//                param.leftMargin = mContext.getResources().getDimensionPixelSize(R.dimen.offset_10dp);
//                param.topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.offset_fu30dp);
//                mGenuineSurroundingLayout.addView(goodsMoreView, param);
//            }
//            mGeniueSurroundLine.setVisibility(View.VISIBLE);
//            mGenuineSurroundingRoot.setVisibility(View.VISIBLE);
//        } else {
//            mGeniueSurroundLine.setVisibility(View.GONE);
//            mGenuineSurroundingRoot.setVisibility(View.GONE);
//        }
//    }

    /**
     * 获取去全部的电影ID拼接一起","分隔
     *
     * @return
     */
    private String getMovieIds(List<CinemaShowtimeUPHalfMovieBean> upHalfMovieBeans) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < upHalfMovieBeans.size(); i++) {
            CinemaShowtimeUPHalfMovieBean bean = upHalfMovieBeans.get(i);
            if (null != bean) {
                if (bean.getMovieId() > 0) {
                    if (i < upHalfMovieBeans.size() - 1) {
                        sb.append(bean.getMovieId()).append(",");
                    } else {
                        sb.append(bean.getMovieId());
                    }
                }
            }
        }
        return sb.toString();
    }

    /**
     * 跳转到商品列表页
     */
//    public void jumpGoodsList(View view) {
//        HashMap<String, String> params = new HashMap<>();
//        params.put("movieID", String.valueOf(mGeniueSurroundingList.get(mPosition).getMovieId()));
//        StatisticPageBean bean = mActivity.assemble(StatisticTicket.TICKET_REC_ITEM, "", "viewMore", "", "", "", params);
//        StatisticManager.getInstance().submit(bean);
////        JumpUtil.startProductListActivity(mContext, null, TextUtils.isEmpty(mGeniueSurroundingList.get(mPosition).getRelatedUrl()) ? "" : mGeniueSurroundingList.get(mPosition).getRelatedUrl(), null);
//    }
    @Override
    public void onItemChanged(int position) {
        if (position < mUpHalfBean.getMovies().size() && !mUpHalfBean.getMovies().get(position).isBorder() && !isFrist) {
            mPosition = position - 2;
            mGeniueSurroundLine.setVisibility(View.GONE);
            mGenuineSurroundingRoot.setVisibility(View.GONE);
            updateMoviesSelectStatus(mUpHalfBean.getMovies(), position);
        } else {
            isFrist = false;
        }
    }

    public interface OnShowtimeListener {
        void setResultForActivity(int resultCode, Intent intent);

        void gotoLogin(int directSalesFlag);

        void gotoBindPhone(int directSalesFlag);

        void gotoCinemaDetail();

        void gotoSeatSelect(long dsPlatformId, String govCinemaId, int cinemaId, String dsShowtimeId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mTicketApi != null) {
            mTicketApi = null;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPosterFilter(PosterFilterEvent event) {
        if (null != movieAdapter) {
            movieAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 更新公告位
     */
    public void updateRcmdNotice(String appCopywriting, String h5Link) {
        if(mNoticeCard == null || mNoticeMarqueeTv == null) {
            return;
        }
        if (TextUtils.isEmpty(appCopywriting)) {
            mNoticeCard.setVisibility(View.GONE);
        } else {
            mNoticeCard.setVisibility(View.VISIBLE);
            showMarquee(mNoticeMarqueeTv, appCopywriting, h5Link);
        }
    }

    /**
     * 更新活动推荐位
     */
    public void updateRcmdActivity(String appCopywriting, String h5Link) {
        if(mActivityMarqueeTv == null || mActivityLineView == null) {
            return;
        }
        if (TextUtils.isEmpty(appCopywriting)) {
            mActivityMarqueeTv.setVisibility(View.GONE);
            mActivityLineView.setVisibility(View.GONE);
        } else {
            showMarquee(mActivityMarqueeTv, appCopywriting, h5Link);
            mActivityLineView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示跑马灯内容
     */
    private void showMarquee(MarqueeTextView marqueeTv, String text, String h5Url) {
        marqueeTv.setVisibility(View.VISIBLE);
        marqueeTv.setText(text);
        if(!TextUtils.isEmpty(h5Url)) {
            marqueeTv.setOnClickListener(v -> {
                // h5
                ProviderExtKt.getProvider(IJsSDKProvider.class).startH5Activity(
                        new BrowserEntity("", h5Url, false)
                );
            });
        }
    }

}
