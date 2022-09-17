package com.mtime.bussiness.mine.history.adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.aspsine.irecyclerview.IViewHolder;
import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.imageload.ImageProxyUrl;
import com.mtime.base.utils.CollectionUtils;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.mine.history.dao.HistoryDao;
import com.mtime.bussiness.mine.history.widget.SwipeLayout;
import com.mtime.frame.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vivian.wei on 2018/8/8.
 * 个人中心-阅读历史Adapter
 */

public class ReadHistoryAdapter extends RecyclerView.Adapter<ReadHistoryAdapter.ViewHolder> {

    private static final int IMG_W_DP = 105;
    private static final int IMG_H_DP = 59;
    private static final int IMG_RADIUS = 4;

    private final BaseActivity mContext;
    private final List<HistoryDao> mList = new ArrayList<>();
    private OnItemContentClickListener mOnItemContentClickListener;
    private OnItemDelClickListener mOnItemDelClickListener;
    private boolean mIsEdit = false;    // 是否编辑状态

    public ReadHistoryAdapter(final BaseActivity context) {
        mContext = context;
    }

    public void setOnItemClickListener(OnItemContentClickListener onItemContentClickListener) {
        mOnItemContentClickListener = onItemContentClickListener;
    }

    public void setOnItemDelClickListener(OnItemDelClickListener onItemDelClickListener) {
        mOnItemDelClickListener = onItemDelClickListener;
    }

    public void clearList() {
        if(null != mList) {
            mList.clear();
        }
    }

    public void addList(final List<HistoryDao> list) {
        mList.addAll(list);
    }

    @Override
    public long getItemId(final int arg0) {
        return arg0;
    }

    @Override
    public int getItemCount() {
        return CollectionUtils.isEmpty(mList) ? 0 : mList.size();
    }

    // 设置编辑状态
    public void setIsEdit(boolean isEdit) {
        mIsEdit = isEdit;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_my_read_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(CollectionUtils.isEmpty(mList)) {
            return;
        }

        HistoryDao bean = mList.get(position);
        if(null == bean) {
            return;
        }

        holder.mSwipeLayout.setSupportSwipe(!mIsEdit);
        holder.checkbox.setVisibility(mIsEdit ? View.VISIBLE : View.GONE);
        holder.checkbox.setChecked(bean.isSelect());
        //加载图片
        ImageHelper.with(mContext, ImageProxyUrl.SizeType.CUSTOM_SIZE, ImageProxyUrl.ClipType.FIX_WIDTH_TRIM_HEIGHT)
                .override(MScreenUtils.dp2px(IMG_W_DP), MScreenUtils.dp2px(IMG_H_DP))
                .roundedCorners(IMG_RADIUS, 0)
                .view(holder.imgIv)
                .load(bean.getImg())
                .placeholder(R.drawable.default_image)
                .showload();

        //设置播放icon
        if(bean.isShowVideoIcon()) {
            holder.imgIv.setForegroundResource(R.drawable.my_read_history_play_icon);
        } else {
            holder.imgIv.setForeground(null);
        }

        holder.titleTv.setText(bean.getTitle());
        holder.publicNameTv.setText(bean.getPublicName());
        holder.publicNameTv.setVisibility(TextUtils.isEmpty(bean.getPublicName()) ? View.INVISIBLE : View.VISIBLE);

        // 内容区
        holder.contentll.setOnClickListener((v) -> {
            if(mIsEdit) {
                if(null != mOnItemContentClickListener) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mOnItemContentClickListener.onItemContentSelectClick(v, holder.getIAdapterPosition(), !bean.isSelect());
                }
            } else {
                if(SwipeLayout.isOpen()) {
                    SwipeLayout.closes();
                    return;
                }

                if(null != mOnItemContentClickListener) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mOnItemContentClickListener.onItemContentJumpClick(v, holder.getIAdapterPosition());
                }
            }
        });

        // 删除
        if(mOnItemDelClickListener != null) {
            holder.delll.setOnClickListener((v) -> {
                SwipeLayout.closes(false);
                //注意，这里的position不要用上面参数中的position，会出现位置错乱
                mOnItemDelClickListener.onItemDelClick(v, holder.getIAdapterPosition());
            });
        }

        holder.mSwipeLayout.setOnSwipeChangeListener(new SwipeLayout.OnSwipeChangeListener() {
            @Override
            public void onDraging(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onOpen(SwipeLayout mSwipeLayout) {
                if(null != mOnItemContentClickListener) {
                    //注意，这里的position不要用上面参数中的position，会出现位置错乱
                    mOnItemContentClickListener.onItemContentSwipeOpen(mSwipeLayout, holder.getIAdapterPosition());
                }
            }

            @Override
            public void onClose(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartOpen(SwipeLayout mSwipeLayout) {

            }

            @Override
            public void onStartClose(SwipeLayout mSwipeLayout) {

            }
        });
    }

    public class ViewHolder extends IViewHolder {

        View contentll;
        CheckedTextView checkbox;
        com.mtime.base.views.ForegroundImageView imgIv;
        TextView titleTv;
        TextView publicNameTv;
        View delll;
        SwipeLayout mSwipeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            contentll = itemView.findViewById(R.id.item_my_read_history_content_ll);
            checkbox = itemView.findViewById(R.id.item_my_read_history_checkbox);
            imgIv = itemView.findViewById(R.id.item_my_read_history_img_iv);
            titleTv = itemView.findViewById(R.id.item_my_read_history_title_tv);
            publicNameTv = itemView.findViewById(R.id.item_my_read_history_public_name_tv);
            delll = itemView.findViewById(R.id.item_my_read_history_del_ll);
            mSwipeLayout = itemView.findViewById(R.id.item_my_read_history_swipemenu);
        }
    }

    public interface OnItemContentClickListener {
        // 编辑状态：选中/取消选中一条中的内容区
        void onItemContentSelectClick(View view, int position, boolean isSelect);
        // 非编辑状态：点击一条跳转
        void onItemContentJumpClick(View view, int position);
        // 非编辑状态：侧滑显示删除按钮
        void onItemContentSwipeOpen(SwipeLayout mSwipeLayout, int position);
    }

    public interface OnItemDelClickListener {
        void onItemDelClick(View view, int position);
    }
}
