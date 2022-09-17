package com.mtime.bussiness.ticket.movie.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.Provider;

import java.util.ArrayList;
import java.util.List;

/**
 * 选座信息
 */
public class SeatInfoJsonBean extends MBaseBean {
    
    private String                error;
    private int                   movieLength;    // 影片时长 ，单位分
    private long                  providerId;     // 本场次的供应商id
    private String                supplierId;     // 本场次的供应商id
    private boolean               isSale;         // 是否可售
    private double                mtimeSellPrice; // 时光网销售价格,单位是分
    private String                price;          // 参考价
    private String                cinemaName;     // 影院名称
    private String                movieName;      // 影片名称
    private String                hallName;       // 影厅名,
    private String                versionDesc;    // 版本,
                                                   
    private String                language;       // 语言,
    private double                salePrice;      // 销售价,单位分
    private double                serviceFee;     // 服务费,单位分
    private long                  realTime;       // 放映时间，格式：135123635241
    private String                orderId;        // 未支付的订单id，没有返回0
    private String                subOrderID;     // 子订单
    private String                orderMsg;       // 未支付的提示信息
    private String                hallSpecialDes;  // 影厅特殊说明
    private Long                  movieId;
    private String                cinemaId;
    private List<SeatRowNameInfo> rowNameList;      // 座位排位信息， 1,2,3... or A,B,C...
    
    private List<Provider>        provider;       // 供应商
    private List<Seat>            seat;           // 座位信息
    private int                   remainSeat;     // 剩余可选座位
    private int                   seatRowCount;   // 影院排数
    private int                   seatColumnCount; // 影院列数
    private boolean               isAutoSelected;// 是否支持自动选座
    private String                dateMessage;// 给用户的傻瓜时间提示

    private List<String>          orderExplains = new ArrayList<String>();// 确认订单页提示

    private List<SalePriceBean>   salePriceList = new ArrayList<SalePriceBean>();//销售金额列表

    public List<SeatRowNameInfo> getRowNameList() {
        return rowNameList;
    }
    
    public void setRowNameList(List<SeatRowNameInfo> rowNameList) {
        this.rowNameList = rowNameList;
    }
    
    public long getProviderId() {
        return providerId;
    }
    
    public void setProviderId(final long providerId) {
        this.providerId = providerId;
    }
    
    public boolean isSale() {
        return isSale;
    }
    
    public void setSale(final boolean isSale) {
        this.isSale = isSale;
    }
    
    public double getMtimeSellPrice() {
        return mtimeSellPrice;
    }
    
    public void setMtimeSellPrice(final double mtimeSellPrice) {
        this.mtimeSellPrice = mtimeSellPrice;
    }
    
    public String getPrice() {
        if (price == null) {
            return "";
        }
        return price;
    }
    
    public void setPrice(final String price) {
        this.price = price;
    }
    
    public String getCinemaName() {
        return cinemaName;
    }
    
    public void setCinemaName(final String cinemaName) {
        this.cinemaName = cinemaName;
    }
    
    public String getMovieName() {
        return movieName;
    }
    
    public void setMovieName(final String movieName) {
        this.movieName = movieName;
    }
    
    public String getHallName() {
        return hallName;
    }
    
    public void setHallName(final String hallName) {
        this.hallName = hallName;
    }
    
    public String getVersionDesc() {
        return versionDesc;
    }
    
    public void setVersionDesc(final String versionDesc) {
        this.versionDesc = versionDesc;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public double getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(final double salePrice) {
        this.salePrice = salePrice;
    }
    
    public double getServiceFee() {
        return serviceFee;
    }
    
    public void setServiceFee(final double serviceFee) {
        this.serviceFee = serviceFee;
    }
    
    public long getRealTime() {
        return realTime;
    }
    
    public void setRealTime(final long realTime) {
        this.realTime = realTime;
    }
    
    public List<Provider> getProvider() {
        return provider;
    }
    
    public void setProvider(final List<Provider> provider) {
        this.provider = provider;
    }
    
    public List<Seat> getSeat() {
        return seat;
    }
    
    public void setSeat(final List<Seat> seat) {
        this.seat = seat;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(final String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }
    
    public String getSubOrderID() {
        return subOrderID;
    }
    
    public void setSubOrderID(final String subOrderID) {
        this.subOrderID = subOrderID;
    }
    
    public String getHallSpecialDes() {
        return hallSpecialDes;
    }
    
    public void setHallSpecialDes(String hallSpecialDes) {
        this.hallSpecialDes = hallSpecialDes;
    }
    
    public Long getMovieId() {
        return movieId;
    }
    
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    
    public String getCinemaId() {
        return cinemaId;
    }
    
    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }
    
    public String getError() {
        return error;
    }
    
    public void setError(String error) {
        this.error = error;
    }
    
    public int getMovieLength() {
        return movieLength;
    }
    
    public void setMovieLength(int movieLength) {
        this.movieLength = movieLength;
    }
    
    public String getOrderMsg() {
        return orderMsg;
    }
    
    public void setOrderMsg(String orderMsg) {
        this.orderMsg = orderMsg;
    }
    
    public int getRemainSeat() {
        return remainSeat;
    }
    
    public void setRemainSeat(int remainSeat) {
        this.remainSeat = remainSeat;
    }
    
    public int getSeatRowCount() {
        return seatRowCount;
    }
    
    public void setSeatRowCount(int seatRowCount) {
        this.seatRowCount = seatRowCount;
    }
    
    public int getSeatColumnCount() {
        return seatColumnCount;
    }
    
    public void setSeatColumnCount(int seatColumnCount) {
        this.seatColumnCount = seatColumnCount;
    }


    public List<SalePriceBean> getSalePriceList() {
        return salePriceList;
    }

    public void setSalePriceList(List<SalePriceBean> salePriceList) {
        this.salePriceList = salePriceList;
    }

    public boolean isAutoSelected() {
        return isAutoSelected;
    }

    public void setIsAutoSelected(boolean isAutoSelected) {
        this.isAutoSelected = isAutoSelected;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public List<String> getOrderExplains() {
        if (orderExplains == null){
            return new ArrayList<String>();
        }
        return orderExplains;
    }

    public void setOrderExplains(List<String> orderExplains) {
        this.orderExplains = orderExplains;
    }
}
