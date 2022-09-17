package com.kotlin.android.mine.ui.content

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.*
import com.kotlin.android.app.data.constant.CommConstant.KEY_CONTENT_TYPE
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityMyContentsBinding
import com.kotlin.android.mine.ui.content.fragment.ContentsFragment
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_MY_CONTENTS_ACTIVITY)
class MyContentsActivity : BaseVMActivity<MyContentsViewModel, ActivityMyContentsBinding>() {

    private val mineProvider =
        getProvider(IMineProvider::class.java)
    private var pagerAdapter: FragPagerItemAdapter? = null
    private var articleUserLimit: Boolean = true
    private val typeArray = arrayListOf(
        CONTENT_TYPE_ARTICLE,
        CONTENT_TYPE_FILM_COMMENT,
        CONTENT_TYPE_POST,
        CONTENT_TYPE_VIDEO,
        CONTENT_TYPE_AUDIO,
        CONTENT_TYPE_JOURNAL
    )

    override fun initVM(): MyContentsViewModel = viewModels<MyContentsViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
            .setTitle(titleRes = R.string.mine_content_title)
            .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                onBackPressed()
            }
            .addItem(isReversed = true, titleRes = R.string.mine_drafts) {
                mineProvider?.startMyDrafts(this)
            }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {

    }

    override fun initData() {
        mViewModel?.getQueryArticleUser()
    }

    override fun startObserve() {
        getArticleUserObserve()
    }

    /**
     * 当前文章用户信息Observe
     */
    private fun getArticleUserObserve() {
        mViewModel?.queryArticleUserState?.observe(this) {
            it.apply {
                if (isEmpty) {
                    initViewPager()
                }
                success.apply {
                    articleUserLimit = this == null || this.type == null
                    initViewPager()
                }
                netError?.run {
                    showToast(this)
                    initViewPager()
                }
                error?.run {
                    showToast(this)
                    initViewPager()
                }
            }
        }
    }

    private fun initViewPager() {
        mBinding?.apply {
            mViewModel?.getPageItem(
                FragPagerItems(this@MyContentsActivity),
                articleUserLimit
            )
                ?.apply {
                    pagerAdapter = FragPagerItemAdapter(supportFragmentManager, this)
                    viewPager.apply {
                        adapter = pagerAdapter
                    }
                    tableLayout.apply {
                        setViewPager(viewPager)
                        setSelectedAnim()
                    }
                }
            viewPager.currentItem =
                typeArray.indexOf(
                    intent.getLongExtra(
                        KEY_CONTENT_TYPE,
                        CONTENT_TYPE_ARTICLE
                    )
                )
        }
    }
}