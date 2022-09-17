package com.mtime.bussiness.ticket.movie.bean;

// JSON Java Class Generator
// Written by Bruce Bao
// Used for API:

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CommodityList  extends MBaseBean implements Serializable {
    private int    commodityId;
    private String name;
    private String shortName;
    private String desc;
    private int    stockQuantity;
    private double price;
    private String imagePath;
    private int    maxLimited;
    private int    totlePrice;
    private int    count;
    private int    quantity;
    private String retailPrice;
    
    public int getCommodityId() {
        return commodityId;
    }
    
    public void setCommodityId(int commodityId) {
        this.commodityId = commodityId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public int getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public double getPrice() {
        return price / 100;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public int getMaxLimited() {
        return maxLimited;
    }
    
    public void setMaxLimited(int maxLimited) {
        this.maxLimited = maxLimited;
    }
    
    public int getTotlePrice() {
        return totlePrice;
    }
    
    public void setTotlePrice(int totlePrice) {
        this.totlePrice = totlePrice;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getRetailPrice() {
        if (retailPrice == null) {
            return "";
        }
        return retailPrice;
    }

    public void setRetailPrice(String retailPrice) {
        this.retailPrice = retailPrice;
    }
    
}
