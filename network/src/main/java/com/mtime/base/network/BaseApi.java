package com.mtime.base.network;

import android.text.TextUtils;

import java.io.File;
import java.util.Map;

/**
 * Created by LiJiaZhi on 17/3/23. 基础 api 类. host() 为 null 时自动使用 NetworkManager 初始化时的 host.
 */
public abstract class BaseApi {

    protected abstract String host();

    public String appendHost(String url) {
        return TextUtils.isEmpty(host()) ? url : host() + url;
    }

    public void cancel() {
        cancel(this);
    }

    public void cancel(Object tag) {
        NetworkManager.getInstance().cancel(tag);
    }


    /**
     * 带缓存时间----header里Cache-Time
     *
     * @param tag
     * @param url
     * @param urlParam
     * @param listener
     * @param cacheTime 毫秒  例如30分钟：30*60*1000
     * @param <T>
     */
    protected <T> void getWithCacheTime(Object tag, String url, Map<String, String> urlParam,
                                        NetworkManager.NetworkListener<T> listener, long cacheTime) {
//        NetworkManager.getInstance().getWithCacheTime(tag, appendHost(url), md5Params(urlParam), listener, cacheTime);
        NetworkManager.getInstance().getWithCacheTime(tag, appendHost(url), urlParam, listener, cacheTime);
    }

    protected <T> void getWithCache(Object tag, String url, Map<String, String> urlParam,
                                    NetworkManager.NetworkWithCacheListener<T> listener) {
        getWithCache(tag, url, urlParam, null, listener);
    }

    protected <T> void getWithCache(Object tag, String url, Map<String, String> urlParam, Map<String, String> headers,
                                    NetworkManager.NetworkWithCacheListener<T> listener) {
//        NetworkManager.getInstance().getWithCache(tag, appendHost(url), md5Params(urlParam), headers, listener);
        NetworkManager.getInstance().getWithCache(tag, appendHost(url), urlParam, headers, listener);
    }


    protected <T> void get(Object tag, String url, Map<String, String> urlParam,
                           NetworkManager.NetworkListener<T> listener) {
        get(tag, url, urlParam, null, listener, false);
    }

    protected <T> void get(Object tag, String url, Map<String, String> urlParam, long cacheTime,
                           NetworkManager.NetworkListener<T> listener) {
        get(tag, url, urlParam, cacheTime, null, listener, false);
    }

    protected <T> void get(Object tag, String url, Map<String, String> urlParam, Map<String, String> headers,
                           NetworkManager.NetworkListener<T> listener, boolean callbackOnBackground) {
        get(tag, url, urlParam, 0, headers, listener, callbackOnBackground);
    }

    public <T> void get(Object tag, String url, Map<String, String> urlParam, long cacheTime, Map<String, String> headers,
                        NetworkManager.NetworkListener<T> listener, boolean callbackOnBackground) {
//        NetworkManager.getInstance().get(tag, appendHost(url), md5Params(urlParam), headers, listener,
//                cacheTime, false, callbackOnBackground, false);
        NetworkManager.getInstance().get(tag, appendHost(url), urlParam, headers, listener,
                cacheTime, false, callbackOnBackground, false);
    }

    protected <T> T syncGet(String url, Map<String, String> param, Class<T> resultClazz) {
        return syncGet(url, param, null, resultClazz);
    }

    protected <T> T syncGet(String url, Map<String, String> param, Map<String, String> headers, Class<T> resultClazz) {
//        return NetworkManager.getInstance().syncGet(appendHost(url), md5Params(param), headers, resultClazz);
        return NetworkManager.getInstance().syncGet(appendHost(url), param, headers, resultClazz);
    }

    protected <T> void post(Object tag, String url, Map<String, String> params,
                            NetworkManager.NetworkListener<T> listener) {
        post(tag, url, params, null, listener, false);
    }

    public <T> void post(Object tag, String url, Map<String, String> params, Map<String, String> headers,
                         NetworkManager.NetworkListener<T> listener, boolean callbackOnBackground) {
//        NetworkManager.getInstance().post(tag, appendHost(url), md5Params(params), headers, listener,
        NetworkManager.getInstance().post(tag, appendHost(url), params, headers, listener,
                callbackOnBackground);
    }

    protected <T> T syncPost(String url, Map<String, String> param, Class<T> resultClazz) {
        return syncPost(url, param, null, resultClazz);
    }

    protected <T> T syncPost(String url, Map<String, String> param, Map<String, String> headers, Class<T> resultClazz) {
//        return NetworkManager.getInstance().syncPost(appendHost(url), md5Params(param), headers, resultClazz);
        return NetworkManager.getInstance().syncPost(appendHost(url), param, headers, resultClazz);
    }

    protected <T> void postJson(Object tag, String url, Object payload, Map<String, String> params, Map<String, String> headers,
                                NetworkManager.NetworkListener<T> listener) {
        postJson(tag, url, payload, params, headers, listener, false);
    }

