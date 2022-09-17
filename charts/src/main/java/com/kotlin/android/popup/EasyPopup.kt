package com.kotlin.android.popup

import android.content.Context
import android.view.View

class EasyPopup : BasePopup<EasyPopup?> {
    private var mOnViewListener: OnViewListener? = null

    constructor() {}
    constructor(context: Context?) {
        setContext(context)
    }

    protected override fun initAttributes() {}
    protected override fun initViews(view: View?, popup: EasyPopup?) {
        if (mOnViewListener != null) {
            mOnViewListener?.initViews(view, popup)
        }
    }

    fun setOnViewListener(listener: OnViewListener?): EasyPopup {
        mOnViewListener = listener
        return this
    }

    interface OnViewListener {
        fun initViews(view: View?, popup: EasyPopup?)
    }

    companion object {
        fun create(): EasyPopup {
            return EasyPopup()
        }

        fun create(context: Context?): EasyPopup {
            return EasyPopup(context)
        }
    }
}