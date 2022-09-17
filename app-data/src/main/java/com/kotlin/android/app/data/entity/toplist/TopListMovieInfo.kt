package com.kotlin.android.app.data.entity.toplist

import com.kotlin.android.app.data.ProguardRule

/**
 * Created by vivian.wei on 2020-09-01
 * 榜单影片实体
 */
data class TopListMovieInfo(
        var movieId: Long ?= 0,             // 影片ID（等于ItemId）
        var movieName: String ?= "",        // 影片名
        var movieNameEn: String ?= "",        // 影片名
        var img: String ?= "",              // 影片图片地址url
        var score: String ?= "",            // 评分 （"7.9"，也可能没有）
        var director: String ?= "",         // 导演
        var actors: String ?= "",           // 演员
        var releaseDate: String ?= "",      // 上映日期(yyyy年M月d日)
        var releaseLocation: String ?= "",  // 上映地区(“中国”)
        var award: String ?= "",            // 主要奖项
        var playState: Long ?= 0,           // 是否有在线播放 （0没有 1有）
        var currentUserWantSee: Boolean ?= false       // 当前用户是否想看
): ProguardRule {

    // 显示用评分
    var showScore: String ?= ""
        get() {
            score?.let {
                return if(it.isNotEmpty() && it.toFloat() > 0) it else ""
            }
            return ""
        }

    // 是否有评分
    fun hasScore(): Boolean {
        score?.let {
            return it.isNotEmpty() && it.toFloat() > 0
        }
        return false
    }

    /**
     * 是否有导演
     */
    fun hasDirector(): Boolean {
        return director?.isNotEmpty()?:false
    }

    /**
     * 是否有导演
     */
    fun hasActor(): Boolean {
        return actors?.isNotEmpty()?:false
    }

    /**
     * 是否有上映日期
     */
    fun hasReleaseDate(): Boolean {
        return releaseDate?.isNotEmpty()?:false
    }

    /**
     * 是否显示上映地区
     */
    fun showReleaseLocation(): Boolean {
        return hasReleaseDate() && (releaseLocation?.isNotEmpty() ?: false)
    }

    /**
     * 是否有主要奖项
     */
    fun hasAward(): Boolean {
        return award?.isNotEmpty()?:false
    }


}