package com.kotlin.android.api.converter

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * 转换工厂：响应体数据为空时，返回null
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class NullOnEmptyConverterFactory : Converter.Factory() {

    companion object {
        fun create(): NullOnEmptyConverterFactory {
            return NullOnEmptyConverterFactory()
        }
    }

    override fun responseBodyConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        val delegate: Converter<ResponseBody, Any?> =
                retrofit.nextResponseBodyConverter(this, type, annotations)
        return Converter<ResponseBody, Any?> { body ->
            if (body.contentLength() == 0L) {
                null
            } else {
                delegate.convert(body)
            }
        }
    }
}