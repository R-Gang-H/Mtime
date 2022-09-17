package com.kotlin.android.qrcode.component.ui.capture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.KeyEvent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.ext.parseAppLink
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.qrcode.component.R
import com.kotlin.android.qrcode.component.databinding.ZxingCaptureBinding
import com.kotlin.android.qrcode.component.journeyapps.barcodescanner.CaptureManager
import com.kotlin.android.qrcode.component.utils.recogniseQrCodeFromFile
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.zxing_capture.*
import org.jetbrains.anko.doAsync

@Route(path = RouterActivityPath.QRCode.PAGE_QRCODE_ACTIVITY)
class CaptureActivity : BaseVMActivity<CaptureViewModel, ZxingCaptureBinding>() {
    private val IMAGE_UNSPECIFIED = "image/*"
    private val CHOOSE_PIC = 2
    private var capture: CaptureManager? = null

    override fun initVM(): CaptureViewModel {
        return viewModels<CaptureViewModel>().value
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        CommonTitleBar().init(this).setTitle(R.string.qrcode_component_scan).setRightTextAndClick(R.string.qrcode_component_album) {
//            选择图库中的二维码进行识别
            openSystemAlbum()
        }.setLeftClickListener { finish() }.create()
    }

    override fun initView() {
        capture = CaptureManager(this, barCodeScanner).apply {
            initializeFromIntent(intent, null)
            decodeContinuous()
            setQrResultListener {
                decodeSuccess(it.toString())
            }
        }
    }

    private fun decodeSuccess(result: String) {
        if (!TextUtils.isEmpty(result)) {
            val uri = Uri.parse(result)
            uri?.let {
                val applinkData = it.getQueryParameter("applinkData")
                applinkData?.let { applink ->
                    parseAppLink(context = applicationContext, appLink = applink)
                    finishAndClose()
                }?:showErrorMessage()
            }?:showErrorMessage()
        }
    }

    private fun showErrorMessage(){
        showToast(R.string.qrcode_component_decode_error)
    }

    private fun finishAndClose() {
        capture?.closeAndFinish()
        finish()
    }

    override fun initData() {}
    override fun startObserve() {}

    override fun onResume() {
        super.onResume()
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture?.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barCodeScanner?.onKeyDown(keyCode, event) == true || super.onKeyDown(keyCode, event)
    }

    // 打开系统相册
    private fun openSystemAlbum() {
        try {
            //跳转到图片选择界面去选择一张二维码图片
            val intent = Intent().apply {
                action = Intent.ACTION_PICK
                type = IMAGE_UNSPECIFIED
            }
            val intent1 = Intent.createChooser(intent, resources.getString(R.string.qrcode_component_select_qrcode_pic))
            startActivityForResult(intent1, CHOOSE_PIC)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 || resultCode != RESULT_OK || data == null) {
            return
        }
        if (CHOOSE_PIC == requestCode) {
            showToast(R.string.qrcode_component_scan_decode)
            // 读取相册图片
            val uri = data.data
            if (uri != null) {
                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = this@CaptureActivity.contentResolver.query(uri, proj, null, null, null)
                if (null != cursor) {
                    cursor.moveToFirst()
                    val columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
                    val imgPath = cursor.getString(columnIndex) // 图片文件路径
                    cursor.close()

                    doAsync {
                        var recogniseQrCodeFromFile = recogniseQrCodeFromFile(imgPath)
                        runOnUiThread { decodeSuccess(recogniseQrCodeFromFile.orEmpty()) }
                    }
                }
            }
        }
    }

}