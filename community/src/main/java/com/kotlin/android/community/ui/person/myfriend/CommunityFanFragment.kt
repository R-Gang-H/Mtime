package com.kotlin.android.community.ui.person.myfriend

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityPersonFanListBinding
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import com.kotlin.android.community.ui.person.binder.CommunityPersonFriendBinder
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.user.afterLogin
import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.ogaclejapan.smarttablayout.utils.v4.Bundler
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.frag_community_person_content_list.mMultiStateView
import kotlinx.android.synthetic.main.frag_community_person_content_list.mRefreshLayout
import kotlinx.android.synthetic.main.frag_community_person_fan_list.*

/**
 * @author WangWei
 * @date 2020/9/30
 * 社区个人主页 粉丝页
 */
class CommunityFanFragment : BaseVMFragment<MyFriendViewModel, FragCommunityPersonFanListBinding>(),
        OnRefreshLoadMoreListener, MultiStateView.MultiStateListener {

    override fun initVM(): MyFriendViewModel = viewModels<MyFriendViewModel>().value

    private lateinit var mAdapter: MultiTypeAdapter
    private var mIsInitData = false

    private var KEY_USERID = "user_id"
    private var KEY_TYPE = "type"
    fun newInstance(userId: Long, type: Long): CommunityFanFragment? {
        val f = CommunityFanFragment()
        f.arguments = bundler(userId, type)
        return f
    }

    fun bundler(userId: Long, type: Long): Bundle {
        return Bundler()
                .putLong(KEY_USERID, userId)
                .putLong(KEY_TYPE, type)
                .get()
    }

    override fun initView() {
//        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnRefreshLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
        mAdapter = createMultiTypeAdapter(mCommunityFanRv)
        mAdapter.setOnClickListener(::onBinderClick)
    }

    var userId: Long = 0L
    var type: Long = 0L
    override fun initData() {
        if (arguments != null) {
            type = arguments?.getLong(KEY_TYPE, 0)!!
            userId = arguments?.getLong(KEY_USERID, 0)!!
        }
    }

    override fun onResume() {
        super.onResume()
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

        //页面数据监听
        registerPageUIStateObserve()
    }

    //页面数据监听
    private fun registerPageUIStateObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                success?.apply {
                    if (isRefresh) {
                        mAdapter?.notifyAdapterDataSetChanged(this)
                    } else mAdapter?.notifyAdapterAdded(this)
                }
            }
        })

        mViewModel?.mFollowViewModel?.uiState?.observe(this, Observer {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.bizCode == 0L) {//

                    } else {//

                    }
                }
                error?.apply {
                    //变回原状态
                }

                netError?.apply {
                    //变回原状态
                }
            }
        })
    }


    override fun destroyView() {
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mViewModel?.loadDataFolowAndFan(false, type, userId)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mViewModel?.loadDataFolowAndFan(true, type, userId)
    }

    private fun showData(
            data: List<MultiTypeBinder<*>>,
            isLoadMore: Boolean,
            noMoreData: Boolean
    ) {
        if (!isLoadMore) {
            mAdapter.notifyAdapterDataSetChanged(data) {
                if (noMoreData) {
                    mRefreshLayout.finishRefreshWithNoMoreData()
                } else {
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

    /**
     * 从MultiTypeBinder回调的一些点击事件
     * 此事件已通过Adapter注册
     */
    private fun onBinderClick(view: View, binder: MultiTypeBinder<*>) {
        when (binder) {
            is CommunityPersonFriendBinder -> {
                when (view.id) {
                    R.id.tv_attend -> {
                        attendAction(binder)
                    }
                }
            }
        }
    }


    private fun attendAction(binder: CommunityPersonFriendBinder) {
        afterLogin {//关注需要登录 ，其他进入社区个人主页不需要登录
            var followed = binder.bean.followed
            when (followed) {
                true -> {//取关操作
                    BaseDialog.Builder(mContext).setContent(R.string.attend_tip).setPositiveButton(R.string.ok, DialogInterface.OnClickListener { dialogInterface, i ->
                        followAction(binder.bean.userId, CommunityPersonViewModel.ACTION_CANCEL)
                        dialogInterface.dismiss()
                        binder.bean.followed = false
                        binder.binding?.data = binder

                    }).setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i -> dialogInterface.dismiss() }).create().show()

                }
                false -> {//关注操作
                    followAction(binder.bean.userId, CommunityPersonViewModel.ACTION_POSITIVE)
                    binder.bean.followed = true
                    binder.binding?.data = binder
                }

            }
        }
    }


    /**
     * 点击"关注/已关注"按钮
     * @param action 目标操作
     */
    private fun followAction(userId: Long, action: Long) {
        afterLogin {
            if (userId > 0) {
                mViewModel?.follow(userId, action)
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
}