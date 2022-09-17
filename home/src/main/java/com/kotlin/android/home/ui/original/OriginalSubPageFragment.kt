package com.kotlin.android.home.ui.original

import android.os.Bundle
import androidx.lifecycle.Observer
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.home.databinding.FagOriginalSubPageBinding
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 * 
 * 首页-时光原创-子列表页面
 */
class OriginalSubPageFragment : BaseVMFragment<OriginalSubPageViewModel, FagOriginalSubPageBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {
    
    companion object {
        const val KEY_TYPE = "key_type"
        
        fun newInstance(type: String): OriginalSubPageFragment{
            val args = Bundle()
            args.putString(KEY_TYPE, type)
            val fragment = OriginalSubPageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var mAdapter: MultiTypeAdapter
    private val rcmdTagsFilter by lazy { arguments?.getString(KEY_TYPE).orEmpty() }

    override fun initView() {
        mBinding?.apply {
            mRefreshLayout.setOnRefreshLoadMoreListener(this@OriginalSubPageFragment)
            mMultiStateView.setMultiStateListener(this@OriginalSubPageFragment)
            mAdapter = createMultiTypeAdapter(mOriginalRecyclerView)
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        mViewModel?.run {
            uiState.observe(this@OriginalSubPageFragment, Observer {
                it.apply {
                    mBinding?.mRefreshLayout?.complete(this)
                    mBinding?.mMultiStateView?.complete(this)
                    
                    success?.items?.apply {
                        if (isRefresh) {
                            mAdapter.notifyAdapterDataSetChanged(this)
                        } else {
                            mAdapter.notifyAdapterAdded(this)
                        }
                    }
                }
            })
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadContentData(false, rcmdTagsFilter)
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadBannerAndContentData(rcmdTagsFilter)
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.mRefreshLayout?.autoRefresh()
            }
        }
    }
}