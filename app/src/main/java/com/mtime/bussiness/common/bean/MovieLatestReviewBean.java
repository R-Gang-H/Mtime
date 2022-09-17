package com.mtime.bussiness.common.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

public class MovieLatestReviewBean implements IObfuscateKeepAll {

    public UserInfo userInfo;
    public Review shortComment;
    public Review longComment;

    public boolean hasLongComment() {
        return longComment != null && longComment.commentId > 0;
    }

    public boolean hasShortComment() {
        return shortComment != null && shortComment.commentId > 0;
    }

    public boolean hasData() {
        return (null != shortComment && shortComment.commentId > 0)
                || (null != longComment && longComment.commentId > 0);
    }

    public int getAttitude() {
        if (null != userInfo) {
            return userInfo.attitude;
        }
        return -1;
    }

    public String getUserImg() {
        if (null != userInfo) {
            return userInfo.img;
        }
        return null;
    }

    public String getUserName() {
        if (null != userInfo) {
            return userInfo.name;
        }
        return "";
    }

    public float getRating() {
        if (null != userInfo) {
            return userInfo.rating;
        }
        return 0;
    }

    public String getContent() {
        if (null != shortComment && shortComment.commentId > 0) {
            return shortComment.content;
        }
        //如果是长影
        /*if (null != longComment && longComment.commentId > 0) {
            return longComment.content;
        }*/
        return "";
    }

    public long getCommentId() {
        if (null != shortComment && shortComment.commentId > 0) {
            return shortComment.commentId;
        }
        if (null != longComment && longComment.commentId > 0) {
            return longComment.commentId;
        }
        return 0;
    }

    public int getType() {
        if (null != shortComment && shortComment.commentId > 0) {
            return shortComment.type;
        }
        if (null != longComment && longComment.commentId > 0) {
            return longComment.type;
        }
        return 0;
    }

    public static class Review implements IObfuscateKeepAll {

        public long commentId; // 20826322
        //评论id
        public int type; // 1:长评 2：短评
        public String content; // "当前用户最新的评"

        // 短影片才有
        public String commentImg; // 图片

    }

    public static class UserInfo implements IObfuscateKeepAll {

        public float rating; // 8.5
        //当前用户的评分
        public String img; // "http://xxx.jpg"
        //当前用户的头像
        public String name; // "张胜男"//当前用户的昵称

        public int attitude = 1; //表态：  -1，未表态; 0, 看过; 1, 想看;

        public float impressionRating; // 印象分
        public float storyRating; // 故事分
        public float showRating; // 表演分
        public float directorRating; // 导演分
        public float pictureRating; // 画面分
        public float musicRating; // 音乐分

        /**
         * 使用总评分
         */
        public boolean useWholdScore() {
            return impressionRating <= 0 && storyRating <= 0 && showRating <= 0 &&
                    directorRating <= 0 && pictureRating <= 0 && musicRating <= 0;
        }
    }
}
