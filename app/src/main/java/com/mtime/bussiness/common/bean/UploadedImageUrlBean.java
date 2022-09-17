package com.mtime.bussiness.common.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author vivian.wei
 * @date 2019-11-25
 * @desc 获取上传图片后的访问地址url Bean
 */
public class UploadedImageUrlBean implements IObfuscateKeepAll {

    private boolean success;
    private String error;
    private String imgUrl;  // 图片访问地址url   http://img22.test.cn/up/2012/04/26/204102.54748234_o.jpg

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
