package com.kotlin.android.film.widget.seat

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import com.kotlin.android.mtime.ktx.ext.showToast
import java.util.*

/**
 * 座位图缩放手势
 *
 * Created on 2022/2/17.
 *
 * @author o.s
 */
internal class SeatClick(
    private val context: Context,
    private val seatIcon: SeatIcon,
    private val seatChart: SeatChart,
    private val seatOverview: SeatOverview,
    private val seatAnim: SeatAnim,
    val refresh: () -> Unit,
) {
    private val matrixValues = seatChart.matrixValues
    private var seatManager: SeatManager<*, *>? = null
    private var maxSelected = 4

    var isOnClick = false

    val selects = ArrayList<Int>() // 座位选择列表

    fun reset() {
        selects.clear()
    }

    fun init(seatManager: SeatManager<*, *>) {
        this.seatManager = seatManager
        maxSelected = seatManager.getMaxSelectCount()
    }

    val gestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if (e != null) {
                    isOnClick = true
                    val x = e.x
                    val y = e.y
//                    seatPlugins?.clearTipsView()
                    // 座位事件
                    for (i in 0 until seatChart.row) {
                        for (j in 0 until seatChart.col) {
                            val tempX = (j * seatIcon.width + j * seatChart.horizontalSpacing) * matrixValues.sx + matrixValues.dx
                            val maxTemX = tempX + seatIcon.width * matrixValues.sx

                            val tempY = (i * seatIcon.height + i * seatChart.verticalSpacing) * matrixValues.sy + matrixValues.dy
                            val maxTempY = tempY + seatIcon.height * matrixValues.sy

                            seatManager?.apply {
                                if (isValidSeat(i, j)
                                    && !isSold(i, j)
                                    && !isCovered(i, j)
                                    && !isRepairSeat(i, j)) {
                                    if (x in tempX..maxTemX && y in tempY..maxTempY) {
                                        val id = getID(i, j)
                                        val index = isHave(id)
                                        if (index >= 0) {
                                            unCheckedSeat(i, j)
                                            seatAnim.autoStartDoubleAnim(x, y)
                                        } else {
                                            if (selects.size >= maxSelected) {
                                                showToast("最多购买${maxSelected}张票")
                                                return super.onSingleTapConfirmed(e)
                                            } else if (selects.size == maxSelected - 1 && (isCoupleLeftSeat(i, j) || isCoupleRightSeat(i, j))) { // 情侣座连选的情况要排除
                                                showToast("最多购买${maxSelected}张票")
                                                return super.onSingleTapConfirmed(e)
                                            } else if (isDisabilitySeat(i, j)) {
                                                showToast("请在影院内购买残疾人坐席")
                                                return super.onSingleTapConfirmed(e)
                                            } else if (!isCheckRule(i, j)) {
                                                // TODO 不符合规则
                                            } else {
                                                checkedSeat(i, j)
                                                seatAnim.autoStartDoubleAnim(x, y)
//                                                seatPlugins?.handleClick(i, j)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return super.onSingleTapConfirmed(e)
            }
        })
    }

    /**
     * 选座座位
     *
     * @param i i
     * @param j j
     */
    fun checkedSeat(i: Int, j: Int) {
        addChooseSeat(i, j)
        seatManager?.checked(i, j)
        seatOverview.isUpdateOverview = true
        refresh()
    }

    private fun addChooseSeat(row: Int, column: Int) {
        val id = getID(row, column)
        for (i in selects.indices) {
            val item = selects[i]
            if (id < item) {
                selects.add(i, id)
                return
            }
        }

        selects.add(id)
    }

    /**
     * 删除座位
     *
     * @param i i
     * @param j j
     */
    fun unCheckedSeat(i: Int, j: Int) {
        cancelSeat(i, j)
        val pos = seatManager?.cancelCoupleRule(i, j)
        if (pos != null) {
            cancelSeat(pos[0], pos[1])
        }
        seatOverview.isUpdateOverview = true
//        seatPlugins?.handleUnClick(i, j)
        refresh()
    }

    private fun cancelSeat(i: Int, j: Int) {
        val id = getID(i, j)
        val index = isHave(id)
        if (index >= 0) {
            // 操作集合删除注意顺序，避免下标混乱越界。
            selects.removeAt(index)

            seatManager?.unCheck(i, j)
        }
    }

    fun getID(row: Int, column: Int): Int = row * seatChart.col + (column + 1)

    /**
     * 判断选择列表中是否有指定的座位
     *
     * @param seat 座位
     * @return true 表示选择列表中有该座位；反之。
     */
    fun isHave(seat: Int?): Int = Collections.binarySearch<Int>(selects, seat)
}