package com.kotlin.android.community.ui.person.bean

import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.constant.CommConstant.USER_AUTH_TYPE_MOVIE_PERSON
import com.kotlin.android.app.data.constant.CommConstant.USER_AUTH_TYPE_ORGANIZATION
import com.kotlin.android.app.data.constant.CommConstant.USER_AUTH_TYPE_REVIEW_PERSON
import com.kotlin.android.app.data.entity.community.person.OngoingMedalInfo
import com.kotlin.android.community.R
import com.kotlin.android.community.ui.person.*
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getDrawable
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.user.UserStore

class UserHomeViewBean(
    val albumCount: Long = 0,
    val articleCount: Long = 0,
    val authType: Long = 2L,//用户认证类型 PERSONAL(1, "个人"), FILM_CRITIC(2, "影评人"), FILM_MAKER(3, "电影人"), INSTITUTION(4, "机构");
    val avatarUrl: String? = "",
    val fansCount: Long = 0,
    val filmCommentCount: Long = 0,
    val followCount: Long = 0,
    var followed: Boolean = false,
    val gender: Int = 0,
    val groupCount: Long = 0,
    val hasSeenCount: Long = 0,
    val info: String? = "",
    val journalCount: Long = 0,
    val nikeName: String?,
    val postCount: Long = 0,
    val registDuration: String? = "",
    val showArticle: Boolean = false,
    val userId: Long = 0,
    val wantSeeCount: Long = 0,
    val level: Long = 0L,
    val status: Long = 0L,//用户状态 PUBLIC(1, "已公开"), SHIELDED(2, "已屏蔽"), LOCK(3, "锁定"), INVALID(4, "失效"), SECURITYRISK(5, "存在安全风险"), NEEDRESETPASSWORD(6, "需要重置密码")
    val authRole: String? = "",//认证角色，"电影人"认证类型 比如“认证 导演”“认证 演员”“认证 编剧”
    val imId: String? = "",//IM即时通讯Id
    val backgroundAppUrl: String? = "",//APP 背景图
    val videoCount: Long = 0,//视频数
    val filmListCount: Long = 0,//片单数
    val audioCount: Long = 0,//音频数
    val showAudio: Boolean = false,//是否显示音频标签
    val creator: Boolean = false,//是否是创作者标签
    var praiseCount: Long? = 0L,//总点赞量
    var collectCount: Long? = 0L,//总收藏量
    var commentCount: Long? = 0L,//总评论量
    var viewsCount: Long? = 0,//总阅读量
    var ongoingMedalInfos: List<OngoingMedalInfo>?,//已完成勋章集合，最新top5
    var creatorAppLogoUrl: String?,//创作者等级图片

) : ProguardRule {

    var praiseCountDes: String = ""
        get() = formatCount(praiseCount ?: 0)
    var collectCountDes: String = ""
        get() = formatCount(collectCount ?: 0)
    var commentCountDes: String = ""
        get() = formatCount(commentCount ?: 0)

    var fansCountDes: String = ""
        get() {
            return formatCount(fansCount)
        }
    var followCountDes: String = ""
        get() {
            return formatCount(followCount)
        }
    var authRoleContentStatus: Int = View.GONE
        get() = when (authType) {
            USER_AUTH_TYPE_REVIEW_PERSON -> View.VISIBLE//影评
            USER_AUTH_TYPE_MOVIE_PERSON -> View.VISIBLE//电影人
            USER_AUTH_TYPE_ORGANIZATION -> View.VISIBLE//机构
            else -> View.GONE

        }
    var authRoleContent: String = ""
        get() = when (authType) {
            USER_AUTH_TYPE_REVIEW_PERSON -> getString(
                R.string.community_auth,
                getString(R.string.community_cir_person)
            )//影评
            USER_AUTH_TYPE_MOVIE_PERSON -> getString(R.string.community_auth, authRole ?: "")//电影人
            USER_AUTH_TYPE_ORGANIZATION -> getString(
                R.string.community_auth,
                getString(R.string.community_jigou)
            )//机构
            else -> ""
        }
    var infoStatus: Int = if (info?.isNullOrEmpty() == true) View.GONE else View.VISIBLE
    var iconForGender: Drawable? = getDrawable(R.drawable.ic_male)
        get() {
            return when (gender) {
                USER_GENDER_MALE -> getDrawable(R.drawable.ic_male)
                USER_GENDER_FEMALE -> getDrawable(R.drawable.ic_femal)
                else -> getDrawable(R.drawable.ic_male)
            }
        }
    var isSelf: Boolean = false
        get() {
            return UserStore.instance.getUser()?.userId == userId
        }
    var levelIcon: Drawable? = getDrawable(R.drawable.ic_femal)
        get() {
            return when (level) {
                USER_LEVEL_RUMEN -> {//入门
                    getDrawable(R.drawable.ic_rumen)
                }
                USER_LEVEL_ZHONGJI -> {//中级
                    getDrawable(R.drawable.ic_zhongji)
                }
                USER_LEVEL_GAOJI -> {//高级
                    getDrawable(R.drawable.ic_gaoji)
                }
                USER_LEVEL_ZISHEN -> {//资深
                    getDrawable(R.drawable.ic_zishen)
                }
                USER_LEVEL_DIANTANG -> {//殿堂
                    getDrawable(R.drawable.ic_diantang)
                }
                else -> {//入门
                    getDrawable(R.drawable.ic_rumen)
                }
            }
        }

    var levelContext: String = ""
        get() {
            return when (level) {
                USER_LEVEL_RUMEN -> getString(R.string.rumen)
                USER_LEVEL_ZHONGJI -> getString(R.string.zhongji)
                USER_LEVEL_GAOJI -> getString(R.string.gaoji)
                USER_LEVEL_ZISHEN -> getString(R.string.zishen)
                USER_LEVEL_DIANTANG -> getString(R.string.diantang)
                else -> getString(R.string.rumen)
            }
        }

    @ColorInt
    var levelBg: Int = getColor(R.color.color_2ab5e1)
        get() {
            return when (level) {
                USER_LEVEL_RUMEN -> getColor(R.color.color_2ab5e1)
                USER_LEVEL_ZHONGJI -> getColor(R.color.color_36c096)
                USER_LEVEL_GAOJI -> getColor(R.color.color_90d959)
                USER_LEVEL_ZISHEN -> getColor(R.color.color_feb12a)
                USER_LEVEL_DIANTANG -> getColor(R.color.color_ff5a36)
                else -> getColor(R.color.color_c2a25c)
            }
        }
    var authStatus: Int = View.GONE
        get() {
            return when (authType) {
                USER_AUTHTYPE_DIANYINGREN, USER_AUTHTYPE_YINGPINGREN, USER_AUTHTYPE_JIGOU -> View.VISIBLE
                else -> View.GONE
            }
        }
    var authIcon: Drawable? = null
        get() = when (authType) {
            USER_AUTHTYPE_DIANYINGREN, USER_AUTHTYPE_YINGPINGREN -> {//影人
                getDrawable(R.drawable.ic_yingren)
            }
            USER_AUTHTYPE_JIGOU -> {//机构
                getDrawable(R.drawable.ic_jigou)
            }
            else -> null
        }


    @ColorInt
    var authBg: Int = getColor(R.color.color_2ab5e1)
        get() {
            return when (authType) {
                USER_AUTHTYPE_DIANYINGREN, USER_AUTHTYPE_YINGPINGREN -> getColor(R.color.color_2ab5e1)
                USER_AUTHTYPE_JIGOU -> getColor(R.color.color_feb12a)
                else -> getColor(R.color.color_feb12a)
            }
        }

    var authContext: String = ""
        get() {
            return when (authType) {
                USER_AUTHTYPE_DIANYINGREN -> getString(R.string.filmer_auth)
                USER_AUTHTYPE_YINGPINGREN -> getString(R.string.filmer_auth_post)
                USER_AUTHTYPE_JIGOU -> getString(R.string.construction_auth)
                else -> getString(R.string.construction_auth)
            }
        }

    //关注文本
    var followContext = ""
        get() {
            return if (!followed) getString(R.string.attend) else getString(R.string.attended)
        }

}