package com.kotlin.android.mine.binder

import android.graphics.Rect
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.DataCenterViewBean
import com.kotlin.android.mine.databinding.MineItemCreatDataCenterBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.binder
 * @ClassName:      DataLableBinder
 * @Description:    创作中心-数据
 * @Author:         haoruigang
 * @CreateDate:     2022/3/14 17:02
 */
class DataLableBinder(
    val data: DataCenterViewBean.DataAmount,
    val rootMargin: Rect = Rect(4.dp, 0.dp, 4.dp, 0.dp),
) :
    MultiTypeBinder<MineItemCreatDataCenterBinding>() {

    override fun layoutId(): Int = R.layout.mine_item_creat_data_center

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is DataLableBinder && other.data != data
    }

    override fun onBindViewHolder(binding: MineItemCreatDataCenterBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            bean = this@DataLableBinder
            root.apply {
                marginTop = rootMargin.top
                marginBottom = rootMargin.bottom
                marginStart = rootMargin.left
                marginEnd = rootMargin.right
            }
            rlFrameBg.setBackground(
                colorRes = data.bgColor,
                strokeWidth = 1.dp,
                strokeColorRes = data.frameColor,
                cornerRadius = 8.dpF
            )
        }
    }
}