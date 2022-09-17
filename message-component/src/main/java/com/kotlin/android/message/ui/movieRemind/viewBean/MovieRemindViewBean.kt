package com.kotlin.android.message.ui.movieRemind.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.MovieRemindResult
import com.kotlin.android.message.R
import com.kotlin.android.message.ui.movieRemind.binder.ItemMovieRemindBinder
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getString

/**
 * Created by zhaoninglongfei on 2022/3/23
 *
 */
data class MovieRemindViewBean(
    var movieId: Long? = null,//电影id
    var isUnread: Boolean? = false,
    var notifyTips: String? = null,//提醒文案
    var notifyDate: String? = null,//提醒日期
    var movieImg: String? = null,
    var movieName: String? = null,
    var movieYear: String? = null,
    var movieRate: String? = null,//评分
    var movieType: String? = null,
    var movieCountry: String? = null
) : ProguardRule {
    companion object {
        fun convertToItemMovieRemindBinders(result: MovieRemindResult): List<ItemMovieRemindBinder> {
            val returnList: ArrayList<ItemMovieRemindBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = MovieRemindViewBean(
                    movieId = it.movieId,
                    isUnread = it.unRead,
                    notifyTips = generateNotifyTips(it),//提醒文案
                    notifyDate = formatPublishTime(it.noticeTime ?: 0L),//提醒日期
                    movieImg = it.logoPath,
                    movieName = if (it.titleCn.isNullOrEmpty()) {
                        it.titleEn
                    } else {
                        it.titleCn
                    },
                    movieYear = "${it.titleEn}(${it.year})",
                    movieRate = it.rating,
                    movieType = generateMovieType(it),
                    movieCountry = generateMovieCounty(it)
                )
                returnList.add(ItemMovieRemindBinder(viewBean))
            }

            return returnList
        }

        //生成电影上映国家
        private fun generateMovieCounty(it: MovieRemindResult.MovieRemind): String? {
            val stringBuilder = StringBuilder()
            if (it.location.isNullOrEmpty()) {
                return null
            }
            for (index in 0 until it.location!!.size) {
                stringBuilder.append(it.location!![index])
                if (index != it.location!!.size - 1) {
                    stringBuilder.append(" / ")
                }
            }

            return stringBuilder.toString()
        }

        //生成电影类型
        private fun generateMovieType(it: MovieRemindResult.MovieRemind): String? {
            val stringBuilder = StringBuilder()
            if (it.type.isNullOrEmpty()) {
                return null
            }
            for (index in 0 until it.type!!.size) {
                stringBuilder.append(it.type!![index])
                if (index != it.type!!.size - 1) {
                    stringBuilder.append(" / ")
                }
            }

            return stringBuilder.toString()
        }

        //生成提醒文案
        private fun generateNotifyTips(it: MovieRemindResult.MovieRemind): String? {
            return if (it.titleCn == null && it.titleEn == null) {
                null
            } else {
                getString(R.string.message_movie_notify).format(
                    if (it.titleCn.isNullOrEmpty()) {
                        it.titleEn
                    } else {
                        it.titleCn
                    }
                )
            }
        }
    }
}
