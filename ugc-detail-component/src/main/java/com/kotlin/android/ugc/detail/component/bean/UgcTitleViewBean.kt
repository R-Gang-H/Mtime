package com.kotlin.android.ugc.detail.component.bean

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.views.CommunityQuoteSpan
import com.kotlin.android.ugc.detail.component.R


/**
 * Created by lushan on 2020/8/5
 * UGC及文章详情标题
 */
data class UgcTitleViewBean(var title: String = "",//文章或日志标题
                            var isTop: Boolean = false,//是否是置顶
                            var isEssence: Boolean = false//是否是加精

) : ProguardRule {
    companion object {
        fun getTitle(titleBean: UgcTitleViewBean, context: Context): SpannableString {
            val ssb = SpannableString(titleBean.title)
            if (titleBean.isTop) {
                ssb.setSpan(CommunityQuoteSpan(
                        context,
                        R.mipmap.icon_community_post_ding, 5.dp),
                        0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            if (titleBean.isEssence) {
                ssb.setSpan(CommunityQuoteSpan(
                        context,
                        R.mipmap.icon_community_post_jing, 5.dp),
                        0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            return ssb
        }
    }
}