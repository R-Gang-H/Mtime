package com.kotlin.android.app.data.entity.image

import android.graphics.Rect
import android.net.Uri
import java.io.Serializable

/**
 * 照片信息
 *
 * Created on 2020/7/26.
 *
 * @author o.s
 */
data class PhotoInfo(
        var id: Long = 0,
        var name: String = "",
        var bucketId: Long = 0,
        var bucketName: String = "",
        var uri: Uri? = null,
        var path: String = "", // 图片原始地址
        var uploadPath: String = "", // 上传图片地址
        var orientation: Int = 0,
        var status: Boolean = false,
        var isCamera: Boolean = false,
        var isCheck: Boolean = false,
        var isFocus: Boolean = false,
        var rect: Rect? = null,
        var localWidth: Int = 0,
        var localHeight: Int = 0,
        var localSize: Long = 0,
        var localDateAdded: Long = 0,
        var localDateModified: Long = 0,
        var localDuration: Int = 0,
        var localOriginal: String? = null,
        var localPkgName: String? = null,
        var localVolumeName: String? = null,

        var success: Boolean = false,//是否上传成功
        var errInfo: String? = "",//错误信息
        var fileID: String? = "",//图片信息
        var url: String? = "",//图片链接地址（上传图片返回地址）
        var fileSize: Long = 0L,//本地使用 文件大小
        var width: Long = 0L,//本地使用图片宽度
        var height: Long = 0L,//本地使用 图片高度
        var imageFormat: String = "jpg"//本地使用 图片格式

) : Serializable {
//    override fun toString(): String {
//        return """
//            id=$id
//            name=$name
//            bucketId=$bucketId
//            bucketName=$bucketName
//            uri=$uri
//            path=$path
//            uploadPath=$uploadPath
//            orientation=$orientation
//            status=$status
//            isCamera=$isCamera
//            isCheck=$isCheck
//            rect=$rect
//            localWidth=$localWidth
//            localHeight=$localHeight
//            localSize=$localSize
//            localDuration=$localDuration
//            success=$success
//            errInfo=$errInfo
//            fileID=$fileID
//            url=$url
//            fileSize=$fileSize
//            width=$width
//            height=$height
//            imageFormat=$imageFormat
//        """.trimIndent()
//    }

    companion object {
        fun copy(dstPhotoInfo: PhotoInfo,
                 srcPhotoInfo: PhotoInfo,
                 size: Long,
                 width: Long,
                 height: Long,
                 imageFormat: String
        ): PhotoInfo {
            dstPhotoInfo.id = srcPhotoInfo.id
            dstPhotoInfo.name = srcPhotoInfo.name
            dstPhotoInfo.bucketId = srcPhotoInfo.bucketId
            dstPhotoInfo.bucketName = srcPhotoInfo.bucketName
            dstPhotoInfo.uri = srcPhotoInfo.uri
            dstPhotoInfo.path = srcPhotoInfo.path
            dstPhotoInfo.uploadPath = srcPhotoInfo.uploadPath
            dstPhotoInfo.status = srcPhotoInfo.status
            dstPhotoInfo.isCamera = srcPhotoInfo.isCamera
            dstPhotoInfo.isCheck = srcPhotoInfo.isCheck
            dstPhotoInfo.rect = srcPhotoInfo.rect

            dstPhotoInfo.fileSize = size
            dstPhotoInfo.width = width
            dstPhotoInfo.height = height
            dstPhotoInfo.imageFormat = imageFormat
            return dstPhotoInfo
        }
    }

    fun update(
            photoInfo: PhotoInfo,
            width: Long,
            height: Long,
            size: Long,
            imageFormat: String
    ): PhotoInfo {
        /**
         * api
         */
        this.success = photoInfo.success
        this.errInfo = photoInfo.errInfo
        this.fileID = photoInfo.fileID
        this.url = photoInfo.url

        /**
         * native
         */
        this.width = width
        this.height = height
        this.fileSize = size
        this.imageFormat = imageFormat

        return this
    }

    fun update(
            photoInfo: PhotoInfo,
    ): PhotoInfo {
        /**
         * api
         */
        this.success = photoInfo.success
        this.errInfo = photoInfo.errInfo
        this.fileID = photoInfo.fileID
        this.url = photoInfo.url

        /**
         * native
         */
        this.width = photoInfo.width
        this.height = photoInfo.height
        this.fileSize = photoInfo.fileSize
        this.imageFormat = photoInfo.imageFormat

        return this
    }
}