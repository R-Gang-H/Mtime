package com.kotlin.android.ugc.detail.component.binderadapter

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.community.R
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOINING
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_BLACK_NAME
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_SUCCESS
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_UNDO
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString


/**
 * Created by Wangwei on 2020/9/26
 */

/**
 * 设置家族成员数
 */
@BindingAdapter("familyCount")
fun setFamilyCount(tv: TextView, familyCount: String?) {
    familyCount ?: return
    if (familyCount.contains("个成员")) {
        val spannableString = SpannableString(familyCount)
        val lastIndexOf = familyCount.lastIndexOf("个成员")
        spannableString.setSpan(StyleSpan(Typeface.ITALIC), 0, lastIndexOf, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        tv.text = spannableString
    } else {
        tv.text = familyCount
    }
}

/**
 * 设置家族加入背景色
 */
@BindingAdapter("familyJoinBackground")
fun setFamilyStatus(view: View, status: Long) {

    when (status) {
        GROUP_JOIN_UNDO, GROUP_JOIN_BLACK_NAME -> {//未加入
            view.background = getDrawableStateList(
                normal = getGradientDrawable(color = getColor(R.color.color_20a0da), cornerRadius = 25.dpF),
                pressed = getGradientDrawable(color = getColor(R.color.color_9920a0da), cornerRadius = 25.dpF)
            )
        }
        GROUP_JOIN_SUCCESS, GROUP_JOINING -> {//加入
            ShapeExt.setShapeCorner2Color2Stroke(view, corner = 25.dp, strokeColor = R.color.color_20a0da)
        }
    }

}

/**
 * 设置加入文案左drawable
 * @param status 当前用户是否加入此群组 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
 */
@BindingAdapter("familyJoinDrawable")
fun setFamilyJoinDrawable(view: TextView, status: Long) {
    if (status == CommunityContent.GROUP_JOIN_SUCCESS) {//已加入
        val drawable = getDrawable(R.drawable.ic_checka)
        drawable?.setTint(getColor(R.color.color_20a0da))
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        view.setCompoundDrawables(drawable, null, null, null)
    } else {
        view.setCompoundDrawables(null, null, null, null)
    }
}

@BindingAdapter("attendBg")
fun setAttendBg(view: TextView, followed: Boolean) {
    if (followed) {
        view.setTextColor(getColor(R.color.color_8798af))
        view.setBackground(cornerRadius = 15f.dpF,colorRes = R.color.color_f2f3f6)
        view.text = getString(R.string.attended)
    } else {
        view.setTextColor(getColor(R.color.color_ffffff))
        view.setBackground(cornerRadius = 15f.dpF,colorRes = R.color.color_20a0da)
        view.text = getString(R.string.attend)

    }
}

@BindingAdapter("limit")
fun limit(view: TextView, count: Long) {
    view.text = formatCount(count)
}


//@BindingAdapter("communityPhotoBackground")
//fun setCommunityPhotoBackground(view: View) {
////    ShapeExt.setShapeCorner2Color(view, R.color.color_ffffff, 5)
//    ShapeExt.setShapeCorner2Color2Stroke(view,corner = 2,strokeColor = R.color.color_ebedf2,strokeWidth = 1)
//}