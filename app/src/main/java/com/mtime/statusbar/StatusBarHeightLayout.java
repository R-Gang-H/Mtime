package com.mtime.statusbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.util.ObjectsCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.AttributeSet;

import com.mtime.R;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/4/11
 * <p>
 * 自定义一个View ,用来做状态栏高度占位
 * 适配低于4.4时 占位View的高度为0 所以不可见
 */
public class StatusBarHeightLayout extends ConstraintLayout {


    public static final int SBHL_USE_TYPE_HEIGHT = 0;
    public static final int SBHL_USE_TYPE_PADDING = 1;
    public static final int SBHL_USE_TYPE_PADDING_EXCLUDE_TOP = 2;
    public static final int SBHL_USE_TYPE_PADDING_EXCLUDE_TOP_DRAW_STATUS_BAR = 3;

    private int statusBarHeight;
    private int type = SBHL_USE_TYPE_PADDING;
    private WindowInsetsCompat mLastInsets;
    private int mOriginPaddingLeft;
    private int mOriginPaddingTop;
    private int mOriginPaddingRight;
    private int mOriginPaddingBottom;

    private ColorDrawable mStatusDrawable;

    public void setUseType(int useType) {
        if (useType != type) {
            type = useType;
            if (type < SBHL_USE_TYPE_HEIGHT || type > SBHL_USE_TYPE_PADDING_EXCLUDE_TOP_DRAW_STATUS_BAR) {
                type = SBHL_USE_TYPE_PADDING;
            }
            requestLayout();
        }
    }

    public StatusBarHeightLayout(Context context) {
        super(context);
        init(null);
    }

    public StatusBarHeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public StatusBarHeightLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
        } else {
            //低版本 直接设置0
            statusBarHeight = 0;
        }

        int color = Color.WHITE;
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.StatusBarHeightLayout);
            type = typedArray.getInt(R.styleable.StatusBarHeightLayout_sbhl_use_type, SBHL_USE_TYPE_PADDING);
            color = typedArray.getColor(R.styleable.StatusBarHeightLayout_sbhl_status_bar_color, color);
            typedArray.recycle();
        }
        mStatusDrawable = new ColorDrawable(color);
        mStatusDrawable.setCallback(this);

        if (!ViewCompat.getFitsSystemWindows(this)) {
            return;
        }
        mOriginPaddingLeft = getPaddingLeft();
        mOriginPaddingTop = getPaddingTop();
        mOriginPaddingRight = getPaddingRight();
        mOriginPaddingBottom = getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(this, (v, insets) -> setWindowInsets(insets));
    }

    // 这里是为了 兼容 contentholder 移除 view 导致的 onApplyInsets 不触发的问题
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 在 view 被添加到 window 的时候，请求一下 insets
        ViewCompat.requestApplyInsets(this);
    }

    private WindowInsetsCompat setWindowInsets(WindowInsetsCompat insets) {
        if (!ObjectsCompat.equals(mLastInsets, insets)) {
            mLastInsets = insets;

            int top = mOriginPaddingTop;
            top += computeTop();
            super.setPadding(mOriginPaddingLeft + insets.getSystemWindowInsetLeft(),
                    top,
                    mOriginPaddingRight + insets.getSystemWindowInsetRight(),
                    mOriginPaddingBottom + insets.getSystemWindowInsetBottom());
        }
        return insets.consumeSystemWindowInsets();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mOriginPaddingLeft = left;
        mOriginPaddingTop = top;
        mOriginPaddingRight = right;
        mOriginPaddingBottom = bottom;
        if (mLastInsets != null) {
            WindowInsetsCompat insets = mLastInsets;
            left += insets.getSystemWindowInsetLeft();
            top += computeTop();
            right += insets.getSystemWindowInsetRight();
            bottom += insets.getSystemWindowInsetBottom();
        }
        super.setPadding(left, top, right, bottom);
    }

    private int computeTop() {
        if (type != SBHL_USE_TYPE_PADDING_EXCLUDE_TOP && type != SBHL_USE_TYPE_PADDING_EXCLUDE_TOP_DRAW_STATUS_BAR) {
            return mLastInsets.getSystemWindowInsetTop();
        }
        return 0;
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (mStatusDrawable != null) {
            mStatusDrawable.setVisible(isVisible, false);
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable dr) {
        return dr == mStatusDrawable || super.verifyDrawable(dr);
    }

    public int getStatusBarColor() {
        return mStatusDrawable.getColor();
    }

    public void setStatusBarColor(@ColorInt int color) {
        if (mStatusDrawable.getColor() != color) {
            mStatusDrawable.setColor(color);
        }
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        super.invalidateDrawable(drawable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (type == SBHL_USE_TYPE_HEIGHT) {
            if (mLastInsets != null) {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(mLastInsets.getSystemWindowInsetTop(), MeasureSpec.EXACTLY);
            } else {
                heightMeasureSpec = MeasureSpec.makeMeasureSpec(statusBarHeight, MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (SBHL_USE_TYPE_PADDING_EXCLUDE_TOP == type) {
            return;
        }
        int h;
        if (mLastInsets == null) {
            h = statusBarHeight;
        } else {
            h = mLastInsets.getSystemWindowInsetTop();
        }
        if (h > 0) {
            mStatusDrawable.setBounds(0, 0, getWidth(), h);
            mStatusDrawable.draw(canvas);
        }
    }
}
