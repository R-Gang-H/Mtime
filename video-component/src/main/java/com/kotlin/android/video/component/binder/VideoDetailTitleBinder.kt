package com.kotlin.android.video.component.binder

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.StyleSpan
import android.view.View
import com.kotlin.android.mtime.ktx.getString

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.video.component.R
import com.kotlin.android.video.component.adapter.VIDEO_DETAIL_PRESELL
import com.kotlin.android.video.component.adapter.VIDEO_DETAIL_TICKET
import com.kotlin.android.video.component.databinding.ItemPreVideoTitleBinding
import com.kotlin.android.video.component.viewbean.PreVideoCommentTitleBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2020/9/2
 * description: 预告片详情 title和相关影片
 */
class VideoDetailTitleBinder(var bean: PreVideoCommentTitleBean) : MultiTypeBinder<ItemPreVideoTitleBinding>() {
    private var ticketProvider = getProvider(ITicketProvider::class.java)
    override fun layoutId(): Int = R.layout.item_pre_video_title

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is VideoDetailTitleBinder && other.bean != bean
    }

    override fun onBindViewHolder(binding: ItemPreVideoTitleBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        处理评分
        binding.scoreTv.text = if (TextUtils.isEmpty(bean.movie?.score)) {
            ""
        } else {
            getScoreSpannableString()
        }

    }

    fun setAttitude(attitude:Long){
        bean.movie?.attitude = attitude
        notifyAdapterSelfChanged()
    }

    /**
     * 设置评分数字样式
     */
    private fun getScoreSpannableString(): SpannableString {
        val score = getString(R.string.video_mtime_score_format).format(bean.movie?.score)
        return SpannableString(score).apply {
            setSpan(AbsoluteSizeSpan(15, true), 4, score.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            setSpan(StyleSpan(Typeface.BOLD_ITALIC), 4, score.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.movieCL -> {//影片，跳转到影片详情
                ticketProvider?.startMovieDetailsActivity(bean.movie?.movieId ?: 0L)
            }
            R.id.movieBtnFl -> {//购票按钮
                when (bean.getMovieBuyState()) {
                    VIDEO_DETAIL_TICKET, VIDEO_DETAIL_PRESELL -> {//购票和预售
                        ticketProvider?.startMovieDetailsActivity(bean.movie?.movieId ?: 0L)
                    }
                    else -> {
                        super.onClick(view)
                    }
                }
            }
            else -> super.onClick(view)
        }

    }
}