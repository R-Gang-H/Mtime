package com.mtime.bussiness.ticket.movie.comment.bean;

import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-19
 */
public class RatingMovieResultBean implements IObfuscateKeepAll {
    public float cr;//评论者评分，可能为空
    public float rTotalRating;// 印象分
    public float storyRating; // 故事分
    public float showRating; // 表演分
    public float directorRating; // 导演分
    public float pictureRating; // 画面分
    public float musicRating; // 音乐分
}
