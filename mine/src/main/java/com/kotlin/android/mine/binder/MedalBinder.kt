package com.kotlin.android.mine.binder

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.widget.ImageView
import android.widget.TextView
import com.kotlin.android.app.data.entity.community.medal.MedalData
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ItemMedalBinding
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class MedalBinder(var bean: MedalData.MedalInfos, private val isAward: Boolean) :
    MultiTypeBinder<ItemMedalBinding>() {
    override fun layoutId(): Int = R.layout.item_medal

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MedalBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemMedalBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        if(!isAward) {
            val filterMatrix = ColorMatrix()
            filterMatrix.setSaturation(0f)
            (binding.medalIv as ImageView).colorFilter = ColorMatrixColorFilter(filterMatrix)
            (binding.medalTv as TextView).setTextColor(getColor(R.color.color_404C57))
        }
    }
}