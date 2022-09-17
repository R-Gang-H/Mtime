package com.kotlin.android.user.login.jguang

import com.kotlin.android.app.data.entity.user.Login
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.retrofit.cookie.CookieManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * 极光一键登录支持类
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */
class JLoginSupport private constructor() : CoroutineScope by MainScope() {

    companion object {
        val instance by lazy { JLoginSupport() }
    }

    /**
     * 极光一键登录，服务器验证登录接口
     */
    fun jVerifyLogin(
            loginToken: String,
            error: (String?) -> Unit,
            netError: (String) -> Unit,
            success: Login.() -> Unit,
    ) {
        JLoginScope.instance.jVerifyLogin(
                loginToken = loginToken,
                error = error,
                netError = netError,
                success = success,
        )
    }

    /**
     * 拉取极光一键登录授权页面失败
     */
    fun authFailure(
            code: Int,
            otherLogin: (() -> Unit)? = null
    ) {
        "拉取极光一键登录授权页面失败：code=$code".e()
        if (code == 6001) {
            // 6001 fetch loginToken failed	    获取loginToken失败
            // 【token过期】 loginAuth code=6001, content=fetch loginToken failed:[CU,（1){"error":"201","error_description":"token已失效"}], operator=null
            JLoginManager.instance.prepare()
        }
        if (code == 6004) {
            // 6004 登录中，连续点击请求拉取一键登录页会出现6004，啥都不做即可
            return
        }
        if (code == 6006) {
            // 【重新取号】 6006	prelogin scrip expired.	    预取号结果超时，需要重新预取号
            JLoginManager.instance.prepare()
        }
        JLoginManager.instance.dismissLoginAuthActivity()
        otherLogin?.invoke()
    }

    /**
     * 取消极光一键登录授权页面
     */
    fun authCancel() {
        "取消极光一键登录授权页面".e()
        JLoginManager.instance.prepare()
    }

    /**
     * 极光一键登录验证网络不给力
     */
    fun netError(
            error: String,
            count: Int,
            otherLogin: (() -> Unit)? = null
    ) {
        "极光一键登录验证网络不给力 error=$error".e()
        showToast("网络不给力，网络异常")
        if (count >= 3) {
            JLoginManager.instance.dismissLoginAuthActivity()
        }
//        JLoginManager.instance.dismissLoginAuthActivity()
//        otherLogin?.invoke()
    }

    /**
     * 极光一键登录验证失败
     */
    fun loginFailure(
            error: String?,
            count: Int,
            otherLogin: (() -> Unit)? = null
    ) {
        "极光一键登录验证失败 error=$error".e()
        showToast("登录失败")
        if (count >= 3) {
            JLoginManager.instance.dismissLoginAuthActivity()
        }
//        JLoginManager.instance.dismissLoginAuthActivity()
//        otherLogin?.invoke()
    }

    /**
     * 极光一键登录验证成功
     */
    fun loginSuccess(
            login: Login,
            count: Int,
            otherLogin: (() -> Unit)? = null,
            completed: ((Login) -> Unit)? = null
    ) {
        "极光一键登录验证成功 login=$login".e()
        login.apply {
            // toast 登录成功/登录失败
            showToast(msg)

            if (status == 1L) {
                // 登录成功
                completed?.invoke(login)
                JLoginManager.instance.dismissLoginAuthActivity(
                        failure = {
                            "关闭授权登录页面失败：code=$it".e()
//                    otherLogin?.invoke()
                        }
                )
            } else {
                //登录失败，则清理cookie
                CookieManager.instance.clear()
                if (count >= 3) {
                    JLoginManager.instance.dismissLoginAuthActivity()
                }
//                otherLogin?.invoke()
            }
        }
    }

}