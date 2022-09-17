package com.kotlin.android.home.ui.findmovie

import android.animation.ValueAnimator
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.app.data.annotation.SEARCH_MOVIE
import com.kotlin.android.app.data.entity.search.ConditionResult
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ActFindMovieBinding
import com.kotlin.android.home.ui.findmovie.adapter.DropMenuAdapter
import com.kotlin.android.home.ui.findmovie.bean.SearchBean
import com.kotlin.android.home.ui.findmovie.view.FilterConditionDialogFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.search.newcomponent.Search
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager


/**
 * 首页找电影
 * @author WangWei
 * @data 2022/4/11
 */
@Route(path = RouterActivityPath.Home.PAGER_FIND_MOVIE_ACTIVITY)
class FindMovieActivity : BaseVMActivity<FindMovieViewModel, ActFindMovieBinding>(),
    MultiStateView.MultiStateListener {
    private var mAdapter: MultiTypeAdapter? = null
    private var mFilterResultAdapter: MultiTypeAdapter? = null
    var searchBean = SearchBean()
    var conditionResult = ConditionResult()
    var filterConditionDialogFragment: FilterConditionDialogFragment? = null

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.home_find_movie),
                isBold = true,
                gravity = Gravity.CENTER,
                drawablePadding = 5.dp,
            ).addItem(
                isReversed = false,
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
                click = {
                    this.finish()
                }
            ).addItem(
                isReversed = true,
                drawableRes = R.drawable.ic_title_bar_36_search,
                reverseDrawableRes = R.drawable.ic_title_bar_36_search_reversed,
                click = {
                    //搜索电影页
                    getProvider(ISearchProvider::class.java)?.startPublishSearchActivity(
                        activity = this,
                        searchType = SEARCH_MOVIE,
                        from = Search.PUBLISH_SEARCH_FROM_FIND_MOVIE
                    )
                }
            )
    }

    private fun moveToFirstPosition() {
        //滚动到头部
        mBinding?.personAppBarLayout?.setExpanded(false, true)
    }

    //滚动到头部
    private fun moviToHead() {
        val behavior: AppBarLayout.Behavior? =
            (mBinding?.personAppBarLayout?.layoutParams as CoordinatorLayout.LayoutParams).behavior as AppBarLayout.Behavior?

        if (behavior != null) {
            val valueAnimator: ValueAnimator = ValueAnimator.ofInt()
            valueAnimator.interpolator = DecelerateInterpolator()
            valueAnimator.addUpdateListener { animation ->
                behavior.topAndBottomOffset = animation.animatedValue as Int
                mBinding?.personAppBarLayout?.requestLayout()
            }
            var location = IntArray(2)
            mBinding?.filterConditionLayout?.getLocationOnScreen(location)
            var yPosiztion = location?.get(1)

            valueAnimator.setIntValues(
                0,
                -(mBinding?.filterConditionLayout?.y.orZero()).toInt() + 64.dp
            )//
            valueAnimator.duration = 1000
            valueAnimator.start()
        }
    }


    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
        initRefreshLayout()

        mBinding?.mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@FindMovieActivity)
            mAdapter = createMultiTypeAdapter(this, layoutManager)
//            mAdapter?.setOnClickListener(::onBinderClick)  //注：多binder 自行处理点击事件

        }
        mBinding?.filterResultRV?.apply {
            layoutManager = LinearLayoutManager(this@FindMovieActivity)
            mFilterResultAdapter = createMultiTypeAdapter(this, layoutManager)
            mFilterResultAdapter?.setOnClickListener(::onBinderClick)
        }

    }

    private fun initFilterDropDownView() {
        /* var titleList = arrayListOf<String>()
         titleList.add("类型")
         titleList.add("地区")
         titleList.add("排序")
         titleList.add("年代")*/
        var data = resources.getStringArray(R.array.home_filter_titles)
        mBinding?.dropDownMenu?.setFrameLayoutContainerView(mBinding?.frameLayoutContainerView!!)
        mBinding?.dropDownMenu?.setMenuAdapter(
            DropMenuAdapter(
                this,
                conditionResult,
                data,
                onFilterDone = { _, _, value, valueYear ->
                    run {
                        when (mBinding?.dropDownMenu?.tabCurrentPosition) {
                            0 -> searchBean.genreTypes = value
                            1 -> searchBean.area = value
                            2 -> searchBean.sortType = value
                            3 -> searchBean.year = valueYear
                        }
                        mViewModel?.loadMoreData(true, searchBean)
                        mBinding?.dropDownMenu?.close()
                    }

                })
        )
        mBinding?.dropDownMenu?.clickTabOperate = { moveToFirstPosition() }
    }

    //    private fun showFilterConditionView() {
