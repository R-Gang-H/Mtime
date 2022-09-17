package com.kotlin.android.ugc.detail.component.ui.album

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.community.album.AlbumUpdate
import com.kotlin.android.ugc.detail.component.repository.UpdateAlbumRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/10/12
 * description:
 */
class UpdateAlbumViewModel :BaseViewModel(){
    private val repo by lazy{UpdateAlbumRepository()}

//    修改相册
    private val updateUIModel = BaseUIModel<AlbumUpdate>()
    val updateState = updateUIModel.uiState


    fun updateAlbum(id:Long,name:String){
        viewModelScope.launch(main){
            updateUIModel.showLoading = true
            val result = withOnIO {
                repo.uploadAlubmName(id,name)
            }

            updateUIModel.checkResultAndEmitUIState(result)
        }
    }
}