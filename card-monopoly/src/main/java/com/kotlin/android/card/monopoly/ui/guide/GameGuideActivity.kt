package com.kotlin.android.card.monopoly.ui.guide

import android.view.View
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActGameGuideBinding
import com.kotlin.android.card.monopoly.ext.*
import com.kotlin.android.card.monopoly.ext.showDigBoxDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.*
import com.kotlin.android.card.monopoly.widget.dialog.view.DealCardView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.*
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.app.router.path.RouterActivityPath
import kotlinx.android.synthetic.main.act_game_guide.*

/**
 * 卡片大富翁游戏介绍页面：
 *
 * Created on 2020/9/24.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_GAME_GUIDE)
class GameGuideActivity : BaseVMActivity<CardMonopolyApiViewModel, ActGameGuideBinding>() {

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        window.setBackgroundDrawable(null)
        initTitleView()
        initContentView()
    }

    override fun initNewData() {
        initData()
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD_STATUS_BAR)
            setState(State.REVERSE)
            addItem(
                drawableRes = R.drawable.ic_title_bar_back_light,
                reverseDrawableRes = R.drawable.ic_title_bar_back_dark
            ) {
                finish()
            }
            setTitle(getString(R.string.game_guide), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true,
            ) {
                showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
        }
    }

    private fun initContentView() {
    }

    fun bidding(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.BIDDING, CommDialog.Data(
                message = "已成功丢弃卡片到骑士oo7akm的口袋。",
                label1 = "出价",
                label2 = "*请输入大于 158 的整数",
                biddingPrice = 158L
        ))
    }

    fun buy(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.BUY, CommDialog.Data(
                message = "已成功丢弃卡片到骑士oo7akm的口袋。",
                label1 = "购买 保险箱卡",
                label2 = "*您已选择购买保险箱卡 1 张，将花费 1000G",
                buyCount = 1L
        ))
    }

    fun noDeal(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.NO_DEAL, CommDialog.Data(
                message = "您谢绝与 rannbbay 交易 国王归来 卡片。"
        ))
    }

    fun auction(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.AUCTION, CommDialog.Data(
                message = "已成功丢弃卡片到骑士oo7akm的口袋。"
        )) {
//            showSearchCardDialog (
//                    dismiss = {
//                        titleBar?.syncStatusBar()
//                    }
//            )
        }
    }

    fun discardFailed(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.DISCARD_FAILED, CommDialog.Data(
                message = "对不起 黑谁谁暗骑士oo7akm 的口袋已没有空位。"
        ))
    }

    fun discardSuccess(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.DISCARD_SUCCESS, CommDialog.Data(
                message = "已成功丢弃卡片到 骑士oo7akm 的口袋。"
        ))
    }

    fun launchDealSuccess(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.LAUNCH_DEAL_SUCCESS, CommDialog.Data(
                message = "你已成功向 这是一小小的baby 发起卡片交易。"
        ))
    }

    fun dealSuccess(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.DEAL_SUCCESS, CommDialog.Data(
                message = "你与 rainbow小baby 已成功交易！\n对方给你支付了 10 个金币。"
        ))
    }

    // https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1396521707,1973816781&fm=26&gp=0.jpg
    // https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1600837985344&di=bd4d9672eadf841c694a5ea7d6a0c41f&imgtype=0&src=http%3A%2F%2Fy2.ifengimg.com%2Fifengimcp%2Fpic%2F20140714%2F44c27a63d6dd1792db70_size236_w1440_h900.jpg
    fun addFriend(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.ADD_FRIEND, CommDialog.Data(
                message = "已成功丢弃卡片到骑士oo7akm的口袋。",
                nickName = "宝石夜空蓝",
                avatarUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1600837985344&di=bd4d9672eadf841c694a5ea7d6a0c41f&imgtype=0&src=http%3A%2F%2Fy2.ifengimg.com%2Fifengimcp%2Fpic%2F20140714%2F44c27a63d6dd1792db70_size236_w1440_h900.jpg"
        ))
    }

    fun refuseAddFriend(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.REFUSE_TO_ADD_FRIEND, CommDialog.Data(
                message = "你谢绝与 黑暗骑士oo7akm 加为好友。"
        ))
    }

    fun synthesisFailed(view: View) {
        showCardMonopolyCommDialog(CommDialog.Style.SYNTHESIS_FAILED, CommDialog.Data(
                message = "五张同一主题的卡片才能合成一个套装哦～"
        ))
    }

    fun suitDetail(view: View) {
        showSuitDetailDialog(Suit()) {

        }
    }

    fun suitUpgrade(view: View) {
        showSuitUpgradeDialog(UpgradeSuit()) {

        }
    }

    fun suitCompose(view: View) {
        showSuitComposeDialog(MixSuit()) {

        }
    }

    fun deal(view: View) {
        showDealDialog(DealCardView.Data(
                srcCard = Card(),
                desCard = Card()
        )) {

        }
    }

    fun usePropSuccess(view: View) {
        showUsePropDialog(UsePropDialog.Data(
                isSuccess = true,
                card = Card(cardName = "防盗卡"),
                message = "· 防盗卡抵抗打劫卡效果一次\n· 将会抵御打劫卡一次")) {

        }
    }

    fun usePropFailed(view: View) {
        showUsePropDialog(UsePropDialog.Data(
                isSuccess = false,
                card = Card(cardName = "防盗卡"),
                message = "· 防盗卡抵抗打劫卡效果一次\n· 将会抵御打劫卡一次")) {

        }
    }

    fun digBox(view: View) {
        showDigBoxDialog(Box(4L, "铂金宝箱")) {

        }
    }

    fun openBox(view: View) {
        showClearPocketDialog(getString(R.string.pocket_is_full))
    }

    fun card(view: View) {
        showCardDialog(CardDialog.Data()) {

        }
    }

    fun getCard(view: View) {
        showGetCardDialog(GetCardDialog.Data("", "对不起，你的口袋已满请清空口袋再试")) {

        }
    }

}