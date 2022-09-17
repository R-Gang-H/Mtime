package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

public class ArticleInfoBean extends MBaseBean {

    private int count;
    private List<ListBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean extends MBaseBean {

        private long articleId;
        private long praiseCount;
        private String praiseCountShow;
        private long commentCount;
        private String commentCountShow;
        private boolean hasFavorite;
        private boolean hasPraise;

        public long getArticleId() {
            return articleId;
        }

        public void setArticleId(long articleId) {
            this.articleId = articleId;
        }

        public long getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(long praiseCount) {
            this.praiseCount = praiseCount;
        }

        public long getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(long commentCount) {
            this.commentCount = commentCount;
        }

        public String getPraiseCountShow() {
            return praiseCountShow;
        }

        public void setPraiseCountShow(String praiseCountShow) {
            this.praiseCountShow = praiseCountShow;
        }

        public String getCommentCountShow() {
            return commentCountShow;
        }

        public void setCommentCountShow(String commentCountShow) {
            this.commentCountShow = commentCountShow;
        }

        public boolean isHasFavorite() {
            return hasFavorite;
        }

        public void setHasFavorite(boolean hasFavorite) {
            this.hasFavorite = hasFavorite;
        }

        public boolean isHasPraise() {
            return hasPraise;
        }

        public void setHasPraise(boolean hasPraise) {
            this.hasPraise = hasPraise;
        }
    }
}
