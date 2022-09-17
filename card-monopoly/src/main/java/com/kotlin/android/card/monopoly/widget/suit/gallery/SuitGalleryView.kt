package com.kotlin.android.card.monopoly.widget.suit.gallery

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.*
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.dimension.dp
import kotlin.math.abs
import kotlin.math.min

/**
 * 套装画廊视图：
 *
 * Created on 2020/9/8.
 *
 * @author o.s
 */
class SuitGalleryView : RecyclerView {

    companion object {
        var itemWidth = 57.5F.dp
        var itemHeight = 89.dp
    }

    private val mIconRatio = 89 / 57.5F
    private var mWidth = 204.dp
    private var mHeight = 160.dp
    private var mItemWidth = 68.dp
    private var mHalfItemWidth = mItemWidth shr 1
    private var mIconWidth = 57.5F.dp
    private var mIconHeight = 89.dp
    private var mHalfIconWidth = mIconWidth shr 1
    private var mHalfIconHeight = mIconHeight shr 1
    private val mItemPaddingTop = 39.dp
    private val mItemPaddingBottom = 32.dp
    private val mItemPaddingLeft = 5.dp
    private val mItemPaddingRight = 5.dp
    private val mScaleX = -0.03F // -0.02F
    private val mScaleY = -0.02F // -0.02F
    private val mScaleZ = 1.4F // 1.47F
    private var mCenterX = 0 // 视图X中心

    private var flag = false
    private var currentPosition: Int = 0
    private var prePosition: Int = 0
    private var leftBorderPosition: Int = 0
    private var rightBorderPosition: Int = 0

    @DrawableRes
    var defDrawableRes: Int = R.drawable.ic_card_center_placeholder

    /**
     * 外部设置的（滚动到）中心位置
     */
    var position: Int = 2
        set(value) {
            field = value + 2
            if (field < 2) {
                field = 2
            }
            scrollToCenter(field)
        }

    private val mCamera by lazy { Camera() }
    private val mMatrix by lazy { Matrix() }
    private val mPaint by lazy { Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG) }
    private val mLayoutManager by lazy { LinearLayoutManager(context, HORIZONTAL, false) }
    private val mDecoration by lazy {
        SuitGalleryItemDecoration(
                mItemPaddingLeft,
                mItemPaddingTop,
                mItemPaddingRight,
                mItemPaddingBottom
        )
    }
    private val mAdapter by lazy {
        SuitGalleryAdapter()
    }

//    private var cacheCompletedBitmap = HashMap<Int, Bitmap>()
//    private var cacheBitmap = HashMap<Int, Bitmap>()
//    private var changeMap = HashMap<Int, Int>()
//    private var completable: Completable? = null

    var itemListener: ItemListener? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initView()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fitsSystemWindows = (parent as ViewGroup).fitsSystemWindows
        children.forEach {
            it.fitsSystemWindows = fitsSystemWindows
        }
    }

    private fun initView() {
        layoutManager = mLayoutManager
        addItemDecoration(mDecoration)
        setupItemTouchListener()
        setupScrollListener()
        isChildrenDrawingOrderEnabled = true
        post {
            adapter = mAdapter
        }

//        postDelayed({
//            position = 1
//        }, 300)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        mWidth = measuredWidth
        mHeight = measuredHeight
        mCenterX = measuredWidth shr 1
        mItemWidth = mWidth / 3
        mHalfItemWidth = mItemWidth shr 1
        mIconWidth = mItemWidth - (mItemPaddingLeft + mItemPaddingRight)
        mIconHeight = (mIconWidth * mIconRatio).toInt()
        mHalfIconWidth = mIconWidth shr 1
        mHalfIconHeight = mIconHeight shr 1

        itemWidth = mIconWidth
        itemHeight = mIconHeight
    }

    override fun drawChild(canvas: Canvas?, child: View?, drawingTime: Long): Boolean {
        (child as? GalleryItemLayout)?.apply {
            alpha = 0.2F
            val childCenterX = (left + right) shr 1
            val dx = childCenterX - mCenterX
            bitmap?.apply {
                prepareMatrix(
                        dx = dx,
                        left = left,
                        top = top
                )
                if (!isRecycled) {
                    canvas?.drawBitmap(this, mMatrix, mPaint)
                }
            }
        }
        return false
    }

    private fun prepareMatrix(
            dx: Int,
            left: Int,
            top: Int
    ) {
        //clip the distance
        val absDx = min(mCenterX, abs(dx))
        mCamera.save()
        if (absDx < mIconWidth) {
            val offset = absDx - mIconWidth
            val x = offset * mScaleX
            val y = offset * mScaleY
            val z = offset * mScaleZ
            mCamera.translate(x, y, z)
            val ratio = abs(offset) / mIconWidth.toFloat() // * 0.5
            mPaint.alpha = (127 * (1 + ratio)).toInt()
//            "ratio = $ratio, mIconWidth = $mIconWidth, mIconHeight = $mIconHeight, offset = $offset, alpha = ${mPaint.alpha}".e()
        } else {
            mPaint.alpha = 127
        }
        translate(mCamera, mMatrix, left, top, mHalfIconWidth, mHalfIconHeight)
    }

    private fun translate(
            camera: Camera,
            matrix: Matrix,
            left: Int,
            top: Int,
            childCenterX: Int,
            childCenterY: Int
    ) {
        camera.getMatrix(matrix)
        camera.restore()
        matrix.preTranslate(-childCenterX.toFloat(), -childCenterY.toFloat())
        matrix.postTranslate(childCenterX.toFloat(), childCenterY.toFloat())
        matrix.postTranslate(left.toFloat(), top.toFloat())
    }

