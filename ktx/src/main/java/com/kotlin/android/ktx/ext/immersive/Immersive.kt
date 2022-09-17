package com.kotlin.android.ktx.ext.immersive

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.ColorInt
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import java.lang.ref.WeakReference

/**
 * 沉浸式样式设置：
 *
 * Created on 2020/9/18.
 *
 * @author o.s
 */
class Immersive {
    val log: String
        get() = "act=${mActivity?.get()?.javaClass?.simpleName}, fragment=${mFragment?.get()?.javaClass?.simpleName}"

    companion object {
        fun with(activity: Activity): Immersive = Immersive(activity)

        fun with(fragment: Fragment): Immersive = Immersive(fragment)

        fun with(dialogFragment: DialogFragment): Immersive = Immersive(dialogFragment)

        fun with(activity: Activity, dialog: Dialog): Immersive = Immersive(activity, dialog)

        const val LOG_DEBUG = false
    }

    private var mParent: Immersive? = null
    private val config by lazy { Config() }

    private var mActivity: WeakReference<Activity>? = null
    private var mFragment: WeakReference<Fragment>? = null
    private var mDialogFragment: WeakReference<DialogFragment>? = null
    private var mDialog: WeakReference<Dialog>? = null
    private var mWindow: WeakReference<Window>? = null
    private var mDecorView: WeakReference<ViewGroup>? = null
    private var mContentView: WeakReference<ViewGroup>? = null

    /**
     * 是否是在Activity里使用
     */
    private var mIsActivity = false

    /**
     * 是否是在Fragment里使用
     */
    private var mIsFragment = false

    /**
     * 是否是DialogFragment
     */
    private var mIsDialogFragment = false

    /**
     * 是否是在Dialog里使用
     */
    private var mIsDialog = false

    /**
     * 是否已经调用过 [init] 方法了
     */
    private var mInitialized = false

    private constructor(activity: Activity) {
        mIsActivity = true
        mActivity = WeakReference(activity)
        initWindow(activity.window)
    }

    private constructor(fragment: Fragment) {
        mIsFragment = true
        mFragment = WeakReference(fragment)
        fragment.activity?.apply {
            mActivity = WeakReference(this)
            initWindow(window)
        }
        initParent()
    }

    private constructor(dialogFragment: DialogFragment) {
        mIsDialog = true
        mIsDialogFragment = true
        mDialogFragment = WeakReference(dialogFragment)
        dialogFragment.activity?.run {
            mActivity = WeakReference(this)
//            initWindow(window)
        }
        dialogFragment.dialog?.run {
            mDialog = WeakReference(this)
            initWindow(window)
        }
        initParent()
    }

    private constructor(activity: Activity, dialog: Dialog) {
        mIsDialog = true
        mActivity = WeakReference(activity)
        mDialog = WeakReference(dialog)
        initWindow(dialog.window)
    }

    private fun initWindow(window: Window?) {
        window?.apply {
            mWindow = WeakReference(this)
            mDecorView = WeakReference(decorView as ViewGroup)
            mContentView = WeakReference(decorView.findViewById(android.R.id.content))
        }
    }

    private fun initParent() {
        if (mParent == null) {
            mActivity?.get()?.apply {
                mParent = with(this)
            }
        }
        mParent?.apply {
            if (!mInitialized) {
                init()
            }
        }
    }

    /**
     * 设置全屏状态（没有状态栏的效果，只有特殊情况下会使用）。
     *
     * 如果是沉浸式布局穿透状态栏，请使用 [transparentStatusBar]
     */
    fun fullscreen(isFullscreen: Boolean = true, style: FullscreenStyle = FullscreenStyle.IMMERSIVE_STICKY): Immersive {
        config.fullscreen = isFullscreen
        syncFullscreen(style)
        return this
    }

    /**
     * 设置沉浸式状态栏背景透明：
     * [isFitsSystemWindows] false：View穿透状态栏，true：View不穿透状态栏。默认不穿透。
     * 注意: [isTransparent] = true 的情况下无法更改状态栏背景色，因为明确了透明色。
     */
    fun transparentStatusBar(isTransparent: Boolean = true, isFitsSystemWindows: Boolean = false): Immersive {
        config.transparentStatusBar = isTransparent
        if (LOG_DEBUG) {
            "(transparentStatusBar, isFitsSystemWindows) -> (${config.transparentStatusBar}, $isFitsSystemWindows), $log".i()
        }
        syncTransparentStatusBar(isFitsSystemWindows)
        return this
    }

