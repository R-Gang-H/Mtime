package com.kotlin.android.ktx.ext

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 创建者: zl
 * 创建时间: 2020/12/2 3:21 下午
 * 描述:
 */
private val gson = Gson()
fun <T> handleJson(gsonString: String, cls: Class<T>?): T? {
    //传入json对象和对象类型,将json转成对象
    return gson.fromJson(gsonString, cls)
}

fun handleJson(any: Any): String {
    val toJson = gson.toJson(any)
//    Log.e("zl", "toJson $toJson")
    return toJson
}

fun <T> handleToList(jsonString: String, cls: Class<T>?): List<T>? {
    //根据泛型返回解析指定的类型,TypeToken<List<T>>{}.getType()获取返回类型
    if (jsonString.isEmpty()) {
        return null
    }
    return gson.fromJson(jsonString, object : TypeToken<List<T>?>() {}.type)
}

fun <T> handleToMap(jsonString: String): Map<String, T>? {
    return gson.fromJson(jsonString, object : TypeToken<Map<String, T>?>() {}.type)
}

fun <T> handleToListMap(jsonString: String): List<Map<String, T>?>? {
    return gson.fromJson(jsonString,
            object : TypeToken<List<Map<String, T>?>?>() {}.type)
}