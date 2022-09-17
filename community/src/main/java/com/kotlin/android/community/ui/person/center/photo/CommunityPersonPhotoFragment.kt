package com.kotlin.android.community.ui.person.center.photo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.databinding.FragCommunityPhotoAlbumBinding
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.community.ui.person.center.photo.CreateAlbumDialogFragment.Companion.TAG_CREATE_DIALOG_FRAGMENT
import com.kotlin.android.community.ui.person.binder.CommunityPersonPhotoCreateBinder
import com.kotlin.android.community.ui.person.center.CommunityPersonContentFragment
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.user.UserStore
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_photo_album.*

/**
 * @author WangWei
 * @date 2020/9/25
 */
class CommunityPersonPhotoFragment : BaseVMFragment<PersonPhotoViewModel, FragCommunityPhotoAlbumBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): PersonPhotoViewModel = viewModels<PersonPhotoViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false
    private var isFirstLoadData = true
    private var userId: Long = 0L
    private var data: ArrayList<MultiTypeBinder<*>> = arrayListOf()


    override fun initView() {
        mRefreshLayout.setEnableRefresh(true)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityPhotoRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    override fun initData() {
        if (arguments != null) {
            userId = arguments?.getLong(KEY_USER_ID, 0)!!
        }
    }

    fun newInstance(userId: Long): CommunityPersonContentFragment? {
        val f = CommunityPersonContentFragment()
        f.arguments = bundler(userId)
        return f
    }

    fun bundler(userId: Long): Bundle {
        return Bundler()
                .putLong(KEY_USER_ID, userId)
                .get()
    }

    override fun onResume() {
        super.onResume()
        //首次加载
        if (!mIsInitData) {
            mIsInitData = true
            mMultiStateView.setViewState(MultiStateView.VIEW_STATE_CONTENT)
            mRefreshLayout.autoRefresh()
        }
    }

    override fun startObserve() {
        LiveEventBus
                .get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java)
                .observe(this, Observer {
                    mIsInitData = false
                })

        //页面数据回调监听
        registerPageUIStageObserve()
    }

    //页面数据回调监听
    private fun registerPageUIStageObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                success?.apply {
                    showData(this.items?: mutableListOf(), isRefresh, noMoreData)
                }

                if (isEmpty) {
                    if (isRefresh) {
                        mRefreshLayout.finishRefresh()
                        showData(mutableListOf(), isRefresh, noMoreData)
                    } else {
                        mRefreshLayout.finishLoadMore(false)
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    if (isRefresh) {
                        mRefreshLayout.finishRefresh()
                    } else {
                        mRefreshLayout.finishLoadMore(false)
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if (isRefresh) {
                        mRefreshLayout.finishRefresh()
                    } else {
                        mRefreshLayout.finishLoadMore(false)
//                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        })
    }

//

    override fun destroyView() {

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        isFirstLoadData = true
        this.data.clear()
        mViewModel?.loadData(false, userId)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true, userId)
    }

    private fun showData(
            data: List<MultiTypeBinder<*>>,
            isRefresh: Boolean,
            noMoreData: Boolean
    ) {
        //自己的主页&&首次加载添加创建图片binder
        if (isFirstLoadData && UserStore.instance.getUser()?.userId == userId) {
            isFirstLoadData = false
            this.data.add(CommunityPersonPhotoCreateBinder().apply {})
        }

        this.data.addAll(data)
        if (isRefresh) {
            mAdapter.notifyAdapterDataSetChanged(this.data,true) {
                if (noMoreData) {
                    mRefreshLayout.finishRefreshWithNoMoreData()
                } else {
                    mRefreshLayout.finishRefresh()
                }
            }
        } else {
            mAdapter.notifyAdapterDataSetChanged(this.data,false) {
                if (noMoreData) {
                    mRefreshLayout.finishLoadMoreWithNoMoreData()
                } else {
                    mRefreshLayout.finishLoadMore()
                }
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mRefreshLayout.autoRefresh()
            }
        }
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityPersonPhotoCreateBinder -> {
               val fragment = CreateAlbumDialogFragment.newInstance()
                fragment.show(childFragmentManager, TAG_CREATE_DIALOG_FRAGMENT)
                fragment.setAddOkOnClickListener(object :
                    CreateAlbumDialogFragment.AddOkOnClickListener {
                    override fun onAddOK() {
                        onRefresh(mRefreshLayout)
                    }
                })
            }
        }
    }

}