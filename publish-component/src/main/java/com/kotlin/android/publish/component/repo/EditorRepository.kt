package com.kotlin.android.publish.component.repo

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.entity.CommContentList
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.common.PublishResult
import com.kotlin.android.app.data.entity.common.StatusResult
import com.kotlin.android.app.data.entity.community.content.CheckReleasedResult
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.data.entity.community.publish.Footmarks
import com.kotlin.android.app.data.entity.community.publish.Group
import com.kotlin.android.app.data.entity.community.publish.RecommendTypes
import com.kotlin.android.app.data.entity.community.record.PostContent
import com.kotlin.android.app.data.entity.movie.LatestComment
import com.kotlin.android.app.data.entity.upload.ApplyUpload
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.retrofit.toRequestBody

/**
 * 编辑器：
 *
 * Created on 2020/10/9.
 *
 * @author o.s
 */
class EditorRepository : DetailBaseRepository() {

    suspend fun getContent(
        @ContentType type: Long,
        contentId: Long,
        recId:Long?
    ): ApiResult<CommunityContent> {
        return request {
            apiMTime.getCommunityContent(
                getContentDetailParams(
                    type = type,
                    contentId = contentId,
                    locationId = GlobalDimensionExt.getDigitsCurrentCityId(),
                    recId = recId
                )
            )
        }
    }

    suspend fun postContent(record: PostContent): ApiResult<PublishResult> {
        return request {
            apiMTime.postCommunityContent(record.toRequestBody())
        }
    }

    suspend fun getRecord(@ContentType type: Long, id: Long): ApiResult<CommunityContent> {
        return request {
            apiMTime.getCommunityRecord(GlobalDimensionExt.getDigitsCurrentCityId(), type, id)
        }
    }

    suspend fun getMovieRating(
        movieId: Long,
        rating: Double,
        subItemRating: String
    ): ApiResult<StatusResult> {
        return request {
            apiMTime.getMovieRating(movieId, rating, subItemRating)
        }
    }

    suspend fun getMovieLatestComment(movieId: Long): ApiResult<LatestComment> {
        return request {
            apiMTime.getMovieLatestComment(movieId.toString())
        }
    }

    suspend fun postDeleteContent(
        @ContentType type: Long,
        contentId: Long
    ): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.postDeleteContent(type, contentId)
        }
    }

    /**
     * 检查内容是否为发布状态
     */
    suspend fun checkReleased(type: Long, contentId: Long): ApiResult<CheckReleasedResult> {
        return request {
            apiMTime.checkReleased(type, contentId)
        }
    }

    /**
     * 选择家族列表：
     * 过滤当前用户都有权限发布的家族列表
     * [userId] 不传表示当前登录用户
     */
    suspend fun loadFamilyList(userId: Long? = null): ApiResult<ArrayList<Group>> {
        return request(
            converter = {
                val groups = ArrayList<Group>()
                it.managedGroupList?.forEach { group ->
                    groups.add(Group(groupId = group.groupId, name = group.groupName))
                }
                it.joinedGroupList?.forEach { group ->
                    groups.add(Group(groupId = group.groupId, name = group.groupName))
                }
                groups
            },
            api = {
                apiMTime.getCommunityMyFamilyCanPostList(1L, 1000L)
            }
        )
    }

    /**
     * 获取家族分区列表
     */
    suspend fun getGroupSectionList(groupId: Long): ApiResult<GroupSectionList> {
        return request {
            apiMTime.getCommunityFamilySectionList(groupId = groupId)
        }
    }

    /**
     * 种草家族列表
     */
    suspend fun getGroupFootmarks(): ApiResult<Footmarks> {
        return request {
            apiMTime.getGroupFootmarks()
        }
    }

    /**
     * 按推荐类型获取子分类
     */
    suspend fun getGroupSubTypes(rcmdType: Long = 7): ApiResult<RecommendTypes> {
        return request {
            apiMTime.getGroupSubTypes(rcmdType = rcmdType)
        }
    }

    /**
     * 发起上传
     */
    suspend fun applyUpload(fileName: String): ApiResult<ApplyUpload> {
        return request {
            apiMTime.getApplyUpload(fileName = fileName, mediaType = "video")
        }
    }

    /**
     * 上传完成
     */
    suspend fun completeUpload(
        videoId: Long,
        mediaId: String,
        mediaUrl: String
    ): ApiResult<CommBizCodeResult> {
        return request {
            apiMTime.getCompleteUpload(
                video_id = videoId,
                media_id = mediaId,
                media_url = mediaUrl,
                need_transcoding = true,
                transcoding_format = "720P"
            )
        }
    }

    /**
     * 获取关联文章列表
     */
    suspend fun getRelationArticles(
        contentId: Long? = null,
        recId: Long? = null,
        pageIndex: Long,
        pageSize: Long,
    ): ApiResult<CommContentList> {
        return request {
            apiMTime.getReObjArticleList(
                contentId = contentId,
                recId = recId,
                pageIndex = pageIndex,
                pageSize = pageSize,
            )
        }
    }

    /**
     * 获取电影名称
     */
    suspend fun getMovieName(movieId: Long): ApiResult<String> {
        return request(
            converter = {
                it.fcMovie?.let { movieItem ->
                    if (movieItem.name.isNullOrEmpty()) {
                        movieItem.nameEn.orEmpty()
                    } else {
                        movieItem.name
                    }
                }
            }
        ) {
            val body = arrayMapOf<String, Any>(
                "type" to CONTENT_TYPE_FILM_COMMENT,
                "include" to arrayListOf("FC_MOVIE"),
                "fcMovie" to movieId,
            ).toRequestBody()
            apiMTime.postContentInit(body)
        }
    }

    /**
     * 获取电影url
     */
    suspend fun getMovieUrl(movieId: Long): ApiResult<String> {
        return request(
            converter = {
                it.fcMovie?.url
            }
        ) {
            val body = arrayMapOf<String, Any>(
                "type" to CONTENT_TYPE_FILM_COMMENT,
                "include" to arrayListOf("FC_MOVIE"),
                "fcMovie" to movieId,
            ).toRequestBody()
            apiMTime.postContentInit(body)
        }
    }
}