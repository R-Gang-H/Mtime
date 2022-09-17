package com.kotlin.android.ugc.detail.component.binderadapter

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.StateListDrawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.databinding.BindingAdapter
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOINING
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_BLACK_NAME
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_SUCCESS
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_UNDO
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_ARTICLE
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_PIC
import com.kotlin.android.ugc.detail.component.ui.UGC_TYPE_REVIEW
import com.kotlin.android.ugc.detail.component.ui.widget.UgcBannerView
import com.kotlin.android.ugc.detail.component.ui.widget.UgcTitleView
import com.kotlin.android.ugc.web.widgets.UgcWebView


/**
 * Created by lushan on 2020/8/5
 */

/**
 * 设置影片背景色
 */
@BindingAdapter("movieBackground")
fun setBackgroundByType(view: View, type: Int) {
    val colorRes = when (type) {
        UGC_TYPE_REVIEW -> R.color.color_20a0da
        UGC_TYPE_PIC, UGC_TYPE_ARTICLE -> R.color.color_ffffff
        else -> R.color.color_20a0da
    }
    ShapeExt.setShapeColorAndCorner(view, colorRes, 4)
}

/**
 * UGC影片字段名称颜色
 */
@BindingAdapter("ugcMovieInfoTextColor")
fun setTextViewColor(tv: TextView, type: Int) {
    val textColorRes = when (type) {
        UGC_TYPE_REVIEW -> R.color.color_ffffff
        UGC_TYPE_PIC, UGC_TYPE_ARTICLE -> R.color.color_8798af
        else -> R.color.color_ffffff
    }

    tv.setTextColor(getColor(textColorRes))

}

@BindingAdapter("ugcMovieScoreTextColor")
fun setScoreTextViewColor(tv: TextView, type: Int) {
    val textColorRes = when (type) {
        UGC_TYPE_REVIEW -> R.color.color_ffffff
        UGC_TYPE_PIC, UGC_TYPE_ARTICLE -> R.color.color_20a0da
        else -> R.color.color_ffffff
    }

    tv.setTextColor(getColor(textColorRes))
}

@BindingAdapter("ugcType", "ugcMovieType", requireAll = true)
fun setMovieBtnBg(view: View, type: Int, movieStatus: Long) {
    view.background = getMovieBtnStateListDrawable(type, movieStatus)
}


private fun getMovieBtnStateListDrawable(ugcType: Int, movieStatus: Long): StateListDrawable {
    val normalColor = getNormalColor(movieStatus, ugcType)
    val pressedColor = getPressedColor(movieStatus, ugcType)
    val normal = getGradientDrawable(color = getColor(normalColor), cornerRadius = 12.dpF)
    val press = getGradientDrawable(color = getColor(pressedColor), cornerRadius = 12.dpF)
    return getDrawableStateList(normal = normal, pressed = press)
}

@ColorRes
private fun getPressedColor(movieStatus: Long, ugcType: Int): Int {
    return when (movieStatus) {
        MOVIE_STATE_SALE, MOVIE_STATE_LIKE -> {//购票和想看
            if (ugcType == UGC_TYPE_REVIEW) {
                R.color.color_4d20a0da
            } else {
                R.color.color_9920a0da
            }
        }
        MOVIE_STATE_PRESCALE -> {//预售
            if (ugcType == UGC_TYPE_REVIEW) {
                R.color.color_4dafd956
            } else {
                R.color.color_99afd956
            }
        }
        MOVIE_STATE_HSA_LIKE -> {//已想看
            R.color.color_ffffff
        }
        else -> {
            if (ugcType == UGC_TYPE_REVIEW) {
                R.color.color_4d20a0da
            } else {
                R.color.color_9920a0da
            }
        }
    }
}

@ColorRes
private fun getNormalColor(movieStatus: Long, ugcType: Int): Int {
    return when (movieStatus) {
        MOVIE_STATE_SALE, MOVIE_STATE_LIKE -> {//购票和想看
            if (ugcType == UGC_TYPE_REVIEW) {
                R.color.color_ffffff
            } else {
                R.color.color_20a0da
            }
        }
        MOVIE_STATE_PRESCALE -> {//预售
            R.color.color_afd956
        }
        MOVIE_STATE_HSA_LIKE -> {//已想看
            R.color.color_ffffff
        }
        else -> {
            if (ugcType == UGC_TYPE_REVIEW) {
                R.color.color_ffffff
            } else {
                R.color.color_20a0da
            }
        }
    }
}


const val MOVIE_STATE_SALE = 2L//购票
const val MOVIE_STATE_PRESCALE = 1L//预售
const val MOVIE_STATE_LIKE = 3L//想看
const val MOVIE_STATE_HSA_LIKE = 4L//已想看


@BindingAdapter("movieBtnText")
fun bindMovieBtnText(view: TextView, movieStatus: Long) {
    view.text = when (movieStatus) {
        MOVIE_STATE_SALE -> getString(R.string.ugc_movie_buy_ticket)
        MOVIE_STATE_PRESCALE -> getString(R.string.ugc_movie_presell)
        MOVIE_STATE_LIKE, MOVIE_STATE_HSA_LIKE -> getString(R.string.ugc_movie_wanna)
        else -> ""
    }
}

/**
 * 影片想看按钮文本
 */
