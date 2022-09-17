package com.kotlin.android.card.monopoly.ext

import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.card.monopoly.widget.dialog.*
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.ktx.ext.core.getActivity

/**
 * 卡片大富翁特殊弹窗扩展：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */

const val TAG_FRAGMENT_CARD_MONOPOLY_CLEAR_POCKET_DIALOG =
    "tag_fragment_card_monopoly_clear_pocket_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_DIG_BOX_DIALOG = "tag_fragment_card_monopoly_dig_box_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_USE_PROP_DIALOG = "tag_fragment_card_monopoly_use_prop_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_SUIT_DETAIL_DIALOG =
    "tag_fragment_card_monopoly_suit_detail_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_SUIT_COMPOSE_DIALOG =
    "tag_fragment_card_monopoly_suit_compose_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_SUIT_UPGRADE_DIALOG =
    "tag_fragment_card_monopoly_suit_upgrade_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_DEAL_DIALOG = "tag_fragment_card_monopoly_deal_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_DEAL_ADD_PRICE_DIALOG =
    "tag_fragment_card_monopoly_deal_add_price_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_CARD_DIALOG = "tag_fragment_card_monopoly_card_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_POCKET_CARD_DIALOG =
    "tag_fragment_card_monopoly_pocket_card_dialog"
const val TAG_FRAGMENT_CARD_MONOPOLY_GET_CARD_DIALOG = "tag_fragment_card_monopoly_get_card_dialog"

/**
 * 清空口袋对话框
 */
