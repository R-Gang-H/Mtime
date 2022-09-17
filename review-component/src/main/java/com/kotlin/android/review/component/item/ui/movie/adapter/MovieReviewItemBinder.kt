package com.kotlin.android.review.component.item.ui.movie.adapter

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.review.MovieReview
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.review.component.R
import com.kotlin.android.review.component.databinding.ItemMovieReviewBinding
import com.kotlin.android.review.component.item.ui.movie.constant.MovieReviewConstant
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.user.isLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class MovieReviewItemBinder(val bean: MovieReview,
                            private val isLong: Boolean,
                            private val totalCount: Long) : MultiTypeBinder<ItemMovieReviewBinding>() {

    private val mCommunityPersonProvider = getProvider(ICommunityPersonProvider::class.java)
    private val mUgcProvider = getProvider(IUgcProvider::class.java)

    var mIMovieReviewActionCallBack: IMovieReviewActionCallBack ?= null

    override fun layoutId(): Int {
        return R.layout.item_movie_review
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is MovieReviewItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemMovieReviewBinding, position: Int) {

        /**
         * 未审核影评：不显示用户关系，已购票，点赞，点踩，评论
         */

        var ticketWidth = 0
        var relationWidth = 0
        var levelWidth = 0
        // 其他应该是105 设置成110让评分与用户等级图标间距大一些
        var otherWidth = 110.dp
        val spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

        // 认证类型Icon
        binding.mItemMovieReviewUserInc.mItemMovieReviewUserAuthTypeIv?.let {
            var showAuthType = false
            bean.mvpType?.let { mvpType ->
                // 仅显示：2影评人，3电影人，4机构，
                showAuthType = mvpType > CommConstant.AUTH_TYPE_PERSONAL
                        && mvpType <= CommConstant.AUTH_TYPE_ORGAN
            }
            it.isVisible = showAuthType
            if(showAuthType) {
                // 影评人和电影人是蓝色，机构认证是黄色
                it.setImageResource(if (bean.mvpType == CommConstant.AUTH_TYPE_ORGAN)
                    R.drawable.ic_jigourenzheng else R.drawable.ic_yingrenrenzheng)
            }
        }
        // 已购票
        binding.mItemMovieReviewUserInc.mItemMovieReviewUserTicketTv?.let {
            var showTicket = !bean.isAudit && bean.isTicket == true
            it.isVisible = showTicket
            if(showTicket) {
                // 背景
                ShapeExt.setShapeCorner2Color2Stroke(it, R.color.color_20a0da_alpha_10,
                        MovieReviewConstant.USER_TAB_CORNER,
                        R.color.color_caedfc,
                        MovieReviewConstant.USER_TAB_STROKE_WIDTH,
                        false)
                it.measure(spec, spec)
                ticketWidth = it.measuredWidth + 5.dp
            }
        }
        // 用户关系：未登录时，标签部分不展示用户关系标签（指：好友、粉丝、已关注）
        binding.mItemMovieReviewUserInc.mItemMovieReviewUseRelationTv?.let {
            // 背景
            ShapeExt.setShapeCorner2Color2Stroke(it, R.color.color_20a0da_alpha_10,
                    MovieReviewConstant.USER_TAB_CORNER,
                    R.color.color_caedfc,
                    MovieReviewConstant.USER_TAB_STROKE_WIDTH,
                    false)
            // 关系
            var showRelation = false
            if(isLogin() || !bean.isAudit) {
                bean.userRelation?.let { relation ->
                    showRelation = relation >= MovieReview.USER_RELATION_FRIEND
                            && relation <= MovieReview.USER_RELATION_FOLLOW
                    if (showRelation) {
                        when (relation) {
                            MovieReview.USER_RELATION_FRIEND -> {
                                it.text = getString(R.string.movie_review_user_relation_friend)
                            }
                            MovieReview.USER_RELATION_FANS -> {
                                it.text = getString(R.string.movie_review_user_relation_fans)
                            }
                            MovieReview.USER_RELATION_FOLLOW -> {
                                it.text = getString(R.string.movie_review_user_relation_follow)
                            }
                            else -> {
                                it.text = ""
                            }
                        }
                    }
                }
            }
            it.isVisible = showRelation
            it.measure(spec, spec)
            relationWidth = if(showRelation) (it.measuredWidth + 5.dp) else 0
        }
        // 会员等级
        binding.mItemMovieReviewUserInc.mItemMovieReviewUserLevelIv?.let {
            var showLevel = false
            bean.memberLevel?.let { level ->
                showLevel = level >= CommConstant.MEMBER_LEVEL_PRIMARY
                        && level <= CommConstant.MEMBER_LEVEL_HALL
                if(showLevel) {
                    when (level) {
                        CommConstant.MEMBER_LEVEL_PRIMARY -> {
                            //入门
                            it.setImageResource(R.drawable.ic_rumen)
                        }
                        CommConstant.MEMBER_LEVEL_MIDDLE -> {
                            //中级
                            it.setImageResource(R.drawable.ic_zhongji)
                        }
                        CommConstant.MEMBER_LEVEL_HIGHT -> {
                            //高级
                            it.setImageResource(R.drawable.ic_gaoji)
                        }
                        CommConstant.MEMBER_LEVEL_SENIOR -> {
                            //资深
                            it.setImageResource(R.drawable.ic_zishen)
                        }
                        CommConstant.MEMBER_LEVEL_HALL -> {
                            //殿堂
                            it.setImageResource(R.drawable.ic_diantang)
                        }
                        else -> {
                        }
                    }
                }
            }
            it.isVisible = showLevel
            levelWidth = if(showLevel) 28.dp else 0
        }
        // 昵称最大长度
        binding.mItemMovieReviewUserInc.mItemMovieReviewUserNicknameTv?.let {
            var maxWidth = screenWidth - ticketWidth - relationWidth - levelWidth - otherWidth
            it.maxWidth = maxWidth
        }
        // 评分
        binding.mItemMovieReviewUserInc.mItemMovieReviewUserRatingTv?.let {
            var showRating = false
            bean.rating?.let { ratingStr ->
                var rating = ratingStr.toFloat()
                if(rating > 0) {
                    showRating = true
                    if(rating >= MovieReviewConstant.RATING_MAX) {
                        it.text = "10"
                    } else {
                        it.text = String.format("%.1f", rating)
                    }
                }
            }
            it.isVisible = showRating
        }
        // 标题
        binding.mItemMovieReviewTitleTv?.let {
            it.isVisible = isLong
        }
        // 内容
        binding.mItemMovieReviewContentTv?.let {
            it.maxLines = if (isLong) MovieReviewConstant.REVIEW_CONTENT_MAX_LINE
            else MovieReviewConstant.SHORT_COMMENT_CONTENT_MAX_LINE
        }
        // 长影评_点赞，点踩，评论
        binding.mItemMovieReviewActionGroup?.let {
            if(bean.isAudit) {
                // 未审核
                it.isVisible = false
            } else {
                // 底部分隔线参照id，需要占位
                it.isInvisible = !isLong
            }
        }
        // 短影评_点赞，评论
        binding.mItemMovieReviewShortActionGroup?.let {
            it.isGone = bean.isAudit || isLong
        }
        // 底部分隔线
        binding.mItemMovieReviewLineView.isVisible = position < totalCount - 1
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.mItemMovieReviewUserInc -> {
                // 点击"用户信息"模块
                bean.userId?.let {
                    // 跳转个人主页
                    mCommunityPersonProvider?.startPerson(it, 0L)
                }
            }
            R.id.mItemMovieReviewTitleTv, R.id.mItemMovieReviewContentTv,
            R.id.mItemMovieReviewCommentIv, R.id.mItemMovieReviewCommentCountTv,
            R.id.mItemMovieReviewShortCommentIv, R.id.mItemMovieReviewShortCommentCountTv -> {
                // 影评详情页
                bean.commentId?.let {
                    mUgcProvider?.launchDetail(it, CommConstant.PRAISE_OBJ_TYPE_FILM_COMMENT,
                            0L, false)
                }
            }
            R.id.mItemMovieReviewPraiseTv, R.id.mItemMovieReviewShortPraiseTv -> {
                // 赞
                mIMovieReviewActionCallBack?.let {
                    it.praiseCallBack(bean, mPosition, true)
                }
            }
            R.id.mItemMovieReviewPraiseDownTv -> {
                // 踩
                mIMovieReviewActionCallBack?.let {
                    it.praiseCallBack(bean, mPosition, false)
                }
            }
            R.id.mItemMovieReviewUserRatingTv -> {
                // 评分（没有跳转）
            }
            else -> {
                // 列表页面处理其他点击事件
                super.onClick(view)
            }
        }
    }

    interface IMovieReviewActionCallBack {

        /**
         * 赞/取消赞(踩/取消踩)
         * @param isPraiseUp  true赞操作 false踩操作
         */
        fun praiseCallBack(bean: MovieReview, position: Int, isPraiseUp: Boolean)

    }
}