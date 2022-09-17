package com.kotlin.android.ugc.detail.component.ui.link

import androidx.fragment.app.FragmentActivity
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/14
 * des:
 **/
const val TAG_FRAGMENT_UGC_LINK = "tag_fragment_ugc_link"

fun FragmentActivity.showUgcLinkDialog(binderList:MutableList<MultiTypeBinder<*>>,listener:((MultiTypeBinder<*>)->Unit)? = null):UgcLinkFragment{
        return getOrGenerateUgcLinkFragment().apply {
            setBinderList(binderList)
            ugcListener = listener
        }
}

fun FragmentActivity.dismissUgcLinkDialog() {
    getUgcLinkFragment()?.run {
        if (isVisible){
            dismissAllowingStateLoss()
        }
    }
}

fun FragmentActivity.getOrGenerateUgcLinkFragment():UgcLinkFragment{
    var ugcLinkFragment = getUgcLinkFragment()
    if (ugcLinkFragment == null){
        ugcLinkFragment = UgcLinkFragment()
        ugcLinkFragment?.showNow(supportFragmentManager, TAG_FRAGMENT_UGC_LINK)
    }
    return ugcLinkFragment

}

fun FragmentActivity.getUgcLinkFragment():UgcLinkFragment? = supportFragmentManager.findFragmentByTag(
    TAG_FRAGMENT_UGC_LINK) as? UgcLinkFragment