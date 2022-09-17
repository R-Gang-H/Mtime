package com.mtime.bussiness.ticket.movie.details.bean;

import android.text.TextUtils;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-29
 *
 * 此实体类非api返回，而是手机从MovieDetailsBasic中取的story数据
 */
public class MovieDetailsIntro implements IObfuscateKeepAll {
    public String story; //影片剧情简介

    public boolean hasData() {
        return !TextUtils.isEmpty(story);
    }
}
