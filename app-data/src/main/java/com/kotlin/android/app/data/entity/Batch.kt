package com.kotlin.android.app.data.entity

/**
 * 批量请求接口的响应结果集对象
 *
 * Created on 2020/5/14.
 *
 * @author o.s
 */
data class Batch(
    var result: List<Result>
) {
    data class Result(
        var host: String,
        var path: String,
        var data: String?
    )
}