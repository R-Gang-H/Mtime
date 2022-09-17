package com.kotlin.android.comment.component.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.kotlin.android.comment.component.R
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.photo.setThumbnail
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import java.io.File

/**
 * create by lushan on 2020/9/27
 * description: 评论、回复评论上传图片空间
 */
class CommentImageLayout @JvmOverloads constructor(var ctx: Context, var attributeSet: AttributeSet? = null, var defaultStyle: Int = -1) : FrameLayout(ctx, attributeSet, defaultStyle) {

    private var mCommentIv: AppCompatImageView? = null//评论图片
    private var closeIv: AppCompatImageView? = null//关闭按钮
    private var imagePath:String? = ""//当前图片地址
    init {
        removeAllViews()
//        图片海报
        mCommentIv = AppCompatImageView(ctx)
        MarginLayoutParams(MarginLayoutParams.MATCH_PARENT, MarginLayoutParams.MATCH_PARENT).apply {
            leftMargin = 5.dp
            topMargin = 10.dp
            rightMargin = 10.dp
            bottomMargin = 14.dp
        }.also {
            addView(mCommentIv, it)
        }

        closeIv = AppCompatImageView(ctx)
        FrameLayout.LayoutParams(20.dp, 20.dp).apply {
            gravity = Gravity.RIGHT or Gravity.TOP
        }.also {
            addView(closeIv, it)
        }

        closeIv?.apply {
            setImageResource(R.drawable.ic_feed_del)
            onClick {
                mCommentIv?.setImageBitmap(null)
                imagePath = ""
                this@CommentImageLayout.visibility = View.GONE
            }
        }

        background = CommentImageDrawable(getColor(R.color.color_ffffff))

    }


    /**
     * 设置加载图片
     */
    fun setPhotoInfo(photoInfo: PhotoInfo) {
        visibility = View.VISIBLE
        imagePath = photoInfo.path
        mCommentIv?.apply{
            tag = photoInfo
            clearAnimation()
            setImageDrawable(getGradientDrawable(color =  getColor(R.color.color_f2f3f6), cornerRadius = 4.dpF))
            setThumbnail(photoInfo,width = 109.dp,
                height = 70.dp, playAnim = true)
        }

    }

    /**
     * 获取当前上传图片本地地址
     */
    fun getImagePath():String = imagePath.orEmpty()

    fun clearImagePath(){
        imagePath = ""
    }

}