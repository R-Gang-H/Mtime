package com.kotlin.android.publish.component.ui.binder

import android.view.View
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.bean.RelateMovieViewBean
import com.kotlin.android.publish.component.databinding.ItemVideoPublishRelatedMovieBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/30
 * des:相关的电影
 **/
class RelateMovieBinder(var bean:RelateMovieViewBean):
    MultiTypeBinder<ItemVideoPublishRelatedMovieBinding>() {
    override fun layoutId(): Int  = R.layout.item_video_publish_related_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is RelateMovieBinder && other.bean.id == bean.id && other.bean.name == bean.name
    }



    override fun onClick(view: View) {
        when(view.id){
            R.id.deleteIv->{
                notifyAdapterSelfRemoved()
                //向外传递，刷新总数
            }
            else->{
            }
        }
        super.onClick(view)
    }

}