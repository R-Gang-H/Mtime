package com.kotlin.android.mine.ui.content.fragment

import android.content.Context
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.community.content.ContentTypeCount
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.bean.ContentsViewBean
import com.kotlin.android.mine.repoistory.ContentsRepository

class ContentsViewModel : BaseViewModel() {

    private val repo by lazy {
        ContentsRepository()
    }

    private val contentUIModel: BaseUIModel<ContentsViewBean> = BaseUIModel()
    val contentState = contentUIModel.uiState

    private val deleteContentUIModel: BaseUIModel<Any> = BaseUIModel()
    val deleteContentState = deleteContentUIModel.uiState

    private val reviewContentUIModel: BaseUIModel<Any> = BaseUIModel()
    val reviewContentState = reviewContentUIModel.uiState

    private val draftsUIModel: BaseUIModel<ContentsViewBean> = BaseUIModel()
    val draftsState = draftsUIModel.uiState

    private val typeCountUIModel: BaseUIModel<ContentTypeCount> = BaseUIModel()
    val typeCountState = typeCountUIModel.uiState

    /**
     * 获取内容列表
     */
    fun getContent(
        context: Context,
        type: Long,
        tab: Long,
        sort: Long,
        isRefresh: Boolean,
        isDrafts: Boolean
    ) {
        val uiModel = if (isDrafts) {
            draftsUIModel
        } else {
            contentUIModel
        }
        call(
            uiModel = uiModel,
            isRefresh = isRefresh,
            hasMore = {
                it.hasNext && !it.nextStamp.isNullOrBlank()
            },
            pageStamp = {
                it.nextStamp
            }
        ) {
            repo.getContent(
                context = context,
                type = type,
                tab = tab,
                nextStamp = uiModel.pageStamp,
                pageSize = uiModel.pageSize,
                sort = sort,
                isDrafts = isDrafts
            )
        }
    }

    fun deleteContent(contentIds: Long?, type: Long) {
        call(uiModel = deleteContentUIModel) {
            repo.deleteContent(contentIds, type)
        }
    }

    fun reviewContent(contentId: Long, type: Long) {
        call(uiModel = reviewContentUIModel) {
            repo.reviewContent(contentId, type)
        }
    }

    fun getTypeCount(type: Long) {
        call(uiModel = typeCountUIModel) {
            repo.getTypeCount(type)
        }
    }
}