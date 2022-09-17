package com.mtime.bussiness.ticket.movie.details.bean;

import android.text.TextUtils;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * 影片详情-票房
 */
public class MovieDetailsBoxOffice implements IObfuscateKeepAll {

    //数据实体
    public int ranking; // -1
    //电影票房排名，-1未有排名信息
    public long movieId; // 38260
    //影片Id
    public long todayBox; // 12000
    //今日票房，类型long，单位分
    public String todayBoxDes; // "1.2"
    //今日票房，单位万或者亿
    public String todayBoxDesUnit; // "今日实时票房（万元）"
    //今日票房单位
    public long totalBox; // 24000
    //累计票房，类型long，单位分
    public String totalBoxDes; // "2.4"
    //累计票房，单位万或者亿
    public String totalBoxUnit; // "累计实时票房（万元）"
    //累计票房单位

    public boolean hasData() {
        return (ranking > 0 && !TextUtils.isEmpty(todayBoxDes)) || !TextUtils.isEmpty(totalBoxDes);
    }
}
