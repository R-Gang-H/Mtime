package com.kotlin.android.comment.component.repository

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.comment.SaveCommentResult
import com.kotlin.android.app.data.entity.common.*
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.mtime.ktx.getServerTime
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.retrofit.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.HashMap

/**
 * create by lushan on 2020/8/26
 * description: 评论点赞相关接口
 */
open class DetailBaseRepository : BaseRepository() {
    private val PAGE_SIZE = 20
    private val PAGE_SIZE_LIVE = 50
    private var hotCommentPageIndex = 1L//热评索引
    private var newCommentPageIndex = 1L//最新评论索引

    /**
     * 关注用户
     */
    suspend fun followUser(action: Long, userId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.followUser(action, userId) }
    }

    suspend fun <T> followUserExt(
        action: Long,
        userId: Long,
        ext: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(api = { apiMTime.followUser(action, userId) }, converter = {
            DetailBaseExtend(it.isSuccess(), ext)
        })
    }

    /**
     * 加载评论
     */
    suspend fun loadCommentList(
        contentId: Long,
        type: Long,
        isNewComment: Boolean,
        isReleased: Boolean,
        optId: Long = -1L
    ): ApiResult<CommentList> {
        val pageIndex = if (isReleased.not()) {
            1
        } else if (isNewComment) {
            newCommentPageIndex = 1
            newCommentPageIndex
        } else {
            hotCommentPageIndex = 1
            hotCommentPageIndex
        }

        val body =
            getCommentRequestBody(type, contentId, isNewComment, pageIndex, optId, isReleased)
        return request(
            converter = {
                if (isReleased) {//审核成功的才增加，审核不成功的只有第一页
                    if (isNewComment) {
                        newCommentPageIndex++
                    } else {
                        hotCommentPageIndex++
                    }
                }
                it
            },
            api = {
                if (isReleased) apiMTime.getCommentListData(body) else apiMTime.getUnReleasedCommentListData(
                    body
                )
            })
    }

    /**
     * 加载更多评论
     */
    suspend fun loadMoreCommentList(
        contentId: Long,
        type: Long,
        isNewComment: Boolean,
        isReleased: Boolean,
        optId: Long = -1L
    ): ApiResult<CommentList> {
        val pageIndex = if (isNewComment) {
            newCommentPageIndex
        } else {
            hotCommentPageIndex
        }

        val body =
            getCommentRequestBody(type, contentId, isNewComment, pageIndex, optId, isReleased)
        return request(
            converter = {
                if (isReleased) {//审核成功的才增加，审核不成功的只有第一页
                    if (isNewComment) {
                        newCommentPageIndex++
                    } else {
                        hotCommentPageIndex++
                    }
                }
                it
            },
            api = {
                if (isReleased) apiMTime.getCommentListData(body) else apiMTime.getUnReleasedCommentListData(
                    body
                )
            })
    }

    /**
     * 获取评论请求体
     */
    private fun getCommentRequestBody(
        type: Long,
        contentId: Long,
        isNewComment: Boolean,
        pageIndex: Long,
        optId: Long,
        isReleased: Boolean
    ): RequestBody {
        val releasePageSize = if (type == CommContent.TYPE_LIVE) PAGE_SIZE_LIVE else PAGE_SIZE
        val params = arrayMapOf<String, Any>(
            "objType" to type,
            "objId" to contentId,
            "pageIndex" to pageIndex,
            "pageSize" to if (isReleased) releasePageSize else 10
        )
        if (optId != -1L) {
            params["optId"] = optId
            params["sort"] = 1
        } else {
            params["sort"] = if (isNewComment) 1 else 2
        }
        return getRequestBody(params)
    }


    /**
     * 保存记录
     */
    suspend fun saveComment(
        type: Long,
        contentId: Long,
        optId: Long = 0,
        image: PhotoInfo? = null,
        content: String
    ): ApiResult<SaveCommentResult> {

        val body = getSaveCommentBody(type, contentId, optId, image, content)
        return request { apiMTime.saveComment(body) }
    }

    private fun getSaveCommentBody(
        type: Long,
        contentId: Long,
        optId: Long = 0,
        image: PhotoInfo? = null,
        content: String
    ): RequestBody {

        val imageArray = arrayOf<Any>(1)
        image?.apply {
            imageArray[0] =
                (arrayMapOf("imageId" to image.fileID.orEmpty(), "imageUrl" to image.url))
        }
        val params: ArrayMap<String, Any> = arrayMapOf<String, Any>(
            "objType" to type,
            "objId" to contentId,
            "optId" to optId,
            "body" to content
        )
        image?.apply {
            params["images"] = imageArray
        }

        return params.toRequestBody()
    }

    /**
     * 点赞/取消点赞
     */
    suspend fun <T> praiseUp(
        action: Long,
        objType: Long,
        objId: Long,
        extend: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(
            api = { apiMTime.praiseUp(action, objType, objId) },
            converter = {
                DetailBaseExtend(it, extend)
            })
    }

    /**
     * 点踩/取消点踩
     */
    suspend fun <T> praiseDown(
        action: Long,
        objType: Long,
        objId: Long,
        extend: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(
            api = { apiMTime.praiseDown(action, objType, objId) },
            converter = {
                DetailBaseExtend(it, extend)
            })
    }

    /**
     * 设置电影为想看/取消想看
     * GET ("/library/movie/setWantToSee.api")
     *
     * movieId	Number  电影Id
     * flag	    Number  操作类型：1想看，2取消想看
     * year	    Number  年代（用于生成XXXX年我想看的第XX部电影）
     * extend   扩展数据，用于UI刷新等
     */
    suspend fun <T> getMovieWantToSee(
        movieId: Long,
        flag: Long,
        year: Long = Calendar.getInstance().get(Calendar.YEAR).toLong(),
        extend: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(
            converter = {
                DetailBaseExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.getMovieWantToSee(movieId, flag, year)
            })
    }


    /**
     * 收藏/取消收藏  @param objType  收藏主体类型 MOVIE(1, "电影"),PERSON(2, "影人"),CINEMA(3, "影院"),POST(4, "帖子"),ARTICLE(5, "文章"),JOURNAL(6, "日志"),FILM_COMMENT(7, "影评"),VIDEO(8, "视频"),AUDIO(9, "音频"),FILM_LIST(10, "片单"),
     * @param action 1.收藏 2.取消收藏
     */
    suspend fun <T> collection(
        action: Long,
        objType: Long,
        objId: Long,
        extend: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(
            api = { apiMTime.collection(action, objType, objId) },
            converter = {
                DetailBaseExtend(it, extend)
            })
    }

    /**
     * 删除某条评论
     * @param objType  收藏主体类型：1.电影 2.影人 3.影院 4.帖子 5.文章
     * @param commentId 评论id
     */
    suspend fun <T> deleteComment(
        objType: Long,
        commentId: Long,
        extend: T
    ): ApiResult<DetailBaseExtend<T>> {
        return request(api = { apiMTime.deleteComment(objType, commentId) }, converter = {
            DetailBaseExtend(it, extend)
        })
    }

    /**
     * 加入家族
     */
    suspend fun <T> joinFamily(
        id: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.joinGroup(id)
            })
    }

    /**
     * 退出家族
     */
    suspend fun <T> outFamily(id: Long, extend: T): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.outGroup(id)
            })
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    suspend fun getShareInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long = 0
    ): ApiResult<CommonShare> {
        return request {
            apiMTime.getShareInfo(type, relateId, secondRelateId)
        }
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     * extend 分享类型
     */
    suspend fun <T> getShareInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long = 0, extend: T
    ): ApiResult<ShareResultExtend<T>> {
        return request(
            api = { apiMTime.getShareInfo(type, relateId, secondRelateId) },
            converter = { ShareResultExtend(result = it, extend) })
    }

    /**
     * 社区内容api - 删除内容(/delete_content.api)
     * POST(/community/delete_content.api)
     * @param   type    Number     内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     * @param   contentId   Number  内容id
     */
    suspend fun deleteContent(type: Long, contentId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.postDeleteContent(type, contentId) }
    }


    /**
     * 帖子置顶
     */
    suspend fun postTop(contentId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.postTop(contentId) }
    }

    /**
     * 获取视频播放地址
     */
    suspend fun getPlayUrlList(
        videoId: Long,
        source: Long,
        scheme: String
    ): ApiResult<VideoPlayList> {
        return request {
            apiMTime.getPlayUrl(videoId, source, scheme)
        }
    }

    /**
     * 帖子取消置顶
     */
    suspend fun postCancelTop(contentId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.postCancelTop(contentId) }
    }

    /**
     * 帖子加精
     */
    suspend fun postEssence(contentId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.postEssence(contentId) }
    }

    /**
     * 帖子取消加精
     */
    suspend fun postCancelEssence(contentId: Long): ApiResult<CommBizCodeResult> {
        return request { apiMTime.postCancelEssence(contentId) }
    }

    /**
     * 图片上传
     */
    suspend fun uploadImage(@UploadImageType imageType: Long, file: String): ApiResult<PhotoInfo> {
        //文件封装
        val requestBody: RequestBody = File(file).asRequestBody("image/jpg".toMediaTypeOrNull())
        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file", file, requestBody
        )
        return request { apiMTime.imageUpload(VariateExt.imgUploadUrl, filePart, imageType) }
    }

    /**
     * 查询点赞点踩状态
     */
    suspend fun getPraiseState(objType: Long, objId: Long): ApiResult<PraiseState> {
        return request { apiMTime.getPraiseState(objType, objId) }
    }

    /**
     * 获取内容详情接口参数集合
     */
    fun getContentDetailParams(
        locationId: Long,
        @ContentType type: Long,
        contentId: Long,
        recId: Long? = null
    ): HashMap<String, Any> {
        var paramsMap = hashMapOf<String, Any>(
            "locationId" to locationId,
            "type" to type,
            "contentId" to contentId
        )
        if (recId != null && recId > 0) {
            paramsMap["recId"] = recId
        }

        return paramsMap
    }

    /**
     * 上报详情页面浏览量
     */
    suspend fun submitContentTrack(
        objType: Long,
        objId: String,
        objCreatorId: Long,
        isShortReview: Boolean = false
    ):ApiResult<Any> {
        hashMapOf<String,Any>().toRequestBody()
        var jsonObject = JSONObject()
        var trackJsonArray = JSONArray()

        JSONObject().apply {
            put("mark",getTrackMark(objType, isShortReview))
            put("objId",objId)
            put("objCreator",objCreatorId)
            put("trackTs", getServerTime())
        }.also {
            trackJsonArray.put(it)
            jsonObject.put("tracks",trackJsonArray)
        }

        return request { apiMTime.submitTrack(jsonObject.toRequestBody()) }



    }

    private fun getTrackMark(objType: Long, isShortReview: Boolean): String {
        return when (objType) {
            CONTENT_TYPE_JOURNAL -> "JOURNAL_DETAIL_VIEW"
            CONTENT_TYPE_POST -> "POST_DETAIL_VIEW"
            CONTENT_TYPE_ARTICLE -> "ARTICLE_DETAIL_VIEW"
            CONTENT_TYPE_VIDEO -> "VIDEO_DETAIL_VIEW"
            CONTENT_TYPE_AUDIO -> "AUDIO_DETAIL_VIEW"
            CONTENT_TYPE_FILM_COMMENT -> {
                if (isShortReview) {
                    "SHORT_FILM_COMMENT_DETAIL_VIEW"
                } else {
                    "LONG_FILM_COMMENT_DETAIL_VIEW"
                }
            }
            else -> ""
        }
    }

}