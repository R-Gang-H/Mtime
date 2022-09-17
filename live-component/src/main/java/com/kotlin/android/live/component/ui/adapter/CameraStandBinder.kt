package com.kotlin.android.live.component.ui.adapter

import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemCameraStandBinding
import com.kotlin.android.live.component.viewbean.CameraStandViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2021/3/9
 * description:机位列表
 */
class CameraStandBinder(var bean: CameraStandViewBean, var isPortrait: Boolean = true) : MultiTypeBinder<ItemCameraStandBinding>() {
    override fun layoutId(): Int = R.layout.item_camera_stand

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean = other is CameraStandBinder && other.bean != bean && other.isPortrait != isPortrait

    override fun onBindViewHolder(binding: ItemCameraStandBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        ShapeExt.setGradientColorWithColor(binding.cameraBgView, startColor = getColor(R.color.color_00000000), endColor = getColor(R.color.color_80000000), corner = 0)
    }
}
