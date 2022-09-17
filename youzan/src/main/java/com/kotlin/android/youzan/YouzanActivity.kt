package com.kotlin.android.youzan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath


/**
 * @author zhangjian
 * @date 2021/10/25 09:25
 */
@Route(path = RouterActivityPath.YOUZANWEB.PAGE_YOUZAN_WEBVIEW)
class YouzanActivity : AppCompatActivity() {
    companion object {
        val KEY_URL = "url"
    }

    private var mFragment: YouzanFragment? = null
    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_youzan)
//        url = intent.getStringExtra(KEY_URL) ?: ""
//        intent.extras?.putString(KEY_URL, url)
        mFragment = YouzanFragment()
        mFragment?.arguments = intent.extras

        mFragment?.apply {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.placeholder, this)
                .commitAllowingStateLoss()
        }

    }

    override fun onBackPressed() {
        if (mFragment == null || !mFragment!!.onBackPressed()) {
            super.onBackPressed()
        }
    }
}