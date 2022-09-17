package com.kotlin.android.community.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.community.album.AlbumCreate
import com.kotlin.android.retrofit.getRequestBody
import okhttp3.RequestBody

/**
 * @author WangWei
 * @date 2020/9/23
 *
 * 社区-个人主页-创建相册 数据仓库
 */
class PersonCreateAlbumRepository : BaseRepository() {

    /**
     * 图片列表
     */
    suspend fun loadData(name: String):
            ApiResult<AlbumCreate> {
        return request {
            apiMTime.createAlbum(getRequestBody(name))
        }
    }

    /**
     * 获取请求体
     */
    private fun getRequestBody(name: String): RequestBody {
        val params = arrayMapOf<String, Any>("name" to name)
        return getRequestBody(params)
    }
}