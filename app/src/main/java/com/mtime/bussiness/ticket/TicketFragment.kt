package com.mtime.bussiness.ticket

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.RegionPublish
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.ITitleBarOfFragment
import com.kotlin.android.core.entity.PageFlag
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.invisible
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.banner.setCommIndicator
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.mtime.R
import com.mtime.base.location.LocationInfo
import com.mtime.bussiness.ticket.adapter.TabTicketBannerAdapter
import com.mtime.bussiness.ticket.bean.TicketBannerItem
import com.mtime.bussiness.ticket.cinema.TabTicketCinemaFragment
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesInComingFragment
import com.mtime.bussiness.ticket.movie.fragment.TicketMoviesOnShowFragment
import com.mtime.databinding.FragTicketBinding
import com.mtime.util.JumpUtil
import com.mtime.widgets.BaseTitleView
import com.mtime.widgets.TitleOfSearchNewView

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/12
 * 描述: 底部导航-购票tab
 */
class TicketFragment: BaseVMFragment<TicketViewModel, FragTicketBinding>() {

    private val mBannerRoundCorner = 5.dpF
    private val mCityNameMaxLength = 4
    private val mCityNameEllipsize = "..."

    // 定位信息
    private var mLocationInfo: LocationInfo = LocationInfo()
    private var mCityId: String = ""
    private var mCityName: String = ""
    private var titleSearch: TitleOfSearchNewView? = null
    private var fragmentAdapter: FragPagerItemAdapter? = null
    private var mOnShowtimeFragment: TicketMoviesOnShowFragment? = null
    private var mCinemaFragment: TabTicketCinemaFragment? = null
    private var mInComingFragment: TicketMoviesInComingFragment? = null

    companion object {
        fun newInstance() = TicketFragment()
    }

