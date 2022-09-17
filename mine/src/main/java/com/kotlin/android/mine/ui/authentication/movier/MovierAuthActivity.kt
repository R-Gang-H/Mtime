package com.kotlin.android.mine.ui.authentication.movier

import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.router.ext.openMine
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.checkIdCard
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.isNotRealName
import com.kotlin.android.ktx.ext.isPhoneNum
import com.kotlin.android.ktx.ext.isPostBox
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthMovieTypeBean
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.bindingadapter.getSubmitDrawable
import com.kotlin.android.mine.databinding.ActivityMovierAuthBinding
import com.kotlin.android.mine.ui.widgets.AuthenTakePhotoView
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_movier_auth.*


/**
 * 身份认证-电影人认证
 */
class MovierAuthActivity : BaseVMActivity<MovierAuthViewModel, ActivityMovierAuthBinding>() {

    override fun initVM(): MovierAuthViewModel = viewModels<MovierAuthViewModel>().value


    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this, false).setTitle(R.string.mine_authen_movier).create()
    }

    override fun initView() {
        submitBtn?.apply {
            background = getSubmitDrawable()
            onClick {//提交
                doSubmit()
            }
        }

        argeeCB?.setOnCheckedChangeListener { compoundButton, b ->
            setCanDoSubmit()
        }

        inputView?.apply {
            setInputType(AuthenticatonCardViewBean.TYPE_MOVIE_PERSON)
            setListener {
                setCanDoSubmit()
            }
        }
        productionView?.refreshListener = {
            setCanDoSubmit()
        }
        initPhotoView()

    }

    private fun setCanDoSubmit() {
        //        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val phoneNum = inputView?.getPhoneNum().orEmpty()//手机号
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片
        val officalPhoto = photoView?.getOfficalPhoto()//公函照片
        val roleId = productionView?.getSelectTagId() ?: 0L//选中的角色

        val productionName = productionView?.getProductionName().orEmpty()//作品名称
        submitBtn?.isEnabled = isContentNotEmpty(realName) && isContentNotEmpty(idCard) && isContentNotEmpty(phoneNum)
                && idCardPhoto != null && officalPhoto != null && roleId != 0L && isContentNotEmpty(productionName) && argeeCB?.isChecked == true
    }

    private fun isContentNotEmpty(content: String): Boolean {
        return TextUtils.isEmpty(content).not()
    }

    private fun doSubmit() {
//        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val phoneNum = inputView?.getPhoneNum().orEmpty()//手机号
        val postNum = inputView?.getPostNum().orEmpty()//邮箱
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片
        val officalPhoto = photoView?.getOfficalPhoto()//公函照片
        val roleId = productionView?.getSelectTagId() ?: 0L//选中的角色

        val productionName = productionView?.getProductionName().orEmpty()//作品名称

        if (canSubmit(realName, idCard, idCardPhoto, officalPhoto, roleId, phoneNum, productionName).not()) {//不可以提交
            showToast(R.string.mine_authen_submit_error_tips)
//            需要判断是否所有字段都为空，都为空，需要置灰处理
            submitBtn?.isEnabled = isAllEmpty(realName, idCard, idCardPhoto, officalPhoto, roleId, phoneNum, productionName).not()
            return
        }
        var isError: Boolean = false//手机号或者邮箱是否有错
        if (phoneNum.isPhoneNum().not()) {//不是手机号
            isError = true
            inputView?.phoneError()
        }

        if (TextUtils.isEmpty(postNum).not() && postNum.isPostBox().not()) {//不是邮箱
            isError = true
            inputView?.postError()
        }

        if (isError) {
            return
        }

        mViewModel?.saveAuth(realName, idCard, phoneNum, postNum, idCardPhoto, officalPhoto, productionName, mutableListOf(productionView?.getSelectRoleName().orEmpty()))


    }

    /**
     * 所有必填字段都没有填写
     */
    private fun isAllEmpty(realName: String, idCard: String, idCardPhoto: PhotoInfo?, officalPhoto: PhotoInfo?, tagId: Long, phoneNum: String, productionName: String): Boolean {
        return TextUtils.isEmpty(realName.trim()) && TextUtils.isEmpty(idCard.trim()) && idCardPhoto == null && officalPhoto == null && tagId == 0L && TextUtils.isEmpty(phoneNum) && TextUtils.isEmpty(productionName)
    }

    /**
     * 校验必填字段
     */

    private fun canSubmit(realName: String, idCard: String, idCardPhoto: PhotoInfo?, officalPhoto: PhotoInfo?, tagId: Long, phoneNum: String, productionName: String): Boolean {
        var canSubmit = true
        if (realName.isNotRealName()) {
            canSubmit = false
            inputView?.realNameError()
        }

        if (TextUtils.isEmpty(idCard.trim())) {
            canSubmit = false
            inputView?.idCardError()
        } else {
//            校验身份证号
            if (checkIdCard(idCard.trim()).not()) {
                canSubmit = false
                inputView?.idCardError()
            }
        }

        if (idCardPhoto == null) {
            canSubmit = false
        }

        if (officalPhoto == null) {
            canSubmit = false
        }
        if (tagId == 0L) {
            canSubmit = false
        }

        if (TextUtils.isEmpty(phoneNum)) {
            canSubmit = false
            inputView?.phoneError()
        }
        if (TextUtils.isEmpty(productionName)) {
            canSubmit = false
            productionView?.productionError()
        }

        return canSubmit
    }


    private fun initPhotoView() {
        photoView?.setType(AuthenticatonCardViewBean.TYPE_MOVIE_PERSON) {
            when (it) {
                AuthenTakePhotoView.AuthenPhotoType.ID_CARD_PHOTO -> {//点击身份证
                    showPhotoAlbumFragment(limitedCount = 1L).actionSelectPhotos = { photoList ->
                        "选择照片信息：$photoList"

                        if (photoList.isNotEmpty()) {
                            photoView.setIdCardImageView(photoList[0])
                            setCanDoSubmit()
                        }
                    }
                }
                AuthenTakePhotoView.AuthenPhotoType.OFFICAL_LETTER_PHOTO -> {//认证公函图片
                    showPhotoAlbumFragment(limitedCount = 1L).actionSelectPhotos = { photoList ->
                        "选择照片信息：$photoList"

                        if (photoList.isNotEmpty()) {
                            photoView.setVisitingCardImageView(photoList[0])
                            setCanDoSubmit()
                        }
                    }
                }
            }
        }
    }

    override fun initData() {
        mViewModel?.getRoleList()
    }

    override fun startObserve() {

//        电影人角色
        mViewModel?.movieRoleState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    val list = mutableListOf<AuthMovieTypeBean>()
                    list.add(AuthMovieTypeBean(0L, "角色", false, false))
                    this.forEachIndexed { index, s ->
                        list.add(AuthMovieTypeBean((index + 1).toLong(), s))
                    }
                    productionView?.setData(list)
                }

                error?.showToast()
                netError?.showToast()
            }
        })

//        提交认证消息结束
        mViewModel?.saveAuthState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (success) {
                        showToast(R.string.mine_auth_submit_success)
                        openMine()
                    } else {
                        error?.showToast()
                    }
                }
                error?.showToast()
                netError?.showToast()
            }
        })
    }

}