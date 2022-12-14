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
 * @desc ??????????????????
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_ADD_ADMIN)
class FamilyAddAdminActivity: BaseVMActivity<FamilyAddAdminViewModel, ActFamilyAddAdminBinding>(),
        OnLoadMoreListener, MultiStateView.MultiStateListener {

    companion object {
        const val COMMA = ","                   // ?????????
        const val REFRESH_FOOTER_HEIGHT = 50f   // ????????????footer??? dp
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
                    // ??????"??????"??????????????????????????????
                    setResult(FamilyConstant.RESULT_CODE_ADD_ADMINISTRATOR)
                    finish()
                })
                .setRightTextAndClick(R.string.community_cancel, View.OnClickListener {
                    // ???????????????????????????????????????
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

    // ???????????????
    private fun initEvent() {
        mRefreshLayout.setFooterHeight(REFRESH_FOOTER_HEIGHT)
        mRefreshLayout.setEnableRefresh(false)
        mRefreshLayout.setOnLoadMoreListener(this)
        mMultiStateView.setMultiStateListener(this)

        // ??????"??????"??????
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

        // ??????????????????"??????"??????
        mFamilySearchDeleteIv.onClick {
            mFamilySearchEditText.setText("")
        }
    }

    override fun initData() {
        // ????????????????????????
        showProgressDialog()
        mViewModel?.getMemberList(mGroupId, mPageIndex)
    }

    override fun startObserve() {
        // ??????????????????
        observeMemberList()
        // ?????????????????????
        observeMememberListByNickName()
        // ???????????????
        observeSetAdmin()
    }

    /**
     * ??????????????????
     */
    private fun observeMemberList() {
        mViewModel?.memberListUiState?.observe(this) {
            it.apply {
                // ????????????loading??????????????????
                dismissProgressDialog()

                success?.apply {
                    if(mPageIndex == 1 && (this == null || this.list == null || this.list?.size == 0)) {
                        showEmptyTip()
                    } else {
                        // ??????????????????
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
     * ?????????????????????
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
     * ???????????????
     */
    private fun observeSetAdmin() {
        mViewModel?.setAdminUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (this.status == 1L) {
                        showToast("????????????")
                        // ????????????
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

    /**
     * ??????????????????
     */
    private fun showMemberList(groupUserList: GroupUserList) {
        groupUserList.list?.let {
            // ????????????
            mAdapter.notifyAdapterAdded(getBinderList(it))
            mAllMemberList.addAll(it)
        }
    }

    /**
     * ????????????Binder
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
     * ??????Item
     */
    private fun onClickItem(bean: GroupUser, position: Int) {
        bean.userId?.let {
            if (bean.checked) {
                // ??????
                if (mActFamilyAddAdminSearchRv.isVisible) {
                    mSearchSelectUserIds.add(it)
                } else {
                    mSelectUserIds.add(it)
                }
            } else {
                // ????????????
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
     * ??????"??????"??????
     */
    private fun onClickFollow(bean: GroupUser, position: Int) {

    }

    /**
     * ???????????????
     */
    private fun showEmptyTip() {
        mActFamilyAddAdminSearchBar.isGone = true
        mActFamilyAddAdminSearchRv.isGone = true
        mRefreshLayout.isGone = true
        mActFamilyAddAdminEmptyTv?.isVisible = true
    }

    /**
     * ????????????????????????
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
     * ?????????????????????
     */
    private fun setAdministrator() {
        // ???????????????userId???
        var userIds = if (mActFamilyAddAdminSearchRv.isVisible)
            getSelectUserIdString(mSearchSelectUserIds)
        else
            getSelectUserIdString(mSelectUserIds)
        if(userIds.isNotEmpty()) {
            // ?????????????????????
            mViewModel?.setAdmin(mGroupId, userIds)
        }
    }

    /**
     * ???????????????userId???
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
     * ?????????????????????????????????
     */
    private fun setSuccess() {
        // ???????????????????????????
        val isSearch = mActFamilyAddAdminSearchRv.isVisible
        if(isSearch) {
            // ??????????????????
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
            // ??????????????????
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
        // ???????????????????????????????????????????????????
        if (mAdapter.itemCount == 0) {
            if(mHasMore) {
                // ????????????????????????
                mPageIndex = 1
                mViewModel?.getMemberList(mGroupId, mPageIndex)
            } else {
                showEmptyTip()
            }
        }
        updateBottomBtnStatus()
    }

    /**
     * ??????
     */
    private fun onSearch(key: String) {
        if (key.isEmpty()) {
            // ?????????????????????????????????????????????????????????
            showMemberListLayout()
        } else {
            // ?????????????????????????????????loading???
            mViewModel?.getMemberListByNickName(mGroupId, key, GroupUser.USER_TYPE_MEMBER)
        }
    }

    /**
     * ??????????????????
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
     * ???????????????????????????????????????
     */
    fun showMemberListLayout() {
        mSearchSelectUserIds.clear()
        mActFamilyAddAdminSearchRv.isGone = true
        mRefreshLayout.isVisible = true
        updateBottomBtnStatus()
    }

}