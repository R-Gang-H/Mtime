package com.mtime.base.location;

import android.text.TextUtils;

/**
 * Created by mtime on 2017/10/30.
 */

public class LocationInfo implements Cloneable{
    public String locationDescribe;     // 百度定位到的-位置描述信息
    public String addr;                 // 百度定位到的-详细地址信息
    public String country;              // 百度定位到的-国家
    public String province;             // 百度定位到的-省份
    public String district;             // 百度定位到的-区县
    public String street;               // 百度定位到的-街道信息

    private Double longitude = 0d;      // 百度定位到的-经度
    private Double latitude = 0d;       // 百度定位到的-纬度
    private String city;                // 百度定位到的-城市

    private String cityId;              // 用户使用中的城市ID
    private String cityName;            // 用户使用中的城市名称
    private String locationCityId;      // 根据真正定位坐标获取到的id (mtime api返回)
    private String locationCityName;    // 根据真正定位坐标获取到的name (mtime api返回)

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCityName() {
        return cityName;
    }
    
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
    
    public String getLocationCityId() {
        return locationCityId;
    }
    
    public void setLocationCityId(String locationCityId) {
        this.locationCityId = locationCityId;
    }
    
    public String getLocationCityName() {
        return locationCityName;
    }
    
    public void setLocationCityName(String locationCityName) {
        this.locationCityName = locationCityName;
    }

    public String getLocationDescribe() {
        return locationDescribe;
    }

    public void setLocationDescribe(String locationDescribe) {
        this.locationDescribe = locationDescribe;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean isLose() {
        return 0 == latitude || 0 == longitude;
    }
    
    /**
     * 使用中的城市与定位的城市是否不一致
     * @return
     */
    public boolean isChangeCity() {
        boolean isEmpty = TextUtils.isEmpty(locationCityName) || TextUtils.isEmpty(locationCityId);
        return !isEmpty && !TextUtils.equals(cityId, locationCityId) && !TextUtils.equals(cityName, locationCityName);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("longitude : ").append(longitude).append("\n");
        sb.append("latitude : ").append(latitude).append("\n");
        sb.append("city : ").append(city).append("\n");
        sb.append("cityId : ").append(cityId).append("\n");
        sb.append("cityName : ").append(cityName).append("\n");
        sb.append("locationCityId : ").append(locationCityId).append("\n");
        sb.append("locationCityName : ").append(locationCityName).append("\n");
        return sb.toString();
    }
    
    @Override
    public LocationInfo clone() {
        try {
            return (LocationInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
