package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.kk.taurus.uiframe.i.HolderData;
import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.DlgRedPacketBean;
import com.mtime.bussiness.mine.bean.MemberRewards;
import com.mtime.common.utils.DateUtil;

import java.text.DecimalFormat;
import java.util.List;

public class TicketDetailBean extends MBaseBean implements HolderData {
    private String orderId;
    private String onlineTime;
    private String subOrderId;
    private String movieId;
    private String movieTitle;

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    private int movieLength;
    private OrderMovieInfo movie;
    //private String              onlineTime;
    private double deductedAmount;           // 抵扣金额，单位分
    private double salesAmount;              // 主订单销售金额，单位分
    private int quantity;       // 数量，票的数量。
    private double price;
    private double serviceFee;
    private double salePrice;   // 销售单价(结算单价+服务费)
    private long amount;        // 1100, 总价（销售单价*数量）
    private long createTime;
    private int orderStatus;
     private int payStatus;     //支付状态，0-未支付，1-已支付
    private int refundStatus;   //退款状态，0-未退款，1-申请退款，2-已退款
    private long showtime;
    private String hallName;
    private String sateName;
    private String language;
    private int version;
    private String versionDesc;
    private String changeInfo;               // 兑换说明
    private boolean isNewElectronicCode;
    private List<NeoElectronicCodeBean> neoElectronicCode;
    private String qrcode;
    private String prompt;
    // private String              externalOrderId;
    // private String              changeMobile;
    private String mobile;
    private String cname;
    private String cinemaId;
    // private String              cityId;
    private String cAddress;
    private String ctel;
    private double longitude;
    private double latitude;
    private double baiduLongitude; //百度经度
    private double baiduLatitude;  //百度维度
    private String desc;
    private OrderCinemaInfo cinemaFeature;
    private String showtimeId;               // 即之前的dId（场次id）
    private long payEndTime;
    private boolean isReSelectSeat;           // 是否需要重新选座
    // private boolean             hasRedPacket;
    private DlgRedPacketBean redPacket;
    private MemberRewards memberRewards;

    private String electronicCode;
    private long reSelectSeatEndTimeSecond; // 重新选座截止时间（单位：秒）,如果不需要重新选座，不返回该字段
    private List<CommodityList> buffetList;


    //----------------------201805影院直销新增！！-------------------------
    /**
     * "dsOrderNo":"aaaaaaaa", //第三方直销平台的订单号。原有的OrderId字段是数字型，为了兼容性，采用String型。
     * "directSalesFlag":0, //是否是第三方直销订单  0：时光 1：第三方
     * "dsPlatformId":1, //第三方直销平台的ID
     * "dsPlatformLogo":"xxxxx",  // 第三方直销平台的LOGO！（应该是一个图片链接，这里只显示万达logo标识，时光网的标识是客户端取自本地  ）
     * "dsCustomerService":"xxxxxx" //客服电话
     * "dsWithGoods"：0 //第三方订单中是否含有卖品  0：没有 1：有
     * "dsNotice":"啊啊啊啊啊" //后台可配置的第三方的直销购票说明
     */
    private String dsOrderNo;
    private int directSalesFlag;
    private int dsPlatformId;
    private String dsPlatformLogo;
    private String dsPlatformName;
    private String dsCustomerServiceLabel;
    private String dsCustomerService;
    private int dsWithGoods;
    private String dsNotice;

    public String getDsOrderNo() {
        return dsOrderNo;
    }

    public void setDsOrderNo(String dsOrderNo) {
        this.dsOrderNo = dsOrderNo;
    }

    public int getDirectSalesFlag() {
        return directSalesFlag;
    }

    public void setDirectSalesFlag(int directSalesFlag) {
        this.directSalesFlag = directSalesFlag;
    }

    public int getDsPlatformId() {
        return dsPlatformId;
    }

    public void setDsPlatformId(int dsPlatformId) {
        this.dsPlatformId = dsPlatformId;
    }

    public String getDsPlatformLogo() {
        return dsPlatformLogo;
    }

    public void setDsPlatformLogo(String dsPlatformLogo) {
        this.dsPlatformLogo = dsPlatformLogo;
    }

    public String getDsPlatformName() {
        return dsPlatformName;
    }

    public void setDsPlatformName(String dsPlatformName) {
        this.dsPlatformName = dsPlatformName;
    }

    public String getDsCustomerServiceLabel() {
        return dsCustomerServiceLabel;
    }

    public void setDsCustomerServiceLabel(String dsCustomerServiceLabel) {
        this.dsCustomerServiceLabel = dsCustomerServiceLabel;
    }

    public String getDsCustomerService() {
        return dsCustomerService;
    }

    public void setDsCustomerService(String dsCustomerService) {
        this.dsCustomerService = dsCustomerService;
    }

    public int getDsWithGoods() {
        return dsWithGoods;
    }

    public void setDsWithGoods(int dsWithGoods) {
        this.dsWithGoods = dsWithGoods;
    }

    public String getDsNotice() {
        return dsNotice;
    }

    public void setDsNotice(String dsNotice) {
        this.dsNotice = dsNotice;
    }

    public boolean isLegalNeoElectronicCode() {
        if (neoElectronicCode != null && neoElectronicCode.size() > 0) {
            NeoElectronicCodeBean bean = neoElectronicCode.get(0);
            return !TextUtils.isEmpty(bean.Value);
        }
        return false;
    }
    //----------------------201805影院直销新增！！-------------------------


    public double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public MemberRewards getMemberRewards() {
        return memberRewards;
    }

