package com.mtime.bussiness.ticket.cinema.bean;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.mtime.R;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.cinema.adapter.PreferentialListAdapter;
import com.mtime.frame.App;
import com.mtime.frame.BaseActivity;
import com.mtime.network.ConstantUrl;
import com.mtime.network.RequestCallback;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;
import com.mtime.widgets.BaseTitleView.StructType;
import com.mtime.widgets.TitleOfNormalView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 影院概览页-优惠公告
 * 
 * @author ye
 * 
 */
public class CinemaPreferentialActivity extends BaseActivity {
    
    private ListView                listview;
    private String                  mCinemaId;

    private PreferentialListAdapter adapter;
    
    @Override
    protected void onInitVariable() {
        this.setSwipeBack(true);
        setPageLabel("cinemaDiscountNotice");
        mCinemaId = getIntent().getStringExtra(App.getInstance().KEY_CINEMA_ID);
    }
    
    @Override
    protected void onInitView(final Bundle savedInstanceState) {
        this.setContentView(R.layout.act_cinema_preferential_list);
        
        View title = this.findViewById(R.id.off_title);
        String label = this.getResources().getString(R.string.str_last_concessions);
        new TitleOfNormalView(this, title, StructType.TYPE_NORMAL_SHOW_BACK_TITLE, label, null);
        
        listview = findViewById(R.id.cinema_special_listview);
        adapter = new PreferentialListAdapter(this, null, null);
        listview.setAdapter(adapter);
    }
    
    @Override
    protected void onInitEvent() {
    }
    
    @Override
    protected void onLoadData() {
    }
    
    @Override
    protected void onUnloadData() {
    }
    
    @Override
    protected void onRequestData() {
        requestCinemaDetail();
    }
    
    private void requestCinemaDetail() {
        RequestCallback mCouponFavourableInfoCallback = new RequestCallback() {
            @Override
            public void onSuccess(final Object o) {
                UIUtil.dismissLoadingDialog();
                
                final CouponListJsonBean jsonBean = (CouponListJsonBean) o;
                List<Coupon> mCoupons = jsonBean.getCoupons();
                if ((mCoupons != null) && (mCoupons.size() > 0)) {
                    adapter.setDatas(mCoupons);
                }
                
            }
            
            @Override
            public void onFail(final Exception e) {
                UIUtil.dismissLoadingDialog();
                MToastUtils.showShortToast(e.getLocalizedMessage());
            }
        };
        
        UIUtil.showLoadingDialog(this);
        // Cinema/CouponFavourableInfo.api?cinemaid={0}
        Map<String, String> param = new HashMap<>(1);
        param.put("cinemaid", mCinemaId);
        HttpUtil.get(ConstantUrl.GET_COUPON_FAVOURABLE_INFO, param, CouponListJsonBean.class, mCouponFavourableInfoCallback);
    }
    
    
    /*private class ShareClickListener implements PreferentialListAdapter.OnShareClickListener {
        
        @Override
        public void onClick(final String fid) {
            ShareView view = new ShareView(CinemaPreferentialActivity.this);
            view.setValues(mCinemaId, ShareView.SHARE_TYPE_CINEMA_COUPONINFO, null, fid, null);
            view.showActionSheet();
        }
    }*/
    
}
