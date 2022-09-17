package com.mtime.mtmovie.network;

import android.content.Context;
import android.widget.Toast;

import com.kotlin.android.retrofit.client.ClientManager;
import com.mtime.mtmovie.network.cookie.CookieJarManager;
import com.mtime.mtmovie.network.cookie.CookiePersistor;
import com.mtime.mtmovie.network.cookie.FileCookiePersistor;
import com.mtime.mtmovie.network.cookie.PersistentCookieJar;
import com.mtime.mtmovie.network.cookie.SetCookieCache;
import com.mtime.mtmovie.network.cookie.SharedPrefsCookiePersistor;
import com.mtime.mtmovie.network.interceptor.CacheInterceptor;
import com.mtime.mtmovie.network.interfaces.Fail;
import com.mtime.mtmovie.network.interfaces.HeadersInterceptor;
import com.mtime.mtmovie.network.interfaces.ParamsInterceptor;
import com.mtime.mtmovie.network.interfaces.Progress;
import com.mtime.mtmovie.network.interfaces.Success;
import com.mtime.mtmovie.network.utils.CheckUtil;
import com.mtime.mtmovie.network.utils.OkHttpUtil;
import com.mtime.mtmovie.network.utils.WriteFileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.cache.DiskLruCache;
import okhttp3.internal.concurrent.TaskRunner;
import okhttp3.internal.io.FileSystem;
import okio.ByteString;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.mtime.mtmovie.network.ApiClient.CookieSaveingMode.SHAREDPREFERENCES;
import static com.mtime.mtmovie.network.CacheProvide.CACHE_SIZE;

/**
 * Created by yinguanping on 17/1/13. 创建retrofit请求
 */

public class ApiClient {
    private static volatile ApiClient mInstance;
    private static volatile RetrofitHttpService mService;
    private static String mVersionApi;
    private static Context context;
    private final ParamsInterceptor mParamsInterceptor;
    private final HeadersInterceptor mHeadersInterceptor;
    private static String cachePath;

    // 构造函数私有，不允许外部调用
    private ApiClient(RetrofitHttpService mService, Context context, String cachePath,
        ParamsInterceptor mParamsInterceptor, HeadersInterceptor mHeadersInterceptor) {
        ApiClient.mService = mService;
        ApiClient.context = context;
        ApiClient.cachePath = cachePath;
        this.mParamsInterceptor = mParamsInterceptor;
        this.mHeadersInterceptor = mHeadersInterceptor;
    }

    public static RetrofitHttpService getService() {
        if (mInstance == null) {
            throw new NullPointerException("ApiClient has not be initialized");
        }
        return mService;
    }

    public static String getCachePath() {
        return cachePath;
    }

    public enum CookieSaveingMode {
        SDCARD, SHAREDPREFERENCES
    }

    public static class SingletonBuilder {
        private Context appliactionContext;
        private String baseUrl = "https://api-m.mtime.cn";// 这里可以传baseurl进来。也可以不设置，默认设置是防止报错。在请求时设置了全地址这个就失效了
        private long timeout;
        private String cachePath;
        private String cookiePath;
        private List<String> servers = new ArrayList<>();
        private String versionApi = "";
        private ParamsInterceptor paramsInterceptor;
        private HeadersInterceptor headersInterceptor;
        private CookieSaveingMode cookieSaveingMode = SHAREDPREFERENCES;// 默认Sharepreference
        private OkHttpClient client_default;
        private CookieJarManager cookieJarManager;
        private static CacheProvide cacheProvide;

        public SingletonBuilder context(Context context) {
            this.appliactionContext = context;
            return this;
        }

        public SingletonBuilder cookieSaveingMode(CookieSaveingMode cookieSaveingMode) {
            this.cookieSaveingMode = cookieSaveingMode;
            return this;
        }

        public SingletonBuilder cachePath(String cachePath) {
            this.cachePath = cachePath;
            return this;
        }

        public SingletonBuilder cookiePath(String cookiePath) {
            this.cookiePath = cookiePath;
            return this;
        }

