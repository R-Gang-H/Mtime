package com.jssdk.beans.calljs;

public class JSCallbackBean<T> {
    public static final String CALL_JS_SHARE_SUCCESS = "receiveShareCallback(1)";
    public static final String CALL_JS_SHARE_FAILURE = "receiveShareCallback(0)";
    public static final String CALL_JS_GIFT = "receiveGiftCallback()";
    
    public T data;
    public String errMsg;
    public String token;
    public int code; //0表示调取成，1表示原生方法不存在
    public boolean success;
}
