package com.kotlin.android.publish.component.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.publish.component.R

/**
 * create by lushan on 2022/4/12
 * des:上传视频状态view
 **/
class VideoUploadStateView @JvmOverloads constructor(
    var ctx: Context,
    var attributeSet: AttributeSet? = null,
    var defStyle: Int = 0
) : FrameLayout(ctx, attributeSet, defStyle) {
    private var uploadPb: CircleSeekBar? = null
    private var errorIv: ImageView? = null
    private var stateTipsTv: TextView? = null
    private var stateValue: Long = VIDEO_UPLOAD_STATE_INIT

    companion object {
        const val VIDEO_UPLOAD_STATE_INIT = 0L//上传初始化，完成后也赋值为0
        const val VIDEO_UPLOAD_STATE_UPLOADING = 1L//上传中
        const val VIDEO_UPLOAD_STATE_FAILED = 3L//上传失败
    }

    init {
        removeAllViews()
        val layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }

        addView(addLinearLayout(), layoutParams)
        setBackgroundColor(getColor(R.color.color_99000000))
        setState(stateValue)
    }

    fun getState(): Long = stateValue

    fun setState(state: Long) {
        stateValue = state
        when (state) {
            VIDEO_UPLOAD_STATE_INIT -> {
                gone()
                errorIv?.visible(false)
                stateTipsTv?.text = getString(R.string.publish_component_uploading)
            }
            VIDEO_UPLOAD_STATE_UPLOADING -> {
                visible()
                uploadPb?.visible(true)
                errorIv?.visible(false)
                stateTipsTv?.text = getString(R.string.publish_component_uploading)
            }
            VIDEO_UPLOAD_STATE_FAILED -> {
                visible()
                uploadPb?.visible(false)
                errorIv?.visible(true)
                stateTipsTv?.text = getString(R.string.publish_component_upload_failed)
            }
        }
    }

    fun setProgress(progress: Int) {
        uploadPb?.setProgress(progress)
    }

    private fun addLinearLayout(): LinearLayout {
        val linearlayout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
        }
        uploadPb = getUploadProgressBar()
        val pbLayoutParams = LinearLayout.LayoutParams(20.dp, 20.dp).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        linearlayout.addView(uploadPb, pbLayoutParams)

        val errorIvLayoutParams = LinearLayout.LayoutParams(20.dp, 20.dp).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        errorIv = getUploadFailedImageView()
        linearlayout.addView(errorIv, errorIvLayoutParams)

        val stateTipsLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            topMargin = 6.dp
        }
        stateTipsTv = getStateTipsTextView()
        linearlayout.addView(stateTipsTv, stateTipsLayoutParams)



        return linearlayout
    }

    private fun getUploadProgressBar(): CircleSeekBar {
        return CircleSeekBar(ctx).apply {
            setMaxProgress(100)
            setProgress(0)
        }
    }

    private fun getUploadFailedImageView(): ImageView {
        return ImageView(ctx).apply {
            setBackgroundResource(R.drawable.ic_upload_error)
            gone()
        }
    }

    private fun getStateTipsTextView(): TextView {
        return TextView(ctx).apply {
            setTextColor(getColor(R.color.white))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
        }
    }
}