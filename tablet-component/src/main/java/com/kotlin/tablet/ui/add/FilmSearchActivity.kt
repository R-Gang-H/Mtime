package com.kotlin.tablet.ui.add

import android.content.Intent
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.router.bus.ext.post
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.*
import com.kotlin.tablet.adapter.FilmListAddBinder
import com.kotlin.tablet.databinding.ActivityFilmSearchBinding
import com.kotlin.tablet.event.FilmListPageCloseEvent
import com.kotlin.tablet.view.FilmCartView

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/23
 * 描述:搜索电影
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_ADD_FILM)
class FilmSearchActivity : BaseVMActivity<FilmSearchViewModel, ActivityFilmSearchBinding>() {

    private lateinit var mAdapter: MultiTypeAdapter

    private var mFilmListId = 0L

    override fun getIntentData(intent: Intent?) {
        mFilmListId = intent?.getLongExtra(KEY_FILM_LIST_ID, 0L) ?: 0L
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
        "acy---> add initTheme".e()
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.tablet_film_list_add_film),
            ).back {
                if (FilmCart.isEdit) {
                    FilmCart.isSave = false
                }
                finish()
            }
    }

    override fun initView() {
        initSearchView()
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(
                mSearchRv,
                LinearLayoutManager(this@FilmSearchActivity)
            ).setOnClickListener(::handleClick)

            mRefreshLayout.apply {
                setOnRefreshListener {
                    search(true)
                }
                setOnLoadMoreListener {
                    search(false)
                }
            }
            mSureLay.addActionListener {
                when (it) {
                    FilmCartView.ActionType.SELECTED_ACTION -> {
                        //通过route设置flag无效 使用startActivity
                        Intent(this@FilmSearchActivity, FilmSelectedActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            putExtra(KEY_FILM_LIST_ID, mFilmListId)
                            putExtra(KEY_TO_SELECTED_FROM, KEY_FROM_SEARCH)
                            startActivity(this)
                        }
                    }
                    FilmCartView.ActionType.SURE_ACTION -> {
                        if (FilmCart.isEdit) {
                            FilmCart.isSave = true
                            //关闭已选影片
                            FilmListPageCloseEvent(PAGE_SELECTED_ACTIVITY).post()
                            finish()
                        } else {
                            val ids = FilmCart.instance.getSelectedIds()
                            if (ids.isEmpty()) {
                                showToast("至少添加1部电影")
                                return@addActionListener
                            }
                            mViewModel?.addMovies(ids, mFilmListId)
                        }
                    }
                }
            }
            mMultiStateView.apply {
                setEmptyResource(
                    R.drawable.ic_film_list_search_placeholder,
                    R.string.tablet_film_list_click_search_add_film
                )
                setViewState(MultiStateView.VIEW_STATE_EMPTY)
                setMultiStateListener(object : MultiStateView.MultiStateListener {
                    override fun onMultiStateChanged(viewState: Int) {
                        if (viewState == MultiStateView.VIEW_STATE_ERROR || viewState == MultiStateView.VIEW_STATE_NO_NET) {
                            mRefreshLayout.autoRefresh()
                        }
                    }
                })
            }
        }
    }

    private fun initSearchView() {
        mBinding?.apply {
            mSearchLay.run {
                setStartIcon(R.drawable.ic_title_bar_search)
                searchAction = {
                    if (it.event == 1) {
                        // 键盘上的搜索按钮
                        setViewState(MultiStateView.VIEW_STATE_CONTENT)
                        mRefreshLayout.autoRefresh()
                    } else {
                        if (TextUtils.isEmpty(it.keyword)) {
                            setViewState(MultiStateView.VIEW_STATE_EMPTY)
                        }
                    }
                    FilmCart.instance.mKeyWord = it.keyword
                }

            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
        observeSearch()
        observeAdd()
        observePageClose()
    }

    /**
     * 关闭当前界面
     */
    private fun observePageClose() {
        observe(FilmListPageCloseEvent::class.java) {
            if (it.page == PAGE_SEARCH_ACTIVITY) {
                finish()
            }
        }
    }

    /**
     * 监听添加电影接口
     */
    private fun observeAdd() {
        mViewModel?.addMoviesUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    if (bizCode == 0L) {
                        //清空搜索缓存
                        FilmCart.instance.clear()
                        //关闭成功页 跳转到上个界面(片单列表or投稿)
                        FilmListPageCloseEvent(PAGE_SUCCESS_ACTIVITY).post()
                        FilmListPageCloseEvent(PAGE_SELECTED_ACTIVITY).post()
                        showToast( "添加影片成功")
                        finish()
                    } else {
                        showToast(bizMessage ?: "添加影片失败，请稍后再试")
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    /**
     * 监听搜索结果
     */
    private fun observeSearch() {
        mViewModel?.searchUiState?.observe(this) {
            it.apply {
                mBinding?.mRefreshLayout.complete(it)
                mBinding?.mMultiStateView.complete(it)
                success?.apply {
                    updateView(this, isRefresh)
                }
            }
        }
    }

    private fun search(isRefresh: Boolean) {
        mViewModel?.search(FilmCart.instance.mKeyWord, isRefresh)
    }

    /**
     * 更新电影列表
     */
    private fun updateView(list: List<FilmListAddBinder>, isRefresh: Boolean) {
        if (isRefresh) {
            mAdapter.notifyAdapterDataSetChanged(list)
        } else {
            mAdapter.notifyAdapterAdded(list)
        }
    }

    private fun handleClick(view: View, binder: MultiTypeBinder<*>) {
        if (binder is FilmListAddBinder) {
            when (view.id) {
                R.id.mAddBtn -> {
                    val result = FilmCart.instance.update(binder.bean)
                    if (result) {
                        binder.notifyAdapterSelfChanged()
                        updateFilmCartView()
                    } else {
                        showToast("一个片单最多可包含${FILM_LIST_CONTAINS_FILM_MAX_COUNT}部影片")
                    }
                }
            }
        }
    }


    private fun setViewState(@MultiStateView.ViewState state: Int) {
        mBinding?.mMultiStateView?.setViewState(state)
    }

    private fun updateFilmCartView() {
        mBinding?.mSureLay?.notifyData()
    }

    override fun onRestart() {
        super.onRestart()
        mAdapter.notifyAdapterDataSetChanged(mAdapter.getList())
        updateFilmCartView()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //解决intent flag FLAG_ACTIVITY_REORDER_TO_FRONT 闪屏问题
        overridePendingTransition(R.anim.slide_right_int,R.anim.slide_left_out)
    }

    override fun onStart() {
        super.onStart()
        updateFilmCartView()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (FilmCart.isEdit.not()) {
            FilmCart.instance.clear()
        }
    }
}