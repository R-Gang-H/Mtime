package com.mtime.bussiness.information.bean;

import com.mtime.base.bean.MBaseBean;
import com.mtime.beans.ADDetailBean;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedItemBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinguanping on 2017/5/10.
 * 文章详情列表显示用bean
 */

public class ArticleDetailListShowBean extends MBaseBean {

    public static final int TYPE_TITLE_ARTICLE = 0xa1;//文章标题
    public static final int TYPE_WEBVIEW = 0xa2;//文章正文
    public static final int TYPE_PUBLICINFO = 0xa3;//公众号信息
    public static final int TYPE_RELATEDMOVIE = 0xa4;//相关影片/购票信息
    public static final int TYPE_RELATEDARTICLE = 0xa5;//相关文章/阅读
    public static final int TYPE_COMMENT = 0xa7;//评论
    public static final int TYPE_TITLE_TOPLIST = 0xa8;//榜单标题
    public static final int TYPE_AD = 0xa9;//榜单广告
    public static final int TYPE_IMGS = 0xa10;//图集横向图片列表
    public static final int TYPE_TOPLIST_PERSON = 0xa11;//榜单--影人
    public static final int TYPE_TOPLIST_MOVIE = 0xa12;//榜单--影片


    /**
     * publicInfo : {"publicId":123343,"avatar":"http://img31.test.cn/mg/2013/02/21/164721.95672150.jpg","name":"时光策划","desc":"时光策划是时光网自己的公众号账户","followCountDesc":"800人关注","articleCountDesc":"902篇文章"}
     * titleinfo : {"title":"title","author":"xiaoT","publishTime":"刚刚"}
     * images : [{"img":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg","desc":"说明"}]
     * webview : {"content":"内容,榜单摘要","articleId":11111}
     * relatedMovie : {"movieId":122,"name":"n","nameEn":"xxxxx","img":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg","buyTicketStatus":1,"rating":"7.8","commentSpecial":"一句话点评"}
     * relatedArticles : [{"articleId":122,"title":"n","img":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg","type":1,"imageCount":1,"videoMins":1111,"videoPlayCount":50}]
     * totalCount : 5
     * movies : [{"movieId ":58244,"name ":"地球脉动","nameEn":"En","rankNum ":1,"img":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg","decade":2006,"rating":"9.5","releaseDate":"2006年2月27日","releaseLocation":"英国","movieType":"纪录片","director":"艾雷斯泰","remark":"IMDB:9.5"}]
     * persons : [{"personId":123456,"nameCn ":"影人中文名","nameEn":"影人英文名","rankNum":2,"img":"http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg","rating":"7.5","sex":"男","birthYear":2000,"birthDay":"10月20","birthLocation":"中国","constellation":"天枰座","summary ":"影人简介"}]
     * toplistTitle : {"title":"title","content":"内容,榜单摘要"}
     * list : [{"commentId":20826322,"content":"《变形金刚2》是一部完美的商业","nickname":"图宾根木匠","commentDate":1251770940,"headImg":"http://dimg.mtime.com/h/613/ODI0NjEz_32X32.jpg?m=10131045","replyCount":0,"replys":[{"replyId":5652,"userId":5652,"userType":1,"nickname":"莎莎123","replyName":"回复给莎莎123","content":"环境不错 测试 呵呵"}]}]
     */

    private int itemType;
    private PublicInfoBean publicInfo;
    private TitleinfoBean titleinfo;
    private WebviewBean webview;
    private RelatedMovieBean relatedMovie;
    private int totalCount;
    //榜单相关
    private ToplistTitleBean toplistTitle;
    private MoviesBean moviesBean;
    private PersonsBean personsBean;

    private List<ImagesBean> images;
    private RelatedArticlesBean relatedArticlesBean;
    //    private List<RelatedArticlesBean> relatedArticles;
    //    private List<PersonsBean> persons;
    private List<ListBean> list;
    private ADDetailBean adBean;
    private CommentBean comment;


    public PublicInfoBean getPublicInfo() {
        return publicInfo;
    }

    public void setPublicInfo(PublicInfoBean publicInfo) {
        this.publicInfo = publicInfo;
    }

    public TitleinfoBean getTitleinfo() {
        return titleinfo;
    }

    public void setTitleinfo(TitleinfoBean titleinfo) {
        this.titleinfo = titleinfo;
    }

    public WebviewBean getWebview() {
        return webview;
    }

