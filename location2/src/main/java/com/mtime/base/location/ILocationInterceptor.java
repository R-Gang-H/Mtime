package com.mtime.base.location;

/**
 * Created by ZhouSuQiang on 2017/11/6.
 */

public interface ILocationInterceptor {
    void intercept(LocationInfo locationInfo, OnLocationInterceptorListener listener);
    
    interface OnLocationInterceptorListener {
        void onInterceptResult(LocationInfo locationInfo);
    }
}
