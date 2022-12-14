package com.kotlin.android.search.newcomponent.ui.history

import android.text.TextUtils
import android.view.View
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.core.ext.removeSpKey
import com.kotlin.android.ktx.ext.handleJson
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHistoryBinder
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHistoryItemBinder
import com.kotlin.android.search.newcomponent.adapter.binder.SearchHotSearchListItemBinder
import com.kotlin.android.search.newcomponent.adapter.binder.SearchRcmdReviewBinder
import com.kotlin.android.search.newcomponent.bean.HotSearchBean
import com.kotlin.android.search.newcomponent.databinding.FragSearchHistoryBinding
import com.kotlin.android.search.newcomponent.ui.ISearchHandler
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/17
 */
class SearchHistoryFragment : BaseVMFragment<SearchHistoryViewModel, FragSearchHistoryBinding>() {

    companion object {
        fun newInstance() = SearchHistoryFragment()
    }

    private var mAdapter: MultiTypeAdapter? = null
    private var mSearchHandler: ISearchHandler? = null

    override fun initView() {
        activity?.let {
            if (it is ISearchHandler) {
                mSearchHandler = it
            }
        }

        mBinding?.run {
            mAdapter = createMultiTypeAdapter(mSearchHistoryRv)
                    .setOnClickListener(::onBinderClick)
        }
    }

    override fun initData() {
        mViewModel?.loadHotSearch()
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it.run {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.run {
                    mAdapter?.notifyAdapterDataSetChanged(this)
                }

                error?.apply {
                    showToast(error)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }

        mViewModel?.uiHotWordState?.observe(this) {
            it.run {
                success?.run {
                    mSearchHandler?.callbackHotWord(this)
                }
            }
        }
    }

    override fun destroyView() {

    }

    /**
     * ???MultiTypeBinder???????????????????????????
     * ??????????????????Adapter??????
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is SearchHistoryBinder -> {
                onSearchHistoryBinderClick(view, binder)
            }
            is SearchHistoryItemBinder -> {
                mSearchHandler?.execSearchResult(binder.item)
            }
            is SearchRcmdReviewBinder -> {
                onReviewBinderClick(view, binder)
            }
            is SearchHotSearchListItemBinder -> {
                onHotSearchBinderClick(view, binder)
            }
        }
    }

    /**
     * ????????????????????????
     */
    private fun onHotSearchBinderClick(view: View, binder: SearchHotSearchListItemBinder) {
        when (view.id) {
            R.id.mHotSearchListItemRoot -> {
                saveSearchHistory(binder.item.title)
                when (binder.item.type) {
                    HotSearchBean.HOT_TYPE_FILM -> {
//                        mViewModel?.searchPopularClick(1, 0, binder.item.id.toString()) //??????????????????????????????????????????????????????
                        getProvider(ITicketProvider::class.java)
                                ?.startMovieDetailsActivity(binder.item.id)
                    }
                    HotSearchBean.HOT_TYPE_PEOPLE -> {
//                        mViewModel?.searchPopularClick(2, -1, binder.item.id.toString()) //??????????????????????????????????????????????????????
                        getProvider(IMainProvider::class.java)
                                ?.startActorViewActivity(binder.item.id, binder.item.title)
                    }
                    HotSearchBean.HOT_TYPE_FAMILY -> {
                        getProvider(ICommunityFamilyProvider::class.java)
                                ?.startFamilyDetail(binder.item.id)
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    private fun onReviewBinderClick(view: View, binder: SearchRcmdReviewBinder) {
        when (view.id) {
            R.id.mReviewRoot -> {
                saveSearchHistory(binder.item.title ?: "")
            }
        }
    }

    /**
     * ????????????Binder????????????
     */
    private fun onSearchHistoryBinderClick(view: View, binder: SearchHistoryBinder) {
        when (view.id) {
            R.id.mSearchHistoryDelIv -> {
                clearHistory(binder)
            }
        }
    }

    private fun clearHistory(binder: SearchHistoryBinder) {
        mViewModel?.uiState?.value?.success?.run {
            this.remove(binder)
            mAdapter?.notifyAdapterDataSetChanged(this)
            mViewModel?.searchHistoryData?.apply {
                clear()
                removeSpKey(Search.SP_KEY_SEARCH_HISTORY)
            }
        }
    }

    /**
     * ????????????????????????
     */
    fun saveSearchHistory(keyword: String) {
        if (TextUtils.isEmpty(keyword)) {
            return
        }

        mViewModel?.apply {
            if (searchHistoryData.indexOf(keyword) != 0) {
                if (searchHistoryData.size >= 7) {
                    searchHistoryData.removeAt(6)
                }
                if (searchHistoryData.contains(keyword)) {
                    searchHistoryData.remove(keyword)
                }
                searchHistoryData.add(0, keyword)
                val jsonStr = handleJson(searchHistoryData)
                putSpValue(Search.SP_KEY_SEARCH_HISTORY, jsonStr)

                uiState.value?.success?.let {
                    val hasHistory = it.size != 0 && it[0] is SearchHistoryBinder
                    if (!hasHistory) {
                        it.add(0, SearchHistoryBinder(searchHistoryData))
                        mAdapter?.notifyAdapterDataSetChanged(it)
                    } else {
                        mAdapter?.notifyAdapterAdded(it)
                    }
                }
            }
        }
    }
}