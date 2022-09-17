package com.kotlin.android.message.ui.movieRemind

import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.message.MovieRemindResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import com.kotlin.android.message.ui.movieRemind.viewBean.MovieRemindViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * Created by zhaoninglongfei on 2022/3/15
 *
 */
class MovieRemindViewModel : BaseViewModel() {
    private val repo = MessageRepository()

    private val movieRemindUiModel =
        BinderUIModel<MovieRemindResult, List<MultiTypeBinder<*>>>()
    val movieRemindUiState = movieRemindUiModel.uiState

    fun loadMovieRemindList(isRefresh: Boolean) {
        call(
            uiModel = movieRemindUiModel,
            isShowLoading = isRefresh,
            isRefresh = isRefresh,
            converter = {
                MovieRemindViewBean.convertToItemMovieRemindBinders(it)
            },
            pageStamp = {
                it.nextStamp
            },
            isEmpty = {
                isRefresh && it.items.isNullOrEmpty()
            },
            hasMore = {
                it.hasNext ?: false
            }
        ) {
            repo.loadMovieRemindList(
                nextStamp = movieRemindUiModel.pageStamp,
                pageSize = movieRemindUiModel.pageSize
            )
        }
    }
}