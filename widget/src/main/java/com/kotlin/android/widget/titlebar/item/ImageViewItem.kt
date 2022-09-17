package com.kotlin.android.widget.titlebar.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.R
import com.kotlin.android.widget.titlebar.IButtonItem
import com.kotlin.android.widget.titlebar.State

/**
 * 标题栏 ImageView Item 实现
 *
 * Created on 2021/12/13.
 *
 * @author o.s
 */
class ImageViewItem : androidx.appcompat.widget.AppCompatImageView, IButtonItem {

    /**
     * 当前View对象本身
     * 重写：    override val view: View
     *              get() = this
     */
    override val view: View
        get() = this

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

    private fun initView() {
        layoutParams = LinearLayout.LayoutParams(30.dp, 30.dp)
    }

    fun setUrl(url: String) {
        loadImage(
            data = url,
            width = 100.dp,
            height = 100.dp,
            circleCrop = true,
            defaultImgRes = R.drawable.default_circle_image,
        )
    }

    /**
     * 通知Item更新状态
     */
    override fun setState(state: State) {

    }

    /**
     * 通知小红点
     */
    override fun showRedPoint(isShow: Boolean, title: CharSequence?) {
    }
}