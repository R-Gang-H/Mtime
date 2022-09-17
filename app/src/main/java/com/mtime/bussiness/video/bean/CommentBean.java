package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.io.Serializable;
import java.util.List;

/**
 * 评论列表
 * Created by lys on 17/8/7.
 */

public class CommentBean extends MBaseBean implements Cloneable, Serializable {
    private int totalCount;
    private List<CommentItemeBean> list;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<CommentItemeBean> getList() {
        return list;
    }

    public void setList(List<CommentItemeBean> list) {
        this.list = list;
    }

    public static class CommentItemeBean extends MBaseBean implements Serializable {
        private int commentId;
        private String content;
        private int userId;
        private String nickname;
        private long enterTime;
        private String headImg;
        private int replyCount;
        private int PariseCount;
        private List<ReplysBean> replys;
        private boolean isPraise;
        private int vid;                //视频Id

        public boolean isPraise() {
            return isPraise;
        }

        public void setPraise(boolean praise) {
            isPraise = praise;
        }

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

        public long getEnterTime() {
            return enterTime;
        }

        public void setEnterTime(long enterTime) {
            this.enterTime = enterTime;
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

        public int getPariseCount() {
            return PariseCount;
        }

        public void setPariseCount(int pariseCount) {
            PariseCount = pariseCount;
        }

        public int getVid() {
            return vid;
        }

        public void setVid(int vid) {
            this.vid = vid;
        }

        public List<ReplysBean> getReplys() {
            return replys;
        }

        public void setReplys(List<ReplysBean> replys) {
            this.replys = replys;
        }

        public static class ReplysBean extends MBaseBean implements Serializable {
            private int replyId;
            private String userImage;
            private int userType;
            private long userId;
            private String nickname;
            private String targetNickname;
            private String content;
            private long enterTime;

            public int getUserType() {
                return userType;
            }

            public void setUserType(int userType) {
                this.userType = userType;
            }

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

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
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

    public Object clone() {
        CommentBean bean;
        try {
            bean = (CommentBean) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
        return bean;
    }
}
