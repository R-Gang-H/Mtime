package com.mtime.base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.mtime.base.views.R;

import static android.content.res.Configuration.ORIENTATION_PORTRAIT;

/**
 * Created by <a href="mailto:kunlin.wang@mtime.com">Wang kunlin</a>
 * <p>
 * On 2018-02-05
 */

public class CancelableDialog extends Dialog {

    public CancelableDialog(@NonNull Context context) {
        super(context);
    }

    public CancelableDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        TypedArray a = context.obtainStyledAttributes(themeResId, R.styleable.CancelableDialog);
        int gravity = a.getInt(R.styleable.CancelableDialog_android_gravity, Gravity.CENTER);
        a.recycle();
        Window window = getWindow();
        window.setGravity(gravity);
    }

    @Override
    public void show() {
        super.show();

        Context context = getContext();
        TypedArray a = context.obtainStyledAttributes(R.styleable.CancelableDialog);

        Resources resources = context.getResources();
        final boolean isPortrait = resources.getConfiguration().orientation == ORIENTATION_PORTRAIT;
        if (isPortrait) {
            if (!a.hasValue(R.styleable.CancelableDialog_dialogFixedWidthMinor)) {
                return;
            }
            final TypedValue fixedWidthMinor = new TypedValue();
            a.getValue(R.styleable.CancelableDialog_dialogFixedWidthMinor, fixedWidthMinor);
            handleWidth(fixedWidthMinor);
        } else {
            if (!a.hasValue(R.styleable.CancelableDialog_dialogFixedWidthMajor)) {
                return;
            }
            final TypedValue fixedWidthMajor = new TypedValue();
            a.getValue(R.styleable.CancelableDialog_dialogFixedWidthMajor, fixedWidthMajor);
            handleWidth(fixedWidthMajor);
        }
        a.recycle();

    }

    private void handleWidth(TypedValue tvw) {
        Resources resources = getContext().getResources();
        final DisplayMetrics metrics = resources.getDisplayMetrics();
        int w = 0;
        boolean hasValue = true;
        if (tvw.type != TypedValue.TYPE_NULL) {
            if (tvw.type == TypedValue.TYPE_DIMENSION) {
                w = (int) tvw.getDimension(metrics);
            } else if (tvw.type == TypedValue.TYPE_FRACTION) {
                w = (int) tvw.getFraction(metrics.widthPixels, metrics.widthPixels);
            } else {
                hasValue = false;
            }
        }
        if (hasValue) {
            Window window = getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            attributes.width = w;
            window.setAttributes(attributes);
        }
    }

    protected CancelableDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private ShouldCancelCallback l;

    private PreCancelCallback mPreCancelCallback;

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (isShowing() && shouldCloseOnTouch(getContext(), event)) {
            cancel();
            return true;
        }
        return false;
    }

    /*
     * copy from Window.java
     */
    private boolean shouldCloseOnTouch(Context context, MotionEvent event) {
        return (l == null || l.shouldCancelOnTouchOutside())
                && event.getAction() == MotionEvent.ACTION_DOWN
                && isOutOfBounds(context, event) && (getWindow() == null ||
                getWindow().peekDecorView() != null);
    }

    /*
     * copy from Window.java
     */
    private boolean isOutOfBounds(Context context, MotionEvent event) {
        final int x = (int) event.getX();
        final int y = (int) event.getY();
        final int slop = ViewConfiguration.get(context).getScaledWindowTouchSlop();
        if (getWindow() == null) return true;
        final View decorView = getWindow().getDecorView();
        return (x < -slop) || (y < -slop)
                || (x > (decorView.getWidth() + slop))
                || (y > (decorView.getHeight() + slop));
    }

    @Deprecated
    @Override
    public void setCancelable(boolean flag) {
//        super.setCancelable(flag);
    }

    @Deprecated
    @Override
    public void setCanceledOnTouchOutside(boolean cancel) {
//        super.setCanceledOnTouchOutside(cancel);
    }

    @Override
    public void onBackPressed() {
        if (l == null || l.shouldCancelOnBackPressed()) {
            cancel();
        }
    }

    @Override
    public void cancel() {
        if (null != mPreCancelCallback) {
            mPreCancelCallback.onPreCancel();
        }
        super.cancel();
    }

    public void setPreCancelCallback(PreCancelCallback preCancelCallback) {
        mPreCancelCallback = preCancelCallback;
    }

    public void setShouldCancelCallback(ShouldCancelCallback shouldCancelCallback) {
        this.l = shouldCancelCallback;
    }

    public interface ShouldCancelCallback {
        boolean shouldCancelOnBackPressed();

        boolean shouldCancelOnTouchOutside();
    }

    public interface PreCancelCallback {
        void onPreCancel();
    }
}
