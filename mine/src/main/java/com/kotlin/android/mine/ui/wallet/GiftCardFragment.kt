package com.kotlin.android.mine.ui.wallet

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mine.BR
import com.kotlin.android.mine.R
import com.kotlin.android.mine.WALLET_CARD_ADD
import com.kotlin.android.mine.databinding.FragGiftCardBinding
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_collection.collectRv
import kotlinx.android.synthetic.main.frag_collection.refreshLayout
import kotlinx.android.synthetic.main.frag_collection.stateView
import kotlinx.android.synthetic.main.frag_gift_card.*
import org.jetbrains.anko.find

/**
 * create by WangWei on 2020/10/25
 * description:礼品卡
 */
class GiftCardFragment : BaseVMFragment<WalletViewModel, FragGiftCardBinding>() {
    companion object {
        fun newInstance(): GiftCardFragment = GiftCardFragment()
    }

    private var mIsInitData: Boolean = false
    private var mAdapter: MultiTypeAdapter? = null
    var dialogFragment: CouponAddDialogFragment? = null

    override fun initVM(): WalletViewModel = viewModels<WalletViewModel>().value

    override fun initView() {
        refreshLayout?.setOnLoadMoreListener {
            loadData(true)
        }
        refreshLayout?.setOnRefreshListener {
            loadData(false)
        }
        tv_add_card.onClick {
            dialogFragment = CouponAddDialogFragment.newInstance()
            dialogFragment?.type = WALLET_CARD_ADD
            dialogFragment?.walletViewModel = mViewModel
            dialogFragment?.show(childFragmentManager, "card_add")
        }
    }

    override fun onResume() {
        super.onResume()
        if (mIsInitData.not()) {
            refreshLayout?.autoRefresh()
        }
    }

    override fun initData() {
        mBinding?.setVariable(BR.data, mViewModel)
        mAdapter = createMultiTypeAdapter(collectRv, LinearLayoutManager(context))
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
        mViewModel?.loadGiftCards(isMore)
    }

    private fun loadComplete() {
        refreshLayout?.finishRefresh()
        refreshLayout?.finishLoadMore()
    }

    override fun startObserve() {
        mViewModel?.giftCardState?.observe(this) {
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
                            stateView.getView(MultiStateView.VIEW_STATE_EMPTY)?.find<View>(R.id.emptyIv)?.visibility = View.GONE
                            stateView.setEmptyResource(0, R.string.card_tip)
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
        mViewModel?.addCardState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.run {
                    if (this.success) {
                        dialogFragment?.dismiss()
                        loadData(false)
                    }
                    else showToast(this.msg)

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