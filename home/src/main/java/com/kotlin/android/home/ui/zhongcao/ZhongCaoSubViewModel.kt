package com.kotlin.android.home.ui.zhongcao

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.entity.CommHasNextList
import com.kotlin.android.app.data.entity.home.RcmdContentList
import com.kotlin.android.app.data.entity.home.zhongcao.ZhongCaoRcmdData
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.home.repository.ZhongCaoSubRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class ZhongCaoSubViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { ZhongCaoSubRepository() }

    private val mUIModel = BaseUIModel<CommHasNextList<MultiTypeBinder<*>>>()
    val uiState = mUIModel.uiState

    /**
     * 加载推荐内容
     *
     * @param isRefresh 是否刷新代表第一页
     * @param subTypeId 类型ID
     */
    fun loadData(isRefresh: Boolean, subTypeId: Long) {
        callParallel(
            { repo.loadData(subTypeId, mUIModel.pageStamp, mUIModel.pageSize) },
            uiModel = mUIModel,
            isShowLoading = false,
            isRefresh = isRefresh,
            hasMore = {
                it.hasNext
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            val binders = arrayListOf<MultiTypeBinder<*>>()
            var hasMore = false
            var pageStamp = ""
            (it[0] as? ZhongCaoRcmdData)?.rcmdContentList?.apply {
                hasMore = this.hasNext
                pageStamp = this.nextStamp
                this.items?.forEach { item ->
                    binders.add(
                        CommunityCardItem.converter2Binder(content = item)
                    )
                }
            }
            CommHasNextList(
                hasNext = hasMore,
                items = binders,
                nextStamp = pageStamp
            )
        }
    }
}