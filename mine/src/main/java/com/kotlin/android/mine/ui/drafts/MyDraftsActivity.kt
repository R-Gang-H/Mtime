package com.kotlin.android.mine.ui.drafts

import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ActivityMyDraftsBinding
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.TitleBarManager

@Route(path = RouterActivityPath.Mine.PAGE_MY_DRAFTS_ACTIVITY)
class MyDraftsActivity : BaseVMActivity<MyDraftsViewModel, ActivityMyDraftsBinding>() {

    private var articleUserLimit: Boolean = true

    override fun initVM(): MyDraftsViewModel = viewModels<MyDraftsViewModel>().value

    override fun initCommonTitleView() {
        super.initCommonTitleView()
        TitleBarManager.with(this)
                .setTitle(titleRes = R.string.mine_drafts)
                .addItem(drawableRes = R.drawable.ic_title_bar_36_back) {
                    onBackPressed()
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
        mViewModel?.getPageItem(FragPagerItems(this@MyDraftsActivity), articleUserLimit)
                ?.apply {
                    val pagerAdapter = FragPagerItemAdapter(supportFragmentManager, this)
                    mBinding?.apply {
                        viewPager?.apply {
                            adapter = pagerAdapter
                        }
                        tableLayout?.apply {
                            setViewPager(viewPager)
                            setSelectedAnim()
                        }
                    }
                }
    }
}