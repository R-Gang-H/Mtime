package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.common.utils.DateUtil;

public class ETicketDetailBean  extends MBaseBean {
    
    private long createTime; // 订单创建时间,
    private int orderStatus; // 订单状态[待定],
    private String commodityName; // 电子券商品名称,
    private long startTime; // 有效期开始时间
    private long endTime; // 有效期结束时间,
    private int quantity; // 券数量,
    private int price; // 结算单价,单位是分
    private double deductedAmount; // 抵扣金额，单位分
    private double salesAmount; // 主订单销售金额，单位分
    private int salePrice; // 销售单价(结算单价+服务费),单位是分
    private int amount; // 销售总价,单位是分
    private int cPrice; // 总价,单位是分
    private String electronicCode; // 兑换码
    private int remainQuantity; // 剩余数量,如果剩余数量为0表示码已经全部兑换完毕
    private String mobile; // 手机号,
    private String cinemaId; // 影院Id,
    private String cinemaName; // 影院名称,
    private String cAddress; // 影院地址,
    private String ctel; // 影院电话,
    private String longitude; // 35.39746, //Mtime影院地址经度
    private String latitude; // Mtime影院地址纬度
    private String desc; // ”备注”
    private int serviceFee; // 服务费
    private int subOrderStatus; // 子订单状态，
    
    private double baiduLongitude; //百度经度
    private double baiduLatitude;  //百度维度
    /**
     * @return the orderId
     */
    public String getOrderId() {
        return orderId;
    }
    
