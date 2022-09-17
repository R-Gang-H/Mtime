package com.kotlin.android.image.component.repository

import android.graphics.BitmapFactory
import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.mine.AvatarEdit
import com.kotlin.android.app.data.entity.mine.UserCenterBgEdit
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.ktx.ext.io.size
import com.kotlin.android.retrofit.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 上传图片：
 *
 * Created on 2020/12/24.
 *
 * @author o.s
 */
class ImageRepository : BaseRepository() {

    /**
     * 上传图片
     */
    suspend fun uploadImage(
        photo: PhotoInfo,
        @UploadImageType imageType: Long
    ): ApiResult<PhotoInfo> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(photo.uploadPath, options)
        val width = options.outWidth
        val height = options.outHeight
        val size = File(photo.uploadPath).size//文件大小
        val imageFormat = photo.uploadPath.substring(photo.uploadPath.lastIndexOf(".") + 1)
        //文件封装
        val requestBody: RequestBody =
            File(photo.uploadPath).asRequestBody("image/$imageFormat".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file", photo.uploadPath, requestBody
        )
        return request(
            converter = {
                photo.update(
                    photoInfo = it,
                    width = width.toLong(),
                    height = height.toLong(),
                    size = size,
                    imageFormat = imageFormat
                )
            }
        ) {
            apiMTime.imageUpload(VariateExt.imgUploadUrl, filePart, imageType)
        }
    }

    /**
     * 更新头像
     */
    suspend fun updateAvatar(fileName: String): ApiResult<AvatarEdit> {
        return request {
            apiMTime.postAvatarEdit(fileName)
        }
    }
    /**
     * 更新背景图
     */
    suspend fun updateUserCenterUrl(fileName: String): ApiResult<UserCenterBgEdit> {
        var param = arrayMapOf<String,String>()
        param["fileName"] = fileName
        param["type"] = 1.toString()

        return request {
            apiMTime.postUserCenterEdit(param.toRequestBody())
        }
    }
}