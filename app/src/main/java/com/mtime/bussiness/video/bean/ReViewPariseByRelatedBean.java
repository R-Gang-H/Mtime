package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 点赞数
 * Created by lys on 17/8/11.
 */

public class ReViewPariseByRelatedBean  extends MBaseBean {
    private boolean success;
    private String error;
    private List<ReviewParisesBean> reviewParises;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<ReviewParisesBean> getReviewParises() {
        return reviewParises;
    }

    public void setReviewParises(List<ReviewParisesBean> reviewParises) {
        this.reviewParises = reviewParises;
    }

    public static class ReviewParisesBean extends MBaseBean {
        private int reviewId;
        private int totalPraise;
        private boolean isPraise;

        public int getReviewId() {
            return reviewId;
        }

        public void setReviewId(int reviewId) {
            this.reviewId = reviewId;
        }

        public int getTotalPraise() {
            return totalPraise;
        }

        public void setTotalPraise(int totalPraise) {
            this.totalPraise = totalPraise;
        }

        public boolean isIsPraise() {
            return isPraise;
        }

        public void setIsPraise(boolean isPraise) {
            this.isPraise = isPraise;
        }
    }
}
