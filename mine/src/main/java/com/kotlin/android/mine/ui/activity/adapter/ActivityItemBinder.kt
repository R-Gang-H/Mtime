package com.kotlin.android.mine.ui.activity.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.ktx.ext.core.marginEnd
import com.kotlin.android.ktx.ext.core.marginStart
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.ActivityViewBean
import com.kotlin.android.mine.databinding.ItemActivityBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/17
 * 描述: 活动Item
 */
class ActivityItemBinder(
        val context: Context,
        val viewBean: ActivityViewBean,
        val isMine: Boolean = false    // 是否个人中心页
) : MultiTypeBinder<ItemActivityBinding>() {

    // 个人中心item布局
    private val mItemLayoutPaddingTop = 7.dp
    private val mItemLayoutPaddingBottom = 8.dp
    private val mNumBgCornerRadius = 2.5F.dpF
    private val mNumTvMarginStart = 16.dp
    private val mImgCardMarginStart = 10.dp
    private val mBtnMarginEnd = 13.dp

    private val mBtnCornerCorner = 15.dpF

    override fun layoutId(): Int {
        return R.layout.item_activity
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ActivityItemBinder && other.viewBean == viewBean
    }

    override fun onBindViewHolder(binding: ItemActivityBinding, position: Int) {
        binding.apply {
            if(isMine) {
                // 个人中心页
                itemLayout.setPadding(0, mItemLayoutPaddingTop,0, mItemLayoutPaddingBottom)
                activityNumBgView.visible()
                activityNumBgView.setBackground(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = mNumBgCornerRadius
                )
                activityNumTv.visible()
                activityNumTv.marginStart = mNumTvMarginStart
                activityNumTv.text = (position + 1).toString()
                activityImgCard.marginStart = mImgCardMarginStart
                activityEnterTv.marginEnd = mBtnMarginEnd
            }
            // 按钮背景
            activityEnterTv.setBackground(
                    colorRes = R.color.color_20a0da,
                    endColorRes = R.color.color_1bafe0,
                    orientation = GradientDrawable.Orientation.BL_TR,
                    cornerRadius = mBtnCornerCorner,
            )
        }
    }

    override fun onClick(view: View) {
        // appLink跳转
        parseAppLink(context, viewBean.appSkipLink)
    }
}