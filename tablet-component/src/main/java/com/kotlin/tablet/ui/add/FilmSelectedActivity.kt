package com.kotlin.tablet.ui.add

import android.content.Intent
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
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
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.*
import com.kotlin.tablet.adapter.FilmListSelectedBinder
import com.kotlin.tablet.databinding.ActivityFilmListFilmSelectedBinding
import com.kotlin.tablet.event.FilmListPageCloseEvent
import com.kotlin.tablet.view.RecyclerViewNoBugLinearLayoutManager

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/23
 * 描述:已选电影
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_SELECTED)
class FilmSelectedActivity :
    BaseVMActivity<FilmSearchViewModel, ActivityFilmListFilmSelectedBinding>() {
    private lateinit var mAdapter: MultiTypeAdapter
    private var mFilmListId = 0L
    private var mFrom = KEY_FROM_SEARCH

    override fun getIntentData(intent: Intent?) {
        mFilmListId = intent?.getLongExtra(KEY_FILM_LIST_ID, 0L) ?: 0L
        mFrom = intent?.getIntExtra(KEY_TO_SELECTED_FROM, KEY_FROM_SEARCH) ?: KEY_FROM_SEARCH
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.tablet_film_list_selected_film),
            ).back {
                if (FilmCart.isEdit) {
                    FilmCart.isSave = false
                }
                finish()
            }.addItem(
                isReversed = true,
                titleRes = R.string.tablet_film_list_add,
                colorRes = R.color.color_ffffff,
                titleHeight = 25.dp,
                titlePaddingStart = 10.dp,
                titlePaddingEnd = 10.dp,
                titleMarginEnd = 7.dp,
                bgDrawable = getShapeDrawable(
                    colorRes = R.color.color_1da7dd,
                    cornerRadius = 13.dpF,
                )
            ) {
                //route addFlags() 不起作用
                Intent(this, FilmSearchActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    putExtra(KEY_FILM_LIST_ID, mFilmListId)
                    startActivity(this)
                }
            }
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(
                mSearchRv,
                RecyclerViewNoBugLinearLayoutManager(this@FilmSelectedActivity)
            ).setOnClickListener(::handleClick)
            mSaveBtn.onClick {
                if (FilmCart.isEdit) {
                    FilmCart.isSave = true
                    //关闭搜索影片
                    FilmListPageCloseEvent(PAGE_SEARCH_ACTIVITY).post()
                    finish()
                } else {
                    val ids = FilmCart.instance.getSelectedIds()
                    if (ids.isEmpty()){
                        showToast("至少添加1部电影")
                        return@onClick
                    }
                    mViewModel?.addMovies(ids, mFilmListId)
                }
            }
        }
    }

    override fun startObserve() {
        observeAddMovies()
        observePageClose()
    }

    /**
     * 关闭当前界面
     */
    private fun observePageClose() {
        observe(FilmListPageCloseEvent::class.java) {
            if (it.page == PAGE_SELECTED_ACTIVITY) {
                finish()
            }
        }
    }

    /**
     * 监听添加电影
     */
    private fun observeAddMovies() {
        mViewModel?.addMoviesUiState?.observe(this) {
            it.apply {
                showProgressDialog(showLoading)
                success?.apply {
                    if (bizCode == 0L) {
                        if (FilmCart.isEdit.not()) {
                            FilmCart.instance.clear()
                        }
                        //关闭搜索页
                        FilmListPageCloseEvent(PAGE_SEARCH_ACTIVITY).post()
                        FilmListPageCloseEvent(PAGE_SUCCESS_ACTIVITY).post()
                        showToast("添加影片成功")
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

    override fun onRestart() {
        super.onRestart()
        updateView()
    }

    override fun initData() {
        updateView()
    }

    private fun updateView() {
        val binders = FilmCart.instance.getSelectedData().map {
            FilmListSelectedBinder(it.value, true)
        }
        mAdapter.notifyAdapterDataSetChanged(binders.reversed())
    }

    private fun handleClick(view: View, binder: MultiTypeBinder<*>) {
        if (binder is FilmListSelectedBinder) {
            when (view.id) {
                R.id.mDelBtn -> {
                    FilmCart.instance.update(binder.bean)
                    mAdapter.notifyAdapterRemoved(binder)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //解决intent flag FLAG_ACTIVITY_REORDER_TO_FRONT 闪屏问题
        overridePendingTransition(R.anim.slide_right_int,R.anim.slide_left_out)
    }

}