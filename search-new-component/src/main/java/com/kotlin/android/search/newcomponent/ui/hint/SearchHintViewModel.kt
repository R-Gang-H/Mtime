package com.kotlin.android.search.newcomponent.ui.hint

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.search.newcomponent.repo.SearchRepository
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/21
 */
class SearchHintViewModel : BaseViewModel() {
    private val repo by lazy { SearchRepository() }

    private val uiModel by lazy { BaseUIModel<List<MultiTypeBinder<*>>>() }
    val uiState = uiModel.uiState
    private var loadHintJop: Job? = null

    fun loadHint(keyword: String,
                 locationId: Long,
                 longitude: Double,
                 latitude: Double) {
        loadHintJop?.let {
            if (it.isActive) {
                it.cancel()
            }
        }
        loadHintJop = viewModelScope.launch {
            val result = withOnIO {
                repo.searchSuggest2(keyword, locationId, longitude, latitude)
            }

            uiModel.checkResultAndEmitUIState(result)
        }

    }
}