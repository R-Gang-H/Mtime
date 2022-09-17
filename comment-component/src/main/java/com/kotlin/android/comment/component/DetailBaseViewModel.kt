package com.kotlin.android.comment.component

import android.text.TextUtils
import androidx.collection.arrayMapOf
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.bean.CommentListViewBean
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.bean.ReplyViewBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.repository.DetailBaseRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.*
import com.kotlin.android.app.data.entity.community.content.CommentList
import com.kotlin.android.app.data.entity.community.praisestate.PraiseState
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.entity.video.VideoPlayList
import com.kotlin.android.image.component.photo.getVideoFrameAtTime
import com.kotlin.android.ktx.ext.uri.isSuccess
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.user.afterLogin
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/8/26
 * description:
 */
open class DetailBaseViewModel : BaseViewModel() {
    companion object {
        const val ACTION_POSITIVE = 1L// 执行
        const val ACTION_CANCEL = 2L//取消执行

        /**
         * js中字符串id转long类型id
         */
        fun getLongValue(id: String): Long {
            if (TextUtils.isEmpty(id)) {
                return 0L
            }
            if (TextUtils.isDigitsOnly(id)) {
                return id.toLong()
            }
            return 0L

        }
    }


    val mRepo by lazy { DetailBaseRepository() }

    //    最热评论列表
    private val hotCommentListUIModel = BaseUIModel<CommentListViewBean>()
    val hotCommentListState = hotCommentListUIModel.uiState

    //    最新评论列表
    private val newCommentListUIModel = BaseUIModel<CommentListViewBean>()
    val newCommentListState = newCommentListUIModel.uiState

    //    关注
    private val followUIModel = BaseUIModel<CommBizCodeResult>()
    val followState = followUIModel.uiState

    private val followExtUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val followExtState = followExtUIModel.uiState

    //    保存评论
    private val saveCommentUIModel = BaseUIModel<Long>()
    val saveCommentState = saveCommentUIModel.uiState

    //    点赞，取消赞
    private val praiseUpUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val praiseUpState = praiseUpUIModel.uiState

    //    点踩、取消踩
    private val praiseDownUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val praiseDownState = praiseDownUIModel.uiState

    //    收藏、取消收藏
    private val collectionUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val collectionState = collectionUIModel.uiState

    //    删除评论
    private val deleteCommentUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val deleteCommentState = deleteCommentUIModel.uiState

    //    加入家族
    private val joinGroupUIModel = BaseUIModel<CommonResultExtend<CommonResult, Any>>()
    val joinGroupState = joinGroupUIModel.uiState

    //    退出家族
    private val exitGroupUiModel = BaseUIModel<CommonResultExtend<CommonResult, Any>>()
    val exitGroupState = exitGroupUiModel.uiState

    //    分享
    private val shareUIModel = BaseUIModel<CommonShare>()
    val shareUIState = shareUIModel.uiState

    //扩展分享
    private val shareExtendUIModel = BaseUIModel<ShareResultExtend<Any>>()
    val shareExtendUIState = shareExtendUIModel.uiState


    //    删除内容
    private val deleteContentUIModel = BaseUIModel<CommBizCodeResult>()
    val deleteContent = deleteContentUIModel.uiState

    //    帖子置顶
    private val postTopUIModel = BaseUIModel<CommBizCodeResult>()
    val postTopState = postTopUIModel.uiState

    //    帖子取消置顶
    private val postCancelTopUIModel = BaseUIModel<CommBizCodeResult>()
    val postCancelTopState = postCancelTopUIModel.uiState

    //    帖子加精
    private val postEssenceUIModel = BaseUIModel<CommBizCodeResult>()
    val postEssenceState = postEssenceUIModel.uiState

    //    帖子取消加精
    private val postCancelEssenceUIModel = BaseUIModel<CommBizCodeResult>()
    val postCancelEssenceState = postCancelEssenceUIModel.uiState


