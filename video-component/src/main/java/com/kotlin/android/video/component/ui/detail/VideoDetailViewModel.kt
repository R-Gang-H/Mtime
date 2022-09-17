package com.kotlin.android.video.component.ui.detail

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.entity.video.VideoDetail
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.video.component.repository.VideoDetailRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/2
 * description: 视频详情VideoModel
 */
class VideoDetailViewModel:DetailBaseViewModel() {
    private val repo:VideoDetailRepository by lazy {
        VideoDetailRepository()
    }
//    视频详情uiModel
    private val videoDetailUIModel = BaseUIModel<VideoDetail>()
    val videoDetailState = videoDetailUIModel.uiState


//    视频点赞点踩状态
    private val videoPraiseStateUIModel = BaseUIModel<PraiseState>()
    val videoPraiseState = videoPraiseStateUIModel.uiState


    /**
     * 加载视频详情
     */
    fun loadVideoDetail(videoId:Long,locationId:Long){
        viewModelScope.launch(main){
            videoDetailUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadVideoDetail(videoId, locationId)
            }
            videoDetailUIModel.checkResultAndEmitUIState(result)
        }
    }



    /**
     * 获取视频点赞信息
     */
    fun getVideoPraiseState(videoId: Long){
        viewModelScope.launch(main){
            val result = withOnIO {
                repo.getVideoPraiseState(videoId)
            }
            videoPraiseStateUIModel.checkResultAndEmitUIState(result)
        }
    }


}