package com.mtime.bussiness.ticket.movie.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.kk.taurus.uiframe.d.BaseState;
import com.kk.taurus.uiframe.v.BaseTitleBarHolder;
import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.base.utils.MToastUtils;
import com.mtime.bussiness.ticket.api.TicketApi;
import com.mtime.bussiness.ticket.movie.holder.TicketDirectSaleOrderDetailHolder;
import com.mtime.bussiness.ticket.movie.bean.TicketDetailBean;
import com.mtime.bussiness.ticket.movie.dialog.PhoneDialog;
import com.mtime.common.utils.Utils;
import com.mtime.frame.BaseActivity;
import com.mtime.util.JumpUtil;
import com.mtime.util.MtimeUtils;


/**
 * Created by JiaJunHui on 2018/5/22.
 */
public class TicketDirectSaleOrderDetailActivity extends BaseActivity<TicketDetailBean, TicketDirectSaleOrderDetailHolder> {

    public static final String KEY_ORDER_ID = "order_id";
    public static final String KEY_SERIAL_NO = "serialNo";

    private String mOrderId;
    private String mSerialNo;

    private TicketApi mTicketApi;

    public static void launch(Context context, String orderId, String serialNo){
        Intent intent = new Intent(context, TicketDirectSaleOrderDetailActivity.class);
        intent.putExtra(KEY_ORDER_ID, orderId);
        intent.putExtra(KEY_SERIAL_NO, serialNo);
        context.startActivity(intent);
    }

    @Override
    public ContentHolder onBindContentHolder() {
        return new TicketDirectSaleOrderDetailHolder(this);
    }

    @Override
    protected void onParseIntent() {
        super.onParseIntent();
        mOrderId = getIntent().getStringExtra(KEY_ORDER_ID);
        mSerialNo = getIntent().getStringExtra(KEY_SERIAL_NO);
    }

    @Override
    protected void onInit(Bundle savedInstanceState) {
        super.onInit(savedInstanceState);
        BaseTitleBarHolder titleBarHolder = getUserHolder().titleBarHolder;
        titleBarHolder.setTitle(R.string.str_order_detail);
        mTicketApi = new TicketApi();
    }

    @Override
    public void onHolderEvent(int eventCode, Bundle bundle) {
        super.onHolderEvent(eventCode, bundle);
        switch (eventCode){
            case TicketDirectSaleOrderDetailHolder.EVENT_GO_MAP_NAVIGATION:
                mapNavigation();
                break;
            case TicketDirectSaleOrderDetailHolder.EVENT_SAVE_TO_ALBUM:
                downloadSaveToAlbum();
                break;
            case TicketDirectSaleOrderDetailHolder.EVENT_INTENT_TO_CALL_PHONE:
                callPhone(bundle.getString(TicketDirectSaleOrderDetailHolder.KEY_SERVICE_NUMBER));
                break;
        }
    }

    private void callPhone(String phone){
        if(TextUtils.isEmpty(phone))
            return;
        PhoneDialog dialog = new PhoneDialog(this, phone);
        dialog.showCallPhoneDlg();
    }

    private void downloadSaveToAlbum() {
        mTicketApi.downloadTicketPhoto("download_image", mOrderId,
                MtimeUtils.DOWNLOAD_FILENAME + Utils.getMd5(mOrderId) + ".png",
                new NetworkManager.NetworkProgressListener<String>() {
                    @Override
                    public void onProgress(float v, long l, long l1) {

                    }
                    @Override
                    public void onSuccess(String s, String s2) {
                        MToastUtils.showShortToast(R.string.save_success_to_album);
                    }
                    @Override
                    public void onFailure(NetworkException<String> networkException, String s) {
                        MToastUtils.showShortToast(String.format(getString(R.string.save_failure_to_album), s));
                    }
                });
    }

    private void mapNavigation() {
        if(mData==null)
            return;
        // 跳转到地图
        String cinemaId = mData.getCinemaId();
        String cinemaName = mData.getCname();
        String cinemaAddress = mData.getcAddress();
        double longitude = mData.getBaiduLongitude();
        double latitude = mData.getBaiduLatitude();
        JumpUtil.startMapViewActivity(this,longitude,latitude,cinemaId,cinemaName,cinemaAddress,"");
    }

    @Override
    protected void onLoadState() {
        setPageState(BaseState.LOADING);
        mTicketApi.getDirectSaleTicketDetail("getDirectTicketDetail", mOrderId, mSerialNo,
                new NetworkManager.NetworkListener<TicketDetailBean>() {
            @Override
            public void onSuccess(TicketDetailBean ticketDetailBean, String s) {
                if(ticketDetailBean!=null && ticketDetailBean.isLegalNeoElectronicCode()){
                    setData(ticketDetailBean);
                    setPageState(BaseState.SUCCESS);
                }else{
                    setPageState(BaseState.ERROR);
                }
            }
            @Override
            public void onFailure(NetworkException<TicketDetailBean> networkException, String s) {
                setPageState(BaseState.ERROR);
            }
        });
    }
}
