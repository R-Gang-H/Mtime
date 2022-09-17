package com.mtime.base.location;

/**
 * Created by mtime on 2017/10/30.
 */

public class LocationException {
    public static final int CODE_UNKNOWN = 0;
    public static final int CODE_PERMISSION_DENIED = 1;
    
    public int code = CODE_UNKNOWN;
    public String msg = "未知的定位错误";
    
    public LocationException() {
    }
    
    public LocationException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
