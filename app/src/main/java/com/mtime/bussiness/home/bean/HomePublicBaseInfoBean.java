package com.mtime.bussiness.home.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by ZhouSuQiang on 2017/12/5.
 * 公众号基本信息实体
 */

public class HomePublicBaseInfoBean implements IObfuscateKeepAll {
    public long publicId; //公众号id（long类型）
    public String avatar; //公众号头像
    public String name; //公众号名称
    
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
}
