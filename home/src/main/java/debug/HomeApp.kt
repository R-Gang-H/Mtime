package debug

import com.alibaba.android.arouter.launcher.ARouter
import com.kotlin.android.app.api.ApiManager
import com.kotlin.android.core.CoreApp
import com.kotlin.android.home.BuildConfig
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.router.RouterManager
import com.kotlin.android.widget.refresh.Refresh

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/3
 */
class HomeApp : CoreApp() {
    override fun onCreate() {
        super.onCreate()
        RouterManager.instance
            .init(this, BuildConfig.DEBUG, "com.kotlin.android.app.router.path")

        //初始化全局下拉刷新和上拉加载样式
        Refresh.initHeaderAndFooter()

        //播放器初始化
        PlayerConfig.init(this, BuildConfig.DEBUG)

        ApiManager.init()
    }
}