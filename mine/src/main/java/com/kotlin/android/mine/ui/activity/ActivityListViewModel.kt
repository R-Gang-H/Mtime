package com.kotlin.android.mine.ui.activity

import android.content.Context
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.activity.ActivityList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.mine.bean.ActivityViewBean
import com.kotlin.android.mine.repoistory.MineRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/16
 * 描述: 活动列表页ViewModel
 */
class ActivityListViewModel: BaseViewModel() {

    private val repo by lazy { MineRepository() }

    // 活动列表
    private val mUIModel = BinderUIModel<ActivityList, MutableList<MultiTypeBinder<*>>>()
    val uIState = mUIModel.uiState

    /**
     * 用户活动列表
     */
    fun getActivityList(context: Context, isRefresh: Boolean) {
        call(
                uiModel = mUIModel,
                isShowLoading = isRefresh,
                isRefresh = isRefresh,
                pageStamp = {
                    it.nextStamp
                },
                hasMore = {
                    it.hasNext.orFalse()
                },
                converter = {
                    ActivityViewBean.build(
                            context = context,
                            beans = it.activities,
                            isMine = false
                    )
                }
        ) {
            repo.getUserActivityList(
                    nextStamp = mUIModel.pageStamp,
                    pageSize = mUIModel.pageSize        // 默认20条
            )
        }
    }

}