    /**
     * 设置导航栏背景半透明
     */
    fun transparentNavigationBar(isTransparent: Boolean = true): Immersive {
        config.transparentNavigationBar = isTransparent
        syncTransparentNavigationBar()
        return this
    }

    /**
     * 设置状态栏字体颜色：true：暗色Icon
     */
    fun statusBarDarkFont(isDarkFont: Boolean): Immersive {
//        if (config.statusBarDarkFont == isDarkFont) {
//            return this
//        }
        config.statusBarDarkFont = isDarkFont
        if (LOG_DEBUG) {
            "statusBarDarkFont = ${config.statusBarDarkFont}".i()
        }
        syncStatusBarDarkFont()
        return this
    }

    /**
     * 设置导航栏字体颜色：true：暗色Icon
     */
    fun navigationBarDarkFont(isDarkFont: Boolean): Immersive {
        if (config.navigationBarDarkFont == isDarkFont) {
            return this
        }
        config.navigationBarDarkFont = isDarkFont
        syncNavigationBarDarkFont()
        return this
    }

    /**
     * 设置状态栏背景颜色
     */
    fun statusBarColor(@ColorInt color: Int): Immersive {
//        removeTranslucentFlag()
        config.statusBarColor = color
        syncStatusBarColor()
        return this
    }

    /**
     * 频繁更新状态栏背景色时使用该方法
     */
    fun updateStatusBarColor(@ColorInt color: Int): Immersive {
        if (config.statusBarColor == color) {
            return this
        }
        config.statusBarColor = color
        updateStatusBarColor()
        return this
    }

    fun navigationBarColor(@ColorInt color: Int): Immersive {
//        removeTranslucentFlag()
        config.navigationBarColor = color
        syncNavigationBarColor()
        return this
    }

    /**
     * 初始化
     */
    fun init() {

        mInitialized = true
    }

    /**
     * 同步 全屏状态
     */
    private fun syncFullscreen(style: FullscreenStyle) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            mDecorView?.get()?.apply {
//                config.fullscreen?.apply {
//                    syncContentViewFitsSystemWindows(false)
//                    if (this) {
//                        windowInsetsController?.hide(WindowInsets.Type.systemBars())
//                    } else {
//                        windowInsetsController?.show(WindowInsets.Type.systemBars())
//                    }
//                    if (this) {
//                        syncSystemUiVisibility(add = View.INVISIBLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//                    } else {
//                        syncSystemUiVisibility(remove = View.INVISIBLE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
//                    }
//                }
//            }
//        } else {
//        }
        removeTranslucentFlag()
        syncFullscreenByWindow()
        syncFullscreenDispatch(style)
    }

