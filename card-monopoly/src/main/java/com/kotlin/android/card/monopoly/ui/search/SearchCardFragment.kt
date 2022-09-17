package com.kotlin.android.card.monopoly.ui.search

import android.view.View
import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.databinding.FragSearchBinding
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.search.SearchCardSuitView
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.monopoly.Card
import com.kotlin.android.app.data.entity.monopoly.Suit
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import kotlinx.android.synthetic.main.frag_search.*

/**
 * 搜索视图：根据卡片套装名称搜索卡片套装
 *
 * Created on 2020/9/17.
 *
 * @author o.s
 */
class SearchCardFragment : BaseVMDialogFragment<CardMonopolyApiViewModel, FragSearchBinding>() {

    var event: ((event: ActionEvent) -> Unit)? = null

    override fun initEnv() {
        dialogStyle = DialogStyle.FULL
        animation = R.style.RightDialogAnimation
        immersive = {
            immersive()
                .transparentStatusBar()
                .statusBarDarkFont(false)
        }
    }

    override fun initVM(): CardMonopolyApiViewModel = viewModels<CardMonopolyApiViewModel>().value

    override fun initView() {
        initTitleView()
        initContentView()
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.querySuitByCardUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    searchCardSuitView.data = suitList
                }
            }
        }

        mViewModel?.suitCardsUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    selectCardView?.data = cardList
                    suitImageView?.data = suitInfo
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
                dismissAllowingStateLoss()
            }
            setTitle(getString(R.string.search), alwaysShow = true) {

            }
            addItem(
                drawableRes = R.drawable.ic_title_bar_more_light,
                reverseDrawableRes = R.drawable.ic_title_bar_more_dark,
                isReversed = true,
            ) {
                activity?.showFunctionMenuDialog(
                    dismiss = {
                        syncStatusBar()
                    }
                )
            }
        }
    }

    fun initContentView() {
        mainLayout?.setBackground(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        contentLayout?.setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 6.dpF,
            direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
        )
        searchCardSuitView?.apply {
            stateChange = {
                when (it) {
                    SearchCardSuitView.State.EXPANDING -> {
                        showSearchResultView(false)
                    }
                    SearchCardSuitView.State.COLLAPSING -> {
                        showSearchResultView()
                    }
                }
            }
            searchAction = {
                if (it.event == 1) {
                    mViewModel?.querySuitByCard(it.keyword, isShowLoading = true)
                } else {
                    mViewModel?.querySuitByCard(it.keyword, isShowLoading = false)
                }
            }

            action = {
                mViewModel?.suitCards(it.suitId)
            }
        }

        selectCardView?.apply {
            action = {
                event?.invoke(ActionEvent(card = it, suit = suitImageView?.data))
                dismissAllowingStateLoss()
            }
        }
        suitImageView?.apply {
            setOnClickListener {
                event?.invoke(ActionEvent(suit = data))
                dismissAllowingStateLoss()
            }
        }
    }

    private fun showSearchResultView(isShow: Boolean = true) {
        val show = if (isShow) {
            View.VISIBLE
        } else {
            View.GONE
        }
        selectCardView?.visibility = show
        suitImageView?.visibility = show
    }

    data class ActionEvent(
        val card: Card? = null,
        val suit: Suit? = null
    )
}