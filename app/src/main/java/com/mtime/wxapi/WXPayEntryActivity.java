package com.mtime.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.mtime.R;
import com.mtime.common.utils.LogWriter;
import com.mtime.frame.App;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
    	api = WXAPIFactory.createWXAPI(this, getResources().getString(R.string.key_wechat_app_id));
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp instanceof PayResp){
			LogWriter.e("mylog", "支付完成 , errCode = " + resp.errCode + ", errstr:" + resp.errStr + ", resp.getType():" + resp.getType() + ", ConstantsAPI.COMMAND_PAY_BY_WX:" + ConstantsAPI.COMMAND_PAY_BY_WX);
			App.getInstance().isFromWx = true;
		}else {
			LogWriter.e("mylog","分享?");
		}

		finish();
	}
}