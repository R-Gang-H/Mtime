package com.mtime.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mtime.R;
/**
 * 带两个EditText的dialog
 * 
 * @category 例 添加礼品卡dialog
 */
public class InputTwoDlg extends Dialog
{
    private Button okButton;
    private Button cancelButton;
    private CheckBox checkBox;
    private View layout_fill;//为了好看
    public CheckBox getCheckBox()
    {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox)
    {
        this.checkBox = checkBox;
    }

    private EditText pwdEdit;
    private EditText numEdit;
    private LinearLayout layoutCheckbox;

    public InputTwoDlg(Context context)
    {
        super(context, R.style.upomp_bypay_MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input_two);
        okButton = findViewById(R.id.btn_ok);
        cancelButton = findViewById(R.id.btn_cancel);
        setLayoutCheckbox(findViewById(R.id.layout_checkbox));
        setPwdEdit(findViewById(R.id.edit_pwd));
        setNumEdit(findViewById(R.id.edit_account));
        cancelButton.setSingleLine(true);
        checkBox = findViewById(R.id.cb_choose);
        layout_fill =  findViewById(R.id.layout_fill);
    }

    public void setBtnOKListener(Button.OnClickListener click)
    {
        if (okButton != null)
        {
            okButton.setOnClickListener(click);
        }
    }

    public void setBtnCancelListener(Button.OnClickListener click)
    {
        if (cancelButton != null)
        {
            cancelButton.setOnClickListener(click);
        }
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener change)
    {
        if (checkBox != null)
        {
            checkBox.setOnCheckedChangeListener(change);
        }
    }

    public void setBtnOKText(String str)
    {
        if (okButton != null)
        {
            okButton.setText(str);
        }
    }

    public void setBtnCancelText(String str)
    {
        if (cancelButton != null)
        {
            cancelButton.setText(str);
        }
    }

    public EditText getPwdEdit()
    {
        return pwdEdit;
    }

    public void setPwdEdit(EditText pwdEdit)
    {
        this.pwdEdit = pwdEdit;
    }

    public EditText getNumEdit()
    {
        return numEdit;
    }

    public void setNumEdit(EditText numEdit)
    {
        this.numEdit = numEdit;
    }

    public LinearLayout getLayoutCheckbox()
    {
        return layoutCheckbox;
    }

    public void setLayoutCheckbox(LinearLayout layoutCheckbox)
    {
        this.layoutCheckbox = layoutCheckbox;
    }
    //非会员使用礼品卡支付 true
    public void setFillLayoutVisible(int visibility) {
        this.layout_fill.setVisibility(visibility);
    }

}
