package debug.main

import androidx.activity.viewModels
import com.kotlin.android.community.databinding.ActCommunityMainBinding
import com.kotlin.android.core.BaseVMActivity

/**
 *
 * Created on 2020/6/15.
 *
 * @author o.s
 */
class CommunityActivity : BaseVMActivity<CommunityViewModel, ActCommunityMainBinding>() {

    override fun initVM(): CommunityViewModel = viewModels<CommunityViewModel>().value

    override fun initView() {

    }

    override fun initData() {

    }

    override fun startObserve() {
    }
}