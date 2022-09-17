package com.mtime.bussiness.main;

import android.os.Bundle;

/**
 * @author ZhouSuQiang
 * @date 2018/8/30
 *
 * main页面与子页面（fragment）数据通信的统一封装
 */
public interface MainCommunicational {
    /**
     * main页面透传的Intent数据 事件
     */
    int EVENT_ON_PARSE_INTENT = 1001;
    
    /**
     * 统一处理事件的方法
     * @param eventCode
     * @param bundle
     */
    boolean onHandleMainEvent(int eventCode, Bundle bundle);
}
