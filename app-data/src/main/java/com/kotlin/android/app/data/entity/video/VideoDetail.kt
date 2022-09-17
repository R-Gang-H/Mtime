package com.kotlin.android.app.data.entity.video

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * create by lushan on 2020/9/2
 * description:视频详情
 */
data class VideoDetail(
    var commentTotal: Long = 0L,//评论数
    var image: String? = "",//视频封面图
    var isAllowComment: Boolean = false,//是否允许评论
    var length: Long = 0L,//播放时长（单位秒）
    var playCount: String? = "",//播放次数字符串（之前需求要求隐藏）
    var pulishTime: Long = 0L,//发布时间（时间戳，单位秒）
    var relatedMovie: RelatedMovie? = null,
    var title: String? = "",//视频名称
    var type: Long = 0L,//视频类型：0预告片 1精彩片段 2拍摄花絮 3影人访谈 4电影首映 5MV 7剧集正片
    var vId:Long = 0L,//视频id
    var videoSource: Long = 0L//视频来源：1预告片 2自媒体 3媒资视频
): ProguardRule,Serializable{
    data class RelatedMovie(
            var broadcastDes: String?,//播出情况备注
            var buyTicketStatus: Long?,//购票状态：1正常购票 2预售 3不可购票
            var dramaList: List<Video>?,//剧集正片视频列表
            var episodeCnt: String?,//电视剧总集数
            var img: String?,//常规封面图（1280*720）
            var isFilter: Boolean,//是否需要恐怖海报过滤
            var isPlay: String?,//是否可以在线播放：1是 2否
            var isSensitive: Boolean,//是否敏感电影
            var movieId: Long = 0L,//影片id
            var movieType: String?,//影片类型
            var name: String?,//电影中文名
            var nameEn: String?,//电影英文名
            var releaseDate: String?,//上映日期（yyyy年M月d日）
            var sideList: List<Video>?,//花絮视频列表
            var summary: String?,//影片概要
            var timeLength: String?,//片长（XX分钟）
            var year: String?,//年代
            var attitude:Long? = -1L//当前用户对电影的态度：-1未表态 0看过 1想看
    ): ProguardRule,Serializable

    data class Video(
            var vId: Long,//视频Id
            var vImg: String?,//视频封面图
            var vName: String?//视频名称
    ): ProguardRule,Serializable


}

