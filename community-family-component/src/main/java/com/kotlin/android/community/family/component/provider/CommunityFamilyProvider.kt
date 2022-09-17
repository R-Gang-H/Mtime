package com.kotlin.android.community.family.component.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.community.family.component.ui.details.FamilyDetailActivity
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/30
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY_FAMILY)
class CommunityFamilyProvider : ICommunityFamilyProvider {

    override fun init(context: Context?) {

    }

    override fun startFamilyDetail(id: Long) {
        val bundle = Bundle().put(FamilyDetailActivity.KEY_ID, id)
        RouterManager.instance.navigation(
            RouterActivityPath.CommunityFamily.PAGER_FAMILY_DETAIL,
            bundle
        )
    }

    override fun startFamilyClassList() {
        RouterManager.instance.navigation(RouterActivityPath.CommunityFamily.PAGER_FAMILY_CLASS_LIST)
    }

    override fun startFamilyCreate() {
        RouterManager.instance.navigation(RouterActivityPath.CommunityFamily.PAGER_FAMILY_CREATE)
    }

    override fun startFamilyEdit(groupId: Long) {
        val bundle = Bundle().put(FamilyConstant.KEY_FAMILY_ID, groupId)
        RouterManager.instance.navigation(
            RouterActivityPath.CommunityFamily.PAGER_FAMILY_CREATE,
            bundle
        )
    }

    override fun startFamilyEditInfo(
        activity: Activity,
        type: Int,
        content: String,
        requestCode: Int
    ) {
        Bundle().apply {
            putInt(FamilyConstant.KEY_FAMILY_EDIT_INFO_TYPE, type)
            putString(FamilyConstant.KEY_FAMILY_EDIT_INFO_CONTENT, content)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_EDIT_INFO,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyPermission(activity: Activity, permission: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_PERMISSION, permission)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_PERMISSION,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyPublishPermission(
        activity: Activity,
        permission: Long,
        groupId: Long,
        requestCode: Int
    ) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_PUBLISH_PERMISSION, permission)
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_PERMISSION,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyCategory(
        activity: Activity,
        primaryCategoryId: Long,
        requestCode: Int
    ) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_ID, primaryCategoryId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_CATEGORY,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyAdmin(activity: Activity, groupId: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_ADMIN,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyAddAdmin(activity: Activity, groupId: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_ADD_ADMIN,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyMember(activity: Activity?, groupId: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_MEMBER,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyMemberManage(activity: Activity, groupId: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_MEMBER_MANAGE,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilySectionManage(activity: Activity, groupId: Long, requestCode: Int) {
        Bundle().apply {
            putLong(FamilyConstant.KEY_FAMILY_ID, groupId)
        }.run {
            RouterManager.instance.navigation(
                path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_SECTION_MANAGER,
                bundle = this,
                context = activity,
                requestCode = requestCode
            )
        }
    }

    override fun startFamilyFind() {
        RouterManager.instance.navigation(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_FIND)
    }
}