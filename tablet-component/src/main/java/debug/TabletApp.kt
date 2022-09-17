package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.crash.CrashHandler
import com.kotlin.android.router.RouterManager
import com.kotlin.android.widget.refresh.Refresh

/**
 * 创建者: zl
 * 创建时间: 2020/7/24 5:25 PM
 * 描述:
 */
class TabletApp : CoreApp() {
    override fun onCreate() {
        super.onCreate()
        RouterManager.instance.init(this, true)
        //初始化全局下拉刷新和上拉加载样式
        Refresh.initHeaderAndFooter()
        CrashHandler.init(this)
    }
}