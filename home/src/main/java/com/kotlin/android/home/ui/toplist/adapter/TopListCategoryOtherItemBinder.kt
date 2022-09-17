package com.kotlin.android.home.ui.toplist.adapter

import android.graphics.drawable.GradientDrawable
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemToplistCategoryOtherBinding
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.ShapeExt

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/20
 * @desc 分类榜单_2~N条
 */
class TopListCategoryOtherItemBinder(val bean: TopListInfo) : MultiTypeBinder<ItemToplistCategoryOtherBinding>() {

    val homeProvider = getProvider(IHomeProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_toplist_category_other
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TopListCategoryOtherItemBinder && other.bean.id == bean.id
    }

    override fun onBindViewHolder(binding: ItemToplistCategoryOtherBinding, position: Int) {
        // 背景渐变蒙层
        ShapeExt.setGradientColor(binding.mItemToplistCategoryOtherCoverView,
                GradientDrawable.Orientation.RIGHT_LEFT,
                R.color.color_4e6382,
                R.color.color_1d2736,
                0)
        binding.mItemToplistCategoryOtherCoverView.alpha = 0.2f
        // 背景图片: 获取榜单封面图，如果为空，则获取第一个item的封面图
        val url = bean.getCoverImgOrFristItemImg()
        // 高斯模糊
        binding.mItemToplistCategoryOtherBgIv.loadImage(
            data = url,
            width = 335.dp,
            height = 110.dp,
            blurRadius = 25F,
            blurSampling = 4F
        )
    }
}