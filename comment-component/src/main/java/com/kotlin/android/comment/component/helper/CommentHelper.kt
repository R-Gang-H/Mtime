package com.kotlin.android.comment.component.helper

import com.kotlin.android.comment.component.bar.item.BarButtonItem
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.comment.component.binder.CommentListEmptyBinder
import com.kotlin.android.comment.component.binder.CommentListTitleBinder
import com.kotlin.android.comment.component.widget.PublishCommentView
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/10/21
 * description:
 */
object CommentHelper {

    fun deleteComment(commentId: Long, allBinderList: MutableList<MultiTypeBinder<*>>, hotCommentBinderList: MutableList<MultiTypeBinder<*>>, mAdapter: MultiTypeAdapter){
        removeCommentById(allBinderList, commentId)
        hotCommentBinderList.filter { it is CommentListBinder && it.bean.commentId == commentId }.forEach { it.notifyAdapterSelfRemoved() }
        removeCommentById(hotCommentBinderList, commentId)
        if (hotCommentBinderList.size == 0) {
            val elementBinder = CommentListEmptyBinder()
            hotCommentBinderList.add(elementBinder)
            allBinderList.add(elementBinder)
            mAdapter.notifyAdapterAdded(hotCommentBinderList)
        }
    }

    private fun removeCommentById(list: MutableList<MultiTypeBinder<*>>, commentId: Long) {
        list.removeIf { it is CommentListBinder && it.bean.commentId == commentId }
    }



    fun addCommentBinder(hotCommentBinderList: MutableList<MultiTypeBinder<*>>, binder: CommentListBinder){
        removeEmptyBinder(hotCommentBinderList, binder)
    }

    private fun removeEmptyBinder(binderList: MutableList<MultiTypeBinder<*>>, binder: CommentListBinder) {
        binderList.removeIf { it is CommentListEmptyBinder }
        binderList.add(0, binder)
    }

    /**
     * 更新详情页面底部状态
     */
    fun updateCommentLayout(ugcCommonBarBean: UgcCommonBarBean?, barButton: PublishCommentView?, isLongReview: Boolean = false) {
        ugcCommonBarBean ?: return

        barButton?.apply {
            style = if (isLongReview) {
                if (ugcCommonBarBean.canComment) {
                    PublishCommentView.Style.LONG_COMMENT
                } else {
                    PublishCommentView.Style.NOT_LONG_COMMENT
                }
            } else {
                if (ugcCommonBarBean.canComment) {
                    PublishCommentView.Style.COMMENT
                } else {
                    PublishCommentView.Style.NOT_COMMENT
                }
            }
            inputEnable(ugcCommonBarBean.canComment)
            setCommentCount(barButton, BarButtonItem.Type.COMMENT, ugcCommonBarBean.commentSupport.commentCount)
            handlePraiseData(barButton, BarButtonItem.Type.PRAISE, ugcCommonBarBean.commentSupport.praiseUpCount, ugcCommonBarBean.commentSupport.userPraised == 1L)
            handlePraiseData(barButton, BarButtonItem.Type.DISPRAISE, ugcCommonBarBean.commentSupport.praiseDownCount, ugcCommonBarBean.commentSupport.userPraised == 2L)
            handlePraiseData(barButton, BarButtonItem.Type.FAVORITE, 0, ugcCommonBarBean.commentSupport.userCollected)

        }
    }

    /**
     * 更新详情页面底部状态
     */
    fun updateCommentLayout(ugcCommonBarBean: UgcCommonBarBean?, barButton: PublishCommentView?, isLongReview: Boolean = false,isCardFlag: Boolean? = false) {
        ugcCommonBarBean ?: return

        barButton?.apply {
            if(isCardFlag == true){
                style = PublishCommentView.Style.WITH_NONE
            }
            inputEnable(ugcCommonBarBean.canComment)
            setCommentCount(barButton, BarButtonItem.Type.COMMENT, ugcCommonBarBean.commentSupport.commentCount)
            handlePraiseData(barButton, BarButtonItem.Type.PRAISE, ugcCommonBarBean.commentSupport.praiseUpCount, ugcCommonBarBean.commentSupport.userPraised == 1L)
            handlePraiseData(barButton, BarButtonItem.Type.DISPRAISE, ugcCommonBarBean.commentSupport.praiseDownCount, ugcCommonBarBean.commentSupport.userPraised == 2L)
            handlePraiseData(barButton, BarButtonItem.Type.FAVORITE, 0, ugcCommonBarBean.commentSupport.userCollected)

        }
    }

    private fun setCommentCount(publishCommentView: PublishCommentView?, type: BarButtonItem.Type, number: Long) {

        publishCommentView?.run {
            try {
//                避免出现找不到对应的type 而造成崩溃
                setTipsByType(type, number)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    private fun handlePraiseData(publishCommentView: PublishCommentView?, type: BarButtonItem.Type, number: Long, isSelected: Boolean) {
        publishCommentView?.run {
            try {
//                避免出现找不到对应的type 而造成崩溃
                isSelectedByType(type, isSelected)
                setTipsByType(type, number)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    /**
     * 重置底部状态栏
     */
    fun resetInput(ugcCommonBarBean: UgcCommonBarBean?, barButton: PublishCommentView?, updateBarState: CommentHelper.UpdateBarState, isLongReview: Boolean = false) {
        when (updateBarState) {
            UpdateBarState.INIT -> {//初始化

            }
            UpdateBarState.ADD -> {//增加
                ugcCommonBarBean?.apply {
                    commentSupport.commentCount++
                }
            }
            UpdateBarState.DELETE -> {//删除
                ugcCommonBarBean?.apply {
                    commentSupport.commentCount = if (commentSupport.commentCount <= 0) 0 else commentSupport.commentCount - 1

                }
            }
        }

        ugcCommonBarBean?.apply {
            updateCommentLayout(ugcCommonBarBean, barButton, isLongReview)
        }

    }


    /**
     * 重置底部状态栏(卡片评论使用)
     */
    fun resetInput(ugcCommonBarBean: UgcCommonBarBean?, barButton: PublishCommentView?, updateBarState: CommentHelper.UpdateBarState, isLongReview: Boolean = false,isCardFlag:Boolean? = false) {
        when (updateBarState) {
            UpdateBarState.INIT -> {//初始化

            }
            UpdateBarState.ADD -> {//增加
                ugcCommonBarBean?.apply {
                    commentSupport.commentCount++
                }
            }
            UpdateBarState.DELETE -> {//删除
                ugcCommonBarBean?.apply {
                    commentSupport.commentCount = if (commentSupport.commentCount <= 0) 0 else commentSupport.commentCount - 1

                }
            }
        }

        ugcCommonBarBean?.apply {
            if(isCardFlag == true){
                updateCommentLayout(ugcCommonBarBean, barButton, isLongReview,isCardFlag)
            }
        }

    }




    /**
     * 更新标题数量
     */
     fun updateCommentTitleList(list: MutableList<MultiTypeBinder<*>>, isDelete: Boolean) {
        list.filter { it is CommentListTitleBinder }.forEach {
            (it as CommentListTitleBinder).bean.totalCount = if (isDelete) {
                if (it.bean.totalCount - 1L > 0L) it.bean.totalCount - 1L else 0L
            } else {
                it.bean.totalCount + 1L
            }
        }
    }

    enum class UpdateBarState {
        INIT,//初始化
        ADD,//增加item
        DELETE//删除item
    }
}