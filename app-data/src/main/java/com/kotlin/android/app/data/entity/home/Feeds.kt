package com.kotlin.android.app.data.entity.home

import android.text.TextUtils
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule

/**
 * Created by oseven on 2020-08-24
 *
 * 首页feed流实体
 */
data class Feeds(var top: List<Feed>?,
                 var rcmd: List<Feed>?,
                 var hasNext: Boolean) : ProguardRule {

    data class Feed(var feedId: String?,
                    var contentType: Long, //1：推荐文章 2：推荐帖子 3：推荐长影评（影片或影人的影评） 4：广告 5：家族列表
                    var commContent: CommContent?,
                    var adv: Adv?,
                    var hotFamilies: HotFamilies?) : ProguardRule {

        companion object {
            const val TYPE_ARTICLE  = 1L
            const val TYPE_POST     = 2L
            const val TYPE_REVIEW   = 3L
            const val TYPE_AD       = 4L
            const val TYPE_FAMILYS  = 5L
        }
    }

    data class Adv(var advTag: String?,
                   var url: String?,
                   var appLink: String?,
                   var enableAdvTag: String?,
                   var title: String?,
                   var subTitle: String?,
                   var img: String?

    ) : ProguardRule {
        fun getAdTag(): String? {
            return if (TextUtils.equals(enableAdvTag, "1")) {
                advTag
            } else {
                null
            }
        }
    }

    data class HotFamilies(var list: List<Family>?) : ProguardRule

    data class Family(var groupId: Long,
                     var groupImg: String?,
                     var uploadId: String?,
                     var status: Long = 2L, //群组状态 1 待审核 2 审核通过
                     var groupName: String?,
                     var groupPeopleCount: Long = 0L,
                     var groupPeopleCountStr: String?,
                     var administratorCount: Long = 0L,
                     var primaryCategoryId: Long = 0L,
                     var primaryCategoryName: String?,
                     var groupDes: String?,
                     var joinPermission: Long = 1L,
                     var hasJoin: Long = 0L, //当前用户是否加入此群组 0:未加入 1：已加入成功 2 加入中（待审核）3 黑名单人员
                     var groupPostsCount: Long = 0L,
                     var groupPostsCountStr: String?,
                     var userType: Long = 5L, //用户类型 -1:申请者 1：群主 2：管理员 3：普通成员 4：黑名单 5:未加入（即和本群组没有任何关系）
                     var groupCreateUser: FamilyUser?,
                     var bizCode: Long = 1L,
                     var bizMsg: String?,
                     var groupJoinUserList: List<FamilyUser>?) : ProguardRule

    data class FamilyUser(var userId: Long,
                         var userName: String?,
                         var userImg: String?
    ) : ProguardRule
}