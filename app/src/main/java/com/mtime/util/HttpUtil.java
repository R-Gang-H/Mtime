package com.mtime.util;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kotlin.android.retrofit.cookie.CookieManager;
import com.mtime.frame.App;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mtime.base.SessionHelper;
import com.mtime.base.network.BaseApi;
import com.mtime.base.network.NetworkException;
import com.mtime.base.network.NetworkManager;
import com.mtime.bussiness.ticket.movie.bean.RechargeBean;
import com.mtime.common.utils.LogWriter;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.network.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * 网络请求工具
 *
 * @author Mtime-le 15-3-30 上午11:31
 */
@Deprecated
public class HttpUtil {
    /**
     * get 1.不带参数 不带缓存时间 示例: HttpUtil.get(context,
     * ConstantUrl.GET_RECOMMEND_TOPLISTFIXED, FindTopFixedListBean.class,
     * topMovieFixedCallback); 2.带参数 不带缓存时间 示例:
     * HttpUtil.get(VideoListActivity.this, ConstantUrl.GET_VIDEO_LIST,
     * urlParam, VideoListBean.class, getVideoListCallback); 3.带参数 带缓存时间 示例:
     * HttpUtil.getVolleyString(context, ConstantUrl.GET_RECOMMEND_HOME, null,
     * RecommendHomeBean.class, homeCallback, 180000); 4.带参数 带缓存时间 带typeToken
     * 示例: HttpUtil.getVolleyString(context, ConstantUrl.GET_RECOMMEND_REVIEW,
     * null, ReviewBean.class, reviewCallback, 180000, typeToken); 5.带参数 带缓存时间
     * 带typeToken 带postUI 示例: 和其他get请求相同
     * HttpUtil.getVolleyString(MapViewActivity.this,
     * ConstantUrl.GET_CELLCHINA_LOCATIONS, urlParam, LocationRawBean.class,
     * callback); 6.带参数 带缓存时间 带typeToken 带postUI 带setHandler(null),
     * 用在下载图片和支付成功-保存图片到相册 // TODO X 7.全带 带connectTimeout 示例:
     * //readTimeout应该不用设置 HttpUtil.getVolleyString(this,
     * ConstantUrl.BASE_CITYDATA, urlParamCity, BaseCityBean.class,
     * baseCityCallback, 1209600000, null, 300000);
     * <p/>
     * post 1.不带参数 示例: HttpUtil.postVolleyString(this,
     * ConstantUrl.POST_MALL_ORDER_TAG, MallOrderTagBean.class, new
     * RequestCallback() 2.带参数 返回BaseRequest 示例: // TODO X未返回BaseRequest
     * Map<String, String> businessParam = new HashMap<String, String>();
     * businessParam.put("keyword", str); businessParam.put("locationId",
     * Constant.locationCity.getCityId());
     * HttpUtil.postVolleyString(SearchActivity.this,
     * ConstantUrl.GET_SEARCH_RESULT_NEW, businessParam, SearchSuggestionNewBean.class,
     * searchSuggestNewCallback); 3.带参数 返回BaseRequest 只有充值时使用
     * 怎么有这种情况RechargeHandler? 考虑重新做一下 // TODO X 4.带参数 带typeToken 示例:
     * Map<String, String> businessParam = new HashMap<String, String>();
     * businessParam.put("cityID", locationId); final Type typeToken = new
     * TypeToken<List<FavoriteCinemaListByCityIDBean>>() { }.getType();
     * HttpUtil.postVolleyString(MovieShowtimeActivity.this,
     * ConstantUrl.POST_FAVORITE_CINEMALIST_BYCITYID, businessParam,
     * FavoriteCinemaListByCityIDBean.class, favoritesCallback, typeToken);
     * 5.带参数 带typeToken 带链接超时 示例:没有地方用到
     */

    private static final String MTNET = "MTNet";

    private static String path_refer;
    
    private static final MApi mApi = new MApi();
    
    static class MApi extends BaseApi {
    
        @Override
        protected String host() {
            return null;
        }
    
        @Override
        public void cancel() {
            super.cancel();
        }
    
        @Override
        public void cancel(Object tag) {
            super.cancel(tag);
        }
    }
    
    public static void setRefer(final String refer) {
        path_refer = refer;
    }

