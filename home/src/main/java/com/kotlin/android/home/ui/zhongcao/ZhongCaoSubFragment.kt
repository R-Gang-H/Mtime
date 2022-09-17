package com.kotlin.android.home.ui.zhongcao

import android.os.Bundle
import com.kotlin.android.community.card.component.item.BaseCardFragment
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.databinding.FagCommonCardListBinding
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete

/**
 * 首页-推荐-种草-子tab页面
 */
class ZhongCaoSubFragment : BaseCardFragment<ZhongCaoSubViewModel, FagCommonCardListBinding>(),
    MultiStateView.MultiStateListener {

    companion object {
        const val KEY_SUB_TYPE_ID = "key_sub_type_id"

        fun newInstance(subTypeId: Long): ZhongCaoSubFragment {
            val args = Bundle()
            args.putLong(KEY_SUB_TYPE_ID, subTypeId)
            val fragment = ZhongCaoSubFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var mAdapter: MultiTypeAdapter? = null
    private val subTypeId by lazy { arguments?.getLong(KEY_SUB_TYPE_ID).orZero() }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(mRecyclerView)
                .setOnClickListener(::onBinderClick)
            mMultiStateView.setMultiStateListener(this@ZhongCaoSubFragment)
            mRefreshLayout.setOnRefreshListener {
                mViewModel?.loadData(true, subTypeId)
            }
            mRefreshLayout.setOnLoadMoreListener {
                mViewModel?.loadData(false, subTypeId)
            }
        }
    }

    override fun onLoginStateChanged() {
        initData()
    }

    override fun startObserve() {
        super.startObserve()

        observeLiveData(mViewModel?.uiState) {
            mBinding?.apply {
                it.apply {
                    mRefreshLayout.complete(this)
                    mMultiStateView.complete(this)

                    success?.items?.apply {
                        if (isRefresh) {
                            mAdapter?.notifyAdapterDataSetChanged(this)
                        } else {
                            mAdapter?.notifyAdapterAdded(this)
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }
}