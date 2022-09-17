package com.kotlin.android.image.component.photo

import android.net.Uri
import com.kotlin.android.app.data.entity.image.PhotoInfo
import java.io.Serializable

/**
 * 相册
 *
 * Created on 2020/7/26.
 *
 * @author o.s
 */
data class PhotoBucket(
        var id: Long = 0,
        var name: String? = null,
        var count: Int = 0,
        var mimeType: String? = null,
        var thumbnailUri: Uri? = null,

        var photoInfo: PhotoInfo? = null,
        var isCheck: Boolean = false,
) : Serializable {
    override fun toString(): String {
        return "\nid = $id, name = $name, count = $count, mimeType = $mimeType, thumbnailUri = $thumbnailUri"
    }
}