package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.widget.refresh.Refresh

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/17
 */
class CommunityApp: CoreApp() {

    override fun onCreate() {
        super.onCreate()
        //初始化全局下拉刷新和上拉加载样式
        Refresh.initHeaderAndFooter()
    }
}