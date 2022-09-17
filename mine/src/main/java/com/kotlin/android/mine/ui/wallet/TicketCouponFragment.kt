package com.kotlin.android.mine.ui.wallet

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.mine.WALLET_COUPON_ALL
import com.kotlin.android.mine.databinding.FragCouponBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import kotlinx.android.synthetic.main.frag_collection.refreshLayout
import kotlinx.android.synthetic.main.frag_collection.stateView
import kotlinx.android.synthetic.main.frag_coupon.*
import org.jetbrains.anko.find

/**
 * create by WangWei on 2020/10/24
 * description:优惠券
 */
class TicketCouponFragment : BaseVMFragment<WalletViewModel, FragCouponBinding>() {
    var type = 0L// 0 是显示全部 1是已使用 2是已过期  （状态描述，已过期、可用、已使用）

    fun newInstance(type: Long): TicketCouponFragment? {
        val f = TicketCouponFragment()
        f.arguments = bundle(type)
        return f
    }

    fun bundle(type: Long): Bundle {
        return Bundler()
                .putLong("type", type)
                .get()
    }

    private var mIsInitData: Boolean = false
    private var mAdapter: MultiTypeAdapter? = null

    override fun initVM(): WalletViewModel = viewModels<WalletViewModel>().value

    var dialogFragment: CouponAddDialogFragment? = null
    override fun initView() {
        ShapeExt.setShapeCorner2Color2Stroke(tv_coupon_record, corner = 20, strokeWidth = 2, strokeColor = R.color.color_1ab2e1)
        ShapeExt.setShapeCorner2Color(tv_add_coupon, corner = 20,  solidColor = R.color.color_1ab2e1)

        refreshLayout?.setOnLoadMoreListener {
            loadData(true)
        }
        refreshLayout?.setOnRefreshListener {
            loadData(false)
        }
        tv_coupon_record.onClick {
            getProvider(IMineProvider::class.java)
                    ?.startMyCouponUsedRecordActivity(context as Activity)
        }
        tv_add_coupon.onClick {
            dialogFragment = CouponAddDialogFragment.newInstance()
            dialogFragment?.walletViewModel = mViewModel
            dialogFragment?.show(childFragmentManager, "coupon_add")
        }
    }

    override fun onResume() {
        super.onResume()
        if (mIsInitData.not()) {
            refreshLayout?.autoRefresh()
        }
    }

    override fun initData() {
        if (arguments != null) {
            type = arguments?.getLong("type", 0)!!
        }
        if (type == WALLET_COUPON_ALL) {
            tv_coupon_record.visible()
            tv_add_coupon.visible()
        } else {
            tv_coupon_record.gone()
            tv_add_coupon.gone()
        }
        mBinding?.setVariable(BR.data, mViewModel)
        mAdapter = createMultiTypeAdapter(recycle, LinearLayoutManager(context))
        mAdapter?.setOnClickListener { view, binder ->

        }
        stateView?.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
                    loadData(false)
                }
            }

        })
    }

    private fun loadData(isMore: Boolean) {
        mViewModel?.loadCoupons(isMore, type)
    }

    private fun loadComplete() {
        refreshLayout?.finishRefresh()
        refreshLayout?.finishLoadMore()
    }

    override fun startObserve() {
        mViewModel?.couponState?.observe(this) {
            mIsInitData = true
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    if (loadMore) {
                        mAdapter?.notifyAdapterAdded(this.list)
                    } else {
                        mAdapter?.notifyAdapterDataSetChanged(this.list)
                        if (this.list.isEmpty()) {
                            setContentState(MultiStateView.VIEW_STATE_EMPTY)
                            if (type == WALLET_COUPON_ALL) {
                                stateView.getView(MultiStateView.VIEW_STATE_EMPTY)?.find<View>(R.id.emptyIv)?.visibility = View.GONE
                                stateView.setEmptyResource(0, R.string.coupon_tip)
                            }
                        }
                    }
                    refreshLayout?.setNoMoreData(noMoreData)
                }

                netError?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果没有数据是显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
                error?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果页面上没有显示数据，需要显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
            }
        }
        mViewModel?.addCouponState?.observe(this) {
            mIsInitData = true
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.run {
                    if (this.success) {
                        showToast("添加成功")
                        dialogFragment?.dismiss()
                        loadData(false)
                    }
                    else showToast(this.error)
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
        stateView?.setViewState(state)
    }


    override fun destroyView() {
    }

}