package com.kotlin.android.community.family.component.ui.clazz

import android.content.Context
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyClassListBinding
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyClassAdapter
import com.kotlin.android.community.family.component.ui.clazz.adapter.FamilyItemBinder
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyClassItem
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyItem
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.user.afterLogin

import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import com.kotlin.android.widget.titlebar.search
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.act_family_class_list.*
import kotlinx.android.synthetic.main.act_family_class_list.mMultiStateView
import kotlinx.android.synthetic.main.act_family_class_list.mRefreshLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/28
 *
 * 家族分类列表
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_CLASS_LIST)
class FamilyClassListActivity: BaseVMActivity<FamilyClassListViewModel, ActFamilyClassListBinding>(), MultiStateView.MultiStateListener, OnLoadMoreListener {

    private val mProvider = getProvider(ICommunityFamilyProvider::class.java)
    private var mCurClassItem: FamilyClassItem? = null
    private var mCurClassPosition = 0
    private lateinit var mAdapter: MultiTypeAdapter
    private var mResumeData = false //第一次不主动加载列表数据

    companion object {
        const val KEY_SELECTED_POSITION = "key_selected_position"

        fun start(context: Context, position: Int = 0) {
            val intent = Intent(context, FamilyClassListActivity::class.java)
            intent.putExtra(KEY_SELECTED_POSITION, position)
            context.startActivity(intent)
        }
    }

    override fun initVM(): FamilyClassListViewModel = viewModels<FamilyClassListViewModel>().value

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        TitleBarManager.with(this)
            .setTitle(getString(R.string.community_family_class_title))
            .back {
                finish()
            }
            .addItem(
                isReversed = true,
                title = getString(R.string.community_create_btn),
                colorRes = R.color.color_000000,
                click = {
                    // 创建家族页
                    afterLogin {
                        mProvider?.startFamilyCreate()
                    }
                }
            )
    }

    override fun initView() {
        mMultiStateView.setMultiStateListener(this)
        mRefreshLayout.setOnLoadMoreListener(this)
        mAdapter = createMultiTypeAdapter(mFamilyRv)
        mAdapter.setOnClickListener(::onBinderClick)
        mListMultiStateView.setMultiStateListener(object : MultiStateView.MultiStateListener {
            override fun onMultiStateChanged(viewState: Int) {
                when (viewState) {
                    MultiStateView.VIEW_STATE_ERROR,
                    MultiStateView.VIEW_STATE_NO_NET -> {
                        mCurClassItem?.let {
                            mViewModel?.loadFamilyList(false, it.id)
                        }
                    }
                }
            }
        })
    }

    override fun initData() {
        mCurClassPosition = intent.getIntExtra(KEY_SELECTED_POSITION, 0)
        mViewModel?.loadClassData()
    }

    override fun onResume() {
        super.onResume()
        //重新加载数据
        if (mResumeData) {
            mCurClassItem?.let {
                mViewModel?.loadFamilyList(false, it.id)
            }
        } else {
            mResumeData = true
        }
    }

    override fun startObserve() {
        //注册家族分类回调监听
        registerFamilyClassObserve()
        //注册家族列表回调监听
        registerFamilyListObserve()
        //注册加入家族回调监听
        registerJoinFamilyObserve()
        //注册退出家族回调监听
        registerOutFamilyObserve()
    }

    //注册家族分类回调监听
    private fun registerFamilyClassObserve() {
        mViewModel?.uiClassState?.observe(this, Observer {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                if (isEmpty) {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                }

                success?.apply {
                    val adapter = FamilyClassAdapter(::onFamilyClassItemClick)
                    adapter.selectedPosition = mCurClassPosition
                    mFamilyClassRv.adapter = adapter
                    adapter.setData(this)
                    onFamilyClassItemClick(adapter.items[mCurClassPosition], mCurClassPosition)
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        })
    }

    //注册家族列表回调监听
    private fun registerFamilyListObserve() {
        mViewModel?.uiListState?.observe(this, Observer {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showListData(this, loadMore, noMoreData)
                }

                if (isEmpty) {
                    if (loadMore) {
                        mRefreshLayout.finishLoadMoreWithNoMoreData()
                    } else {
                        mListMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    if (loadMore) {
                        mRefreshLayout.finishLoadMore(false)
                    } else {
                        mListMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if (loadMore) {
                        mRefreshLayout.finishLoadMore(false)
                    } else {
                        mListMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        })
    }

    //注册加入家族回调监听
    private fun registerJoinFamilyObserve() {
        mViewModel?.mCommJoinFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    extend.joinChanged(result)
                    postJoinFamily()
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    //注册退出家族回调监听
    private fun registerOutFamilyObserve() {
        mViewModel?.mCommOutFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.status == 1L) {
                        extend.outChanged()
                    } else {
                        showToast(result.failMsg)
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    private fun showListData(
            data: List<MultiTypeBinder<*>>,
            isLoadMore: Boolean,
            noMoreData: Boolean
    ) {
        if (!isLoadMore) {
            mListMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
            mAdapter.notifyAdapterDataSetChanged(data) {
                if (noMoreData) {
                    mRefreshLayout.finishRefreshWithNoMoreData()
                } else {
                    mRefreshLayout.resetNoMoreData()
                    mRefreshLayout.finishRefresh()
                }
            }
        } else {
            mAdapter.notifyAdapterAdded(data) {
                if (noMoreData) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    mRefreshLayout.finishLoadMore()
                }
            }
        }
    }

    //分类列表item点击事件回调
    private fun onFamilyClassItemClick(item: FamilyClassItem, position: Int) {
        mCurClassItem = item
        mCurClassItem?.let {
            mViewModel?.loadFamilyList(false, it.id)
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mViewModel?.loadClassData()
            }
        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mCurClassItem?.let {
            mViewModel?.loadFamilyList(true, it.id)
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is FamilyItemBinder -> {
                when (view.id) {
                    R.id.mCommunityFamilyBtnFl -> {
                        joinBtnClickListener(binder)
                    }
                }
            }
        }
    }

    //家族item按钮的点击事件
    private fun joinBtnClickListener(binder: FamilyItemBinder) {
        binder.apply {
            if (item.joinType == FamilyItem.JOIN_TYPE_NO_JOIN) {
                //未加入
                mViewModel?.joinFamily(item.id, this)
            } else {
                //已加入
                mViewModel?.outFamily(item.id, this)
            }
        }
    }
}