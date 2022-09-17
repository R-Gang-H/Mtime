package com.kotlin.android.publish.component.bean

/**
 *
 * Created on 2022/4/27.
 *
 * @author o.s
 */
data class VideoUploadViewBean(
    var width: Int? = null,
    var height: Int? = null,
    var path: String,
    var videoId: Long,
    var mediaId: String,
    var mediaUrl: String,
    var coverUrl: String,
)
