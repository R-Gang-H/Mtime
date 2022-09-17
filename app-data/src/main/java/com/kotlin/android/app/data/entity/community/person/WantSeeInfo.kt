package com.kotlin.android.app.data.entity.community.person

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.mine.CollectionMovie

data class WantSeeInfo(
    var count: Long = 0L,
    var pageSize: Long = 0L,
    var nextStamp: String = "",
    var hasNext: Boolean = false,
    var items: List<Movie> = arrayListOf()
) : ProguardRule {
    data class Movie(
        var year: String? = "",//出品年代
        var releaseDate: String? = "",//上映日期
        var earliestReleaseDate: String? = "",//上映日期
        var minutes: String? = "",//时长:分钟
        var rating: String?,//时光评分
        var nameEn: String? = "",//电影英文名
        var imgUrl: String? = "",//图片url
        var playState: Long = 1,//电影播放按钮状态（1选座购票 2观看全片 3预售 4暂无排片)
        var play: Int = 0,// 电影正片播放 0:不可播放1:可播放，此状态不与购票按钮枚举值互斥，有情景出现可购票\预售，但也有正片播放
        var genreTypes: String? = "",//类型
        var name: String? = "",//电影中文名
        var id: Long,//电影ID
        var releaseArea: String?,//上映地区
        var hasTicket: Boolean,//是否有在线购票
        var mainDirectors: ArrayList<CollectionMovie.Person> = arrayListOf(),//主要导演列表
        var mainActors: ArrayList<CollectionMovie.Person> = arrayListOf(),//主要演员列表
        var enterTime: CollectionMovie.CollectionTime,//记录时间

        //看过
        var fcRating: String,//影评发表者评分
        var shortInteractiveObj: Content?,//交互相关 长影评、影评评分等内容，用户维度最新相关数据
        var longInteractiveObj: Content?,//交互相关 短影评、影评评分等内容，用户维度最新相关数据

        //时光轴使用
        var attitude: Long = 1L,//按钮显示，1:购票2:想看3:看过
        var showDate: Boolean = false,//是否显示时间（是否显示字段：enterTime）true显示，false不显示
        var timeLineId: Long = 0L,//时间轴id
    ) : ProguardRule

    data class Content(
        var contentId: Long = 0L,//内容ID
        var type: Long = 0L,//内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
        var title: String? = "",//标题
        var mixWord: String? = "",//混合文字 优先级按照摘要、富文本文字截取
        var fcType: Long = 0L,//影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
    )
}
