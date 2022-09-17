package com.kotlin.android.community.post.component.item.adapter

import android.view.View
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemPkVoteBinding
import com.kotlin.android.community.post.component.item.bean.PKStatusViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/11/2
 * description: pk帖子详情投票信息
 */
class PkVoteDetailBinder(var bean: PKStatusViewBean) : MultiTypeBinder<ItemPkVoteBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_pk_vote
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is PkVoteDetailBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemPkVoteBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.positiveTv?.apply {
            ShapeExt.setShapeCorner2Color2Stroke(this, corner = 14, strokeColor = R.color.color_36c096)
        }
        binding.navigationTv?.apply {
            ShapeExt.setShapeCorner2Color2Stroke(this, corner = 14, strokeColor = R.color.color_feb12a)
        }
        startPkPercentAnim(bean.positionCount, bean.joinCount)
    }

    /**
     * 设置pk百分比
     */
    private fun startPkPercentAnim(leftPercent: Long, totalCount: Long) {
        if (totalCount != 0L) {
            val percent = (leftPercent * 100 / totalCount).toInt()
            val rightPercent = 100 - percent
            binding?.pkPercentView?.setPercent(percent, rightPercent)?.startAnim()
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.positivePercentTv, R.id.positivePercentDesTv, R.id.navigationPercentTv, R.id.navigationPercentDesTv -> {
                showToast(R.string.community_post_has_vote)
                binding?.pkPercentView?.resetOnShot()
                startPkPercentAnim(bean.positionCount, bean.joinCount)
//                notifyAdapterSelfChanged()
            }
            else -> {
                super.onClick(view)
            }
        }

    }
}