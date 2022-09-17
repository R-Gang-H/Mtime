package com.mtime.mtmovie.network.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yinguanping on 17/1/13.
 */

public class DownLoadInterceptor implements Interceptor {
    String mBaseIp;

    public DownLoadInterceptor(String ip) {
        mBaseIp = ip;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder().
                addHeader("RANGE", "bytes=" + "breakprogress" + "-").build();//实现断点下载
        String url = request.url().toString();
        url.replace(mBaseIp, "");
        Request newRequest = request.newBuilder().url(url).build();
        return chain.proceed(newRequest);
    }
}