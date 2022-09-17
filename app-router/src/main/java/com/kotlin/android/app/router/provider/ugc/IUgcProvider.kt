package com.kotlin.android.app.router.provider.ugc

import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * create by lushan on 2020/8/10
 * description: ugc详情provider
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_UGC_DETAIL)
interface IUgcProvider : IBaseProvider {
    /**
     * 跳转帖子详情页面
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章
     * @param recId 记录id 获取副本传递，如果没有传0即可
     * @param needToComment 数据加载完成是否要滑动到评论列表
     */
    fun startUgcDetail(
        contentId: Long,
        ugcType: Long,
        recId: Long = 0L,
        needToComment: Boolean = false
    )

    /**
     * 跳转到相册详情
     * @param albumId 相册id
     */
    fun startAlbumDetail(albumId: Long)

    /**
     * 跳转到对应详情页面
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章 5.视频 6.音频
     * @param recId 记录id 获取副本传递，如果没有传0即可
     * @param needToComment 数据加载完成是否要滑动到评论列表
     */
    fun launchDetail(@ContentType contentId: Long, type: Long, recId: Long = 0L, needToComment: Boolean = false)

    /**
     * 跳转到ugc视频详情或音乐详情
     * @param contentId 如果是发布过的传内容id
     * @param recId     记录id，草稿中有记录id
     * @param type      5视频 6音频
     * @param needToComment 数据加载完成是否要滑动到评论列表
     */
    fun startUgcMediaDetail(
        contentId: Long,
        ugcType: Long,
        recId: Long = 0L,
        needToComment: Boolean = false
    )
}