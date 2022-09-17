package com.mtime.bussiness.ticket.movie.comment.bean;

import com.mtime.base.bean.MBaseBean;
import com.helen.obfuscator.IObfuscateKeepAll;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-05-21
 */
public abstract class BaseCommentBean extends MBaseBean implements IObfuscateKeepAll {

    public static final int TYPE_UNKNOWN = -1;
    public static final int TYPE_SCORE = 0;
    public static final int TYPE_TITLE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int TYPE_IMAGE = 3;

    int mType;

    private int mId;

    public boolean showInput = false;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public boolean hasId() {
        return mId > 0;
    }

    BaseCommentBean(int type) {
        this.mType = type;
    }

    public int getType() {
        return mType;
    }

    public int length() {
        return 0;
    }

    public abstract boolean isEmpty();

    public abstract BaseCommentBean copy();
}
