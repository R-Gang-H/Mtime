package com.mtime.bussiness.ticket.movie.details.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.mtime.R;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.bussiness.home.original.bean.HomeOriginalFeedItemBean;
import com.mtime.bussiness.home.original.widget.HomeOriginalFeedView;

import java.util.List;

/**
 * @author vivian.wei
 * @date 2019/6/10
 * @desc 影片时光原创列表Adapter
 */
public class MovieOriginalAdapter extends BaseQuickAdapter<HomeOriginalFeedItemBean, BaseViewHolder> {

    private final Activity mActivity;
    private final HomeOriginalFeedView.OnFeedItemClickListener mOnFeedItemClickListener;
    private final OnMovieOriginalAdapterListener mOnMovieOriginalAdapterListener;

    public MovieOriginalAdapter(Activity activity, @Nullable List<HomeOriginalFeedItemBean> data,
                                HomeOriginalFeedView.OnFeedItemClickListener onFeedItemClickListener,
                                OnMovieOriginalAdapterListener onMovieOriginalAdapterListener ) {
        super(R.layout.item_movie_original, data);
        mActivity = activity;
        mOnFeedItemClickListener = onFeedItemClickListener;
        mOnMovieOriginalAdapterListener = onMovieOriginalAdapterListener;
    }

    @Override
    protected void convert(BaseViewHolder holder, HomeOriginalFeedItemBean bean) {
        HomeOriginalFeedView homeOriginalFeedView = holder.getView(R.id.item_movie_original_homeoriginalfeedview);
        homeOriginalFeedView.setData(mActivity, bean, holder.getAdapterPosition());
        if(mOnFeedItemClickListener != null) {
            homeOriginalFeedView.setOnFeedItemClickListener(mOnFeedItemClickListener);
        }

    }

    @Override
    public void onViewAttachedToWindow(BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(mOnMovieOriginalAdapterListener != null && CollectionUtils.isNotEmpty(getData())) {
            int position = holder.getAdapterPosition();
            HomeOriginalFeedItemBean bean = getData().get(position);
            mOnMovieOriginalAdapterListener.onShow(holder.getAdapterPosition(), bean);
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public interface OnMovieOriginalAdapterListener {
        void onShow(int position, HomeOriginalFeedItemBean bean);
    }
}
