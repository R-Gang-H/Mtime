package com.mtime.bussiness.common.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2019/4/8
 */
public class InputMethodHelper {
    //输入法是否显示着
    public static boolean isShowKeyBoard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        return null != imm && imm.isActive();
    }

    //隐藏虚拟键盘
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm && imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

        }
    }

    //隐藏虚拟键盘
    public static void hideKeyboard(Dialog dialog) {
        InputMethodManager imm = (InputMethodManager) dialog.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = dialog.getCurrentFocus();
        if (null != imm && imm.isActive() && null != view) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    //显示虚拟键盘
    public static void showKeyboard(View v) {
        v.requestFocus();
        v.requestFocusFromTouch();
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.showSoftInput(v, InputMethodManager.SHOW_FORCED);
        }
    }



    private static final Map<OnSoftKeyBoardChangeListener, SoftKeyBoardListener> sSoftKeyBoardListenerMap = new HashMap<>();

    public static void addOnSoftKeyBoardChangeListener(View view, OnSoftKeyBoardChangeListener listener) {
        SoftKeyBoardListener softKeyBoardListener = new SoftKeyBoardListener(view, listener);
        softKeyBoardListener.addOnGlobalLayoutListener();
        sSoftKeyBoardListenerMap.put(listener, softKeyBoardListener);
    }

    public static void removeOnSoftKeyBoardChangeListener(OnSoftKeyBoardChangeListener listener) {
        SoftKeyBoardListener softKeyBoardListener = sSoftKeyBoardListenerMap.remove(listener);
        if(null != softKeyBoardListener) {
            softKeyBoardListener.removeOnGlobalLayoutListener();
        }
        Log.i("zsq", "sSoftKeyBoardListenerMap size="+sSoftKeyBoardListenerMap.size());
    }

    public interface OnSoftKeyBoardChangeListener {
        void onKeyBoardShow();
        void onKeyBoardHide();
    }

    static class SoftKeyBoardListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private final View rootView;//activity的根视图
        private int rootViewVisibleHeight;//纪录根视图的显示高度
        private final OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener;

        private boolean isShow;

        public SoftKeyBoardListener(@NonNull View rootView, @NonNull OnSoftKeyBoardChangeListener onSoftKeyBoardChangeListener) {
            this.rootView = rootView;
            this.onSoftKeyBoardChangeListener = onSoftKeyBoardChangeListener;
        }

        public void addOnGlobalLayoutListener() {
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        public void removeOnGlobalLayoutListener() {
            rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }

        @Override
        public void onGlobalLayout() {
            //获取当前根视图在屏幕上显示的大小
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);

            int visibleHeight = r.height();
            if (rootViewVisibleHeight == 0) {
                rootViewVisibleHeight = visibleHeight;
                return;
            }

            //根视图显示高度没有变化，可以看作软键盘显示／隐藏状态没有改变
            if (rootViewVisibleHeight == visibleHeight) {
                return;
            }

            //根视图显示高度变小超过200，可以看作软键盘显示了
            if (!isShow && rootViewVisibleHeight - visibleHeight > 200) {
                isShow = true;
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.onKeyBoardShow();
                }
            }

            //根视图显示高度变大超过200，可以看作软键盘隐藏了
            if (isShow && visibleHeight - rootViewVisibleHeight > 200) {
                isShow = false;
                if (onSoftKeyBoardChangeListener != null) {
                    onSoftKeyBoardChangeListener.onKeyBoardHide();
                }
            }

            rootViewVisibleHeight = visibleHeight;
        }
    }
}
