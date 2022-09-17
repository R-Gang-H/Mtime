package com.mtime.payment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mtime.R;

/**
 * 支付-完成支付全屏dialog
 * 
 */
public class OrderPayAgainDialog extends Dialog {
    private TextView btnOK;
    private TextView btnChange;
    
    public OrderPayAgainDialog(final Context context) {
        super(context, R.style.fullscreen_notitle_dialog);
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_order_pay_again);
        initTitle();
        btnOK = findViewById(R.id.btn_ok);
        btnChange = findViewById(R.id.btn_change);
    }
    
    private void initTitle() {
        findViewById(R.id.share).setVisibility(View.INVISIBLE);
        findViewById(R.id.favorite).setVisibility(View.INVISIBLE);
        findViewById(R.id.message).setVisibility(View.INVISIBLE);
        findViewById(R.id.feedbacklist).setVisibility(View.INVISIBLE);
        findViewById(R.id.back).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.title)).setText("");
    }
    
    public void setBtnOKListener(final View.OnClickListener click) {
        if (btnOK != null) {
            btnOK.setOnClickListener(click);
        }
    }
    public void setBtnChangeListener(final View.OnClickListener click) {
        if (btnChange != null) {
            btnChange.setOnClickListener(click);
        }
    }

    public void setCancleText(String cancleText) {
        if (btnChange != null && !TextUtils.isEmpty(cancleText)){
            btnChange.setText(cancleText);
        }
    }
}
