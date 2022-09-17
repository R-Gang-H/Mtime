package com.kotlin.android.api.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * API转换工厂
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class ApiConverterFactory private constructor(val gson: Gson) : Converter.Factory() {

    companion object {
        fun create(gson: Gson = Gson()): ApiConverterFactory {
            return ApiConverterFactory(gson)
        }
    }

    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return ApiResponseBodyConverter(gson, adapter,type)
    }

    override fun requestBodyConverter(
            type: Type,
            parameterAnnotations: Array<Annotation>,
            methodAnnotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return ApiRequestBodyConverter(gson, adapter)
    }

}