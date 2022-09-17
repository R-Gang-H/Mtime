package com.kotlin.android.search.newcomponent.ui.result

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.viewpager.widget.ViewPager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.put
import com.kotlin.android.router.liveevent.SEARCH_RESULT_TYPE_TAB
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.FragSearchResultWithSubTabBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.SEARCH_SORT_COMPREHENSIVE
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant.Companion.SEARCH_SORT_TIME_DESC
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim

/**
 * 整站搜索_结果页_二级tab_Fragment
 */
class SearchResultSubTabFragment: BaseVMFragment<BaseViewModel, FragSearchResultWithSubTabBinding>() {

    companion object {
        // 搜索类型：0影片、1影院、2影人、3全部、5文章、6 用户、7 影评、8 帖子、9 日志、10 家族、11片单、12视频、13播客  默认全部
        val TAB_MOVIE: Map<String, Long> = mapOf(
            getString(R.string.search_type_movie) to SEARCH_MOVIE,
            getString(R.string.search_type_film_comment) to SEARCH_FILM_COMMENT
        )
        val TAB_ARTICLE: Map<String, Long> = mapOf(
            getString(R.string.search_type_article) to SEARCH_ARTICLE,
            getString(R.string.search_type_post) to SEARCH_POST,
            getString(R.string.search_type_log) to SEARCH_LOG,
            getString(R.string.search_type_video) to SEARCH_VIDEO,
            getString(R.string.search_type_audio) to SEARCH_AUDIO
        )
    }

    private val mSortCorner = 11.dpF // 排序按钮圆角
    // 可以排序的搜索类型
    private val mCanSortSearchType = arrayOf(
            SEARCH_FILM_COMMENT,
            SEARCH_ARTICLE,
            SEARCH_POST,
            SEARCH_LOG,
            SEARCH_VIDEO,
            SEARCH_AUDIO
    )

    private var mSearchType = SEARCH_ALL
    private var mSearchKey = ""
    private var mTabs: Map<String, Long>? = null
    private var mFragAdapter: FragPagerItemAdapter? = null