    /**
     * 使用FLAG_FULLSCREEN对window设置来达到全屏效果
     */
    private fun syncFullscreenByWindow() {
        config.fullscreen?.apply {
            if (this) {
                syncWindowFlags(add = WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                syncWindowFlags(remove = WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
        }
    }

    /**
     * 1，在向后倾斜模式和沉浸式模式下，当系统栏显示后不会自动隐藏，如果需要再次隐藏需再次调用 setSystemUiVisibility()方法并设置相应参数。而在粘性沉浸式模式下，系统栏显示后可以自动隐藏。
     * 2，在向后倾斜模式和沉浸式模式下，当系统栏显示后用户会收到onSystemUiVisibilityChange（）回调，而在粘性沉浸式模式下用户不会收到该回调
     * 3，在粘性沉浸模式下当用户从边缘滑动退出全屏时，App会接收到滑动事件，而在普通沉浸模式下不会。
     */
    private fun syncFullscreenDispatch(style: FullscreenStyle) {
        syncContentViewFitsSystemWindows(false)
        when (style) {
            FullscreenStyle.ONCE -> syncFullscreenOnce()
            FullscreenStyle.IMMERSIVE -> syncFullscreenImmersive()
            FullscreenStyle.IMMERSIVE_STICKY -> syncFullscreenImmersiveSticky()
        }
    }

    /**
     * 1，向后倾斜模式
     * 向后倾斜模式适用于用户不会与屏幕进行大量互动的全屏体验，例如在观看视频时。当用户希望再次调出系统栏时，只需点按屏幕上的任意位置即可。
     * 要启用向后倾斜模式，可以通过调用 [setSystemUiVisibility] 并传递入 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION]。
     */
    private fun syncFullscreenOnce() {
        mDecorView?.get()?.apply {
            config.fullscreen?.apply {
                if (this) {
                    syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                } else {
                    syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
                }
            }
        }
    }

    /**
     * 2，沉浸模式
     * 沉浸模式适用于用户将与屏幕进行大量互动的应用。 示例包括游戏、查看图库中的图片或者阅读分页内容，如图书或演示文稿中的幻灯片。
     * 当用户需要调出系统栏时，他们可从隐藏系统栏的任一边滑动。要求使用这种这种意图更强的手势是为了确保用户与您应用的互动不会因意外轻触和滑动而中断。
     * 要启用沉浸模式，请调用 [setSystemUiVisibility] 并将 [View.SYSTEM_UI_FLAG_IMMERSIVE] 标志与 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 一起传递。
     */
    private fun syncFullscreenImmersive() {
        mDecorView?.get()?.apply {
            config.fullscreen?.apply {
                if (this) {
                    syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE)
                } else {
                    syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE)
                }
            }
        }
    }

    /**
     * 3，粘性沉浸模式
     * 在普通的沉浸模式中，只要用户从边缘滑动，系统就会负责显示系统栏，您的应用甚至不会知道发生了该手势。因此，如果用户实际上可能是出于主要的应用体验而需要从屏幕边缘滑动，例如在玩需要大量滑动的游戏或使用绘图应用时，您应改为启用“粘性”沉浸模式。
     * 在粘性沉浸模式下，如果用户从隐藏了系统栏的边缘滑动，系统栏会显示出来，但它们是半透明的，并且轻触手势会传递给应用，因此应用也会响应该手势。
     * 例如，在使用这种方法的绘图应用中，如果用户想绘制从屏幕最边缘开始的线条，则从这个边缘滑动会显示系统栏，同时还会开始绘制从最边缘开始的线条。无互动几秒钟后，或者用户在系统栏之外的任何位置轻触或做手势时，系统栏会自动消失。
     * 要启用粘性沉浸模式，请调用 [setSystemUiVisibility] 并将 [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] 标志与 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 一起传递
     */
    private fun syncFullscreenImmersiveSticky() {
        mDecorView?.get()?.apply {
            config.fullscreen?.apply {
                if (this) {
                    syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                } else {
                    syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
                }
            }
        }
    }

    /**
     * 同步状态栏透明情况
     */
    private fun syncTransparentStatusBar(isFitsSystemWindows: Boolean = false) {
        removeTranslucentFlag()
        syncFlagLayoutFullScreen(config.transparentStatusBar)
        syncFlagLayoutStable(config.transparentStatusBar)
        syncDrawStatusBarColor(config.transparentStatusBar)
        syncContentViewFitsSystemWindows(isFitsSystemWindows)
        syncStatusBarColor()
    }

    /**
     * 同步导航栏半透明情况
     */
    private fun syncTransparentNavigationBar() {
        syncFlagTranslucentNavigation(config.transparentNavigationBar)
        syncNavigationBarColor()
    }

    /**
     * 移除半透明状态
     * 注意清除时，默认状态栏字体【白色】
     */
    private fun removeTranslucentFlag() {
        syncWindowFlags(remove = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    }

    /**
     * 同步 Window 的 Flags
     */
    private fun syncWindowFlags(add: Int? = null, remove: Int? = null) {
        mWindow?.get()?.apply {
            add?.apply {
                addFlags(this)
            }
            remove?.apply {
                clearFlags(this)
            }
        }
    }

    /**
     * 同步 DecorView 的 systemUiVisibility
     */
    private fun syncSystemUiVisibility(add: Int? = null, remove: Int? = null) {
        mDecorView?.get()?.apply {
            if (LOG_DEBUG) {
                "syncSystemUiVisibility systemUiVisibility = $systemUiVisibility".i()
            }
            add?.apply {
                systemUiVisibility = systemUiVisibility or this
            }
            remove?.apply {
                systemUiVisibility = systemUiVisibility and this.inv()
            }
            if (LOG_DEBUG) {
                "syncSystemUiVisibility systemUiVisibility = $systemUiVisibility".w()
            }
        }
    }

    /**
     * [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]
     *  作用：在不隐藏StatusBar的情况下，将view所在window的显示范围扩展到StatusBar下面。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
     *  (1).当使用此Flag时，设置 [View.setFitsSystemWindows] = true 的view，会被系统自动添加大小为statusBar和ActionBar高度之和相同的paddingTop。
     *  (2).当window设置 [WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS] 时，此Flag会被系统会自动添加。
     */
    private fun syncFlagLayoutFullScreen(isAdd: Boolean?) {
        isAdd?.apply {
            if (this) {
                syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            } else {
                syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            }
        }
    }

    /**
     * [View.SYSTEM_UI_FLAG_LAYOUT_STABLE]:
     *  作用: 稳定布局。当StatusBar和NavigationBar动态显示和隐藏时，系统为 [View.setFitsSystemWindows] = true 的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。
     */
    private fun syncFlagLayoutStable(isAdd: Boolean?) {
        isAdd?.apply {
            if (this) {
                syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            } else {
                syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            }
        }
    }

    /**
     * [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY]：(>=api19)
     *  作用:避免某些用户交互造成系统自动清除全屏状态。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
     *  (1).用 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 隐藏导航栏，配合使用此Flag,只有用户的第四种操作会导致状态栏或（和）导航栏的隐藏状态被系统自动清除。否则任何交互都会导致相应状态的清除。
     *  (2).此Flag只有配合 [View.SYSTEM_UI_FLAG_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 使用时才会起作用。
     */
    private fun syncFlagImmersiveSticky(isAdd: Boolean?) {
        isAdd?.apply {
            if (this) {
                syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            } else {
                syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
            }
        }
    }

    /**
     * [WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION]：(>=api19) Android4.4
     *  (1).半透明NavigationBar,并且不会因用户交互而被清除。
     *  (2).设置了此flag,系统会自动设置 [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 和 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION]
     */
    private fun syncFlagTranslucentNavigation(isAdd: Boolean?) {
        isAdd?.let {
            if (it) {
                syncWindowFlags(add = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            } else {
                syncWindowFlags(remove = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            }
        }
    }

    /**
     * 只有在透明状态栏的前提下，才允许修改状态栏背景颜色
     */
    private fun syncDrawStatusBarColor(isAdd: Boolean?) {
        isAdd?.apply {
            if (this) {
                // 允许修改系统状态栏背景色
                syncWindowFlags(add = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            } else {
                syncWindowFlags(remove = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            }
        }
    }

    /**
     * 同步状态栏字体颜色
     */
    private fun syncStatusBarDarkFont() {
        if (LOG_DEBUG) {
            "SDK_INT=${Build.VERSION.SDK_INT}, syncStatusBarDarkFont = ${config.statusBarDarkFont}".i()
        }
        mDecorView?.get()?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                config.statusBarDarkFont?.apply {
                    if (this) {
                        windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_STATUS_BARS, APPEARANCE_LIGHT_STATUS_BARS)
                    } else {
                        windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_STATUS_BARS)
                    }
                }
            } else {
                setStatusBarDarkFont()
            }
        }
    }

    /**
     * 同步导航栏字体颜色
     */
    private fun syncNavigationBarDarkFont() {
        mDecorView?.get()?.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                config.navigationBarDarkFont?.apply {
                    if (this) {
                        windowInsetsController?.setSystemBarsAppearance(APPEARANCE_LIGHT_NAVIGATION_BARS, APPEARANCE_LIGHT_NAVIGATION_BARS)
                    } else {
                        windowInsetsController?.setSystemBarsAppearance(0, APPEARANCE_LIGHT_NAVIGATION_BARS)
                    }
                }
            } else {
                setNavigationBarDarkFont()
            }
        }
    }

