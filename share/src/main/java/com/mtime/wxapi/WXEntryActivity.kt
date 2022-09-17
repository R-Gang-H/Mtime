package com.mtime.wxapi

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kotlin.android.bonus.scene.component.postShareEvent
import com.kotlin.android.data.auth.AuthLogin
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.share.R
import com.kotlin.android.share.ShareEnv
import com.kotlin.android.share.ShareState
import com.kotlin.android.share.auth.AuthPlatform
import com.kotlin.android.share.auth.AuthState
import com.kotlin.android.share.entity.AuthEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.wx.AuthWX
import com.kotlin.android.share.wx.ShareWX
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory

/**
 * [4]接收微信的请求及返回值
 * 如果你的程序需要接收微信发送的请求，或者接收发送到微信请求的响应结果，需要下面 3 步操作：
 * a. 在你的包名相应目录下新建一个 应用包名.wxapi 目录，并在该 wxapi 目录下新增一个 [WXEntryActivity] 类，该类继承自 [Activity]
 *      并在 manifest 文件里面加上exported、taskAffinity及launchMode属性，
 *      其中exported设置为true，taskAffinity设置为你的包名，launchMode设置为singleTask，
 * b. 实现 [IWXAPIEventHandler] 接口，微信发送的请求将回调到 [onReq] 方法，发送到微信请求的响应结果将回调到 [onResp] 方法
 * c. 在 [WXEntryActivity] 中将接收到的 intent 及实现了 [IWXAPIEventHandler] 接口的对象传递给 [IWXAPI] 接口的 [IWXAPI.handleIntent] 方法
 *
 * Created on 2020/7/7.
 *
 * @author o.s
 */
class WXEntryActivity : AppCompatActivity(), IWXAPIEventHandler {

    val api: IWXAPI by lazy { WXAPIFactory.createWXAPI(this, ShareEnv.APP_ID_WX, true) }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        api.handleIntent(intent, this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        api.handleIntent(intent, this)
    }

    /**
     * 发送到微信请求的响应结果将回调到 [onResp] 方法
     */
    override fun onResp(resp: BaseResp?) {
        if (resp is SendAuth.Resp) {
            when (resp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    showToast(R.string.auth_success)
                    AuthWX.authState?.invoke(AuthEntity(
                            state = AuthState.SUCCESS,
                            platform = AuthPlatform.WE_CHAT,
                            wxCode = resp.code,
                            authLogin = AuthLogin(
                                    platformId = AuthPlatform.WE_CHAT.id,
                                    code = resp.code,
                            )
                    ))
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    showToast(R.string.auth_cancel)
                    AuthWX.authState?.invoke(AuthEntity(
                            state = AuthState.FAILURE,
                            platform = AuthPlatform.WE_CHAT
                    ))
                }
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    showToast(R.string.auth_denied)
                    AuthWX.authState?.invoke(AuthEntity(
                            state = AuthState.FAILURE,
                            platform = AuthPlatform.WE_CHAT
                    ))
                }
                else -> {
                    showToast(R.string.auth_fail)
                    AuthWX.authState?.invoke(AuthEntity(
                            state = AuthState.FAILURE,
                            platform = AuthPlatform.WE_CHAT
                    ))
                }
            }
            finish()
        } else if (resp is SendMessageToWX.Resp) {
            /**
             * int ERR_OK = 0;
             * int ERR_COMM = -1;
             * int ERR_USER_CANCEL = -2;
             * int ERR_SENT_FAILED = -3;
             * int ERR_AUTH_DENIED = -4;
             * int ERR_UNSUPPORT = -5;
             * int ERR_BAN = -6;
             */
            when (resp.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    showToast(R.string.share_success)
                    ShareWX.shareState?.invoke(ShareState.SUCCESS)
                    dismissShareDialog()
                    postShareEvent()
                }
//                BaseResp.ErrCode.ERR_COMM -> toast(R.string.share_fail)
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    showToast(R.string.share_cancel)
                    ShareWX.shareState?.invoke(ShareState.CANCEL)
                }
                BaseResp.ErrCode.ERR_SENT_FAILED -> {
                    showToast(R.string.share_fail)
                    ShareWX.shareState?.invoke(ShareState.FAILURE)
                }
//                BaseResp.ErrCode.ERR_AUTH_DENIED -> toast(R.string.share_fail)
//                BaseResp.ErrCode.ERR_UNSUPPORT -> toast(R.string.share_fail)
//                BaseResp.ErrCode.ERR_BAN -> toast(R.string.share_fail)
                else -> {
                    showToast(R.string.share_fail)
                    ShareWX.shareState?.invoke(ShareState.FAILURE)
                }
            }
            finish()
        }

    }

    /**
     * 微信发送的请求将回调到 [onReq] 方法
     */
    override fun onReq(req: BaseReq?) {
    }

}