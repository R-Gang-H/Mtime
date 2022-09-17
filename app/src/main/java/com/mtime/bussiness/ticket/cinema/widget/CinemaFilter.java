package com.mtime.bussiness.ticket.cinema.widget;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IRecyclerView;
import com.kekstudio.dachshundtablayout.DachshundTabLayout;
import com.kekstudio.dachshundtablayout.indicators.DachshundIndicator;
import com.mtime.R;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterBusinessAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterDistrictAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterDistrictPagerAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterFeatureAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterStationAdapter;
import com.mtime.bussiness.ticket.cinema.adapter.CinemaFilterSubwayAdapter;
import com.mtime.bussiness.ticket.cinema.bean.BusinessAreaNewBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaFeatureBean;
import com.mtime.bussiness.ticket.cinema.bean.DistrictBean;
import com.mtime.bussiness.ticket.cinema.bean.StationBean;
import com.mtime.bussiness.ticket.cinema.bean.SubwayBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by vivian.wei on 2017/10/18.
 * Tab购票_影院_排序&筛选组件（2017年10月新版）
 */

public class CinemaFilter implements View.OnClickListener, CinemaFilterAdapterListener {

    // Tab
    @BindView(R.id.layout_cinema_list_filter_tab_sort_rl)
    View mTabSortView;
    @BindView(R.id.layout_cinema_list_filter_tab_district_rl)
    View mTabDistrictView;
    @BindView(R.id.layout_cinema_list_filter_tab_effect_rl)
    View mTabEffectView;
    // Tab文字
    @BindView(R.id.layout_cinema_list_filter_tab_sort_tv)
    TextView mTabSortTv;
    @BindView(R.id.layout_cinema_list_filter_tab_district_tv)
    TextView mTabDistrictTv;
    @BindView(R.id.layout_cinema_list_filter_tab_effect_tv)
    TextView mTabEffectTv;
    // Tab箭头
    @BindView(R.id.layout_cinema_list_filter_tab_sort_arrow_iv)
    ImageView mTabSortArrowIv;
    @BindView(R.id.layout_cinema_list_filter_tab_district_arrow_iv)
    ImageView mTabDistrictArrowIv;
    @BindView(R.id.layout_cinema_list_filter_tab_effect_arrow_iv)
    ImageView mTabEffectArrowIv;
    // 展开层
    @BindView(R.id.layout_cinema_list_filter_sort_rl)
    View mSortView;
    @BindView(R.id.layout_cinema_list_filter_district_rl)
    View mDistrictView;
    @BindView(R.id.layout_cinema_list_filter_effect_rl)
    View mEffectView;
    // 遮盖层
    @BindView(R.id.layout_cinema_list_filter_cover_iv)
    ImageView mCoverIv;
    // 排序_离我最近
    @BindView(R.id.layout_cinema_list_filter_sort_distance_tv)
    TextView mDistanceTv;
    // 排序_价格最低
    @BindView(R.id.layout_cinema_list_filter_sort_price_tv)
    TextView mPriceTv;

    // 商圈|地铁_切换tab
    @BindView(R.id.layout_cinema_list_filter_district_subway_tablayout)
    DachshundTabLayout mTabLayout;
    @BindView(R.id.layout_cinema_list_filter_district_subway_viewpager)
    ViewPager mViewPager;

    // 影厅特效
    @BindView(R.id.layout_cinema_list_filter_feature_grid)
    GridView mFeatureGrid;

    public enum FilterEventType {
        TYPE_UNKNOWN,
        TYPE_SORT_DISTANCE,
        TYPE_SORT_PRICE,
        TYPE_BUSINESS,
        TYPE_STATION,
        TYPE_FEATURE
    }

