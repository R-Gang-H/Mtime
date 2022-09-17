package com.kotlin.android.card.monopoly.widget.suit.gallery

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 *
 * Created on 2021/2/23.
 *
 * @author o.s
 */
class GalleryItemLayout : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    fun initView() {

    }

    var bitmap: Bitmap? = null
        set(value) {
            field?.recycle()
            field = value
            if (value != null) {
                invalidateParentView()
            }
        }

    private fun invalidateParentView() {
        (parent as? ViewGroup)?.invalidate()
    }

    var url: String? = null

    fun recycle() {
        bitmap?.apply {
            if (!isRecycled) {
                recycle()
            }
        }
    }

    data class Data(
        var bitmap: Bitmap? = null,
        var url: String? = null,
        var tag: String? = null
    )
}