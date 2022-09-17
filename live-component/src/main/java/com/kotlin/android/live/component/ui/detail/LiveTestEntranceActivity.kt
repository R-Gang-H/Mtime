package com.kotlin.android.live.component.ui.detail

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.live.component.R
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.router.provider.IBaseProvider
import com.kotlin.android.app.router.provider.live.ILiveProvider
import kotlinx.android.synthetic.main.activity_live_test_entrance.*

@Route(path = RouterActivityPath.Live.PAGE_LIVE_TEST_ENTRANCE_ACTIVITY)
class LiveTestEntranceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_test_entrance)

        enterBtn?.onClick {
            val liveId = liveIdET?.text?.toString().orEmpty().trim()
            if (TextUtils.isEmpty(liveId)) {
                showToast("请输入直播id")
                return@onClick
            }
            if (TextUtils.isDigitsOnly(liveId).not()) {
                showToast("请输入数字字符串")
                return@onClick
            }

            val provider = getProvider(ILiveProvider::class.java)
            provider?.launchLiveDetail(liveId.toLong())
        }
    }
}