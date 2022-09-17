package com.kotlin.android.api.converter

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import retrofit2.Converter
import java.io.OutputStreamWriter
import java.nio.charset.Charset

/**
 * API请求体转换
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class ApiRequestBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
) : Converter<T, RequestBody> {
    private val MEDIA_TYPE = "application/json; charset=UTF-8".toMediaType()
    private val UTF_8 = Charset.forName("UTF-8")

    override fun convert(value: T): RequestBody? {
        val buffer = Buffer()
        val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
        val jsonWriter = gson.newJsonWriter(writer)
        adapter.write(jsonWriter, value)
        return buffer.readByteString().toRequestBody(MEDIA_TYPE)
    }

}