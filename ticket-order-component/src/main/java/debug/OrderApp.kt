package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.widget.refresh.Refresh

/**
 * create by lushan on 2020/9/16
 * description:
 */
class OrderApp:CoreApp() {

    override fun onCreate() {
        super.onCreate()
        Refresh.initHeaderAndFooter()
    }
}