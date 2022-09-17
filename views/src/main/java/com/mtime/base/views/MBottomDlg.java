package com.mtime.base.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by LiJiaZhi on 17/4/1.
 * 通用下面弹出的对话框
 * demo：
 * Item :第一个是名字，第二个是否是选中（可设置选中不同的颜色），第三个是obj，设置的话，可以在回调里，gettag获取
 * MBottomDlg.Item[] items ={ new MBottomDlg.Item("1",true,1), new MBottomDlg.Item("2",false,2), new MBottomDlg.Item("3") };
 * mdlg = new MBottomDlg(v.getContext())
 * mdlg = new MBottomDlg(v.getContext())
 * .setTitle("title")
 * .setTitleColor(R.color.green)
 * .setTitleSize(14)
 * .setBackgroundColor(R.color.blue)
 * .setItems(items)
 * .setLineColor(R.color.red)
 * .setDefaultColor(R.color.red)
 * .setSelectedColor(R.color.green)
 * .setCancelName("cancel")
 * .setLineHeight(100)
 * .setTitleColor(R.color.red)
 * .setOnItemClickListener(new MBottomDlg.ClickItemListener() {
 *
 * @Override public void itemClickListener(int buttonIndex, View view) {
 * System.err.println(String.valueOf(buttonIndex)+"   "+(int)view.getTag());
 * }
 * });
 * mdlg.show();
 */

public class MBottomDlg {

    protected Context mContext;
    protected TextView tvTitle;
    protected TextView tvCancel;
    protected LinearLayout layoutContainer;
    protected Dialog mDialog;

    // title
    private String mTitle = "";
    // title颜色
    private int mTitleColor = -1;
    // title字体大小
    private int mTitleSize = 0;
    // title行高
    private final int mTitleHeight = 0;

    // 取消按钮文案
    private String mCancelName = "";

    // 行高
    private int mLineHeight = 0;
    // 默认字体颜色
    private int mDefaultColor = -1;
    // 选中字体颜色
    private int mSelectedColor = -1;
    // 背景色
    private int mBackgroundColor = -1;
    // 默认字体大小
    private int mDefaultSize = 0;

    private int mLineColor = -1;

    private Item[] mItems;

    // 监听
    private ClickItemListener mItemListener;
    private ClickCancelListener mCancelListener;

