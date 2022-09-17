package com.kotlin.android.image.component.widget.crop

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.photo.getBitmapWithLocalUri
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.dimension.dpF
import kotlin.math.sqrt

/**
 * 图片裁剪矩阵视图：
 *
 * Created on 2020/12/22.
 *
 * @author o.s
 */
class CropView : View {

    private var mStubMatrix: Matrix = Matrix() // 自动调整动画，所需的存档矩阵。
    private var mMatrix: Matrix = Matrix() // 主视图矩阵
    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }
    private val mPaintFrame by lazy {
        Paint().apply {
            color = Color.TRANSPARENT
            alpha = 100
            isAntiAlias = true
            strokeWidth = 1.dpF
            color = Color.TRANSPARENT
        }
    }

    private var mWidth: Int = 0 // 视图宽
    private var mHeight: Int = 0 // 视图高
    private var mBitmapWidth: Int = 0 // 图片宽
    private var mBitmapHeight: Int = 0 // 图片高
    private val mCenterPoint: PointF by lazy { PointF(0F, 0F) }// 视图中心点
    private val mTouchCenterPoint: PointF by lazy { PointF(0F, 0F) }// 两点触摸对称中心点
    private val mTouchPointTemp: PointF by lazy { PointF(0F, 0F) }// 触摸点临时
    private var mDx: Float = 0F // X位移量
    private var mDy: Float = 0F // Y位移量

    private var mRatio: Float = 1F // 视图宽高比
    private var mBitmapRatio: Float = 1F // 图片宽高比
    private var mScale: Float = 1F // 图片缩放
    private var mScaleMAX: Float = 3F // 图片最大缩放
    private var mScaleMIN: Float = 0.33F // 图片最小缩放
    private var isBitmapWidthMin: Boolean = true // 图片宽度较窄
    private var allowEnlarge = false // 允许放大图片
    private var allowDeflate = false // 允许缩小图片
    private var isTouch = false // 是否触摸中
    private var isTouchScale = false // 是否触摸缩放中

    /**
     * 调整动画
     */
    private var animator: ValueAnimator? = null

    /**
     * 操作类型
     */
    private var opType: OperationType = OperationType.DEFAULT
    private val srcRectF: RectF by lazy { RectF(0F, 0F, 0F, 0F) }
    private val dstRectF: RectF by lazy { RectF(0F, 0F, 0F, 0F) }
    private var cropRectF: RectF = RectF(0F, 0F, 0F, 0F)
        get() {
            field.left = if (dstRectF.left > 0) {
                dstRectF.left
            } else {
                0F
            }
            field.right = if (dstRectF.right < mWidth) {
                dstRectF.right
            } else {
                mWidth.toFloat()
            }
            field.top = if (dstRectF.top > 0) {
                dstRectF.top
            } else {
                0F
            }
            field.bottom = if (dstRectF.bottom < mHeight) {
                dstRectF.bottom
            } else {
                mHeight.toFloat()
            }
            return field
        }

    /**
     * 标线点集
     */
    private val mPointLine = FloatArray(16)

    /**
     * Bitmap 持有对象
     */
    private var mBaseBitmap: Bitmap? = null

    /**
     * 两个触摸点之间的距离（临时）
     */
    private var twoPointsDistanceTemp = 0F

    /**
     * 1，设置原始图片；
     * 2，获取裁剪后的图片
     */
    var bitmap: Bitmap? = null
        set(value) {
            field = value
            post { // 等待视图渲染完成后填充照片数据
                syncBaseBitmap(value)
                fillBitmap()
            }
        }
        get() {
            return cropBitmap()
        }

    var data: PhotoInfo? = null
        set(value) {
            field = value
            post { // 等待视图渲染完成后填充照片数据
                fillData()
            }
        }

    private fun fillData() {
        context.getBitmapWithLocalUri(data)?.apply {
            syncBaseBitmap(this)
            fillBitmap()
        }
    }

    private fun syncBaseBitmap(bitmap: Bitmap?) {
        if (mBaseBitmap?.isRecycled == false) {
            mBaseBitmap?.recycle()
        }
        mBaseBitmap = bitmap
    }
    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mRatio = mWidth / mHeight.toFloat()
        mCenterPoint.x = mWidth / 2F
        mCenterPoint.y = mHeight / 2F

        setupLine(0F, 0F, mWidth.toFloat(), mHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        drawBitmap(canvas)
        drawLine(canvas)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        mBaseBitmap?.let {
            event?.apply {
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        cancelAnimator()
                    }
                    MotionEvent.ACTION_CANCEL -> {
                        resetTouchParams()
                        delayedHideLine()
                        smoothScrollBetter()
                    }
                    MotionEvent.ACTION_UP -> {
                        resetTouchParams()
                        delayedHideLine()
                        smoothScrollBetter()
                    }
                }

                if (this.pointerCount == 1) {
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {
                            isTouch = true
                            isTouchScale = false
                            mTouchPointTemp.x = x
                            mTouchPointTemp.y = y
                            showLine()
                        }
                        MotionEvent.ACTION_MOVE -> {

                            translate(event)

                        }
                    }
                } else if (this.pointerCount == 2) {
                    when (action) {
                        MotionEvent.ACTION_DOWN -> {

                        }
                        MotionEvent.ACTION_MOVE -> {
                            isTouch = false
                            isTouchScale = true

                            scale(event)

                        }
                    }
                }
            }
        }
        return true
    }

    /**
     * 绘制图片到矩阵上
     */
    private fun drawBitmap(canvas: Canvas?) {
        mBaseBitmap?.apply {
            if (!isRecycled) {
                canvas?.drawBitmap(this, mMatrix, mPaint)
            }
        }
    }

    /**
     * 绘制标线:
     *       |---( 0, 1)---( 2, 3)---|
     *       |      |         |      |
     * (14,15)-----------------------( 4, 5)
     *       |      |         |      |
     * (12,13)-----------------------( 6, 7)
     *       |      |         |      |
     *       |---(10,11)---( 8, 9)---|
     */
    private fun drawLine(canvas: Canvas?) {
        canvas?.apply {
            drawLine(mPointLine[0], mPointLine[1], mPointLine[10], mPointLine[11], mPaintFrame)
            drawLine(mPointLine[2], mPointLine[3], mPointLine[8], mPointLine[9], mPaintFrame)
            drawLine(mPointLine[14], mPointLine[15], mPointLine[4], mPointLine[5], mPaintFrame)
            drawLine(mPointLine[12], mPointLine[13], mPointLine[6], mPointLine[7], mPaintFrame)
        }
    }

    /**
     * 设置标线
     *       |---( 0, 1)---( 2, 3)---|          y1
     *       |      |         |      |
     * (14,15)-----------------------( 4, 5)    y2
     *       |      |         |      |
     * (12,13)-----------------------( 6, 7)    y3
     *       |      |         |      |
     *       |---(10,11)---( 8, 9)---|          y4
     *
     *       x1     x2       x3     x4
     */
    private fun setupLine(left: Float, top: Float, right: Float, bottom: Float) {
        val unitX = (right - left) / 3F
        val unitY = (bottom - top) / 3F

        val x1 = left
        val x2 = x1 + unitX
        val x3 = x2 + unitX
        val x4 = right

        val y1 = top
        val y2 = y1 + unitY
        val y3 = y2 + unitY
        val y4 = bottom

        mPointLine[0] = x2
        mPointLine[1] = y1

        mPointLine[2] = x3
        mPointLine[3] = y1

        mPointLine[4] = x4
        mPointLine[5] = y2

        mPointLine[6] = x4
        mPointLine[7] = y3

        mPointLine[8] = x3
        mPointLine[9] = y4

        mPointLine[10] = x2
        mPointLine[11] = y4

        mPointLine[12] = x1
        mPointLine[13] = y3

        mPointLine[14] = x1
        mPointLine[15] = y2
    }

    /**
     * 填充 Bitmap 视图
     */
    private fun fillBitmap() {
        mBaseBitmap?.apply {
            mBitmapWidth = width
            mBitmapHeight = height
            srcRectF.right = mBitmapWidth.toFloat()
            srcRectF.bottom = mBitmapHeight.toFloat()
            mBitmapRatio = width / height.toFloat()
            isBitmapWidthMin = mRatio > mBitmapRatio
            mScale = if (isBitmapWidthMin) {
                mWidth / width.toFloat()
            } else {
                mHeight / height.toFloat()
            }
            when {
                mScale > mScaleMAX -> {
                    allowEnlarge = false
                    allowDeflate = true
                }
                mScale < mScaleMIN -> {
                    allowEnlarge = true
                    allowDeflate = false
                }
                else -> {
                    allowEnlarge = true
                    allowDeflate = true
                }
            }
            val left = mCenterPoint.x - width / 2F
            val top = mCenterPoint.y - height / 2F
            mDx = left
            mDy = top
            syncTouchCenterPoint(mCenterPoint, mCenterPoint)

            matrix.reset()
            opType = OperationType.TRANSLATE_AND_SCALE
            syncMatrix()
        }
    }

    /**
     * 裁剪 Bitmap
     */
    private fun cropBitmap(): Bitmap? {
        return mBaseBitmap?.run {
            cropRect()?.let {
                Bitmap.createBitmap(this, it.left, it.top, it.width(), it.height(), mMatrix, true)
            }
        }
    }

    private fun syncMatrix() {
        when (opType) {
            OperationType.TRANSLATE -> {
                mMatrix.postTranslate(mDx, mDy)
            }
            OperationType.SCALE -> {
                mMatrix.postScale(mScale, mScale, mTouchCenterPoint.x, mTouchCenterPoint.y)
            }
            OperationType.TRANSLATE_AND_SCALE -> {
                mMatrix.postTranslate(mDx, mDy)
                mMatrix.postScale(mScale, mScale, mTouchCenterPoint.x, mTouchCenterPoint.y)
            }
            else -> {

            }
        }

        mMatrix.mapRect(dstRectF, srcRectF)
        syncSetupLine()
        invalidate()
    }

    private fun syncSetMatrix() {
        when (opType) {
            OperationType.TRANSLATE -> {
                mMatrix.postTranslate(mDx, mDy)
            }
            OperationType.SCALE -> {
                mMatrix.setScale(mScale, mScale, mTouchCenterPoint.x, mTouchCenterPoint.y)
            }
            OperationType.TRANSLATE_AND_SCALE -> {
                mMatrix.postTranslate(mDx, mDy)
                mMatrix.setScale(mScale, mScale, mTouchCenterPoint.x, mTouchCenterPoint.y)
            }
            else -> {
                return
            }
        }

        mMatrix.mapRect(dstRectF, srcRectF)
        syncSetupLine()
        invalidate()
    }

    /**
     * 同步标线
     */
    private fun syncSetupLine() {
        val left = if (dstRectF.left > 0) {
            dstRectF.left
        } else {
            0F
        }
        val top = if (dstRectF.top > 0) {
            dstRectF.top
        } else {
            0F
        }
        val right = if (dstRectF.right < mWidth) {
            dstRectF.right
        } else {
            mWidth.toFloat()
        }
        val bottom = if (dstRectF.bottom < mHeight) {
            dstRectF.bottom
        } else {
            mHeight.toFloat()
        }
        setupLine(left, top, right, bottom)
    }

    /**
     * 立即显示标线
     */
    private fun showLine() {
        mPaintFrame.color = getColor(R.color.color_efefef)
        invalidate()
    }

    /**
     * 延迟隐藏标线
     */
    private fun delayedHideLine() {
        postDelayed({
            if (!isTouch) {
                mPaintFrame.color = Color.TRANSPARENT
                invalidate()
            }
        }, 500)
    }

    /**
     * 移动
     */
    private fun translate(event: MotionEvent?) {
        event?.apply {
            if (mTouchPointTemp.x == 0F || mTouchPointTemp.y == 0F) {
                mTouchPointTemp.x = x
                mTouchPointTemp.y = y
                return
            }
            val dx = x - mTouchPointTemp.x
            val dy = y - mTouchPointTemp.y
            mTouchPointTemp.x = x
            mTouchPointTemp.y = y

            if (isTouch) {
                mDx = dx
                mDy = dy

                val left: Float = dstRectF.left
                val top: Float = dstRectF.top
                val right: Float = dstRectF.right
                val bottom: Float = dstRectF.bottom
                if (left > 0 && dx > 0) {
                    mDx = dx * ((mWidth - left) / mWidth)
                }

                if (right < mWidth && dx < 0) {
                    mDx = dx * (right / mWidth)
                }

                if (top > 0 && dy > 0) {
                    mDy = dy * ((mHeight - top) / mHeight)
                }

                if (bottom < mHeight && dy < 0) {
                    mDy = dy * (bottom / mHeight)
                }

                opType = OperationType.TRANSLATE
                syncMatrix()
            }
        }
    }

    /**
     * 缩放
     */
    private fun scale(event: MotionEvent?) {
        event?.apply {
            val src = PointF(getX(0), getY(0))
            val dst = PointF(getX(1), getY(1))
            val distance = getDistanceOfTwoPoints(src, dst)
            if (twoPointsDistanceTemp == 0F) {
                twoPointsDistanceTemp = distance
                return
            }
            val scale = distance / twoPointsDistanceTemp
            twoPointsDistanceTemp = distance
            mScale = when {
                scale > 1.2F -> {
                    1.2F
                }
                scale < 0.8F -> {
                    0.8F
                }
                else -> {
                    scale
                }
            }

            syncTouchCenterPoint(src, dst)
            opType = OperationType.SCALE
            syncMatrix()
        }
    }

    /**
     * 平滑的滚动到更好的位置
     */
    private fun smoothScrollBetter() {
        syncTouchCenterPoint(mCenterPoint, mCenterPoint)

        val delta = FloatArray(2)
        autoTranslateBetter(delta)

        val ds = autoScaleBetter() - 1F

        if (ds != 0F || delta[0] != 0F || delta[1] != 0F) {
            startAnimator(
                    dx = delta[0],
                    dy = delta[1],
                    ds = ds
            )
        }
    }

    /**
     * 更好的缩放比例
     */
    private fun autoScaleBetter(): Float {
        var scale = 1F
        val sw = mWidth / dstRectF.width()
        val sh = mHeight / dstRectF.height()
        if (isBitmapWidthMin) {
            if (sw > 1) {
                if (sh > 1) {
                    scale = sh
                }
            } else if (sw < 0.4F) {
                scale = sw / 0.4F
            }
        } else {
            if (sh > 1) {
                if (sw > 1) {
                    scale = sw
                }
            } else if (sh < 0.4F) {
                scale = sh / 0.4F
            }
        }

        return scale
    }

    /**
     * 更好的位移
     * [delta] 位移变化量缓存
     */
    private fun autoTranslateBetter(delta: FloatArray) {
        val left: Float = dstRectF.left
        val top: Float = dstRectF.top
        val right: Float = dstRectF.right
        val bottom: Float = dstRectF.bottom

        val w = dstRectF.width()
        val h = dstRectF.height()

        var srcX = 0f
        var dstX = 0f
        var srcY = 0f
        var dstY = 0f

        // translate left right
        if (w < mWidth) {
            srcX = left
            dstX = (mWidth - w) / 2
        } else {
            if (left > 0) {
                srcX = left
                dstX = 0f
            } else if (right < mWidth) {
                srcX = right
                dstX = mWidth.toFloat()
            } else {
                if (isTouchScale) {
                    srcX = left
                    dstX = left - (left + right - mWidth) / 2
                }
            }
        }

        // translate top bottom
        if (h < mHeight) {
            srcY = top
            dstY = (mHeight - h) / 2
        } else {
            if (top > 0) {
                srcY = top
                dstY = 0f
            } else if (bottom < mHeight) {
                srcY = bottom
                dstY = mHeight.toFloat()
            } else {
                if (isTouchScale) {
                    srcY = top
                    dstY = top - (top + bottom - mHeight) / 2
                }
            }
        }

        val dx = dstX - srcX
        val dy = dstY - srcY
        delta[0] = dx
        delta[1] = dy
    }

    /**
     * 同步触摸对称中心点
     */
    private fun syncTouchCenterPoint(src: PointF, dst: PointF) {
        mTouchCenterPoint.x = src.x + (dst.x - src.x) / 2F
        mTouchCenterPoint.y = src.y + (dst.y - src.y) / 2F
    }

    /**
     * 同步缩放中心点
     */
    private fun syncTouchCenterPoint() {
        mTouchCenterPoint.x = (dstRectF.left + dstRectF.right) / 2
        mTouchCenterPoint.y = (dstRectF.top + dstRectF.bottom) / 2
    }

    /**
     * 重置触摸相关参数
     */
    private fun resetTouchParams() {
        twoPointsDistanceTemp = 0F
        isTouch = false
//        isTouchScale = false
        mTouchPointTemp.x = 0F
        mTouchPointTemp.y = 0F
    }

    /**
     * 获取直角三角形斜边长度（两点之间的距离）
     */
    private fun getDistanceOfTwoPoints(src: PointF, dst: PointF): Float {
        val a = dst.x - src.x.toDouble()
        val b = dst.y - src.y.toDouble()
        return sqrt(a * a + b * b).toFloat()
    }

    /**
     * 开始动画（当手指离开屏幕时，根据实际情况决定是否要开启调整动画）
     */
    private fun startAnimator(
            dx: Float = 0F,
            dy: Float = 0F,
            ds: Float = 0F,
    ) {
        // 动画开始前，矩阵存档
        mStubMatrix = mMatrix
        animator = ValueAnimator.ofFloat(0F, 1F).apply {
            duration = 400
            interpolator = DecelerateInterpolator()
            addUpdateListener {
                (it?.animatedValue as? Float)?.apply {
                    animatorUpdate(this, dx, dy, ds)
                }
            }
            start()
        }
    }

    /**
     * 取消动画（当触摸时）
     */
    private fun cancelAnimator() {
        animator?.apply {
            cancel()
        }
    }

    /**
     * 动画更新矩阵
     * [factor] 动画变化因子（0F - 1F）；
     * [dx] x轴位移, [dy] y轴位移, [ds] 缩放变化量
     */
    private fun animatorUpdate(
            factor: Float,
            dx: Float,
            dy: Float,
            ds: Float
    ) {
        mDx = dx * factor
        mDy = dy * factor
        mScale = 1F + ds * factor

        val isScale = mScale != 1F
        val isTranslate = mDx != 0F || mDy != 0F
        opType = when {
            isScale && isTranslate -> {
                OperationType.TRANSLATE_AND_SCALE
            }
            isScale -> {
                OperationType.SCALE
            }
            isTranslate -> {
                OperationType.TRANSLATE
            }
            else -> {
                OperationType.DEFAULT
            }
        }
        "opType=$opType, mScale=$mScale, ds=$ds, float=$factor".e()
        // 重要：高频次动画缩放，每次都需要根据矩阵存档来进行（缩放递进）。
        mMatrix = Matrix(mStubMatrix)
        syncMatrix()
    }

    private fun cropRect(): Rect? {
        return mBaseBitmap?.run {
            val cropRectF = RectF(0F, 0F, 0F, 0F).apply {
                left = if (dstRectF.left > 0) {
                    dstRectF.left
                } else {
                    0F
                }
                right = if (dstRectF.right < mWidth) {
                    dstRectF.right
                } else {
                    mWidth.toFloat()
                }
                top = if (dstRectF.top > 0) {
                    dstRectF.top
                } else {
                    0F
                }
                bottom = if (dstRectF.bottom < mHeight) {
                    dstRectF.bottom
                } else {
                    mHeight.toFloat()
                }
            }

            val ratio = width / dstRectF.width()
            Rect(0, 0, 0, 0).apply {
                left = ((cropRectF.left - dstRectF.left) * ratio).toInt()
                right = ((cropRectF.right - dstRectF.left) * ratio).toInt()
                top = ((cropRectF.top - dstRectF.top) * ratio).toInt()
                bottom = ((cropRectF.bottom - dstRectF.top) * ratio).toInt()

                if (width() > width) {
                    right = left + width
                }
                if (height() > height) {
                    bottom = top + height
                }
            }
        }
    }

    fun recycle() {
        mBaseBitmap?.recycle()
    }
}

/**
 * 操作类型
 */
enum class OperationType {
    /**
     * 默认
     */
    DEFAULT,

    /**
     * 移动
     */
    TRANSLATE,

    /**
     * 缩放
     */
    SCALE,

    /**
     * 移动并缩放
     */
    TRANSLATE_AND_SCALE
}