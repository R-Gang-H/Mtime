package com.mtime.bussiness.location;

import java.util.ArrayList;
import java.util.Map;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.google.gson.Gson;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.kotlin.android.mtime.ktx.GlobalDimensionExt;
import com.kotlin.android.router.liveevent.EventKeyExtKt;
import com.kotlin.android.app.router.liveevent.event.CityChangedState;
import com.kotlin.android.app.router.path.RouterActivityPath;
import com.mtime.R;
import com.mtime.base.location.LocationException;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.location.adapter.CityAdadper;
import com.mtime.bussiness.location.adapter.CitySearchAdapter;
import com.mtime.bussiness.location.bean.ChangeCitySortBean;
import com.mtime.bussiness.location.bean.ChinaProvincesBean;
import com.mtime.bussiness.location.bean.CityBean;
import com.mtime.bussiness.location.bean.ProvinceBean;
import com.mtime.bussiness.location.widget.CityDataView;
import com.mtime.bussiness.mine.bean.MessageConfigsSetBean;
import com.mtime.common.cache.CacheManager;
import com.mtime.common.utils.Utils;
import com.mtime.constant.Constants;
import com.mtime.event.EventManager;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.ToolsUtils;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

// 城市切换
@Route(path = RouterActivityPath.Main.PAGER_CITY_CHANGE)
public class CityChangeActivity extends BaseActivity implements OnClickListener {
    
    public static void launch(AppCompatActivity context, String refer, int requestcode) {
        context.startActivityForResult(launch(context, refer), requestcode);
    }
    
    public static void launch(Fragment fragment, String refer, int requestcode) {
        fragment.startActivityForResult(launch(fragment.getContext(), refer), requestcode);
    }
    
    public static Intent launch(Context context, String refer) {
        Intent launcher = new Intent(context, CityChangeActivity.class);
        dealRefer(context, refer, launcher);
        return launcher;
    }

    public static final String KEY_IS_FROM_GUIDE = "key_is_from_guide";

    private final int HOT_PROVINCE = 12;
    private ListView cityList;
    private ArrayList<ChangeCitySortBean> changeCitySortBeans;

    private OnItemClickListener itemClickListener;
    private EditText searchCity;
    private TextView cancelSearch;        // 取消
    private TextView mSearchTv;           // 搜索
    private ImageButton mClearBtn;        // 清空
    private ListView searchResult;

    private ArrayList<CityBean> cityBeans;
    private CitySearchAdapter cSearchAdapter;
    private ArrayList<CityBean> searchBeans;
    private LinearLayout searchBody;

    private TextView noInfoText;
    private CityDataView data = null;
    private ChinaProvincesBean chinaProvincesBean;  // 二级城市数据
    private String toActId;
    private boolean isFromGuide;

    private void FormatCityBean() {
        for (int i = 1; i < changeCitySortBeans.size(); i++) {
            for (int j = 0; j < changeCitySortBeans.get(i).getCityBeans().size(); j++) {
                cityBeans.add(changeCitySortBeans.get(i).getCityBeans().get(j));
            }
        }
    }

    @Override
    public void onInitVariable() {
        cityBeans = new ArrayList<CityBean>();
        toActId = getIntent().getStringExtra(App.getInstance().KEY_TARGET_ACTIVITY_ID);
        isFromGuide = getIntent().getBooleanExtra(KEY_IS_FROM_GUIDE, false);

        data = new CityDataView(this);
        setPageLabel("selectCity");
    }

