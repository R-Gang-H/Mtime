package com.kotlin.android.image.component.repository

import android.graphics.BitmapFactory
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.ktx.ext.io.size
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * create by lushan on 2020/9/27
 * description:选择相册上传图片
 */
class PhotoAlbumRepository : BaseRepository() {


    /**
     * com.github.nanchen2251:CompressHelper:1.0.6
     * 压缩图片
    val imageFormat = file.path.substring(file.path.lastIndexOf("."))
    val compressToFile = CompressHelper.Builder(CoreApp.instance).setQuality(80).
    setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath)
    .setMaxWidth(width.toFloat())
    .setMaxHeight(height.toFloat())
    .build().compressToFile(File(file.path))
    val size = File(compressToFile.absolutePath).size//文件大小
    "width=$width,height=$height,size=$size,bitmap$bitmap,imageFormat=$imageFormat,compressToFile:${compressToFile.path}".e()
    //文件封装
    val requestBody: RequestBody = compressToFile.asRequestBody("image/$imageFormat".toMediaTypeOrNull())
    val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
    "file", compressToFile.absolutePath, requestBody)
     */

    suspend fun uploadImage(
        @UploadImageType imageType: Long,
        file: PhotoInfo
    ): ApiResult<PhotoInfo> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFile(file.path, options)

        val width = options.outWidth
        val height = options.outHeight
        val size = File(file.path).size//文件大小
        val imageFormat = file.path.substring(file.path.lastIndexOf(".") + 1)
        //文件封装
        val requestBody: RequestBody =
            File(file.path).asRequestBody("image/$imageFormat".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file", file.path, requestBody
        )
        return request(
            converter = {
//               PhotoInfo.copy(it,file,size.toLong(),width.toLong(),height.toLong(), imageFormat)
                file.update(
                    photoInfo = it,
                    width = width.toLong(),
                    height = height.toLong(),
                    size = size,
                    imageFormat = imageFormat
                )
            },
            api = {
                apiMTime.imageUpload(VariateExt.imgUploadUrl, filePart, imageType)
            }
        )
    }
}
