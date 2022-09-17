package com.mtime.payment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mtime.R;

public class OrderPayRechargeWindowDialog extends Dialog {
    
    private TextView         btnOK              = null;
    private EditText       editView           = null;
    private final TextView textView           = null;
    private int            mLayoutID          = 0;
    private CharSequence   textInput;
    private int            MAX_TEXT_INPUT_LEN = 200;
    private boolean        isShowInput        = true;
    private View    backBtn;
    
    /**
     * @return the textView
     */
    public TextView getTextView() {
        return textView;
    }
    
    private View.OnClickListener okListener = null;
    
    /**
     * @return 获取到确定按钮
     */
    public TextView getBtnOK() {
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
     * 设置返回按钮的事件
     */
    public void setBtnBackClickListener(View.OnClickListener backClickListener) {
        if (backBtn != null) {
            backBtn.setOnClickListener(backClickListener);
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
    
    public OrderPayRechargeWindowDialog(final Context context) {
        super(context);
    }
    
    public OrderPayRechargeWindowDialog(final Context context, final int resLayout) {
        super(context, R.style.fullscreen_notitle_dialog);
        mLayoutID = resLayout;
    }
    
    public OrderPayRechargeWindowDialog(final Context context, final int resLayout, final boolean showInput) {
        this(context, resLayout);
        isShowInput = showInput;
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(mLayoutID);
        initTitle();
        btnOK = findViewById(R.id.btn_ok);
        editView = findViewById(R.id.dlg_text);
        if (isShowInput) {
            editView.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    textInput = s;
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
        
        if (okListener != null) {
            btnOK.setOnClickListener(okListener);
        }
        else {
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    OrderPayRechargeWindowDialog.this.dismiss();
                }
            });
        }
    }
    
    private void initTitle() {
        findViewById(R.id.share).setVisibility(View.INVISIBLE);
        findViewById(R.id.favorite).setVisibility(View.INVISIBLE);
        findViewById(R.id.message).setVisibility(View.INVISIBLE);
        findViewById(R.id.feedbacklist).setVisibility(View.INVISIBLE);
        backBtn = findViewById(R.id.back);
    }
    
    public void setTitle(String titleString) {
        ((TextView) findViewById(R.id.title)).setText(titleString);
    }
    
    /**
     * 设置最大输入字数
     */
    public void setMaxNumber(final int maxNum) {
        MAX_TEXT_INPUT_LEN = maxNum;
    }
}
