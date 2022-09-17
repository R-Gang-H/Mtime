package com.kotlin.android.mine.ui.setting

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.binder.ThirdAccountBinder
import com.kotlin.android.mine.databinding.ActivityThirdAccountBindBinding
import com.kotlin.android.mine.ui.setting.viewmodel.ThirdAccountViewModel
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBarManager
import kotlinx.android.synthetic.main.frag_contents.*

@Route(path = RouterActivityPath.Mine.PAGE_THIRD_ACCOUNT_BIND_ACTIVITY)
class ThirdAccountActivity :
    BaseVMActivity<ThirdAccountViewModel, ActivityThirdAccountBindBinding>() {

    private val mAdapter: MultiTypeAdapter by lazy {
        createMultiTypeAdapter(contentRv, LinearLayoutManager(this)).apply {
            setOnClickListener { view, binder ->
                when (view.id) {
                    R.id.bindStateTv -> {
                        if ((binder as ThirdAccountBinder).bean.bindStatus == 0) {
                            mViewModel?.unbindAccount(binder.bean.platformId)
                        }
                    }
                }
            }
        }
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_third_account_bind)
            .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                onBackPressed()
            }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {

    }

    override fun initData() {
        mViewModel?.getBindList(this)
    }

    override fun startObserve() {
        mViewModel?.bindState?.observe(this) {
            it?.apply {
                success?.run {
                    setContentState(MultiStateView.VIEW_STATE_CONTENT)
                    loadComplete()
                    mAdapter.notifyAdapterDataSetChanged(this.list)
                    if (this.list.isEmpty()) {
                        setContentState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }
                netError?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果没有数据是显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
                error?.run {
                    loadComplete()
                    if (mAdapter?.itemCount ?: 0 == 0) {//如果页面上没有显示数据，需要显示加载失败重试页面
                        setContentState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }
            }
        }
        // 解绑
        mViewModel?.unbindState?.observe(this) {
            it?.apply {
                success?.run {
                    mViewModel?.getBindList(this@ThirdAccountActivity)
                }
                netError?.run {

                }
                error?.run {

                }
            }
        }
    }

    private fun setContentState(@MultiStateView.ViewState state: Int) {
        mBinding?.stateView?.setViewState(state)
    }

    private fun loadComplete() {
        mBinding?.apply {
            refreshLayout?.finishRefresh()
            refreshLayout?.finishLoadMore()
        }
    }
}