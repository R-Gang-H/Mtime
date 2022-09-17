package com.mtime.bussiness.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.View;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.kotlin.android.router.ext.ProviderExtKt;

import com.kotlin.android.app.router.path.RouterProviderPath;
import com.kotlin.android.app.router.provider.mine.IMineProvider;
import com.kotlin.android.user.login.UserLoginKt;
import com.mtime.frame.BaseActivity;
import com.mtime.R;
import com.kotlin.android.user.UserManager;
import com.mtime.bussiness.mine.adapter.MtimeCoinAdapter;
import com.mtime.bussiness.mine.bean.CouponExchangeResult;
import com.mtime.bussiness.mine.bean.MtimeCoinBean;
import com.mtime.bussiness.mine.bean.MtimeCoinListBean;
import com.mtime.network.RequestCallback;
import com.mtime.widgets.BaseTitleView;
import com.mtime.widgets.TitleOfNormalView;
import com.mtime.mtmovie.widgets.pullrefresh.LoadMoreFooterView;
import com.mtime.network.ConstantUrl;
import com.mtime.util.CustomAlertDlg;
import com.mtime.util.HttpUtil;
import com.mtime.util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MtimeCoinListActivity extends BaseActivity implements OnRefreshListener,OnLoadMoreListener,MtimeCoinAdapter.ExchangeCoinListener{

    private int mPageIndex = 1;
    private IRecyclerView mIRecyclerView;
    private LoadMoreFooterView mLoadMoreFooterView;
    private RequestCallback mRequestCallback;
    private MtimeCoinAdapter mMtimeCoinAdapter;
    private final ArrayList<MtimeCoinBean> mAllBeans = new ArrayList<>();

    private int mCouponType = 0;
    private RequestCallback mExchangeCouponCallback;
    private CustomAlertDlg mFailDialog;
    private CustomAlertDlg mSuccessDialog;
    @Override
    protected void onInitVariable() {

    }

    @Override
    protected void onInitView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_mtime_coin_list);
        initTitle();
        mIRecyclerView = findViewById(R.id.public_irecyclerview);
        mIRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoadMoreFooterView = (LoadMoreFooterView) mIRecyclerView.getLoadMoreFooterView();
    }
    public void initTitle(){
        View navBar = findViewById(R.id.navigationbar);
        TitleOfNormalView titleView = new TitleOfNormalView(this,navBar, BaseTitleView.StructType.TYPE_NORMAL_SHOW_BACK_TITLE,
                getResources().getString(R.string.mtime_coin_exchange),null);
    }

    @Override
    protected void onInitEvent() {
        mIRecyclerView.setOnRefreshListener(this);
        mIRecyclerView.setOnLoadMoreListener(this);

        mRequestCallback = new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                mLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.GONE);
                mIRecyclerView.setRefreshing(false);
                MtimeCoinListBean bean = (MtimeCoinListBean)o;
                List<MtimeCoinBean> listBean = bean.getList();
                if(mPageIndex == 1){
                    mAllBeans.clear();
                    mAllBeans.addAll(listBean);
                    mMtimeCoinAdapter = new MtimeCoinAdapter(MtimeCoinListActivity.this,mAllBeans);
                    mMtimeCoinAdapter.setmExchangeCoinListener(MtimeCoinListActivity.this);
                    mIRecyclerView.setAdapter(mMtimeCoinAdapter);
                }else{
                    mAllBeans.addAll(listBean);
                    mMtimeCoinAdapter.notifyDataSetChanged();
                }
                if(!bean.isHasMore()){
                    mLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.THE_END);
                }
            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                mIRecyclerView.setRefreshing(false);
                UIUtil.showLoadingFailedLayout(MtimeCoinListActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onRequestData();
                    }
                });
            }
        };
        mExchangeCouponCallback = new RequestCallback() {
            @Override
            public void onSuccess(Object o) {
                UIUtil.dismissLoadingDialog();
                    CouponExchangeResult result = (CouponExchangeResult)o;
                    // 1:兑换成功 0:兑换失败
                    int bizCode = result.getBizCode();
                    String bizMsg = result.getBizMsg();
                    if(bizCode == 1){
                        showSuccessDialog();
                    }else{
                        showFailDialog(bizMsg);
                    }
            }

            @Override
            public void onFail(Exception e) {
                UIUtil.dismissLoadingDialog();
                showFailDialog(getResources().getString(R.string.exchange_fail));
            }
        };
    }

    @Override
    protected void onRequestData() {
        requestData(mPageIndex);
    }

    @Override
    protected void onLoadData() {

    }

    @Override
    protected void onUnloadData() {

    }

    @Override
    public void onRefresh() {
        mPageIndex = 1;
        requestData(mPageIndex);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        if (mLoadMoreFooterView.canLoadMore()) {
            mLoadMoreFooterView.setStatus(LoadMoreFooterView.Status.LOADING);
            mPageIndex++;
            requestData(mPageIndex);
        }
    }
    private void requestData(int pageIndex){
        UIUtil.showLoadingDialog(this);
        Map<String, String> param = new HashMap<>(1);
        param.put("pageIndex", String.valueOf(pageIndex));
        HttpUtil.get(ConstantUrl.GET_MTIME_COIN_LIST, param, MtimeCoinListBean.class, mRequestCallback);
    }
    private void exchangeCoupon(int couponId, int couponType, String couponName){
        UIUtil.showLoadingDialog(this);
        Map<String, String> param = new HashMap<>(3);
        param.put("couponId", String.valueOf(couponId));
        param.put("couponType", String.valueOf(couponType));
        param.put("couponTitle", couponName);
        HttpUtil.get(ConstantUrl.EXCHANGE_COUPON_BY_COIN, param, CouponExchangeResult.class, mExchangeCouponCallback);
    }
    @Override
    public void goExchange(int couponId, int couponType, String couponName,long coinNum) {
        // 优惠券类型，1:购物券，2:票务券
        mCouponType = couponType;
        showConfirmDialog(couponId,couponType,couponName,coinNum);
    }
    private void showConfirmDialog(final int couponId, final int couponType, final String couponName,long coinNum){
        final CustomAlertDlg dlg = new CustomAlertDlg(MtimeCoinListActivity.this,CustomAlertDlg.TYPE_OK_CANCEL);
        dlg.setBtnCancelListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dlg.dismiss();
            }
        });
        dlg.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                exchangeCoupon(couponId,couponType,couponName);
            }
        });
        dlg.show();
        dlg.setBtnCancelText(getResources().getString(R.string.close));
        dlg.setText(String.format(getResources().getString(R.string.exchange_confirm),coinNum));
    }
    private void showSuccessDialog(){
        if(mSuccessDialog != null){
            if(mSuccessDialog.isShowing()){
                return;
            }
            mSuccessDialog.show();
            return;
        }
        mSuccessDialog = new CustomAlertDlg(MtimeCoinListActivity.this,CustomAlertDlg.TYPE_OK_CANCEL);
        mSuccessDialog.setBtnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessDialog.dismiss();
            }
        });
        mSuccessDialog.setBtnOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuccessDialog.dismiss();
                Intent intent = new Intent();
                if(mCouponType == 1){
                    //商品
                    intent.putExtra("CouponRemindType", 2);
                }else{
                    //票务
                    intent.putExtra("CouponRemindType", 1);
                }
                // 2020/11/3 需要跳转到新的我的钱包界面
