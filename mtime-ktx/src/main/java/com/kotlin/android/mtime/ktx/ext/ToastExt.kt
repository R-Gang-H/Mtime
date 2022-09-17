@file:Suppress("NOTHING_TO_INLINE", "unused")

package com.kotlin.android.mtime.ktx.ext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kotlin.android.core.CoreApp
import com.kotlin.android.mtime.ktx.R

/**
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 * 代码中如果有大量重复的lambda表达式，代码会生成很多临时的无用对象，可以使用inline修饰方法，这样当方法在编译时就会拆解方法的调用为语句的调用，进而减少创建不必要的对象。
 * 如果过度使用inline管关键字，会使代码块变得很庞大，查找问题很麻烦，所以inline一般用来修饰高阶函数.
 */
inline fun Context?.showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    if (message.isNullOrEmpty()) {
        return
    }
    this?.apply {
        val toast = Toast(applicationContext)
        val view = LayoutInflater.from(applicationContext).inflate(R.layout.view_toast, null)
                as TextView
        ShapeExt.setShapeColorAndCorner(view = view, solidColor = R.color.color_000000, corner = 6)
        view.text = message
        toast.view = view
        toast.duration = duration
        toast.show()
    }
}

inline fun Context?.showToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        showToast(getString(id), duration)
    }
}

inline fun Fragment?.showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    this?.context?.apply {
        showToast(message, duration)
    }
}

inline fun Fragment?.showToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    this?.context?.apply {
        showToast(getString(id), duration)
    }
}

inline fun View?.showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    this?.context?.apply {
        showToast(message, duration)
    }
}

inline fun View?.showToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    this?.context?.apply {
        showToast(getString(id), duration)
    }
}

inline fun Any.showToast(context: Context?, message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    context?.apply {
        showToast(message, duration)
    }
}

inline fun Any.showToast(context: Context?, @StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    context?.apply {
        showToast(getString(id), duration)
    }
}

inline fun showToast(message: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    CoreApp.instance.apply {
        showToast(message, duration)
    }
}

inline fun showToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    CoreApp.instance.apply {
        showToast(getString(id), duration)
    }
}

inline fun String?.showToast(){
    showToast(this)
}