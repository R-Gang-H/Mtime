package com.mtime.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mtime.R;

public class CustomAlertDlg extends Dialog {

    private View.OnClickListener okListener     = null;
    private View.OnClickListener cancelListener = null;

    /**
     * @return 获取到返回按钮
     */
    public Button getBtnCancel() {
        return btnCancel;
    }

    /**
     * @return 获取到确定按钮
     */
    public Button getBtnOK() {
        return btnOK;
    }

    /**
     * 设置确定按钮的事件
     */
    public void setBtnOKListener(final View.OnClickListener click) {
        okListener = click;
        if (btnOK != null) {
            btnOK.setOnClickListener(click);
        }
    }

    /**
     * 设置取消按钮的事件
     */
    public void setBtnCancelListener(final View.OnClickListener click) {
        cancelListener = click;
        if (btnCancel != null) {
            btnCancel.setOnClickListener(click);
        }
    }

    public void setBtnCancelText(CharSequence text) {
        if ((btnCancel != null) && (text != null)) {
            btnCancel.setText(text);
        }
    }

    public void setBtnOkText(CharSequence text) {
        if ((btnOK != null) && (text != null)) {
            btnOK.setText(text);
        }
    }

    public void setLabels(final String labelCancel, final String labelOK) {
        if (null != this.btnOK) {
            this.btnOK.setText(labelOK);
        }

        if (null != this.btnCancel) {
            this.btnCancel.setText(labelCancel);
        }
    }

    public void setLabelsColor(int color){
        if (null != this.btnOK) {
            this.btnOK.setTextColor(color);
        }

        if (null != this.btnCancel) {
            this.btnCancel.setTextColor(color);
        }
    }
    public void setLabel(final String label) {
        if (null != this.btnOK) {
            this.btnOK.setText(label);
        }

        if (null != this.btnCancel) {
            this.btnCancel.setText(label);
        }

    }


    public static final int TYPE_OK        = 1;
    public static final int TYPE_CANCEL    = 2;
    public static final int TYPE_OK_CANCEL = 3;
    public static final int TYPE_YES_NO     = 4;
    private int             dlg_type       = 1;
    private Button          btnOK          = null;
    private Button          btnCancel      = null;
    private TextView        textView       = null;
    private TextView        titleText      = null;
    private TextView        tipText        = null;
    private TextView        smallText      = null;
    private ImageView       image;

    public void setTitleText(final String titleStr) {
        if ((titleText != null) && (titleStr != null)) {
            titleText.setText(titleStr);
        }
    }

    public void setImage(boolean isShow){
        if(isShow) {
            image.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 12, 0, 48);
            lp.gravity = Gravity.CENTER;
            textView.setLayoutParams(lp);
        }
        else
            image.setVisibility(View.GONE);
    }
    /**
     * 设置显示文本内容
     */
    public void setText(final String text) {
        if ((textView != null) && (text != null)) {
            textView.setText(text);
        }
    }
    public void setText(CharSequence text) {
        if ((textView != null) && (text != null)) {
            textView.setText(text);
        }
    }

    public void setTipText(final String text) {
        if ((tipText != null) && (text != null)) {
            tipText.setText(text);
        }
    }

    /**
     * 获取到textView组件
     */
    public TextView getTextView() {
        return textView;
    }

    public TextView getTipView() {
        return tipText;
    }

    /**
     * 获取对话框显示文本内容
     */
    public String getText() {
        return textView.getText().toString();
    }

    /**
     * 构造函数，tpye可以取CustomAlertDlg的 TYPE_OK，TYPE_CANCEL，TYPE_OK_CANCEL
     *
     */
    public CustomAlertDlg(final Context context, final int type) {
        // 设置dialog没有边框
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.custom_dialog_ok);

        btnCancel = findViewById(R.id.btn_cancel);
        btnOK = findViewById(R.id.btn_ok);
        textView = findViewById(R.id.dlg_text);
        titleText = findViewById(R.id.dlg_title);
        tipText = findViewById(R.id.dlg_tip);
        image = findViewById(R.id.image);
        smallText = findViewById(R.id.dlg_small_text);
        switch (dlg_type) {
            case TYPE_OK: {
                btnCancel.setVisibility(View.GONE);
            }
                break;
            case TYPE_CANCEL: {
                btnOK.setVisibility(View.GONE);
            }
                break;
            case TYPE_OK_CANCEL: {
                btnCancel.setVisibility(View.VISIBLE);
                btnOK.setVisibility(View.VISIBLE);
            }
                break;
            case TYPE_YES_NO:{
                btnCancel.setVisibility(View.VISIBLE);
                btnOK.setVisibility(View.VISIBLE);
                btnCancel.setText("否");
                btnOK.setText("是");
            }
                break;
            default:

                break;
        }
        if (okListener != null) {
            btnOK.setOnClickListener(okListener);
        }
        if (cancelListener != null) {
            btnCancel.setOnClickListener(cancelListener);
        }
    }

    public TextView getTitleText() {
        return titleText;
    }

    public TextView getSmallText() {
        return smallText;
    }

    public void setSmallText(String text) {
        if(null != smallText) {
            smallText.setVisibility(View.VISIBLE);
            this.smallText.setText(text);
        }
    }
}
