package com.kotlin.android.image.component.viewmodel

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.repository.PhotoAlbumRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/27
 * description:选择相片
 */
class PhotoAlbumViewModel:BaseViewModel() {
    private val repo by lazy {
        PhotoAlbumRepository()
    }
    private val uploadImageUIModel = BaseUIModel<PhotoInfo>()
    val uploadImageState = uploadImageUIModel.uiState

    /**
     * 上传图片
     */
    fun uploadImage(@UploadImageType imageType:Long, file: PhotoInfo){
        viewModelScope.launch(main){
            uploadImageUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.uploadImage(imageType, file)
            }

            uploadImageUIModel.checkResultAndEmitUIState(result)
        }
    }

}