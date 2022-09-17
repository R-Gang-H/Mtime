package com.kotlin.android.community.family.component.ui.details.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/29
 */
data class FamilyDetail(
    var id: Long,
    var name: String,
    var pic: String,
    var des: String,
    var postNumber: Long,
    var memberNumber: Long,
    var userType: Long, //-1:申请者  1：群主  2：管理员  3：普通成员 4：黑名单 5:未加入
    var creator: FamilyDetailMember, //创建者，群主
    var memberList: List<FamilyDetailMember>?,
    var administratorCount: Long = 0,    // 管理员人数
    var joinPermission: Long = 0,        // 加入权限  1.任何人均可自由加入 2.需要管理员审核
    var status: Long = 2,                // 状态 1 待审核 2 审核通过 注意！！！：如果是待审核，没有ID字段！！！！！！
    var uploadId: String = "",          //编辑群组的场景需要使用，其他场景不需要  [i5]2019/12/13/103624.75878680
    var primaryCategoryId: Long = 0,    // 群组一级分类ID
    var primaryCategoryName: String = "", // 群组一级分类名称
    var bizCode: Long = 1, //1 正常 2 群组不存在 3 群组已删除
    var posting: Boolean = false, //是否可以发帖
    var groupAuthority: Long = 0L //群组的发帖和评论的权限 //加入发帖加入评论 1,"加入发帖加入评论" 自由发帖自由评论 2,"自由发帖自由评论" 管理员发帖自由评论 3,"管理员发帖自由评论"
) : ProguardRule {
    companion object {
        // 权限：1.任何人均可自由加入 2.需要管理员审核
        const val PERMISSION_NULL = 0L
        const val PERMISSION_FREE = 1L
        const val PERMISSION_REVIEW = 2L

        //-1:申请者  1：群主  2：管理员  3：普通成员 4：黑名单 5:未加入
        const val USER_TYPE_JOINING = -1L
        const val USER_TYPE_MASTER = 1L
        const val USER_TYPE_MANAGER = 2L
        const val USER_TYPE_MEMBER = 3L
        const val USER_TYPE_BLACKLIST = 4L
        const val USER_TYPE_NO_JOIN = 5L

        // 发帖和评论的权限
        const val GROUP_AUTHORITY_JOIN = 1L //1,"加入发帖加入评论"
        const val GROUP_AUTHORITY_FREE = 2L //2,"自由发帖自由评论"
        const val GROUP_AUTHORITY_MANAGER = 3L //3,"管理员发帖自由评论"
    }

    fun getPostNumberStr(): String {
        return formatCount(postNumber)
    }

    fun getMemberNumberStr(): String {
        return formatCount(memberNumber)
    }
}

data class FamilyDetailMember(
    var id: Long,
    var name: String,
    var pic: String = "",
    var userType: Long = 0  // 用户类型 -1:申请者  1：群主  2：管理员  3：普通成员 4：黑名单
) : ProguardRule

