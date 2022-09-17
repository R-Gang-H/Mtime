package com.kotlin.android.comment.component.provider

import android.content.Context
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.comment.component.ui.detail.CommentDetailActivity
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.comment.ICommentProvider

/**
 * create by lushan on 2020/8/20
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_COMMENT)
class CommentProvider : ICommentProvider {
    override fun startComment(commentId: Long, objType: Long, isPositiveBoolean: Boolean, isUserBlack: Boolean, familyStatus: Long, userGroupRole: Long, commentPmsn: Long?, familyPostStatus: Long, groupId: Long) {
        Bundle().apply {
            put(CommentDetailActivity.COMMENT_ID, commentId)
            put(CommentDetailActivity.COMMENT_OBJECT_TYPE, objType)
            put(CommentDetailActivity.COMMENT_IS_POSITIVE, isPositiveBoolean)
            put(CommentDetailActivity.COMMENT_IS_USER_BLACK,isUserBlack)
            put(CommentDetailActivity.COMMENT_FAMILYSTATUS,familyStatus)
            put(CommentDetailActivity.COMMENT_USERGROUPROLE,userGroupRole)
            put(CommentDetailActivity.COMMENT_PMSN,commentPmsn)
            put(CommentDetailActivity.COMMENT_FAMILYPOSTSTATUS,familyPostStatus)
            put(CommentDetailActivity.COMMENT_GROUP_ID,groupId)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Comment.PAGE_COMMENT_DETAIL_ACTIVITY, it)
        }
    }

    override fun init(context: Context?) {

    }
}