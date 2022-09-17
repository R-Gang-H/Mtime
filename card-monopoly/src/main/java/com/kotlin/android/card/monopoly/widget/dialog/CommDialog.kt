package com.kotlin.android.card.monopoly.widget.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.kotlin.android.card.monopoly.BuildConfig
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.event.EventSelectedSuit
import com.kotlin.android.card.monopoly.ext.showPocketCardDialog
import com.kotlin.android.card.monopoly.widget.dialog.view.*
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.bus.ext.observe

import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.UserManager

/**
 * 卡片大富翁通用对话框：
 *
 * Created on 2020/9/22.
 *
 * @author o.s
 */
class CommDialog : DialogFragment() {

    private val mDialogView by lazy {
        context?.run {
            CommDialogView(this)
        }
    }

    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private val mSingleTextContentView by lazy { SingleTextContentView(requireContext()) }
    private val mInputSingleTextContentView by lazy { InputSingleTextContentView(requireContext()) }
    private val mInputMultiTextContentView by lazy { InputMultiTextContentView(requireContext()) }
    private val mLinkTextContentView by lazy { LinkTextContentView(requireContext()) }
    private val mBuyProdCardContentView by lazy { BatchBuyPropCardDialogView(requireContext()) }
    private val mAuctionContentView by lazy {
        AuctionContentView(requireContext()).apply {
            action = {
                when (it) {
                    Constants.TYPE_CARD -> {
                        selectCard(this)
                    }
                    Constants.TYPE_SUIT -> {
//                        showSearchDialog(this)
                        mProvider?.startSuitSelectedActivity(
                            from = 2,
                            userId = UserManager.instance.userId,
                            suitC = "14",
                        )
                    }
                }

            }
        }
    }

//    private fun showSearchDialog(view: AuctionContentView) {
//        hide()
//        showSearchCardDialog(false, {
//            show()
//        }) {
//            val card =
//                Card(cardId = it.suit?.suitId ?: 0L, cardCover = it.suit?.suitCover, type = 3L)
//            view.card = card
//        }
//    }

    private fun selectCard(view: AuctionContentView) {
        hide()
        showPocketCardDialog(
            style = PocketCardDialog.Style.CARD_AND_PROP_CARD,
            dismiss = {
                show()
            }
        ) {
            it?.firstOrNull()?.apply {
                if (isPropCard) {
                    view.propCard = PropCard(
                        toolId = cardId,
                        toolName = cardName,
                        toolCover = cardCover
                    )
                } else {
                    view.card = this
                }
            }
        }
    }

    private val mAddFriendContentView by lazy { AddFriendContentView(requireContext()) }

    var dismiss: (() -> Unit)? = null

    var close: (() -> Unit)? = null

    var event: ((data: Data?) -> Unit)? = null

    var style: Style = Style.COMMON
        set(value) {
            field = value
            notifyChange()
        }

    var data: Data? = null
        set(value) {
            field = value
            notifyChange()
        }

    fun show() {
        dialog?.show()
    }

    fun hide() {
        dialog?.hide()
    }

