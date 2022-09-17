package com.mtime.mtmovie.network.cookie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by yinguanping on 17/2/13.
 * SharePreference持久化cookie
 */

@SuppressLint("CommitPrefEdits")
public class SharedPrefsCookiePersistor implements CookiePersistor {

    private final SharedPreferences sharedPreferences;
    private static final String SET_COOKIE_SEPARATOR = "; ";
    private static final String NAME_VALUE_SEPARATOR = "=";

    public SharedPrefsCookiePersistor(Context context) {
        this(context.getSharedPreferences("CookiePersistence", Context.MODE_PRIVATE));
    }

    public SharedPrefsCookiePersistor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public List<Cookie> loadAll() {
        List<Cookie> cookies = new ArrayList<>(sharedPreferences.getAll().size());

        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String serializedCookie = (String) entry.getValue();
            Cookie cookie = new SerializableCookie().decode(serializedCookie);
            if (cookie != null) {
                cookies.add(cookie);
            }
        }
        return cookies;
    }

    @Override
    public void saveAll(HttpUrl url, Collection<Cookie> cookies) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        for (Cookie cookie : cookies) {
//            String key = createCookieKey(cookie);
//            if (sharedPreferences.getAll().containsKey(key)) {//存在old。merge后重新保存
//                String oldCookieStr = (new SerializableCookie().decode((String) sharedPreferences.getAll().get(key))).toString();
//                String newCookieStr = cookie.toString();
//                String saveValue = mergeCookies(oldCookieStr, newCookieStr);
//                editor.putString(createCookieKey(cookie), new SerializableCookie().encode(Cookie.parse(url, saveValue)));
//            } else {//没有相同key存在。直接存储
//                editor.putString(createCookieKey(cookie), new SerializableCookie().encode(cookie));
//            }
//        }
//        editor.commit();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.putString(createCookieKey(cookie), new SerializableCookie().encode(cookie));
        }
        editor.commit();
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.remove(createCookieKey(cookie));
        }
        editor.commit();
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }

    @Override
    public void clear() {
        sharedPreferences.edit().clear().commit();
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
}
