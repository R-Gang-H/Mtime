package com.mtime.bussiness.information.bean;

import android.text.TextUtils;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

/**
 * Created by zhuqiguang on 2018/6/27.
 * website www.zhuqiguang.cn
 */
public class ArticleRelatedMoviesBean extends MBaseBean implements Serializable {
    public static final String CAN_PLAY = "1";
    public static final String NOT_PLAY = "2";
    public static final int TICKET_STATE_NORMAL = 1;
    public static final int TICKET_STATE_PRESALE = 2;
    public static final int TICKET_STATE_NOT = 3;

    private long movieId;
    private int buyTicketStatus;
    private String isPlay;
    private String name;
    private String nameEn;
    private String img;
    private String movieType;
    private boolean isWantSee;
    private String releaseDate;
    private boolean isFilter;
    private String commentSpecial;
    public boolean isSensitive;  //是否是敏感影片----201910新增

    public String getCommentSpecial() {
        return commentSpecial;
    }

    public void setCommentSpecial(String commentSpecial) {
        this.commentSpecial = commentSpecial;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }


    public int getBuyTicketStatus() {
        return buyTicketStatus;
    }

    public void setBuyTicketStatus(int buyTicketStatus) {
        this.buyTicketStatus = buyTicketStatus;
    }

    public String getIsPlay() {
        return isPlay;
    }

    public void setIsPlay(String isPlay) {
        this.isPlay = isPlay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isWantSee() {
        return isWantSee;
    }

    public void setWantSee(boolean wantSee) {
        isWantSee = wantSee;
    }

    @Override
    public String toString() {
        return "ArticleRelatedMoviesBean{" +
                "movieId=" + movieId +
                ", buyTicketStatus=" + buyTicketStatus +
                ", isPlay='" + isPlay + '\'' +
                ", name='" + name + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", img='" + img + '\'' +
                ", movieType='" + movieType + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(isPlay) && TextUtils.isEmpty(name) &&
                TextUtils.isEmpty(nameEn) && TextUtils.isEmpty(img) &&
                TextUtils.isEmpty(movieType) && TextUtils.isEmpty(releaseDate);
    }
}
