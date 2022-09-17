package debug.main

import androidx.activity.viewModels
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.review.component.BR
import com.kotlin.android.review.component.databinding.ActivityReviewDebugBinding

import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.router.ext.getProvider

class ReviewDebugActivity : BaseVMActivity<ReviewDebugViewModel, ActivityReviewDebugBinding>() {
    private var provider :IReviewProvider?  = getProvider(IReviewProvider::class.java)


    override fun initVM() = viewModels<ReviewDebugViewModel>().value

    override fun initView() {
        mBinding?.setVariable(BR.provider,provider)
    }

    override fun initData() {
    }

    override fun startObserve() {
    }
}