package com.kotlin.android.widget.titlebar.item

import android.content.Context
import android.util.AttributeSet
import android.widget.*
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.R

/**
 * 主页搜索/扫一扫标题中间视图
 *
 * Created on 2021/12/14.
 *
 * @author o.s
 */

class HomeCenterTitleView : FrameLayout {
    private var titleView: TextView? = null
    private var scanView: ImageView? = null

    private val cornerRadius = 15.dpF

    var action: (() -> Unit)? = null

    constructor(context: Context) : super(context) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        initView()
    }

    private fun initView() {
        inflate(context, R.layout.view_home_center_title, this)
        titleView = findViewById(R.id.titleView)
        scanView = findViewById(R.id.scanView)

        titleView?.setBackground(
            colorRes = R.color.color_1a8798af,
            cornerRadius = cornerRadius,
        )

        scanView?.setOnClickListener {
            action?.invoke()
        }
    }
}