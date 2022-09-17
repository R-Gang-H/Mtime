package com.mtime.base.widget;

import android.view.View;

import com.mtime.base.utils.MViewUtil;

/**
 * Created by ZhouSuQiang on 2017/11/25.
 * OnClickListener的包装类，防止重复点击
 */

public abstract class OnViewClickListener implements View.OnClickListener {
    
    @Override
    public void onClick(View v) {
        if(MViewUtil.isNotRepeatClick(v))
            onClicked(v);
    }
    
    public abstract void onClicked(View v);
}
