package com.baijiahulian.common.cache.modelcache;

import java.util.List;

import android.content.Context;

import com.baijiahulian.common.cache.sp.SharePreferenceUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LightModelCache extends SharePreferenceUtil implements IModelCache {

	private Gson mGson = null;
	public LightModelCache(Context context, String cacheFilename) {
		super(context, cacheFilename);
	}

	public LightModelCache(Context context) {
		super(context);
	}
	
	public void setGson(Gson gson) {
		this.mGson = gson;
	}
	
	@Override
	public <T extends IBaseCacheModel> boolean putModel(String key, T model) {
		if (model == null || key == null) return false;
		String value = this.mGson.toJson(model);

		return putString(key, value);
	}
	
	@Override
	public <T extends IBaseCacheModel> boolean putModelList(String key, List<T> modelList) {
		if (modelList == null || key == null) return false;
		String value = this.mGson.toJson(modelList);

		return putString(key, value);
	}
	
	@Override
	public <T extends IBaseCacheModel> T getModel(String key, Class<T> clazz) {
		if (key == null) throw new NullPointerException("key must not null.");
		String value = getStringValue(key, null);
		if (value == null) return null;
		return this.mGson.fromJson(value, clazz);
	}
	
	@Override
	public <T extends IBaseCacheModel> List<T> getModelList(String key, TypeToken<List<T>> typeToken) {
		if (key == null) throw new NullPointerException("key must not null.");
		String value = getStringValue(key, null);
		if (value == null) return null;
		
		List<T> list = this.mGson.fromJson(value, typeToken.getType());
		return list;
	}

	@Override
	public boolean removeModel(String key) {
		if (key == null) return false;
		return super.delete(key);
	}

}