    public static void clearRequest() {
        mApi.cancel();
    }

    /**
     * 取消特定请求
     * @param tag
     */
    public static void cancelRequest(Object tag){
        mApi.cancel(tag);
    }

    /**
     * get 不带参数 不带缓存时间
     *
     * @param <T>
     */
    public static <T> void get(String url, final Class<T> bean, final RequestCallback callback) {
        get(url, null, bean, callback);
    }

    /**
     * get 带参数 不带缓存时间
     *
     * @param <T>
     */
    public static <T> void get(String url, final Map<String, String> urlParam, final Class<T> bean, final RequestCallback callback) {
        get(url, urlParam, bean, callback, 0);
    }

    /**
     * get 带参数 带缓存时间
     *
     * @param <T>
     */
    public static <T> void get(String url, final Map<String, String> urlParam, final Class<T> bean, final RequestCallback callback, long cacheTime) {
        get(url, urlParam, bean, callback, cacheTime, null);
    }

    /**
     * get 带参数 带缓存时间 带typeToken
     *
     * @param <T>
     */
    public static <T> void get(String url, final Map<String, String> urlParam, final Class<T> bean, final RequestCallback callback, long cacheTime,
                               final Type typeToken) {
        get(url, urlParam, bean, callback, cacheTime, typeToken, 0);
    }

    /**
     * get 带参数 带缓存时间 带typeToken 带超时时间
     *
     * @param <T>
     */
    public static <T> void get(String url, final Map<String, String> urlParam, final Class<T> bean, final RequestCallback callback, long cacheTime,
                               final Type typeToken, int connectTimeout) {
        get(url, urlParam, bean, callback, cacheTime, typeToken, connectTimeout, false);
    }

    /**
     * get 带参数 带缓存时间 带typeToken 带超时时间 带缓存模式
     *
     * @param <T>
     */
    public static <T> void get(String url, final Map<String, String> urlParam, final Class<T> bean, final RequestCallback callback, long cacheTime,
                               final Type typeToken, int connectTimeout, final boolean isUpdateCache) {
        LogWriter.i(MTNET, "/***********************网络请求 start************************/");
    
        mApi.get(mApi, url, urlParam, cacheTime, null, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                if(null != callback) {
                    Object responseBeanObject = null;
                    if (typeToken != null) {
                        try {
                            responseBeanObject = new Gson().fromJson(result, typeToken);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String code = getResponseCode(jsonObject);
                            String msg = getResponseMsg(jsonObject);
                            if (code != null && !"".equals(code) && msg != null) {
                                if ("1".equals(code)) {
                                    String data = jsonObject.getString("data");
                                    LogWriter.e("mylog", "data:" + data);
                                    responseBeanObject = new Gson().fromJson(data, bean);
                                } else {
                                    callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
                                    return;
                                }
                            } else {
                                responseBeanObject = new Gson().fromJson(result, bean);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    callback.onSuccess(responseBeanObject);
                }
            }
    
            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                if(null != callback) {
                    callback.onFail(new Exception(showMsg));
                }
            }
        }, false);
        
        
//        final String urls = urlGetParam(urlParam, url);
//        LogWriter.i(MTNET, "请求Get Url:" + url);
//        final String mxcid = Guid.get();
//        new ApiClient.Builder(urls)
//                .CacheTime(cacheTime / 1000)
//                .isUpdateCache(isUpdateCache)
//                .Headers(getHeaders(url, urlParam, mxcid, !TextUtils.isEmpty(path_refer) ? path_refer : ""))
//                .Tag(App.getInstance())//需要取消请求的tag
//                .Success(new Success() {
//                    @Override
//                    public void Success(Object o) {
//                        Response response = (Response) o;
//                        String model = praseNetWork(response);
//                        LogWriter.i(MTNET, "onResponse:" + model);
//                        LogWriter.i(MTNET, "/***********************网络请求 end success ************************/");
//                        if (typeToken != null) {
//                            Object responseBeanObject = null;
//                            try {
//                                responseBeanObject = new Gson().fromJson(model, typeToken);
//                            } catch (Exception e) {
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(urls, mxcid, e.toString());
//                            }
//
//                            if (responseBeanObject == null) {
////                                        if (callback != null) {
////                                            callback.onFail(new Exception("数据类型错误"));
////                                        }
////                                        return;
////                                    }
////
////                                    if (callback != null) {
////                                        callback.onSuccess(responseBeanObject);
////                                    }
//                        } else {
//                            Object responseBeanObject = null;
//                            try {
//                                JSONObject jsonObject = new JSONObject(model);
//                                String code = getResponseCode(jsonObject);
//                                String msg = getResponseMsg(jsonObject);
//                                if (code != null && !"".equals(code) && msg != null) {
//                                    if ("1".equals(code)) {
//                                        String data = jsonObject.getString("data");
//                                        LogWriter.e("mylog", "data:" + data);
//                                        responseBeanObject = new Gson().fromJson(data, bean);
//                                    } else {
//                                        if (callback != null) {
//                                            callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
//                                        }
//                                        return;
//                                    }
//                                } else {
//                                    responseBeanObject = new Gson().fromJson(model, bean);
//                                }
//                            } catch (Exception e) {
//                                LogWriter.e(e.getLocalizedMessage());
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(urls, mxcid, e.toString());
//                            }
//
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                }
//                                return;
//                            }
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        }
//                    }
//                })
//                .Fail(new Fail() {
//                    @Override
//                    public void Fail(Object... values) {
//                        LogWriter.e(MTNET, "onErrorResponse:" + values[1].toString());
//                        LogWriter.i(MTNET, "/***********************网络请求 end fail************************/");
//                        if (callback != null) {
//                            callback.onFail(new Exception(values[1].toString()));
//                        }
//
//                        reportErrorLog(urls, mxcid, values[1].toString());
//                    }
//                })
//                .get();
    }

    /**
     * 下载文件, 用于支付成功-保存图片到相册
     */
    public static void download(String url, final Map<String, String> urlParam, String savePath, final RequestCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        url = urlGetParam(urlParam, url);
        mApi.downloadFile(mApi, url, savePath, new NetworkManager.NetworkProgressListener<String>() {
            @Override
            public void onProgress(float progress, long done, long total) {
            
            }
    
            @Override
            public void onSuccess(String result, String showMsg) {
                if (callback != null) {
                    callback.onSuccess(result);
                }
            }
    
            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                if (callback != null) {
                    callback.onFail(new Exception(showMsg));
                }
            }
        });
        
        
