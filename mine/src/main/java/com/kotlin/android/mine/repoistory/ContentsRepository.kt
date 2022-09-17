package com.kotlin.android.mine.repoistory

import android.content.Context
import android.text.TextUtils
import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.data.constant.CommConstant.MINE_CONTENT_TAB_DRAFTS
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.app.data.entity.community.content.ContentTypeCount
import com.kotlin.android.mine.bean.ContentsViewBean
import com.kotlin.android.retrofit.getRequestBody

class ContentsRepository : com.kotlin.android.app.api.base.BaseRepository() {

    /**
     * 查询 tab 类型的数据
     */
    suspend fun getContent(
        context: Context,
        type: Long,
        tab: Long,
        nextStamp: String?,
        pageSize: Long,
        sort: Long,
        isDrafts: Boolean
    ): ApiResult<ContentsViewBean> {
        var tabNum: Long = if (isDrafts) MINE_CONTENT_TAB_DRAFTS else tab
        var pageStamp: String = if (!TextUtils.isEmpty(nextStamp)) nextStamp!! else ""
        return request(
            converter = { contentList ->
                ContentsViewBean.obtain(context, contentList, type, isDrafts)
            }
        ) {
            val body = getRequestBody(
                arrayMapOf(
                    "type" to type,
                    "tab" to tabNum,
                    "nextStamp" to pageStamp,
                    "pageSize" to pageSize,
                    "sort" to sort
                )
            )
            apiMTime.getCommunityQueryContent(body)
        }
    }

    suspend fun deleteContent(contentIds: Long?, type: Long): ApiResult<Any> {
        return request { apiMTime.deleteContent(type, contentIds) }
    }

    suspend fun reviewContent(contentId: Long, type: Long): ApiResult<Any> {
        return request { apiMTime.reviewContent(type, contentId) }
    }

    suspend fun getTypeCount(type: Long): ApiResult<ContentTypeCount> {
        return request { apiMTime.getCommunityTypeCount(type) }
    }

    /**
     * 查询当前文章用户信息
     */
    suspend fun getQueryArticleUser(): ApiResult<ArticleUser> {
        return request { apiMTime.getQueryArticleUser() }
    }
}