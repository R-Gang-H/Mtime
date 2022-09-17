package com.kotlin.android.qrcode.component.ui.login

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.qrcode.component.R
import com.kotlin.android.qrcode.component.databinding.ActQrcodeLoginBinding
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.qrcode.IQRcodeProvider
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.act_qrcode_login.*
/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/12/25
 *
 * 二维码网页登录确认
 */
@Route(path = RouterActivityPath.QRCode.PAGE_QRCODE_LOGIN_ACTIVITY)
class QrcodeLoginActivity : BaseVMActivity<QrcodeLoginViewModel, ActQrcodeLoginBinding>() {
    companion object {
        const val KEY_UUID = "key_uuid";
    }

    private var mUuid: String? = ""

    override fun initVM(): QrcodeLoginViewModel = viewModels<QrcodeLoginViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar()
                .init(this)
                .setTitle(R.string.qrcode_login_title)
                .setLeftClickListener { finish() }
                .create()
    }

    override fun initView() {
        mQrcodeLoginBtnCancelTv.onClick {
            finish()
        }
        mQrcodeLoginBtnConfirmTv.onClick {
            mUuid?.let { uuid ->
                mViewModel?.qrcodeLogin(uuid)
            }
        }
    }

    override fun initData() {
        mUuid = intent?.getStringExtra(KEY_UUID)
    }

    override fun startObserve() {
        mViewModel?.uiSate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    bizMsg?.apply {
                        showToast(this)
                    }
                    finish()
                    if (!isSuccess()) {
                        getProvider(IQRcodeProvider::class.java)
                                ?.startQrScanActivity()
                    }
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(this)
                }
            }
        }
    }
}

