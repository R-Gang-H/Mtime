// Used for API:
package com.mtime.bussiness.ticket.movie.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

public class MovieBean extends MBaseBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int id;
    private String t;
    private double r;
    private int rc;
    private String img;
    private String m;
    private String dN;
    private String aN1;
    private String aN2;
    private String actors;
    private boolean isHasTrailer;
    private List<String> p;
    private String rd;
    private String d;                    // 影片放映时长
    private int cC;
    private int sC;
    private String tEn;
    /**
     * 此字段貌似不适用
     */
    private int rsC;
    private int ua;
    private int def;
    /**
     * 最近可以购票的日期
     */
    private long dataTicket;
    // 影片上映时间
    private long rD;
    private String pt;
    private String commonSpecial;
    private boolean is3D;                 // true表示有、false反之。
    private boolean isIMAX;               // 同上
    private boolean isDMAX;               // 同上
    private boolean isIMAX3D;             // 同上

    private boolean isNew;
    private int NearestShowtimeCount;
    private int NearestCinemaCount;
    private long NearestDay;

    private String tCn;
    private List<VersionDetailBean> versions;
    private int wantedCount;
    private String movieType;
    private boolean isFilter;             //是否屏蔽海报
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public String getMovieType() {
        if (TextUtils.isEmpty(movieType)) {
            return "";
        }
        return movieType;
    }

    public void setMovieType(final String movieType) {
        this.movieType = movieType;
    }

    public int getWantedCount() {
        return wantedCount;
    }

    public void setWantedCount(final int wantedCount) {
        this.wantedCount = wantedCount;
    }

    public String getSummary() {
        return pt;
    }

    public void setSummary(final String pt) {
        this.pt = pt;
    }

    /**
     * 获取上映时间
     */
    public long getShowTime() {
        return rD;
    }

    public void setShowTime(final long rD) {
        this.rD = rD;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(final boolean isNew) {
        this.isNew = isNew;
    }

    private boolean isTicket;

    public boolean isTicket() {
        return isTicket;
    }

    public void setTicket(final boolean isTicket) {
        this.isTicket = isTicket;
    }

    public List<VersionDetailBean> getVersions() {
        return versions;
    }

    public void setVersions(final List<VersionDetailBean> versions) {
        this.versions = versions;
    }

    public String getCommonSpecial() {
        return commonSpecial;
    }

    public void setCommonSpecial(final String commonSpecial) {
        this.commonSpecial = commonSpecial;
    }

    public String getEnglishName() {
        return tEn;
    }

    public void setEnglishName(final String tEn) {
        this.tEn = tEn;
    }

    public int getId() {
        return id;
    }

    public String getStringID() {
        return String.valueOf(id);
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getName() {
        return t;
    }

    public void setT(final String t) {
        this.t = t;
    }

    public double getRatingScore() {
        return r;
    }

    public void setR(final double r) {
        this.r = r;
    }

    public int getRatingCount() {
        return rc;
    }

    public void setRc(final int rc) {
        this.rc = rc;
    }

    public String getImageUrl() {
        return img;
    }

    public void setImg(final String img) {
        this.img = img;
    }

    /**
     * 获取预告片图片url
     */
    public String getTrailerUrl() {
        return m;
    }

    public void setM(final String m) {
        this.m = m;
    }

    public String getDirectorName() {
        return dN;
    }

    public void setDN(final String dN) {
        this.dN = dN;
    }

    public String getActorName1() {
        return aN1;
    }

    public void setAN1(final String aN1) {
        this.aN1 = aN1;
    }

    public String getActorName2() {
        return aN2;
    }

    public void setAN2(final String aN2) {
        this.aN2 = aN2;
    }

    public List<String> getType() {
        return p;
    }

    public void setP(final List<String> p) {
        this.p = p;
    }

    public String getShowdate() {
        return rd;
    }

    public void setRd(final String rd) {
        this.rd = rd;
    }

    public String getDuration() {
        return d;
    }

    public void setD(final String d) {
        this.d = d;
    }

    public int getCinemaCount() {
        return cC;
    }

    public void setCC(final int cC) {
        this.cC = cC;
    }

    public int getShowtimeCount() {
        return sC;
    }

    public void setSC(final int sC) {
        this.sC = sC;
    }

    /**
     * 此方法貌似不适用
     */
    public int getRsC() {
        return rsC;
    }

    public void setRsC(final int rsC) {
        this.rsC = rsC;
    }

    public int getUserAttitude() {
        return ua;
    }

    public void setUserAttitude(final int ua) {
        this.ua = ua;
    }

    public long getDataTicket() {
        return dataTicket;
    }

    public void setDataTicket(final long dataTicket) {
        this.dataTicket = dataTicket;
    }

    public boolean isDMAX() {
        return isDMAX;
    }

    public void setIsDMAX(final boolean isDMAX) {
        this.isDMAX = isDMAX;
    }

    public boolean isIs3D() {
        return is3D;
    }

    public void setIs3D(final boolean is3d) {
        is3D = is3d;
    }

    public boolean isIMAX3D() {
        return isIMAX3D;
    }

    public void setIsIMAX3D(final boolean isIMAX3D) {
        this.isIMAX3D = isIMAX3D;
    }

    public boolean isIMAX() {
        return isIMAX;
    }

    public void setIMAX(final boolean isIMAX) {
        this.isIMAX = isIMAX;
    }

    public int getDef() {
        return def;
    }

    public void setDef(final int def) {
        this.def = def;
    }

    public int getNearestShowtimeCount() {
        return NearestShowtimeCount;
    }

    public void setNearestShowtimeCount(int nearestShowtimeCount) {
        NearestShowtimeCount = nearestShowtimeCount;
    }

    public int getNearestCinemaCount() {
        return NearestCinemaCount;
    }

    public void setNearestCinemaCount(int nearestCinemaCount) {
        NearestCinemaCount = nearestCinemaCount;
    }

    public long getNearestDay() {
        return NearestDay;
    }

    public void setNearestDay(long nearestDay) {
        NearestDay = nearestDay;
    }

    public String gettCn() {
        return tCn;
    }

    public void settCn(String tCn) {
        this.tCn = tCn;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public boolean isHasTrailer() {
        return isHasTrailer;
    }

    public void setHasTrailer(boolean hasTrailer) {
        isHasTrailer = hasTrailer;
    }
}
