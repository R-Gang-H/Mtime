package com.mtime.bussiness.ticket.movie.activity;

import android.os.Bundle;
import android.view.View;

import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

/**
 * 支付-添加优惠券页
 * 
 */
public class OrderPayActivateActivty extends BaseActivity {
    
    private View order_pay_activate_add_vouchercode;
    private View order_pay_activate_add_moviecard;
    
    @Override
    protected void onInitVariable() {
        setPageLabel("orderAddCoupon");
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_order_pay_activate);
        View navBar = this.findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, "添加优惠", null);
        order_pay_activate_add_vouchercode = findViewById(R.id.order_pay_activate_add_vouchercode);
        order_pay_activate_add_moviecard = findViewById(R.id.order_pay_activate_add_moviecard);
    }
    
    @Override
    protected void onInitEvent() {
        order_pay_activate_add_vouchercode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVouchercodeClick(view);
            }
        });
        order_pay_activate_add_moviecard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMoviecardClick(view);
            }
        });
    }
    
    @Override
    protected void onLoadData() {
        
    }
    
    @Override
    protected void onRequestData() {
        
    }
    
    @Override
    protected void onUnloadData() {
        
    }
    
    public void addVouchercodeClick(View view) {
        setResult(11);
        finish();
    }
    
    public void addMoviecardClick(View view) {
        setResult(12);
        finish();
    }
    
}
