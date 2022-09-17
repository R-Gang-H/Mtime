package com.kotlin.android.search.newcomponent.ui.result

import android.os.Bundle
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.core.ext.removeSpKey
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.put
import com.kotlin.android.router.liveevent.SEARCH_RESULT_TAB_ALL_REFRESH
import com.kotlin.android.router.liveevent.SEARCH_RESULT_TYPE_TAB
import com.kotlin.android.app.router.liveevent.event.SearchResultTabAllRefreshState
import com.kotlin.android.app.router.liveevent.event.SearchResultTypeTabState
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.FragSearchResultBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.KEY_UNION_SEARCH_TYPE
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.SP_UNION_SEARCH_KEYWORD
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim

/**
 * 整站搜索_结果页Fragment
 */
class SearchResultFragment: BaseVMFragment<BaseViewModel, FragSearchResultBinding>()  {

    companion object {
        // 搜索类型：0影片、1影院、2影人、3全部、5文章、6用户、7影评、8帖子、9日志、10家族、11片单、12视频、13播客 默认全部
        val TAB: Map<String, Long> = mapOf(
            getString(R.string.search_type_all) to SEARCH_ALL,
            getString(R.string.search_type_movie_and_tv) to SEARCH_MOVIE,
            getString(R.string.search_type_cinema) to SEARCH_CINEMA,
            getString(R.string.search_type_person) to SEARCH_PERSON,
            getString(R.string.search_type_content) to SEARCH_ARTICLE,
            getString(R.string.search_type_user) to SEARCH_USER,
            getString(R.string.search_type_family) to SEARCH_FAMILY,
            getString(R.string.search_type_film_list) to SEARCH_FILM_LIST
            )

        fun newInstance() = SearchResultFragment()
    }

    override fun initView() {
        mBinding?.apply {
            // tab渐变背景
            mFragSearchResultTabLayout.setBackground(
                    colorRes = R.color.color_f9f9fb,
                    endColorRes = R.color.color_ffffff,
            )

            val adapter = FragPagerItemAdapter(
                    childFragmentManager,
                    FragPagerItems(mContext).apply {
                        TAB.map {
                            when(it.value) {
                                SEARCH_MOVIE, SEARCH_ARTICLE -> { // 影视、文章有子tab
                                    add(title = it.key,
                                            clazz = SearchResultSubTabFragment::class.java,
                                            args = Bundle().put(KEY_UNION_SEARCH_TYPE, it.value))
                                }
                                else -> {
                                    add(title = it.key,
                                            clazz = SearchResultTypeFragment::class.java,
                                            args = Bundle().put(KEY_UNION_SEARCH_TYPE, it.value))
                                }
                            }
                        }
                    }
            )
            mFragSearchResultViewPager.adapter = adapter
            mFragSearchResultViewPager.offscreenPageLimit = 8
            mFragSearchResultTabLayout.setViewPager(mFragSearchResultViewPager)
            mFragSearchResultTabLayout.setSelectedAnim()
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        // 从全部tab点击各分类"查看更多"事件监听
        LiveEventBus.get(SEARCH_RESULT_TYPE_TAB, SearchResultTypeTabState::class.java)
            .observe(this) {
                var index = 0
                var type = it.searchType
                // 一级tab归属
                when(type) {
                    SEARCH_FILM_COMMENT -> {
                        type = SEARCH_MOVIE
                    }
                    SEARCH_POST, SEARCH_LOG, SEARCH_VIDEO, SEARCH_AUDIO -> {
                        type = SEARCH_ARTICLE
                    }
                    else -> {
                    }
                }
                run flag@{
                    TAB.map { tab ->
                        if (tab.value == type) {
                            return@flag
                        }
                        index++
                    }
                }
                // 定位一级tab(二级tab在SearchResultSubTabFragment里接收事件）
                mBinding?.mFragSearchResultViewPager?.setCurrentItem(index, false)
            }
    }

    override fun destroyView() {
        removeSpKey(SP_UNION_SEARCH_KEYWORD)
    }

    /**
     * 搜索
     */
    fun search(searchKey: String) {
        val curSearchKey = getSpValue(SP_UNION_SEARCH_KEYWORD, "")
        if(searchKey.isNotEmpty() && searchKey != curSearchKey) {
            putSpValue(SP_UNION_SEARCH_KEYWORD, searchKey)
            if(mBinding?.mFragSearchResultViewPager?.currentItem == 0) {
                // 如果当前页为全部Tab，重新搜索，则不会调SearchResultTypeFragment的onResume，需要手动刷新页面
                LiveEventBus.get(SEARCH_RESULT_TAB_ALL_REFRESH)
                    .post(SearchResultTabAllRefreshState())
            } else {
                mBinding?.mFragSearchResultViewPager?.setCurrentItem(0, false)
            }
        }
    }

}