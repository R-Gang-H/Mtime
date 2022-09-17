package com.kotlin.android.ugc.detail.component.bean

import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOINING
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_BLACK_NAME
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_SUCCESS
import com.kotlin.android.app.data.entity.community.content.CommunityContent.Companion.GROUP_JOIN_UNDO
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.ugc.detail.component.R

/**
 * create by lushan on 2020/8/13
 * description: 话题，家族标题
 */
data class TopicFamilyViewBean(var familyId: Long = 0L,//家族id
                               var familyName: String = "",//家族名称
                               var familyPic: String = "",//家族头像
                               var familyCount: Long = 0L,//成员数
                               var familyRule: String = "",//家族公告
                               var familyStatus: Long = 0,//当前家族加入状态 当前用户是否加入此群组 0:未加入1:已加入成功2:加入中（待审核）3:黑名单
                               var familyPostStatus: Long = 0L,//群组的发帖和评论的权限 JOIN_POST_JOIN_COMMENT(1, "加入发帖加入评论"), FREE_POST_FREE_COMMENT(2, "自由发帖自由评论"), ADMIN_POST_FREE_COMMENT(3, "管理员发帖自由评论");
                               var userPostCommentPmsn: Long? = 0L,//当前用户帖子评论权限 1:可评论2:不可评论
                               var userGroupRole: Long? = 0L//当前用户家族权限 APPLICANT(-1, "申请者"), OWNER(1, "族长"), ADMINISTRATOR(2, "管理员"), MEMBER(3, "普通成员"), BLACKLIST(4, "黑名单");

) : ProguardRule {


    fun setJoinFamilyStatus(status: Long) {
        familyStatus = if (status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED) CommunityContent.GROUP_JOIN_SUCCESS else GROUP_JOINING
        if (status == CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED) {
            familyCount++
        }
    }

    fun setExitFamilyStatus(status: Long) {
        familyStatus = if (status == 1L) {//退群成功
            GROUP_JOIN_UNDO
        } else {
            familyStatus
        }
    }

    fun isBlack(): Boolean = familyStatus == GROUP_JOIN_BLACK_NAME

    /**
     * 未加入到家族
     */
    fun isUnJoinFamily(): Boolean = familyStatus == GROUP_JOIN_UNDO

    /**
     * 加入成功
     */
    fun isJoinSuccess(): Boolean = familyStatus == GROUP_JOIN_SUCCESS

    /**
     * 加入按钮显示文案
     */
    fun getJoinContent(): String {
        return when (familyStatus) {
            GROUP_JOINING -> getString(R.string.ugc_group_joining)//加入中
            else -> getString(R.string.ugc_detail_family_join)//加入或者黑名单显示文案
        }
    }

    /**
     * 获取成员数量
     */
    fun getFamilyCountContent(): String {
        return getString(R.string.ugc_family_member_count_format).format(formatCount(if (familyCount < 0) 0 else familyCount))
    }
}