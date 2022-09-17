package com.kotlin.android.video.component.viewbean

import android.text.TextUtils
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.video.component.R
import com.kotlin.android.video.component.adapter.VIDEO_DETAIL_UN_ATTITUDE

/**
 * create by lushan on 2020/9/2
 * description:预告片评论上方标题、相关影片
 */
data class PreVideoCommentTitleBean(
        var title: String = "",//标题
        var releaseDate: String = "",//发布时间
        var movie: ReMovieBean? = null//相关影片
) : ProguardRule {
    fun getMovieName(): String = movie?.movieName.orEmpty()

    fun getMoviePic(): String = movie?.moviePic.orEmpty()

    fun getMovieDurationAndType(): String {
        val isDurationEmpty = TextUtils.isEmpty(movie?.movieDuration)
        val isMovieTypeEmpty = TextUtils.isEmpty(movie?.generType)
        return when {
            isDurationEmpty -> {
                movie?.generType.orEmpty()
            }
            isMovieTypeEmpty -> {
                movie?.movieDuration.orEmpty()
            }
            else -> {
                "${movie?.movieDuration.orEmpty()}-${movie?.generType.orEmpty()}"
            }
        }
    }

    fun getReleaseAndLocation(): String {
        val isDateEmpty = TextUtils.isEmpty(movie?.releaseDate)
        val islocationEmpty = TextUtils.isEmpty(movie?.releaseLocation)
        return if (islocationEmpty && isDateEmpty) {
            ""
        } else {
            getString(R.string.video_release_format).format(movie?.releaseDate.orEmpty(), movie?.releaseLocation.orEmpty())
        }
    }

    //    获取购票状态
    fun getMovieBuyState() = movie?.buyTicketStatus ?: 0L

    //    获取想看状态
    fun getAttuideState() = movie?.attitude ?: VIDEO_DETAIL_UN_ATTITUDE

    fun getMovieId(): Long = movie?.movieId ?: 0L
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PreVideoCommentTitleBean

        if (title != other.title) return false
        if (releaseDate != other.releaseDate) return false
        if (movie != other.movie) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + releaseDate.hashCode()
        result = 31 * result + (movie?.hashCode() ?: 0)
        return result
    }


    companion object {
        /**
         * 转换实体类
         */
        fun covert(bean: VideoDetail): PreVideoCommentTitleBean {
            return PreVideoCommentTitleBean(title = bean.title.orEmpty(),
                    releaseDate = getString(R.string.video_publish_time_format).format(formatPublishTime(bean.pulishTime)),
                    movie = covertReMovieBean(bean.relatedMovie))
        }

        /**
         * 转化相关影片
         */
        private fun covertReMovieBean(bean: VideoDetail.RelatedMovie?): ReMovieBean? {
            bean ?: return null
            with(bean) {

                return ReMovieBean(movieId = movieId,
                        movieName = if (TextUtils.isEmpty(name).not()) name.orEmpty() else nameEn.orEmpty(),
                        score = "",
                        moviePic = img.orEmpty(),
                        movieDuration = timeLength.orEmpty(),
                        generType = movieType.orEmpty(),
                        releaseDate = releaseDate.orEmpty(),
                        releaseLocation = "",
                        buyTicketStatus = buyTicketStatus ?: 0L,
                        attitude = attitude ?: -1L
                )
            }
        }




    }
    data class ReMovieBean(
            var movieId: Long = 0L,//影片id
            var movieName: String = "",//影片名称
            var score: String = "",//评分
            var moviePic: String = "",//影片封面
            var movieDuration: String = "",//影片时长
            var generType: String = "",//影片制式
            var releaseDate: String = "",//上映日期
            var releaseLocation: String = "",//上映地区
            var attitude: Long = -1L,//当前用户对电影的态度：-1未表态 0看过 1想看
            var buyTicketStatus: Long = 0L,//购票状态：1正常购票 2预售 3不可购票

    ) : ProguardRule {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ReMovieBean

            if (movieId != other.movieId) return false
            if (movieName != other.movieName) return false
            if (score != other.score) return false
            if (moviePic != other.moviePic) return false
            if (movieDuration != other.movieDuration) return false
            if (generType != other.generType) return false
            if (releaseDate != other.releaseDate) return false
            if (releaseLocation != other.releaseLocation) return false
            if (attitude != other.attitude) return false
            if (buyTicketStatus != other.buyTicketStatus) return false

            return true
        }

        override fun hashCode(): Int {
            var result = movieId.hashCode()
            result = 31 * result + movieName.hashCode()
            result = 31 * result + score.hashCode()
            result = 31 * result + moviePic.hashCode()
            result = 31 * result + movieDuration.hashCode()
            result = 31 * result + generType.hashCode()
            result = 31 * result + releaseDate.hashCode()
            result = 31 * result + releaseLocation.hashCode()
            result = 31 * result + attitude.hashCode()
            result = 31 * result + buyTicketStatus.hashCode()
            return result
        }
    }


}