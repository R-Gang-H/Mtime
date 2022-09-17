package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CollectionCinemaViewBean
import com.kotlin.android.mine.databinding.ItemCollectionCinemaBinding

import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description: 影院收藏binder
 */
class CollectionCinemaBinder(var bean: CollectionCinemaViewBean) : MultiTypeBinder<ItemCollectionCinemaBinding>() {
    override fun layoutId(): Int = R.layout.item_collection_cinema

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is CollectionCinemaBinder && other.bean != bean

    override fun onClick(view: View) {
        if (view.id == R.id.cinemaRootView) {
            val provider: ITicketProvider? = getProvider(ITicketProvider::class.java)
            provider?.startCinemaShowTimeActivity(bean.cinemaId, "", "")
        } else {
            super.onClick(view)
        }
    }
}