package com.kotlin.android.widget.views

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import com.kotlin.android.widget.R

/**
 * 伸缩layout
 */
class ExpandLayout @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle) {
    private var layoutView: View? = null
    private var viewHeight = 0
    var isExpand = false
        private set
    private var animationDuration: Long = 0
    private var lock = false
    private fun initView() {
        layoutView = this
        isExpand = true
        animationDuration = 500
        setViewDimensions()
    }

    /**
     * @param isExpand 初始状态是否折叠
     */
    fun initExpand(isExpand: Boolean) {
        this.isExpand = isExpand
        setViewDimensions()
    }

    /**
     * 设置动画时间
     *
     * @param animationDuration 动画时间
     */
    fun setAnimationDuration(animationDuration: Long) {
        this.animationDuration = animationDuration
    }

    /**
     * 获取subView的总高度
     * View.post()的runnable对象中的方法会在View的measure、layout等事件后触发
     */
    private fun setViewDimensions() {
        layoutView!!.post {
            if (viewHeight <= 0) {
                viewHeight = layoutView!!.measuredHeight
            }
            setViewHeight(layoutView, if (isExpand) viewHeight else 0)
        }
    }

    /**
     * 切换动画实现
     */
    private fun animateToggle(animationDuration: Long) {
        val heightAnimation = if (isExpand) ValueAnimator.ofFloat(
            0f,
            viewHeight.toFloat()
        ) else ValueAnimator.ofFloat(viewHeight.toFloat(), 0f)
        heightAnimation.duration = animationDuration / 2
        heightAnimation.startDelay = animationDuration / 2
        heightAnimation.addUpdateListener { animation ->
            val value = (animation.animatedValue as Float).toInt()
            setViewHeight(layoutView, value)
            if (value == viewHeight || value == 0) {
                lock = false
            }
        }
        heightAnimation.start()
        lock = true
    }

    /**
     * 折叠view
     */
    fun collapse(iv: ImageView) {
        isExpand = false
        animateToggle(animationDuration)
        iv.setImageResource(R.drawable.ic_label_arrow_down)
    }

    /**
     * 展开view
     */
    fun expand(iv: ImageView) {
        isExpand = true
        animateToggle(animationDuration)
        iv.setImageResource(R.drawable.ic_label_arrow_up)
    }

    fun toggleExpand(iv: ImageView) {
        if (lock) {
            return
        }
        if (isExpand) {
            collapse(iv)
        } else {
            expand(iv)
        }
    }

    companion object {
        fun setViewHeight(view: View?, height: Int) {
            val params = view!!.layoutParams
            params.height = height
            view.requestLayout()
        }
    }

    init {
        initView()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return true
    }
}