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
 * @desc ??????|???????????????
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
    private var mGroup: Group? = null  // ??????????????????????????????????????????
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
        // ??????
        mBinding?.mActFamilyCreateCategoryLineView?.isGone = isEdit
        mBinding?.mActFamilyCreateCategoryCl?.isGone = isEdit
        // ?????????
        mBinding?.mActFamilyCreateAdministratorLineView?.isVisible = isEdit
        mBinding?.mActFamilyCreateAdministratorCl?.isVisible = isEdit
        // ??????
        mBinding?.mActFamilyCreateMemberLineView?.isVisible = isEdit
        mBinding?.mActFamilyCreateMemberCl?.isVisible = isEdit
        //????????????
        mBinding?.mSectionManagerCl?.isVisible = isEdit
        mBinding?.mActFamilySectionLineView?.isVisible = isEdit
        //????????????
        mBinding?.mActFamilyPublishLineView?.isVisible = isEdit
        mBinding?.mPublishManagerCl?.isVisible = isEdit
        // btn
        mBinding?.mActFamilyCreateBtn?.text =
            if (isEdit) getString(R.string.community_ok_btn) else getString(R.string.community_create_btn)
        // ??????
        initEvent()
        // ????????????????????????isClickable?????????setOnClickListener???????????????????????????????????????????????????
        updateSaveBtnStatus(false)
    }

    private fun initEvent() {
        // ???????????????
        mBinding?.mActFamilyCreateImgCl?.onClick {
            // ????????????????????????
            showUploadImgFragment()
        }
        // ????????????
        mBinding?.mActFamilyCreateNameCl?.onClick {
            // ??????????????????|?????????
            val name = mBinding?.mActFamilyCreateNameTv?.text.toString()
            mProvider?.startFamilyEditInfo(
                this,
                FamilyConstant.FAMILY_EDIT_INFO_TYPE_NAME,
                name,
                FamilyConstant.REQUEST_CODE_CHANGE_NAME_DES
            )
        }
        // ????????????
        mBinding?.mActFamilyCreateDesCl?.onClick {
            // ??????????????????|?????????
            val des = mBinding?.mActFamilyCreateDesTv?.text.toString()
            mProvider?.startFamilyEditInfo(
                this,
                FamilyConstant.FAMILY_EDIT_INFO_TYPE_DES,
                des,
                FamilyConstant.REQUEST_CODE_CHANGE_NAME_DES
            )
        }
        // ???????????????
        mBinding?.mActFamilyCreatePermissionCl?.onClick {
            mProvider?.startFamilyPermission(this, mPermission, FamilyConstant.REQUEST_CODE_CHANGE_PERMISSION)
        }
        // ???????????????
        mBinding?.mActFamilyCreateCategoryCl?.onClick {
            mProvider?.startFamilyCategory(
                this,
                mPrimaryCategoryId,
                FamilyConstant.REQUEST_CODE_CHANGE_CATEGORY
            )
        }
        // ??????????????????
        mBinding?.mActFamilyCreateAdministratorCl?.onClick {
            mProvider?.startFamilyAdmin(this, mGroupId, FamilyConstant.REQUEST_CODE_ADMINISTRATOR)
        }
        // ?????????????????????
        mBinding?.mActFamilyCreateMemberCl?.onClick {
            mProvider?.startFamilyMember(this, mGroupId, FamilyConstant.REQUEST_CODE_MEMBER)
        }
        //????????????
        mBinding?.mSectionManagerCl?.onClick {
            mProvider?.startFamilySectionManage(this,mGroupId,FamilyConstant.REQUEST_CODE_CHANGE_SECTION)
        }
        //????????????
        mBinding?.mPublishManagerCl?.onClick {
            mProvider?.startFamilyPublishPermission(this, mPublishPermission, mGroupId,FamilyConstant.REQUEST_CODE_CHANGE_PUBLISH_PERMISSION)
        }

        // ??????????????????
        mBinding?.mActFamilyCreateBtn?.onClick {
            // ????????????
            save()
        }
        // ????????????
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun initData() {
        if (mGroupId > 0) {
            updateMaxCount(-1)
            // ????????????
            mViewModel?.getGroupDetail(mGroupId)
        } else {
            // ???????????????????????????
            mViewModel?.getCreateMaxCount()
        }
    }

    override fun startObserve() {
        // ????????????????????????????????????
        observeCreateGroupCount()
        // ????????????
        observeCreateGroup()
        // ????????????
        observeEditGroup()
        // ????????????????????????
        observeGroupDetail()
    }

    /**
     * ????????????????????????????????????
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
     * ????????????
     */
    private fun observeCreateGroup() {
        mViewModel?.createGroupUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // status 1 ?????????????????? 2 ??????????????????
                    if (this.status == 1L) {
                        showToast("???????????????????????????")
                        postCreateFamily()
                        // ???????????????
                        finish()
                    } else {
                        showToast(this.failMsg)
                    }
                }

                isEmpty.apply {
                    if (this) {
                        showToast("????????????")
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
                        showToast("?????????")
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun observeEditGroup() {
        mViewModel?.editGroupUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    // status 1 ?????????????????? 2 ??????????????????
                    if (this.status == 1L) {
                        // ??????????????????
                        showToast("???????????????????????????")
                        // ???????????????????????????????????????
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
                        showToast("????????????")
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
                        showToast("?????????")
                    }
                }
            }
        }
    }

    /**
     * ????????????????????????
     */
    private fun observeGroupDetail() {
        mViewModel?.groupDetailUiState?.observe(this) {
            it.apply {
                showOrHideProgressDialog(showLoading)

                success?.apply {
                    if (mOnlyUpdateAdminCount) {
                        // ??????????????????
                        updateAdministratorCount(this.administratorCount)
                        mOnlyUpdateAdminCount = false
                    }
                    if (mOnlyUpdateMemberCount) {
                        // ???????????????
                        updateMemberCount(this.groupPeopleCountStr)
                        mOnlyUpdateMemberCount = false
                    }
                    //??????????????????
                    if (mOnlyUpdateSectionCount) {
                        updateSectionCount((groupSections?.size)?:0)
                        mOnlyUpdateSectionCount = false
                    }
                    if (!mOnlyUpdateAdminCount && !mOnlyUpdateMemberCount) {
                        // ??????????????????
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
     * ????????????????????????"??????/??????"???????????????
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
     * ????????????????????????(?????????
     * @param maxCount
     */
    private fun updateMaxCount(maxCount: Long) {
        mBinding?.mActFamilyCreateMaxCountTv?.let {
            // 0?????????????????????????????????
            it.isVisible = maxCount >= 0
            if (it.isVisible) {
                it.text = String.format(getString(R.string.family_create_max_count), maxCount)
            }
        }
    }

    /**
     * ????????????????????????????????????
     */
    private fun updateGroupUI(group: Group?) {
        group?.let {
            // ??????????????????????????????
            if (isLogin() && group.userType == GroupUser.USER_TYPE_CREATOR) {
                mGroup = it

                // ???????????????????????????
                mUploadID = it.uploadId ?: ""
                mName = it.groupName ?: ""
                mDes = it.groupDes ?: ""
                mPermission = group.joinPermission

                // ??????????????????????????????
                updateImg(it.groupImg)
                updateName(it.groupName)
                updateDes(it.groupDes)
                updatePermission(it.joinPermission)
                updateAdministratorCount(it.administratorCount)
                updateMemberCount(it.groupPeopleCountStr)
                //??????????????????
                updateSectionCount((it.groupSections?.size)?:0)
                //??????????????????
                updatePublishState(it.groupAuthority)
            } else {
                // ???????????????
                finish()
            }
        }
    }

    /**
     * ??????????????????
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
     * ??????section?????????
     */
    private fun updateSectionCount(groupSectionsSize: Int) {
        val countStr = "${groupSectionsSize}/10"
        mBinding?.mSectionManagerCountTv?.text = countStr
    }

    /**
     * ?????????????????????
     */
    private fun gotoWeb(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val uri = Uri.parse(url)
        intent.data = uri
        startActivity(intent)
    }

    /**
     * ??????????????????????????????
     */
    private fun checkEmpty() {
        val clickable = !TextUtils.isEmpty(mUploadID) && !TextUtils.isEmpty(mName)
                && mPermission != FamilyDetail.PERMISSION_NULL && mPrimaryCategoryId > 0
        updateSaveBtnStatus(clickable)
    }

    /**
     * ?????????????????????????????????????????????
     */
    private fun checkChange() {
        val clickable = !mUploadID.equals(mGroup?.uploadId) || !mName.equals(mGroup?.groupName)
                || !mDes.equals(mGroup?.groupDes) || mPermission != mGroup?.joinPermission
        updateSaveBtnStatus(clickable)
    }

    /**
     * ?????????????????????
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
     * ????????????????????????
     */
    private fun showUploadImgFragment() {
        showPhotoAlbumFragment(
            isUploadImageInComponent = true,
            imageFileType = CommConstant.IMAGE_UPLOAD_COMMON,
            limitedCount = 1L
        ).apply {
            actionSelectPhotos = {
                // ?????????????????????
                it[0].let { uploadImage ->
                    uploadImage.fileID?.let { fileID ->
                        mUploadID = fileID
                        // ?????????????????????
                        updateImg(uploadImage.url)
                        if (mGroupId > 0L) {
                            // ?????????????????????
                            checkChange()
                        } else {
                            // ??????????????????
                            checkEmpty()
                        }
                    }
                }
            }
        }
    }

    /**
     * ????????????
     */
    private fun save() {
        if (mGroupId > 0L) {
            // ????????????
            mViewModel?.postCommunityEditGroup(mGroupId, mUploadID, mName, mDes, mPermission)
        } else {
            // ????????????
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
     * ?????????????????????
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == FamilyConstant.RESULT_CODE_ADD_ADMINISTRATOR) {
            // ???????????????????????????????????????????????????
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
                                // ????????????????????????????????????
                                mDes = it
                                updateDes(mDes)
                            }
                            else -> {
                            }
                        }
                        if (mGroupId > 0) {
                            // ?????????????????????
                            checkChange()
                        }
                        return
                    }
                }
            }
            FamilyConstant.REQUEST_CODE_CHANGE_PERMISSION -> { // ??????????????????
                data?.let {
                    mPermission = it.getLongExtra(
                        FamilyConstant.KEY_FAMILY_PERMISSION,
                        FamilyDetail.PERMISSION_NULL
                    )
                    if (mPermission != FamilyDetail.PERMISSION_NULL) {
                        updatePermission(mPermission)
                        if (mGroupId > 0L) {
                            // ?????????????????????
                            checkChange()
                        } else {
                            // ??????????????????
                            checkEmpty()
                        }
                    }
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_PUBLISH_PERMISSION -> {
                //????????????
                data?.apply {
                    mPublishPermission = getLongExtra(FamilyConstant.KEY_FAMILY_PUBLISH_PERMISSION,1L)
                    updatePublishState(mPublishPermission)
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_SECTION -> {
                //????????????
                data?.apply {
                    updateSectionCount(getIntExtra(FamilyConstant.KEY_FAMILY_SECTION_COUNT,0))
                }
            }

            FamilyConstant.REQUEST_CODE_CHANGE_CATEGORY -> { // ????????????
                data?.let {
                    mPrimaryCategoryId =
                        it.getLongExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_ID, 0)
                    mPrimaryCategoryName =
                        it.getStringExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_NAME)
                    mPrimaryCategoryName?.let { categoryName ->
                        if (mPrimaryCategoryId > 0 && categoryName.isNotEmpty()) {
                            updateCategory(categoryName)
                            // ?????????????????????????????????
                            checkEmpty()
                        }
                    }
                }
            }
            FamilyConstant.REQUEST_CODE_ADMINISTRATOR -> {               // ??????????????????
                updateMemberCount(true, false)
            }
            FamilyConstant.REQUEST_CODE_MEMBER -> {            // ???????????????
                updateMemberCount(false, true)
            }
            else -> {
            }
        }

        // ????????????
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * ?????????????????????
     */
    private fun updateImg(imgUrl: String?) {
        imgUrl?.let {
            if (it.isEmpty()) {
                return
            }
            // ?????????
            mBinding?.mActFamilyCreateImgIv?.loadImage(
                data = it,
                width = 80,
                height = 80,
                circleCrop = false
            )
            // ??????
            mBinding?.mActFamilyCreateImgCoverIv?.visibility = View.VISIBLE
            // ????????????
            mBinding?.mActFamilyCreateCameraIv?.setImageResource(R.drawable.ic_family_create_camera_light)
        }
    }

    /**
     * ???????????????
     * @param groupName
     */
    private fun updateName(groupName: String?) {
        groupName?.let {
            // ??????????????????
            mBinding?.mActFamilyCreateNameTv?.text = groupName
        }
    }

    /**
     * ????????????
     * @param groupDes
     */
    private fun updateDes(groupDes: String?) {
        // ??????????????????
        mBinding?.mActFamilyCreateDesTv?.text = groupDes ?: ""
    }

    /**
     * ??????????????????
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
     * ????????????
     * @param categoryName
     */
    private fun updateCategory(categoryName: String) {
        mBinding?.mActFamilyCreateCategoryTv?.text = categoryName
    }

    /**
     * ??????????????????
     * @param count
     */
    private fun updateAdministratorCount(count: Long?) {
        // ???????????????????????????????????????
        mBinding?.mActFamilyCreateAdministratorCountTv?.text =
            String.format(getString(R.string.family_administrator_count), count ?: 0)
    }

    /**
     * ???????????????
     * @param count
     */
    private fun updateMemberCount(count: String?) {
        // ???????????????????????????????????????
        mBinding?.mActFamilyCreateMemberCountTv?.text = count ?: ""
    }

    /**
     * ???????????????????????????
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