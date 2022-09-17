package com.kotlin.android.app.data.entity.user

import com.kotlin.android.app.data.ProguardRule
import java.io.Serializable

/**
 * 用户信息
 * 参见：AccountDetailBean
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
data class User(
        var nickname: String? = null,
        var headPic: String? = null,
        var balance: Double = 0.0,
        var rechargeMax: Long = 0,
        var bindMobile: String? = null,
        val email: String? = null,
        var sex: Int = 0,
        var userLevel: Int = 0,     // 性别 1 男，2 女 ，3 保密
        val vipUrl: String? = null,
        var userId: Long = 0, //是否是第三方授权登录
        val isAuthLogin: Boolean = false,
        val mobile: String? = null, //用户注册手机号
        var hasPassword: Boolean = false,
        var birthday: String? = null,
        var location: UserLocation? = null, // 居住地
        val userLevelDesc: String? = null, // 等级描述 如：黄金会员
        val mtimeCoin: Int = 0, // 时光币
        val hasGiftPack: Boolean = false, // 是否有礼包
        val memberIcon: String? = null, //会员显示文案，是否签到显示签到抽好礼，是否有礼包显示会员礼包待领取，前者显示优先级大于后者
        var sign: String? = null,        // 用户签名（自我介绍）,有可能为null或空值
        var isModifiedSex:Boolean = false,//是否修改过性别
        var userAuthType:Long? = 0L,//用户认证类型：null代表没有认证， 1"个人", 2"影评人", 3"电影人", 4"机构", -1“审核中”;
        var joinDays:Long? = 0L,//加入天数 (如果为0，说明用户数据不完整，勿展示)
        var markedMovieCount:Long? = 0L, //标记影片数量（看过加想看）(如果为0，说明用户没有数据，勿展示)
        var authorizeUrl: String? = null,       // 针对H5使用第三方授权登录调度页
        var dataEncryption: String? = null,     // userId加密
        var userAuthRole: String? = null,       // 认证角色，"电影人"认证类型 比如“认证 导演”“认证 演员”“认证 编剧”
        var backgroundAppPic: String? = null,   // APP 背景图
        var wantSeeCount:Long? = 0L,            // 想看总数
        var hotFilmWantSeeCount:Long? = 0L,     // 想看电影中正在热映的电影总数
        var watchedCount:Long? = 0L,            // 看过电影总数
        var ratingWaitCount:Long? = 0L,         // 看过电影中为评分的电影总数
        var favoriteCount:Long? = 0L,           // 收藏数
) : Serializable, ProguardRule {

//    var sex: Int = 0
//        set(value) {
//            field = if (value == 0) {
//                2
//            } else {
//                value
//            }
//        }

    companion object {
        private const val serialVersionUID = 516840708041254691L
    }
}