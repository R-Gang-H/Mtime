package com.kotlin.android.ktx.ext.toast

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

/**
 *
 * Created on 2020/4/21.
 *
 * @author o.s
 */

fun Context?.showSystemToast(content: CharSequence?, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        if (content.isNullOrEmpty() || content.isBlank()) {
            return
        }
        Toast.makeText(this, content, duration)?.apply {
            show()
        }
    }
}

fun Context?.showSystemToast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    this?.apply {
        showSystemToast(getString(id),duration)
    }
}






