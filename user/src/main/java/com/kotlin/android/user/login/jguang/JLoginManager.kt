package com.kotlin.android.user.login.jguang

import android.content.Context
import com.kotlin.android.core.ext.getSpValue
import com.kotlin.android.core.ext.putSpValue
import com.kotlin.android.ktx.ext.log.e

/**
 * 登录管理器
 *
 * Created on 2021/3/1.
 *
 * @author o.s
 */
class JLoginManager private constructor() {

    private val ONE_KEY_LOGIN_PROTOCOL = "oneKeyLoginProtocol"

    companion object {
        val instance by lazy { JLoginManager() }
    }

    fun initialize(applicationContext: Context) {
        init(applicationContext) {
            prepare()
        }
        setDebugMode()
    }

    /**
     * 初始化接口。建议在Application的onCreate中调用
     */
    fun init(applicationContext: Context, failure: ((Int) -> Unit)? = null, success: (() -> Unit)? = null) {
        JLoginApi.instance.init(applicationContext, failure, success)
    }

    /**
     * 设置是否开启debug模式。true则会打印更多的日志信息。建议在init接口之前调用。
     */
    fun setDebugMode(isDebug: Boolean = true) {
        JLoginApi.instance.setDebugMode(isDebug)
    }

    /**
     * 获取sdk是否整体初始化成功的标识
     */
    fun isInitSuccess(): Boolean {
        return JLoginApi.instance.isInitSuccess()
    }

    /**
     * 当前网络环境不支持认证
     * 返回true代表可以使用；返回false建议使用其他验证方式。
     */
    fun checkVerifyEnable(): Boolean {
        return JLoginApi.instance.checkVerifyEnable()
    }

    /**
     * 在预定时间内获取当前在线的sim卡所在运营商及token，如果超过所设时间，接口回调返回超时。如果获取成功代表可以用来验证手机号，获取失败则建议做短信验证。
     *
     * timeOut: 超时时间（毫秒）,有效取值范围(0,10000],若小于等于0则取默认值5000.大于10000则取10000.为保证获取token的成功率，建议设置为3000-5000ms.
     *
     * code: 返回码，2000代表获取成功，其他为失败，详见错误码描述
     * content: 成功时为token，可用于调用验证手机号接口。token有效期为1分钟，超过时效需要重新获取才能使用。失败时为失败信息
     * operator: 成功时为对应运营商，CM代表中国移动，CU代表中国联通，CT代表中国电信。失败时可能为null
     */
    fun getToken(
            failure: ((Int) -> Unit)? = null,
            cancel: (() -> Unit) ?= null,
            success: (JLoginToken.() -> Unit)? = null
    ) {
        JLoginApi.instance.getToken(failure, cancel, success)
    }

    /**
     * SDK一键登录预取号
     * sdk会缓存预取号结果，提升之后授权页拉起速度。所以建议拉起授权页前，比如在开屏页或者业务入口页预先调用此接口进行预取号。
     * 请求成功后，不要频繁重复调用。
     * 不要在预取号回调中重复调用预取号或者拉起授权页接口。
     *
     * timeOut: 超时时间（毫秒）,有效取值范围(0,10000],若小于等于0则取默认值5000.大于10000则取10000, 为保证预取号的成功率，建议设置为3000-5000ms.
     *
     * code: 返回码，7000代表获取成功，其他为失败，详见错误码描述
     * content: 调用结果信息描述
     */
    fun preLogin(failure: ((Int) -> Unit)? = null, success: (() -> Unit)? = null) {
        JLoginApi.instance.preLogin()
    }

    /**
     * SDK清除预取号缓存
     */
    fun clearPreLoginCache() {
        JLoginApi.instance.clearPreLoginCache()
    }

    /**
     * 准备工作
     */
    fun prepare() {
        clearPreLoginCache()
        preLogin()
    }

    /**
     * SDK自定义授权页面UI样式
     *
     * 修改授权页面主题，开发者可以通过 setCustomUIWithConfig 方法修改授权页面主题，需在 loginAuth 接口之前调用
     * uiConfig：主题配置对象，开发者在JVerifyUIConfig.java类中调用对应的方法配置授权页中对应的元素
     */
    fun setCustomUIWithConfig(
            userAgreementUrl: String,
            privacyPolicyUrl: String,
            otherLogin: (() -> Unit)? = null
    ) {
        JLoginApi.instance.setCustomUIWithConfig(
                userAgreementUrl = userAgreementUrl,
                privacyPolicyUrl = privacyPolicyUrl,
                isSelectedPrivacy = checkboxState,
                otherLogin = otherLogin
        )
    }