//        LogWriter.i(MTNET, "/***********************网络请求 start************************/");
//        url = urlGetParam(urlParam, url);
//        LogWriter.i(MTNET, "请求GET Url--" + url);
//
//        new ApiClient.Builder(url)
//                .savePath(savePath)
//                .CacheTime(0)
//                .Progress(new Progress() {
//                    @Override
//                    public void progress(float p,long done, long total) {
//
//                    }
//                })
//                .Success(new Success() {
//                    @Override
//                    public void Success(Object o) {
//                        //返回path
//                        LogWriter.i(MTNET, "/***********************网络请求 end success ************************/");
//                        if (callback != null) {
//                            callback.onSuccess(String.valueOf(o));
//                        }
//                    }
//                })
//                .Fail(new Fail() {
//                    @Override
//                    public void Fail(Object... values) {
//                        LogWriter.i(MTNET, "/***********************网络请求 end fail************************/");
//                        if (callback != null) {
//                            callback.onFail(new Exception(values.toString()));
//                        }
//                    }
//                })
//                .download();
    }

    /**
     * post 不带参数
     *
     * @param <T>
     */
    public static <T> void post(String url, final Class<T> bean, final RequestCallback callback) {
        post(url, null, bean, callback);
    }

    /**
     * post 带参数
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback) {
        post(url, params, bean, callback, null);
    }

    /**
     * post 带参数 带typeToken
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback, final Type typeToken) {
        post(url, params, bean, callback, typeToken, 0);
    }

    /**
     * post 带参数 带typeToken 带超时
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(final String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback, final Type typeToken,
                                final int connectTimeout) {
        
        mApi.post(mApi, url, params, null, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                Object responseBeanObject = null;
                if (typeToken != null) {
                    try {
                        responseBeanObject = new Gson().fromJson(result, typeToken);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new Exception(showMsg));
                        }
                        return;
                    }
        
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String code = getResponseCode(jsonObject);
                        String msg = getResponseMsg(jsonObject);
                        if (code != null && !"".equals(code) && msg != null) {
                            if ("1".equals(code)) {
                                String data = jsonObject.getString("data");
                                LogWriter.e("mylog", "data:" + data);
                                responseBeanObject = new Gson().fromJson(data, bean);
                            } else {
                                if (callback != null) {
                                    callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
                                }
                                return;
                            }
                        } else {
                            responseBeanObject = new Gson().fromJson(result, bean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new Exception(showMsg));
                        }
                        return;
                    }
        
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }
                }
            }
    
            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                if (callback != null) {
                    callback.onFail(new Exception(showMsg));
                }
            }
        }, false);
        
        
//        LogWriter.i(MTNET, "/***********************网络请求 start************************/");
//        LogWriter.i(MTNET, "请求方式--Post");
//        LogWriter.i(MTNET, "请求Url--" + url);
//        final String mxcid = Guid.get();
//        new ApiClient.Builder(url)
//                .Headers(getHeaders(url, params != null ? params : new HashMap<String, String>(), mxcid, !TextUtils.isEmpty(path_refer) ? path_refer : ""))
//                .Params(params != null ? params : new HashMap<String, String>())
//                .Tag(App.getInstance())//需要取消请求的tag
//                .Success(new Success() {
//                    @Override
//                    public void Success(Object o) {
//                        Response response = (Response) o;
//                        String model = praseNetWork(response);
//                        LogWriter.i(MTNET, "onResponse:" + model);
//                        LogWriter.i(MTNET, "/***********************网络请求 end success ************************/");
//                        if (typeToken != null) {
//                            Object responseBeanObject = null;
//                            try {
//                                responseBeanObject = new Gson().fromJson(model, typeToken);
//                            } catch (JsonSyntaxException e) {
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(url, mxcid, e.toString());
//                            }
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                }
//                                return;
//                            }
//
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        } else {
//                            Object responseBeanObject = null;
//                            try {
//                                JSONObject jsonObject = new JSONObject(model);
//                                String code = getResponseCode(jsonObject);
//                                String msg = getResponseMsg(jsonObject);
//                                if (code != null && !"".equals(code) && msg != null) {
//                                    if ("1".equals(code)) {
//                                        String data = jsonObject.getString("data");
//                                        LogWriter.e("mylog", "data:" + data);
//                                        responseBeanObject = new Gson().fromJson(data, bean);
//                                    } else {
//                                        if (callback != null) {
//                                            callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
//                                        }
//
//                                        reportErrorLog(url, mxcid, getResponseShowMsg(jsonObject));
//                                        return;
//                                    }
//                                } else {
//                                    responseBeanObject = new Gson().fromJson(model, bean);
//                                }
//                            } catch (Exception e) {
//                                LogWriter.e(e.getLocalizedMessage());
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(url, mxcid, e.toString());
//                            }
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                }
//                                return;
//                            }
//
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        }
//                    }
//                })
//                .Fail(new Fail() {
//                    @Override
//                    public void Fail(Object... values) {
//                        LogWriter.e(MTNET, "onErrorResponse:" + values[1].toString());
//                        LogWriter.i(MTNET, "/***********************网络请求 end fail************************/");
//                        if (callback != null) {
//                            callback.onFail(new Exception(values[1].toString()));
//                        }
//
//                        reportErrorLog(url, mxcid, values[1].toString());
//                    }
//                })
//                .post();

    }

    /**
     * post 带参数 带typeToken 只有充值时使用
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void postRecharge(final String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback, final Type typeToken) {
    
        mApi.post(mApi, url, params, null, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                Object responseBeanObject = null;
                if (typeToken != null) {
                    try {
                        responseBeanObject = new Gson().fromJson(result, typeToken);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new Exception(showMsg));
                        }
                        return;
                    }
                
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }
                } else {
                    if (!TextUtils.isEmpty(result)) {
                        final RechargeBean bean = new RechargeBean();
                        try {
                            final JSONObject json = new JSONObject(result);
            
                            bean.setSuccess(RechargeHandler.getBoolean(json, "success"));
                            bean.setMessage(RechargeHandler.getString(json, "msg"));
                            bean.setStatus(RechargeHandler.getInt(json, "status"));
                            bean.setError(RechargeHandler.getString(json, "error"));
                            bean.setRechargeNum(RechargeHandler.getString(json, "rechargeNumber"));
                            bean.setFromXML(RechargeHandler.getString(json, "formXML"));
                            bean.setOrderId(RechargeHandler.getLong(json, "orderId"));
                            bean.setSubOrderId(RechargeHandler.getLong(json, "subOrderId"));
                            if (callback != null) {
                                callback.onSuccess(bean);
                            }
                            return;
                        } catch (final JSONException e) {
                            e.printStackTrace();
                        }
        
                    }
    
                    if (callback != null) {
                        callback.onFail(new Exception(showMsg));
                    }
                }
            }
        
            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                if (callback != null) {
                    callback.onFail(new Exception(showMsg));
                }
            }
        }, false);
        
        
//        LogWriter.i(MTNET, "/***********************网络请求 start************************/");
//        LogWriter.i(MTNET, "请求POST Url--" + url);
//        final String mxcid = Guid.get();
//
//        new ApiClient.Builder(url)
//                .Headers(getHeaders(url, params, mxcid, !TextUtils.isEmpty(path_refer) ? path_refer : ""))
//                .Params(params)
//                .Tag(App.getInstance())//需要取消请求的tag
//                .Success(new Success() {
//                    @Override
//                    public void Success(Object o) {
//                        Response response = (Response) o;
//                        String model = praseNetWork(response);
//                        LogWriter.i(MTNET, "onResponse:" + model);
//                        LogWriter.i(MTNET, "/***********************网络请求 end success ************************/");
//                        if (typeToken != null) {
//                            Object responseBeanObject = null;
//                            try {
//                                responseBeanObject = new Gson().fromJson(model, typeToken);
//                            } catch (Exception e) {
//                                LogWriter.d(e.getLocalizedMessage());
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(url, mxcid, e.toString());
//                            }
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                    return;
//                                }
//                            }
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        } else {
//                            // TODO X
//                            // request.setHandler(new RechargeHandler());
//                            if (model != null) {
//                                final RechargeBean bean = new RechargeBean();
//                                try {
//                                    final JSONObject json = new JSONObject(model);
//
//                                    bean.setSuccess(RechargeHandler.getBoolean(json, "success"));
//                                    bean.setMessage(RechargeHandler.getString(json, "msg"));
//                                    bean.setStatus(RechargeHandler.getInt(json, "status"));
//                                    bean.setError(RechargeHandler.getString(json, "error"));
//                                    bean.setRechargeNum(RechargeHandler.getString(json, "rechargeNumber"));
//                                    bean.setFromXML(RechargeHandler.getString(json, "formXML"));
//                                    bean.setOrderId(RechargeHandler.getLong(json, "orderId"));
//                                    bean.setSubOrderId(RechargeHandler.getLong(json, "subOrderId"));
//                                    if (callback != null) {
//                                        callback.onSuccess(bean);
//                                    }
//                                    return;
//                                } catch (final JSONException e) {
//                                    LogWriter.debugError("RechargeHandler :Parser Error! \r\n" + e.toString());
//
//                                    reportErrorLog(url, mxcid, e.toString());
//                                }
//
//                            }
//
//                            if (callback != null) {
//                                callback.onFail(new Exception("数据类型错误"));
//                            }
//                        }
//                    }
//                })
//                .Fail(new Fail() {
//                    @Override
//                    public void Fail(Object... values) {
//                        LogWriter.e(MTNET, "onErrorResponse:" + values[1].toString());
//                        LogWriter.i(MTNET, "/***********************网络请求 end fail************************/");
//                        if (callback != null) {
//                            callback.onFail(new Exception(values[1].toString()));
//                        }
//
//                        reportErrorLog(url, mxcid, values[1].toString());
//                    }
//                })
//                .post();
    }

    /**
     * get 处理url参数
     *
     * @param urlParam
     * @param url
     */
    public static String urlGetParam(final List<String> urlParam, final String url) {
        if (urlParam != null && urlParam.size() > 0) {

            try {
                final StringBuffer sBuffer = new StringBuffer();
                final StringBuffer urlBuffer = new StringBuffer(url);
                for (int i = 0; i < urlParam.size(); i++) {
                    sBuffer.append("{" + i + "}");
                    final int start = urlBuffer.indexOf(sBuffer.toString());
                    if (start == -1) {
                        sBuffer.delete(0, sBuffer.toString().length());
                        continue;
                    }
                    final int len = sBuffer.length();
                    if (urlParam != null && urlParam.size() > i) {
                        String s = urlParam.get(i);
                        if (s != null) {
                            s = URLEncoder.encode(s, "utf-8");
                            urlBuffer.replace(start, start + len, s);
                            sBuffer.delete(0, sBuffer.toString().length());
                        }
                    }
                }
                LogWriter.i(MTNET, "URL Param 处理之后:" + urlBuffer.toString());
                return urlBuffer.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        return url;
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

            LogWriter.e("checkParam", urlBuffer.toString());
            return urlBuffer.toString();
        } catch (Exception e) {
            LogWriter.e(e.getLocalizedMessage());
        }
        return urlBuffer.toString();
    }


    private static String getResponseCode(JSONObject jsonObject) {
        try {
            String code = jsonObject.getString("code");
            return code;
        } catch (JSONException e) {
            LogWriter.e(e.getLocalizedMessage());
        }
        return null;
    }

    private static String getResponseMsg(JSONObject jsonObject) {
        try {
            String msg = jsonObject.getString("msg");
            return msg;
        } catch (JSONException e) {
            LogWriter.e(e.getLocalizedMessage());
        }
        return null;
    }

    private static String getResponseShowMsg(JSONObject jsonObject) {
        try {
            String msg = jsonObject.getString("showMsg");
            return msg;
        } catch (JSONException e) {
            LogWriter.e(e.getLocalizedMessage());
        }
        return null;
    }

    public static String getUserToken() {
        String userToken = "";
        String cookie = getCookieStr();
        if (cookie != null) {
            if (cookie.contains(";")) {
                String[] cookies = cookie.split(";");
                if (cookies.length > 0) {
                    for (String str : cookies) {
                        if (str.contains("_mi_=")) {
                            userToken = str.trim().substring(5);
                        }
                    }
                }
            } else {
                if (cookie.contains("_mi_=")) {
                    userToken = cookie.trim().substring(5);
                }
            }
        }
        return userToken;
    }

    public static String getCookieStr() {
        return CookieManager.Companion.getInstance().getCookiesDesc();
////        List<Cookie> cookies = NetworkManager.getInstance().mCookieJarManager.getCookies();
//        List<Cookie> cookies = CookieManager.Companion.getInstance().getCookies();
//        StringBuffer stringBuffer = new StringBuffer();
//        for (int i = 0; i < cookies.size(); i++) {
//            if (i > 0) {
//                stringBuffer.append(";");
//            }
//            stringBuffer.append(cookies.get(i).toString());
//        }
//        return stringBuffer.toString();
    }

    /**
     * 设置请求头
     */
    private static Map<String, String> getHeaders(final String url, final Map<String, String> params, final String mxcid, final String referer) {
        final long time = System.currentTimeMillis();

        final StringBuffer paramBuffer = new StringBuffer();
        // TODO  尝试添加header数据。验证的还是需要在这里负值。
        if (null != params && params.size() > 0) {
            LogWriter.i("MTNet", "businessParam:" + params.toString());
            int index = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                //TODO X key需要encode吗?
                paramBuffer.append(entry.getKey());
                paramBuffer.append('=');
                String value = entry.getValue() != null ? entry.getValue() : "";
                // TODO 改成原来的url encode方法，需要验证一下
                paramBuffer.append(Utils.UrlEncodeUnicode(value));
                if (++index >= params.size()) {
                    break;
                }
                paramBuffer.append('&');
            }

        }

        StringBuffer sb1 = new StringBuffer();
        sb1.append(FrameConstant.APP_ID).append(FrameConstant.CLIENT_KEY).append(time).append(url).append(paramBuffer.toString());

        StringBuffer sb = new StringBuffer();
        sb.append(FrameConstant.APP_ID).append(FrameConstant.COMMA).append(time).append(FrameConstant.COMMA);
        sb.append(Utils.getMd5(sb1.toString()));
        sb.append(FrameConstant.COMMA).append(FrameConstant.CHANNEL_ID);

        Map<String, String> headers = new HashMap<String, String>();
        headers.clear();
        /**
         * 添加必要的头信息
         */
        headers.put(FrameConstant.HEADER, sb.toString());
        String log = String.format("http:header is %s:%s", FrameConstant.HEADER, sb.toString());
        LogWriter.debugError(log);

        headers.put(FrameConstant.HEADER_DEVICE_INFO, String.format("%s_%s", Build.MODEL, Build.HARDWARE));
        headers.put(FrameConstant.HEADER_PUSH_TOKEN, FrameConstant.push_token);
        headers.put(FrameConstant.HEADER_JPUSH_ID, FrameConstant.jpush_id);
        headers.put(FrameConstant.HEADER_MX_CID, mxcid);

        // CIP数据经纬度
        StringBuffer sblocation = new StringBuffer();
        sblocation.append(FrameConstant.UA_LOCATION_LONGITUDE);
        sblocation.append(FrameConstant.COMMA);
        sblocation.append(FrameConstant.UA_LOCATION_LATITUDE);
        headers.put(FrameConstant.HEADER_CIP_LOCATION, sblocation.toString());
        // CIP数据设备信息
        StringBuffer sbtelephone = new StringBuffer();
        sbtelephone.append("CELLID=");
        sbtelephone.append(FrameConstant.UA_MOBILE_CELLID);
        sbtelephone.append(";");
        sbtelephone.append("CID=");
        sbtelephone.append(FrameConstant.UA_MOBILE_CID);
        sbtelephone.append(";");
        sbtelephone.append("IMEI=");
        sbtelephone.append(FrameConstant.UA_MOBILE_IMEI);
        sbtelephone.append(";");
        sbtelephone.append("IMSI=");
        sbtelephone.append(FrameConstant.UA_MOBILE_IMSI);
        sbtelephone.append(";");
        sbtelephone.append("MAC=");
        sbtelephone.append(FrameConstant.UA_MOBILE_MAC_ADDRESS);
        sbtelephone.append(";");
        sbtelephone.append("LANGUAGE=");
        sbtelephone.append(FrameConstant.UA_MOBILE_LANGUAGE);
        headers.put(FrameConstant.HEADER_CIP_TELEPHONE_INFO, "");//sbtelephone.toString()

        headers.put(FrameConstant.ACCEPT, FrameConstant.UTF8);
        headers.put(FrameConstant.UA, FrameConstant.UA_STR);
        headers.put(FrameConstant.AE, "gzip");
        if (null != url && !TextUtils.isEmpty(referer)) {
            headers.put("Referer", referer);
        }
        return headers;
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
                    FrameConstant.setServerDate(sdf.parse(Date));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if (set.contains(("X-DDOS"))) {
            String ddosString = headers.get("X-DDOS");
            if (ddosString != null && "true".equals(ddosString.toLowerCase().trim())) {
                final String ddosStr = ddosString.replace("{\"html\":\"", "").replace("\"}", "")
                        .replace("\\\"", "\"");
                Intent intent = new Intent(SessionHelper.ACTION_DDOS_ATTACH);
                intent.putExtra("ddos_url", ddosStr);
                LocalBroadcastManager.getInstance(App.getInstance()).sendBroadcast(intent);
                return new Exception("ddos").toString();
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

                        String read;

                        while ((read = in.readLine()) != null) {
                            output.append(read).append("\n");
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


/*************************给请求添加特定的tag，方便取消请求  *****/
    /**
     * post 带参数
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(Object tag,String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback) {
        post(tag,url, params, bean, callback, null);
    }

    /**
     * post 带参数 带typeToken
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(Object tag,String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback, final Type typeToken) {
        post(tag,url, params, bean, callback, typeToken, 0);
    }

    /**
     * post 带参数 带typeToken 带超时
     *
     * @param <T> List<RequestParameter>
     */
    public static <T> void post(Object tag,final String url, final Map<String, String> params, final Class<T> bean, final RequestCallback callback, final Type typeToken,
                                final int connectTimeout) {
    
        mApi.post(tag, url, params, null, new NetworkManager.NetworkListener<String>() {
            @Override
            public void onSuccess(String result, String showMsg) {
                Object responseBeanObject = null;
                if (typeToken != null) {
                    try {
                        responseBeanObject = new Gson().fromJson(result, typeToken);
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new Exception(showMsg));
                        }
                        return;
                    }
                
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        String code = getResponseCode(jsonObject);
                        String msg = getResponseMsg(jsonObject);
                        if (code != null && !"".equals(code) && msg != null) {
                            if ("1".equals(code)) {
                                String data = jsonObject.getString("data");
                                LogWriter.e("mylog", "data:" + data);
                                responseBeanObject = new Gson().fromJson(data, bean);
                            } else {
                                if (callback != null) {
                                    callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
                                }
                                return;
                            }
                        } else {
                            responseBeanObject = new Gson().fromJson(result, bean);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                
                    if (responseBeanObject == null) {
                        if (callback != null) {
                            callback.onFail(new Exception(showMsg));
                        }
                        return;
                    }
                
                    if (callback != null) {
                        callback.onSuccess(responseBeanObject);
                    }
                }
            }
        
            @Override
            public void onFailure(NetworkException<String> exception, String showMsg) {
                if (callback != null) {
                    callback.onFail(new Exception(showMsg));
                }
            }
        }, false);
        
        
//        LogWriter.i(MTNET, "/***********************网络请求 start************************/");
//        LogWriter.i(MTNET, "请求方式--Post");
//        LogWriter.i(MTNET, "请求Url--" + url);
//        final String mxcid = Guid.get();
//        new ApiClient.Builder(url)
//                .Headers(getHeaders(url, params != null ? params : new HashMap<String, String>(), mxcid, !TextUtils.isEmpty(path_refer) ? path_refer : ""))
//                .Params(params != null ? params : new HashMap<String, String>())
//                .Tag(tag)//需要取消请求的tag
//                .Success(new Success() {
//                    @Override
//                    public void Success(Object o) {
//                        Response response = (Response) o;
//                        String model = praseNetWork(response);
//                        LogWriter.i(MTNET, "onResponse:" + model);
//                        LogWriter.i(MTNET, "/***********************网络请求 end success ************************/");
//                        if (typeToken != null) {
//                            Object responseBeanObject = null;
//                            try {
//                                responseBeanObject = new Gson().fromJson(model, typeToken);
//                            } catch (JsonSyntaxException e) {
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(url, mxcid, e.toString());
//                            }
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                }
//                                return;
//                            }
//
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        } else {
//                            Object responseBeanObject = null;
//                            try {
//                                JSONObject jsonObject = new JSONObject(model);
//                                String code = getResponseCode(jsonObject);
//                                String msg = getResponseMsg(jsonObject);
//                                if (code != null && !"".equals(code) && msg != null) {
//                                    if ("1".equals(code)) {
//                                        String data = jsonObject.getString("data");
//                                        LogWriter.e("mylog", "data:" + data);
//                                        responseBeanObject = new Gson().fromJson(data, bean);
//                                    } else {
//                                        if (callback != null) {
//                                            callback.onFail(new Exception(getResponseShowMsg(jsonObject)));
//                                        }
//
//                                        reportErrorLog(url, mxcid, getResponseShowMsg(jsonObject));
//                                        return;
//                                    }
//                                } else {
//                                    responseBeanObject = new Gson().fromJson(model, bean);
//                                }
//                            } catch (Exception e) {
//                                LogWriter.e(e.getLocalizedMessage());
//                                // TODO 在这里获取到缓存对象，清除这个request的缓存
////                        clearSpeCache(stringRequest);
//                                reportErrorLog(url, mxcid, e.toString());
//                            }
//                            if (responseBeanObject == null) {
//                                if (callback != null) {
//                                    callback.onFail(new Exception("数据类型错误"));
//                                }
//                                return;
//                            }
//
//                            if (callback != null) {
//                                callback.onSuccess(responseBeanObject);
//                            }
//                        }
//                    }
//                })
//                .Fail(new Fail() {
//                    @Override
//                    public void Fail(Object... values) {
//                        LogWriter.e(MTNET, "onErrorResponse:" + values[1].toString());
//                        LogWriter.i(MTNET, "/***********************网络请求 end fail************************/");
//                        if (callback != null) {
//                            callback.onFail(new Exception(values[1].toString()));
//                        }
//
//                        reportErrorLog(url, mxcid, values[1].toString());
//                    }
//                })
//                .post();

    }
    
}

