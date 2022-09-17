package com.kotlin.android.user.login.jguang

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import cn.jiguang.verifysdk.api.AuthPageEventListener
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jiguang.verifysdk.api.JVerifyUIConfig
import cn.jiguang.verifysdk.api.LoginSettings
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.toDP
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.user.R

/**
 * 极光一键登录API
 *
 * Created on 2021/3/3.
 *
 * @author o.s
 */
class JLoginApi private constructor() {

    companion object {
        val instance by lazy { JLoginApi() }
    }

    private var context: Context? = null
    private var isInit = false
    var loginToken: JLoginToken? = null

    /**
     * 初始化接口。建议在Application的onCreate中调用
     * [applicationContext] Application Context
     */
    fun init(applicationContext: Context, failure: ((Int) -> Unit)? = null, success: (() -> Unit)? = null) {
        context = applicationContext
        // debug log
        JVerificationInterface.init(applicationContext, 30_000) { code, msg ->
            "一键登录初始化：code=$code, msg=$msg".e()
            if (code == 8000) {
                isInit = true
                // 初始化成功
                success?.invoke()
            } else {
                // 跳转至一般登录页面
                failure?.invoke(code)
            }
        }
    }

    /**
     * 设置是否开启debug模式。true则会打印更多的日志信息。建议在init接口之前调用。
     */
    fun setDebugMode(isDebug: Boolean = true) {
        JVerificationInterface.setDebugMode(isDebug)
    }

    /**
     * 获取sdk是否整体初始化成功的标识
     */
    fun isInitSuccess(): Boolean {
        return JVerificationInterface.isInitSuccess()
    }