    public void setWebview(WebviewBean webview) {
        this.webview = webview;
    }

    public RelatedMovieBean getRelatedMovie() {
        return relatedMovie;
    }

    public void setRelatedMovie(RelatedMovieBean relatedMovie) {
        this.relatedMovie = relatedMovie;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public ToplistTitleBean getToplistTitle() {
        return toplistTitle;
    }

    public void setToplistTitle(ToplistTitleBean toplistTitle) {
        this.toplistTitle = toplistTitle;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public RelatedArticlesBean getRelatedArticlesBean() {
        return relatedArticlesBean;
    }

    public void setRelatedArticlesBean(RelatedArticlesBean relatedArticlesBean) {
        this.relatedArticlesBean = relatedArticlesBean;
    }

    public MoviesBean getMoviesBean() {
        return moviesBean;
    }

    public void setMoviesBean(MoviesBean moviesBean) {
        this.moviesBean = moviesBean;
    }

    public PersonsBean getPersonsBean() {
        return personsBean;
    }

    public void setPersonsBean(PersonsBean personsBean) {
        this.personsBean = personsBean;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public ADDetailBean getAdBean() {
        return adBean;
    }

    public void setAdBean(ADDetailBean adBean) {
        this.adBean = adBean;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public CommentBean getComment() {
        return comment;
    }

    public void setComment(CommentBean comment) {
        this.comment = comment;
    }

    public static class PublicInfoBean extends MBaseBean implements Serializable {
        /**
         * publicId : 123343
         * avatar : http://img31.test.cn/mg/2013/02/21/164721.95672150.jpg
         * name : 时光策划
         * desc : 时光策划是时光网自己的公众号账户
         * followCountDesc : 800人关注
         * articleCountDesc : 902篇文章
         */

        private long publicId;
        private String avatar;
        private String name;
        private String desc;
        private String followCountDesc;
        private String articleCountDesc;
        private boolean isAttention;

        public long getPublicId() {
            return publicId;
        }

        public void setPublicId(long publicId) {
            this.publicId = publicId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getFollowCountDesc() {
            return followCountDesc;
        }

        public void setFollowCountDesc(String followCountDesc) {
            this.followCountDesc = followCountDesc;
        }

        public String getArticleCountDesc() {
            return articleCountDesc;
        }

        public void setArticleCountDesc(String articleCountDesc) {
            this.articleCountDesc = articleCountDesc;
        }

        public boolean isAttention() {
            return isAttention;
        }

        public void setAttention(boolean attention) {
            isAttention = attention;
        }
    }

    public static class TitleinfoBean extends MBaseBean {
        /**
         * title : title
         * author : xiaoT
         * publishTime : 刚刚
         */

        private String title;
        private String author;
        private long publishTime;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public long getPublishTime() {
            return publishTime;
        }

        public void setPublishTime(long publishTime) {
            this.publishTime = publishTime;
        }
    }

    public static class WebviewBean extends MBaseBean {
        /**
         * content : 内容,榜单摘要
         * articleId : 11111
         */

        private String content;
        private int articleId;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getArticleId() {
            return articleId;
        }

        public void setArticleId(int articleId) {
            this.articleId = articleId;
        }
    }

    public static class RelatedMovieBean extends MBaseBean {
        /**
         * movieId : 122
         * name : n
         * nameEn : xxxxx
         * img : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
         * buyTicketStatus : //购票状态0不可购票 1 正常购票2预售
         * rating : 7.8
         * commentSpecial : 一句话点评
         */

        private int movieId;
        private String name;
        private String nameEn;
        private String img;
        private int buyTicketStatus;//购票状态0不可购票 1 正常购票2预售
        private boolean isWantSee;
        private String rating;
        private String commentSpecial;
        private boolean isFilter; // 是否需要恐怖海报过滤

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getBuyTicketStatus() {
            return buyTicketStatus;
        }

        public void setBuyTicketStatus(int buyTicketStatus) {
            this.buyTicketStatus = buyTicketStatus;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getCommentSpecial() {
            return commentSpecial;
        }

        public void setCommentSpecial(String commentSpecial) {
            this.commentSpecial = commentSpecial;
        }

        public boolean isWantSee() {
            return isWantSee;
        }

        public void setWantSee(boolean wantSee) {
            isWantSee = wantSee;
        }

        public boolean isFilter() {
            return isFilter;
        }

        public void setFilter(boolean filter) {
            isFilter = filter;
        }
    }

    public static class ToplistTitleBean extends MBaseBean {
        /**
         * title : title
         * content : 内容,榜单摘要
         */

        private String title;
        private String content;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class ImagesBean extends MBaseBean {
        /**
         * img : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
         * desc : 说明
         */

        private String img;
        private String desc;

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public static class RelatedArticlesBean extends MBaseBean {
        public static final int ARTICLE_TYPE_RANK = 2;
        public static final int ARTICLE_TYPE_VIDEO = 3;
        public RelatedArticlesBean() {
            this.relatedArticleList = new ArrayList<>();
        }

        public List<HomeOriginalFeedItemBean> relatedArticleList;
    }

    public static class MoviesBean extends MBaseBean {
        /**
         * movieId  : 58244
         * name  : 地球脉动
         * nameEn : En
         * rankNum  : 1
         * img : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
         * decade : 2006
         * rating : 9.5
         * releaseDate : 2006年2月27日
         * releaseLocation : 英国
         * movieType : 纪录片
         * director : 艾雷斯泰
         * remark : IMDB:9.5
         */

        private int movieId;
        private String name;
        private String nameEn;
        private int rankNum;
        private String img;
        private int decade;
        private String rating;
        private String releaseDate;
        private String releaseLocation;
        private String movieType;
        private String director;
        private String remark;

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public int getRankNum() {
            return rankNum;
        }

        public void setRankNum(int rankNum) {
            this.rankNum = rankNum;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getDecade() {
            return decade;
        }

        public void setDecade(int decade) {
            this.decade = decade;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getReleaseDate() {
            return releaseDate;
        }

        public void setReleaseDate(String releaseDate) {
            this.releaseDate = releaseDate;
        }

        public String getReleaseLocation() {
            return releaseLocation;
        }

        public void setReleaseLocation(String releaseLocation) {
            this.releaseLocation = releaseLocation;
        }

        public String getMovieType() {
            return movieType;
        }

        public void setMovieType(String movieType) {
            this.movieType = movieType;
        }

        public String getDirector() {
            return director;
        }

        public void setDirector(String director) {
            this.director = director;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    public static class PersonsBean extends MBaseBean {
        /**
         * personId : 123456
         * nameCn  : 影人中文名
         * nameEn : 影人英文名
         * rankNum : 2
         * img : http: //img31.mtime.cn/mt/244/58244/58244_96X128.jpg
         * rating : 7.5
         * sex : 男
         * birthYear : 2000
         * birthDay : 10月20
         * birthLocation : 中国
         * constellation : 天枰座
         * summary  : 影人简介
         */

        private int personId;
        private String nameCn;
        private String nameEn;
        private int rankNum;
        private String img;
        private String rating;
        private String sex;
        private int birthYear;
        private String birthDay;
        private String birthLocation;
        private String constellation;
        private String summary;

        public int getPersonId() {
            return personId;
        }

        public void setPersonId(int personId) {
            this.personId = personId;
        }

        public String getNameCn() {
            return nameCn;
        }

        public void setNameCn(String nameCn) {
            this.nameCn = nameCn;
        }

        public String getNameEn() {
            return nameEn;
        }

        public void setNameEn(String nameEn) {
            this.nameEn = nameEn;
        }

        public int getRankNum() {
            return rankNum;
        }

        public void setRankNum(int rankNum) {
            this.rankNum = rankNum;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getBirthYear() {
            return birthYear;
        }

        public void setBirthYear(int birthYear) {
            this.birthYear = birthYear;
        }

        public String getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(String birthDay) {
            this.birthDay = birthDay;
        }

        public String getBirthLocation() {
            return birthLocation;
        }

        public void setBirthLocation(String birthLocation) {
            this.birthLocation = birthLocation;
        }

        public String getConstellation() {
            return constellation;
        }

        public void setConstellation(String constellation) {
            this.constellation = constellation;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }
    }

    public static class ListBean extends MBaseBean {
        /**
         * commentId : 20826322
         * content : 《变形金刚2》是一部完美的商业
         * nickname : 图宾根木匠
         * commentDate : 1251770940
         * headImg : http://dimg.mtime.com/h/613/ODI0NjEz_32X32.jpg?m=10131045
         * replyCount : 0
         * replys : [{"replyId":5652,"userId":5652,"userType":1,"nickname":"莎莎123","replyName":"回复给莎莎123","content":"环境不错 测试 呵呵"}]
         */

        private int commentId;
        private String content;
        private String nickname;
        private int commentDate;
        private String headImg;
        private int replyCount;
        private List<ReplysBean> replys;

        public int getCommentId() {
            return commentId;
        }

        public void setCommentId(int commentId) {
            this.commentId = commentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(int commentDate) {
            this.commentDate = commentDate;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public List<ReplysBean> getReplys() {
            return replys;
        }

        public void setReplys(List<ReplysBean> replys) {
            this.replys = replys;
        }

        public static class ReplysBean extends MBaseBean {
            /**
             * replyId : 5652
             * userId : 5652
             * userType : 1
             * nickname : 莎莎123
             * replyName : 回复给莎莎123
             * content : 环境不错 测试 呵呵
             */

            private int replyId;
            private int userId;
            private int userType;
            private String nickname;
            private String replyName;
            private String content;

            public int getReplyId() {
                return replyId;
            }

            public void setReplyId(int replyId) {
                this.replyId = replyId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public int getUserType() {
                return userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getReplyName() {
                return replyName;
            }

            public void setReplyName(String replyName) {
                this.replyName = replyName;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }
        }
    }

    public static class CommentBean extends MBaseBean {
        /**
         * commentId : 20826322
         * content : 《变形金刚2》是一部完美的商业
         * nickname : 图宾根木匠
         * commentDate : 1471924615
         * headImg : http://dimg.mtime.com/h/613/ODI0NjEz_32X32.jpg?m=10131045
         * replyCount : 0
         * replys : [{"replyId":5652,"userId":5652,"userType":1,"nickname":"莎莎123","targetNickname":"回复给莎莎123","content":"环境不错 测试 呵呵"}]
         */

        private boolean isFirst;//是否第一条评论
        private boolean isLast;//是否第一条评论
        private long commentId;
        private String content;
        private String nickname;
        private long commentDate;
        private String headImg;
        private int replyCount;
        private List<ReplysBean> replys;
        private long totalCount;//long类型 总条数
        private boolean isPraised;//是否已赞
        public String articlesId; //文章id
        public int index; //评论列表中的index
        private int supportCnt; // 点赞数

        public boolean isLast() {
            return isLast;
        }

        public void setLast(boolean last) {
            isLast = last;
        }

        public boolean isPraise() {
            return isPraised;
        }

        public void setPraise(boolean praise) {
            isPraised = praise;
        }

        public int getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(int praiseCount) {
            this.praiseCount = praiseCount;
        }

        private int praiseCount;

        public boolean isFirst() {
            return isFirst;
        }

        public void setFirst(boolean first) {
            isFirst = first;
        }

        public long getCommentId() {
            return commentId;
        }

        public void setCommentId(long commentId) {
            this.commentId = commentId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getCommentDate() {
            return commentDate;
        }

        public void setCommentDate(long commentDate) {
            this.commentDate = commentDate;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }

        public int getReplyCount() {
            return replyCount;
        }

        public void setReplyCount(int replyCount) {
            this.replyCount = replyCount;
        }

        public List<ReplysBean> getReplys() {
            return replys;
        }

        public void setReplys(List<ReplysBean> replys) {
            this.replys = replys;
        }

        public long getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(long totalCount) {
            this.totalCount = totalCount;
        }

        public int getSupportCnt() {
            return supportCnt;
        }

        public void setSupportCnt(int supportCnt) {
            this.supportCnt = supportCnt;
        }

        public static class ReplysBean extends MBaseBean {
            /**
             * replyId : 5652
             * userId : 5652
             * userType : 1
             * nickname : 莎莎123
             * targetNickname : 回复给莎莎123
             * content : 环境不错 测试 呵呵
             */

            private long replyId;
            private long userId;
            private int userType;
            private String nickname;
            private String targetNickname;
            private String content;
            private String headImg;
            private long replysDate; // 单位 s

            public long getReplyId() {
                return replyId;
            }

            public void setReplyId(long replyId) {
                this.replyId = replyId;
            }

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
                this.userId = userId;
            }

            public int getUserType() {
                return userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public String getTargetNickname() {
                return targetNickname;
            }

            public void setTargetNickname(String targetNickname) {
                this.targetNickname = targetNickname;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getHeadImg() {
                return headImg;
            }

            public void setHeadImg(String headImg) {
                this.headImg = headImg;
            }

            public long getReplysDate() {
                return replysDate;
            }

            public void setReplysDate(long replysDate) {
                this.replysDate = replysDate;
            }
        }
    }
}
