package com.kotlin.android.image.component.ui

import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.viewModels
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.CROP_TYPE_1_1
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.databinding.LayoutPhotoCropBinding
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.saveShareImage
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.layout_photo_album.titleBar
import kotlinx.android.synthetic.main.layout_photo_crop.*

/**
 *
 * Created on 2020/12/21.
 *
 * @author o.s
 */

class PhotoCropFragment : BaseVMDialogFragment<ImageViewModel, LayoutPhotoCropBinding>() {

    /**
     * 设置正确的上传图片类型 imageType 可以自动上传，并回调 [callback] 包含上传完成的图片网络地址 [PhotoInfo.url]；
     * 否则（如：imageType = -1L）直接回调 [callback] 包含保存的裁剪图片地址 [PhotoInfo.uploadPath]，可以自定义上传内容。
     */
    var imageType: Long = -1L

    /**
     * 裁剪完成（保存）时，回调照片对象，
     * 其中：[PhotoInfo] 可能包含成功上传的网络图片地址，也可能只包含保存本地裁剪的图片地址，这取决于 [imageType]。
     */
    var callback: ((photo: PhotoInfo) -> Unit)? = null

    /**
     * 上传头像回调头像图片地址
     */
    var avatar: ((avatarUrl: String) -> Unit)? = null

    /**
     * 裁剪的原始照片数据
     */
    var photo: PhotoInfo? = null
        set(value) {
            field = value
            fillData()
        }
    var cropType: String = CROP_TYPE_1_1
        set(value) {
            field = value
            setCropLayout()
        }

    private fun setCropLayout() {
        val constraintSet = ConstraintSet()
        constraintSet.clone(cropCL)
        constraintSet.setDimensionRatio(R.id.cropView, cropType)
        constraintSet.applyTo(cropCL)
        cropView?.requestLayout()
    }

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        }
        window = {
            setWindowAnimations(R.style.BottomDialogAnimation)
        }
        immersive = {
            dialog?.apply {
                activity?.immersive(this)
                    ?.statusBarColor(getColor(R.color.color_ffffff))
                    ?.statusBarDarkFont(true)
            }

        }
    }

    override fun initVM(): ImageViewModel = viewModels<ImageViewModel>().value

    override fun initView() {
        initTitleView()
        initContentView()
    }

    override fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        cropView?.recycle()
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    handleUploadResult(this)
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }

        mViewModel?.avatarUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (success) {
                        avatar?.invoke(headPic)
                    } else {
                        showToast(R.string.update_avatar_fail)
                    }
                    dismissAllowingStateLoss()
                }

                error?.apply {
                    showToast(R.string.update_avatar_fail)
                    dismissAllowingStateLoss()
                }

                netError?.apply {
                    showToast(this)
                    dismissAllowingStateLoss()
                }
            }
        }
        mViewModel?.userCenterUiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    if (success) {
                        avatar?.invoke(backgroundAppPic)
                    } else {
                        showToast(R.string.update_user_center_bg_fail)
                    }
                    dismissAllowingStateLoss()
                }

                error?.apply {
                    showToast(R.string.update_user_center_bg_fail)
                    dismissAllowingStateLoss()
                }

                netError?.apply {
                    showToast(this)
                    dismissAllowingStateLoss()
                }
            }
        }
    }

    private fun handleUploadResult(photoInfo: PhotoInfo) {
        photoInfo.apply {
            if (success) {
                "上传成功[imageType=$imageType]：photo=$this".e()
                when (imageType) {
                    CommConstant.IMAGE_UPLOAD_USER_UPLOAD -> {
                        fileID?.apply {
                            mViewModel?.updateAvatar(this)
                        }
                    }
                    CommConstant.IMAGE_UPLOAD_COMMON -> {
                        // TODO
                        callback?.invoke(this)
                        dismissAllowingStateLoss()
                    }
                    CommConstant.IMAGE_UPLOAD_USER_CENTER_BG -> {
                        fileID?.apply {
                            mViewModel?.updateUserCenterBg(this)
                        }
                    }
                    else -> {
                        callback?.invoke(this)
                        dismissAllowingStateLoss()
                    }
                }
            } else {
                showToast("图片上传失败")
            }
        }
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD)
            setState(State.REVERSE)
//            setTitle(
//                    title = getString(R.string.camera_roll),
//                    gravity = Gravity.CENTER
//            )
            addItem(
                drawableRes = R.drawable.ic_title_bar_close,
                reverseDrawableRes = R.drawable.ic_title_bar_close
            ) {
                dismissAllowingStateLoss()
            }
            addItem(
                title = "上传",
                isReversed = true
            ) {
                val path = cropView?.bitmap?.saveShareImage(photo?.imageFormat, true)?.apply {
                    photo?.uploadPath = this
                }
                "裁剪图片路径path = $path".w()

                when (imageType) {
                    CommConstant.IMAGE_UPLOAD_USER_UPLOAD -> {
                        photo?.apply {
                            mViewModel?.uploadImage(this, CommConstant.IMAGE_UPLOAD_USER_UPLOAD)
                        }
                    }
                    CommConstant.IMAGE_UPLOAD_COMMON -> {
                        photo?.apply {
                            mViewModel?.uploadImage(this, CommConstant.IMAGE_UPLOAD_COMMON)
                        }
                    }
                    CommConstant.IMAGE_UPLOAD_USER_CENTER_BG -> {
                        photo?.apply {
                            mViewModel?.uploadImage(this, CommConstant.IMAGE_UPLOAD_COMMON)
                        }
                    }
                    else -> {
                        photo?.apply {
                            callback?.invoke(this)
                            dismissAllowingStateLoss()
                        }
                    }
                }
                //                if (needUpload) {
                //                    photo?.apply {
                //                        mViewModel?.uploadImage(this, CommConstant.IMAGE_UPLOAD_USER_UPLOAD)
                //                    }
                //                } else {
                //                    photo?.apply {
                //                        callback?.invoke(this)
                //                    }
                //                }
            }
        }
    }

    private fun initContentView() {

    }

    private fun fillData() {
        cropView?.data = photo
    }
}
