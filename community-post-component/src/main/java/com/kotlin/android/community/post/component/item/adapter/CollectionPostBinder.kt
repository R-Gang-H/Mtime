package com.kotlin.android.community.post.component.item.adapter

import android.view.View
import com.kotlin.android.community.post.component.R
import com.kotlin.android.community.post.component.databinding.ItemCollectionPostBinding
import com.kotlin.android.community.post.component.item.bean.CollectionPostViewBean
import com.kotlin.android.app.data.constant.CommConstant.PRAISE_OBJ_TYPE_POST
import com.kotlin.android.mtime.ktx.getDrawable

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/15
 * description:帖子收藏binder
 */
class CollectionPostBinder(var bean: CollectionPostViewBean) : MultiTypeBinder<ItemCollectionPostBinding>() {
    override fun layoutId(): Int = R.layout.item_collection_post

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is CollectionPostBinder && other.bean != bean

    override fun onClick(view: View) {
        if (view.id == R.id.postRootView) {
            val provider: IUgcProvider? = getProvider(IUgcProvider::class.java)
            provider?.launchDetail(bean.postId, PRAISE_OBJ_TYPE_POST)
        } else {
            super.onClick(view)
        }
    }

    override fun onBindViewHolder(binding: ItemCollectionPostBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        getDrawable(R.drawable.ic_comment_reply)?.apply {
            setBounds(0, 0, intrinsicWidth / 2, intrinsicHeight / 2)
        }?.also {
            binding.replyTv.setCompoundDrawables(it, null, null, null)
        }
    }
}