    //    想看、取消想看
    private val wantToSeeUIModel = BaseUIModel<DetailBaseExtend<Any>>()
    val wantToSeeState = wantToSeeUIModel.uiState

    // 当前登录用户点赞状态
    private val praiseStateUIModel = BaseUIModel<PraiseState>()
    val praiseUiState = praiseStateUIModel.uiState

    //    文章详情 1.标题，2.文章详情 3.影片卡片 4.相关新闻推荐标题 5.推荐新闻 6.评论标题+列表
//    1/2/3  4/5  6
//    视频播放地址
    private val videoPlayUrlUIModel = BaseUIModel<VideoPlayList>()
    val videoPlayUrlState = videoPlayUrlUIModel.uiState

    //上传图片
    private val uploadPhotoUIModel = BaseUIModel<PhotoInfo>()
    val uploadPhotoState = uploadPhotoUIModel.uiState

    fun uploadPhoto(path: String) {
        viewModelScope.launch(main) {
            uploadPhotoUIModel.showLoading = false
            val result = withOnIO {
                val videoThumbnailPath = getVideoFrameAtTime(path)
                mRepo.uploadImage(CommConstant.IMAGE_UPLOAD_COMMON, videoThumbnailPath)
            }
            uploadPhotoUIModel.checkResultAndEmitUIState(result)
        }

    }