fun FragmentActivity.showClearPocketDialog(
    message: String? = null,
    isCardMainPage: Boolean = false,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): ClearPocketDialog {
    return getOrGenerateClearPocketDialog().apply {
        setCancelable(isCancelable)
        this.message = message
        this.isCardMainPage = isCardMainPage
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 清空口袋对话框
 */
fun Fragment.showClearPocketDialog(
    message: String? = null,
    isCardMainPage: Boolean = false,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): ClearPocketDialog? {
    return activity?.showClearPocketDialog(message, isCardMainPage, isCancelable, dismiss, event)
}

/**
 * 清空口袋对话框
 */
fun View.showClearPocketDialog(
    message: String? = null,
    isCardMainPage: Boolean = false,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): ClearPocketDialog? {
    return (getActivity() as? FragmentActivity)?.showClearPocketDialog(
        message,
        isCardMainPage,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissClearPocketDialog() {
    getClearPocketDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateClearPocketDialog(): ClearPocketDialog {
    var fragment = getClearPocketDialog()
    if (fragment == null) {
        fragment = ClearPocketDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_CLEAR_POCKET_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getClearPocketDialog(): ClearPocketDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_CLEAR_POCKET_DIALOG) as? ClearPocketDialog
}

/**
 * 挖宝箱（获取宝箱）对话框
 */
fun FragmentActivity.showDigBoxDialog(
    data: Box,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): DigBoxDialog {
    return getOrGenerateDigBoxDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 挖宝箱（获取宝箱）对话框
 */
fun Fragment.showDigBoxDialog(
    data: Box,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): DigBoxDialog? {
    return activity?.showDigBoxDialog(data, isCancelable, dismiss, event)
}

/**
 * 挖宝箱（获取宝箱）对话框
 */
fun View.showDigBoxDialog(
    data: Box,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): DigBoxDialog? {
    return (getActivity() as? FragmentActivity)?.showDigBoxDialog(
        data,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissDigBoxDialog() {
    getDigBoxDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateDigBoxDialog(): DigBoxDialog {
    var fragment = getDigBoxDialog()
    if (fragment == null) {
        fragment = DigBoxDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_DIG_BOX_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getDigBoxDialog(): DigBoxDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_DIG_BOX_DIALOG) as? DigBoxDialog
}

/**
 * 使用道具对话框
 */
fun FragmentActivity.showUsePropDialog(
    data: UsePropDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): UsePropDialog {
    return getOrGenerateUsePropDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 使用道具对话框
 */
fun Fragment.showUsePropDialog(
    data: UsePropDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): UsePropDialog? {
    return activity?.showUsePropDialog(data, isCancelable, dismiss, event)
}

/**
 * 使用道具对话框
 */
fun View.showUsePropDialog(
    data: UsePropDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): UsePropDialog? {
    return (getActivity() as? FragmentActivity)?.showUsePropDialog(
        data,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissUsePropDialog() {
    getUsePropDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateUsePropDialog(): UsePropDialog {
    var fragment = getUsePropDialog()
    if (fragment == null) {
        fragment = UsePropDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_USE_PROP_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getUsePropDialog(): UsePropDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_USE_PROP_DIALOG) as? UsePropDialog
}

/**
 * 套装详情对话框
 */
fun FragmentActivity.showSuitDetailDialog(
    data: Suit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    action: ((cardList: List<Card>?) -> Unit)? = null,
    event: ((data: UpgradeSuit) -> Unit)? = null
): SuitDetailDialog {
    return getOrGenerateSuitDetailDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
        this.action = action
    }
}

/**
 * 套装详情对话框
 */
fun Fragment.showSuitDetailDialog(
    data: Suit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    action: ((cardList: List<Card>?) -> Unit)? = null,
    event: ((data: UpgradeSuit) -> Unit)? = null
): SuitDetailDialog? {
    return activity?.showSuitDetailDialog(data, isCancelable, dismiss, action, event)
}

/**
 * 套装详情对话框
 */
fun View.showSuitDetailDialog(
    data: Suit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    action: ((cardList: List<Card>?) -> Unit)? = null,
    event: ((data: UpgradeSuit) -> Unit)? = null
): SuitDetailDialog? {
    return (getActivity() as? FragmentActivity)?.showSuitDetailDialog(
        data,
        isCancelable,
        dismiss,
        action,
        event
    )
}

fun FragmentActivity.dismissSuitDetailDialog() {
    getSuitDetailDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateSuitDetailDialog(): SuitDetailDialog {
    var fragment = getSuitDetailDialog()
    if (fragment == null) {
        fragment = SuitDetailDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_SUIT_DETAIL_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getSuitDetailDialog(): SuitDetailDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_SUIT_DETAIL_DIALOG) as? SuitDetailDialog
}

/**
 * 套装合成成功对话框
 */
fun FragmentActivity.showSuitComposeDialog(
    data: MixSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    close: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitComposeDialog {
    return getOrGenerateSuitComposeDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.close = close
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 套装合成成功对话框
 */
fun Fragment.showSuitComposeDialog(
    data: MixSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    close: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitComposeDialog? {
    return activity?.showSuitComposeDialog(data, isCancelable, dismiss, close, event)
}

/**
 * 套装合成成功对话框
 */
fun View.showSuitComposeDialog(
    data: MixSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    close: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitComposeDialog? {
    return (getActivity() as? FragmentActivity)?.showSuitComposeDialog(
        data,
        isCancelable,
        dismiss,
        close,
        event
    )
}

fun FragmentActivity.dismissSuitComposeDialog() {
    getSuitComposeDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateSuitComposeDialog(): SuitComposeDialog {
    var fragment = getSuitComposeDialog()
    if (fragment == null) {
        fragment = SuitComposeDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_SUIT_COMPOSE_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getSuitComposeDialog(): SuitComposeDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_SUIT_COMPOSE_DIALOG) as? SuitComposeDialog
}

/**
 * 套装升级成功对话框
 */
fun FragmentActivity.showSuitUpgradeDialog(
    data: UpgradeSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitUpgradeDialog {
    return getOrGenerateSuitUpgradeDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 套装升级成功对话框
 */
fun Fragment.showSuitUpgradeDialog(
    data: UpgradeSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitUpgradeDialog? {
    return activity?.showSuitUpgradeDialog(data, isCancelable, dismiss, event)
}

/**
 * 套装升级成功对话框
 */
fun View.showSuitUpgradeDialog(
    data: UpgradeSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitUpgradeDialog? {
    return (getActivity() as? FragmentActivity)?.showSuitUpgradeDialog(
        data,
        isCancelable,
        dismiss,
        event
    )
}

fun DialogFragment.showSuitUpgradeDialog(
    data: UpgradeSuit,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): SuitUpgradeDialog? {
    return activity?.showSuitUpgradeDialog(data, isCancelable, dismiss, event)
}

fun FragmentActivity.dismissSuitUpgradeDialog() {
    getSuitUpgradeDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateSuitUpgradeDialog(): SuitUpgradeDialog {
    var fragment = getSuitUpgradeDialog()
    if (fragment == null) {
        fragment = SuitUpgradeDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_SUIT_UPGRADE_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getSuitUpgradeDialog(): SuitUpgradeDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_SUIT_UPGRADE_DIALOG) as? SuitUpgradeDialog
}

/**
 * 交易对话框
 */
fun FragmentActivity.showDealDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealDialog {
    return getOrGenerateDealDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 交易对话框
 */
fun Fragment.showDealDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealDialog? {
    return activity?.showDealDialog(data, isCancelable, dismiss, event)
}

/**
 * 交易对话框
 */
fun View.showDealDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealDialog? {
    return (getActivity() as? FragmentActivity)?.showDealDialog(data, isCancelable, dismiss, event)
}

fun FragmentActivity.dismissDealDialog() {
    getDealDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateDealDialog(): DealDialog {
    var fragment = getDealDialog()
    if (fragment == null) {
        fragment = DealDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_DEAL_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getDealDialog(): DealDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_DEAL_DIALOG) as? DealDialog
}

/**
 * 交易加价对话框
 */
fun FragmentActivity.showDealAddPriceDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealAddPriceDialog {
    return getOrGenerateDealAddPriceDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 交易加价对话框
 */
fun Fragment.showDealAddPriceDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealAddPriceDialog? {
    return activity?.showDealAddPriceDialog(data, isCancelable, dismiss, event)
}

/**
 * 交易加价对话框
 */
fun View.showDealAddPriceDialog(
    data: DealCardView.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((bizCode: Long) -> Unit)? = null
): DealAddPriceDialog? {
    return (getActivity() as? FragmentActivity)?.showDealAddPriceDialog(
        data,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissDealAddPriceDialog() {
    getDealAddPriceDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateDealAddPriceDialog(): DealAddPriceDialog {
    var fragment = getDealAddPriceDialog()
    if (fragment == null) {
        fragment = DealAddPriceDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_DEAL_ADD_PRICE_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getDealAddPriceDialog(): DealAddPriceDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_DEAL_ADD_PRICE_DIALOG) as? DealAddPriceDialog
}

/**
 * 卡片对话框
 */
fun FragmentActivity.showCardDialog(
    data: CardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    clearMainPocket: (() -> Unit)? = null,
    event: ((pocketCards: PocketCards?) -> Unit)? = null
): CardDialog {
    return getOrGenerateCardDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.clearMainPocket = clearMainPocket
        this.dismiss = dismiss
    }
}

/**
 * 卡片对话框
 */
fun Fragment.showCardDialog(
    data: CardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    clearMainPocket: (() -> Unit)? = null,
    event: ((pocketCards: PocketCards?) -> Unit)? = null
): CardDialog? {
    return activity?.showCardDialog(data, isCancelable, dismiss, clearMainPocket, event)
}

/**
 * 卡片对话框
 */
fun View.showCardDialog(
    data: CardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    clearMainPocket: (() -> Unit)? = null,
    event: ((pocketCards: PocketCards?) -> Unit)? = null
): CardDialog? {
    return (getActivity() as? FragmentActivity)?.showCardDialog(
        data,
        isCancelable,
        dismiss,
        clearMainPocket,
        event
    )
}

fun FragmentActivity.dismissCardDialog() {
    getCardDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateCardDialog(): CardDialog {
    var fragment = getCardDialog()
    if (fragment == null) {
        fragment = CardDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_CARD_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getCardDialog(): CardDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_CARD_DIALOG) as? CardDialog
}

/**
 * 开宝箱对话框
 */
fun FragmentActivity.showOpenBoxDialog(
        data: OpenBoxDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        clearMainPocket: (() -> Unit)? = null,
        event: ((pocketCards: PocketCards?) -> Unit)? = null
): OpenBoxDialog {
    return getOrGenerateOpenBoxDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.clearMainPocket = clearMainPocket
        this.dismiss = dismiss
    }
}

/**
 * 开宝箱对话框
 */
fun Fragment.showOpenBoxDialog(
        data: OpenBoxDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        clearMainPocket: (() -> Unit)? = null,
        event: ((pocketCards: PocketCards?) -> Unit)? = null
): OpenBoxDialog? {
    return activity?.showOpenBoxDialog(data, isCancelable, dismiss, clearMainPocket, event)
}

/**
 * 卡片对话框
 */
fun View.showOpenBoxDialog(
        data: OpenBoxDialog.Data,
        isCancelable: Boolean = false,
        dismiss: (() -> Unit)? = null,
        clearMainPocket: (() -> Unit)? = null,
        event: ((pocketCards: PocketCards?) -> Unit)? = null
): OpenBoxDialog? {
    return (getActivity() as? FragmentActivity)?.showOpenBoxDialog(data, isCancelable, dismiss, clearMainPocket, event)
}

fun FragmentActivity.dismissOpenBoxDialog() {
    getOpenBoxDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateOpenBoxDialog(): OpenBoxDialog {
    var fragment = getOpenBoxDialog()
    if (fragment == null) {
        fragment = OpenBoxDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_CARD_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getOpenBoxDialog(): OpenBoxDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_CARD_DIALOG) as? OpenBoxDialog
}

/**
 * 我的口袋卡片对话框
 */
fun FragmentActivity.showPocketCardDialog(
    data: PocketCardDialog.Data? = null,
    style: PocketCardDialog.Style = PocketCardDialog.Style.CARD,
    selectModel: SelectCardView.SelectModel = SelectCardView.SelectModel.SINGLE,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((selectedCards: List<Card>?) -> Unit)? = null
): PocketCardDialog {
    return getOrGeneratePocketCardDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.style = style
        this.selectModel = selectModel
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 我的口袋卡片对话框
 */
fun Fragment.showPocketCardDialog(
    data: PocketCardDialog.Data? = null,
    style: PocketCardDialog.Style = PocketCardDialog.Style.CARD,
    selectModel: SelectCardView.SelectModel = SelectCardView.SelectModel.SINGLE,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((selectedCards: List<Card>?) -> Unit)? = null
): PocketCardDialog? {
    return activity?.showPocketCardDialog(data, style, selectModel, isCancelable, dismiss, event)
}

/**
 * 我的口袋卡片对话框
 */
fun View.showPocketCardDialog(
    data: PocketCardDialog.Data? = null,
    style: PocketCardDialog.Style = PocketCardDialog.Style.CARD,
    selectModel: SelectCardView.SelectModel = SelectCardView.SelectModel.SINGLE,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: ((selectedCards: List<Card>?) -> Unit)? = null
): PocketCardDialog? {
    return (getActivity() as? FragmentActivity)?.showPocketCardDialog(
        data,
        style,
        selectModel,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissPocketCardDialog() {
    getPocketCardDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGeneratePocketCardDialog(): PocketCardDialog {
    var fragment = getPocketCardDialog()
    if (fragment == null) {
        fragment = PocketCardDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_POCKET_CARD_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getPocketCardDialog(): PocketCardDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_POCKET_CARD_DIALOG) as? PocketCardDialog
}

/**
 * 获得卡片对话框
 */
fun FragmentActivity.showGetCardDialog(
    data: GetCardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): GetCardDialog {
    return getOrGenerateGetCardDialog().apply {
        setCancelable(isCancelable)
        this.data = data
        this.event = event
        this.dismiss = dismiss
    }
}

/**
 * 获得卡片对话框
 */
fun Fragment.showGetCardDialog(
    data: GetCardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): GetCardDialog? {
    return activity?.showGetCardDialog(data, isCancelable, dismiss, event)
}

/**
 * 获得卡片对话框
 */
fun View.showGetCardDialog(
    data: GetCardDialog.Data,
    isCancelable: Boolean = false,
    dismiss: (() -> Unit)? = null,
    event: (() -> Unit)? = null
): GetCardDialog? {
    return (getActivity() as? FragmentActivity)?.showGetCardDialog(
        data,
        isCancelable,
        dismiss,
        event
    )
}

fun FragmentActivity.dismissGetCardDialog() {
    getGetCardDialog()?.apply {
        dismissAllowingStateLoss()
    }
}

fun FragmentActivity.getOrGenerateGetCardDialog(): GetCardDialog {
    var fragment = getGetCardDialog()
    if (fragment == null) {
        fragment = GetCardDialog()
        fragment.showNow(supportFragmentManager, TAG_FRAGMENT_CARD_MONOPOLY_GET_CARD_DIALOG)
    }
    return fragment
}

fun FragmentActivity.getGetCardDialog(): GetCardDialog? {
    return supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_CARD_MONOPOLY_GET_CARD_DIALOG) as? GetCardDialog
}