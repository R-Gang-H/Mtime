package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.comment.component.bean.UgcCommonBarBean

/**
 * create by lushan
 * des: ugc 日志详情 普通帖子
 *
 **/

data class UgcDetailViewBean(var titleData: UgcTitleViewBean? = null,//标题
                             var bannerData: MutableList<UgcImageViewBean>? = null,//图集
                             var h5Data: UgcWebViewBean? = null,//日志详情
                             var movieViewBean: MovieViewBean? = null,//影片相关
                             var commonBar: UgcCommonBarBean? = null,//底部相关
                             var groupViewBean:TopicFamilyViewBean? = null//群组相关
)