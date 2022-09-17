package com.mtime.bussiness.home.recommend.bean;

import com.mtime.bussiness.home.bean.HomeFeedItemBean;

import java.util.List;

/**
 * Created by ZhouSuQiang on 2017/11/20.
 * 首页-推荐-feed流item实体类
 */

public class HomeRecommendFeedItemBean extends HomeFeedItemBean {
    public String feedId; //关联的id
    public String tag; //tag文本
    public String tagFontColor; //tag字体颜色
    public HomeRecommendFeedAdvBean adv; //广告信息
    public List<HomeRecommendFeedLogxBean> logx; //负反馈信息(针对当前推荐 特有的)
    
    //非服务器返回字段==========================================================
    
    //用于上次刷新item
//    public boolean isPreviousRefresh; //是否为上次刷新item标识
//    public long previousRefreshTime; //上次刷新时间戳
    
    //用于区分是否是置顶数据
    public boolean isStickType;
    
    //============================================================
    
    public HomeRecommendFeedAdvBean getAdv() {
        return adv;
    }
    
    public void setAdv(HomeRecommendFeedAdvBean adv) {
        this.adv = adv;
    }
    
    public List<HomeRecommendFeedLogxBean> getLogx() {
        return logx;
    }
    
    public void setLogx(List<HomeRecommendFeedLogxBean> logx) {
        this.logx = logx;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HomeRecommendFeedItemBean) {
            return ((HomeRecommendFeedItemBean) obj).feedId.equals(this.feedId);
        }
        return super.equals(obj);
    }

}