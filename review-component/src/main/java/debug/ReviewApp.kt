package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.widget.refresh.Refresh

/**
 * create by lushan on 2020/8/10
 * description:
 */
class ReviewApp:CoreApp() {
    override fun onCreate() {
        super.onCreate()
        Refresh.initHeaderAndFooter()
    }
}