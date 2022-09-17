package com.kotlin.android.app.data.entity.mine

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.app.data.entity.mine
 * @ClassName:      DataCenterBean
 * @Description:    创作者数据中心
 * @Author:         haoruigang
 * @CreateDate:     2022/4/6 14:42
 */
class DataCenterBean {

    /**
     * 整体概览
     */
    data class EarthBean(
            val collectCount: Long = 0L, // 总收藏数
            val commentCount: Long = 0L, // 总评论数
            val statisticsInfos: List<StatisticsInfo> = listOf(),
            val upCount: Long = 0L, // 总点赞数
            val userId: Long = 0L, // 创作者用户ID
            val viewsCount: Long = 0L, // 总浏览数
            val contentCount: Long = 0L, // 总内容数
    ) : ProguardRule {
        data class StatisticsInfo(
                val collectCount: Long = 0, // 收藏数
                val commentCount: Long = 0, // 评论数
                val time: String, // 汇总统计时间,按天汇总 YYYYMMDD,按月汇总 YYYYMM,按年汇总 YYYY
                val upCount: Long = 0, // 点赞数
                val viewsCount: Long = 0, // 浏览数
                val contentCount: Long = 0, // 内容数
        ) : ProguardRule
    }

    /**
     * 单篇分析
     */
    data class SingleAnalysisBean(
            val hasNext: Boolean = false,
            val nextStamp: String? = null,
            val pageSize: Long = 0,
            val items: List<Item>? = listOf(),
    ) : ProguardRule {
        data class Item(
                val contentId: Long = 0,
                val createUser: CreateUser = CreateUser(),
                val essence: Boolean = false,
                val fcMovie: FcMovie? = FcMovie(),
                val fcPerson: FcPerson = FcPerson(),
                val fcRating: String = "",
                val fcSubItemRatings: List<FcSubItemRating> = listOf(),
                val fcType: Long = 0,
                val group: Group = Group(),
                val images: List<Image> = listOf(),
                val interactive: Interactive = Interactive(), // 交互数据(已发布才存在)
                val mixImages: List<MixImage> = listOf(),
                val video: Video = Video(),
                val mixVideos: List<MixVideo> = listOf(),
                val mixWord: String = "",
                val title: String = "", // 标题
                val top: Boolean = false,
                val type: Long = 0, // 内容类型 JOURNAL(1, "日志"), POST(2, "帖子"), FILM_COMMENT(3, "影评"), ARTICLE(4, "文章"); VIDEO(5, "视频"), AUDIO(6, "音频"),
                val userCreateTime: UserCreateTime = UserCreateTime(),  // 用户创建时间
                val vote: Vote = Vote(),
        ) : Serializable, ProguardRule {
            data class CreateUser(
                    val authRole: String = "",
                    val authType: Long = 0,
                    val avatarUrl: String = "",
                    val gender: Long = 0,
                    val nikeName: String = "",
                    val userId: Long = 0,
            ) : Serializable, ProguardRule

            data class FcMovie(
                    val genreTypes: String = "",
                    val id: Long = 0,
                    val imgUrl: String = "",
                    val minutes: String = "",
                    val name: String = "",
                    val nameEn: String = "",
                    val rating: String = "",
                    val releaseArea: String = "",
                    val releaseDate: String = "",
            ) : Serializable, ProguardRule

            data class FcPerson(
                    val birthDate: String = "",
                    val id: Long = 0,
                    val imgUrl: String = "",
                    val nameCn: String = "",
                    val nameEn: String = "",
                    val profession: String = "",
            ) : Serializable, ProguardRule

            data class FcSubItemRating(
                    val index: Long = 0,
                    val rating: Double = 0.0,
                    val title: String = "",
            ) : Serializable, ProguardRule

            data class Group(
                    val groupAuthority: Long = 0,
                    val groupDesc: String = "",
                    val groupImgUrl: String = "",
                    val id: Long = 0,
                    val memberCount: Long = 0,
                    val name: String = "",
            ) : Serializable, ProguardRule

            data class Image(
                    val imageDesc: String = "",
                    val imageFormat: String = "",
                    val imageId: String = "",
                    val imageUrl: String = "",
                    val isGif: Boolean = false,
            ) : Serializable, ProguardRule

            data class Interactive(
                    val commentCount: Long = 0, // 评论数
                    val praiseDownCount: Long = 0, // 点踩数
                    val praiseUpCount: Long = 0, // 点赞数
                    val userPraised: Long = 0, // 当前用户赞：1:点赞 2点踩 null:无(未操作或当前用户未登录)
                    val userVoted: List<Long> = listOf(),
                    val viewCount: Long = 0L, // 浏览数
                    val collectCount: Long = 0L, // 浏览数
                    val voteStat: VoteStat = VoteStat(), // 投票统计
            ) : Serializable, ProguardRule {
                data class VoteStat(
                        val count: Long = 0, // 总投票数
                        val optCounts: List<OptCount> = listOf(), // 选项投票数
                ) : Serializable, ProguardRule {
                    data class OptCount(
                            val count: Long = 0, // 选项投票数
                            val optId: Long = 0, // 选项Id
                    )
                }
            }

            data class MixImage(
                    val imageDesc: String = "",
                    val imageFormat: String = "",
                    val imageId: String = "",
                    val imageUrl: String = "",
                    val isGif: Boolean = false,
            ) : Serializable, ProguardRule

            data class Video(
                    val videoSec: Long = 0L,
            ) : Serializable, ProguardRule

            data class MixVideo(
                    val posterUrl: String = "",
                    val vId: Long = 0,
                    val vSource: Long = 0,
            ) : Serializable, ProguardRule

            data class UserCreateTime(
                    val show: String = "", // Unix时间戳:毫秒
                    val stamp: Long = 0, // 展示 默认格式:yyyy-MM-dd HH:mm:ss
            ) : Serializable, ProguardRule

            data class Vote(
                    val multiple: Boolean = false,
                    val opts: List<Opt> = listOf(),
            ) : Serializable, ProguardRule {
                data class Opt(
                        val optDesc: String = "",
                        val optId: Long = 0,
                ) : Serializable, ProguardRule
            }
        }
    }

}