    /**
     * 设置状态栏字体颜色，android6.0以上
     */
    private fun setStatusBarDarkFont() {
        config.statusBarDarkFont?.apply {
            if (this) {
                syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            } else {
                syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            }
        }
    }

    /**
     * 设置导航栏图标亮色与暗色
     */
    private fun setNavigationBarDarkFont() {
        config.navigationBarDarkFont?.apply {
            if (this) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    syncSystemUiVisibility(add = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    syncSystemUiVisibility(remove = View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }
            }
        }
    }

    /**
     * 同步状态栏背景颜色
     */
    private fun syncStatusBarColor() {
        if (config.transparentStatusBar == true) {
            config.statusBarColor = Color.TRANSPARENT
        }
        if (LOG_DEBUG) {
            "syncStatusBarColor color = ${config.statusBarColor}".i()
        }
        updateStatusBarColor()
    }

    /**
     * 更新状态栏背景色时使用该方法
     */
    private fun updateStatusBarColor() {
        mWindow?.get()?.apply {
            config.statusBarColor?.apply {
                statusBarColor = this
            }
        }
    }

    /**
     * 同步导航栏背景颜色
     */
    private fun syncNavigationBarColor() {
        if (config.transparentNavigationBar == true) {
            config.navigationBarColor = Color.TRANSPARENT
        }
        updateNavigationBarColor()
    }

