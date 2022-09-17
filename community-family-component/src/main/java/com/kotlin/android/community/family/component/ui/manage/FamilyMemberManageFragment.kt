package com.kotlin.android.community.family.component.ui.manage

import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.FragFamilyMemberManageBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilyMemberItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import com.kotlin.android.ktx.ext.keyboard.hideSoftInput
import com.kotlin.android.ktx.ext.keyboard.isShowSoftInput
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.frag_family_member_manage.*
import kotlinx.android.synthetic.main.frag_family_member_manage.mMultiStateView
import kotlinx.android.synthetic.main.frag_family_member_manage.mRefreshLayout
import kotlinx.android.synthetic.main.layout_family_search.*

/**
 * @author vivian.wei
 * @date 2020/8/13
 * @desc 类描述
 */
class FamilyMemberManageFragment: BaseVMFragment<FamilyMemberManageFragViewModel, FragFamilyMemberManageBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    companion object {
        // 底部按钮操作
        const val EVENT_CODE_REMOVE = 10013
        const val EVENT_CODE_RESTORE = 10014
        const val EVENT_CODE_ADD = 10015
        const val EVENT_CODE_REFUSE = 10016
        // 分隔符
        const val COMMA = ","
        // 刷新组件footer高
        const val REFRESH_FOOTER_HEIGHT = 50f
    }

    private var mGroupId: Long = 0
    private var mMemberType: Long = GroupUser.USER_TYPE_MEMBER
    private var mPageIndex = 1
    private lateinit var mAdapter: MultiTypeAdapter
    private lateinit var mSearchAdapter: MultiTypeAdapter
    private var mAllMemberList = ArrayList<GroupUser>()
    private var mSelectUserIds = ArrayList<Long>()
    private var mSearchSelectUserIds = ArrayList<Long>()
    private var mIsInitData = false
    private var mHasMore = false

    override fun initVM(): FamilyMemberManageFragViewModel {
        mGroupId = arguments?.getLong(FamilyConstant.KEY_FAMILY_ID, 0) ?: 0
        mMemberType = arguments?.getLong(FamilyConstant.KEY_FAMILY_MEMBER_TYPE, GroupUser.USER_TYPE_MEMBER)
                ?: GroupUser.USER_TYPE_MEMBER

        return viewModels<FamilyMemberManageFragViewModel>().value
    }

    override fun initView() {
        // 搜索框背景
        ShapeExt.setShapeColorAndCorner(mFamilySearchEditText, R.color.color_1a8798af, 20)

        mAdapter = createMultiTypeAdapter(mFragFamilyMemberManageRv, LinearLayoutManager(mContext))
        mSearchAdapter = createMultiTypeAdapter(mFragFamilyMemberManageSearchRv, LinearLayoutManager(mContext))

        mFragFamilyMemberManageSearchRv.isGone = true
        mFragFamilyMemberManageEmptyTv.isGone = true

        initEvent()
    }

    // 初始化事件
    private fun initEvent() {
        mRefreshLayout.setFooterHeight(REFRESH_FOOTER_HEIGHT)
        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)

        // 搜索框
        mFamilySearchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val content = s.toString().trim { it <= ' ' }
                onSearch(content)
            }

            override fun afterTextChanged(s: Editable) {
                val content = s.toString().trim { it <= ' ' }
                mFamilySearchDeleteIv.isVisible = content.isNotEmpty()
            }
        })

        // 点击搜索框里"删除"按钮
        mFamilySearchDeleteIv.onClick {
            mFamilySearchEditText.setText("")
        }

        // 解决滑动时与点击事件冲突
        val onItemTouchListener: RecyclerView.OnItemTouchListener = object : RecyclerView.OnItemTouchListener {
            var x1 = 0f
            var x2 = 0f
            var y1 = 0f
            var y2 = 0f
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    x1 = e.x
                    y1 = e.y
                }
                if (e.action == MotionEvent.ACTION_UP) {
                    x2 = e.x
                    y2 = e.y
                    if (Math.abs(x1 - x2) < 6) {
                        return false // 距离较小，当作click事件来处理
                    }
                    if (Math.abs(x1 - x2) > 60) {  // 真正的onTouch事件
                        return true
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        }

        // 点击"移除|恢复"按钮
        mFragFamilyMemberManageRemoveTv.onClick {
            clickBottomBtn(if(mMemberType == GroupUser.USER_TYPE_MEMBER) EVENT_CODE_REMOVE else EVENT_CODE_RESTORE)
        }

        // 点击"通过"按钮
        mFragFamilyMemberManagePassTv.onClick {
            clickBottomBtn(EVENT_CODE_ADD)
        }

        // 点击"拒绝"按钮
        mFragFamilyMemberManageRefuseTv.onClick {
            clickBottomBtn(EVENT_CODE_REFUSE)
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()

        mIsInitData = false
        refreshData()
    }

    /**
     * 刷新整个页面
     */
    private fun refreshData() {
        //首次加载
        if (!mIsInitData) {
            // 切换tab要重新获取数据
            mRefreshLayout.isVisible = true
            initSearchBar()
            setBottomBtn()
            mPageIndex = 1
            mSelectUserIds.clear()
            mSearchSelectUserIds.clear()
            showProgressDialog()
            // 群组成员列表
            mViewModel?.getMemberList(mGroupId, mPageIndex, mMemberType)
        }
    }

    /**
     * 初始化搜索框
     */
    private fun initSearchBar() {
        // 键盘收起
        if (activity.isShowSoftInput()) {
            activity.hideSoftInput()
        }

        mFamilySearchEditText.setText("")
        mFamilySearchEditText.clearFocus()
        mFamilySearchEditText.isFocusableInTouchMode = true
    }

    /**
     * 设置底部按钮文字和显示
     */
    private fun setBottomBtn() {
        when (mMemberType) {
            GroupUser.USER_TYPE_MEMBER, GroupUser.USER_TYPE_BLACK_LIST -> {
                mFragFamilyMemberManageRemoveTv.isVisible = true
                mFragFamilyMemberManagePassTv.isGone = true
                mFragFamilyMemberManageRefuseTv.isGone = true
                var text = getString(if (mMemberType == GroupUser.USER_TYPE_MEMBER) R.string.community_remove else R.string.community_restore)
                mFragFamilyMemberManageRemoveTv.text = text
            }
            GroupUser.USER_TYPE_APPLY -> {
                mFragFamilyMemberManageRemoveTv.isGone = true
                mFragFamilyMemberManagePassTv.isVisible = true
                mFragFamilyMemberManageRefuseTv.isVisible = true
            }
            else -> {
            }
        }
    }

    override fun startObserve() {
        // 群组成员列表
        observeMemberList()
        // 按昵称精确搜索
        observeMememberListByNickName()
        // 底部按钮操作
        observeBtnEvent()
    }

    /**
     * 群组成员列表
     */
    private fun observeMemberList() {
        mViewModel?.memberListUiState?.observe(this) {
            it.apply {
                // 第一页有loading，更多页没有
                dismissProgressDialog()

                //首次加载
                if (!mIsInitData) {
                    mIsInitData = true
                }

                success?.apply {
                    if(mPageIndex == 1 && (this == null || this.list == null || this.list?.size == 0)) {
                        showEmptyTip(true)
                    } else {
                        // 显示成员列表
                        showMemberList(this)
                        if(this.hasMore == true) {
                            mRefreshLayout.finishLoadMore()
                            mPageIndex++
                            mHasMore = true
                        } else {
                            mRefreshLayout.finishLoadMoreWithNoMoreData()
                            mHasMore = false
                        }
                    }
                }

                error?.apply {
                    if(mPageIndex == 1) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if(mPageIndex == 1) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    }
                }
            }
        }
    }

    /**
     * 按昵称精确搜索
     */
    private fun observeMememberListByNickName() {
        mViewModel?.memberListByNickNameUiState?.observe(this) {
            it.apply {

                success?.apply {
                    showSearchData(this)
                }

            }
        }
    }

    /**
     * 底部按钮操作
     */
    private fun observeBtnEvent() {
        mViewModel?.btnEventCodeUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.status == 1L) {
                        showToast("操作成功")
                        // 更新列表
                        setSuccess()
                    } else if (this.status == 2L) {
                        showToast(this.failMsg)
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

    /**
     * 加载更多
     */
    override fun onLoadMore(refreshLayout: RefreshLayout){
        // 分类群组成员列表
        mViewModel?.getMemberList(mGroupId, mPageIndex, mMemberType)
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mIsInitData = false
                // 刷新整个页面
                refreshData()
            }
        }
    }

    override fun destroyView() {

    }

    // 刷新数据
    private fun showMemberList(groupUserList: GroupUserList) {
        groupUserList.list?.let {
            if(mPageIndex == 1) {
                mAllMemberList.clear()
                mAdapter.notifyAdapterClear()
                mSearchAdapter.notifyAdapterClear()
                mRefreshLayout.setNoMoreData(false)
                showEmptyTip(false)
            }
            var binderList: List<MultiTypeBinder<*>> = getBinderList(it)
            mAdapter.notifyAdapterAdded(binderList)
            mAllMemberList.addAll(it)
        }
    }

    private fun getBinderList(list: List<GroupUser>): List<MultiTypeBinder<*>> {
        val arrayList = ArrayList<FamilyMemberItemBinder>()
        list.map {
            val binder = FamilyMemberItemBinder(it, false, true, ::onClickItem, ::onClickFollow)
            arrayList.add(binder)
        }
        return arrayList
    }

    /**
     * 点击Item
     */
    private fun onClickItem(bean: GroupUser, position: Int) {
        bean.userId?.let {
            if (bean.checked) {
                // 选中
                if (mFragFamilyMemberManageSearchRv.isVisible) {
                    mSearchSelectUserIds.add(it)
                } else {
                    mSelectUserIds.add(it)
                }
            } else {
                // 取消选中
                if (mFragFamilyMemberManageSearchRv.isVisible) {
                    mSearchSelectUserIds.remove(it)
                } else {
                    mSelectUserIds.remove(it)
                }
            }
            updateBottomBtnStatus()
        }
    }

    /**
     * 点击"关注"按钮
     */
    private fun onClickFollow(bean: GroupUser, position: Int) {

    }

    /**
     * 更新底部按钮状态
     */
    private fun updateBottomBtnStatus() {
        val selectCount = if (mFragFamilyMemberManageSearchRv.isVisible)
            mSearchSelectUserIds.size else mSelectUserIds.size
        val clickable = selectCount > 0
        when (mMemberType) {
            GroupUser.USER_TYPE_MEMBER, GroupUser.USER_TYPE_BLACK_LIST -> {
                // 成员/黑名单列表
                mFragFamilyMemberManageRemoveTv.isClickable = clickable
                val text: String = getString(if (mMemberType == GroupUser.USER_TYPE_MEMBER)
                    R.string.community_remove else R.string.community_restore)
                if (clickable) {
                    mFragFamilyMemberManageRemoveTv.text = String.format("%s %d", text, selectCount)
                    mFragFamilyMemberManageRemoveTv.setTextColor(getColor(R.color.color_ff5a36))
                } else {
                    mFragFamilyMemberManageRemoveTv.text = text
                    mFragFamilyMemberManageRemoveTv.setTextColor(
                            getColor(R.color.color_aab7c7_20_alpha))
                }
            }
            GroupUser.USER_TYPE_APPLY -> {
                // 申请列表
                mFragFamilyMemberManagePassTv.isClickable = clickable
                mFragFamilyMemberManageRefuseTv.isClickable = clickable
                if (clickable) {
                    mFragFamilyMemberManagePassTv.text = String.format("%s %d", getString(R.string.family_member_manage_pass), selectCount)
                    mFragFamilyMemberManagePassTv.setTextColor(getColor(R.color.color_36c096))
                    mFragFamilyMemberManageRefuseTv.text = String.format("%s %d", getString(R.string.family_member_manage_refuse), selectCount)
                    mFragFamilyMemberManageRefuseTv.setTextColor(getColor(R.color.color_ff5a36))
                } else {
                    mFragFamilyMemberManagePassTv.text = getString(R.string.family_member_manage_pass)
                    mFragFamilyMemberManagePassTv.setTextColor(getColor(R.color.color_aab7c7_20_alpha))
                    mFragFamilyMemberManageRefuseTv.text = getString(R.string.family_member_manage_refuse)
                    mFragFamilyMemberManageRefuseTv.setTextColor(getColor(R.color.color_aab7c7_20_alpha))
                }
            }
            else -> {
            }
        }
    }

    /**
     * 显示空提示
     */
    private fun showEmptyTip(isEmpty: Boolean) {
        mFragFamilyMemberManageSearchBar.isGone = isEmpty
        mFragFamilyMemberManageSearchRv.isGone = isEmpty
        mRefreshLayout.isGone = isEmpty
        mFragFamilyMemberManageEmptyTv.isVisible = isEmpty
        if(isEmpty) {
            when (mMemberType) {
                GroupUser.USER_TYPE_MEMBER -> {
                    mFragFamilyMemberManageEmptyTv.text = getString(R.string.family_member_manage_member_empty)
                }
                GroupUser.USER_TYPE_BLACK_LIST -> {
                    mFragFamilyMemberManageEmptyTv.text = getString(R.string.family_member_manage_black_list_empty)
                }
                GroupUser.USER_TYPE_APPLY -> {
                    mFragFamilyMemberManageEmptyTv.text = getString(R.string.family_member_manage_apply_empty)
                }
                else -> {
                }
            }
        }
    }

    /**
     * 搜索
     */
    private fun onSearch(key: String) {
        if (key.isEmpty()) {
            // 清空搜索框：显示成员列表，隐藏搜索列表
            showMemberTypeList()
        } else {
            // 按昵称精确搜索（不显示loading）
            mViewModel?.getCommunityGroupMemberListByNickName(mGroupId, key, mMemberType)
        }
    }

    /**
     * 显示成员|黑名单|申请列表，隐藏搜索列表
     */
    private fun showMemberTypeList() {
        mSearchSelectUserIds.clear()
        mFragFamilyMemberManageSearchRv.isGone = true
        mRefreshLayout.isVisible = true
        updateBottomBtnStatus()
    }

    /**
     * 显示搜索结果
     * @param data
     */
    fun showSearchData(groupUserList: GroupUserList) {
        mRefreshLayout.isGone = true
        mFragFamilyMemberManageSearchRv.isVisible = true
        mSearchSelectUserIds.clear()
        mSearchAdapter.notifyAdapterClear()
        groupUserList.list?.let {
            mSearchAdapter.notifyAdapterAdded(getBinderList(it))
        }
        updateBottomBtnStatus()
    }

    /**
     * 获取选中的userId串
     */
    private fun getSelectUserIdString(list: List<Long>): String {
        list.let {
            val buffer = StringBuffer()
            var i = 0
            val size = it.size
            it.forEach {
                buffer.append(it.toString())
                if (i < size - 1) {
                    buffer.append(COMMA)
                }
                i++
            }
            return buffer.toString()
        }
        return ""
    }

    /**
     * 点击底部按钮
     */
    private fun clickBottomBtn(eventCode: Int) {
        // 获取选中的userId串
        var userIds = if (mFragFamilyMemberManageSearchRv.isVisible)
            getSelectUserIdString(mSearchSelectUserIds)
        else
            getSelectUserIdString(mSelectUserIds)
        if(userIds.isNotEmpty()) {
            mViewModel?.clickBottomBtn(mGroupId, userIds, eventCode)
        }
    }

    /**
     * 操作按钮成功后更新列表
     */
    private fun setSuccess() {
        // 当前是否为搜索状态
        val isSearch = mFragFamilyMemberManageSearchRv.isVisible
        if(isSearch) {
            // 处理成员列表
            mSearchSelectUserIds.forEach flag@{ userId ->
                mSelectUserIds.remove(userId)
                mAllMemberList.forEach {
                    if(it.userId == userId) {
                        mAllMemberList.remove(it)
                        return@flag
                    }
                }
            }
            mAdapter.notifyAdapterClear()
            mAdapter.notifyAdapterAdded(getBinderList(mAllMemberList))
            // 清空搜索列表
            mSearchAdapter.notifyAdapterClear()
            mSearchSelectUserIds.clear()
        } else {
            mSelectUserIds.forEach flag@{ userId ->
                mAllMemberList.forEach {
                    if(it.userId == userId) {
                        mAllMemberList.remove(it)
                        return@flag
                    }
                }
            }
            mAdapter.notifyAdapterClear()
            mAdapter.notifyAdapterAdded(getBinderList(mAllMemberList))
            mSelectUserIds.clear()
        }
        // 如果成员列表移除了所有，显示空提示
        if (mAdapter.itemCount == 0) {
            if(mHasMore) {
                // 分类群组成员列表
                mPageIndex = 1
                mViewModel?.getMemberList(mGroupId, mPageIndex, mMemberType)
            } else {
                showEmptyTip(true)
            }
        }
        updateBottomBtnStatus()
    }

}