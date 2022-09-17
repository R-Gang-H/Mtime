package com.mtime.bussiness.main.maindialog.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.BeanTypeInterface;

/**
 * 未使用的电影票
 */
public class UnusedTicketItemBean extends MBaseBean implements BeanTypeInterface, Parcelable {
    int type = BeanTypeInterface.TYPE_TICKET;
    
    private String orderId;
    private String cinemaId;
    private String movieName;
    private String imageUrl;
    private int quantity;
    private long showtime;
    private double latitude;
    private String cinemaName;
    private double distance;
    private boolean showOutDate;
    
    private double baiduLongitude; //百度经度
    private double baiduLatitude;  //百度维度
    
    public void setType(final int t) {
        type = t;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }
    
    public String getCinemaId() {
        return cinemaId;
    }
    
    public void setCinemaId(final String cinemaId) {
        this.cinemaId = cinemaId;
    }
    
    public String getMovieName() {
        return movieName;
    }
    
    public void setMovieName(final String movieName) {
        this.movieName = movieName;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
    
    public long getShowtime() {
        return showtime;
    }
    
    public void setShowtime(final long showtime) {
        this.showtime = showtime;
    }
    
    public double getDistance() {
        return distance;
    }
    
    public void setDistance(final double distance) {
        this.distance = distance;
    }
    
    public String getCinemaName() {
        return cinemaName;
    }
    
    public void setCinemaName(final String cinemaName) {
        this.cinemaName = cinemaName;
    }
    
    public double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }
    
    public double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }
    
    private double longitude;
    
    @Override
    public int getBeanType() {
        return type;
    }
    
    public boolean getShowOutDate() {
        return showOutDate;
    }
    
    public void setShowOutDate(final boolean showOutDate) {
        this.showOutDate = showOutDate;
    }

    public double getBaiduLongitude() {
        return baiduLongitude;
    }

    public void setBaiduLongitude(double baiduLongitude) {
        this.baiduLongitude = baiduLongitude;
    }

    public double getBaiduLatitude() {
        return baiduLatitude;
    }

    public void setBaiduLatitude(double baiduLatitude) {
        this.baiduLatitude = baiduLatitude;
    }
    
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.type);
        dest.writeString(this.orderId);
        dest.writeString(this.cinemaId);
        dest.writeString(this.movieName);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.quantity);
        dest.writeLong(this.showtime);
        dest.writeDouble(this.latitude);
        dest.writeString(this.cinemaName);
        dest.writeDouble(this.distance);
        dest.writeByte(this.showOutDate ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.baiduLongitude);
        dest.writeDouble(this.baiduLatitude);
        dest.writeDouble(this.longitude);
    }
    
    public UnusedTicketItemBean() {
    }
    
    protected UnusedTicketItemBean(Parcel in) {
        this.type = in.readInt();
        this.orderId = in.readString();
        this.cinemaId = in.readString();
        this.movieName = in.readString();
        this.imageUrl = in.readString();
        this.quantity = in.readInt();
        this.showtime = in.readLong();
        this.latitude = in.readDouble();
        this.cinemaName = in.readString();
        this.distance = in.readDouble();
        this.showOutDate = in.readByte() != 0;
        this.baiduLongitude = in.readDouble();
        this.baiduLatitude = in.readDouble();
        this.longitude = in.readDouble();
    }
    
    public static final Parcelable.Creator<UnusedTicketItemBean> CREATOR = new Parcelable.Creator<UnusedTicketItemBean>() {
        @Override
        public UnusedTicketItemBean createFromParcel(Parcel source) {
            return new UnusedTicketItemBean(source);
        }
        
        @Override
        public UnusedTicketItemBean[] newArray(int size) {
            return new UnusedTicketItemBean[size];
        }
    };
}
