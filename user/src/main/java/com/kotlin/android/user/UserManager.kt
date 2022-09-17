package com.kotlin.android.user

import android.text.TextUtils
import com.kotlin.android.app.data.entity.user.*
import com.kotlin.android.retrofit.cookie.CookieManager

/**
 * 用户管理器
 *
 * Created on 2020/8/14.
 *
 * @author o.s
 */
class UserManager private constructor() {

    companion object {
        val instance by lazy { UserManager() }
    }

    /**
     * 兼容统计回调处理
     */
    var statistics: ((userId: String) -> Unit)? = null

    /**
     * 用户信息
     */
    var user: User? = null
        get() {
            if (field == null) {
                // 初始化用户信息
                field = UserStore.instance.getUser()
            }
            return field
        }

    /**
     * Token
     */
    var token: String = ""
        private set
        get() = CookieManager.instance.token

    /**
     * 获取用户ID
     */
    var userId: Long = 0L
        private set
        get() = user?.userId ?: 0L

    /**
     * 获取用户昵称
     */
    var nickname: String
        set(value) {
            user?.nickname = value
            save()
        }
        get() = user?.nickname ?: ""

    /**
     * 获取用户签名
     */
    var sign: String
        set(value) {
            user?.sign = value
            save()
        }
        get() = user?.sign ?: ""

    /**
     * 获取用户的头像
     */
    var userAvatar: String?
        set(value) {
            user?.headPic = value
            save()
        }
        get() = user?.headPic

    var email: String? = null
        private set
        get() = user?.email

    var userLevelDesc: String? = null
        private set
        get() = user?.userLevelDesc

    var userLevel: Int = 0
        private set
        get() = user?.userLevel ?: 0

    var balance: Double = 0.0
        private set
        get() = user?.balance ?: 0.0

    var mtimeCoin: Int = 0
        private set
        get() = user?.mtimeCoin ?: 0

    var memberIcon: String? = null
        private set
        get() = user?.memberIcon

    var birthday: String?
        set(value) {
            user?.birthday = value
            save()
        }
        get() = user?.birthday

    var bindMobile: String?
        set(value) {
            user?.bindMobile = value
            save()
        }
        get() = user?.bindMobile

    var hasPassword: Boolean
        set(value) {
            user?.hasPassword = value
            save()
        }
        get() = user?.hasPassword ?: false

    var location: UserLocation?
        set(value) {
            user?.location = value
            save()
        }
        get() = user?.location

    /**
     * 获取用户的性别
     */
    var userSex: Int
        set(value) {
            user?.sex = value
            save()
        }
        get() = user?.sex ?: -1

    /**
     * 用户是否绑定手机
     */
    var hasBindMobile: Boolean = false
        private set
        get() = !TextUtils.isEmpty(user?.bindMobile)

    /**
     * 用户是否登录
     */
    var isLogin: Boolean = false
        private set
        get() =
            user != null && CookieManager.instance.isCookieExistByName("_mi_")

    /**
     * 申请认证状态
     */
    var userAuthType: Long = 0L
        private set
        get() = user?.userAuthType ?: 0L

    var userAuthRole: String? = null
        private set
        get() = user?.userAuthRole

    /**
     * 清除本地用户信息
     */
    fun clear() {
        user = null
        UserStore.instance.clear()
        statistics?.invoke("")
    }

    /**
     * 更新用户信息
     */
    fun update(user: User?) {
        this.user = user
        save()
        updateOther()
    }

    private fun updateOther() {
        statistics?.invoke(userId.toString())
    }

    fun update(user: ItemUser, hasPassword: Boolean) {
        if (this.user == null) {
            this.user = User()
        }
        this.user?.apply {
            this.hasPassword = hasPassword
            this.userId = user.userId
            this.nickname = user.nickname
            this.headPic = user.headImg
            this.sex = user.gender
            this.bindMobile = user.mobile
        }
        update(this.user)
    }

    fun update(user: MallUser?) {
        user?.let {
            if (this.user == null) {
                this.user = User()
            }
            this.user?.apply {
                this.userId = it.userId
                this.nickname = it.nickname
                this.headPic = it.headPic
                this.sex = it.sex
                this.balance = it.balance
                this.rechargeMax = it.rechargeMax
                this.bindMobile = it.bindMobile
            }
            update(this.user)
        }
    }

    fun update(user: SwitchUser) {
        if (this.user == null) {
            this.user = User()
        }
        this.user?.apply {
            this.userId = user.userId
            this.nickname = user.nickname
            this.headPic = user.headPic
            this.sex = user.sex
            this.balance = user.balance
            this.bindMobile = user.mobile
            this.userLevel = user.userLevel
            this.birthday = user.birthday
            this.location = user.location
        }
        update(this.user)
    }

    fun save() {
        UserStore.instance.saveUser(this.user)
    }

    /**
     * 加入时光网天数
     */
    var joinDays: Long = 0L
        private set
        get() = user?.joinDays ?: 0L

    /**
     * 标记电影数量
     */
    var markMovieNum: Long = 0L
        private set
        get() = user?.markedMovieCount ?: 0L

}