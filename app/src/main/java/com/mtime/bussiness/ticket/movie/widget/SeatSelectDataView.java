package com.mtime.bussiness.ticket.movie.widget;

import com.kotlin.android.ktx.ext.time.TimeExt;
import com.mtime.frame.BaseActivity;
import com.mtime.frame.App;
import com.mtime.bussiness.ticket.movie.bean.CinemaJsonBean;
import com.mtime.bussiness.ticket.cinema.bean.CinemaShowTimeInfoUIBean;
import com.mtime.bussiness.ticket.movie.bean.Seat;
import com.mtime.bussiness.ticket.movie.bean.SeatInfoJsonBean;
import com.mtime.bussiness.ticket.movie.bean.SeatInfoUIBean;
import com.mtime.bussiness.ticket.movie.bean.ShowTimeUIBean;
import com.mtime.bussiness.ticket.cinema.bean.ShowtimeJsonBean;
import com.mtime.common.utils.DateUtil;
import com.mtime.common.utils.TextUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 在线选座
 */
public class SeatSelectDataView{
    private final CinemaShowTimeInfoUIBean cinemaShowTimeInfo = new CinemaShowTimeInfoUIBean();
    private SeatInfoUIBean seatInfo = new SeatInfoUIBean();

    public SeatSelectDataView(final BaseActivity activity) {
    }

    public SeatInfoUIBean getSeatInfo() {
        return seatInfo;
    }

    /**
     * SeatInfo的Json实体转换成UI实体
     *
     * @param seatInfoJsonBean
     */
    public void setSeatInfo(final SeatInfoJsonBean seatInfoJsonBean) {
        final SeatInfoUIBean tmpSeatInfo = new SeatInfoUIBean();
        final boolean falg = seatInfoJsonBean.isSale();
        tmpSeatInfo.setSale(falg);
        if (!falg) {
            // return seatInfo;
            seatInfo = tmpSeatInfo;
            return;
        }

        tmpSeatInfo.setCinemaName(seatInfoJsonBean.getCinemaName());
        tmpSeatInfo.setHallName(seatInfoJsonBean.getHallName());
        tmpSeatInfo.setLanguage(seatInfoJsonBean.getLanguage());
        tmpSeatInfo.setMovieName(seatInfoJsonBean.getMovieName());
        tmpSeatInfo.setRealTime(seatInfoJsonBean.getRealTime());
        tmpSeatInfo.setSalePrice(seatInfoJsonBean.getSalePrice() / 100);
        tmpSeatInfo.setServiceFee(seatInfoJsonBean.getServiceFee() / 100);
        tmpSeatInfo.setVersionDesc(seatInfoJsonBean.getVersionDesc());
        tmpSeatInfo.setMtimeSellPrice(seatInfoJsonBean.getMtimeSellPrice() / 100);
        tmpSeatInfo.setOrderId(seatInfoJsonBean.getOrderId());
        tmpSeatInfo.setSubOrderID(seatInfoJsonBean.getSubOrderID());
        tmpSeatInfo.setProviderId(seatInfoJsonBean.getSupplierId()); // 本场次的供应商ID
        tmpSeatInfo.setAllSeats(seatInfoJsonBean.getSeat());
        tmpSeatInfo.setMovieId(seatInfoJsonBean.getMovieId().toString());
        tmpSeatInfo.setCinemaId(seatInfoJsonBean.getCinemaId());
        final List<Seat> allSeats = seatInfoJsonBean.getSeat();
        if (null == allSeats) {
            return;
        }

        List<Seat> sameYValueSeats; // Y值相等的seat集合
        for (final Seat seat : allSeats) {
            // Seat s=new Seat();
            final int y = seat.getY();
            if (tmpSeatInfo.getYmap().containsKey(y)) {
                sameYValueSeats = tmpSeatInfo.getYmap().get(y);
            } else {
                sameYValueSeats = new ArrayList<Seat>();
            }
            sameYValueSeats.add(seat);

            tmpSeatInfo.getYmap().put(y, sameYValueSeats);
        }

        tmpSeatInfo.setProviderList(seatInfoJsonBean.getProvider());
        if (seatInfoJsonBean.getSalePriceList() != null && seatInfoJsonBean.getSalePriceList().size() > 0) {
            tmpSeatInfo.setSalePriceList(seatInfoJsonBean.getSalePriceList());
        }
        seatInfo = tmpSeatInfo;
    }

