package com.kotlin.android.app.data.entity.community.publish

/**
 * 推荐分类：
 *
 * 推荐类型：7种草内容推荐，9找家族推荐，10片单推荐
 *
 * Created on 2022/4/18.
 *
 * @author o.s
 */
data class RecommendType(
    var subTypeId: Long? = null, // 推荐分类Id
    var name: String? = null, // 推荐分类名称
)
