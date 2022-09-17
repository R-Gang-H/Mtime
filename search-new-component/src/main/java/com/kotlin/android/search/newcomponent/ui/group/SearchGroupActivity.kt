package com.kotlin.android.search.newcomponent.ui.group

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.Search.KEY_SEARCH_DATA_GROUP
import com.kotlin.android.search.newcomponent.databinding.ActSearchGroupBinding
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete

/**
 * @des 家族搜索(只展示家族的帖子)
 * @author zhangjian
 * @date 2022/3/25 10:59
 */

@Route(path = RouterActivityPath.Search.PAGE_SEARCH_POST_GROUP_ACTIVITY)
class SearchGroupActivity : BaseVMActivity<SearchGroupViewModel, ActSearchGroupBinding>(),
    MultiStateView.MultiStateListener {

    private var mAdapter: MultiTypeAdapter? = null
    private var keyWord = ""
    private var groupId = 0L

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            groupId = getLongExtra(KEY_SEARCH_DATA_GROUP,0L)
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
        mBinding?.mRecycleView?.apply {
            mAdapter = createMultiTypeAdapter(
                this,
                LinearLayoutManager(
                    this@SearchGroupActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
        }
        mBinding?.mRefreshLayout?.setOnRefreshListener {
            loadData(keyWord, groupId,true)
        }
        mBinding?.mRefreshLayout?.setOnLoadMoreListener {
            loadData(keyWord, groupId,false)
        }
        mBinding?.searchCancelTv?.onClick {
            finish()
        }
        mBinding?.mSearchView?.apply {
            setStartIcon()
            setEndIcon()
            searchAction = {
                if (it.event == 1) {
                    // 键盘上的搜索按钮
                    if (it.keyword.isEmpty()) {
                        mBinding?.autoHintLayout?.getCurHintText()?.apply {
                            if (this.isNotEmpty()) {
                                it.keyword = this
                            }
                        }
                    }
                    clearFocus()
                    hideSoftInput()
                    keyWord = it.keyword
                    showProgressDialog(true)
                    loadData(keyWord, groupId,true)
                }
            }
        }

    }

    override fun initData() {

    }

    override fun startObserve() {
        mViewModel?.uiModelState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)
//                mBinding?.mMultiStateView?.complete(it)
                mBinding?.mRefreshLayout?.complete(it)
                if(isEmpty && isRefresh){
                    mBinding?.mMultiStateView?.setEmptyResource(
                        resId = R.drawable.ic_empty,
                        resid = R.string.search_newcomponent_search_hint_empty)
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }
                success?.apply {
                    mAdapter?.notifyAdapterAdded(this)
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

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                showProgressDialog(true)
                loadData(keyWord, groupId,true)
            }
        }
    }

    private fun loadData(keyWord:String,groupId:Long,flag:Boolean){
        if (flag){
            mAdapter?.notifyAdapterClear()
        }
        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
        mViewModel?.searchPost(keyWord, groupId,flag)
    }
}