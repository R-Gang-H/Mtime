package debug

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.live.component.R

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.live.ILiveProvider
import com.kotlin.android.router.ext.getProvider
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        liveBtn?.onClick {
            var instance:ILiveProvider? = getProvider(ILiveProvider::class.java)
            instance?.launchLiveDetail(123L)
        }

    }
}