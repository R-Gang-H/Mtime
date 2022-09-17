package com.kotlin.android.community.ui.person.bean

import com.kotlin.android.community.ui.person.binder.CommunityPersonPhotoBinder
import com.kotlin.android.app.data.entity.community.person.AlbumList
import com.kotlin.android.app.data.ProguardRule

/**
 * @author WangWei
 * @date 2020.9.27
 *社区个人主页图片 item
 */
data class CommunityPhotoViewBean(
        var coverUserImageUrl: String? = "",
        var description: String = "",
        var fileName: String = "",
        var id: Long = 0,
        var memory: Long = 0,
        var name: String? = "",
        var photoNumber: Long = 0
): ProguardRule {
    companion object {
        fun converter2Binder(item: AlbumList.AlbumItem): CommunityPersonPhotoBinder {
            var data = CommunityPhotoViewBean()
            data.id = item.id
            data.name = item.name
            data.photoNumber = item.photoNumber
            data.coverUserImageUrl = item.firstFileUrl
            return CommunityPersonPhotoBinder(data)
        }
    }
}