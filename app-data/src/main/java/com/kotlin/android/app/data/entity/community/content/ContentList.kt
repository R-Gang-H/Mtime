package com.kotlin.android.app.data.entity.community.content

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.CommContent

data class ContentList(
    val nextStamp: String? = "",
    val pageSize: Long = 0L,
    val hasNext: Boolean,
    val items: List<Item>? = mutableListOf(),
) : ProguardRule {

    data class Item(
        val userCreateTime: UserCreateTime?,
        val fcRating: String? = "",
        val images: List<CommContent.Image>?,
        val essence: Boolean,
        val interactive: CommContent.Interactive? = null,
        val contentId: Long? = 0L,
        val type: Long? = 0L,
        val title: String? = "",
        val fcMovie: CommContent.FcMovie? = null,
        val creatorAuthority: CommunityContent.CreatorAuthority? = null,
        val top: Boolean,
        val video: Video?,
        val mixVideos: List<CommContent.MixVideo>,
        val createUser: CreateUser? = null,
        val fcPerson: CommContent.FcPerson? = null,
        val fcSubItemRatings: List<FCSubItemRatings>?,
        val vote: CommContent.Vote? = null,
        val mixImages: List<CommContent.Image>,
        val mixWord: String? = "",
        val fcType: Long? = 0L,
        val group: Group? = null
    ) : ProguardRule

    data class UserCreateTime(val show: String? = "", val stamp: Long? = 0L) : ProguardRule

    data class CreateUser(
        val authRole: String? = "",
        val gender: Long? = 0L,
        val avatarUrl: String? = "",
        val userSign: String? = "",
        val nikeName: String? = "",
        val authType: Long? = 0L,
        val userId: Long? = 0L
    ) : ProguardRule

    data class FCSubItemRatings(
        var index: Long = 0,
        var rating: Float? = 0.0f,
        var title: String? = ""
    ) : ProguardRule

    data class Group(
        val groupImgUrl: String? = "",
        val groupDesc: String? = "",
        val memberCount: Long? = 0L,
        val name: String? = "",
        val id: Long? = 0L,
        val groupAuthority: Long? = 0L
    ) : ProguardRule

    data class Video(val videoSec: Long = 0L) : ProguardRule

}