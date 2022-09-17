package com.mtime.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mtime.R;

/**
 * 带EditText的dialog
 * 
 * @category 例 添加优惠券dialog
 */
public class InputDlg extends Dialog {
    
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
    private CharSequence    textInput;
    private int             MAX_TEXT_INPUT_LEN = 200;
    private boolean         isShowInput        = true;
    
    /**
     * @return the textView
     */
    public TextView getTextView() {
        return textView;
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
    
    // private TextView
    /**
     * 设置显示文本内容
     */
    public void setText(final String text) {
        if ((editView != null) && (text != null)) {
            editView.setText(text);
        }
    }
    
    /**
     * 获取到textView组件
     */
    public EditText getEditView() {
        return editView;
    }
    
    /**
     * 获取对话框显示文本内容
     */
    public String getText() {
        return editView.getText().toString();
    }
    
    public InputDlg(final Context context) {
        this(context, InputDlg.TYPE_OK_CANCEL);
    }
    
    public InputDlg(final Context context, final int type, final int resLayout) {
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
        mLayoutID = resLayout;
    }
    
    public InputDlg(final Context context, final int type, final int resLayout, final boolean showInput) {
        this(context, type, resLayout);
        isShowInput = showInput;
    }
    
    /**
     * 构造函数，tpye可以取CustomAlertDlg的 TYPE_OK，TYPE_CANCEL，TYPE_OK_CANCEL
     */
    public InputDlg(final Context context, final int type) {
        super(context, R.style.upomp_bypay_MyDialog);
        dlg_type = type;
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mLayoutID == 0) {
            mLayoutID = R.layout.dialog_share;
        }
        
        this.setContentView(mLayoutID);
        btnCancel = findViewById(R.id.btn_cancel);
        btnOK = findViewById(R.id.btn_ok);
        editView = findViewById(R.id.dlg_text);
        textNum = findViewById(R.id.textView2);
        if (isShowInput) {
            editView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    textInput = s;
                    if (textNum != null) {
                        textNum.setText(String.valueOf(MAX_TEXT_INPUT_LEN - s.length()));
                    }
                    Editable editable = editView.getText();
                    int len = editable.length();

                    if(len > MAX_TEXT_INPUT_LEN)
                    {
                        int selEndIndex = Selection.getSelectionEnd(editable);
                        String str = editable.toString();
                        //截取新字符串
                        String newStr = str.substring(0,MAX_TEXT_INPUT_LEN);
                        editView.setText(newStr);
                        editable = editView.getText();

                        //新字符串的长度
                        int newLen = editable.length();
                        //旧光标位置超过字符串长度
                        if(selEndIndex > newLen)
                        {
                            selEndIndex = editable.length();
                        }
                        //设置新光标所在的位置
                        Selection.setSelection(editable, selEndIndex);

                    }
                }
                
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                    
                }
                
                @Override
                public void afterTextChanged(final Editable s) {

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
        if (cancelListener != null) {
            btnCancel.setOnClickListener(cancelListener);
        }
        else {
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    InputDlg.this.dismiss();
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
}
