package com.kotlin.android.community.post.component.item.bean

import android.text.TextUtils
import com.kotlin.android.comment.component.bean.CommentViewBean
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatPublishTime
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.mtime.ktx.formatTime
import java.text.DecimalFormat

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/20
 *
 * 帖子UI实体
 */

data class CommunityPostItem(
        var id: Long = 0,
        var userId: Long = 0,
        var userAuthType: Long = 1,
        var userPic: String? = "",
        var userName: String? = "",
        var publishDate: String? = "",
        var title: String? = "",
        var mixWord: String? = "",
        var imgs: List<CommunityPostImg>? = null,
        var movieId: Long = 0,
        var moviePic: String? = "",
        var movieName: String? = "",
        var movieMtimeScore: String? = "",
        var movieType: String? = "",
        var movieReleaseDate: String? = "",
        var movieBtnState: Long = 1, //1:预售2:购票3:想看4:已想看
        var hotComments: List<CommunityPostHotComment>? = null,
        var familyId: Long = 0,
        var familyName: String? = "",
        var likeCount: Long = 0,
        var isLike: Boolean = false,
        var commentCount: Long = 0,
        var opinions: List<VoteOpinion>? = null, //投票选项
        var voteNumber: Long = 0, //投票人数
        var type: Long = 1, //内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");VIDEO(5, "视频"), AUDIO(6, "音频")
        var fcType: Long = 0, //影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
        var isPublished: Boolean = true, //代表帖子是否已发布(审核通过)
        var isTop: Boolean = false, //帖子是否置顶
        var isEssence: Boolean = false, //帖子是否加精
        var timeMillis: Long = 0L, //帖子时间
        var time: String = "",//帖子时间
        var praiseObjType: Long = 0L, //点赞类型
        var mediaTime: String = "", //音视频长时
        var binderType: Long = 1L, //1个人主页（隐藏） 2收藏（显示） 用于判断是否显示userinfo view
        var hasVideo: Boolean = false
) : ProguardRule {

    override fun hashCode(): Int {
        System.currentTimeMillis()
        var hotCommentsHashCode = 0
        hotComments?.forEach {
            hotCommentsHashCode += it.hashCode()
        }
        return id.toInt() + isLike.hashCode() + likeCount.hashCode() + commentCount.hashCode() + movieBtnState.hashCode() + voteNumber.hashCode() + hotCommentsHashCode
    }

    fun getShareType(): Long {
        when (type) {
            CommContent.TYPE_JOURNAL -> {
                return CommConstant.SHARE_TYPE_JOURNAL
            }
            CommContent.TYPE_POST -> {
                return CommConstant.SHARE_TYPE_POST
            }
            CommContent.TYPE_REVIEW -> {
                return if (fcType == 1L) {
                    CommConstant.SHARE_TYPE_LONG_REVIEW
                } else {
                    CommConstant.SHARE_TYPE_SHORT_REVIEW
                }
            }
            CommContent.TYPE_VIDEO -> {
                return CommConstant.SHARE_TYPE_VIDEO
            }
            else -> {
                return CommConstant.SHARE_TYPE_ARTICLE
            }
        }
    }

    companion object {

        /**
         * 将社区公共实体转换成UI Binder
         *
         * @param commContent 社区公共实体
         * @param isPublished 帖子是否已发布(审核通过)
         */
        fun converter2Binder(
                commContent: CommContent?,
                isPublished: Boolean = true
        ): CommunityPostBinder {
            return CommunityPostBinder(converter(commContent, isPublished))
        }

        /**
         * 将社区个人主页内容实体转换成UI实体
         *
         * @param commContent 社区公共实体
         * @param isPublished 帖子是否已发布(审核通过)
         * @param binderType 区分个人主页列表 、收藏列表
         */
        fun converter(
                commContent: CommContent?,
                isPublished: Boolean = true,
                binderType: Long = 1L
        ): CommunityPostItem {
            var data = converter(commContent, isPublished)
            data.binderType = binderType
            return data
        }

        /**
         * 将社区公共实体转换成UI实体
         *
         * @param commContent 社区公共实体
         * @param isPublished 帖子是否已发布(审核通过)
         */
        fun converter(
                commContent: CommContent?,
                isPublished: Boolean = true,
        ): CommunityPostItem {
            commContent?.let {
                return CommunityPostItem(
                        id = if (isPublished) it.contentId else it.recId,
                        userId = it.createUser?.userId ?: 0,
                        userAuthType = it.createUser?.authType ?: 1,
                        userPic = it.createUser?.avatarUrl ?: "",
                        userName = it.createUser?.nikeName ?: "",
                        publishDate = formatPublishTime(it.userCreateTime?.stamp),
                        title = if (it.fcType == 2L) it.mixWord else it.title,
                        mixWord = it.mixWord,
                        imgs = if (it.type == CommContent.TYPE_VIDEO) {
                            if (it.mixImages.isNullOrEmpty()) {
                                null
                            } else {
                                it.mixImages?.run {
                                    listOf(
                                        CommunityPostImg(
                                            isGif = get(0).isGif,
                                            pic = get(0).imageUrl
                                        )
                                    )
                                }
                            }
                        } else {
                            it.mixImages?.map { image ->
                                CommunityPostImg(
                                        isGif = image.isGif,
                                        pic = image.imageUrl
                                )
                            }
                        },
                        movieId = it.fcMovie?.id ?: 0L,
                        moviePic = it.fcMovie?.imgUrl ?: "",
                        movieName = it.fcMovie?.name ?: "",
                        movieMtimeScore = it.fcMovie?.rating ?: "",
                        movieType = if (TextUtils.isEmpty(it.fcMovie?.minutes) && TextUtils.isEmpty(it.fcMovie?.genreTypes)) ""
                        else it.fcMovie?.minutes + "-" + it.fcMovie?.genreTypes,
                        movieReleaseDate = if (TextUtils.isEmpty(it.fcMovie?.releaseDate) && TextUtils.isEmpty(
                                        it.fcMovie?.releaseArea
                                )
                        ) ""
                        else it.fcMovie?.releaseDate + it.fcMovie?.releaseArea,
                        movieBtnState = it.fcMovie?.btnShow ?: 3,
                        hotComments = it.hotComments?.map { hotComment ->
                            CommunityPostHotComment(
                                    id = hotComment.commentId,
                                    userName = hotComment.createUser?.nikeName,
                                    content = hotComment.bodyWords.orEmpty(),
                                    likeCount = hotComment.interactive?.praiseUpCount ?: 0L,
                                    isLike = null != hotComment.interactive
                                            && hotComment.interactive?.userPraised == 1L,
                                    dislikeCount = hotComment.interactive?.praiseDownCount ?: 0L,
                                    isDislike = null != hotComment.interactive
                                            && hotComment.interactive?.userPraised == 2L,
                                    voteOpinionId = hotComment.optId,
                                    hasPic = !hotComment.images.isNullOrEmpty()
                            )
                        },
                        familyId = it.group?.id ?: 0L,
                        familyName = it.group?.name,
                        likeCount = it.interactive?.praiseUpCount ?: 0L,
                        isLike = it.interactive?.userPraised == 1L,
                        commentCount = it.interactive?.commentCount ?: 0L,
                        opinions = it.interactive?.voteStat?.optCounts?.map { opt ->
                            VoteOpinion(
                                    id = opt.optId,
                                    label = it.getVoteOptLabel(opt.optId),
                                    count = opt.count,
                                    isCheck = it.interactive?.userVoted?.get(0) == opt.optId
                            )
                        },
                        voteNumber = it.interactive?.voteStat?.count ?: 0L,
                        type = it.type,
                        fcType = it.fcType ?: 0L,
                        isPublished = isPublished,
                        isTop = it.top,
                        isEssence = it.essence,
                        timeMillis = it.userCreateTime?.stamp ?: 0L,
                        time = it.userCreateTime?.show ?: "",
                        praiseObjType = commContent.getPraiseObjType(),
                        mediaTime = formatTime(commContent.getMediaTime()),
                        hasVideo = commContent.mixVideos.isNullOrEmpty().not()
                )
            }
            return CommunityPostItem()
        }
    }

    fun hasMediaTime() = mediaTime.isNotEmpty()

    //是不是机构认证用户
    fun isInstitutionAuthUser(): Boolean {
        return userAuthType == 4L
    }

    //是不是认证用户
    fun isAuthUser(): Boolean {
        return userAuthType > 1L
    }

    fun isPkType(): Boolean {
        return type == CommContent.TYPE_POST && hasOpinions()
    }

    fun isCheckVoteOpinion(): Boolean {
        if (hasOpinions()) {
            opinions?.forEach {
                if (it.isCheck) {
                    return true
                }
            }
        }
        return false
    }

    fun isFirstVoteOpinion(voteOpinionId: Long): Boolean {
        return opinions?.get(0)?.id == voteOpinionId
    }

    fun hasHotComment(): Boolean {
        return !hotComments.isNullOrEmpty()
    }

    fun hasOpinions(): Boolean {
        return !opinions.isNullOrEmpty()
    }

    fun getPic(index: Int): String {
        imgs?.let {
            return it[index].pic ?: ""
        }
        return ""
    }

    fun getOpinionLabel(id: Long): String {
        opinions?.forEach {
            if (it.id == id) {
                return it.label
            }
        }
        return ""
    }
}

