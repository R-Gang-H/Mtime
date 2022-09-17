package com.kotlin.android.community.family.component.ui.manage

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.group.Group
import com.kotlin.android.app.data.entity.community.group.GroupSection
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.bonus.scene.component.postCreateFamily
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyCreateBinding
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBar
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * @author vivian.wei
 * @date 2020/8/4
 * @desc 创建|管理家族页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_CREATE)
class FamilyCreateActivity : BaseVMActivity<FamilyCreateViewModel, ActFamilyCreateBinding>(),
    MultiStateView.MultiStateListener {

    private val mProvider = getProvider(ICommunityFamilyProvider::class.java)

    private var titleBar: TitleBar? = null

    private var mGroupId: Long = 0
    private var mUploadID: String = ""
    private var mName: String = ""
    private var mDes: String = ""
    private var mPermission = FamilyDetail.PERMISSION_NULL
    private var mPublishPermission = FamilyDetail.GROUP_AUTHORITY_FREE
    private var mPrimaryCategoryId: Long = 0
    private var mPrimaryCategoryName: String? = ""
    private var mGroup: Group? = null  // 编辑状态下，保存群组原始信息
    private var mOnlyUpdateAdminCount = false
    private var mOnlyUpdateMemberCount = false
    private var mOnlyUpdateSectionCount = false

    override fun initVM(): FamilyCreateViewModel {
        intent?.let {
            mGroupId = it.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0)
        }

        return viewModels<FamilyCreateViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        titleBar = TitleBarManager.with(this)
            .back { this.finish() }
        if (mGroupId == 0L) {
            titleBar?.setTitle(
                title = getString(R.string.family_create_title),
                isBold = true,
                gravity = Gravity.CENTER,
                drawablePadding = 5.dp,
            )
        } else {
            titleBar?.setTitle(
                title = ""
            )
        }
    }

    override fun initView() {
        val isEdit = mGroupId > 0
        mBinding?.mActFamilyCreateMaxCountTv?.isGone = isEdit
        // 分类
        mBinding?.mActFamilyCreateCategoryLineView?.isGone = isEdit
        mBinding?.mActFamilyCreateCategoryCl?.isGone = isEdit
        // 管理员
        mBinding?.mActFamilyCreateAdministratorLineView?.isVisible = isEdit
        mBinding?.mActFamilyCreateAdministratorCl?.isVisible = isEdit
        // 成员
        mBinding?.mActFamilyCreateMemberLineView?.isVisible = isEdit
        mBinding?.mActFamilyCreateMemberCl?.isVisible = isEdit
        //分区管理
        mBinding?.mSectionManagerCl?.isVisible = isEdit
        mBinding?.mActFamilySectionLineView?.isVisible = isEdit
        //发布管理
        mBinding?.mActFamilyPublishLineView?.isVisible = isEdit
        mBinding?.mPublishManagerCl?.isVisible = isEdit
        // btn
        mBinding?.mActFamilyCreateBtn?.text =
            if (isEdit) getString(R.string.community_ok_btn) else getString(R.string.community_create_btn)
        // 事件
        initEvent()
        // 按钮是否可点击（isClickable要放到setOnClickListener之后才有效，初始化时需要注意顺序）
        updateSaveBtnStatus(false)
    }

    private fun initEvent() {
        // 点击封面图
        mBinding?.mActFamilyCreateImgCl?.onClick {
            // 显示上传图片组件
            showUploadImgFragment()
        }
        // 点击名称
        mBinding?.mActFamilyCreateNameCl?.onClick {
            // 修改家族名称|简介页
            val name = mBinding?.mActFamilyCreateNameTv?.text.toString()
            mProvider?.startFamilyEditInfo(
                this,
                FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME,
                name,
                FamilyConstant.REQUEST_CODE_CHANGE_NAME_DES
            )
        }
        // 点击简介
        mBinding?.mActFamilyCreateDesCl?.onClick {
            // 修改家族名称|简介页
            val des = mBinding?.mActFamilyCreateDesTv?.text.toString()
            mProvider?.startFamilyEditInfo(
                this,
                FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES,
                des,
                FamilyConstant.REQUEST_CODE_CHANGE_NAME_DES
            )
        }
        // 加入权限页
        mBinding?.mActFamilyCreatePermissionCl?.onClick {
            mProvider?.startFamilyPermission(this, mPermission, FamilyConstant.REQUEST_CODE_CHANGE_PERMISSION)
        }
        // 家族分类页
        mBinding?.mActFamilyCreateCategoryCl?.onClick {
            mProvider?.startFamilyCategory(
                this,
                mPrimaryCategoryId,
                FamilyConstant.REQUEST_CODE_CHANGE_CATEGORY
            )
        }
        // 家族管理员页
        mBinding?.mActFamilyCreateAdministratorCl?.onClick {
            mProvider?.startFamilyAdmin(this, mGroupId, FamilyConstant.REQUEST_CODE_ADMINISTRATOR)
        }
        // 家族成员列表页
        mBinding?.mActFamilyCreateMemberCl?.onClick {
            mProvider?.startFamilyMember(this, mGroupId, FamilyConstant.REQUEST_CODE_MEMBER)
        }
        //分区管理
        mBinding?.mSectionManagerCl?.onClick {
            mProvider?.startFamilySectionManage(this,mGroupId,FamilyConstant.REQUEST_CODE_CHANGE_SECTION)
        }
        //发布管理
        mBinding?.mPublishManagerCl?.onClick {
            mProvider?.startFamilyPublishPermission(this, mPublishPermission, mGroupId,FamilyConstant.REQUEST_CODE_CHANGE_PUBLISH_PERMISSION)
        }

        // 点击确定按钮
        mBinding?.mActFamilyCreateBtn?.onClick {
            // 保存家族
            save()
        }
        // 接口错误
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun initData() {
        if (mGroupId > 0) {
            updateMaxCount(-1)
            // 群组详情
            mViewModel?.getGroupDetail(mGroupId)
        } else {
            // 用户可创建多少群组
            mViewModel?.getCreateMaxCount()
        }
    }

    override fun startObserve() {
        // 用户可创建群组数（创建）
        observeCreateGroupCount()
        // 创建群组
        observeCreateGroup()
        // 修改群组
        observeEditGroup()
        // 群组详情（编辑）
        observeGroupDetail()
    }

    /**
     * 用户可创建群组数（创建）
     */
    private fun observeCreateGroupCount() {
        mViewModel?.createGroupCountUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    updateMaxCount(this.count ?: -1)
                }
            }
        }
    }

    /**
     * 创建群组
     */
    private fun observeCreateGroup() {
        mViewModel?.createGroupUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // status 1 群组创建成功 2 群组创建失败
                    if (this.status == 1L) {
                        showToast("提交成功，等待审核")
                        postCreateFamily()
                        // 返回上一页
                        finish()
                    } else {
                        showToast(this.failMsg)
                    }
                }

                isEmpty.apply {
                    if (this) {
                        showToast("创建失败")
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
     * 修改群组
     */
    private fun observeEditGroup() {
        mViewModel?.editGroupUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // status 1 群组创建成功 2 群组创建失败
                    if (this.status == 1L) {
                        // 停留在当前页
                        showToast("提交成功，等待审核")
                        // 更新用于编辑比较的群组信息
                        mGroup?.uploadId = mUploadID
                        mGroup?.groupName = mName
                        mGroup?.groupDes = mDes
                        mGroup?.joinPermission = mPermission
                        updateSaveBtnStatus(false)
                    } else {
                        showToast(this.failMsg)
                    }
                }

                isEmpty.apply {
                    if (this) {
                        showToast("修改失败")
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
     * 群组详情（编辑）
     */
    private fun observeGroupDetail() {
        mViewModel?.groupDetailUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (mOnlyUpdateAdminCount) {
                        // 更新管理员数
                        updateAdministratorCount(this.administratorCount)
                        mOnlyUpdateAdminCount = false
                    }
                    if (mOnlyUpdateMemberCount) {
                        // 更新成员数
                        updateMemberCount(this.groupPeopleCountStr)
                        mOnlyUpdateMemberCount = false
                    }
                    //分区数量显示
                    if (mOnlyUpdateSectionCount) {
                        updateSectionCount((groupSections?.size)?:0)
                        mOnlyUpdateSectionCount = false
                    }
                    if (!mOnlyUpdateAdminCount && !mOnlyUpdateMemberCount) {
                        // 刚进入编辑页
                        updateGroupUI(this)
                    }
                }

                isEmpty.apply {
                    if (this && !mOnlyUpdateAdminCount && !mOnlyUpdateMemberCount) {
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    if (!mOnlyUpdateAdminCount && !mOnlyUpdateMemberCount) {
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    }
                }

                netError?.apply {
                    if (!mOnlyUpdateAdminCount && !mOnlyUpdateMemberCount) {
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
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
            MultiStateView.VIEW_STATE_EMPTY,
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                initData()
            }
        }
    }

    /**
     * 更新可创建群组数(创建）
     * @param maxCount
     */
    private fun updateMaxCount(maxCount: Long) {
        mBinding?.mActFamilyCreateMaxCountTv?.let {
            // 0也显示，接口失败不显示
            it.isVisible = maxCount >= 0
            if (it.isVisible) {
                it.text = String.format(getString(R.string.family_create_max_count), maxCount)
            }
        }
    }

    /**
     * 更新页面群组信息（编辑）
     */
    private fun updateGroupUI(group: Group?) {
        group?.let {
            // 群主才能编辑群组信息
            if (isLogin() && group.userType == GroupUser.USER_TYPE_CREATOR) {
                mGroup = it

                // 创建后分类不能修改
                mUploadID = it.uploadId ?: ""
                mName = it.groupName ?: ""
                mDes = it.groupDes ?: ""
                mPermission = group.joinPermission

                // 编辑状态更新页面信息
                updateImg(it.groupImg)
                updateName(it.groupName)
                updateDes(it.groupDes)
                updatePermission(it.joinPermission)
                updateAdministratorCount(it.administratorCount)
                updateMemberCount(it.groupPeopleCountStr)
                //分区数量显示
                updateSectionCount((it.groupSections?.size)?:0)
                //发布权限展示
                updatePublishState(it.groupAuthority)
            } else {
                // 返回上一页
                finish()
            }
        }
    }

    /**
     * 更新发布权限
     */
    private fun updatePublishState(groupAuthority: Long) {
        mPublishPermission = groupAuthority
        when (groupAuthority) {
            FamilyDetail.GROUP_AUTHORITY_FREE -> {
                mBinding?.mPublishManagerCountTv?.text =
                    getString(R.string.family_publish_no_permission_review)
            }
            FamilyDetail.GROUP_AUTHORITY_JOIN -> {
                mBinding?.mPublishManagerCountTv?.text =
                    getString(R.string.family_publish_join_permission_review)
            }
            else -> {
                mBinding?.mPublishManagerCountTv?.text =
                    getString(R.string.family_publish_manager_permission_review)
            }
        }
    }

    /**
     * 更新section的数量
     */
    private fun updateSectionCount(groupSectionsSize: Int) {
        val countStr = "${groupSectionsSize}/10"
        mBinding?.mSectionManagerCountTv?.text = countStr
    }

    /**
     * 跳转系统浏览器
     */
    private fun gotoWeb(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        startActivity(intent)
    }

    /**
     * 创建时：检查是否为空
     */
    private fun checkEmpty() {
        val clickable = !TextUtils.isEmpty(mUploadID) && !TextUtils.isEmpty(mName)
                && mPermission != FamilyDetail.PERMISSION_NULL && mPrimaryCategoryId > 0
        updateSaveBtnStatus(clickable)
    }

    /**
     * 编辑时：检查填写内容是否有变化
     */
    private fun checkChange() {
        val clickable = !mUploadID.equals(mGroup?.uploadId) || !mName.equals(mGroup?.groupName)
                || !mDes.equals(mGroup?.groupDes) || mPermission != mGroup?.joinPermission
        updateSaveBtnStatus(clickable)
    }

    /**
     * 按钮是否可点击
     */
    private fun updateSaveBtnStatus(clickable: Boolean) {
        mBinding?.mActFamilyCreateBtn?.let {
            if (clickable) {
                ShapeExt.setGradientColorWithColor(
                    it, GradientDrawable.Orientation.BL_TR,
                    getColor(R.color.color_20a0da), getColor(R.color.color_1bafe0), 20
                )
            } else {
                ShapeExt.setShapeColorAndCorner(it, R.color.color_e3e5ed, 20)
            }
            it.isClickable = clickable
        }
    }

    /**
     * 显示上传图片组件
     */
    private fun showUploadImgFragment() {
        showPhotoAlbumFragment(
            isUploadImageInComponent = true,
            imageFileType = CommConstant.IMAGE_UPLOAD_COMMON,
            limitedCount = 1L
        ).apply {
            actionSelectPhotos = {
                // 图片上传成功后
                it[0].let { uploadImage ->
                    uploadImage.fileID?.let { fileID ->
                        mUploadID = fileID
                        // 更新群组封面图
                        updateImg(uploadImage.url)
                        if (mGroupId > 0L) {
                            // 编辑：变化检查
                            checkChange()
                        } else {
                            // 创建：空检查
                            checkEmpty()
                        }
                    }
                }
            }
        }
    }

    /**
     * 保存家族
     */
    private fun save() {
        if (mGroupId > 0L) {
            // 编辑群组
            mViewModel?.postCommunityEditGroup(mGroupId, mUploadID, mName, mDes, mPermission)
        } else {
            // 创建群组
            mViewModel?.postCommunityCreateGroup(
                mUploadID,
                mName,
                mDes,
                mPrimaryCategoryId,
                mPermission
            )
        }
    }

    /**
     * 从其他页面回来
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == FamilyConstant.RESULT_CODE_ADD_ADMINISTRATOR) {
            // 从添加管理员页面返回：更新管理员数
            updateMemberCount(true, false)
        }

        when (requestCode) {
            FamilyConstant.REQUEST_CODE_CHANGE_NAME_DES -> {
                data?.let {
                    var editType = it.getIntExtra(
                        FamilyConstant.KEY_FAMILY_EDIT_INFO_TYPE,
                        FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME
                    )
                    var content = it.getStringExtra(FamilyConstant.KEY_FAMILY_EDIT_INFO_CONTENT)
                    content?.let {
                        when (editType) {
                            FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME -> {
                                mName = it
                                updateName(mName)
                                if (mGroupId == 0L) {
                                    checkEmpty()
                                }
                            }
                            FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES -> {
                                // 简介可以为空，不用空判断
                                mDes = it
                                updateDes(mDes)
                            }
                            else -> {
                            }
                        }
                        if (mGroupId > 0) {
                            // 编辑：变化检查
                            checkChange()
                        }
                        return
                    }
                }
            }
            FamilyConstant.REQUEST_CODE_CHANGE_PERMISSION -> { // 更新加入权限
                data?.let {
                    mPermission = it.getLongExtra(
                        FamilyConstant.KEY_FAMILY_PERMISSION,
                        FamilyDetail.PERMISSION_NULL
                    )
                    if (mPermission != FamilyDetail.PERMISSION_NULL) {
                        updatePermission(mPermission)
                        if (mGroupId > 0L) {
                            // 编辑：变化检查
                            checkChange()
                        } else {
                            // 创建：空检查
                            checkEmpty()
                        }
                    }
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_PUBLISH_PERMISSION -> {
                //发布管理
                data?.apply {
                    mPublishPermission = getLongExtra(FamilyConstant.KEY_FAMILY_PUBLISH_PERMISSION,1L)
                    updatePublishState(mPublishPermission)
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_SECTION -> {
                //分区管理
                data?.apply {
                    updateSectionCount(getIntExtra(FamilyConstant.KEY_FAMILY_SECTION_COUNT,0))
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_CATEGORY -> { // 更新分类
                data?.let {
                    mPrimaryCategoryId =
                        it.getLongExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_ID, 0)
                    mPrimaryCategoryName =
                        it.getStringExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_NAME)
                    mPrimaryCategoryName?.let { categoryName ->
                        if (mPrimaryCategoryId > 0 && categoryName.isNotEmpty()) {
                            updateCategory(categoryName)
                            // 创建时才有家族分类选择
                            checkEmpty()
                        }
                    }
                }
            }
            FamilyConstant.REQUEST_CODE_ADMINISTRATOR -> {               // 更新管理员数
                updateMemberCount(true, false)
            }
            FamilyConstant.REQUEST_CODE_MEMBER -> {            // 更新成员数
                updateMemberCount(false, true)
            }
            else -> {
            }
        }

        // 相册回调
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 更新群组封面图
     */
    private fun updateImg(imgUrl: String?) {
        imgUrl?.let {
            if (it.isEmpty()) {
                return
            }
            // 封面图
            mBinding?.mActFamilyCreateImgIv?.loadImage(
                data = it,
                width = 80,
                height = 80,
                circleCrop = false
            )
            // 蒙层
            mBinding?.mActFamilyCreateImgCoverIv?.visibility = View.VISIBLE
            // 相机图片
            mBinding?.mActFamilyCreateCameraIv?.setImageResource(R.drawable.ic_family_create_camera_light)
        }
    }

    /**
     * 更新群组名
     * @param groupName
     */
    private fun updateName(groupName: String?) {
        groupName?.let {
            // 名称不能为空
            mBinding?.mActFamilyCreateNameTv?.text = groupName
        }
    }

    /**
     * 更新简介
     * @param groupDes
     */
    private fun updateDes(groupDes: String?) {
        // 简介可以清空
        mBinding?.mActFamilyCreateDesTv?.text = groupDes ?: ""
    }

    /**
     * 更新加入权限
     * @param permission
     */
    private fun updatePermission(permission: Long) {
        mBinding?.mActFamilyCreatePermissionTv?.let {
            if (permission == FamilyDetail.PERMISSION_FREE) {
                it.text = getString(R.string.family_join_permission_free)
            } else if (permission == FamilyDetail.PERMISSION_REVIEW) {
                it.text = getString(R.string.family_join_permission_review)
            } else {
                it.text = ""
            }
        }
    }

    /**
     * 更新分类
     * @param categoryName
     */
    private fun updateCategory(categoryName: String) {
        mBinding?.mActFamilyCreateCategoryTv?.text = categoryName
    }

    /**
     * 更新管理员数
     * @param count
     */
    private fun updateAdministratorCount(count: Long?) {
        // 直接显示原数，不做格式处理
        mBinding?.mActFamilyCreateAdministratorCountTv?.text =
            String.format(getString(R.string.family_administrator_count), count ?: 0)
    }

    /**
     * 更新成员数
     * @param count
     */
    private fun updateMemberCount(count: String?) {
        // 直接显示原数，不做格式处理
        mBinding?.mActFamilyCreateMemberCountTv?.text = count ?: ""
    }

    /**
     * 更新管理员和成员数
     */
    private fun updateMemberCount(
        updateAdmin: Boolean,
        updateMember: Boolean,
        updateSection: Boolean? = false
    ) {
        mOnlyUpdateAdminCount = updateAdmin
        mOnlyUpdateMemberCount = updateMember
        mOnlyUpdateSectionCount = updateSection ?: false
        mViewModel?.getGroupDetail(mGroupId)
    }

}