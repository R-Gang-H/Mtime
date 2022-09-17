package com.kotlin.android.ugc.detail.component.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.adapter.UgcImageAdapter
import com.kotlin.android.ugc.detail.component.bean.UgcImageViewBean
import kotlinx.android.synthetic.main.view_ugc_banner.view.*

/**
 * create by lushan on 2020/8/7
 * description:ugc图集详情
 */
class UgcBannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    private var bannerAdapter: UgcImageAdapter = UgcImageAdapter(context, mutableListOf())


    init {
        removeAllViews()
        val inflate = LayoutInflater.from(context).inflate(R.layout.view_ugc_banner, null)
        addView(inflate)
    }

    fun setData(title: String, list: MutableList<UgcImageViewBean>) {
        bannerAdapter.setData(list)
        ugcIndexTv?.text = if (list.isNotEmpty()) "1/${list.size}" else ""
        ugcContentTv?.text = if (list.isNotEmpty()) list[0].ugcContent else ""
        ugcTitleTv?.text = title

        val layoutParams = ugcTitleTv?.layoutParams as? MarginLayoutParams
        layoutParams?. topMargin= 58.dp + statusBarHeight
        ugcTitleTv?.layoutParams = layoutParams

        "setData==ugcIndexTv：$ugcIndexTv== ugcContentTv:$ugcContentTv == ugcTitleTv:$ugcTitleTv ==viewPager2:$viewPager2".e()
        setStyle()
    }

    private fun setStyle(){
        ShapeExt.setGradientColor(contentFL, startColor = R.color.color_00000000, endColor = R.color.color_80000000, corner = 0)
        ShapeExt.setShapeColorAndCorner(ugcIndexTv, R.color.color_8a1d2736, 12)
        ShapeExt.setGradientColor(titleFL, startColor = R.color.color_80000000, endColor = R.color.color_00000000, corner = 0)

        viewPager2?.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = bannerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
//                    "setStyle==ugcIndexTv：${this@UgcBannerView.ugcIndexTv}== ugcContentTv:${this@UgcBannerView.ugcContentTv} == ugcTitleTv:${this@UgcBannerView.ugcTitleTv} ==viewPager2:$viewPager2".e()
                    this@UgcBannerView.ugcContentTv?.text = bannerAdapter.getContent(position)
//                    titleTv?.text = bannerAdapter.getTitle(position)
                    this@UgcBannerView.ugcIndexTv?.text = "${position + 1}/${bannerAdapter.itemCount}"
                }
            })

        }
    }

}