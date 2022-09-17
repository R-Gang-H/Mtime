package com.mtime.beans;

import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.cinema.bean.GoodsListBean;

import java.util.ArrayList;
import java.util.List;

public class RelatedGoods extends MBaseBean {
    private int relatedId;
    private int relatedType;
    private String relatedUrl;
    private int goodsCount;
    private List<GoodsListBean> goodsList;
    private String voucherMessage;
    // 从list修改成一个对象，
    private final List<String> voucherimg = new ArrayList<>();

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(final String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public int getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(final int relatedId) {
        this.relatedId = relatedId;
    }

    public int getGoodsCount() {
        return goodsCount;
    }

    public void setGoodsCount(final int goodsCount) {
        this.goodsCount = goodsCount;
    }

    public int getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(final int relatedType) {
        this.relatedType = relatedType;
    }

    public List<GoodsListBean> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(final List<GoodsListBean> goodsList) {
        this.goodsList = goodsList;
    }

    public List<String> getVoucherimg() {
        return voucherimg;
    }

    public void setVoucherimg(List<String> voucherimg) {
        if (null != voucherimg && voucherimg.size() > 0) {
            this.voucherimg.addAll(voucherimg);
        }
    }

    public String getVoucherMessage() {
        if (voucherMessage == null){
            return "";
        }
        return voucherMessage;
    }

    public void setVoucherMessage(String voucherMessage) {
        this.voucherMessage = voucherMessage;
    }
}
