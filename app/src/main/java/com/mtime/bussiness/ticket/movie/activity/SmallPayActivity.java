package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.mtime.bussiness.ticket.movie.adapter.SmallPayAdapter;
import com.mtime.bussiness.ticket.movie.bean.CommodityList;
import com.mtime.bussiness.ticket.movie.bean.SmallPayBean;
import com.mtime.bussiness.ticket.movie.widget.CYTextView;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * 小卖页
 * 
 */
public class SmallPayActivity extends BaseActivity {
    private ListView                 listView;
    private TextView                   nextBtn;
    private SmallPayBean             sBean;
    private LinearLayout             priceLin;
    private TextView                 limit;
    private TextView                 price;
    private ArrayList<CommodityList> hasCountList;
    private boolean isFromSelectActivity;

    public static final String KEY_SMALL_PAY_LIST = "smallpaylist"; // 小卖
    public static final String KEY_IS_FROM_SEATSELECT_ACTIVITY = "is_from_seatselect_activity";

    @Override
    protected void onInitVariable() {
        hasCountList = new ArrayList<CommodityList>();
        sBean = (SmallPayBean) getIntent().getSerializableExtra(KEY_SMALL_PAY_LIST);
        isFromSelectActivity = getIntent().getBooleanExtra(KEY_IS_FROM_SEATSELECT_ACTIVITY, false);

        setPageLabel("smallBusiness");
    }
    
    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.act_smallpay);
        View navBar = findViewById(R.id.navigationbar);
        new TitleOfNormalView(this, navBar, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, getResources().getString(
                R.string.str_smallpay_title), null);
        listView = findViewById(R.id.small_pay_list);
        nextBtn = findViewById(R.id.btn_next_step);
        priceLin = findViewById(R.id.lin_total_price);
        limit = findViewById(R.id.tv_limit);
        price = findViewById(R.id.tv_total_price);
        CYTextView small_pay_tip = findViewById(R.id.small_pay_tip);
        if (sBean != null && !TextUtils.isEmpty(sBean.getTips())){
            SpannableString ss = new SpannableString(sBean.getTips());
            small_pay_tip.setMText(ss);
            small_pay_tip.setTextSize(15);
            small_pay_tip.setTextColor(getResources().getColor(R.color.color_9f4400));
        } else {
            SpannableString ss = new SpannableString(getResources().getString(R.string.str_smallpay_headtip));
            small_pay_tip.setMText(ss);
            small_pay_tip.setTextSize(15);
            small_pay_tip.setTextColor(getResources().getColor(R.color.color_9f4400));
        }
    }

    @Override
    protected void onInitEvent() {
        if (sBean != null && sBean.getCommodityList() != null && sBean.getCommodityList().size() > 0) {
            SmallPayAdapter spAdapter = new SmallPayAdapter(SmallPayActivity.this, sBean.getCommodityList());
            listView.setAdapter(spAdapter);
            
        }
        nextBtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = getIntent();
                for (int i = 0; i < sBean.getCommodityList().size(); i++) {
                    if (sBean.getCommodityList().get(i).getCount() > 0) {
                        CommodityList cList = sBean.getCommodityList().get(i);
                        hasCountList.add(cList);
                    }
                    
                }
                if (hasCountList.size() > 0) {
                    intent.putExtra(App.getInstance().HAS_COUNT_LIST, hasCountList);
                }
                intent.putExtra(App.getInstance().IS_FROM_SEATSELECT_ACTIVITY, isFromSelectActivity);
                startActivity(OrderConfirmActivity.class, intent);
                finish();
                
            }
        });
        
    }
    
    public void refreshPrice() {
        double totalPrice = 0;
        for (int i = 0; i < sBean.getCommodityList().size(); i++) {
            CommodityList cList = sBean.getCommodityList().get(i);
            totalPrice += cList.getPrice() * cList.getCount();
        }
        if (totalPrice > 0) {
            priceLin.setVisibility(View.VISIBLE);
            limit.setVisibility(View.GONE);
            BigDecimal bd = new BigDecimal(Double.toString(totalPrice));
            double d = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            price.setText(String.valueOf(d));
        }
        else {
            priceLin.setVisibility(View.GONE);
            limit.setVisibility(View.VISIBLE);
        }
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

    public static void launch(Context context, Intent launcher, SmallPayBean payBean, boolean isFromSelectActivity){
        launcher.putExtra(KEY_SMALL_PAY_LIST,payBean);
        launcher.putExtra(KEY_IS_FROM_SEATSELECT_ACTIVITY,isFromSelectActivity);
        ((BaseActivity)context).startActivity(SmallPayActivity.class,launcher);
    }
    
}
