package com.mtime.bussiness.ticket.movie.details.bean;


import android.text.TextUtils;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsBaseBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * 影片详情-基础信息
 */
public class MovieDetailsBasic implements IObfuscateKeepAll {

    // 电影分类：0电影，1电视剧，2电视剧单集
    public static final long MOVIE_TYPE_MOVIE = 0;

    public long movieId;
    public String name; // "碟中谍4"
    //Mtime影片名
    public String nameEn; // "Mission: Impossible — Ghost Protocol"
    // 电影分类：0电影，1电视剧，2电视剧单集
    public long movieType;
    //影片英文名
    public float overallRating; // -1
    //综合评分
    public int isEReleased = 1; //电影是否上映（按最早上映日期判断） - 202105新增
    public List<SubItemRating> movieSubItemRatings; //综合分项分列表 - 202105新增
    public String message; // "该操作将清除您对该片的评分！是否确认？"
    public boolean isFilter; // false
    //是否过滤恐怖片
    public String url; // "http://movie.test.com/74843/"
    //影片网站url
    public String img; // "http://img21.test.cn/mt/2011/10/31/115209.38830905.jpg"
    //影片封面图
    public String bigImage; // "http://img21.test.cn/mt/2011/10/31/115209.38830905.jpg"
    //新增APP大图  注：新增加一个字段（前端直接使用）
    public Video video;
    public java.util.List<String> type;
    //类型
    public boolean isEggHunt; // false
    //是否有彩蛋
    public boolean sensitiveStatus; // false
    //是否敏感影片，true：敏感，false：不敏感
    public Community community;
    public String commentSpecial; // ""
    //一句话点评
    public int hotRanking; // -1
    //时光热度排名，-1未有排名信息
    public Director director;
    //演员，最多给20个
    public java.util.List<Actor> actors;
    public int personCount; // 5
    //演职员总数
    public boolean isTicket; // true
    //是否支持购票,使用该字段必须传城市ID参数。
    public int showCinemaCount; // 1
    //上映影院,使用该字段必须传城市ID参数。
    public int showtimeCount; // 14
    //场次,使用该字段必须传城市ID参数。
    public Style style;
    public boolean is3D; // true
    //是否3D
    public boolean isIMAX; // true
    //是否IMAX
    public boolean isIMAX3D; // false
    //是否IMAX3D
    public boolean isDMAX; // true
    //是否DMAX(中国巨幕)
    //电影节列表
    public List<Festivals> festivals;
    public Award award;
    public String releaseDate; // "20120101"
    //上映日期 格式 yyyyMMdd
    public String releaseArea; // "中国"
    //上映地区
    public String mins; // "110分钟"
    //片长
    public String story; // ""
    //Mtime影片剧情
    public StageImg stageImg;
    public QuizGame quizGame;
    // 201905APP改版 新增字段
    public int ratingCount; // 1414
    public String ratingCountShow;
    //评分人数
    public int attitude = -1; //表态：  -1，未表态; 0, 看过; 1, 想看;
    public int wantToSeeCount; // 13700
    //想看人数
    public String wantToSeeCountShow; // "13.7k人想看"
    public int hasSeenCount; // 16800
    //看过人数
    public String hasSeenCountShow; // "16.8k人看过"

    public String eggDesc; // "2个彩蛋，在片尾字幕后出现"

    public String wantToSeeNumberShow; //XXX年我想看的第XX部电影

    public int isFavorite; //是否收藏 收藏：1 未收藏：0

    public List<SubItemRating> getSubItemRatings() {
        return movieSubItemRatings;
    }

