package com.mtime.bussiness.mine.profile.activity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.aspsine.irecyclerview.IRecyclerView;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.kotlin.android.user.UserManager;
import com.kotlin.android.app.data.entity.user.UserLocation;
import com.mtime.frame.BaseActivity;
import com.mtime.frame.App;
import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.mine.profile.adapter.LocationListAdapter;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.bussiness.location.LocationHelper;
import com.mtime.bussiness.mine.profile.bean.LocationListBean;
import com.mtime.bussiness.mine.profile.bean.LocationListItemBean;
import com.mtime.bussiness.mine.profile.bean.UpdateMemberInfoBean;
import com.mtime.network.RequestCallback;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vivian.wei on 2017/4/7.
 * 个人资料-选择居住地页
 */
@Route(path = RouterActivityPath.Main.PAGER_LOCAL_SELECT)
public class LocationSelectActivity extends BaseActivity implements View.OnClickListener {

    private View header;
    private View llAutoLocation;
    private IRecyclerView iRecyclerView;
    private View loading_failed_layout;

    // 已经设置的居住地值
    private int userCountryId = 0;
    private String userLocationId = "";
    // 当前层级，1-国家 2-省 3-市
    private int curLevel = 0;
    // 接口参数
    private int paramLocationId = 0;

    private List<LocationListItemBean> countryList = new ArrayList<>();
    private List<LocationListItemBean> provinceList = new ArrayList<>();
    private List<LocationListItemBean> locationList;
    private LocationListAdapter adapter;
    private RequestCallback getLocationListCallback;

    // 接口参数
    private static final String PARAM_LOCATIONID = "locationId";
    private static final String PARAM_TYPE = "type";
    // 接口参数值
    private static final String TYPE_LOCATION = "2";
    // 分隔符
    private static final String CROSS_LINE = "-";
    // 地区层级
    private static final int LEVEL_COUNTRY = 1;
    private static final int LEVEL_PROVINCE = 2;

    private String mNormalCityId;
    private String mNormalCityName;

