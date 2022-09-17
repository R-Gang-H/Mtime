package com.kotlin.android.community.family.component.ui.clazz.bean

import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.app.data.entity.community.group.Group
import com.kotlin.android.app.data.ProguardRule

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/27
 *
 * 家族item实体
 */
data class FamilyItem(
        var id: Long,
        var pic: String,
        var name: String,
        var numberCount: Long,
        var intro: String,
        var joinType: Long = 0, //0:未加入 1：已加入成功 2 加入中（待审核） 3 黑名单人员
        var joinPermission: Long = 1L //加入群组的权限 1.任何人均可自由加入 2.需要管理员审核
) : ProguardRule {
    companion object {
        const val JOIN_TYPE_NO_JOIN = 0L
        const val JOIN_TYPE_JOINED = 1L
        const val JOIN_TYPE_JOINING = 2L
        const val JOIN_TYPE_BLACKLIST = 3L

        fun converter2Binder(group: Group, goneLinePosition: Int = 0): FamilyItemBinder {
            return FamilyItemBinder(FamilyItem(
                    id = group.groupId,
                    pic = group.groupImg ?: "",
                    name = group.groupName ?: "",
                    numberCount = group.groupPeopleCount,
                    intro = group.groupDes ?: "",
                    joinType = group.hasJoin,
                    joinPermission = group.joinPermission
            ), goneLinePosition)
        }
    }
}