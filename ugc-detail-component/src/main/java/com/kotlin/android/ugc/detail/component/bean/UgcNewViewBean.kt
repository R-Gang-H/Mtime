package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule


/**
 * Created by lushan on 2020/8/5
 * 文章详情下方相关新闻推荐
 */
data class UgcNewViewBean(
        var newId: Long = 0L,//新闻ID
        var newsTitle: String = "",//新闻标题
        var newsPic: String = "",//推荐新闻地址
        var userName: String = "",//用户名称
        var userHeaderPic: String = "",//用户头像
        var userId: Long = 0L//用户ID
) : ProguardRule