package com.kotlin.android.core.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
 * Created by suq on 2021/12/7
 * des: LiveData扩展类
 */

fun <T> LifecycleOwner.observeLiveData(uiState: LiveData<T>?, observer: Observer<T>) {
    uiState?.observe(this, observer)
}