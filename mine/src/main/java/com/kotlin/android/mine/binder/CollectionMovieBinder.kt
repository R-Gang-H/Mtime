package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.CollectionMovieViewBean
import com.kotlin.android.mine.databinding.ItemCollectionMovieBinding

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/14
 * description: 收藏电影binder
 */
class CollectionMovieBinder(val bean: CollectionMovieViewBean) : MultiTypeBinder<ItemCollectionMovieBinding>() {
    override fun layoutId(): Int = R.layout.item_collection_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CollectionMovieBinder && other.bean != bean
    }

    override fun onClick(view: View) {
        if (view.id == R.id.movieRootView){
            val instance:ITicketProvider? = getProvider(ITicketProvider::class.java)
            instance?.startMovieDetailsActivity(bean.movieId)
        }else {
            super.onClick(view)
        }
    }

}