    /**
     * 当前网络环境不支持认证
     * 返回true代表可以使用；返回false建议使用其他验证方式。
     */
    fun checkVerifyEnable(): Boolean {
        if (!ensureInit) {
            return false
        }
        return JVerificationInterface.checkVerifyEnable(context)
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
        if (!ensureInit) {
            return
        }
        JVerificationInterface.getToken(context, 8000) { code, content, operator ->
            "getToken code=$code, content=$content, operator=$operator".e()
            loginToken = JLoginToken(code, content, operator)
            when (code) {
                2000 -> {
                    // 【成功】 2000	内容为token	                获取token成功
                    loginToken?.run {
                        success?.invoke(this)
                    }
                }
                2001 -> {
                    // 【失败】 2001	fetch token failed	        获取token失败
                    failure?.invoke(code)
                }
                2008 -> {
                    // 2008	Token requesting, please try again later	正在获取token中，稍后再试
                    failure?.invoke(code)
                }
                2009 -> {
                    // 2009	verifying, please try again later	正在认证中，稍后再试
                    failure?.invoke(code)
                }
                6002 -> {
                    // 取消
                    cancel?.invoke()
                }
                6004 -> {
                    // 登录中，连续点击请求拉取一键登录页会出现6004，啥都不做即可
                    failure?.invoke(code)
                }
                else -> {
                    failure?.invoke(code)
                }
            }
        }
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
    fun preLogin(failure: ((Int) -> Unit) ?= null, success: (() -> Unit) ?= null) {
        if (!ensureInit) {
            return
        }
        JVerificationInterface.preLogin(context, 8000) { code, content ->
            "preLogin code=$code, content=$content".e()
            if (code == 7000) {
                success?.invoke()
            } else {
                failure?.invoke(code)
            }
        }
    }

    /**
     * SDK清除预取号缓存
     */
    fun clearPreLoginCache() {
        JVerificationInterface.clearPreLoginCache()
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
     */
    fun loginAuth(
            event: ((Int) -> Unit)? = null,
            failure: ((Int) -> Unit)? = null,
            cancel: (() -> Unit) ?= null,
            success: (JLoginToken.() -> Unit)? = null
    ) {
        "JLoginApi loginAuth 开始拉取授权登录页".e()
        if (!ensureInit) {
            return
        }
        val settings = LoginSettings().apply {
            isAutoFinish = false // 设置登录完成后是否自动关闭授权页
            timeout = 15_000 // 设置超时时间，单位毫秒。 合法范围（0，30000],范围以外默认设置为10000
            authPageEventListener = object : AuthPageEventListener() {
                override fun onEvent(code: Int, content: String?) {
                    "loginAuth AuthPageEventListener code=$code, content=$content".e()
                    event?.invoke(code)
                }
            }
        }
        JVerificationInterface.loginAuth(context, settings) { code, content, operator ->
            "loginAuth code=$code, content=$content, operator=$operator".e()
            loginToken = JLoginToken(code, content, operator)
            when (code) {
                6000 -> {
                    // 成功
                    loginToken?.run {
                        success?.invoke(this)
                    }
                }
                6001 -> {
                    // 【失败】 6001	fetch loginToken failed	    获取loginToken失败
                    failure?.invoke(code)
                }
                6002 -> {
                    // 【取消】 6002	fetch loginToken canceled	用户取消获取loginToken
                    cancel?.invoke()
                }
                6004 -> {
                    // 【登录中】 6004	authorization requesting, please try again later	正在登录中，稍后再试 (连续点击请求拉取一键登录页会出现6004，啥都不做即可)
                    failure?.invoke(code)
                }
                6006 -> {
                    // 【重新取号】 6006	prelogin scrip expired.	    预取号结果超时，需要重新预取号
                    failure?.invoke(code)
                }
                else -> {
                    failure?.invoke(code)
                }
            }
        }
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
        if (!ensureInit) {
            return
        }
        JVerificationInterface.dismissLoginAuthActivity(true) { code, desc ->
            "dismissLoginAuthActivity code=$code, desc=$desc".e()
            if (code == 0) {
                // 关闭授权页面成功
                success?.invoke()
            } else {
                failure?.invoke(code)
            }
        }
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
            isSelectedPrivacy: Boolean = false,
            otherLogin:(() -> Unit)? = null
    ) {
        "setCustomUIWithConfig".e()
        if (!ensureInit) {
            return
        }
        "setCustomUIWithConfig ensureInit".e()
        // 登录按钮宽度
        val logBtnWidth = screenWidth.toDP - 40
        val config = JVerifyUIConfig.Builder()
                /**
                 * 设置授权页背景
                 * 说明：图片会默认拉伸铺满整个屏幕，适配不同尺寸手机，建议使用 .9.png 图片来解决适配问题。
                setAuthBGImgPath	String	设置背景图片
                setAuthBGGifPath	String	设置本地gif背景图片，需要放置到drawable文件中，传入图片名称即可
                setAuthBGVideoPath	String,String	设置背景Video文件路径:(支持本地路径如："android.resource://" + context.getPackageName() + "/" + R.raw.testvideo
                支持网络路径(建议下载到本地后使用本地路径，网络路径会出现卡顿等网络问题)如："https://xxx")，
                设置默认第一频图片:(需要放置到drawable文件中，传入图片名称即可,如果Video文件路径为本地，则可以填null)
                 */
                .setAuthBGImgPath(getString(R.string.auth_bg_img_path))
//                .setAuthBGGifPath("")
//                .setAuthBGVideoPath("")

                /**
                 * 状态栏
                setStatusBarColorWithNav	boolean	设置状态栏与导航栏同色。仅在android 5.0以上设备生效。 since 2.4.1
                setStatusBarDarkMode	boolean	设置状态栏暗色模式。仅在android 6.0以上设备生效。 since 2.4.8
                setStatusBarTransparent	boolean	设置状态栏是否透明。仅在android 4.4以上设备生效。 since 2.4.8
                setStatusBarHidden	boolean	设置状态栏是否隐藏。since 2.4.8
                setVirtualButtonTransparent	boolean	设置虚拟按键栏背景是否透明。since 2.5.2
                 */
                .setStatusBarTransparent(true)
                .setStatusBarDarkMode(true)

                /**
                 * 授权页导航栏
                setNavColor	int	设置导航栏颜色
                setNavText	String	设置导航栏标题文字
                setNavTextColor	int	设置导航栏标题文字颜色
                setNavReturnImgPath	String	设置导航栏返回按钮图标
                setNavTransparent	boolean	设置导航栏背景是否透明。默认不透明。since 2.3.2
                setNavTextSize	int	设置导航栏标题文字字体大小（单位：sp）。since 2.4.1
                setNavReturnBtnHidden	boolean	设置导航栏返回按钮是否隐藏。默认不隐藏。since 2.4.1
                setNavReturnBtnWidth	int	设置导航栏返回按钮宽度。since 2.4.8
                setNavReturnBtnHeight	int	设置导航栏返回按钮高度。since 2.4.8
                setNavReturnBtnOffsetX	int	设置导航栏返回按钮距屏幕左侧偏移。since 2.4.8
                setNavReturnBtnRightOffsetX	int	设置导航栏返回按钮距屏幕右侧偏移。since 2.4.8
                setNavReturnBtnOffsetY	int	设置导航栏返回按钮距上端偏移。since 2.4.8
                setNavHidden	boolean	设置导航栏是否隐藏。since 2.4.8
                setNavTextBold	boolean	设置导航栏标题字体是否加粗。since 2.5.4
                 */
                .setNavTransparent(true)
//                .setNavColor(0xff0086d0)
                .setNavText("")
//                .setNavTextColor(0xffffffff)
                .setNavReturnBtnWidth(36)
                .setNavReturnBtnHeight(36)
                .setNavReturnImgPath(getString(R.string.nav_return_img_path))

                /**
                 * 授权页logo
                setLogoWidth	int	设置logo宽度（单位：dp）
                setLogoHeight	int	设置logo高度（单位：dp）
                setLogoHidden	boolean	隐藏logo
                setLogoOffsetY	int	设置logo相对于标题栏下边缘y偏移
                setLogoImgPath	String	设置logo图片
                setLogoOffsetX	int	设置logo相对于屏幕左边x轴偏移。since 2.3.8
                setLogoOffsetBottomY	int	设置logo相对于屏幕底部y轴偏移。since 2.4.8
                 */
//                .setLogoWidth(70)
//                .setLogoHeight(70)
//                .setLogoOffsetY(50)
//                .setLogoImgPath("logo_cm")
                .setLogoHidden(true)

                /**
                 * 授权页号码栏
                setNumberColor	int	设置手机号码字体颜色
                setNumberSize	Number	设置手机号码字体大小（单位：sp）。since 2.3.2
                setNumFieldOffsetY	int	设置号码栏相对于标题栏下边缘y偏移
                setNumFieldOffsetX	int	设置号码栏相对于屏幕左边x轴偏移。since 2.3.8
                setNumberFieldOffsetBottomY	int	设置号码栏相对于屏幕底部y轴偏移。since 2.4.8
                setNumberFieldWidth	int	设置号码栏宽度。since 2.4.8
                setNumberFieldHeight	int	设置号码栏高度。since 2.4.8
                setNumberTextBold	boolean	设置手机号码字体是否加粗。since 2.5.4
                 */
                .setNumberColor(getColor(R.color.phone_number_color))
                .setNumberSize(25)
                .setNumFieldOffsetY(180)
                .setNumberTextBold(true)

                /**
                 * 授权页登录按钮
                setLogBtnText	String	设置登录按钮文字
                setLogBtnTextColor	int	设置登录按钮文字颜色
                setLogBtnImgPath	String	设置授权登录按钮图片
                setLogBtnOffsetY	int	设置登录按钮相对于标题栏下边缘y偏移
                setLogBtnOffsetX	int	设置登录按钮相对于屏幕左边x轴偏移。since 2.3.8
                setLogBtnWidth	int	设置登录按钮宽度。since 2.3.8
                setLogBtnHeight	int	设置登录按钮高度。since 2.3.8
                setLogBtnTextSize	int	设置登录按钮字体大小。since 2.3.8
                setLogBtnBottomOffsetY	int	设置登录按钮相对屏幕底部y轴偏移。since 2.4.8
                setLogBtnTextBold	boolean	设置登录按钮字体是否加粗。since 2.5.4
                 */
                // 登录按钮
                .setLogBtnText(getString(R.string.login_btn_text))
                .setLogBtnTextColor(getColor(R.color.login_btn_text_color))
                .setLogBtnTextSize(15)
                .setLogBtnOffsetY(240)
                .setLogBtnHeight(50)
                .setLogBtnWidth(logBtnWidth)
                .setLogBtnImgPath(getString(R.string.login_btn_img_path))

                /**
                 * 授权页隐私栏
                setAppPrivacyOne	String,String	设置开发者隐私条款1名称和URL(名称，url)
                setAppPrivacyTwo	String,String	设置开发者隐私条款2名称和URL(名称，url)
                setAppPrivacyColor	int,int	设置隐私条款名称颜色(基础文字颜色，协议文字颜色)
                setPrivacyOffsetY	int	设置隐私条款相对于授权页面底部下边缘y偏移
                setCheckedImgPath	String	设置复选框选中时图片
                setUncheckedImgPath	String	设置复选框未选中时图片
                setPrivacyState	boolean	设置隐私条款默认选中状态，默认不选中。since 2.3.2
                setPrivacyOffsetX	int	设置隐私条款相对于屏幕左边x轴偏移。since 2.3.8
                setPrivacyTextCenterGravity	boolean	设置隐私条款文字是否居中对齐（默认左对齐）。since 2.3.8
                setPrivacyText	String,String,String,String	设置隐私条款名称外的文字。
                如：登录即同意...和...、...并使用本机号码登录
                参数1为："登录即同意"。
                参数2为："和"。
                参数3为："、"。
                参数4为："并使用本机号码登录"。since 2.3.8
                setPrivacyTextSize	int	设置隐私条款文字字体大小（单位：sp）。since 2.4.1
                setPrivacyTopOffsetY	int	设置隐私条款相对导航栏下端y轴偏移。since 2.4.8
                setPrivacyCheckboxHidden	boolean	设置隐私条款checkbox是否隐藏。since 2.4.8
                setPrivacyCheckboxSize	int	设置隐私条款checkbox尺寸。since 2.4.8
                setPrivacyWithBookTitleMark	boolean	设置隐私条款运营商协议名是否加书名号。since 2.4.8
                setPrivacyCheckboxInCenter	boolean	设置隐私条款checkbox是否相对协议文字纵向居中。默认居顶。since 2.4.8
                setPrivacyTextWidth	int	设置隐私条款文字栏宽度，单位dp。since 2.5.0
                enableHintToast	boolean Toast	协议栏checkbox未选中时，点击登录按钮是否弹出toast提示用户勾选协议，默认不弹。支持自定义Toast。since 2.5.0
                setPrivacyTextBold	boolean	设置隐私条款文字字体是否加粗。since 2.5.4
                setPrivacyUnderlineText	boolean	设置隐私条款文字字体是否加下划线。since 2.5.4
                 */
                .setUncheckedImgPath(getString(R.string.unchecked_img_path))//("umcsdk_uncheck_image")
                .setCheckedImgPath(getString(R.string.checked_img_path))//("umcsdk_check_image")
                .setPrivacyState(isSelectedPrivacy) // 设置隐私条款默认选中状态，默认不选中。
                .setAppPrivacyOne(getString(R.string.app_privacy_one), userAgreementUrl)
                .setAppPrivacyTwo(getString(R.string.app_privacy_two), privacyPolicyUrl)
                .setAppPrivacyColor(getColor(R.color.app_privacy_color1), getColor(R.color.app_privacy_color2))
                // "我已阅读并同意", "和", "以及", ""
                .setPrivacyText(
                        getString(R.string.privacy_text1),
                        getString(R.string.privacy_text2),
                        getString(R.string.privacy_text3),
                        getString(R.string.privacy_text4)) // 设置隐私条款名称外的文字。如：登录即同意...和...、...并使用本机号码登录
                .setPrivacyTextSize(10) // sp
                .setPrivacyWithBookTitleMark(true) // 设置隐私条款运营商协议名是否加书名号。
                .setPrivacyCheckboxInCenter(true)
                .setPrivacyCheckboxSize(16)
                .enableHintToast(true, Toast.makeText(context, R.string.checkbox_hint_toast, Toast.LENGTH_SHORT))

                /**
                 * 授权页隐私协议web页面
                setPrivacyNavColor	int	设置协议展示web页面导航栏背景颜色。since 2.4.8
                setPrivacyNavTitleTextColor	int	设置协议展示web页面导航栏标题文字颜色。since 2.4.8
                setPrivacyNavTitleTextSize	int	设置协议展示web页面导航栏标题文字大小（sp）。since 2.4.8
                setPrivacyNavReturnBtn	View	设置协议展示web页面导航栏返回按钮图标。since 2.4.8
                setAppPrivacyNavTitle1	String	设置自定义协议1对应web页面导航栏文字内容。since 2.5.2
                setAppPrivacyNavTitle2	String	设置自定义协议2对应web页面导航栏文字内容。since 2.5.2
                setPrivacyStatusBarColorWithNav	boolean	设置授权协议web页面状态栏与导航栏同色。仅在android 5.0以上设备生效。since 2.5.2
                setPrivacyStatusBarDarkMode	boolean	设置授权协议web页面状态栏暗色模式。仅在android 6.0以上设备生效。since 2.5.2
                setPrivacyStatusBarTransparent	boolean	设置授权协议web页面状态栏是否透明。仅在android 4.4以上设备生效。since 2.5.2
                setPrivacyStatusBarHidden	boolean	设置授权协议web页面状态栏是否隐藏。since 2.5.2
                setPrivacyVirtualButtonTransparent	boolean	设置授权协议web页面虚拟按键栏背景是否透明。since 2.5.2
                setPrivacyNavTitleTextBold	boolean	设置协议展示web页面导航栏字体是否加粗。since 2.5.4
                 */
                .setPrivacyNavColor(getColor(R.color.privacy_nav_color))
                .setPrivacyNavTitleTextColor(getColor(R.color.privacy_nav_title_text_color))
                .setPrivacyNavReturnBtn(createBackView())
                .setAppPrivacyNavTitle1(getString(R.string.app_privacy_nav_title1))
                .setAppPrivacyNavTitle2(getString(R.string.app_privacy_nav_title2))
                .setPrivacyStatusBarColorWithNav(true)
                .setPrivacyStatusBarDarkMode(true)

                /**
                 * 授权页slogan
                setSloganTextColor	int	设置移动slogan文字颜色
                setSloganOffsetY	int	设置slogan相对于标题栏下边缘y偏移
                setSloganOffsetX	int	设置slogan相对于屏幕左边x轴偏移。since 2.3.8
                setSloganBottomOffsetY	int	设置slogan相对于屏幕底部下边缘y轴偏移。since 2.3.8
                setSloganTextSize	int	设置slogan字体大小。since 2.4.8
                setSloganHidden	int	设置slogan是否隐藏。since 2.4.8
                setSloganTextBold	boolean	设置slogan字体是否加粗。since 2.5.4
                 */
//                .setSloganTextColor(getColor(R.color.slogan_text_color))
//                .setSloganTextSize(25)
//                .setSloganTextBold(true)
//                .setSloganOffsetY(100)
                .setSloganHidden(true)

                /**
                 * 自定义loading view
                 *
                 * setLoadingView	View,Animation	设置login过程中展示的loading view以及动画效果。since 2.4.8
                 */

                /**
                 * 授权页动画
                setNeedStartAnim	boolean	设置拉起授权页时是否需要显示默认动画。默认展示。since 2.5.2
                setNeedCloseAnim	boolean	设置关闭授权页时是否需要显示默认动画。默认展示。since 2.5.2
                 */
                .setNeedStartAnim(true) // 设置拉起授权页时是否需要显示默认动画。默认展示。since 2.5.2
                .setNeedCloseAnim(true) // 设置关闭授权页时是否需要显示默认动画。默认展示。since 2.5.2

                /**
                 * 开发者自定义控件 (SDK授权页面添加自定义控件)
                addCustomView	见以上方法定义	在授权页空白处添加自定义控件以及点击监听
                addNavControlView	见以上方法定义	在授权页面顶部导航栏添加自定义控件以及点击监听
                 */
                /**
                 * addCustomView(View view, boolean finishFlag,JVerifyUIClickCallback callback)
                 *
                 * view：开发者传入自定义的控件，开发者需要提前设置好控件的布局属性，SDK只支持RelativeLayout布局
                 * finishFlag：是否在授权页面通过自定义控件的点击finish授权页面
                 * callback： 自定义控件的点击回调
                 */
                .addCustomView(createTitleView(), false) { _, _ ->

                }
                .addCustomView(createOtherView(), false) { _, _ ->
                    otherLogin?.invoke()
                }
                .build()

        JVerificationInterface.setCustomUIWithConfig(config)
    }

    /**
     * 其他方式登录视图
     */
    private fun createOtherView(): ViewGroup {
        // 其他登录
        val otherLoginView = LinearLayout(context).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                topMargin = 310.dp
            }
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(15, 10, 15, 10)
        }

        TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            setText(R.string.other_login_text)
            setTextColor(getColor(R.color.other_login_btn_text_color))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13F)
            typeface = Typeface.DEFAULT_BOLD

