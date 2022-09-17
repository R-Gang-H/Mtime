package com.kotlin.android.app.router.provider.comment

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/20
 * description:
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_COMMENT)
interface ICommentProvider : IBaseProvider {
    fun startComment(commentId:Long,objType:Long,isPositiveBoolean: Boolean = false,isUserBlack:Boolean = false, familyStatus:Long = 0L,userGroupRole:Long = 0L,
                     commentPmsn:Long? = 1L,familyPostStatus:Long = 0L,groupId:Long = 0L
                     )


}