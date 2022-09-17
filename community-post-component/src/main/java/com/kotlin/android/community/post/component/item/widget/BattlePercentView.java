package com.kotlin.android.community.post.component.item.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.kotlin.android.community.post.component.R;


/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-03-19
 */
public class BattlePercentView extends View {

    private ValueAnimator mAnimator;
    private final TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

    public BattlePercentView(Context context) {
        super(context);
        init(context, null);
    }

    public BattlePercentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BattlePercentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private int mPositiveColor;
    private float mPositivePercent;
    private int mNegativeColor;
    private float mNegativePercent;
    private Paint mPaint;
    private final RectF mBounds = new RectF();
    private final RectF mTmpBounds = new RectF();
    private float mRadius;

    private float mCurPositivePercent;
    private float mCurNegativePercent;
    private PercentUpdateCallback mPercentUpdateCallback;
    private final Path mPath = new Path();
    private boolean mOneShoot = false;

    /**
     * 初始化 view
     */
    private void init(Context context, @Nullable AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BattlePercentView);
        mPositiveColor = a.getColor(R.styleable.BattlePercentView_positive_color, Color.RED);
        mNegativeColor = a.getColor(R.styleable.BattlePercentView_negative_color, Color.BLUE);
        mRadius = a.getDimension(R.styleable.BattlePercentView_android_radius, 0);

        a.recycle();

        if (mRadius < 0) {
            mRadius = 0;
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        if (isInEditMode()) {
            mCurPositivePercent = 0.33f;
            mCurNegativePercent = 0.67f;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBounds.set(0, 0, w, h);
        mPath.reset();
        mPath.addRoundRect(mBounds, mRadius, mRadius, Path.Direction.CW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float positiveW = mBounds.width() * mCurPositivePercent;
        float negativeW = mBounds.width() * mCurNegativePercent;

        int sc = canvas.save();
        canvas.clipPath(mPath);

        mTmpBounds.set(0, 0, positiveW, mBounds.bottom);
        mPaint.setColor(mPositiveColor);
        canvas.drawRect(mTmpBounds, mPaint);

        mTmpBounds.set(mBounds.width() - negativeW, 0, mBounds.right, mBounds.bottom);
        mPaint.setColor(mNegativeColor);
        canvas.drawRect(mTmpBounds, mPaint);

        canvas.restoreToCount(sc);
    }

    public BattlePercentView setPercent(float positive, float negative) {
        if (negative >= 0 && mNegativePercent != negative) {
            mNegativePercent = negative;
        }
        if (positive >= 0 && mPositivePercent != positive) {
            mPositivePercent = positive;
        }
        return this;
    }

    public BattlePercentView setPercent(int positive, int negative) {
        float pos = positive * 1f / 100;
        float neg = negative * 1f / 100;
        setPercent(pos, neg);
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnimator != null && (mAnimator.isRunning() || mAnimator.isStarted())) {
            mAnimator.end();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
    }

    public void startAnim() {
        if (mOneShoot) {
            mCurPositivePercent = mPositivePercent;
            mCurNegativePercent = mNegativePercent;
            invalidate();
            if (mPercentUpdateCallback != null) {
                mPercentUpdateCallback.onPercentChange((int) (mCurPositivePercent * 100),
                        (int) (mCurNegativePercent * 100));
            }
            return;
        }
        mOneShoot = true;
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(1200);
        mAnimator.setInterpolator(mInterpolator);
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animation.removeAllListeners();
                ValueAnimator va = (ValueAnimator) animation;
                va.removeAllUpdateListeners();
            }
        });
        mAnimator.addUpdateListener(animation -> {
            float fraction = animation.getAnimatedFraction();
            mCurPositivePercent = fraction * mPositivePercent;
            mCurNegativePercent = fraction * mNegativePercent;
            invalidate();
            if (mPercentUpdateCallback != null) {
                mPercentUpdateCallback.onPercentChange((int) (mCurPositivePercent * 100),
                        (int) (mCurNegativePercent * 100));
            }
        });
        mAnimator.start();
    }

    public void setPercentUpdateCallback(PercentUpdateCallback percentUpdateCallback) {
        mPercentUpdateCallback = percentUpdateCallback;
    }

    public interface PercentUpdateCallback {
        void onPercentChange(int positive, int negative);
    }

    public void resetOnShot(){
        mOneShoot = false;
    }
}
