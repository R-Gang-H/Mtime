package com.kotlin.android.community.ui.person.collect

import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.BR
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.FragCommunityPersonCollectionBinding
import com.kotlin.android.community.ui.person.*
import com.kotlin.android.community.ui.person.center.CommunityPersonViewModel
import com.kotlin.android.community.ui.person.center.content.article.CommunityCenterContentFragment
import com.kotlin.android.community.ui.person.center.photo.CommunityPersonPhotoFragment
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager


/**
 * 社区个人主页 收藏页面
 * @author WangWei
 * @data 2022/3/20
 */
@Route(path = RouterActivityPath.Community.PAGER_PERSON_COLLECTION)
class CommunityPersonCollectionActivity :
    BaseVMActivity<CommunityPersonViewModel, FragCommunityPersonCollectionBinding>() {

    var userId: Long = 0L//100049
    var index: Long = 0L

    override fun initTheme() {
        super.initTheme()
        immersive()
            .transparentStatusBar()
            .statusBarDarkFont(true)
    }


    override fun initVariable() {
        super.initVariable()
        userId = intent.getLongExtra(KEY_USER_ID, 0)
        index = intent.getLongExtra(KEY_TYPE, 0)
    }

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this, ThemeStyle.STANDARD_STATUS_BAR)
            .setTitle(
                titleRes = R.string.community_person_collection,
            )
            .addItem(
                drawableRes = R.drawable.ic_title_bar_36_back
            ) {
                onBackPressed()
            }
    }

    override fun initView() {
        var items = FragPagerItems(this@CommunityPersonCollectionActivity)
        items.add(
            title = getString(R.string.community_content, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment()
                .bundler(userId, USER_CENTER_TYPE_ARTICLE, USER_CENTER_TYPE_COLLECTION)
        )

        items.add(
            title = getString(R.string.community_post, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment().bundler(
                userId,
                USER_CENTER_TYPE_POST,
                USER_CENTER_TYPE_COLLECTION
            )
        )
        items.add(
            title = getString(R.string.community_film_coment, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment().bundler(
                userId,
                USER_CENTER_TYPE_FILM_COMMENT,
                USER_CENTER_TYPE_COLLECTION
            )
        )
        items.add(
            title = getString(R.string.community_video, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment().bundler(
                userId,
                USER_CENTER_TYPE_FILM_VIDEO,
                USER_CENTER_TYPE_COLLECTION
            )
        )
        items.add(
            title = getString(R.string.community_brodcast, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment().bundler(
                userId,
                USER_CENTER_TYPE_AUDIO,
                USER_CENTER_TYPE_COLLECTION
            )
        )
        items.add(
            title = getString(R.string.community_diary, ""),
            clazz = CommunityCenterContentFragment::class.java,
            args = CommunityCenterContentFragment().bundler(
                userId,
                USER_CENTER_TYPE_DIARY,
                USER_CENTER_TYPE_COLLECTION
            )
        )
        //电影
        items.add(
            title = getString(R.string.community_film, ""),
            clazz = CollectionMovieFragment::class.java,
            args = CollectionMovieFragment().bundler(userId)
        )
        //电影人
        items.add(
            title = getString(R.string.community_filmer, ""),
            clazz = CollectionPersonFragment::class.java,
            args = CollectionPersonFragment().bundler(userId)
        )
        //影院
        items.add(
            title = getString(R.string.community_cinema, ""),
            clazz = CollectionCinemaFragment::class.java,
            args = CollectionCinemaFragment().bundler(userId)
        )

        var adapter = FragPagerItemAdapter(
            this@CommunityPersonCollectionActivity.supportFragmentManager,
            items
        )

        mBinding?.mViewPager?.adapter = adapter
        mBinding?.mViewPager?.offscreenPageLimit = adapter.count
        mBinding?.mTabLayout?.setViewPager(mBinding?.mViewPager)
        mBinding?.mTabLayout?.setSelectedAnim()
        if (index != 0L)
            mBinding?.mViewPager?.currentItem = index.toInt()
        else mBinding?.mViewPager?.currentItem = 0
    }

    override fun initData() {
        mBinding?.setVariable(BR.data, mViewModel)
    }

    override fun startObserve() {
    }

}