    /**
     * SDK请求授权一键登录（新）
     * 一键登录需要依赖预取号结果，如果没有预取号，一键登录时会自动预取号。
     * 建议拉起授权页前，比如在开屏页或者业务入口页预先调用此接口进行预取号，可以提升授权页拉起速度，优化体验。
     * 一键登录请求成功后，不要频繁重复调用。运营商会限制单位时间内请求次数。
     * 不要在一键登录回调中重复调用预取号或者拉起授权页接口。
     *
     * settings: 登录接口设置项。
     * listener: 登录授权结果回调
     *
     * code: 返回码，6000代表loginToken获取成功，6001代表loginToken获取失败，其他返回码详见描述
     * content: 返回码的解释信息，若获取成功，内容信息代表loginToken。
     * operator: 成功时为对应运营商，CM代表中国移动，CU代表中国联通，CT代表中国电信。失败时可能为null
     *
     * [event]: 点击回调
     * [failure]: 拉取授权登录页失败
     * [cancel]: 取消授权登录页
     * [success]: 点击一键登录按钮，授权登录loginToken获取成功
     */
    fun loginAuth(
//            event: ((Int) -> Unit)? = null,
            failure: ((Int) -> Unit)? = null,
            cancel: (() -> Unit)? = null,
            success: (JLoginToken.() -> Unit)? = null
    ) {
        "JLoginManager loginAuth 开始拉取授权登录页".e()
        if (!isInitSuccess() || !checkVerifyEnable()) {
            "拉取授权登录页面失败：sdk未初始化或当前网络环境不支持认证".e()
            failure?.invoke(-1)
            return
        }
        JLoginApi.instance.loginAuth(
                event = {
                    saveCheckboxState(it)
                },
                failure = failure,
                cancel = cancel,
                success = success)
    }

    /**
     * 关闭登录授权页，如果当前授权正在进行，则loginAuth接口会立即触发6002取消回调。
     *
     * needCloseAnim: 是否需要展示默认授权页关闭的动画（如果有）。true - 需要，false - 不需要
     *
     * code: 返回码，0 标识成功关闭授权页
     * desc: 返回码的描述信息。
     */
    fun dismissLoginAuthActivity(
            failure: ((Int) -> Unit)? = null,
            success: (() -> Unit)? = null
    ) {
        JLoginApi.instance.dismissLoginAuthActivity(failure, success)
    }

    /**
     * SDK获取验证码
     * 获取短信验证码，使用此功能需要在Portal控制台中极光短信模块添加短信签名和验证码短信模版，或者使用默认的签名或模版。详见：操作指南
     * 通过此接口获得到短信验证码后，需要调用极光验证码验证API来进行验证，详见：验证码验证 API
     *
     * phonenum：电话号码
     * signid：短信签名id，如果为null，则为默认短信签名id
     * tempid：短信模板id，如果为null，则为默认短信模板id
     * listener：回调接口
     *
     * code: 返回码，3000代表获取验证码成功，msg为此次获取的唯一标识码(uuid)，其他为失败，详见错误码描述
     * msg：结果描述
     */
    fun getSmsCode(
            phoneNum: String,
            failure: ((Int) -> Unit)? = null,
            success: (String) -> Unit
    ) {
        JLoginApi.instance.getSmsCode(phoneNum, failure, success)
    }

    /**
     * SDK设置前后两次获取验证码的时间间隔
     * 设置前后两次获取验证码的时间间隔，默认 30000ms，有效范围(0,300000)
     * 参数说明：intervalTime：时间间隔，单位是毫秒(ms)。
     */
    fun setSmsIntervalTime(intervalTime: Long = 60_000) {
        JLoginApi.instance.setSmsIntervalTime(intervalTime)
    }

    /**
     * 保存协议勾选框状态
     * 本地不要保存勾选状态
     */
    fun saveCheckboxState(it: Int) {
//        when (it) {
//            6 -> {
//                // 选中
//                putSpValue(ONE_KEY_LOGIN_PROTOCOL, true)
//            }
//            7 -> {
//                // 取消选中
//                putSpValue(ONE_KEY_LOGIN_PROTOCOL, false)
//            }
//        }
    }

    /**
     * 获取协议勾选框状态
     */
    val checkboxState: Boolean
        get() = getSpValue(ONE_KEY_LOGIN_PROTOCOL, false)
}