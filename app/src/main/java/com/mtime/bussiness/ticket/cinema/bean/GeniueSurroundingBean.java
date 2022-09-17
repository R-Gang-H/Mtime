package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by lys on 17/10/16.
 * 正版周边
 */

public class GeniueSurroundingBean extends MBaseBean {

    private List<DetailRelatedsBean> detailRelateds;

    public List<DetailRelatedsBean> getDetailRelateds() {
        return detailRelateds;
    }

    public void setDetailRelateds(List<DetailRelatedsBean> detailRelateds) {
        this.detailRelateds = detailRelateds;
    }

    public static class DetailRelatedsBean extends MBaseBean {
        private int movieId;
        private int relateId;
        private int type;
        private int goodsCount;
        private String voucherMessage;
        private String relatedUrl;
        private List<String> voucherImg;
        private List<GoodsListBean> goodsList;

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public int getRelateId() {
            return relateId;
        }

        public void setRelateId(int relateId) {
            this.relateId = relateId;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getGoodsCount() {
            return goodsCount;
        }

        public void setGoodsCount(int goodsCount) {
            this.goodsCount = goodsCount;
        }

        public String getVoucherMessage() {
            return voucherMessage;
        }

        public void setVoucherMessage(String voucherMessage) {
            this.voucherMessage = voucherMessage;
        }

        public String getRelatedUrl() {
            return relatedUrl;
        }

        public void setRelatedUrl(String relatedUrl) {
            this.relatedUrl = relatedUrl;
        }

        public List<String> getVoucherImg() {
            return voucherImg;
        }

        public void setVoucherImg(List<String> voucherImg) {
            this.voucherImg = voucherImg;
        }

        public List<GoodsListBean> getGoodsList() {
            return goodsList;
        }

        public void setGoodsList(List<GoodsListBean> goodsList) {
            this.goodsList = goodsList;
        }
    }
}
