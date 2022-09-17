package com.mtime.base.network;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public interface DynamicHeaderInterceptor {
    int REQUEST_TYPE_GET = 0;
    int REQUEST_TYPE_POST = 1;
    
    @IntDef({REQUEST_TYPE_GET, REQUEST_TYPE_POST})
    @Retention(RetentionPolicy.SOURCE)
    @interface RequestType {
    }
    
    Map<String, String> checkHeaders(@RequestType int type, Map<String, String> headers, String url, Map<String, String> params);
    
}
