package debug

import com.kotlin.android.core.BuildConfig
import com.kotlin.android.core.CoreApp
import com.kotlin.android.player.PlayerConfig
import com.kotlin.android.widget.refresh.Refresh

/**
 * create by lushan on 2020/9/1
 * description:
 */
class VideoApp:CoreApp() {
    override fun onCreate() {
        super.onCreate()
        Refresh.initHeaderAndFooter()
        //播放器初始化
        PlayerConfig.init(this, BuildConfig.DEBUG)
    }
}