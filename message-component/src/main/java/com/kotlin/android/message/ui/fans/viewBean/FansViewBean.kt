package com.kotlin.android.message.ui.fans.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.UserFansListResult
import com.kotlin.android.message.ui.fans.FansViewModel
import com.kotlin.android.message.ui.fans.binder.ItemFansBinder
import com.kotlin.android.message.widget.AuthHeaderView

/**
 * Created by zhaoninglongfei on 2022/3/16
 *
 */
data class FansViewBean(
    val userId: Long?,
    val name: String?,
    val signature: String?,
    var hasFollowed: Boolean = false,
    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {
    companion object {
        fun convertToItemFansBinders(
            result: UserFansListResult,
            followUser: (Long, Long, FansViewModel.FollowSuccessCallback) -> Unit
        ): List<ItemFansBinder> {
            val returnList: ArrayList<ItemFansBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = FansViewBean(
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
                returnList.add(ItemFansBinder(viewBean, followUser))
            }

            return returnList
        }
    }

    fun changeFollowedStatus() {
        this.hasFollowed = !hasFollowed
    }
}