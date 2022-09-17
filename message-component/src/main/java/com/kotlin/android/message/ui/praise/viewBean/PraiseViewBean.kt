package com.kotlin.android.message.ui.praise.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.PraiseListResult
import com.kotlin.android.message.tools.toContentType
import com.kotlin.android.message.ui.praise.binder.ItemPraiseBinder
import com.kotlin.android.message.widget.AuthHeaderView
import com.kotlin.android.message.widget.MainContentView
import com.kotlin.android.message.widget.MultiplePraiseHeaderView
import com.kotlin.android.mtime.ktx.formatPublishTime

/**
 * Created by zhaoninglongfei on 2022/3/22
 *
 */
data class PraiseViewBean(
    val messageId: String? = null,
    val time: String? = null,

    //是否是多人点赞
    val isMultiplePraise: Boolean = false,

    //点赞人数
    val priseCount: Long? = null,

    //点赞人展示 （一人或两人）
    val praiseName: String? = null,

    // "等8人"
    val nameSupplement: String? = null,
    //点赞内容类型 "赞了你的日志/片单/..."
    val praiseContentType: String? = null,

    //评论主体
    val mainContent: MainContentView.MainContentViewProperty? = null,

    //单人点赞头像
    val authHeader: AuthHeaderView.AuthHeader? = null,
    //多人点赞头像
    val multiplePraiseHeader: MultiplePraiseHeaderView.MultiplePraiseHeader? = null
) : ProguardRule {

    fun hasSupplement(): Boolean {
        return nameSupplement != null
    }

    companion object {
        fun convertToItemPraiseBinders(result: PraiseListResult): List<ItemPraiseBinder> {
            val returnList: ArrayList<ItemPraiseBinder> = arrayListOf()
            result.items?.forEach {
                val viewBean = PraiseViewBean(
                    messageId = it.messageId,
                    praiseName = convertPraiseName(it),
                    nameSupplement = convertPraiseSupplement(it),
                    praiseContentType = convertPraiseContentType(it),
                    time = formatPublishTime(it.praiseDate ?: 0L),
                    mainContent = generateMainContent(it),
                    isMultiplePraise = it.users?.size ?: 1 > 1,
                    priseCount = it.praiseUserTotal,
                    authHeader = createAuthHeader(it.users, it.unRead),
                    multiplePraiseHeader = createMultiplePraiseHeader(it.unRead, it.users)
                )
                returnList.add(ItemPraiseBinder(viewBean))
            }

            return returnList
        }

        //生成单人头像
        private fun createAuthHeader(
            users: List<PraiseListResult.Praise.PraiseUser>?,
            unRead: Boolean?
        ): AuthHeaderView.AuthHeader? {
            return if (users?.size ?: 1 > 1) {
                null
            } else {
                AuthHeaderView.AuthHeader(
                    userId = users?.get(0)?.userId,
                    headImg = users?.get(0)?.avatarUrl,
                    unread = unRead,
                    authType = users?.get(0)?.authType,
                    authRole = users?.get(0)?.authRole,
                )
            }
        }

        //生成多人点赞头像
        private fun createMultiplePraiseHeader(
            unRead: Boolean?,
            users: List<PraiseListResult.Praise.PraiseUser>?
        ): MultiplePraiseHeaderView.MultiplePraiseHeader? {
            return if (users?.size ?: 1 > 1) {
                MultiplePraiseHeaderView.MultiplePraiseHeader(
                    authHeader1 = AuthHeaderView.AuthHeader(
                        userId = users!![0].userId,
                        headImg = users[0].avatarUrl,
                        authRole = users[0].authRole,
                        authType = users[0].authType
                    ),
                    authHeader2 = AuthHeaderView.AuthHeader(
                        userId = users[1].userId,
                        headImg = users[1].avatarUrl,
                        authRole = users[1].authRole,
                        authType = users[1].authType
                    ),
                    unread = unRead
                )
            } else {
                null
            }
        }

        //生成内容主体
        private fun generateMainContent(it: PraiseListResult.Praise): MainContentView.MainContentViewProperty? {
            when (it.praiseLevel) {
                1L -> return MainContentView.MainContentViewProperty(
                    //显示标题，没有标题取正文
                    contentId = it.contentInfo?.contentId,
                    contentType = it.contentInfo?.contentType.toContentType(),
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
                2L -> return MainContentView.MainContentViewProperty(
                    contentId = it.commentInfo?.commentId,
                    contentType = it.contentInfo?.contentType.toContentType(),
                    content = it.commentInfo?.commentContent,
                    img = it.commentInfo?.commentImg,
                    commentCount = null,
                    praiseCount = it.commentInfo?.praiseCount?.toLong() ?: 0L,
                    insideContent = null,
                    jumpType = MainContentView.MainContentViewProperty.JumpType.Comment
                )
                3L -> return MainContentView.MainContentViewProperty(
                    contentId = it.commentInfo?.commentId,
                    contentType = it.contentInfo?.contentType.toContentType(),
                    content = it.replyInfo?.replyContent,
                    img = it.replyInfo?.replyImg,
                    commentCount = null,
                    praiseCount = it.replyInfo?.praiseCount?.toLong() ?: 0L,
                    insideContent = null,
                    jumpType = MainContentView.MainContentViewProperty.JumpType.Comment
                )
                else -> return null
            }
        }

        private fun generateContentImg(imgList: java.util.ArrayList<String>?): String? {
            return if (imgList.isNullOrEmpty()) {
                null
            } else {
                imgList[0]
            }
        }

        //转化点赞主体类型
        private fun convertPraiseContentType(it: PraiseListResult.Praise): String? {
            return when (it.praiseLevel) {
                //1针对内容主体点赞
                1L -> {
                    val type = when (it.relatedType) {
                        1L -> "日志"
                        2L -> "帖子"
                        3L -> "影评"
                        4L -> "文章"
                        5L -> "相册"
                        6L -> "榜单"
                        8L -> "预告片"
                        12L -> "视频"
                        13L -> "播客"
                        14L -> "片单"
                        else -> "评论"
                    }
                    return "赞了你的$type"
                }
                // 2针对评论点赞 3针对回复点赞
                else -> "赞了你的评论"
            }
        }

        //转化点赞补充
        private fun convertPraiseSupplement(it: PraiseListResult.Praise): String? {
            return if (it.users?.size == 1) {
                null
            } else {
                "等${it.praiseUserTotal}人"
            }
        }

        //转化点赞人
        private fun convertPraiseName(it: PraiseListResult.Praise): String? {
            if (it.users.isNullOrEmpty()) {
                //这种情况不太可能出现
                return null
            }

            return if (it.users?.size == 1) {
                it.users?.get(0)?.nickName
            } else {
                //最初点赞两人的昵称
                "${it.users?.get(0)?.nickName}、${it.users?.get(1)?.nickName}"
            }
        }
    }
}