package com.kotlin.android.core.statusbar

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.View
import android.view.WindowManager
import com.kotlin.android.core.R
import com.kotlin.android.core.statusbar.SystemUtils.isFlyme
import com.kotlin.android.core.statusbar.SystemUtils.isMiui
import com.kotlin.android.core.statusbar.SystemUtils.isOverMIUI9
import com.kotlin.android.ktx.ext.dimension.toDPF

/**
 * 创建者: zl
 * 创建时间: 2020/7/6 4:44 PM
 * 描述:
 */
object StatusBarUtils {
    var screenWidthPx: Int = 0 //屏幕宽 px
    var screenHeightPx: Int = 0 //屏幕高 px
    var density: Float = 0.toFloat()//屏幕密度
    var densityDPI: Int = 0//屏幕密度
    var screenWidthDip: Float = 0.toFloat()//  dp单位
    var screenHeightDip: Float = 0.toFloat()//  dp单位
    var statusBarHeight: Int = 0

    var isInitComplete: Boolean = false //初始化是否完成

    /**
     * 在Application中初始化设备的尺寸信息
     * todo 暂时没用到
     */
    fun initDisplayOpinion(mContext: Activity) {
        if (isInitComplete) {
            return
        }
        val dm = mContext.resources.displayMetrics
        density = dm.density
        densityDPI = dm.densityDpi
        screenWidthPx = dm.widthPixels
        screenHeightPx = dm.heightPixels

        screenWidthDip = dm.widthPixels.toDPF
        screenHeightDip = dm.heightPixels.toDPF
        statusBarHeight = getStatusBarHeight(mContext)
        isInitComplete = true
    }

    /**
     * 获取设备状态栏的高度
     * 兼容全面屏
     */
    private fun getStatusBarHeight(context: Activity): Int {
        var statusBarHeight = this.statusBarHeight
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        if (statusBarHeight == 0) {
            val frame = Rect()
            context.window.decorView.getWindowVisibleDisplayFrame(frame)
            statusBarHeight = frame.top
        }
        if (statusBarHeight == 0) {
            statusBarHeight = context.resources.getDimensionPixelOffset(R.dimen.status_bar_height)
        }
        return statusBarHeight
    }

    fun getStatusBarHeight(context: Context?): Int {
        context?.apply {
            val resources = context.resources
            var statusBarHeight = this@StatusBarUtils.statusBarHeight
            //获取status_bar_height资源的ID
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                //根据资源ID获取响应的尺寸值
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            if (statusBarHeight <= 0) {
                try {
                    val clazz = Class.forName("com.android.internal.R\$dimen")
                    val obj = clazz.newInstance()
                    val height = clazz.getField("status_bar_height")[obj].toString().toInt()
                    statusBarHeight = resources.getDimensionPixelSize(height)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            return statusBarHeight
        }

        return 0
    }
    /**
     * 设置沉浸式并设置状态栏字体颜色
     * true 状态栏文字为黑色
     * false 状态栏文字为白色
     */
    fun translucentStatusBar(activity: Activity, isFontColorDark: Boolean = true,ignoreImmersive:Boolean = false) {
        // 设置MIUI或FLYME系统的状态栏字体颜色
        if (isMiui()) {
            setMiuiStatusBarLightMode(activity, isFontColorDark)
        } else if (isFlyme()) {
            setFlymeStatusBarLightMode(activity, isFontColorDark)
        }

        // 设置沉浸式和状态栏字体颜色
        val window = activity.window
        if (ignoreImmersive.not()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        if (isFontColorDark) {
            if (ignoreImmersive.not()) {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR//
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }

        /**
         * 动态设置状态栏样式时，content内容会自动下压，先注释掉
         */
//        val contentView = window.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
//        val childView = contentView.getChildAt(0)
//        if (childView != null) {
//            childView.fitsSystemWindows = false
//            ViewCompat.requestApplyInsets(childView)
//        }
    }

    /**
     * 是否能改变状态栏字体颜色
     */
    fun canControlStatusBarTextColor(): Boolean {
        return isMiui() || isFlyme() || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun setMiuiStatusBarLightMode(activity: Activity, isFontColorDark: Boolean) {
        try {
            val window = activity.window
            if (window != null) {
                if (!isOverMIUI9()) {
                    val clazz = window::class.java
                    try {
                        var darkModeFlag = 0
                        val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                        val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                        darkModeFlag = field.getInt(layoutParams)
                        val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                        if (isFontColorDark) {
                            extraFlagField.invoke(window, darkModeFlag, darkModeFlag)//状态栏透明且黑色字体
                        } else {
                            extraFlagField.invoke(window, 0, darkModeFlag)//清除黑色字体
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setFlymeStatusBarLightMode(activity: Activity, isFontColorDark: Boolean): Boolean {
        var result = false
        val window = activity.window
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                value = if (isFontColorDark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
                result = true
            } catch (e: Exception) {

            }
        }
        return result
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    fun calculateStatusColor(color: Int, alpha: Int): Int {
        val a = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * a + 0.5).toInt()
        green = (green * a + 0.5).toInt()
        blue = (blue * a + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }
}