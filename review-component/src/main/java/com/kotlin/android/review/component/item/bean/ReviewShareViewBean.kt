package com.kotlin.android.review.component.item.bean

import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.getRatingLevelHintText
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.review.component.R
import com.kotlin.android.mtime.ktx.htmlDecode

/**
 * create by lushan on 2020/11/4
 * description:影评分享
 */
data class ReviewShareViewBean(
        var movieId: Long = 0L,//电影id
        var movieNameCn: String = "",//中文名称
        var movieNameEn: String = "",//英文名
        var generType: String = "",//影片类型
        var score: String = "",//影片时光评分
        var content: String = "",//影评内容
        var userId: Long = 0L,//用户id
        var userName: String = "",//用户昵称
        var showTime: String = "",//影评展示时间
        var userScore: String = "",//用户评分
        var userSubScores: List<MovieSubItemRatingBean> = listOf(), //用户的分项评分
        var userPic: String = "",//用户头像
        var joinDays: Long = 0L,//加入时光网天数
        var movieNum: Long = 0L,//标记的电影数
) : ProguardRule {

    /**
     * 获取标记
     */
    fun getJoinDaysAndMovieNum(): String {
        if (joinDays <= 0L && movieNum <= 0L) {
            return ""
        }
        if (joinDays <= 0L) {
            return getString(R.string.film_review_movies_format).format(movieNum)
        }
        if (movieNum <= 0) {
            return getString(R.string.film_review_join_days_format).format(joinDays)
        }
        return getString(R.string.film_review_join_days_and_movie_format).format(joinDays, movieNum)
    }

    fun getRatingHintText(): String {
        if (!TextUtils.isEmpty(userScore)) {
            return getRatingLevelHintText(userScore.toFloat().toInt())
        }
        return ""
    }

    companion object {
        fun build(reviewDetailViewBean: ReviewDetailViewBean): ReviewShareViewBean {
//目前缺少加入时光天数、标记电影数
            return ReviewShareViewBean(movieId = reviewDetailViewBean.movieViewBean?.movieId ?: 0L,
                    movieNameCn = reviewDetailViewBean.movieViewBean?.nameCn.orEmpty(),
                    movieNameEn = reviewDetailViewBean.movieViewBean?.nameEn.orEmpty(),
                    generType = reviewDetailViewBean.movieViewBean?.movieType.orEmpty(),
                    score = reviewDetailViewBean.movieViewBean?.mTimeScore.orEmpty(),
                    content = subStringWithIn1000(getReviewContent(reviewDetailViewBean.commonBar?.createUser?.score.orEmpty(), htmlDecode(reviewDetailViewBean.ugcWebViewBean?.content.orEmpty()).orEmpty())),
                    userId = reviewDetailViewBean.commonBar?.createUser?.userId ?: 0L,
                    userName = reviewDetailViewBean.commonBar?.createUser?.nikeName.orEmpty(),
                    showTime = reviewDetailViewBean.commonBar?.createUser?.createTime.orEmpty(),
                    userScore = reviewDetailViewBean.commonBar?.createUser?.score.orEmpty(),
                    userSubScores = reviewDetailViewBean.commonBar?.createUser?.fcSubItemRatings?.map {
                        MovieSubItemRatingBean(
                                index = it.index,
                                title = it.title ?: "",
                                rating = it.rating ?: 0F
                        )
                    } ?: listOf(),
                    userPic = reviewDetailViewBean.commonBar?.createUser?.avatarUrl.orEmpty()
            )
        }

        /**
         * 是否展示评分
         */
        fun getScore(mTimeScore: String): Double {
            val score = mTimeScore.trim()
            if (TextUtils.isEmpty(score)) {
                return 0.0
            }
            return try {
                score.toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
                0.0
            }
        }

        private fun getReviewContent(score: String, body: String): String {
            return if (TextUtils.isEmpty(body)) {//没有进行评论
//                解析评分
                val scoreNum = getScore(score)
                if (scoreNum >= 0.0 && scoreNum <= 2.0) {
                    getString(R.string.film_review_share_tips_0_2)
                } else if (scoreNum > 2 && scoreNum <= 5.0) {
                    getString(R.string.film_review_share_tips_3_5)
                } else if (scoreNum > 5.0 && scoreNum <= 7.0) {
                    getString(R.string.film_review_share_tips_6_7)
                } else if (scoreNum > 7.0 && scoreNum <= 8.0) {
                    getString(R.string.film_review_share_tips_8)
                } else if (scoreNum > 8.0 && scoreNum <= 10) {
                    getString(R.string.film_review_share_tips_9_10)
                } else {
                    ""
                }
            } else {
                body
            }


        }

        private fun subStringWithIn1000(content: String): String {
            return if (content.length > 1000) {
                "${content.substring(0, 997)}..."
            } else {
                content
            }
        }
    }

    fun isShowMtimeScore(): Boolean {
        return isShowScore(score)
    }

    fun isShowUserScore(): Boolean {
        return isShowScore(userScore)
    }

    /**
     * 是否展示评分
     */
    private fun isShowScore(mTimeScore: String): Boolean {
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
}