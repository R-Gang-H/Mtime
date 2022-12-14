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
 * @desc ??????/?????????/????????????????????????Fragment
 */
class TopListTypeFragment : BaseVMFragment<TopListTypeViewModel, FragToplistTypeBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    private val mHomeProvider = getProvider(IHomeProvider::class.java)

    // ????????????
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

        // ????????????2~7
        mCategoryOtherAdapter = createMultiTypeAdapter(mFragTopListTypeCategoryOtherRv,
                GridLayoutManager(mContext, 3))
        // ????????????
        if(mToplistType == TopListConstant.TOPLIST_TYPE_MOVIE) {
            mYearlyAdapter = createMultiTypeAdapter(mFragTopListTypeYearRv,
                    GridLayoutManager(context, 2))
        }
        // ????????????
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

        //????????????
        if (mIsFirst) {
            showProgressDialog()
            // ??????App?????????????????????????????????
            mViewModel?.getIndexTopList()
            mIsFirst = false
        }
    }

    override fun startObserve() {
        // ???????????????
        observeRecommend()
        // ????????????
        observeQuery()
    }

    /**
     * ?????????????????????
     */
    private fun observeRecommend() {
        mViewModel?.recommendUiState?.observe(this) { it ->
            it.apply {
                success?.let {
                    // ?????????????????????
                    showRecommendData(it)
                }
                // ????????????????????????
                mViewModel?.getTopListQuery(mToplistType, mPageIndex)
            }
        }
    }

    /**
     * ????????????????????????
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
     * ??????????????????????????????
     */
    private fun queryError(viewState: Int) {
        if(mPageIndex == 1) {
            mMultiStateView.setViewState(viewState)
        }
    }

    /**
     * ????????????
     */
    override fun onLoadMore(refreshLayout: RefreshLayout){
        // ????????????????????????
        mViewModel?.getTopListQuery(mToplistType, mPageIndex)
    }

    /**
     * ????????????????????????"??????/??????"???????????????
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
                // ??????App?????????????????????????????????
                mViewModel?.getIndexTopList()
            }
        }
    }

    override fun destroyView() {

    }

    /**
     * ?????????????????????
     */
    private fun showRecommendData(indexAppTopList: IndexAppTopList?) {
        // ??????????????????
        indexAppTopList?.let { indexAppTopList ->
            mViewModel?.getTypeData(mToplistType, indexAppTopList)
            mViewModel?.topListUIModel?.value?.let { value ->
                // ????????????1
                value.firstCategoryTopList?.let {
                    // ??????????????????
                    ShapeExt.setGradientColor(mItemToplistCategoryFirstCoverView,
                            GradientDrawable.Orientation.RIGHT_LEFT,
                            R.color.color_4e6382,
                            R.color.color_1d2736,
                            0)
                    mItemToplistCategoryFirstCoverView.alpha = 0.2f
                    // ?????????: ?????????????????????????????????????????????????????????item????????????
                    var url = it.getCoverImgOrFristItemImg()
                    /**
                     * ????????????
                     * radius "23": ???????????????(???0.0???25.0??????)????????????25";
                     * sampling "4":??????????????????
                     */
                    mItemToplistCategoryFirstBgIv.loadImage(
                        data = url,
                        width = screenWidth,
                        185.dp,
                        blurRadius = 25F,
                        blurSampling = 4F
                    )
                }
                // ????????????2~7
                mCategoryOtherAdapter.notifyAdapterAdded(getCategoryOtherBinderList(value.otherCategoryTopLists))
                // ????????????
                if (mToplistType == TopListConstant.TOPLIST_TYPE_MOVIE) {
                    mYearlyAdapter.notifyAdapterAdded(getYearlyBinderList(value.yearlyTopLists))
                    mYearlyTopListInfos = indexAppTopList.movieTopListYearly?.topListInfosYearly
                    // ????????????
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
     * ??????????????????
     */
    private fun showQueryList(indexTopListQuery: IndexTopListQuery) {
        indexTopListQuery.items?.let {
            mFragTopListTypeSelectTitleTv.isVisible = it.isNotEmpty()
            mSelectAdapter.notifyAdapterAdded(getSelectBinderList(it))
        }
    }

    /**
     * ??????2~7BinderList
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
     * ????????????BinderList
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
     * ????????????BinderList
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
     * ????????????????????????
     */
    private fun showDatePicker(selectYear: Long, years: List<Long>) {
        TopListYearSelectDialog.newInstance(selectYear, years, ::selectYearCallback)
                .showNow(childFragmentManager, TopListYearSelectDialog.TAG_YEAR_SELECT_DIALOG_FRAGMENT)
    }

    /**
     * ?????????????????????
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