package com.mtime.base.network;

/**
 * Created by LiJiaZhi on 17/3/24.
 * 过滤器
 */
public interface Filter<T> {

    boolean onFilter(T t, String message);
}