    /**
     * 获取当前选择的Fragment
     */
    private val currentFragment: BaseVMFragment<*, *>?
        get() = if (mFragAdapter?.count.orZero() > 0) {
            mFragAdapter?.getItem(mBinding?.subViewPager?.currentItem ?: 0) as? BaseVMFragment<*, *>
        } else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            mSearchType = bundle.getLong(SearchResultConstant.KEY_UNION_SEARCH_TYPE, SEARCH_ALL)
        }
    }

    override fun initView() {
        // 初始化二级tab
        initSubTab()
        // 初始化排序View
        initSortView()
    }

    /**
     * 初始化二级tab
     */
    private fun initSubTab() {
        when(mSearchType) {
            SEARCH_MOVIE -> {
                mTabs = TAB_MOVIE
            }
            SEARCH_ARTICLE -> {
                mTabs = TAB_ARTICLE
            }
            else -> {
            }
        }
        mTabs?.let { tabs ->
            if (tabs.isNotEmpty()) {
                mFragAdapter = FragPagerItemAdapter(
                        childFragmentManager, FragPagerItems(mContext).apply {
                    tabs.map {
                        add(
                                title = it.key,
                                clazz = SearchResultTypeFragment::class.java,
                                args = Bundle().put(SearchResultConstant.KEY_UNION_SEARCH_TYPE, it.value)
                        )
                    }
                }
                )
                mBinding?.apply {
                    subViewPager.adapter = mFragAdapter
                    subViewPager.offscreenPageLimit = tabs.size
                    subTabLayout.setViewPager(subViewPager)
                    subTabLayout.setSelectedAnim()

                    // 监听切换页面（按排序搜索都在二级tab里）
                    subViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

                        override fun onPageScrollStateChanged(state: Int) { }

                        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }

                        override fun onPageSelected(position: Int) {
                            // 设置排序是否可见
                            setSortVisible()
                        }

                    })
                }
            }
        }
    }

    /**
     * 初始化排序View
     */
    private fun initSortView() {
        mBinding?.apply {
            // 排序按钮背景
            sortBtnCl.setBackground(
                    colorRes = R.color.color_f3f3f4,
                    cornerRadius = mSortCorner,
            )
            // 点击排序按钮
            sortBtnCl.onClick {
                setSortCardAndArrow(expand = !sortCard.isVisible)
            }
            // 点击综合排序
            sortComprehensiveTv.onClick {
                clickSortType(sortType = SEARCH_SORT_COMPREHENSIVE)
            }
            // 点击时间倒序
            sortTimeTv.onClick {
                clickSortType(sortType = SEARCH_SORT_TIME_DESC)
            }
        }
    }

    /**
     * 设置排序是否可见
     */
    private fun setSortVisible() {
        mBinding?.apply {
            // 收起排序Card
            setSortCardAndArrow(expand = false)
            // 当前搜索类型
            val searchType = (currentFragment as SearchResultTypeFragment).getSearchType()
            if (searchType in mCanSortSearchType) {
                sortBtnCl.visible()
                // 获取当前tab排序类型
                val sortType = (currentFragment as SearchResultTypeFragment).getSortType()?:SEARCH_SORT_COMPREHENSIVE
                // 设置排序文本UI
                setSortTextUI(sortType = sortType)
            } else {
                sortBtnCl.gone()
            }
        }
    }

    /**
     * 设置排序文本UI
     */
    private fun setSortTextUI(sortType: Long) {
        mBinding?.apply {
            sortBtnTv.text = getString(
                    if(sortType == SEARCH_SORT_COMPREHENSIVE)
                        R.string.search_result_sort_comprehensive
                    else
                        R.string.search_result_sort_time
            )
            sortComprehensiveTv.setTextColorRes(
                    if(sortType == SEARCH_SORT_COMPREHENSIVE)
                        R.color.color_20a0da
                    else
                        R.color.color_4e5e73
            )
            sortTimeTv.setTextColorRes(
                    if(sortType == SEARCH_SORT_TIME_DESC)
                        R.color.color_20a0da
                    else
                        R.color.color_4e5e73
            )
        }
    }

    /**
     * 设置排序Card和箭头
     */
    private fun setSortCardAndArrow(expand: Boolean) {
        mBinding?.apply {
            sortCard.visible(expand)
            sortBtnIv.setImageResource(
                    if (expand)
                        R.drawable.ic_search_sort_up
                    else
                        R.drawable.ic_search_sort_down
            )
        }
    }

    /**
     * 点击单个排序类型
     */
    private fun clickSortType(sortType: Long) {
        // 收起排序Card
        setSortCardAndArrow(expand = false)
        // 设置排序文本UI
        setSortTextUI(sortType = sortType)
        // 按排序搜索
        (currentFragment as SearchResultTypeFragment).searchBySort(sortType)
    }

    override fun initData() {
    }

    override fun startObserve() {
        // 从全部tab点击各分类"查看更多"事件监听
        LiveEventBus.get(SEARCH_RESULT_TYPE_TAB, com.kotlin.android.app.router.liveevent.event.SearchResultTypeTabState::class.java)
            .observe(this) {
                var index = 0
                run flag@{
                    mTabs?.map { tab ->
                        if (tab.value == it.searchType) {
                            return@flag
                        }
                        index++
                    }
                }
                // 定位二级tab
                mBinding?.subViewPager?.setCurrentItem(index, false)
            }
    }

    override fun onResume() {
        super.onResume()

        val searchKey = getSpValue(SearchResultConstant.SP_UNION_SEARCH_KEYWORD, "")
        // 搜索关键词变化，子tab需要定位到第一个
        if (searchKey.isNotEmpty() && mSearchKey != searchKey) {
            mSearchKey = searchKey
            mBinding?.subViewPager?.setCurrentItem(0, false)
        }

        // 设置排序是否可见
        setSortVisible()
    }

    override fun destroyView() {

    }

}