package com.kotlin.android.message.ui.privateChat.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.UserFollowListResult
import com.kotlin.android.message.ui.privateChat.binder.ItemPrivateChatBinder
import com.kotlin.android.message.widget.AuthHeaderView

/**
 * Created by zhaoninglongfei on 2022/3/17
 *
 */
data class PrivateChatViewBean(
    val imId: String?,
    val name: String?,
    val signature: String?,
    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {
    companion object {
        fun convertToItemPrivateChatBinders(result: UserFollowListResult): List<ItemPrivateChatBinder> {
            val returnList: ArrayList<ItemPrivateChatBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = PrivateChatViewBean(
                    imId = it.imId,
                    name = it.nikeName,
                    signature = it.userSign,
                    authHeader = AuthHeaderView.AuthHeader(
                        userId = it.userId,
                        headImg = it.avatarUrl,
                        unread = false,
                        authType = it.authType,
                        authRole = it.authRole
                    )
                )
                returnList.add(ItemPrivateChatBinder(viewBean))
            }

            return returnList
        }
    }
}