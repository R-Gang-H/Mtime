package com.kotlin.android.ktx.ext.keyboard

import android.app.Activity
import android.graphics.Rect
import android.os.ResultReceiver
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.kotlin.android.ktx.ext.core.inputMethodManager
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w

/**
 * 软键盘相关扩展:
 *
 * Created on 2021/3/9.
 *
 * @author o.s
 */

/**
 * 隐藏软键盘
 */
fun Activity?.hideSoftInput(flags: Int = 0, resultReceiver: ResultReceiver? = null) {
    this?.inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, flags, resultReceiver)
//    this?.currentFocus?.clearFocus()
}

/**
 * 判断键盘是否弹出
 */
fun Activity?.isShowSoftInput(): Boolean {
    return this?.inputMethodManager?.isActive(currentFocus) ?: false
}

/**
 * 如果键盘弹出执行后隐藏，如果隐藏执行就弹出
 */
fun Activity?.showOrHideSoftInput(view: EditText? = null){
    val editText = view ?: (this?.currentFocus as? EditText)
    "showOrHideSoftInput editText:$editText".e()
    if (isShowSoftInput()){
//        editText?.clearFocus()
        editText?.hideSoftInput()
    }else{
        editText?.postDelayed({ editText.showSoftInput() },100L)
    }
}

/**
 * 使用当前 view 的 Activity 获取当前有焦点的 EditText，如果键盘弹出执行后隐藏，如果隐藏执行就弹出
 */
fun View?.showOrHideSoftInputWithActivity(view: EditText? = null){
    (this?.context as? Activity).showOrHideSoftInput(view)
}

/**
 * Activity 获取当前有焦点的 EditText，并弹起键盘
 */
fun Activity?.showSoftInput(view: EditText? = null) {
    val editText = view ?: (this?.currentFocus as? EditText)
    editText.showSoftInput()
}

/**
 * 使用当前 view 的 Activity 获取当前有焦点的 EditText，并弹起键盘
 */
fun View?.showSoftInputWithActivity() {
    (this?.context as? Activity).showSoftInput()
}

/**
 * 请求显示键盘：[View.showSoftInput]
 *
 * 注意：隐藏键盘和判断键盘是否弹起，请使用：（键盘弹起应作用在Activity上）
 * [Activity.isShowSoftInput]: 判断UI是否弹起键盘
 * [Activity.hideSoftInput]: 隐藏键盘并清除焦点（键盘应和焦点保持一致性）
 *
 * [flags]: 操作标志。目前可以是
 *  0:
 *  [InputMethodManager.SHOW_IMPLICIT]: 隐式请求显示输入窗口，这种情况可能不会显示该窗口
 *  [InputMethodManager.SHOW_FORCED]: 表示用户强制显示输入窗口
 *
 * [resultReceiver]: 已完成的操作回调，
 *  [InputMethodManager.RESULT_UNCHANGED_SHOWN]: 输入窗口的状态不变处于显示状态。
 *  [InputMethodManager.RESULT_UNCHANGED_HIDDEN]: 输入窗口的状态不变处于隐藏状态。
 *  [InputMethodManager.RESULT_SHOWN]: 输入窗口的状态从隐藏变为显示。
 *  [InputMethodManager.RESULT_HIDDEN]: 输入窗口的状态从显示变为隐藏。
 */
fun View?.showSoftInput(flags: Int = 0, resultReceiver: ResultReceiver? = null) {
    this?.requestFocus()
    this?.context?.inputMethodManager?.showSoftInput(this, flags, resultReceiver)
}

/**
 * 隐藏软键盘
 */
fun View?.hideSoftInput(flags: Int = InputMethodManager.HIDE_NOT_ALWAYS, resultReceiver: ResultReceiver? = null) {
    this?.context?.inputMethodManager?.hideSoftInputFromWindow(windowToken, flags, resultReceiver)
//    this?.clearFocus()
}

/**
 * 判断键盘是否弹出
 */
fun View?.isShowSoftInput(): Boolean {
    return this?.context?.inputMethodManager?.isActive(this) ?: false
}

internal var mWindowHeight = 0

/**
 * 注意回收监听器
 */
fun Activity.setKeyboardListener(hide: (Int) -> Unit, show: (Int) -> Unit): ViewTreeObserver.OnGlobalLayoutListener {
    return ViewTreeObserver.OnGlobalLayoutListener {
        val rect = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rect)
        val height = rect.height()

        if (mWindowHeight == 0) {
            //一般情况下，这是原始的窗口高度
            mWindowHeight = height
            "init mWindowHeight:$mWindowHeight".i()
        } else {
            if (mWindowHeight != height) {
                //两次窗口高度相减，就是软键盘高度
                val softKeyboardHeight = mWindowHeight - height
                if (softKeyboardHeight > 400) {
                    show.invoke(softKeyboardHeight)
                } else if (softKeyboardHeight < -400) {
                    hide.invoke(-softKeyboardHeight)
                }
                "mWindowHeight:$mWindowHeight height:$height softKeyboardHeight:$softKeyboardHeight".w()
                mWindowHeight = height
            }
        }
    }.apply {
        window.decorView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }
}