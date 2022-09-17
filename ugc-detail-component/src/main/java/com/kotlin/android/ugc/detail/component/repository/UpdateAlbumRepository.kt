package com.kotlin.android.ugc.detail.component.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.album.AlbumUpdate
import com.kotlin.android.retrofit.toRequestBody

/**
 * create by lushan on 2020/10/12
 * description:
 */
class UpdateAlbumRepository : BaseRepository() {
    /**
     * 修改相册
     */
    suspend fun uploadAlubmName(id: Long, name: String): ApiResult<AlbumUpdate> {
        val body = arrayMapOf("id" to id, "name" to name).toRequestBody()
        return request { apiMTime.upateAlbum(body) }
    }
}