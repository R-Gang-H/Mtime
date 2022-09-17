package com.mtime.bussiness.ticket.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.bean.RatingItemsBean;

/**
 * 影片/影人/影院的收藏评分状态
 * 
 * @author ye
 */
public class TargetObjStatus extends MBaseBean {
    
    private int isFavorite; // 是否收藏 收藏：1 未收藏：0
    private float rating; // 用户对该对象的打分
    private boolean isRemind;// 是否添加了上映提醒，只有类型为电影时才会返回
    private boolean isWantSee; // 是否想看，只有类型为电影时返回。
    private RatingItemsBean ratingItems; // 分项评分。只有类型为电影时返回
    private float reffect;   // 影院观影效果，10分制，只有类型为影院返回. float
    private float rservice;  // 影院服务，10分制，只有类型为影院返回. float

    /** 是否收藏 : 收藏-1 ,未收藏-0 */
    public int getIsFavorite() {
        return this.isFavorite;
    }

    public void setIsFavorite(final int isFavorite) {
        this.isFavorite = isFavorite;
    }

    public float getRating() {
        return this.rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public boolean getIsRemind() {
        return isRemind;
    }

    public void setIsRemind(final boolean isRemind) {
        this.isRemind = isRemind;
    }
    
    public boolean getIsWantSee() {
        return isWantSee;
    }

    public void setIsWantSee(final boolean isWantSee) {
        this.isWantSee = isWantSee;
    }
    
    public RatingItemsBean getRatingItems() {
        return ratingItems;
    }
    
    public void setRatingItems(final RatingItemsBean ratingItems)  {
        this.ratingItems = ratingItems;
    }

    public float getReffect() {
        return reffect;
    }

    public void setReffect(float reffect) {
        this.reffect = reffect;
    }

    public float getRservice() {
        return rservice;
    }

    public void setRservice(float rservice) {
        this.rservice = rservice;
    }

}
