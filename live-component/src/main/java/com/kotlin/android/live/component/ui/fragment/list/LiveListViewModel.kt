package com.kotlin.android.live.component.ui.fragment.list

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.CommonResultExtend
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.live.LiveAppointResult
import com.kotlin.android.live.component.repository.LiveListRepository
import com.kotlin.android.live.component.ui.fragment.list.adapter.LiveListItemBinder
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2021/3/2
 * description:直播列表viewModel
 */
class LiveListViewModel : BaseViewModel() {
    private val repo by lazy { LiveListRepository() }
    private val mUIModel = BaseUIModel<List<MultiTypeBinder<*>>>()
    private val mUILiveAppointModel = BaseUIModel<CommonResultExtend<LiveAppointResult, LiveListItemBinder>>()
    val uiState = mUIModel.uiState
    val uiLiveAppointState = mUILiveAppointModel.uiState

    private var pageIndex = 2L
    private var pageSize = 20L

    /**
     * 加载直播列表页总接口
     */
    fun getLiveList() {
        viewModelScope.launch {
            pageIndex = 2L
            val result = withOnIO {
                repo.getLiveList()
            }
            mUIModel.checkResultAndEmitUIState(result = result, isEmpty = {
                it.isNullOrEmpty()
            })
        }
    }

    /**
     * 精彩回放模块（/live_room/getWonderVodList）
     */
    fun getLiveWonderVodList() {
        viewModelScope.launch {
            val result = withOnIO {
                repo.getLiveWonderVodList(pageIndex, pageSize)
            }

            checkResult(result, error = {
                mUIModel.emitUIState(error = it, loadMore = true)
            }, netError = {
                mUIModel.emitUIState(netError = it, loadMore = true)
            }, needLogin = {
                mUIModel.emitUIState(needLogin = true, loadMore = true)
            }, success = {
                ++pageIndex
                mUIModel.emitUIState(
                        success = it,
                        loadMore = true,
                        noMoreData = it.isNullOrEmpty()
                )
            })
        }
    }

    /**
     * 直播预约（/live_room/appoint）
     */
    fun getLiveAppoint(liveId: Long, extend: LiveListItemBinder) {
        afterLogin {
            viewModelScope.launch {
                mUILiveAppointModel.emitUIState(showLoading = true)

                val result = withOnIO {
                    repo.getLiveAppoint(liveId, extend)
                }

                mUILiveAppointModel.checkResultAndEmitUIState(result = result)
            }
        }
    }
}