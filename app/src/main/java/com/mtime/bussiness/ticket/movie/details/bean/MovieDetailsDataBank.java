package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * 影片详情-资料库
 */
public class MovieDetailsDataBank implements IObfuscateKeepAll {
    public long movieId; //影片ID，非服务器返回


    public boolean isClassicLines; // true
    //经典台词
    public int classicLinesCount; // 10
    //经典台词个数
    public String classicLine; // "Run！Gump，Run！"
    //经典台词一句
    public boolean isCompany; // true
    //制作发行
    public int companyCount; // 10
    //发行公司个数
    public boolean isMediaReview; // true
    //媒体评论
    public int mediaReviewCount; // 20
    //媒体评论个数
    public boolean isBehind; // true
    //幕后制作
//    public boolean isSound; // true
//    //原声音乐
//    public int soundCount; // 20
//    //原声音乐个数
//    public boolean isMore; // true //更多资料,此字段无用

    public String classicUrl; //"http://xxxx/1234566",//经典台词地址
    public String behindUrl; //":"http://xxxx/1234566"//幕后制作地址

    /**
     * 是否存在【扩展资料】数据
     * 媒体评论、幕后制作、制作发行、更多资料
     */
    public boolean hasExtendInfoDatas() {
        return  isBehind || isCompany || isMediaReview;
    }

    /**
     * 转成 经典台词 实体
     * @return
     */
    public ClassicLines getClassicLines() {
        ClassicLines classicLines = new ClassicLines();
        classicLines.isClassicLines = true;
        classicLines.classicLinesCount = classicLinesCount;
        classicLines.classicLine = classicLine;
        classicLines.classicUrl = classicUrl;
        classicLines.movieId = movieId;
        return classicLines;
    }

    /**
     * 经典台词实体，本地使用，非服务器返回
     */
    public static class ClassicLines implements IObfuscateKeepAll {
        public long movieId; //影片ID，非服务器返回

        public boolean isClassicLines; // true
        //经典台词
        public int classicLinesCount; // 10
        //经典台词个数
        public String classicLine; // "Run！Gump，Run！"

        public String classicUrl; //"http://xxxx/1234566",//经典台词地址
    }
}
