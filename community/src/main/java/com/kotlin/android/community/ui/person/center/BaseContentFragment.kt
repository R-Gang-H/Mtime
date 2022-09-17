package com.kotlin.android.community.ui.person.center

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.kotlin.android.api.base.BinderUIModel
import com.kotlin.android.community.databinding.FragCommunityPersonContentListBinding
import com.kotlin.android.community.ui.person.CONTENT_TYPE
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.ogaclejapan.smarttablayout.utils.v4.Bundler

/**
 * @author wangwei 2022.3.26
 * 个人主页内容基类
 */
abstract class BaseContentFragment<VM : BaseViewModel, VB : ViewBinding> :
    BaseVMFragment<PersonCenterViewModel, FragCommunityPersonContentListBinding>(),
    MultiStateView.MultiStateListener {
    var userId = 0L//用户id
    var type = 0L//用户id
    var contentType = 1L//内容类型 1L主页 2L收藏
    private var dataAdapter: MultiTypeAdapter? = null

    /* fun newInstance(userId: Long, type: Long): CommunityCenterArticleFragment? {
         val f = CommunityCenterArticleFragment()
         f.arguments = bundler(userId, type)
         return f
     }
 */
    fun bundler(userId: Long, type: Long=0L, contentType: Long = 1L): Bundle {
        return Bundler()
            .putLong(KEY_USER_ID, userId)
            .putLong(KEY_TYPE, type)
            .putLong(CONTENT_TYPE, contentType)
            .get()
    }

    override fun initView() {
        userId = arguments?.getLong(KEY_USER_ID, 0L) ?: 0
        type = arguments?.getLong(KEY_TYPE, 0L) ?: 0
        contentType = arguments?.getLong(CONTENT_TYPE, 1L) ?: 1L

        mBinding?.mMultiStateView?.setMultiStateListener(this)
        mBinding?.mRecyclerView?.apply {
            layoutManager = LinearLayoutManager(activity)
            dataAdapter = createMultiTypeAdapter(this, layoutManager)
            dataAdapter?.setOnClickListener(::onBinderClick)
            adapter = dataAdapter
        }
        initRefreshLayout()
    }

    override fun startObserve() {
        startMyPageObserve()
    }

    abstract fun startMyPageObserve()

    override fun initData() {
        mBinding?.mRefreshLayout?.autoRefresh()
    }

    fun <T : Any> BinderUIModel<T, List<MultiTypeBinder<*>>>.observerMethod() {
        this.apply {
            mBinding?.mRefreshLayout.complete(this)
//            mBinding?.mMultiStateView.complete(this)

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
//                mBinding?.mRefreshLayout?.setNoMoreData(noMoreData)
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
                showToast(this)
            }

            error?.apply {
                finishLoad()
                if (isRefresh && hasDataList().not()) {//下拉刷新、无旧数据才显示
                    setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
                showToast(this)

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
    abstract fun loadData(isRefresh: Boolean = false)

    /**
     * 需要回调再实现
     * 关注、订阅之类
     */
    open fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {}

    override fun onDestroyView() {
        super.onDestroyView()
//        mBinding?.mMultiStateView?.setMultiStateListener(null)
    }

    override fun onRefresh(any: Any?) {
        super.onRefresh(any)
        mBinding?.mRefreshLayout?.autoRefresh()
    }
}