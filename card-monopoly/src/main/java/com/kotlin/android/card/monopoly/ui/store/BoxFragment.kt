package com.kotlin.android.card.monopoly.ui.store

import androidx.fragment.app.viewModels
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.adapter.StoreBoxItemBinder
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.FragStoreBoxBinding
import com.kotlin.android.card.monopoly.ui.CardMonopolyApiViewModel
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.app.data.entity.monopoly.BoxList
import com.kotlin.android.app.data.entity.monopoly.RewardInfo
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.SafeLinearLayoutManager
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.notifyBinderChanged
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * @desc  展示宝箱页面
 * @author zhangjian
 * @date 2020/9/19 15:00
 */
class BoxFragment : BaseVMFragment<CardMonopolyApiViewModel, FragStoreBoxBinding>(), MultiStateView.MultiStateListener {

    private val mAdapter: MultiTypeAdapter? by lazy {
        activity?.let {
            mBinding?.rvBox?.let { recyclerView ->
                createMultiTypeAdapter(recyclerView, SafeLinearLayoutManager(it))
            }
        }
    }
    private var mListData: ArrayList<StoreBoxItemBinder> = ArrayList()
    private var mBoxList: BoxList? = null
    private var mBuyBox: Box? = null
    private var hasBoughtBox: Boolean = false
    private var isFirstRequest: Boolean = true

    override fun initVM(): CardMonopolyApiViewModel {
        return viewModels<CardMonopolyApiViewModel>().value
    }

    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun initData() {
        refreshData()
    }

    override fun startObserve() {
        mViewModel?.boxUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    hasBoughtBox = this.boughtBox != null
                    mBoxList = this
//                    updateUI()
                    val arr = boxToList(this)
                    mListData.clear()
                    arr.forEach {
                        mListData.add(StoreBoxItemBinder(it, ::buyBox))
                    }
                    mAdapter?.notifyAdapterClear()
                    mAdapter?.notifyAdapterDataSetChanged(mListData)
                    isFirstRequest = false
                }

                if (isEmpty) {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.buyCardBoxUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (bizCode == 1L) {
                        showOpenBoxView(rewardInfo)
//                        val cardReward = this.rewardInfo
//                        val cardData = CardDialog.Data(
//                            0, cardReward?.gold
//                                ?: 0L, cardReward?.cardList
//                        )
//                        showCardDialog(cardData, dismiss = {
//                            refreshData()
//                        })
                    } else {
                        showToast(this.bizMessage)
                    }
                }
            }
        }

        mViewModel?.openBoxUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    //购买成功,刷新列表
                    when (bizCode) {
                        1L -> {
                            showOpenBoxView(rewardInfo)
//                            rewardInfo?.apply {
//                                showCardDialog(CardDialog.Data(0, this.gold, this.cardList),
//                                    dismiss = {
//                                        refreshData()
//                                    }) {
//
//                                }
//                            }
                        }
                        else -> {
                            showToast(bizMessage)
                        }
                    }
                }
                error?.apply {
                    showToast("打开失败")
                }
            }
        }
    }

    fun refreshData(needRefresh:Boolean? = true) {
        if (needRefresh == true){
            mViewModel?.loadBoxList()
        }
    }


    private fun updateUI() {
        notifyBinderChanged(
            adapter = mAdapter,
            binderList = mListData,
            dataList = boxToList(mBoxList),
            isSame = { binder, data ->
                binder.data.cardBoxId == data.cardBoxId
                        && binder.data.type == data.type
            },
            syncDataAndNotify = { binder, data ->
                val notify = binder.data.remainCount != data.remainCount
                if (notify) {
                    binder.data.remainCount = data.remainCount
                }
                notify
            },
            createBinder = {
                StoreBoxItemBinder(it, ::buyBox)
            }
        )
    }

    /**
     * 显示开宝箱视图
     */
    private fun showOpenBoxView(rewardInfo: RewardInfo?) {
        val act = activity
        if (act is StoreActivity) {
            mBuyBox?.position = 0
//            act.showOpenBoxAnimView(mBuyBox, rewardInfo)
            act.showOpenBoxView(mBuyBox, rewardInfo)
        }
    }

    private fun boxToList(boxList: BoxList?): ArrayList<Box> {
        val list = ArrayList<Box>()
        boxList?.apply {
            activityBoxList?.forEach {
                it.type = Constants.STORE_BOX_ACTIVITY
                list.add(it)
            }
            boughtBox?.let {
                it.type = Constants.STORE_BOX_REWARD
                list.add(it)
            }
            commonBoxList?.forEach {
                it.type = Constants.STORE_BOX_NORMAL
                list.add(it)
            }
            limitBoxList?.forEach {
                it.type = Constants.STORE_BOX_LIMIT
                list.add(it)
            }
        }
        return list
    }

    /**
     * 购买宝箱
     */
    private fun buyBox(data: Box, flag: Boolean) {
        mBuyBox = data
        //绑定手机号才可以购买活动宝箱
        val state =
            (data.type == Constants.STORE_BOX_ACTIVITY && !UserManager.instance.hasBindMobile)
        if (state) {
            showToast(R.string.hint_please_bind_phone)
        } else {
            if (flag) {
                mViewModel?.openBox(0)
            } else {
                mViewModel?.buyCardBox(data.cardBoxId ?: 0)
            }
        }


    }

    override fun destroyView() {
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                refreshData()
            }
        }
    }
}