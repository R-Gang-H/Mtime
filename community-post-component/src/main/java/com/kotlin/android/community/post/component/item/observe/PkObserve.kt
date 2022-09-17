package com.kotlin.android.community.post.component.item.observe

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.common.CollectionResult
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.observer.CommonObserver
import com.kotlin.android.comment.component.observer.CommonObserver.Companion.TYPE_PRAISE_DOWN
import com.kotlin.android.comment.component.observer.CommonObserver.Companion.TYPE_PRAISE_UP
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.community.post.component.item.adapter.PkCommentBinder
import com.kotlin.android.community.post.component.item.bean.PKComentViewBean
import com.kotlin.android.community.post.component.item.widget.PkCommentListView
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/22
 * description: pk观察者
 */
class PkObserve(
    var activity: FragmentActivity,
    var handleType: Int,
    var commentView: PublishCommentView?,
    var type: BarButtonItem.Type,
    var commentListView: PkCommentListView? = null,
    var binderList: MutableList<MultiTypeBinder<*>> = mutableListOf()
) : Observer<BaseUIModel<DetailBaseExtend<Any>>> {

    override fun onChanged(t: BaseUIModel<DetailBaseExtend<Any>>?) {
        t?.apply {
            success?.apply {
                activity.dismissProgressDialog()
                when (handleType) {
                    TYPE_PRAISE_UP -> {//点赞
                        when (extend) {
                            is PKComentViewBean -> {//pk帖子中评论列表点赞
                                if (commentListView != null) {
                                    commentListView?.updatePraiseUp(extend as PKComentViewBean)
                                } else {
                                    binderList.filter { it is PkCommentBinder }.forEach {
                                        (it as PkCommentBinder).updatePraiseUp(extend as PKComentViewBean)
                                    }
                                }
                            }
                            else -> {//底部点赞
                                if (commentView?.hasType(BarButtonItem.Type.DISPRAISE) == true && commentView?.getSelectedStatusByType(
                                        BarButtonItem.Type.DISPRAISE
                                    ) == true
                                ) {//点踩了
                                    //                            需要处理底部点踩消息  点赞和点踩是互斥的
                                    handlePraiseData(
                                        commentView,
                                        BarButtonItem.Type.DISPRAISE,
                                        (result as Boolean)
                                    )
                                }
//                            处理底部点赞消息
                                handlePraiseData(commentView, type, (result as Boolean).not())
                            }
                        }
                    }
                    TYPE_PRAISE_DOWN -> {//点踩
                        when (extend) {
                            is PKComentViewBean -> {//pk帖子中评论列表点赞
                                if (commentListView != null) {
                                    commentListView?.updatePraiseDown(extend as PKComentViewBean)
                                } else {
                                    binderList.filter { it is PkCommentBinder }.forEach {
                                        (it as PkCommentBinder).updatePraiseDown(extend as PKComentViewBean)
                                    }
                                }
                            }
                            else -> {//底部点赞
                                if (commentView?.hasType(BarButtonItem.Type.PRAISE) == true && commentView?.getSelectedStatusByType(
                                        BarButtonItem.Type.PRAISE
                                    ) == true
                                ) {//点赞了
                                    handlePraiseData(
                                        commentView,
                                        BarButtonItem.Type.PRAISE,
                                        result as Boolean
                                    )
                                }
                                handlePraiseData(commentView, type, (result as Boolean).not())
                            }
                        }
                    }
                    CommonObserver.TYPE_COLLECTIN -> {
                        val collectionResult = result as? CollectionResult
                        if (collectionResult?.isSuccess().orFalse()) {
                            commentView?.isSelectedByType(
                                type,
                                commentView?.getSelectedStatusByType(type).orFalse().not()
                            )
                        }
                    }
                }
            }

            error?.apply {
                handleError()
            }
            netError?.apply {
                handleError()
            }
        }
    }

    private fun String.handleError() {
        activity.dismissProgressDialog()
        showToast(this)
    }

    private fun handlePraiseData(
        publishCommentView: PublishCommentView?,
        type: BarButtonItem.Type,
        isSelected: Boolean
    ) {
        publishCommentView?.run {
            try {
//                避免出现找不到对应的type 而造成崩溃
                isSelectedByType(type, isSelected)
                setTipsByType(
                    type,
                    if (isSelected) getTipsByType(type) + 1L else getTipsByType(type) - 1L
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}