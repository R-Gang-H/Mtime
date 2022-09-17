package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * 影片详情-直播
 */
public class MovieDetailsLive implements IObfuscateKeepAll {
    public long movieId; //影片ID，非服务器返回

    public int count; // 1
    //总数
    public int liveId; // 12
    //直播id
    public String img; // "http://img21.test.cn/mt/2011/10/31/115209.38830905.jpg"
    //缩微图
    public String title; // "长城电影发布会"
    //标题
    public int     status; // 1 //状态 1未开始 2直播中 3稍后回看 4回看 5直播结束
    public String playTag; // "10月6日 开始"
    //  播放状态标签，未开始显示 xx时候开始，正在播放时，显示正在播放
    public String playNumTag; // "12万人观看"
    //  播放数量标签  替换内容为xx观看，xx预约，xx次播放

    public boolean isSubscribe; //是否已预约

}
