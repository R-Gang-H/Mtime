package com.mtime.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtime.R;

/**
 * 验证码对话框
 * 
 */

public class CapchaDlg extends Dialog {
    public static final int TYPE_OK            = 1;
    public static final int TYPE_CANCEL        = 2;
    public static final int TYPE_OK_CANCEL     = 3;
    private int             dlg_type           = InputDlg.TYPE_OK_CANCEL;
    private Button          btnOK              = null;
    private Button          btnCancel          = null;
    private EditText        editView           = null;
    private final TextView  textView           = null;
    private int             mLayoutID          = 0;
    private TextView        textNum;
    private ImageView       capchaText;
    private ImageView       capchaImg;
    private CharSequence    textInput;
    private int             MAX_TEXT_INPUT_LEN = 200;
    private boolean         isShowInput        = true;
    
    public TextView getTextView() {
        return textView;
    }
    
    private View.OnClickListener okListener        = null;
    private View.OnClickListener cancelListener    = null;
    private View.OnClickListener textClickListener = null;
    
    public Button getBtnCancel() {
        return btnCancel;
    }
    
    public Button getBtnOK() {
        return btnOK;
    }
    
    public void setBtnOKListener(final View.OnClickListener click) {
        okListener = click;
        if (btnOK != null) {
            btnOK.setOnClickListener(click);
        }
    }
    
    public void setCapchaTextListener(final View.OnClickListener click) {
        textClickListener = click;
        if (capchaText != null) {
            capchaText.setOnClickListener(click);
        }
    }
    
    public void setBtnCancelListener(final View.OnClickListener click) {
        cancelListener = click;
        if (btnCancel != null) {
            btnCancel.setOnClickListener(click);
        }
    }
    
    /**
     * 设置显示文本内容
     */
    public void setText(final String text) {
        if ((editView != null) && (text != null)) {
            editView.setText(text);
        }
    }
    
    public EditText getEditView() {
        return editView;
    }
    
    public ImageView getImageView() {
        return capchaImg;
    }
    
    /**
     * 获取对话框显示文本内容
     */
    public String getText() {
        return editView.getText().toString();
    }
    
    public CapchaDlg(final Context context) {
        this(context, InputDlg.TYPE_OK_CANCEL);
    }
    
    public CapchaDlg(final Context context, final int type, final int resLayout) {
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
        mLayoutID = resLayout;
    }
    
    public CapchaDlg(final Context context, final int type, final int resLayout, final boolean showInput) {
        this(context, type, resLayout);
        isShowInput = showInput;
    }
    
    /**
     * 构造函数，tpye可以取CustomAlertDlg的 TYPE_OK，TYPE_CANCEL，TYPE_OK_CANCEL
     */
    public CapchaDlg(final Context context, final int type) {
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mLayoutID == 0) {
            mLayoutID = R.layout.dialog_capcha;
        }
        
        this.setContentView(mLayoutID);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOK = findViewById(R.id.btn_ok);
        editView = findViewById(R.id.capcha_edit);
        textNum = findViewById(R.id.textView2);
        capchaImg = findViewById(R.id.capcha_img);
        capchaText = findViewById(R.id.capcha_text);
        if (isShowInput) {
            editView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    textInput = s;
                    if (textNum != null) {
                        textNum.setText(String.valueOf(MAX_TEXT_INPUT_LEN - s.length()));
                    }
                }
                
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                    
                }
                
                @Override
                public void afterTextChanged(final Editable s) {
                    final int selectionStart = editView.getSelectionStart();
                    final int selectionEnd = editView.getSelectionEnd();
                    
                    if ((textInput != null) && (textInput.length() > MAX_TEXT_INPUT_LEN)) {
                        s.delete(selectionStart - 1, selectionEnd);
                        final int tempSelection = selectionStart;
                        editView.setText(s);
                        editView.setSelection(tempSelection);
                    }
                }
            });
        }
        
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
            default:
                
                break;
        }
        if (okListener != null) {
            btnOK.setOnClickListener(okListener);
        }
        
        if (textClickListener != null) {
            if(capchaText != null) {
                capchaText.setOnClickListener(textClickListener);
            }
            capchaImg.setOnClickListener(textClickListener);
        }
        if (cancelListener != null) {
            btnCancel.setOnClickListener(cancelListener);
        }
        else {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    CapchaDlg.this.dismiss();
                }
            });
        }
    }
    
    /**
     * 设置最大输入字数
     */
    public void setMaxNumber(final int maxNum) {
        MAX_TEXT_INPUT_LEN = maxNum;
    }

    // 设置灰度
    public void setBackgroundDimAmount(float amount) {
        getWindow().setDimAmount(amount);
    }
}