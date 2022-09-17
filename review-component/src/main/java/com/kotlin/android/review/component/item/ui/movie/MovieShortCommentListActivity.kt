package com.kotlin.android.review.component.item.ui.movie

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.review.component.databinding.ActMovieShortCommentListBinding
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.kotlin.android.widget.titlebar.CommonTitleBar
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.act_movie_short_comment_list.*

/**
 * @author vivian.wei
 * @date 2021/1/4
 * @desc 影片短影评列表页
 */
@Route(path = RouterActivityPath.Review.PAGE_MOVIE_SHORT_COMMENT_LIST_ACTIVITY)
class MovieShortCommentListActivity : BaseVMActivity<BaseViewModel, ActMovieShortCommentListBinding>() {

    private var mMovieId: String = ""
    private var mTitle: String = ""

    override fun initVM(): BaseViewModel = viewModels<BaseViewModel>().value

    override fun initVariable() {
        super.initVariable()

        intent?.let {
            mMovieId = it.getStringExtra(MovieReviewConstant.KEY_MOVIE_ID) ?: ""
            mTitle = it.getStringExtra(MovieReviewConstant.KEY_MOVIE_TITLE) ?: ""
        }
    }

    override fun initCommonTitleView() {
        // title
        CommonTitleBar().init(this)
                .setTitle(mTitle)
                .create()
    }

    override fun initView() {
        val adapter = FragPagerItemAdapter(
                supportFragmentManager, FragPagerItems(baseContext)
                .add(title = "最热", clazz = MovieShortCommentListFragment::class.java,
                        args = Bundle().put(MovieReviewConstant.KEY_MOVIE_ID, mMovieId)
                                .put(MovieReviewConstant.KEY_ORDER_TYPE, MovieReviewConstant.SHORT_COMMENT_ORDER_TYPE_HOT))
                .add(title = "最新", clazz = MovieShortCommentListFragment::class.java,
                        args = Bundle().put(MovieReviewConstant.KEY_MOVIE_ID, mMovieId)
                                .put(MovieReviewConstant.KEY_ORDER_TYPE, MovieReviewConstant.SHORT_COMMENT_ORDER_TYPE_NEW))
                )
        mActMovieShortCommentListViewPager.adapter = adapter
        mTabLayout.setViewPager(mActMovieShortCommentListViewPager)
        mTabLayout.setSelectedAnim()
    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}