/*
 * Copyright (C) 2016 Francisco José Montiel Navarro.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mtime.mtmovie.network.cookie;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mtime.mtmovie.network.utils.CheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by yinguanping on 17/1/22.
 * cookie SD卡读取和存储，默认。如果没有设置cookie目录，默认系统cache路径
 */
public class FileCookiePersistor implements CookiePersistor {

    private final Context context;
    private final String cookiePath;
    private final static String FILENAME = "/cookie.txt";
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String NAME_VALUE_SEPARATOR = "=";

    public FileCookiePersistor(Context context, String cookiePath) {
        this.context = context;
        this.cookiePath = !CheckUtil.checkNULL(cookiePath) ? cookiePath : context.getCacheDir().getAbsolutePath();
    }

    @Override
    public List<Cookie> loadAll() {
        String cookieStr = readFromFile();
        Map<String, String> cookieMap = strToMap(cookieStr);
        List<Cookie> cookies = new ArrayList<>();
        if (null == cookieMap) {
            return cookies;
        }
        if (cookieMap.size() > 0) {
            for (Map.Entry<String, ?> entry : cookieMap.entrySet()) {
                String serializedCookie = (String) entry.getValue();
                Cookie cookie = new SerializableCookie().decode(serializedCookie);
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    @Override
    public void saveAll(HttpUrl url, Collection<Cookie> cookies) {
        Map<String, String> cookiesMap = new ConcurrentHashMap<>();
        for (Cookie cookie : cookies) {
            cookiesMap.put(createCookieKey(cookie), cookie.toString());
        }

        //每次写之前先尝试取出之前的，如果有作比对，如果是重复的，则更新。
        String oldCookie = readFromFile();
        Map<String, String> oldCookieMap = null;
        final Map<String, String> finalSavemap = new ConcurrentHashMap<>();
        if (!CheckUtil.checkCookieNULL(oldCookie)) {//存在旧的cookie，需要做比对。看是否更新还是追加
            oldCookieMap = strToMap(oldCookie);
            boolean hasKey = false;
            for (Map.Entry<String, ?> entry : oldCookieMap.entrySet()) {
                String key = entry.getKey();
                String oldValue = (String) entry.getValue();
                String newValue;
                if (cookiesMap.containsKey(key) && !"".equals(cookiesMap.get(key)) && !"{}".equals(cookiesMap.get(key))) {
                    hasKey = true;
                    newValue = mergeCookies((new SerializableCookie().decode(oldValue)).toString(), cookiesMap.get(key));
                    finalSavemap.put(key, new SerializableCookie().encode(Cookie.parse(url, newValue)));
                    oldCookieMap.remove(key);
                }
            }
            if (!hasKey) {
                finalSavemap.putAll(addMap(url, cookiesMap));
            }
            finalSavemap.putAll(oldCookieMap);
        } else {
            finalSavemap.putAll(addMap(url, cookiesMap));
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                saveToFile(finalSavemap);
            }
        }).start();
    }

    private Map<String, String> addMap(HttpUrl url, Map<String, String> cookiesMap) {
        Map<String, String> finalSavemap = new ConcurrentHashMap<>();
        Iterator<Map.Entry<String, String>> it = cookiesMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> me = it.next();
            String key = me.getKey();
            String value = me.getValue();
            finalSavemap.put(key, new SerializableCookie().encode(Cookie.parse(url, value)));
        }
        return finalSavemap;
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        String cookieStr = readFromFile();
        Log.i("cookieStr", "removeAll cookieStr=" + cookieStr);
        Map<String, String> cookieMap = strToMap(cookieStr);
        if (null == cookieMap) {
            return;
        }
        for (Cookie cookie : cookies) {
            cookieMap.remove(createCookieKey(cookie));
        }
        if (cookieMap.size() > 0) {
            saveToFile(cookieMap);
        }
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }

    @Override
    public void clear() {
        File targetFile = new File(cookiePath + FILENAME);
        if (targetFile.exists()) {
            targetFile.delete();
        }
    }

    private String readFromFile() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File folder = new File(cookiePath);

            if (folder == null || !folder.exists()) {
                folder.mkdir();
            }

            File targetFile = new File(cookiePath + FILENAME);
            String readedStr = "";

            try {
                if (!targetFile.exists()) {
                    return "";
                } else {
                    InputStream in = new BufferedInputStream(new FileInputStream(targetFile));
                    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    String tmp;

                    while ((tmp = br.readLine()) != null) {
                        readedStr += tmp;
                    }
                    br.close();
                    in.close();

                    return readedStr;
                }
            } catch (Exception e) {
                return "";
            }
        } else {
            return "";
        }

    }

    private boolean saveToFile(Map<String, String> cookiesMap) {
        if (cookiesMap == null || cookiesMap.size() == 0) {
            return false;
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File folder = new File(cookiePath);

            if (folder == null || !folder.exists()) {
                folder.mkdir();
            }

            File targetFile = new File(cookiePath + FILENAME);
            OutputStreamWriter osw;

            try {
                if (!targetFile.exists()) {
                    targetFile.createNewFile();
                }
                osw = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8);
                osw.write(mapToJson(cookiesMap).toString());
                osw.close();
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    private String mergeCookies(final String oldCookies, final String newCookies) {
        String[] oldCookie = oldCookies.split(SET_COOKIE_SEPARATOR);
        Map cookies = new ConcurrentHashMap();
        for (int i = 0; i < oldCookie.length; i++) {
            String[] data = oldCookie[i].split(NAME_VALUE_SEPARATOR);
            if (null != data && data.length > 1) {
                cookies.put(data[0], data[1]);
            }
        }

        String[] newCookie = newCookies.split(SET_COOKIE_SEPARATOR);
        for (int i = 0; i < newCookie.length; i++) {
            String[] data = newCookie[i].split(NAME_VALUE_SEPARATOR);
            if (null != data && data.length > 1) {
                cookies.put(data[0], data[1]);
            }
        }

        StringBuffer cookieStringBuffer = new StringBuffer();
        Iterator cookieNames = cookies.keySet().iterator();
        while (cookieNames.hasNext()) {
            String cookieName = (String) cookieNames.next();

            cookieStringBuffer.append(cookieName);
            cookieStringBuffer.append("=");
            cookieStringBuffer.append((String) cookies.get(cookieName));
            if (cookieNames.hasNext()) cookieStringBuffer.append(SET_COOKIE_SEPARATOR);
        }

        return cookieStringBuffer.toString();
    }

    private Map<String, String> strToMap(String str) {
        if (CheckUtil.checkNULL(str)) {
            return null;
        }
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            return null;
        }
        if (jsonObject.length() == 0) {
            return null;
        }
        ConcurrentHashMap<String, String> map = new ConcurrentHashMap();
        Iterator<String> keysItr = jsonObject.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            String value = "";
            try {
                value = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            map.put(key, value);
        }
        return map;
    }

    private JSONObject mapToJson(Map<String, String> map) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject();
            Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> me = it.next();
                String key = me.getKey();
                String value = me.getValue();
                jsonObject.put(key, value);
            }
        } catch (Exception e) {
            return null;
        }
        return jsonObject;
    }
}
