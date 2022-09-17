package com.kotlin.android.card.monopoly.ext

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 卡片大富翁通用弹窗扩展：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */

const val TAG_FRAGMENT_CARD_MONOPOLY_COMM_DIALOG = "tag_fragment_card_monopoly_comm_dialog"

fun FragmentActivity.showCardMonopolyCommDialog(
        style: CommDialog.Style = CommDialog.Style.COMMON,
        data: CommDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        close: (() -> Unit)? = null,
        event: ((data: CommDialog.Data?) -> Unit)? = null
): CommDialog {
    return getOrGenerateCardMonopolyCommDialog(style).apply {
        setCancelable(isCancelable)
        this.style = style
        this.data = data
        this.event = event
        this.close = close
        this.dismiss = dismiss
    }
}

fun Fragment.showCardMonopolyCommDialog(
        style: CommDialog.Style = CommDialog.Style.COMMON,
        data: CommDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        close: (() -> Unit)? = null,
        event: ((data: CommDialog.Data?) -> Unit)? = null
): CommDialog? {
    return activity?.showCardMonopolyCommDialog(style, data, isCancelable, dismiss, close, event)
}

fun View.showCardMonopolyCommDialog(
        style: CommDialog.Style = CommDialog.Style.COMMON,
        data: CommDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        close: (() -> Unit)? = null,
        event: ((data: CommDialog.Data?) -> Unit)? = null
): CommDialog? {
    return (getActivity() as? FragmentActivity)?.showCardMonopolyCommDialog(style, data, isCancelable, dismiss, close, event)
}

fun FragmentActivity.dismissCardMonopolyCommDialog() {
    getCardMonopolyCommDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateCardMonopolyCommDialog(style: CommDialog.Style): CommDialog {
    var fragment = getCardMonopolyCommDialog()
    if (fragment == null) {
        fragment = CommDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_COMM_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getCardMonopolyCommDialog(): CommDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_COMM_DIALOG) as? CommDialog
}