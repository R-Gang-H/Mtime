package com.kotlin.android.audio.floatview.component.aduiofloat

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout

open class FloatingMagnetView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(
    context, attrs, defStyleAttr
) {
    private var mOriginalRawX = 0f
    private var mOriginalRawY = 0f
    private var mOriginalX = 0f
    private var mOriginalY = 0f
    private var mMagnetViewListener: MagnetViewListener? = null
    private var mLastTouchDownTime: Long = 0
    protected var mMoveAnimator: MoveAnimator? = null
    protected var mScreenWidth = 0
    private var mScreenHeight = 0
    private val mStatusBarHeight = 0
    private var isNearestLeft = true
    private var mPortraitY = 0f
    fun setMagnetViewListener(magnetViewListener: MagnetViewListener?) {
        mMagnetViewListener = magnetViewListener
    }

    private fun init() {
        mMoveAnimator = MoveAnimator()
        isClickable = true
        //        updateSize();
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return false
        //        if (event == null) {
//            return false;
//        }
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                changeOriginalTouchParams(event);
//                updateSize();
//                mMoveAnimator.stop();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                updateViewPosition(event);
//                break;
//            case MotionEvent.ACTION_UP:
//                clearPortraitY();
//                moveToEdge();
//                if (isOnClickEvent()) {
//                    dealClickEvent();
//                }
//                break;
//        }
//        return true;
    }

    protected fun dealClickEvent() {
        mMagnetViewListener?.onClick(this)
    }

    protected val isOnClickEvent: Boolean
        protected get() = System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD

    private fun updateViewPosition(event: MotionEvent) {
        x = mOriginalX + event.rawX - mOriginalRawX
        // 限制不可超出屏幕高度
        var desY = mOriginalY + event.rawY - mOriginalRawY
        if (desY < mStatusBarHeight) {
            desY = mStatusBarHeight.toFloat()
        }
        if (desY > mScreenHeight - height) {
            desY = (mScreenHeight - height).toFloat()
        }
        y = desY
    }

    private fun changeOriginalTouchParams(event: MotionEvent) {
        mOriginalX = x
        mOriginalY = y
        mOriginalRawX = event.rawX
        mOriginalRawY = event.rawY
        mLastTouchDownTime = System.currentTimeMillis()
    }

    protected fun updateSize() {
        val viewGroup = parent as? ViewGroup
        if (viewGroup != null) {
            mScreenWidth = viewGroup.width - width
            mScreenHeight = viewGroup.height
        }
        //        mScreenWidth = (SystemUtils.getScreenWidth(getContext()) - this.getWidth());
//        mScreenHeight = SystemUtils.getScreenHeight(getContext());
    }

    @JvmOverloads
    fun moveToEdge(isLeft: Boolean = isNearestLeft(), isLandscape: Boolean = false) {
        val moveDistance =
             mScreenWidth - MARGIN_EDGE.toFloat()
        var y = y
        if (!isLandscape && mPortraitY != 0f) {
            y = mPortraitY
            clearPortraitY()
        }
        mMoveAnimator?.start(
            moveDistance,
            Math.min(Math.max(0f, y), (mScreenHeight - height).toFloat())
        )
    }

    private fun clearPortraitY() {
        mPortraitY = 0f
    }

    protected fun isNearestLeft(): Boolean {
        val middle = mScreenWidth / 2
        isNearestLeft = x < middle
        return isNearestLeft
    }

    fun onRemove() {
        mMagnetViewListener?.onRemove(this)
    }

    protected inner class MoveAnimator : Runnable {
        private val handler = Handler(Looper.getMainLooper())
        private var destinationX = 0f
        private var destinationY = 0f
        private var startingTime: Long = 0
        fun start(x: Float, y: Float) {
            destinationX = x
            destinationY = y
            startingTime = System.currentTimeMillis()
            handler.post(this)
        }

        override fun run() {
            if (rootView == null || rootView.parent == null) {
                return
            }
            val progress = Math.min(1f, (System.currentTimeMillis() - startingTime) / 400f)
            val deltaX = (destinationX - x) * progress
            val deltaY = (destinationY - y) * progress
            move(deltaX, deltaY)
            if (progress < 1) {
                handler.post(this)
            }
        }

        private fun stop() {
            handler.removeCallbacks(this)
        }
    }

    private fun move(deltaX: Float, deltaY: Float) {
        x = x + deltaX
        y = y + deltaY
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (parent != null) {
            val isLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
            markPortraitY(isLandscape)
            (parent as? ViewGroup)?.post {
                updateSize()
                moveToEdge(isNearestLeft, isLandscape)
            }
        }
    }

    private fun markPortraitY(isLandscape: Boolean) {
        if (isLandscape) {
            mPortraitY = y
        }
    }

    companion object {
        const val MARGIN_EDGE = 13
        private const val TOUCH_TIME_THRESHOLD = 150
    }

    init {
        init()
    }
}