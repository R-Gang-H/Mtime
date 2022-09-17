package com.kotlin.tablet.ui.history

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.filmlist.HistoryActivityInfo
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.tablet.R
import com.kotlin.tablet.adapter.ContributeSubjectBinder
import com.kotlin.tablet.databinding.ActivityContributeHistoryBinding
import com.kotlin.tablet.view.showContributeDialog

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:投稿历史主题
 **/
@Route(path = RouterActivityPath.TABLET.FILM_LIST_CONTRIBUTE_HISTORY)
class ContributeHistoryActivity :
    BaseVMActivity<ContributeHistoryViewModel, ActivityContributeHistoryBinding>(),
    MultiStateView.MultiStateListener {
    private lateinit var mAdapter: MultiTypeAdapter
    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(
                titleRes = R.string.tablet_film_list_history_subject
            )
            .back {
                finish()
            }
    }

    override fun initTheme() {
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.apply {
            mAdapter = createMultiTypeAdapter(
                mHistoryRv,
                LinearLayoutManager(this@ContributeHistoryActivity)
            ).setOnClickListener(::handleClick)
            mRefreshLayout.apply {
                setEnableRefresh(false)
                setOnLoadMoreListener {
                    mViewModel?.reqHistoryData(false)
                }
            }
            mMultiStateView.setMultiStateListener(this@ContributeHistoryActivity)
        }
    }

    private fun handleClick(view: View, binder: MultiTypeBinder<*>) {
        if (binder is ContributeSubjectBinder) {
            when (view.id) {
                R.id.mSubjectHostLay -> {
                    //活动说明
                    showContributeDialog(
                        getString(R.string.tablet_film_list_activity_info),
                        binder.bean.synopsis
                    )
                }
            }
        }

    }

    override fun initData() {
        mViewModel?.reqHistoryData(true)
    }

    override fun startObserve() {
        mViewModel?.historyUIState?.observe(this) {
            it?.apply {
                mBinding?.mRefreshLayout?.complete(this)
                mBinding?.mMultiStateView?.complete(this)
                success?.apply {
                    if (isRefresh) {
                        mAdapter.notifyAdapterDataSetChanged(getHistoryBinder(this))
                    } else {
                        mAdapter.notifyAdapterAdded(getHistoryBinder(this))
                    }
                }
            }
        }
    }

    private fun getHistoryBinder(historyActivityInfo: HistoryActivityInfo): List<MultiTypeBinder<*>> {
        return historyActivityInfo.activitys?.map { ContributeSubjectBinder(it) } ?: mutableListOf()
    }

    override fun onMultiStateChanged(viewState: Int) {
        if (viewState == MultiStateView.VIEW_STATE_NO_NET || viewState == MultiStateView.VIEW_STATE_ERROR) {
            initData()
        }
    }
}