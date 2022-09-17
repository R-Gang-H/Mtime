package com.kotlin.android.ktx.ext.statusbar

import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

/**
 * 操作状态栏相关扩展：StatusBar
 *
 * Created on 2020/9/2.
 *
 * @author o.s
 */

/**
 * 处理文章详情、ugc详情及pk帖子详情状态栏
 */
@Deprecated(message = "替代", replaceWith = ReplaceWith("com.kotlin.android.ktx.ext.immersive.immersive"))
 fun Activity?.handleArticleStatusBar(isDarkTheme:Boolean) {
     this?:return
    val window: Window = this.window
    val decorView: View = window.decorView
    var systemUiVisibility = (decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
    systemUiVisibility = if (!isDarkTheme) {
        systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    } else {
        systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    decorView.systemUiVisibility = systemUiVisibility
    window.statusBarColor = Color.TRANSPARENT

    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
}