//投票选项
data class VoteOpinion(
        var id: Long,
        var label: String = "",
        var count: Long = 0,
        var isCheck: Boolean
) : ProguardRule

//热门评论
data class CommunityPostHotComment(
        var id: Long = 0,
        var userName: String? = "",
        var content: String? = "",
        var likeCount: Long = 0,
        var isLike: Boolean = false,
        var dislikeCount: Long = 0,
        var isDislike: Boolean = false,
        var voteOpinionId: Long = 0,
        var hasPic: Boolean = false
) : ProguardRule {
    override fun hashCode(): Int {
        return id.toInt() + isLike.hashCode()
    }

    //评论的点赞点踩类型，具体返回值请查看CommConstant
    fun getPraiseType(contentType: Long): Long {
        return contentType + 100
    }
}

//帖子图片
data class CommunityPostImg(
        var isGif: Boolean,
        var pic: String? = ""
) : ProguardRule

///////////////////////////////////////////////////////////////////////////////////////////////////

//pk帖子评论
data class PKComentViewBean(
        var commentId: Long = 0L,
        var userId: Long = 0L,//用户ID
        var userHeadPic: String = "",//用户头像
        var userNickName: String = "",//用户昵称
        var comment: String = "",//用户评论内容
        var praiseCount: Long = 0L,//赞的数字
        var unPraiseCount: Long = 0L,//踩的数字
        var likeState: Long = 0L,//自己点赞、踩的状态 0未操作、1点了赞、2点了踩
        var isCommentPositive: Boolean = false,//是否是支持方
        var userAuthType: Long = 0L//认证类型
) : ProguardRule {
    companion object {
        fun create(bean: CommentViewBean, isCommentPositive: Boolean): PKComentViewBean {
            return PKComentViewBean(
                    commentId = bean.commentId, userId = bean.userId,
                    userHeadPic = bean.userPic,
                    userNickName = bean.userName,
                    comment = bean.commentContent,
                    praiseCount = bean.likeCount,
                    unPraiseCount = bean.praiseDownCount,
                    likeState = bean.userPraised ?: 0L,
                    isCommentPositive = isCommentPositive,
                    userAuthType = bean.userAuthType
            )
        }
    }

    /**
     * 是否是点赞
     */
    fun isPariseUp(): Boolean = likeState == PRAISE_STATE_PRAISE

    /**
     * 时是否是点踩
     */
    fun isPraiseDown(): Boolean = likeState == PRAISE_STATE_UNPRAISE
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PKComentViewBean

        if (commentId != other.commentId) return false
        if (userId != other.userId) return false
        if (userHeadPic != other.userHeadPic) return false
        if (userNickName != other.userNickName) return false
        if (comment != other.comment) return false
        if (praiseCount != other.praiseCount) return false
        if (unPraiseCount != other.unPraiseCount) return false
        if (likeState != other.likeState) return false
        if (isCommentPositive != other.isCommentPositive) return false

        return true
    }

    override fun hashCode(): Int {
        var result = commentId.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + userHeadPic.hashCode()
        result = 31 * result + userNickName.hashCode()
        result = 31 * result + comment.hashCode()
        result = 31 * result + praiseCount.hashCode()
        result = 31 * result + unPraiseCount.hashCode()
        result = 31 * result + likeState.hashCode()
        result = 31 * result + isCommentPositive.hashCode()
        return result
    }

}

