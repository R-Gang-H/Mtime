package com.kotlin.android.mine.ui.authentication.organization

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.data.ext.VariateExt
import com.kotlin.android.app.router.ext.openMine
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.checkIdCard
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.isNotRealName
import com.kotlin.android.ktx.ext.permission.isGranted
import com.kotlin.android.ktx.ext.permission.permissions
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.bindingadapter.getSubmitDrawable
import com.kotlin.android.mine.databinding.ActivityOrganizationAuthBinding
import com.kotlin.android.mine.ui.widgets.AuthenTakePhotoView
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_organization_auth.*
import java.io.File

/**
 * 身份认证-机构认证
 */
class OrganizationAuthActivity : BaseVMActivity<OrganizationAuthViewModel, ActivityOrganizationAuthBinding>() {

    override fun initVM(): OrganizationAuthViewModel = viewModels<OrganizationAuthViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this, false).setTitle(R.string.mine_authen_orgnization).create()
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
            setInputType(AuthenticatonCardViewBean.TYPE_ORGANIZATION)
            setListener {
                setCanDoSubmit()
            }
        }

        initPhotoView()

    }

    private fun setCanDoSubmit() {
        //        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片
        val officalPhoto = photoView?.getOfficalPhoto()//公函照片

        submitBtn?.isEnabled = isContentNotEmpty(realName) && isContentNotEmpty(idCard) && idCardPhoto != null && officalPhoto != null && (argeeCB?.isChecked == true)
    }

    private fun isContentNotEmpty(content: String): Boolean {
        return TextUtils.isEmpty(content).not()
    }

    private fun doSubmit() {
//        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片
        val officalPhoto = photoView?.getOfficalPhoto()//公函照片

        if (canSubmit(realName, idCard, idCardPhoto, officalPhoto).not()) {//不可以提交
            showToast(R.string.mine_authen_submit_error_tips)
//            需要判断是否所有字段都为空，都为空，需要置灰处理
            submitBtn?.isEnabled = isAllEmpty(realName, idCard, idCardPhoto, officalPhoto).not()
            return
        }

        mViewModel?.saveAuth(realName, idCard, idCardPhoto, officalPhoto)


    }

    /**
     * 所有必填字段都没有填写
     */
    private fun isAllEmpty(realName: String, idCard: String, idCardPhoto: PhotoInfo?, officalPhoto: PhotoInfo?): Boolean {
        return TextUtils.isEmpty(realName.trim()) && TextUtils.isEmpty(idCard.trim()) && idCardPhoto == null && officalPhoto == null
    }

    /**
     * 校验必填字段
     */
    private fun canSubmit(realName: String, idCard: String, idCardPhoto: PhotoInfo?, officalPhoto: PhotoInfo?): Boolean {
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


        return canSubmit
    }

    private fun initPhotoView() {
        photoView?.setType(AuthenticatonCardViewBean.TYPE_ORGANIZATION) {
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
                AuthenTakePhotoView.AuthenPhotoType.OFFICAL_LETTER_DOWN -> {//公证函下载
                    if (isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        downloadAuthTemplateImg()
                    } else {
                        permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                            onShowRationale {
                                downloadAuthTemplateImg()
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 下载认证公函
     */
    private fun downloadAuthTemplateImg() {
        mViewModel?.downloadAuthFile(VariateExt.authTemplateImg)
    }

    override fun initData() {
    }

    override fun startObserve() {
//        提交认证消息
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

        mViewModel?.downloadFileState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    runOnUiThread {
                        showToast(getString(R.string.mine_auth_pic_download_success).format(this))
                    }
                    val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                    val contentUri = Uri.fromFile(File(this))
                    mediaScanIntent.data = contentUri
                    sendBroadcast(mediaScanIntent)
                }
                netError?.apply {
                    showToast(R.string.mine_auth_pic_download_failed)
                    showOrHideProgressDialog(false)
                }
                error?.apply {
                    showOrHideProgressDialog(false)
                    showToast(R.string.mine_auth_pic_download_failed)
                }
            }
        })

    }
}