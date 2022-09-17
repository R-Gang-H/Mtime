package com.kotlin.android.image.component.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.ui.adapter.PhotoGalleryAdapter

/**
 * 照片画廊
 *
 * Created on 2022/5/13.
 *
 * @author o.s
 */
class PhotoGalleryView : RecyclerView {
    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    private val mLayoutManager by lazy { LinearLayoutManager(context, HORIZONTAL, false) }
    private var currentPosition: Int = 0
    private var prePosition: Int = -1

    private val mAdapter by lazy {
        PhotoGalleryAdapter { photo, position ->
            action?.invoke(photo, position)
        }
    }

    var action: ((photo: PhotoInfo, position: Int) -> Unit)? = null
    var itemChange: ((position: Int, prePosition: Int) -> Unit)? = null

    var photos: ArrayList<PhotoInfo>? = null
        set(value) {
            field = value
            mAdapter.photos = value
        }

    private fun initView() {
        layoutManager = mLayoutManager
        PagerSnapHelper().attachToRecyclerView(this)
        setupScrollListener()
        adapter = mAdapter
    }

    /**
     * 设置滚动监听
     */
    private fun setupScrollListener() {
        addOnScrollListener(object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    currentPosition = mLayoutManager.findFirstVisibleItemPosition()
//                    "onScrollStateChanged (currentPosition, prePosition):($currentPosition, $prePosition)".i()
                    if (prePosition != currentPosition) {
                        itemChange?.invoke(currentPosition, prePosition)
                        prePosition = currentPosition
                    }
                }
            }
        })
    }

//
//    /**
//     * 设置 item 触摸监听
//     */
//    private fun setupItemTouchListener() {
//        addOnItemTouchListener(object : OnItemTouchListener {
//            private val mGestureDetector by lazy {
//                GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
//                    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
//                        return true
//                    }
//                })
//            }
//
//            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
//                rv.findChildViewUnder(e.x, e.y)?.apply {
//                    // 如果是点击事件，处理UI状态
//                    if (mGestureDetector.onTouchEvent(e)) {
//                        val position = getChildAdapterPosition(this)
//                        "onInterceptTouchEvent (position, currentPosition, prePosition):($position, $currentPosition, $prePosition)".e()
//                        action?.invoke(position, prePosition)
//                    }
//                }
//                return false
//            }
//
//            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
//            }
//
//            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
//            }
//
//        })
//    }
}