    public void setMemberRewards(MemberRewards memberRewards) {
        this.memberRewards = memberRewards;
    }


    public static class NeoElectronicCodeBean extends MBaseBean {
        private String Name;
        private String Value;

        public String getName() {
            return Name;
        }

        public void setName(String name) {
            this.Name = name;
        }

        public String getValue() {
            return Value;
        }

        public void setValue(String value) {
            this.Value = value;
        }
    }

    public List<NeoElectronicCodeBean> getNeoElectronicCode() {
        return neoElectronicCode;
    }

    public void setNeoElectronicCode(List<NeoElectronicCodeBean> neoElectronicCode) {
        this.neoElectronicCode = neoElectronicCode;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public boolean isNewElectronicCode() {
        return isNewElectronicCode;
    }

    public void setNewElectronicCode(boolean newElectronicCode) {
        isNewElectronicCode = newElectronicCode;
    }

    public DlgRedPacketBean getRedPacket() {
        return redPacket;
    }

    public void setRedPacket(DlgRedPacketBean redPacket) {
        this.redPacket = redPacket;
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(final int version) {
        this.version = version;
    }

    /**
     * @return the versionDesc
     */
    public String getVersionDesc() {
        return versionDesc;
    }

    /**
     * @param versionDesc the versionDesc to set
     */
    public void setVersionDesc(final String versionDesc) {
        this.versionDesc = versionDesc;
    }

    /**
     * @return the changeInfo
     */
    public String getChangeInfo() {
        return changeInfo;
    }

    /**
     * @param changeInfo the changeInfo to set
     */
    public void setChangeInfo(final String changeInfo) {
        this.changeInfo = changeInfo;
    }

    /**
     * @return the language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language the language to set
     */
    public void setLanguage(final String language) {
        this.language = language;
    }

    public OrderCinemaInfo getCinemaFeature() {
        return cinemaFeature;
    }

    public void setCinemaFeature(OrderCinemaInfo cinemaFeature) {
        this.cinemaFeature = cinemaFeature;
    }

    public void setMovieId(final String id) {
        movieId = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public String getSubOrderId() {
        return subOrderId;
    }

    public void setSubOrderId(final String subOrderId) {
        this.subOrderId = subOrderId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(final String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
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

    public double getPrice() {
        return price / 100;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public double getSalePrice() {
        return salePrice / 100;
    }

    public void setSalePrice(final double salePrice) {
        this.salePrice = salePrice;
    }

    public long getAmount() {
        return amount / 100;
    }

    public String getDoubleAmount() {
        if ((amount / 100) == (amount * 1.0f / 100)) {
            return String.valueOf(amount / 100);
        }
        double am = amount * 1.0f / 100;
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(am);
    }

    public void setAmount(final long amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return DateUtil.getLongToDate(DateUtil.sdf6, createTime);
    }

    public long getCreateTimelong() {
        return createTime;
    }

    public void setCreateTime(final long createTime) {
        this.createTime = createTime;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
    }

    public long getShowtimeLong() {
        return showtime;
    }

    public String getShowtime() {
        return DateUtil.getLongToDate(DateUtil.sdf13, showtime);
    }

    public long getOriginShowTime() {
        return showtime;
    }

    public void setShowtime(final long showtime) {
        this.showtime = showtime;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(final String hallName) {
        this.hallName = hallName;
    }

    public String getSeatName() {
        return sateName;
    }

    public void setSeatName(final String sateName) {
        this.sateName = sateName;
    }

    public String getElectronicCode() {
        return electronicCode;
    }

    public void setElectronicCode(final String electronicCode) {
        this.electronicCode = electronicCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(final String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return cname;
    }

    public void setName(final String cname) {
        this.cname = cname;
    }

    public String getAddress() {
        return cAddress;
    }

    public void setAddress(final String cAddress) {
        this.cAddress = cAddress;
    }

    public String getTelphone() {
        return ctel;
    }

    public void setTelphone(final String ctel) {
        this.ctel = ctel;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }

    /**
     * 获取场次id(即之前的dId)
     */
    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(final String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public long getPayEndTime()

    {
        return payEndTime;
    }

    public void setPayEndTime(final long payEndTime) {
        this.payEndTime = payEndTime;
    }

    public String getcAddress() {
        return cAddress;
    }

    public void setcAddress(final String cAddress) {
        this.cAddress = cAddress;
    }

    public String getCtel() {
        return ctel;
    }

    public void setCtel(final String ctel) {
        this.ctel = ctel;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(final String cname) {
        this.cname = cname;
    }

    public boolean isReSelectSeat() {
        return isReSelectSeat;
    }

    public void setReSelectSeat(final boolean isReSelectSeat) {
        this.isReSelectSeat = isReSelectSeat;
    }

    public long getReSelectSeatEndTimeSecond() {
        return reSelectSeatEndTimeSecond;
    }

    public void setReSelectSeatEndTimeSecond(final long reSelectSeatEndTimeSecond) {
        this.reSelectSeatEndTimeSecond = reSelectSeatEndTimeSecond;
    }

    public int getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(final int movieLength) {
        this.movieLength = movieLength;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(final String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public List<CommodityList> getBuffetList() {
        return buffetList;
    }

    public void setBuffetList(List<CommodityList> buffetList) {
        this.buffetList = buffetList;
    }

    public OrderMovieInfo getMovie() {
        return movie;
    }

    public void setMovie(OrderMovieInfo movie) {
        this.movie = movie;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
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

    public int getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(int refundStatus) {
        this.refundStatus = refundStatus;
    }
}
