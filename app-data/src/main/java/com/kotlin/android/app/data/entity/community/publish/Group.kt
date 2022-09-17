package com.kotlin.android.app.data.entity.community.publish

/**
 * 种草家族
 *
 * Created on 2022/4/18.
 *
 * @author o.s
 */
data class Group(
    var groupId: Long? = null, // 家族Id
    var name: String? = null, // 家族名称
    var logoPath: String? = null, // Logo地址
    var memberCount: Long? = null, // 成员数
    var postCount: Long? = null, // 帖子数
)
