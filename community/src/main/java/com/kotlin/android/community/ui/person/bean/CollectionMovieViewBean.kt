package com.kotlin.android.community.ui.person.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/14
 * description: 收藏电影ViewBean
 */

data class CollectionMovieViewBean(
    var movieId: Long = 0L,//电影id
    var movieName: String = "",//影片名称
    var moviePic: String = "",//影片海报
    var genreTypes: String = "",//类型
    var actors: String = "",//演员
    var year: String? = "",//年份
    var releaseContent: String = "",//上映信息
    var score: String = "",//电影评分
    var nameEn: String = "",//英文名字
    var directors: String = "",//导演
    var canPlay: Boolean = false,//是否可以播放
    var isShowing: Boolean = false,//是否进行 如电影未进行，则显示，反则不显示
) : ProguardRule {
    fun isScoreEmpty(): Boolean = TextUtils.isEmpty(score)
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CollectionMovieViewBean

        if (movieId != other.movieId) return false
        if (movieName != other.movieName) return false
        if (moviePic != other.moviePic) return false
        if (genreTypes != other.genreTypes) return false
        if (actors != other.actors) return false
        if (releaseContent != other.releaseContent) return false
        if (score != other.score) return false

        return true
    }

    override fun hashCode(): Int {
        var result = movieId.hashCode()
        result = 31 * result + movieName.hashCode()
        result = 31 * result + moviePic.hashCode()
        result = 31 * result + genreTypes.hashCode()
        result = 31 * result + actors.hashCode()
        result = 31 * result + releaseContent.hashCode()
        result = 31 * result + score.hashCode()
        return result
    }
}