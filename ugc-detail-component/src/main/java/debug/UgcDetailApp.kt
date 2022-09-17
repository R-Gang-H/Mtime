package debug

import com.kotlin.android.core.CoreApp
import com.kotlin.android.widget.refresh.Refresh

class UgcDetailApp:CoreApp() {
    override fun onCreate() {
        super.onCreate()
        Refresh.initHeaderAndFooter()
    }
}