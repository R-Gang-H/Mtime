package com.kotlin.android.community.family.component.ui.manage

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyMemberBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilyMemberItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.community.family.component.ui.manage.widget.FloatingItemDecoration
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.community.group.GroupTypeUserList
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.spF
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.user.afterLogin

import com.kotlin.android.router.liveevent.LOGIN_STATE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.act_family_member.*
import kotlinx.android.synthetic.main.act_family_member.mRefreshLayout
import java.util.*

/**
 * @author vivian.wei
 * @date 2020/8/12
 * @desc ?????????????????????
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_MEMBER)
class FamilyMemberActivity: BaseVMActivity<FamilyMemberViewModel, ActFamilyMemberBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    companion object {
        // ????????????title
        val FLOATING_TITLE_HEIGHT: Int = 30.dp               // dp
        val FLOATING_TITLE_TEXT_PADDING_LEFT: Int = 15.dp    // dp
        val FLOATING_TITLE_TEXT_SIZE: Float = 12.spF
    }

    private val mFamilyProvider = getProvider(ICommunityFamilyProvider::class.java)

    private var mGroupId: Long = 0
    private var mPageIndex = 1
    private var mFollowUserId: Long = 0
    private var mTypeList = ArrayList<GroupTypeUserList>()                // ????????????
    private lateinit var mFloatingItemDecoration: FloatingItemDecoration  // ????????????title
    private lateinit var mTitleBar: CommonTitleBar
    private lateinit var mAdapter: MultiTypeAdapter
    private var mAllBinderList = ArrayList<MultiTypeBinder<*>>()

    override fun initVM(): FamilyMemberViewModel {
        mGroupId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0)
        return viewModels<FamilyMemberViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        mTitleBar = CommonTitleBar()
        mTitleBar.init(this)
                .setTitle(R.string.family_member_list)
                .setRightClickListener(View.OnClickListener {
                    // ??????"??????": ?????????????????????
                    mFamilyProvider?.startFamilyMemberManage(this, mGroupId, FamilyConstant.REQUEST_CODE_MEMBER_MANAGE)
                })
                .setRightTextColor(R.color.color_8798af)
                .setRightTextStyle(Typeface.BOLD)
                .create()
    }

    override fun initView() {
        mAdapter = createMultiTypeAdapter(mActFamilyMemberRv, LinearLayoutManager(this))
        // ??????title????????????
        mFloatingItemDecoration = FloatingItemDecoration(this)
        mFloatingItemDecoration.setTitleHeight(FLOATING_TITLE_HEIGHT)
        mFloatingItemDecoration.setTitleViewmTitleViewPaddingLeft(FLOATING_TITLE_TEXT_PADDING_LEFT)
        mFloatingItemDecoration.setTtitleBackground(ContextCompat.getColor(this, R.color.color_f2f3f6_alpha_92))
        mFloatingItemDecoration.setTitleTextColor(ContextCompat.getColor(this, R.color.color_8798af))
        mFloatingItemDecoration.setTitleTextSize(FLOATING_TITLE_TEXT_SIZE)
        mFloatingItemDecoration.setTitleTextBold(true)
        mActFamilyMemberRv.addItemDecoration(mFloatingItemDecoration)

        mRefreshLayout.setFooterHeight(100f)
        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        // ???????????????????????????+??????????????????
        mViewModel?.getAdminActiveMemberList(mGroupId)
    }

    override fun startObserve() {
        // ?????????????????????+??????????????????
        observeAdminActiveList()
        // ??????????????????
        observeMemberList()
        // ??????
        observeFollow()
        // ?????????????????????????????????
        loginEventObserve()
    }

    /**
     * observe ?????????????????????+??????????????????
     */
    private fun observeAdminActiveList() {
        mViewModel?.adminActiveListUiState?.observe(this, Observer {
            it.apply {

                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // ??????/?????????
                    this.administratorList?.let {
                        var typeUserList = GroupTypeUserList()
                        typeUserList.typeName = getString(R.string.family_member_title_creator_admin)
                        typeUserList.userList = it
                        mTypeList.add(typeUserList)
                    }
                    // ????????????
                    this.activeMemberList.let {
                        var typeUserList = GroupTypeUserList()
                        typeUserList.typeName = getString(R.string.family_member_title_active)
                        typeUserList.userList = it
                        mTypeList.add(typeUserList)
                    }
                    mViewModel?.getMemberList(mGroupId, mPageIndex)
                }

                isEmpty.apply {
                    if (this) {
                        mViewModel?.getMemberList(mGroupId, mPageIndex)
                    }
                }

                error?.apply {
                    mViewModel?.getMemberList(mGroupId, mPageIndex)
                }

                netError?.apply {
                    mViewModel?.getMemberList(mGroupId, mPageIndex)
                }

                needLogin.apply {
                    if (this) {
                        mViewModel?.getMemberList(mGroupId, mPageIndex)
                    }
                }
            }
        })
    }

    /**
     * observe ??????????????????
     */
    private fun observeMemberList() {
        mViewModel?.memberListUiState?.observe(this, Observer {
            it.apply {
                dismissProgressDialog()

                success?.apply {
                    if (mPageIndex == 1) {
                        var showManage = false
                        this.let { groupUserList ->
                            groupUserList.list?.let {
                                // ????????????
                                var typeUserList = GroupTypeUserList()
                                typeUserList.typeName = getString(R.string.family_member_title_new)
                                typeUserList.userList = it
                                mTypeList.add(typeUserList)
                            }
                            // ????????????????????????
                            showManage = groupUserList.type == GroupUser.USER_TYPE_CREATOR
                                    || groupUserList.type == GroupUser.USER_TYPE_ADMINISTRATOR
                        }
                        // ???????????????"??????"
                        if(showManage) {
                            mTitleBar.setRightTitle(R.string.community_manage_btn)
                        }
                        // ?????????????????????
                        refreshList()
                    } else {
                        // ??????????????????
                        addMemberList(this)
                    }
                    if(this.hasMore == true) {
                        mRefreshLayout.finishLoadMore()
                        mPageIndex++
                    } else {
                        mRefreshLayout.finishLoadMoreWithNoMoreData()
                    }
                }

                error?.apply {
                    if(mPageIndex == 1 && mTypeList.isEmpty()) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if(mPageIndex == 1&& mTypeList.isEmpty()) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        })
    }

    /**
     * observe ??????
     */
    private fun observeFollow() {
        mViewModel?.followUiState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    var result  = this
                    if(result.isSuccess()) {
                        showToast(getString(R.string.family_follow_success))
                        // ??????"??????"??????
                        updateFollowBtn()
                    } else {
                        showToast(result.bizMsg)
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        })
    }

    /**
     * ?????????????????????????????????
     */
    private fun loginEventObserve() {
        LiveEventBus.get(LOGIN_STATE, com.kotlin.android.app.router.liveevent.event.LoginState::class.java).observe(this) {
            if (it?.isLogin == true) {//????????????
                refreshData()
            }
        }
    }

    /**
     * ????????????
     */
    override fun onLoadMore(refreshLayout: RefreshLayout){
        // ????????????????????????
        mViewModel?.getMemberList(mGroupId, mPageIndex)
    }

    /**
     * ????????????????????????"??????/??????"???????????????
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }

    // ?????????????????????
    private fun refreshList() {
        // ????????????
        if(mTypeList == null || mTypeList.isEmpty()) {
            return
        }

        if (mPageIndex == 1) {
            mFloatingItemDecoration.clearTitles()
            mAdapter.notifyAdapterClear()
        }

        // ????????????
        var index = 0
        mTypeList.forEach { typeUserList ->
            typeUserList.userList?.let {
                mFloatingItemDecoration.appendTitles(index, typeUserList.typeName?:"")
                var binderList: List<MultiTypeBinder<*>> = getBinderList(it)
                mAllBinderList.addAll(binderList)
                mAdapter.notifyAdapterAdded(binderList)
                index += it.size
            }
        }
    }

    // ??????????????????
    private fun addMemberList(groupUserList: GroupUserList?) {
        groupUserList?.list?.let {
            var binderList: List<MultiTypeBinder<*>> = getBinderList(it)
            mAllBinderList.addAll(binderList)
            mAdapter.notifyAdapterAdded(binderList)
        }
    }

    /**
     * ??????BinderList
     */
    private fun getBinderList(list: List<GroupUser>): List<MultiTypeBinder<*>> {
        val arrayList = ArrayList<FamilyMemberItemBinder>()
        list.map {
            val binder = FamilyMemberItemBinder(it, true, false, :: onClickItem, ::onClickFollow)
            arrayList.add(binder)
        }
        return arrayList
    }

    /**
     * ??????Item
     */
    private fun onClickItem(bean: GroupUser, position: Int) {

    }

    /**
     * ??????"??????"??????
     */
    private fun onClickFollow(bean: GroupUser, position: Int) {
        afterLogin {
            var userId = bean.userId ?: 0
            if (userId > 0) {
                mFollowUserId = userId
                mViewModel?.follow(mFollowUserId)
            }
        }
    }

    /**
     * ??????"??????"??????
     */
    private fun updateFollowBtn() {
        // 3???????????????????????????????????????????????????
        mAllBinderList.forEach {
            var binder = it as FamilyMemberItemBinder
            if(binder.bean.userId == mFollowUserId) {
                binder.bean.followed = true
                binder.notifyAdapterSelfChanged()
            }
        }
    }

    /**
     * ?????????????????????
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FamilyConstant.REQUEST_CODE_MEMBER_MANAGE) {
            // ?????????????????????????????????????????????????????????????????????
            refreshData()
        }
    }

    /**
     * ??????????????????
     */
    private fun refreshData() {
        mPageIndex = 1
        mTypeList.clear()
        mAllBinderList.clear()
        mAdapter.notifyAdapterClear()
        initData()
    }

}