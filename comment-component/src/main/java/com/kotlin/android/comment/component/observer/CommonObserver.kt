package com.kotlin.android.comment.component.observer

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.common.CollectionResult
import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.DetailBaseExtend
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentReplyBinder
import com.kotlin.android.comment.component.binder.CommentReplyDetailBinder
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/3
 * description:详情下方点赞、点踩、收藏
 */

class CommonObserver(var activity: FragmentActivity, var handleType: Int, var commentView: PublishCommentView?, var type: BarButtonItem.Type) : Observer<BaseUIModel<DetailBaseExtend<Any>>> {
    companion object {
        const val TYPE_PRAISE_UP = 1//点赞
        const val TYPE_PRAISE_DOWN = 2//点踩
        const val TYPE_COLLECTIN = 3//收藏
        const val TYPE_DELETE_COMMENT = 4//删除评论

         fun handlePraiseData(publishCommentView: PublishCommentView?, type: BarButtonItem.Type, isSelected: Boolean) {
            publishCommentView?.run {
                try {
//                避免出现找不到对应的type 而造成崩溃
                    isSelectedByType(type, isSelected)
                    setTipsByType(type, if (isSelected) getTipsByType(type) + 1L else getTipsByType(type) - 1L)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    private var mainProvider: IMainProvider? = getProvider(IMainProvider::class.java)
    override fun onChanged(t: BaseUIModel<DetailBaseExtend<Any>>?) {
        t?.apply {
            success?.apply {
                activity.dismissProgressDialog()
                when (handleType) {
                    TYPE_PRAISE_UP -> {//点赞
                        when (extend) {
                            is CommentListBinder -> {//外部大评论列表中的点赞
                                (extend as? CommentListBinder)?.upatePraiseUp()
                            }
                            is CommentReplyBinder -> {////回复列表点赞
                                (extend as? CommentReplyBinder)?.updatePraiseUp()
                            }

                            is CommentReplyDetailBinder -> {//评论详情上方点赞
                                (extend as? CommentReplyDetailBinder)?.updatePraiseUp()
                            }
                        }
                        if (extend !is MultiTypeBinder<*>) {//外部大评论列表中的点赞
                            if (commentView?.hasType(BarButtonItem.Type.DISPRAISE) == true && commentView?.getSelectedStatusByType(BarButtonItem.Type.DISPRAISE) == true) {//点踩了
                                //                            需要处理底部点踩消息  点赞和点踩是互斥的
                                handlePraiseData(commentView, BarButtonItem.Type.DISPRAISE, (result as Boolean))
                            }
//                            处理底部点赞消息
                            handlePraiseData(commentView, type, (result as Boolean).not())

                        }
                    }
                    TYPE_PRAISE_DOWN -> {//点踩
                        if (extend !is MultiTypeBinder<*>) {
                            if (commentView?.hasType(BarButtonItem.Type.PRAISE) == true && commentView?.getSelectedStatusByType(BarButtonItem.Type.PRAISE) == true) {//点赞了
                                handlePraiseData(commentView, BarButtonItem.Type.PRAISE, result as Boolean)
                            }
                            handlePraiseData(commentView, type, (result as Boolean).not())

                        }
                    }

                    TYPE_COLLECTIN -> {//收藏
                        if (extend !is MultiTypeBinder<*>) {
                            val collectionResult = result as? CollectionResult
                            if (collectionResult?.isSuccess().orFalse()) {
                                commentView?.isSelectedByType(type, commentView?.getSelectedStatusByType(type).orFalse().not())
                            }
                        }
                    }

                    TYPE_DELETE_COMMENT->{//删除评论
                        if (extend is MultiTypeBinder<*>){
                            (extend as? MultiTypeBinder<*>)?.notifyAdapterSelfRemoved()
                        }
                    }
                }
            }

            netError?.apply {
                activity.dismissProgressDialog()
                showToast(this)
            }
            error?.apply {
                activity.dismissProgressDialog()
                showToast(this)
            }
            if (needLogin) {
                activity.dismissProgressDialog()
                //需要登录
                mainProvider?.startLoginActivity(null)
            }
        }
    }



}