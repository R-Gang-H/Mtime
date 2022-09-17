package com.mtime.bussiness.main.maindialog.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.util.MtimeUtils;

public class UpdateDlg extends Dialog {
    public interface Listener {
        void onCancelClick(View v);
        void onOKClick(View v);
        void onDismiss(DialogInterface dialog);
    }
    public static class SimpleListener implements Listener {
    
        @Override
        public void onCancelClick(View v) {
        
        }
    
        @Override
        public void onOKClick(View v) {
        
        }
    
        @Override
        public void onDismiss(DialogInterface dialog) {
        
        }
    }
    
    public static UpdateDlg show(Activity activity, int type, String changelog, Listener listener) {
        UpdateDlg dlg = new UpdateDlg(activity, type);
        if (UpdateDlg.TYPE_OK == type) {
            dlg.setCanceledOnTouchOutside(false);
            dlg.setCancelable(false);
        }
        // 必须先调用show，然后再设置信息
        dlg.show();
        dlg.setText(changelog);
    
        dlg.setBtnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(null != listener) {
                    listener.onCancelClick(v);
                }
                dlg.dismiss();
            }
        });
        dlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if(null != listener) {
                    listener.onOKClick(v);
                }
                MtimeUtils.downLoadApk(activity);
                MToastUtils.showShortToast(R.string.str_download_tips);
                dlg.dismiss();
            }
        });
    
        dlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(null != listener) {
                    listener.onDismiss(dialog);
                }
            }
        });
        return dlg;
    }
    
    
    
    /**
     * @return the titleView
     */
    public TextView getTitleView() {
        return titleView;
    }
    
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
    
    public static final int TYPE_OK        = 1;
    public static final int TYPE_CANCEL    = 2;
    public static final int TYPE_OK_CANCEL = 3;
    private int             dlg_type       = 1;
    private Button          btnOK          = null;
    private Button          btnCancel      = null;
    private TextView        textView       = null;
    private TextView        titleView      = null;
    
    /**
     * 设置显示文本内容
     */
    public void setText(final String text) {
        if ((textView != null) && (text != null)) {
            textView.setText(text);
        }
    }
    
    /**
     * 获取到textView组件
     */
    public TextView getTextView() {
        return textView;
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
    public UpdateDlg(final Context context, final int type) {
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_update);
        
        btnCancel = findViewById(R.id.btn_cancel);
        btnOK = findViewById(R.id.btn_update);
        View btnSepartorLine = findViewById(R.id.btn_separator_line);
        
        titleView = findViewById(R.id.titleText);
        
        textView = findViewById(R.id.text);
        switch (dlg_type) {
            case TYPE_OK: {
                btnCancel.setVisibility(View.GONE);
                btnSepartorLine.setVisibility(View.GONE);
            }
                break;
            case TYPE_CANCEL: {
                btnOK.setVisibility(View.GONE);
                btnSepartorLine.setVisibility(View.GONE);
            }
                break;
            case TYPE_OK_CANCEL: {
                btnCancel.setVisibility(View.VISIBLE);
                btnOK.setVisibility(View.VISIBLE);
                btnSepartorLine.setVisibility(View.VISIBLE);
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
}