    @Override
    protected void onInitVariable() {
        String levelRelation = getIntent().getStringExtra(App.getInstance().KEY_LOCATION_LEVEL_RELATION);
        // 根据用户居住地层级Id串获取国家Id、省Id、城市Id
        getLevelRelationIds(levelRelation);
    }

    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_location_select);

        // 标题栏
        initTitle();
        // 地区列表
        iRecyclerView = findViewById(R.id.location_list);
        iRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        header = getLayoutInflater().inflate(R.layout.location_select_header, null);
        View footer = getLayoutInflater().inflate(R.layout.location_select_footer, null);
        iRecyclerView.addHeaderView(header);
        iRecyclerView.addFooterView(footer);
        // 显示自动定位
        showAutoLocation();
        loading_failed_layout = findViewById(R.id.loading_failed_layout);
    }

    @Override
    protected void onInitEvent() {
        getLocationListCallback = new RequestCallback() {

            @Override
            public void onSuccess(Object o) {

                UIUtil.dismissLoadingDialog();
                curLevel++;

                LocationListBean bean = (LocationListBean) o;
                List<LocationListItemBean> list = bean.getList();
                if (null != list && list.size() > 0) {
                    // 国家列表不会变，只请求一次，并存起来
                    if (LEVEL_COUNTRY == curLevel) {
                        countryList = getSortCountryList(list);
                    } else if (LEVEL_PROVINCE == curLevel) {
                        provinceList = list;
                    }
                    if (null == adapter) {
                        locationList = list;
                        adapter = new LocationListAdapter(LocationSelectActivity.this, locationList);
                        iRecyclerView.setIAdapter(adapter);

                        adapter.setOnItemClickListener(new OnLocationItemClickListener() {

                            @Override
                            public void onItemClick(View view, LocationListItemBean bean) {
                                if (bean.isSubset()) {
                                    // 获取子地区列表数据，并更新列表
                                    LocationSelectActivity.this.requestLocationList(bean.getLocationId());
                                } else {
                                    // 更新居住地
                                    LocationSelectActivity.this.updateLocation(String.valueOf(bean.getLocationId()), bean.getLocationName());
                                }
                            }

                        });

                    } else {
                        locationList.clear();
                        locationList.addAll(list);
                        adapter.notifyDataSetChanged();

                        if (curLevel > 1) {
                            header.setVisibility(View.GONE);
                        }
                    }
                }

            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                showReloadView();
            }
        };
    }

    @Override
    protected void onRequestData() {
        // 显示地区列表
        requestLocationList(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_auto_location:
                // 更新居住地
                updateLocation(mNormalCityId, mNormalCityName);
                break;
            default:
                break;
        }
    }

    // 根据用户居住地层级Id串获取国家Id、省Id、城市Id
    private void getLevelRelationIds(String level) {
        if (TextUtils.isEmpty(level)) {
            return;
        }
        String[] array = level.split(CROSS_LINE);
        if (0 == array.length || array.length > 3) {
            return;
        }
        userCountryId = Integer.parseInt(array[0]);
        userLocationId = array[array.length - 1];
    }

    // 标题栏
    private void initTitle() {
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE,
                getResources().getString(R.string.location_select_title), new BaseTitleView.ITitleViewLActListener() {

            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if (type == BaseTitleView.ActionType.TYPE_BACK) {
                    // 返回上一级地区列表
                    backPreLevel();
                }
            }

        }).setCloseParent(false);
    }

    // 显示自动定位国家和城市
    private void showAutoLocation() {
        llAutoLocation = header.findViewById(R.id.ll_auto_location);
        final ImageView ivAutoLocationIcon = header.findViewById(R.id.iv_auto_location_icon);
        final TextView tvAutoLocationName = header.findViewById(R.id.tv_auto_location_name);
        final TextView tvAutoLocationSetTip = header.findViewById(R.id.tv_auto_location_set_tip);

        LocationHelper.location(getApplicationContext(), true, new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if (null != locationInfo) {
                    mNormalCityId = locationInfo.getLocationCityId();
                    mNormalCityName = locationInfo.getLocationCityName();
                    //定位成功
                    ivAutoLocationIcon.setImageResource(R.drawable.icon_location_select);
                    tvAutoLocationName.setText(mNormalCityName);
                    tvAutoLocationSetTip.setVisibility(View.GONE);

                    llAutoLocation.setOnClickListener(LocationSelectActivity.this);
                } else {
                    //定位失败
                    ivAutoLocationIcon.setImageResource(R.drawable.icon_location_select_fail);
                    tvAutoLocationName.setText(getResources().getString(R.string.location_select_auto_fail));
                    tvAutoLocationSetTip.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onLocationFailure(LocationException locationException) {
                //定位失败
                ivAutoLocationIcon.setImageResource(R.drawable.icon_location_select_fail);
                tvAutoLocationName.setText(getResources().getString(R.string.location_select_auto_fail));
                tvAutoLocationSetTip.setVisibility(View.VISIBLE);
            }
        });

    }

    // 更新居住地
    private void updateLocation(final String newLocationId, final String newLocationName) {
        if (userLocationId.equals(newLocationId)) {
            finish();
            return;
        }

        UIUtil.showLoadingDialog(LocationSelectActivity.this);

        Map<String, String> param = new HashMap<>(2);
        param.put(PARAM_LOCATIONID, newLocationId);
        param.put(PARAM_TYPE, TYPE_LOCATION);
        HttpUtil.post(ConstantUrl.UPDATE_MEMBERINFO, param, UpdateMemberInfoBean.class, new RequestCallback() {

            @Override
            public void onSuccess(final Object obj) {
                UIUtil.dismissLoadingDialog();

                final UpdateMemberInfoBean bean = (UpdateMemberInfoBean) obj;
                if (0 == bean.getBizCode()) {
                    // 更新成功后设置
                    UserLocation location = new UserLocation(Integer.parseInt(newLocationId), newLocationName, bean.getLocationRelation());
                    UserManager.Companion.getInstance().setLocation(location);
                    MToastUtils.showShortToast("居住地修改成功");
                    finish();
                } else {
                    MToastUtils.showShortToast(bean.getBizMsg());
                }
            }

            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast("修改居住地失败:" + e.getLocalizedMessage());
            }
        });
    }

    // 显示地区列表
    private void requestLocationList(int locationId) {
        paramLocationId = locationId;
        UIUtil.showLoadingDialog(this);
        Map<String, String> param = null;
        if (paramLocationId > 0) {
            param = new HashMap<>(1);
            param.put("locationId", String.valueOf(paramLocationId));
        }
        HttpUtil.get(ConstantUrl.GET_LOCATION_LIST, param, LocationListBean.class, getLocationListCallback);
    }

    // 保存国家列表
    private List<LocationListItemBean> getSortCountryList(List<LocationListItemBean> list) {
        // 把用户已选中的国家放第一个
        List<LocationListItemBean> sortList = new ArrayList<>();
        LocationListItemBean itemBean;
        int selectIndex = -1;
        for (int i = 0, size = list.size(); i < size; i++) {
            itemBean = list.get(i);
            if (userCountryId > 0 && itemBean.getLocationId() == userCountryId) {
                selectIndex = i;
                itemBean.setSelect(true);
                sortList.add(0, itemBean);
            } else {
                sortList.add(itemBean);
            }
        }
        if (selectIndex > -1) {
            list.remove(selectIndex);
            list.add(0, sortList.get(0));
        }
        return sortList;
    }

    public interface OnLocationItemClickListener {
        void onItemClick(View view, LocationListItemBean bean);
    }

    // 返回上一级地区列表
    private void backPreLevel() {
        if (curLevel > 1) {
            locationList.clear();
            if (2 == curLevel) {
                locationList.addAll(countryList);
            } else if (3 == curLevel) {
                locationList.addAll(provinceList);
            }
            adapter.notifyDataSetChanged();
            curLevel--;
            if (curLevel < 2) {
                header.setVisibility(View.VISIBLE);
            }
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 返回上一级地区列表
            backPreLevel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 显示加载失败
    private void showReloadView() {
        loading_failed_layout.setVisibility(View.VISIBLE);
        UIUtil.showLoadingFailedLayout(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading_failed_layout.setVisibility(View.GONE);
                onRequestData();
            }
        });
    }
}