package com.kotlin.android.article.component.item.bean

import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ugc.detail.component.bean.*
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/8/19
 * description:文章详情，包括标题/内容，
 */
data class ArticleDetailViewBean(var titleData: UgcTitleViewBean? = null,
                                 var webContentData: UgcWebViewBean? = null,
                                 var movieData: MovieViewBean? = null,
                                 var bannerData:MutableList<UgcImageViewBean> = mutableListOf(),
                                 var commonBar: UgcCommonBarBean? = null,
                                 var copyRight:String = "",
                                 var ugcLinkBinderList:MutableList<MultiTypeBinder<*>> = mutableListOf()//关联内容binderList
) : ProguardRule