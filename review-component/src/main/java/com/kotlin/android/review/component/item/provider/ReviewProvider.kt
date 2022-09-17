package com.kotlin.android.review.component.item.provider

import android.content.Context
import android.os.Bundle
import androidx.collection.arrayMapOf
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.review.component.item.ui.detail.ReviewDetailActivity
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.review.component.item.ui.share.ReviewShareActivity
import com.kotlin.android.router.RouterManager
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.review.IReviewProvider
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_NEED_TO_COMMENT
import com.kotlin.android.ugc.detail.component.ui.UGC_DETAIL_REC_ID

/**
 * create by lushan on 2020/8/10
 * description:
 */
@Route(path = RouterProviderPath.Provider.PROVIDER_REVIEW)
class ReviewProvider :IReviewProvider{
    override fun startReviewDetail(contentId: Long, type: Long, recId: Long, needToComment: Boolean) {
        Bundle().put(arrayMapOf(ReviewDetailActivity.REVIEW_CONTENT_ID to contentId,ReviewDetailActivity.REVIEW_TYPE to type,
        UGC_DETAIL_REC_ID to recId,
        UGC_DETAIL_NEED_TO_COMMENT to needToComment)).also {
            RouterManager.instance.navigation(RouterActivityPath.Review.PAGE_REVIEW_DETAIL_ACTIVITY,it)
        }
    }

    override fun startReviewShare(contentId: Long, isPublished: Boolean, isShowEditBtn: Boolean) {
        Bundle().put(arrayMapOf(
            ReviewShareActivity.REVIEW_CONTENT_ID to contentId,
            ReviewShareActivity.REVIEW_IS_PUBLISHED to isPublished,
            ReviewShareActivity.REVIEW_IS_SHOW_EDIT_BTN to isShowEditBtn
        )).also {
            RouterManager.instance.navigation(RouterActivityPath.Review.PAGE_REVIEW_SHARE_ACTIVITY,it)
        }
    }

    override fun startMovieReviewList(movieId: String, title: String) {
        Bundle().put(arrayMapOf(MovieReviewConstant.KEY_MOVIE_ID to movieId, MovieReviewConstant.KEY_MOVIE_TITLE to title)).also {
            RouterManager.instance.navigation(RouterActivityPath.Review.PAGE_MOVIE_REVIEW_LIST_ACTIVITY, it)
        }
    }

    override fun startMovieShortCommentList(movieId: String, title: String) {
        Bundle().put(arrayMapOf(MovieReviewConstant.KEY_MOVIE_ID to movieId,
                MovieReviewConstant.KEY_MOVIE_TITLE to title))
                .also {
            RouterManager.instance.navigation(RouterActivityPath.Review.PAGE_MOVIE_SHORT_COMMENT_LIST_ACTIVITY, it)
        }
    }

    override fun startMovieRatingDetail(movieId: Long) {
        Bundle().put(arrayMapOf(MovieReviewConstant.KEY_MOVIE_ID to movieId))
                .also {
                    RouterManager.instance.navigation(RouterActivityPath.Review.PAGE_MOVIE_RATING_DETAIL_ACTIVITY, it)
                }
    }

    override fun init(context: Context?) {
    }

}