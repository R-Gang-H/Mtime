package com.kotlin.android.mine.ui.home

import android.content.Context
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.activity.ActivityList
import com.kotlin.android.app.data.entity.mine.AccountStatisticsInfo
import com.kotlin.android.app.data.entity.user.User
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.bean.AccountStatisticsInfoViewBean
import com.kotlin.android.mine.bean.ActivityViewBean
import com.kotlin.android.mine.bean.UserViewBean
import com.kotlin.android.mine.repoistory.MineRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/10
 * 描述: 我的首页ViewModel
 */
class MineVMViewModel: BaseViewModel() {

    private val repo by lazy { MineRepository() }

    // 用户详情信息
    private val mAccountDetailUIModel = BinderUIModel<User, UserViewBean>()
    val accountDetailState = mAccountDetailUIModel.uiState

    // 用户统计信息
    private val mStatisticUIModel = BinderUIModel<AccountStatisticsInfo, AccountStatisticsInfoViewBean>()
    val statisticUIState = mStatisticUIModel.uiState

    // 活动列表
    private val mActivityUIModel = BinderUIModel<ActivityList, MutableList<MultiTypeBinder<*>>>()
    val activityUIState = mActivityUIModel.uiState

    /**
     * 用户详情信息
     */
    fun getAccountDetail() {
        call(
                uiModel = mAccountDetailUIModel,
                converter = {
                    UserViewBean.objectToViewBean(it)
                }
        ) {
            repo.getMineUserDetail()
        }
    }

    /**
     * 用户统计信息
     */
    fun getMineStatisticInfo(context: Context) {
        call(
                uiModel = mStatisticUIModel,
                converter = {
                    AccountStatisticsInfoViewBean.objectToViewBean(context, it)
                }
        ) {
            repo.getMineStatisticInfo()
        }
    }

    /**
     * 活动列表
     * 未登录用户也可以查看，登录用户从统计信息接口取值
     */
    fun getActivityList(context: Context, pageSize: Long) {
        call(
                uiModel = mActivityUIModel,
                converter = {
                    ActivityViewBean.build(context, it.activities, true)
                }
        ) {
            repo.getUserActivityList(
                    pageSize = pageSize
            )
        }
    }

}