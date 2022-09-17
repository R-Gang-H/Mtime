package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * 影片详情-在线播放
 */
public class MovieDetailsOnlinePlay implements IObfuscateKeepAll {

    public String sourceId; // "655345"
    //播放平台ID
    public String playSourceName; // "优酷"
    // 播放平台的名称
    public String picUrl; // "http://img21.test.cn/mt/2011/10/31/115209.38830905.jpg"
    //播放平台的缩略图
    public String payRule; // "VIP用券"
    // 免费， VIP免费， VIP付费， VIP用券
    public String playUrl; // "http://www.xxx.com"
    //播放地址
}
