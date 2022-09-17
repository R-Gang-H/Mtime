package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

public class CinemaShowtimeUPHalfMovieBean extends MBaseBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private int movieId;//影片Id
    private String title;//影片名称
    private String titleEn;//影片英文名
    private float ratingFinal;//综合评分
    private String length;//时长
    private String type;//影片类型
    private String img;//图片
    private List<String> showDates;//影讯日期["2014-10-30","2014-10-31"]
    private boolean isFilter;      //是否屏蔽海报
    private boolean isBorder = false;//是否是边界的空的填充数据(为了实现第一个item可以滑动到中间的效果，左右各添加了两个空item)

    public CinemaShowtimeUPHalfMovieBean(boolean isBorder) {
        this.isBorder = isBorder;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        if (title == null) {
            return "";
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEn() {
        if (titleEn == null) {
            return "";
        } else {
            return titleEn;
        }
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public float getRatingFinal() {
        return ratingFinal;
    }

    public void setRatingFinal(float ratingFinal) {
        this.ratingFinal = ratingFinal;
    }

    public String getLength() {
        if (length == null) {
            return "";
        } else {
            return length;
        }
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getType() {
        if (type == null) {
            return "";
        } else {
            return type;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImg() {
        if (img == null) {
            return "";
        } else {
            return img;
        }
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<String> getShowDates() {
        return showDates;
    }

    public void setShowDates(List<String> showDates) {
        this.showDates = showDates;
    }

    public void setIsFilter(boolean isFilter) {
        this.isFilter = isFilter;
    }

    public boolean isFilter() {
        return isFilter;
    }

    public boolean isBorder() {
        return isBorder;
    }

    public void setBorder(boolean border) {
        isBorder = border;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }
}
