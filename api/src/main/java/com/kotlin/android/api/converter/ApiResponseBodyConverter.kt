package com.kotlin.android.api.converter

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.TypeAdapter
import com.kotlin.android.api.ApiCode
import com.kotlin.android.api.ApiResponse
import com.kotlin.android.api.key.ApiKey
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.extractMessage
import com.kotlin.android.ktx.ext.getTestApiContent
import okhttp3.ResponseBody
import retrofit2.Converter
import java.lang.reflect.Type

/**
 * API响应体转换
 *
 * Created on 2020/5/7.
 *
 * @author o.s
 */
class ApiResponseBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>,
        private val type: Type
) : Converter<ResponseBody, T> {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun convert(value: ResponseBody): T? {
        if (CoreApp.testFlag) {
            // TODO: 2020/8/31 本地测试json数据
            val extractMessage = extractMessage(type.typeName)
            val testApi = getTestApiContent(CoreApp.testApiContents, extractMessage) ?: ""
            return adapter.fromJson(testApi)
        } else {
            value.use {
                var nullFlag = false
                var code = ApiCode.RESULT_SERVER_ERROR
                var msg: String? = null
                val json = it.string()
                val element = JsonParser.parseString(json)
                if (element?.isJsonObject == true) {
                    val obj = element.asJsonObject
                    code = obj.get(ApiKey.CODE)?.asInt ?: ApiCode.RESULT_SERVER_ERROR
                    msg = obj.get(ApiKey.MSG)?.asString
                    val data = obj.get(ApiKey.DATA)?.apply {
                        if (this.isJsonNull) {
                            nullFlag = true
                        } else if (isJsonPrimitive) {
                            if (asString == "") {
                                nullFlag = true
                            }
                        } else if (isJsonObject) {
                            val dataObj = asJsonObject
                            if (dataObj?.keySet()?.size ?: 0 == 0) {
                                nullFlag = true
                            }
                        } else if (isJsonArray) {
                            val dataArray = asJsonArray
                            if (dataArray?.size() ?: 0 == 0) {
                                nullFlag = true
                            }
                        }
                    }
                    if (data == null) {
                        nullFlag = true
                    }
                } else {
                    nullFlag = true
                }
                return if (nullFlag) {
                    ApiResponse(code, msg, null) as T
                } else {
                    adapter.fromJson(json)
                }
            }
        }
    }

//    override fun convert(value: ResponseBody): T? {
//        val jsonReader = gson.newJsonReader(value.charStream())
//        try {
//            val result = adapter.read(jsonReader)
//            if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
//                throw JsonIOException("JSON document was not fully consumed.")
//            }
//            return result
//        } finally {
//            value.close()
//        }
//    }

}