package com.kotlin.android.widget.titlebar.item

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.kotlin.android.widget.titlebar.State

/**
 * 标题栏 Title TextView 实现
 *
 * Created on 2021/12/13.
 *
 * @author o.s
 */
class TitleView : AppCompatTextView {

    private var state: State = State.NORMAL
    private var isDisplay = false

    @ColorRes
    private var colorResId: Int = 0

    @ColorRes
    private var colorReverseResId: Int = 0

    private var startDrawable: Drawable? = null
    private var reverseStartDrawable: Drawable? = null
    private var endDrawable: Drawable? = null
    private var reverseEndDrawable: Drawable? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    fun init() {

    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
//    }

    fun setTextColor(@ColorRes resId: Int, @ColorRes reverseResId: Int) {
        this.colorResId = resId
        this.colorReverseResId = reverseResId
        refreshTextColor()
    }

    fun setStartDrawable(startDrawable: Drawable?, reverseStartDrawable: Drawable?) {
        this.startDrawable = startDrawable
        this.reverseStartDrawable = reverseStartDrawable
//        if (startDrawable != null || reverseStartDrawable != null) {
//        }
        refreshCompoundDrawables()
    }

    fun setEndDrawable(endDrawable: Drawable?, reverseEndDrawable: Drawable?) {
        this.endDrawable = endDrawable
        this.reverseEndDrawable = reverseEndDrawable
//        if (endDrawable != null || reverseEndDrawable != null) {
//        }
        refreshCompoundDrawables()
    }

    fun setState(state: State) {
        if (this.state == state) {
            return
        }
        this.state = state
        refreshTextColor()
        refreshCompoundDrawables()
    }

    fun displayTitle(title: CharSequence?, isDisplay: Boolean) {
        if (this.isDisplay != isDisplay) {
            this.isDisplay = isDisplay
            refreshCompoundDrawables()
        }
        text = if (isDisplay) {
            title
        } else {
            ""
        }
    }

    @SuppressLint("ResourceType")
    private fun refreshTextColor() {
        when (state) {
            State.REVERSE -> {
                if (colorResId > 0) {
                    if (colorReverseResId > 0) {
                        setTextColor(ContextCompat.getColor(context, colorReverseResId))
                    } else {
                        setTextColor(ContextCompat.getColor(context, colorResId))
                    }
                }
            }
            State.NORMAL -> {
                if (colorResId > 0) {
                    setTextColor(ContextCompat.getColor(context, colorResId))
                }
            }
        }
    }

    private fun refreshCompoundDrawables() {
        if (isDisplay) {
            when (state) {
                State.REVERSE -> {
                    setCompoundDrawables(reverseStartDrawable, null, reverseEndDrawable, null)
                }
                State.NORMAL -> {
                    setCompoundDrawables(startDrawable, null, endDrawable, null)
                }
            }
        } else {
            setCompoundDrawables(null, null, null, null)
        }
    }
}