            otherLoginView.addView(this)
        }

        ImageView(context).apply {
            layoutParams = LinearLayout.LayoutParams(20.dp, 20.dp)
            setImageResource(R.drawable.bg_login_other_)

            otherLoginView.addView(this)
        }
        return otherLoginView
    }

    private fun createTitleView(): View {
        return TextView(context).apply {
            layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
                topMargin = 60.dp
            }
            setText(R.string.one_key_login_title_text)
            setTextColor(getColor(R.color.one_key_login_title_text_color))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 25F)
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    private fun createBackView(): View {
        return ImageView(context).apply {
            layoutParams = ViewGroup.MarginLayoutParams(36.dp, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                marginStart = 12.dp
            }
            setImageResource(R.drawable.bg_login_back)
        }
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
        if (!ensureInit) {
            return
        }
        JVerificationInterface.getSmsCode(context, phoneNum, "signid", "tempid") { code, msg ->
            if (code == 3000) {
                success(msg)
            } else {
                failure?.invoke(code)
            }
        }
    }

    /**
     * SDK设置前后两次获取验证码的时间间隔
     * 设置前后两次获取验证码的时间间隔，默认 30000ms，有效范围(0,300000)
     * 参数说明：intervalTime：时间间隔，单位是毫秒(ms)。
     */
    fun setSmsIntervalTime(intervalTime: Long = 60_000) {
        JVerificationInterface.setSmsIntervalTime(intervalTime)
    }

    private fun getString(@StringRes resId: Int): String {
        return context!!.getString(resId)
    }

    private fun getColor(@ColorRes resId: Int): Int {
        return context!!.getColor(resId)
    }

    /**
     * 确保 [JVerificationInterface] 初始化
     */
    private val ensureInit: Boolean
        get() {
            if (!isInit) {
                "JLoginApi 没有初始化".e()
            }
            return isInit
        }

}

