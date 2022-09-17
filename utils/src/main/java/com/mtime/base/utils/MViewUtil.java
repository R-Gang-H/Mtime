package com.mtime.base.utils;

import android.view.View;

/**
 * Created by ZhouSuQiang on 2017/11/30.
 */

public class MViewUtil {
    private static final int REPEAT_CLICK_DELAY = 500;
    
    /**
     * 判断是否重复点击(一定时间内)
     * @param view
     * @return
     */
    public static boolean isNotRepeatClick(View view) {
        if(null != view) {
            Object tag = view.getTag(R.id.view_tag_last_click_time);
            long curTime = System.currentTimeMillis();
            if(null != tag && tag instanceof Long) {
                long lastTime = (long)tag;
                if(curTime - lastTime > REPEAT_CLICK_DELAY) {
                    view.setTag(R.id.view_tag_last_click_time, curTime);
                    return true;
                } else {
                    return false;
                }
            } else if(null == tag) {
                view.setTag(R.id.view_tag_last_click_time, curTime);
            }
        }
        return true;
    }
}
