package com.kotlin.android.community.post.component.item.adapter

import android.view.View
import com.kotlin.android.comment.component.binder.CommentListBinder
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemPkCommentBinding
import com.kotlin.android.community.post.component.item.bean.PKComentViewBean
import com.kotlin.android.community.post.component.item.bean.PRAISE_STATE_INIT
import com.kotlin.android.community.post.component.item.bean.PRAISE_STATE_PRAISE
import com.kotlin.android.community.post.component.item.bean.PRAISE_STATE_UNPRAISE
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/11/2
 * description:pk帖子评论
 */
class PkCommentBinder(var positiveList: MutableList<PKComentViewBean>, var navigateList: MutableList<PKComentViewBean>, var pkListener: ((View, PKComentViewBean, MultiTypeBinder<*>) -> Unit)? = null) : MultiTypeBinder<ItemPkCommentBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_pk_comment
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is PkCommentBinder && (other.positiveList == positiveList || other.navigateList == navigateList)
    }

    override fun onBindViewHolder(binding: ItemPkCommentBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.pkCommentListView?.apply {

            if (navigateList.size != getNaviSize()) {
                clearNavi()
                setCommentList(navigateList, false)
            }
            if (positiveList.size != getPositiveSize()) {
                clearPositivie()
                setCommentList(positiveList, true)
            }

            setPkListener { view, pkComentViewBean ->
                pkListener?.invoke(view, pkComentViewBean, this@PkCommentBinder)
            }

        }
    }


    fun updatePraiseUp(bean: PKComentViewBean) {
        binding?.pkCommentListView?.updatePraiseUp(bean)
    }

    fun updatePraiseDown(bean: PKComentViewBean) {
        binding?.pkCommentListView?.updatePraiseDown(bean)
    }


    fun addComment(commentList: MutableList<MultiTypeBinder<*>>, positive: Boolean,familyStatus:Long,userGroupRole:Long, familyPostStatus: Long, groupId: Long,commentPmsn: Long?) {
        synchronized(this) {
            val elements = commentList.filter { it is CommentListBinder }.map {
                val binder = it as CommentListBinder
                val bean = binder.bean
                PKComentViewBean.create(bean, positive)
            }.toMutableList()
            if (positive) {
                positiveList.addAll(elements)
            } else {
                navigateList.addAll(elements)
            }
            binding?.pkCommentListView?.addCommentBinderList(commentList.filter { it is CommentListBinder }.toMutableList(),positive)
            binding?.pkCommentListView?.setBlackUser(familyStatus, userGroupRole, familyPostStatus, groupId, commentPmsn)
            notifyAdapterSelfChanged()
        }
    }

    fun updateGroup(familyStatus:Long,userGroupRole:Long, familyPostStatus: Long, groupId: Long,commentPmsn: Long?){
        binding?.pkCommentListView?.setBlackUser(familyStatus, userGroupRole, familyPostStatus, groupId, commentPmsn)
    }

    fun deleteComment(commentId: Long) {
        binding?.pkCommentListView?.deleteComment(commentId, true)
        binding?.pkCommentListView?.deleteComment(commentId, false)
    }

    fun addPkComment(pkCommentBean: PKComentViewBean, isCommentPositive: Boolean, isFirst: Boolean = false) {
        if (isCommentPositive) {
            if (isFirst) {
                positiveList.add(0, pkCommentBean)
            } else {
                positiveList.add(pkCommentBean)
            }
        } else {
            if (isFirst) {
                navigateList.add(0, pkCommentBean)
            } else {
                navigateList.add(pkCommentBean)
            }
        }
        binding?.pkCommentListView?.addComment(pkCommentBean, isCommentPositive, isFirst)
        notifyAdapterSelfChanged()
    }

}