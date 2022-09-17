package com.kotlin.android.community.family.component.ui.details

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.data.entity.community.post.PostReleasedList
import com.kotlin.android.community.family.component.comm.FamilyCommViewModel
import com.kotlin.android.community.family.component.repository.FamilyDetailRepository
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.community.post.component.item.bean.CommunityPostItem
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/29
 */
class FamilyDetailViewModel : FamilyCommViewModel<FamilyDetail>() {

    private val repo by lazy { FamilyDetailRepository() }

    private val mFamilyDetailUIModel = BaseUIModel<FamilyDetail>()

    val uiDetailState = mFamilyDetailUIModel.uiState

    private val mFamilySectionUIModel = BaseUIModel<GroupSectionList>()

    val mFamilySectionUIModelState = mFamilySectionUIModel.uiState

    private val mFamilyPostListUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()

    val uiListState = mFamilyPostListUIModel.uiState

    private val mFamilyPostListStampUIModel =
        BinderUIModel<PostReleasedList, List<MultiTypeBinder<*>>>()

    val uiListStampState = mFamilyPostListStampUIModel.uiState

    private val mAddSectionUIModel = BaseUIModel<CommonResult>()

    val mAddSectionUIModelState = mAddSectionUIModel.uiState

    var mDetailId: Long = 0
    private var pageIndex = 1L
    private var pageSize = 20L

    /**
     * 获取家族简介
     */
    fun loadFamilyDetail() {
        viewModelScope.launch(main) {
            mFamilyDetailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadFamilyDetail(mDetailId)
            }

            checkResult(result, error = {
                mFamilyDetailUIModel.emitUIState(error = it)
            }, netError = {
                mFamilyDetailUIModel.emitUIState(netError = it)
            }, needLogin = {
                mFamilyDetailUIModel.emitUIState(needLogin = true)
            }, success = {
                mFamilyDetailUIModel.emitUIState(success = it)
            })
        }
    }

    /**
     * 获取家族分组
     */
    fun loadFamilySection() {
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.loadFamilySection(mDetailId)
            }
            checkResult(
                result = result,
                error = {
                    mFamilySectionUIModel.emitUIState(error = it)
                },
                netError = {
                    mFamilySectionUIModel.emitUIState(netError = it)
                },
                needLogin = {
                    mFamilySectionUIModel.emitUIState(needLogin = true)
                },
                success = {
                    mFamilySectionUIModel.emitUIState(success = it)
                }
            )
        }
    }

    /**
     * 加载帖子数据
     *
     * @param isLoadMore 是否是加载更多
     * @param type 1.最新 2.最热 3.精华
     */
    fun loadPostList(isLoadMore: Boolean, type: Long) {
        viewModelScope.launch(main) {
            var unreleasedList: List<MultiTypeBinder<*>>? = null
            if (!isLoadMore) {
                pageIndex = 1L
                mFamilyPostListUIModel.emitUIState(showLoading = true)

                if (type != 3L && isLogin()) {
                    //精华：【未审核】帖子在精华帖子列表 不出现
                    val resultUnreleased = withOnIO {
                        repo.loadPostUnreleased(mDetailId)
                    }
                    checkResult(resultUnreleased, success = {
                        unreleasedList = it
                    })
                }
            }

            val resultReleased = withOnIO {
                val essence = type == 3L
                val sort = if (essence) 1L else type
                repo.loadPostReleased(mDetailId, essence, sort, pageIndex, pageSize)
            }

            checkResult(resultReleased, error = {
                mFamilyPostListUIModel.emitUIState(
                    error = it,
                    loadMore = isLoadMore
                )
            }, netError = {
                mFamilyPostListUIModel.emitUIState(
                    netError = it, loadMore = isLoadMore
                )
            }, success = {
                ++pageIndex
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.topList?.let { topList ->
                    list.addAll(topList)
                }
                unreleasedList?.let { unList ->
                    list.addAll(unList)
                }
                list.addAll(it.list)
                mFamilyPostListUIModel.emitUIState(
                    success = list,
                    loadMore = isLoadMore,
                    noMoreData = !it.hasMore
                )
            })
        }
    }

    /**
     * 加载帖子数据
     */
    fun loadPostListByStamp(isRefresh: Boolean, type: Long, sectionId: Long, groupID: Long) {
        viewModelScope.launch(main) {
            val essence = type == 3L
            val sort = if (essence) 1L else type
            call(
                uiModel = mFamilyPostListStampUIModel,
                pageStamp = { it.nextStamp },
                hasMore = { it.hasNext },
                isRefresh = isRefresh,
                converter = {
                    val list = mutableListOf<CommunityPostBinder>()
                    it.items?.forEach { item ->
                        list.add(CommunityPostItem.converter2Binder(item))
                    }
                    list
                }
            ) {
                repo.loadPostReleasedV2(
                    groupID,
                    sectionId,
                    essence,
                    sort,
                    mFamilyPostListStampUIModel.pageStamp ?: "",
                    mFamilyPostListStampUIModel.pageSize
                )
            }
        }


    }

    /**
     * 添加分区
     */
    fun addFamilySection(groupID: Long, sectionName: String) {
        call(
            uiModel = mAddSectionUIModel
        ) {
            repo.addFamilySection(groupID, sectionName)
        }
    }
}