package com.kotlin.android.review.component.item.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ugc.detail.component.bean.MovieViewBean
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.ugc.detail.component.bean.UgcWebViewBean

/**
 * create by lushan on 2020/8/17
 * description:影评信息
 */
data class ReviewDetailViewBean(
        var title: String = "",//影评title
        var spoiler: String = "",//此影评包含剧透
        var movieViewBean: MovieViewBean? = null,//相关影片
        var ugcWebViewBean: UgcWebViewBean? = null,//文章详情
        var commonBar: UgcCommonBarBean? = null,
        var copyRight:String = ""//版权
) : ProguardRule