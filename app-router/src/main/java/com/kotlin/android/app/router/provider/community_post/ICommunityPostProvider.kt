package com.kotlin.android.app.router.provider.community_post

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/14
 * description: 帖子详情
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY_POST)
interface ICommunityPostProvider:IBaseProvider {
    /**
     * 跳转到帖子详情 普通帖子和pk帖子
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章
     * @param recId 记录id，没有传0
     * @param needScrollToComment 是否自动滑动到帖子评论处
     *
     */
    fun startPostDetail(contentId: Long, type: Long, recId:Long,needScrollToComment:Boolean)
}