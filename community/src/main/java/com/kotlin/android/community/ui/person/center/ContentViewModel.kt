package com.kotlin.android.community.ui.person.center

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.community.repository.PersonContentRepository
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.user.UserStore
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @date 2020/9/23
 */
class ContentViewModel : CommViewModel<MultiTypeBinder<*>>() {
    private val repo by lazy { PersonContentRepository() }

    private val mSelectionUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiState = mSelectionUIModel.uiState

    private var pageIndex = 1L
    private var pageSize = 20L


    fun loadData(isLoadMore: Boolean, type: Long, userId: Long) {
        viewModelScope.launch(main) {
            var unreleasedList: CommHasMoreList<MultiTypeBinder<*>>? = null
            if (!isLoadMore) {
                pageIndex = 1L
            }
            if (pageIndex == 1L && userId == UserStore.instance.getUser()?.userId)//第一页且是主态账户才获取未发布内容TODO userId判断
            {
                val unReleased = withOnIO {
                    repo.loadDataUnreleased(pageIndex, pageSize, type, userId)
                }
                checkResult(unReleased, success = {
                    unreleasedList = it
                })
            }

            val released = withOnIO {
                repo.loadDataReleased(pageIndex, pageSize, type, userId)
            }

            val list = mutableListOf<MultiTypeBinder<*>>()

            if (unreleasedList?.list?.isNotEmpty() == true) {
                unreleasedList?.list.let { unList ->
                    if (unList != null) {
                        list.addAll(unList)
                    }
                }
            }
            checkResult(released, isEmpty = {
                it.list.isNullOrEmpty()
            }, empty = {
                //未审核数据非空也要显示
                if (list.isNotEmpty()) {
                    mSelectionUIModel.emitUIState(
                            success = list,
                            loadMore = isLoadMore,
                            noMoreData = true
                    )
                } else mSelectionUIModel.emitUIState(
                        loadMore = isLoadMore,
                        isEmpty = true)
            }, error = {
                if (list.isNotEmpty()) {
                    mSelectionUIModel.emitUIState(
                            success = list,
                            loadMore = isLoadMore,
                            noMoreData = true
                    )
                } else mSelectionUIModel.emitUIState(error = it, loadMore = isLoadMore)
            }, netError = {
                if(list.isNotEmpty()){
                    mSelectionUIModel.emitUIState(
                            success = list,
                            loadMore = isLoadMore,
                            noMoreData = true
                    )
                }else mSelectionUIModel.emitUIState(netError = it, loadMore = isLoadMore)
            }, needLogin = {
                mSelectionUIModel.emitUIState(needLogin = true)
            }, success = {
                list.addAll(it.list)
                ++pageIndex
                mSelectionUIModel.emitUIState(
                        success = list,
                        loadMore = isLoadMore,
                        noMoreData = !it.hasMore
                )
            })
        }
    }
}