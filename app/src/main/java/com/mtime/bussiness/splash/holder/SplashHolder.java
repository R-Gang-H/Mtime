package com.mtime.bussiness.splash.holder;

import android.content.Context;

import com.kk.taurus.uiframe.v.ContentHolder;
import com.mtime.R;
import com.mtime.bussiness.common.bean.CommonAdListBean;
import com.mtime.bussiness.ticket.TabTicketFragment;
import com.mtime.util.ToolsUtils;

/**
 * Created by CSY on 2018/4/25.
 * 闪屏页-布局内容
 */
public class SplashHolder extends ContentHolder<CommonAdListBean> {

    public SplashHolder(Context context) {
        super(context);
    }

    @Override
    public void onCreate() {
        setContentView(R.layout.act_splash);
        TabTicketFragment.type = TabTicketFragment.TYPE_MOVIE_HOT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ToolsUtils.clear();
    }
}
