package com.kotlin.tablet.ui.list

import android.annotation.SuppressLint
import android.view.View
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.flexbox.FlexboxLayoutManager
import com.kotlin.android.app.data.entity.filmlist.Category
import com.kotlin.android.app.data.entity.filmlist.PageRcmd
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
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.bus.ext.observe
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
import com.kotlin.tablet.adapter.FilmListBinder
import com.kotlin.tablet.adapter.FilmListLabelBinder
import com.kotlin.tablet.databinding.ActivityTabletMainBinding
import com.kotlin.tablet.event.FilmListDetailsEvent
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener

/**
 * 片单列表页面
 */
@Route(path = RouterActivityPath.TABLET.TABLET_MAIN)
class TabletMainActivity : BaseVMActivity<TabletMainViewModel, ActivityTabletMainBinding>(),
    MultiStateView.MultiStateListener,
    OnLoadMoreListener {

    private lateinit var mAdapter: MultiTypeAdapter
    private lateinit var mListAdapter: MultiTypeAdapter
    private var cateNumberId: Long? = null
    private var isSelectId: Long? = 0L
    private val iTabletProvider: ITabletProvider? = getProvider(ITabletProvider::class.java)
    override fun initVM(): TabletMainViewModel {
        return viewModels<TabletMainViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar()
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, themeStyle = ThemeStyle.STANDARD_STATUS_BAR)
            .setState(State.NORMAL)
            .setTitle(
                titleRes = R.string.tablet_main_title
            )
            .back {
                finish()
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
                // TODO 投稿
                iTabletProvider?.startContributeActivity()
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        mBinding?.apply {
            expand.setOnTouchListener { _, _ -> true }
            mRefreshLayout.setEnableRefresh(false)
            mRefreshLayout.setOnLoadMoreListener(this@TabletMainActivity)
            mMultiStateView.setMultiStateListener(this@TabletMainActivity)
            etSearch.setStartIcon(R.drawable.ic_title_bar_search)
            mAdapter = createMultiTypeAdapter(
                mTabletRecycleView,
            )
            mListAdapter =
                createMultiTypeAdapter(
                    mFilmListView,
                    FlexboxLayoutManager(this@TabletMainActivity)
                )
        }
        initListener()
    }

    private fun initListener() {
        mBinding?.apply {
            tvCreate.onClick { //创建片单
                iTabletProvider?.startFilmListCreateActivity(false)
            }
            tvSelect.onClick {
                if (expand.visibility == View.VISIBLE) {
                    expand.gone()
                    ivExpand.setBackgroundResource(R.drawable.ic_label_arrow_up)
                } else {
                    expand.visible()
                    ivExpand.setBackgroundResource(R.drawable.ic_label_arrow_down)
                }
            }
            tvCollection.onClick {
                //跳转我的片单
                iTabletProvider?.startFilmListMineActivity()
            }
            etSearch.onClick {
                //跳转片单搜索页面
                iTabletProvider?.startFilmListSearchActivity(tvSelect.text.toString(), isSelectId)
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
                                            rlTitle.gone()
                                        } else {
                                            tvSelect.setText(R.string.tablet_film_list_label)
                                            tvSelect.setTextColorRes(R.color.color_3d4955)
                                            binder.category[binder.selectPosition].isSelect = false
                                            cateNumberId = null
                                            rlTitle.visible()
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
                                //选中刷新页面
                                mViewModel?.loadData(isRefresh = true, cateNumberId)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mViewModel?.loadData(isRefresh = true, cateNumberId)
    }

    override fun startObserve() {
        mViewModel?.listUiState?.observe(this) {
            it?.apply {
                mBinding?.mRefreshLayout?.complete(this)
                mBinding?.mMultiStateView?.complete(this)

                success?.apply {
                    mBinding?.bean = this
                    cateNumberId = this.currCategoryId
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(
                            getListBinder(pageRcmds),
                            isScrollToTop = false
                        )
                    } else {
                        mAdapter.notifyAdapterAdded(getListBinder(pageRcmds))
                    }
                    mListAdapter.notifyAdapterDataSetChanged(
                        getLabelBinder(categories),
                        isScrollToTop = false
                    )//标签数据
                }
                error?.apply {
                    showToast("请求失败请稍后重试")
                }
            }
        }
        observe(FilmListDetailsEvent::class.java) {
            if (it.title != "标签") {
                mBinding?.tvSelect?.setTextColorRes(R.color.color_20a0da)
                mBinding?.tvSelect?.text = it.title
            } else {
                mBinding?.tvSelect?.setTextColorRes(R.color.color_3d4955)
                mBinding?.tvSelect?.text = it.title
            }
            isSelectId = it.isSelectId
            mViewModel?.loadData(isRefresh = true, it.isSelectId)
        }
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

    /**
     * 列表binder
     */
    private fun getListBinder(item: MutableList<PageRcmd>?): List<MultiTypeBinder<*>> {
        val listBinder = mutableListOf<MultiTypeBinder<*>>()
        item?.forEach {
            listBinder.add(FilmListBinder(it))
        }
        return listBinder
    }


    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(isRefresh = false, cateNumberId)
    }

    //各种状态下页面展示
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mViewModel?.loadData(isRefresh = true, cateNumberId)
            }
        }
    }
}