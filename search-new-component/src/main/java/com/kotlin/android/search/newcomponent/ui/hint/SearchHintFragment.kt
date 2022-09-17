package com.kotlin.android.search.newcomponent.ui.hint

import android.view.View
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHintBinder
import com.kotlin.android.search.newcomponent.databinding.FragSearchHintBinding
import com.kotlin.android.search.newcomponent.ui.ISearchHandler
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/20
 */
class SearchHintFragment : BaseVMFragment<SearchHintViewModel, FragSearchHintBinding>()
        , MultiStateView.MultiStateListener {

    companion object {
        fun newInstance() = SearchHintFragment()
    }

    private var mKeyword: String = ""
    private var mSearchHandler: ISearchHandler? = null
    private var mAdapter: MultiTypeAdapter? = null

    override fun initView() {
        activity?.run {
            if (this is ISearchHandler) {
                mSearchHandler = this
            }
        }

        mBinding?.run {
            mAdapter = createMultiTypeAdapter(mSearchHintRv)
                .setOnClickListener(::onBinderClick)
            mMultiStateView.setMultiStateListener(this@SearchHintFragment)
            mMultiStateView.setEmptyResource(
                    resId = R.drawable.ic_empty,
                    resid = R.string.search_newcomponent_search_hint_empty)
        }
    }

    override fun initData() {

    }

    fun searchHint(keyword: String) {
        mAdapter?.notifyAdapterClear()
        mKeyword = keyword
        mViewModel?.loadHint(
                mKeyword,
                GlobalDimensionExt.getDigitsCurrentCityId(),
                GlobalDimensionExt.getLongitude(),
                GlobalDimensionExt.getLatitude()
        )

    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it.run {
                mBinding?.run {
                    mAdapter?.notifyAdapterDataSetChanged(success)

//                    if(isEmpty) {
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
//                    }
//
//                    error?.apply {
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
//                    }
//
//                    netError?.apply {
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
//                    }
                }
            }
        }
    }

    override fun destroyView() {

    }

    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is SearchHintBinder -> {
                mSearchHandler?.execSearchResult(binder.item)
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
//        when (viewState) {
//            MultiStateView.VIEW_STATE_ERROR,
//            MultiStateView.VIEW_STATE_NO_NET -> {
//                mKeyword.run {
//                    mViewModel?.loadHint(this)
//                }
//            }
//        }
    }
}