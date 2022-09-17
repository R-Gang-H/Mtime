package com.kotlin.android.community.ui.person.wantsee

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityMyWantseeBinding
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.community.ui.person.PERSON_TYPE_HAS_SEEN
import com.kotlin.android.community.ui.person.PERSON_TYPE_WANT_SEE
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import kotlinx.android.synthetic.main.frag_community_my_friend.*
import kotlinx.android.synthetic.main.frag_community_person.mTabLayout
import kotlinx.android.synthetic.main.frag_community_person.mViewPager

/**
 * 社区- 想看看过
 * @author WangWei
 * @data 2022/10/13
 */
@Route(path = RouterActivityPath.Community.PAGE_WANT_SEE)
class MyWantSeeActivity : BaseVMActivity<CommunityPersonViewModel, FragCommunityMyWantseeBinding>() {

    var userId: Long = 0L
    var type: Long = 0L
    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }
    override fun initCommonTitleView() {
        super.initCommonTitleView()
        mBinding?.title?.visible()
        mBinding?.mTitleBackIv?.setOnClickListener { finish() }
    }

    override fun initVariable() {
        super.initVariable()
        userId = intent.getLongExtra(KEY_USER_ID, 0)
        type = intent.getLongExtra(KEY_TYPE, 0)
    }
    override fun initView() {
        var creator = FragPagerItems(this@MyWantSeeActivity)
                .add(titleRes = (R.string.wantsee), clazz = CommunityWantSeeFragment::class.java, args = CommunityWantSeeFragment().bundler(userId, PERSON_TYPE_WANT_SEE))
                .add(titleRes = (R.string.wantseed), clazz = CommunityWantSeeFragment::class.java, args = CommunityWantSeeFragment().bundler(userId, PERSON_TYPE_HAS_SEEN))

        var adapter = FragPagerItemAdapter(this@MyWantSeeActivity.supportFragmentManager, creator)

        mViewPager.adapter = adapter
        mTabLayout.setViewPager(mViewPager)
        mTabLayout.setSelectedAnim()
        if (type == PERSON_TYPE_WANT_SEE)
            mViewPager.setCurrentItem(0, true)
        else mViewPager.setCurrentItem(1, true)
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

}