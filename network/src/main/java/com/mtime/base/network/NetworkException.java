package com.mtime.base.network;

/**
 * Created by LiJiaZhi on 17/3/24.
 */

public class NetworkException<T> {

    protected int code;
    protected Exception originException;
    protected String message;
    protected T data;
    /**
     * 非正式的json 例如QQ 请求 时候的返回string。
     */
    protected String informalityJson;
    public NetworkException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public NetworkException(Exception e) {
        this.code = NetworkConfig.CODE_REQUEST_Exception_Local;
        this.message = e.getMessage();
        this.originException = e;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
    public T getData(){
        return data;
    }
    public String getInformalityJson(){
        return informalityJson;
    }
}