/**
错误码

code	message	                    备注
1000	verify consistent	        手机号验证一致
1001	verify not consistent	    手机号验证不一致
1002	unknown result	            未知结果
1003	token expired	            token失效
1004	sdk verify has been closed	SDK发起认证未开启
1005	包名和 AppKey 不匹配	        请检查客户端配置的包名与官网对应 Appkey 应用下配置的包名是否一致
1006	frequency of verifying single number is beyond the maximum limit	同一号码自然日内认证消耗超过限制
1007	beyond daily frequency limit    appKey自然日认证消耗超过限制
1008	AppKey 非法	                请到官网检查此应用信息中的 appkey，确认无误
1009		                        请到官网检查此应用的应用详情；更新应用中集成的极光SDK至最新
1010	verify interval is less than the minimum limit	同一号码连续两次提交认证间隔过短
1011	appSign invalid	            应用签名错误，检查签名与Portal设置的是否一致
2000	内容为token	                获取token成功
2001	fetch token failed	        获取token失败
2002	init failed	                SDK初始化失败
2003	network not reachable	    网络连接不通
2004	get uid failed	            极光服务注册失败
2005	request timeout	            请求超时
2006	fetch config failed	        获取应用配置失败
2007	                            内容为异常信息	验证遇到代码异常
2008	Token requesting, please try again later	正在获取token中，稍后再试
2009	verifying, please try again later	正在认证中，稍后再试
2010	don't have READ_PHONE_STATE permission	未开启读取手机状态权限
2011	                            内容为异常信息	获取配置时代码异常
2012	                            内容为异常信息	获取token时代码异常
2013	                            内容为具体错误原因	网络发生异常
2014	internal error while requesting token	请求token时发生内部错误
2016	network type not supported	当前网络环境不支持认证
2017	carrier config invalid	    运营商配置错误
2018	Local unsupported operator	本地不支持的运营商
3000	                            获取短信验证码成功	获取短信验证码成功
3001	没有初始化	                没有初始化
3002	无效电话号码	                无效电话号码
3003	前后两次请求少于设定时间	    前后两次请求少于设定时间
3004	未知错误	                    请求错误,具体查看错误信息msg
4001	parameter invalid	        参数错误。请检查参数，比如是否手机号格式不对
4014	appkey is blocked	        功能被禁用
4018		                        没有足够的余额
4031		                        不是认证SDK用户
4032		                        获取不到用户配置
4033	appkey is not support login	不是一键登录用户（请在认证设置-一键登录中配置RSA公钥）
5000	bad server	                服务器未知错误
6000	内容为token	                获取loginToken成功
6001	fetch loginToken failed	    获取loginToken失败
6002	fetch loginToken canceled	用户取消获取loginToken
6003	UI 资源加载异常	            未正常添加sdk所需的资源文件
6004	authorization requesting, please try again later	正在登录中，稍后再试
6006	prelogin scrip expired.	    预取号结果超时，需要重新预取号
7000	preLogin success	sdk     预取号成功
7001	preLogin failed	sdk         预取号失败
7002	preLogin requesting, please try again later	正在预取号中，稍后再试
8000	init success	            初始化成功
8004	init failed	                初始化失败，详见日志
8005	init timeout	            初始化超时，稍后再试
-994	                            网络连接超时
-996	                            网络连接断开
-997	                            注册失败/登录失败	（一般是由于没有网络造成的）如果确保设备网络正常，还是一直遇到此问题，则还有另外一个原因：JPush 服务器端拒绝注册。
而这个的原因一般是：你当前 App 的 Android 包名以及 AppKey，与你在 Portal 上注册的应用的 Android 包名与 AppKey 不相同。

 */