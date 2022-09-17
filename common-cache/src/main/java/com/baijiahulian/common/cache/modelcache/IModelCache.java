package com.baijiahulian.common.cache.modelcache;

import java.util.List;

import com.google.gson.reflect.TypeToken;

/**
 * Model cache模块
 * @author yanglei
 *
 */
public interface IModelCache {
	
	public <T extends IBaseCacheModel> boolean putModel(String key, T model);
	
	public boolean contains(String key);
	
	public <T extends IBaseCacheModel> T getModel(String key, Class<T> clazz);
	
	public <T extends IBaseCacheModel> boolean putModelList(String key, List<T> modelList);
	
	public <T extends IBaseCacheModel> List<T> getModelList(String key, TypeToken<List<T>> typeToken);
	
	public boolean removeModel(String key);
}
