package com.kotlin.android.community.post.component.item.provider

import android.content.Context
import android.os.Bundle
import androidx.collection.arrayMapOf
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.post.component.item.ui.detail.PostDetailActivity.Companion.POST_CONTENT_ID
import com.kotlin.android.community.post.component.item.ui.detail.PostDetailActivity.Companion.POST_TYPE
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_post.ICommunityPostProvider
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID

/**
 * create by lushan on 2020/8/14
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_COMMUNITY_POST)
class CommunityPostProvider : ICommunityPostProvider {


    /**
     * 跳转到帖子详情 普通帖子和pk帖子
     * @param recId 记录id，没有传0
     * @param contentId 如果是发布过的传内容id 如果没有发布传记录id
     * @param type 1.日志 2.帖子 3.影评 4.文章
     * @param needScrollToComment 是否自动滑动到帖子评论处
     *
     */
    override fun startPostDetail(contentId: Long, type: Long, recId:Long, needScrollToComment: Boolean) {
        Bundle().apply {
            put(arrayMapOf(POST_CONTENT_ID to contentId,
                    POST_TYPE to type,
                    UGC_DETAIL_REC_ID to recId,
                    UGC_DETAIL_NEED_TO_COMMENT to needScrollToComment))
        }.also {
            RouterManager.instance.navigation(RouterActivityPath.Post.PAGE_POST_DETAIL_ACTIVITY, it)
        }
    }

    override fun init(context: Context?) {
    }
}