        public SingletonBuilder timeout(long timeout) {
            this.timeout = timeout;
            return this;
        }

        public SingletonBuilder client(OkHttpClient client) {
            this.client_default = client;
            return this;
        }

        public SingletonBuilder versionApi(String versionApi) {
            this.versionApi = versionApi;
            return this;
        }

        public SingletonBuilder paramsInterceptor(ParamsInterceptor interceptor) {
            this.paramsInterceptor = interceptor;
            return this;
        }

        public SingletonBuilder headersInterceptor(HeadersInterceptor headersInterceptor) {
            this.headersInterceptor = headersInterceptor;
            return this;
        }

        public SingletonBuilder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public SingletonBuilder addServerUrl(String ipUrl) {
            this.servers.add(ipUrl);
            return this;
        }

        public SingletonBuilder serverUrls(List<String> servers) {
            this.servers = servers;
            return this;
        }

        public CookieJarManager getCookieJarManagerInstance() {
            return this.cookieJarManager;
        }

        public OkHttpClient getClient() {
            return client_default;
        }

        public static void clearCache() {
            if (cacheProvide != null)
                cacheProvide.clearCache();
        }

        public ApiClient build() {
            if (appliactionContext == null) {
                throw new NullPointerException("can not init retrofit ,because of context is null");
            }
            if (cookieJarManager == null) {
                CookiePersistor cookiePersistor;
                switch (cookieSaveingMode) {
                    case SDCARD:
                        cookiePersistor = new FileCookiePersistor(appliactionContext, cookiePath);
                        break;
                    case SHAREDPREFERENCES:
                        cookiePersistor = new SharedPrefsCookiePersistor(appliactionContext);
                        break;
                    default:// 默认SHAREDPREFERENCES存储
                        cookiePersistor = new SharedPrefsCookiePersistor(appliactionContext);
                        break;
                }
                cookieJarManager = new PersistentCookieJar(new SetCookieCache(), cookiePersistor);
            }

            if (cacheProvide == null) {
                cacheProvide = new CacheProvide(appliactionContext, cachePath);
            }

            /**
             * 初始化默认clinet和service，即默认读缓存，若缓存过期或不存在缓存，则走网络
             */
            CacheInterceptor cacheDefault = new CacheInterceptor();
            if (client_default == null) {
                client_default = ClientManager.INSTANCE.getClient();
//                client_default = OkHttpUtil.okHttpClient(baseUrl, timeout, cookieJarManager, cacheProvide, cacheDefault);
            }
            //所有操作都在子线程进行返回
            Retrofit.Builder builder = new Retrofit.Builder().callbackExecutor(Executors.newCachedThreadPool());
            Retrofit retrofit = builder.baseUrl(baseUrl + "/")
                // .addConverterFactory(ByteConverterFactory.create())
                .client(client_default).build();
            RetrofitHttpService mService_default = retrofit.create(RetrofitHttpService.class);

            mInstance =
                new ApiClient(mService_default, appliactionContext, cachePath, paramsInterceptor, headersInterceptor);
            return mInstance;
        }
    }

    public static String V(String url) {
        if (checkNULL(mVersionApi)) {
            throw new NullPointerException("can not add VersionApi ,because of VersionApi is null");
        }
        if (!url.contains(mVersionApi)) {
            return mVersionApi + url;
        }
        return url;
    }

