package com.kotlin.android.comment.component.repository

import androidx.collection.ArrayMap
import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.binder.CommentReplyBinder
import com.kotlin.android.comment.component.binder.CommentReplyDetailBinder
import com.kotlin.android.app.data.entity.comment.ReplyList
import com.kotlin.android.app.data.entity.comment.SaveCommentResult
import com.kotlin.android.app.data.entity.comment.SaveReplyResult
import com.kotlin.android.app.data.entity.common.CommBizCodeResult
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.retrofit.getRequestBody
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import okhttp3.RequestBody

/**
 * create by lushan on 2020/8/20
 * description: 评论详情
 */
class CommentDetailRepository : DetailBaseRepository() {
    private var replyIndex = 1L
    private val PAGE_SIZE = 20

    suspend fun loadReplyListData(
        objType: Long,
        commentId: Long,
        isReleased: Boolean
    ): ApiResult<Pair<MutableList<MultiTypeBinder<*>>, Boolean>> {
        if (isReleased) {
            replyIndex = 1L
        }
        return request(
            converter = { data ->
                Pair(getReplyMultiTypeBinderList(data, isReleased), data.hasNext)
            },
            api = {
                if (isReleased) apiMTime.getReleasedReplyList(
                    getReplyBody(
                        objType,
                        commentId,
                        isReleased
                    )
                ) else apiMTime.getUnReleasedReplyList(getReplyBody(objType, commentId, isReleased))
            }
        )
    }

    private fun getReplyMultiTypeBinderList(
        replyList: ReplyList,
        isReleased: Boolean
    ): MutableList<MultiTypeBinder<*>> {
        val replyItemList = replyList.items?.toMutableList()
        if (isReleased) {
            replyIndex++
        }
        replyItemList ?: return mutableListOf()

        return replyItemList.map {
            CommentReplyBinder(
                ReplyViewBean(
                    userId = it.createUser?.userId ?: 0L,
                    userName = it.createUser?.nikeName.orEmpty(),
                    userPic = it.createUser?.avatarUrl.orEmpty(),
                    replyContent = "${if (it.reReplyId != null) "@${it.reUser?.nikeName.orEmpty()}:" else ""}${it.body.orEmpty()}",
                    publishDate = formatPublishTime(it.userCreateTime?.stamp),
                    praiseCount = (it.interactive?.praiseUpCount
                        ?: 0L), picUrl = it.getFirstPic(),
                    isLike = it.interactive?.userPraised == 1L,
                    replyId = it.replyId, userAuthType = it.createUser?.authType ?: 0L
                )
            )
        }.toMutableList()

    }

    private fun getReplyBody(objType: Long, commentId: Long, isReleased: Boolean): RequestBody {
        return getRequestBody(
            arrayMapOf(
                "objType" to objType,
                "commentId" to commentId,
                "pageIndex" to if (isReleased) replyIndex else 1,
                "pageSize" to PAGE_SIZE
            )
        )
    }

    suspend fun loadMoreReplyListData(
        objType: Long,
        commentId: Long,
        isReleased: Boolean
    ): ApiResult<Pair<MutableList<MultiTypeBinder<*>>, Boolean>> {
        return request(
            converter = { data ->
                Pair(getReplyMultiTypeBinderList(data, isReleased), data.hasNext)
            },
            api = {
            if (isReleased) apiMTime.getReleasedReplyList(
                getReplyBody(
                    objType,
                    commentId,
                    isReleased
                )
            ) else apiMTime.getUnReleasedReplyList(getReplyBody(objType, commentId, isReleased))
        })
    }

    suspend fun loadDetailData(
        objType: Long,
        commentId: Long
    ): ApiResult<CommentReplyDetailBinder> {
        return request(
            converter = { data ->
                CommentReplyDetailBinder(
                    CommentViewBean(
                        commentId = data.commentId,
                        userId = data.createUser?.userId ?: 0L,
                        userName = data.createUser?.nikeName.orEmpty(),
                        userPic = data.createUser?.avatarUrl.orEmpty(),
                        publishDate = formatPublishTime(data.userCreateTime?.stamp),
                        commentContent = data.body.orEmpty(),
                        likeCount = data.interactive?.praiseUpCount ?: 0L,
                        userPraised = data.interactive?.userPraised,
                        replyCount = data.replyCount,
                        commentPic = data.getFirstCommentPic(),
                        userAuthType = data.createUser?.authType ?: 0L,
                        objId = data.objId,
                        type = data.objType
                    )
                )
            },
            api = { apiMTime.getCommentData(objType, commentId) })

    }


    /**
     * 保存回复
     */
    suspend fun saveReply(
        objType: Long,
        commentId: Long,
        reReplyId: Long,
        image: PhotoInfo? = null,
        body: String
    ): ApiResult<SaveReplyResult> {
        val imageArray = arrayOf<Any>(1)
        if (image != null) {
            imageArray[0] =
                (arrayMapOf("imageId" to image.fileID.orEmpty(), "imageUrl" to image.url))
        }
        val params: ArrayMap<String, Any> = arrayMapOf(
            "objType" to objType,
            "commentId" to commentId,
            "body" to body
        )
        if (image != null) {
            params["images"] = imageArray
        }
        if (reReplyId != 0L) {
            params["reReplyId"] = reReplyId
        }
        val body = getRequestBody(params)
        return request { apiMTime.saveReply(body) }

    }


}