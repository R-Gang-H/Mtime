package com.kotlin.android.comment.component.ui.detail

import android.text.TextUtils
import androidx.collection.arrayMapOf
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.R
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentReplyDetailBinder
import com.kotlin.android.comment.component.repository.CommentDetailRepository
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.ktx.ext.uri.isSuccess
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/8/20
 * description: 评论详情
 */
class CommentDetailViewModel : DetailBaseViewModel() {
    private val repo: CommentDetailRepository by lazy {
        CommentDetailRepository()
    }

    private val detailUIModel: BaseUIModel<CommentReplyDetailBinder> = BaseUIModel()

    val detailState = detailUIModel.uiState

    private val replyUIModel: BaseUIModel<MutableList<MultiTypeBinder<*>>> = BaseUIModel()
    val replyState = replyUIModel.uiState

    //    保存回复
    private val saveReplyUIModel = BaseUIModel<Long>()
    val saveReplyState = saveReplyUIModel.uiState


    fun loadDetail(objType: Long, commentId: Long) {
        viewModelScope.launch(main) {
            detailUIModel.emitUIState(showLoading = true)
            val withOnIO = withOnIO {
                repo.loadDetailData(objType, commentId)
            }
            checkResult(withOnIO, netError = { detailUIModel.emitUIState(netError = it) },
                    error = { detailUIModel.emitUIState(error = it) },
                    success = { detailUIModel.emitUIState(success = it) },
                    empty = { detailUIModel.emitUIState(isEmpty = true) })
        }
    }

    fun loadReplyListData(objType: Long, commentId: Long, isMore: Boolean) {
        viewModelScope.launch(main) {
            val list = mutableListOf<MultiTypeBinder<*>>()
            withOnIO {//审核通过的回复列表
                val releaseReplyList = if (isMore) {
                    repo.loadMoreReplyListData(objType, commentId, true)
                } else {
                    repo.loadReplyListData(objType, commentId, true)
                }

                var isError = true
                var hasMore = true//是否还有下一页
                if (isMore.not()) {//第一次请求，需要请求未审核通过的回复
                    val unReleaseReplyList = repo.loadReplyListData(objType, commentId, false)

                    if (unReleaseReplyList is ApiResult.Success) {
                        isError = false
                        val elements = unReleaseReplyList.data
                        list.addAll(elements.first)
                    }
                    if (releaseReplyList is ApiResult.Success) {
                        isError = false
                        val data = releaseReplyList.data
                        hasMore = data.second
                        list.addAll(data.first)
                    }
                    if (list.isEmpty()) {
                        list.add(CommentListEmptyBinder())
                    }
                } else {
                    if (releaseReplyList is ApiResult.Success) {
                        isError = false
                        val data = releaseReplyList.data
                        hasMore = data.second
                        list.addAll(data.first)
                    }
                }

                withOnUI {
                    replyUIModel.emitUIState(loadMore = isMore, noMoreData = hasMore.not(), error = "", netError = "", success = if (isError) null else list)
                }

            }
        }
    }


    /**
     * 保存回复
     * @param objType 评论大主题
     * @param   reReplyId 被回复人id
     * @param   commentId 评论的id
     * @param   images 要上传的图片
     * @param   body 回复正文
     */
    fun saveReply(objType: Long, reReplyId: Long, commentId: Long, path: String, body: String) {
        afterLogin {
            viewModelScope.launch(main) {
                saveReplyUIModel.emitUIState(showLoading = true)
                withOnIO {
//                    图片信息
                        if (TextUtils.isEmpty(path).not() && upLoadImageMap[path] == null) {//重新上传图片
                            val uploadImageResult =
                                    mRepo.uploadImage(CommConstant.IMAGE_UPLOAD_COMMON, path)

                            if (uploadImageResult is ApiResult.Success) {
                                if (uploadImageResult.data.success) {//图片上传成功，提交回复
                                    upLoadImageMap[path] = uploadImageResult.data
                                    val saveReply =
                                            repo.saveReply( objType, commentId, reReplyId, upLoadImageMap[path], body)
                                    withOnUI {
                                        checkResult(saveReply, success = {
                                            if (it.bizCode.isSuccess()) {
                                                saveReplyUIModel.emitUIState(success = it.replyId)
                                            } else {
                                                saveReplyUIModel.emitUIState(error = it.bizMsg.orEmpty())
                                            }
                                        }, error = { saveReplyUIModel.emitUIState(error = it) }, netError = { saveReplyUIModel.emitUIState(netError = it) })
                                    }
//                                saveReplyUIModel.emitUIState(saveReply)
                                } else {
                                    withOnUI {
                                        saveReplyUIModel.emitUIState(netError = getString(R.string.error_normal_tv))
                                    }
                                }
                            } else {//图片上传失败是继续提交还是直接报错？
                                withOnUI {
                                    saveReplyUIModel.emitUIState(netError = getString(R.string.error_normal_tv))
                                }
                            }

                        } else {//已经上传成功过，直接去map中就行，直接提交
                            val saveReply =
                                    repo.saveReply(objType, commentId, reReplyId, upLoadImageMap[path], body)
                            withOnUI {
                                checkResult(saveReply, success = {
                                    if (it.bizCode.isSuccess()) {
                                        saveReplyUIModel.emitUIState(success = it.replyId)
                                    } else {
                                        saveReplyUIModel.emitUIState(error = it.bizMsg.orEmpty())
                                    }
                                }, error = { saveReplyUIModel.emitUIState(error = it) }, netError = { saveReplyUIModel.emitUIState(netError = it) })
                            }
                        }



                }
            }
        }
    }


}