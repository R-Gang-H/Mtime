package com.kotlin.android.home.ui.adapter

import android.graphics.Rect
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ItemHomeBannerBinding
import com.kotlin.android.home.databinding.ItemHomeBannerItemBinding
import com.kotlin.android.home.ui.bean.BannerItem
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.marginBottom
import com.kotlin.android.ktx.ext.core.marginEnd
import com.kotlin.android.ktx.ext.core.marginStart
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.banner.setCommIndicator

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/10
 */
class BannerBinder(
    val bannerItems: List<BannerItem>,
    val marginRect: Rect = Rect(7.dp, 0, 7.dp, 0)
) : MultiTypeBinder<ItemHomeBannerBinding>() {

    override fun layoutId(): Int {
        return R.layout.item_home_banner
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is BannerBinder && other.bannerItems == bannerItems
    }

    override fun onBindViewHolder(binding: ItemHomeBannerBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.root.apply {
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }
        }
        binding.mHomeBannerView.apply {
            marginStart = marginRect.left
            marginTop = marginRect.top
            marginEnd = marginRect.right
            marginBottom = marginRect.bottom
        }
        binding.mHomeBannerView
            .setRoundCorners(12.dpF)
            .setCommIndicator().adapter = BannerAdapter(bannerItems)
    }

    override fun onViewAttachedToWindow(binding: ItemHomeBannerBinding, position: Int) {
        binding.mHomeBannerView.startTurning()
    }

    override fun onViewDetachedFromWindow(binding: ItemHomeBannerBinding, position: Int) {
        binding.mHomeBannerView.stopTurning()
    }
}

class BannerAdapter(bannerItems: List<BannerItem>) :
    BaseBindingAdapter<BannerItem, ItemHomeBannerItemBinding>(bannerItems) {
    override fun onBinding(binding: ItemHomeBannerItemBinding?, item: BannerItem, position: Int) {
        binding?.apply {
            mHomeBannerItemImgIv.loadImage(
                data = item.img,
                width = 330.dp,
                height = 134.dp
            )
            root.onClick {
                item.applinkData?.run {
//                    getProvider(IMainProvider::class.java)?.startForApplink(this)
                    parseAppLink(
                        context = it.context,
                        appLink = this
                    )
                }
            }
        }
    }
}