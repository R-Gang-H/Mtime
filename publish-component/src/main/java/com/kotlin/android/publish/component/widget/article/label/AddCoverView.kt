package com.kotlin.android.publish.component.widget.article.label

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ViewEdtiorAddCoverBinding

/**
 * 添加封面
 *
 * Created on 2022/4/20.
 *
 * @author o.s
 */
class AddCoverView : LinearLayout {
    enum class Action {
        ADD,
        DElELT
    }
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { initView() }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) { initView() }

    private var mBinding: ViewEdtiorAddCoverBinding? = null

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            value?.apply {
                photo = covers?.firstOrNull()?.let {
                    PhotoInfo(
                        fileID = it.imageId,
                        url = it.imageUrl,
                        imageFormat = it.imageFormat ?: "jpg",
                    )
                }
            }
        }

    var action: ((Action) -> Unit)? = null

    val image: Image?
        get() {
            return photo?.run {
                Image(
                    imageId = fileID,
                    imageUrl = url,
                    imageFormat = imageFormat,
                    isGif = false
                )
            }
        }

    var photo: PhotoInfo? = null
        set(value) {
            field = value
            showDelView(isShow = value != null)
            value?.apply {
                mBinding?.coverView?.loadImage(data = uri ?: url, width = 180.dp, height = 100.dp)
            }
        }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = ViewEdtiorAddCoverBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        mBinding?.apply {
            delCoverView.apply {
                setBackground(
                    colorRes = R.color.color_80000000,
                    cornerRadius = 8.dpF,
                    direction = Direction.LEFT_BOTTOM or Direction.RIGHT_TOP
                )
                isVisible = false
                setOnClickListener {
//                    action?.invoke(Action.DElELT)
                    photo = null
                    mBinding?.coverView?.setImageDrawable(null)
                }
            }

            coverView.apply {
                setBackground(
                    strokeColorRes = R.color.color_e1e3ea,
                    strokeWidth = 1.dp,
                    dashWidth = 4.dpF,
                    dashGap = 4.dpF,
                    cornerRadius = 8.dpF,
                )
                setOnClickListener {
                    action?.invoke(Action.ADD)
                }
            }

        }
    }

    private fun showDelView(isShow: Boolean) {
        mBinding?.delCoverView?.isVisible = isShow
    }
}