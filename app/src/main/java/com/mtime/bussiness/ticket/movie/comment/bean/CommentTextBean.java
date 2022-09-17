package com.mtime.bussiness.ticket.movie.comment.bean;

import androidx.core.util.ObjectsCompat;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-21
 */
public class CommentTextBean extends BaseCommentBean {

    public String text = "";

    public int selection = 0;

    public CommentTextBean(String text) {
        this();
        this.text = text;
    }

    public CommentTextBean() {
        super(TYPE_TEXT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentTextBean that = (CommentTextBean) o;
        return ObjectsCompat.equals(text, that.text) && mType == that.mType;
    }

    @Override
    public int hashCode() {
        return ObjectsCompat.hash(text, mType);
    }

    @Override
    public int length() {
        return text == null ? 0 : text.length();
    }

    @Override
    public boolean isEmpty() {
        return length() <= 0;
    }

    @Override
    public BaseCommentBean copy() {
        return new CommentTextBean(text);
    }
}
