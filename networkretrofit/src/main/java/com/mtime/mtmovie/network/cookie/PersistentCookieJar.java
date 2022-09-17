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

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by yinguanping on 17/1/22.
 * cookie 持久化
 */
public class PersistentCookieJar implements CookieJarManager {

    private final CookieCache cache;
    private final CookiePersistor persistor;

    public PersistentCookieJar(CookieCache cache, CookiePersistor persistor) {
        this.cache = cache;
        this.persistor = persistor;

        this.cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        Log.e("NET_", "saveFromResponse " + cookies.toString());
        cache.addAll(cookies);
        persistor.saveAll(url, filterPersistentCookies(cookies));
    }

    private static List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();

        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    /**
     * 供外部调用设置cookies
     *
     * @param cookieStr
     */
    @Override
    synchronized public void setCookies(HttpUrl url, String cookieStr) {
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(Cookie.parse(url, cookieStr));
        cache.addAll(cookies);
        persistor.saveAll(url, filterPersistentCookies(cookies));
    }

    /**
     * 外部调用获取全部cookies
     *
     * @return
     */
    @Override
    synchronized public List<Cookie> getCookies() {
        return persistor.loadAll();
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();

        for (Iterator<Cookie> it = cache.iterator(); it.hasNext(); ) {
            Cookie currentCookie = it.next();

            if (isCookieExpired(currentCookie)) {
                cookiesToRemove.add(currentCookie);
                it.remove();

            } else if (currentCookie.matches(url)) {//此处注意，hostonly默认为false。使用时如果需要为true需要设置
                validCookies.add(currentCookie);
            }
        }

        persistor.removeAll(cookiesToRemove);

        Log.e("NET_", "cookiesToRemove " + cookiesToRemove.toString());
        Log.e("NET_", "validCookies " + validCookies.toString());
        return validCookies;
    }

    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    @Override
    synchronized public void clearSession() {
        cache.clear();
        cache.addAll(persistor.loadAll());
    }

    @Override
    synchronized public void clear() {
        cache.clear();
        persistor.clear();
    }
}