//                startActivity(MyVoucherListActivity.class, intent);
                IMineProvider iMineProvider = ProviderExtKt.getProvider(IMineProvider.class);
                if (null != iMineProvider) {
                    iMineProvider.startMyWalletActivity(null, null);
                }

            }
        });
        mSuccessDialog.show();
        mSuccessDialog.setBtnCancelText(getResources().getString(R.string.close));
        mSuccessDialog.setBtnOkText(getResources().getString(R.string.check_coupon));
        mSuccessDialog.setText(getResources().getString(R.string.exchange_success));
    }
    private void showFailDialog(String msg){
        if(mFailDialog !=null){
            if(mFailDialog.isShowing()){
                mFailDialog.dismiss();
            }
            mFailDialog = null;
        }
        mFailDialog = new CustomAlertDlg(MtimeCoinListActivity.this,CustomAlertDlg.TYPE_CANCEL);
        mFailDialog.setBtnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFailDialog.dismiss();
            }
        });
        mFailDialog.show();
        mFailDialog.setText(msg);
        mFailDialog.setBtnCancelText(getResources().getString(R.string.close));
    }



    /**
     * 自己定义refer
     * @param context
     * @param refer
     */
    public static void launch(Context context, String refer) {
        if (UserManager.Companion.getInstance().isLogin()) {
            Intent launcher = new Intent(context, MtimeCoinListActivity.class);
            context.startActivity(launcher);
            dealRefer(context,refer, launcher);
        } else {
            UserLoginKt.gotoLoginPage(context, null, null);
//            launcher = new Intent(context, LoginActivity.class);
        }

    }
}
