package com.kotlin.android.home.ui.home

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.kotlin.android.app.data.entity.home.HomeTabNavList
import com.kotlin.android.app.data.entity.js.sdk.BrowserEntity
import com.kotlin.android.app.router.provider.daily.IDailyProvider
import com.kotlin.android.app.router.provider.message_center.IMessageCenterProvider
import com.kotlin.android.app.router.provider.message_center.UnReadMessageObserver
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.FragHomeBinding
import com.kotlin.android.home.ui.original.OriginalFragment
import com.kotlin.android.home.ui.recommend.RecommendFragment
import com.kotlin.android.home.ui.tashuo.TaShuoFragment
import com.kotlin.android.home.ui.zhongcao.ZhongCaoFragment
import com.kotlin.android.ktx.ext.KEY_DATA
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.core.marginTop
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.statusBarHeight
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.GlobalDimensionExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.formatMsgCount
import com.kotlin.android.qb.common.H5Fragment
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.put
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setCustomTabView
import com.kotlin.android.widget.titlebar.item.HomeCenterTitleView
import com.kotlin.android.widget.titlebar.message
import java.util.*


/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/7
 */
class HomeFragment : BaseVMFragment<HomeViewModel, FragHomeBinding>(), LifecycleObserver,
    MultiStateView.MultiStateListener {

    private var curCityId: String = ""

    init {
        lifecycle.addObserver(this)
    }


    private var unReadMessageObserver: UnReadMessageObserver? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun unRegisterUnreadMessageCount() {
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.removeUnreadMessageCountObserver(it)
        }
    }

    /**
     * 添加未读消息数的监听
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerUnreadMessageCount() {
        if (unReadMessageObserver == null) {
            unReadMessageObserver = object : UnReadMessageObserver {
                override fun onNotifyMessageCount(totalCount: Long) {
                    // 更新消息中心红点
                    mBinding?.titleBar?.apply {
                        updateRedPoint(
                            isReversed = true,
                            isShow = totalCount != 0L,
                            title = formatMsgCount(totalCount)
                        )
                    }
                }
            }
        }
        unReadMessageObserver?.let {
            getProvider(IMessageCenterProvider::class.java)?.addUnreadMessageCountObserver(it)
        }
    }

    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar(isFitsSystemWindows = false)
            .statusBarDarkFont(true)
    }

    private fun initSearchTitleBar() {
        mBinding?.titleBar?.apply {
            marginTop = statusBarHeight
            initMargin(11.dp, 11.dp)
            addItem(
                title = TimeExt.getValueByCalendarField(Calendar.DAY_OF_MONTH).toString(),
                bgDrawable = getCompoundDrawable(R.drawable.ic_home_calendar, 30.dp, 30.dp),
                gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM,
                textSize = 12f,
                isBold = true,
                colorRes = R.color.color_1ea3db,
                titlePaddingBottom = 4.dp,
                titleWidth = 30.dp,
                titleHeight = 30.dp
            ) {
                //今日推荐
                getProvider(IDailyProvider::class.java)
                    ?.startDailyRecommendActivity()
            }
            addCenterView(
                HomeCenterTitleView(mContext).apply {
                    action = {
                        //扫一扫
                        getProvider(IQRcodeProvider::class.java)
                            ?.startQrScanActivity()
                    }
                    onClick {
                        //搜索
                        getProvider(ISearchProvider::class.java)
                            ?.startSearchActivity(activity)
                    }
                }
            )
            message {
                getProvider(IMessageCenterProvider::class.java)
                    ?.startMessageCenterActivity(context as Activity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (curCityId != GlobalDimensionExt.getCurrentCityId()) {
            curCityId = GlobalDimensionExt.getCurrentCityId()
            mViewModel?.loadNav(curCityId)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mBinding?.apply {
            // 此处的作用是main tab切换后不走生命周期方法，所以在此主动通知子fragment的onHiddenChanged，
            // 目前主要用于通知推荐页面暂停播放器和恢复播放
            (mHomeViewPager.adapter as? FragPagerItemAdapter)?.getItem(mHomeViewPager.currentItem)?.onHiddenChanged(hidden)
        }
    }

    override fun initView() {
        mBinding?.stateView?.setMultiStateListener(this)
        initSearchTitleBar()
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)
                if (showLoading.not()) {
                    mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                    if (netError != null) {
                        mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    } else if (null != success) {
                        setNavData(success!!)
                    } else {
                        setNavData(HomeTabNavList.getDefaultNavList())
                    }
                }
            }
        }
    }

    //设置导航数据
    private fun setNavData(navList: HomeTabNavList) {
        mBinding?.apply {
            val creator = FragPagerItems(mContext)
            navList.tabList.forEach {
                addFragment(it, creator)
            }

            val adapter = FragPagerItemAdapter(childFragmentManager, creator)
            mHomeViewPager.adapter = adapter
            mHomeTabLayout.setCustomTabView(R.layout.widget_tab_fixed_item, true)
            mHomeViewPager.offscreenPageLimit = 8
            mHomeTabLayout.setViewPager(mHomeViewPager)
        }
    }

    //添加tab对应的Fragment
    private fun addFragment(tabNav: HomeTabNavList.TabNav, creator: FragPagerItems) {
        val fragment: Class<out Fragment>
        val bundle = Bundle()
        if (tabNav.redirectType == 2L) {
            fragment = H5Fragment::class.java
            bundle.put(
                KEY_DATA,
                BrowserEntity(url = tabNav.h5Url.orEmpty())
            )
        } else {
            fragment = when (tabNav.appPageCode) {
                HomeTabNavList.TYPE_RECOMMEND -> {
                    RecommendFragment::class.java
                }
                HomeTabNavList.TYPE_ORIGINAL -> {
                    OriginalFragment::class.java
                }
                HomeTabNavList.TYPE_TA_SHUO -> {
                    TaShuoFragment::class.java
                }
                HomeTabNavList.TYPE_ZHONG_CAO -> {
                    ZhongCaoFragment::class.java
                }
                else -> {
                    H5Fragment::class.java
                }
            }
        }
        tabNav.name?.apply {
            if (this.isNotEmpty()) {
                creator.add(
                    title = this,
                    tag = tabNav.tips.orEmpty(),
                    clazz = fragment,
                    args = bundle
                )
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                curCityId = GlobalDimensionExt.getCurrentCityId()
                mViewModel?.loadNav(curCityId)
            }
        }
    }

}