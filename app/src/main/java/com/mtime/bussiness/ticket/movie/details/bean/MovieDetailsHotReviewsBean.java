package com.mtime.bussiness.ticket.movie.details.bean;

import com.helen.obfuscator.IObfuscateKeepAll;
import com.mtime.base.utils.CollectionUtils;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-05-27
 *
 * 影片详情-长、短影评
 */
public class MovieDetailsHotReviewsBean implements IObfuscateKeepAll {
    public long movieId; //影片id,非服务器返回
    public String movieName; //影片名称，非服务器返回

    public int commentTotalCount; //长评和短评的总条数
    public String commentTotalCountShow; //长评和短评的总条数show
    public ShortReviewList mini;
    public LongReviewList plus;

    public boolean hasData() {
        return hasShortReview() || hasLongReview();
    }

    public boolean hasShortReview() {
        return null != mini && !CollectionUtils.isEmpty(mini.list);
    }

    public boolean hasLongReview() {
        return null != plus && !CollectionUtils.isEmpty(plus.list);
    }

    public ShortReviewList getShortReview() {
        if (hasShortReview()) {
            mini.movieId = movieId;
            mini.movieName = movieName;
            return mini;
        }
        return null;
    }

    public LongReviewList getLongReview() {
        if (hasLongReview()) {
            plus.movieId = movieId;
            plus.movieName = movieName;
            return plus;
        }
        return null;
    }

    public static class Review implements IObfuscateKeepAll {

        public int commentId; // 20826322
        //影评Id
        public String content; // "《变形金刚2》是一部完美的商业"
        //评论内容
        public String nickname; // "图宾根木匠"
        //评论者昵称
        public int commentDate; // 1251770940000
        //评论时间,unix时间戳，单位秒
        public String headImg; // "http://dimg.mtime.com/h/613/ODI0NjEz_32X32.jpg?m=10131045"
        //评论者头像
        public int replyCount; // 0
        //回复数
        public float rating; // 8.2
        //评论者评分，可能为空
        public String locationName; // "北京"
        //城市名

    }

    public static class ShortReview extends Review implements IObfuscateKeepAll {

        //微影评Id
        public boolean isHot; // true
        //城市名
        public int praiseCount; // 1
        //点赞数
        public boolean isPraise; // false
        //是否被当前用户点赞

    }

    public static class LongReview extends Review implements IObfuscateKeepAll {
        public String title; //影评title
        public boolean isWantSee; //是否想看
    }

    public static class ShortReviewList implements IObfuscateKeepAll {
        public long movieId; //影片id,非服务器返回
        public String movieName; //影片名称，非服务器返回

        public java.util.List<ShortReview> list;
        public int total; // 100
        //总数

    }

    public static class LongReviewList implements IObfuscateKeepAll {
        public long movieId; //影片id,非服务器返回
        public String movieName; //影片名称，非服务器返回

        public java.util.List<LongReview> list;
        public int total; //总数
        public boolean clientPublish; //当前用户是否发布过长评
    }
}