    /**
     * 获取视频URL
     */
    fun getVideoPlayUrl(videoId: Long, source: Long, scheme: String) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                mRepo.getPlayUrlList(videoId, source, scheme)
            }
            videoPlayUrlUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 帖子置顶
     */
    fun postTop(contentId: Long) {
        viewModelScope.launch(main) {
            postTopUIModel.showLoading = true
            val result = withOnIO {
                mRepo.postTop(contentId)
            }

            postTopUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 帖子取消置顶
     */
    fun postCancelTop(contentId: Long) {
        viewModelScope.launch(main) {
            postCancelTopUIModel.showLoading = true
            val result = withOnIO {
                mRepo.postCancelTop(contentId)
            }

            postCancelTopUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 帖子加精
     */
    fun postEssence(contentId: Long) {
        viewModelScope.launch(main) {
            postEssenceUIModel.showLoading = true
            val result = withOnIO {
                mRepo.postEssence(contentId)
            }

            postEssenceUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 帖子取消加精
     */
    fun postCancelEssence(contentId: Long) {
        viewModelScope.launch(main) {
            postCancelEssenceUIModel.showLoading = true
            val result = withOnIO {
                mRepo.postCancelEssence(contentId)
            }
            postCancelEssenceUIModel.checkResultAndEmitUIState(result)
        }
    }


    /**
     * 关注用户
     */
    fun follow(userId: Long) {
        afterLogin {
            call(followUIModel,
                api = {
                    mRepo.followUser(ACTION_POSITIVE, userId)
                }
            )
        }

    }

    fun followExt(userId: Long, isFollow: Boolean, ext: Any) {
        afterLogin {
            call(followExtUIModel,
                api = {
                    mRepo.followUserExt(
                        if (isFollow) ACTION_POSITIVE else ACTION_CANCEL,
                        userId,
                        ext
                    )
                }
            )
        }
    }

    /**
     * 加载评论列表
     * @param contentId 内容id
     * @param type 类型文章
     * @param isNewComment 是否是最新评论
     * @param isLoadMore 是否是加载更多
     */
    fun loadCommentList(
        context: FragmentActivity,
        contentId: Long,
        type: Long,
        isNewComment: Boolean,
        isLoadMore: Boolean,
        optId: Long = -1L,
        selectListener: ((Boolean) -> Unit)? = null
    ) {
        viewModelScope.launch(main) {
            var totalCount = 0L//对外暴露审核通过的评论条数，目前只有榜单需要用到
            val list = mutableListOf<MultiTypeBinder<*>>()
            withOnIO {
                var isError = true
                var hasMore = true
                val releasedCommentList = if (isLoadMore) {
                    mRepo.loadMoreCommentList(contentId, type, isNewComment, true, optId)
                } else {
                    mRepo.loadCommentList(contentId, type, isNewComment, true, optId)
                }
                if (isLoadMore.not()) {//第一页请求且在登录状态下需要加载未发布的评论
                    if (isLogin()) {
                        val unReleaseCommentList =
                            mRepo.loadCommentList(contentId, type, isNewComment, false, optId)
                        if (unReleaseCommentList is ApiResult.Success) {
                            isError = false
                            val data = unReleaseCommentList.data
                            if (releasedCommentList is ApiResult.Success) {
                                val releasedData = releasedCommentList.data
                                totalCount = releasedData.totalCount.toLong()
//                                list.add(CommentListTitleBinder(CommentTitleViewBean(totalCount, isNewComment), selectListener))
                                list.addAll(getCommentBinderList(context, data))
                                list.addAll(getCommentBinderList(context, releasedData))
                                addCommentEmptyBinder(
                                    data.items?.isEmpty() == true && releasedData.items?.isEmpty() == true,
                                    list
                                )
                                hasMore = releasedCommentList.data.hasNext
                            } else {//正常评论没有请求成功
//                                list.add(CommentListTitleBinder(CommentTitleViewBean(totalCount, isNewComment), selectListener))
                                list.addAll(getCommentBinderList(context, data))
                                addCommentEmptyBinder(data.items?.isEmpty() == true, list)
                            }
                        } else {//未发布的评论没有请求成功
                            if (releasedCommentList is ApiResult.Success) {
                                isError = false
                                totalCount = releasedCommentList.data.totalCount.toLong()
//                                list.add(CommentListTitleBinder(CommentTitleViewBean(totalCount, isNewComment), selectListener))
                                list.addAll(getCommentBinderList(context, releasedCommentList.data))
                                addCommentEmptyBinder(
                                    releasedCommentList.data.items?.isEmpty() == true,
                                    list
                                )
                                hasMore = releasedCommentList.data.hasNext
                            }
                        }
                    } else {
                        if (releasedCommentList is ApiResult.Success) {
                            isError = false
                            totalCount = releasedCommentList.data.totalCount.toLong()
//                            list.e()
//                            list.add(CommentListTitleBinder(CommentTitleViewBean(totalCount, isNewComment), selectListener))
//                            list.e()
                            list.addAll(getCommentBinderList(context, releasedCommentList.data))
//                            list.e()
                            addCommentEmptyBinder(
                                releasedCommentList.data.items?.isEmpty() == true,
                                list
                            )
//                            list.e()
                            hasMore = releasedCommentList.data.hasNext
                        }
                    }

                } else {
                    if (releasedCommentList is ApiResult.Success) {
                        isError = false
                        list.addAll(getCommentBinderList(context, releasedCommentList.data))
                        hasMore = releasedCommentList.data.hasNext
                    }
                }
                withOnUI {
                    emitUIState(
                        isError,
                        isNewComment,
                        releasedCommentList,
                        list,
                        isLoadMore,
                        hasMore,
                        totalCount
                    )
                }
            }

        }
    }

    private fun addCommentEmptyBinder(isEmpty: Boolean, list: MutableList<MultiTypeBinder<*>>) {
        if (isEmpty) {
            list.add(CommentListEmptyBinder())
        }
    }

    /**
     * 处理发送消息
     */
    private fun emitUIState(
        isError: Boolean,
        isNewComment: Boolean,
        releasedCommentList: ApiResult<CommentList>,
        list: MutableList<MultiTypeBinder<*>>,
        isLoadMore: Boolean,
        hasMore: Boolean,
        totalCount: Long
    ) {
        if (isError) {
            if (isNewComment) {
                checkResult(
                    releasedCommentList,
                    error = { newCommentListUIModel.emitUIState(error = it) })
            } else {
                checkResult(
                    releasedCommentList,
                    error = { hotCommentListUIModel.emitUIState(error = it) })
            }
        } else {
            if (isNewComment) {
                newCommentListUIModel.emitUIState(
                    success = CommentListViewBean(totalCount, list),
                    loadMore = isLoadMore,
                    noMoreData = hasMore.not()
                )
            } else {
                hotCommentListUIModel.emitUIState(
                    success = CommentListViewBean(totalCount, list),
                    loadMore = isLoadMore,
                    noMoreData = hasMore.not()
                )
            }
        }
    }

    private fun getCommentBinderList(
        context: FragmentActivity,
        commentList: CommentList?
    ): MutableList<CommentListBinder> {

        return commentList?.items?.map {
            CommentListBinder(
                context, CommentViewBean(
                    commentId = it.commentId,
                    userId = it.createUser?.userId ?: 0L,
                    userName = it.createUser?.nikeName.orEmpty(),
                    userPic = it.createUser?.avatarUrl.orEmpty(),
                    publishDate = formatPublishTime(it.userCreateTime?.stamp),
                    commentContent = it.bodyWords.orEmpty(),
                    likeCount = (it.interactive?.praiseUpCount ?: 0).toLong(),
                    praiseDownCount = it.interactive?.praiseDownCount ?: 0L,
                    userPraised = it.interactive?.userPraised,
                    commentPic = if (it.images?.isNotEmpty() == true) it.images?.get(0)?.imageUrl.orEmpty() else "",
                    replyList = it.replies?.map { reply ->
                        val replyContent =
                            if (TextUtils.isEmpty(reply.body) && reply.images?.isEmpty() == false) getString(
                                R.string.comment_reply_has_pic_only
                            ) else reply.body.orEmpty()
                        ReplyViewBean(
                            replyId = reply.replyId,
                            userId = reply.createUser?.userId
                                ?: 0L,
                            userName = reply.createUser?.nikeName.orEmpty(),
                            replyContent = replyContent,
                            publishDate = formatPublishTime(reply.userCreateTime?.stamp)
                        )
                    }?.toMutableList() ?: mutableListOf(),
                    replyCount = it.replyCount,
                    type = it.objType,
                    objId = it.objId,
                    userAuthType = it.createUser?.authType ?: 0L
                )
            )
        }?.toMutableList() ?: mutableListOf()
    }


    //    上传成功的图片
    internal val upLoadImageMap = arrayMapOf<String, PhotoInfo?>()

    /**
     * 获取成功后的图片
     */
    fun getUpLoadImageUrl(path: String): String = upLoadImageMap[path]?.url.orEmpty()

    /**
     * 保存评论
     */
    fun saveComment(type: Long, contentId: Long, optId: Long = 0, path: String, content: String) {
        afterLogin {
            viewModelScope.launch(main) {
                saveCommentUIModel.emitUIState(showLoading = true)
                withOnIO {
//                        //                    图片信息
                    if (TextUtils.isEmpty(path).not() && upLoadImageMap[path] == null) {//重新上传图片
                        val uploadImageResult =
                            mRepo.uploadImage(CommConstant.IMAGE_UPLOAD_COMMON, path)
                        if (uploadImageResult is ApiResult.Success) {//图片上传成功
                            if (uploadImageResult.data.success) {//图片上传成功，提交回复
                                upLoadImageMap[path] = uploadImageResult.data
                                val saveCommentResult = mRepo.saveComment(
                                    type,
                                    contentId,
                                    optId,
                                    upLoadImageMap[path],
                                    content
                                )
                                withOnUI {
                                    checkResult(
                                        saveCommentResult,
                                        success = {
                                            if (it.bizCode.isSuccess()) {
                                                saveCommentUIModel.emitUIState(success = it.commentId)
                                            } else {
                                                saveCommentUIModel.emitUIState(error = it.bizMsg.orEmpty())
                                            }
                                        },
                                        error = { saveCommentUIModel.emitUIState(error = it) },
                                        netError = { saveCommentUIModel.emitUIState(netError = it) })
                                }
                            } else {
                                withOnUI {
                                    saveCommentUIModel.emitUIState(netError = getString(R.string.error_normal_tv))
                                }
                            }

                        } else {//图片上传失败
                            withOnUI {
                                saveCommentUIModel.emitUIState(netError = getString(R.string.error_normal_tv))
                            }
                        }

                    } else {//提交信息
                        val saveCommentResult = mRepo.saveComment(
                            type,
                            contentId,
                            optId,
                            upLoadImageMap[path],
                            content
                        )
                        withOnUI {
                            checkResult(
                                saveCommentResult,
                                success = {
                                    if (it.bizCode.isSuccess()) {
                                        saveCommentUIModel.emitUIState(success = it.commentId)
                                    } else {
                                        saveCommentUIModel.emitUIState(error = it.bizMsg.orEmpty())
                                    }
                                },
                                error = { saveCommentUIModel.emitUIState(error = it) },
                                netError = { saveCommentUIModel.emitUIState(netError = it) })
                        }

                    }

                }


            }
        }

    }

    /**
     * 点赞或取消点赞
     * @param type 主体类型
     * @param contentId 主体id
     * @param isCancel 是否是取消点赞
     */
    fun praiseUpOrCancel(type: Long, contentId: Long, isCancel: Boolean, extend: Any) {
        afterLogin {
            viewModelScope.launch(main) {
                praiseUpUIModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.praiseUp(
                        if (isCancel) ACTION_CANCEL else ACTION_POSITIVE,
                        type,
                        contentId,
                        extend
                    )
                }
                checkResult(result,
                    error = {
                        praiseUpUIModel.emitUIState(error = it)
                    },
                    netError = {
                        praiseUpUIModel.emitUIState(netError = it)
                    },
                    success = {
                        if ((it.result as CommBizCodeResult).isSuccess()) {
                            praiseUpUIModel.emitUIState(
                                success = DetailBaseExtend(
                                    isCancel,
                                    extend
                                )
                            )
                        } else {
                            praiseUpUIModel.emitUIState(error = "")
                        }
                    },
                    needLogin = {
                        praiseUpUIModel.emitUIState(needLogin = true)
                    },
                    empty = {
                        praiseUpUIModel.emitUIState(
                            success = DetailBaseExtend(
                                isCancel,
                                extend
                            )
                        )
                    })
            }
        }

    }

    /**
     * 想看、取消想看
     */
    fun wantToSee(movieId: Long, flag: Long, extend: Any) {
        afterLogin {
            viewModelScope.launch(main) {
                wantToSeeUIModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.getMovieWantToSee(movieId, flag, extend = extend)
                }

                checkResult(result, success = {
                    if (((it.result) as WantToSeeResult).isSuccess()) {
                        wantToSeeUIModel.emitUIState(success = it)
                    } else {
                        wantToSeeUIModel.emitUIState(error = ((it.result) as WantToSeeResult).statusMsg.orEmpty())
                    }
                })
            }
        }
    }


    /**
     * 点踩或取消点踩
     * @param type 主体类型
     * @param contentId 主体id
     * @param isCancel 是否是取消点踩
     */
    fun praiseDownOrCancel(type: Long, contentId: Long, isCancel: Boolean, extend: Any) {
        afterLogin {
            viewModelScope.launch(main) {
                praiseDownUIModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.praiseDown(
                        if (isCancel) ACTION_CANCEL else ACTION_POSITIVE,
                        type,
                        contentId,
                        extend
                    )
                }

                checkResult(result, error = { praiseDownUIModel.emitUIState(error = it) },
                    netError = { praiseDownUIModel.emitUIState(netError = it) },
                    success = {
                        if ((it.result as CommBizCodeResult).isSuccess()) {
                            praiseDownUIModel.emitUIState(
                                success = DetailBaseExtend(
                                    isCancel,
                                    extend
                                )
                            )
                        } else {
                            praiseDownUIModel.emitUIState(error = "")
                        }
                    },
                    needLogin = { praiseDownUIModel.emitUIState(needLogin = true) })
            }
        }

    }


    /**
     * 收藏、取消收藏
     */
    fun collectionOrCancel(type: Long, contentId: Long, isCancel: Boolean, extend: Any) {
        afterLogin {
            call(uiModel = collectionUIModel,
                api = {
                    mRepo.collection(
                        if (isCancel) ACTION_CANCEL else ACTION_POSITIVE,
                        type,
                        contentId,
                        extend
                    )
                })
        }
    }


    /**
     * 删除某条评论
     */
    fun deleteComment(objType: Long, commentId: Long, extend: Any) {
        afterLogin {
            viewModelScope.launch {
                deleteCommentUIModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.deleteComment(objType, commentId, extend)
                }

                checkResult(result, error = { deleteCommentUIModel.emitUIState(error = it) },
                    netError = { deleteCommentUIModel.emitUIState(netError = it) },
                    success = {
                        deleteCommentUIModel.emitUIState(
                            success = DetailBaseExtend(
                                (it.result as CommBizCodeResult).isSuccess(),
                                extend
                            )
                        )
                    },
                    needLogin = { deleteCommentUIModel.emitUIState(needLogin = true) })
            }
        }
    }

    /**
     * 加入群组
     */
    fun joinGroup(groupId: Long, extend: Any) {
        afterLogin {
            viewModelScope.launch {
                joinGroupUIModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.joinFamily(groupId, extend)
                }
                joinGroupUIModel.checkResultAndEmitUIState(result)
            }
        }
    }

    /**
     * 退出群组
     */
    fun exitGroup(groupId: Long, extend: Any) {
        afterLogin {
            viewModelScope.launch {
                exitGroupUiModel.emitUIState(showLoading = true)
                val result = withOnIO {
                    mRepo.outFamily(groupId, extend)
                }
                exitGroupUiModel.checkResultAndEmitUIState(result)
            }
        }
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    fun getShareInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long = 0
    ) {
        viewModelScope.launch {
            shareUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mRepo.getShareInfo(type, relateId, secondRelateId)
            }

            shareUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    fun getShareExtendInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long = 0, extend: Any
    ) {
        viewModelScope.launch {
            shareExtendUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mRepo.getShareInfo(type, relateId, secondRelateId, extend)
            }

            shareExtendUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 社区内容api - 删除内容(/delete_content.api)
     * POST(/community/delete_content.api)
     * @param   type    Number     内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");
     * @param   contentId   Number  内容id
     */
    fun deleteContent(type: Long, contentId: Long) {
        viewModelScope.launch(main) {
            deleteContentUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.deleteContent(type, contentId)
            }
            deleteContentUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 查询点赞点踩状态
     */
    fun getPraiseState(objType: Long, objId: Long) {
        viewModelScope.launch(main) {

            val result = withOnIO {
                mRepo.getPraiseState(objType, objId)
            }

            praiseStateUIModel.checkResultAndEmitUIState(result)
        }
    }


    /**
     * 评论是否是加载中
     */
    fun isCommentLoading(): Boolean {
        return newCommentListUIModel.showLoading || hotCommentListUIModel.showLoading
    }

    /**
     * 提交详情页埋点
     */
    fun submitContentTrace(
        objType: Long,
        objId: String,
        objCreatorId: Long,
        isShortReview: Boolean = false
    ) {
        viewModelScope.launch(main) {
            withOnIO { mRepo.submitContentTrack(objType, objId, objCreatorId, isShortReview) }
        }
    }


}