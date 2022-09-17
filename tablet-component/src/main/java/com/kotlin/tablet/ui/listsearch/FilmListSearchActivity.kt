package com.kotlin.tablet.ui.listsearch

import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.android.app.data.entity.filmlist.Category
import com.kotlin.android.app.data.entity.search.FilmList
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.router.bus.ext.post
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.FilmListLabelBinder
import com.kotlin.tablet.adapter.FilmSearchListBinder
import com.kotlin.tablet.databinding.ActivityFilmListSearchBinding
import com.kotlin.tablet.event.FilmListDetailsEvent
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * 片单列表搜索页面
 */
@Route(path = RouterActivityPath.TABLET.FILM_LIST_SEARCH)
class FilmListSearchActivity :
    BaseVMActivity<FilmListSearchViewModel, ActivityFilmListSearchBinding>(),
    OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {
    private val iTabletProvider: ITabletProvider? = getProvider(ITabletProvider::class.java)
    private lateinit var mAdapter: MultiTypeAdapter
    private lateinit var mListAdapter: MultiTypeAdapter
    private var cateNumberId: Long? = null
    private var isSelectId: Long? = null
    private var keyWord: String = ""
    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initView() {
        isSelectId = intent.getLongExtra("key_isSelected", 0L)
        val label = intent.getStringExtra("key_label")
        mBinding?.apply {
            if (label != "标签") {
                tvSelect.text = label
                tvSelect.setTextColorRes(R.color.color_20a0da)
            }
            mRefreshLayout.setOnLoadMoreListener(this@FilmListSearchActivity)
            mRefreshLayout.setOnRefreshListener(this@FilmListSearchActivity)
            mMultiStateView.setMultiStateListener(this@FilmListSearchActivity)
            etSearch.setStartIcon(R.drawable.ic_title_bar_search)
            mAdapter = createMultiTypeAdapter(
                mTabletRecycleView,
            )
            mListAdapter =
                createMultiTypeAdapter(
                    mFilmListView,
                    FlexboxLayoutManager(this@FilmListSearchActivity)
                )
        }
        initListener()
    }

    private fun initListener() {
        mBinding?.apply {
            tvSelect.onClick {
                if (expand.visibility == View.VISIBLE) {
                    expand.gone()
                    ivExpand.setBackgroundResource(R.drawable.ic_label_arrow_up)
                } else {
                    expand.visible()
                    ivExpand.setBackgroundResource(R.drawable.ic_label_arrow_down)
                }
            }
            etSearch.searchAction = {
                keyWord = it.keyword
                //搜索
                if (it.event == 1) {
                    mViewModel?.searchList(keyWord, isSelectId, true)
                }
            }
        }
        mListAdapter.setOnClickListener { view, binder ->
            when (binder) {
                is FilmListLabelBinder -> {
                    when (view.id) {
                        R.id.tv_categoryName -> {
                            binder.category.forEachIndexed { index, _ ->
                                if (binder.selectPosition == index) {
                                    binder.category[index].isSelect = true
                                    mBinding?.apply {
                                        if (tvSelect.text.toString() != binder.category[binder.selectPosition].categoryName) {
                                            tvSelect.text =
                                                binder.category[binder.selectPosition].categoryName
                                            tvSelect.setTextColorRes(R.color.color_20a0da)
                                            cateNumberId =
                                                binder.category[binder.selectPosition].categoryId
                                        } else {
                                            tvSelect.setText(R.string.tablet_film_list_label)
                                            tvSelect.setTextColorRes(R.color.color_3d4955)
                                            binder.category[binder.selectPosition].isSelect = false
                                            cateNumberId = null
                                        }
                                        expand.gone()
                                        ivExpand.setBackgroundResource(R.drawable.ic_label_arrow_down)
                                    }
                                    isSelectId = if (binder.category[index].isSelect) {//如果选中
                                        binder.category[index].categoryId
                                    } else {//如果取消选中
                                        null
                                    }
                                } else {
                                    binder.category[index].isSelect = false
                                }
                                binder.mPosition = index
                                binder.notifyAdapterSelfChanged()
                            }
                            mBinding?.apply {
                                //选中清空页面
                                mAdapter.notifyAdapterClear()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mViewModel?.loadData(true, cateNumberId)
    }

    override fun startObserve() {
        mViewModel?.listUiState?.observe(this) {
            it.apply {
                success?.apply {
                    mBinding?.bean = this
                    cateNumberId = this.currCategoryId
                    mListAdapter.notifyAdapterDataSetChanged(
                        getLabelBinder(categories)
                    )//标签数据
                }
            }
        }
        mViewModel?.searchUIState?.observe(this) {
            it.apply {
                mBinding?.mRefreshLayout?.complete(this)
                mBinding?.mMultiStateView?.complete(this, ({
                    hideSoftInput()
                    mBinding?.mEmptyView?.visible()
                    mAdapter.notifyAdapterClear()
                }))
                success?.apply {
                    mBinding?.mEmptyView?.gone()
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(getListBinder(filmListItems))
                    } else {
                        mAdapter.notifyAdapterAdded(getListBinder(filmListItems))
                    }
                }
            }
        }
    }

    /**
     * 搜索列表
     */
    private fun getListBinder(filmList: List<FilmList>?): List<MultiTypeBinder<*>> {
        val listBinder = mutableListOf<MultiTypeBinder<*>>()
        filmList?.forEach {
            listBinder.add(FilmSearchListBinder(it))
        }
        return listBinder
    }

    /**
     * 标签binder
     */
    private fun getLabelBinder(categories: MutableList<Category>?): List<MultiTypeBinder<*>> {
        val labelBinder = mutableListOf<MultiTypeBinder<*>>()
        categories?.forEachIndexed { index, _ ->
            if (categories[index].categoryId == isSelectId) {//如果跟上次选中的id相同就设置为true
                categories[index].isSelect = true
            }
            labelBinder.add(FilmListLabelBinder(categories))
        }
        return labelBinder
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, themeStyle = ThemeStyle.STANDARD_STATUS_BAR)
            .setState(State.NORMAL)
            .setTitle(
                titleRes = R.string.tablet_main_title
            )
            .back {
                onBackPressed()
            }
            .addItem( // 可设置点击效果选择器
                isReversed = true,
                titleRes = R.string.tablet_main_right_title,
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
                iTabletProvider?.startContributeActivity()
            }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(isRefresh = true, cateNumberId)
        mViewModel?.searchList(keyWord, cateNumberId, true)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(isRefresh = false, cateNumberId)
        mViewModel?.searchList(keyWord, cateNumberId, false)
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.mRefreshLayout?.autoRefresh()
            }
        }
    }

    override fun onBackPressed() {
        FilmListDetailsEvent(mBinding?.tvSelect?.text.toString(), isSelectId).post()
        super.onBackPressed()
    }
}