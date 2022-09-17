package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.ProguardRule

/**
 * create by lushan on 2020/9/29
 * description:上传达到相册照片
 */
data class UploadImageInAlbum(var fileId: String = "",
                              var fileSize: Long = 0L,
                              var width: Long = 0L,
                              var height: Long = 0L): ProguardRule