//pk帖子，支持信息
data class PKStatusViewBean(
        var hasPk: Boolean = false,//是否选择过pk
        var positiveId: Long = 0L,//支持id
        var positiveDes: String = "",//支持文案
        var nagivationDes: String = "",//反对文案
        var positionCount: Long = 0L,//支持人数
        var nagivationId: Long = 0L,//反对id
        var nagivationCount: Long = 0L,//反对人数
        var joinCount: Long = 0L//总投票数
) : ProguardRule {

    /**
     * 获取pk支持百分比
     */
    fun getPositionPercent(): String {
        if (joinCount == 0L) {
            return "0%"
        }

        val d = 1 - (nagivationCount.toDouble()) / (joinCount.toDouble())
        return "${getPercent(100 * d)}%"
    }

    private fun getPercent(num: Double): String {
        val df = DecimalFormat("#0")
        return df.format(num)
    }

    /**
     * 获取反对票百分比
     */
    fun getNagivationPercent(): String {
        if (joinCount == 0L) {
            return "0%"
        }
        val d = (nagivationCount.toDouble()) / (joinCount.toDouble())
        return "${getPercent(100 * d)}%"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PKStatusViewBean

        if (hasPk != other.hasPk) return false
        if (positiveId != other.positiveId) return false
        if (positiveDes != other.positiveDes) return false
        if (nagivationDes != other.nagivationDes) return false
        if (positionCount != other.positionCount) return false
        if (nagivationId != other.nagivationId) return false
        if (nagivationCount != other.nagivationCount) return false
        if (joinCount != other.joinCount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hasPk.hashCode()
        result = 31 * result + positiveId.hashCode()
        result = 31 * result + positiveDes.hashCode()
        result = 31 * result + nagivationDes.hashCode()
        result = 31 * result + positionCount.hashCode()
        result = 31 * result + nagivationId.hashCode()
        result = 31 * result + nagivationCount.hashCode()
        result = 31 * result + joinCount.hashCode()
        return result
    }

    companion object {
        fun create(bean: UgcCommonBarBean.CommentSupport): PKStatusViewBean {
            val vote = bean.vote
            val positiveId = if (vote?.opts?.isNotEmpty() == true) vote.opts?.get(0)?.optId
                    ?: 0L else 0L
            val nagivationId = if (vote?.opts?.size == 2) vote.opts?.get(1)?.optId
                    ?: 0L else 0L
            return PKStatusViewBean(
                    hasPk = bean.userVotedId != 0L,
                    positiveId = positiveId,
                    positiveDes = getVoteDes(positiveId, vote),
                    positionCount = if (bean.voteStat?.optCounts?.isNotEmpty() == true) bean.voteStat?.optCounts?.get(
                            0
                    )?.count
                            ?: 0L else 0L,
                    nagivationId = nagivationId,
                    nagivationDes = getVoteDes(nagivationId, vote),
                    nagivationCount = if (bean.voteStat?.optCounts?.size == 2) bean.voteStat?.optCounts?.get(
                            1
                    )?.count
                            ?: 0L else 0L,
                    joinCount = bean.voteStat?.count ?: 0L
            )

        }

        /**
         * 根据投票id获取投票选项
         */
        private fun getVoteDes(voteId: Long, vote: CommunityContent.Vote?): String {
            if (vote == null) return ""
            vote.opts?.forEach {
                if (it.optId == voteId) {
                    return it.optDesc.orEmpty()
                }
            }

            return ""
        }
    }
}

const val PRAISE_STATE_INIT = 0L//点赞、踩初始状态
const val PRAISE_STATE_PRAISE = 1L//点赞
const val PRAISE_STATE_UNPRAISE = 2L//踩