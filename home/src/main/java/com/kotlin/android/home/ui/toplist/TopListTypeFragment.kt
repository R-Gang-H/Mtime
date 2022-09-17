package com.kotlin.android.home.ui.toplist

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.toplist.IndexAppTopList
import com.kotlin.android.app.data.entity.toplist.IndexTopListQuery
import com.kotlin.android.app.data.entity.toplist.TopListInfo
import com.kotlin.android.app.data.entity.toplist.TopListInfos
import com.kotlin.android.home.BR
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FragToplistTypeBinding
import com.kotlin.android.home.ui.toplist.adapter.TopListCategoryOtherItemBinder
import com.kotlin.android.home.ui.toplist.adapter.TopListSelectItemBinder
import com.kotlin.android.home.ui.toplist.constant.TopListConstant
import com.kotlin.android.home.ui.toplist.widget.TopListYearSelectDialog
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog

import com.kotlin.android.app.router.provider.home.IHomeProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.frag_toplist_type.*
import kotlinx.android.synthetic.main.frag_toplist_type.mMultiStateView
import kotlinx.android.synthetic.main.frag_toplist_type.mRefreshLayout
import kotlinx.android.synthetic.main.item_toplist_category_first.*

/**
 * @author vivian.wei
 * @date 2020/7/10
 * @desc 电影/电视剧/影人榜单列表共用Fragment
 */
