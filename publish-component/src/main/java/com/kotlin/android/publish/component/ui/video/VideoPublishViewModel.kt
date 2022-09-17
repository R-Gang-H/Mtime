package com.kotlin.android.publish.component.ui.video

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.callParallel
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.CommonResultExtend
import com.kotlin.android.app.data.entity.common.PublishResult
import com.kotlin.android.app.data.entity.community.content.ContentInit
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.publish.component.repo.VideoPublishRepository

/**
 * create by lushan on 2022/3/29
 * des:视频发布
 **/
class VideoPublishViewModel : DetailBaseViewModel() {
    private val repo by lazy { VideoPublishRepository() }
    private val classUIModel = BaseUIModel<ContentInit>()
    val classState = classUIModel.uiState

    private val videoRecordUIModel = BaseUIModel<Map<Int, *>>()
    val videoRecordState = videoRecordUIModel.uiState

    //保存视频详情内容
    private val saveContentUIModel =
        BaseUIModel<CommonResultExtend<PublishResult, Pair<Boolean, Boolean>>>()
    val saveContentState = saveContentUIModel.uiState

    //发起上传视频
    private val applyUploadUIModel = BaseUIModel<ApplyUpload>()
    val applyUploadState = applyUploadUIModel.uiState

    //上传完成
    private val completeUIModel = BaseUIModel<CommBizCodeResult>()
    val completeState = completeUIModel.uiState


    /**
     * 加载视频分类列表
     */
    fun loadClassifiesData() {
        call(uiModel = classUIModel,
            api = { repo.postMovieClassifies() })
    }

    /**
     * 加载视频详情内容
     */
    fun loadRecordData(contentId: Long,recId:Long) {
        callParallel({
            repo.postMovieClassifies()
        }, {
            repo.getDetail(contentId,recId)
        }, uiModel = videoRecordUIModel
        ) {
            it
        }
    }

    /**
     * 保存发布视频内容
     */
    fun postContent(postContentBean: PostContent, isPublish: Boolean, finished: Boolean) {
        call(
            uiModel = saveContentUIModel,
            api = { repo.postVideoDetail(postContentBean, isPublish, finished) }
        )
    }

    /**
     * 发起视频上传获取一次性token
     */
    fun applyUpload(filName: String) {
        call(uiModel = applyUploadUIModel,
            api = { repo.applyUpload(filName) })
    }

    /**
     * 视频在腾讯云点播上传完成后同步给服务器
     */
    fun completeUpload(videoId: Long, mediaId: String, mediaUrl: String) {
        call(uiModel = completeUIModel,
            api = { repo.completeUpload(videoId, mediaId, mediaUrl) })
    }
}