    override fun setCancelable(cancelable: Boolean) {
        super.setCancelable(cancelable)
        dialog?.setCanceledOnTouchOutside(cancelable)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismiss?.invoke()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {
            decorView.setPadding(0, 0, 0, 0)
            setLayout(WindowManager.LayoutParams.MATCH_PARENT,  WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawable(null)
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mDialogView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        immersive().transparentStatusBar()
        initAuctionEvent()
        notifyChange()
    }

    private fun initAuctionEvent() {
        (context as? AppCompatActivity)?.apply {
            observe(EventSelectedSuit::class.java) {
                if (it.from == 2) {
                    val selectedSuit = it.suit
                    val card = Card(cardId = selectedSuit.suitId, cardCover = selectedSuit.suitCover, type = 3L)
                    mAuctionContentView.card = card
                    mAuctionContentView.suit = it.suit
                }
            }
        }
    }

    private fun notifyChange() {
        mDialogView?.apply {
            data?.title?.apply {
                setTitle(this)
            } ?: setTitle(style.title)
            positive(style.positive) {
                ensurePositive {
                    event?.invoke(data)
                    if (!BuildConfig.DEBUG || style != Style.AUCTION) {
                        dismissAllowingStateLoss()
                    }
                }
            }
            negative(style.negative) {
                close?.invoke()
                dismissAllowingStateLoss()
            }
            close {
                close?.invoke()
                dismissAllowingStateLoss()
            }

            when (style) {
                Style.BIDDING -> {
                    setContextView(mInputMultiTextContentView)
                    mInputMultiTextContentView.apply {
                        unit = getString(R.string.g)
                        data?.let {
                            text = it.biddingPrice.toString()
                            label1 = it.label1
                            label2 = it.label2
                        }
                    }
                }
                Style.BUY_COUNT -> {
                    setContextView(mBuyProdCardContentView)
                    mBuyProdCardContentView.apply {
                        data?.let {
                            if (it.buyCount != 0L) {
                                this.text = it.buyCount.toString()
                            } else {
                                this.text = ""
                            }
                            this.mPrice = it.biddingPrice
                            this.label1 = "合计: ".toSpan().append(
                                (it.biddingPrice * it.buyCount).toString().toSpan()
                                    .toColor(color = getColor(R.color.color_ff5a36))
                            )
                                .append("金币")
                        }
                    }
                }
                Style.BUY -> {
                    setContextView(mInputMultiTextContentView)
                    mInputMultiTextContentView.apply {
                        unit = getString(R.string.zhang)
                        data?.let {
                            text = it.buyCount.toString()
                            label1 = it.label1
                            label2 = it.label2
                        }
                    }
                }
                Style.NO_DEAL -> {
                    setContextView(mInputSingleTextContentView)
                    mInputSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.AUCTION -> {
                    setContextView(mAuctionContentView)
                }
                Style.DISCARD_FAILED -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.DISCARD_SUCCESS -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.LAUNCH_DEAL_SUCCESS -> {
                    setContextView(mLinkTextContentView)
                    mLinkTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                        action = {
                            // TODO 去口袋页面
                            data?.actionEvent = it
                            event?.invoke(data)
                            dismissAllowingStateLoss()
                        }
                    }
                }
                Style.DEAL_SUCCESS -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.ADD_FRIEND -> {
                    setContextView(mAddFriendContentView)
                    mAddFriendContentView.apply {
                        data?.let {
                            it.avatarUrl?.apply {
                                avatarUrl = this
                            }
                            nickName = it.nickName
                        }
                    }
                }
                Style.REFUSE_TO_ADD_FRIEND -> {
                    setContextView(mInputSingleTextContentView)
                    mInputSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.SYNTHESIS_FAILED -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.UNLOCK_COFFER -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.UNLOCK_COFFER_POSITION -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.DRAW_BOX -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                Style.SUBMIT_BUY,
                Style.COMMON_CANCEL -> {
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
                else -> {
                    mSingleTextContentView.messageGravity = Gravity.CENTER_HORIZONTAL
                    setContextView(mSingleTextContentView)
                    mSingleTextContentView.apply {
                        data?.let {
                            message = it.message
                        }
                    }
                }
            }
        }
    }

    private fun ensurePositive(allow: () -> Unit) {
        when (style) {
            Style.BIDDING -> {
                mInputMultiTextContentView.apply {
                    data?.let {
                        if (number < it.biddingPrice) {
                            showToast("请输入大于 ${it.biddingPrice} 的整数竞价")
                        } else {
                            it.biddingPrice = number
                            allow.invoke()
                        }
                    }
                }
            }
            Style.BUY_COUNT -> {
                mBuyProdCardContentView.apply {
                    data?.let {
                        if (text?.isEmpty() == true || (text?.toLong() ?: 0L) < 1L) {
                            showToast("购买数量不正确")
                        } else {
                            it.buyCount = (text?.toLong() ?: 0L)
                            allow.invoke()
                        }
                    }
                }
            }
            Style.BUY -> {
                mInputMultiTextContentView.apply {
                    data?.let {
                        if (number < it.buyCount) {
                            showToast("请输入购买数量")
                        } else {
                            it.buyCount = number
                            allow.invoke()
                        }
                    }
                }
            }
            Style.AUCTION -> {
                mAuctionContentView.apply {
                    data?.let {
                        when {
                            (card == null && propCard == null) -> {
                                showToast("请选择需要拍卖的卡片")
                            }
                            startPrice <= 0L -> {
                                showToast("请输入底价")
                            }
                            fixPrice <= 0L -> {
                                showToast("请输入一口价")
                            }
                            fixPrice < startPrice -> {
                                showToast("一口价应大于底价")
                            }
                            else -> {
                                it.card = card
                                it.toolCard = propCard
                                it.suit = suit
                                it.startPrice = startPrice
                                it.fixPrice = fixPrice
                                it.timeLimited = timeLimited
                                allow.invoke()
                            }
                        }
                    }
                }
            }
            Style.NO_DEAL,
            Style.REFUSE_TO_ADD_FRIEND -> {
                mInputSingleTextContentView.apply {
                    data?.let {
                        it.postscript = postscript
                        allow.invoke()
                    }
                }
            }
            Style.ADD_FRIEND -> {
                mAddFriendContentView.apply {
                    data?.let {
                        it.postscript = postscript
                        allow.invoke()
                    }
                }
            }
            else -> {
                allow.invoke()
            }
        }
    }

    private fun syncData() {
        when (style) {
            Style.BIDDING -> {
                mInputMultiTextContentView.apply {
                    data?.let {
                        it.biddingPrice = number
                    }
                }
            }
            Style.BUY -> {
                mInputMultiTextContentView.apply {
                    data?.let {
                        it.buyCount = number
                    }
                }
            }
            Style.AUCTION -> {
                mAuctionContentView.apply {
                    data?.let {
                        it.card = card
                        it.toolCard = propCard
                        it.suit = suit
                        it.startPrice = startPrice
                        it.fixPrice = fixPrice
                        it.timeLimited = timeLimited
                    }
                }
            }
            Style.NO_DEAL,
            Style.REFUSE_TO_ADD_FRIEND -> {
                mInputSingleTextContentView.apply {
                    data?.let {
                        it.postscript = postscript
                    }
                }
            }
            Style.ADD_FRIEND -> {
                mAddFriendContentView.apply {
                    data?.let {
                        it.postscript = postscript
                    }
                }
            }
            else -> {
            }
        }
    }

    /**
     * 卡片大富翁通用对话框样式（可扩展）
     */
    enum class Style(val title: String, var positive: String?, var negative: String?) {
        COMMON("", "确定", null),
        COMMON_CANCEL("", "确定", "取消"),
        BIDDING("竞  价", "确定", "取消"),
        BUY_COUNT("", "确定", "取消"),
        BUY("购  买", "确定", "取消"),
        NO_DEAL("谢绝交易", "确定", "取消"),
        AUCTION("拍  卖", "拍卖", "取消"),
        DISCARD_FAILED("丢弃失败", "确定", null),
        DISCARD_SUCCESS("丢弃成功", "确定", null),
        LAUNCH_DEAL_SUCCESS("发起交易成功", "确定", null),
        DEAL_SUCCESS("交易成功", "确定", null),
        ADD_FRIEND("添加卡友", "发送", "取消"),
        REFUSE_TO_ADD_FRIEND("拒绝添加卡友", "确定", "取消"),
        SYNTHESIS_FAILED("合成套装有误", "确定", null),
        UNLOCK_COFFER("解锁保险箱", "解锁", "取消"),
        UNLOCK_COFFER_POSITION("解锁空位", "解锁", "取消"),
        DRAW_BOX("领取宝箱", "确定", "取消"),
        SUBMIT_BUY("确认购买", "确定", "取消")
    }

    data class Data(
        var message: CharSequence? = null, // 消息
        var label1: CharSequence? = null, // 标签1
        var label2: CharSequence? = null, // 标签2
        var avatarUrl: String? = null, // 用户头像url
        var nickName: String? = null, // 昵称
        var postscript: String? = "", // 附言
        var biddingPrice: Long = 0L, // 竞价出价
        var buyCount: Long = 0L, // 购买数量
        var card: Card? = null, // 卡片Id 【cardId与toolCardId互斥，只能一个有值】
        var toolCard: PropCard? = null, // 道具卡Id 【cardId与toolCardId互斥，只能一个有值】
        var suit: Suit? = null, // 套装
        var startPrice: Long? = null, // 底价
        var fixPrice: Long? = null, // 一口价
        var timeLimited: Long? = null, // 拍卖时限： 2小时， 8小时， 16小时
        var title: CharSequence? = null, // title
    ) {
        /**
         * 发起交易成功点击事件类型
         */
        var actionEvent: LinkTextContentView.ActionEvent? = null
    }
}