package com.kotlin.android.home.ui

import com.kotlin.android.app.router.provider.video.IVideoProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.ext.observeLiveData
import com.kotlin.android.home.R
import com.kotlin.android.home.databinding.ActTrailerRcmdListBinding
import com.kotlin.android.home.databinding.ItemTrailerListItemBinding
import com.kotlin.android.home.ui.recommend.bean.TrailerItem
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.bindingadapter.BaseBindingAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * 首页-推荐-每日佳片-全部->推荐列表页
 */
class RcmdTrailerListActivity : BaseVMActivity<RcmdTrailerListVIewModel, ActTrailerRcmdListBinding>(),
    MultiStateView.MultiStateListener {

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(
                titleRes = R.string.home_rcmd_trailer_list_title,
            )
            .back {
                finish()
            }
    }
    
    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun initData() {
        mViewModel?.loadData()
    }

    override fun startObserve() {
        observeLiveData(mViewModel?.uiState) {
            mBinding?.apply { 
                it.apply { 
                    showOrHideProgressDialog(showLoading)
                    mMultiStateView.complete(this)
                    
                    success?.apply {
                        recyclerView.adapter = ListAdapter(this)
                    }
                }
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }

    private class ListAdapter(items: List<TrailerItem>) 
        : BaseBindingAdapter<TrailerItem, ItemTrailerListItemBinding>(items) {
        override fun onBinding(
            binding: ItemTrailerListItemBinding?,
            item: TrailerItem,
            position: Int
        ) {
            binding?.apply {
                line.visible(position != 0)
                root.onClick {
                    getProvider(IVideoProvider::class.java) {
                        startPreVideoActivity(item.videoId)
                    }
                }
            }
        }
    }
}