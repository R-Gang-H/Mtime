package com.kotlin.android.mine.binder

import android.view.View
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthMovieTypeBean
import com.kotlin.android.mine.databinding.ItemAuthenMovieTypeBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/9
 * description:电影人角色认证
 */
class AuthMovieTypeBinder(var bean: AuthMovieTypeBean) : MultiTypeBinder<ItemAuthenMovieTypeBinding>() {
    override fun layoutId(): Int = R.layout.item_authen_movie_type

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is AuthMovieTypeBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemAuthenMovieTypeBinding, position: Int) {
        super.onBindViewHolder(binding, position)

        binding.tagTv.apply {
            val color = if (bean.canClick) {
                if (bean.isSelected) {
                    R.color.color_ffffff
                } else {
                    R.color.color_8798af
                }
            } else {
                R.color.color_4e5e73
            }
            setTextColor(getColor(color))
            if (bean.canClick){
                ShapeExt.setShapeColorAndCorner(this,if (bean.isSelected)R.color.color_20a0da else R.color.color_f2f3f6,5)
            }else{
                background = null
            }
        }
    }

    override fun onClick(view: View) {
        if (bean.canClick.not()){
            return
        }
        super.onClick(view)
    }

}