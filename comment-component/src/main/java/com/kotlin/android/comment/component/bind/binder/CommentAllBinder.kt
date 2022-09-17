package com.kotlin.android.comment.component.bind.binder

import com.kotlin.android.bind.holder.ItemViewBindingHolder
import com.kotlin.android.bind.item.ItemViewBindingBinder
import com.kotlin.android.comment.component.databinding.BindItemCommentAllBinding
import com.kotlin.android.app.data.entity.comment.CommentAll
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider

/**
 *
 *
 * Created on 2021/7/13.
 *
 * @author o.s
 */
class CommentAllBinder : ItemViewBindingBinder<CommentAll, BindItemCommentAllBinding, CommentAllBinder.Holder>() {

    class Holder(binding: BindItemCommentAllBinding) : ItemViewBindingHolder<CommentAll, BindItemCommentAllBinding>(binding) {
        override fun onBind(binding: BindItemCommentAllBinding, position: Int, item: CommentAll) {
            binding.titleView.text = item.title
            binding.titleView.setOnClickListener {
                gotoMessageBoard(item)
            }
        }

        private fun gotoMessageBoard(item: CommentAll) {
            getProvider(ICardMonopolyProvider::class.java) {
                startSuitCommentActivity(
                    contentId = item.objId,
                    title = item.objTitle,
                    type = item.type
                )
            }
        }
    }

}