package com.kotlin.android.card.monopoly.ui.store

import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.StorePropItemBinder
import com.kotlin.android.card.monopoly.databinding.FragStorePropBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.monopoly.PropCard
import com.kotlin.android.app.data.entity.monopoly.PropList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.SafeLinearLayoutManager
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.notifyBinderChanged
import com.kotlin.android.widget.multistate.MultiStateView
import kotlinx.android.synthetic.main.frag_store_prop.*

/**
 * @desc 道具卡页面
 * @author zhangjian
 * @date 2020/9/19 15:02
 */
class PropFragment : BaseVMFragment<CardMonopolyApiViewModel, FragStorePropBinding>(), MultiStateView.MultiStateListener {

    private val mAdapter: MultiTypeAdapter? by lazy {
        activity?.let {
            createMultiTypeAdapter(rvProp, SafeLinearLayoutManager(it))
        }
    }
    private var mListData: ArrayList<StorePropItemBinder> = ArrayList()
    private var mPropList: PropList? = null

    override fun initVM(): CardMonopolyApiViewModel {
        return viewModels<CardMonopolyApiViewModel>().value
    }

    override fun initView() {
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        loadData()
    }

    override fun startObserve() {
        mViewModel?.propCardStoreUiState?.observe(this, {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mPropList = this
                    updateUI()
                }

                if (isEmpty) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        })

        mViewModel?.buyPropCardUiState?.observe(this, {
            it?.apply {
                // 购买成功后加载道具卡列表，再显示loading吧，串行请求loading框会多次弹起
//                showProgressDialog(showLoading)

                success?.apply {
                    showToast(this.bizMessage)
                    if (this.bizCode == 1L) {
                        loadData()
                    }
                }

                error?.apply {
                    showToast(getString(R.string.str_fail))
                }
            }
        })

        mViewModel?.buyBatchPropCardUiState?.observe(this, {
            it?.apply {
                // 购买成功后加载道具卡列表，再显示loading吧，串行请求loading框会多次弹起
//                showProgressDialog(showLoading)

                success?.apply {
                    showToast(this.bizMessage)
                    if (this.bizCode == 1L) {
                        loadData()
                    }
                }

                error?.apply {
                    showToast(getString(R.string.str_fail))
                }
            }
        })
    }

    private fun loadData() {
        mViewModel?.loadPropCardList()
    }

    private fun updateUI() {
        notifyBinderChanged(
            adapter = mAdapter,
            binderList = mListData,
            dataList = mPropList?.toolCardList,
            isSame = { binder, data ->
                binder.data.toolId == data.toolId
            },
            syncDataAndNotify = { binder, data ->
                val notify = binder.data.todaySaleCount != data.todaySaleCount
                if (notify) {
                    binder.data.todaySaleCount = data.todaySaleCount
                }
                notify
            },
            createBinder = {
                StorePropItemBinder(it, activity, ::buyBatchToolsCard)
            }
        )
    }

    /**
     * 购买道具卡
     */
    private fun buyToolsCard(data: PropCard) {
        mViewModel?.buyPropCard(data.toolId)
    }

    /**
     * 批量购买道具卡
     */
    private fun buyBatchToolsCard(data: PropCard) {
        mViewModel?.buyBatchPropCard(data.toolId, data.buyNum)
    }

    override fun destroyView() {
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                loadData()
            }
        }
    }
}