package com.mtime.widgets.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import com.mtime.R;

/**
 * 自定义coverflow RecyclerView
 */
public class CoverFlowRecyclerView extends RecyclerView {

    private static final String TAG = "CoverFlowRecyclerView";
    public static final int VERTICAL = 1;
    public static final int HORIZONTAL = 2;

    private int current_position = 0;
    private int left_border_position = 0;
    private int right_border_position = 0;
    private int last_scroll_position = 0;

    private boolean flag = false;

    private CoverFlowItemListener coverFlowListener;
    private LinearLayoutManager layoutManager;

    private final Camera mCamera = new Camera();
    private final Camera mBorderCamera = new Camera();
    private final Matrix mMatrix = new Matrix();
    private final Matrix borderMatrix = new Matrix();
    /**
     * Paint object to draw with
     */
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private DisplayMetrics dm;

    public CoverFlowRecyclerView(Context context) {
        super(context);
        init();
    }

    public CoverFlowRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CoverFlowRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mPaint.setAntiAlias(true);
        setOrientation();
        this.setChildrenDrawingOrderEnabled(true);
        this.setOnScrollListener(new CoverFlowScrollListener());
        dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        try {
            // (top,left) is the pixel position of the child inside the list
            final int top = child.getTop();
            final int left = child.getLeft();
            // center point of child
            final int childCenterY = child.getHeight() / 2;
            final int childCenterX = child.getWidth() / 2;
            //center of list
            final int parentCenterX = getWidth() / 2;
            //center point of child relative to list
            final int absChildCenterX = child.getLeft() + childCenterX;
            //distance of child center to the list center

            final int distanceX = parentCenterX - absChildCenterX;

            prepareMatrix(mMatrix, distanceX, getWidth() / 2, child.getWidth());

            mMatrix.preTranslate(-childCenterX, -childCenterY);
            mMatrix.postTranslate(childCenterX, childCenterY);
            mMatrix.postTranslate(left, top);

            prepareBorderMatrix(borderMatrix, distanceX, getWidth() / 2, child.getWidth());
            borderMatrix.preTranslate(-childCenterX, -childCenterY);
            borderMatrix.postTranslate(childCenterX, childCenterY);
            borderMatrix.postTranslate(left, top);

//            Bitmap bkBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bk_cinema_showtime_border);
//            canvas.drawBitmap(bkBitmap, borderMatrix, mPaint);

//            child.getMatrix().set(mMatrix);
//            super.drawChild(canvas, child, drawingTime);

//            Bitmap bitmap = getChildDrawingCache(child);
            Bitmap bitmap = getViewBitmap(child);
            canvas.drawBitmap(bitmap, mMatrix, mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void prepareMatrix(final Matrix outMatrix, int distanceY, int r, int childWidth) {
        //clip the distance
        final int d = Math.min(r, Math.abs(distanceY));
        mCamera.save();
        if (d < childWidth) {
            float scaleZ;
            if (dm.density >= 2 && dm.density < 2.5) {
                scaleZ = 0.9f;
            } else if (dm.density >= 2.5 && dm.density < 3.5) {
                scaleZ = 0.6f;
            } else if (dm.density >= 3.5) {
                scaleZ = 0.45f;
            } else {
                scaleZ = 1.2f;
            }
            mCamera.translate(0, (d - childWidth) * 0.13f, (d - childWidth) * scaleZ);
        }
        mCamera.getMatrix(outMatrix);
        mCamera.restore();
    }

    private void prepareBorderMatrix(final Matrix outMatrix, int distanceY, int r, int childWidth) {
        //clip the distance
        final int d = Math.min(r, Math.abs(distanceY));
        mBorderCamera.save();
        if (d < childWidth) {
            float scaleZ;
            if (dm.density >= 2 && dm.density < 2.5) {
                scaleZ = 0.975f;
            } else if (dm.density >= 2.5 && dm.density < 3.5) {
                scaleZ = 0.65f;
            } else if (dm.density >= 3.5) {
                scaleZ = 0.4875f;
            } else {
                scaleZ = 1.3f;
            }
            mBorderCamera.translate(0, (d - childWidth) * 0.13f, (d - childWidth) * scaleZ);
        }
        mBorderCamera.getMatrix(outMatrix);
        mBorderCamera.restore();
    }

    private Bitmap getChildDrawingCache(final View child) {
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        return Bitmap.createBitmap(bitmap);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        int centerChild = childCount / 2;
        current_position = layoutManager.findFirstVisibleItemPosition() + centerChild;
        if (!flag) {
//            ((CinemaShowtimeMovieAdapter) getAdapter()).setBorder_position(centerChild);
            left_border_position = centerChild;
            right_border_position = getAdapter().getItemCount() - centerChild - 1;
            flag = true;
            scrollToCenter(current_position);
        }
        int rez = i;
        //find drawIndex by centerChild
        if (i > centerChild) {
            //below center
            rez = (childCount - 1) - i + centerChild;
        } else if (i == centerChild) {
            //center row
            //draw it last
            rez = childCount - 1;
        } else {
            //above center - draw as always
            // i < centerChild
            rez = i;
        }
        return rez;

    }

    public interface CoverFlowItemListener {
        void onItemChanged(int position);
    }

    public void setCoverFlowListener(CoverFlowItemListener coverFlowListener) {
        this.coverFlowListener = coverFlowListener;
    }

    public class CoverFlowScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                Log.i(TAG, "onScrollStateChanged");

                if (last_scroll_position != current_position) {
                    coverFlowListener.onItemChanged(current_position);
                    last_scroll_position = current_position;
                }
//                Log.i(TAG, "current_position:" + current_position);
                if (current_position > right_border_position) {
                    scrollToCenter(right_border_position);
                    return;
                }
                if (current_position < left_border_position) {
                    scrollToCenter(left_border_position);
                    return;
                }

                int first_position = layoutManager.findFirstVisibleItemPosition();
                View centerChild = CoverFlowRecyclerView.this.getChildAt(current_position - first_position);
                if (null == centerChild) {
                    return;
                }
                int[] location = new int[2];
                centerChild.getLocationInWindow(location);
                int centerItemX = location[0] + centerChild.getWidth() / 2;

                final int centerX = dm.widthPixels / 2;
                CoverFlowRecyclerView.this.smoothScrollBy(centerItemX - centerX, 0);
            }
        }
    }

    public void scrollToCenter(int position) {
        if (position <= right_border_position && position >= left_border_position) {
            int first_position = layoutManager.findFirstVisibleItemPosition();
            int current_position = position - first_position;
            View targetChild = this.getChildAt(current_position);
            if (null == targetChild) {
                return;
            }
            int[] location = new int[2];
            targetChild.getLocationInWindow(location);
            final int targetItemX = location[0] + targetChild.getWidth() / 2;

//            Display display = getDisplay();
//            final Point size = new Point();
//            display.getSize(size);
//            int width = size.x;
            final int centerX = dm.widthPixels / 2;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    CoverFlowRecyclerView.this.smoothScrollBy(targetItemX - centerX, 0);
                }
            });
        }
    }

    private void setOrientation() {
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(20, 0);
        this.setLayoutManager(layoutManager);
        this.addItemDecoration(itemDecoration);
    }

    private class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int leftPadding;
        private final int topPadding;

        public DividerItemDecoration(int leftPadding, int topPadding) {
            this.leftPadding = leftPadding;
            this.topPadding = topPadding;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (view.getId() == 0) {
                return;
            }
            outRect.left = leftPadding;
            outRect.top = topPadding;
        }
    }

    @Override
    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }
}