//    /**
//     * 根据Tag渲染View
//     */
//    private fun handleTag(view: View, position: Int, function: (bitmap: Bitmap) -> Unit) {
//        val data = (view as? ViewGroup)?.children?.firstOrNull()?.tag as? Data
////        val data = view.tag as? Data
//        if (data?.needRefresh == true) {
//            data.needRefresh = false
//            data.drawable?.toBitmap(width = mIconWidth, height = mIconHeight)?.apply {
//                cacheCompletedBitmap[position] = this
//            }
//        } else {
//            if (data?.init == true) {
//                data.init = false
//                cacheDefaultBitmap(function)
//            } else {
//                cacheCompletedBitmap[position]?.apply {
//                    function.invoke(this)
//                } ?: cacheDefaultBitmap(function)
//            }
//        }
//    }
//
//    private fun cacheDefaultBitmap(function: (bitmap: Bitmap) -> Unit) {
//        var cb = cacheBitmap[position]
//        if (cb == null) {
//            cb = getDrawable(defDrawableRes)?.toBitmap(width = mIconWidth, height = mIconHeight)?.apply {
//                cacheBitmap[position] = this
//            }
//        }
//
//        if (cb != null) {
//            function.invoke(cb)
//        }
//    }
//
//    /**
//     * 缓存子视图Bitmap
//     */
//    private fun cacheBitmapByView(view: View, position: Int, complete: (bitmap: Bitmap) -> Unit) {
//        changeBitmap(view, position) {
//            var bitmap = cacheCompletedBitmap[position]
//            if (bitmap == null || bitmap.isRecycled) {
//                if (completable?.itemCompleted(position) == true) {
//                    getBitmapFromView(view) {
//                        cacheCompletedBitmap[position] = it
//                        complete.invoke(it)
//                    }
//                } else {
//                    bitmap = cacheBitmap[position]
//                    if (bitmap == null || bitmap.isRecycled) {
//                        getBitmapFromView(view) {
//                            cacheBitmap[position] = it
//                            complete.invoke(it)
//                        }
//                    }
//                }
//            } else {
//                complete.invoke(bitmap)
//            }
//        }
//    }
//
//    private fun changeBitmap(child: View, position: Int, after: () -> Unit) {
//        val height = child.height
//        val value: Int? = changeMap[position]
//        if (value != height) {
//            getBitmapFromView(child) {
//                if (completable?.itemCompleted(position) == true) {
//                    cacheCompletedBitmap[position] = it
//                } else {
//                    cacheBitmap[position] = it
//                }
//                after.invoke()
//            }
//            changeMap[position] = height
//        } else {
//            after.invoke()
//        }
//    }
//
//    /**
//     * 获取子视图的截图Bitmap
//     */
//    private fun getChildDrawingCache(child: View): Bitmap? {
//        try {
//            var bitmap = child.drawingCache
//            if (bitmap == null) {
//                child.isDrawingCacheEnabled = true
//                child.buildDrawingCache()
//                bitmap = child.drawingCache
//            }
//            return bitmap
//        } catch (e: Exception) {
////            e.printStackTrace()
//            return try {
//                child.isDrawingCacheEnabled = true
//                child.buildDrawingCache()
//                child.drawingCache
//            } catch (e: Exception) {
//                e.printStackTrace()
//                null
//            }
//        }
//    }
//
//    private fun getBitmapFromView(view: View, complete: (bitmap: Bitmap) -> Unit) {
//        var bitmap = view.drawingCache
//        if (bitmap == null) {
//            view.isDrawingCacheEnabled = true
//            view.buildDrawingCache()
//            bitmap = view.drawingCache
//        }
//        complete.invoke(bitmap)
//
////        var b: Bitmap? = null
////        getActivity()?.run {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                val location = IntArray(2)
////                view.getLocationInWindow(location)
////                b = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888, true)
////                val rect = Rect(location[0], location[1], location[0] + view.width, location[1] + view.height)
////                PixelCopy.request(window, rect, b!!, {
////                    if (it == PixelCopy.SUCCESS) {
////                        b?.apply {
////                            complete.invoke(this)
////                        }
////
////                    }
////                }, Handler(Looper.getMainLooper()))
////            } else {
////                view.isDrawingCacheEnabled = true
////                view.buildDrawingCache()
////                view.measure(MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY),
////                        MeasureSpec.makeMeasureSpec(view.height, MeasureSpec.EXACTLY))
////                view.layout(view.x.toInt(), view.y.toInt(),
////                        view.x.toInt() + view.measuredWidth, view.y.toInt() + view.measuredWidth)
////                b = Bitmap.createBitmap(view.drawingCache,
////                        0, 0, view.measuredWidth, view.measuredHeight)
////                view.isDrawingCacheEnabled = false
////                view.destroyDrawingCache()
////
////                b?.apply {
////                    complete.invoke(this)
////                }
////            }
////        }
//    }

    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        val centerPosition = getCenterBetterPosition(childCount, i)
        currentPosition = mLayoutManager.findFirstVisibleItemPosition() + centerPosition

        if (!flag) {
            flag = true
            scrollToPosition(position)
        }
        val index = when {
            i > centerPosition -> {
                childCount - 1 + centerPosition - i
            }
            i == centerPosition -> {
                childCount - 1
            }
            else -> {
                i
            }
        }
