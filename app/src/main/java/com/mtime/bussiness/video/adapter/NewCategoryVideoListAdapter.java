package com.mtime.bussiness.video.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.widget.layout.OnVisibilityCallback;
import com.mtime.bussiness.common.utils.VideoUtils;
import com.mtime.bussiness.video.NewPreviewListPlayLogic;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.bussiness.video.view.VisibilityStateLinearLayout;
import com.mtime.player.PlayerHelper;

import java.util.List;

/**
 * Created by JiaJunHui on 2018/2/28.
 */

public class NewCategoryVideoListAdapter extends RecyclerView.Adapter<NewCategoryVideoListAdapter.VideoItemHolder> {

    private final Context mContext;
    private final List<CategoryVideosBean.RecommendVideoItem> mItems;
    private final RecyclerView mRecycler;

    private final int mItemH;

    private OnCategoryVideoListListener onCategoryVideoListListener;

    private final NewPreviewListPlayLogic mListPlayLogic;

    public void setOnCategoryVideoListListener(OnCategoryVideoListListener onCategoryVideoListListener) {
        this.onCategoryVideoListListener = onCategoryVideoListListener;
    }

    public NewCategoryVideoListAdapter(Context context, List<CategoryVideosBean.RecommendVideoItem> list, RecyclerView recyclerView) {
        this.mContext = context;
        this.mItems = list;
        this.mRecycler = recyclerView;
        mItemH = (int) (PlayerHelper.getScreenMinW(context) * 9.0f / 16);
        mListPlayLogic = new NewPreviewListPlayLogic(recyclerView, this, list);
    }

    public NewPreviewListPlayLogic getListPlayLogic() {
        return mListPlayLogic;
    }

    public void notifyListInsert(int positionStart, int itemCount) {
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public VideoItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new VideoItemHolder(View.inflate(mContext, R.layout.item_category_video, null));
    }

    @Override
    public void onBindViewHolder(VideoItemHolder holder, int position) {
        CategoryVideosBean.RecommendVideoItem item = getItem(position);

        holder.playerContainer.removeAllViews();

        onAdjustLayoutInfo(holder, item, position);
        onBindVideoInfo(holder, item, position);
        onBindTextInfo(holder, item);
        onBindListener(holder, item, position);
    }

    private void onBindVideoInfo(VideoItemHolder holder, CategoryVideosBean.RecommendVideoItem item, final int position) {
        //display album cover
        ImageHelper.with(ImageProxyUrl.SizeType.ORIGINAL_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_AND_HEIGHT)
                .view(holder.albumCover).placeholder(R.drawable.default_mtime).load(item.getImage()).showload();
        //video time
        holder.videoTime.setText(VideoUtils.getTime(VideoUtils.FORMAT_TYPE_01, item.getLength(), "片长", null));
        //title
        holder.title.setText(item.getTitle());
        holder.albumCoverContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListPlayLogic.play(position);
            }
        });
    }

    private void onAdjustLayoutInfo(VideoItemHolder holder, CategoryVideosBean.RecommendVideoItem item, int position) {
        ViewGroup.LayoutParams layoutParams = holder.topContainer.getLayoutParams();
        layoutParams.height = mItemH;
        holder.topContainer.setLayoutParams(layoutParams);
    }

    private void onBindListener(final VideoItemHolder holder, final CategoryVideosBean.RecommendVideoItem item, final int position) {
        if (onCategoryVideoListListener != null) {
            holder.bottomLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCategoryVideoListListener.onToDetailPage(false, item, position);
                }
            });

            holder.commentInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCategoryVideoListListener.onToDetailPage(true, item, position);
                }
            });

            holder.praiseInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UserManager.Companion.getInstance().isLogin()) {
                        onCategoryVideoListListener.onNeedLogin();
                        return;
                    }
                    boolean praise = !item.isPraised();
                    item.setPraised(praise);
                    handlePraise(holder, item, praise);
                    onCategoryVideoListListener.onPraiseHandle(praise, item, position);
                }
            });
