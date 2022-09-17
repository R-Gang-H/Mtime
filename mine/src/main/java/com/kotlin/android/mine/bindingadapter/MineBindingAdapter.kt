package com.kotlin.android.mine.bindingadapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mine.*
import com.kotlin.android.mine.bean.AuthenPrivilegeViewBean
import com.kotlin.android.mine.bean.AuthenticatonCardViewBean
import com.kotlin.android.mine.bean.MineUserViewBean
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor

/**
 * create by lushan on 2020/8/27
 * description:
 */

@BindingAdapter(value = ["isLogin", "content", "defaultRes"], requireAll = true)
fun setMineTextWithLogin(textView: TextView, isLogin: Boolean, content: String?, defaultRes: String?) {
    textView.text = if (isLogin) {
        content.orEmpty()
    } else {
        defaultRes.orEmpty()
    }
}

@BindingAdapter("userLevelImage")
fun setUserLevelImage(imageView: ImageView,level:Long){
    imageView.setImageResource(getUserLevelIcon(level))
}

/**
 * 获取会员等级对应图片
 */
private fun getUserLevelIcon(level:Long): Int {
    return when (level) {
        USER_LEVEL_ZHONGJI -> {//中级
            R.mipmap.ic_user_vip_zhongji
        }
        USER_LEVEL_GAOJI -> {//高级
            R.mipmap.ic_user_vip_gaoji
        }
        USER_LEVEL_ZISHEN -> {//资深
            R.mipmap.ic_user_vip_zishen
        }
        USER_LEVEL_DIANTANG -> {//殿堂
            R.mipmap.ic_user_vip_diantang
        }
        else -> {//入门
            R.mipmap.ic_user_vip_rumen
        }
    }
}

@BindingAdapter("userImage")
fun setUserImage(imageView: ImageView, user: MineUserViewBean?) {
    user?.apply {
        if (TextUtils.isEmpty(user.userHeadPic)) {
            imageView.setImageBitmap(null)
            imageView.setImageResource(R.drawable.default_user_head)
        }else{
            imageView.loadImage(
                data = user.userHeadPic,
                width = 60.dp,
                height = 60.dp,
                circleCrop = true,
                defaultImgRes = R.drawable.default_user_head
            )
        }
    }
}

/**
 * 设置身份认证卡片背景
 */
@BindingAdapter("authenBackground")
fun setAuthenBack(view: View, type: Long) {
    when (type) {
        AuthenticatonCardViewBean.TYPE_REVIEW_PERSON -> ShapeExt.setGradientColor(view, GradientDrawable.Orientation.BL_TR, R.color.color_19b3c2, R.color.color_30ece3, 5)
        AuthenticatonCardViewBean.TYPE_MOVIE_PERSON -> ShapeExt.setGradientColor(view, GradientDrawable.Orientation.BL_TR, R.color.color_20a0da, R.color.color_05d8ed, 5)
        AuthenticatonCardViewBean.TYPE_ORGANIZATION -> ShapeExt.setGradientColor(view, GradientDrawable.Orientation.BL_TR, R.color.color_feb12a, R.color.color_fff430, 5)
    }
}

@BindingAdapter("authenIcon")
fun setAuthenIcon(view: ImageView, type: Long) {

    val resId = when (type) {
        AuthenticatonCardViewBean.TYPE_REVIEW_PERSON -> R.drawable.ic_auth_yingren
        AuthenticatonCardViewBean.TYPE_MOVIE_PERSON -> R.drawable.ic_auth_yingren
        AuthenticatonCardViewBean.TYPE_ORGANIZATION -> R.drawable.ic_auth_jigouren
        else -> R.drawable.ic_auth_yingren
    }
    view.setImageResource(resId)
}

/**
 * 设置认证特权中icon
 */
@BindingAdapter("authenPrivilegeIcon")
fun setAuthenPrivilegeIcon(view: ImageView, type: Long){
    val resId = when (type) {
        AuthenPrivilegeViewBean.PRIVILEGE_TYPE_BIAOZHI -> R.drawable.ic_auth_privilege_biaozhi
        AuthenPrivilegeViewBean.PRIVILEGE_TYPE_RECOMMEND -> R.drawable.ic_auth_privilege_recommend
        AuthenPrivilegeViewBean.PRIVILEGE_TYPE_MORE -> R.drawable.ic_auth_privilege_more
        else -> R.drawable.ic_auth_privilege_biaozhi
    }
    view.setImageResource(resId)
}


/**
 * 获取认证提交按钮背景
 */
fun getSubmitDrawable(): Drawable {
    return getDrawableStateList(
        normal = getGradientDrawable(color = getColor(R.color.color_20a0da), cornerRadius = 20.dpF),
        pressed = getGradientDrawable(color = getColor(R.color.color_4d20a0da), cornerRadius = 20.dpF),
        disable = getGradientDrawable(color = getColor(R.color.color_e3e5ed), cornerRadius = 20.dpF)
    )
}


/**
 * 个人中心-我的服务设置上图标
 */
@BindingAdapter("mineServiceDrawableTop")
fun setDrawableTop(tv: TextView, drawable: Drawable?) {
    drawable?.apply {
        setBounds(0, 0, intrinsicWidth / 2, intrinsicHeight / 2)
    }?.also {
        tv.setCompoundDrawables(null, it, null, null)
    }
}

/**
 * 加载订单图片
 */
@BindingAdapter("orderImage")
fun loadOrderImage(imageView: ImageView,url:String){
    imageView.loadImage(
        data = url,
        width = 35.dp,
        height = 51.dp,
        roundedRadius = 2.dpF,
        defaultImgRes = R.drawable.ic_order,
    )
}


