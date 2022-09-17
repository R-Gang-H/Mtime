package com.kotlin.android.mine.ui.member

import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.mine.R
import com.kotlin.android.mine.VIP_LUNCKY_DRAW
import com.kotlin.android.mine.binder.MemberGoodsBinder
import com.kotlin.android.mine.databinding.ActivityMemberCenterBinding
import com.kotlin.android.mine.ui.widgets.dialog.dismissExchangeDialog
import com.kotlin.android.mine.ui.widgets.dialog.getExchangeDialog
import com.kotlin.android.mine.ui.widgets.dialog.showExchangeDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.sdk.IJsSDKProvider
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_member_center.*

/**
 * 会员中心
 */
@Route(path = RouterActivityPath.Mine.PAGE_MEMBER_CENTER_ACTIVITY)
class MemberCenterActivity : BaseVMActivity<MemberCenterViewModel, ActivityMemberCenterBinding>() {
    private val adapter by lazy {
        createMultiTypeAdapter(memeberRv).apply {
            setOnClickListener(::handleListBinder)
        }
    }
    private var isInit = true//是否是第一次请求

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarDarkFont(true)
    }

    override fun initVM(): MemberCenterViewModel {
        return viewModels<MemberCenterViewModel>().value
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle(R.string.mine_member_center)
                .setRightTextColor(R.color.color_20a0da).setRightTextAndClick(R.string.mine_member_draw_lottery) {
//            跳转到抽奖页面
                    getProvider(IJsSDKProvider::class.java)?.startH5Activity(
                            BrowserEntity(title = getString(R.string.mine_member_draw_lottery), url = VIP_LUNCKY_DRAW,true))
                }
                .create()
    }

    override fun initView() {
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                    loadHomeCenterData()
                }
            }
        })
    }

    override fun initData() {
//        loadHomeCenterData(isInit)
    }

    private fun loadHomeCenterData(showLoading: Boolean = true) {
        mViewModel?.loadHomeCenterData(showLoading)
    }

    override fun startObserve() {
//        会员中心主页信息和M都兑换列表
        mViewModel?.memberCenterHomeStatus?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    mViewModel?.getBinderList(this)?.apply {
                        adapter.notifyAdapterDataSetChanged(this, false)
                    }
                    isInit = false
                }
                error?.apply {
                    if (isInit) {
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                        isInit = false
                    }
                }
                netError?.apply {
                    if (isInit) {
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                        isInit = false
                    }
                }
                if (isEmpty && isInit) {
                    setContentState(MultiStateView.VIEW_STATE_EMPTY)
                }
            }
        })

//        兑换商品
        mViewModel?.exchangeGoodsStatus?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (this.bindCode == 1L) {//兑换成功
                        showToast(R.string.mine_exchange_goods_success)
                    } else {
                        showToast(this.bindMsg.orEmpty())
                    }
//                            刷新数据
                    loadHomeCenterData()
//                        弹框消失
                    dismissExchangeDialog()

                }
                netError?.showToast()
                error?.showToast()
            }

        })
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        stateView.setViewState(state)
    }

    override fun onResume() {
        super.onResume()
        if (getExchangeDialog()?.dialog?.isShowing == true) {//已经显示了对话框返回不刷新数据
        } else {
//            如果不是第一次请求数据，需要显示加载框
            loadHomeCenterData(isInit)
        }

    }

    private fun handleListBinder(view: View, binder: MultiTypeBinder<*>) {
        when (view.id) {
            R.id.exchangeTv -> {//兑换按钮,显示兑换按钮
                if (binder is MemberGoodsBinder) {
                    showExchangeDialog(binder.bean) {
//                        请求接口
                        mViewModel?.exchangeGoods(it?.id ?: 0L, it?.mNeedNum ?: 0L)
                    }
                }
            }
        }
    }


}