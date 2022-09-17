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
import com.kotlin.android.publish.component.databinding.ViewEdtiorAddImagesBinding

/**
 * 添加图集 / 发日志 footerView
 *
 * Created on 2022/4/20.
 *
 * @author o.s
 */
class AddImagesView : LinearLayout {
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

    private var mBinding: ViewEdtiorAddImagesBinding? = null

    var action: (() -> Unit)? = null

    /**
     * 编辑模式：回显内容
     */
    var content: CommunityContent? = null
        set(value) {
            field = value
            value?.apply {
                photos.clear()
                images?.forEach {
                    photos.add(
                        PhotoInfo(
                            fileID = it.imageId,
                            url = it.imageUrl,
                            imageFormat = it.imageFormat ?: "jpg",
                        )
                    )
                }
                display()
            }
        }

    val images: List<Image>
        get() = photos.map {
            Image(
                imageId = it.fileID,
                imageUrl = it.url,
                imageFormat = it.imageFormat,
                isGif = false
            )
        }

    val photos by lazy {
        ArrayList<PhotoInfo>()
    }

    fun setPhotos(photos: List<PhotoInfo>) {
        this.photos.clear()
        this.photos.addAll(photos)
        display()
    }

    private fun initView() {
        orientation = VERTICAL
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        mBinding = ViewEdtiorAddImagesBinding.inflate(LayoutInflater.from(context))
        addView(mBinding?.root)
        mBinding?.apply {
            tipsView.apply {
                setBackground(
                    colorRes = R.color.color_80000000,
                    cornerRadius = 8.dpF,
                    direction = Direction.LEFT_TOP or Direction.RIGHT_BOTTOM
                )
                isVisible = false
            }

            imageView.apply {
                setBackground(
                    strokeColorRes = R.color.color_e1e3ea,
                    strokeWidth = 1.dp,
                    dashWidth = 4.dpF,
                    dashGap = 4.dpF,
                    cornerRadius = 8.dpF,
                )
                setOnClickListener {
                    action?.invoke()
                }
            }

            imageView2.isVisible = false
            imageView3.isVisible = false
        }
    }

    private fun display() {
        mBinding?.apply {
            val size = photos.size
            if (size <= 0) {
                tipsView.isVisible = false
                imageView.setImageDrawable(null)
            } else {
                tipsView.isVisible = true
                tipsView.text = "共${size}张"
            }
            if (size > 0) {
                imageView.isVisible = true
                photos[0].apply {
                    imageView.loadImage(data = uri ?: url, width = 100.dp, height = 100.dp)
                }
            }
            if (size > 1) {
                imageView2.isVisible = true
                photos[1].apply {
                    imageView2.loadImage(data = uri ?: url, width = 100.dp, height = 100.dp)
                }
            } else {
                imageView2.isVisible = false
            }
            if (size > 2) {
                imageView3.isVisible = true
                photos[2].apply {
                    imageView3.loadImage(data = uri ?: url, width = 100.dp, height = 100.dp)
                }
            } else {
                imageView3.isVisible = false
            }
        }
    }
}