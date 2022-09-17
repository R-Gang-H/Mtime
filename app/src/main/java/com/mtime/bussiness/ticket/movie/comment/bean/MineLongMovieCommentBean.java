package com.mtime.bussiness.ticket.movie.comment.bean;

import com.mtime.base.bean.MBaseBean;
import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-10-10
 */
public class MineLongMovieCommentBean extends MBaseBean implements IObfuscateKeepAll {

    public String nickName; //昵称

    public String img; // 头像

    public String title; // 评论 标题

    public String body; // 主评论内容

    public float rating; // 评分

    public long commentId; // 评论id

    public long time; // 评论时间

    public String relatedName; // 电影名

    public String relateImg; // 电影海报

    public long relatedId; // 电影id
}
