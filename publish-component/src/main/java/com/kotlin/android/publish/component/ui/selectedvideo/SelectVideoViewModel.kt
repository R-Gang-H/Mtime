package com.kotlin.android.publish.component.ui.selectedvideo

import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.publish.component.widget.selector.PictureSelectionConfig
import com.kotlin.android.publish.component.widget.selector.loadAllVideo

/**
 * create by lushan on 2022/4/6
 * des:
 **/
class SelectVideoViewModel : BaseViewModel() {
    var videoUIModel: BaseUIModel<ArrayList<LocalMedia>> = BaseUIModel()
    val videoState = videoUIModel.uiState
    private var allVideoList: ArrayList<LocalMedia> = arrayListOf()
    private var pageIndex = 0
    private var PAGESIZE = 20

    fun getAllVideoData(isRefresh: Boolean) {
        call(
            videoUIModel,
            isShowLoading = true,
            api = {
                loadVideo(isRefresh)
            },
            isRefresh = isRefresh,
            hasMore = {
                it.isEmpty().not()
            }
        )
    }

    private suspend fun loadVideo(isRefresh: Boolean): ApiResult<ArrayList<LocalMedia>> {
        val videoList: ArrayList<LocalMedia> = arrayListOf()
        if (isRefresh || allVideoList.isEmpty()) {
            PictureSelectionConfig.cleanInstance?.apply {
                selectMaxDurationSecond = 5 * 60
                selectMaxFileSize = 50 * 1024 * 1024
                isMp4 = true
            }?.also {
                var loadAllVideo = loadAllVideo(it)
                allVideoList.clear()
                allVideoList.addAll(loadAllVideo)
                videoList.addAll(loadAllVideo.take(PAGESIZE))
            }

        } else {
            if (pageIndex * PAGESIZE < allVideoList.size) {
                videoList.addAll(
                    allVideoList.subList(pageIndex * PAGESIZE, allVideoList.size).take(PAGESIZE)
                )
            }
        }
        pageIndex++
        return ApiResult.Success(videoList)

    }
}