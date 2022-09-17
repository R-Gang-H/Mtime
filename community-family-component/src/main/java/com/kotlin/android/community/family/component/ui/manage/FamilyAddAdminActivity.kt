package com.kotlin.android.community.family.component.ui.manage

import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyAddAdminBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilyMemberItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.act_family_add_admin.*
import kotlinx.android.synthetic.main.act_family_add_admin.mMultiStateView
import kotlinx.android.synthetic.main.act_family_add_admin.mRefreshLayout
import kotlinx.android.synthetic.main.layout_family_search.*

/**
 * @author vivian.wei
 * @date 2020/8/12
 * @desc 添加管理员页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_ADD_ADMIN)
class FamilyAddAdminActivity: BaseVMActivity<FamilyAddAdminViewModel, ActFamilyAddAdminBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    companion object {
        const val COMMA = ","                   // 分隔符
        const val REFRESH_FOOTER_HEIGHT = 50f   // 刷新组件footer高 dp
    }

    private var mGroupId: Long = 0
    private var mPageIndex = 1
    private lateinit var mAdapter: MultiTypeAdapter
    private lateinit var mSearchAdapter: MultiTypeAdapter
    private var mAllMemberList = ArrayList<GroupUser>()
    private var mSelectUserIds = ArrayList<Long>()
    private var mSearchSelectUserIds = ArrayList<Long>()
    private var mHasMore = false

    override fun initVM(): FamilyAddAdminViewModel {
        mGroupId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0)
        return viewModels<FamilyAddAdminViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        CommonTitleBar().init(this)
                .setTitle(R.string.family_admin_list)
                .setLeftClickListener(View.OnClickListener {
                    // 点击"返回"箭头：返回群组管理页
                    setResult(FamilyConstant.RESULT_CODE_ADD_ADMINISTRATOR)
                    finish()
                })
                .setRightTextAndClick(R.string.community_cancel, View.OnClickListener {
                    // 点击取消：返回管理员列表页
                    finish()
                })
                .setRightTextColor(R.color.color_8798af)
                .setRightTextStyle(Typeface.BOLD)
                .create()
    }

    override fun initView() {
        ShapeExt.setShapeColorAndCorner(mFamilySearchEditText, R.color.color_1a8798af, 20)

        mAdapter = createMultiTypeAdapter(mActFamilyAddAdminRv, LinearLayoutManager(this))
        mSearchAdapter = createMultiTypeAdapter(mActFamilyAddAdminSearchRv, LinearLayoutManager(this))

        mActFamilyAddAdminSearchRv.isGone = true
        mActFamilyAddAdminEmptyTv.isGone = true
        mActFamilyAddAdminSearchBar.isVisible = true
        mRefreshLayout.isVisible = true
        updateBottomBtnStatus()

        initEvent()
    }

    // 初始化事件
    private fun initEvent() {
        mRefreshLayout.setFooterHeight(REFRESH_FOOTER_HEIGHT)
        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)

        // 点击"底部"按钮
        mActFamilyAddAdminBottomBtn.onClick {
            setAdministrator()
        }

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
    }

    override fun initData() {
        // 获取群组成员列表
        showProgressDialog()
        mViewModel?.getMemberList(mGroupId, mPageIndex)
    }

    override fun startObserve() {
        // 群组成员列表
        observeMemberList()
        // 按昵称精确搜索
        observeMememberListByNickName()
        // 设置管理员
        observeSetAdmin()
    }

    /**
     * 群组成员列表
     */
    private fun observeMemberList() {
        mViewModel?.memberListUiState?.observe(this) {
            it.apply {
                // 第一页有loading，更多页没有
                dismissProgressDialog()

                success?.apply {
                    if(mPageIndex == 1 && (this == null || this.list == null || this.list?.size == 0)) {
                        showEmptyTip()
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
     * 设置管理员
     */
    private fun observeSetAdmin() {
        mViewModel?.setAdminUiState?.observe(this) {
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
        // 获取群组成员列表
        mViewModel?.getMemberList(mGroupId, mPageIndex)
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(@MultiStateView.ViewState viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }

    /**
     * 显示成员列表
     */
    private fun showMemberList(groupUserList: GroupUserList) {
        groupUserList.list?.let {
            // 追加列表
            mAdapter.notifyAdapterAdded(getBinderList(it))
            mAllMemberList.addAll(it)
        }
    }

    /**
     * 成员列表Binder
     */
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
                if (mActFamilyAddAdminSearchRv.isVisible) {
                    mSearchSelectUserIds.add(it)
                } else {
                    mSelectUserIds.add(it)
                }
            } else {
                // 取消选中
                if (mActFamilyAddAdminSearchRv.isVisible) {
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
     * 显示空提示
     */
    private fun showEmptyTip() {
        mActFamilyAddAdminSearchBar.isGone = true
        mActFamilyAddAdminSearchRv.isGone = true
        mRefreshLayout.isGone = true
        mActFamilyAddAdminEmptyTv?.isVisible = true
    }

    /**
     * 更新底部按钮状态
     */
    private fun updateBottomBtnStatus() {
        val selectCount = if (mActFamilyAddAdminSearchRv.isVisible) mSearchSelectUserIds.size else mSelectUserIds.size
        val clickable = selectCount > 0
        mActFamilyAddAdminBottomBtn.isClickable = clickable
        if (clickable) {
            mActFamilyAddAdminBottomBtn.text = String.format("%s %d", getString(R.string.community_add), selectCount)
            mActFamilyAddAdminBottomBtn.setTextColor(getColor(R.color.color_ff5a36))
        } else {
            mActFamilyAddAdminBottomBtn.text = getString(R.string.community_add)
            mActFamilyAddAdminBottomBtn.setTextColor(getColor(R.color.color_aab7c7_20_alpha))
        }
    }

    /**
     * 添加群组管理员
     */
    private fun setAdministrator() {
        // 获取选中的userId串
        var userIds = if (mActFamilyAddAdminSearchRv.isVisible)
            getSelectUserIdString(mSearchSelectUserIds)
        else
            getSelectUserIdString(mSelectUserIds)
        if(userIds.isNotEmpty()) {
            // 添加群组管理员
            mViewModel?.setAdmin(mGroupId, userIds)
        }
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
     * 操作按钮成功后更新列表
     */
    private fun setSuccess() {
        // 当前是否为搜索状态
        val isSearch = mActFamilyAddAdminSearchRv.isVisible
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
                mViewModel?.getMemberList(mGroupId, mPageIndex)
            } else {
                showEmptyTip()
            }
        }
        updateBottomBtnStatus()
    }

    /**
     * 搜索
     */
    private fun onSearch(key: String) {
        if (key.isEmpty()) {
            // 清空搜索框：显示成员列表，隐藏搜索列表
            showMemberListLayout()
        } else {
            // 按昵称精确搜索（不显示loading）
            mViewModel?.getMemberListByNickName(mGroupId, key, GroupUser.USER_TYPE_MEMBER)
        }
    }

    /**
     * 显示搜索结果
     * @param data
     */
    fun showSearchData(groupUserList: GroupUserList) {
        mRefreshLayout.isGone = true
        mActFamilyAddAdminSearchRv.isVisible = true
        mSearchSelectUserIds.clear()
        mSearchAdapter.notifyAdapterClear()
        groupUserList.list?.let {
            mSearchAdapter.notifyAdapterAdded(getBinderList(it))
        }
        updateBottomBtnStatus()
    }

    /**
     * 显示成员列表，隐藏搜索列表
     */
    fun showMemberListLayout() {
        mSearchSelectUserIds.clear()
        mActFamilyAddAdminSearchRv.isGone = true
        mRefreshLayout.isVisible = true
        updateBottomBtnStatus()
    }

}