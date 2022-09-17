package com.kotlin.android.widget.autohintlayout

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/13
 */
class AutoHintHelper(val mView: View, val mAutoHintDrawer: IAutoHintDrawer?) {

    private val mHintBounds = Rect()
    private var mLastHintText = ""
    var mHintText = ""
    private var mHintTextSize = 15f
    private var mHintTextColor: ColorStateList? = null
    private var mTypeFace: Typeface? = null
    private var mGravity = Gravity.CENTER_VERTICAL
    private var mLastDrawX = 0f
    private var mDrawX = 0f
    private var mDrawY = 0f
    private var state: IntArray? = null
    private val mPaint = Paint()

    private var mAnimator: ValueAnimator? = null
    private var mCurrentFraction = 0f

    private var mShowHint = true

    init {
        initAnim()
    }

    private fun initAnim() {
        mAnimator = ValueAnimator()
        mAnimator?.duration = 350
        mAnimator?.setFloatValues(0f, 1f)
        mAnimator?.addUpdateListener { animation ->
            mCurrentFraction = animation.animatedFraction
            mView.invalidate()
        }
    }

    fun setHintText(text: String, anim: Boolean) {
        mLastHintText = mHintText
        mHintText = text
        if (mAnimator?.isRunning == true) {
            mAnimator?.cancel()
        }
        calculateDrawXY()
        if (anim) {
            mCurrentFraction = 0f
            mAnimator?.start()
        } else {
            mCurrentFraction = 1f
            mView.invalidate()
        }
    }

    fun setHintTextSize(mHintTextSize: Float) {
        this.mHintTextSize = mHintTextSize
        mPaint.textSize = mHintTextSize
        calculateDrawXY()
    }

    fun setHintTextColor(mHintTextColor: ColorStateList?) {
        this.mHintTextColor = mHintTextColor
    }

    fun setTypeFace(mTypeFace: Typeface?) {
        this.mTypeFace = mTypeFace
        mPaint.typeface = mTypeFace
        calculateDrawXY()
    }

    fun setState(state: IntArray?) {
        this.state = state
    }

    fun setGravity(mGravity: Int) {
        this.mGravity = mGravity
        calculateDrawXY()
        mView.invalidate()
    }

    fun setHintBounds(left: Int, top: Int, right: Int, bottom: Int) {
        Log.d("Debug", "set bounds:$left $top $right $bottom")
        if (!rectEquals(mHintBounds, left, top, right, bottom)) {
            mHintBounds.set(left, top, right, bottom)
            onBoundsChanged()
        }
    }

    fun showHint(showHint: Boolean) {
        mShowHint = showHint
        mHintText = ""
        mView.invalidate()
    }

    private fun onBoundsChanged() {
        calculateDrawXY()
        mView.invalidate()
    }

    /**
     * 根据gravity和paint的参数计算lastHint和当前hint的drawX以及drawY
     */
    private fun calculateDrawXY() {
        val lastHintLength = mPaint.measureText(mLastHintText, 0, mLastHintText.length)
        val hintLength = mPaint.measureText(mHintText, 0, mHintText.length)
        when (mGravity and GravityCompat.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            Gravity.CENTER_HORIZONTAL -> {
                mLastDrawX = mHintBounds.centerX() - lastHintLength / 2
                mDrawX = mHintBounds.centerX() - hintLength / 2
            }
            Gravity.RIGHT -> {
                mLastDrawX = mHintBounds.right - lastHintLength
                mDrawX = mHintBounds.right - hintLength
            }
            Gravity.LEFT -> {
                mDrawX = mHintBounds.left.toFloat()
                mLastDrawX = mDrawX
            }
            else -> {
                mDrawX = mHintBounds.left.toFloat()
                mLastDrawX = mDrawX
            }
        }
        mDrawY = when (mGravity and Gravity.VERTICAL_GRAVITY_MASK) {
            Gravity.BOTTOM -> mHintBounds.bottom.toFloat()
            Gravity.TOP -> mHintBounds.top - mPaint.ascent()
            Gravity.CENTER_VERTICAL -> {
                val textHeight = mPaint.descent() - mPaint.ascent()
                val textOffset = textHeight / 2 - mPaint.descent()
                mHintBounds.centerY() + textOffset
            }
            else -> {
                val textHeight = mPaint.descent() - mPaint.ascent()
                val textOffset = textHeight / 2 - mPaint.descent()
                mHintBounds.centerY() + textOffset
            }
        }
    }

    fun draw(canvas: Canvas?) {
        if (!mShowHint) {
            // draw empty
            return
        }
        canvas?.let {
            mPaint.color =
                mHintTextColor?.getColorForState(
                    state,
                    mHintTextColor?.defaultColor ?: 0
                ) ?: 0
            mAutoHintDrawer?.draw(
                mHintBounds, mLastDrawX, mDrawX, mDrawY, mCurrentFraction, mLastHintText, mHintText,
                it, mPaint
            )
        }
    }

    private fun rectEquals(r: Rect, left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return !(r.left != left || r.top != top || r.right != right || r.bottom != bottom)
    }
}