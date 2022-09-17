package com.kotlin.android.live.component.observer

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast

/**
 * create by lushan on 2021/3/8
 * description:
 */
class BaseObserver<T : Any> constructor(private var activity: FragmentActivity, private var netErrorAction: ((String) -> Unit)? = null, private var errorAction: ((String) -> Unit)? = null, private var action: (T) -> Unit) : Observer<BaseUIModel<T>> {
    override fun onChanged(t: BaseUIModel<T>?) {
        t?.apply {
            activity.showOrHideProgressDialog(showLoading)
            success?.apply {
                action.invoke(this)
            }
            netError?.apply {
                if (netErrorAction != null) {
                    netErrorAction?.invoke(this)
                } else {
                    showToast()
                }
            }
            error?.apply {
                if (errorAction != null) {
                    errorAction?.invoke(this)
                } else {
                    showToast()
                }
            }
            if (isEmpty){
                errorAction?.invoke(error.orEmpty())
            }
        }
    }

}