    /**
     * 更新导航栏背景色时使用该方法
     */
    private fun updateNavigationBarColor() {
        mWindow?.get()?.apply {
            config.navigationBarColor?.apply {
                navigationBarColor = this
            }
        }
    }

    /**
     * 同步UI ContentView 树的 fitsSystemWindows 属性，为true时，当状态栏穿透时，给状态栏保留扣件
     *
     * 说明：
     * 1，使用 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] Flag时，设置 [View.setFitsSystemWindows] = true 的view，会被系统自动添加大小为NavigationBar高度相同的paddingBottom。
     * 2，使用 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] Flag时，设置 [View.setFitsSystemWindows] = true 的view，会被系统自动添加大小为statusBar和ActionBar高度之和相同的paddingTop。
     * 3，使用 [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] Flag时，稳定布局。当StatusBar和NavigationBar动态显示和隐藏时，系统为 [View.setFitsSystemWindows] = true 的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。
     *
     * 注意：fitSystemWindows 只有设置了 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] 或 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]，View的 [View.setFitsSystemWindows] = true 才会有效果。
     */
    private fun syncContentViewFitsSystemWindows(isFitsSystemWindows: Boolean = false) {
        mContentView?.get()?.apply {
//            "syncContentViewFitsSystemWindows isFitsSystemWindows = $isFitsSystemWindows".i()
//            if (fitsSystemWindows != isFitsSystemWindows) {
//                "syncContentViewFitsSystemWindows isFitsSystemWindows = $isFitsSystemWindows".w()
//            }
            syncFitsSystemWindows(isFitsSystemWindows)
        }
    }

    /**
     * 是否是宿主
     */
    fun isOwner(any: Any): Boolean {
        return when (any) {
            is Activity -> {
                mActivity?.get() == any
            }
            is Fragment -> {
                mFragment?.get() == any
            }
            is DialogFragment -> {
                mDialogFragment?.get() == any
            }
            is Dialog -> {
                mDialog?.get() == any
            }
            else -> {
                false
            }
        }
    }

    class Config {
        /**
         * 状态栏背景颜色
         */
        @ColorInt
        var statusBarColor: Int? = null // Color.TRANSPARENT

        /**
         * 导航栏背景颜色
         */
        @ColorInt
        var navigationBarColor: Int? = null // Color.BLACK

        /**
         * 状态栏透明
         */
        var transparentStatusBar: Boolean? = null

        /**
         * 导航栏透明
         */
        var transparentNavigationBar: Boolean? = null

        /**
         * 状态栏字体：暗色
         */
        var statusBarDarkFont: Boolean? = null

        /**
         * 导航栏字体：暗色
         */
        var navigationBarDarkFont: Boolean? = null

        /**
         * 全屏
         */
        var fullscreen: Boolean? = null
    }

    /**
     * 全屏样式：
     * 1，在向后倾斜模式和沉浸式模式下，当系统栏显示后不会自动隐藏，如果需要再次隐藏需再次调用 setSystemUiVisibility()方法并设置相应参数。而在粘性沉浸式模式下，系统栏显示后可以自动隐藏。
     * 2，在向后倾斜模式和沉浸式模式下，当系统栏显示后用户会收到onSystemUiVisibilityChange（）回调，而在粘性沉浸式模式下用户不会收到该回调
     * 3，在粘性沉浸模式下当用户从边缘滑动退出全屏时，App会接收到滑动事件，而在普通沉浸模式下不会。
     */
    enum class FullscreenStyle {
        /**
         * 向后倾斜模式
         */
        ONCE,

        /**
         * 沉浸式模式
         */
        IMMERSIVE,

        /**
         * 粘性沉浸模式
         */
        IMMERSIVE_STICKY
    }
}