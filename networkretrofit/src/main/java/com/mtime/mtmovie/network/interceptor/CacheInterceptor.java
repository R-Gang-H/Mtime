package com.mtime.mtmovie.network.interceptor;

import com.mtime.mtmovie.network.utils.CheckUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yinguanping on 17/1/13.
 * 这样做是为了防止客户端有缓存需求，而服务器返回的header里面不支持缓存,此处做拦截
 * 自定义缓存Interceptor，这个header一定不能被其他地方使用，不然会被覆盖值。这里我们定义的header的key名字为：Cache-Time。
 * 我们在拦截器里去取这个header。如果取得了不为空的值，说明这个请求是要支持缓存的，缓存的时间就是Cache-Time对应的值
 */

public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Request.Builder requestBuilder = request.newBuilder();
        request = requestBuilder.build();
        Response response = chain.proceed(request);
        String cache = request.header("Cache-Time");
        if (!CheckUtil.checkNULL(cache)) {
            Response response1 = response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "max-age=" + cache)
                    .build();
            return response1;
        } else {
            return response;
        }
    }
}
