package com.mtime.payment.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mtime.R;

/**
 * 正在出票
 * 
 */
public class OrderPayTicketOutingDialog extends Dialog {
    
    private final TextView    textView  = null;
    
    public TextView getTextView() {
        return textView;
    }
    
    public OrderPayTicketOutingDialog(final Context context) {
        super(context, R.style.fullscreen_notitle_dialog);
    }
    
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_order_pay_ticket_outing);
        initTitle();
    }
    
    private void initTitle() {
        findViewById(R.id.share).setVisibility(View.INVISIBLE);
        findViewById(R.id.favorite).setVisibility(View.INVISIBLE);
        findViewById(R.id.message).setVisibility(View.INVISIBLE);
        findViewById(R.id.feedbacklist).setVisibility(View.INVISIBLE);
        findViewById(R.id.back).setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.title)).setText("出票中");
    }
    
}
