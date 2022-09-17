package com.mtime.bussiness.ticket.movie.comment.bean;

import androidx.core.util.ObjectsCompat;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-21
 */
public class CommentTitleBean extends BaseCommentBean {

    public String title;

    public boolean original = true; // 原创

    public boolean containMovieContent = false; // 包含剧透

    public CommentTitleBean(String title, boolean original, boolean containMovieContent) {
        this();
        this.title = title;
        this.original = original;
        this.containMovieContent = containMovieContent;
    }

    public CommentTitleBean() {
        super(TYPE_TITLE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentTitleBean that = (CommentTitleBean) o;
        return ObjectsCompat.equals(title, that.title) &&
                mType == that.mType;
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(title, original, containMovieContent, mType);
    }

    @Override
    public boolean isEmpty() {
        return title == null || title.trim().isEmpty();
    }

    @Override
    public BaseCommentBean copy() {
        return new CommentTitleBean(title, original, containMovieContent);
    }
}