    @Override
    public void onBackPressed() {
        if (isFromGuide) {
            MToastUtils.showShortToast(R.string.choose_city);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.city_sidebar_view);
        View title = this.findViewById(R.id.city_title);
        TitleOfNormalView titleOfNormalView = new TitleOfNormalView(this, title,
                StructType.TYPE_NORMAL_SHOW_BACK_TITLE,
                getResources().getString(R.string.city_change_title),
                new BaseTitleView.ITitleViewLActListener() {
            @Override
            public void onEvent(BaseTitleView.ActionType type, String content) {
                if (isFromGuide && type == BaseTitleView.ActionType.TYPE_BACK) {
                    onBackPressed();
                }
            }
        });
        titleOfNormalView.setCloseParent(!isFromGuide);
        titleOfNormalView.setTitleSize(17f);
        titleOfNormalView.setTitleStyle(Typeface.DEFAULT_BOLD);

        View cityView = this.findViewById(R.id.city_search);
//        View search_region = cityView.findViewById(R.id.search_region);
        searchCity = cityView.findViewById(R.id.search_content);
        searchCity.setHint(getString(R.string.st_input_city_name));
        // 取消
        cancelSearch = cityView.findViewById(R.id.back);
        cancelSearch.setVisibility(View.INVISIBLE);  // 需要占位
        cancelSearch.setTextColor(getColor(R.color.color_8798af));
        cancelSearch.setTextSize(15f);
        // 搜索
        mSearchTv = findViewById(R.id.search);
        mSearchTv.setVisibility(View.VISIBLE);
        mSearchTv.setTextColor(getColor(R.color.color_8798af));
        mSearchTv.setTextSize(15f);
        // 清空
        mClearBtn = cityView.findViewById(R.id.cancel);
        mClearBtn.setVisibility(View.GONE);

        cityList = findViewById(R.id.city_list);

        View header = getLayoutInflater().inflate(R.layout.citylist_head, null);
        final TextView nowCity = header.findViewById(R.id.current_city);
        ImageView locationIv = header.findViewById(R.id.citylist_head_location_iv);
        nowCity.setText("定位中...");
        LocationHelper.location(getApplicationContext(), true, new OnLocationCallback() {
            @Override
            public void onLocationSuccess(LocationInfo locationInfo) {
                if(null != locationInfo && !TextUtils.isEmpty(locationInfo.getLocationCityName())) {
                    nowCity.setOnClickListener(CityChangeActivity.this);
                    nowCity.setText(locationInfo.getLocationCityName());
                    locationIv.setImageResource(R.drawable.ic_city_change_locate_success);
                } else {
                    nowCity.setText(getResources().getString(R.string.city_change_location_fail_tip));    // 定位失败，请检查定位权限或GPS是否开启
                    locationIv.setImageResource(R.drawable.ic_city_change_locate_fail);
                }
            }
    
            @Override
            public void onLocationFailure(LocationException e) {
                nowCity.setText(getResources().getString(R.string.city_change_location_fail_tip));    // 定位失败，请检查定位权限或GPS是否开启
                locationIv.setImageResource(R.drawable.ic_city_change_locate_fail);
            }
        });

        cityList.addHeaderView(header);

        // search view
        searchResult = findViewById(R.id.search_list);
        searchBody = findViewById(R.id.search_body);

        noInfoText = findViewById(R.id.no_info_tv);
    }

