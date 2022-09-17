package com.mtime.bussiness.daily.recommend.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.statistic.StatisticDataBuild;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.daily.recommend.bean.DailyRecommendBean;
import com.mtime.bussiness.daily.recommend.bean.HistoryMovieListBean;
import com.mtime.bussiness.daily.widget.RcmdHistoryFloatingItemDecoration;
import com.mtime.statistic.large.StatisticManager;
import com.mtime.statistic.large.dailyrecmd.StatisticDailyRecmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



/**
 * Created by mtime on 2018/8/8.
 * 今日推荐--历史推荐
 */

public class HistoryRcdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<HistoryMovieListBean> mContentList = new ArrayList<>();
    public static final int VIEW_TYPE_ITEM_SPERATE = 1110;
    private static final int VIEW_TYPE_ITEM_CONTENT = 1111;
    private int totalSize;

    public List<DailyRecommendBean> getmRcdList() {
        return mRcdList;
    }

    private final List<DailyRecommendBean> mRcdList = new ArrayList<>();
    private final RcmdHistoryFloatingItemDecoration mDecoration;
    private String mRefer;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private OnItemClickListener onItemClickListener;

    public HistoryRcdAdapter(final Context context, RcmdHistoryFloatingItemDecoration decoration) {
        mContext = context;
        this.mDecoration = decoration;
    }

    public void addList(final List<HistoryMovieListBean> list) {
        mContentList.addAll(list);
        for (HistoryMovieListBean bean : list) {
            if (!CollectionUtils.isEmpty(bean.rcmdList)) {
                totalSize += bean.rcmdList.size();
            }
            mRcdList.addAll(bean.rcmdList);
        }
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(final int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return mContentList.size() + totalSize;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_ITEM_SPERATE;
        }
        int size = 0;
        for (HistoryMovieListBean bean : mContentList) {
            size += bean.rcmdList.size() + 1;
            if (position == size) {
                return VIEW_TYPE_ITEM_SPERATE;
            }
        }
        return VIEW_TYPE_ITEM_CONTENT;
    }

    private int getGroupNum(int position) {
        int size = 0;
        int count = mContentList.size();
        for (int i = 0; i < count; i++) {
            HistoryMovieListBean item = mContentList.get(i);
            size += item.rcmdList.size() + 1;
            if (position + 1 <= size) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int pos = holder.getAdapterPosition();
        if (getItemViewType(pos) == VIEW_TYPE_ITEM_CONTENT) {
            int index = pos - getGroupNum(pos) - 1;
            if(index >= 0 && index < mRcdList.size()) {
                DailyRecommendBean item = mRcdList.get(index);
                HashMap<String, String> params = new HashMap<>(1);
                params.put(StatisticDailyRecmd.MOVIE_ID, item.movieId);
                StatisticPageBean assemble = StatisticDataBuild.assemble(mRefer, StatisticDailyRecmd.HISTORY_RECOMMEND_PN
                        , StatisticDailyRecmd.HISTORY_RCMD_LIST, null, StatisticDailyRecmd.POSTER,
                        null, StatisticDailyRecmd.SHOW, null, params);
                StatisticManager.getInstance().submit(assemble);
            }
        }
    }

    public void setRefer(String refer) {
        mRefer = refer;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == VIEW_TYPE_ITEM_SPERATE) {
                        return gridLayoutManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM_SPERATE) {
            TitleHolder titleHolder = new TitleHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.act_history_recommend_item_header, parent, false));
            return titleHolder;
        } else {
            ContentHolder holder = new ContentHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.act_history_recommend_item, parent, false));
            int width = (MScreenUtils.getScreenWidth() - MScreenUtils.dp2px(25)) / 3;
            ViewGroup.LayoutParams params = holder.bgIv.getLayoutParams();
            params.height = MScreenUtils.dp2px(135);
            params.width = width;
            holder.bgIv.setLayoutParams(params);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_ITEM_SPERATE) {
            TitleHolder titleHolder = (TitleHolder) holder;
            if (position == 0) {
                titleHolder.mSperator.setVisibility(View.GONE);
            } else {
                titleHolder.mSperator.setVisibility(View.VISIBLE);
            }
            int group = getGroupNum(position);
            String monthTitle = mContentList.get(group).monthShow;
            mDecoration.appendTitles(position, monthTitle);
        } else {
            ContentHolder contentHolder = (ContentHolder) holder;
            DailyRecommendBean item = mRcdList.get(position - getGroupNum(position) - 1);
            contentHolder.dateTv.setText(item.dailyMovieTime);
            ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE,
                    ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                    .placeholder(R.drawable.default_image)
                    .override(MScreenUtils.dp2px(110), MScreenUtils.dp2px(135))
                    .view(contentHolder.bgIv)
                    .load(item.poster)
                    .showload();
            contentHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != onItemClickListener) {
                        onItemClickListener.onItemClick(item);
                    }
                }
            });
        }
    }

    static class ContentHolder extends RecyclerView.ViewHolder {
        ImageView bgIv;
        TextView dateTv;

        public ContentHolder(View itemView) {
            super(itemView);
            bgIv = itemView.findViewById(R.id.act_history_recommend_item_bg_iv);
            dateTv = itemView.findViewById(R.id.act_history_recommend_item_date_tv);
        }
    }

    static class TitleHolder extends RecyclerView.ViewHolder {
        View mSperator;

        TitleHolder(View itemView) {
            super(itemView);
            mSperator = itemView.findViewById(R.id.act_history_recommend_item_divider_view);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DailyRecommendBean item);
    }
}
