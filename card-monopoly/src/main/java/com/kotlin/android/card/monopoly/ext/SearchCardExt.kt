package com.kotlin.android.card.monopoly.ext

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.ui.search.SearchCardFragment
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 搜索卡片套装 Dialog 扩展
 *
 * Created on 2020/9/17.
 *
 * @author o.s
 */

const val TAG_FRAGMENT_SEARCH_CARD = "tag_fragment_search_card"

fun FragmentActivity.showSearchCardDialog(
        isCancelable: Boolean = true,
        dismiss: (() -> Unit)? = null,
        event: ((event: SearchCardFragment.ActionEvent) -> Unit)? = null
) {
    getOrGenerateSearchCardDialog().run {
        setCancelable(isCancelable)
        this.event = event
        this.dismiss = dismiss
    }
}

fun DialogFragment.showSearchCardDialog(
        isCancelable: Boolean = true,
        dismiss: (() -> Unit)? = null,
        event: ((event: SearchCardFragment.ActionEvent) -> Unit)? = null
) {
    activity?.showSearchCardDialog(isCancelable, dismiss, event)
}

fun View.showSearchCardDialog(
        isCancelable: Boolean = true,
        dismiss: (() -> Unit)? = null,
        event: ((event: SearchCardFragment.ActionEvent) -> Unit)? = null
) {
    (getActivity() as? FragmentActivity)?.showSearchCardDialog(isCancelable, dismiss, event)
}

fun FragmentActivity.dismissSearchCardDialog() {
    getSearchCardDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateSearchCardDialog(): SearchCardFragment {
    var fragment = getSearchCardDialog()
    if (fragment == null) {
        fragment = SearchCardFragment()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_SEARCH_CARD)
    }
    return fragment
}

fun FragmentActivity.getSearchCardDialog(): SearchCardFragment? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_SEARCH_CARD) as? SearchCardFragment
}