package com.kotlin.android.community.ui.person.center

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.community.person.WantSeeInfo
import com.kotlin.android.community.repository.UserHomeRepository
import com.kotlin.android.community.ui.person.bean.UserHomeViewBean
import com.kotlin.android.community.ui.person.bean.WantSeeViewBean
import com.kotlin.android.community.ui.person.binder.CommunityTimeLineBinder
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @data 2020/9/8
 *
 */
class CommunityPersonViewModel : BaseViewModel() {
    companion object {
        const val ACTION_POSITIVE = 1L// 执行
        const val ACTION_CANCEL = 2L//取消执行
    }

    private val repo by lazy { UserHomeRepository() }

    private val usserInfoViewModel = BaseUIModel<UserHomeViewBean>()
    val uiState: LiveData<BaseUIModel<UserHomeViewBean>> =
            usserInfoViewModel.uiState as LiveData<BaseUIModel<UserHomeViewBean>>

    private val mFollowViewModel = BaseUIModel<CommBizCodeResult>()
    val followUiState = mFollowViewModel.uiState

    //时间轴
    val mTimeLineViewModel = BinderUIModel<WantSeeInfo, List<MultiTypeBinder<*>>>()

    fun loadTimeLine(isRefresh: Boolean) {
        call(
                uiModel = mTimeLineViewModel,
                isShowLoading = false,
                isRefresh = isRefresh,
                isEmpty = {
                    it?.items.isEmpty()
                },
                hasMore = {
                    it.hasNext
                },
                pageStamp = { it?.nextStamp },
                api = {
                    repo.loadTimeLine(
                            mTimeLineViewModel?.pageStamp,
                            mTimeLineViewModel?.pageSize
                    )
                },
                converter = {
                    var count = it?.count
                    var binders = arrayListOf<CommunityTimeLineBinder>()
                    it.items?.forEachIndexed { index: Int, it: WantSeeInfo.Movie ->
                        var isFirstItem = mTimeLineViewModel.pageIndex == 1L && index == 0
                        binders?.add(WantSeeViewBean.convertToTimeLineBinder(it, count, isFirstItem = isFirstItem))
                    }
                    binders
                }
        )
    }

    /**
     * 加载数据
     */
    fun loadUserHomeInfo(userId: Long) {
        viewModelScope.launch(main) {
            usserInfoViewModel.emitUIState(showLoading = true)
            var result = withOnIO {
                repo.loadData(userId)
            }
            usserInfoViewModel.emitUIState(showLoading = false)
            checkResult(result, error = {
                usserInfoViewModel.emitUIState(error = it)
            }, success = {
                usserInfoViewModel.emitUIState(success = it)
            }, netError = {
                usserInfoViewModel.emitUIState(netError = it)
            })
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