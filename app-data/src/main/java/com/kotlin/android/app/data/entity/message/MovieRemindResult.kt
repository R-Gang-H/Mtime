package com.kotlin.android.app.data.entity.message

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by zhaoninglongfei on 2022/3/18
 * 电影上映通知 列表返回
 */
data class MovieRemindResult(
    var nextStamp: String? = null,
    var pageSize: Long? = 0L,
    var items: List<MovieRemind>? = null,
    var hasNext: Boolean? = null//是否有下一页
) : ProguardRule {

    data class MovieRemind(
        var movieId: Long? = null,//电影id
        var titleCn: String? = null,//电影中文名称
        var titleEn: String? = null,//电影英文名称
        var year: Long? = null,//电影发行时间
        var type: List<String>? = null,//电影类型
        var logoPath: String? = null,//电影封面
        var location: List<String>? = null,//电影发行地区
        var rating: String? = null,//电影评分
        var noticeTime: Long? = null,//电影提醒时间，时间戳ms
        var unRead: Boolean? = null,//是否未读
    ) : ProguardRule
}