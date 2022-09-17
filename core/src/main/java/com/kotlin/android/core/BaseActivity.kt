package com.kotlin.android.core

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView


/**
 * 创建者: zl
 * 创建时间: 2020/5/18 9:03 AM
 * 描述:
 */
abstract class BaseActivity : AppCompatActivity() {
    /**初始化参数*/
    protected open fun initVariable() {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        RouterManager.instance.inject(this)

//        requestedOrientation =
//                if (isOrientation()) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//                else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        initVariable()
//        // TODO: 2020/7/14 先放着，后面看看是否需要使用
//        /**目前没有使用注解方式获取传参*/
//        ARouter.getInstance().inject(this)
//        StatusBarUtils.initDisplayOpinion(this)
//        if (canControlStatusBarTextColor()) {
//            if (isDarkTheme()) {
//                translucentStatusBar(this, true)
//            } else {
//                translucentStatusBar(this, false)
//            }
//        }

//        immersive()
//                .transparentStatusBar()
//                .statusBarDarkFont(isDarkTheme())
    }

//    /**
//     * SystemUI是否默认是黑字风格(暗黑)
//     */
//    protected open fun isDarkTheme(): Boolean {
//        return true
//    }

//    /**
//     * 设置页面默认为竖屏
//     */
//    protected open fun isOrientation(): Boolean {
//        return true
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        shareFragment {
            // 为分享模块预留（QQ分享回调）
            it.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun shareFragment(run: (fragment: Fragment) -> Unit) {
        // 分享回调分发 tag = "tag_fragment_share"
        supportFragmentManager.findFragmentByTag("tag_fragment_share")?.run {
            if (isVisible) {
                run.invoke(this)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        FloatingView.get()?.attach(this)
    }

    override fun onStop() {
        super.onStop()
        FloatingView.get()?.detach(this)
    }
}