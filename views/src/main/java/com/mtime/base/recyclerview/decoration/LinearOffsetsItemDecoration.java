package com.mtime.base.recyclerview.decoration;

import android.graphics.Rect;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class can only be used in the RecyclerView which use a LinearLayoutManager or
 * its subclass.
 */
public class LinearOffsetsItemDecoration extends RecyclerView.ItemDecoration {
    public static final int LINEAR_OFFSETS_HORIZONTAL = LinearLayoutManager.HORIZONTAL;
    public static final int LINEAR_OFFSETS_VERTICAL = LinearLayoutManager.VERTICAL;

    private final SparseArray<OffsetsCreator> mTypeOffsetsFactories = new SparseArray<>();
    private boolean mOffsetLast;

    @IntDef({
            LINEAR_OFFSETS_HORIZONTAL,
            LINEAR_OFFSETS_VERTICAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    @Orientation
    private int mOrientation;
    private int mItemOffsets;

    private int mFirstOffset = 0,mLastOffset = 0;


    /**
     * 默认情况下只控制RecyclerView中间部分的间距
     *
     * @param orientation
     */
    public LinearOffsetsItemDecoration(@Orientation int orientation) {
        this.mOrientation = orientation;
    }

    public void setOrientation(@Orientation int orientation) {
        this.mOrientation = orientation;
    }

    /**
     * 设置第一个position的offset值
     *
     * @param firstOffset
     */
    public void setFirstOffset(int firstOffset) {
        this.mFirstOffset = firstOffset;
    }

    /**
     * 设置最后一个position的offset值，项目中最后一个item的offset值有可能比item之间的间距大
     *
     * @param endOffset
     */
    public void setLastOffset(int endOffset) {
        this.mLastOffset = endOffset;
        this.mOffsetLast = true;
    }


    public void setItemOffsets(int itemOffsets) {
        this.mItemOffsets = itemOffsets;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPosition = parent.getChildAdapterPosition(view);

        if (mOrientation == LINEAR_OFFSETS_HORIZONTAL) {
            outRect.right = getDividerOffsets(parent, view);
        } else {
            outRect.bottom = getDividerOffsets(parent, view);
        }

        if (mOrientation == LINEAR_OFFSETS_HORIZONTAL) {
            outRect.left = adapterPosition == 0 ? mFirstOffset : 0;
        } else {
            outRect.top = adapterPosition == 0 ? mFirstOffset : 0;
        }

        if ((adapterPosition == parent.getAdapter().getItemCount() - 1) && mOffsetLast) {
            if (mOrientation == LINEAR_OFFSETS_HORIZONTAL) {
                outRect.right = mLastOffset;
            } else {
                outRect.bottom = mLastOffset;
            }
        }
    }

    private int getDividerOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final OffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.create(parent, adapterPosition);
        }

        return 0;
    }

    public void registerTypeOffset(int itemType, OffsetsCreator offsetsCreator) {
        mTypeOffsetsFactories.put(itemType, offsetsCreator);
    }

    public interface OffsetsCreator {
        int create(RecyclerView parent, int adapterPosition);
    }
}
