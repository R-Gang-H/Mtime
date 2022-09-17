package com.kotlin.android.community.ui.person.wantsee

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.community.R
import com.kotlin.android.community.card.component.item.adapter.CommunityCardBaseBinder
import com.kotlin.android.community.databinding.FragCommunityWantseeListBinding
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.textHighLight
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_wantsee_list.*

/**
 * @author WangWei
 * @date 2020/10/13
 * 社区个人主页 想看页
 */
class CommunityWantSeeFragment : BaseVMFragment<CommunityWantSeeViewModel, FragCommunityWantseeListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): CommunityWantSeeViewModel = viewModels<CommunityWantSeeViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter

    fun newInstance(userId: Long, type: Long): CommunityWantSeeFragment {
        val f = CommunityWantSeeFragment()
        f.arguments = bundler(userId, type)
        return f
    }

    fun bundler(userId: Long, type: Long): Bundle {
        return Bundler()
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_TYPE, type)
                .get()
    }

    override fun initView() {
        mRefreshLayout.setEnableRefresh(true)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityWantSeeRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    var userId: Long = 0L
    var type: Long = 0L
    override fun initData() {
        if (arguments != null) {
            type = arguments?.getLong(KEY_TYPE, 0)!!
            userId = arguments?.getLong(KEY_USER_ID, 0)!!
        }
        mRefreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        //页面数据监听
        registerPageUIStateObserve()
    }

    //页面数据监听
    private fun registerPageUIStateObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                mBinding?.mMultiStateView?.complete(this)
                mBinding?.mRefreshLayout?.complete(this)

                success?.apply {
                    setCountTv(this.count)

                    binders?.apply {
                        if (isRefresh) {
                            mAdapter?.notifyAdapterDataSetChanged(this)
                        } else
                            mAdapter?.notifyAdapterAdded(this)
                    }
                }
            }
        })
    }


    override fun destroyView() {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(true, userId, type)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadData(false, userId, type)
    }

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityCardBaseBinder -> {
                //社区帖子相关点击事件
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
     * 设置共有个数
     */
    private fun setCountTv(totalNum: Long) {
        val text = String.format(getString(R.string.favourite_movie_num, totalNum))
        context?.apply {
            countTv.textHighLight(text, ContextCompat.getColor(this, R.color.color_20a0da))
        }
        countTv?.visible()
    }

}