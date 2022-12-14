//package com.mtime.bussiness.ticket.cinema.widget;
//
//import android.app.Dialog;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.util.ArrayMap;
//import android.text.TextUtils;
//import android.util.TypedValue;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.baidu.location.LocationClient;
//import com.mtime.frame.App;
//import com.mtime.frame.BaseActivity;
//import com.google.gson.reflect.TypeToken;
//import com.mtime.R;
//import com.kotlin.android.user.UserManager;
//import com.mtime.base.location.LocationInfo;
//import com.mtime.base.location.OnLocationCallback;
//import com.mtime.base.statistic.StatisticConstant;
//import com.mtime.base.statistic.bean.StatisticPageBean;
//import com.mtime.base.utils.MToastUtils;
//import com.mtime.beans.ADDetailBean;
//import com.mtime.beans.ADTotalBean;
//import com.mtime.bussiness.location.LocationHelper;
//import com.mtime.bussiness.ticket.TabPayTicketFragment;
//import com.mtime.bussiness.ticket.bean.BaseCityBean;
//import com.mtime.bussiness.ticket.cinema.activity.AddOftenGoCinemaActivity;
//import com.mtime.bussiness.ticket.cinema.bean.BusinessAreaBean;
//import com.mtime.bussiness.ticket.cinema.bean.BusinessBaseCinema;
//import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseBean;
//import com.mtime.bussiness.ticket.cinema.bean.CinemaBaseInfo;
//import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
//import com.mtime.bussiness.ticket.cinema.bean.CinemaOffenGoBean;
//import com.mtime.bussiness.ticket.cinema.bean.CinemaResultFeatureBean;
//import com.mtime.bussiness.ticket.cinema.bean.CouponActivityItem;
//import com.mtime.bussiness.ticket.cinema.bean.FavoriteCinemaListByCityIDBean;
//import com.mtime.bussiness.ticket.cinema.bean.SubwayCinemaBaseBean;
//import com.mtime.bussiness.ticket.cinema.bean.SyncFavoriteCinemaBean;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterView.FilterEventType;
//import com.mtime.bussiness.ticket.cinema.widget.CinemaFilterView.ICinemaFilterViewClickListener;
//import com.mtime.bussiness.ticket.widget.CouponLayout;
//import com.mtime.common.utils.LogWriter;
//import com.mtime.common.utils.Utils;
//import com.mtime.mtmovie.widgets.ADWebView;
//import com.mtime.network.ConstantUrl;
//import com.mtime.network.RequestCallback;
//import com.mtime.statistic.large.StatisticManager;
//import com.mtime.statistic.large.ticket.StatisticTicket;
//import com.mtime.util.HttpUtil;
//import com.mtime.util.JumpUtil;
//import com.mtime.util.MtimeUtils;
//import com.mtime.util.SaveOffenGo;
//import com.mtime.util.ToolsUtils;
//import com.mtime.util.UIUtil;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//@Deprecated
//@SuppressWarnings("rawtypes")
//public class TicketCinemasView implements OnClickListener, ICinemaFilterViewClickListener {
//
//    private static final int NEARBY_DISTANCE = 10000;
//    private static final int TYPE_ALL = 0;
//    private final View root;
//    private int showingType = TYPE_ALL;
//    private static final int TYPE_NEARBY = 1;
//    private static final int TYPE_PRICE = 2;
//    private static final int TYPE_FILTER = 3;
//    private static final int TYPE_COUPONS = 4;
//
//    private Dialog dialog;
//    private BaseActivity context;
//    private TextView all;
//    private TextView cinema_coupons_txt;
//    private View cinema_coupon_img;
//    private TextView nearby;
//    private TextView price;
//    private ImageView sortby;
//    private boolean sortAscending;
//    private ImageView filtricon;
//    private TextView filter;
//    private ListView list;
//    private ImageView location_failed;
//    private View location_holder;
//    private View location_running;
//    private TicketCinemaAdapter adapter;
//    private int setFavorite = 0;
//    private View scale_cover;
//    private CinemaFilterView viewFilter;
//    private FilterEventType filtertype = FilterEventType.TYPE_UNKNOWN;
//    private LocationInfo mLocationInfo;
//    private RequestCallback baseCityCallback;
//    private RequestCallback allCinemasCallback;
//    private RequestCallback favoritesCallback;
//    private BaseCityBean baseCityBean;
//    private List<CinemaBaseInfo> allCinemas;
//    private ArrayList<CinemaShowingInfo> baseCinemas = new ArrayList<CinemaShowingInfo>();
//    //showing data when type changed.??????"??????/??????"???"??????"???????????????????????????????????????????????????????????????
//    private ArrayList<CinemaShowingInfo> usedCinemas = new ArrayList<CinemaShowingInfo>();
//    //??????????????????????????????????????????????????????,???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????(?????????????????????10km?????????????????????)
//    private ArrayList<CinemaShowingInfo> showingCinemas = new ArrayList<CinemaShowingInfo>();
//    private ITicketCinemasViewListener listener;
//    private ADWebView ad1;
//    private boolean showAD;
//    private List<CinemaFeatureBean> cinemaFeatures = new ArrayList<CinemaFeatureBean>();
//    private TabPayTicketFragment fragment;
//
//    private LocationClient mLocClient;
//    private boolean isLocation; // ??????????????????
//    private boolean isSearching;
//    private View price_view;
//    private View cinema_coupons;
//
//    //???????????????????????????/??????/???????????????????????????????????????????????????
//    private boolean isNearby;
//    private boolean isPrice;
//    private boolean isFavor;
//
//    private View layout_failed_holder;
//    private ImageView imageview_failed;
//
//    private long mStartTime;
//    private String referBean;
//    private View header;
//
//    public void setStartTime(long mStartTime) {
//        this.mStartTime = mStartTime;
//    }
//
//    public void setReferBean(String referBean) {
//        this.referBean = referBean;
//    }
//
//
//    public TicketCinemasView(final BaseActivity context, TabPayTicketFragment fragment, final View root, final boolean showAD) {
//        this.context = context;
//        this.showAD = showAD;
//        this.fragment = fragment;
//        this.root = root;
//        this.init(context, root);
//        this.initAnimations();
//    }
//
//    public TicketCinemasView(final BaseActivity context, final View root, final boolean showAD) {
//        this.context = context;
//        this.showAD = showAD;
//        this.fragment = null;
//        this.root = root;
//        this.init(context, root);
//        this.initAnimations();
//    }
//
//    // TODO: 2017/10/12 vivian.wei ????????? 2017.10????????????
//    private void showGuiderView() {
//        if (ToolsUtils.getSpecialFilterGuider(context, "SpecialFilterGuider") && context.canShowDlg) {
//            View guideView = LayoutInflater.from(context).inflate(R.layout.special_cinemalist_filter_layout, null);
//            ImageView i_known_btn = (ImageView) guideView.findViewById(R.id.guide_iknow_btn);
//            i_known_btn.setOnClickListener(this);
//
//            dialog = new Dialog(context, R.style.TransparentFullScreen);
//            dialog.setContentView(guideView);
//            dialog.show();
//
//            ToolsUtils.saveSpecialFilterGuider(context, "SpecialFilterGuider", false);
//        }
//    }
//
//    public void onInitVariable() {
//        sortAscending = false;
//        // IMAX??? ???????????? 4K????????? 4D??? ?????????????????? ????????? ????????? Wifi
//        CinemaFeatureBean item = new CinemaFeatureBean();
//        item.setName("IMAX???");
//        item.setSupport(false);
//        cinemaFeatures.add(item);
//
//        CinemaFeatureBean item1 = new CinemaFeatureBean();
//        item1.setName("????????????");
//        item1.setSupport(false);
//        cinemaFeatures.add(item1);
//
//        CinemaFeatureBean item3 = new CinemaFeatureBean();
//        item3.setName("4K?????????");
//        item3.setSupport(false);
//        cinemaFeatures.add(item3);
//
//        CinemaFeatureBean item7 = new CinemaFeatureBean();
//        item7.setName("4D???");
//        item7.setSupport(false);
//        cinemaFeatures.add(item7);
//
//        CinemaFeatureBean item6 = new CinemaFeatureBean();
//        item6.setName("??????????????????");
//        item6.setSupport(false);
//        cinemaFeatures.add(item6);
//
//        CinemaFeatureBean item2 = new CinemaFeatureBean();
//        item2.setName("?????????");
//        item2.setSupport(false);
//        cinemaFeatures.add(item2);
//
//        CinemaFeatureBean item4 = new CinemaFeatureBean();
//        item4.setName("?????????");
//        item4.setSupport(false);
//        cinemaFeatures.add(item4);
//
//        CinemaFeatureBean item5 = new CinemaFeatureBean();
//        item5.setName("WIFI");
//        item5.setSupport(false);
//        cinemaFeatures.add(item5);
//
//    }
//
//    public void onInitEvent() {
//        baseCityCallback = new RequestCallback() {
//            @Override
//            public void onSuccess(final Object o) {
//                baseCityBean = (BaseCityBean) o;
//                final Type type = new TypeToken<List<CinemaBaseInfo>>() {
//                }.getType();
//                // OnlineLocationCinema/OnlineCinemasByCity.api?locationId={0}
//                Map<String, String> param = new HashMap<>(1);
//                param.put("locationId", mLocationInfo.getCityId());
//                HttpUtil.get(ConstantUrl.GET_CINEMANEWLIST_NORMAL, param, CinemaBaseInfo.class, allCinemasCallback, 0, type);
//
//                // TODO: 2017/10/11 vivian.wei ????????????????????????
//                //????????????
//                String currentVersion = Utils.getVersionName(context);
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                String lastVersion = prefs.getString(App.getInstance().VERSION_KEY, null);
//                if (lastVersion == null || Utils.compareVersion(currentVersion, lastVersion)) {
//                    showGuiderView();
//                    ToolsUtils.saveHomeGuider(context, false);
//                }
//                prefs.edit().putString(App.getInstance().VERSION_KEY, currentVersion).apply();
//                // todo end
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast("????????????????????????????????????:" + e.getLocalizedMessage());
//                UIUtil.showLoadingFailedLayout(layout_failed_holder, imageview_failed, new OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        onRequestData();
//                    }
//                });
//
//                // TODO: 2017/10/11 vivian.wei ????????????????????????
//                String currentVersion = Utils.getVersionName(context);
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
//                String lastVersion = prefs.getString(App.getInstance().VERSION_KEY, null);
//                if (lastVersion == null) {
//                    showGuiderView();
//                } else if (Utils.compareVersion(currentVersion, lastVersion)) {
//                    showGuiderView();
//                }
//                prefs.edit().putString(App.getInstance().VERSION_KEY, currentVersion).apply();
//                // todo end
//            }
//        };
//
//        allCinemasCallback = new RequestCallback() {
//
//            @Override
//            @SuppressWarnings("unchecked")
//            public void onSuccess(final Object o) {
//                //????????????
//                clearList();
//                setAllType();
//                root.setVisibility(View.VISIBLE);
//                header.setVisibility(View.VISIBLE);
//                allCinemas = (List<CinemaBaseInfo>) o;//??????????????????
//                if (UserManager.Companion.getInstance().isLogin()) {
//                    // ????????????????????????????????????
//                    // TODO: 2017/10/11 vivian.wei 10??????????????????????????????????????????????????????  https://ticket-api-m.mtime.cn/cinema/favoriteList.api
//                    Map<String, String> parameterList = new ArrayMap<String, String>(1);
//                    parameterList.put("cityID", mLocationInfo.getCityId());
//                    final Type typeToken = new TypeToken<List<FavoriteCinemaListByCityIDBean>>() {
//                    }.getType();
//                    HttpUtil.post(ConstantUrl.POST_FAVORITE_CINEMALIST_BYCITYID, parameterList, FavoriteCinemaListByCityIDBean.class, favoritesCallback, typeToken);
//                } else {
//                    //???????????????
//                    UIUtil.dismissLoadingDialog();
//
//                    usedCinemas.clear();
//                    baseCinemas.clear();
//
//                    // FavoriteCinemaListByCityIDBean
//                    List<CinemaBaseBean> cinemas = baseCityBean.getCinemas();
//
//                    // ?????????????????????????????????baseCityBean???????????????(??????id???????????????????????????????????????baseCityBean?????????????????????????????????????????????
//                    for (int j = 0; j < allCinemas.size(); j++) {
//                        for (int i = 0; i < cinemas.size(); i++) {
//                            if (null != allCinemas.get(j).getCouponActivityList() && allCinemas.get(j).getCouponActivityList().size() > 0) {
//                                cinema_coupons.setVisibility(View.VISIBLE);
//                                if (ToolsUtils.getSpecialFilterGuider(context, "cinema_coupont_clicked")) {
//                                    // ??????????????????
//                                    cinema_coupon_img.setVisibility(View.VISIBLE);
//                                }
//                                // ??????????????????
//                                price_view.setVisibility(View.GONE);
//                            }
//                            if (cinemas.get(i).getId() == allCinemas.get(j).getCinemaId()) {
//                                // ????????????????????? usedCinemas
//                                setCinemas(cinemas.get(i), allCinemas.get(j));
//                            }
//                        }
//                    }
//
//                    // ??????????????????
//                    viewFilter.setData(baseCityBean, cinemaFeatures);
//
//                    // TODO: 2017/10/11 vivian.wei 10???????????????????????????????????????
//                    List<CinemaOffenGoBean> localFavorites = SaveOffenGo.getInstance().getAll();
//                    mergeLocalFavorites(localFavorites, usedCinemas, true);
//                    // todo end
//
//                    baseCinemas.addAll(usedCinemas);
//
//                    // TODO: 2017/10/11 vivian.wei 10???????????????????????????????????????
//                    FavoriteSort sort22 = new FavoriteSort();
//                    Collections.sort(usedCinemas, sort22);
//                    // todo end
//
//                    reorderByNearest(usedCinemas);
//                    // ??????
//                    sortPartValuesWithDistance(usedCinemas);
//                    adapter.setDatas(usedCinemas);
//                }
//            }
//
//            @Override
//            public void onFail(final Exception e) {
//                UIUtil.dismissLoadingDialog();
//                adapter.clear();
//                root.setVisibility(View.GONE);
//                header.setVisibility(View.GONE);
//                MToastUtils.showShortToast("??????????????????");
//            }
//        };
//
//        favoritesCallback = new RequestCallback() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//
//                usedCinemas.clear();
//                baseCinemas.clear();
//
//                // FavoriteCinemaListByCityIDBean
//                List<FavoriteCinemaListByCityIDBean> favorites = (List<FavoriteCinemaListByCityIDBean>) o;
//                List<CinemaBaseBean> cinemas = baseCityBean.getCinemas();
//
//                for (int j = 0; j < allCinemas.size(); j++) {
//                    for (int i = 0; i < cinemas.size(); i++) {
//                        if (null != allCinemas.get(j).getCouponActivityList() && allCinemas.get(j).getCouponActivityList().size() > 0) {
//                            cinema_coupons.setVisibility(View.VISIBLE);
//                            if (ToolsUtils.getSpecialFilterGuider(context, "cinema_coupont_clicked")) {
//                                // ??????????????????
//                                cinema_coupon_img.setVisibility(View.VISIBLE);
//                            }
//                            // ??????????????????
//                            price_view.setVisibility(View.GONE);
//                        }
//                        if (cinemas.get(i).getId() == allCinemas.get(j).getCinemaId()) {
//                            // ????????????????????? usedCinemas
//                            setCinemas(cinemas.get(i), allCinemas.get(j));
//                        }
//                    }
//                }
//
//                // ??????????????????
//                viewFilter.setData(baseCityBean, cinemaFeatures);
//
//                List<CinemaOffenGoBean> localFavorites = SaveOffenGo.getInstance().getAll();
//                if (!UserManager.Companion.getInstance().isLogin()) {
//                    // TODO: 2017/10/11 vivian.wei 10???????????????????????????????????????
//                    // read the local favorites
//                    mergeLocalFavorites(localFavorites, usedCinemas, true);
//                    // todo end
//                } else {
//                    for (int i = 0; i < favorites.size(); i++) {
//                        // ??????????????????
//                        for (int j = 0; j < usedCinemas.size(); j++) {
//                            if (favorites.get(i).getId().equalsIgnoreCase(String.valueOf(usedCinemas.get(j).id))) {
//                                usedCinemas.get(j).favorited = true;
//                                usedCinemas.get(j).fid = favorites.get(i).getFid();
//                            }
//                        }
//                    }
//
//                }
//
//                baseCinemas.addAll(usedCinemas);
//                FavoriteSort sort22 = new FavoriteSort();
//                Collections.sort(usedCinemas, sort22);
//
//                reorderByNearest(usedCinemas);
//                // ??????
//                sortPartValuesWithDistance(usedCinemas);
//
//                adapter.setDatas(usedCinemas);
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast("??????????????????:" + e.getLocalizedMessage());
//            }
//        };
//    }
//
//    public void onRequestData() {
//        UIUtil.showLoadingDialog(context);
//
//        // TODO: 2017/10/11  vivian.wei 2017.10??????????????????/Showtime/BaseCityData.api ??????????????????https://ticket-api-m.mtime.cn/cinema/screening.api
//
//        // Showtime/BaseCityData.api?locationid={0}
//        Map<String, String> param = new HashMap<>(1);
//        param.put("locationid", mLocationInfo.getCityId());
//        HttpUtil.get(ConstantUrl.BASE_CITYDATA, param, BaseCityBean.class, baseCityCallback, 86400000, null, 300000);
//        requestAds();
//    }
//
//    public void setLocationInfo(LocationInfo info) {
//        this.mLocationInfo = info.clone();
//    }
//
//    public boolean needRequest(final LocationInfo info) {
//        if (null == mLocationInfo || !TextUtils.equals(mLocationInfo.getCityId(), info.getCityId())) {
//            this.mLocationInfo = info.clone();
//            return true;
//        }
//
//        return false;
//    }
//
//    // ??????"??????"tab
//    public void setAllType() {
//        LogWriter.d("checkList" + "setAlltype");
//        isSearching = false;
//
//        showingType = TYPE_ALL;
//        // reset adapter and update UI
//        hideAll(true);
//
//        all.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//        usedCinemas.clear();
//        // baseCinemas??????????????????????????????????????????????????????????????????
//        usedCinemas.addAll(baseCinemas);
//
//        // ?????????
//        FavoriteSort sort2 = new FavoriteSort();
//        Collections.sort(usedCinemas, sort2);
//
//        reorderByNearest(usedCinemas);
//        sortPartValuesWithDistance(usedCinemas);
//
//        adapter.setDatas(usedCinemas);
//    }
//
//    // ??????
//    public void search(final String content) {
//        if (null == baseCityBean) {
//            return;
//        }
//
//        if (TextUtils.isEmpty(content)) {
//            adapter.setDatas(usedCinemas);
//            return;
//        }
//
//        isSearching = true;
//        usedCinemas.clear();
//
//        // ????????????????????????????????????????????????,??????????????????
//        String value = content.toLowerCase();
//        // ????????????
//        for (int i = 0; i < baseCinemas.size(); i++) {
//            if (baseCinemas.get(i).name.toLowerCase().contains(value)) {
//                usedCinemas.add(baseCinemas.get(i));
//            }
//        }
//        // ??????
//        ArrayList<Integer> ids = new ArrayList<>();
//        // ???????????????????????????????????????ID
//        List<BusinessAreaBean> businessLists = baseCityBean.getBusinessAreas();
//        List<BusinessBaseCinema> businessCinemas = baseCityBean.getBusinessCinemas();
//        if (null != businessLists && businessLists.size() > 0 && null != businessCinemas && businessCinemas.size() > 0) {
//            for (int i = 0; i < businessLists.size(); i++) {
//                String name = businessLists.get(i).getName();
//                if (TextUtils.isEmpty(name) || !name.contains(value)) {
//                    continue;
//                }
//                // ???????????????id
//                for (int j = 0; j < businessCinemas.size(); j++) {
//                    if (businessLists.get(i).getId() == businessCinemas.get(j).getBusId()) {
//                        // ????????????????????????????????????????????????????????????
//                        ids.add(businessCinemas.get(j).getCId());
//                    }
//                }
//            }
//        }
//        // ???????????????????????????????????????????????????
//        for (int j = 0; j < ids.size(); j++) {
//            boolean find = false;
//            // ??????????????????????????????????????????????????????????????????????????????
//            for (int k = 0; k < usedCinemas.size(); k++) {
//                // ???????????????????????????
//                if (ids.get(j).intValue() == usedCinemas.get(k).id) {
//                    find = true;
//                    break;
//                }
//            }
//
//            // ????????????????????????????????????
//            if (find) {
//                continue;
//            }
//
//            // ???????????????????????????????????????
//            for (int i = 0; i < baseCinemas.size(); i++) {
//                // ????????????????????????????????????????????????,??????????????????
//                if (ids.get(j).intValue() == baseCinemas.get(i).id) {
//                    // ????????????????????????????????????
//                    usedCinemas.add(baseCinemas.get(i));
//                    break;
//                }
//            }
//        }
//
//        // ????????????
//        for (int i = 0; i < baseCinemas.size(); i++) {
//            // ????????????????????????????????????????????????,??????????????????
//            if (baseCinemas.get(i).address.toLowerCase().contains(value)) {
//                boolean find = false;
//                // ????????????????????????????????????????????????
//                for (int k = 0; k < usedCinemas.size(); k++) {
//                    if (baseCinemas.get(i).id == usedCinemas.get(k).id) {
//                        find = true;
//                        break;
//                    }
//                }
//
//                if (find) {
//                    continue;
//                }
//
//                usedCinemas.add(baseCinemas.get(i));
//            }
//        }
//
//        adapter.setDatas(usedCinemas);
//    }
//
//    public void setListener(ITicketCinemasViewListener listener) {
//        this.listener = listener;
//    }
//
//    @SuppressWarnings("unchecked")
//    public void requestFavtories() {
//        if (TYPE_ALL != showingType) {
//            return;
//        }
//
//        final List<CinemaOffenGoBean> ids = SaveOffenGo.getInstance().getAll();
//        if (!UserManager.Companion.getInstance().isLogin()) {
//
//            // TODO: 2017/10/12 vivian.wei 10?????????????????????????????????????????????
//
//            // ?????????
//            mergeLocalFavorites(ids, baseCinemas, true);
//            if (TYPE_ALL == showingType) {
//                usedCinemas.clear();
//                usedCinemas.addAll(baseCinemas);
//            } else {
//                mergeLocalFavorites(ids, usedCinemas, true);
//            }
//
//            FavoriteSort sort22 = new FavoriteSort();
//            Collections.sort(usedCinemas, sort22);
//            Collections.sort(baseCinemas, sort22);
//
//            showingCinemas.clear();
//            showingCinemas.addAll(usedCinemas);
//
//            if (TYPE_ALL == showingType) {
//                reorderByNearest(usedCinemas);
//                reorderByNearest(showingCinemas);
//                sortPartValuesWithDistance(usedCinemas);
//                sortPartValuesWithDistance(showingCinemas);
//            } else if (TYPE_COUPONS == showingType) {
//                calCoupon();
//            }
//
//            adapter.setDatas(showingCinemas);
//
//            return;
//        }
//
//        RequestCallback request = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                List<FavoriteCinemaListByCityIDBean> favorites = (List<FavoriteCinemaListByCityIDBean>) o;
//
//                /*
//                 * ??????????????????????????????????????????
//                 */
//                for (int j = 0; j < baseCinemas.size(); j++) {
//                    baseCinemas.get(j).favorited = false;
//                    baseCinemas.get(j).fid = null;
//                }
//
//                // ??????????????????????????????????????????
//                for (int i = 0; i < favorites.size(); i++) {
//                    LogWriter.d("checkList", "receive favorite name:" + favorites.get(i).getTitle());
//                    for (int j = 0; j < baseCinemas.size(); j++) {
//                        if (favorites.get(i).getId().equalsIgnoreCase(String.valueOf(baseCinemas.get(j).id))) {
//                            baseCinemas.get(j).favorited = true;
//                            baseCinemas.get(j).fid = favorites.get(i).getFid();
//                        }
//                    }
//                }
//                /*
//                 * reset the used cinemas's data
//                 */
//                if (TYPE_ALL == showingType) {
//                    usedCinemas.clear();
//                    usedCinemas.addAll(baseCinemas);
//                } else {
//                    // ??????usedCinemas??????????????????
//                    for (int j = 0; j < usedCinemas.size(); j++) {
//                        usedCinemas.get(j).favorited = false;
//                        usedCinemas.get(j).fid = null;
//                    }
//                    // ??????usedCinemas??????????????????
//                    for (int i = 0; i < favorites.size(); i++) {
//                        for (int j = 0; j < usedCinemas.size(); j++) {
//                            if (favorites.get(i).getId().equalsIgnoreCase(String.valueOf(usedCinemas.get(j).id))) {
//                                usedCinemas.get(j).favorited = true;
//                                usedCinemas.get(j).fid = favorites.get(i).getFid();
//                                break;
//                            }
//                        }
//                    }
//                }
//
//                FavoriteSort sort22 = new FavoriteSort();
//                Collections.sort(usedCinemas, sort22);
//                Collections.sort(baseCinemas, sort22);
//
//                showingCinemas.clear();
//                showingCinemas.addAll(usedCinemas);
//
//                if (TYPE_ALL == showingType) {
//                    reorderByNearest(usedCinemas);
//                    reorderByNearest(showingCinemas);
//                    sortPartValuesWithDistance(usedCinemas);
//                    sortPartValuesWithDistance(showingCinemas);
//                } else if (TYPE_COUPONS == showingType) {
//                    // ??????????????????????????????: showingCinemas
//                    calCoupon();
//                }
//
//                adapter.setDatas(showingCinemas);
//                UIUtil.dismissLoadingDialog();
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast("????????????????????????????????????:" + e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(context);
//
//        // TODO: 2017/10/11 vivian.wei 10??????????????????????????????????????????????????????  https://ticket-api-m.mtime.cn/cinema/favoriteList.api
//        Map<String, String> parameterList = new ArrayMap<String, String>(1);
//        parameterList.put("cityID", mLocationInfo.getCityId());
//        final Type typeToken = new TypeToken<List<FavoriteCinemaListByCityIDBean>>() {
//        }.getType();
//        HttpUtil.post(ConstantUrl.POST_FAVORITE_CINEMALIST_BYCITYID, parameterList, FavoriteCinemaListByCityIDBean.class, request, typeToken);
//    }
//
//    @SuppressWarnings({"unchecked"})
//    @Override
//    public void onClick(View arg0) {
//        FavoriteSort sort2 = new FavoriteSort();
//        switch (arg0.getId()) {
//            case R.id.all_view://??????
//                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_SORT, null, "all", null, null, null, null);
//                StatisticManager.getInstance().submit(bean);
//                isNearby = false;
//                isPrice = false;
//                isFavor = false;
//                isSearching = false;
//                if (TYPE_ALL == showingType) {
//                    break;
//                }
//
//                if (TYPE_NEARBY == showingType) {
//                    location_holder.setVisibility(View.GONE);
//                    list.setVisibility(View.VISIBLE);
//                }
//
//                showingType = TYPE_ALL;
//
//                // ??????tab??????????????????
//                hideAll(true);
//
//                all.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//                usedCinemas.clear();
//                usedCinemas.addAll(baseCinemas);
//
//                // ??????????????????
//                Collections.sort(usedCinemas, sort2);
//
//                reorderByNearest(usedCinemas);
//
//                sortPartValuesWithDistance(usedCinemas);
//
//                adapter.setDatas(usedCinemas);
//
//                break;
//            case R.id.nearby_view:
//                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                StatisticPageBean bean1 = context.assemble(StatisticTicket.TICKET_SORT, null, "near", null, null, null, null);
//                StatisticManager.getInstance().submit(bean1);
//                isNearby = true;
//                isPrice = false;
//                isFavor = false;
//                isSearching = false;
//                if (TYPE_NEARBY == showingType) {
//                    break;
//                }
//
//                location_holder.setVisibility(View.GONE);
//                showingType = TYPE_NEARBY;
//
//                hideAll(true);
//                // ????????????
//                nearby.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//                if (null != viewFilter) {
//                    viewFilter.restore();
//                }
//
//                showingCinemas.clear();
//                showingCinemas.addAll(baseCinemas);
//                // ??????????????????
//                calNearbyData();
//
//                adapter.setDatas(showingCinemas);
//                break;
//            case R.id.price_view://??????
//                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                StatisticPageBean bean2 = context.assemble(StatisticTicket.TICKET_SORT, null, "price", null, null, null, null);
//                StatisticManager.getInstance().submit(bean2);
//                isSearching = false;
//                if (TYPE_NEARBY == showingType) {
//                    location_holder.setVisibility(View.GONE);
//                    list.setVisibility(View.VISIBLE);
//                }
//
//                // ????????????
//                if (isPrice)
//                    sortAscending = !sortAscending;
//
//                isNearby = false;
//                isPrice = true;
//                isFavor = false;
//
//                // remove the unpriced cinemas
//                showingCinemas.clear();
//                showingCinemas.addAll(usedCinemas);
//                // ???????????????????????????
//                removeUnpriced();
//
//                showingType = TYPE_PRICE;
//
//                hideAll(false);
//
//                price.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//
//                Comparator sort;
//                if (sortAscending) {
//                    sort = new AscendingSort();
//                    sortby.setImageResource(R.drawable.price_sort_up);
//                } else {
//                    sort = new DescendingSort();
//                    sortby.setImageResource(R.drawable.price_sort_down);
//                }
//                Collections.sort(showingCinemas, sort);
//
//                adapter.setDatas(showingCinemas);
//                break;
//            case R.id.filter_view:
//                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                StatisticPageBean bean3 = context.assemble(StatisticTicket.TICKET_SORT, null, "screeningCinema", null, null, null, null);
//                StatisticManager.getInstance().submit(bean3);
//                if (TYPE_NEARBY == showingType) {
//                    location_holder.setVisibility(View.GONE);
//                    list.setVisibility(View.VISIBLE);
//                }
//
//                hideAll(false);
//                // run the scale animation with root
//                scale_cover.setVisibility(View.VISIBLE);
//
//                filtricon.setBackgroundResource(R.drawable.filter_on);
//                filter.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//                viewFilter.setVisibile();
//                showingType = TYPE_FILTER;
//                break;
//            case R.id.cinema_coupons: {
//                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                StatisticPageBean bean4 = context.assemble(StatisticTicket.TICKET_SORT, null, "cheap", null, null, null, null);
//                StatisticManager.getInstance().submit(bean4);
//                isNearby = false;
//                isPrice = false;
//                isFavor = true;
//                isSearching = false;
//
//                if (TYPE_COUPONS == showingType) {
//                    break;
//                }
//                if (TYPE_NEARBY == showingType) {
//                    location_holder.setVisibility(View.GONE);
//                    list.setVisibility(View.VISIBLE);
//                }
//
//                showingType = TYPE_COUPONS;
//                cinema_coupon_img.setVisibility(View.GONE);
//                ToolsUtils.saveSpecialFilterGuider(context, "cinema_coupont_clicked", false);
//
//                hideAll(true);
//                cinema_coupons_txt.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//
//                showingCinemas.clear();
//                showingCinemas.addAll(usedCinemas);
//                // ??????????????????????????????: showingCinemas
//                calCoupon();
//                adapter.setDatas(showingCinemas);
//            }
//            break;
//            case R.id.scale_cover:
//                //viewFilter.closeView();
//                break;
//            case R.id.guide_iknow_btn:
//                if (null != dialog) {
//                    dialog.dismiss();
//                    dialog = null;
//                }
//            default:
//                break;
//        }
//    }
//
//    // ??????????????????
//    @SuppressWarnings("unchecked")
//    @Override
//    public void onEvent(FilterEventType filtertype, int id, String content) {
//        // run the scale animation with root
//        scale_cover.setVisibility(View.INVISIBLE);
//
//        if (FilterEventType.TYPE_CLOSE == filtertype) {
//            return;
//        }
//        isSearching = false;
//        FavoriteSort sort22 = new FavoriteSort();
//
//        // ??????_??????
//        if (FilterEventType.TYPE_ALL == filtertype) {
//            isNearby = false;
//            isPrice = false;
//            isFavor = false;
//            // show all
//            this.filtertype = FilterEventType.TYPE_UNKNOWN;
//            showingType = TYPE_ALL;
//            // reset adapter and update UI
//            hideAll(true);
//            all.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//
//            usedCinemas.clear();
//            usedCinemas.addAll(baseCinemas);
//
//            Collections.sort(usedCinemas, sort22);
//            reorderByNearest(usedCinemas);
//            sortPartValuesWithDistance(usedCinemas);
//
//            adapter.setDatas(usedCinemas);
//
//            return;
//        }
//
//        // ???????????????????????????
//        this.filter.setText(content);
//        this.filtertype = filtertype;
//        switch (filtertype) {
//            case TYPE_FEATURE:
//                // ???baseCinemas?????????????????????????????????usedCinemas
//                calFeatureCinemas(id);
//                Collections.sort(usedCinemas, sort22);
//                break;
//            case TYPE_BUSINESS:
//                // ???????????????????????????????????????usedCinemas
//                calBusinessCinemas(id);
//                Collections.sort(usedCinemas, sort22);
//                break;
//            case TYPE_DISTRICT:
//                // ???????????????????????????????????????usedCinemas
//                calDistrictCinemas(id);
//                Collections.sort(usedCinemas, sort22);
//                break;
//            case TYPE_STATION:
//                // ??????????????????????????????????????????usedCinemas
//                calStationCinemas(id);
//                Comparator sort = new DistanceSort();
//                Collections.sort(usedCinemas, sort);
//                Collections.sort(usedCinemas, sort22);
//                break;
//            default:
//                break;
//        }
//
//        showingCinemas.clear();
//        showingCinemas.addAll(usedCinemas);
//
//        if (isPrice) { // ??????
//            // ???????????????????????????
//            removeUnpriced();
//
//            Comparator sort;
//            if (sortAscending) {
//                sort = new AscendingSort();
//                sortby.setImageResource(R.drawable.price_sort_up);
//            } else {
//                sort = new DescendingSort();
//                sortby.setImageResource(R.drawable.price_sort_down);
//            }
//            Collections.sort(showingCinemas, sort);
//        } else if (isFavor) { // ??????
//            // ??????????????????????????????: showingCinemas
//            calCoupon();
//        } else {
//            // ????????????????????????,??????????????????????????????
//            if (0 != mLocationInfo.getLatitude() && 0 != mLocationInfo.getLongitude()) {
//                DistanceSort sort2 = new DistanceSort();
//                Collections.sort(showingCinemas, sort2);
//            }
//        }
//
//        adapter.setDatas(showingCinemas);
//    }
//
//    private void init(final BaseActivity context, final View root) {
//        this.scale_cover = root.findViewById(R.id.scale_cover);
//        this.scale_cover.setOnClickListener(this);
//
//        this.layout_failed_holder = root.findViewById(R.id.loading_failed_layout);
//        this.imageview_failed = (ImageView) root.findViewById(R.id.load_failed);
//
//        // tabs.
//        View all_view = root.findViewById(R.id.all_view);
//        all_view.setOnClickListener(this);
//
//        View nearby_view = root.findViewById(R.id.nearby_view);
//        nearby_view.setOnClickListener(this);
//
//        price_view = root.findViewById(R.id.price_view);
//        price_view.setOnClickListener(this);
//
//        cinema_coupons = root.findViewById(R.id.cinema_coupons);
//        cinema_coupons.setOnClickListener(this);
//
//        View filter_view = root.findViewById(R.id.filter_view);
//        filter_view.setOnClickListener(this);
//
//        this.all = (TextView) root.findViewById(R.id.all);
//        this.nearby = (TextView) root.findViewById(R.id.nearby);
//        this.price = (TextView) root.findViewById(R.id.price);
//        this.sortby = (ImageView) root.findViewById(R.id.price_icon);
//        this.filtricon = (ImageView) root.findViewById(R.id.filter_icon);
//        this.filter = (TextView) root.findViewById(R.id.filter);
//        this.cinema_coupons_txt = (TextView) root.findViewById(R.id.cinema_coupons_txt);
//        this.cinema_coupon_img = root.findViewById(R.id.cinema_coupon_img);
//
//        // list header
//        header = context.getLayoutInflater().inflate(R.layout.cinema_list_header, null);
//        // favorite
//        this.ad1 = (ADWebView) header.findViewById(R.id.ad1);
//
////        this.favoriteicon = (ImageView) header.findViewById(R.id.favorite);
////        this.favorite = (TextView) header.findViewById(R.id.favorite_label);
////        this.favorite.setOnClickListener(this);
////        this.favoriteicon.setOnClickListener(this);
//
//        this.location_holder = root.findViewById(R.id.failed_holder);
//        this.location_failed = (ImageView) root.findViewById(R.id.location_failed);
//        this.location_running = root.findViewById(R.id.location_running);
//        this.location_holder.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // ??????,?????????
//                location_failed.setVisibility(View.INVISIBLE);
//                location_running.setVisibility(View.VISIBLE);
//                // ????????????
//                startLocation();
//            }
//        });
//        this.list = (ListView) root.findViewById(R.id.list);
//        this.list.addHeaderView(header);
//
//        adapter = new TicketCinemaAdapter(context, usedCinemas);
//        this.list.setAdapter(adapter);
//
//        this.list.setOnTouchListener(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View arg0, MotionEvent arg1) {
//                // ???????????????????????????
//                if (showAD) {
//                    if (fragment != null) {
//                        fragment.hideSoftkeyBoard();
//                    }
//                } else {
//                    // TODO: 2017/10/12 vivian.wei 10?????????????????????????????????
//                    if (context instanceof AddOftenGoCinemaActivity) {
//                        ((AddOftenGoCinemaActivity) context).hideSoftkeyBoard();
//                    }
//                }
//                return false;
//            }
//        });
//
//        this.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                if (0 == arg2) {
//                    return;
//                }
//
//                // ??????Id
//                int id = adapter.getid(arg2 - 1);
//                if (-1 == id) {
//                    return;
//                }
//
//                onListItemClick(id, arg2);
//            }
//        });
//        View filter = root.findViewById(R.id.cinema_filter_layout);
//        filter.setVisibility(View.INVISIBLE);
//        viewFilter = new CinemaFilterView(context, filter, this);
//
//        // default show all the cinemas.......
//        hideAll(true);
//        all.setTextColor(ContextCompat.getColor(context, R.color.color_0075c4));
//
//        isLocation = false;
//        isSearching = false;
//    }
//
//    // ??????????????????
//    private void onListItemClick(int id, int position) {
//        // ??????
//        context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//        StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//        if (!TextUtils.isEmpty(referBean)) {
//            bean1.refer = referBean;
//        }
//        StatisticManager.getInstance().submit(bean1);
//
//        context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//        Map<String, String> params = new HashMap<>();
//        params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//        StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//        if (!TextUtils.isEmpty(referBean)) {
//            bean3.refer = referBean;
//        }
//        StatisticManager.getInstance().submit(bean3);
//
//        context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//        Map<String, String> businessParam = new HashMap<String, String>();
//        businessParam.put(StatisticConstant.CINEMA_ID, String.valueOf(id));
//        StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, null, "cinema", String.valueOf(position - 1), null, null, businessParam);
//        if (!TextUtils.isEmpty(referBean)) {
//            bean.refer = referBean;
//        }
//        StatisticManager.getInstance().submit(bean);
//
//        JumpUtil.startCinemaShowtimeActivity(context, bean.toString(), String.valueOf(id), null, null, 0);
//    }
//
//    // ????????????????????? usedCinemas
//    private void setCinemas(CinemaBaseBean bean, CinemaBaseInfo info) {
//        CinemaShowingInfo item = new CinemaShowingInfo();
//        item.id = info.getCinemaId();
//        item.name = bean.getName();
//        item.address = bean.getAddress();
//        item.districtId = (int) bean.getDistrictId();
//        item.baidulatitude = info.getBaiduLatitude();
//        item.baidulongitude = info.getBaiduLongitude();
//        item.distance = MtimeUtils.gps2m(mLocationInfo.getLatitude(), mLocationInfo.getLongitude(), item.baidulatitude, item.baidulongitude);
//        item.favorited = false;
//
//        item.price = info.isTicket() ? info.getMinPrice() : 0;
//
//        CinemaResultFeatureBean feature = info.getFeature();
//        if (feature != null) {
//            item.has3D = 1 == feature.getHas3D() ? true : false;
//            item.hasIMax = 1 == feature.getHasIMAX() ? true : false;
//            item.hasMachined = 1 == feature.getHasServiceTicket() ? true : false;
//            item.hasParks = 1 == feature.getHasPark() ? true : false;
//            item.hasVIP = 1 == feature.getHasVIP() ? true : false;
//            item.hasWifi = 1 == feature.getHasWifi() ? true : false;
//            item.hasFeature4K = 1 == feature.getHasFeature4K() ? true : false;
//            item.hasLoveseat = 1 == feature.getHasLoveseat() ? true : false;
//            item.hasFeature4D = 1 == feature.getHasFeature4D() ? true : false;
//            item.hasFeatureDolby = 1 == feature.getHasFeatureDolby() ? true : false;
//            item.hasFeatureHuge = 1 == feature.getHasFeatureHuge() ? true : false;
//        }
//
//        // ??????_??????
//        // order:0:"IMAX", 1:"????????????", 2:"4K?????????", 3:"4D???", 4:"??????????????????", 5:????????? 6: ????????? 7:Wifi
//        if (item.hasIMax) {
//            cinemaFeatures.get(0).setSupport(true);
//        }
//        if (item.hasFeatureHuge) {
//            cinemaFeatures.get(1).setSupport(true);
//        }
//        if (item.hasFeature4K) {
//            cinemaFeatures.get(2).setSupport(true);
//        }
//        if (item.hasFeature4D) {
//            cinemaFeatures.get(3).setSupport(true);
//        }
//        if (item.hasFeatureDolby) {
//            cinemaFeatures.get(4).setSupport(true);
//        }
//        if (item.hasLoveseat) {
//            cinemaFeatures.get(5).setSupport(true);
//        }
//        if (item.hasParks) {
//            cinemaFeatures.get(6).setSupport(true);
//        }
//        if (item.hasWifi) {
//            cinemaFeatures.get(7).setSupport(true);
//        }
//
//        List<CouponActivityItem> couponActivityList = info.getCouponActivityList();
//        if (couponActivityList != null && couponActivityList.size() > 0) {
//            //????????????????????????usedCinemas???
//
//            for (int i = 0; i < couponActivityList.size(); i++) {
//
//                try {
//                    item.activityList.add(couponActivityList.get(i));
//                    // TODO: 2017/10/11 vivian.wei ????????????????????????????????????
//                    item.activityList.get(i).setId(couponActivityList.get(i).getId());
//                    item.activityList.get(i).setIsSelected(couponActivityList.get(i).getIsSelected());
//                    item.activityList.get(i).setTag(couponActivityList.get(i).getTag());
//                    item.activityList.get(i).setDesc(couponActivityList.get(i).getDesc());
//                    // todo end
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        usedCinemas.add(item);
//    }
//
//    // ????????????
//    private String getDistance(double distance) {
//        String value;
//
//        if (distance < 1) {
//            value = "";
//        } else if (distance < 500) {
//            value = "<500m";
//        } else if (distance < 1000) {
//            value = String.format("%dm", (int) distance);
//        } else if (distance <= 20000) {
//            value = String.format("%.1fkm", (float) (distance / 1000));
//        } else {
//            value = ">20km";
//        }
//
//        return value;
//    }
//
//    // ??????tab??????????????????
//    private void hideAll(boolean changeLabel) {
//        all.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
//        nearby.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
//        cinema_coupons_txt.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
//        price.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
//        filtricon.setBackgroundResource(R.drawable.filter);
//        filter.setTextColor(ContextCompat.getColor(context, R.color.color_333333));
//        if (changeLabel) {
//            filter.setText(R.string.ticket_cinema_filter_label);
//        }
//    }
//
//    // ??????????????????
//    private void calNearbyData() {
//        // ??????10km???????????????
//        for (Iterator it = showingCinemas.iterator(); it.hasNext(); ) {
//            CinemaShowingInfo info = (CinemaShowingInfo) it.next();
//            final double s = MtimeUtils.gps2m(mLocationInfo.getLatitude(), mLocationInfo.getLongitude(), info.baidulatitude,
//                    info.baidulongitude);
//            if (s > NEARBY_DISTANCE) {
//                it.remove();
//            }
//        }
//
//        if (null == mLocationInfo || mLocationInfo.isLose()) {
//            MToastUtils.showShortToast("????????????");
//            list.setVisibility(View.GONE);
//            location_holder.setVisibility(View.VISIBLE);
//            if (isLocation) {
//                location_failed.setVisibility(View.INVISIBLE);
//                location_running.setVisibility(View.VISIBLE);
//            } else {
//                UIUtil.loadLocationFailedPicture(location_failed);
//                location_running.setVisibility(View.INVISIBLE);
//            }
//        }
//
//        if (showingCinemas.size() == 0) {
//            MToastUtils.showShortToast("?????????10??????????????????????????????");
//        }
//
//        // ???????????????
//        Comparator sort1 = new DistanceSort();
//        Collections.sort(showingCinemas, sort1);
//    }
//
//    // ???????????????????????????????????????usedCinemas
//    private void calBusinessCinemas(int businessId) {
//        this.usedCinemas.clear();
//
//        // get the cinemas with the business id
//        List<BusinessBaseCinema> cinemas = this.baseCityBean.getBusinessCinemas();
//        for (int i = 0; i < cinemas.size(); i++) {
//            if (businessId != cinemas.get(i).getBusId()) {
//                continue;
//            }
//
//            for (int j = 0; j < this.baseCinemas.size(); j++) {
//                if (cinemas.get(i).getCId() == this.baseCinemas.get(j).id) {
//                    this.usedCinemas.add(this.baseCinemas.get(j));
//                }
//            }
//        }
//    }
//
//    // ???????????????????????????????????????usedCinemas
//    private void calDistrictCinemas(int districtId) {
//        this.usedCinemas.clear();
//
//        for (int i = 0; i < this.baseCinemas.size(); i++) {
//            if (districtId == this.baseCinemas.get(i).districtId) {
//                this.usedCinemas.add(this.baseCinemas.get(i));
//            }
//        }
//    }
//
//    // ??????????????????????????????????????????usedCinemas
//    private void calStationCinemas(int stationId) {
//        this.usedCinemas.clear();
//
//        List<SubwayCinemaBaseBean> subwayCinemas = this.baseCityBean.getSubwayCinemas();
//        for (int i = 0; i < subwayCinemas.size(); i++) {
//            if (stationId != subwayCinemas.get(i).getStationId()) {
//                continue;
//            }
//
//            for (int j = 0; j < this.baseCinemas.size(); j++) {
//                if (subwayCinemas.get(i).getCId() == this.baseCinemas.get(j).id) {
//                    CinemaShowingInfo item = new CinemaShowingInfo(this.baseCinemas.get(j));
//
//                    this.usedCinemas.add(item);
//                    this.usedCinemas.get(this.usedCinemas.size() - 1).distance = Double.valueOf(subwayCinemas.get(i).getDistance());
//                }
//            }
//        }
//    }
//
//    // ??????????????????????????????: showingCinemas
//    private void calCoupon() {
//        if (showingCinemas.size() < 1) {
//            return;
//        }
//
//        // TODO ?????????????????????????????? ???????????????????????????????????????????????? ??????????????????(?????????)???????????????????????????????????????
//        // TODO ???????????????????????? ????????????(?????????)?????????????????????????????????
//        ArrayList<CinemaShowingInfo> couponPriceCinemas = new ArrayList<CinemaShowingInfo>(showingCinemas.size());
//        ArrayList<CinemaShowingInfo> couponunpriceCinemas = new ArrayList<CinemaShowingInfo>(showingCinemas.size());
//        ArrayList<CinemaShowingInfo> notCouponPriceCinemas = new ArrayList<CinemaShowingInfo>(showingCinemas.size());
//        ArrayList<CinemaShowingInfo> notCouponunpriceCinemas = new ArrayList<CinemaShowingInfo>(showingCinemas.size());
//        for (int i = 0; i < showingCinemas.size(); i++) {
//            if (showingCinemas.get(i).activityList.size() > 0) {
//                if (showingCinemas.get(i).price > 0) {
//                    couponPriceCinemas.add(showingCinemas.get(i));
//                } else {
//                    couponunpriceCinemas.add(showingCinemas.get(i));
//                }
//            } else {
//                if (showingCinemas.get(i).price > 0) {
//                    notCouponPriceCinemas.add(showingCinemas.get(i));
//                } else {
//                    notCouponunpriceCinemas.add(showingCinemas.get(i));
//                }
//            }
//        }
//
//        // ????????????????????????
//        Comparator sortprice = new DescendingSort();
//        Collections.sort(notCouponPriceCinemas, sortprice);
//
//        if (null == mLocationInfo || mLocationInfo.isLose()) {
//            // ?????????????????????
//            Collections.sort(couponPriceCinemas, sortprice);
//        } else {
//            // ???????????????
//            Comparator sortdistance = new DistanceSort();
//            Collections.sort(couponPriceCinemas, sortdistance);
//        }
//
//        showingCinemas.clear();
//        showingCinemas.addAll(couponPriceCinemas);
//        showingCinemas.addAll(couponunpriceCinemas);
//        showingCinemas.addAll(notCouponPriceCinemas);
//        showingCinemas.addAll(notCouponunpriceCinemas);
//    }
//
//    private void initAnimations() {
//        new Thread() {
//
//            @Override
//            public void run() {
//                // alphaToHide = AnimationUtils.loadAnimation(context,
//                // R.anim.cinema_favorite_alpha_to_hide);
//                // alphaToShow = AnimationUtils.loadAnimation(context,
//                // R.anim.cinema_favorite_alpha_to_show);
//                //
//                // translateToLeft = new TranslateAnimation(34, 0, 0, 0);
//                // translateToRight = new TranslateAnimation(0, 34, 0, 0);
//                // translateToRight.setDuration(300);
//                // translateToRight.setFillAfter(true);
//                // translateToLeft.setDuration(300);
//                // translateToLeft.setFillAfter(true);
//
//                super.run();
//            }
//
//        }.start();
//    }
//
//    // TODO: 2017/10/12 vivian.wei ?????????
//    private int changedFavorites() {
//        int count = 0;
//        for (int i = 0; i < usedCinemas.size(); i++) {
//            if (!usedCinemas.get(i).changed) {
//                continue;
//            }
//
//            count++;
//        }
//
//        return count;
//    }
//
//    // TODO: 2017/10/12 vivian.wei ?????????
//    private void sendFavorites() {
//        StringBuffer addIds = new StringBuffer();
//        StringBuffer cancelIds = new StringBuffer();
//
//        for (int i = 0; i < usedCinemas.size(); i++) {
//            if (!usedCinemas.get(i).changed) {
//                continue;
//            }
//
//            if (usedCinemas.get(i).favorited) {
//                addIds.append(usedCinemas.get(i).id);
//                addIds.append(",");
//            } else {
//                cancelIds.append(usedCinemas.get(i).fid);
//                cancelIds.append(",");
//            }
//        }
//
//        if (addIds.length() < 1 && cancelIds.length() < 1) {
//            return;
//        }
//
//        String valueAdd = addIds.toString();
//        if (valueAdd.length() > 0) {
//            valueAdd = TextUtils.substring(valueAdd, 0, valueAdd.length() - 1);
//        }
//
//        String valueCancele = cancelIds.toString();
//        if (valueCancele.length() > 0) {
//            valueCancele = TextUtils.substring(valueCancele, 0, valueCancele.length() - 1);
//        }
//
//        RequestCallback syncFavoriteCinemaCallback = new RequestCallback() {
//
//            @Override
//            public void onSuccess(Object o) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast("????????????????????????");
//            }
//
//            @Override
//            public void onFail(Exception e) {
//                UIUtil.dismissLoadingDialog();
//                MToastUtils.showShortToast("??????/?????????????????? " + e.getLocalizedMessage());
//            }
//        };
//
//        UIUtil.showLoadingDialog(context);
//
//        Map<String, String> parameterList = new ArrayMap<String, String>(2);
//        parameterList.put("addIds", valueAdd);
//        parameterList.put("deleteFIds", valueCancele);
//        HttpUtil.post(ConstantUrl.SYNC_FAVORITE_CINEMA, parameterList, SyncFavoriteCinemaBean.class, syncFavoriteCinemaCallback);
//
//    }
//
//    // ??????????????????
//    private void mergeLocalFavorites(List<CinemaOffenGoBean> ids, ArrayList<CinemaShowingInfo> cinemas, boolean reset) {
//        if (null == ids || ids.size() < 1 || null == cinemas || cinemas.size() < 1) {
//            return;
//        }
//
//        for (int i = 0; i < cinemas.size(); i++) {
//            if (reset) {
//                cinemas.get(i).favorited = false;
//            }
//            for (int j = 0; j < ids.size(); j++) {
//                if (cinemas.get(i).id == ids.get(j).getId()) {
//                    cinemas.get(i).favorited = true;
//                    LogWriter.d("checkList", "merge facorite:" + cinemas.get(i).name);
//                }
//            }
//        }
//    }
//
//    // TODO: 2017/10/12 vivian.wei ?????????
//    private void saveOffenGoCinemas() {
//
//        for (int i = 0; i < usedCinemas.size(); i++) {
//            if (!usedCinemas.get(i).changed) {
//                continue;
//            }
//
//            if (usedCinemas.get(i).favorited) {
//                final CinemaOffenGoBean offengoBean = new CinemaOffenGoBean();
//                offengoBean.setAdress(usedCinemas.get(i).address);
//                offengoBean.setName(usedCinemas.get(i).name);
//                offengoBean.setId(usedCinemas.get(i).id);
//                SaveOffenGo.getInstance().add(offengoBean);
//            } else {
//                SaveOffenGo.getInstance().remove(String.valueOf(usedCinemas.get(i).id));
//            }
//        }
//    }
//
//    // ???????????????????????????
//    private void removeUnpriced() {
//        for (int i = showingCinemas.size() - 1; i >= 0; i--) {
//            if (0 == showingCinemas.get(i).price) {
//                showingCinemas.remove(i);
//            }
//        }
//    }
//
//    // ????????????
//    private void requestAds() {
//        if (!showAD) {
//            return;
//        }
//        // ad1
//        // Advertisement/MobileAdvertisementInfo.api?locationId={0}
//        Map<String, String> param = new HashMap<>(1);
//        param.put("locationId", mLocationInfo.getCityId());
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
//
//                ADDetailBean item = ToolsUtils.getADBean(bean, App.getInstance().AD_CINEMA_LIST);
//                if (!ADWebView.show(item)) {
//                    ad1.setVisibility(View.GONE);
//                    return;
//                }
//
//                ad1.setVisibility(View.VISIBLE);
//
//                ad1.setOnAdItemClickListenner(new ADWebView.OnAdItemClickListenner() {
//                    @Override
//                    public void onAdItemClick(ADDetailBean item, String url) {
//                        context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                        StatisticPageBean bean1 = context.assemble(StatisticTicket.TICKET_AD, null, null, null, null, null, null);
//                        StatisticManager.getInstance().submit(bean1);
//                        ad1.setAdReferer(bean1.toString());
//                    }
//                });
//                ad1.load(context, item);
//
//            }
//        }, 30 * 60 * 1000, null, 2000);
//    }
//
//    // TODO: 2017/10/11 vivian.wei 10???????????????????????????
//    // ???????????????????????????
//    private void reorderByNearest(List<CinemaShowingInfo> cinemas) {
//        if (null == cinemas || cinemas.size() < 1) {
//            return;
//        }
//
//        int index = 0;
//        int offset = 100000;
//        for (int i = 0; i < cinemas.size(); i++) {
//            if (offset > (int) cinemas.get(i).distance) {
//                offset = (int) cinemas.get(i).distance;
//                index = i;
//            } else if (offset == (int) cinemas.get(i).distance && cinemas.get(i).favorited) {
//                index = i;
//            }
//        }
//
//        // move the nearest item to zero
//        // ????????????7,?????????????????????
//        if (index >= cinemas.size()) {
//            return;
//        }
//
//        if (TYPE_ALL == showingType && cinemas.size() > 7) {
//            CinemaShowingInfo item = cinemas.get(index);
//            if (!item.favorited) {
//                item = cinemas.remove(index);
//            }
//            cinemas.add(0, item);
//        } else {
//            CinemaShowingInfo item = cinemas.remove(index);
//            cinemas.add(0, item);
//        }
//
//    }
//
//    // ???baseCinemas?????????????????????????????????usedCinemas
//    private void calFeatureCinemas(final int index) {
//        // order:0:"IMAX", 1:"????????????", 2:"4K?????????", 3:"4D???", 4:"??????????????????", 5:????????? 6: ????????? 7:Wifi
//        this.usedCinemas.clear();
//
//        // get the cinemas with the feature index
//        for (int j = 0; j < this.baseCinemas.size(); j++) {
//            boolean find = false;
//            switch (index) {
//                case 0:
//                    if (this.baseCinemas.get(j).hasIMax) {
//                        find = true;
//                    }
//                    break;
//                case 1:
//                    if (this.baseCinemas.get(j).hasFeatureHuge) {
//                        find = true;
//                    }
//                    break;
//                case 2:
//                    if (this.baseCinemas.get(j).hasFeature4K) {
//                        find = true;
//                    }
//                    break;
//                case 3:
//                    if (this.baseCinemas.get(j).hasFeature4D) {
//                        find = true;
//                    }
//                    break;
//                case 4:
//                    if (this.baseCinemas.get(j).hasFeatureDolby) {
//                        find = true;
//                    }
//                    break;
//                case 5:
//                    if (this.baseCinemas.get(j).hasLoveseat) {
//                        find = true;
//                    }
//                    break;
//                case 6:
//                    if (this.baseCinemas.get(j).hasParks) {
//                        find = true;
//                    }
//                    break;
//                case 7:
//                    if (this.baseCinemas.get(j).hasWifi) {
//                        find = true;
//                    }
//                    break;
//                default:
//                    break;
//            }
//            if (find) {
//                this.usedCinemas.add(this.baseCinemas.get(j));
//            }
//        }
//    }
//
//    // ????????????
//    private void startLocation() {
//        if (isLocation) {
//            return;
//        }
//        isLocation = true;
//
//        LocationHelper.location(context.getApplicationContext(), new OnLocationCallback() {
//            @Override
//            public void onLocationSuccess(LocationInfo locationInfo) {
//                if (null != locationInfo) {
//                    mLocationInfo = locationInfo.clone();
//                    isLocation = false;
//                    updateDataAfterLocation();
//                }
//            }
//        });
//    }
//
//    // ???????????????????????????
//    private void updateDataAfterLocation() {
//        // ???????????????????????????????????????
//        synchronized (usedCinemas) {
//            for (CinemaShowingInfo item : usedCinemas) {
//                item.distance = MtimeUtils.gps2m(mLocationInfo.getLatitude(), mLocationInfo.getLongitude(), item.baidulatitude, item.baidulongitude);
//            }
//        }
//
//        // ??????????????????????????????????????????????????????????????????????????????????????????
//
//        if (null != list) {
//            list.post(new Runnable() {
//                @Override
//                public void run() {
//                    // ???????????????, ??????????????????tab????????????????????????????????????
//                    UIUtil.loadLocationFailedPicture(location_failed);
//                    location_running.setVisibility(View.INVISIBLE);
//                    location_holder.setVisibility(View.GONE);
//                    list.setVisibility(View.VISIBLE);
//                    // ????????????
//                    if (TYPE_ALL == showingType) {
//                        // ???????????????????????????????????????
//                        usedCinemas.clear();
//                        usedCinemas.addAll(baseCinemas);
//
//                        FavoriteSort sort2 = new FavoriteSort();
//                        Collections.sort(usedCinemas, sort2);
//
//                        reorderByNearest(usedCinemas);
//                        sortPartValuesWithDistance(usedCinemas);
//
//                        adapter.setDatas(usedCinemas);
//                    } else if (TYPE_NEARBY == showingType) {
//                        showingCinemas.clear();
//                        showingCinemas.addAll(baseCinemas);
//                        calNearbyData();
//
//                        adapter.setDatas(showingCinemas);
//                    }
//                }
//            });
//        }
//    }
//
//    // TODO: 2017/10/11 vivian.wei 10?????????????????????
//    // ???????????????????????????????????????????????????????????????
//    private void sortPartValuesWithDistance(List<CinemaShowingInfo> cinemas) {
//        if (null == cinemas || cinemas.size() < 2 || null == mLocationInfo || mLocationInfo.isLose()) {
//            return;
//        }
//
//        CinemaShowingInfo first = cinemas.get(0);
//        List<CinemaShowingInfo> favorited = new ArrayList<CinemaShowingInfo>();
//        List<CinemaShowingInfo> left = new ArrayList<CinemaShowingInfo>();
//        // ??????????????????????????????????????????
//        for (int i = 1; i < cinemas.size(); i++) {
//            if (cinemas.get(i).favorited) {
//                favorited.add(cinemas.get(i));
//            } else {
//                left.add(cinemas.get(i));
//            }
//        }
//
//        // ??????????????????????????????
//        cinemas.clear();
//
//        Comparator sort1 = new DistanceSort();
//        Collections.sort(left, sort1);
//
//        cinemas.add(first);
//        cinemas.addAll(favorited);
//        cinemas.addAll(left);
//    }
//
//    private void clearList() {
//        // ??????????????????????????????????????????
//        hideAll(true);
//
//        if (null != allCinemas) {
//            allCinemas.clear();
//        }
//
//        baseCinemas.clear();
//        usedCinemas.clear();
//        showingCinemas.clear();
//        adapter.setDatas(usedCinemas);
//        adapter.notifyDataSetChanged();
//    }
//
//    // TODO: 2017/10/11 vivian.wei 10??????????????????
//    public interface ITicketCinemasViewListener {
//        public void onEvent();
//    }
//
//    // ???????????????
//    private class AscendingSort implements Comparator {
//
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            CinemaShowingInfo info1 = (CinemaShowingInfo) arg0;
//            CinemaShowingInfo info2 = (CinemaShowingInfo) arg1;
//            if (info1.price == info2.price) {
//                return 0;
//            } else if (info1.price > info2.price) {
//                return -1;
//            } else {
//                return 1;
//            }
//        }
//
//    }
//
//    // ???????????????
//    private class DescendingSort implements Comparator {
//
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            CinemaShowingInfo info1 = (CinemaShowingInfo) arg0;
//            CinemaShowingInfo info2 = (CinemaShowingInfo) arg1;
//
//            if (info1.price == info2.price) {
//                return 0;
//            } else if (info1.price > info2.price) {
//                return 1;
//            } else {
//                return -1;
//            }
//
//        }
//    }
//
//    // ???????????????
//    private class DistanceSort implements Comparator {
//
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            CinemaShowingInfo info1 = (CinemaShowingInfo) arg0;
//            CinemaShowingInfo info2 = (CinemaShowingInfo) arg1;
//
//            if (info1.distance == info2.distance) {
//                return 0;
//            } else if (info1.distance > info2.distance) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//    }
//
//    // ???????????????
//    private class FavoriteSort implements Comparator {
//
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            CinemaShowingInfo info1 = (CinemaShowingInfo) arg0;
//            CinemaShowingInfo info2 = (CinemaShowingInfo) arg1;
//
//            int value1 = info1.favorited ? 1 : 0;
//            int value2 = info2.favorited ? 1 : 0;
//            if (value1 > value2) {
//                return -1;
//            } else if (value1 == value2) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//    }
//
//    // TODO: 2017/10/12 vivian.wei ?????????
//    // ????????????????????????
//    private class CouponSort implements Comparator {
//
//        @Override
//        public int compare(Object arg0, Object arg1) {
//            CinemaShowingInfo info1 = (CinemaShowingInfo) arg0;
//            CinemaShowingInfo info2 = (CinemaShowingInfo) arg1;
//
//            int value1 = info1.activityList.size() > 0 ? 1 : 0;
//            int value2 = info2.activityList.size() > 0 ? 1 : 0;
//            if (value1 > value2) {
//                return -1;
//            } else if (value1 == value2) {
//                return 0;
//            } else {
//                return 1;
//            }
//        }
//    }
//
//    private class CinemaShowingInfo {
//        public CinemaShowingInfo() {
//
//        }
//
//        public CinemaShowingInfo(final CinemaShowingInfo item) {
//            this.id = item.id;
//            this.fid = item.fid; // ??????id
//            this.name = item.name;
//            this.districtId = item.districtId;
//            this.favorited = item.favorited;
//            this.changed = item.changed; // ?
//            this.price = item.price;
//            this.address = item.address;
//            this.distance = item.distance;
//            this.hasMachined = item.hasMachined;
//            this.hasParks = item.hasParks;
//            this.hasIMax = item.hasIMax;
//            this.has3D = item.has3D;
//            this.hasVIP = item.hasVIP;
//            this.hasWifi = item.hasWifi;
//            this.hasFeature4K = item.hasFeature4K;
//            this.hasLoveseat = item.hasLoveseat;
//            this.hasFeatureDolby = item.hasFeatureDolby;
//            this.hasFeatureHuge = item.hasFeatureHuge;
//            this.hasFeature4D = item.hasFeature4D;
//            this.baidulatitude = item.baidulatitude;
//            this.baidulongitude = item.baidulongitude;
//
//            this.activityList.addAll(item.activityList);
//        }
//
//
//        int id;
//        String fid;
//        String name;
//        int districtId;
//        boolean favorited;
//        boolean changed;
//        double price;
//        String address;
//        double distance;
//        boolean hasMachined;
//        boolean hasParks;
//        boolean hasIMax;
//        boolean has3D;
//        boolean hasVIP;
//        boolean hasWifi;
//        boolean hasFeature4K;
//        boolean hasLoveseat;
//        boolean hasFeatureDolby;
//        boolean hasFeatureHuge;
//        boolean hasFeature4D;
//
//        double baidulatitude;
//        double baidulongitude;
//
//        List<CouponActivityItem> activityList = new ArrayList<>();
//    }
//
//    private class TicketCinemaAdapter extends BaseAdapter {
//
//        private final int MIN_SHOW_COUNT = 7;
//        private List<CinemaShowingInfo> cinemas = new ArrayList<CinemaShowingInfo>();
//        private BaseActivity context;
//        private int favoriteCount = 0;
//
//        public TicketCinemaAdapter(final BaseActivity context, final List<CinemaShowingInfo> cinemas) {
//            this.context = context;
//            if (null != cinemas) {
//                this.cinemas.addAll(cinemas);
//            }
//            getFavoriteCount();
//        }
//
//        public void clear() {
//            this.cinemas.clear();
//            this.notifyDataSetChanged();
//        }
//
//        // ????????????Id
//        public int getid(int pos) {
//            if (this.cinemas.size() > pos) {
//                return cinemas.get(pos).id;
//            } else {
//                return -1;
//            }
//        }
//
//        @Override
//        public int getCount() {
//            return cinemas.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            return arg0;
//        }
//
//        @Override
//        public long getItemId(int arg0) {
//            return arg0;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            final ViewHolder holder;
//            if (convertView == null) {
//                holder = new ViewHolder();
//                convertView = LayoutInflater.from(context).inflate(R.layout.cinema_showing_list_item, null);
//
//                holder.ll_cinema_showing_item = (LinearLayout) convertView.findViewById(R.id.ll_cinema_showing_item);
//                holder.cinema_group_view = (ImageView) convertView.findViewById(R.id.cinema_group_view);
//                holder.name = (TextView) convertView.findViewById(R.id.name);
//                holder.ticket_value = (TextView) convertView.findViewById(R.id.ticket_value);
//                holder.ticket_money_icon = (TextView) convertView.findViewById(R.id.ticket_value_money_mark);
//                holder.ticket_value_label = (TextView) convertView.findViewById(R.id.ticket_value_icon);
//                holder.address = (TextView) convertView.findViewById(R.id.address);
//                holder.distance = (TextView) convertView.findViewById(R.id.distance);
//
//                holder.icon_machine = (ImageView) convertView.findViewById(R.id.icon_machine);
//                holder.icon_parks = (ImageView) convertView.findViewById(R.id.icon_parks);
//                holder.icon_parks_left = (ImageView) convertView.findViewById(R.id.icon_parks_left);
//                holder.icon_imax = (ImageView) convertView.findViewById(R.id.icon_imax);
//                holder.icon_imax_left = (ImageView) convertView.findViewById(R.id.icon_imax_left);
//                holder.icon_3d = (ImageView) convertView.findViewById(R.id.icon_3d);
//                holder.icon_3d_left = (ImageView) convertView.findViewById(R.id.icon_3d_left);
//                holder.icon_vip = (ImageView) convertView.findViewById(R.id.icon_vip);
//                holder.icon_vip_left = (ImageView) convertView.findViewById(R.id.icon_vip_left);
//                holder.icon_wififree = (ImageView) convertView.findViewById(R.id.icon_wififree);
//                holder.icon_wififree_left = (ImageView) convertView.findViewById(R.id.icon_wifi_left);
//                holder.icon_coupon_left = (ImageView) convertView.findViewById(R.id.icon_coupon_left);
//
//                holder.couponLayout = (CouponLayout) convertView.findViewById(R.id.coupon_tag_layout);
//                holder.item_seperate_bg = convertView.findViewById(R.id.item_seperate_bg);
//
//                holder.location_value = (TextView) convertView.findViewById(R.id.location_value);
//
//                // ??????????????????
//                holder.no_location_tip = convertView.findViewById(R.id.no_location_tip);
//                holder.no_location_tip.setClickable(true);
//                holder.no_location_tip.setOnClickListener(new OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        holder.location_value.setText(R.string.st_location_running);
//                        // ??????????????????,???????????????????????????
//                        startLocation();
//                    }
//                });
//
//                holder.address_holder = convertView.findViewById(R.id.address_holder);
//                holder.features = convertView.findViewById(R.id.features);
//                holder.name_part = convertView.findViewById(R.id.name_part);
//
//                convertView.setTag(holder);
//            } else {
//                holder = (ViewHolder) convertView.getTag();
//            }
//
//            final CinemaShowingInfo item = cinemas.get(position);
//
//            // ?????????>7?????????????????????????????????
//            holder.item_seperate_bg.setVisibility(View.GONE);
//            holder.cinema_group_view.setVisibility(View.GONE);
//
//            holder.no_location_tip.setVisibility(View.GONE);
//            holder.address_holder.setVisibility(View.VISIBLE);
//            holder.features.setVisibility(View.VISIBLE);
//            holder.name_part.setVisibility(View.VISIBLE);
//
//            //??????????????????
//            holder.ll_cinema_showing_item.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onListItemClick(item.id, position);
//                }
//            });
//
//            LogWriter.d("checkList", "search:" + isSearching + ", count:" + getCount() + ", show type:" + showingType);
//            if (!isSearching && getCount() > MIN_SHOW_COUNT && TYPE_ALL == showingType) {
//                if (0 == position) {
//                    // ??????????????????
//                    holder.cinema_group_view.setImageResource(R.drawable.cinema_list_nearest_icon);
//                    holder.cinema_group_view.setVisibility(View.VISIBLE);
//                    holder.item_seperate_bg.setVisibility(View.VISIBLE);
//                    holder.ll_cinema_showing_item.setOnClickListener(new OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                            StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                            if (!TextUtils.isEmpty(referBean)) {
//                                bean1.refer = referBean;
//                            }
//                            StatisticManager.getInstance().submit(bean1);
//
//                            context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                            Map<String, String> params = new HashMap<>();
//                            params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                            StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                            if (!TextUtils.isEmpty(referBean)) {
//                                bean3.refer = referBean;
//                            }
//                            StatisticManager.getInstance().submit(bean3);
//
//                            context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                            Map<String, String> businessParam = new HashMap<>();
//                            businessParam.put(StatisticConstant.CINEMA_ID, String.valueOf(item.id));
//                            StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, null, "nearest", null, null, null, businessParam);
//                            if (!TextUtils.isEmpty(referBean)) {
//                                bean.refer = referBean;
//                            }
//                            StatisticManager.getInstance().submit(bean);
//
//                            JumpUtil.startCinemaShowtimeActivity(context, bean.toString(), String.valueOf(item.id), null, null, 0);
//                        }
//                    });
//                    if (null == mLocationInfo || mLocationInfo.isLose()) {
//                        // ??????????????????
//                        holder.no_location_tip.setVisibility(View.VISIBLE);
//                        if (isLocation) {
//                            holder.location_value.setText(R.string.st_location_running);
//                        } else {
//                            holder.location_value.setText(R.string.st_location_failed_tip);
//                        }
//                        holder.address_holder.setVisibility(View.GONE);
//                        holder.features.setVisibility(View.GONE);
//                        holder.name_part.setVisibility(View.GONE);
//                    }
//                } else if (favoriteCount > 0) {
//                    if (1 == position) {
//                        holder.cinema_group_view.setImageResource(R.drawable.cinema_list_favorite_icon);
//                        holder.cinema_group_view.setVisibility(View.VISIBLE);
//                        holder.ll_cinema_showing_item.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean1.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean1);
//
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                Map<String, String> params = new HashMap<>();
//                                params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                                StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean3.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean3);
//
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                Map<String, String> businessParam = new HashMap<>();
//                                businessParam.put(StatisticConstant.CINEMA_ID, String.valueOf(item.id));
//                                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, null, "frequented", "1", null, null, businessParam);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean);
//                                JumpUtil.startCinemaShowtimeActivity(context, bean.toString(), String.valueOf(item.id), null, null, 0);
//                            }
//                        });
//                    } else if (favoriteCount == position) {
//                        holder.item_seperate_bg.setVisibility(View.VISIBLE);
//                        holder.ll_cinema_showing_item.setOnClickListener(new OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                StatisticPageBean bean1 = context.assemble(StatisticConstant.CLOSE, null, null, null, null, null, null);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean1.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean1);
//
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                Map<String, String> params = new HashMap<>();
//                                params.put("duration", String.valueOf(System.currentTimeMillis() - mStartTime));
//                                StatisticPageBean bean3 = context.assemble(StatisticConstant.TIMING, null, null, null, null, null, params);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean3.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean3);
//
//                                context.mPageLabel = StatisticTicket.PN_CINEMA_LIST;
//                                Map<String, String> businessParam = new HashMap<>();
//                                businessParam.put(StatisticConstant.CINEMA_ID, String.valueOf(item.id));
//                                StatisticPageBean bean = context.assemble(StatisticTicket.TICKET_LIST, null, "frequented", String.valueOf(position), null, null, businessParam);
//                                if (!TextUtils.isEmpty(referBean)) {
//                                    bean.refer = referBean;
//                                }
//                                StatisticManager.getInstance().submit(bean);
//                                JumpUtil.startCinemaShowtimeActivity(context, bean.toString(), String.valueOf(item.id), null, null, 0);
//                            }
//                        });
//                    } else {
//                        holder.item_seperate_bg.setVisibility(View.GONE);
//                        holder.cinema_group_view.setVisibility(View.GONE);
//                    }
//
//                    if (favoriteCount == position) {
//                        holder.item_seperate_bg.setVisibility(View.VISIBLE);
//                    }
//                } else {
//                    holder.item_seperate_bg.setVisibility(View.GONE);
//                    holder.cinema_group_view.setVisibility(View.GONE);
//                }
//            }
//            holder.name.setText(item.name);
//            if (item.price <= 0) {
//                holder.ticket_value.setVisibility(View.INVISIBLE);
//                holder.ticket_money_icon.setVisibility(View.INVISIBLE);
//                holder.ticket_value_label.setVisibility(View.INVISIBLE);
//            } else {
//                holder.ticket_value.setVisibility(View.VISIBLE);
//                holder.ticket_money_icon.setVisibility(View.VISIBLE);
//                holder.ticket_value_label.setVisibility(View.VISIBLE);
//                holder.ticket_value.setText(String.valueOf((int) item.price / 100));
//            }
//            holder.address.setText(TextUtils.isEmpty(item.address) ? "--" : item.address);
//
//            if (null == mLocationInfo || mLocationInfo.isLose()) {
//                holder.distance.setVisibility(View.GONE);
//            } else {
//                // if order by subway, add "?????????"
//                String value = getDistance(item.distance);
//                if (TYPE_FILTER == showingType && FilterEventType.TYPE_STATION == filtertype) {
//                    value = String.format("?????????%s", value);
//                }
//                holder.distance.setText(value);
//            }
//
//            // ?????????VIP???3D???IMAX???????????????WIFI
//            if (item.hasMachined) {
//                holder.icon_machine.setVisibility(View.GONE);
//                holder.icon_vip_left.setVisibility(View.GONE);
//            } else {
//                holder.icon_machine.setVisibility(View.GONE);
//                holder.icon_vip_left.setVisibility(View.GONE);
//            }
//
//            if (item.hasVIP) {
//                holder.icon_vip.setVisibility(View.VISIBLE);
//                holder.icon_3d_left.setVisibility(View.VISIBLE);
//            } else {
//                holder.icon_vip.setVisibility(View.GONE);
//                holder.icon_3d_left.setVisibility(View.GONE);
//            }
//
//            if (item.has3D) {
//                holder.icon_3d.setVisibility(View.GONE);
//                holder.icon_imax_left.setVisibility(View.GONE);
//            } else {
//                holder.icon_3d.setVisibility(View.GONE);
//                holder.icon_imax_left.setVisibility(View.GONE);
//            }
//
//            if (item.hasIMax) {
//                holder.icon_imax.setVisibility(View.VISIBLE);
//                holder.icon_parks_left.setVisibility(View.VISIBLE);
//            } else {
//                holder.icon_imax.setVisibility(View.GONE);
//                holder.icon_parks_left.setVisibility(View.GONE);
//            }
//
//            if (item.hasParks) {
//                holder.icon_parks.setVisibility(View.VISIBLE);
//                holder.icon_wififree_left.setVisibility(View.VISIBLE);
//            } else {
//                holder.icon_parks.setVisibility(View.GONE);
//                holder.icon_wififree_left.setVisibility(View.GONE);
//            }
//
//            if (item.hasWifi) {
//                holder.icon_wififree.setVisibility(View.VISIBLE);
//                holder.icon_coupon_left.setVisibility(View.VISIBLE);
//            } else {
//                holder.icon_wififree.setVisibility(android.view.View.GONE);
//                holder.icon_coupon_left.setVisibility(View.GONE);
//            }
//            holder.couponLayout.removeAllViews();
//            if (item.activityList != null && item.activityList.size() > 0) {
//                holder.couponLayout.setVisibility(View.VISIBLE);
//                for (int j = 0; j < item.activityList.size(); j++) {
//
//                    TextView textView = new TextView(context);
//                    CouponActivityItem couponItem = item.activityList.get(j);
//
//                    if (couponItem.getIsSelected()) {
//                        textView.setBackgroundResource(R.drawable.coupon_item_bg_orange);
//                        textView.setTextColor(ContextCompat.getColor(context, R.color.color_ff8600));
//                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_size_pxtosp_20));
//                    } else {
//                        textView.setBackgroundResource(R.drawable.coupon_item_bg_red);
//                        textView.setTextColor(ContextCompat.getColor(context, R.color.color_f15153));
//                        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.font_size_pxtosp_20));
//                    }
//                    textView.setText(couponItem.getTag());
//                    holder.couponLayout.addView(textView);
//                }
//                TextView textView = new TextView(context);
//                textView.setTextColor(ContextCompat.getColor(context, R.color.color_ff8600));
//                textView.setText("...");
//                holder.couponLayout.addView(textView);
//
//            } else {
//                holder.couponLayout.setVisibility(View.GONE);
//            }
//
//            return convertView;
//        }
//
//        // ??????????????????
//        private void getFavoriteCount() {
//            favoriteCount = 0;
//            int index = 0;
//            for (int i = 0; i < this.cinemas.size(); i++) {
//                // ??????????????????????????????????????????????????????
//                if (0 != i && cinemas.get(i).favorited) {
//                    this.favoriteCount++;
//                    LogWriter.d("checkList", "get favorite name:" + cinemas.get(i).name);
//                } else {
//                    index++;
//                }
//
//                // ????????????????????????????????????????????????????????????????????????????????????
//                if (index >= 2) {
//                    break;
//                }
//            }
//
//            LogWriter.d("checkList", "favorite count:" + favoriteCount);
//        }
//
//        public void setDatas(final ArrayList<CinemaShowingInfo> cinemas) {
//            this.cinemas.clear();
//            if (null != cinemas) {
//                this.cinemas.addAll(cinemas);
//            }
//
//            getFavoriteCount();
//
//            this.notifyDataSetChanged();
//        }
//    }
//
//    private class ViewHolder {
//        ImageView cinema_group_view;
//        TextView name;
//        TextView ticket_value;
//        TextView ticket_money_icon;
//        TextView ticket_value_label;
//        TextView address;
//        TextView distance;
//
//        ImageView icon_machine;
//        ImageView icon_parks;
//        ImageView icon_parks_left;
//        ImageView icon_imax;
//        ImageView icon_imax_left;
//        ImageView icon_3d;
//        ImageView icon_3d_left;
//        ImageView icon_vip;
//        ImageView icon_vip_left;
//        ImageView icon_wififree;
//        ImageView icon_wififree_left;
//        ImageView icon_coupon_left;
//        CouponLayout couponLayout;
//        View item_seperate_bg;
//
//        TextView location_value;
//        View no_location_tip;
//        View features;
//        View address_holder;
//        View name_part;
//        LinearLayout ll_cinema_showing_item;
//    }
//}
