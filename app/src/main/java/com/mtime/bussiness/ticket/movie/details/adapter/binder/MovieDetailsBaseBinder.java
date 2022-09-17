package com.mtime.bussiness.ticket.movie.details.adapter.binder;

import androidx.annotation.LayoutRes;

import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.details.holder.MovieDetailsHolder;
import com.mtime.frame.BaseStatisticHelper;

import me.drakeet.multitype.ItemViewBinder;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019-06-10
 */
public abstract class MovieDetailsBaseBinder<T> extends ItemViewBinder<T, BaseViewHolder>  {
    public static final int allId = -100;

    protected MovieDetailsHolder.OnJumpPageCallback mOnJumpPageCallback;
    //页面基础埋点帮忙类
    protected BaseStatisticHelper mBaseStatisticHelper;

    public MovieDetailsBaseBinder(MovieDetailsHolder.OnJumpPageCallback callback, BaseStatisticHelper helper) {
        this.mOnJumpPageCallback = callback;
        this.mBaseStatisticHelper = helper;
    }
}
