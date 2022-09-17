package com.mtime.base.imageload;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2019-06-28
 */
public class LogoDrawable extends Drawable implements Drawable.Callback {

    private LogoState mLogoState;
    private boolean mMutated;

    private final Path mPath = new Path();
    private final RectF mRect = new RectF();

    /**
     * 必须 public 否则 api >= 24 的系统 反射会失败
     */
    public LogoDrawable() {
        this(null, null);
    }

    private LogoDrawable(LogoState state, Resources res) {
        mLogoState = createConstantState(state, res);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        LogoState state = mLogoState;
        Paint paint = state.mPaint;
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            return;
        }
        mRect.set(bounds);

        if (state.mRadiusArray != null) {
            mPath.reset();
            mPath.addRoundRect(mRect, state.mRadiusArray, Path.Direction.CW);
            canvas.drawPath(mPath, paint);
        } else if (state.mRadius > 0.0f) {
            float rad = Math.min(state.mRadius,
                    Math.min(mRect.width(), mRect.height()) * 0.5f);
            canvas.drawRoundRect(mRect, rad, rad, paint);
        } else {
            if (paint.getColor() != 0 || paint.getShader() != null) {
                canvas.drawRect(mRect, paint);
            }
        }

        if (state.mChild != null) {
            Drawable dr = state.mChild.mDrawable;
            if (dr == null) {
                return;
            }
            int w = dr.getIntrinsicWidth();
            int h = dr.getIntrinsicHeight();
            int cx = bounds.centerX();
            int cy = bounds.centerY();
            int left = cx - w / 2;
            int top = cy - h / 2;
            dr.setBounds(left, top, left + w, top + h);

//            int bw = bounds.width();
//            int bh = bounds.height();
//            boolean needScale = bw < w || bh < h;
//
//            int sc = -1;
//
//            if (needScale) {
//                float bRatio = bw * 1f / bh;
//                float ration = w * 1f / h;
//                float scale;
//                if (bRatio >= ration) { // 以 高度为基准
//                    scale = 0.8f;
//                } else { // 以宽度为基准
//                    scale = 0.8f;
//                }
//                sc = canvas.save();
//                canvas.scale(scale, scale, cx, cy);
//            }
            dr.draw(canvas);

//            if (needScale) {
//                canvas.restoreToCount(sc);
//            }
        }

    }

    @Override
    public void setAlpha(int alpha) {
        final int oldAlpha = mLogoState.mPaint.getAlpha();
        if (alpha != oldAlpha) {
            mLogoState.mPaint.setAlpha(alpha);
            if (mLogoState.mChild != null) {
                if (mLogoState.mChild.mDrawable != null) {
                    mLogoState.mChild.mDrawable.setAlpha(alpha);
                }
            }
            invalidateSelf();
        }
    }

    @Override
    public int getAlpha() {
        return mLogoState.mPaint.getAlpha();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mLogoState.mPaint.setColorFilter(colorFilter);
        if (mLogoState.mChild != null) {
            if (mLogoState.mChild.mDrawable != null) {
                mLogoState.mChild.mDrawable.setColorFilter(colorFilter);
            }
        }
        invalidateSelf();
    }

    @Nullable
    @Override
    public ColorFilter getColorFilter() {
        return mLogoState.mPaint.getColorFilter();
    }

    @Override
    public int getOpacity() {
        return mLogoState.getOpacity();
    }

    @Override
    protected boolean onStateChange(int[] state) {
        boolean changed = false;
        if (mLogoState.mChild != null) {
            Drawable dr = mLogoState.mChild.mDrawable;
            if (dr != null && dr.isStateful() && dr.setState(state)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    protected boolean onLevelChange(int level) {
        boolean changed = false;
        if (mLogoState.mChild != null) {
            Drawable dr = mLogoState.mChild.mDrawable;
            if (dr != null && dr.setLevel(level)) {
                changed = true;
            }
        }
        return changed;
    }

    /*@Override
    public int getIntrinsicWidth() {
        int width = 0;
        Callback callback = getCallback();
        if (callback instanceof View) {
            View v = (View) callback;
            width = v.getMeasuredWidth();
        }
        return width;
    }

    @Override
    public int getIntrinsicHeight() {
        int height = 0;
        Callback callback = getCallback();
        if (callback instanceof View) {
            View v = (View) callback;
            height = v.getMeasuredHeight();
        }
        return height;
    }*/

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        invalidateSelf();
    }

    private void setCornerRadius(float radius) {
        mLogoState.setCornerRadius(radius);
        invalidateSelf();
    }

    private void setCornerRadii(@Nullable float[] radii) {
        mLogoState.setCornerRadii(radii);
        invalidateSelf();
    }

    @NonNull
    public static LogoDrawable createFromXmlInner(@NonNull Resources r, @NonNull XmlPullParser parser,
                                                  @NonNull AttributeSet attrs, Resources.Theme theme) {
        final LogoDrawable drawable = new LogoDrawable();
        drawable.inflate(r, parser, attrs, theme);
        return drawable;
    }

    private static @NonNull
    TypedArray makeAttributes(@NonNull Resources res, @Nullable Resources.Theme theme,
                              @NonNull AttributeSet set) {
        if (theme == null) {
            return res.obtainAttributes(set, R.styleable.LogoDrawable);
        }
        return theme.obtainStyledAttributes(set, R.styleable.LogoDrawable, 0, 0);
    }

    @Override
    public void inflate(@NonNull Resources r, @NonNull XmlPullParser parser, @NonNull AttributeSet attrs,
                        @Nullable Resources.Theme theme) {
        final LogoState state = mLogoState;
        final int density = resolveDensity2(r, 0);
        state.setDensity(density);

        TypedArray a = makeAttributes(r, theme, attrs);

        int radius = a.getDimensionPixelSize(R.styleable.LogoDrawable_android_radius, 0);
        setCornerRadius(radius);

        final int topLeftRadius = a.getDimensionPixelSize(
                R.styleable.LogoDrawable_android_topLeftRadius, radius);
        final int topRightRadius = a.getDimensionPixelSize(
                R.styleable.LogoDrawable_android_topRightRadius, radius);
        final int bottomLeftRadius = a.getDimensionPixelSize(
                R.styleable.LogoDrawable_android_bottomLeftRadius, radius);
        final int bottomRightRadius = a.getDimensionPixelSize(
                R.styleable.LogoDrawable_android_bottomRightRadius, radius);
        if (topLeftRadius != radius || topRightRadius != radius ||
                bottomLeftRadius != radius || bottomRightRadius != radius) {
            // The corner radii are specified in clockwise order (see Path.addRoundRect())
            setCornerRadii(new float[]{
                    topLeftRadius, topLeftRadius,
                    topRightRadius, topRightRadius,
                    bottomRightRadius, bottomRightRadius,
                    bottomLeftRadius, bottomLeftRadius
            });
        }

        Drawable dr = a.getDrawable(R.styleable.LogoDrawable_android_drawable);
        if (dr != null) {
            ChildDrawable childDrawable = new ChildDrawable(density);
            childDrawable.mDrawable = dr;
            state.mChild = childDrawable;
        }

        final Paint paint = state.mPaint;
        int color = paint.getColor();
        color = a.getColor(R.styleable.LogoDrawable_android_color, color);
        paint.setColor(color);

        a.recycle();
    }

    @Nullable
    @Override
    public ConstantState getConstantState() {
        mLogoState.mChangingConfigurations = getChangingConfigurations();
        return mLogoState;
    }

    @Override
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mLogoState.getChangingConfigurations();
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        mLogoState.invalidateCache();
        invalidateSelf();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
        scheduleSelf(what, when);
    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
        unscheduleSelf(what);
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (mLogoState.mChild != null) {
            if (mLogoState.mChild.mDrawable != null) {
                mLogoState.mChild.mDrawable.setVisible(visible, restart);
            }
        }
        return changed;
    }

    @NonNull
    @Override
    public Drawable mutate() {
        if (!mMutated && super.mutate() == this) {
            mLogoState = createConstantState(mLogoState, null);
            final ChildDrawable child = mLogoState.mChild;
            Drawable dr = child.mDrawable;
            if (dr != null) {
                dr.mutate();
            }
            mMutated = true;
        }
        return this;
    }

    private LogoState createConstantState(@Nullable LogoState state, @Nullable Resources res) {
        return new LogoState(state, this, res);
    }

    final static class LogoState extends ConstantState {

        int mChangingConfigurations;
        ChildDrawable mChild;
        int mDensity;
        boolean mCheckedOpacity;
        int mOpacity;
        int mChildChangingConfigurations;
        private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private float mRadius;
        private float[] mRadiusArray;

        LogoState(@Nullable LogoState orig, @NonNull LogoDrawable owner,
                  @Nullable Resources res) {
            mDensity = resolveDensity2(res, orig != null ? orig.mDensity : 0);

            if (orig != null) {
                mChild = new ChildDrawable(orig.mChild, owner, res);
                mChangingConfigurations = orig.mChangingConfigurations;
                mChildChangingConfigurations = orig.mChildChangingConfigurations;
                mOpacity = orig.mOpacity;
                mRadius = orig.mRadius;
                if (orig.mRadiusArray != null) {
                    mRadiusArray = orig.mRadiusArray.clone();
                }
                if (orig.mDensity != mDensity) {
                    applyDensityScaling(orig.mDensity, mDensity);
                }

                mPaint.setColor(orig.mPaint.getColor());
                mPaint.setColorFilter(orig.mPaint.getColorFilter());
                mPaint.setAlpha(orig.mPaint.getAlpha());

            } else {
                mChild = null;
            }

            mPaint.setStyle(Paint.Style.FILL);

        }

        private void applyDensityScaling(int sourceDensity, int targetDensity) {
            if (mRadius > 0) {
                mRadius = scaleFromDensity3(mRadius, sourceDensity, targetDensity);
            }
            if (mRadiusArray != null) {
                mRadiusArray[0] = scaleFromDensity2(
                        (int) mRadiusArray[0], sourceDensity, targetDensity);
                mRadiusArray[1] = scaleFromDensity2(
                        (int) mRadiusArray[1], sourceDensity, targetDensity);
                mRadiusArray[2] = scaleFromDensity2(
                        (int) mRadiusArray[2], sourceDensity, targetDensity);
                mRadiusArray[3] = scaleFromDensity2(
                        (int) mRadiusArray[3], sourceDensity, targetDensity);
            }
        }

        void setCornerRadius(float radius) {
            if (radius < 0) {
                radius = 0;
            }
            mRadius = radius;
            mRadiusArray = null;
        }

        void setCornerRadii(float[] radii) {
            mRadiusArray = radii;
            if (radii == null) {
                mRadius = 0;
            }
        }

        final void setDensity(int targetDensity) {
            if (mDensity != targetDensity) {
                final int sourceDensity = mDensity;
                mDensity = targetDensity;
                applyDensityScaling(sourceDensity, targetDensity);
            }
        }

        @Override
        public boolean canApplyTheme() {
            return false;
        }

        @NonNull
        @Override
        public Drawable newDrawable() {
            return new LogoDrawable(this, null);
        }

        @NonNull
        @Override
        public Drawable newDrawable(@Nullable Resources res) {
            return new LogoDrawable(this, res);
        }

        @Override
        public int getChangingConfigurations() {
            return mChangingConfigurations | mChildChangingConfigurations;
        }

        int getOpacity() {
            if (mCheckedOpacity) {
                return mOpacity;
            }

            int op;
            if (mChild != null && mChild.mDrawable != null) {
                op = mChild.mDrawable.getOpacity();
            } else {
                op = PixelFormat.TRANSPARENT;
            }

            mOpacity = Drawable.resolveOpacity(op, mOpacity);
            mCheckedOpacity = true;
            return op;
        }

        void invalidateCache() {
            mCheckedOpacity = false;
        }
    }

    final static class ChildDrawable {
        Drawable mDrawable;

        int mDensity;

        ChildDrawable(int density) {
            mDensity = density;
        }

        ChildDrawable(@NonNull ChildDrawable orig, @NonNull LogoDrawable owner,
                      @Nullable Resources res) {
            final Drawable dr = orig.mDrawable;
            final Drawable clone;
            if (dr != null) {
                final ConstantState cs = dr.getConstantState();
                if (cs == null) {
                    clone = dr;
                    if (dr.getCallback() != null) {
                        // This drawable already has an owner.
                        Log.w("LogoDrawable", "Invalid drawable added to LogoDrawable! Drawable already "
                                        + "belongs to another owner but does not expose a constant state.",
                                new RuntimeException());
                    }
                } else if (res != null) {
                    clone = cs.newDrawable(res);
                } else {
                    clone = cs.newDrawable();
                }
                clone.setBounds(dr.getBounds());
                clone.setLevel(dr.getLevel());

                // Set the callback last to prevent invalidation from
                // propagating before the constant state has been set.
                clone.setCallback(owner);
            } else {
                clone = null;
            }

            mDrawable = clone;

            mDensity = resolveDensity2(res, orig.mDensity);
        }
    }

    private static int resolveDensity2(@Nullable Resources r, int parentDensity) {
        final int densityDpi = r == null ? parentDensity : r.getDisplayMetrics().densityDpi;
        return densityDpi == 0 ? DisplayMetrics.DENSITY_DEFAULT : densityDpi;
    }

    private static int scaleFromDensity2(int pixels, int sourceDensity, int targetDensity) {
        if (pixels == 0 || sourceDensity == targetDensity) {
            return pixels;
        }

        final float result = pixels * targetDensity / (float) sourceDensity;

        final int rounded = Math.round(result);
        if (rounded != 0) {
            return rounded;
        } else if (pixels > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    private static float scaleFromDensity3(float pixels, int sourceDensity, int targetDensity) {
        return pixels * targetDensity / sourceDensity;
    }
}
