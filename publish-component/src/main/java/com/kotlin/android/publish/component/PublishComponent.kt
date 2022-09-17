package com.kotlin.android.publish.component

import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.publish.component.ui.PublishFragment
import com.kotlin.android.publish.component.widget.PublishStyle


/**
 * 发布组件：
 *
 * Created on 2020/7/2.
 *
 * @author o.s
 */

/**
 * 发布 [PublishFragment] TAG
 */
const val TAG_FRAGMENT_PUBLISH = "tag_fragment_publish"

/**
 * 展示发布视图Fragment，并返回 [PublishFragment]；如果没有展示，则新建并添加展示
 */
fun FragmentActivity.showPublishFragment(
        @IdRes containerViewId: Int,
): PublishFragment {
    return getOrGeneratePublishFragment(containerViewId)
}

fun FragmentActivity.getOrGeneratePublishFragment(@IdRes containerViewId: Int): PublishFragment {
    var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PUBLISH) as? PublishFragment
    if (fragment == null) {
        fragment = PublishFragment()
        supportFragmentManager
                .beginTransaction()
                .add(containerViewId, fragment, TAG_FRAGMENT_PUBLISH)
                .commit()
    }
    return fragment
}