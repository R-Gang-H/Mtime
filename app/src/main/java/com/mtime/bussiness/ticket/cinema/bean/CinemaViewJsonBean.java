package com.mtime.bussiness.ticket.cinema.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class CinemaViewJsonBean extends MBaseBean {
    
    private String                  mobileImage;
    private String                  image;
    private String                  name;
    private String                  address;
    private double                  rating;

    private String                  licenceButtonLabel;
    private String                  licenceImgUrl;
    
    private String                  tel;
    private double                  baiduLongitude;
    private double                  baiduLatitude;
    private int                     hallCount;
    private double                  qualityRating;
    private double                  serviceRating;
    private String                  open;

    private Coupon coupon;
    private boolean                 isTicket;
    private List<CinemaViewEticket> etickets;
    
    private CinemaViewFeature feature;
    private int                     galleryTotalCount;
    private String                  createMembershipCardUrl;
    private List<BranchCinemasBean> branchCinemas;
    private String rectifyMessage;

    public String getRectifyMessage() {
        return rectifyMessage;
    }

    public void setRectifyMessage(String rectifyMessage) {
        this.rectifyMessage = rectifyMessage;
    }

    public String getImage() {
        return image;
    }
    
    public void setImage(final String image) {
        this.image = image;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(final String address) {
        this.address = address;
    }
    
    public double getRating() {
        return rating;
    }

    public String getLicenceButtonLabel() {
        return licenceButtonLabel;
    }

    public void setLicenceButtonLabel(String licenceButtonLabel) {
        this.licenceButtonLabel = licenceButtonLabel;
    }

    public String getLicenceImgUrl() {
        return licenceImgUrl;
    }

    public void setLicenceImgUrl(String licenceImgUrl) {
        this.licenceImgUrl = licenceImgUrl;
    }

    public void setRating(final double rating) {
        this.rating = rating;
    }
    
    public String getTel() {
        return tel;
    }
    
    public void setTel(final String tel) {
        this.tel = tel;
    }
    
    /** 返回影院的第一个电话号码（影院可能有多个电话号码） */
    public String getFirstPhoneNumber() {
        if (!TextUtils.isEmpty(tel)) {
            
            final String[] phones = tel.split("[\\s/、,，；;（）()]");
            if (null != phones && phones.length > 0) {
                return phones[0];
            }
        }
        
        return tel;
    }
    
    public double getQualityRating() {
        return qualityRating;
    }
    
    public void setQualityRating(final double qualityRating) {
        this.qualityRating = qualityRating;
    }
    
    public double getServiceRating() {
        return serviceRating;
    }
    
    public void setServiceRating(final double serviceRating) {
        this.serviceRating = serviceRating;
    }
    
    public String getOpen() {
        return open;
    }
    
    public void setOpen(final String open) {
        this.open = open;
    }
    
    public Coupon getCoupon() {
        return coupon;
    }
    
    public void setCoupon(final Coupon coupon) {
        this.coupon = coupon;
    }
    
    public List<CinemaViewEticket> getEtickets() {
        return etickets;
    }
    
    public void setEtickets(final List<CinemaViewEticket> etickets) {
        this.etickets = etickets;
    }
    
    public CinemaViewFeature getFeature() {
        return feature;
    }
    
    public void setFeature(final CinemaViewFeature feature) {
        this.feature = feature;
    }
    
    public String getCreateMembershipCardUrl() {
        return createMembershipCardUrl;
    }
    
    public void setCreateMembershipCardUrl(String createMembershipCardUrl) {
        this.createMembershipCardUrl = createMembershipCardUrl;
    }
    
    public String getMobileImage() {
        return mobileImage;
    }
    
    public void setMobileImage(String mobileImage) {
        this.mobileImage = mobileImage;
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
    
    public int getHallCount() {
        return hallCount;
    }
    
    public void setHallCount(int hallCount) {
        this.hallCount = hallCount;
    }
    
    public boolean isTicket() {
        return isTicket;
    }
    
    public void setTicket(boolean isTicket) {
        this.isTicket = isTicket;
    }
    
    public List<BranchCinemasBean> getBranchCinemas() {
        return branchCinemas;
    }
    
    public void setBranchCinemas(List<BranchCinemasBean> branchCinemas) {
        this.branchCinemas = branchCinemas;
    }
    
    public int getGalleryTotalCount() {
        return galleryTotalCount;
    }
    
    public void setGalleryTotalCount(int galleryTotalCount) {
        this.galleryTotalCount = galleryTotalCount;
    }
    
}