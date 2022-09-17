package com.kotlin.android.community.post.component.item.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.comment.component.bean.UgcCommonBarBean

/**
 * create by lushan on 2020/9/21
 * description:
 */
class PkDetailViewBean(var isAlbumType: Boolean,
                       var createUser: UgcCommonBarBean.CreateUser,
                       var canComment: Boolean,//是否都可以评论
                       var commentSupport: UgcCommonBarBean.CommentSupport//点赞相关
) : ProguardRule