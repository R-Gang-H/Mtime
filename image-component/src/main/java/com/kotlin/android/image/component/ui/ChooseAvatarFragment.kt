package com.kotlin.android.image.component.ui

import android.content.Intent
import android.provider.MediaStore
import androidx.fragment.app.viewModels
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.camera.REQUEST_OPEN_PHOTO
import com.kotlin.android.image.component.camera.onActivityResultCameraData
import com.kotlin.android.image.component.camera.openCameraWithPermissions
import com.kotlin.android.image.component.databinding.LayoutChooseAvatarBinding
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.immersive.immersive
import kotlinx.android.synthetic.main.layout_choose_avatar.*

/**
 * 选择头像：（换头像）
 *
 * Created on 2020/12/24.
 *
 * @author o.s
 */
class ChooseAvatarFragment : BaseVMDialogFragment<ImageViewModel, LayoutChooseAvatarBinding>() {

    var photo: ((photo: PhotoInfo) -> Unit)? = null

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        }
        window = {
            setWindowAnimations(R.style.BottomDialogAnimation)
        }
        immersive = {
            immersive()
                    .transparentStatusBar()
                    .statusBarDarkFont(false)
        }
    }

    override fun initVM(): ImageViewModel = viewModels<ImageViewModel>().value

    override fun initView() {
        cameraView?.apply {
            setBackground(
                    colorRes = R.color.color_ffffff,
                    cornerRadius = 12.dpF,
                    direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            setOnClickListener {
                startCamera()
            }
        }
        photoView?.apply {
//            setBackground(
//                    colorRes = R.color.color_ffffff,
//                    cornerRadius = 6.dpF,
//                    direction = Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
//            )
            setOnClickListener {
                startPhoto()
            }
        }
        cancelView?.apply {
//            setBackground(
//                    colorRes = R.color.color_ffffff,
//                    cornerRadius = 6.dpF
//            )
            setOnClickListener {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    private fun startCamera() {
        activity?.openCameraWithPermissions()
    }

    private fun startPhoto() {
        val intent = Intent(Intent.ACTION_GET_CONTENT, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivityForResult(intent, REQUEST_OPEN_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultCameraData(requestCode, resultCode, data) { path, uri ->
            try {
                uri.lastPathSegment?.apply {
                    val id = if (this.contains(":")) {
                        substring(indexOf(":") + 1).toLong()
                    } else {
                        toLong()
                    }
                    val photoInfo = PhotoInfo(id = id, uri = uri, path = path, uploadPath = path)
                    "添加照片信息 $photoInfo".i()
                    cameraView?.post {
                        photo?.invoke(photoInfo)
                        dismissAllowingStateLoss()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}