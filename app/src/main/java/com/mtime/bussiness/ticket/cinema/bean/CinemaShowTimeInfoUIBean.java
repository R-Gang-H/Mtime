/**
 *
 */
package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeUIBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CinemaShowTimeInfoUIBean extends MBaseBean {
    // 影院id
    private int id;
    // 影院名
    private String name;
    // 影院地址
    private String address;
    /*
     * // 影院电话 private String tel; // 影院邮编 private String postCode; // 地理纬度
     * private double geoLatitude; // 地理经度 private double geoLongitude; // 行车路线
     * private String route;
     */
    // 影院总评分
    private double ratingScore;
    /*
     * // 影院周边评分 private double ratingScore4Arround; // 影院设施评分 private double
     * ratingScore4Equipment; // 影院舒适评分 private double ratingScore4Comfort; //
     * 影院交通评分 private double ratingScore4Traffic; // 影院音响评分 private double
     * ratingScore4Sound; // 影院服务评分 private double ratingScore4Service; // 残疾人通道
     * private boolean hasSpecialHandicappedAccess; // 停车场 private boolean
     * hasSpecialPark; // 儿童娱乐区 private boolean hasSpecialChildPlay; // 餐饮区
     * private boolean hasSpecialFoodCourt; // 台阶式座椅 private boolean
     * hasSpecialLadderChair; // 游戏厅 private boolean hasSpecialGameRoom; // SDDS
     * private boolean hasSpecialSDDS; // DTS private boolean hasSpecialDTS; //
     * DLP private boolean hasSpecialDLP; // 影院公告 private String announce;
     * 
     * // 影院所在地区名称 private String locationName; // 影院所在地区编号 private int
     * locationId;
     * 
     * // 某影片剩余场次 private int remainingShowtimeCount; // 距离 单位：米 private double
     * distance; // 影院服务品质 private double ratingScoreService; // 观影效果 private
     * double ratingScoreEnjoy;
     */

    private List<ShowTimeUIBean> showTimeList; // 此电影院某影片的放映时间
    private boolean isEticket; // 是否可以购电子券
    private boolean isTicket; // 是否可以购票
    private int lapse; // 定位到显示的列中

    public boolean isTicket() {
        return isTicket;
    }

    public void setTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }

    public boolean isEticket() {
        return isEticket;
    }

    public void setEticket(final boolean isEticket) {
        this.isEticket = isEticket;
    }

    public List<ShowTimeUIBean> getShowTimeList() {
        return showTimeList;
    }

    /**
     * 获取过滤后的场次信息（过滤掉了不可购票的场次）
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public List<ShowTimeUIBean> getFilteredShowTimeList() {
        if (showTimeList == null) {
            return null;
        }
        final List<ShowTimeUIBean> tmpList = new ArrayList<ShowTimeUIBean>();
        for (final ShowTimeUIBean bean : showTimeList) {
            if (bean.isValid() && bean.isTicket()) {
                tmpList.add(bean);
            }
        }
        Collections.sort(tmpList, new Comparator() {
            @Override
            public int compare(final Object arg0, final Object arg1) {
                final ShowTimeUIBean bean1 = (ShowTimeUIBean) arg0;
                final ShowTimeUIBean bean2 = (ShowTimeUIBean) arg1;
                final int flag = (int) (bean1.getTime() - bean2.getTime());
                return flag;
            }
        });
        return tmpList;
    }

    public void setShowTimeList(final List<ShowTimeUIBean> showTimeList) {
        this.showTimeList = showTimeList;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getLapse() {
        return lapse;
    }

    public void setLapse(final int lapse) {
        this.lapse = lapse;
    }

    public double getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(final double ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }
}
