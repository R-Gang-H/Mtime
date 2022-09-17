package com.kotlin.android.ugc.detail.component.ui.album

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.app.data.entity.community.album.AlbumInfo
import com.kotlin.android.app.data.entity.community.album.AlbumUpdate
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumItemViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumItemBinder
import com.kotlin.android.ugc.detail.component.repository.UgcAlbumRepository
import com.kotlin.android.user.UserManager
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/8/12
 * description:
 */
class UgcAlbumViewModel : DetailBaseViewModel() {
    private val repo by lazy {
        UgcAlbumRepository()
    }
    private val _titleBar = MutableLiveData<UgcTitleBarBean>()

    val titleBar: LiveData<UgcTitleBarBean>
        get() = _titleBar

    fun setTitleBar() {
        _titleBar.value = UgcTitleBarBean()
    }

    //    相册名称详情
    private val detailUIModel = BaseUIModel<AlbumInfo>()

    val detailState = detailUIModel.uiState


    //    相册图片
    private val albumUIModel: BaseUIModel<UgcAlbumViewBean> = BaseUIModel<UgcAlbumViewBean>()

    val albumState = albumUIModel.uiState

    //    删除相册
    private val deleteAlbumUIModel = BaseUIModel<AlbumUpdate>()
    val deleteAlbumState = deleteAlbumUIModel.uiState

    //    上传相册图片
    private val uploadImageAlbumUIModel = BaseUIModel<MutableList<UgcAlbumItemBinder>>()
    val uploadImageAlbumState = uploadImageAlbumUIModel.uiState

    private var upLoadSuccessPhotoList: MutableList<PhotoInfo> = mutableListOf()//某次上传成功的集合

    /**
     * 加载相册详情
     */
    fun loadDetailData(albumId: Long, userId: Long) {
        viewModelScope.launch(main) {
            detailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadDetailData(albumId, userId == UserManager.instance.userId && userId != -1L)
            }

            checkResult(result, error = { detailUIModel.emitUIState(error = it) },
                    netError = { detailUIModel.emitUIState(netError = it) },
                    success = { detailUIModel.emitUIState(success = it) },
                    empty = { detailUIModel.emitUIState(isEmpty = true) }
            )
        }
    }

    fun loadAlbumListData(albumId: Long, userId: Long, isLoadMore: Boolean) {
        viewModelScope.launch(main) {
            val withOnIO = withOnIO {
                var list = arrayListOf<Long>()
                list.add(1L)//正常
                if (userId == UserManager.instance.userId && userId != -1L) {
                    list.add(4L)//待审核
                }
                repo.loadAlbumImageListData(albumId, userId, list, isLoadMore)
            }

            checkResult(withOnIO, error = { albumUIModel.emitUIState(error = it) },
                    netError = { albumUIModel.emitUIState(netError = it) },
                    success = { albumUIModel.emitUIState(success = it, loadMore = isLoadMore) })
        }
    }


    /**
     * 删除相册
     */
    fun deleteAlbum(albumId: Long) {
        viewModelScope.launch(main) {
            deleteAlbumUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.deleteAlbum(albumId)
            }
            deleteAlbumUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 往相册中上传图片
     */
    fun uploadImageByAlbumId(albumId: Long, imageList: MutableList<PhotoInfo>) {
        viewModelScope.launch(main) {
            uploadImageAlbumUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.uploadImageInAlbum(albumId, imageList)
            }

            uploadImageAlbumUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun setUploadImageList(imageList: MutableList<PhotoInfo>) {
        upLoadSuccessPhotoList.clear()
        upLoadSuccessPhotoList.addAll(imageList)
    }

    fun getAlbumImageBinder(): MutableList<UgcAlbumItemBinder> {
        return upLoadSuccessPhotoList.map { UgcAlbumItemBinder(UgcAlbumItemViewBean(0L, it.url.orEmpty())) }.toMutableList()

    }

}