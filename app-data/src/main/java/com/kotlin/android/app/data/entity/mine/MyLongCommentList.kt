package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/10/13
 * description:我的长影评列表
 */
data class MyLongCommentList(
    var count: Long = 0L,//总记录数
    var pageCount: Long = 0L,//总页数
    var userCommtentList: MutableList<UserCommtent>? = mutableListOf()//长影评列表
): ProguardRule {
    data class UserCommtent(
            var body: String? = "",
            var commentId: Long = 0L,//评论id
            var img: String? = "",//发表人头像
            var nickName: String? = "",//发表人昵称
            var rating: Double = 0.0,
            var relateImg: String? = "",//关联影片封面图
            var relatedId: Long = 0L,//关联影片id
            var relatedName: String? = "",//关联影片id
            var time: Long = 0L,//评论时间(时间戳，单位秒)
            var title: String? = ""//影评标题
    ): ProguardRule
}

