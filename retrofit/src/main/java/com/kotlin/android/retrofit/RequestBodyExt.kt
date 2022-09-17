package com.kotlin.android.retrofit

import android.os.Bundle
import android.os.Parcelable
import androidx.collection.ArrayMap
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

/**
 * create by lushan on 2020/9/1
 * description:获取requestBody
 */


/**
 * 获取requestBody
 */
fun String.jsonToRequestBody(): RequestBody {
    return this.toRequestBody("application/json; charset=UTF-8".toMediaType())
}

/**
 * 获取requestBody
 */
fun JSONObject.toRequestBody(): RequestBody {
    return this.toString().jsonToRequestBody()
}

/**
 * 获取RequestBody对象
 *
 */
fun getRequestBody(params:ArrayMap<String,Any>): RequestBody {
    return Gson().toJson(params).jsonToRequestBody()
}

/**
 * HashMap转化成RequestBody
 */
fun ArrayMap<String,Any>.toRequestBody():RequestBody{
    return getRequestBody(this)
}

fun Any.toRequestBody(): RequestBody {
    return Gson().toJson(this).jsonToRequestBody()
}