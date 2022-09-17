package com.kotlin.android.ugc.detail.component.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.binderadapter.MOVIE_STATE_LIKE

/**
 * create by lushan on 2022/3/14
 * des:关联影片
 **/
data class LinkMovieViewBean(
    var movieId: Long = 0L,//影片id
    var img: String = "",//电影海报
    var movieName: String = "",//电影名称
    var movieNameEn: String = "",//电影英文名称
    var score: String = "",//时光评分
    var duration: String = "",//影片时长
    var type:String = "",//影片类型
    var publishDate: String = "",//上映日期
    var publishLocation: String = "",//上映国家地区
    var movieStatus: Long = 0L//影片状态，预售还是可购票
) : ProguardRule {

    fun isWanna() = movieStatus == MOVIE_STATE_LIKE
    companion object{
        fun buildLinkMovie(bean: CommunityContent.RoMovie):LinkMovieViewBean{
            return LinkMovieViewBean(
                movieId = bean.id,
                img = bean.imgUrl.orEmpty(),
                movieName = bean.name.orEmpty(),
                movieNameEn = bean.nameEn.orEmpty(),
                score = bean.rating.orEmpty(),
                duration = bean.minutes.orEmpty(),
                type = bean.genreTypes.orEmpty(),
                publishDate = bean.releaseDate.orEmpty(),
                publishLocation = bean.releaseArea.orEmpty(),
                movieStatus = bean.btnShow.orZero()
            )
        }
    }

    fun showScore() = score.isNotEmpty()

    fun getMovieNameStr() = if (TextUtils.isEmpty(movieName).not()) movieName else movieNameEn

    fun getMovieDurationAndType(): String {
        return "$duration $type"
    }

    fun getMovieReleaseDateAndLocation(): String {
        if (TextUtils.isEmpty(publishDate) && TextUtils.isEmpty(publishLocation)) {
            return ""
        }
        return getString(R.string.ugc_detail_movie_release_format).format("${publishDate}${publishLocation}")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinkMovieViewBean

        if (movieId != other.movieId) return false
        if (img != other.img) return false
        if (movieName != other.movieName) return false
        if (movieNameEn != other.movieNameEn) return false
        if (score != other.score) return false
        if (duration != other.duration) return false
        if (type != other.type) return false
        if (publishDate != other.publishDate) return false
        if (publishLocation != other.publishLocation) return false
        if (movieStatus != other.movieStatus) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieId.hashCode()
        result = 31 * result + img.hashCode()
        result = 31 * result + movieName.hashCode()
        result = 31 * result + movieNameEn.hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + publishDate.hashCode()
        result = 31 * result + publishLocation.hashCode()
        result = 31 * result + movieStatus.hashCode()
        return result
    }

}