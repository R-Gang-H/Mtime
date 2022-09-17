package com.kotlin.android.app.data.entity.live

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2021/3/5
 * description: 直播详情
 */
data class LiveDetail(
    var appointDesc: String? = "",//直播前字段，预约文案展示    当前有0人预约
    var appointed: Boolean = false,//是否已预约
    var appointCount:Long = 0L,//预约人数
    var appointedImage: String? = "",//预约背景图
    var description: String? = "",//直播间描述
    var footer: String? = "",//背景图底部
    var header: String? = "",//背景图顶部
    var imToken: String? = "",//信令TOKEN
    var image: String? = "",//直播间配图
    var liveId: Long = 0L,//直播id
    var liveStatus: Long = 0L,//直播状态 1=预告 2=直播 3=稍后回看 4=回看
    var middleColor: String? = "",//背景中间颜色，格式：1A4D57
    var movie: Movie? = null, //关联影片信息
    var movieIsShow: Long = 0L,//关联电影是否显示  1-显示，2-不显示
    var onlineCount: Long = 0L,//直播中字段，在线人数
    var roomNum: String? = "",//直播中字段，直播房间号=聊天室ID
    var easeMobRoomNum: String? = "",//2021年新版聊天室，环信直播房间号
    var shareImage: String? = "",//直播间分享H5大底图
    var shareUrl: String? = "",//直播间分享缩略图
    var startTime: Long = 0L,//单位秒
    var subjectHeight: String? = "", //主体高度，单位px
    var tabIndex: Long = 0L,//默认TAB页。这个字段应该废弃，默认TAB应该永远都是评论聊天 待确认
    var tabList: MutableList<Tab>? = null,//TAB页列表。API端写死就返回两个元素
    var ticketStatus: Long = 0L,//是否支持购票 0不支持购票 1购票  2预售
    var title: String? = "",//直播间标题
    var video: MutableList<Video>? = null////关联预告片视频
): ProguardRule {
    data class Movie(
            var img: String? = "",
            var mins: String? = "",
            var movieId: Long = 0L,
            var name: String? = "",
            var nameEn: String? = "",
            var overallRating: Float = 0f,
            var releaseArea: String? = "",
            var releaseDate: String? = "",
            var story: String? = "",
            var type: MutableList<String>? = null,
            var wantCount: Long = 0
    ): ProguardRule

    data class Tab(
            var tabId: Long = 0L,
            var tabName: String? = "",
            var tabUrl: String? = ""
    ): ProguardRule

    data class Video(
            var hightUrl: String? = "",
            var img: String? = "",
            var length: Long = 0L,
            var title: String? = "",
            var url: String? = "",
            var videoId: Long = 0L,
            var source:Long = 0L,//视频来源

            // 自定义字段
            var isSelect: Boolean = false  // 是否当前选中
    ): ProguardRule
}

