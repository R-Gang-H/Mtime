package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.message.BuildConfig
import com.kotlin.android.router.RouterManager
import com.kotlin.android.widget.refresh.Refresh.initHeaderAndFooter

/**
 * Created by zhaoninglongfei on 2022/3/15
 *
 */
class MessageCenterApp : CoreApp() {

    override fun onCreate() {
        super.onCreate()

        RouterManager.instance
            .init(this, BuildConfig.DEBUG, "com.kotlin.android.message.router.path")

        //初始化全局下拉刷新和上拉加载样式
        initHeaderAndFooter()
    }
}