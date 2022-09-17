package com.mtime.bussiness.daily.recommend.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;

import java.util.List;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-22
 */
public class DailyRecmdAdapter extends RecyclerView.Adapter<DailyRecmdAdapter.RecmdHolder> {

    private List<DailyRecommendBean> mRecmdList;
    private final Context mContext;
    private final LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private final int mPosterWidth;
    private final int mPosterHeight;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public DailyRecmdAdapter(Context context, int posterWidth, int posterHeight) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mPosterHeight = posterHeight;
        mPosterWidth = posterWidth;
    }

    @NonNull
    @Override
    public RecmdHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_daily_recommend_large, parent, false);
        return new RecmdHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecmdHolder holder, int position) {
        DailyRecommendBean bean = mRecmdList.get(position);
        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .override(mPosterWidth, mPosterHeight)
                .roundedCorners(5, 0)
                .load(bean.poster)
                .view(holder.largeIv)
                .placeholder(R.drawable.default_image)
                .showload();
//        holder.largeIv.setImageResource(R.drawable.ic_launcher);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.size(mRecmdList);
//        return 10;
    }

    public void setData(List<DailyRecommendBean> data) {
        mRecmdList = data;
        notifyDataSetChanged();
    }

    class RecmdHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView largeIv;

        RecmdHolder(View itemView) {
            super(itemView);
            largeIv = itemView.findViewById(R.id.item_daily_rec_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(getLayoutPosition());
            }
        }
    }
}