    /**
     * @param orderId
     *            the orderId to set
     */
    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }
    
    /**
     * @return the createTime
     */
    public String getCreateTime() {
        return DateUtil.getLongToDate(DateUtil.sdf6, createTime);
    }
    
    public long getCreateTimelong() {
        return createTime;
    }
    
    /**
     * @param createTime
     *            the createTime to set
     */
    public void setCreateTime(final long createTime) {
        this.createTime = createTime;
    }
    
    /**
     * @return the orderStatus
     */
    public int getOrderStatus() {
        return orderStatus;
    }
    
    /**
     * @param orderStatus
     *            the orderStatus to set
     */
    public void setOrderStatus(final int orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    /**
     * @return the commodityName
     */
    public String getCommodityName() {
        return commodityName;
    }
    
    /**
     * @param commodityName
     *            the commodityName to set
     */
    public void setCommodityName(final String commodityName) {
        this.commodityName = commodityName;
    }
    
    /**
     * @return the mStartTime
     */
    public String getStartTime() {
        return DateUtil.getLongToDate(DateUtil.sdf8, startTime);
    }
    
    /**
     * @param startTime
     *            the mStartTime to set
     */
    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }
    
    public long getPayStartTime() {
        return startTime;
    }
    
    public long getPayEndTime() {
        return endTime;
    }
    
    public String getEndTime() {
        return DateUtil.getLongToDate(DateUtil.sdf8, endTime);
    }
    
    /**
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }
    
    /**
     * @return the quantity
     */
    public int getQuantity() {
        return quantity;
    }
    
    /**
     * @param quantity
     *            the quantity to set
     */
    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }
    
    /**
     * @return the price
     */
    public int getPrice() {
        return price;
    }
    
    /**
     * @param price
     *            the price to set
     */
    public void setPrice(final int price) {
        this.price = price;
    }
    
    public double getDeductedAmount() {
        return deductedAmount;
    }
    
    public void setDeductedAmount(final double deductedAmount) {
        this.deductedAmount = deductedAmount;
    }
    
    public double getSalesAmount() {
        return salesAmount;
    }
    
    public void setSalesAmount(final double salesAmount) {
        this.salesAmount = salesAmount;
    }
    
    /**
     * @return the salePrice
     */
    public int getSalePrice() {
        return salePrice;
    }
    
    /**
     * @param salePrice
     *            the salePrice to set
     */
    public void setSalePrice(final int salePrice) {
        this.salePrice = salePrice;
    }
    
    /**
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * @param amount
     *            the amount to set
     */
    public void setAmount(final int amount) {
        this.amount = amount;
    }
    
    /**
     * @return the electronicCode
     */
    public String getElectronicCode() {
        return electronicCode;
    }
    
    /**
     * @param electronicCode
     *            the electronicCode to set
     */
    public void setElectronicCode(final String electronicCode) {
        this.electronicCode = electronicCode;
    }
    
    /**
     * @return the remainQuantity
     */
    public int getRemainQuantity() {
        return remainQuantity;
    }
    
    /**
     * @param remainQuantity
     *            the remainQuantity to set
     */
    public void setRemainQuantity(final int remainQuantity) {
        this.remainQuantity = remainQuantity;
    }
    
    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }
    
    /**
     * @param mobile
     *            the mobile to set
     */
    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }
    
    /**
     * @return the cinemaId
     */
    public String getCinemaId() {
        return cinemaId;
    }
    
    /**
     * @param cinemaId
     *            the cinemaId to set
     */
    public void setCinemaId(final String cinemaId) {
        this.cinemaId = cinemaId;
    }
    
    /**
     * @return the cinemaName
     */
    public String getCinemaName() {
        return cinemaName;
    }
    
    /**
     * @param cinemaName
     *            the cinemaName to set
     */
    public void setCinemaName(final String cinemaName) {
        this.cinemaName = cinemaName;
    }
    
    /**
     * @return the cAddress
     */
    public String getcAddress() {
        return cAddress;
    }
    
    /**
     * @param cAddress
     *            the cAddress to set
     */
    public void setcAddress(final String cAddress) {
        this.cAddress = cAddress;
    }
    
    /**
     * @return the ctel
     */
    public String getTel() {
        return ctel;
    }
    
    /**
     * @param ctel
     *            the ctel to set
     */
    public void setTel(final String ctel) {
        this.ctel = ctel;
    }
    
    /**
     * @return the longitude
     */
    public String getLongitude() {
        return longitude;
    }
    
    public double getLongitudeDouble() {
        double lon = 0.0;
        try {
            lon = Double.parseDouble(longitude);
        }
        catch (final Exception e) {
        }
        return lon;
    }
    
    /**
     * @param longitude
     *            the longitude to set
     */
    public void setLongitude(final String longitude) {
        this.longitude = longitude;
    }
    
    /**
     * @return the latitude
     */
    public String getLatitude() {
        return latitude;
    }
    
    public double getLatitudeDouble() {
        double lat = 0.0;
        try {
            lat = Double.parseDouble(latitude);
        }
        catch (final Exception e) {
        }
        return lat;
    }
    
    /**
     * @param latitude
     *            the latitude to set
     */
    public void setLatitude(final String latitude) {
        this.latitude = latitude;
    }
    
    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }
    
    /**
     * @param desc
     *            the desc to set
     */
    public void setDesc(final String desc) {
        this.desc = desc;
    }
    
    public String getCtel() {
        return ctel;
    }
    
    public void setCtel(final String ctel) {
        this.ctel = ctel;
    }
    
    public int getcPrice() {
        return cPrice;
    }
    
    public void setcPrice(final int cPrice) {
        this.cPrice = cPrice;
    }
    
    public int getServiceFee() {
        return serviceFee;
    }
    
    public void setServiceFee(final int serviceFee) {
        this.serviceFee = serviceFee;
    }
    
    private String orderId = null;
    private String subOrderId;
    
    public String getSubOrderId() {
        return subOrderId;
    }
    
    public void setSubOrderId(final String subOrderId) {
        this.subOrderId = subOrderId;
    }
    
    public int getSubOrderStatus() {
        return subOrderStatus;
    }
    
    public void setSubOrderStatus(final int subOrderStatus) {
        this.subOrderStatus = subOrderStatus;
    }
    
    public String getCommodityId() {
        return commodityId;
    }
    
    public void setCommodityId(final String commodityId) {
        this.commodityId = commodityId;
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

    private String commodityId; // 电子券商品Id
}
