package com.kotlin.android.article.component.item.bean

import androidx.annotation.ColorRes
import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.item.adapter.ArticleBigPicItemBinder
import com.kotlin.android.article.component.item.adapter.ArticleNoPicItemBinder
import com.kotlin.android.article.component.item.adapter.ArticleSmallPicItemBinder
import com.kotlin.android.article.component.item.adapter.ArticleThreeSmallPicItemBinder
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/9
 *
 * 新闻item实体
 */
data class ArticleItem(
    var id: Long = 0,
    var title: String = "",
    var author: String = "",
    var authorId: Long = 0,
    var authorImg: String = "",
    var publishTime: String? = "",
    var tagText: String? = "",
    @ColorRes var tagColorRes: Int = R.color.color_feb12a,
    var isVideo: Boolean = false,
    var type: Long = 1, //内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");VIDEO(5, "视频"), AUDIO(6, "音频")
    var pics: List<String>? = mutableListOf(),
    var isPublish: Boolean = true,
    var commentCount: String = "",
    var isAudio: Boolean = false
) : ProguardRule {

    companion object {
        const val TYPE_BIG_PIC = 1L
        const val TYPE_SMALL_PIC = 2L
        const val TYPE_THREE_PIC = 3L
        const val TYPE_NO_PIC = 4L

        fun converter2ArticleBinder(
            commContent: CommContent?,
            rcmdTop: Boolean = false,
            isPublish: Boolean = true
        ): MultiTypeBinder<*> {
            val showType = getShowType(commContent)
            val item = converter(commContent, rcmdTop, isPublish)
            return when (showType) {
                TYPE_BIG_PIC -> {
                    ArticleBigPicItemBinder(item)
                }
                TYPE_SMALL_PIC -> {
                    ArticleSmallPicItemBinder(item)
                }
                TYPE_THREE_PIC -> {
                    ArticleThreeSmallPicItemBinder(item)
                }
                else -> {
                    ArticleNoPicItemBinder(item)
                }
            }
        }

        /**
         * 将社区公共实体转换成UI实体
         */
        fun converter(
            commContent: CommContent?,
            rcmdTop: Boolean,
            isPublish: Boolean = true
        ): ArticleItem {
            commContent?.let {
                var id = if (it.contentId != 0L) it.contentId else it.recId
                return ArticleItem(
                    id = id,
                    title = it.title ?: "",
                    author = it.createUser?.nikeName ?: "",
                    authorId = it.createUser?.userId ?: 0,
                    authorImg = it.createUser?.avatarUrl ?: "",
                    publishTime = formatPublishTime(it.userCreateTime?.stamp),
                    tagText = if (rcmdTop) "置顶" else "",
                    isVideo = it.mixVideos.isNullOrEmpty()
                        .not() || it.type == CommContent.TYPE_VIDEO,
                    type = it.type,
                    pics = if (it.mixVideos.isNullOrEmpty().not())
                        listOf(it.mixVideos?.get(0)?.posterUrl ?: "")
                    else converterImgs(it.mixImages),
                    isPublish = isPublish,
                    commentCount = "${formatCount(it.getCommentCount())}人评论",
                    isAudio = it.type == CommContent.TYPE_AUDIO,
                )
            }
            return ArticleItem()
        }

        //根据数据生成文章的类型 //1为大图，2为小图，3为三小图, 4为无图
        private fun getShowType(commContent: CommContent?): Long {
            commContent?.let {
                if (it.mixVideos.isNullOrEmpty().not()
                    || it.type == CommContent.TYPE_VIDEO
                ) {
                    return TYPE_BIG_PIC
                }
                if (it.type == CommContent.TYPE_AUDIO) {
                    return TYPE_SMALL_PIC
                }
                it.mixImages?.let { imgs ->
                    if (imgs.isNotEmpty()) {
                        return if (imgs.size < 3) {
                            TYPE_SMALL_PIC
                        } else {
                            TYPE_THREE_PIC
                        }
                    }
                }
            }
            return TYPE_NO_PIC
        }

        //转换图片
        private fun converterImgs(mixImages: List<CommContent.Image>?): List<String>? {
            mixImages?.run {
                return map {
                    it.imageUrl ?: ""
                }
            }
            return null
        }
    }

    fun getPic(index: Int): String {
        pics?.let {
            if (index >= 0 && index < it.size) {
                return it[index]
            }
        }
        return ""
    }
}