package com.kotlin.android.app.data.entity.review

import com.kotlin.android.app.data.ProguardRule

data class MovieReview(
    var nickname: String ?= "",                 // 发表者昵称
    var userImage: String ?= "",                // 发表者头像（原图）
    var rating: String ?= "",                   // 发表者评分
    var content: String ?= "",                  // 影评内容
    var memberLevel: Long ?= 0,                 // 会员等级：0入门影迷，1中级影迷，2高级影迷，3资深影迷，4殿堂影迷
    var isTicket: Boolean ?= false,             // 当前登录用户是否购买了影评所属影片的电影票
    var userRelation: Long ?= 0,                // 当前登录用户与当前影评发表者的关系：0没有关系，1好友（双方互相关注），2粉丝（对方单项关注了自己），3已关注（自己关注了对方）
    var praiseDownCount: Long = 0,              // 点踩数（长影评才有）
    var isPraiseDown: Boolean ?= false,         // 当前用户是否点踩（长影评才有）
    var userId: Long ?= 0,                      // 发表者Id
    var attitude: Long ?= 0,                    // 发表者对电影态度：-1未表态 0看过 1想看
    var praiseCount: Long = 0,                  // 点赞数
    var isPraise: Boolean ?= false,             // 当前用户是否点赞
    var commentCount: Long ?= 0,                // 回复数
    var commentTime: Long ?= 0,                 // 发表时间（时间戳，单位秒）
    var mvpType: Long ?= 0,                     // 认证类型：1个人，2影评人，3电影人，4机构
    var commentId: Long ?= 0,                   // 影评Id（如果在未审核列表里就是未审核记录Id）
    var title: String ?= "",                    // 影评标题（长影评才有）
    var isWantSee: Boolean ?= false,            // 是否想看
    var location: String ?= "",                 // 发表者所在地区名（取不到）

    // 自定义字段
    var isAudit: Boolean = false                // 是否待审核：true是 false否

): ProguardRule {
    companion object {

        /**
         * 当前登录用户与当前影评发表者的关系
         * 0没有关系，1好友（双方互相关注），2粉丝（对方单向关注了自己），3已关注（自己单向关注了对方）
         */
        const val USER_RELATION_NOTHTING = 0L
        const val USER_RELATION_FRIEND = 1L
        const val USER_RELATION_FANS = 2L
        const val USER_RELATION_FOLLOW = 3L

    }
}