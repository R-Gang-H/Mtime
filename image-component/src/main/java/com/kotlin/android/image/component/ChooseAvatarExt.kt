package com.kotlin.android.image.component

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentHostCallback
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.ui.ChooseAvatarFragment
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 选择头像视图扩展：
 *
 * Created on 2020/12/24.
 *
 * @author o.s
 */
const val TAG_FRAGMENT_CHOOSE_AVATAR = "tag_fragment_choose_avatar"

/**
 * 显示选择头像视图：
 */
fun FragmentActivity.showChooseAvatarDialog(
//        callback: ((avatarUrl: String) -> Unit)? = null,
        photo: ((photo: PhotoInfo) -> Unit)? = null
): ChooseAvatarFragment {
    return getOrGenerateChooseAvatarDialog().apply {
//        this.callback = callback
        this.photo = photo
    }
}

/**
 * 显示选择头像视图：
 */
fun Fragment.showChooseAvatarDialog(
        photo: ((photo: PhotoInfo) -> Unit)? = null
): ChooseAvatarFragment? {
    return activity?.showChooseAvatarDialog(photo)
}

/**
 * 显示选择头像视图：
 */
fun View.showChooseAvatarDialog(
        photo: ((photo: PhotoInfo) -> Unit)? = null
): ChooseAvatarFragment? {
    return (getActivity() as? FragmentActivity)?.showChooseAvatarDialog(photo)
}

fun FragmentActivity.getOrGenerateChooseAvatarDialog(): ChooseAvatarFragment {
    var fragment = getChooseAvatarFragment()
    if (fragment == null) {
        fragment = ChooseAvatarFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CHOOSE_AVATAR)
    }
    return fragment
}

fun FragmentActivity.getChooseAvatarFragment(): ChooseAvatarFragment? =
        supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CHOOSE_AVATAR) as? ChooseAvatarFragment