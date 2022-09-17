package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.bean.Provider;
import com.mtime.common.utils.LogWriter;

import java.io.Serializable;
import java.util.List;

/**
 * 影院--影讯信息
 *
 * @author ye
 */
public class ShowtimeJsonBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = -4909106624139456076L;
    private int sid;
    private long showDay;
    private boolean isMovies; // true
    // 表示连映，false
    // 反之
    private int version;
    private String versionDesc;
    private String language;
    private int length;
    private String price; // 此字段需定义为String类型，不然服务端返回price数据为""时会崩溃
    private double cinemaPrice;
    private double salePrice;
    private double activityPrice;
    private boolean isCoupon;
    private String hall;
    private int hallID;
    private long endTime; // 停售时间，如：1351672200
    private long startTime; // 开售时间，如：1351672200
    private boolean isVaildTicket; // 购票是否有效，true该段时间购票有效，false 反之。

    private boolean isTicket;
    private List<Provider> providers;
    private int isSeatLess;
    private String seatSalesTip;
    private List<Provider> provider;
    private int ticketStatus; //0:可购票  1:不可购票(包括满座和停售)
    private String ticketText;//按钮文案， 目前需求上有  购票/满座/停售  三个文案。接口判断该显示哪个文案返回
    private String hallSize;//影厅规模
    private String effect;//影厅特效
    private String capacity;

    public int getSid() {
        return sid;
    }

    public void setSid(final int sid) {
        this.sid = sid;
    }

    public long getShowDay() {
        return showDay;
    }

    public void setShowDay(final long showDay) {
        this.showDay = showDay;
    }

    public boolean isMovies() {
        return isMovies;
    }

    public void setMovies(final boolean isMovies) {
        this.isMovies = isMovies;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(final int version) {
        this.version = version;
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

    /**
     * 返回"2D/中文版"格式样式的字符串 如果language为空，则返回"2D"格式样式字符串
     *
     * @return
     */
    public String getVersionLanguage() {
        final StringBuilder result = new StringBuilder();
        if ((versionDesc != null) && (versionDesc.trim().length() > 0)) {
            result.append(versionDesc);
        }
        if ((language != null) && (language.trim().length() > 0)) {
            if (result.length() > 0) {
                result.append("/").append(language);
            } else {
                result.append(language);
            }
        }
        return result.toString();
    }

    public int getLength() {
        return length;
    }

    public void setLength(final int length) {
        this.length = length;
    }

    public String getPrice() {
        return price;
    }

    public double getPriceDouble() {
        try {
            return Double.parseDouble(price);
        } catch (final Exception e) {
            LogWriter.e("MTIME", "价格" + price + "转换错误! " + e.getMessage());
            return -1;
        }
    }

    public void setPrice(final String price) {
        this.price = price;
    }

    public double getCinemaPrice() {
        return cinemaPrice;
    }

    public void setCinemaPrice(final double cinemaPrice) {
        this.cinemaPrice = cinemaPrice;
    }

    public double getSalePrice() {
        return salePrice / 100;
    }

    public void setSalePrice(final double salePrice) {
        this.salePrice = salePrice;
    }

    public String getHall() {
        return hall;
    }

    public void setHall(final String hall) {
        this.hall = hall;
    }

    public int getHallID() {
        return hallID;
    }

    public void setHallID(final int hallID) {
        this.hallID = hallID;
    }

    public long getEndTime() {
        // String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new
        // Date(endTime));

        return endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public long getStartTime() {
        // String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new
        // Date(mStartTime));

        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    /**
     * 购票是否有效，true该段时间购票有效，false 反之
     */
    public boolean isVaildTicket() {
        return isVaildTicket;
    }

    public void setVaildTicket(final boolean isVaildTicket) {
        this.isVaildTicket = isVaildTicket;
    }

    public boolean isTicket() {
        return isTicket;
    }

    public void setTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }

    public int getIsSeatLess() {
        return isSeatLess;
    }

    public void setIsSeatLess(int isSeatLess) {
        this.isSeatLess = isSeatLess;
    }

    public boolean isCoupon() {
        return isCoupon;
    }

    public void setCoupon(boolean isCoupon) {
        this.isCoupon = isCoupon;
    }

    public String getSeatSalesTip() {
        if (seatSalesTip == null) {
            return "";
        }
        return seatSalesTip;
    }

    public void setSeatSalesTip(String seatSalesTip) {
        this.seatSalesTip = seatSalesTip;
    }

    public List<Provider> getProviders() {
        return providers;
    }

    public void setProviders(List<Provider> providers) {
        this.providers = providers;
    }

    public double getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(double activityPrice) {
        this.activityPrice = activityPrice;
    }

    public List<Provider> getProvider() {
        return provider;
    }

    public void setProvider(List<Provider> provider) {
        this.provider = provider;
    }

    public int getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(int ticketStatus) {
        this.ticketStatus = ticketStatus;
    }

    public String getTicketText() {
        return ticketText;
    }

    public void setTicketText(String ticketText) {
        this.ticketText = ticketText;
    }

    public String getHallSize() {
        return hallSize;
    }

    public void setHallSize(String hallSize) {
        this.hallSize = hallSize;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }
}