    public static Map<String, String> checkParams(Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        if (mInstance.mParamsInterceptor != null) {
            params = mInstance.mParamsInterceptor.checkParams(params);
        }
        // retrofit的params的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getValue() == null) {
                params.put(entry.getKey(), "");
            }
        }
        return params;
    }

    public static Map<String, String> checkHeaders(Map<String, String> headers) {
        if (headers == null) {
            headers = new HashMap<>();
        }
        if (mInstance.mHeadersInterceptor != null) {
            headers = mInstance.mHeadersInterceptor.checkHeaders(headers);
        }
        // retrofit的headers的值不能为null，此处做下校验，防止出错
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getValue() == null) {
                headers.put(entry.getKey(), "");
            }
        }
        return headers;
    }

    // 判断是否NULL
    public static boolean checkNULL(String str) {
        return str == null || "null".equals(str) || "".equals(str);

    }

    public static void Error(Context context, String msg) {
        if (checkNULL(msg)) {
            msg = "未知异常";
        }
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static String message(String mes) {
        if (checkNULL(mes)) {
            mes = "似乎已断开与互联网连接";
        }

        if (mes.equals("timeout") || mes.equals("SSL handshake timed out")) {
            return "网络请求超时";
        } else {
            return mes;
        }

    }

    final static Map<String, Call> CALL_MAP = new HashMap<>();

    /**
     * 添加某个请求
     */
    private static synchronized void putCall(Object tag, String url, Call call) {
        if (tag == null)
            return;
        synchronized (CALL_MAP) {
            CALL_MAP.put(tag.toString() + url, call);
        }
    }

    /**
     * 取消某个界面都所有请求，或者是取消某个tag的所有请求 如果要取消某个tag单独请求，tag需要转入tag+url
     */
    public static synchronized void cancel(Object tag) {
        if (tag == null)
            return;
        List<String> list = new ArrayList<>();
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.startsWith(tag.toString())) {
                    CALL_MAP.get(key).cancel();
                    list.add(key);
                }
            }
        }
        for (String s : list) {
            removeCall(s);
        }

    }

    /**
     * 移除某个请求
     */
    private static synchronized void removeCall(String url) {
        synchronized (CALL_MAP) {
            for (String key : CALL_MAP.keySet()) {
                if (key.contains(url)) {
                    url = key;
                    break;
                }
            }
            CALL_MAP.remove(url);
        }
    }

    public static class Builder {
        Map<String, String> params = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, RequestBody> requestBodys = new HashMap<>();
        String url;
        String savePath;
        Fail mErrorCallBack;
        Success mSuccessCallBack;
        Progress mProgressCallBack;
        boolean addVersion = false;
        boolean isUpdateCache = false;
        Object tag;

        // json string
        String jsonString;

        public Builder CacheTime(long time) {
            headers.put("Cache-Time", String.valueOf(time));
            return this;
        }

        public Builder Url(String url) {
            this.url = url;
            return this;
        }

        public Builder savePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public Builder Tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder json(String json) {
            this.jsonString = json;
            return this;
        }

        /**
         * 该方法与Params(String key, String value)互斥，用了传map后，则同时不可存在单独键值对参数，传了也会无效 如果必须要使用，则需要在传map参数之后
         *
         * @param params
         * @return
         */
        public Builder Params(Map<String, String> params) {
            if (params != null) {
                this.params = params;
            }
            return this;
        }

        public Builder Params(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public Builder Headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public Builder Headers(String key, String value) {
            if (!checkNULL(value)) {
                this.headers.put(key, value);
            }
            return this;
        }

        public Builder isUpdateCache(boolean isUpdateCache) {
            this.isUpdateCache = isUpdateCache;
            return this;
        }

        public Builder requestBodys(Map<String, Object> requestBodys) {
            Iterator<Map.Entry<String, Object>> it = requestBodys.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                requestBodys(entry.getKey(), entry.getValue());
            }
            return this;
        }

        // 根据传进来的Object对象来判断是String还是File类型的参数
        public Builder requestBodys(String key, Object o) {
            if (o instanceof String) {
                RequestBody body = RequestBody.create(MediaType.parse("text/plain"), (String) o);
                this.requestBodys.put(key, body);
            } else if (o instanceof File) {
                RequestBody body = RequestBody.create(MediaType.parse("multipart/form-data"), (File) o);
                this.requestBodys.put(key + "\"; filename=\"" + ((File) o).getName() + "", body);
            }
            return this;
        }

        public Builder Success(Success success) {
            this.mSuccessCallBack = success;
            return this;
        }

        public Builder Progress(Progress progress) {
            this.mProgressCallBack = progress;
            return this;
        }

        public Builder Version() {
            this.addVersion = true;
            return this;
        }

        public Builder Fail(Fail error) {
            this.mErrorCallBack = error;
            return this;
        }

        public Builder() {
            this.setParams();
        }

        public Builder(String url) {
            this.setParams(url);
        }

        private void setParams() {
            this.setParams(null);
        }

        private void setParams(String url) {
            if (mInstance == null) {
                throw new NullPointerException("ApiClient has not be initialized");
            }
            this.url = url;
            this.params = new HashMap<>();
        }

        private String checkUrl(String url) {
            if (checkNULL(url)) {
                throw new NullPointerException("absolute url can not be empty");
            }
            if (addVersion) {
                url = V(url);
            }
            return url;
        }

        public void get() {
            if (isUpdateCache) {
                try {
                    File cacheDirectory = !CheckUtil.checkNULL(cachePath) ? new File(cachePath) : context.getCacheDir();
                    DiskLruCache diskLruCache = new DiskLruCache(FileSystem.SYSTEM, cacheDirectory, 201105, 2, CACHE_SIZE, TaskRunner.INSTANCE);
//                        DiskLruCache.create(FileSystem.SYSTEM, cacheDirectory, 201105, 2, CACHE_SIZE);
                    diskLruCache.remove(ByteString.encodeUtf8(url).md5().hex());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Call<ResponseBody> call = getService().get(checkHeaders(headers), checkUrl(this.url), checkParams(params));
            putCall(tag, url, call);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response);
                    } else {
                        mErrorCallBack.Fail(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (!call.isCanceled()) {
                        mErrorCallBack.Fail(200, message(t.getMessage()), t);
                    }
                    if (tag != null)
                        removeCall(url);
                }
            });
        }

        public void post() {
            Call<ResponseBody> call = getService().post(checkHeaders(headers), checkUrl(this.url), checkParams(params));
            putCall(tag, url, call);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response);
                    } else {
                        mErrorCallBack.Fail(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (!call.isCanceled()) {
                        mErrorCallBack.Fail(200, message(t.getMessage()), t);
                    }
                    if (tag != null)
                        removeCall(url);
                }
            });
        }

        /**
         * post json
         */
        public void postJson() {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
            Call<ResponseBody> call = getService().postJson(checkHeaders(headers), checkUrl(this.url), body);
            putCall(tag, url, call);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response);
                    } else {
                        mErrorCallBack.Fail(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (!call.isCanceled()) {
                        mErrorCallBack.Fail(200, message(t.getMessage()), t);
                    }
                    if (tag != null)
                        removeCall(url);
                }
            });
        }

        // 下载
        public void download() {
            this.url = checkUrl(this.url);
            Call call = getService().download(url);
            putCall(tag, url, call);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call call, final Response response) {
                    if (response.code() == 200) {
                        new WriteFileUtil().writeFile((ResponseBody) response.body(), savePath, mProgressCallBack,
                            mSuccessCallBack, mErrorCallBack);
                    } else {
                        mErrorCallBack.Fail(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (!call.isCanceled()) {
                        mErrorCallBack.Fail(t);
                    }
                }
            });
        }

        // 上传
        public void upload() {
            this.url = checkUrl(this.url);
            if (requestBodys.size() == 0) {// 无效上传，没有上传实体参数
                return;
            }
            Call call = getService().upload(checkHeaders(headers), checkUrl(this.url), requestBodys);
            putCall(tag, url, call);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call call, Response response) {
                    if (response.code() == 200) {
                        mSuccessCallBack.Success(response);
                    } else {
                        mErrorCallBack.Fail(response.code(), message(response.message()), null);
                    }
                    if (tag != null)
                        removeCall(url);
                    requestBodys.clear();
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    if (!call.isCanceled()) {
                        mErrorCallBack.Fail(t);
                    }
                }
            });
        }
    }
}
