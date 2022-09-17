package com.kotlin.android.widget.banner

import android.view.ViewGroup
import android.widget.RelativeLayout
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.R

/**
 * xx
 */
fun Banner.setCommIndicator(): Banner {
    val params = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
    params.bottomMargin = 10.dp
    params.rightMargin = 10.dp

    setIndicator(
        IndicatorView(context)
            .setIndicatorSpacing(0f)
            .setIndicatorRadius(2.5f)
            .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_DASH)
            .setIndicatorSelectedRatio(2f)
            .setIndicatorColorRes(R.color.color_80ffffff)
            .setIndicatorSelectorColorRes(R.color.color_ffffff)
            .setParams(params)
    )
    return this
}

fun Banner.setMemberLevelIndicator(): Banner {
    val params = RelativeLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
    params.addRule(RelativeLayout.CENTER_HORIZONTAL)
    params.bottomMargin = 8.dp
    params.leftMargin = 10.dp

    setIndicator(
        IndicatorView(context)
            .setIndicatorSpacing(2.5f)
            .setIndicatorRadius(2.5f)
            .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_DASH)
            .setIndicatorSelectedRatio(2f)
            .setIndicatorColorRes(R.color.color_b8bfc7)
            .setIndicatorSelectorColorRes(R.color.color_004696)
            .setParams(params)
    )
    return this
}