    public MBottomDlg(Context context) {
        this.mContext = context;
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) mContext).getCurrentFocus();
            if (focusView != null)
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    private View generateContentView(Context context) {
        View container = LayoutInflater.from(context).inflate(R.layout.base_layout_dailog_bottom, null);
        layoutContainer = container.findViewById(R.id.container_btm_dlg);
        tvTitle = container.findViewById(R.id.tv_btm_dlg_title);
        if ("".equals(mTitle)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(mTitle);
            if (mTitleColor != -1) {
                tvTitle.setTextColor(mTitleColor);
            }
            if (mTitleSize != 0) {
                tvTitle.setTextSize(mTitleSize);
            }
            if (mTitleHeight != 0) {
                tvTitle.setHeight(mTitleHeight);
            }
            tvTitle.setVisibility(View.VISIBLE);
        }

        tvCancel = container.findViewById(R.id.btn_btm_dlg_cancel);
        if (!"".equals(mCancelName)) {
            tvCancel.setText(mCancelName);
        }
        if (0 != mLineHeight) {
            tvCancel.setHeight(mLineHeight);
        }
        if (mBackgroundColor != -1) {
            tvTitle.setBackgroundColor(mBackgroundColor);
            tvCancel.setBackgroundColor(mBackgroundColor);
            layoutContainer.setBackgroundColor(mBackgroundColor);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelListener != null) {
                    mCancelListener.cancelListener(v);
                } else {
                    mDialog.dismiss();
                }
            }
        });
        int size = null == mItems ? 0 : mItems.length;
        for (int i = 0; i < size; i++) {
            Item item = mItems[i];
            View view = new View(context);
            view.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1));
            if (mLineColor != -1) {
                view.setBackgroundColor(mContext.getResources().getColor(mLineColor));
            } else {
                view.setBackgroundColor(Color.parseColor("#DCDDDE"));
            }
            layoutContainer.addView(view);

            TextView textView = new TextView(context);
            if (mLineHeight != 0) {
                textView.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        mLineHeight));
            } else {
                textView.setLayoutParams(new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 80));
            }
            textView.setText(item.name);
            textView.setGravity(Gravity.CENTER);
            if (item.selected) {
                if (mSelectedColor != -1) {
                    textView.setTextColor(mContext.getResources().getColor(mSelectedColor));
                } else {
                    textView.setTextColor(Color.parseColor("#3C3D3D"));
                }
            } else {
                if (mDefaultColor != -1) {
                    textView.setTextColor(mContext.getResources().getColor(mDefaultColor));
                } else {
                    textView.setTextColor(Color.parseColor("#3C3D3D"));
                }
            }
            if (mDefaultSize != 0) {
                textView.setTextSize(mDefaultSize);
            } else {
                textView.setTextSize(16);
            }
            textView.setId(i);
            textView.setTag(item.data);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemListener != null) {
                        mItemListener.itemClickListener(v.getId(), v);
                        mDialog.dismiss();
                    }
                }
            });
            layoutContainer.addView(textView);
        }
        return container;
    }

    private void createDialog() {
        mDialog = new Dialog(mContext, R.style.MBottomDlg);
        mDialog.setContentView(generateContentView(mContext), new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = mDialog.getWindow();
        window.setWindowAnimations(R.style.MBottomDlgAnimation);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        wl.gravity = Gravity.BOTTOM;
        mDialog.onWindowAttributesChanged(wl);
        mDialog.setCanceledOnTouchOutside(true);
    }

    public void show() {
        if (mDialog == null) {
            createDialog();
        }
        if (mDialog != null && !mDialog.isShowing()) {
            Activity activity = (Activity) mContext;
            if (activity.isFinishing()) {
                return;
            }
            mDialog.show();
        }
    }

    public boolean isShow() {
        return mDialog != null && mDialog.isShowing();
    }

    public void close() {
        if (mDialog != null)
            mDialog.dismiss();
    }

    public MBottomDlg setBackgroundColor(int mBackgroundColor) {
        this.mBackgroundColor = mBackgroundColor;
        return this;
    }

    public MBottomDlg setCancelName(String mCancelName) {
        this.mCancelName = mCancelName;
        return this;
    }

    public MBottomDlg setDefaultColor(int mDefaultColor) {
        this.mDefaultColor = mDefaultColor;
        return this;
    }

    public MBottomDlg setDefaultSize(int mDefaultSize) {
        this.mDefaultSize = mDefaultSize;
        return this;
    }

    public MBottomDlg setItems(Item[] mItems) {
        this.mItems = mItems;
        return this;
    }

    public MBottomDlg setLineColor(int mLineColor) {
        this.mLineColor = mLineColor;
        return this;
    }

    public MBottomDlg setLineHeight(int mLineHeight) {
        this.mLineHeight = mLineHeight;
        return this;
    }

    public MBottomDlg setSelectedColor(int mSelectedColor) {
        this.mSelectedColor = mSelectedColor;
        return this;
    }

    public MBottomDlg setTitle(String mTitle) {
        this.mTitle = mTitle;
        return this;
    }

    public MBottomDlg setTitleColor(int mTitleColor) {
        this.mTitleColor = mTitleColor;
        return this;
    }

    public MBottomDlg setTitleSize(int mTitleSize) {
        this.mTitleSize = mTitleSize;
        return this;
    }

    public MBottomDlg setOnItemClickListener(ClickItemListener listener) {
        mItemListener = listener;
        return this;
    }

    public MBottomDlg setOnCancelClickListener(ClickCancelListener listener) {
        mCancelListener = listener;
        return this;
    }

    public interface ClickItemListener {
        void itemClickListener(int buttonIndex, View view);
    }

    public interface ClickCancelListener {
        void cancelListener(View view);
    }

    public static class Item {
        public String name;// 名称
        public boolean selected;// 是否选中
        public Object data;// 关联的数据

        public Item(String name) {
            this(name, false, null);
        }

        public Item(String name, boolean selected) {
            this(name, selected, null);
        }

        public Item(String name, boolean selected, Object data) {
            this.data = data;
            this.name = name;
            this.selected = selected;
        }
    }
}
