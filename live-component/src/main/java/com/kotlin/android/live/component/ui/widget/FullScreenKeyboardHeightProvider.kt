package com.kotlin.android.live.component.ui.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Rect
import android.widget.PopupWindow
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Gravity
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator

/**
 * Created by zhaoninglongfei on 2021/12/20.
 * 当开发使用的是Activity主题（Theme）是非全屏主题（fullscreen）非沉浸式的时候，可以通过DecordView 获取ViewTreeObserve去监听Layout变化
 * 但是，如果当我们使用全屏主题的时候就会发现监听没任何作用了
 *
 * 通过添加一个PopWindow 来解决无法监听软键盘的问题
 */
class FullScreenKeyboardHeightProvider(private val activity: Activity) : PopupWindow(activity), OnGlobalLayoutListener {
    private val mRootView: View
    private var mHeightListener: HeightListener? = null
    private var mHeightMax = 0// 记录popup内容区的最大高度
    private var mKeyboardHeight = 0

    init {
        // 基础配置
        mRootView = View(activity)
        contentView = mRootView

        // 监听全局Layout变化
        mRootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        setBackgroundDrawable(ColorDrawable(0))

        // 设置宽度为0，高度为全屏
        width = 0
        height = ViewGroup.LayoutParams.MATCH_PARENT

        // 设置键盘弹出方式
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        inputMethodMode = INPUT_METHOD_NEEDED
    }

    fun init(): FullScreenKeyboardHeightProvider {
        if (!isShowing) {
            val view = activity.window.decorView
            // 延迟加载popupWindow，如果不加延迟就会报错
            view.post { showAtLocation(view, Gravity.NO_GRAVITY, 0, 0) }
        }
        return this
    }

    fun setHeightListener(listener: HeightListener?): FullScreenKeyboardHeightProvider {
        mHeightListener = listener
        return this
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        mRootView.getWindowVisibleDisplayFrame(rect)
        if (rect.bottom > mHeightMax) {
            mHeightMax = rect.bottom
        }
        // 两者的差值就是键盘的高度
        val currentKeyboardHeight = mHeightMax - rect.bottom
        if (currentKeyboardHeight == mKeyboardHeight) {
            return
        }
        if (mHeightListener != null) {
            val valueAnimator = ValueAnimator.ofInt(mKeyboardHeight, currentKeyboardHeight)
            valueAnimator.addUpdateListener { animation ->
                val animatedValue = animation.animatedValue as Int
            }
            valueAnimator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    mKeyboardHeight = currentKeyboardHeight
                    mHeightListener!!.onKeyboardHeightChanged(mKeyboardHeight)
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
            valueAnimator.duration = 200
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.start()
        }
    }

    interface HeightListener {
        fun onKeyboardHeightChanged(height: Int)
    }
}