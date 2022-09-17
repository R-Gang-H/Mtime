package com.kotlin.android.ktx.lifecycle

import androidx.activity.ComponentActivity
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy

/**
 * ViewModel 构建扩展:
 *
 * Created on 2020/8/25.
 *
 * @author o.s
 */

/**
 * Returns a [Lazy] delegate to access the ComponentActivity's ViewModel
 *
 * ```
 * class MyAny {
 *     var vm: MyViewModel? = null
 *
 *     fun initVM(activity: ComponentActivity) {
 *         vm = viewModels<MyViewModel>(activity).value
 *     }
 * }
 * ```
 *
 * This property can be accessed only after the Activity is attached to the Application,
 * and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> viewModels(activity: ComponentActivity): Lazy<VM> {
    return ViewModelLazy(VM::class, { activity.viewModelStore }, { activity.defaultViewModelProviderFactory })
}

/**
 * Returns a property delegate to access [ViewModel] by **default** scoped to this [Fragment]:
 *
 * ```
 * class MyAny {
 *     var vm: MyViewModel? = null
 *
 *     fun initVM(fragment: Fragment) {
 *         vm = viewModels<MyViewModel>(fragment).value
 *     }
 * }
 * ```
 *
 * This property can be accessed only after this Fragment is attached i.e., after
 * [Fragment.onAttach()], and access prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> viewModels(fragment: Fragment): Lazy<VM> {
//    return ViewModelLazy(VM::class, { fragment.requireActivity().viewModelStore }, { fragment.requireActivity().defaultViewModelProviderFactory })
    return ViewModelLazy(VM::class, { fragment.viewModelStore }, { fragment.defaultViewModelProviderFactory })
}
