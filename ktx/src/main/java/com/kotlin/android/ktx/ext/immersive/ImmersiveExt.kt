package com.kotlin.android.ktx.ext.immersive

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.ContentFrameLayout
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.kotlin.android.ktx.ext.log.d

/**
 * 沉浸式扩展：
 * 沉浸式相关参数含义
 *
 * Created on 2020/9/18.
 *
 * @author o.s
 */

fun Activity.immersive() = ImmersiveManager.instance.get(this)
fun Fragment.immersive() = ImmersiveManager.instance.get(this)
fun DialogFragment.immersive() = ImmersiveManager.instance.get(this) // 全屏请使用style: R.style.Dialog_FullScreen
fun Activity.immersive(dialog: Dialog) = Immersive.with(this, dialog) // ImmersiveManager.instance.get(this, dialog)

/**
 * ## 能够造成SystemUI Flag被系统自动清除的交互分类
 * 一.触摸屏幕任何位置
 * 二.顶部下拉状态栏
 * 三.底部上拉导航栏
 * 四.Window的变化(如：跳转到其他界面、弹出键盘等)
 *
 * ## SystemUI Flag 相关特性详解 api=30 时，以下属性都过时了：
 *
 * 1.[View.SYSTEM_UI_FLAG_HIDE_NAVIGATION]：(>=api16)
 *  作用是隐藏系统NavigationBar。
 *  但是用户的任何交互，都会导致此Flag被系统清除，进而导航栏自动重新显示，同时 [View.SYSTEM_UI_FLAG_FULLSCREEN] 也会被自动清除，因此StatusBar也会同时显示出来。
 *
 * 2.[View.SYSTEM_UI_FLAG_FULLSCREEN]：(>=api16)
 *  作用是隐藏StatusBar。
 *  和 [WindowManager.LayoutParams.FLAG_FULLSCREEN] 有相同视觉效果。不同在于，此Flag一般用在暂时需要全屏的情形（如：阅读应用，全屏视频等），以便让用户的注意力暂时集中在内容上，
 *  而如果只是简单的需要一直停留在全屏状态（如：游戏应用），使用 [WindowManager.LayoutParams.FLAG_FULLSCREEN] 则是更好的选择。
 *  此Flag会因为各种的交互（如：跳转到其他应用,下拉StatusBar，弹出键盘）的发送而被系统清除。
 *
 * 3.[View.SYSTEM_UI_FLAG_IMMERSIVE]：(>=api19)
 *  作用:避免某些用户交互造成系统自动清除全屏状态。
 *  (1).[View.SYSTEM_UI_FLAG_IMMERSIVE] 和 [View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY] 的使用主要是为了当设置全屏模式时，避免某些用户交互造成系统自动清除全屏状态。
 *  (2).当使用 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 隐藏导航栏时，配合此特性，只有第三、四种操作会导致导航栏的隐藏状态被系统自动清除；否则，任何交互都会导致导航栏的隐藏状态被系统自动清除。
 *  (3).此标识只有配合 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 才有作用。
 *
 * 4.[View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY]：(>=api19)
 *  作用:避免某些用户交互造成系统自动清除全屏状态。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
 *  (1).用 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 隐藏导航栏，配合使用此Flag,只有用户的第四种操作会导致状态栏或（和）导航栏的隐藏状态被系统自动清除。否则任何交互都会导致相应状态的清除。
 *  (2).此Flag只有配合 [View.SYSTEM_UI_FLAG_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION] 使用时才会起作用。
 *
 * 5.[View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION]：(>=api16) 设置 [WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION] 时自动添加
 *  作用:在不隐藏导航栏的情况下，将Activity的显示范围扩展到导航栏底部。同时Activity的部分内容也因此被NavigationBar覆盖遮挡。
 *  (1).当使用此Flag时，设置 [View.setFitsSystemWindows] = true 的view，会被系统自动添加大小为NavigationBar高度相同的paddingBottom。
 *  (2).当window设置 [WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION] 时，此Flag会被系统会自动添加。
 *
 * 6.[View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]：(>=api16) 设置 [WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS] 时自动添加
 *  作用：在不隐藏StatusBar的情况下，将view所在window的显示范围扩展到StatusBar下面。同时Activity的部分内容也因此被StatusBar覆盖遮挡。
 *  (1).当使用此Flag时，设置 [View.setFitsSystemWindows] = true 的view，会被系统自动添加大小为statusBar和ActionBar高度之和相同的paddingTop。
 *  (2).当window设置 [WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS] 时，此Flag会被系统会自动添加。
 *
 * 7.[View.SYSTEM_UI_FLAG_LAYOUT_STABLE]:
 *  作用: 稳定布局。当StatusBar和NavigationBar动态显示和隐藏时，系统为 [View.setFitsSystemWindows] = true 的view设置的padding大小都不会变化，所以view的内容的位置也不会发生移动。
 *  (1).当使用 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 或 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION]，同时view设置 [View.setFitsSystemWindows] = true 时，系统会为此View自动设置padding。
 *   此padding的大小由 [View.fitSystemWindows] 的Rect提供。一般情况下，当StatusBar和NavigationBar显示时，paddingTop的大小为StatusBar的高度。
 *   如果把 [Activity.getWindow.requestFeature] 设置为 [Window.FEATURE_ACTION_BAR_OVERLAY], paddingTop的大小则为StatusBar和ActionBar的高度之和。paddingBottom的大小则为NavigationBar的高度。
 *   当StatusBar和NavigationBar被隐藏时，View的padingBottom和paddingTop的大小就变成了0, 因此StatusBar和NavigationBar的显示和隐藏造成的padding变化，进而View内容的位置变化，从而造成位置闪动的视觉效果，影响体验。
 *   使用 [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 的作用便是当StatusBar和NavigationBar的显示和隐藏，系统为View设置的padding都不会变化，因此View内容的位置不会变化，此即为稳定布局。
 *  (2).（[View.SYSTEM_UI_FLAG_FULLSCREEN] | [View.SYSTEM_UI_FLAG_LAYOUT_STABLE]）同时使用，会同时隐藏Actionbar和StatusBar，但StatusBar所占空间不会隐藏，只会变成空白。
 *   同时View所在window的显示范围也不会伸展到StatusBar所占空间。若是加上[View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]，View所在window的显示范围则会伸展到StatusBar所在的空间。
 *   同样对NavigationBar如此操作，也会是一样的效果。([View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION])
 *  (3).当你设置了 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 时，配合此特性，若此时设置或取消 [View.SYSTEM_UI_FLAG_FULLSCREEN]，不会因为StatusBar的显示或隐藏不会造成内容view的不稳定。
 *  (4).当你设置了 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION]，配合此特性，
 *   若此时设置或取消 [View.SYSTEM_UI_FLAG_FULLSCREEN] 和 [View.SYSTEM_UI_FLAG_HIDE_NAVIGATION]，不会因为StatusBar和导航栏的显示或隐藏不会造成内容view的不稳定。
 *  (5).此特性不应该只配合 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] 使用
 *  (6).使用 [WindowManager.LayoutParams.FLAG_FULLSCREEN]（而不是使用 [View.SYSTEM_UI_FLAG_FULLSCREEN]）来隐藏StatusBar是一个一直持续隐藏的状态。
 *   这时你仍然可以使用 [View.SYSTEM_UI_FLAG_FULLSCREEN] | [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 隐藏Actionbar，并且不会因为ActionBar的显示或隐藏而不稳定。
 *
 * ## fitSystemWindows 只有设置了 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] 或 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]，View的 [View.setFitsSystemWindows] = true 才会有效果。
 *
 * ## WindowManager 相关特性详解
 *
 * 1.[WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS]： (>=api16)
 *  (1).半透明StatusBar,并且不会因用户交互而被清除。
 *  (2).设置了此flag,系统会自动设置 [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 和 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN]
 *
 * 2.[WindowManager.LayoutParams.FLAG_FULLSCREEN]：
 *  (1).用于隐藏StatusBar
 *  (2).使用此flag,系统会自动忽略输入法的 [WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE] 的特性。
 *
 * 3.[WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION]：(>=api19) Android4.4
 *  (1).半透明NavigationBar,并且不会因用户交互而被清除。
 *  (2).设置了此flag,系统会自动设置 [View.SYSTEM_UI_FLAG_LAYOUT_STABLE] 和 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION]
 *
 * 4.[WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS]：(>=api21) Android5.0
 *  1.用于给StatusBar和NavigationBar设置背景颜色。
 *  2.原理：将StatusBar和NavigationBar设置为透明背景，并且将StatusBar和NavigationBar所在空间设置为 [Window.getStatusBarColor] 和 [Window.getNavigationBarColor] 方法获得的颜色。
 *
 *  SystemUI Flag引起的软键盘变化
 *  在使用 [WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE]，
 *  同时 [View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN] 或 [View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION] 时，当键盘弹出时，只会 [View.setFitsSystemWindows] = true 的view所占区域会被resize，其他view将会被软键盘覆盖。
 *
 */