//            holder.share.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onCategoryVideoListListener.onShare(item, position);
//                }
//            });

            OnVisibilityCallback.Tag tag = new OnVisibilityCallback.Tag();
            tag.data = item;
            tag.position = position;

            holder.stateLayout.setOnVisibilityListener(new OnVisibilityCallback(tag) {
                @Override
                protected void onShow(Tag tag) {
                    onCategoryVideoListListener.onItemVisibleChange(true, tag);
                }

                @Override
                protected void onHidden(Tag tag) {
                    onCategoryVideoListListener.onItemVisibleChange(false, tag);
                }
            });
        }
    }

    private void onBindTextInfo(VideoItemHolder holder, CategoryVideosBean.RecommendVideoItem item) {
        holder.title.setText(item.getTitle());
        holder.playCountInfo.setText(item.getPlayCount());
        holder.commentInfo.setText(TextUtils.isEmpty(item.getCommentTotal()) ? "0" : item.getCommentTotal());
        onBindPraiseInfo(holder, item);
    }

    private void handlePraise(VideoItemHolder holder, CategoryVideosBean.RecommendVideoItem item, boolean praise) {
        long praiseCount = 0L;
        try {
            praiseCount = item.getUpCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (praise) {
            praiseCount++;
        } else {
            if (praiseCount > 0) {
                praiseCount--;
            }
        }
        item.setPraiseInfo(String.valueOf(praiseCount));
        item.setUpCount(praiseCount);
        item.setCurrentUserPraise(praise ? 1L : 0L);
        onBindPraiseInfo(holder, item);
    }

    private void onBindPraiseInfo(VideoItemHolder holder, CategoryVideosBean.RecommendVideoItem item) {
        boolean praise = item.isPraised();
        if (praise) {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.common_icon_thumb_up_light);
            holder.praiseInfo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        } else {
            Drawable drawable = ContextCompat.getDrawable(mContext, R.drawable.common_icon_thumb_up);
            holder.praiseInfo.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
        }
        holder.praiseInfo.setText(String.valueOf(item.getUpCount()));
        holder.praiseInfo.setTextColor(ContextCompat.getColor(mContext, praise ? R.color.color_20A0DA : R.color.color_8798AF));
    }

    private CategoryVideosBean.RecommendVideoItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public int getItemCount() {
        if (mItems == null)
            return 0;
        return mItems.size();
    }

    public static class VideoItemHolder extends RecyclerView.ViewHolder {

        public FrameLayout playerContainer;
        View topContainer;
        View albumCoverContainer;
        ImageView albumCover;
        TextView videoTime;
        VisibilityStateLinearLayout stateLayout;
        RelativeLayout bottomLayout;
        TextView title;
        TextView playCountInfo;
        TextView commentInfo;
        TextView praiseInfo;
//        ImageView share;

        public VideoItemHolder(View itemView) {
            super(itemView);
            playerContainer = itemView.findViewById(R.id.item_category_video_player_container);
            topContainer = itemView.findViewById(R.id.item_category_video_top_container);
            albumCoverContainer = itemView.findViewById(R.id.item_category_video_album_cover);
            albumCover = itemView.findViewById(R.id.layout_album_image_cover_album_image_iv);
            videoTime = itemView.findViewById(R.id.layout_album_image_cover_video_time_tv);
            stateLayout = itemView.findViewById(R.id.stateLayout);
            bottomLayout = itemView.findViewById(R.id.item_category_video_bottom_layout);
            title = itemView.findViewById(R.id.item_category_video_title_tv);
            playCountInfo = itemView.findViewById(R.id.item_category_video_play_count_tv);
            commentInfo = itemView.findViewById(R.id.item_category_video_comment_info_tv);
            praiseInfo = itemView.findViewById(R.id.item_category_video_praise_info_tv);
//            share = itemView.findViewById(R.id.item_category_video_share_iv);
        }

    }

    public interface OnCategoryVideoListListener {
        void onNeedLogin();

        void onToDetailPage(boolean commentClick, CategoryVideosBean.RecommendVideoItem item, int position);

        void onPraiseHandle(boolean praise, CategoryVideosBean.RecommendVideoItem item, int position);

        void onShare(CategoryVideosBean.RecommendVideoItem item, int position);

        void onItemVisibleChange(boolean show, OnVisibilityCallback.Tag tag);
    }

}
