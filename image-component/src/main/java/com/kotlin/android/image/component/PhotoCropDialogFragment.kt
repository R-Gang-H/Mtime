package com.kotlin.android.image.component

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.databinding.DialogPhotoCropBinding
import com.kotlin.android.image.component.ui.adapter.PhotoCropStyleAdapter
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive

/**
 * 相册裁剪
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_photo_crop_dialog")
class PhotoCropDialogFragment : BaseVMDialogFragment<BaseViewModel, DialogPhotoCropBinding>() {

    private val mAdapter by lazy { PhotoCropStyleAdapter() }

    private var isReady = false

    var photo: PhotoInfo? = null
        set(value) {
            field = value
            fillData()
        }

    override fun initEnv() {
        dialogStyle = DialogStyle.FULL
        animation = R.style.RightDialogAnimation
        immersive = {
            immersive().fullscreen()
        }
    }

    override fun initView() {
        mBinding?.apply {
            cropRecyclerView.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                adapter = mAdapter
            }
            cancel.setOnClickListener {
                dismiss()
            }
            confirm.apply {
                setBackground(
                    colorRes = R.color.color_20a0da,
                    cornerRadius = 14.dpF
                )
            }
        }

        isReady = true
        fillData()
    }

    override fun initData() {

    }

    override fun startObserve() {
    }

    private fun fillData() {
        if (isReady) {
            photo?.apply {
                mBinding?.apply {
                    icon.loadImage(uri)
                }
            }
        }
    }

}