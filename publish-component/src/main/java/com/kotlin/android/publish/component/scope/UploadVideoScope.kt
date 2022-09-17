package com.kotlin.android.publish.component.scope

import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.api.upload.TencentUploadManager
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.publish.component.bean.VideoUploadViewBean
import com.kotlin.android.publish.component.repo.EditorRepository
import kotlinx.coroutines.*

/**
 * 视频上传的完整封装
 *
 * Created on 2022/4/27.
 *
 * @author o.s
 */
class UploadVideoScope(
    val path: String,
    val width: Int? = null,
    val height: Int? = null
) {

    val repo by lazy { EditorRepository() }

    fun upload(
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        progress: ((Int) -> Unit)? = null,
        success: VideoUploadViewBean.() -> Unit,
    ) {
        progress?.invoke(0)
        // 第一步：准备上传拿token
        preUpload(
            error = error,
            netError = netError,
        ) {
            // 第二步：根据拿到的token上传到腾讯云
            uploadToTencent(
                token = token.orEmpty()
            ) { complete, isSuccess, progressValue, mediaId, mediaUrl, coverUrl ->
                if (complete) {//上传完成
                    if (isSuccess) {
                        progress?.invoke(100)
                        // 第三步：根据腾讯云上传的成功结果，同步上传状态，完成上传
                        completeUpload(
                            videoId = videoId,
                            mediaId = mediaId,
                            mediaUrl = mediaUrl,
                            error = error,
                            netError = netError,
                        ) {
                            if (isSuccess()) {
                                success(
                                    VideoUploadViewBean(
                                        width = width,
                                        height = height,
                                        path = path,
                                        videoId = videoId,
                                        mediaId = mediaId,
                                        mediaUrl = mediaUrl,
                                        coverUrl = coverUrl,
                                    )
                                )
                                progress?.invoke(-1)
                            } else {
                                error?.invoke("上传视频失败")
//                                progress?.invoke(-2)
                            }
                        }
                    } else {
                        error?.invoke("上传视频失败")
//                        progress?.invoke(-2)
                    }
                } else {
                    progress?.invoke((progressValue * 100).toInt())
                }
            }
        }
    }

    /**
     * 第一步：准备上传拿token
     */
    private fun preUpload(
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        success: ApplyUpload.() -> Unit
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.applyUpload(path)
                }

                val result = deferred.await()

                result.i()

                checkResult(
                    result = result,
                    error = {
                        error?.invoke(it)
                    },
                    netError = {
                        netError?.invoke(it)
                    },
                    success = {
                        success.invoke(it)
                    }
                )

                // 取消协程
                main.cancel()
            }
        }
    }

    /**
     * 第二步：根据拿到的token上传到腾讯云
     */
    private fun uploadToTencent(
        token: String,
        listener: ((Boolean, Boolean, Double, String, String, String) -> Unit)? = null
    ) {
        TencentUploadManager.upload(
            videoPath = path,
            token = token,
            listener = listener
        )
    }

    /**
     * 第三步：根据腾讯云上传的结果，完成上传
     */
    private fun completeUpload(
        videoId: Long,
        mediaId: String,
        mediaUrl: String,
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        success: CommBizCodeResult.() -> Unit
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.completeUpload(
                        videoId = videoId,
                        mediaId = mediaId,
                        mediaUrl = mediaUrl,
                    )
                }

                val result = deferred.await()

                result.i()

                checkResult(
                    result = result,
                    error = {
                        error?.invoke(it)
                    },
                    netError = {
                        netError?.invoke(it)
                    },
                    success = {
                        success.invoke(it)
                    }
                )
                // 取消协程
                main.cancel()
            }
        }
    }
}