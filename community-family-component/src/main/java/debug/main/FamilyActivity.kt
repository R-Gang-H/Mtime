package debug.main

import androidx.activity.viewModels
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActCommunityFamilyMainBinding
import com.kotlin.android.core.BaseVMActivity

/**
 *
 * Created on 2020/6/15.
 *
 * @author o.s
 */
class FamilyActivity : BaseVMActivity<FamilyViewModel, ActCommunityFamilyMainBinding>() {
    override fun initVM(): FamilyViewModel = viewModels<FamilyViewModel>().value

    override fun initView() {

    }

    override fun initData() {

    }

    override fun startObserve() {
    }
}