class TopListTypeFragment : BaseVMFragment<TopListTypeViewModel, FragToplistTypeBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    private val mHomeProvider = getProvider(IHomeProvider::class.java)

    // 榜单类型
    private var mToplistType: Long = TopListConstant.TOPLIST_TYPE_MOVIE
    private var mYearlyTopListInfos: List<TopListInfos> ?= null
    private lateinit var mCategoryOtherAdapter: MultiTypeAdapter
    private lateinit var mYearlyAdapter: MultiTypeAdapter
    private lateinit var mSelectAdapter: MultiTypeAdapter
    private var mPageIndex = 1
    private var mIsFirst = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getLong(TopListConstant.KEY_TOPLIST_TYPE, TopListConstant.TOPLIST_TYPE_MOVIE)?.let {
            mToplistType = it
        }
    }

    override fun initVM(): TopListTypeViewModel = viewModels<TopListTypeViewModel>().value

    override fun initView() {
        mBinding?.setVariable(BR.homeProvider, mHomeProvider)
        mBinding?.setVariable(BR.topListTypeViewModel, mViewModel)

        // 分类榜单2~7
        mCategoryOtherAdapter = createMultiTypeAdapter(mFragTopListTypeCategoryOtherRv,
                GridLayoutManager(mContext, 3))
        // 年度榜单
        if(mToplistType == TopListConstant.TOPLIST_TYPE_MOVIE) {
            mYearlyAdapter = createMultiTypeAdapter(mFragTopListTypeYearRv,
                    GridLayoutManager(context, 2))
        }
        // 精选榜单
        mSelectAdapter = createMultiTypeAdapter(mFragTopListTypeSelectRv,
                LinearLayoutManager(mContext))

        mFragTopListTypeCategoryFirstLayout.isGone = true
        mFragTopListTypeCategoryOtherRv.isGone = true
        mFragTopListTypeYearTitleCl.isGone = true
        mFragTopListTypeYearRv.isGone = true
        mFragTopListTypeSelectTitleTv.isGone = true

        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()

        //首次加载
        if (mIsFirst) {
            showProgressDialog()
            // 获取App榜单列表（推荐位数据）
            mViewModel?.getIndexTopList()
            mIsFirst = false
        }
    }

    override fun startObserve() {
        // 推荐位数据
        observeRecommend()
        // 精选片单
        observeQuery()
    }

    /**
     * 监视推荐位数据
     */
    private fun observeRecommend() {
        mViewModel?.recommendUiState?.observe(this) { it ->
            it.apply {
                success?.let {
                    // 显示推荐位数据
                    showRecommendData(it)
                }
                // 获取精选片单列表
                mViewModel?.getTopListQuery(mToplistType, mPageIndex)
            }
        }
    }

    /**
     * 监视精选片单列表
     */
    private fun observeQuery() {
        mViewModel?.queryUiState?.observe(this) {
            it.apply {
                dismissProgressDialog()

                success?.apply {
                    showQueryList(this)
                    if(this.hasNext == true) {
                        mRefreshLayout.finishLoadMore()
                        mPageIndex++
                    } else {
                        mRefreshLayout.finishLoadMoreWithNoMoreData()
                    }
                }

                if(isEmpty) {
                    queryError(MultiStateView.VIEW_STATE_EMPTY)
                }

                error?.apply {
                    queryError(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    queryError(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    /**
     * 精选榜单接口错误处理
     */
    private fun queryError(viewState: Int) {
        if(mPageIndex == 1) {
            mMultiStateView.setViewState(viewState)
        }
    }

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout){
        // 获取精选片单列表
        mViewModel?.getTopListQuery(mToplistType, mPageIndex)
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mCategoryOtherAdapter.notifyAdapterClear()
                if(mToplistType == TopListConstant.TOPLIST_TYPE_MOVIE) {
                    mYearlyAdapter.notifyAdapterClear()
                }
                mSelectAdapter.notifyAdapterClear()
                showProgressDialog()
                // 获取App榜单列表（推荐位数据）
                mViewModel?.getIndexTopList()
            }
        }
    }

    override fun destroyView() {

    }

    /**
     * 显示推荐位数据
     */
    private fun showRecommendData(indexAppTopList: IndexAppTopList?) {
        // 获取分类数据
        indexAppTopList?.let { indexAppTopList ->
            mViewModel?.getTypeData(mToplistType, indexAppTopList)
            mViewModel?.topListUIModel?.value?.let { value ->
                // 分类榜单1
                value.firstCategoryTopList?.let {
                    // 背景渐变蒙层
                    ShapeExt.setGradientColor(mItemToplistCategoryFirstCoverView,
                            GradientDrawable.Orientation.RIGHT_LEFT,
                            R.color.color_4e6382,
                            R.color.color_1d2736,
                            0)
                    mItemToplistCategoryFirstCoverView.alpha = 0.2f
                    // 背景图: 获取榜单封面图，如果为空，则获取第一个item的封面图
                    var url = it.getCoverImgOrFristItemImg()
                    /**
                     * 高斯模糊
                     * radius "23": 设置模糊度(在0.0到25.0之间)，默认”25";
                     * sampling "4":图片缩放比例
                     */
                    mItemToplistCategoryFirstBgIv.loadImage(
                        data = url,
                        width = screenWidth,
                        185.dp,
                        blurRadius = 25F,
                        blurSampling = 4F
                    )
                }
                // 分类榜单2~7
                mCategoryOtherAdapter.notifyAdapterAdded(getCategoryOtherBinderList(value.otherCategoryTopLists))
                // 年度榜单
                if (mToplistType == TopListConstant.TOPLIST_TYPE_MOVIE) {
                    mYearlyAdapter.notifyAdapterAdded(getYearlyBinderList(value.yearlyTopLists))
                    mYearlyTopListInfos = indexAppTopList.movieTopListYearly?.topListInfosYearly
                    // 切换年代
                    val years: List<Long>? = indexAppTopList.movieTopListYearly?.years
                    years?.let {
                        mFragTopListTypeYearSelectTv.onClick {
                            val selectYear = mFragTopListTypeYearSelectTv.text.toString().toLong()
                            showDatePicker(selectYear, years)
                        }
                    }
                }
            }
        }
    }

    /**
     * 显示精选片单
     */
    private fun showQueryList(indexTopListQuery: IndexTopListQuery) {
        indexTopListQuery.items?.let {
            mFragTopListTypeSelectTitleTv.isVisible = it.isNotEmpty()
            mSelectAdapter.notifyAdapterAdded(getSelectBinderList(it))
        }
    }

    /**
     * 分类2~7BinderList
     */
    private fun getCategoryOtherBinderList(list: List<TopListInfo>?): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListCategoryOtherItemBinder>()
        list?.map{
            var binder = TopListCategoryOtherItemBinder(it)
            binderList.add(binder)
        }
        return binderList
    }

    /**
     * 年度榜单BinderList
     */
    private fun getYearlyBinderList(list: List<TopListInfo>?): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListCategoryOtherItemBinder>()
        list?.map{
            var binder = TopListCategoryOtherItemBinder(it)
            binderList.add(binder)
        }
        return binderList
    }

    /**
     * 精选片单BinderList
     */
    private fun getSelectBinderList(list: List<TopListInfo>): List<MultiTypeBinder<*>> {
        val binderList = ArrayList<TopListSelectItemBinder>()
        list.map{ toplistInfo ->
            context?.let { it ->
                var binder = TopListSelectItemBinder(it, toplistInfo)
                binderList.add(binder)
            }
        }
        return binderList
    }

    /**
     * 显示年代选择弹窗
     */
    private fun showDatePicker(selectYear: Long, years: List<Long>) {
        TopListYearSelectDialog.newInstance(selectYear, years, ::selectYearCallback)
                .showNow(childFragmentManager, TopListYearSelectDialog.TAG_YEAR_SELECT_DIALOG_FRAGMENT)
    }

    /**
     * 选中年份后回调
     */
    private fun selectYearCallback(year: Long) {
        mFragTopListTypeYearSelectTv.text = year.toString()
        mYearlyTopListInfos?.let {
            mViewModel?.getYearlyTopLists(year, it)?.let {
                mYearlyAdapter.notifyAdapterDataSetChanged(getYearlyBinderList(it))
            }
        }
    }

}