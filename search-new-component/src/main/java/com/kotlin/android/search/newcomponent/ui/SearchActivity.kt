package com.kotlin.android.search.newcomponent.ui

import android.text.TextUtils
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.search.newcomponent.databinding.ActSearchNewComponentBinding
import com.kotlin.android.search.newcomponent.ui.hint.SearchHintFragment
import com.kotlin.android.search.newcomponent.ui.history.SearchHistoryFragment
import com.kotlin.android.search.newcomponent.ui.result.SearchResultFragment

/**
 * 搜索
 */
@Route(path = RouterActivityPath.Search.PAGE_SEARCH_ACTIVITY)
class SearchActivity :
    BaseVMActivity<SearchViewModel, ActSearchNewComponentBinding>(), ISearchHandler {

    //当前页面状态：0代表history，1代表hint，2代表result
    private var mCurState = -1
    private var mCurFragment: Fragment? = null
    private var mSearchHistoryFragment: SearchHistoryFragment? = null
    private var mSearchHintFragment: SearchHintFragment? = null
    private var mSearchResultFragment: SearchResultFragment? = null

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.apply {
            searchView.run {
                setStartIcon()
                setEndIcon()
                searchAction = {
                    if (it.event == 1) {
                        // 键盘上的搜索按钮
                        if (it.keyword.isEmpty()) {
                            mBinding?.mAutoHintLayout?.getCurHintText()?.apply {
                                if (this.isNotEmpty()) {
                                    it.keyword = this
                                }
                            }
                        }
                        execSearchResult(it.keyword)
                    } else {
                        // 输入框内容变更
                        if (TextUtils.isEmpty(it.keyword)) {
                            //内容为空时显示历史页面
                            changeSate(Search.SEARCH_PAGE_SATE_HISTORY, it.keyword)
                        } else if (hasFocus()) {
                            //有内容时显示智能提示页
                            changeSate(Search.SEARCH_PAGE_SATE_HINT, it.keyword)
                        }
                    }
                }
            }

            mSearchCancelTv.onClick {
                finish()
            }
        }

        //初始显示历史页面
        changeSate(Search.SEARCH_PAGE_SATE_HISTORY, "")
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    /**
     * 页面状态变更
     */
    private fun changeSate(state: Int, keyword: String) {
        mCurState = state
        when (state) {
            Search.SEARCH_PAGE_SATE_HISTORY -> {
                if (null == mSearchHistoryFragment) {
                    mSearchHistoryFragment = SearchHistoryFragment.newInstance()
                }
                mSearchHistoryFragment?.let {
                    showFragment(it)
                }
            }
            Search.SEARCH_PAGE_SATE_HINT -> {
                if (null == mSearchHintFragment) {
                    mSearchHintFragment = SearchHintFragment.newInstance()
                }
                mSearchHintFragment?.let {
                    showFragment(it)
                    it.searchHint(keyword)
                }
            }
            Search.SEARCH_PAGE_SATE_RESULT -> {
                if (null == mSearchResultFragment) {
                    mSearchResultFragment = SearchResultFragment.newInstance()
                }
                mSearchResultFragment?.let {
                    showFragment(it)
                    it.search(keyword)
                }
            }
        }
    }

    /**
     * 显示指定的Fragment
     */
    private fun showFragment(fragment: Fragment) {
        if (mCurFragment != fragment) {
            supportFragmentManager
                .beginTransaction().run {
                    mCurFragment?.let {
                        hide(it)
                    }
                    if (fragment.isHidden) {
                        show(fragment)
                    } else {
                        add(R.id.mFragmentLayout, fragment, "search_page_${mCurState}")
                    }
                    commitNowAllowingStateLoss()
                }
            mCurFragment = fragment
        }
    }

    /**
     * 执行搜索，HistoryFragment和HintFragment回调以及键盘上的搜索按钮
     */
    override fun execSearchResult(keyword: String) {
        if (keyword.isNotEmpty()) {
            mBinding?.searchView?.run {
                clearFocus()
                hideSoftInput()
                setText(keyword)
            }
            mSearchHistoryFragment?.saveSearchHistory(keyword)
            changeSate(Search.SEARCH_PAGE_SATE_RESULT, keyword)
        } else {
            showToast("内容不成为空")
        }
    }

    /**
     * 热门词条HistoryFragment回调
     */
    override fun callbackHotWord(list: List<String>) {
        mBinding?.mAutoHintLayout?.setHints(list)
    }
}

/**
 * Fragment回调Activity的接口定义
 */
interface ISearchHandler {
    fun execSearchResult(keyword: String)
    fun callbackHotWord(list: List<String>)
}