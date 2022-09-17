package com.kotlin.android.community.ui.person.myfriend

import android.view.Gravity
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityMyFriendBinding
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import com.kotlin.android.community.ui.person.KEY_TYPE
import com.kotlin.android.community.ui.person.KEY_USER_ID
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.frag_community_my_friend.*
import kotlinx.android.synthetic.main.frag_community_person.mTabLayout
import kotlinx.android.synthetic.main.frag_community_person.mViewPager

/**
 * 社区-我的好友
 * @author WangWei
 * @data 2020/9/30
 */
@Route(path = RouterActivityPath.Community.PAGER_FRIEND)
class CommunityMyFriendActivity : BaseVMActivity<CommunityPersonViewModel, FragCommunityMyFriendBinding>() {

    var userId: Long = 0L
    var type: Long = 0L
    override fun initTheme() {
        super.initTheme()
        immersive().statusBarColor(getColor(R.color.color_ffffff)).statusBarDarkFont(true)
    }
    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(
                title = getString(R.string.friend),
                isBold = true,
                gravity = Gravity.CENTER,
                drawablePadding = 5.dp,
            ).addItem(
                isReversed = false,
                drawableRes = R.drawable.ic_title_bar_36_back,
                reverseDrawableRes = R.drawable.ic_title_bar_36_back_reversed,
                click = {
                    this.finish()
                }
            )
    }

    override fun initVariable() {
        super.initVariable()
        userId = intent.getLongExtra(KEY_USER_ID, 0)
        type = intent.getLongExtra(KEY_TYPE, 0)
    }
    override fun initView() {
        var creator = FragPagerItems(this@CommunityMyFriendActivity)
                .add(titleRes = (R.string.title_attend), clazz = CommunityAttendFragment::class.java, args = CommunityAttendFragment().bundler(userId, 0))
                .add(titleRes = (R.string.title_fan), clazz = CommunityAttendFragment::class.java, args = CommunityAttendFragment().bundler(userId, 1))

        var adapter = FragPagerItemAdapter(this@CommunityMyFriendActivity.supportFragmentManager, creator)

        mViewPager.adapter = adapter
        mTabLayout.setViewPager(mViewPager)
        mTabLayout.setSelectedAnim()
        if (type == 0L)
            mViewPager.setCurrentItem(0, true)
        else mViewPager.setCurrentItem(1, true)
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

}