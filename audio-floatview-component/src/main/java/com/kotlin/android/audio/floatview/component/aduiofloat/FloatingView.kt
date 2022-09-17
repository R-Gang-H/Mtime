package com.kotlin.android.audio.floatview.component.aduiofloat

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.widget.FrameLayout
import com.kotlin.android.audio.floatview.component.R
import android.view.ViewGroup
import android.os.Looper
import android.widget.RelativeLayout
import android.view.Gravity
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import com.kotlin.android.ktx.ext.dimension.dp
import java.lang.Exception
import java.lang.ref.WeakReference

class FloatingView private constructor() : IFloatingView {
    override var view: FloatingMagnetView? = null
        private set
    private var mContainer: WeakReference<FrameLayout?>? = null

    @LayoutRes
    private var mLayoutId = R.layout.view_float_audio
    private var mLayoutParams: ViewGroup.LayoutParams? = params
    override fun remove(): FloatingView? {
        Handler(Looper.getMainLooper()).post(object : Runnable {
            override fun run() {
                if (view == null) {
                    return
                }
                view?.apply {
                    if (ViewCompat.isAttachedToWindow(this) && container != null) {
                        container?.removeView(view)
                    }
                    view = null
                }

            }
        })
        return this
    }

    private fun ensureFloatingView(context: Context?) {
        synchronized(this) {
            if (view != null) {
                return
            }
            context?:return
            val enFloatingView = EnFloatingView(context, mLayoutId)
            view = enFloatingView
            enFloatingView.layoutParams = mLayoutParams
            addViewToWindow(enFloatingView)
        }
    }

    override fun add(context: Context?): FloatingView? {
        ensureFloatingView(context)
        return this
    }

    override fun attach(activity: Activity?): FloatingView? {
        attach(getActivityRoot(activity))
        return this
    }

    override fun attach(container: FrameLayout?): FloatingView? {
        if (container == null || view == null) {
            mContainer = WeakReference(container)
            return this
        }
        if (view?.parent == container) {
            return this
        }
        if (view?.parent != null) {
            (view?.parent as? ViewGroup)?.removeView(view)
        }
        mContainer = WeakReference(container)
        container.addView(view)
        return this
    }

    override fun detach(activity: Activity?): FloatingView? {
        detach(getActivityRoot(activity))
        return this
    }

    override fun detach(container1: FrameLayout?): FloatingView? {
        view?.apply {
            if (container1 != null && ViewCompat.isAttachedToWindow(this)
            ) {
                container1?.removeView(view)
            }
        }

        if (mContainer == container1) {
            mContainer = null
        }
        return this
    }

    override fun icon(@DrawableRes resId: Int): FloatingView? {
        return this
    }

    override fun customView(viewGroup: FloatingMagnetView?): FloatingView? {
        view = viewGroup
        return this
    }

    override fun customView(@LayoutRes resource: Int): FloatingView? {
        mLayoutId = resource
        return this
    }

    override fun layoutParams(params: ViewGroup.LayoutParams?): FloatingView? {
        mLayoutParams = params
            view?.layoutParams = params
        return this
    }

    override fun listener(magnetViewListener: MagnetViewListener?): FloatingView? {
            view?.setMagnetViewListener(magnetViewListener)
        return this
    }

    private fun addViewToWindow(view: View) {
        container?.addView(view)
    }

    private val container: FrameLayout?
        private get() = if (mContainer == null) {
            null
        } else mContainer?.get()
    private val params: FrameLayout.LayoutParams
        private get() {
            val params = FrameLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.BOTTOM or Gravity.END
            params.setMargins(13, params.topMargin, 0, 100.dp)
            return params
        }

    private fun getActivityRoot(activity: Activity?): FrameLayout? {
        if (activity == null) {
            return null
        }
        try {
            return activity.window.decorView.findViewById<View>(android.R.id.content) as FrameLayout
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        @Volatile
        private var mInstance: FloatingView? = null
        @JvmStatic
        fun get(): FloatingView? {
            if (mInstance == null) {
                synchronized(FloatingView::class.java) {
                    if (mInstance == null) {
                        mInstance = FloatingView()
                    }
                }
            }
            return mInstance
        }
    }
}