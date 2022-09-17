package com.kotlin.android.app.data.entity.movie

/**
 *
 * Created on 2022/5/17.
 *
 * @author o.s
 */
data class MovieMore(
    var name: ArrayList<String>? = null, // 中文非主要片名列表
    var nameEn: ArrayList<String>? = null, // 英文文非主要片名列表
    var length: Long? = null, // 通用片长
    var language: String? = null, // 对白语言（多个用/分隔）
)
