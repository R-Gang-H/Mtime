package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * 专题评论详情
 * Created by lys on 17/8/18.
 */

public class SubjectReviewInfoBean extends MBaseBean {
    private ParentCommentBean parentComment;
    private int replyTotalCount;
    private List<ReplysBean> replys;

    public ParentCommentBean getParentComment() {
        return parentComment;
    }

    public void setParentComment(ParentCommentBean parentComment) {
        this.parentComment = parentComment;
    }

    public int getReplyTotalCount() {
        return replyTotalCount;
    }

    public void setReplyTotalCount(int replyTotalCount) {
        this.replyTotalCount = replyTotalCount;
    }

    public List<ReplysBean> getReplys() {
        return replys;
    }

    public void setReplys(List<ReplysBean> replys) {
        this.replys = replys;
    }

    public static class ParentCommentBean extends MBaseBean {
        private int commentId;
        private String content;
        private int userId;
        private String nickname;
        private int enterTime;
        private String headImg;

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

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getEnterTime() {
            return enterTime;
        }

        public void setEnterTime(int enterTime) {
            this.enterTime = enterTime;
        }

        public String getHeadImg() {
            return headImg;
        }

        public void setHeadImg(String headImg) {
            this.headImg = headImg;
        }
    }

    public static class ReplysBean extends MBaseBean {
        /**
         * replyId : 5652
         * userImage : http://img32.test.cn/up/2013/06/21/142349.29011620_o.jpg
         * userId : 5652
         * nickname : 莎莎123
         * targetNickname : 回复给莎莎123
         * content : 环境不错 测试 呵呵
         * enterTime : 13746026201231
         */

        private int replyId;
        private String userImage;
        private int userId;
        private String nickname;
        private String targetNickname;
        private String content;
        private long enterTime;
        private boolean isPraised;
        private int userType;

        public int getUserType() {
            return userType;
        }

        public void setUserType(int userType) {
            this.userType = userType;
        }

        public boolean isPraised() {
            return isPraised;
        }

        public void setPraised(boolean praised) {
            isPraised = praised;
        }

        public int getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(int praiseCount) {
            this.praiseCount = praiseCount;
        }

        private int praiseCount;
        public int getReplyId() {
            return replyId;
        }

        public void setReplyId(int replyId) {
            this.replyId = replyId;
        }

        public String getUserImage() {
            return userImage;
        }

        public void setUserImage(String userImage) {
            this.userImage = userImage;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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

        public long getEnterTime() {
            return enterTime;
        }

        public void setEnterTime(long enterTime) {
            this.enterTime = enterTime;
        }
    }
}
