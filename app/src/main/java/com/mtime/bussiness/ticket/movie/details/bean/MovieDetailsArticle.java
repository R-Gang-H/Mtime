package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

import java.util.List;

/**
 * 影片详情-文章
 */
public class MovieDetailsArticle implements IObfuscateKeepAll {
    public long movieId; //本地字段
    public String movieName; //本地字段
    public int titleResId; //本地字段，标题对应的string资源id

    public int relatedId; // 32131313
    public int contentType; // 2
    public int publishTime; // 1558240186
    public String title; // "Brie Larson奥斯卡影后的超级英雄"
    public String bodyText; // "brie larson 出演惊奇队长之前已经获得了奥斯卡影后。。。"
    public List<Images> images;
    public int videoId; // 66042
    public int videoSourceType; // 3
    public PublicInfo publicInfo;

    public String getImgUrl() {
        if (CollectionUtils.isEmpty(images))
            return "";
        return images.get(0).imgUrl;
    }

    public boolean isImgVideo() {
        if (CollectionUtils.isEmpty(images))
            return false;
        return images.get(0).isVideo;
    }

    public static class Images implements IObfuscateKeepAll {

        public String imgUrl; // "http://img5.mtime.cn/sma/2019/05/19/122728.28696740.jpg"
        public boolean isVideo; // true

    }

    public static class PublicInfo implements IObfuscateKeepAll {
        public String avatar;
        public String name;
        public long publicId;
    }
}
