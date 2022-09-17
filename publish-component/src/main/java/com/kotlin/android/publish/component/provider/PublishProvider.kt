package com.kotlin.android.publish.component.provider

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.ContentType
import com.kotlin.android.core.CoreApp
import com.kotlin.android.app.data.annotation.PublishType
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.publish.component.ui.selectedvideo.REQUEST_VIDEO
import com.kotlin.android.publish.component.ui.video.PreviewVideoActivity
import com.kotlin.android.publish.component.ui.video.PreviewVideoActivity.Companion.KEY_VIDEO_BEAN

/**
 *
 * Created on 2020/9/15.
 *
 * @author o.s
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_PUBLISH)
class PublishProvider : IPublishProvider {
    override fun startEditorActivity(
        @ContentType type: Long,
        contentId: Long?,
        recordId: Long?,
        movieId: Long?,
        movieName: String?,
        familyId: Long?,
        familyName: String?,
        isFootmarks: Boolean,
        isLongComment: Boolean,
    ) {
        Bundle().apply {
            put(Publish.KEY_TYPE, type)
            put(Publish.KEY_IS_FOOTMARKS, isFootmarks)
            put(Publish.KEY_IS_LONG_COMMENT, isLongComment)
            put(Publish.KEY_CONTENT_ID, contentId)
            put(Publish.KEY_RECORD_ID, recordId)
            put(Publish.KEY_MOVIE_ID, movieId)
            put(Publish.KEY_MOVIE_NAME, movieName)
            put(Publish.KEY_FAMILY_ID, familyId)
            put(Publish.KEY_FAMILY_NAME, familyName)
        }.also {
            RouterManager.instance.navigation(
                path = RouterActivityPath.Publish.PAGER_EDITOR_ACTIVITY,
                context = CoreApp.instance,
                bundle = it
            )
        }
    }

    /**
     * [type]: [PublishType]发布类型：内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评")
     */
    override fun startPublishActivity(
        @PublishType type: Long,
        movieId: Long?,
        movieName: String?,
        familyId: Long?,
        familyName: String?,
    ) {
        Bundle().apply {
            put(Publish.KEY_TYPE, type)
            put(Publish.KEY_MOVIE_ID, movieId)
            put(Publish.KEY_MOVIE_NAME, movieName)
            put(Publish.KEY_FAMILY_ID, familyId)
            put(Publish.KEY_FAMILY_NAME, familyName)
        }.also {
            RouterManager.instance.navigation(
                path = RouterActivityPath.Publish.PAGER_PUBLISH_ACTIVITY,
                context = CoreApp.instance,
                bundle = it
            )
        }
    }

    override fun startFamilyListActivity(
        activity: Activity?,
        isFootmarks: Boolean
    ) {
        activity?.apply {
            Bundle().apply {
                put(Publish.KEY_IS_FOOTMARKS, isFootmarks)
            }.also {
                RouterManager.instance.navigation(
                    path = RouterActivityPath.Publish.PAGER_FAMILY_LIST_ACTIVITY,
                    bundle = it,
                    context = this,
                    requestCode = Publish.FAMILY_LIST_REQUEST_CODE
                )
            }
        }
    }

    override fun startVideoPublishActivity(contentId: Long, recId:Long,videoPath: String) {
        Bundle().apply {
            put(Publish.KEY_CONTENT_ID, contentId)
            put(Publish.KEY_RECORD_ID,recId)
            put(Publish.KEY_VIDEO_PATH, videoPath)
        }.also {
            RouterManager.instance.navigation(
                RouterActivityPath.Publish.PAGER_VIDEO_PUBLISH_ACTIVITY,
                it
            )
        }
    }

    override fun startPreviewVideoActivity(activity:Activity?,videoBean: Parcelable,toVideoPublish:Boolean) {
        Bundle().apply {
            putParcelable(KEY_VIDEO_BEAN,videoBean)
            putBoolean(PreviewVideoActivity.KEY_TO_VIDEO_PUBLISH,toVideoPublish)
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Publish.PAGER_PREVIEW_VIDEO_ACTIVITY,it,activity, requestCode = REQUEST_VIDEO)
        }

    }

    override fun init(context: Context?) {

    }

}