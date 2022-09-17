package com.kotlin.android.review.component.item.bean

import android.text.TextUtils
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.review.component.item.adapter.ReviewBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/8
 *
 * 影评列表中的Item实体
 */
data class ReviewItem(
        var id: Long = 0,
        var title: String = "",
        var content: String = "",
        var movieId: Long = 0,
        var movieName: String = "",
        var moviePic: String = "",
        var movieScore: String = "",
        var userId: Long = 0,
        var userAuthType: Long = 1,
        var userName: String = "",
        var userPic: String = "",
        var userScore: String = "",
        var tag: String = "",
        var likeCount: Long = 0,
        var dislikeCount: Long = 0,
        var isLike: Boolean = true,
        var isDislike: Boolean = false
) : ProguardRule {
    companion object {
        /**
         * 将社区公共实体转换成Binder
         */
        fun converter2Binder(commContent: CommContent?): ReviewBinder {
            return ReviewBinder(converter(commContent))
        }

        /**
         * 将社区公共实体转换成UI实体
         */
        fun converter(commContent: CommContent?): ReviewItem {
            commContent?.let {
                return ReviewItem(
                        id = it.contentId,
                        title = it.title.orEmpty(),
                        content = it.mixWord.orEmpty(),
                        movieId = it.fcMovie?.id ?: 0,
                        movieName = it.fcMovie?.nameEn ?: it.fcMovie?.name.orEmpty(),
                        moviePic = it.fcMovie?.imgUrl.orEmpty(),
                        movieScore = it.fcMovie?.rating.orEmpty(),
                        userId = it.createUser?.userId ?: 0,
                        userAuthType = it.createUser?.authType ?: 1,
                        userName = it.createUser?.nikeName.orEmpty(),
                        userPic = it.createUser?.avatarUrl.orEmpty(),
                        userScore = it.fcRating.orEmpty(),
                        tag = it.rcmdText.orEmpty(),
                        likeCount = it.interactive?.praiseUpCount ?: 0,
                        dislikeCount = it.interactive?.praiseDownCount ?: 0,
                        isLike = it.interactive?.userPraised == 1L,
                        isDislike = it.interactive?.userPraised == 2L
                )
            }
            return ReviewItem()
        }
    }

    fun getLikeCountFormat(): String {
        return formatCount(likeCount)
    }

    fun getDislikeCountFormat(): String {
        return formatCount(dislikeCount)
    }

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > 1
    }
}