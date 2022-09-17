package com.mtime.bussiness.ticket.cinema.bean;

import com.mtime.base.bean.MBaseBean;

public class GoodsListBean extends MBaseBean {
    private int goodsId;
    private String iconText;
    private String background;
    private String name;
    private String longName;
    private String image;
    private int marketPrice;
    private int minSalePrice;
    private String goodsUrl;
    private String marketPriceFormat;
    private String minSalePriceFormat;
    private String goodsTip;
    private String imageSrc;


    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    private String url;

    public String getGoodsTip() {
        return goodsTip;
    }

    public void setGoodsTip(String goodsTip) {
        this.goodsTip = goodsTip;
    }

    public int getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(int goodsId) {
        this.goodsId = goodsId;
    }

    public String getIconText() {
        return iconText;
    }

    public void setIconText(String iconText) {
        this.iconText = iconText;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLongName() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName = longName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(int marketPrice) {
        this.marketPrice = marketPrice;
    }

    public int getMinSalePrice() {
        return minSalePrice;
    }

    public void setMinSalePrice(int minSalePrice) {
        this.minSalePrice = minSalePrice;
    }

    public String getGoodsUrl() {
        return goodsUrl;
    }

    public void setGoodsUrl(String goodsUrl) {
        this.goodsUrl = goodsUrl;
    }

    public String getMarketPriceFormat() {
        return marketPriceFormat;
    }

    public void setMarketPriceFormat(String marketPriceFormat) {
        this.marketPriceFormat = marketPriceFormat;
    }

    public String getMinSalePriceFormat() {
        return minSalePriceFormat;
    }

    public void setMinSalePriceFormat(String minSalePriceFormat) {
        this.minSalePriceFormat = minSalePriceFormat;
    }

    public String getUrl() {
        if (url == null) {
            return "";
        }
        return url;
    }


    public String getImageSrc() {
        return imageSrc;
    }
}