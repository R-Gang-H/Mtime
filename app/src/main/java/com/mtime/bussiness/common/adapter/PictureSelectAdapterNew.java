package com.mtime.bussiness.common.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mtime.R;
import com.mtime.base.imageload.ImageHelper;
import com.mtime.base.utils.MToastUtils;
import com.mtime.beans.ImageBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-09-25
 */
public class PictureSelectAdapterNew extends RecyclerView.Adapter<PictureSelectAdapterNew.ViewHolder> {
    public static final int PIC_SELECT_MAX_NUM = 5;

    public interface OnImageSelectedListener {
        void notifyChecked(ImageBean selectBean);
    }

    public interface OnImageSelectedCountListener {
        int getImageSelectedCount();

        void onCameraClick();
    }

    private final Context context;
    private final LayoutInflater mInflater;
    public List<ImageBean> bean = new ArrayList<>();
    OnImageSelectedListener onImageSelectedListener;
    OnImageSelectedCountListener onImageSelectedCountListener;
    private final int mMaxSelect;

    public PictureSelectAdapterNew(Context context, OnImageSelectedCountListener onImageSelectedCountListener, int maxSelect) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.onImageSelectedCountListener = onImageSelectedCountListener;
        mMaxSelect = maxSelect;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup vg, int i) {
        View v = mInflater.inflate(R.layout.picture_selection_item, vg, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final int index = i;
        ImageBean ib = bean.get(index);

        viewHolder.imageView
                .setImageResource(R.drawable.default_image);

//        viewHolder.imageView.setTag(ib.path);
        if (index == 0) {
            viewHolder.imageView.setImageResource(R.drawable.camera);
            viewHolder.checkBox.setVisibility(View.GONE);
        } else {
            viewHolder.checkBox.setVisibility(View.VISIBLE);
            viewHolder.checkBox.setSelected(ib.isChecked);

            if (TextUtils.isEmpty(ib.path)) {
                viewHolder.imageView.setImageResource(R.drawable.default_image);
            } else {
                ImageHelper.with(context)
                        .load(ib.path)
                        .error(R.drawable.default_image)
                        .placeholder(R.drawable.default_image)
                        .view(viewHolder.imageView)
                        .showload();
            }
        }
    }

    @Override
    public int getItemCount() {
        return bean == null ? 0 : bean.size();
    }

    public void setList(List<ImageBean> bean) {
        this.bean = bean;
        this.notifyDataSetChanged();
    }

    public void setOnImageSelectedListener(
            OnImageSelectedListener onImageSelectedListener) {
        this.onImageSelectedListener = onImageSelectedListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        View checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.child_image);
            checkBox = itemView.findViewById(R.id.child_checkbox);
            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos < 0) {
                return;
            }
            if (pos == 0) {
                onImageSelectedCountListener.onCameraClick();
                return;
            }
            ImageBean ib = bean.get(pos);
            boolean isChecked = checkBox.isSelected();// 当前是否选中
            int count = onImageSelectedCountListener.getImageSelectedCount();
            if (count == mMaxSelect) {
                if (isChecked) {
                    ib.isChecked = false;
                    onImageSelectedListener.notifyChecked(ib);
                    checkBox.setSelected(false);
                } else {
                    MToastUtils.showShortToast(v.getContext().getString(R.string.picture_select_tips, mMaxSelect));
                }
            } else {
                ib.isChecked = !isChecked;
                checkBox.setSelected(!isChecked);
                onImageSelectedListener.notifyChecked(ib);
            }
        }
    }
}