    public boolean hasSubRatings() {
        if (null != movieSubItemRatings) {
            for (SubItemRating item : movieSubItemRatings) {
                if (item.rating > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 是否存在视频
     * @return
     */
    public boolean hasVideo() {
        return null != video && video.videoId > 0;
    }

    public boolean hasStageImg() {
        return null != stageImg && !CollectionUtils.isEmpty(stageImg.list);
    }

    /**
     * 获取剧照数据
     * @return
     */
    public StageImg getStageImg() {
        if (hasStageImg()) {
            stageImg.movieId = movieId;
            stageImg.name = name;
            return stageImg;
        }
        return null;
    }

    public boolean hasIntro() {
        return !TextUtils.isEmpty(story);
    }

    /**
     * 获取 简介实体
     * @return MovieDetailsIntro
     */
    public MovieDetailsIntro getIntroBean() {
        if (!hasIntro())
            return null;
        MovieDetailsIntro intro = new MovieDetailsIntro();
        intro.story = story;
        return intro;
    }

    public boolean hasActorsBean() {
        return (null != director && director.directorId > 0) || !CollectionUtils.isEmpty(actors);
    }

    /**
     * 获取  演职人员（包括导演）实体
     * @return MovieDetailsActors
     */
    public MovieDetailsActors getActorsBean() {
        MovieDetailsActors actorsbean = new MovieDetailsActors();
        actorsbean.movieId = movieId;
        actorsbean.director = director;
        actorsbean.actors = actors;
        return actorsbean;
    }

    public boolean hasAward() {
        return null != award && (award.totalWinAward > 0 || award.totalNominateAward > 0);
    }

    /**
     * 奖项
     * @return
     */
    public Award getAward() {
        if (hasAward()) {
            award.movieId = movieId;
            award.movieName = name;
            award.festivals = festivals;
            return award;
        }
        return null;
    }

    public static class Actor implements IObfuscateKeepAll, MultiItemEntity {

        public int actorId; // 1323220
        public String name; // "张毅主"
        public String nameEn; // "张毅哥哥英文"
        public String img; // "http://img31.test.cn/ph/1220/1323220/1323220.jpg"
        public String roleName; // ""
        public String roleImg; // "http://192.168.50.23/V3_ImageServer/tc/.jpg"

        public boolean isDirector; // 本地字段，代表是否是导演

        @Override
        public int getItemType() {
            return actorId == MovieDetailsBaseBinder.allId ? 1 : 0;
        }
    }

    public static class Award implements IObfuscateKeepAll {
        public long movieId; //影片ID，非服务器返回
        public String movieName; //影片名称，非服务器返回
        //电影节列表
        public List<Festivals> festivals; //非服务器返回, 主要用于奖项列表页面

        public int totalWinAward; // 3
        //获奖总数
        public int totalNominateAward; // 5
        //提名总数
        //获奖荣誉列表
        public List<AwardList> awardList;

        public static class AwardList implements IObfuscateKeepAll {

            public int festivalId; // 93
            //电影节Id
            public int winCount; // 0
            //获奖次数
            public int nominateCount; // 2
            //提名次数
            //获奖列表
            public List<WinAward> winAwards;
            //提名列表
            public List<WinAward> nominateAwards;


            public static class WinAward implements IObfuscateKeepAll {

                public int sequenceNumber; // 38
                //电影节届数
                public String festivalEventYear; // "2012"
                //年代
                public String awardName; // "金奖-银奖"
                //获奖名称
                public List<Person> persons;


                public static class Person implements IObfuscateKeepAll {

                    public int personId; // 1233049
                    public String nameCn; // ""
                    public String nameEn; // "John Knoll"

                }

            }

        }

    }

    public static class Community implements IObfuscateKeepAll {

        //社区入口
        public String title; // "时光影趣"
        //标签名
        public String count; // "99+"
        //数据
        public String url; // "http:baidu.com"
        //跳转链接

    }

    public static class Director implements IObfuscateKeepAll {

        //导演
        public int directorId; // 1323560
        public String name; // "布鲁斯- 杰- 弗里德曼"
        public String nameEn; // "Bruce Jay Friedman"
        public String img; // "http://img31.test.cn/ph/2014/04/11/091200.84262346.jpg"

    }

    public static class Festivals implements IObfuscateKeepAll {

        public int festivalId; // 93
        //电影节Id
        public String img; // "http://img31.test.cn/mg/2014/02/19/185311.74561395.jpg"
        //图片
        public String nameCn; // "奥斯卡金像奖0418"
        //电影节中文名
        public String nameEn; // "Academy Awards, USAAcademy Awards"
        //电影节英文名
        public String shortName; // "奥斯卡"
        //简称

        // 非接口字段
        public boolean isExpand; // 是否展开

    }

    public static class QuizGame implements IObfuscateKeepAll {

        //猜电影游戏
        public String url; // "m.mtime.cn/quizgame/movie/1471/"
        //url
        public String title; // "xx道题考考你"
        //主标题
        public String smallTitle; // "对xx了解吗？"
        //副标题

    }

    public static class StageImg implements IObfuscateKeepAll {

        //movieId和name非服务器返回
        public long movieId;
        public String name; // "碟中谍4"

        //201905二期改版需求返回20条
        public int count; // 23
        //剧照总数
        public List<Img> list;

        public static class Img implements IObfuscateKeepAll, MultiItemEntity {
            //剧照id
            public int imgId; // 123652

            //剧照url
            public String imgUrl; // "http: //img31.test.cn/ph/1218/123652/123652.jpg"

            @Override
            public int getItemType() {
                return imgId == MovieDetailsBaseBinder.allId ? 1 : 0;
            }

        }

    }

    public static class Style implements IObfuscateKeepAll {

        //自定义样式
        public int isLeadPage; // 1
        //是否有前导页 0表示没有 1 有
        public String leadUrl; // "http://localhost:55771/Index.aspx"
        //地址
        public String leadImg; // "http://img31.test.cn/mg/2014/10/23/175657.90168867.jpg"
        //图片

    }

    public static class Video implements IObfuscateKeepAll {

        //## 特别注意 ## 为了兼容老主客APP，此处有可能返回空的video实体，此时videoId=0,请当作没有视频处理
        public int type; // 0
        //0-预告片,1-精彩片段,2-拍摄花絮,3-影人访谈,4-电影首映,5-MV
        public int videoSourceType; // 1
        //视频来源 注：新增加一个字段
        public String url; // ""
        //主要视频
        public int videoId; // 12345
        //主要视频Id
        public String title; // "测试预告片 添加"
        //主要视频标题
        public String hightUrl; // ""
        //高清视频地址
        public String img; // "http://img21.test.cn/mt/2011/10/31/115209.38830905.jpg"
        //视频图片
        public int count; // 25
        //视频数

    }

    public static class SubItemRating implements IObfuscateKeepAll {
        public int index;
        public String title;
        public float rating;
    }
}
