package com.mtime.bussiness.location;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import com.mtime.base.location.LocationException;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.base.location.LocationInfo;
import com.mtime.base.location.OnLocationCallback;
import com.mtime.base.utils.MToastUtils;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

/**
 * 地图定位页面
 */
public class MapViewActivity extends BaseActivity {
    // ----------------------------------------------------------------------------------------------------------------------------------
    private static final int     LOCATON_FAIL        = 0;                    // 定位失败
    private static final int     LOCATON_NEAR_CINEMA = 1;                    // 附近影院
    private static final int     LOCATON_DETAIL      = 2;                    // 位置
    // ----------------------------------------------------------------------------------------------------------------------------------
    
    private MapView              mapView;
    private BaiduMap             mBaiduMap;
    private Marker               marker;
    private BitmapDescriptor     markerIcon;
    // 影院传过来的经纬度
    private double               latitude;
    private double               longitude;
    
    private View                 layoutView;
    private String               cinemaName, cinemaAdress, cinemaId;
                                                                              
    private Handler              handler;
    private String titleLabel;

    public static final String KEY_CINEMA_ID = "cinema_id";
    public static final String KEY_CINEMA_NAME = "cinema_name";
    public static final String KEY_CINEMA_ADRESS = "cinema_adress";

    public static final String KEY_MAP_LONGITUDE = "map_longitude"; // 经度
    public static final String KEY_MAP_LATITUDE = "map_latitude"; // 纬度

    public static final String KEY_TITLE = "title";

    // ----------------------------------------------------------------------------------------------------------------------------------
    protected void onInitVariable() {
        Intent intent = getIntent();
        cinemaAdress = intent.getStringExtra(KEY_CINEMA_ADRESS);
        cinemaName = intent.getStringExtra(KEY_CINEMA_NAME);
        cinemaId = intent.getStringExtra(KEY_CINEMA_ID);

        latitude = intent.getDoubleExtra(KEY_MAP_LATITUDE, 0.0);
        longitude = intent.getDoubleExtra(KEY_MAP_LONGITUDE, 0.0);
        titleLabel = intent.getStringExtra(KEY_TITLE);
        if (TextUtils.isEmpty(titleLabel)) {
            titleLabel = "影院";// 默认为影院，现在只有自提点不使用默认的
        }
        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                
                switch (msg.what) {
                    case LOCATON_FAIL:
                        MToastUtils.showShortToast("定位失败,请稍后重试");
                        break;
                    case LOCATON_DETAIL:
                        // * 1e6
                        // make the center.
                        LatLng point = new LatLng(latitude, longitude);
                        MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(point);
                        mBaiduMap.animateMapStatus(u);
                        
                        // draw the marker 
                        OverlayOptions ooA = new MarkerOptions().position(point).icon(markerIcon)
                                .zIndex(9).draggable(true);
                        marker = (Marker) (mBaiduMap.addOverlay(ooA));
                        
                        initOverlay();
                        
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icon_map_normal);
                        int pos = -100;
                        if (null != bmp) {
                            pos = bmp.getHeight() * -1;
                        }
                        
                        LatLng ll = marker.getPosition();
                        try {
                            InfoWindow mInfoWindow = new InfoWindow(layoutView, ll, pos);
                            mBaiduMap.showInfoWindow(mInfoWindow);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        break;
                    case LOCATON_NEAR_CINEMA:
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };

        setPageLabel("map");
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------------
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.activity_map_view);
        
        View navBar = findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, titleLabel, null);

        mapView = findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        mapView.showZoomControls(true);
        
        markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.icon_map_normal);
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------------
    protected void onRequestData() {
        if (latitude > 0.0) {
            // show the map now.
            final Message message = handler.obtainMessage(LOCATON_DETAIL);
            handler.sendMessage(message);
        }
        else {
            final Message message = handler.obtainMessage(LOCATON_FAIL);
            handler.sendMessage(message);
        }
        
    }
    
    @Override
    protected void onResume() {
        this.mapView.onResume();
        super.onResume();
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        this.mapView.onPause();
    }
    
    // ----------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        this.mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
    
    private void initOverlay() {
        layoutView = getLayoutInflater().inflate(R.layout.overlay_pop, null);
        final TextView titleView = layoutView.findViewById(R.id.overlay_title);
        final TextView addressView = layoutView.findViewById(R.id.overlay_address);
        final TextView searchbtn = layoutView.findViewById(R.id.search_path);
        
        titleView.setText(cinemaName);
        addressView.setText(cinemaAdress);
        
        layoutView.setVisibility(View.VISIBLE);
        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                JumpUtil.startCinemaShowtimeActivity(MapViewActivity.this, null, cinemaId, null, null, 0);
            }
        });
        
        searchbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                UIUtil.showLoadingDialog(MapViewActivity.this);
                LocationHelper.location(getApplicationContext(), true, new OnLocationCallback() {
                    @Override
                    public void onLocationSuccess(LocationInfo locationInfo) {
                        UIUtil.dismissLoadingDialog();
                        jumpBaiduMapApp(locationInfo);
                    }

                    @Override
                    public void onLocationFailure(LocationException e) {
                        onLocationSuccess(LocationHelper.getDefaultLocationInfo());
                    }
                });
            }
        });
    }
    
    @SuppressWarnings("deprecation")
    private void jumpBaiduMapApp(LocationInfo locationInfo) {
        // 判断用户手机是否安装百度地图客户端,如果已安装则跳转到百度地图客户端否则跳转百度地图web界面
        if (MtimeUtils.isInstallAppByPackageName(MapViewActivity.this, "com.baidu.BaiduMap")) {
            try {
                // 导航模式，固定为transit、driving、walking，分别表示公交、驾车和步行
                String uri = "intent://map/direction?origin=latlng:" + locationInfo.getLatitude() + ","
                        + locationInfo.getLongitude() + "|name:我当前位置&destination=latlng:" + latitude + ","
                        + longitude + "|name:" + cinemaAdress
                        + "&mode=transit&src=mtime|mtime#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                Intent intent = Intent.getIntent(uri);
                startActivity(intent);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                String url = "http://api.map.baidu.com/direction?origin=latlng:" + locationInfo.getLatitude() + ","
                        + locationInfo.getLongitude() + "|name:我当前位置&destination=latlng:" + latitude + "," + longitude
                        + "|name:" + cinemaAdress + "&mode=transit&region=" + locationInfo.getLocationCityName() + "&output=html&src=mtime|mtime";
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(url);
                intent.setData(content_url);
                startActivity(intent);
            } catch (Exception e) {

            }
        }
    }

    public static void launch(Context context,double longitude, double latitude, String cinemaId, String cinemaName, String cinemaAddress, String title){
        Intent launcher = new Intent();
        launcher.putExtra(KEY_MAP_LONGITUDE,longitude);
        launcher.putExtra(KEY_MAP_LATITUDE,latitude);
        launcher.putExtra(KEY_CINEMA_ID,cinemaId);
        launcher.putExtra(KEY_CINEMA_NAME,cinemaName);
        launcher.putExtra(KEY_CINEMA_ADRESS,cinemaAddress);
        launcher.putExtra(KEY_TITLE,title);
        ((BaseActivity)context).startActivity(MapViewActivity.class, launcher);
    }

}