@BindingAdapter(value = ["movieBtnDrawable", "moviehasWanna"], requireAll = true)
fun bindMovieBtnDrawable(view: TextView, ugcType: Int, movieStatus: Long) {
    if (ugcType == UGC_TYPE_REVIEW) {
        if (movieStatus != MOVIE_STATE_HSA_LIKE) {
            view.setCompoundDrawablesAndPadding()
        } else {
            val drawable = getDrawable(R.drawable.ic_checkb)
            drawable?.setTint(getColor(R.color.color_20a0da))
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            view.setCompoundDrawables(drawable, null, null, null)
        }
        view.setTextColor(getColor(if (movieStatus != MOVIE_STATE_PRESCALE) R.color.color_20a0da else R.color.color_ffffff))
    } else {
        if (movieStatus != MOVIE_STATE_HSA_LIKE) {
            view.setCompoundDrawablesAndPadding()
        } else {
            val drawable = getDrawable(R.drawable.ic_checkb)
            drawable?.setTint(getColor(R.color.color_ffffff))
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            view.setCompoundDrawables(drawable, null, null, null)
        }
        view.setTextColor(getColor(R.color.color_ffffff))
    }

}


/**
 * ugc详情 加载html
 */
@BindingAdapter("ugcWebViewData")
fun loadUgcWebData(webView: UgcWebView, bean: UgcWebViewBean) {
    webView.setData(bean.content, bean.webType)
}

/**
 * ugc详情推荐头相册设置数据
 */
@BindingAdapter("ugcBannerData", "ugcTitle", requireAll = true)
fun setUgcBannerData(
    bannerView: UgcBannerView,
    list: MutableList<UgcImageViewBean>,
    title: String
) {
    bannerView.setData(title, list)
}


/**
 * ugc详情标题
 */
@BindingAdapter("ugcTitleBarData")
fun setUgcTitleBar(titleView: UgcTitleView, bean: UgcTitleBarBean?) {
    bean?.apply {
        titleView.setData(this)
    }
}

/**
 * 设置家族成员数
 */
@BindingAdapter("familyCount")
fun setFamilyCount(tv: TextView, familyCount: String?) {
    familyCount ?: return
    if (familyCount.contains("个成员")) {
        val spannableString = SpannableString(familyCount)
        val lastIndexOf = familyCount.lastIndexOf("个成员")
        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
            0,
            lastIndexOf,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
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
                normal = getGradientDrawable(
                    color = getColor(R.color.color_20a0da),
                    cornerRadius = 25.dpF
                ),
                pressed = getGradientDrawable(
                    color = getColor(R.color.color_9920a0da),
                    cornerRadius = 25.dpF
                )
            )
        }
        GROUP_JOIN_SUCCESS, GROUP_JOINING -> {//加入
            ShapeExt.setShapeCorner2Color2Stroke(
                view,
                corner = 25.dp,
                strokeColor = R.color.color_20a0da
            )
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


@BindingAdapter("link_movie_status")
fun setLinkMovieBtnStatus(view: View, status: Long) {
    view.background = getLinkMovieBtnStateListDrawable(status)
}


@BindingAdapter("link_movie_drawable")
fun setLinkMovieDrawable(view: TextView, movieStatus: Long) {
    if (movieStatus != MOVIE_STATE_HSA_LIKE) {
        view.setCompoundDrawablesAndPadding()
    } else {
        val drawable = getDrawable(R.drawable.ic_checkb)
        drawable?.setTint(getColor(R.color.color_feb12a))
        drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        view.setCompoundDrawables(drawable, null, null, null)
    }
    view.setTextColor(getColor(if (movieStatus == MOVIE_STATE_HSA_LIKE) R.color.color_feb12a else R.color.color_ffffff))

}

private fun getLinkMovieBtnStateListDrawable(movieStatus: Long): StateListDrawable {
    val normalColor = getLinkNormalColor(movieStatus)
    val pressedColor = getLinkPressedColor(movieStatus)
    val normal = getGradientDrawable(
        color = getColor(normalColor),
        cornerRadius = 12.dpF,
        strokeColor = if (movieStatus == MOVIE_STATE_HSA_LIKE) getColor(
            R.color.color_feb12a
        ) else Color.TRANSPARENT,
        strokeWidth = 1.dp
    )
    val press = getGradientDrawable(
        color = getColor(pressedColor),
        cornerRadius = 12.dpF,
        strokeColor = if (movieStatus == MOVIE_STATE_HSA_LIKE) getColor(
            R.color.color_feb12a
        ) else Color.TRANSPARENT,
        strokeWidth = 1.dp
    )
    return getDrawableStateList(normal = normal, pressed = press)
}

@ColorRes
private fun getLinkNormalColor(movieStatus: Long): Int {
    return when (movieStatus) {
        MOVIE_STATE_SALE -> {//购票和想看
            R.color.color_20a0da
        }
        MOVIE_STATE_LIKE -> {//想看
            R.color.color_feb12a
        }
        MOVIE_STATE_PRESCALE -> {//预售
            R.color.color_afd956
        }
        MOVIE_STATE_HSA_LIKE -> {//已想看
            R.color.color_ffffff
        }
        else -> {
            R.color.color_20a0da
        }
    }
}

@ColorRes
private fun getLinkPressedColor(movieStatus: Long): Int {
    return when (movieStatus) {
        MOVIE_STATE_SALE -> {//购票和想看
            R.color.color_9920a0da
        }
        MOVIE_STATE_LIKE -> {//想看
            R.color.color_99feb12a
        }
        MOVIE_STATE_PRESCALE -> {//预售
            R.color.color_99afd956
        }
        MOVIE_STATE_HSA_LIKE -> {//已想看
            R.color.color_ffffff
        }
        else -> {
            R.color.color_9920a0da
        }
    }
}

