package com.kotlin.android.app.data.entity.search

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 联合搜索/search/unionSearch
 * 影片
 *
 * Created on 2020/10/15.
 *
 * @author o.s
 */
data class Movie(
        val actors: List<String>? = null, // 演员
        val actorObjs: List<Actor>? = null, // 演员
        val canPlay: Long? = null,  // 是否可播放 0不可播放 1可播放
        val directors: List<String>? = null, // 导演
        val directorObjs: List<Director>? = null, // 导演
        val href: String? = null, // 链接
        val img: String? = null, // 影片封面图
        val isFilter: Boolean? = null, // 是否是恐怖电影海报过滤。
        val isTicket: Boolean? = null, // 地区影片是否能够票。使用该字段必须传城市ID参数。
        val isVideo: Boolean? = null, // 是否有预告片
        val locationName: String? = null, // 影片产地
        val movieId: Long? = null, // 影片Id
        val movieType: String? = null, // 影片类型 "惊悚/剧情"
        val name: String? = null, // 中文名称
        val nameEn: String? = null, // 英文名
        val rDay: Long? = null, // 上映日
        val rLocation: String? = null, // 临时改成影片产地数据，等最新版上线，换成上映地区数据
        val rMonth: Long? = null, // 上映月份
        val rYear: Long? = null, // 上映年份
        val rating: Double? = null, // 加权最终分
        val realTime: String? = null, // 上映日期 "2013年4月16日"
        val releaseStatus: Long? = null, // 地区影片的上映状态 0表示下线、1表示正在热映、 2 表示即将上映。 使用该字段必须传城市ID参数。
        val titleOthersCn: List<String>? = null, // 影片更多中文名称
        val year: String? = null, // 年代
        val length: Long? = null, // 影片时长（分钟）
        // 二期新增
        val saleType: Long? = 0L,   // 是否可售票 0不可售票 1可预售 2可售票
        var isAdd: Boolean = false, //片单中 是否添加标识
        val content: String? = null, // 简介
        val summary: String? = null, // 简介
        val id: Long? = null, // 影片Id
) : ProguardRule, Serializable