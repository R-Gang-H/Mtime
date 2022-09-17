package com.mtime.bussiness.ticket.adapter

import android.content.Context
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import com.mtime.bussiness.ticket.bean.TicketBannerItem
import com.mtime.databinding.ItemTabTicketBannerBinding

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/13
 * 描述: 购票tab BannerAdapter
 */
class TabTicketBannerAdapter(
        context: Context,
        bannerItems: List<TicketBannerItem>
) :
        BaseBindingAdapter<TicketBannerItem, ItemTabTicketBannerBinding>(bannerItems) {

    private val mContext = context
    private val mImageWidth = 330.dp
    private val mImageHeight = 105.dp

    override fun onBinding(binding: ItemTabTicketBannerBinding?, item: TicketBannerItem, position: Int) {
        binding?.apply {
            bannerIv.loadImage(
                    data = item.appImage,
                    width = mImageWidth,
                    height = mImageHeight
            )
            root.onClick {
                // AppLink跳转
                parseAppLink(mContext, item.appLink)
            }
        }
    }

}