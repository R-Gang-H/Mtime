package com.mtime.base.recyclerview.decoration;

import android.graphics.Rect;
import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This class can only be used in the RecyclerView which use a GridLayoutManager
 * or StaggeredGridLayoutManager, but it's not always work for the StaggeredGridLayoutManager,
 * because we can't figure out which position should belong to the last column or the last row
 */
public class GridOffsetsItemDecoration extends RecyclerView.ItemDecoration {
    public static final int GRID_OFFSETS_HORIZONTAL = GridLayoutManager.HORIZONTAL;
    public static final int GRID_OFFSETS_VERTICAL = GridLayoutManager.VERTICAL;

    private final SparseArray<OffsetsCreator> mTypeOffsetsFactories = new SparseArray<>();

    @IntDef({
            GRID_OFFSETS_HORIZONTAL,
            GRID_OFFSETS_VERTICAL
    })
    @Retention(RetentionPolicy.SOURCE)
    private @interface Orientation {
    }

    @Orientation
    private int mOrientation;
    private int mVerticalItemOffsets;
    private int mHorizontalItemOffsets;

    private boolean mIsOffsetEdge;
    private boolean mIsOffsetLast;

    public GridOffsetsItemDecoration(@Orientation int orientation) {
        setOrientation(orientation);
        mIsOffsetLast = true;
        mIsOffsetEdge = true;
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }

    public void setVerticalItemOffsets(int verticalItemOffsets) {
        this.mVerticalItemOffsets = verticalItemOffsets;
    }

    public void setHorizontalItemOffsets(int horizontalItemOffsets) {
        this.mHorizontalItemOffsets = horizontalItemOffsets;
    }

    public void setOffsetEdge(boolean isOffsetEdge) {
        this.mIsOffsetEdge = isOffsetEdge;
    }

    public void setOffsetLast(boolean isOffsetLast) {
        this.mIsOffsetLast = isOffsetLast;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int adapterPosition = parent.getChildAdapterPosition(view);


        int horizontalOffsets = getHorizontalOffsets(parent, view);
        int verticalOffsets = getVerticalOffsets(parent, view);


        boolean isFirstRow = isTopEdge(adapterPosition,parent);
        boolean isLastRow = isBottomEdge(adapterPosition,parent);
        boolean isFirstColumn = isLeftEdge(adapterPosition, parent);
        boolean isLastColumn = isRightEdge(adapterPosition,parent);

        outRect.set(0, 0, horizontalOffsets, verticalOffsets);
        outRect.bottom = mOrientation != GRID_OFFSETS_VERTICAL && isLastRow ? 0 : verticalOffsets;
        outRect.right = mOrientation != GRID_OFFSETS_HORIZONTAL && isLastColumn ? 0 : horizontalOffsets;




      /*  if (mIsOffsetEdge) {
            outRect.top = isFirstRow ? verticalOffsets : 0;
            outRect.left = isFirstColumn ? horizontalOffsets : 0;
            outRect.right = horizontalOffsets;
            outRect.bottom = verticalOffsets;
        }*/

       /* if (!mIsOffsetLast) {
            if (mOrientation == GRID_OFFSETS_VERTICAL && isLastRow) {
                outRect.bottom = 0;
            } else if (mOrientation == GRID_OFFSETS_HORIZONTAL && isLastColumn) {
                outRect.right = 0;
            }
        }*/
    }

    private int getHorizontalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mHorizontalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final OffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createHorizontal(parent, adapterPosition);
        }

        return 0;
    }

    private int getVerticalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mVerticalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final OffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createVertical(parent, adapterPosition);
        }

        return 0;
    }



    private boolean isFirstColumn(int position, int spanCount, int childCount,RecyclerView parent) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {

            return position % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position < childCount - lastColumnCount;
        }
    }

    private boolean isLastColumn(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            return (position + 1) % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        }
    }

    private boolean isFirstRow(int position, int spanCount, int childCount,RecyclerView parent) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {

            return position < spanCount;
        } else {
            return position % spanCount == 0;
        }
    }

    private boolean isLastRow(int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        } else {
            return (position + 1) % spanCount == 0;
        }
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else {
            throw new UnsupportedOperationException("the GridDividerItemDecoration can only be used in " +
                    "the RecyclerView which use a GridLayoutManager or StaggeredGridLayoutManager");
        }
    }

    public void registerTypeDrawable(int itemType, OffsetsCreator offsetsCreator) {
        mTypeOffsetsFactories.put(itemType, offsetsCreator);
    }

    public interface OffsetsCreator {
        int createVertical(RecyclerView parent, int adapterPosition);

        int createHorizontal(RecyclerView parent, int adapterPosition);
    }


    public boolean isTopEdge(int childPosition,RecyclerView parent) {
        RecyclerView.LayoutManager  layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();

            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }

            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                return childSpanCount <= totalSpanCount;
            } else {
                return childSpanCount % totalSpanCount == 1;
            }


        }

        return false;
    }


    /**
     * 是否是最下边的item，以recyclerview的方向做参考
     *
     * @param childPosition
     * @return
     */
    public boolean isBottomEdge(int childPosition,RecyclerView parent) {
        RecyclerView.LayoutManager  layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();
            int itemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;
            int totalSpanCount = gridLayoutManager.getSpanCount();
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                //最后一行item的个数
                int lastRowCount = itemCount % totalSpanCount;
                if (lastRowCount == 0) {
                    lastRowCount = gridLayoutManager.getSpanCount();
                }
                return childSpanCount > itemCount - lastRowCount;
            } else {
                return childSpanCount % totalSpanCount == 0;
            }

        }
        return false;
    }


    /**
     * 是否是最左边的item，如果是竖向，表示左方，如果是横向，表示上边
     *
     * @param childPosition
     * @return
     */
    public boolean isLeftEdge(int childPosition,RecyclerView parent) {
        RecyclerView.LayoutManager  layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();
            int childSpanCount = 0;
            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                return childSpanCount % gridLayoutManager.getSpanCount() == 1 || childPosition == 0;
            } else {
                return childSpanCount <= totalSpanCount;
            }

        }

        return false;
    }


    /**
     * 是否是最右边的item，如果是竖向，表示右边，如果是横向表示下边
     *
     * @param childPosition
     * @return
     */
    public boolean isRightEdge(int childPosition,RecyclerView parent) {
        RecyclerView.LayoutManager  layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {

            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            GridLayoutManager.SpanSizeLookup spanSizeLookUp = gridLayoutManager.getSpanSizeLookup();

            int totalSpanCount = gridLayoutManager.getSpanCount();
            int totalItemCount = gridLayoutManager.getItemCount();
            int childSpanCount = 0;

            for (int i = 0; i <= childPosition; i++) {
                childSpanCount += spanSizeLookUp.getSpanSize(i);
            }
            if (mOrientation == GRID_OFFSETS_VERTICAL) {
                return childSpanCount % gridLayoutManager.getSpanCount() == 0;
            } else {
                int lastColumnSize = totalItemCount % totalSpanCount;
                if (lastColumnSize == 0) {
                    lastColumnSize = totalSpanCount;
                }
                return childSpanCount > totalItemCount - lastColumnSize;
            }

        }
        return false;
    }


}
