package com.kotlin.android.community.ui.person.myfriend

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.person.PersonMyFriendList
import com.kotlin.android.community.repository.PersonMyFriendRepository
import com.kotlin.android.community.ui.person.bean.MyFriendViewBean
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @date 2020/9/23
 */
class MyFriendViewModel : BaseViewModel() {
    private val repo by lazy { PersonMyFriendRepository() }

    private val mUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    val uiState = mUIModel.uiState

    val mDataUIModel = BinderUIModel<PersonMyFriendList, List<MultiTypeBinder<*>>>()
    val uiDataState = mDataUIModel.uiState


    val mFollowViewModel = BaseUIModel<CommBizCodeResult>()
    val followUiState = mFollowViewModel.uiState

    //关注
    fun loadDataFollow(isRefresh: Boolean, userId: Long) {
        call(
            uiModel = mDataUIModel,
            isRefresh = isRefresh,
            isEmpty = {
                it.items.isNullOrEmpty()
            },
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            },
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.items.forEachIndexed { index, item ->
                    if (mDataUIModel?.pageIndex == 1L && index == 0) {//第一页第一个 head
                        list.add(MyFriendViewBean.converter2FriendBinder(item, isHead = true))
                    } else if (!it.hasNext && index == it.items.size - 1) {//最后一页的最后一个 tail
                        list.add(MyFriendViewBean.converter2FriendBinder(item, isTail = true))
                    } else list.add(MyFriendViewBean.converter2FriendBinder(item))
                }
                list
            }
        ) {
            repo.loadDataFollow(mDataUIModel?.pageStamp, mDataUIModel?.pageSize, userId)
        }
    }

    //粉丝和关注
    fun loadDataFolowAndFan(isRefresh: Boolean, userId: Long, type: Long) {
        call(
            uiModel = mDataUIModel,
            isRefresh = isRefresh,
            isEmpty = {
                it.items.isNullOrEmpty()
            },
            pageStamp = {
                it.nextStamp
            },
            hasMore = {
                it.hasNext
            },
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.items.forEachIndexed { index, item ->
                    if (mDataUIModel?.pageStamp == null && index == 0) {//第一页第一个 head
                        list.add(
                            MyFriendViewBean.converter2FriendBinder(
                                item,
                                isHead = true,
                                isFollowBinder = type == 0L,
                                count = it.count
                            )
                        )
                    } else if (!it.hasNext && index == it.items.size - 1) {//最后一页的最后一个 tail
                        list.add(
                            MyFriendViewBean.converter2FriendBinder(
                                item,
                                isTail = true,
                                isFollowBinder = type == 0L,
                                count = it.count
                            )
                        )
                    } else list.add(
                        MyFriendViewBean.converter2FriendBinder(
                            item,
                            isFollowBinder = type == 0L,
                            count = it.count
                        )
                    )
                }
                list
            }
        ) {
            when (type) {
                0L -> repo.loadDataFollow(mDataUIModel?.pageStamp, mDataUIModel?.pageSize, userId)
                1L -> repo.loadDataFan(mDataUIModel?.pageStamp, mDataUIModel?.pageSize, userId)
                else -> repo.loadDataFan(mDataUIModel?.pageStamp, mDataUIModel?.pageSize, userId)
            }
        }
    }

    /**
     * 关注用户
     */
    fun follow(userId: Long, action: Long) {
        afterLogin {
            viewModelScope.launch(main) {
                mFollowViewModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    repo.followUser(action, userId)
                }
                mFollowViewModel.emitUIState(showLoading = false)
                checkResult(result,
                    error = { mFollowViewModel.emitUIState(error = it) },
                    netError = { mFollowViewModel.emitUIState(netError = it) },
                    success = { mFollowViewModel.emitUIState(success = it) })
            }
        }

    }

}