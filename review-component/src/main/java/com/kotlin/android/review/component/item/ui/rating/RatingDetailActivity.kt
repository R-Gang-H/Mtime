package com.kotlin.android.review.component.item.ui.rating

import android.content.Intent
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.topStatusMargin
import com.kotlin.android.review.component.databinding.ActRatingDetailBinding
import com.kotlin.android.review.component.item.adapter.RatingRatiosAdapter
import com.kotlin.android.review.component.item.adapter.RatingSubRatingAdapter
import com.kotlin.android.review.component.item.bean.RatingDetailBean
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.multistate.MultiStateView

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/24
 */
@Route(path = RouterActivityPath.Review.PAGE_MOVIE_RATING_DETAIL_ACTIVITY)
class RatingDetailActivity : BaseVMActivity<RatingDetailViewModel, ActRatingDetailBinding>(), MultiStateView.MultiStateListener {
    private var mMovieId = 0L

    override fun getIntentData(intent: Intent?) {
        intent?.run {
            mMovieId = getLongExtra(MovieReviewConstant.KEY_MOVIE_ID, 0)
        }
    }

    override fun initView() {
        mBinding?.mMultiStateView?.setMultiStateListener(this)
        mBinding?.mRatingDetailBackIv?.run {
            topStatusMargin()
            onClick {
                finish()
            }
        }
    }

    override fun initData() {
        mViewModel?.getMovieRatingDetail(mMovieId)
    }

    override fun startObserve() {
        mViewModel?.uiSate?.observe(this) {
            it.run {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showUI(this)
                }

                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }
    }

    private fun showUI(detail: RatingDetailBean) {
        mBinding?.apply {
            mRatingDetailPicIv.loadImage(
                    data = detail.img,
                    width = screenWidth.dp,
                    height = 440.dp
            )
            mRatingDetailScoreTv.text = detail.overallRating.toString()
            mRatingDetailScoreCountTv.text = detail.ratingCountShow + "人评分"
            mRatingDetailRatingRatiosRv.adapter = RatingRatiosAdapter().setData(detail.ratingCountRatios)

            var isSubRatingModel = false
            run breaking@{
                detail.movieSubItemRatings.forEach { item ->
                    val rating = item.rating
                    if (rating > 0f) {
                        isSubRatingModel = true
                        return@breaking
                    }
                }
            }
            if (!isSubRatingModel) {
                mRatingDetailSubScoreTitleTv.gone()
                mRatingDetailSubScoreCountTv.gone()
            } else {
                mRatingDetailSubScoreCountTv.text = detail.subItemRatingCountShow + "人评分"
                mRatingDetailSubScoreRv.adapter = RatingSubRatingAdapter().setData(detail.movieSubItemRatings)
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        if (viewState == MultiStateView.VIEW_STATE_ERROR
                || viewState == MultiStateView.VIEW_STATE_NO_NET) {
            initData()
        }
    }
}