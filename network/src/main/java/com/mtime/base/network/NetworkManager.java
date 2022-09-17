package com.mtime.base.network;

import android.content.Context;
import android.text.TextUtils;

import com.mtime.base.bean.MParserBean;
import com.mtime.base.bean.MSyncBaseBean;
import com.mtime.base.cache.MCacheManager;
import com.mtime.base.cache.MFileCache;
import com.mtime.base.utils.DispatchAsync;
import com.mtime.base.utils.MJsonUtils;
import com.mtime.base.utils.MLogWriter;
import com.mtime.base.utils.MNetworkUtils;
import com.mtime.base.utils.MTimeUtils;
import com.mtime.mtmovie.network.ApiClient;
import com.mtime.mtmovie.network.cookie.CookieJarManager;
import com.mtime.mtmovie.network.interfaces.Fail;
import com.mtime.mtmovie.network.interfaces.HeadersInterceptor;
import com.mtime.mtmovie.network.interfaces.ParamsInterceptor;
import com.mtime.mtmovie.network.interfaces.Progress;
import com.mtime.mtmovie.network.interfaces.Success;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.zip.GZIPInputStream;

import okhttp3.Cookie;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by LiJiaZhi on 17/3/23.
 * <p>
 * 网络----单例
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";

    /**
     * cookie
     */
    public CookieJarManager mCookieJarManager;
    /**
     * 网络过滤码 --备用
     */
    private final FilterManager<Integer> mErrorCodeFilter = new FilterManager<>();
    /**
     * context
     */
    private Context mApplicationContext;
    /**
     * 通用headers
     */
    HashMap<String, String> mCommonHeaders;
    /**
     * 通用params
     */
    HashMap<String, String> mCommonParmas;
    /**
     * 动态头部及参数拦截监听器
     */
    private DynamicHeaderInterceptor mDynamicHeaderInterceptor;

    private NetworkParser mNetworkParser = new DefaultNetworkParser();

    private String mBaseurl;

    /**
     * 根据名称获取cookie
     *
     * @param name
     * @return
     */
    public Cookie getCookieByName(String name) {
        List<Cookie> cookies = mCookieJarManager.getCookies();
        if (TextUtils.isEmpty(name) || cookies == null || cookies.isEmpty()) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.name().equals(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 回调接口
     *
     * @param <T>
     */
    public interface NetworkListener<T> {
        void onSuccess(T result, String showMsg);

        void onFailure(NetworkException<T> exception, String showMsg);
    }

    public interface NetworkParseListener<T> extends NetworkListener<T>, NetworkParser {

    }

    public interface NetworkProgressListener<T> extends NetworkListener<T> {
        void onProgress(float progress, long done, long total);
    }

    public interface NetworkWithCacheListener<T> extends NetworkListener<T> {
        void onCacheSuccess(T result, String showMsg);

    }

    private static volatile NetworkManager sInstance;

    public static NetworkManager getInstance() {
        if (sInstance == null) {
            synchronized (NetworkManager.class) {
                if (sInstance == null) {
                    sInstance = new NetworkManager();
                }
            }
        }

        return sInstance;
    }

    public void clearRequest() {
        ApiClient.cancel(mApplicationContext);
    }

    // 请求前参数统一处理，追加渠道名称用户手机号之类
    ParamsInterceptor mParamsInterceptor = new ParamsInterceptor() {
        @Override
        public Map checkParams(Map params) {
            // 追加统一参数
            if (null != mCommonParmas) {
                for (Map.Entry<String, String> entry : mCommonParmas.entrySet()) {
                    params.put(entry.getKey(), entry.getValue());
                }
            }
            return params;
        }
    };
    // 请求前headers统一处理
    HeadersInterceptor mHeadersInterceptor = new HeadersInterceptor() {
        @Override
        public Map checkHeaders(Map headers) {
            // 追加统一header
            if (null != mCommonHeaders) {
                for (Map.Entry<String, String> entry : mCommonHeaders.entrySet()) {
                    headers.put(entry.getKey(), entry.getValue());
                }
            }
            return headers;
        }
    };

    public void setCommonHeaders(HashMap<String, String> headers) {
        mCommonHeaders = headers;
    }

    public void addCommonHeaders(HashMap<String, String> headers) {
        if (null != mCommonHeaders) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                mCommonHeaders.put(entry.getKey(), entry.getValue());
            }
        } else {
            mCommonHeaders = headers;
        }
    }

    public void setCommonParams(HashMap<String, String> params) {
        mCommonParmas = params;
    }


    public void setDynamicHeaderInterceptor(DynamicHeaderInterceptor dynamicHeaderInterceptor) {
        mDynamicHeaderInterceptor = dynamicHeaderInterceptor;
    }

    public void setNetworkParser(NetworkParser networkParser) {
        mNetworkParser = networkParser;
    }

    /**
     * 最好在application里初始化
     *
     * @param applicationContext
     * @param baseurl
     */
    public void initialize(Context applicationContext, String baseurl, long connectTimeout, int codeRequestSuccess, Filter<Integer>... codeFilters) {
        mApplicationContext = applicationContext;
        mBaseurl = baseurl;
        NetworkConfig.CODE_REQUEST_SUCCESS = codeRequestSuccess;
        ApiClient.SingletonBuilder apiClient = new ApiClient.SingletonBuilder();
        apiClient.context(mApplicationContext)
                .baseUrl(baseurl)// URL请求前缀地址。如果不想传可以不传，每次接口请求时传入完整url baseurl即不生效
                .cachePath(MFileCache.NETWORK_CACHE)// 接口数据缓存路径，不传默认系统缓存路径
                .cookieSaveingMode(ApiClient.CookieSaveingMode.SHAREDPREFERENCES)
                .paramsInterceptor(mParamsInterceptor)
                .headersInterceptor(mHeadersInterceptor)
                .timeout(connectTimeout)
//                .cookiePath(MFileCache.CACHE_PATH)//cookies保存路径，不传默认系统缓存路径。cookie保存文件名固定为/cookie.txt。传路径时末尾不要带 "/"
                .build();
        // 添加调试工具
        // TODO: 17/3/31 修改网络库
        // apiClient.getClient().networkInterceptors().add(new StethoInterceptor());
        mCookieJarManager = apiClient.getCookieJarManagerInstance();// 或得到cookie全局管理manager

        if (codeFilters != null && codeFilters.length > 0) {
            for (int i = 0; i < codeFilters.length; ++i)
                mErrorCodeFilter.registerFilter(codeFilters[i]);
        }
    }

    /**
     * 取消请求
     *
     * @param tag
     */
    public void cancel(Object tag) {
        ApiClient.cancel(tag);
    }

    /**
     * get 不带参数
     *
     * @param <T>
     */
    public <T> void get(Object tag, String url, final NetworkListener<T> listener) {
        get(tag, url, null, listener);
    }

    /**
     * get 带参数
     *
     * @param <T>
     */
    public <T> void get(Object tag, String url, final Map<String, String> urlParam,
                        final NetworkListener<T> listener) {
        get(tag, getUrl(url), urlParam, null, listener, false);
    }

    /**
     * get 带参数带缓存
     *
     * @param <T>
     */
    public <T> void getWithCache(Object tag, String url, final Map<String, String> urlParam,
                                 final NetworkWithCacheListener<T> listener) {
        getWithCache(tag, getUrl(url), urlParam, null, listener);
    }

    /**
     * get 带参数带缓存
     *
     * @param <T>
     */
    public <T> void getWithCache(Object tag, String url, final Map<String, String> urlParam, final Map<String, String> headers,
                                 final NetworkWithCacheListener<T> listener) {
        String paramUrl = urlGetParam(urlParam, url);
        String cacheStr = MCacheManager.DEFAULT.getString(paramUrl);
        //首先返回缓存
        if (!TextUtils.isEmpty(cacheStr)) {
            Type type = ParameterizedTypeUtil.getParameterizedType(listener.getClass(), NetworkListener.class, 0);
            T model = MJsonUtils.parseString(cacheStr, type);
            listener.onCacheSuccess(model, "success");
        }
        get(tag, getUrl(url), urlParam, headers, listener, 0, false, false, true);
    }

    /**
     * get 带参数带缓存时间
     *
     * @param <T>
     */
    public <T> void getWithCacheTime(Object tag, String url, final Map<String, String> urlParam,
                                     final NetworkListener<T> listener, long cacheTime) {
        get(tag, getUrl(url), urlParam, null, listener, cacheTime, false, false, true);
    }

    /**
     * get 带参数 带头信息
     *
     * @param <T>
     */
    public <T> void get(Object tag, String url, final Map<String, String> urlParam, Map<String, String> headers,
                        final NetworkListener<T> listener, boolean callbackOnBackground) {
        get(tag, getUrl(url), urlParam, headers, listener, 0, false, callbackOnBackground, false);
    }

    private String getUrl(String url) {
        if (!url.startsWith("http")) {
            return mBaseurl + url;
        }
        return url;
    }

    /**
     * get 带参数 带头信息 带缓存时间 带缓存模式
     *
     * @param <T>
     */
    public <T> void get(Object tag, String url, final Map<String, String> urlParam, Map<String, String> headers,
                        final NetworkListener<T> listener, long cacheTime, final boolean isUpdateCache,
                        final boolean callbackOnBackground, final boolean isLocalCache) {
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_GET, headers, getUrl(url), urlParam);
        }

        final long startTime = System.currentTimeMillis();
        final String urls = urlGetParam(urlParam, url);
