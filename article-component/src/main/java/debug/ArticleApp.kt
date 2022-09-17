package debug

import com.kotlin.android.article.component.BuildConfig
import com.kotlin.android.core.CoreApp
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.widget.refresh.Refresh
import com.tencent.smtt.sdk.QbSdk
import java.util.concurrent.Executors

/**
 * create by lushan on 2020/8/11
 * description:
 */
class ArticleApp:CoreApp() {
    override fun onCreate() {
        super.onCreate()
        PlayerConfig.init(this, BuildConfig.DEBUG)
        Refresh.initHeaderAndFooter()

        // 开启线程初始化其他数据
        val exec = Executors.newSingleThreadExecutor()
        exec?.execute {
            //提前初始化x5解决在配置较低手机第一次加载过慢的问题


            //提前初始化x5解决在配置较低手机第一次加载过慢的问题
            QbSdk.initX5Environment(this, null)
        }
    }

}