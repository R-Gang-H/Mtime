package com.kotlin.android.live.component.ui.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.live.component.R
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getDrawable

/**
 * create by lushan on 2021/3/9
 * description:
 */

//设置机位选中的背景
@BindingAdapter(value = ["camerastandbg","portrait"])
fun setCameraStandSelectedBg(view:View,isSelected:Boolean,isPortrait:Boolean){
    if (isSelected){
        ShapeExt.setShapeCorner2Color2Stroke(view, R.color.color_3300000000,if (isPortrait) 4 else 6,R.color.color_feb12a,1)
    }else{
        view.background = null
    }
}

//设置机位选中和未选中drawable
@BindingAdapter(value = ["cameraStandTitle","portrait"],requireAll = true)
fun setCameraStandTitleStyle(view:TextView,isSelected: Boolean,isPortrait:Boolean){
    if (isSelected) {
        val drawable = getDrawable(R.drawable.ic_live_play)?.apply {
            setBounds(0, 0, if (isPortrait) 8.dp else 10.dp, if (isPortrait) 8.dp else 11.dp)
        }
        view.setCompoundDrawables(drawable,null,null,null)
    }else{
        view.setCompoundDrawables(null,null,null,null)
    }
}

@BindingAdapter("item_width_height_margin_left")
fun setItemWidth_hight_marginLeft(view:View,isPortrait: Boolean){
    val layoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
    layoutParams.width =  view.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_132px else R.dimen.offset_206px)
    layoutParams.height =  view.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_78px else R.dimen.offset_122px)
    layoutParams.leftMargin =  view.resources.getDimensionPixelOffset(if (isPortrait) R.dimen.offset_20px else R.dimen.offset_30px)
    view.layoutParams = layoutParams

}

