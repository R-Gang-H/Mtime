package com.kotlin.android.card.monopoly.ui.store

import androidx.activity.viewModels
import androidx.fragment.app.FragmentTransaction
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.app.data.entity.monopoly.RewardInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.card_monopoly.ICardMonopolyProvider
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ActStoreBinding
import com.kotlin.android.card.monopoly.ext.showCardMonopolyCommDialog
import com.kotlin.android.card.monopoly.ext.showClearPocketDialog
import com.kotlin.android.card.monopoly.ext.showFunctionMenuDialog
import com.kotlin.android.card.monopoly.ids
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.card.monopoly.widget.dialog.CommDialog
import com.kotlin.android.card.monopoly.widget.dialog.view.OpenBoxView
import com.kotlin.android.card.monopoly.widget.nav.NavView
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.act_store.*

/**
 * 卡片大富翁套装详情页面：
 *
 * Created on 2020/9/7.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.CardMonopoly.PAGER_STORE)
class StoreActivity : BaseVMActivity<CardMonopolyApiViewModel, ActStoreBinding>() {
    private val mProvider by lazy {
        getProvider(ICardMonopolyProvider::class.java)
    }

    private var CONSTANT_BOX = 0
    private var CONSTANT_PROP = 1

    private var boxFragment: BoxFragment? = null
    private var propFragment: PropFragment? = null

    //是否是打卡宝箱的标识,用于刷新
    private var openBoxState: Boolean = false

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
        navView?.selectItem(CONSTANT_BOX)
    }

    override fun startObserve() {
        mViewModel?.drawBoxModelUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    when (bizCode) {
                        1L -> {
                            // 领取成功，回调并关闭对话框
                            showToast(bizMessage)
                            openBoxView?.hide()
                            boxFragment?.refreshData(openBoxState)
                            openBoxState = false

                        }
                        -4L, -6L -> {
                            showClearPocketDialog(
                                message = bizMessage ?: getString(R.string.pocket_is_full),
                                isCardMainPage = true
                            ) {
                                openBoxView?.hide()
                                mProvider?.startCardMainActivity(this@StoreActivity)
                            }
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }

                error?.apply {
                    openBoxView?.hide()
                }

                netError?.apply {
                    openBoxView?.hide()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        openBoxAnimaView?.recycle()
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
            setTitle(getString(R.string.card_store), alwaysShow = true) {

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
        mainLayout?.background = getShapeDrawable(
            colorRes = R.color.color_a2edff,
            endColorRes = R.color.color_ffffff
        )
        navView?.apply {
            setItems(NavView.Category.TREASURE, NavView.Category.PROP_CARD)
            style = NavView.Style.DOUBLE
            action = {
                showFragment(it)
            }
        }
        openBoxView?.apply {
            dismiss = {
                boxFragment?.refreshData(true)
            }
            action = {
                val ids = it?.ids { cardId }.orEmpty()
                if (ids.isNotEmpty()) {
                    data?.box?.apply {
                        mViewModel?.drawBox(position, ids)
                    }
                } else {
                    showCardMonopolyCommDialog(
                        style = CommDialog.Style.DRAW_BOX,
                        data = CommDialog.Data(message = getString(R.string.warning_not_pick_card)),
                        close = {
                            show()
                        }
                    ) {
                        data?.box?.apply {
                            mViewModel?.drawBox(position, "")
                        }
                    }
                }
            }
        }
    }

    /**
     * 显示开宝箱视图
     */
    fun showOpenBoxView(box: Box?, rewardInfo: RewardInfo?) {
        rewardInfo?.apply {
            openBoxView?.apply {
                openBoxState = box?.type == Constants.STORE_BOX_REWARD
                show()
                rewardInfo.position = 0
                data = OpenBoxView.Data(box = box, info = rewardInfo)
            }
        }
    }

    /**
     * 显示开宝箱动画页
     */
    fun showOpenBoxAnimView(box: Box?, info: RewardInfo?) {
        openBoxAnimaView?.apply {
            complete = {
                openBoxView?.apply {
                    openBoxState = box?.type == Constants.STORE_BOX_REWARD
                    show()
                    data = it
                }
            }
            data = OpenBoxView.Data(box = box, info = info)
        }
    }

    private fun showFragment(type: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        hideFragment(transaction)
        when (type) {
            CONSTANT_BOX -> {
                if (boxFragment == null) {
                    boxFragment = BoxFragment()
                    transaction.add(R.id.frameContainer, boxFragment!!)
                }
                transaction.show(boxFragment!!)
            }
            CONSTANT_PROP -> {
                if (propFragment == null) {
                    propFragment = PropFragment()
                    transaction.add(R.id.frameContainer, propFragment!!)
                }
                transaction.show(propFragment!!)
            }
        }
        transaction.commit()
    }

    private fun hideFragment(transaction: FragmentTransaction) {
        boxFragment?.let { transaction.hide(it) }
        propFragment?.let { transaction.hide(it) }
    }

}