package com.kotlin.android.image.component.scope

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.repository.PhotoAlbumRepository
import kotlinx.coroutines.*

/**
 *
 * Created on 2022/5/14.
 *
 * @author o.s
 */
class PhotoUploadScope private constructor() {

    companion object {
        val instance by lazy { PhotoUploadScope() }
    }

    private val repo by lazy {
        PhotoAlbumRepository()
    }

    /**
     * 批量上传图片
     *
     * [photos] 等待上传的图片列表
     * [success] 上传成功的列表
     * [error] 上传失败的列表
     */
    fun upload(
        photos: ArrayList<PhotoInfo>,
        progressBar: ((isShow: Boolean) -> Unit)? = null,
        error: (ArrayList<PhotoInfo>) -> Unit,
        success: (ArrayList<PhotoInfo>) -> Unit,
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                progressBar?.invoke(true)
                val successList = ArrayList<PhotoInfo>()
                val errorList = ArrayList<PhotoInfo>()
                withContext(Dispatchers.IO) {
                    val deferredList = ArrayList<Deferred<ApiResult<PhotoInfo>>>()
                    val resultList = ArrayList<ApiResult<PhotoInfo>>()
                    photos.forEach { photo ->
                        val deferred = async { repo.uploadImage(imageType = CommConstant.IMAGE_UPLOAD_COMMON, file = photo) }
                        deferredList.add(deferred)
                    }
                    deferredList.forEach {
                        resultList.add(it.await())
                    }

                    resultList.forEachIndexed { index, apiResult ->
                        if (apiResult is ApiResult.Success) {
                            successList.add(apiResult.data)
                        } else {
                            errorList.add(photos[index])
                        }
                    }
                }
                error(errorList)
                success(successList)
                progressBar?.invoke(false)
                // 取消协程
                main.cancel()
            }
        }
    }
}