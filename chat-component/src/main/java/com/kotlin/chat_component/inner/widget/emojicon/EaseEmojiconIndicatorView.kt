package com.kotlin.chat_component.inner.widget.emojicon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.hyphenate.util.DensityUtil
import com.kotlin.chat_component.R
import com.kotlin.chat_component.inner.widget.EImageView
import java.util.ArrayList

@SuppressLint("NewApi")
class EaseEmojiconIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var selectedBitmap: Bitmap? = null
    private var unselectedBitmap: Bitmap? = null
    private var dotViews: ArrayList<ImageView>? = null
    private var dotHeight = 12

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : this(context, null) {}

    private fun init(context: Context, attrs: AttributeSet?) {
        dotHeight = DensityUtil.dip2px(context, dotHeight.toFloat())
        selectedBitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.ease_dot_emojicon_selected)
        unselectedBitmap =
            BitmapFactory.decodeResource(context.resources, R.drawable.ease_dot_emojicon_unselected)
        gravity = Gravity.CENTER_HORIZONTAL
    }

    fun init(count: Int) {
        dotViews = ArrayList()
        for (i in 0 until count) {
            val rl = RelativeLayout(context)
            val params = LayoutParams(dotHeight, dotHeight)
            val layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
            val imageView: ImageView = EImageView(context)
            if (i == 0) {
                imageView.setImageBitmap(selectedBitmap)
                rl.addView(imageView, layoutParams)
            } else {
                imageView.setImageBitmap(unselectedBitmap)
                rl.addView(imageView, layoutParams)
            }
            this.addView(rl, params)
            dotViews?.add(imageView)
        }
    }

    fun updateIndicator(count: Int) {
        if (dotViews == null) {
            return
        }
        for (i in dotViews!!.indices) {
            if (i >= count) {
                dotViews!![i].visibility = GONE
                (dotViews!![i].parent as View).visibility = GONE
            } else {
                dotViews!![i].visibility = VISIBLE
                (dotViews!![i].parent as View).visibility = VISIBLE
            }
        }
        if (count > dotViews!!.size) {
            val diff = count - dotViews!!.size
            for (i in 0 until diff) {
                val rl = RelativeLayout(context)
                val params = LayoutParams(dotHeight, dotHeight)
                val layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
                val imageView: ImageView = EImageView(context)
                imageView.setImageBitmap(unselectedBitmap)
                rl.addView(imageView, layoutParams)
                rl.visibility = GONE
                imageView.visibility = GONE
                this.addView(rl, params)
                dotViews!!.add(imageView)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (selectedBitmap != null) {
            selectedBitmap!!.recycle()
        }
        if (unselectedBitmap != null) {
            unselectedBitmap!!.recycle()
        }
    }

    fun selectTo(position: Int) {
        for (iv in dotViews!!) {
            iv.setImageBitmap(unselectedBitmap)
        }
        dotViews!![position].setImageBitmap(selectedBitmap)
    }

    fun selectTo(startPosition: Int, targetPostion: Int) {
        val startView = dotViews!![startPosition]
        val targetView = dotViews!![targetPostion]
        startView.setImageBitmap(unselectedBitmap)
        targetView.setImageBitmap(selectedBitmap)
    }

    init {
        init(context, attrs)
    }
}