package com.kotlin.android.community.ui.person.center.photo

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.community.repository.PersonCreateAlbumRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.community.album.AlbumCreate
import kotlinx.coroutines.launch

/**
 * @author WangWei
 * @date 2020/9/29
 */
class CreateAlbumViewModel : BaseViewModel() {
    private val repo by lazy { PersonCreateAlbumRepository() }

    private val mUIModel = BaseUIModel<AlbumCreate>()

    val uiState = mUIModel.uiState

    fun loadData(name: String) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                repo.loadData(name)
            }

            checkResult(result, error = {
                mUIModel.emitUIState(error = it)
            }, netError = {
                mUIModel.emitUIState(netError = it)
            }, needLogin = {
                mUIModel.emitUIState(needLogin = true)
            }, success = {
                mUIModel.emitUIState(success = it)
            })
        }
    }
}