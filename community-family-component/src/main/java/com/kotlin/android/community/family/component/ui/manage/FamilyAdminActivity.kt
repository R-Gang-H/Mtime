package com.kotlin.android.community.family.component.ui.manage

import android.content.Intent
import android.graphics.Typeface
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.data.entity.community.group.GroupUserList
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyAdminBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilyMemberItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.community.family.component.ui.manage.widget.ActionSheetDialog
import com.kotlin.android.community.family.component.ui.manage.widget.SheetItemBean
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.act_family_admin.*

/**
 * @author vivian.wei
 * @date 2020/8/10
 * @desc 家族管理员列表页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_ADMIN)
class FamilyAdminActivity: BaseVMActivity<FamilyAdminViewModel, ActFamilyAdminBinding>(),
        MultiStateView.MultiStateListener {

    companion object {
        // 当前页面状态
        const val CUR_STATUS_LIST = 0
        const val CUR_STATUS_REMOVE = 1
        // 分隔符
        const val COMMA = ","
    }

    private val mProvider = getProvider(ICommunityFamilyProvider::class.java)

    private var mGroupId: Long = 0
    private var mCurStatus = CUR_STATUS_LIST
    private lateinit var titleBar: CommonTitleBar
    private lateinit var mAdapter: MultiTypeAdapter
    private var mGroupUserList = ArrayList<GroupUser>()
    private var mSelectUserIds = ArrayList<Long>()

    override fun initVM(): FamilyAdminViewModel {
        mGroupId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0)
        return viewModels<FamilyAdminViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        titleBar = CommonTitleBar()
        titleBar.init(this)
                .setTitle(R.string.family_admin_list)
                .setRightTextAndClick(R.string.community_manage_btn, View.OnClickListener {
                    clickManageBtn()
                })
                .setRightTextColor(R.color.color_8798af)
                .setRightTextStyle(Typeface.BOLD)
                .create()
    }

    override fun initView() {
        mActFamilyAdminBottomShadowView.isVisible = false
        mActFamilyAdminBottomBtn.isVisible = false

        mAdapter = createMultiTypeAdapter(mActFamilyAdminRv, LinearLayoutManager(this))

        // 点击"移除"按钮
        mActFamilyAdminBottomBtn.onClick {
            unSetAdmin()
        }
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        mViewModel?.getAdminList(mGroupId)
    }

    override fun startObserve() {
        // 管理员列表
        observeAdminList()
        // 取消管理员
        observeUnsetAdmin()
    }

    /**
     * 管理员列表
     */
    private fun observeAdminList() {
        mViewModel?.uiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // 没有分页
                    showAdminList(this)
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    /**
     * 取消管理员
     */
    private fun observeUnsetAdmin() {
        mViewModel?.unsetAdminUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.status == 1L) {
                        showToast("移除成功")
                        // 更新列表
                        unsetSuccess()
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

                needLogin.apply {
                    if (this) {
                        showToast("未登录")
                    }
                }
            }
        }
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }

    /**
     * 显示管理员列表
     */
    private fun showAdminList(groupUserList: GroupUserList?) {
        groupUserList?.list?.let {
            mGroupUserList.addAll(it)
            mAdapter.notifyAdapterDataSetChanged(getBinderList(it))
        }
    }

    /**
     * 管理员列表Binder
     */
    private fun getBinderList(list: List<GroupUser>): List<MultiTypeBinder<*>> {
        val arrayList = ArrayList<FamilyMemberItemBinder>()
        list.map {
            val binder = FamilyMemberItemBinder(it, false,
                    mCurStatus == CUR_STATUS_REMOVE, ::onClickItem, ::onClickFollow)
            arrayList.add(binder)
        }
        return arrayList
    }

    /**
     * 点击Item
     */
    private fun onClickItem(bean: GroupUser, position: Int) {
        if (mCurStatus != CUR_STATUS_LIST) {
            bean.userId?.let {
                if (bean.checked) {
                    mSelectUserIds.add(it)
                } else {
                    mSelectUserIds.remove(it)
                }
                updateBottomBtnStatus()
            }
        }
    }

    /**
     * 点击"关注"按钮
     */
    private fun onClickFollow(bean: GroupUser, position: Int) {
    }

    /**
     * 点击 管理/取消 按钮
     */
    private fun clickManageBtn() {
        if (mCurStatus == CUR_STATUS_LIST) {
            // 点击"管理": 显示底部弹框
            showSheetDialog()
        } else {
            // 点击"取消"
            mCurStatus = CUR_STATUS_LIST
            titleBar.setRightTitle(getString(R.string.community_manage_btn))
            mActFamilyAdminBottomShadowView.isVisible = false
            mActFamilyAdminBottomBtn.isVisible = false
            mSelectUserIds.clear()
            mGroupUserList.forEach {
                it.checked = false
            }
            mAdapter.notifyAdapterDataSetChanged(getBinderList(mGroupUserList))
            updateBottomBtnStatus()
        }
    }

    /**
     * 更新底部按钮状态
     */
    private fun updateBottomBtnStatus() {
        val selectCount = mSelectUserIds.size
        val clickable = selectCount > 0
        mActFamilyAdminBottomBtn.isClickable = clickable
        if (clickable) {
            mActFamilyAdminBottomBtn.text = String.format("%s %d", getString(R.string.community_remove), selectCount)
            mActFamilyAdminBottomBtn.setTextColor(getColor(R.color.color_ff5a36))
        } else {
            mActFamilyAdminBottomBtn.text = getString(R.string.community_remove)
            mActFamilyAdminBottomBtn.setTextColor(getColor(R.color.color_aab7c7_20_alpha))
        }
    }

    /**
     * 移除群组管理员
     */
    private fun unSetAdmin() {
        var userIds = getSelectUserIdString()
        if(userIds.isNotEmpty()) {
            mViewModel?.unsetAdmin(mGroupId, userIds)
        }
    }

    /**
     * 获取选中的userId串
     */
    private fun getSelectUserIdString(): String {
        mSelectUserIds.let {
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
     * 移除管理员成功后更新列表
     */
    private fun unsetSuccess() {
        // 从列表中移除
        mSelectUserIds.forEach flag@{ userId ->
            mGroupUserList.forEach {
                if(it.userId == userId) {
                    mGroupUserList.remove(it)
                    return@flag
                }
            }
        }
        // 更新列表
        mAdapter.notifyAdapterDataSetChanged(getBinderList(mGroupUserList))
        mSelectUserIds.clear()
        updateBottomBtnStatus()
    }

    /**
     * 显示底部弹框
     */
    private fun showSheetDialog() {
        var sheetItemBeans = ArrayList<SheetItemBean>()
        sheetItemBeans.add(SheetItemBean("添加", ActionSheetDialog.SheetItemColor.Blue))
        sheetItemBeans.add(SheetItemBean("移除", ActionSheetDialog.SheetItemColor.Red))
        ActionSheetDialog.newInstance(this, sheetItemBeans, ::onClickSheetItem)
                .showNow(supportFragmentManager, ActionSheetDialog.TAG_ACTION_SHEET_DIALOG_FRAGMENT)
    }

    /**
     * 点击底部弹框选项
     */
    private fun onClickSheetItem(which: Int) {
        if (which == 0) {
            // 添加
            mProvider?.startFamilyAddAdmin(this, mGroupId, FamilyConstant.REQUEST_ADD_ADMINISTRATOR_CODE)
        } else if (which == 1) {
            // 移除: 显示底部按钮、checkbox
            mCurStatus = CUR_STATUS_REMOVE
            titleBar.setRightTitle(getString(R.string.community_cancel))
            mActFamilyAdminBottomShadowView.isVisible = true
            mActFamilyAdminBottomBtn.isVisible = true
            mAdapter.notifyAdapterClear()
            mAdapter.notifyAdapterDataSetChanged(getBinderList(mGroupUserList))
        }
    }

    /**
     * 从其他页面回来
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == FamilyConstant.RESULT_CODE_ADD_ADMINISTRATOR) {
            // 从添加管理员页面点击"返回"箭头：返回到群组管理页
            setResult(resultCode)
            finish()
        }

        if (requestCode == FamilyConstant.REQUEST_ADD_ADMINISTRATOR_CODE) {
            // 从添加群组管理员页点击"取消"回来：需要刷新列表
            mGroupUserList.clear()
            mSelectUserIds.clear()
            mAdapter.notifyAdapterClear()
            mViewModel?.getAdminList(mGroupId)
        }

    }
}