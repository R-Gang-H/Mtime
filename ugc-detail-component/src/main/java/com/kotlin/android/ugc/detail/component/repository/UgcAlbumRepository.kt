package com.kotlin.android.ugc.detail.component.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.app.data.entity.community.album.AlbumInfo
import com.kotlin.android.app.data.entity.community.album.AlbumUpdate
import com.kotlin.android.app.data.entity.community.album.ImageListInAlbum
import com.kotlin.android.app.data.entity.community.album.SaveImageListInAlbum
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.retrofit.jsonToRequestBody
import com.kotlin.android.retrofit.toRequestBody
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumItemViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumTitleViewBean
import com.kotlin.android.ugc.detail.component.bean.UgcAlbumViewBean
import com.kotlin.android.ugc.detail.component.bean.UploadImageInAlbum
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumItemBinder
import com.kotlin.android.ugc.detail.component.binder.UgcAlbumTitleBinder
import okhttp3.RequestBody

/**
 * create by lushan on 2020/8/19
 * description: ugc相册详情
 *  1.相册名称
 *  2.相册浏览量、数量分页加载
 *  3.热门评论
 *  4.最新评论
 */
class UgcAlbumRepository : DetailBaseRepository() {
    //    加载相册详情
    suspend fun loadDetailData(albumId: Long, isMineUser: Boolean): ApiResult<AlbumInfo> {
        val list = arrayListOf<Long>()
        list.add(1L)
        if (isMineUser) {
            list.add(4)
        }
        val params = arrayMapOf<String, Any>(
            "albumId" to albumId,
            "shieldStatus" to 0,
            "status" to list.toArray()
        )
        return request(
            api = { apiMTime.getAlbumDetail(params.toRequestBody()) },
            converter = { it })
    }


    private val START_INDEX = 1
    private var albumPageIndex = START_INDEX//相册图片索引
    private val PAGE_SIZE = 20
    suspend fun loadAlbumImageListData(
        albumId: Long,
        userId: Long,
        status: ArrayList<Long>,
        isMore: Boolean
    ): ApiResult<UgcAlbumViewBean> {
        if (isMore.not()) {
            albumPageIndex = START_INDEX
        }
        return request(
            api = {
                apiMTime.getImageListInAlbum(
                    getAlbumImageBody(
                        albumId,
                        userId,
                        status
                    )
                )
            },
            converter = { getUgcAlbumViewBean(it) }
        )
    }

    private fun getUgcAlbumViewBean(bean: ImageListInAlbum): UgcAlbumViewBean {
        albumPageIndex++
        return UgcAlbumViewBean(
            0L,
            bean.totalCount,
            getAlbumItemBinder(bean.items, mutableListOf())
        )
    }

    private fun getAlbumItemBinder(
        list: MutableList<ImageListInAlbum.Image>?,
        imageList: MutableList<PhotoInfo>
    ): MutableList<UgcAlbumItemBinder> {
        return list?.map {
            UgcAlbumItemBinder(
                UgcAlbumItemViewBean(
                    it.id,
                    it.fileUrl.orEmpty(),
                    getImagePath(it.fileId.orEmpty(), imageList)
                )
            )
        }?.toMutableList()
            ?: mutableListOf()
    }

    private fun getImagePath(fileId: String, imageList: MutableList<PhotoInfo>): String {
        val filter = imageList.filter { fileId == it.fileID }
        return if (filter.isEmpty()) {
            ""
        } else {
            filter[0].path
        }
    }

    private fun getAlbumImageBody(
        albumId: Long,
        userId: Long, status: ArrayList<Long>
    ): RequestBody {
        val paramsMap = arrayMapOf<String, Any>().apply {
            put("albumId", albumId)
            put("shieldStatus", 0L)
            put("userId", userId)
            put("status", status)
            arrayMapOf<String, Any>().apply {
                put("pageIndex", albumPageIndex)
                put("pageSize", PAGE_SIZE)
            }.also {
                put("page", it)
            }
        }

        return paramsMap.toRequestBody()
    }

    /**
     * 删除相册
     */
    suspend fun deleteAlbum(albumId: Long): ApiResult<AlbumUpdate> {
        return request { apiMTime.deleteAlbum(arrayMapOf<String, Any>("id" to albumId).toRequestBody()) }
    }

    /**
     * 上传相册图片
     */
    suspend fun uploadImageInAlbum(
        albumId: Long,
        imageList: MutableList<PhotoInfo>
    ): ApiResult<MutableList<UgcAlbumItemBinder>> {
        val params = arrayMapOf<String, Any>("albumId" to albumId)
        if (imageList.isNotEmpty()) {
            params["saveImages"] = imageList.map {
                UploadImageInAlbum(it.fileID.orEmpty())
            }.toTypedArray()
        }

        return request(
            converter = { getAlbumItemBinder(it.items, imageList) },
            api = { apiMTime.saveAlbumImage(params.toRequestBody()) }
        )
    }

}