//        MLogWriter.d(TAG, "/***********************网络请求 start 请求Get Url: " + urls);

//        final String mxcid = Guid.get();
        new ApiClient.Builder(urls).CacheTime(cacheTime / 1000).isUpdateCache(isUpdateCache)
                // .Headers(getHeaders(getUrl(url), urlParam, mxcid, !TextUtils.isEmpty(path_refer) ? path_refer : ""))
                .Headers(headers != null ? headers : new HashMap<String, String>()).Tag(tag)// 需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(Object o) {
                        Response response = (Response) o;
                        String model = onResponse(response, listener, callbackOnBackground, urls, isLocalCache);
                        if(BuildConfig.DEBUG) {
                            MLogWriter.w(TAG, "/***********************网络请求 end success 请求Get Url: "
                                    + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime) + "\nonResponse => " + model);
                        }
                    }
                }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                if(BuildConfig.DEBUG) {
                    MLogWriter.d(TAG, "onErrorResponse:" + values[1].toString());
                    MLogWriter.d(TAG, "/***********************网络请求 end fail 请求Get Url: " + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime));
                }
                NetworkException exception = new NetworkException(new Exception(values[1].toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
//                reportErrorLog(urls, mxcid, values[1].toString());
            }
        }).get();
    }

    /**
     * 同步 get 请求
     */
    public <T> T syncGet(String url, Map<String, String> urlParam, Map<String, String> headers, Class<T> resultClazz) {
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_GET, headers, getUrl(url), urlParam);
        }
        String urls = urlGetParam(urlParam, url);
        Call<ResponseBody> call = ApiClient.getService()
                .get(ApiClient.checkHeaders(headers), urls,
                        ApiClient.checkParams(null));
        try {
            Response<ResponseBody> response = call.execute();
            return parseResponse(response, resultClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T parseResponse(Response<ResponseBody> response,
                                Class<T> resultClazz) throws Exception {
        if (response.code() != 200) {
            if (null != resultClazz && MSyncBaseBean.class.isAssignableFrom(resultClazz)) {
                T t = resultClazz.newInstance();
                MSyncBaseBean baseBean = (MSyncBaseBean) t;
                baseBean.httpCode = response.code();
                baseBean.httpMessage = response.message();
                return t;
            }
            return null;
        }
        String result = praseNetWork(response);
//        Type resultType = resultClazz != null ? ParameterizedTypeUtil.getParameterizedType(resultClazz, MSyncBaseBean.class, 0) : null;

        MParserBean<T> parserBean = mNetworkParser.onParse(result, resultClazz);
        if (parserBean.isSucceed) {
            if (parserBean.data instanceof MSyncBaseBean) {
                MSyncBaseBean baseBean = (MSyncBaseBean) parserBean.data;
                baseBean.code = parserBean.code;
                baseBean.msg = TextUtils.isEmpty(parserBean.showMsg) ? parserBean.msg : parserBean.showMsg;
                baseBean.httpCode = response.code();
                baseBean.httpMessage = response.message();
            }
            return parserBean.data;
        } else {
            if (null != resultClazz && MSyncBaseBean.class.isAssignableFrom(resultClazz)) {
                T t = resultClazz.newInstance();
                MSyncBaseBean baseBean = (MSyncBaseBean) t;
                baseBean.code = parserBean.code;
                baseBean.msg = TextUtils.isEmpty(parserBean.showMsg) ? parserBean.msg : parserBean.showMsg;
                baseBean.httpCode = response.code();
                baseBean.httpMessage = response.message();
                return t;
            }
            return null;
        }
        
        
        /*JsonObject jsonObject = MJsonUtils.getJsonParserInstance().parse(result).getAsJsonObject();
        if (jsonObject.get("code") == null) {
            return null;
        }
        int code = jsonObject.get("code").getAsInt();
        String msg;
        if (jsonObject.has("showMsg")) {
            msg = jsonObject.get("showMsg").getAsString();
        } else {
            msg = jsonObject.get("msg").getAsString();
        }
        JsonElement data = jsonObject.get("data");
        if (code != NetworkConfig.CODE_REQUEST_SUCCESS
                || data == null || data.isJsonNull()) {
            if (MSyncBaseBean.class.isAssignableFrom(resultClazz)) {
                T t = resultClazz.newInstance();
                MSyncBaseBean baseBean = (MSyncBaseBean) t;
                baseBean.code = code;
                baseBean.msg = msg;
                return t;
            }

            return null;
        }
        T t = MJsonUtils.parseJsonObject(data, resultClazz);
        if (t instanceof MSyncBaseBean) {
            MSyncBaseBean baseBean = (MSyncBaseBean) t;
            baseBean.code = code;
            baseBean.msg = msg;
        }
        return t;*/
    }

    /**
     * post 不带参数
     *
     * @param <T>
     */
    public <T> void post(Object tag, String url, final NetworkListener<T> listener) {
        post(tag, url, null, listener);
    }

    /**
     * post 带参数
     */
    public <T> void post(Object tag, String url, final Map<String, String> params, final NetworkListener<T> listener) {
        post(tag, url, params, null, listener, false);
    }

    /**
     * post 带参数 带参数 带头信息
     */
    public <T> void post(Object tag, final String url, final Map<String, String> params, Map<String, String> headers,
                         final NetworkListener<T> listener, final boolean callbackOnBackground) {
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
        }

        final long startTime = System.currentTimeMillis();
        final String urls = urlGetParam(params, url);
//        MLogWriter.d(TAG, "/***********************网络请求 start 请求Post Url:" + urls);
        new ApiClient.Builder(url)
                .Headers(headers != null ? headers : new HashMap<String, String>())
                .Params(params != null ? params : new HashMap<String, String>())
                .Tag(tag)// 需要取消请求的tag
                .Success(new Success() {
                    @Override
                    public void Success(Object o) {
                        Response response = (Response) o;
                        String model = onResponse(response, listener, callbackOnBackground);
                        if(BuildConfig.DEBUG) {
                            MLogWriter.w(TAG, "/***********************网络请求 end success 请求Post Url: "
                                    + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime) + "\nonResponse => " + model);
                        }
                    }
                }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                if(BuildConfig.DEBUG) {
                    MLogWriter.d(TAG, "onErrorResponse:" + values[1].toString());
                    MLogWriter.d(TAG, "/***********************网络请求 end fail 请求Post Url: " + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime));
                }
                NetworkException exception = new NetworkException(new Exception(values[1].toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
            }
        }).post();
    }

    /**
     * 同步 post 请求
     */
    public <T> T syncPost(String url, Map<String, String> params, Map<String, String> headers, Class<T> resultClazz) {
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
        }
        Call<ResponseBody> call = ApiClient.getService()
                .post(ApiClient.checkHeaders(headers), url,
                        ApiClient.checkParams(params));
        try {
            Response<ResponseBody> response = call.execute();
            return parseResponse(response, resultClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * json
     *
     * @param tag
     * @param url
     * @param params
     * @param headers
     * @param listener
     * @param <T>
     */
    public <T> void postJson(Object tag, final String url, Object payload, final Map<String, String> params, Map<String, String> headers,
                             final NetworkListener<T> listener) {
        postJson(tag, url, payload, params, headers, listener, false);
    }

    public <T> void postJson(Object tag, final String url, Object payload, final Map<String, String> params, Map<String, String> headers,
                             final NetworkListener<T> listener, final boolean callbackOnBackground) {
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
        }

        /**    默认开启gzip压缩  */
        if (null == headers) {
            headers = new HashMap<>();
        }
        headers.put("Content-Encoding", "gzip");

        final long startTime = System.currentTimeMillis();
        final String urls = urlGetParam(params, url);
//        MLogWriter.d(TAG, "/***********************网络请求 start 请求PostJson Url:" + urls);

        new ApiClient.Builder(url)
                .Headers(headers != null ? headers : new HashMap<String, String>())
                .Params(params != null ? params : new HashMap<String, String>())
                .Tag(tag)// 需要取消请求的tag
                .json(MJsonUtils.toString(payload))
                .Success(new Success() {
                    @Override
                    public void Success(Object o) {
                        Response response = (Response) o;
                        String model = onResponse(response, listener, callbackOnBackground);
                        if(BuildConfig.DEBUG) {
                            MLogWriter.w(TAG, "/***********************网络请求 end success 请求PostJson Url: "
                                    + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime) + "\nonResponse => " + model);
                        }
                    }
                }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                if(BuildConfig.DEBUG) {
                    MLogWriter.d(TAG, "onErrorResponse:" + values[1].toString());
                    MLogWriter.d(TAG, "/***********************网络请求 end fail 请求PostJson Url: " + urls + " ********** totalTime=" + (System.currentTimeMillis() - startTime));
                }
                NetworkException exception = new NetworkException(new Exception(values[1].toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
//                reportErrorLog(url, mxcid, values[1].toString());
            }
        }).postJson();
    }


    /**
     * 下载文件---不带参数
     */
    public void downloadFile(Object tag, String url, String dirOrFile, final NetworkProgressListener<String> listener) {
        downloadFile(tag, url, null, dirOrFile, listener, false);
    }

    /**
     * 下载文件 带参数
     */
    public void downloadFile(Object tag, String url, final Map<String, String> urlParam, final String dirOrFile,
                             final NetworkProgressListener<String> listener, final boolean callbackOnBackground) {
        if(BuildConfig.DEBUG) {
            MLogWriter.d(TAG, "/***********************网络请求 start************************/");
        }
        Map<String, String> headers = new HashMap<>();
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_GET, headers, getUrl(url), urlParam);
        }

        url = urlGetParam(urlParam, url);
        if(BuildConfig.DEBUG) {
            MLogWriter.i(TAG, "请求GET Url--" + url);
        }
        new ApiClient.Builder(url).Headers(headers).savePath(dirOrFile).CacheTime(0).Tag(tag).Progress(new Progress() {
            @Override
            public void progress(final float progress, final long done, final long total) {
                if (callbackOnBackground) {
                    listener.onProgress(progress, done, total);
                    return;
                }
                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                    @Override
                    public void runInMain() {
                        listener.onProgress(progress, done, total);
                    }
                });
            }
        }).Success(new Success() {
            @Override
            public void Success(Object o) {
                // 返回path
                if(BuildConfig.DEBUG) {
                    MLogWriter.d(TAG, "/***********************网络请求 end success ************************/");
                }
                if (callbackOnBackground) {
                    listener.onSuccess(dirOrFile, "");
                    return;
                }
                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                    @Override
                    public void runInMain() {
                        listener.onSuccess(dirOrFile, "");
                    }
                });
            }
        }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                if(BuildConfig.DEBUG) {
                    MLogWriter.d(TAG, "/***********************网络请求 end fail************************/");
                }
                NetworkException exception = new NetworkException(new Exception(values.toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
            }
        }).download();
    }

    public <T> T syncUploadFile(String url, Map<String, String> params, String fileKey, File file, Class<T> resultClazz) {
        Map<String, File> fileMap = new HashMap<>(1);
        fileMap.put(fileKey, file);
        Map<String, String> headers = new HashMap<>();
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
        }
        Map<String, Object> objects = new HashMap<>();
        if (null != params && params.size() > 0) {
            objects.putAll(params);
        }
        objects.putAll(fileMap);

        Call<ResponseBody> call = ApiClient.getService()
                .upload(ApiClient.checkHeaders(headers), url, requestBodys(objects));
        try {
            Response<ResponseBody> response = call.execute();
            return parseResponse(response, resultClazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, RequestBody> requestBodys(Map<String, Object> requestBodys) {
        Map<String, RequestBody> bodyMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : requestBodys.entrySet()) {
            this.requestBodys(entry.getKey(), entry.getValue(), bodyMap);
        }

        return bodyMap;
    }

    private void requestBodys(String key, Object o, Map<String, RequestBody> requestBodys) {
        RequestBody body;
        if (o instanceof String) {
            body = RequestBody.create(MediaType.parse("text/plain"), (String) o);
            requestBodys.put(key, body);
        } else if (o instanceof File) {
            body = RequestBody.create(MediaType.parse("multipart/form-data"), (File) o);
            requestBodys.put(key + "\"; filename=\"" + ((File) o).getName() + "", body);
        }
    }

    public <T> void uploadFile(Object tag, String url, Map<String, String> params, String fileKey, File file,
                               NetworkProgressListener<T> listener) {
        uploadFile(tag, url, params, fileKey, file, listener, false);
    }

    public <T> void uploadFile(Object tag, String url, Map<String, String> params, String fileKey, final File file,
                               final NetworkProgressListener<T> listener, final boolean callbackOnBackground) {
        Map<String, File> files = new HashMap<>(1);
        files.put(fileKey, file);
        uploadFiles(tag, url, params, files, listener, callbackOnBackground);
//        MLogWriter.d(TAG, "/***********************网络请求 start************************/");
//        Map<String, String> headers = new HashMap<>();
//        if (null != mDynamicHeaderInterceptor) {
//            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
//        }
//        MLogWriter.i(TAG, "请求GET Url--" + url);
//        Map<String, Object> objects = new HashMap<>();
//        if (null != params && params.size() > 0) {
//            objects.putAll(params);
//        }
//        objects.put(fileKey, file);
//        new ApiClient.Builder(url).Headers(headers).requestBodys(objects)
//                // .requestBodys(Map<String, Object> requestBodys)//多文件可带参数,参数key指定,
//                // 文件需要传入File(切记)，文件的key没要求，请求时会在key之后拼接文件名
//                .CacheTime(0).Tag(tag).Progress(new Progress() {
//            @Override
//            public void progress(final float progress, long done, long total) {
//                if (callbackOnBackground) {
//                    listener.onProgress(progress, done, total);
//                    return;
//                }
//                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
//                    @Override
//                    public void runInMain() {
//                        listener.onProgress(progress, done, total);
//                    }
//                });
//            }
//        }).Success(new Success() {
//            @Override
//            public void Success(Object o) {
//                // 返回path
//                MLogWriter.d(TAG, "/***********************网络请求 end success ************************/");
//                Response response = (Response) o;
//                onResponse(response, listener, callbackOnBackground);
//            }
//        }).Fail(new Fail() {
//            @Override
//            public void Fail(Object... values) {
//                MLogWriter.d(TAG, "/***********************网络请求 end fail************************/");
//                NetworkException exception = new NetworkException(new Exception(values.toString()));
//                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
//                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
//                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
//                }
//                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
//            }
//        }).upload();
    }

    public <T> void uploadFiles(Object tag, String url, Map<String, String> params, final Map<String, File> files,
                                final NetworkProgressListener<T> listener, final boolean callbackOnBackground) {
        MLogWriter.d(TAG, "/***********************网络请求 start************************/");
        Map<String, String> headers = new HashMap<>();
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), params);
        }
        MLogWriter.i(TAG, "请求GET Url--" + url);
        Map<String, Object> objects = new HashMap<>();
        if (null != params && params.size() > 0) {
            objects.putAll(params);
        }
        objects.putAll(files);
        new ApiClient.Builder(url).Headers(headers)
                .requestBodys(objects)//多文件可带参数,参数key指定,
                // 文件需要传入File(切记)，文件的key没要求，请求时会在key之后拼接文件名
                .CacheTime(0).Tag(tag).Progress(new Progress() {
            @Override
            public void progress(final float progress, long done, long total) {
                if (callbackOnBackground) {
                    listener.onProgress(progress, done, total);
                    return;
                }
                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                    @Override
                    public void runInMain() {
                        listener.onProgress(progress, done, total);
                    }
                });
            }
        }).Success(new Success() {
            @Override
            public void Success(Object o) {
                // 返回path
                MLogWriter.d(TAG, "/***********************网络请求 end success ************************/");
                Response response = (Response) o;
                onResponse(response, listener, callbackOnBackground);
            }
        }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                MLogWriter.d(TAG, "/***********************网络请求 end fail************************/");
                NetworkException exception = new NetworkException(new Exception(values.toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                NetworkManager.this.onResponseFailure(exception, exception.message, listener, callbackOnBackground);
            }
        }).upload();
    }

    /**
     * 上传文件 参数全部都在body 里面，目前仅用于用户头像上传
     */
    public <T> void uploadFileUseBody(Object tag, String url, Map<String, Object> body, final NetworkProgressListener<T> listener) {
        Map<String, String> headers = new HashMap<>();
        if (null != mDynamicHeaderInterceptor) {
            headers = mDynamicHeaderInterceptor.checkHeaders(DynamicHeaderInterceptor.REQUEST_TYPE_POST, headers, getUrl(url), null);
        }
        new ApiClient.Builder(url).Headers(headers).requestBodys(body) // body 多参数上传
                // .requestBodys(Map<String, Object> requestBodys)//多文件可带参数,参数key指定,
                // 文件需要传入File(切记)，文件的key没要求，请求时会在key之后拼接文件名
                .CacheTime(0).Tag(tag).Progress(new Progress() {
            @Override
            public void progress(final float progress, long done, long total) {

                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                    @Override
                    public void runInMain() {
                        listener.onProgress(progress, done, total);
                    }
                });
            }
        }).Success(new Success() {
            @Override
            public void Success(Object o) {
                // 返回path
                MLogWriter.d(TAG, "/***********************网络请求 end success ************************/");
                Response response = (Response) o;
                onResponse(response, listener, false);
            }
        }).Fail(new Fail() {
            @Override
            public void Fail(Object... values) {
                MLogWriter.d(TAG, "/***********************网络请求 end fail************************/");
                NetworkException exception = new NetworkException(new Exception(values.toString()));
                if (!MNetworkUtils.isNetConnected(mApplicationContext)) {
                    exception.code = (int) NetworkConst.ERROR_CODE_NETWORK_FAIL;
                    exception.message = mApplicationContext.getString(R.string.TX_ERROR_CODE_NETWORK_FAIL);
                }
                onResponseFailure(exception, exception.message, listener, false);
            }
        }).upload();
    }

    /**
     * get 处理url参数
     *
     * @param urlParam
     * @param url
     */
    public static String urlGetParam(final Map<String, String> urlParam, final String url) {
        if (null == urlParam || urlParam.size() == 0 || TextUtils.isEmpty(url)) {
            return url;
        }

        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append(url);
        if (!url.endsWith("?")) {
            urlBuffer.append("?");
        }

        int index = 0;
        try {
            for (Map.Entry<String, String> entry : urlParam.entrySet()) {
                urlBuffer.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                urlBuffer.append("=");
                String value = null != entry.getValue() ? entry.getValue() : "";
                urlBuffer.append(URLEncoder.encode(value, "UTF-8"));
                if (++index >= urlParam.size()) {
                    break;
                }

                urlBuffer.append("&");
            }

            return urlBuffer.toString();
        } catch (Exception e) {
            MLogWriter.e(TAG, e.getLocalizedMessage());
        }
        return urlBuffer.toString();
    }

    /**
     * 获取cookie
     *
     * @return
     */
    public String getCookieStr() {
        List<Cookie> cookies = mCookieJarManager.getCookies();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
            if (i > 0) {
                stringBuffer.append(";");
            }
            stringBuffer.append(cookies.get(i).toString());
        }
        return stringBuffer.toString();
    }

    /**
     * 清除网络缓存
     */
    public void clearCache() {
        ApiClient.SingletonBuilder.clearCache();
    }

    private static String praseNetWork(Response response) {
        Headers headers = response.headers();
        Set<String> set = headers.names();

        //服务器时间
        if (set.contains("Date")) {
            String Date = headers.get("Date");
            if (Date != null && !"".equals(Date.trim())) {
                try {
                    // 获取到gmt时间
                    final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
                    TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
                    MTimeUtils.setLastServerTime(sdf.parse(Date).getTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        if (set.contains("Content-Encoding")) {
            String sessionId = headers.get("Content-Encoding");
            if (sessionId != null && sessionId.length() > 0) {
                byte[] bytes = null;
                try {
                    bytes = ((ResponseBody) response.body()).bytes();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if ("gzip".equalsIgnoreCase(sessionId.trim()) && bytes != null && bytes.length > 0) {

                    StringBuilder output = new StringBuilder();
                    try {
                        GZIPInputStream gStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                        InputStreamReader reader = new InputStreamReader(gStream);
                        BufferedReader in = new BufferedReader(reader, 16384);

                        String rend;
                        while ((rend = in.readLine()) != null) {
                            if (!TextUtils.isEmpty(rend)) {
                                output.append(rend).append("\n");
                            }
                        }
                        reader.close();
                        in.close();
                        gStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return output.toString();
                }
            }
        }
        try {
            return ((ResponseBody) response.body()).string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 统一处理失败返回
     *
     * @param exception
     * @param showMsg
     * @param listener
     * @param callbackOnBackground
     * @param <T>
     */
    protected <T> void onResponseFailure(final NetworkException exception, final String showMsg,
                                         final NetworkListener<T> listener, boolean callbackOnBackground) {
        if (callbackOnBackground) {
            if (null != listener) {
                listener.onFailure(exception, showMsg);
            }
            return;
        }

        DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
            @Override
            public void runInMain() {
                if (null != listener) {
                    listener.onFailure(exception, showMsg);
                }
            }
        });
    }

    //兼容旧的数据结构
//    private String compatOldResponse(String data) {
//        return "{\"code\": 1,\"showMsg\": \"\",\"msg\": \"成功\",\"data\":" + data + "}";
//    }

    protected <T> String onResponse(Response response, final NetworkListener<T> listener,
                                  boolean callbackOnBackground) {
        return onResponse(response, listener, callbackOnBackground, "", false);
    }

    /**
     * 统一处理成功返回
     *
     * @param response
     * @param listener
     * @param callbackOnBackground
     * @param <T>
     */
    protected <T> String onResponse(Response response, final NetworkListener<T> listener,
                                  boolean callbackOnBackground, String url, boolean isLocalCache) {

        if (!response.isSuccessful()) {
            final NetworkException exception = new NetworkException(response.code(), response.message());
            NetworkManager.this.onResponseFailure(exception, "", listener, callbackOnBackground);
            if(BuildConfig.DEBUG) {
                MLogWriter.d(TAG, "response is not success ==> ");
            }
            return null;
        }

        String result = praseNetWork(response);
        Type resultType = listener != null ? ParameterizedTypeUtil.getParameterizedType(listener.getClass(), NetworkListener.class, 0) : null;

        //状态成功且返回为空，为统计接口.
        if (TextUtils.isEmpty(result)) {
            if (callbackOnBackground) {
                if (null != listener) {
                    listener.onSuccess(null, "success");
                }
            } else {
                DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                    @Override
                    public void runInMain() {
                        if (null != listener) {
                            listener.onSuccess(null, "success");
                        }
                    }
                });
            }
            return null;
        }

        try {
            NetworkParser parser = mNetworkParser;
            if (null != listener && listener instanceof NetworkParseListener) {
                parser = (NetworkParseListener) listener;
            }

            MParserBean<T> mParserBean = parser.onParse(result, resultType);
            if (mParserBean.isSucceed) {
                //本地缓存
                if (isLocalCache && !TextUtils.isEmpty(url) && null != mParserBean.data) {
                    MCacheManager.DEFAULT.put(url, MJsonUtils.toString(mParserBean.data));
                }
                //回调
                if (callbackOnBackground) {
                    if (null != listener) {
                        listener.onSuccess(mParserBean.data, mParserBean.showMsg);
                    }
                } else {
                    DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                        @Override
                        public void runInMain() {
                            if (null != listener) {
                                listener.onSuccess(mParserBean.data, mParserBean.showMsg);
                            }
                        }
                    });
                }
            } else {
                final NetworkException exception;
                if (mErrorCodeFilter.sendNext(mParserBean.code, mParserBean.msg)) {
                    exception = new NetworkException(NetworkConfig.CODE_REQUEST_Response_interrupt, mParserBean.msg);
                } else {
                    exception = new NetworkException(mParserBean.code, mParserBean.msg);
                }
                exception.data = mParserBean.data;
                onResponseFailure(exception, mParserBean.showMsg, listener, callbackOnBackground);
            }

            /*//兼容旧的数据结构([{},{}])
            if (!TextUtils.isEmpty(result) && result.startsWith("[")) {
                result = compatOldResponse(result);
            }
            JsonObject jsonObject = MJsonUtils.getJsonParserInstance().parse(result).getAsJsonObject();
            //兼容旧的数据结构
            if (!jsonObject.has("code") || !jsonObject.has("data")) {
                result = compatOldResponse(result);
                jsonObject = MJsonUtils.getJsonParserInstance().parse(result).getAsJsonObject();
            }

            int code = jsonObject.has("code") ? jsonObject.get("code").getAsInt() : -1;
            String msg = jsonObject.has("msg") ? jsonObject.get("msg").getAsString() : "";
            final String showMsg = jsonObject.has("showMsg") ? jsonObject.get("showMsg").getAsString() : "";

            if (code != NetworkConfig.CODE_REQUEST_SUCCESS) {
                MLogWriter.d(TAG, "response is not success ==> ");
                final NetworkException exception;
                if (mErrorCodeFilter.sendNext(code, msg)) {
                    exception = new NetworkException(NetworkConfig.CODE_REQUEST_Response_interrupt, msg);
                } else {
                    exception = new NetworkException(code, msg);
                }
                try {
                    JsonElement data = jsonObject.get("data");
                    if (!data.isJsonNull()) {
                        final T model;
                        if (null == resultType || resultType == String.class) {
                            if (data.isJsonPrimitive()) {
                                model = (T) data.getAsString();
                            } else {
                                model = (T) data.toString();
                            }
                        } else {
                            if (!data.isJsonObject() && !data.isJsonArray()) {
                                throw new JsonParseException(
                                        "expect Model. but result is not JsonObject or JsonArray!");
                            }
                            model = MJsonUtils.parseString(data.toString(), resultType);
                        }
                        exception.data = model;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NetworkManager.this.onResponseFailure(exception, showMsg, listener, callbackOnBackground);
            } else {
                try {
                    JsonElement data = jsonObject.get("data");
                    MLogWriter.d(TAG, "data = " + data);
                    if (data.isJsonNull()) {
                        if (callbackOnBackground) {
                            if (null != listener) {
                                listener.onSuccess(null, showMsg);
                            }
                        } else {
                            DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                                @Override
                                public void runInMain() {
                                    if (null != listener) {
                                        listener.onSuccess(null, showMsg);
                                    }
                                }
                            });
                        }
                        return;
                    }

                    final T model;
                    if (null == resultType || resultType == String.class) {
                        if (data.isJsonPrimitive()) {
                            model = (T) data.getAsString();
                        } else {
                            model = (T) data.toString();
                        }
                    } else {
                        if (!data.isJsonObject() && !data.isJsonArray()) {
                            throw new JsonParseException("expect Model. but result is not JsonObject or JsonArray!");
                        }

                        model = MJsonUtils.parseString(data.toString(), resultType);
                    }
                    //本地缓存
                    if (isLocalCache && !TextUtils.isEmpty(url)) {
                        MCacheManager.DEFAULT.put(url, MJsonUtils.toString(model));
                    }
                    if (callbackOnBackground) {
                        if (null != listener) {
                            listener.onSuccess(model, showMsg);
                        }
                    } else {
                        DispatchAsync.dispatchAsync(new SimpleDispatchRunnable() {
                            @Override
                            public void runInMain() {
                                if (null != listener) {
                                    listener.onSuccess(model, showMsg);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    MLogWriter.e(TAG, "Exception = " + e.getMessage());
                    final NetworkException exception = new NetworkException(e);
                    NetworkManager.this.onResponseFailure(exception, showMsg, listener, callbackOnBackground);
                }
            }
*/
        } catch (Exception e) {
            e.printStackTrace();
            if(BuildConfig.DEBUG) {
                MLogWriter.e(TAG, "Exception = " + e.getMessage());
            }
            final NetworkException exception = new NetworkException(e);
            exception.informalityJson = result;
            NetworkManager.this.onResponseFailure(exception, "", listener, callbackOnBackground);
        }

        return result;
    }

    private static abstract class SimpleDispatchRunnable implements DispatchAsync.DispatchRunnable {

        @Override
        public void runInBackground() {
        }
    }


}
