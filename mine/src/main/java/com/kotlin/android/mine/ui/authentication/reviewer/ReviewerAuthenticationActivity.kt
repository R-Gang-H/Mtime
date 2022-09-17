package com.kotlin.android.mine.ui.authentication.reviewer

import android.content.Intent
import android.text.TextUtils
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.app.router.ext.openMine
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.component.getPhotoAlbumFragment
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.checkIdCard
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.isNotRealName
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean.Companion.TYPE_REVIEW_PERSON
import com.kotlin.android.mine.bindingadapter.getSubmitDrawable
import com.kotlin.android.mine.databinding.ActivityReviewerAuthenticationBinding
import com.kotlin.android.mine.ui.widgets.AuthenTakePhotoView
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.activity_reviewer_authentication.*

/**
 * 身份认证-影评人认证
 */
class ReviewerAuthenticationActivity : BaseVMActivity<ReviewerAuthenticationViewModel, ActivityReviewerAuthenticationBinding>() {

    override fun initVM(): ReviewerAuthenticationViewModel = viewModels<ReviewerAuthenticationViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this, false).setTitle(R.string.mine_authen_reviewer).create()
    }

    override fun initView() {
        scrollView?.setCanScroll(false)
        submitBtn?.apply {
            background = getSubmitDrawable()
            onClick {//提交
                doSubmit()
            }
        }

        argeeCB?.setOnCheckedChangeListener { compoundButton, b ->
            setCanDoSubmit()
        }

        inputView?.setInputType(TYPE_REVIEW_PERSON)
        initPhotoView()


        ShapeExt.setGradientColorWithColor(expandView, startColor = getColor(R.color.color_66ffffff), endColor = getColor(R.color.color_ffffff))
        expandAllTv?.onClick {
            it.gone()
            expandView?.gone()
            scrollView?.setCanScroll(true)
        }

//        输入内容
        inputView?.setListener {
            setCanDoSubmit()
        }
//        选择影评
        reviewView?.setRefreshListener {
            setCanDoSubmit()
        }
    }

    private fun setCanDoSubmit() {
        //        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片

        val selectReviewIdList = reviewView?.getSelectReviewIdList() ?: mutableListOf()//选中的影评
        submitBtn?.isEnabled = isContentNotEmpty(realName) && isContentNotEmpty(idCard) && idCardPhoto != null && selectReviewIdList.size == 3 && (argeeCB?.isChecked == true)
    }

    private fun isContentNotEmpty(content: String): Boolean {
        return TextUtils.isEmpty(content).not()
    }


    private fun doSubmit() {
//        必填：用户名、真实姓名、身份证号
        val realName = inputView?.getRealName().orEmpty()//真实姓名
        val idCard = inputView?.getIdCard().orEmpty()//身份证号
        val idCardPhoto = photoView?.getIdCardPhoto()//身份证照片

        val selectReviewIdList = reviewView?.getSelectReviewIdList() ?: mutableListOf()//选中的影评

        if (canSubmit(realName, idCard, idCardPhoto, selectReviewIdList).not()) {//不可以提交
            showToast(R.string.mine_authen_submit_error_tips)
//            需要判断是否所有字段都为空，都为空，需要置灰处理
            submitBtn?.isEnabled = isAllEmpty(realName, idCard, idCardPhoto, selectReviewIdList).not()
            return
        }

        mViewModel?.saveAuth(realName, idCard, selectReviewIdList, idCardPhoto)
    }

    /**
     * 所有必填字段都没有填写
     */
    private fun isAllEmpty(realName: String, idCard: String, idCardPhoto: PhotoInfo?, selectReviewIdList: MutableList<Long>): Boolean {
        return TextUtils.isEmpty(realName.trim()) && TextUtils.isEmpty(idCard.trim()) && idCardPhoto == null && selectReviewIdList.isEmpty()
    }

    /**
     * 校验必填字段
     */
    private fun canSubmit(realName: String, idCard: String, idCardPhoto: PhotoInfo?, selectReviewIdList: MutableList<Long>): Boolean {
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

        if (selectReviewIdList.isEmpty()) {
            canSubmit = false
        }


        return canSubmit
    }


    private fun initPhotoView() {
        photoView?.setType(TYPE_REVIEW_PERSON) {
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
        mViewModel?.getMyLongCommentList()
    }

    override fun startObserve() {
        mViewModel?.myCommentListState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    reviewView?.setData(this)
                }

                netError?.showToast()
                error?.showToast()
            }
        })

        mViewModel?.saveAuthState?.observe(this, Observer {
            it?.apply {
                showOrHideProgressDialog(showLoading)
                success?.apply {
                    if (success) {//提交成功
                        showToast(R.string.mine_auth_submit_reviewer_success)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        getPhotoAlbumFragment()?.onActivityResult(requestCode, resultCode, data)
    }
}