package com.kotlin.android.ugc.web.component.widgets

import android.content.Context
import android.util.AttributeSet
import com.kotlin.android.qb.webview.X5WebView


/**
 * create by lushan on 2020/10/19
 * description:
 */
class NoBlankScrollWebView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : X5WebView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val makeMeasureSpec = MeasureSpec.makeMeasureSpec(Int.MAX_VALUE.shr(2), MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, makeMeasureSpec)
    }

}