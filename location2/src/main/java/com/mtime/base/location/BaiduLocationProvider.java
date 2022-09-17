package com.mtime.base.location;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.util.Map;

/**
 * Created by mtime on 2017/11/2.
 */

public class BaiduLocationProvider extends BaseLocationProvider {

    private final String TAG = "BaiduLocationProvider";
    private LocationClient mLocationClient;

    private final int MAX_TIME_OUT = 6000;

    private int mCount = 0;

    private final int MSG_CODE_CHECK_TIME_OUT = 1;

    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_CHECK_TIME_OUT:
                    if(mCount < 3){
                        mCount ++;
                        location();
                    } else {
                        mCount = 0;
                        onLocationFailure(null);
                    }
                    break;
            }
        }
    };

    @Override
    public synchronized void initLocation(Context context, Map<String, Object> options) {
        if (mLocationClient == null) {
            SDKInitializer.initialize(context);
            mLocationClient = new LocationClient(context);
            final LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true);
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll"); // 设置坐标类型
            option.setIsNeedAddress(true); // 需要地址信息
            option.setIsNeedLocationDescribe(true); // 需要位置描述信息

            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(new MyLocationListener());

            Log.d(TAG, "baidu location inited...");
        }
    }

    @Override
    public void startLocation(OnLocationProviderListener onLocationProviderListener) {
        if (mLocationClient == null) {
            throw new RuntimeException("please init location !");
        }
        super.startLocation(onLocationProviderListener);

        location();
    }

    private void location() {
        if(null != mLocationClient) {
            if(mLocationClient.isStarted()) {
                Log.d(TAG, "baidu request location...");
                mLocationClient.requestLocation();
            } else {
                mLocationClient.start();
            }
            mHandler.removeMessages(MSG_CODE_CHECK_TIME_OUT);
            mHandler.sendEmptyMessageDelayed(MSG_CODE_CHECK_TIME_OUT,MAX_TIME_OUT);
        }
    }

    @Override
    public void refreshLocation() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        }
    }

    @Override
    public void stop() {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.stop();
        }
        mHandler.removeMessages(MSG_CODE_CHECK_TIME_OUT);
    }

    private LocationInfo parseLocationInfo(BDLocation location) {
        LocationInfo locationInfo = new LocationInfo();
        locationInfo.setLongitude(location.getLongitude());
        locationInfo.setLatitude(location.getLatitude());
        locationInfo.setCity(location.getCity());
        // 基本的城市信息和位置描述信息
        locationInfo.locationDescribe = location.getLocationDescribe();
        locationInfo.addr = location.getAddrStr();
        locationInfo.country = location.getCountry();
        locationInfo.district = location.getDistrict();
        locationInfo.province = location.getProvince();
        locationInfo.street = location.getStreet();
        return locationInfo;
    }

    /**
     * getLocType
     * 61 ： GPS定位结果，GPS定位成功。
     * 62 ： 无法获取有效定位依据，定位失败，请检查运营商网络或者WiFi网络是否正常开启，尝试重新请求定位。
     * 63 ： 网络异常，没有成功向服务器发起请求，请确认当前测试手机网络是否通畅，尝试重新请求定位。
     * 65 ： 定位缓存的结果。
     * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果。
     * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果。
     * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果。
     * 161： 网络定位结果，网络定位成功。
     * 162： 请求串密文解析失败，一般是由于客户端SO文件加载失败造成，请严格参照开发指南或demo开发，放入对应SO文件。
     * 167： 服务端定位失败，请您检查是否禁用获取位置信息权限，尝试重新请求定位。
     * 502： AK参数错误，请按照说明文档重新申请AK。
     * 505：AK不存在或者非法，请按照说明文档重新申请AK。
     * 601： AK服务被开发者自己禁用，请按照说明文档重新申请AK。
     * 602： key mcode不匹配，您的AK配置过程中安全码设置有问题，请确保：SHA1正确，“;”分号是英文状态；且包名是您当前运行应用的包名，请按照说明文档重新申请AK。
     * 501～700：AK验证失败，请按照说明文档重新申请AK。
     */
    private class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            int errorCode = location.getLocType();

            Log.i(TAG, "latitude="+latitude+" longitude="+longitude+" radius="+radius+" coorType="+coorType+" errorCode="+errorCode);

            if (errorCode != 61 && errorCode != 161) {
                Log.d(TAG, "baidu location failure...");
                onLocationFailure(location);
                return;
            }

            Log.d(TAG, "baidu location success location..." + System.currentTimeMillis());
            onLocationSuccess(location);
            Log.d(TAG, "baidu location success callback...");
            mCount = 0;
        }
    }

    private void onLocationFailure(BDLocation location) {
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        }
        if (mOnLocationProviderListener != null) {
            LocationException exception = new LocationException(null == location ? LocationException.CODE_UNKNOWN : location.getLocType(), "定位失败");
            mOnLocationProviderListener.onProviderException(exception);
        }
    }

    private void onLocationSuccess(BDLocation location) {
        stop();//停止定位服务，节省资源开支
        LocationInfo originalLocationInfo = parseLocationInfo(location);
        if(mOnLocationProviderListener != null) {
            mOnLocationProviderListener.onProviderLocationSuccess(originalLocationInfo);
        }
    }
}
