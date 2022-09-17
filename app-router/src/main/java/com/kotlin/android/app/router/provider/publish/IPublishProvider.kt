package com.kotlin.android.app.router.provider.publish

import android.app.Activity
import android.os.Parcelable
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.app.data.annotation.PublishType
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.annotation.RouteProvider
import com.kotlin.android.router.provider.IBaseProvider

/**
 * 发布组件：
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
@RouteProvider(path = RouterProviderPath.Provider.PROVIDER_PUBLISH)
interface IPublishProvider : IBaseProvider {

    /**
     * [type]: [ContentType] 内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"), VIDEO(5, "视频"), AUDIO(6, "音频");
     * [isFootmarks] 是否种草
     * [isLongComment] 是否长影评
     *
     * 需要编辑时：
     * [contentId] 文章内容ID
     * [recordId] 记录ID
     */
    fun startEditorActivity(
        @ContentType type: Long,
        contentId: Long? = null, // 文章内容ID
        recordId: Long? = null, // 记录ID
        movieId: Long? = null,
        movieName: String? = null,
        familyId: Long? = null,
        familyName: String? = null,
        isFootmarks: Boolean = false,
        isLongComment: Boolean = false,
    )

    /**
     * [type]: [PublishType]发布类型：内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评")
     */
    fun startPublishActivity(
            @PublishType type: Long,
            movieId: Long? = null,
            movieName: String? = null,
            familyId: Long? = null,
            familyName: String? = null,
    )

    /**
     * [isFootmarks] 是否种草家族
     */
    fun startFamilyListActivity(
        activity: Activity?,
        isFootmarks: Boolean = false
    )

    /**
     * 跳转到视频发布页
     * 如果已经保存了视频再进入编辑视频相应信息需传递内容id，如果是新发布，contentId传0即可，videoPath为选中的视频地址，新发布视频传递
     * @param contentId 发布视频内容id
     * @param recId 发布记录才有此id,新创建不用传
     * @param videoPath 本地选中视频地址
     */
    fun startVideoPublishActivity(
        contentId:Long = 0L,
        recId:Long = 0L,
        videoPath:String = ""
    )

    /**
     * 跳转到视频预览页面
     * @param videoPath 本地视频地址
     */
    fun startPreviewVideoActivity(activity:Activity? = null,videoBean:Parcelable,toVideoPublish:Boolean = true)
}