//        "getChildDrawingOrder [prePosition = $prePosition, currentPosition = $currentPosition, position = $i, index = $index, centerPosition = $centerPosition, childCount = $childCount]".i()
        return index
    }

    fun getCurrentData(): Suit? {
        return mAdapter.getDataAt(currentPosition)
    }

    /**
     * 设置数据
     */
    fun setData(data: List<Suit>) {
        mAdapter.setData(data)

        // 更新数据时需要初始化边界属性
        initBorder()
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        recycle()
//        val size = adapter?.itemCount ?: 0
//        cacheCompletedBitmap = HashMap(size)
//        cacheBitmap = HashMap(size)
//        changeMap = HashMap(size)

//        completable = mAdapter

        initBorder()
    }

    private fun initBorder() {
        leftBorderPosition = 2
        rightBorderPosition = (adapter?.itemCount ?: 3) - 3
    }

    /**
     * 设置 item 触摸监听
     */
    private fun setupItemTouchListener() {
        addOnItemTouchListener(object : OnItemTouchListener {
            private val mGestureDetector by lazy {
                GestureDetector(
                        context,
                        object : GestureDetector.SimpleOnGestureListener() {

                            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                                return true
                            }
                        })
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                rv.findChildViewUnder(e.x, e.y)?.apply {
                    if (mGestureDetector.onTouchEvent(e)) {
                        val position = getChildAdapterPosition(this)
//                        "onInterceptTouchEvent [prePosition = $prePosition, currentPosition = $currentPosition, position = $position]".w()
                        if (currentPosition == position) {
                            itemListener?.onHighlightItemClick(this, position)
                        } else {
                            itemListener?.onItemClick(this, position)
                            this@SuitGalleryView.position = position - 2
                        }
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }

        })
    }

    /**
     * 设置滚动监听
     */
    private fun setupScrollListener() {
        addOnScrollListener(object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
//                "onScrollStateChanged [prePosition = $prePosition, currentPosition = $currentPosition, leftBorderPosition = $leftBorderPosition, rightBorderPosition = $rightBorderPosition]".d()
                if (newState == SCROLL_STATE_IDLE) {
                    if (prePosition != currentPosition) {
                        itemListener?.onItemChanged(currentPosition)
                        prePosition = currentPosition
                    }
                    if (currentPosition > rightBorderPosition) {
                        scrollToCenter(rightBorderPosition)
//                        "onScrollStateChanged [prePosition = $prePosition, currentPosition = $currentPosition, rightBorderPosition = $rightBorderPosition]".w()
                        return
                    }
                    if (currentPosition <= leftBorderPosition) {
                        scrollToCenter(leftBorderPosition)
//                        "onScrollStateChanged [prePosition = $prePosition, currentPosition = $currentPosition, leftBorderPosition = $leftBorderPosition]".w()
                        return
                    }
                    mLayoutManager.apply {
                        val firstPosition = findFirstVisibleItemPosition()
                        getChildAt(currentPosition - firstPosition)?.apply {
                            val offset = ((left + right) shr 1) - mCenterX
                            syncCurrentPosition(currentPosition, offset)
                        }
                    }
                }
            }
        })
    }

    /**
     * 同步 [currentPosition]
     */
    private fun syncCurrentPosition(position: Int, dx: Int) {
//        "syncCurrentPosition [currentPosition = $currentPosition, position = $position, dx = $dx]".e()
        if (abs(dx) <= mHalfItemWidth) {
            if (dx != 0) {
                smoothScrollBy(dx, 0)
            }
        } else if (abs(dx) > mHalfItemWidth) {
            currentPosition = position - 1
            mLayoutManager.apply {
                val firstPosition = findFirstVisibleItemPosition()
                getChildAt(currentPosition - firstPosition)?.apply {
                    val offset = ((left + right) shr 1) - mCenterX
//                    "syncCurrentPosition [firstPosition, offset] => [$firstPosition, $offset]".w()
                    if (offset != 0) {
                        smoothScrollBy(offset, 0)
                    }
                }
            }
        }
    }

    private fun scrollToCenter(position: Int) {
        if (position in leftBorderPosition..rightBorderPosition) {
            val firstPosition = mLayoutManager.findFirstVisibleItemPosition()
            val lastPosition = mLayoutManager.findLastVisibleItemPosition()
            val offsetFirstPosition = position - firstPosition
            val offsetLastPosition = position - lastPosition

            val offsetX = getChildAt(offsetFirstPosition)?.run {
                /**
                 * 目标 item 可见
                 */
                (left + right) shr 1
            } ?: getChildAt(lastPosition)?.run {
//                "scrollToCenter [ offsetX=  ($left + ${offsetLastPosition * mItemWidth} + $mHalfItemWidth)]".e()
                /**
                 * 目标 item 不可见
                 * 显示的最后一个item的 x 坐标位置 + 目标位置到最后一个 item 的总item宽度 + item 的一半宽度
                 */
                ((left + right) shr 1) + offsetLastPosition * mItemWidth
            }
            offsetX?.apply {
                val dx = offsetX - mCenterX
//                "scrollToCenter [prePosition = $prePosition, currentPosition = $currentPosition, offsetFirstPosition:$offsetFirstPosition = (position:$position - firstPosition:$firstPosition), lastPosition = $lastPosition, dx:$dx = (offsetX:$offsetX - centerX:$centerX)]".e()
                if (dx != 0) {
                    post {
                        smoothScrollBy(dx, 0)
                    }
                }
            }
        }
    }

    /**
     * 获取一个合适的中间位置
     */
    private fun getCenterBetterPosition(childCount: Int, position: Int): Int {
        if (childCount < 0) {
            return position
        }
        var centerPosition = childCount shr 1
        if (childCount % 2 == 0) {
            val nextPosition = centerPosition
            val prePosition = nextPosition - 1
            val preChild = getChildAt(prePosition)
            val nextChild = getChildAt(nextPosition)

            val pl = preChild.left
            val pw = preChild.width
            val nl = nextChild.left
            val nw = nextChild.width
            val preCenterX = pl + pw shr 1
            val nextCenterX = nl + nw shr 1
            val preOffset = abs(preCenterX - mCenterX)
            val nextOffset = abs(nextCenterX - mCenterX)

            centerPosition = if (preOffset <= nextOffset) {
                prePosition
            } else {
                nextPosition
            }
        }
//        "getCenterBetterPosition [currentPosition = $currentPosition, position = $position, centerPosition = $centerPosition, childCount = $childCount]".i()
        return centerPosition
    }

    /**
     * 资源回收
     */
    fun recycle() {
//        cacheCompletedBitmap.apply {
//            values.forEach {
//                it.recycle()
//            }
//            clear()
//        }
//        cacheBitmap.apply {
//            values.forEach {
//                it.recycle()
//            }
//            clear()
//        }
//        changeMap.clear()

        children.forEach {
            (it as? GalleryItemLayout)?.apply {
                recycle()
            }
        }
    }

    /**
     * item 监听
     */
    interface ItemListener {
        fun onItemChanged(position: Int)
        fun onItemClick(view: View, position: Int)
        fun onHighlightItemClick(view: View, position: Int)
    }

//    /**
//     * item是否加载完成
//     */
//    interface Completable {
//        fun itemCompleted(position: Int): Boolean
//    }
//
//    data class Data(
//            var init: Boolean = true,
//            var drawable: Drawable? = null,
//            @DrawableRes var defDrawableRes: Int = R.drawable.ic_card_center_placeholder
//    ) {
//        var needRefresh: Boolean = false
//            set(value) {
//                field = value
//                if (value) {
//                    init = false
//                }
//            }
//    }
}