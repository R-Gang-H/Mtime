package com.mtime.bussiness.ticket.movie.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * 影人评分结果Bean
 */
public class PersonRatingResultBean implements IObfuscateKeepAll {

    private boolean success;    // 是否成功
    private String error;       // 错误信息

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

}
