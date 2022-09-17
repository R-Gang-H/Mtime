package com.kotlin.android.widget.recycler

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * create by lushan on 2020/9/7
 * description:item在屏幕显示情况下也可以滑动到指定位置的recyclerView
 */
class ScrollRecyclerView @JvmOverloads constructor(var ctx:Context,var arrts:AttributeSet? = null,var defaultStyle:Int = 0) :RecyclerView(ctx,arrts,defaultStyle){

    private var move = false
    private var mIndex = 0
    init {
        addOnScrollListener(object :OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                //在这里进行第二次滚动（最后的距离）
                if (move) {
                    move = false
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    val linearLayoutManager = recyclerView.layoutManager as? LinearLayoutManager
                    val n = mIndex - (linearLayoutManager?.findFirstVisibleItemPosition() ?: 0)
                    if (0 <= n && n < recyclerView.childCount) {
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        val top: Int = recyclerView.getChildAt(n).top //- toolbar.getHeight() * 2
                        //最后的移动
                        recyclerView.scrollBy(0, top)
                    } else if (n < 0) {
                        linearLayoutManager?.scrollToPositionWithOffset(mIndex, 0)//toolbar.getHeight() * 2)
                    }
                }
            }
        })
    }


    /**
     * recyclerView滑动到指定位置
     * @param index 指定位置
     */
     fun moveToPosition(index: Int) {
        if (index<0) return
        mIndex = index
        //获取当前recycleView屏幕可见的第一项和最后一项的Position
        val linearLayoutManager = layoutManager as LinearLayoutManager
        val firstItem = linearLayoutManager.findFirstVisibleItemPosition()
        val lastItem = linearLayoutManager.findLastVisibleItemPosition()
        //然后区分情况
        if (index <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            smoothScrollToPosition(index)
            move = true
        } else if (index <= lastItem) {
            //当要置顶的项已经在屏幕上显示时，计算它离屏幕原点的距离
            val top: Int = getChildAt(index - firstItem).top// - toolbar.getHeight() * 2
            scrollBy(0, top)
        } else {
            //当要置顶的项在当前显示的最后一项的后面时
            smoothScrollToPosition(index)
            //记录当前需要在RecyclerView滚动监听里面继续第二次滚动
            move = true
        }
    }
}