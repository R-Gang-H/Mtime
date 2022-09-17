package com.kotlin.android.community.ui.person.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.person.WantSeeInfo
import com.kotlin.android.app.data.entity.mine.CollectionMovie
import com.kotlin.android.community.R
import com.kotlin.android.community.ui.person.PERSON_TYPE_HAS_SEEN
import com.kotlin.android.community.ui.person.PERSON_TYPE_WANT_SEE
import com.kotlin.android.community.ui.person.binder.CommunityTimeLineBinder
import com.kotlin.android.community.ui.person.binder.CommunityWantSeeBinder
import com.kotlin.android.ktx.ext.date.getCurrentYear
import com.kotlin.android.ktx.ext.date.monthBig
import com.kotlin.android.ktx.ext.date.toStringWithDateFormat
import com.kotlin.android.mtime.ktx.getRatingLevelHintText
import com.kotlin.android.mtime.ktx.getString
import java.util.*

/**
 *  想看、看过
 */
data class WantSeeViewBean(
    var type: Long = 0L,//类型 0是想看 1是看过
    var count: Long = 0,//总数
    var movieId: Long = 0L,//电影id
    var movieName: String? = "",//影片名称
    var movieNameEn: String? = "",//影片名称
    var moviePic: String? = "",//影片海报
    var director: String? = "",//导演
    var actors: String? = "",//演员
    var releaseContent: String? = "",//上映信息
    var earliestReleaseDate: String? = "",//时间
    var rating: String = "",//时光电影评分
    var ratingFinal: String = "",//我的评分
    var canPlay: Boolean = false,//可播放
    var hasTicket: Boolean = false,//是否有在线购票
    var playState: Long = 1L,//电影播放按钮状态（1选座购票 2观看全片 3预售 4暂无排片）
    var shortInteractiveObj: WantSeeInfo.Content? = null, //交互相关 长影评、影评评分等内容，用户维度最新相关数据
    var longInteractiveObj: WantSeeInfo.Content? = null,//交互相关 短影评、影评评分等内容，用户维度最新相关数据

    var attitude: Long = 1L,//按钮显示，1:购票2:想看3:看过
    var showDate: Boolean = false,//是否显示时间（是否显示字段：enterTime）true显示，false不显示
    var timeLineId: Long = 0L,//时间轴id

    var isFirstItem: Boolean = false,//整体第一条
    var isFirstYearMonthItem: Boolean = false,//是否是第一条 年、月第一条
    var year: String? = "",//2022
    var month: String = "",//十二月
    var day: String = "",//12号
    var time: String = "",//10：00
    var yearMonth: String = "",//2022 /n 十二月
    var dayTime: String = ""//12号 10：00

) : ProguardRule {

    fun getRatingHintText(): String {
        if (!TextUtils.isEmpty(ratingFinal)) {
            return getRatingLevelHintText(ratingFinal.toFloat().toInt())
        }
        return ""
    }

    fun hasScored(): Boolean {
        return type == PERSON_TYPE_HAS_SEEN && ratingFinal.isNotEmpty()
    }

    /**
     * 判断按钮显示 看过
     */
    fun isShowBtn(): Boolean {
        return type == PERSON_TYPE_HAS_SEEN && ratingFinal.isEmpty()
    }

    /**
     * 我评分 看过
     */
    fun isShowMyScore(): Boolean {
        return type == PERSON_TYPE_HAS_SEEN && ratingFinal.isNotEmpty()
    }

    /**
     * 官方评分
     */
    fun isShowScore(): Boolean {
        return /*type == PERSON_TYPE_WANT_SEE &&*/ rating.isNotEmpty()
    }

    companion object {
        fun convertToWantSeeBinder(
            movie: WantSeeInfo.Movie,
            count: Long,
            type: Long
        ): CommunityWantSeeBinder {
            var bean = WantSeeViewBean()
            bean.type = type
            bean.count = count
            bean.movieId = movie.id
            if (type == PERSON_TYPE_WANT_SEE) {//想看
                bean.actors = ""
                bean.director = movie.genreTypes
                bean.releaseContent = movie.releaseArea
            } else {// 看过
                if (movie.mainActors.isNullOrEmpty()) {
                    bean.actors = ""
                } else {
                    bean.actors = getString(R.string.actor)
                }

                movie.mainActors?.apply {
                    var actors = ""
                    this.forEachIndexed { index: Int, it: CollectionMovie.Person ->
                        actors = if (index == 0)
                            actors.plus(
                                if (it.personNameCn?.isEmpty() == true)
                                    it.personNameEn else it.personNameCn
                            )
                        else actors.plus("/ ")
                            .plus(
                                if (it.personNameCn?.isEmpty() == true)
                                    it.personNameEn else it.personNameCn
                            )
                    }
                    bean.actors = bean.actors.plus(actors)
                }
                if (movie.mainDirectors.isNullOrEmpty()) {
                    bean.director = ""
                } else {
                    bean.director = getString(R.string.director)
                }

                movie.mainDirectors?.apply {
                    var directors = ""
                    this.forEachIndexed { index: Int, it: CollectionMovie.Person ->
                        directors = if (index == 0)
                            directors.plus(
                                if (it.personNameCn?.isEmpty() == true)
                                    it.personNameEn else it.personNameCn
                            )
                        else directors.plus("/ ")
                            .plus(
                                if (it.personNameCn?.isEmpty() == true)
                                    it.personNameEn else it.personNameCn
                            )
                    }
                    bean.director = bean.director.plus(directors)
                }
                bean.releaseContent = getString(R.string.realtime).plus(
                    movie.earliestReleaseDate
                        ?: ""
                ).plus(" ")
                    .plus(if (movie.releaseArea?.isEmpty() == true) "" else "(" + movie.releaseArea + ")")
            }

            if (!movie.name.isNullOrEmpty()) {
                bean.movieName = movie.name
            } /*else if (!movie.name.isNullOrEmpty()) {
                bean.movieName = movie.name
            } else bean.movieName = movie.nameEn*/
            bean.movieNameEn = movie.nameEn
            bean.year = movie.year
            bean.moviePic = movie.imgUrl
            bean.rating = movie.rating.orEmpty()
//            bean.ratingFinal = if (movie.ratingFinal > 0.0) movie.ratingFinal.toString() else ""
            bean.ratingFinal = movie.fcRating.orEmpty()
            movie.longInteractiveObj?.apply {
                bean.longInteractiveObj =
                    WantSeeInfo.Content(contentId, type, title.orEmpty(), mixWord.orEmpty(), fcType)
            }
            movie.shortInteractiveObj?.apply {
                bean.shortInteractiveObj =
                    WantSeeInfo.Content(contentId, type, title.orEmpty(), mixWord.orEmpty(), fcType)
            }

            bean.playState = movie.playState
            bean.canPlay = movie.play == 1
            bean.earliestReleaseDate = movie.earliestReleaseDate
            bean.hasTicket = movie.hasTicket
            return CommunityWantSeeBinder(bean)
        }

        //时光轴
        fun convertToTimeLineBinder(
            movie: WantSeeInfo.Movie,
            count: Long,
            type: Long = 0L,
            isFirstItem: Boolean = false
        ): CommunityTimeLineBinder {
            var bean = WantSeeViewBean()
            bean.type = type
            bean.count = count
            bean.movieId = movie.id

            bean.actors = movie.genreTypes
            bean.director = ""
            bean.releaseContent = movie.releaseArea

            if (!movie.name.isNullOrEmpty()) {
                bean.movieName = movie.name
            }
            bean.movieNameEn = movie.nameEn
            bean.moviePic = movie.imgUrl
            bean.rating = movie.rating.orEmpty()
//            bean.ratingFinal = if (movie.ratingFinal > 0.0) movie.ratingFinal.toString() else ""
            bean.ratingFinal = movie.fcRating.orEmpty()
            movie.longInteractiveObj?.apply {
                bean.longInteractiveObj =
                    WantSeeInfo.Content(contentId, type, title, mixWord, fcType)
            }
            movie.shortInteractiveObj?.apply {
                bean.shortInteractiveObj =
                    WantSeeInfo.Content(contentId, type, title, mixWord, fcType)
            }

            bean.playState = movie.playState
            bean.canPlay = movie.play == 1

            bean.attitude = movie.attitude
            bean.showDate = movie.showDate
            bean.timeLineId = movie.timeLineId
            bean.year = movie.year
            bean.isFirstItem = isFirstItem
            bean.month = Date(movie.enterTime.stamp).monthBig + "月"

            var isCurrentYear = false
            var currentYear = getCurrentYear()
            var year = movie.enterTime.stamp.toStringWithDateFormat("yyyy")
            if (currentYear.toString() == year) {
                isCurrentYear = true
            }
            bean.yearMonth = if (isCurrentYear) bean.month else "$bean.year\n${bean.month}"

            bean.day = movie.enterTime.stamp.toStringWithDateFormat("d号")
            bean.time = movie.enterTime.stamp.toStringWithDateFormat("HH:mm")
            bean.dayTime = "${bean.day} ${bean.time}"

            return CommunityTimeLineBinder(bean)
        }
    }

}


