package com.kotlin.android.community.ui.person.wantsee

import android.widget.TextView
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ActCommunityWantseeBinding
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.ui.person.*
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import kotlinx.android.synthetic.main.act_community_wantsee.*


/**
 * 社区- 想看看过
 * @author WangWei
 * @data 2020/10/13
 */
//@Route(path = RouterActivityPath.Community.PAGE_WANT_SEE)
class WantSeeActivity : BaseVMActivity<CommunityPersonViewModel, ActCommunityWantseeBinding>() {

    companion object {
        const val TAB_CORNER_RADIUS = 15 // dp
    }

    var userId: Long = 0L
    var type: Long = 0L
    var wantSeeFragment : CommunityWantSeeFragment?= null//想看
    var hasSeenFragment : CommunityWantSeeFragment? = null //看过
    var currentFragment: CommunityWantSeeFragment? = null

    override fun initVM(): CommunityPersonViewModel {
        userId = intent.getLongExtra(KEY_USER_ID, 0)
        type = intent.getLongExtra(KEY_TYPE, 0)

        return viewModels<CommunityPersonViewModel>().value
    }

    override fun initView() {
        wantSeeFragment = CommunityWantSeeFragment().newInstance(userId, PERSON_TYPE_WANT_SEE)//想看
        hasSeenFragment = CommunityWantSeeFragment().newInstance(userId, PERSON_TYPE_HAS_SEEN)//看过

        ShapeExt.setShapeColorAndCorner(tip, R.color.color_f1f4f5, TAB_CORNER_RADIUS)
        when (type) {
            PERSON_TYPE_WANT_SEE -> {
                addFragment(wantSeeFragment!!)
                changeViewStatus(left_label, right_label)
            }
            PERSON_TYPE_HAS_SEEN -> {
                addFragment(hasSeenFragment!!)
                changeViewStatus(right_label, left_label)
            }
        }
        left_label.setOnClickListener {
            switchFragment(hasSeenFragment!!, wantSeeFragment!!)
            changeViewStatus(left_label, right_label)
        }
        right_label.setOnClickListener {
            switchFragment(wantSeeFragment!!, hasSeenFragment!!)
            changeViewStatus(right_label, left_label)
        }
//        mTitleBackIv.setImageDrawable(getDrawable(R.drawable.ic_back)?.mutate())
        mTitleBackIv.setOnClickListener { finish() }
    }

    /**
     * 改变状态
     */
    private fun changeViewStatus(chosenView: TextView, unChosenView: TextView) {
        // 选中
        ShapeExt.setShapeColorAndCorner(chosenView, R.color.color_20a0da, TAB_CORNER_RADIUS)
        chosenView.setTextColor(getColor(R.color.color_ffffff))
        // 未选中
        ShapeExt.setShapeColorAndCorner(unChosenView, R.color.color_00000000, TAB_CORNER_RADIUS)
        unChosenView.setTextColor(getColor(R.color.color_8798af))
    }

    override fun initData() {
    }

    override fun startObserve() {

    }

    private fun addFragment(fragment: CommunityWantSeeFragment){
        var transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment, fragment).commitNowAllowingStateLoss()
        currentFragment = fragment
    }
    private fun switchFragment(from: CommunityWantSeeFragment, to: CommunityWantSeeFragment) {
        if (currentFragment != to) {
            currentFragment = to
            var transaction = supportFragmentManager.beginTransaction()
//            transaction.setCustomAnimations(R.anim.fragment_fade_enter, R.anim.fragment_fade_exit)
            if (to.isAdded) {
                // 隐藏当前的fragment，显示下一个
                if (from != null) transaction.hide(from)
                transaction.show(to).commit()
            } else {
                // 隐藏当前的fragment，add下一个到Activity中
                if (from != null) transaction.hide(from)
                transaction.add(R.id.fragment, to).commitNowAllowingStateLoss()
            }
        }
    }

}