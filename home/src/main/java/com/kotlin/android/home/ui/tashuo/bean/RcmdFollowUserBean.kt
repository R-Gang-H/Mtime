package com.kotlin.android.home.ui.tashuo.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.home.tashuo.RcmdFollowUser
import com.kotlin.android.core.CoreApp
import com.kotlin.android.home.R
import com.kotlin.android.home.ui.tashuo.adapter.RcmdFollowListBinder
import com.kotlin.android.ktx.ext.span.addStartImage
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.isSelf

data class RcmdFollowUserBean(
    var fansCount: Long = 0L,
    var latestContentTitle: String = "",
    var followed: Boolean = false,
    var userId: Long = 0,
    var nikeName: String = "",
    var avatarUrl: String = "",
    var authType: Long = 1, //用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
    var authRole: String = "" //认证角色 用户认证类型为"电影人"，才有的角色字段，其余为空
): ProguardRule {

    fun isSelf() = isSelf(userId)
    
    fun getFansCountStr(): String {
        return formatCount(fansCount)
    }
    
    fun getFollowBtnText(): String {
        return if (followed) {
            "已关注"
        } else {
            "关注"
        }
    }
    
    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return authType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return authType > 1
    }
    
    fun getContentTitle(): CharSequence {
        return if (latestContentTitle.isNotEmpty()) {
            latestContentTitle.addStartImage(
                context = CoreApp.instance,
                res = R.drawable.ic_home_hot
            )
        } else {
            ""
        }
    }
    
    companion object {
        fun converter2Binder(list: List<RcmdFollowUser>): RcmdFollowListBinder {
            return RcmdFollowListBinder(
                list.map {
                    RcmdFollowUserBean(
                        fansCount = it.fansCount,
                        latestContentTitle = it.latestContent?.title.orEmpty(),
                        followed = it.followed,
                        userId = it.userId,
                        nikeName = it.nikeName.orEmpty(),
                        avatarUrl = it.avatarUrl.orEmpty(),
                        authType = it.authType,
                        authRole = it.authRole.orEmpty()
                    )
                }
            )
        }
    }
}