    public void cinemaJsonBean2UIBean(final CinemaJsonBean cinema,
                                      final boolean isMoviesTicket) {
        // 将json实体转换成ui实体
        cinemaShowTimeInfo.setAddress(cinema.getAddress());
        cinemaShowTimeInfo.setName(cinema.getName());
        cinemaShowTimeInfo.setRatingScore(cinema.getRating());
        cinemaShowTimeInfo.setEticket(cinema.isETicket());

        final List<ShowTimeUIBean> showTimes = new ArrayList<ShowTimeUIBean>();
        int lapse = 0;
        if (null == cinema.getS()) {
            cinemaShowTimeInfo.setLapse(lapse);
            cinemaShowTimeInfo.setShowTimeList(showTimes);
            return;
        }
        for (final ShowtimeJsonBean st : cinema.getS()) {
            final ShowTimeUIBean s = new ShowTimeUIBean();
            // 新需求中，只可购券也显示showTimeList，所以就算不可购票也应该添加以下数据
            s.setTicket(st.isTicket()); // 是否可以购票，当isTicket为true是，显示salePrice价格，
            s.setId(st.getSid());
            final long showDay = st.getShowDay();
            // 电影放映时间,服务器返回是2012-02-01
            String dateStr = TimeExt.INSTANCE.millis2String(showDay, DateUtil.sdf8);
            s.setDateTime(dateStr);
            // 10:30:34，转成后为10:30
            s.setTime(showDay);
            if (st.isVaildTicket()) { // 影片有效，还没有到点
                s.setValid(true);
            } else {
                lapse++;
                s.setValid(false);
            }
            s.setPrice(st.getPrice()); // 原价
            String version = null;
            // 0-无; 1-2D; 2-3D; 3-IMAX; 4-IMAX 3D; 5-4D; 6-DMAX
            switch (st.getVersion()) {
                case 1:
                    version = "2D";
                    break;
                case 2:
                    version = "3D";
                    break;
                case 3:
                    version = "IMAX";
                    break;
                case 4:
                    version = "IMAX 3D";
                    break;
                case 5:
                    version = "4D";
                case 6:
                    version = "DMAX";
                    break;
                default:
                    version = null;
                    break;
            }
            s.setVersion(version); // 版本 用于显示图片
            s.setCinemaPrice(st.getCinemaPrice()); // 影院门市价格（单位是分，可以在线选座时有效）

            StringBuffer sbf = new StringBuffer();
            final String versionDesc = st.getVersionDesc(); // 用于文字
            if ((versionDesc != null) && !"".equals(versionDesc)) {
                s.setVersionDesc(versionDesc);
                sbf.append(versionDesc).append(App.getInstance().CHAR_SLASH);
            }
            final String language = st.getLanguage(); // 语言
            if (TextUtil.stringIsNotNull(language)
                    && !App.getInstance().NO_DETAIL.equals(language)) {
                s.setLanguage(language);
                sbf.append(language).append(App.getInstance().CHAR_SLASH);
            }
            final int length = st.getLength();
            if (length > 0) {// 时长
                s.setDuration(length);
            }

            if (s.isTicket()) { // 价格 当isTicket为true是，显示salePrice价格，
                final double price = st.getSalePrice();
                if (price > 0) {
                    s.setSalePrice(price); // 在线售票售价，单位分
                }
            }
            final String hall = st.getHall(); // 厅
            if (TextUtil.stringIsNotNull(hall)) {
                s.setHall(hall);
            }
            s.setDescribe(sbf.toString()); // 描述

            if (null != st.getProviders()) {
                s.setProviderList(st.getProviders());
            } else {
                s.setProviderList(st.getProvider());
            }
            s.setIsSeatLess(st.getIsSeatLess());
            s.setActivityPrice(st.getActivityPrice());
            s.setCoupon(st.isCoupon());
            s.setSeatSalesTip(st.getSeatSalesTip());
            showTimes.add(s);
        }
        cinemaShowTimeInfo.setLapse(lapse);
        cinemaShowTimeInfo.setShowTimeList(showTimes);
    }

    public CinemaShowTimeInfoUIBean getCinemaShowTimeInfo() {
        return cinemaShowTimeInfo;
    }
}
