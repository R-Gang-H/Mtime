package com.kotlin.android.card.monopoly.ui.robot

import android.content.Intent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.card.monopoly.KEY_CARD_MONOPOLY_ROBOT
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.ActCardRobotBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ext.showClearPocketDialog
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ext.showPocketCardDialog
import com.kotlin.android.card.monopoly.ids
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.userCardIds
import com.kotlin.android.card.monopoly.widget.card.OpenCardView
import com.kotlin.android.card.monopoly.widget.card.view.CheckCardView
import com.kotlin.android.card.monopoly.widget.card.view.SelectCardView
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.card.monopoly.widget.dialog.PocketCardDialog
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.monopoly.PocketCards
import com.kotlin.android.app.data.entity.monopoly.Robot
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.span.toBold
import com.kotlin.android.ktx.ext.span.toColor
import com.kotlin.android.ktx.ext.span.toSpan
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import kotlinx.android.synthetic.main.act_card_robot.*
import kotlinx.android.synthetic.main.act_card_robot.mainLayout
import kotlinx.android.synthetic.main.act_card_robot.titleBar

/**
 * 卡片大富翁套装列表页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_CARD_ROBOT)
class CardRobotActivity : BaseVMActivity<CardMonopolyApiViewModel, ActCardRobotBinding>() {

    private var mRobot: Robot? = null
        set(value) {
            field = value
            mOpenPocketCards = value?.openPocketCards
        }
    private var mOpenPocketCards: PocketCards? = null

    private var mRobotInfo: Robot? = null
        set(value) {
            field = value
            mRobotId = value?.robotId ?: 1L
        }
    private var mRobotId: Long = 1L

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            mRobotInfo = getParcelableExtra(KEY_CARD_MONOPOLY_ROBOT)
        }
    }

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
        mViewModel?.robotPocket(mRobotId)
    }

    override fun startObserve() {
        mViewModel?.apply {
            robotPocketUiState.observe(this@CardRobotActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        mRobot = this
                        updateUI()
                    }
                }
            }

            pickCardFromFriendUiState.observe(this@CardRobotActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    it.success?.apply {
                        when (bizCode) {
                            1L -> {
                                mOpenPocketCards = openPocketCards
                                updateUI()
                                showToast(bizMessage)
                            }
                            -4L -> {
                                showClearPocketDialog(bizMessage ?: getString(R.string.pocket_is_full))
                            }
                            else -> {
                                showToast(bizMessage)
                            }
                        }
                    }
                }
            }

            myPocketCardsUiState.observe(this@CardRobotActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        showPocketCardDialog(
                                data = PocketCardDialog.Data(
                                        cardList = cardList,
                                        friendId = mRobotId,
                                        isRobot = true
                                ),
                                selectModel = SelectCardView.SelectModel.MULTIPART
                        ) { cards ->
                            cards?.apply {
                                mViewModel?.discard(userCardIds(), mRobotId, true)
                            }
//                            cards?.firstOrNull()?.apply {
//                                mViewModel?.discard(userCardId ?: 0L, mRobotId, true)
//                            }
                        }
                    }
                }
            }

            discardUiState.observe(this@CardRobotActivity) {
                it?.apply {
                    showProgressDialog(showLoading)

                    success?.apply {
                        when (bizCode) {
                            1L -> {
//                                val message = getString(R.string.discard_success_).toSpan()
//                                        .append(
//                                                mRobotInfo?.robotName?.toSpan().toBold()?.toColor(color = getColor(R.color.color_20a0da))
//                                        ).append(
//                                                getString(R.string._it_pocket)
//                                        )
//                                showCardMonopolyCommDialog(CommDialog.Style.DISCARD_SUCCESS, CommDialog.Data(message = message))
                                showToast(R.string.discard_succcess)
                                mOpenPocketCards = openPocketCards
                                updateUI()
                            }
                            -5L -> {
                                val message = getString(R.string.sorry_).toSpan()
                                        .append(
                                                mRobotInfo?.robotName?.toSpan().toBold()?.toColor(color = getColor(R.color.color_20a0da))
                                        ).append(
                                                getString(R.string._pocket_no_position)
                                        )
                                showCardMonopolyCommDialog(CommDialog.Style.DISCARD_FAILED, CommDialog.Data(message = message))
                            }
                            else -> {
                                showToast(bizMessage)
                            }
                        }
                    }
                }
            }
        }
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
            setTitle(getString(R.string.card_monopoly), alwaysShow = true) {

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
        titleView?.text = mRobotInfo?.robotName ?: getString(R.string.card_robot_name, mRobotId)
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        cardLayout?.setBackground(
            colorRes = android.R.color.white,
            cornerRadius = 6.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        openCardView?.apply {
            spec = CheckCardView.Spec.ROBOT
            title = getString(R.string.card_lost_to_ta_by_card_mate)
            showDiscardActionView()
            action = {
                when (it.type) {
                    OpenCardView.ActionType.PICKUP -> {
                        // 拾取/多个拾取，等待新接口
                        val ids = it.selectedCards.ids { userCardId ?: 0 }
                        mViewModel?.batchPickCardFromFriend(ids, mRobotId, true)
                    }
                    OpenCardView.ActionType.DISCARD -> {
                        // 丢卡给TA
                        showPocketCardDialog(
                                selectModel = SelectCardView.SelectModel.MULTIPART
                        ) { cards ->
                            cards?.apply {
                                mViewModel?.discard(userCardIds(), mRobotId, true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateUI() {
        openCardView?.apply {
            mOpenPocketCards?.apply {
                data = cardList
            }
        }
    }

}