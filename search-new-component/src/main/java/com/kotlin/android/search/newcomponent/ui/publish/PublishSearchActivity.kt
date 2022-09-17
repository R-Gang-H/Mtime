package com.kotlin.android.search.newcomponent.ui.publish

import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.SEARCH_MOVIE
import com.kotlin.android.app.data.annotation.SEARCH_PERSON
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_BUNDLE_KEY_FROM
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_BUNDLE_KEY_TYPE
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_FROM_FIND_MOVIE
import com.kotlin.android.search.newcomponent.Search.PUBLISH_SEARCH_FROM_PUBLISH
import com.kotlin.android.search.newcomponent.databinding.ActPublishSearchBinding
import com.kotlin.android.search.newcomponent.ui.publish.adapter.PublishSearchMovieItemBinder
import com.kotlin.android.search.newcomponent.ui.publish.adapter.PublishSearchPersonItemBinder
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/6
 * 描述: 发布组件-搜索影片/影人
 */
@Route(path = RouterActivityPath.Search.PAGE_PUBLISH_SEARCH_ACTIVITY)
class PublishSearchActivity: BaseVMActivity<PublishSearchViewModel, ActPublishSearchBinding>() {

    private val mTicketProvider: ITicketProvider? = getProvider(ITicketProvider::class.java)

    private var mSearchType = SEARCH_MOVIE
    private var mFrom = PUBLISH_SEARCH_FROM_PUBLISH  // 页面来源
    private var mAdapter: MultiTypeAdapter? = null

    override fun initVM(): PublishSearchViewModel= viewModels<PublishSearchViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
                .setTitle(
                        titleRes = when(mSearchType) {
                            SEARCH_PERSON -> R.string.publish_search_person_title
                            else -> {
                                if(mFrom == PUBLISH_SEARCH_FROM_FIND_MOVIE)
                                    R.string.search_movie
                                else
                                    R.string.publish_search_movie_title
                            }
                        },
                )
                .addItem(
                        drawableRes = R.drawable.ic_title_bar_36_back
                ) {
                    onBackPressed()
                }
    }

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.let {
            mSearchType = it.getLongExtra(PUBLISH_SEARCH_BUNDLE_KEY_TYPE, SEARCH_MOVIE)
            mFrom = it.getLongExtra(PUBLISH_SEARCH_BUNDLE_KEY_FROM, PUBLISH_SEARCH_FROM_PUBLISH)
        }
    }

    override fun initView() {
        mBinding?.apply {
            searchEditText.apply {
                setStartIcon()
                setEndIcon()
                setHint(
                        when(mSearchType) {
                            SEARCH_PERSON -> R.string.publish_search_person_hint
                            else -> R.string.publish_search_movie_hint
                        }
                )
                searchAction = {
                    val keyword = it.keyword.trim()
                    if (it.event == 1 && keyword.isNotEmpty()) {
                        mViewModel?.unionSearch(
                                isRefresh = true,
                                keyword = keyword,
                                searchType = mSearchType
                        )
                    }
                }
            }
            mAdapter = createMultiTypeAdapter(searchRv)
            mAdapter?.setOnClickListener(::onBinderClick)
        }
    }

    override fun initData() {

    }

    override fun startObserve() {
        unionSearchObserve()
    }

    private fun unionSearchObserve() {
        mViewModel?.uIState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if(binders.isNullOrEmpty()) {
                        mAdapter?.notifyAdapterClear()
                    } else {
                        mAdapter?.notifyAdapterDataSetChanged(binders)
                    }
                }
            }
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is PublishSearchMovieItemBinder -> {  // 点击影片item
                clickMovieItem(binder.movie)
            }
            is PublishSearchPersonItemBinder -> { // 点击影人item
                clickPersonItem(binder.person)
            }
            else -> {
            }
        }
    }

    /**
     * 点击影片item
     */
    private fun clickMovieItem(movie: Movie) {
        when(mFrom) {
            PUBLISH_SEARCH_FROM_PUBLISH -> {
                // 选中的影片信息回传给发布页
                Intent().apply {
                    putExtra(Search.KEY_SEARCH_DATA_MOVIE, movie)
                }.apply {
                    setResult(Search.SEARCH_MOVIE_RESULT_CODE, this)
                }
                finish()
            }
            PUBLISH_SEARCH_FROM_FIND_MOVIE -> {
                // 影片详情页
                mTicketProvider?.startMovieDetailsActivity(movie.movieId.orZero())
            }
            else -> {

            }
        }

    }

    /**
     * 点击影人item
     */
    private fun clickPersonItem(person: Person) {
        when(mFrom) {
            PUBLISH_SEARCH_FROM_PUBLISH -> {
                // 选中的影人信息回传给发布组件
                Intent().apply {
                    putExtra(Search.KEY_SEARCH_DATA_PERSON, person)
                }.apply {
                    setResult(Search.SEARCH_PERSON_RESULT_CODE, this)
                }
                finish()
            }
            else -> {

            }
        }

    }
}