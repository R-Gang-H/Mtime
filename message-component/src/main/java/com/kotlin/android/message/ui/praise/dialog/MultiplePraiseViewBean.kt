package com.kotlin.android.message.ui.praise.dialog

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.PraiseUserResult
import com.kotlin.android.message.widget.AuthHeaderView

/**
 * Created by zhaoninglongfei on 2022/4/26
 *
 */
data class MultiplePraiseViewBean(
    val userId: Long?,
    val name: String?,
    val signature: String?,
    var hasFollowed: Boolean = false,
    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {
    companion object {
        fun convertToItemMultiplePraiseBinders(
            result: PraiseUserResult,
            followUser: (Long, Long, MultiplePraiseViewModel.FollowSuccessCallback) -> Unit
        ): List<ItemMultiplePraiseBinder> {
            val returnList: ArrayList<ItemMultiplePraiseBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = MultiplePraiseViewBean(
                    userId = it.userId,
                    name = it.nickName,
                    signature = it.userSign,
                    hasFollowed = it.followed ?: false,
                    authHeader = AuthHeaderView.AuthHeader(
                        userId = it.userId,
                        headImg = it.avatarUrl,
                        unread = it.unread,
                        authType = it.authType,
                        authRole = it.authRole
                    )
                )
                returnList.add(ItemMultiplePraiseBinder(viewBean, followUser))
            }
            return returnList
        }
    }

    fun changeFollowedStatus(){
        this.hasFollowed = !hasFollowed
    }
}