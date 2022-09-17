package com.kotlin.android.app.data.entity

import com.google.gson.Gson
import com.kotlin.android.app.data.entity.PostInfo.Companion.create


/**
 * batch批量请求接口的请求参数对象封装:
 * 提供了方便的生成方法参见 [create] and [getPostJson]
 *
 * Created on 2020/5/14.
 *
 * @author o.s
 */
data class PostInfo(
    var method: String,
    var host: String,
    var path: String,
    var params: List<ParamInfo>
) {
    data class ParamInfo(
        var key: String,
        var value: String
    )

    companion object {
        const val key = "json"
        /**
         * 默认参数列表中添加 json = true
         */
        fun create(method: String, host: String, path: String, vararg params: Pair<String, String>): PostInfo {
            val list = ArrayList<ParamInfo>()
            var isAdded = false
            params.forEach {
                if (key == it.first) {
                    isAdded = true
                }
                list.add(ParamInfo(it.first, it.second))
            }
            if (!isAdded) {
                list.add(ParamInfo(key, true.toString()))
            }
            return PostInfo(method, host, path, list)
        }

        fun toJson(vararg info: PostInfo): String {
            return toJson(info.toList())
        }

        fun toJson(info: List<PostInfo>): String {
            return Gson().toJson(info)
        }

//        fun testPostJson(): String {
//            return PostInfo.toJson(
//                PostInfo.create(Http.GET, Env.instance.hostMisc, "path", "other" to "xxx"),
//                PostInfo.create(Http.GET, Env.instance.hostMisc, "path", "json" to "true", "other" to "xxx"),
//                PostInfo.create(Http.GET, Env.instance.hostMisc, "path", "json" to "true", "other" to "xxx")
//            )
//        }
    }
}