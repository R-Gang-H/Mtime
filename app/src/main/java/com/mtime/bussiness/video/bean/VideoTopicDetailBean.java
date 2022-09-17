package com.mtime.bussiness.video.bean;

import com.mtime.base.bean.MBaseBean;

import java.util.List;

/**
 * Created by mtime on 2017/11/13.
 */

public class VideoTopicDetailBean extends MBaseBean {

    private int count;
    private String bgImg;
    private String barrageTip;
    private String title;
    private String pcTopImg;
    private String pcBottomImg;
    private String pcTopImgHeight;
    private String pcBottomImgHeight;
    private String pcBodyHeight;
    private String pcColor;
    private String intro;
    private List<Integer> goodsIds;
    private List<TopicItemBean> list;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBgImg() {
        return bgImg;
    }

    public void setBgImg(String bgImg) {
        this.bgImg = bgImg;
    }

    public String getBarrageTip() {
        return barrageTip;
    }

    public void setBarrageTip(String barrageTip) {
        this.barrageTip = barrageTip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPcTopImg() {
        return pcTopImg;
    }

    public void setPcTopImg(String pcTopImg) {
        this.pcTopImg = pcTopImg;
    }

    public String getPcBottomImg() {
        return pcBottomImg;
    }

    public void setPcBottomImg(String pcBottomImg) {
        this.pcBottomImg = pcBottomImg;
    }

    public String getPcTopImgHeight() {
        return pcTopImgHeight;
    }

    public void setPcTopImgHeight(String pcTopImgHeight) {
        this.pcTopImgHeight = pcTopImgHeight;
    }

    public String getPcBottomImgHeight() {
        return pcBottomImgHeight;
    }

    public void setPcBottomImgHeight(String pcBottomImgHeight) {
        this.pcBottomImgHeight = pcBottomImgHeight;
    }

    public String getPcBodyHeight() {
        return pcBodyHeight;
    }

    public void setPcBodyHeight(String pcBodyHeight) {
        this.pcBodyHeight = pcBodyHeight;
    }

    public String getPcColor() {
        return pcColor;
    }

    public void setPcColor(String pcColor) {
        this.pcColor = pcColor;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<Integer> getGoodsIds() {
        return goodsIds;
    }

    public void setGoodsIds(List<Integer> goodsIds) {
        this.goodsIds = goodsIds;
    }

    public List<TopicItemBean> getList() {
        return list;
    }

    public void setList(List<TopicItemBean> list) {
        this.list = list;
    }

    public static class TopicItemBean extends MBaseBean {
        private int vId;
        private String img;
        private int length;
        private String title;
        private boolean enable;
        private String playCount;
        private int sourceType;

        public int getVId() {
            return vId;
        }

        public void setVId(int vId) {
            this.vId = vId;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getPlayCount() {
            return playCount;
        }

        public void setPlayCount(String playCount) {
            this.playCount = playCount;
        }

        public int getSourceType() {
            return sourceType;
        }

        public void setSourceType(int sourceType) {
            this.sourceType = sourceType;
        }
    }

}
