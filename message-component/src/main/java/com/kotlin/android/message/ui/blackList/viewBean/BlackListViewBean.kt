package com.kotlin.android.message.ui.blackList.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.BlockListResult
import com.kotlin.android.message.ui.blackList.binder.ItemBlackListBinder
import com.kotlin.android.message.widget.AuthHeaderView

/**
 * Created by zhaoninglongfei on 2022/3/23
 *
 */
data class BlackListViewBean(
    val name: String? = null,
    val userId: Long? = null,
    val headImg: String? = null,
    val signature: String? = null,
    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {
    companion object {
        fun convertToItemBlackListBinders(
            result: BlockListResult,
            removeFromBlackList: (Long, Int) -> Unit
        ): List<ItemBlackListBinder> {
            val returnList: ArrayList<ItemBlackListBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = BlackListViewBean(
                    name = it.nikeName,
                    userId = it.userId,
                    headImg = it.avatarUrl,
                    signature = it.userSign,
                    authHeader = AuthHeaderView.AuthHeader(
                        userId = it.userId,
                        headImg = it.avatarUrl,
                        unread = false,
                        authType = it.authType,
                        authRole = it.authRole
                    )
                )
                returnList.add(ItemBlackListBinder(viewBean, removeFromBlackList))
            }

            return returnList
        }
    }
}