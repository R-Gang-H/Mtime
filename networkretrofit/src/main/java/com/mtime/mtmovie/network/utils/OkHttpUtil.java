package com.mtime.mtmovie.network.utils;

import android.util.Log;

import com.kotlin.android.retrofit.cookie.CookieManager;
import com.mtime.mtmovie.network.CacheProvide;
import com.mtime.mtmovie.network.cookie.CookieJarManager;
import com.mtime.mtmovie.network.interceptor.CacheInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by yinguanping on 17/1/17.
 * okhttp初始化
 */

public class OkHttpUtil {
    private static long CONNECT_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 15;
    private static final long WRITE_TIMEOUT = 15;
    /**
     * 初始化okHttp
     */
    public static OkHttpClient okHttpClient(String BASE_URL, long timeout, CookieJarManager cookieJarManager, CacheProvide cacheProvide, CacheInterceptor cacheInterceptor) {
        OkHttpUtil.CONNECT_TIMEOUT = timeout > 0 ? timeout : OkHttpUtil.CONNECT_TIMEOUT;
        OkHttpClient okHttpClient;
        synchronized (OkHttpUtil.class) {
//            X509TrustManager trustManager = Platform.get().trustManager(HttpsURLConnection.getDefaultSSLSocketFactory());
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(cacheInterceptor)
                    .cache(cacheProvide.provideCache())
//                    .cookieJar(cookieJarManager)
                    .cookieJar(CookieManager.Companion.getInstance().getCookieJar())
//                    .sslSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), trustManager)
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                    .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                    .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                    .addInterceptor(logInterceptor())
                    .build();
            Log.e("NET_", "OkHttpClient build");
//            if (BuildConfig.DEBUG) {//printf logs while  debug
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                client = client.newBuilder().addInterceptor(logging).build();
//            }
            okHttpClient = client;
        }
        return okHttpClient;
    }

    /**
     * 日志处理拦截器
     */
    public static Interceptor logInterceptor() {
        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(
                new HttpLoggingInterceptor.Logger() {
                    final StringBuilder sb = new StringBuilder();
                    @Override
                    public void log(String message) {
                        if (message.contains("-->") && !message.contains("--> END")) {
                            if (sb.length() > 0) {
                                Log.e("NET_", "HttpLoggingInterceptor1 " + sb);
                                sb.setLength(0);
                            }
                        }
                        sb.append(message);
                        sb.append("\n");
                        if (message.contains("<-- END HTTP")) {
                            Log.e("NET_", "HttpLoggingInterceptor2 " + sb);
                            sb.setLength(0);
                        }
                    }
                }
        );
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return logInterceptor;
    }
}