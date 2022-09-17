package com.kotlin.android.publish.component.ui.binder

import android.view.View
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.bean.RelateMovieViewBean
import com.kotlin.android.publish.component.databinding.ItemVideoPublishClassBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/30
 * des:
 **/
class RelateMovieClassBinder(var bean: RelateMovieViewBean) :
    MultiTypeBinder<ItemVideoPublishClassBinding>() {
    override fun layoutId(): Int = R.layout.item_video_publish_class

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is RelateMovieClassBinder && other.bean.id == bean.id && other.bean.isSelected == bean.isSelected
    }

    fun updateSelected(isSelected: Boolean) {
        if (bean.isSelected != isSelected) {
            bean.isSelected = isSelected
            notifyAdapterSelfChanged()
        }
    }


    override fun onClick(view: View) {
        super.onClick(view)

    }
}