    private final Context mContext;
    private final View mRoot;
    private View mCurView;
    private TextView mCurTabTv;
    private ImageView mCurTabArrowIv;
    // 商圈|地铁
    private CinemaFilterDistrictPagerAdapter mTabLayoutAdapter;
    private ArrayList<View> mPagerViews = new ArrayList<>();
    private View mDistrictPagerView;
    private View mSubwayPagerView;
    private IRecyclerView mDistrictIRecyclerView;
    private IRecyclerView mBusinessIRecyclerView;
    private IRecyclerView mSubwayIRecyclerView;
    private IRecyclerView mStationIRecyclerView;

    private Animation mDownIn;
    private Animation mDownOut;
    private Animation mCoverDownIn;
    private Animation mCoverDownOut;

    private List<CinemaFeatureBean> mCinemaFeatures;
    private CinemaFilterFeatureAdapter mFeatureAdapter;
    private List<DistrictBean> mDistricts = new ArrayList<>(); // 城区列表
    private List<SubwayBean> mSubways = new ArrayList<>();     // 地铁列表
    private CinemaFilterDistrictAdapter mDistrictAdapter;
    private CinemaFilterBusinessAdapter mBusinessAdapter;
    private CinemaFilterSubwayAdapter mSubwayAdapter;
    private CinemaFilterStationAdapter mStationAdapter;

    private ICinemaFilterClickListener mListener;

    // 当前选中的Id
    private int mDistrictSelectIndex = 0;
    private int mBusinessSelectIndex = -1;
    private int mSubwaySelectIndex = 0;
    private int mStationSelectIndex = -1;

    public CinemaFilter(final Context context, final View root, final ICinemaFilterClickListener listener) {
        mContext = context;
        mRoot = root;
        mListener = listener;
        initView();
        initAnimation();
        initEvent();
    }

