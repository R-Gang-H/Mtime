package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

/**
 * 视频分享ViewBean
 * Created by lys on 17/8/15.
 */

public class ShareViewBean extends MBaseBean {
    private EmailBean email;
    private MtimeBean mtime;
    private QqBean qq;
    private String smsDesc;
    private String url;
    private WeiboBean weibo;
    private WeixinBean weixin;

    public EmailBean getEmail() {
        return email;
    }

    public void setEmail(EmailBean email) {
        this.email = email;
    }

    public MtimeBean getMtime() {
        return mtime;
    }

    public void setMtime(MtimeBean mtime) {
        this.mtime = mtime;
    }

    public QqBean getQq() {
        return qq;
    }

    public void setQq(QqBean qq) {
        this.qq = qq;
    }

    public String getSmsDesc() {
        return smsDesc;
    }

    public void setSmsDesc(String smsDesc) {
        this.smsDesc = smsDesc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public WeiboBean getWeibo() {
        return weibo;
    }

    public void setWeibo(WeiboBean weibo) {
        this.weibo = weibo;
    }

    public WeixinBean getWeixin() {
        return weixin;
    }

    public void setWeixin(WeixinBean weixin) {
        this.weixin = weixin;
    }

    public static class EmailBean extends MBaseBean {

        private String desc;
        private String img;
        private String title;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static class MtimeBean extends MBaseBean {

        private String desc;
        private String img;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class QqBean extends MBaseBean {

        private String desc;
        private String img;
        private String title;
        private String url;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public static class WeiboBean extends MBaseBean {

        private String desc;
        private String img;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public static class WeixinBean extends MBaseBean {

        private String desc;
        private String img;
        private String title;
        private String url;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
