package com.mtime.bussiness.ticket.movie.comment.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

import java.util.List;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-25
 */
public class ShareMovieCommentBean implements IObfuscateKeepAll {
    public String name;
    public String nameEn;
    public List<MovieStillBean> movieStills; // 影片剧照

    public String userName;
    public String userImage;
    public String userRating;//用户评分
    public String userComment;
    public String commentDateTime;
    public String type;
    public String actors;
    public String rating;//电影评分
    public float fRating;
    public int ratingCount;
    public int joinCount;
    public int signCount;
    public String joinCountShow;
    public String signCountShow;
    public String longCommentURL;
    public String longCommentRcmdMsg;
    public String movieDetailURL;
    public String movieDetailRcmdMsg;

    public boolean sensitiveStatus; // 敏感影片
}
