package com.mtime.base.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LiJiaZhi on 17/3/21.
 *
 * SharePreference 通用类
 */

public class MSharePreferenceUtils {
    private final SharedPreferences settings;

    public MSharePreferenceUtils(Context context) {
        this(context, null);
    }

    public MSharePreferenceUtils(Context context, String sharePreFileName) {
        if (sharePreFileName == null) {
            sharePreFileName = context.getPackageName();
        }

        this.settings = context.getSharedPreferences(sharePreFileName, MODE_PRIVATE);
    }

    public String getStringValue(String key, String defValue) {
        return this.settings.getString(key, defValue);
    }

    public boolean getBooleanValue(String key, boolean defValue) {
        return this.settings.getBoolean(key, defValue);
    }

    public float getFloatValue(String key, float defValue) {
        return this.settings.getFloat(key, defValue);
    }

    public int getIntValue(String key, int defValue) {
        return this.settings.getInt(key, defValue);
    }

    public long getLongValue(String key, long defValue) {
        return this.settings.getLong(key, defValue);
    }

    public boolean putBoolean(String key, boolean value) {
        return this.settings.edit().putBoolean(key, value).commit();
    }

    public boolean putString(String key, String value) {
        return this.settings.edit().putString(key, value).commit();
    }

    public boolean putFloat(String key, float value) {
        return this.settings.edit().putFloat(key, value).commit();
    }

    public boolean putLong(String key, long value) {
        return this.settings.edit().putLong(key, value).commit();
    }

    public boolean putInt(String key, int value) {
        return this.settings.edit().putInt(key, value).commit();
    }

    public Map getAll() {
        return this.settings.getAll();
    }

    public boolean contains(String key) {
        return this.settings.contains(key);
    }

    public boolean delete(String key) {
        return this.settings.edit().remove(key).commit();
    }

    public boolean clear() {
        return this.settings.edit().clear().commit();
    }
}
