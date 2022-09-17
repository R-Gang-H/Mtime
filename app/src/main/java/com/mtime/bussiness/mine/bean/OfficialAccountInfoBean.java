package com.mtime.bussiness.mine.bean;


import com.mtime.base.bean.MBaseBean;

public class OfficialAccountInfoBean  extends MBaseBean {
    private int publicId;
    private String avatar;
    private String name;
    private String desc;
    private long contentCount;
    private long attentionCount;
    private boolean hasAttention;

    public int getPublicId() {
        return publicId;
    }

    public void setPublicId(int publicId) {
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

    public long getContentCount() {
        return contentCount;
    }

    public void setContentCount(long contentCount) {
        this.contentCount = contentCount;
    }

    public long getAttentionCount() {
        return attentionCount;
    }

    public void setAttentionCount(long attentionCount) {
        this.attentionCount = attentionCount;
    }

    public boolean isHasAttention() {
        return hasAttention;
    }

    public void setHasAttention(boolean hasAttention) {
        this.hasAttention = hasAttention;
    }
}
