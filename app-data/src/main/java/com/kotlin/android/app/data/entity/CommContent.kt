package com.kotlin.android.app.data.entity

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant

/**
 * Updated by zhousuqiang on 2020-08-25
 *
 * 社区内容公共实体（日志、帖子、文章、影评）
 */
data class CommContent(
    var userCreateTime: UserCreateTime? = null,
    var createUser: User? = null,
    var essence: Boolean = false, //是否加精
    var interactive: Interactive? = null,
    var recId: Long = 0L, //未发布群组帖子记录ID
    var contentId: Long = 0L,
    var type: Long = 1L, //内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章");VIDEO(5, "视频"), AUDIO(6, "音频"),
    var title: String? = "",
    var fcMovie: FcMovie? = null, //关联的电影
    var top: Boolean = false, //是否置顶（家族内的置顶）
    var fcPerson: FcPerson? = null, //关联的影人
    var vote: Vote? = null,
    var mixWord: String? = null, //混合文字 优先级按照摘要、富文本文字截取
    var fcType: Long? = 0L, //影评类型 LONG_COMMENT(1, "长影评"), SHORT_COMMENT(2, "短影评");
    var fcRating: String? = null, //影评发表者评分
    var rcmdText: String? = null, //推荐语
    var group: Group? = null, //所属的家族
    var hotComments: List<HotComment>? = null, //热门评论
    var mixVideos: List<MixVideo>? = null, //混合视频集合 按照视频集合、富文本视频集合合并
    var mixImages: List<Image>? = null, //混合图片集合 按照封面、图片集合、富文本图片集合合并
    var video: Video? = null,
    var audio: Audio? = null
) : ProguardRule {

    companion object {
        const val TYPE_JOURNAL = 1L//日志
        const val TYPE_POST = 2L//帖子
        const val TYPE_REVIEW = 3L//影评
        const val TYPE_ARTICLE = 4L//文章
        const val TYPE_VIDEO = 5L//视频
        const val TYPE_AUDIO = 6L//音频

        //todo 以下可能用于评论
        const val TYPE_VIDEO_DETIAL = 8L//视频详情
        const val TYPE_LIVE = 9L //直播
    }
    
    fun getMediaTime(): Long? {
        return video?.videoSec ?: audio?.audioSec
    }

    fun getPraiseObjType(): Long {
        return when (type) {
            TYPE_JOURNAL -> {
                CommConstant.PRAISE_OBJ_TYPE_JOURNAL
            }
            TYPE_POST -> {
                CommConstant.PRAISE_OBJ_TYPE_POST
            }
            TYPE_REVIEW -> {
                CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT
            }
            TYPE_ARTICLE -> {
                CommConstant.PRAISE_OBJ_TYPE_ARTICLE
            }
            TYPE_VIDEO -> {
                CommConstant.PRAISE_OBJ_TYPE_VIDEO
            }
            TYPE_AUDIO -> {
                CommConstant.PRAISE_OBJ_TYPE_AUDIO
            }
            else -> {
                0
            }
        }
    }

    fun getCommentCount(): Long {
        return interactive?.commentCount ?: 0L
    }

    fun getImg(index: Int): String? {
        return if (!mixImages.isNullOrEmpty()) {
            mixImages?.get(index)?.imageUrl
        } else {
            ""
        }
    }

    fun isShortReviewType(): Boolean {
        return type == TYPE_REVIEW && fcType == 2L
    }

    fun isPkType(): Boolean {
        return type == TYPE_POST && !vote?.opts.isNullOrEmpty()
    }

    //获取选项的label描述
    fun getVoteOptLabel(id: Long): String {
        vote?.opts?.forEach {
            if (it.optId == id) {
                return it.optDesc ?: ""
            }
        }
        return ""
    }

    data class UserCreateTime(
        var show: String?,
        var stamp: Long
    ) : ProguardRule

    data class User(
        var avatarUrl: String?,
        var nikeName: String?,
        var authType: Long, //用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
        var userId: Long
    ) : ProguardRule

    data class Interactive(
        var userPraised: Long? = 0L, //用户赞：1:点赞 2点踩 null:无
        var praiseUpCount: Long,
        var praiseDownCount: Long,
        var voteStat: VoteStat?,
        var commentCount: Long,
        var userVoted: List<Long>
    ) : ProguardRule

    data class VoteStat(
        var count: Long,
        var optCounts: List<OptCounts>?
    ) : ProguardRule

    data class OptCounts(
        var count: Long,
        var optId: Long
    ) : ProguardRule

    data class FcMovie(
        var imgUrl: String? = "",
        var btnShow: Long? = -1, //当前用户对电影的态度：1:预售2:购票3:想看4:已想看
        var genreTypes: String?,
        var releaseDate: String?,
        var minutes: String?,
        var rating: String?,
        var name: String?,
        var id: Long,
        var nameEn: String?,
        var releaseArea: String?
    ) : ProguardRule

    data class FcPerson(
        var imgUrl: String? = "",
        var profession: String?,
        var nameCn: String?,
        var id: Long,
        var nameEn: String?,
        var birthDate: String?
    ) : ProguardRule

    data class Vote(
        var multiple: Boolean,
        var opts: List<Opts>?
    ) : ProguardRule

    data class Opts(
        var optDesc: String?,
        var optId: Long
    ) : ProguardRule

    data class Group(
        var name: String? = "",
        var id: Long
    ) : ProguardRule

    data class HotComment(
        var userCreateTime: UserCreateTime?,
        var createUser: User?,
        var replyCount: Long,
        var interactive: Interactive?,
        var objId: Long,
        var commentId: Long,
        var body: String,
        var bodyWords: String? = "",//兼容prd上的老数据，去掉html标签
        var objType: Long,
        var optId: Long,
        var images: List<Image>?,
        var replies: List<Replies>?
    ) : ProguardRule

    data class Image(
        var imageFormat: String? = null,
        var imageId: String? = null,
        var imageUrl: String? = null,
        var imageDesc: String? = null,
        var isGif: Boolean = false
    ) : ProguardRule

    data class Replies(
        var userCreateTime: UserCreateTime?,
        var reUser: User?,
        var createUser: User?,
        var reReplyId: Long,
        var replyId: Long,
        var body: String?,
        var images: List<Image>?
    ) : ProguardRule

    data class MixVideo(
        var vid: Long,
        var vsource: Long, //视频来源 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资");
        var posterUrl: String?
    ) : ProguardRule

    data class Video(
        var videoSec: Long? //时长 单位秒
    ) : ProguardRule

    data class Audio(
        var audioSec: Long? //时长 单位秒
    ) : ProguardRule
}