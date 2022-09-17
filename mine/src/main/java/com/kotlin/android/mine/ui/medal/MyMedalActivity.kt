package com.kotlin.android.mine.ui.medal

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.binder.MedalBinder
import com.kotlin.android.mine.databinding.ActivityMyMedalBinding
import com.kotlin.android.mine.ui.widgets.dialog.MedalDetailDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_MY_MEDAL_ACTIVITY)
class MyMedalActivity : BaseVMActivity<MyMedalViewModel, ActivityMyMedalBinding>() {

    private val mAwardedAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(mBinding?.awardedGv!!, GridLayoutManager(this, 3)).apply {
            setOnClickListener { view, binder ->
                when (view.id) {
                    R.id.containerLl -> {
                        if (System.currentTimeMillis() - awardedLastClickTime < clickDelayTime) {
                            return@setOnClickListener
                        }
                        awardedLastClickTime = System.currentTimeMillis()
                        isAward = true
                        mViewModel?.getMedalDetail(((binder as MedalBinder).bean.medalId))
                    }
                }
            }
        }
    }

    private val mLossAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(mBinding?.lossGv!!, GridLayoutManager(this, 3)).apply {
            setOnClickListener { view, binder ->
                when (view.id) {
                    R.id.containerLl -> {
                        if (System.currentTimeMillis() - lossLastClickTime < clickDelayTime) {
                            return@setOnClickListener
                        }
                        lossLastClickTime = System.currentTimeMillis()
                        isAward = false
                        mViewModel?.getMedalDetail(((binder as MedalBinder).bean.medalId))
                    }
                }
            }
        }
    }

    private var isAward: Boolean = false
    private var awardedLastClickTime: Long = 0L
    private var lossLastClickTime: Long = 0L
    private val clickDelayTime = 500

    override fun initVM(): MyMedalViewModel = viewModels<MyMedalViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, themeStyle = ThemeStyle.IMMERSIVE)
            .autoSyncStatusBar(false)
            .allowShadow()
            .setState(State.REVERSE)
            .target(mBinding?.scrollView)
            .setTitle(
                titleRes = R.string.mine_title_medal
            )
            .addItem(
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back,
            ) {
                finish()
            }
    }

    override fun initView() {
        mBinding?.apply {
            awardedLl.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
            )
            lossLl.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
            )
            medalNum.text = getString(R.string.mine_medal_cumulative_awarded, "0")
        }
    }

    override fun initData() {
        mViewModel?.getMedalData()
    }

    @SuppressLint("StringFormatMatches")
    override fun startObserve() {
        mViewModel?.medalState?.observe(this) {
            it?.apply {
                success?.run {
                    mBinding?.apply {
                        awardedLl.visibility =
                            if (!ongoingMedalInfos.isNullOrEmpty()) View.VISIBLE else View.GONE
                        lossLl.visibility =
                            if (!completeMedalInfos.isNullOrEmpty()) View.VISIBLE else View.GONE
                        medalNum.text = getString(
                            R.string.mine_medal_cumulative_awarded,
                            "$medalCount"
                        )
                        containerRl.visibility = View.VISIBLE
                    }
                    mViewModel?.obtainOngoingMedalInfo(ongoingMedalInfos)
                        ?.let { it1 -> mAwardedAdapter.notifyAdapterAdded(it1) }
                    mViewModel?.obtainCompleteMedalInfo(completeMedalInfos)
                        ?.let { it1 -> mLossAdapter.notifyAdapterAdded(it1) }
                }
                netError?.run {
                    setContentState(MultiStateView.VIEW_STATE_NO_NET)
                }
                error?.run {
                    setContentState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        }

        mViewModel?.medalDetailState?.observe(this) {
            it?.apply {
                success?.run {
                    MedalDetailDialog(this, isAward).show(
                        supportFragmentManager,
                        MedalDetailDialog.TAG_MEDAL_DETAIL_DIALOG_FRAGMENT
                    )
                }
                netError?.run {
                    showToast(this)
                }
                error?.run {
                    showToast(this)
                }
            }
        }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.stateView?.setViewState(state)
    }
}