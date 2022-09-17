package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.annotation.SEARCH_ALL
import com.kotlin.android.app.data.entity.search.Audio
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.ui.result.adapter.SearchResultAudioItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/30
 * 描述: 搜索结果_博客ViewBean
 */
data class AudioViewBean(
        var id: Long = 0L,               // 播客Id
        var title: String = "",          // 标题
        var cover: String = "",          // 封面图
        var authorImg: String = "",      // 发布人头像
        var authorId: Long = 0L,         // 发布人id
        var authorName: String = "",     // 发布人昵称
        var likeNum: Long = 0L,          // 点赞数
        var commentNum: Long = 0L,       // 评论数
        var createTime: Long = 0L,       // 发布时间
        var isLikeUp: Boolean = false,   //当前用户是否点赞
        var authTag: Long = 0L,          // 认证标识
): ProguardRule {

    companion object {

        private val mFirstMarginTopAll = 5.dp
        private val mFirstMarginTopType = 16.dp

        /**
         * 转换ViewBean
         */
        private fun objectToViewBean(bean: Audio): AudioViewBean {
            return AudioViewBean(
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

        fun build(searchType: Long, keyword: String, beans: List<Audio>) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans.mapIndexed { index, it ->
                val viewBean = objectToViewBean(it)
                binderList.add(
                        SearchResultAudioItemBinder(
                                keyword = keyword,
                                viewBean = viewBean,
                                isFirstItem = index == 0,
                                firstMarginTop = if(searchType == SEARCH_ALL) mFirstMarginTopAll else mFirstMarginTopType,
                        )
                )
            }
            return binderList
        }

    }

}