package com.mtime.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.mtime.base.utils.MToastUtils;

/**
 * Created by <a href="mailto:wangkunlin1992@gmail.com">Wang kunlin</a>
 * <p>
 * On 2018-06-21
 * <p>
 * view 截图工具
 */
public class ViewCapture {

    public interface CaptureCallback<T extends ViewCapture> {
        void onCaptured(T capture, Bitmap b);
    }

    private CaptureCallback mCallback;
    private final View mView;
    private final ViewGroup mParent;
    private PopupWindow mPopup;
    protected Context mContext;

    public ViewCapture(ViewGroup parent, View toCapture) {
        mContext = parent.getContext();
        onCreate();
        mParent = parent;
        mView = toCapture;
        onViewCreated(mView);
    }

    public ViewCapture(ViewGroup parent, int layoutId) {
        mContext = parent.getContext();
        onCreate();
        mParent = parent;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mView = inflater.inflate(layoutId, parent, false);
        onViewCreated(mView);
    }

    private void showPopup() {
        if (mView.getParent() != null) {
            MToastUtils.showShortToast("控件无父类，截图失败");
            return;
        }
        mParent.post(new Runnable() {
            @Override
            public void run() {
                doShow();
            }
        });
    }

    protected void onCreate() {
    }

    protected void onViewCreated(View view) {
    }

    public final void setCaptureCallback(CaptureCallback callback) {
        mCallback = callback;
    }

    public final void capture() {
        showPopup();
    }

    protected void doCapture() {
        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCallback != null) {
                    Bitmap b = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);
                    Canvas canvas = new Canvas(b);
                    mView.draw(canvas);

                    mCallback.onCaptured(ViewCapture.this, b);
                }
                mPopup.dismiss();
            }
        }, 100);
    }

    private void doShow() {

        View view = mView;
        ViewGroup parent = mParent;

        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        int widthMeasureSpec = getMeasureSpec(parent.getWidth(), params.width);
        int heightMeasureSpec = getMeasureSpec(parent.getHeight(), params.height);
        view.measure(widthMeasureSpec, heightMeasureSpec);

        mPopup = new PopupWindow(view);
        int width = view.getMeasuredWidth();

        view.addOnAttachStateChangeListener(mDetachListener);

        mPopup.setWidth(width);
        mPopup.setHeight(view.getMeasuredHeight());
        mPopup.setFocusable(false);
        mPopup.setClippingEnabled(false);
        mPopup.setIgnoreCheekPress();
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.setOutsideTouchable(false);
        mPopup.setSplitTouchEnabled(false);
        mPopup.setTouchable(false);
        mPopup.setBackgroundDrawable(new ColorDrawable(0));
        mPopup.showAtLocation(parent, Gravity.NO_GRAVITY, -10 * width, 0); // 向左偏移 10个屏幕宽度，以使其隐藏
        onShow();
    }

    private final View.OnAttachStateChangeListener mDetachListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(View v) {
//            doCapture();
        }

        @Override
        public void onViewDetachedFromWindow(View v) {
            v.removeOnAttachStateChangeListener(this);
            if (mPopup.isShowing()) {
                mPopup.dismiss();
            }
            onDismiss();
        }
    };

    protected void onShow() {
    }

    protected void onDismiss() {
    }

    private static int getMeasureSpec(int pSize, int cSize) {
        if (cSize > 0) {
            return MeasureSpec.makeMeasureSpec(cSize, MeasureSpec.EXACTLY);
        }
        if (cSize == ViewGroup.LayoutParams.MATCH_PARENT) {
            return MeasureSpec.makeMeasureSpec(pSize, MeasureSpec.EXACTLY);
        }
        return MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >>> 2, MeasureSpec.AT_MOST);
    }


}
