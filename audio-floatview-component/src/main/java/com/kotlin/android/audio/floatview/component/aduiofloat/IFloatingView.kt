package com.kotlin.android.audio.floatview.component.aduiofloat

import android.app.Activity
import android.content.Context
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingView
import android.widget.FrameLayout
import com.kotlin.android.audio.floatview.component.aduiofloat.FloatingMagnetView
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.kotlin.android.audio.floatview.component.aduiofloat.MagnetViewListener

interface IFloatingView {
    fun remove(): FloatingView?
    fun add(context: Context?): FloatingView?
    fun attach(activity: Activity?): FloatingView?
    fun attach(container: FrameLayout?): FloatingView?
    fun detach(activity: Activity?): FloatingView?
    fun detach(container: FrameLayout?): FloatingView?
    val view: FloatingMagnetView?
    fun icon(@DrawableRes resId: Int): FloatingView?
    fun customView(viewGroup: FloatingMagnetView?): FloatingView?
    fun customView(@LayoutRes resource: Int): FloatingView?
    fun layoutParams(params: ViewGroup.LayoutParams?): FloatingView?
    fun listener(magnetViewListener: MagnetViewListener?): FloatingView?
}