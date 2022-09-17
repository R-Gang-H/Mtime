package com.kotlin.android.community.ui.person.binder

import android.view.View
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemPersonCollectionMovieBinding
import com.kotlin.android.community.ui.person.bean.CollectionMovieViewBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description: 收藏电影binder
 */
class CollectionMovieBinder(val bean: CollectionMovieViewBean) :
    MultiTypeBinder<ItemPersonCollectionMovieBinding>() {
    override fun layoutId(): Int = R.layout.item_person_collection_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CollectionMovieBinder && other.bean != bean
    }

    override fun onClick(view: View) {
        if (view.id == R.id.movieRootView) {
            val instance: ITicketProvider? = getProvider(ITicketProvider::class.java)
            instance?.startMovieDetailsActivity(bean.movieId)
        } else {
            super.onClick(view)
        }
    }

    fun notifyWantSeeStatus() {
        bean.isShowing = !bean.isShowing
        notifyAdapterSelfChanged()
    }

}