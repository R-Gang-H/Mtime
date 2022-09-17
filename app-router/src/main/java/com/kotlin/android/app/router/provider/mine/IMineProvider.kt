package com.kotlin.android.app.router.provider.mine

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_ARTICLE

/**
 * 创建者: zl
 * 创建时间: 2020/7/24 2:02 PM
 * 描述:个人中心接口
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_MINE)
interface IMineProvider : IBaseProvider {

    /**
     *  跳转到想看，看过
     */
    fun startWannaSeeActivity(bundle: Bundle?, activity: Activity)

    /**
     * 跳转到认证首页
     */
    fun startAuthenActivity(activity: Activity)

    /**
     * 跳转到我的收藏页面
     */
    fun startMyCollection(activity: Activity)

    /**
     * 跳转到我的内容页面
     */
    fun startMyContent(activity: Activity, @ContentType contentType: Long? = CONTENT_TYPE_ARTICLE)

    /**
     * 跳转到草稿箱页面
     */
    fun startMyDrafts(activity: Activity)

    /**
     * 跳转到我的勋章页面
     */
    fun startMyMedal(activity: Activity)

    /**
     * 跳转到设置页面
     */
    fun startSetting(activity: Activity)

    /**
     * 跳转到个人资料页面
     */
    fun startPersonalData(activity: Activity)

    /**
     * 跳转到昵称页面
     */
    fun startNickname(activity: Activity, requestCode: Int)

    /**
     * 跳转到编辑签名页面
     */
    fun startEditSign(activity: Activity, requestCode: Int)

    /**
     * 跳转到账号设置页面
     */
    fun startAccountSetting(activity: Activity)

    /**
     * 跳转到第三方账号绑定页面
     */
    fun startThirdAccountBind(activity: Activity)

    /**
     * 跳转到推送设置页面
     */
    fun startPushSettingBind(activity: Activity)

    /**
     * 跳转到屏蔽设置页面
     */
    fun startShieldingSettingBind(activity: Activity)

    /**
     * 跳转到会员中心页面
     */
    fun startMemberCenterActivity(context: Context)

    /**
     * 跳转到我的钱包
     */
    fun startMyWalletActivity(context: Context)

    /**
     * 跳转到我的钱包
     */
    fun startMyWalletActivity(activity: Activity?, requestCode: Int?)

    /**
     * 券使用记录页面
     */
    fun startMyCouponUsedRecordActivity(activity: Activity)

    /**
     * 跳转到证照信息页
     */
    fun startLicenseActivity(context: Context)

    fun startActivityListActivity()

    /**
     * 跳转到任务中心页面
     */
    fun startActivityCreatorTaskActivity()

    /**
     * 跳转到权益说明页面
     */
    fun startActivityCreatorRewardActivity()

    /**
     * 跳转到创作中心页面
     */
    fun startActivityCreatorCenterActivity()

    /**
     * 跳转到数据中心页面
     */
    fun startActivityDataCenterActivity()

    /**
     * 跳转到单篇分享详情页面 contentId: Long
     */
    fun startActivitySingleAnalysActivity(
        type: Long,
        bean: DataCenterBean.SingleAnalysisBean.Item,
    )
}