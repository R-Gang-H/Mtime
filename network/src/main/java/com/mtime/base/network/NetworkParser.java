package com.mtime.base.network;

import androidx.annotation.NonNull;

import com.mtime.base.bean.MParserBean;

import java.lang.reflect.Type;

/**
 * @author ZhouSuQiang
 * @date 2018/10/31
 */
public interface NetworkParser {
    @NonNull <T> MParserBean<T> onParse(String result, Type type);
}
