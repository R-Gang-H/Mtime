package com.kotlin.android.app.router.provider.community_family

import android.app.Activity
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/30
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY_FAMILY)
interface ICommunityFamilyProvider: IBaseProvider {
    fun startFamilyDetail(id: Long)
    fun startFamilyClassList()
    fun startFamilyCreate()
    fun startFamilyEdit(groupId: Long)
    fun startFamilyEditInfo(activity: Activity, type: Int, content: String, requestCode: Int)
    fun startFamilyPermission(activity: Activity, permission: Long, requestCode: Int)
    fun startFamilyPublishPermission(activity: Activity, permission: Long, groupId: Long,requestCode: Int)
    fun startFamilyCategory(activity: Activity, primaryCategoryId: Long, requestCode: Int)
    fun startFamilyAdmin(activity: Activity, groupId: Long, requestCode: Int)
    fun startFamilyAddAdmin(activity: Activity, groupId: Long, requestCode: Int)
    fun startFamilyMember(activity: Activity? = null, groupId: Long, requestCode: Int = 0)
    fun startFamilyMemberManage(activity: Activity, groupId: Long, requestCode: Int)
    fun startFamilySectionManage(activity: Activity,groupId: Long,requestCode: Int)

    /**
     * 找家族页面
     */
    fun startFamilyFind()
}