package com.kotlin.android.ktx.lifecycle

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

/**
 * ViewBinding/ViewDataBinding 构建扩展:
 *
 * 基于 include, merge 引入的子布局，在使用ViewBinding时注意:
 * include: include标签加 id 即可 (如：mBinding.titleBar.back)
 * merge:   include标签不可以加 id，需要使用子布局 val subBinding = xxxSubBinding.bind(mBinding.root)
 *
 * Created on 2021/2/5.
 *
 * @author o.s
 */

///**
// * 创建基于 [AppCompatActivity] 的 [ViewBinding]
// */
//inline fun <reified VB: ViewBinding> viewBindings(activity: ComponentActivity): VB {
//    val inflateMethod = VB::class.java.getDeclaredMethod("inflate", LayoutInflater::class.java, Object::class.java)
//    return inflateMethod.invoke(null, activity.layoutInflater) as VB
//}

/**
 * Activity
 */
inline fun <reified VB : ViewBinding> Activity.viewBinding(): Lazy<VB> {
    return ViewBindingLazy(VB::class, layoutInflater)
}

/**
 * Fragment
 */
inline fun <reified VB : ViewBinding> Fragment.viewBinding(
    container: ViewGroup? = null,
    attachToRoot: Boolean = false
): Lazy<VB> {
    return ViewBindingLazy(
        viewBindingClass = VB::class,
        layoutInflater = layoutInflater,
        root = container,
        attachToRoot = attachToRoot
    )
}

/**
 * RecyclerView.Adapter, ViewHolder 等
 */
inline fun <reified VB : ViewBinding> ViewGroup.viewBinding(
//    parent: ViewGroup,
    attachToRoot: Boolean = false
): Lazy<VB> {
    return ViewBindingLazy(
        viewBindingClass = VB::class,
        layoutInflater = LayoutInflater.from(context),
        root = this,
        attachToRoot = attachToRoot
    )
}

class ViewBindingLazy<VB : ViewBinding>(
    private val viewBindingClass: KClass<VB>,
    private val layoutInflater: LayoutInflater,
    private val root: ViewGroup? = null,
    private val attachToRoot: Boolean? = null,
) : Lazy<VB> {
    private var cached: VB? = null

    /**
     * Gets the lazily initialized value of the current Lazy instance.
     * Once the value was initialized it must not change during the rest of lifetime of this Lazy instance.
     */
    override val value: VB
        get() {
            return cached
                ?: if (attachToRoot != null) {
                    val inflateMethod = viewBindingClass.java.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java,
                        ViewGroup::class.java,
                        Boolean::class.java
                    )
                    (inflateMethod.invoke(null, layoutInflater, root, attachToRoot) as VB).apply {
                        cached = this
                    }
                } else {
                    val inflateMethod = viewBindingClass.java.getDeclaredMethod(
                        "inflate",
                        LayoutInflater::class.java
                    )
                    (inflateMethod.invoke(null, layoutInflater) as VB).apply {
                        cached = this
                    }
                }
        }

    /**
     * Returns `true` if a value for this Lazy instance has been already initialized, and `false` otherwise.
     * Once this function has returned `true` it stays `true` for the rest of lifetime of this Lazy instance.
     */
    override fun isInitialized(): Boolean = cached != null

}