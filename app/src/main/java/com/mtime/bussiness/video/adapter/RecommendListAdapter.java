package com.mtime.bussiness.video.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.bussiness.common.utils.VideoUtils;
import com.mtime.bussiness.video.bean.CategoryVideosBean;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/4/2.
 */

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.RecommendItemHolder>{

    private final Context mContext;
    private final List<CategoryVideosBean.RecommendVideoItem> mItems;

    private OnRecommendListListener onRecommendListListener;

    private int checkIndex = -1;

    private CategoryVideosBean.Category mCategory;

    public RecommendListAdapter(Context context, List<CategoryVideosBean.RecommendVideoItem> list){
        this.mContext = context;
        this.mItems = list;
    }

    public void setCheckIndex(int checkIndex) {
        this.checkIndex = checkIndex;
    }

    public void setCategory(CategoryVideosBean.Category category){
        this.mCategory = category;
    }

    public void setOnRecommendListListener(OnRecommendListListener onRecommendListListener) {
        this.onRecommendListListener = onRecommendListListener;
    }

    @Override
    public RecommendItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecommendItemHolder(View.inflate(mContext, R.layout.item_category_recommend_list, null));
    }

    private boolean isCurrentPlayPos(int position){
        if(mCategory==null)
            return false;
        return checkIndex==position;
    }

    @Override
    public void onBindViewHolder(final RecommendItemHolder holder, final int position) {
        CategoryVideosBean.RecommendVideoItem item = getItem(position);
        //handle select bg
        holder.coverBg.setBackground(isCurrentPlayPos(position)?mContext.getResources().getDrawable(R.drawable.icon_category_video_list_item_bg):null);
        //display album cover
        ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .view(holder.cover).placeholder(R.drawable.default_mtime).load(item.getImage()).showload();
        //time length
        holder.timeLength.setText(VideoUtils.getTime(VideoUtils.FORMAT_TYPE_02, item.getLength(), null, null));
        //name
        holder.name.setText(item.getTitle());
        //play count
        holder.playCount.setText(item.getPlayCount());

        if(onRecommendListListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRecommendListListener.onItemClick(holder, position);
                }
            });
        }
    }

    public CategoryVideosBean.RecommendVideoItem getItem(int position){
        if(mItems==null)
            return null;
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        if(mItems==null)
            return 0;
        return mItems.size();
    }

    public static class RecommendItemHolder extends RecyclerView.ViewHolder{

        RelativeLayout coverBg;
        ImageView cover;
        TextView timeLength;
        TextView name;
        TextView playCount;

        public RecommendItemHolder(View itemView) {
            super(itemView);
            coverBg = itemView.findViewById(R.id.item_category_recommend_list_cover_bg_rl);
            cover = itemView.findViewById(R.id.item_category_recommend_list_cover_iv);
            timeLength = itemView.findViewById(R.id.item_category_recommend_list_time_length_tv);
            name = itemView.findViewById(R.id.item_category_recommend_list_name_tv);
            playCount = itemView.findViewById(R.id.item_category_recommend_list_play_count_tv);
        }
    }

    public interface OnRecommendListListener{
        void onItemClick(RecommendItemHolder holder, int position);
    }

}