    public <T> void postJson(Object tag, String url, Object payload, Map<String, String> params, Map<String, String> headers,
                             NetworkManager.NetworkListener<T> listener, boolean callbackOnBackground) {
        NetworkManager.getInstance().postJson(tag, appendHost(url), payload, params, headers, listener, callbackOnBackground);
    }


    protected <T> void uploadFile(Object tag, String url, Map<String, String> params, String fileKey, File file,
                                  NetworkManager.NetworkProgressListener<T> listener) {
        uploadFile(tag, url, params, fileKey, file, listener, false);
    }

    public <T> void uploadFile(Object tag, String url, Map<String, String> params, String fileKey, File file,
                               NetworkManager.NetworkProgressListener<T> listener, boolean callbackOnBackground) {
//        NetworkManager.getInstance().uploadFile(tag, appendHost(url), md5Params(params), fileKey, file, listener,
//                callbackOnBackground);
        NetworkManager.getInstance().uploadFile(tag, appendHost(url), params, fileKey, file, listener,
                callbackOnBackground);
    }

    protected <T> T syncUploadFile(String url, Map<String, String> params, String fileKey, File file, Class<T> clazz) {
        return NetworkManager.getInstance().syncUploadFile(url, params, fileKey, file, clazz);
    }

    protected <T> void uploadFiles(Object tag, String url, Map<String, String> params, Map<String, File> files,
                                   NetworkManager.NetworkProgressListener<T> listener) {
//        NetworkManager.getInstance().uploadFiles(tag, appendHost(url), md5Params(params), files, listener,
//                true);
        NetworkManager.getInstance().uploadFiles(tag, appendHost(url), params, files, listener,
                true);
    }

    protected <T> void uploadFiles(Object tag, String url, Map<String, String> params, Map<String, File> files,
                                   NetworkManager.NetworkProgressListener<T> listener, boolean callbackOnBackground) {
//        NetworkManager.getInstance().uploadFiles(tag, appendHost(url), md5Params(params), files, listener,
//                callbackOnBackground);
        NetworkManager.getInstance().uploadFiles(tag, appendHost(url), params, files, listener,
                callbackOnBackground);
    }

    public <T> void uploadFileUseBody(Object tag, String url, Map<String, Object> params,
                                      NetworkManager.NetworkProgressListener<T> listener) {

        NetworkManager.getInstance().uploadFileUseBody(tag, appendHost(url), params, listener);

    }


    public void downloadFile(Object tag, String url, String dirOrFile,
                             NetworkManager.NetworkProgressListener<String> listener) {
        NetworkManager.getInstance().downloadFile(tag, appendHost(url), dirOrFile, listener);
    }

    public void downloadFile(Object tag, String url, String dirOrFile,
                             NetworkManager.NetworkProgressListener<String> listener, boolean callbackOnBackground) {
        NetworkManager.getInstance().downloadFile(tag, appendHost(url), null, dirOrFile, listener,
                callbackOnBackground);
    }


//    /**
//     * 请求参数统一处理
//     * <p>
//     * sign: 请求参数按key名升序排列, 得到初始secstr，如a=1&ab=22&b=2&c=3&d=URL.safeEncode(xxx, 'utf-8')&salt=xxxx 最终得到sign =
//     * MD5(secstr) salt:为静态盐 android-》salt="5562a5ad362a78624c58a518ad5a5cb3" ios——>
//     * salt="b7aeb7b26b8a8fd9b01c5390a95c1927" h5——> salt="0221d1357ec47ea5d03395aea08964b9"
//     *
//     * @param urlParam
//     * @return
//     */
//    private Map<String, String> md5Params(Map<String, String> urlParam) {
//        if (null == urlParam || urlParam.size() == 0)
//            return null;
//        StringBuilder sb = new StringBuilder();
//        Map<String, String> temp = sortMapByKey(urlParam);
//        for (Map.Entry<String, String> entry : temp.entrySet()) {
//            if (!TextUtils.isEmpty(entry.getKey()) && !TextUtils.isEmpty(entry.getValue()))
//                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue()));
//        }
//        sb.append("salt=5562a5ad362a78624c58a518ad5a5cb3");
//        urlParam.put("sign", MD5.getMD5(sb.toString()));
//        return urlParam;
//    }
//
//    /**
//     * 使用 Map按key进行排序
//     *
//     * @param map
//     * @return
//     */
//    private Map<String, String> sortMapByKey(Map<String, String> map) {
//        if (map == null || map.isEmpty()) {
//            return null;
//        }
//        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());
//        sortMap.putAll(map);
//        return sortMap;
//    }
//
//    // 比较器类
//    private static class MapKeyComparator implements Comparator<String> {
//        @Override
//        public int compare(String str1, String str2) {
//            return str1.compareTo(str2);
//        }
//    }
}