    override fun initVM() = viewModels<TicketViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
                .transparentStatusBar(isFitsSystemWindows = false)
                .statusBarDarkFont(true)
    }

    override fun initTitleBar(): ITitleBarOfFragment {
        return TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
                .setTitle(
                        titleRes = R.string.ticket_tab_title
                )
    }

    override fun initView() {
        mBinding?.apply {
            // tab
            fragmentAdapter = FragPagerItemAdapter(
                    childFragmentManager,
                    FragPagerItems(mContext).apply {
                        add(
                                title = getString(R.string.ticket_tab_hot_show),
                                clazz = TicketMoviesOnShowFragment::class.java
                        )
                        add(
                                title = getString(R.string.ticket_tab_cinema),
                                clazz = TabTicketCinemaFragment::class.java
                        )
                        add(
                                title = getString(R.string.ticket_tab_in_comming),
                                clazz = TicketMoviesInComingFragment::class.java
                        )
                    }
            )
            viewPager.adapter = fragmentAdapter
            viewPager.offscreenPageLimit = 3
            tabLayout.setViewPager(viewPager)
            tabLayout.setSelectedAnim()
            // fragment
            mOnShowtimeFragment = (fragmentAdapter?.getPage(0))?.let {
                it as TicketMoviesOnShowFragment
            }
            mCinemaFragment = (fragmentAdapter?.getPage(1))?.let {
                it as TabTicketCinemaFragment
            }
            mInComingFragment = (fragmentAdapter?.getPage(2))?.let {
                it as TicketMoviesInComingFragment
            }
            // 搜索
            setSearch()
        }
    }

    /**
     * 从其他页面往指定tab跳转
     */
    override fun onPageFlag(flag: PageFlag) {
        super.onPageFlag(flag)
        mBinding?.apply {
            viewPager.currentItem = flag.position
        }
    }

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        requestData()
    }

    /**
     * 请求数据
     */
    private fun requestData() {
        mCityId = GlobalDimensionExt.getCurrentCityId()
        mCityName = GlobalDimensionExt.getCurrentCityName()
        mLocationInfo = LocationInfo().apply {
            cityId = mCityId
            cityName = mCityName
            latitude = GlobalDimensionExt.getLatitude()
            longitude = GlobalDimensionExt.getLongitude()
        }
        // 城市名
        setCityName()
        // banner
        requestBanner()
        // 刷新热映
        refreshTabList(CommConstant.TYPE_TICKET_HOME_MOVIE_HOT)
        // 刷新影院
        refreshTabList(CommConstant.TYPE_TICKET_HOME_MOVIE_CINEMA)
        // 刷新待映
        refreshTabList(CommConstant.TYPE_TICKET_HOME_MOVIE_INCOMING)
    }

    /**
     * 加载banner
     */
    fun requestBanner() {
        mViewModel?.loadBanner()
    }

    /**
     * 刷新指定tab数据
     */
    private fun refreshTabList(curType: Int) {
        mBinding?.apply {
            when (curType) {
                CommConstant.TYPE_TICKET_HOME_MOVIE_HOT -> { // 热映
                    mOnShowtimeFragment?.let { frag ->
                        if (frag.needRequest(mCityId) || frag.listMovieData.isNullOrEmpty()) {
                            // 热映列表
                            frag.onLoadData()
                        }
                    }
                }
                CommConstant.TYPE_TICKET_HOME_MOVIE_CINEMA -> { // 影院
                    mCinemaFragment?.let { frag ->
                        if ((frag.needRequest(mLocationInfo))) {
                            // 刷新影院列表页
                            frag.setFirstLoad()
                            frag.cancelSearch()
                            frag.updateDataAfterLocation()
                        }
                    }
                }
                CommConstant.TYPE_TICKET_HOME_MOVIE_INCOMING -> { // 待映
                    mInComingFragment?.let { frag ->
                        if (frag.needRequest(mCityId) || frag.attentionsBean.isNullOrEmpty()) {
                            frag.reLoadData()
                        }
                    }
                }
                else -> {
                }
            }
        }
    }

    override fun startObserve() {
        mViewModel?.uIState?.observe(this) {
            it.apply {
                success?.apply {
                    showBanner(this)
                }
                error?.apply {
                    showBanner()
                }
                netError?.apply {
                    showBanner()
                }
            }
        }
    }

    /**
     * 设置城市名称
     */
    private fun setCityName() {
        mBinding?.apply {
            // 长度
            if (mCityName.length > mCityNameMaxLength) {
                mCityName = mCityName.substring(0, mCityNameMaxLength - 1).plus(mCityNameEllipsize)
            }
            cityTv.text = mCityName
            cityTv.onClick {
                // 选择城市页
                JumpUtil.startCityChangeActivityForResult(context as AppCompatActivity?, null, 0)
            }

        }
    }

    /**
     * 设置搜索
     */
    private fun setSearch() {
        mBinding?.apply {
            // 搜索影院组件
            titleSearch = TitleOfSearchNewView(activity, searchCinemaView.root) { type, content ->
                if (BaseTitleView.ActionType.TYPE_BACK == type) { // 点击"取消"
                    titleSearch?.let {
                        it.setVisibile(View.GONE)
                        it.hideInput()
                    }
                    // 取消搜索
                    mCinemaFragment?.cancelSearch()
                    navLayout.visible()
                } else if (BaseTitleView.ActionType.TYPE_CONTENT_CHANGED == type
                        || BaseTitleView.ActionType.TYPE_SEARCH == type) { // 搜索影院
                    if (content.isEmpty()) {
                        // 清空搜索
                        mCinemaFragment?.clearSearch()
                    } else {
                        // 点击"搜索"：搜索影院
                        mCinemaFragment?.search(content)
                    }
                }
            }
            titleSearch?.let {
                it.setEditHint(getString(R.string.str_title_search_hint_cinemacontent))
                it.setCloseParent(false)
                it.setVisibile(View.GONE)
            }

            // 点击搜索Icon
            searchIv.onClick { _ ->
                when (viewPager.currentItem) {
                    0, 2 -> {
                        // 热映、待映: 跳整站搜索页
                        getProvider(ISearchProvider::class.java)?.startSearchActivity(mContext)
                    }
                    1 -> {
                        // 显示搜索影院布局
                        navLayout.invisible()
                        titleSearch?.let { it ->
                            it.setVisibile(View.VISIBLE)
                            it.editTextConent = ""
                            it.hideClearIcon()
                            it.setFocus()
                        }
                        mCinemaFragment?.showSearchView()
                    }
                }
            }
        }
    }

    /**
     * 显示banner
     */
    private fun showBanner(regionPublish: RegionPublish? = null) {
        mBinding?.apply {
            if (regionPublish == null) {
                bannerLayout.visible(false)
                return
            }
            val bannerItems = TicketBannerItem.converter2BannerItems(regionPublish)
            if(bannerItems.isNullOrEmpty()) {
                bannerLayout.visible(false)
                return
            }
            bannerLayout.visible()
            banner.setRoundCorners(mBannerRoundCorner)
                  .setCommIndicator().adapter = TabTicketBannerAdapter(
                            context = mContext,
                            bannerItems = bannerItems
                        )
        }
    }

    /**
     * 当前page
     */
    fun getCurrentItem(): Int {
        return mBinding?.viewPager?.currentItem ?: -1
    }

}