//        filterConditionDialogFragment?.setData(
//            SearchBean(type = searchBean.type,keyword = searchBean.keyword,genreTypes = searchBean.genreTypes,year = searchBean.year,area = searchBean.area,sortType = searchBean.sortType),
//            SearchBean.convertFilterConditionBinder(0, conditionResult)
//        ) {
//            //回调选择结果 不同请求筛选
//            if (searchBean != it) {
//                mViewModel?.loadMoreData(true, it)
//                searchBean = it
//            }
//        }
//        filterConditionDialogFragment?.show(supportFragmentManager)
//    }
//    private fun showFilterConditionView() {
//        filterConditionDialogFragment?.setData(
//            SearchBean(
//                type = searchBean.type,
//                keyword = searchBean.keyword,
//                genreTypes = searchBean.genreTypes,
//                year = searchBean.year,
//                area = searchBean.area,
//                sortType = searchBean.sortType
//            ),
//            SearchBean.convertFilterConditionBinder(0, conditionResult)
//        ) {
//            //回调选择结果 不同请求筛选
//            if (searchBean != it) {
//                mViewModel?.loadMoreData(true, it)
//                searchBean = it
//            }
//        }
//        var filterView = FilterConditionView(this)
//        filterView?.setData(
//            SearchBean(
//                type = searchBean.type,
//                keyword = searchBean.keyword,
//                genreTypes = searchBean.genreTypes,
//                year = searchBean.year,
//                area = searchBean.area,
//                sortType = searchBean.sortType
//            ),
//            SearchBean.convertFilterConditionBinder(0, conditionResult)
//        ) {
//            //回调选择结果 不同请求筛选
//            if (searchBean != it) {
//                mViewModel?.loadMoreData(true, it)
//                searchBean.update(it.keyword, it.genreTypes, it.year, it.area, it.sortType)
//            }
//        }
//        var height = screenHeight - 44.dp - 50.dp
//        val popupWindow = CommonPopupWindow.Builder(this)
//            .setView(filterView.rootView)
////            .setAnimationStyle(R.style.HOM)
//            .setViewOnclickListener { popupWindow, view, layoutResId ->
//                popupWindow.dismiss()
//            }
//            .setWidthAndHeight(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                height
//            )
//            .create()
//
//        popupWindow.showAsDropDown(mBinding?.filterConditionLayout)
//    }


    override fun startObserve() {
        registerUIStateObserve()
    }

    private fun registerUIStateObserve() {
        //banner、片单、榜单、条件筛选电影
        observeLiveData(mViewModel?.uiState) {
            mBinding?.apply {
                it.apply {
                    mRefreshLayout.complete(this)
                    mMultiStateView.complete(this)

                    success?.apply {
                        items?.apply {
                            if (isRefresh) {
                                mAdapter?.notifyAdapterDataSetChanged(this)
                            } else {
                                mAdapter?.notifyAdapterAdded(this)
                            }
                        }
                    }
                }
            }
        }
        //筛选条件
        observeLiveData(mViewModel?.filterConditionUiState) {
            mBinding?.apply {
                it.apply {
                    success?.apply {
                        conditionResult = this

                        if (this.genres?.isEmpty() == true
                            || this.locations?.isEmpty() == true
                            || this.sorts?.isEmpty() == true
                            || this.years?.isEmpty() == true
                        ) {
                            showOrHideFilterView(View.GONE)
                        } else {
                            mViewModel?.loadMoreData(true)
                            showOrHideFilterView(View.VISIBLE)
                            //设置数据
                            initFilterDropDownView()
                        }
                    }
                    error?.apply {
                        showOrHideFilterView(View.GONE)
                    }
                }
            }
        }
        //筛选结果
        observeLiveData(mViewModel?.filterResultUiState) {
            mBinding?.apply {
                it.apply {
                    mRefreshLayout.complete(this)
                    mBinding?.mResultMultiStateView.complete(this)

                    success?.apply {

                        items?.apply {
                            if (isRefresh) {
                                mFilterResultAdapter?.notifyAdapterDataSetChanged(this)
                            } else {
                                mFilterResultAdapter?.notifyAdapterAdded(this)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showOrHideFilterView(state: Int) {
        mBinding?.filterConditionLayout?.visibility = state
        mBinding?.filterConditionLayout?.onClick {
            moveToFirstPosition()
        }
        mBinding?.filterResultRV?.visibility = state
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    /**
     * 初始化筛选fragment
     */
    private fun initFilterFragment() {
        filterConditionDialogFragment = FilterConditionDialogFragment.newInstance()
    }


    /**
     * 状态回调
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.mRefreshLayout?.autoRefresh()
            }
        }
    }

    /**
     * 设置下拉刷新逻辑
     */
    private fun initRefreshLayout() {
        mBinding?.mRefreshLayout?.apply {
            setOnRefreshListener {
                loadData(true)
            }
            setOnLoadMoreListener {
                //load more
//                loadData(false)
                mViewModel?.loadMoreData(false, searchBean)
            }

        }
        mBinding?.mResultMultiStateView?.setMultiStateListener(object :
            MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                when (viewState) {
                    MultiStateView.VIEW_STATE_ERROR,
                    MultiStateView.VIEW_STATE_NO_NET -> {
                        mBinding?.mRefreshLayout?.autoRefresh()
                    }
                }
            }
        })
    }


    /**
     *  加载数据
     */
    fun loadData(isRefresh: Boolean = false) {
        mViewModel?.loadData()
        mViewModel?.loadFilterCondition()
    }

    /**
     * 需要回调再实现
     * 关注、订阅之类
     */
    open fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
    }


}