package com.kotlin.android.message.ui.comment.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.CommentListResult
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.message.tools.ContentType
import com.kotlin.android.message.tools.toContentType
import com.kotlin.android.message.ui.comment.binder.ItemCommentBinder
import com.kotlin.android.message.widget.AuthHeaderView
import com.kotlin.android.message.widget.MainContentView
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.user.UserManager

/**
 * Created by zhaoninglongfei on 2022/3/22
 *
 */
data class CommentViewBean(
    val messageId: String,
    val commentId: Long? = null,
    val userId: Long? = null,

    val name: String? = null,
    val time: String? = null,

    //主体类型
    val contentType: ContentType? = null,
    //评论描述 （"评论了你的文章"/"回复了你的帖子"）
    val commentDesc: String? = null,
    //评论内容
    val commentString: String? = null,
    //评论图片
    val commentPic: String? = null,
    //评论主体
    val mainContent: MainContentView.MainContentViewProperty? = null,

    val authHeader: AuthHeaderView.AuthHeader?
) : ProguardRule {

    fun hasCommentPic(): Boolean {
        return !commentPic.isNullOrEmpty()
    }

    companion object {
        fun convertToItemCommentBinders(
            result: CommentListResult,
            deleteComment: (String) -> Unit
        ): List<ItemCommentBinder> {
            val returnList: ArrayList<ItemCommentBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = CommentViewBean(
                    messageId = it.messageId ?: "",
                    commentId = it.commentInfo?.commentId,
                    userId = it.user?.userId,
                    name = it.user?.nickName,
                    time = formatPublishTime(it.commentDate ?: 0L),
                    contentType = it.contentInfo?.contentType.toContentType(),
                    commentDesc = convertCommentDesc(it),
                    commentString = "\"${convertCommentString(it)}\"",
                    commentPic = convertCommentPic(it),
                    mainContent = generateMainContent(it),
                    authHeader = AuthHeaderView.AuthHeader(
                        userId = it.user?.userId,
                        headImg = it.user?.avatarUrl,
                        unread = it.unRead,
                        authType = it.user?.authType,
                        authRole = it.user?.authRole
                    )
                )
                //viewBean.log()
                returnList.add(ItemCommentBinder(viewBean, deleteComment))
            }

            return returnList
        }

        //生成内容主体
        private fun generateMainContent(it: CommentListResult.Comment): MainContentView.MainContentViewProperty? {
            return when (it.type) {
                //对内容的评论
                1L -> MainContentView.MainContentViewProperty(
                    //显示标题，没有标题取正文
                    contentId = it.contentInfo?.contentId,
                    contentType = it.contentInfo?.contentType?.toContentType(),
                    content = if (it.contentInfo?.title.isNullOrEmpty()) {
                        it.contentInfo?.content
                    } else {
                        it.contentInfo?.title
                    },
                    img = generateContentImg(it.contentInfo?.imgList),
                    commentCount = it.contentInfo?.commentCount ?: 0,
                    praiseCount = it.contentInfo?.praiseCount ?: 0,
                    insideContent = null
                )
                //对评论的回复
                2L -> MainContentView.MainContentViewProperty(
                    contentId = it.commentInfo?.commentId,
                    contentType = it.contentInfo?.contentType.toContentType(),
                    content = "${UserManager.instance.nickname}：${it.commentInfo?.commentContent}",
                    img = it.commentInfo?.commentImg,
                    commentCount = 0,
                    praiseCount = 0,
                    insideContent = MainContentView.MainContentViewProperty.InsideContent(
                        //显示标题，没有标题取正文
                        insideContentId = it.contentInfo?.contentId,
                        insideContentType = it.contentInfo?.contentType.toContentType(),
                        content = if (it.contentInfo?.title.isNullOrEmpty()) {
                            it.contentInfo?.content
                        } else {
                            it.contentInfo?.title
                        },
                        img = generateContentImg(it.contentInfo?.imgList),
                        commentCount = it.contentInfo?.commentCount ?: 0,
                        praiseCount = it.contentInfo?.praiseCount ?: 0
                    )
                )
                //对回复的回复
                3L -> MainContentView.MainContentViewProperty(
                    contentId = it.commentInfo?.commentId,
                    contentType = it.contentInfo?.contentType?.toContentType(),
                    content = "${UserManager.instance.nickname}：${it.replyToReplyInfo?.replyContent}",
                    img = it.replyToReplyInfo?.replyImg,
                    commentCount = 0,
                    praiseCount = 0,
                    insideContent = MainContentView.MainContentViewProperty.InsideContent(
                        //显示标题，没有标题取正文
                        insideContentId = it.contentInfo?.contentId,
                        insideContentType = it.contentInfo?.contentType?.toContentType(),
                        content = if (it.contentInfo?.title.isNullOrEmpty()) {
                            it.contentInfo?.content
                        } else {
                            it.contentInfo?.title
                        },
                        img = generateContentImg(it.contentInfo?.imgList),
                        commentCount = it.contentInfo?.commentCount ?: 0,
                        praiseCount = it.contentInfo?.praiseCount ?: 0
                    )
                )
                else -> null
            }
        }

        private fun generateContentImg(imgList: java.util.ArrayList<String>?): String? {
            return if (imgList.isNullOrEmpty()) {
                null
            } else {
                imgList[0]
            }
        }

        //转化评论图片
        private fun convertCommentPic(it: CommentListResult.Comment): String? {
            return when (it.type) {
                1L -> it.commentInfo?.commentImg
                2L -> it.replyInfo?.replyImg
                3L -> it.replyInfo?.replyImg
                else -> null
            }
        }

        //转化评论内容
        private fun convertCommentString(it: CommentListResult.Comment): String? {
            return when (it.type) {
                1L -> it.commentInfo?.commentContent
                2L -> it.replyInfo?.replyContent
                3L -> it.replyInfo?.replyContent
                else -> null
            }
        }

        //转化评论描述
        private fun convertCommentDesc(comment: CommentListResult.Comment): String? {
            //评论主体类型
            var contentType = when (comment.contentInfo?.contentType) {
                1L -> "日志"
                2L -> "帖子"
                3L -> "影评"
                4L -> "文章"
                5L -> "相册"
                6L -> "榜单"
                7L -> "影院"
                8L -> "预告片"
                9L -> "直播"
                10L -> "卡片大富翁"//卡片用户
                11L -> "卡片大富翁"//卡片套装
                12L -> "视频"
                13L -> "播客"
                14L -> "片单"
                else -> "内容"
            }

            return if (comment.type == 1L) {
                "评论了你的${contentType}："
            } else {
                "回复了你${contentType}下的评论："
            }
        }
    }

    private fun log() {
        "CommentViewBean : messageId $messageId,commentDesc $commentDesc,commentString $commentString,commentPic $commentPic".e()
    }
}