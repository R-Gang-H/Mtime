package com.baijiahulian.common.cache.sp;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SharePreferenceUtil {

	private SharedPreferences settings;
	
	public SharePreferenceUtil(Context context) {
		this(context, null);
	}

	// private Editor editor;
	@SuppressWarnings("static-access")
	public SharePreferenceUtil(Context context, String sharePreFileName) {
		if (sharePreFileName == null) {
			sharePreFileName = context.getPackageName();
		}
		settings = context.getSharedPreferences(sharePreFileName, context.MODE_WORLD_WRITEABLE);
		// editor=settings.edit();
	}

	public String getStringValue(String key, String defValue) {
		return settings.getString(key, defValue);
	}

	public boolean getBooleanValue(String key, boolean defValue) {
		return settings.getBoolean(key, defValue);
	}

	public float getFloatValue(String key, float defValue) {
		return settings.getFloat(key, defValue);
	}

	public int getIntValue(String key, int defValue) {
		return settings.getInt(key, defValue);
	}

	public long getLongValue(String key, long defValue) {
		return settings.getLong(key, defValue);
	}

	public boolean putBoolean(String key, boolean value) {
		return settings.edit().putBoolean(key, value).commit();
	}

	public boolean putString(String key, String value) {
		return settings.edit().putString(key, value).commit();
	}

	public boolean putFloat(String key, float value) {
		return settings.edit().putFloat(key, value).commit();
	}

	public boolean putLong(String key, long value) {
		return settings.edit().putLong(key, value).commit();
	}

	public boolean putInt(String key, int value) {
		return settings.edit().putInt(key, value).commit();
	}

	@SuppressWarnings("rawtypes")
	public Map getAll() {
		return settings.getAll();
	}

	public boolean contains(String key) {
		return settings.contains(key);
	}

	public boolean delete(String key) {
		return settings.edit().remove(key).commit();
	}
	
	public boolean clear() {
		return settings.edit().clear().commit();
	}

}
