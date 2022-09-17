package com.kotlin.android.mine.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.getDrawable
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.invisible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.toDP
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean.Companion.TYPE_MOVIE_PERSON
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean.Companion.TYPE_ORGANIZATION
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean.Companion.TYPE_REVIEW_PERSON
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import kotlinx.android.synthetic.main.view_authen_image.view.*

/**
 * create by lushan on 2020/9/8
 * description: 身份认证拍照上传功能
 * 影评人：手持身份证照、示例
 * 电影人：手持身份证照、示例、名片或工作证
 * 机构认证：营业执照、认证公函、 认证公函下载
 */
class AuthenTakePhotoView @JvmOverloads constructor(var ctx: Context, var attres: AttributeSet? = null, var defaultStyle: Int = -1) : LinearLayoutCompat(ctx, attres, defaultStyle) {
    private var authenType = 0L//认证类型

    init {
        orientation = VERTICAL
        initView()
    }

    private var idCardPhotoInfo: PhotoInfo? = null//身份证照
    private var officalPhotoInfo: PhotoInfo? = null//公函照

    private var listener: ((AuthenPhotoType) -> Unit)? = null

    private fun initView() {
        removeAllViews()
        val rootView = View.inflate(ctx, R.layout.view_authen_image, null)
        addView(rootView)


        takePhotoCV?.onClick {//上传身份证
            listener?.invoke(AuthenPhotoType.ID_CARD_PHOTO)
        }

        visitingCardTakePhotoCV?.onClick {//上传认证公函、名片或工作证
            listener?.invoke(AuthenPhotoType.OFFICAL_LETTER_PHOTO)
        }

        officalLetterDownBtn?.onClick {//下载认证公函
            listener?.invoke(AuthenPhotoType.OFFICAL_LETTER_DOWN)
        }
    }




    /**
     * 设置身份证照片
     */
    fun setIdCardImageView(bean: PhotoInfo) {
        this.idCardPhotoInfo = bean
        takePhotoIv?.loadImage(
            data = bean.path,
            width = takePhotoIv.width,
            height = takePhotoIv.height,
            useProxy = false
        )
    }

    /**
     * 设置认真公函、名片或工作证
     */
    fun setVisitingCardImageView(bean: PhotoInfo) {
        this.officalPhotoInfo = bean
        visitingCardTakePhotoIv?.loadImage(
            data = bean.path,
            width = visitingCardTakePhotoIv.width,
            height = visitingCardTakePhotoIv.height,
            useProxy = false
        )
    }


    /**
     * 获取身份证照
     */
    fun getIdCardPhoto(): PhotoInfo? = idCardPhotoInfo

    /**
     * 获取公函照片
     */
    fun getOfficalPhoto(): PhotoInfo? = officalPhotoInfo

    /**
     * 设置认证类型
     */
    fun setType(type: Long,listener:((AuthenTakePhotoView.AuthenPhotoType)->Unit)? = null) {
        this.listener = listener
        this.authenType = type
        handleView(authenType)
    }

    private fun handleView(authenType: Long) {
        when (authenType) {
            TYPE_REVIEW_PERSON -> {//影评人
                visitingCardTv?.gone()
                visitingCardTakePhotoCV?.gone()
                officalLetterDownBtn?.gone()
                visitingCardTipsTv?.gone()
            }
            TYPE_MOVIE_PERSON -> {//电影人
                officalLetterDownBtn?.gone()
            }

            TYPE_ORGANIZATION -> {//机构认证
                simpleCV?.invisible()
                simpleTV?.invisible()
                takePhotoTv?.gone()
                ShapeExt.setShapeCorner2Color2Stroke(officalLetterDownBtn,corner = 12,strokeWidth = 1.dp,strokeColor = R.color.color_20a0da)
                getDrawable(R.mipmap.ic_download)?.apply {
                    setBounds(0,0,intrinsicWidth/2,intrinsicHeight/2)
                }.also {
                    officalLetterDownTv?.setCompoundDrawables(it,null,null,null)
                }
                idCardTitleTv?.text = getString(R.string.mine_authen_business_license)
                visitingCardTv?.text = getString(R.string.mine_authen_offical_letter)

            }
        }
    }

    enum class AuthenPhotoType {
        ID_CARD_PHOTO,//身份证
        OFFICAL_LETTER_PHOTO,//认证公函
        OFFICAL_LETTER_DOWN //认证公函下载

    }

}