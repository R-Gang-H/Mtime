package com.mtime.bussiness.home.recommend.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by ZhouSuQiang on 2017/11/22.
 * 首页-推荐-feed流中item内的资讯负反馈理由实体
 */

public class HomeRecommendFeedLogxBean implements IObfuscateKeepAll {
    public String categoryName;
    public int categoryType;
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public int getCategoryType() {
        return categoryType;
    }
    
    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }
    
}
