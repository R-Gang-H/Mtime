package com.mtime.bussiness.video.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.mtime.base.bean.MBaseBean;
import com.mtime.bussiness.ticket.movie.details.adapter.binder.MovieDetailsBaseBinder;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mtime on 2017/10/19.
 * 用于获取影片分类视频
 * https://ticket-api-m.mtime.cn/movie/category/video.api
 * 采用HTTP GET方式提交请求，要求HTTP的版本为1.1以上。消息编码采用UTF-8。
 */

public class CategoryVideosBean extends MBaseBean {

    private String movieTitle;
    private int pageCount;
    private List<Category> category;
    private List<RecommendVideoItem> videoList;

    public int getListCount() {
        if (hasListData()) {
            return videoList.size();
        }
        return 0;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public List<Category> getCategory() {
        return category;
    }

    public void setCategory(List<Category> category) {
        this.category = category;
    }

    public List<RecommendVideoItem> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<RecommendVideoItem> videoList) {
        this.videoList = videoList;
    }

    public boolean hasListData() {
        return videoList != null && videoList.size() > 0;
    }

    public static class Category extends MBaseBean implements Serializable {

        //默-1-推荐， 0-预告片,1-精彩片段,2-拍摄花絮,3-影人访谈,4-电影首映,5-MV
        public static final int TYPE_RECOMMEND = -1;
        public static final int TYPE_PREVUE_VIDEO = 0;
        public static final int TYPE_WONDERFUL_SNIPPETS = 1;
        public static final int TYPE_TIDBITS_VIDEO = 2;
        public static final int TYPE_FILMER_INTERVIEW = 3;
        public static final int TYPE_FILM_PREMIERE = 4;
        public static final int TYPE_MV = 5;

        private int type;
        private String name;
        private boolean isSelect;

        private int index = -1;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isSelect() {
            return isSelect;
        }

        public void setSelect(boolean select) {
            isSelect = select;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public static String getTag(Category category) {
            switch (category.getType()) {
                case TYPE_PREVUE_VIDEO:
                    return "category:预告片";
                case TYPE_WONDERFUL_SNIPPETS:
                    return "category:精彩片段";
                case TYPE_TIDBITS_VIDEO:
                    return "category:拍摄花絮";
                case TYPE_FILMER_INTERVIEW:
                    return "category:影人访谈";
                case TYPE_FILM_PREMIERE:
                    return "category:电影首映";
                case TYPE_MV:
                    return "category:MV";
                default:
                case TYPE_RECOMMEND:
                    return "category:推荐";
            }
        }
    }


    /**
     * "recommendID": "aabbccc1111",           //推荐id
     * "recommendType": "video",           //推荐类型
     * "vId": 42239, //视频id
     * "videoSource",  1,  //视频来源1 预告片、2 自媒体、3 媒资视频
     * "image": "http://img31.test.cn/mg/2012/09/27/094150.46515035.jpg",  //视频图片
     * "title": "郭_flv",       //影片名
     * "type": 0,                //0-预告片,1-精彩片段,2-拍摄花絮,3-影人访谈,4-电影首映,5-MV
     * "length": 1213,           //影片时长,单位秒
     * "playCount" : "11,111" ,             //播放数量字
     * "commentTotal":"123,122",           //评论总数
     * "pulishTime":1111                 //发布时间戳，标准unix时间戳，单位秒
     */
    public static class RecommendVideoItem extends MBaseBean implements Serializable, MultiItemEntity {
        private String recommendID;
        private String recommendType;
        private int vId;
        private int videoSource;
        private String image;
        private String title;
        private int type;
        private int length;
        private String playCount;
        private String commentTotal;
        private long pulishTime;

        private String praiseInfo;
        private boolean isPraised;
        private long upCount;//点赞数
        private long downCount;//点踩数
        private Long currentUserPraise;//当前用户赞/踩状态 1:赞 2：踩 null:无 -1:当前未登陆

        public long getUpCount() {
            return upCount;
        }

        public void setUpCount(long upCount) {
            this.upCount = upCount;
        }

        public long getDownCount() {
            return downCount;
        }

        public void setDownCount(long downCount) {
            this.downCount = downCount;
        }

        public Long getCurrentUserPraise() {
            return currentUserPraise;
        }

        public void setCurrentUserPraise(Long currentUserPraise) {
            this.currentUserPraise = currentUserPraise;
        }

        public String getRecommendID() {
            return recommendID;
        }

        public void setRecommendID(String recommendID) {
            this.recommendID = recommendID;
        }

        public String getRecommendType() {
            return recommendType;
        }

        public void setRecommendType(String recommendType) {
            this.recommendType = recommendType;
        }

        public int getvId() {
            return vId;
        }

        public void setvId(int vId) {
            this.vId = vId;
        }

        public int getVideoSource() {
            return videoSource;
        }

        public void setVideoSource(int videoSource) {
            this.videoSource = videoSource;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getPlayCount() {
            return playCount;
        }

        public void setPlayCount(String playCount) {
            this.playCount = playCount;
        }

        public String getCommentTotal() {
            return commentTotal;
        }

        public void setCommentTotal(String commentTotal) {
            this.commentTotal = commentTotal;
        }

        public long getPulishTime() {
            return pulishTime;
        }

        public void setPulishTime(long pulishTime) {
            this.pulishTime = pulishTime;
        }

        public String getPraiseInfo() {
            return praiseInfo;
        }

        public void setPraiseInfo(String praiseInfo) {
            this.praiseInfo = praiseInfo;
        }

        public boolean isPraised() {
            return currentUserPraise != null && currentUserPraise.longValue() == 1L;
        }

        public void setPraised(boolean praised) {
            isPraised = praised;
        }

        @Override
        public int getItemType() {
            return vId == MovieDetailsBaseBinder.allId ? 1 : 0;
        }
    }

}
