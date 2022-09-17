package com.kotlin.android.community.card.component.item.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 */
abstract class CommunityCardBaseBinder<T : ViewDataBinding>(val item: CommunityCardItem) : MultiTypeBinder<T>() {

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityCardBaseBinder
                && other.item.id == item.id
                && other.item.isLike != item.isLike
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityCardRoot -> {
                getProvider(IUgcProvider::class.java) {
                    launchDetail(
                        contentId = item.id,
                        type = item.type
                    )
                }
            }

            R.id.mCommunityCardUserLayout -> {
                getProvider(ICommunityPersonProvider::class.java) {
                    startPerson(item.userId)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    fun praiseUpChanged() {
        item.isLike = !item.isLike
        if (item.isLike) {
            item.likeCount++
        } else {
            item.likeCount--
        }
        notifyAdapterSelfChanged()
    }
}
