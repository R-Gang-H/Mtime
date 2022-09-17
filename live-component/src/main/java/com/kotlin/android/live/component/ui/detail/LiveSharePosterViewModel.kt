package com.kotlin.android.live.component.ui.detail

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.ShareResultExtend
import com.kotlin.android.live.component.repository.LiveDetailRepository
import com.kotlin.android.live.component.ui.adapter.LiveSharePosterItemBinder
import com.kotlin.android.live.component.viewbean.LiveDetailExtraBean
import com.kotlin.android.live.component.viewbean.LiveSharePosterItemViewBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

class LiveSharePosterViewModel : BaseViewModel() {

    private val mRepo by lazy { LiveDetailRepository() }

    // 直播详情
    private val detailUIModel by lazy { BaseUIModel<LiveDetailExtraBean>() }
    val detailUIState by lazy { detailUIModel.uiState }

    // 分享信息
    private val shareExtendUIModel by lazy { BaseUIModel<ShareResultExtend<Any>>() }
    val shareExtendUIState by lazy { shareExtendUIModel.uiState }

    /**
     * 获取直播详情
     */
    fun getLiveDetail(liveId: Long, isFromLogin: Boolean) {
        viewModelScope.launch(main) {
            detailUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.getLiveDetail(liveId, isFromLogin)
            }
            detailUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取分享信息
     */
    fun getShareInfo(liveId: Long, extend: Any) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                mRepo.getShareInfo(liveId, extend)
            }
            shareExtendUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取分享列表
     */
    fun getShareList(): MutableList<MultiTypeBinder<*>> {
        val list = mutableListOf<MultiTypeBinder<*>>()
        list.add(LiveSharePosterItemBinder(LiveSharePosterItemViewBean(LiveSharePosterItemViewBean.PLATFORM_LOCAL)))
        list.add(LiveSharePosterItemBinder(LiveSharePosterItemViewBean(LiveSharePosterItemViewBean.PLATFORM_WECHAT)))
        list.add(LiveSharePosterItemBinder(LiveSharePosterItemViewBean(LiveSharePosterItemViewBean.PLATFORM_FRIEND)))
        list.add(LiveSharePosterItemBinder(LiveSharePosterItemViewBean(LiveSharePosterItemViewBean.PLATFORM_SINA)))
        list.add(LiveSharePosterItemBinder(LiveSharePosterItemViewBean(LiveSharePosterItemViewBean.PLATFORM_QQ)))
        return list
    }

}