    private void initView() {
        ButterKnife.bind(this, mRoot);

        // 商圈|地铁
        mDistrictPagerView = View.inflate(mContext, R.layout.pager_cinema_list_filter_district, null);
        mSubwayPagerView = View.inflate(mContext, R.layout.pager_cinema_list_filter_subway, null);

        mPagerViews.add(mDistrictPagerView);
        mPagerViews.add(mSubwayPagerView);
        mTabLayoutAdapter = new CinemaFilterDistrictPagerAdapter(mPagerViews);
        mViewPager.setAdapter(mTabLayoutAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setAnimatedIndicator(new DachshundIndicator(mTabLayout));
        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
        mTabLayout.setTabTextColors(ContextCompat.getColor(mContext, R.color.color_777777), ContextCompat.getColor(mContext, R.color.color_f97d3f));

        mDistrictIRecyclerView = mDistrictPagerView.findViewById(R.id.pager_cinema_list_filter_district_list_irecyclerview);
        mBusinessIRecyclerView = mDistrictPagerView.findViewById(R.id.pager_cinema_list_filter_district_business_irecyclerview);
        mSubwayIRecyclerView = mSubwayPagerView.findViewById(R.id.pager_cinema_list_filter_subway_list_irecyclerview);
        mStationIRecyclerView = mSubwayPagerView.findViewById(R.id.pager_cinema_list_filter_subway_station_irecyclerview);
    }

    // 初始化影厅特效布局
    private void initFeatureView(int featureIndex) {
        // features
        mFeatureAdapter = new CinemaFilterFeatureAdapter(mContext, mCinemaFeatures);
        mFeatureAdapter.setListener(this);
        mFeatureGrid.setAdapter(mFeatureAdapter);

        mFeatureAdapter.setSelectIndex(featureIndex);
        mFeatureAdapter.notifyDataSetChanged();
    }

    // 初始化商圈地铁列表布局
    private void initDistrictSubway() {
        // 城区列表
        mDistrictAdapter = new CinemaFilterDistrictAdapter(mContext, mDistricts);
        mDistrictAdapter.setOnItemClickListener(this);
        mDistrictIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mDistrictIRecyclerView.setIAdapter(mDistrictAdapter);
        // 商圈列表
        mBusinessAdapter = new CinemaFilterBusinessAdapter(mContext, null);
        mBusinessAdapter.setOnItemClickListener(this);
        mBusinessIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mBusinessIRecyclerView.setIAdapter(mBusinessAdapter);
        // 地铁线列表
        mSubwayAdapter = new CinemaFilterSubwayAdapter(mContext, mSubways);
        mSubwayAdapter.setOnItemClickListener(this);
        mSubwayIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSubwayIRecyclerView.setIAdapter(mSubwayAdapter);
        // 地铁站列表
        mStationAdapter = new CinemaFilterStationAdapter(mContext, null);
        mStationAdapter.setOnItemClickListener(this);
        mStationIRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mStationIRecyclerView.setIAdapter(mStationAdapter);
    }

    // 初始化动画
    private void initAnimation() {
        mDownIn = AnimationUtils.loadAnimation(mContext, R.anim.set_cinema_filter_down_in);
        mDownOut = AnimationUtils.loadAnimation(mContext, R.anim.set_cinema_filter_down_out);
        mCoverDownIn = AnimationUtils.loadAnimation(mContext, R.anim.set_cinema_filter_cover_in);
        mCoverDownOut = AnimationUtils.loadAnimation(mContext, R.anim.set_cinema_filter_cover_out);

        mDownOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if(null != mCurView) {
                    mCurView.setVisibility(View.GONE);
                    mCurView = null;
                }
            }
        });

        mCoverDownOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                mCoverIv.setVisibility(View.GONE);
            }
        });
    }

    // 初始化事件
    private void initEvent() {
        // Tab
        mTabSortView.setOnClickListener(this);
        mTabDistrictView.setOnClickListener(this);
        mTabEffectView.setOnClickListener(this);
        // 遮盖
        mCoverIv.setOnClickListener(this);
        // 排序
        mDistanceTv.setOnClickListener(this);
        mPriceTv.setOnClickListener(this);
    }

    // 点击Tab
    private void clickTab(View v1, View v2, View v3) {
        if(View.VISIBLE == v1.getVisibility()) {
            // 隐藏
            v1.startAnimation(mDownOut);
            mCoverIv.startAnimation(mCoverDownOut);
        } else {
            // 显示
            mCurView = v1;
            v1.setVisibility(View.VISIBLE);
            v1.startAnimation(mDownIn);
            if(View.GONE == mCoverIv.getVisibility()) {
                mCoverIv.setVisibility(View.VISIBLE);
                mCoverIv.startAnimation(mCoverDownIn);
            }
        }
        v2.setVisibility(View.GONE);
        v3.setVisibility(View.GONE);
    }

    // 隐藏全部
    private void hideAll() {
        if(null != mCurView) {
            if(mCurView.getId() == R.id.layout_cinema_list_filter_sort_rl) {
                clickTab(mSortView, mDistrictView, mEffectView);
            } else if(mCurView.getId() == R.id.layout_cinema_list_filter_district_rl) {
                clickTab(mDistrictView, mSortView, mEffectView);
            } else {
                clickTab(mEffectView, mSortView, mDistrictView);
            }
        }
    }

    // 设置Tab文字颜色和箭头
    private void setTabTextColorAndArrow(boolean expand, TextView tabTv, ImageView arrowIv) {
        if(expand) { // 展开
            if(null != mCurTabTv) {
                // 将已经展开的tab文字和箭头设置为灰色
                mCurTabTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
                mCurTabArrowIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_cinema_sort_down));
            }
            // 设置当前点击的tab文字和箭头颜色
            tabTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
            arrowIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_cinema_sort_up));
            mCurTabTv = tabTv;
            mCurTabArrowIv = arrowIv;
        } else {
            tabTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
            arrowIv.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_cinema_sort_down));
            mCurTabTv = null;
            mCurTabArrowIv = null;
        }
    }

    // 选中"离我最近"
    private void selectNearest() {
        // tab
        mTabSortTv.setText(mContext.getResources().getString(R.string.nearest));
        setTabTextColorAndArrow(false, mTabSortTv, mTabSortArrowIv);
        // 排序展开层
        mDistanceTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
        mPriceTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
    }

    // 重置商圈地铁
    private void resetDistrict() {
        // 文字箭头
        mTabDistrictTv.setText(mContext.getResources().getString(R.string.whole_city));
        setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
        // 默认选中商圈
        if(null != mViewPager && mViewPager.getCurrentItem() > 0
                && null != mTabLayout && mTabLayout.getSelectedTabPosition() > 0) {
            mViewPager.setCurrentItem(0);
            mTabLayout.getTabAt(0).select();
        }
        // 展开层
        mDistrictSelectIndex = 0;
        mBusinessSelectIndex = -1;
        mSubwaySelectIndex = 0;
        mStationSelectIndex = -1;
        resetDistrictAdapter();
        resetSubwayAdapter();
    }

    // 重置特殊设施
    private void resetFeature() {
        mTabEffectTv.setText(mContext.getResources().getString(R.string.hall_effects));
        setTabTextColorAndArrow(false, mTabEffectTv, mTabEffectArrowIv);
    }

    // 更新城区和商圈列表
    private void resetDistrictAdapter() {
        if(null != mDistrictAdapter) {
            mDistrictAdapter.setSelectIndex(mDistrictSelectIndex);
            mDistrictAdapter.notifyDataSetChanged();
        }
        if(null != mBusinessAdapter) {
            // 需要清空
            List<BusinessAreaNewBean> list = new ArrayList<>();
            mBusinessAdapter.setList(list);
            mBusinessAdapter.setSelectIndex(mBusinessSelectIndex);
            mBusinessAdapter.notifyDataSetChanged();
        }
    }

    // 更新地铁线和车站列表
    private void resetSubwayAdapter() {
        if(null != mSubwayAdapter) {
            mSubwayAdapter.setSelectIndex(mSubwaySelectIndex);
            mSubwayAdapter.notifyDataSetChanged();
        }
        if(null != mStationAdapter) {
            // 需要清空
            List<StationBean> list = new ArrayList<>();
            mStationAdapter.setList(list);
            mStationAdapter.setSelectIndex(mStationSelectIndex);
            mStationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.layout_cinema_list_filter_tab_sort_rl: // tab_排序
                setTabTextColorAndArrow(!(mSortView.getVisibility() == View.VISIBLE), mTabSortTv, mTabSortArrowIv);
                clickTab(mSortView, mDistrictView, mEffectView);
                break;
            case R.id.layout_cinema_list_filter_tab_district_rl: // tab_全城
                setTabTextColorAndArrow(!(mDistrictView.getVisibility() == View.VISIBLE), mTabDistrictTv, mTabDistrictArrowIv);
                clickTab(mDistrictView, mSortView, mEffectView);
                break;
            case R.id.layout_cinema_list_filter_tab_effect_rl: // tab_特效
                setTabTextColorAndArrow(!(mEffectView.getVisibility() == View.VISIBLE), mTabEffectTv, mTabEffectArrowIv);
                clickTab(mEffectView, mSortView, mDistrictView);
                break;
            case R.id.layout_cinema_list_filter_cover_iv: // 遮盖层
                hideAll();
                if(null != mCurView) {
                    if(mCurView.getId() == R.id.layout_cinema_list_filter_sort_rl) {
                        setTabTextColorAndArrow(false, mTabSortTv, mTabSortArrowIv);
                    } else if(mCurView.getId() == R.id.layout_cinema_list_filter_district_rl) {
                        setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
                    } else {
                        setTabTextColorAndArrow(false, mTabEffectTv, mTabEffectArrowIv);
                    }
                }
                break;
            case R.id.layout_cinema_list_filter_sort_distance_tv: // 离我最近
                hideAll();
                selectNearest();
                if (null != mListener) {
                    mListener.onEvent(FilterEventType.TYPE_SORT_DISTANCE, 0, 0);
                }
                break;
            case R.id.layout_cinema_list_filter_sort_price_tv: // 价格最低
                hideAll();
                // tab
                mTabSortTv.setText(mContext.getResources().getString(R.string.lowest_price));
                setTabTextColorAndArrow(false, mTabSortTv, mTabSortArrowIv);
                // 排序展开层
                mDistanceTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_777777));
                mPriceTv.setTextColor(ContextCompat.getColor(mContext, R.color.color_f97d3f));
                if (null != mListener) {
                    mListener.onEvent(FilterEventType.TYPE_SORT_PRICE, 0, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onEvent(CinemaFilterAdapterType type, int position) {
        switch (type) {
            case TYPE_FEATURE: {  // 影厅特效
                hideAll();
                String name = mCinemaFeatures.get(position).getName();
                // tab
                mTabEffectTv.setText(0 == position ? mContext.getResources().getString(R.string.hall_effects) : name);
                setTabTextColorAndArrow(false, mTabEffectTv, mTabEffectArrowIv);
                if (null != mListener) {
                    mListener.onEvent(CinemaFilter.FilterEventType.TYPE_FEATURE, position, 0);
                }
            }
            break;
            case TYPE_DISTRICT: {  // 城区
                // 设置城区选中颜色
                mDistrictAdapter.setSelectIndex(position);
                mDistrictAdapter.notifyDataSetChanged();

                // 刷新右侧商圈列表
                List<BusinessAreaNewBean> businessAreas = new ArrayList<>();
                if(position > 0) {
                    DistrictBean bean = mDistricts.get(position);
                    businessAreas = bean.getBusinessAreas();
                }
                // 设置当前选中的右侧商圈item
                int index = -1;
                if(mDistrictSelectIndex == position) {
                    index = mBusinessSelectIndex;
                }
                mBusinessAdapter.setSelectIndex(index);
                mBusinessAdapter.setList(businessAreas);
                mBusinessAdapter.notifyDataSetChanged();

                if(position == 0) {
                    // 按城区_全部筛
                    mDistrictSelectIndex = 0;
                    mBusinessSelectIndex = -1;
                    mSubwaySelectIndex = 0;
                    mStationSelectIndex = -1;
                    resetSubwayAdapter();
                    hideAll();
                    String name = mContext.getResources().getString(R.string.whole_city);
                    // tab
                    mTabDistrictTv.setText(name);
                    setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
                    if (null != mListener) {
                        mListener.onEvent(FilterEventType.TYPE_BUSINESS, 0, 0);
                    }
                }
            }
            break;
            case TYPE_BUSINESS: {  // 商圈
                mDistrictSelectIndex = mDistrictAdapter.getSelectIndex();
                mBusinessSelectIndex = position;
                mSubwaySelectIndex = 0;
                mStationSelectIndex = -1;
                resetSubwayAdapter();

                // 设置城区选中颜色
                mBusinessAdapter.setSelectIndex(position);
                mBusinessAdapter.notifyDataSetChanged();

                DistrictBean districtBean = mDistricts.get(mDistrictSelectIndex);
                BusinessAreaNewBean businessBean = districtBean.getBusinessAreas().get(position);
                // 按商圈_全部筛
                hideAll();
                String name = 0 == position ? districtBean.getName() : businessBean.getName();
                // tab
                mTabDistrictTv.setText(name);
                setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
                if (null != mListener) {
                    mListener.onEvent(FilterEventType.TYPE_BUSINESS, businessBean.getBusinessId(), districtBean.getDistrictId());
                }
            }
            break;
            case TYPE_SUBWAY: { // 地铁线
                // 设置城区选中颜色
                mSubwayAdapter.setSelectIndex(position);
                mSubwayAdapter.notifyDataSetChanged();
                // 刷新右侧车站列表
                List<StationBean> stations = new ArrayList<>();
                if(position > 0) {
                    SubwayBean bean = mSubways.get(position);
                    stations = bean.getStations();
                }
                // 设置当前选中的地铁站item
                int index = -1;
                if(mSubwaySelectIndex == position) {
                    index = mStationSelectIndex;
                }
                mStationAdapter.setSelectIndex(index);
                mStationAdapter.setList(stations);
                mStationAdapter.notifyDataSetChanged();

                if(position == 0) {
                    // 按地铁线_全部筛
                    mDistrictSelectIndex = 0;
                    mBusinessSelectIndex = -1;
                    resetDistrictAdapter();
                    mSubwaySelectIndex = 0;
                    mStationSelectIndex = -1;
                    hideAll();
                    String name = mContext.getResources().getString(R.string.whole_city);
                    // tab
                    mTabDistrictTv.setText(name);
                    setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
                    if (null != mListener) {
                        mListener.onEvent(FilterEventType.TYPE_STATION, 0, 0);
                    }
                }
            }
            break;
            case TYPE_STATION: { // 地铁站
                // 设置城区选中颜色
                mSubwaySelectIndex = mSubwayAdapter.getSelectIndex();
                mDistrictSelectIndex = 0;
                mBusinessSelectIndex = -1;
                resetDistrictAdapter();
                mStationSelectIndex = position;

                mStationAdapter.setSelectIndex(position);
                mStationAdapter.notifyDataSetChanged();
                SubwayBean subwayBean = mSubways.get(mSubwaySelectIndex);
                StationBean stationBean = subwayBean.getStations().get(position);
                // 按车站_全部筛
                hideAll();
                String name = 0 == position ? subwayBean.getName() : stationBean.getStName();
                // tab
                mTabDistrictTv.setText(name);
                setTabTextColorAndArrow(false, mTabDistrictTv, mTabDistrictArrowIv);
                if (null != mListener) {
                    mListener.onEvent(FilterEventType.TYPE_STATION, stationBean.getStId(), subwayBean.getSubwayId());
                }
            }
            break;
            default:
                break;
        }

    }

    // 设置影厅特效数据
    public void setCinemaFeatureData(List<CinemaFeatureBean> cinemaFeatures, int featureIndex) {
        mCinemaFeatures = cinemaFeatures;
        initFeatureView(featureIndex);
    }

    // 设置商圈和地铁筛选数据
    public void setCinemaScreeningData(List<DistrictBean> districts, List<SubwayBean> subways) {
        mDistricts = districts;
        mSubways = subways;
        initDistrictSubway();
    }

    // 重置
    public void reset() {
        mSortView.setVisibility(View.GONE);
        mDistrictView.setVisibility(View.GONE);
        mEffectView.setVisibility(View.GONE);
        mCoverIv.setVisibility(View.GONE);
        mCurView = null;
        mCurTabTv = null;
        mCurTabArrowIv = null;
        selectNearest();
        resetDistrict();
        resetFeature();
    }

    // 清空List
    public void clear() {
        mPagerViews = null;
        mCinemaFeatures = null;
        mDistricts = null;
        mSubways = null;

        if(null != mDistrictAdapter) {
            mDistrictAdapter.setOnItemClickListener(null);
        }
        if(null != mBusinessAdapter) {
            mBusinessAdapter.setOnItemClickListener(null);
        }
        if(null != mSubwayAdapter) {
            mSubwayAdapter.setOnItemClickListener(null);
        }
        if(null != mStationAdapter) {
            mStationAdapter.setOnItemClickListener(null);
        }

        if(null != mTabSortView) {
            mTabSortView.setOnClickListener(null);
        }
        if(null != mTabDistrictView) {
            mTabDistrictView.setOnClickListener(null);
        }
        if(null != mTabEffectView) {
            mTabEffectView.setOnClickListener(null);
        }
        if(null != mCoverIv) {
            mCoverIv.setOnClickListener(null);
        }
        if(null != mDistanceTv) {
            mDistanceTv.setOnClickListener(null);
        }
        if(null != mPriceTv) {
            mPriceTv.setOnClickListener(null);
        }
    }

    // 设置筛选事件
    public void setFilterListener(CinemaFilter.ICinemaFilterClickListener listener) {
        mListener = listener;
    }

    public interface ICinemaFilterClickListener {
        void onEvent(final CinemaFilter.FilterEventType type, final int id, final int parentId);
    }
}
