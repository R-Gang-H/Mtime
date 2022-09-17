package com.kotlin.android.mine.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_ARTICLE
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.constant.CommConstant.KEY_CONTENT_TYPE
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.router.RouterManager

/**
 * 创建者: zl
 * 创建时间: 2020/7/24 2:35 PM
 * 描述:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_MINE)
class MineProvider : IMineProvider {
    /**
     *  跳转到想看，看过
     */
    override fun startWannaSeeActivity(bundle: Bundle?, activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Community.PAGE_WANT_SEE,
                bundle = bundle,
                context = activity
        )
    }

    /**
     * 跳转到认证首页
     */
    override fun startAuthenActivity(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_AUTHEN_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到我的收藏页面
     */
    override fun startMyCollection(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_COLLECTION_ACTIVITY,
                context = activity
        )
    }

    /**
     * [contentType]: [ContentType] 内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
     * 跳转到我的内容页面
     */
    override fun startMyContent(activity: Activity, @ContentType contentType: Long?) {
        val bundle = Bundle().apply {
            putLong(KEY_CONTENT_TYPE, contentType ?: CONTENT_TYPE_ARTICLE)
        }
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_CONTENTS_ACTIVITY,
                context = activity,
                bundle = bundle
        )
    }

    /**
     * 跳转到草稿箱页面
     */
    override fun startMyDrafts(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_DRAFTS_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到我的勋章页面
     */
    override fun startMyMedal(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_MEDAL_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到设置页面
     */
    override fun startSetting(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_SETTING_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到个人资料页面
     */
    override fun startPersonalData(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_PERSONAL_DATA_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到昵称页面
     */
    override fun startNickname(activity: Activity, requestCode: Int) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MODIFY_NICKNAME_ACTIVITY,
                context = activity,
                requestCode = requestCode
        )
    }

    /**
     * 跳转到编辑签名页面
     */
    override fun startEditSign(activity: Activity, requestCode: Int) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_EDIT_SIGN_ACTIVITY,
                context = activity,
                requestCode = requestCode
        )
    }

    /**
     * 跳转到账号设置页面
     */
    override fun startAccountSetting(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_ACCOUNT_SETTING_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到第三方账号绑定页面
     */
    override fun startThirdAccountBind(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_THIRD_ACCOUNT_BIND_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到推送设置页面
     */
    override fun startPushSettingBind(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_PUSH_SETTING_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到屏蔽设置页面
     */
    override fun startShieldingSettingBind(activity: Activity) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_SHIELDING_SETTING_ACTIVITY,
                context = activity
        )
    }

    /**
     * 跳转到会员中心页面
     */
    override fun startMemberCenterActivity(context: Context) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MEMBER_CENTER_ACTIVITY,
                context = context
        )
    }

    override fun startMyWalletActivity(context: Context) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_WALLET,
                context = context
        )
    }

    override fun startMyWalletActivity(activity: Activity?, requestCode: Int?) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_MY_WALLET,
                context = activity,
                requestCode = requestCode ?: -1
        )
    }

    override fun startMyCouponUsedRecordActivity(activity: Activity) {
        RouterManager.instance.navigation(path = RouterActivityPath.Mine.PAGE_MY_COUPON_USED_RECORD)
    }

    override fun startLicenseActivity(context: Context) {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_LICENSE,
                context = context
        )
    }

    /**
     * 活动列表页
     */
    override fun startActivityListActivity() {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_ACTIVITY_LIST
        )
    }

    /**
     * 跳转任务中心页面
     */
    override fun startActivityCreatorTaskActivity() {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_CREATOR_ACTIVITY,
        )
    }

    /**
     * 跳转权益说明页面
     */
    override fun startActivityCreatorRewardActivity() {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_CREATOR_REWARD_ACTIVITY,
        )
    }

    /**
     * 跳转到创作中心页面
     */
    override fun startActivityCreatorCenterActivity() {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_CREAT_CENTER
        )
    }

    /**
     * 跳转到数据中心页面
     */
    override fun startActivityDataCenterActivity() {
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_DATA_CENTER
        )
    }

    /**
     * 跳转到单篇分享详情页面
     */
    override fun startActivitySingleAnalysActivity(
            type: Long,
            bean: DataCenterBean.SingleAnalysisBean.Item,
    ) {
        val bundle = Bundle().apply {
            putSerializable("bean", bean)
            putLong("type", type)
            putLong("contentId", bean.contentId)
        }
        RouterManager.instance.navigation(
                path = RouterActivityPath.Mine.PAGE_ANALYS_DETAIL,
                bundle = bundle
        )
    }
}