fun systemUIFlags() {
//    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//    View.SYSTEM_UI_FLAG_FULLSCREEN
//    WindowManager.LayoutParams.FLAG_FULLSCREEN
//    View.SYSTEM_UI_FLAG_IMMERSIVE
//    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//
//    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
//
//    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//
//    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//
//    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
}

/**
 * 同步View给StatusBar腾出空间的策略
 * 只有在设置了透明穿透时才会生效，即： [Immersive.transparentStatusBar]
 */
fun View?.syncFitsSystemWindows(isFitsSystemWindows: Boolean = false) {
    this?.fitsSystemWindows = isFitsSystemWindows
    if (Immersive.LOG_DEBUG) {
        this?.javaClass?.simpleName.d()
    }
    if (this is ViewGroup) {
        this.children.forEach {
            it.syncFitsSystemWindows(isFitsSystemWindows)
        }
    }
}

/**
 * DecorView 树，查找 ContentFrameLayout(xml-RootView-parent)
mDecorView
|-	LinearLayout
|-	|-	ViewStub
|-	|-	FrameLayout (mContentView)
|-	|-	|-	FitWindowsLinearLayout
|-	|-	|-	|-	ViewStubCompat
|-	|-	|-	|-	ContentFrameLayout(xml-RootView-parent)
|-	|-	|-	|-	|-	MultiStateView
 *
 */
fun View?.findContentFrameLayout(): ContentFrameLayout? {
    if (Immersive.LOG_DEBUG) {
        "findContentFrameLayout >>> ${this?.javaClass?.simpleName}".d()
    }
    when (this) {
        is ContentFrameLayout -> {
            return this
        }
        is ViewGroup -> {
            this.children.forEach {
                val result = it.findContentFrameLayout()
                if (result != null) {
                    return result
                }
            }
        }
    }
    return null
}