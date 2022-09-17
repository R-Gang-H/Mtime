package com.kotlin.android.community.ui.person.timeline

import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ActCommunityTimeLineBinding
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBarManager

/**
 * 社区-我的好友
 * @author WangWei
 * @data 2020/9/30
 */
@Route(path = RouterActivityPath.Community.PAGER_TIME_LINE)
class CommunityTimeLineActivity :
    BaseVMActivity<CommunityPersonViewModel, ActCommunityTimeLineBinding>(),
    MultiStateView.MultiStateListener {
    private var dataAdapter: MultiTypeAdapter? = null

    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.time_line),
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
            )
    }


    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
        mBinding?.mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(this@CommunityTimeLineActivity)
            dataAdapter = createMultiTypeAdapter(this, layoutManager)
            dataAdapter?.setOnClickListener(::onBinderClick)
            adapter = dataAdapter
        }
        initRefreshLayout()
    }


    override fun startObserve() {
        mViewModel?.mTimeLineViewModel?.uiState?.observe(this) {
            it.observerMethod()
        }
    }

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    fun <T : Any> BinderUIModel<T, List<MultiTypeBinder<*>>>.observerMethod() {
        this.apply {
            binders?.apply {
                setViewState(MultiStateView.VIEW_STATE_CONTENT)

                if (isRefresh) {//删除原来数据，再添加新数据
                    dataAdapter?.notifyAdapterRemoved(
                        dataAdapter?.getList()?.toMutableList() ?: mutableListOf()
                    ) {
                        dataAdapter?.notifyAdapterAdded(this)
                    }
                } else {//不是下拉刷新直接添加
                    dataAdapter?.notifyAdapterAdded(this)
                }
                mBinding?.mRefreshLayout?.setNoMoreData(noMoreData)
            }

            showLoading?.apply {
                showProgressDialog(showLoading)
            }

            success?.apply {
                finishLoad()
            }

            isEmpty?.apply {//下拉刷新、空、无旧数据才显示
                if (isRefresh && isEmpty && hasDataList().not())
                    setViewState(MultiStateView.VIEW_STATE_EMPTY)
            }

            netError?.apply {
                finishLoad()
                if (isRefresh && hasDataList().not()) {//下拉刷新、无旧数据才显示
                    setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }

            error?.apply {
                finishLoad()
                if (isRefresh && hasDataList().not()) {//下拉刷新、无旧数据才显示
                    setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
            }
        }
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
     * 判断是否有数据
     * 有true
     * 无false
     */
    private fun hasDataList(): Boolean {
        return dataAdapter?.itemCount ?: 0 > 0
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
                loadData(false)
            }

        }
    }


    private fun finishLoad() {
        mBinding?.mRefreshLayout?.finishLoadMore()
        mBinding?.mRefreshLayout?.finishRefresh()
    }

    private fun setViewState(@MultiStateView.ViewState state: Int) {
        mBinding?.mMultiStateView?.setViewState(state)
    }

    /**
     *  加载数据
     */
     fun loadData(isRefresh: Boolean = false){
         mViewModel?.loadTimeLine(isRefresh)
     }

    /**
     * 需要回调再实现
     * 关注、订阅之类
     */
    open fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {}


}