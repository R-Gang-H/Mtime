package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.entity.search.Video
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.ui.result.adapter.SearchResultVideoItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/30
 * 描述: 搜索结果_视频ViewBean
 */
data class VideoViewBean(
        var id: Long = 0,               // 视频Id
        var title: String = "",         // 标题
        var cover: String = "",         // 封面图
        var authorImg: String = "",     // 发布人头像
        var authorId: Long = 0,         // 发布人id
        var authorName: String = "",    // 发布人昵称
        var likeNum: Long = 0,          // 点赞数
        var commentNum: Long = 0,       // 评论数
        var createTime: Long = 0,       // 发布时间
        var isLikeUp: Boolean = false,  // 当前用户是否点赞
        var authTag: Long = 0,          // 认证标识
): ProguardRule {

    companion object {

        private val mFirstMarginTopType = 11.dp

        /**
         * 转换ViewBean
         */
        private fun objectToViewBean(bean: Video): VideoViewBean {
            return VideoViewBean(
                    id = bean.id.orZero(),
                    title = bean.title.orEmpty(),
                    cover = bean.cover.orEmpty(),
                    authorImg = bean.authorImg.orEmpty(),
                    authorId = bean.authorId.orZero(),
                    authorName = bean.authorName.orEmpty(),
                    likeNum = bean.likeNum.orZero(),
                    commentNum = bean.commentNum.orZero(),
                    createTime = bean.createTime.orZero(),
                    isLikeUp = bean.isLikeUp.orFalse(),
                    authTag = bean.authTag.orZero(),
            )
        }

        fun build(searchType: Long, keyword: String, beans: List<Video>) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans.mapIndexed { index, it ->
                val viewBean = objectToViewBean(it)
                binderList.add(
                        SearchResultVideoItemBinder(
                                keyword = keyword,
                                viewBean = viewBean,
                                isFirstItem = index == 0,
                                firstMarginTop = if(searchType == SEARCH_ALL) 0 else mFirstMarginTopType,
                        )
                )
            }
            return binderList
        }

    }
}
