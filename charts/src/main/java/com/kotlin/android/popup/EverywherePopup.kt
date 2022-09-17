package com.kotlin.android.popup

import android.content.Context
import android.graphics.PointF
import android.view.Gravity
import android.view.View
import com.kotlin.android.R

class EverywherePopup private constructor(context: Context?) : BasePopup<EverywherePopup?>() {
    private var mOnViewListener: OnViewListener? = null

    override fun initAttributes() {}
    override fun initViews(view: View?, popup: EverywherePopup?) {
        if (mOnViewListener != null) {
            mOnViewListener?.initViews(view, popup)
        }
        /*setOnRealWHAlreadyListener(new OnRealWHAlreadyListener() {
            @Override
            public void onRealWHAlready(BasePopup basePopup, int popWidth, int popHeight, int anchorW, int anchorH) {

            }
        });*/
    }

    fun setOnViewListener(listener: OnViewListener?): EverywherePopup {
        mOnViewListener = listener
        return this
    }

    interface OnViewListener {
        fun initViews(view: View?, popup: EverywherePopup?)
    }

    /**
     * 自适应触摸点 弹出
     * @param parent
     * @param touchX    计算偏移量后的x触摸点
     * @param touchY    计算偏移量后的y触摸点
     * @param point     原始触摸点
     * @param location  当前view在屏幕中的坐标
     * @param widgetWidth   控件宽 默认屏幕宽
     * @param widgetHeiget  控件高 默认屏幕高
     * @return
     */
    fun showEverywhere(
        parent: View?,
        touchX: Int,
        touchY: Int,
        point: PointF,
        location: IntArray,
        widgetWidth: Int = getScreenWidthHeight()[0],
        widgetHeiget: Int = getScreenWidthHeight()[1],
    ): EverywherePopup {
        //if (isRealWHAlready) {
        val screenWidth: Int = widgetWidth
        val screenHeight: Int = widgetHeiget
        var offsetX = touchX
        var offsetY = touchY
        if (point.x < width && screenHeight - point.y < height) {
            //左下弹出动画
            popupWindow?.animationStyle = R.style.LeftBottomPopAnim
            offsetY = touchY - height
        } else if (point.x + width > screenWidth && point.y + height > screenHeight) {
            //右下弹出动画
            popupWindow?.animationStyle = R.style.RightBottomPopAnim
            offsetX = touchX - width
            offsetY = touchY - height
        } else if (point.x + location[0] + width > screenWidth - location[0]) {
            popupWindow?.animationStyle = R.style.RightTopPopAnim
            offsetX = touchX - width
        } else {
            popupWindow?.animationStyle = R.style.LeftTopPopAnim
        }
        showAtLocation(parent, Gravity.NO_GRAVITY, offsetX, offsetY)
        //}
        return this
    }

    companion object {
        fun create(context: Context?): EverywherePopup {
            return EverywherePopup(context)
        }
    }

    init {
        setContext(context)
    }

}