    @Override
    public void onInitEvent() {

        // 点击搜索按钮
        mSearchTv.setOnClickListener((v) -> {
            searchCity.requestFocus();
        });

        // 点击"取消"按钮，取消搜索状态
        cancelSearch.setOnClickListener((v) -> {
            searchCity.clearFocus();
            searchCity.setText("");
        });

        searchCity.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    // 聚焦
                    mSearchTv.setVisibility(View.GONE);
                    cityList.setVisibility(View.GONE);
                    cancelSearch.setVisibility(View.VISIBLE);
                    searchBody.setVisibility(View.VISIBLE);
                } else {
                    // 需要占位
                    cancelSearch.setVisibility(View.INVISIBLE);
                    searchBody.setVisibility(View.GONE);
                    mSearchTv.setVisibility(View.VISIBLE);
                    cityList.setVisibility(View.VISIBLE);
                    // 隐藏键盘
                    final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchCity.getWindowToken(), 0);
                }
            }
        });

        searchCity.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(final View v, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // 点击"物理返回键"
                    searchCity.clearFocus();
                    searchCity.setText("");
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_ENTER) {
                    // 搜索是实时的，这里什么都不用做
                    return true;
                }
                return false;
            }
        });

        searchCity.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                String value = searchCity.getText().toString();
                if (!TextUtils.isEmpty(value)) {
                    // 搜索筛选
                    shifBySearch(value);
                } else {
                    searchBeans = cityBeans;
                    if (cSearchAdapter != null) {
                        cSearchAdapter.setCityBean(searchBeans, "");
                        cSearchAdapter.notifyDataSetChanged();
                    }
                    searchResult.setVisibility(View.VISIBLE);
                    noInfoText.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
            }
        });

        itemClickListener = new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final int position = (Integer) arg0.getTag();
                String id = String.valueOf(changeCitySortBeans.get(position).getCityBeans().get(arg2).getId());
                String name = changeCitySortBeans.get(position).getCityBeans().get(arg2).getName();
                notifyCityChanged(id, name);
                String pushtoken = ToolsUtils.getToken(getApplicationContext());
                String jpushid = ToolsUtils.getJPushId(getApplicationContext());

                if (!TextUtils.isEmpty(pushtoken) || !TextUtils.isEmpty(jpushid)) {
                    Map<String, String> parameterList = new ArrayMap<String, String>(4);
                    parameterList.put("deviceToken", pushtoken);
                    parameterList.put("setMessageConfigType", String.valueOf(3));
                    parameterList.put("locationId", id);
                    parameterList.put("jPushRegId", jpushid);
                    HttpUtil.post(ConstantUrl.SET_MESSAGECONFIGS, parameterList, MessageConfigsSetBean.class, null);
                }
                exit();
            }
        };

        searchResult.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String id = String.valueOf(searchBeans.get(arg2).getId());
                String name = searchBeans.get(arg2).getName();
                notifyCityChanged(id, name);
                exit();
            }
        });

    }

    // 隐藏键盘
    public void setWindowDown() {
        // 隐藏键盘
        final InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchCity.getWindowToken(), 0);
        searchCity.clearFocus();
        searchCity.setText("");
    }

    // 搜索筛选
    private void shifBySearch(final String inputInfo) {

        searchBeans = new ArrayList<CityBean>();
        CityBean cityBean;
        for (int i = 0; i < cityBeans.size(); i++) {
            cityBean = cityBeans.get(i);
            if (cityBean.getName() != null && cityBean.getName().contains(inputInfo)
            || cityBean.getPinyinShort() != null && cityBean.getPinyinShort().toLowerCase().contains(inputInfo.toLowerCase())
            || cityBean.getPinyinFull()!= null && cityBean.getPinyinFull().toLowerCase().contains(inputInfo.toLowerCase())) {
                searchBeans.add(cityBean);
            }
        }
        if (cSearchAdapter == null) {
            cSearchAdapter = new CitySearchAdapter(this, searchBeans, inputInfo);
            searchResult.setAdapter(cSearchAdapter);
        } else {
            cSearchAdapter.setCityBean(searchBeans, inputInfo);
            cSearchAdapter.notifyDataSetChanged();
        }
        isNullList();

    }

    // 获取searchBeans

    public ArrayList<CityBean> getSearchBeans() {
        return searchBeans;

    }

    private void isNullList() {
        if (cSearchAdapter.getCount() == 0) {
            searchResult.setVisibility(View.GONE);
            noInfoText.setVisibility(View.VISIBLE);
        } else {
            searchResult.setVisibility(View.VISIBLE);
            noInfoText.setVisibility(View.GONE);
        }
    }

    /*
     * @see com.mtime.frame.BaseActivity#onInitView(android.os.Bundle)
     */

    /*
     * @see com.mtime.frame.BaseActivity#onLoadData()
     */
    @Override
    protected void onLoadData() {

    }

    /**
     * 获取缓存或项目里存的Json城市列表
     */
    private void getCacheOrJsonAllCity() {
        if (null == chinaProvincesBean) {

            final Object obj = CacheManager.getInstance().getFileCache(ConstantUrl.GET_ALLCITY);
            if (null != obj) {
                getAssetsJsonObj(String.valueOf(obj));
            }

            if (null == chinaProvincesBean) {
                final String JSONString = Utils.getJsonFromAssets(CityChangeActivity.this, "cityJson");
                getAssetsJsonObj(JSONString);
            }
            parserAndUseData();
        }
    }

    /*
     * @see com.mtime.frame.BaseActivity#onRequestData()
     */
    @Override
    protected void onRequestData() {
        RequestCallback cityAllCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                if (null == o) {
                    // 获取缓存或项目里存的Json城市列表
                    getCacheOrJsonAllCity();
                    return;
                }
                synchronized (o) {
                    chinaProvincesBean = (ChinaProvincesBean) o;
                }

                parserAndUseData();
            }

            @Override
            public void onFail(final Exception e) {
                // 获取缓存或项目里存的Json城市列表
                getCacheOrJsonAllCity();
            }
        };

        HttpUtil.get(ConstantUrl.GET_ALLCITY, null, ChinaProvincesBean.class, cityAllCallback, 0, null, 2000);
    }

    /**
     * 获取内的Assets数据 生成对象
     */
    public boolean getAssetsJsonObj(String ret) {
        if (ret != null) {
            final Object o = handle(ret);
            synchronized (o) {
                try {
                    chinaProvincesBean = (ChinaProvincesBean) o;
                } catch (Exception e) {
                    chinaProvincesBean = new ChinaProvincesBean();
                }
            }
        }
        return true;
    }

    public Object handle(final String data) {
        try {
            final Gson gson = new Gson();
            return gson.fromJson(data, ChinaProvincesBean.class);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /*
     * @see com.mtime.frame.BaseActivity#onUnloadData()
     */
    @Override
    protected void onUnloadData() {
        System.gc();
    }

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.current_city:
                LocationHelper.location(getApplicationContext(), new OnLocationCallback() {
                    @Override
                    public void onLocationSuccess(LocationInfo locationInfo) {
                        if(null != locationInfo) {
                            //通知票房页刷新
                            Intent intent = new Intent("ben");
                            intent.putExtra("change", "yes");
                            LocalBroadcastManager.getInstance(CityChangeActivity.this).sendBroadcast(intent);
                            notifyCityChanged(locationInfo.getLocationCityId(), locationInfo.getLocationCityName());
                        }
                    }

                    @Override
                    public void onLocationFailure(LocationException e) {
                        onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                    }
                });

                exit();
                break;
            default:
                break;
        }
    }

    private void notifyCityChanged(String id, String name) {
        if (!TextUtils.equals(id, GlobalDimensionExt.INSTANCE.getCurrentCityId())) {
            LocationHelper.cacheUserCityInfo(id, name);
            EventManager.getInstance().sendCityChangedEvent(null, null, id, name);
            LiveEventBus.get(EventKeyExtKt.CITY_CHANGED).post(new CityChangedState(id, name));
        }
    }

    private void exit() {
        setResult(-1);
        if (!TextUtils.isEmpty(toActId)) {
            try {
                Class<?> clazz = Class.forName(toActId);
                Intent intent = new Intent();
                intent.putExtra(Constants.KEY_MAIN_TAB_INDEX, getIntent().getIntExtra(Constants.KEY_MAIN_TAB_INDEX, 0));
                startActivity(clazz, intent);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        finish();
    }

    private synchronized void parserAndUseData() {
        if (null == cityList || null == chinaProvincesBean.getProvinces() || chinaProvincesBean.getProvinces().size() < 1) {
            return;
        }

        final ArrayList<ProvinceBean> hotList = new ArrayList<ProvinceBean>();

        int count = chinaProvincesBean.getProvinces().size() > HOT_PROVINCE ? HOT_PROVINCE : chinaProvincesBean.getProvinces().size();
        for (int i = 0; i < count; i++) {
            hotList.add(chinaProvincesBean.getProvinces().get(i));
        }

        if (null != changeCitySortBeans) {
            changeCitySortBeans.clear();
        }

        if (null != cityBeans) {
            cityBeans.clear();
        }
        if (null != searchBeans) {
            searchBeans.clear();
        }

        changeCitySortBeans = data.changeCitySortBean(chinaProvincesBean, hotList);
        FormatCityBean();

        final CityAdadper cityAdadper = new CityAdadper(changeCitySortBeans, CityChangeActivity.this,
                itemClickListener);
        cityList.setAdapter(cityAdadper);

        // initialize the search city
        searchBeans = cityBeans;
        cSearchAdapter = null;

        cSearchAdapter = new CitySearchAdapter(CityChangeActivity.this, cityBeans, "");
        searchResult.setAdapter(cSearchAdapter);

        hotList.clear();
    }
    
}
