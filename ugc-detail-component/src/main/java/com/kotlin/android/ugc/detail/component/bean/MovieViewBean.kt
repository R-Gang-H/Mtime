package com.kotlin.android.ugc.detail.component.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_LIKE
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_PRESCALE

/**
 * UGC-影片卡片
 */
data class MovieViewBean(var movieId: Long = 0L,//电影ID
                         var nameCn: String = "",//中文名称
                         var nameEn: String = "",//英文名称
                         var picUrl: String = "",//影片封面
                         var mTimeScore: String = "",//时光评分
                         var duration: String = "",//影片时长
                         var movieType: String = "",//影片制式
                         var releaseDate: String = "",//影片上映日期
                         var movieStatus: Long = 0L,//影片状态，预售还是可购票
                         var isLike: Boolean = false,//是否想看
                         var ugcType: Int = 0//UGC类型，类型不同，背景色不同 1影评、2图集
) : ProguardRule {

    fun getWannTextColor(): Int {
        return if (ugcType == 1 && movieStatus != MOVIE_STATE_PRESCALE) {
            getColor(R.color.color_20a0da)
        }else {
            getColor(R.color.color_ffffff)
        }
    }

    /**
     * 是否展示评分
     */
    fun isShowScore(): Boolean {
        val score = mTimeScore.trim()
        if (TextUtils.isEmpty(score)) {
            return false
        }
        return try {
            score.toDouble() > 0.0
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }

    }

    fun getDurationAndMovieType(): String {
        return if (TextUtils.isEmpty(duration)) {
            movieType
        } else if (TextUtils.isEmpty(movieType)) {
            duration
        } else {
            "$duration-$movieType"
        }
    }

    //    是否是想看电影
    fun isWanna(): Boolean = movieStatus == MOVIE_STATE_LIKE
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieViewBean

        if (movieId != other.movieId) return false
        if (nameCn != other.nameCn) return false
        if (nameEn != other.nameEn) return false
        if (picUrl != other.picUrl) return false
        if (mTimeScore != other.mTimeScore) return false
        if (duration != other.duration) return false
        if (movieType != other.movieType) return false
        if (releaseDate != other.releaseDate) return false
        if (movieStatus != other.movieStatus) return false
        if (isLike != other.isLike) return false
        if (ugcType != other.ugcType) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieId.hashCode()
        result = 31 * result + nameCn.hashCode()
        result = 31 * result + nameEn.hashCode()
        result = 31 * result + picUrl.hashCode()
        result = 31 * result + mTimeScore.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + movieType.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + movieStatus.hashCode()
        result = 31 * result + isLike.hashCode()
        result = 31 * result + ugcType
        return result
    }


}