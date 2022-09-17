package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.entity.common.MovieSubItemRating
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.video.VideoPlayUrl

/**
 * create by lushan on 2020/8/24
 * description: 文章、日志、帖子、影评详情
 */
data class CommunityContent(
    val author: String? = "",//作者
    val body: String? = "",//富文本正文格式
    val contentId: Long = 0L,//内容id
    val covers: List<Cover>? = mutableListOf(),//封面图片
    val createUser: CreateUser? = null,//创建人
    val editor: String? = "",//编辑
    val essence: Boolean = false,//是否加精
    val fcMovie: RoMovie? = null,//影评电影
    val fcPerson: RoPerson? = null,//影评影人
    val fcRating: String? = "",//影评发表这评分
    val fcSubItemRatings: List<MovieSubItemRating>? = null, //影评发表者分项评分列表
    val fcType: Long? = 0,//影评类型 1.长影评 2.短影评
    val group: Group? = null,//群组
    val images: List<Cover>? = mutableListOf(),//图集
    val interactive: Interactive? = null,//点赞、投票  交互数据(已发布才存在)
    val keywords: List<String>? = null,
    val reObjs: List<ReObj>? = mutableListOf(),//关联对象集合 关联对象集合 目前只有电影影人返回 关联文章请调用单独api获取
    val source: String? = "",//来源
    val summary: String? = "",//摘要
    val tags: List<Long>? = mutableListOf(),//1.原创 2.剧透 3.版权声明 4.免责声明
    val title: String? = "",//标题
    val top: Boolean = false,//是否置顶
    val type: Long = 0L,//1.日志 2.帖子 3.影评 4.文章
    val userCreateTime: UserCreateTime? = null,//用户创建时间
    val vote: Vote? = null,//投票
    val commentPmsn: Long? = 0L,//评论权限 1允许任何人 2禁止所有人
    val creatorAuthority: CreatorAuthority? = null,//创作者权限(创作本人才存在)
    val classifies: MutableList<Long>? = null,//分类 VIDEO_REVIEW_CLASSIC(1, "回顾经典"), VIDEO_MOVIE_EXPLAIN(2, "影视解读"), VIDEO_BEHIND_TRIVIA(3, "幕后花絮"), VIDEO_GOOD_RCMD(4, "好物推荐"), VIDEO_PRIVATE_COLLECT(5, "私人收藏"), VIDEO_TRAILER(6, "预告片"),
    val video: UgcVideo? = null,//视频
    val audio: Audio? = null,//音频
    val section: Section? = null//帖子分区
) : ProguardRule {
    companion object {
        const val GROUP_JOIN_UNDO = 0L//未加入
        const val GROUP_JOIN_SUCCESS = 1L//加入成功
        const val GROUP_JOINING = 2L//加入中
        const val GROUP_JOIN_BLACK_NAME = 3L//黑名单
    }

    data class Section(
        val sectionId: Long = 0L,//群组分区id
        val sectionName: String? = ""//群组分区名称
    ) : ProguardRule

    data class Audio(
        var audioId: Long = 0L,//音频id，
        var audioUrl: String? = "",//音频URL
        var audioSec: Long = 0L,//音频时长 单位秒
        var fileSize: Long = 0L//文件大小，单位字节
    ) : ProguardRule

    data class UgcVideo(
        val videoSource: Long = 0L,//视频来源 MOVIE_VIDEO(1, "电影预告片"), SELF_MEDIA_VIDEO(2, "自媒体"), MEDIA_VIDEO(3, "媒资");
        val videoId: Long = 0L,//视频id
        val posterUrl: String? = "",//视频封面url
        val videoSec: Long = 0L,//视频时长单位秒
        val showType: Long = 0L,//视频展示类型 1.横版 2.竖版
        val videoResolutions: ArrayList<VideoPlayUrl>? = arrayListOf()//各种清晰度播放源
    ) : ProguardRule

    data class CreatorAuthority(
        val creatorContentStatus: Long = 0L,//创作者内容状态 DRAFT(0, "草稿"), WAIT_HANDLE(11, "待处理"), WAIT_REVIEW(12, "待审核"), REVIEW_FAIL(13, "审核失败"), RELEASED(20, "已发布"), RELEASED_WAIT_HANDLE(21, "已发布编辑待处理"), RELEASED_WAIT_REVIEW(22, "已发布编辑待审核"), RELEASED_REVIEW_FAIL(23, "已发布编辑审核失败"),
        val reviewFailMsg: String? = "",//审核失败说明
        val btnDetail: BtnShow? = null,//当前详情按钮只有contentId
        val btnCopyPreview: BtnShow? = null,//副本预览按钮
        val btnDelete: BtnShow? = null,// 删除按钮 只有contentId
        val btnCancel: BtnShow? = null,// 撤销按钮(删除并保存为新草稿) 只有contentId
        val btnEdit: BtnShow? = null//编辑按钮
    ) : ProguardRule

    data class BtnShow(
        val contentId: Long = 0L,//内容id
        val recId: Long? = null,//记录id
        val draftAble:Boolean? = false//是否保存为草稿
    ) : ProguardRule

    //    封面
    data class Cover(
        val imageDesc: String? = "",
        val imageFormat: String? = "",//图片格式，是否是gif
        val imageId: String? = "",
        val imageUrl: String? = ""
    ) : ProguardRule

    //    创建人
    data class CreateUser(
        val authType: Long = 0L,//1.个人 2.影评人 3.电影人 4.机构
        val avatarUrl: String? = "",//昵称
        val followed: Boolean = false,//当前用户是否关注该用户
        val nikeName: String? = "",//用户昵称
        val userId: Long = 0L,//用户id
        val fansCount: Long = 0L//粉丝数量
    ) : ProguardRule


    data class Group(
        val id: Long = 0L,//群组id
        val name: String? = "",//群组名称
        val userJoin: Long = 0,//当前用户是否加入此群组 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
        val groupImgUrl: String? = "",//群组封面url
        val memberCount: Long = 0L,//群组人数
        val groupDesc: String? = "",//群组简介
        val groupAuthority: Long? = 0L,//群组的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论");
        val userPostCommentPmsn: Long? = 0L,//当前用户帖子评论权限 1:可评论2:不可评论
        val userGroupRole: Long? = 0L//当前用户家族权限 APPLICANT(-1, "申请者"), OWNER(1, "族长"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");
    ) : ProguardRule {
        companion object {
            const val GROUP_AUTH_JOIN = 1L
            const val GROUP_AUTH_FREE = 2L
            const val GROUP_AUTH_ADMIN = 3L//管理员发帖

            const val USER_GROUP_ROLE_APPLICANT = -1L//申请者
            const val USER_GROUP_ROLE_OWNER = 1L//族长
            const val USER_GROUP_ROLE_ADMINISTRATOR = 2L//管理员
            const val USER_GROUP_ROLE_MEMBER = 3L//普通成员
            const val USER_GROUP_ROLE_BLACKLIST = 4L//普通成员

        }
    }


    //    内容底部交互数据
    data class Interactive(
        val commentCount: Long = 0L,//评论数
        val praiseDownCount: Long = 0L,//点踩数
        val praiseUpCount: Long = 0L,//点赞数
        val userCollected: Boolean? = false,//用户是否收藏
        val userPraised: Long? = 0,//当前用户点赞 1.点赞、2.点踩 null 无
        val userVoted: List<Long>? = mutableListOf(),//用户投票
        val voteStat: VoteStat? = null,//投票统计
        val viewCount:Long = 0L//浏览数
    ) : ProguardRule

    data class ReObj(
        val roMovie: RoMovie? = null,//关联影片
        val roPerson: RoPerson? = null//关联影人
    ) : ProguardRule

    data class UserCreateTime(
        val show: String? = "",//展示时间
        val stamp: Long = 0L//时间戳
    ) : ProguardRule


    //    投票
    data class Vote(
        val opts: List<Opts>? = mutableListOf(),//投票选项
        val multiple: Boolean = false//是否是多选
    ) : ProguardRule

    //    投票选项
    data class Opts(
        val optId: Long = 0L,
        val optDesc: String? = ""
    ) : ProguardRule

    data class VoteStat(
        val count: Long = 0L,//总投票数
        val optCounts: List<OptCount>? = mutableListOf()//选项投票数
    ) : ProguardRule

    data class OptCount(
        val count: Long = 0L,//选项投票数
        val optId: Long = 0L//选项id
    ) : ProguardRule

    data class RoMovie(
        val genreTypes: String? = "",//类型
        val id: Long = 0L,
        val imgUrl: String? = "",
        val minutes: String? = "",//时长：分钟
        val name: String? = "",
        val nameEn: String? = "",//影片中文名称
        val rating: String? = "",//时光评分
        val releaseArea: String? = "",//上映地区
        val releaseDate: String? = "",//上映日期
        var btnShow: Long? = 0L//按钮显示 1.预购 2.购票 3.想看 4.已想看 null 不显示按钮
    ) : ProguardRule

    //    相关影人
    data class RoPerson(
        val birthDate: String? = "",
        val id: Long = 0,
        val imgUrl: String? = "",
        val nameCn: String? = "",
        val nameEn: String? = "",
        val profession: String? = "",//职业
        val rating: String? = ""//时光评分
    ) : ProguardRule
}

