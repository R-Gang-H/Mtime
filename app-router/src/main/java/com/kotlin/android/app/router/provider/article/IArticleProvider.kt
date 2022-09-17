package com.kotlin.android.app.router.provider.article

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/20
 * description:
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_ARTICLE)
interface IArticleProvider : IBaseProvider {

    /**
     * 跳转到文章详情页面
     * @param isPublished 是否已经是发布的 true 其他用户的  false自己发布的还没有审核通过或还没有发布
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章
     *
     */
    fun startActicleActivity(contentId: Long, type: Long, recId:Long, needToComment: Boolean = false)
}