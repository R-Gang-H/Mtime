package com.kotlin.tablet.ui.contribute

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.ext.AppLinkExtra
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.tablet.ITabletProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.*
import com.kotlin.tablet.PAGE_CONTRIBUTE_ACTIVITY
import com.kotlin.tablet.PAGE_SUCCESS_ACTIVITY
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.ContributeShortlistBinder
import com.kotlin.tablet.adapter.ContributeShortlistParentBinder
import com.kotlin.tablet.adapter.ContributeSubjectBinder
import com.kotlin.tablet.adapter.ContributeSubjectTitleBinder
import com.kotlin.tablet.databinding.ActivityContributeBinding
import com.kotlin.tablet.event.FilmListPageCloseEvent
import com.kotlin.tablet.view.showContributeBottomDialog
import com.kotlin.tablet.view.showContributeDialog

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE)
class ContributeActivity : BaseVMActivity<ContributeViewModel, ActivityContributeBinding>(),
    MultiStateView.MultiStateListener {
    private lateinit var mAdapter: MultiTypeAdapter

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                titleRes = R.string.tablet_main_right_title
            )
            .back {
                finish()
            }.share1 {
                mViewModel?.getContributeShareInfo()
            }
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter =
                createMultiTypeAdapter(
                    mContributeRv,
                    LinearLayoutManager(this@ContributeActivity)
                ).setOnClickListener(::handleClick)
            mMultiStateView.setMultiStateListener(this@ContributeActivity)
        }
    }


    override fun initData() {
        mViewModel?.reqCurrActivities()
    }

    override fun startObserve() {
        observePageClose()
        observeContributeInfo()
        observeShare()
    }

    /**
     * 片单活动分享
     */
    private fun observeShare() {
        mViewModel?.shareUIState?.observe(this) {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // 显示分享弹窗
                    showShareDialog(
                        ShareEntity.build(this), ShareFragment.LaunchMode.STANDARD, event = null
                    )
                }
                error?.showToast()
                netError?.showToast()
            }
        }
    }

    private fun observeContributeInfo() {
        mViewModel?.currActivityUiState?.observe(this) {
            it.apply {
                showProgressDialog(it.showLoading)
                success?.let { info ->
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                    mAdapter.notifyAdapterDataSetChanged(mutableListOf<MultiTypeBinder<*>>().apply {
                        add(ContributeSubjectTitleBinder())
                        info.activitys?.forEach {
                            add(ContributeSubjectBinder(it))
                        }
                        add(ContributeShortlistParentBinder(info.talentStatistics))
                    })
                }
                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    private fun handleClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is ContributeSubjectTitleBinder -> {
                if (view.id == R.id.mHistorySubjectLay) {
                    getProvider(ITabletProvider::class.java)
                        ?.startContributeHistoryActivity()
                }
            }
            is ContributeSubjectBinder -> {
                when (view.id) {
                    R.id.mSubjectHostLay -> {
                        //活动说明
                        showContributeDialog(
                            getString(R.string.tablet_film_list_activity_info),
                            binder.bean.synopsis
                        )
                    }
                    R.id.mContributeTv -> {
                        //投稿
                        showContributeBottomDialog(mutableListOf<String>().apply {
                            add(getString(R.string.tablet_film_list_from_my_create))
                            add(getString(R.string.tablet_film_list_new_create))
                        }) {
                            when (it) {
                                0 -> {
                                    //从我的片单选择
                                    getProvider(ITabletProvider::class.java)
                                        ?.startMyCreateActivity(binder.bean.activityId)
                                }
                                1 -> {
                                    //新建片单
                                    getProvider(ITabletProvider::class.java)?.startFilmListCreateActivity(
                                        false
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is ContributeShortlistParentBinder -> {
                when (view.id) {
                    R.id.mInfoIv -> {
                        //达人榜说明
                        showContributeDialog(
                            getString(R.string.tablet_film_list_shortlist_info),
                            getString(R.string.tablet_film_list_shortlist_info_content)
                        )
                    }
                }
            }
        }

    }

    /**
     * 关闭当前界面
     */
    private fun observePageClose() {
        observe(FilmListPageCloseEvent::class.java) {
            if (it.page == PAGE_CONTRIBUTE_ACTIVITY) {
                finish()
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        initData()
    }
}