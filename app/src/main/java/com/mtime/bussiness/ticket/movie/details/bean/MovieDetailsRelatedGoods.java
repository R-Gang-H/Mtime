package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * 影片详情-关联商品
 */
public class MovieDetailsRelatedGoods implements IObfuscateKeepAll {
    public long movieId; //影片Id, 非服务器返回

    public int relateId; // 38260
    //关联编号
    public int type; // 1
    //关联类型
    public int goodsCount; // 1
    //关联商品总数
    public List<String> voucherImg;
    public String voucherMessage; // "优惠券已存入到我的优惠券"
    public String relatedUrl; // "http://mall.wv.mtime.cn/#!/commerce/list/movie/38260/"
    //混合实现，关联列表url
    public List<Goods> goodsList; //关联商品列表

    public static class Goods implements IObfuscateKeepAll {

        //关联商品列表
        public int goodsId; // 114502
        //商品Id
        public String goodsTip; // "自营"
        // 是否是自营，如果是则显示“自营”     20180728新增！！！
        public String iconText; // ""
        //默认背景#FF5A4D,闪购：文字为"限时抢购"（背景色#FF5A4D，预售："超前预售"（背景色#4EC178）
        public String background; // "#FF5A4D"
        //背景颜色
        public String name; // "HOTTOYS 动漫会场版"
        //商品名称
        public String longName; // "全新现货 HOTTOYS 动漫会场版 "
        //商品长名
        public String image; // "http://img31.test.cn/mg/2015/01/15/145907.42225951.jpg"
        //图片
        public int marketPrice; // 165432
        //市场价，单位分
        public int minSalePrice; // 165432
        //最低价，单位分
        public String goodsUrl; // "http://m.mtime.cn/#!/commerce/item/114502/"
        //混合实现，商品详情页
        public String marketPriceFormat; // "1654.32"
        //市场价，单位元，保留两位小数
        public String minSalePriceFormat; // "1654.32"
        //最低价，单位元，保留两位小数

    }
}
