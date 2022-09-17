package com.kotlin.android.community.ui.person.binder

import android.view.View
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.router.provider.ticket.ITicketProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityTimeLineBinding
import com.kotlin.android.community.ui.person.bean.WantSeeViewBean
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by wangwei on 2022/4/7
 * description: 想看看过binder
 */
class CommunityTimeLineBinder(val bean: WantSeeViewBean) :
    MultiTypeBinder<ItemCommunityTimeLineBinding>() {
    override fun layoutId(): Int = R.layout.item_community_time_line
    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityTimeLineBinder && other.bean.movieId != bean.movieId
    }

    override fun onPreBindViewHolder(binding: ItemCommunityTimeLineBinding, position: Int) {
        super.onPreBindViewHolder(binding, position)
        updateUI(binding, bean)
    }

    override fun onBindViewHolder(binding: ItemCommunityTimeLineBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        updateUI(binding, bean)
    }

    private fun updateUI(binding: ItemCommunityTimeLineBinding, bean: WantSeeViewBean) {
        binding?.apply {
            when (bean.attitude) {//1:购票2:想看3:看过
                1L -> {//购票
                    binding?.des.text = getString(R.string.community_buy_ticket)
                    shoHideView(binding, false)
                }
                2L -> {//想看
                    binding?.des.text = getString(R.string.community_mark)
                    binding?.tvScoreComment.text = getString(R.string.wantsee)
                    shoHideView(binding, true)
                }
                3L -> {//看过
                    binding?.des.text = getString(R.string.community_mark)
                    binding?.tvScoreComment.text =
                        getString(R.string.wantseed_two) + " " + bean.ratingFinal + " " + bean.getRatingHintText()
                    shoHideView(binding, true)
                }
            }
        }
    }

    //根据长短影评内容显示view
    private fun showContentView(binding: ItemCommunityTimeLineBinding) {
        if (bean.shortInteractiveObj?.mixWord?.isEmpty() == true) {
            binding?.tvFilmDes.gone()
        }
        if (bean.longInteractiveObj?.mixWord?.isEmpty() == true) {
            binding?.divider.gone()
            binding?.tvFilmComment.gone()
        }
    }

    private fun shoHideView(binding: ItemCommunityTimeLineBinding, show: Boolean) {
        if (show) {
            binding?.tvScoreComment.visible()
            if (bean.shortInteractiveObj != null && bean.shortInteractiveObj?.mixWord?.isNotEmpty() == true)
                binding?.tvFilmDes.visible()
            else binding?.tvFilmDes.gone()
            if (bean.longInteractiveObj != null && bean.longInteractiveObj?.title?.isNotEmpty() == true) {
                binding?.divider.visible()
                binding?.tvFilmComment.visible()
            } else {
                binding?.divider.gone()
                binding?.tvFilmComment.gone()
            }
        } else {
            binding?.tvScoreComment.gone()
            binding?.tvFilmDes.gone()
            binding?.divider.gone()
            binding?.tvFilmComment.gone()
        }
    }

    override fun onClick(view: View) {
        if (view.id == R.id.wantSeeRootView) {
            getProvider(ITicketProvider::class.java) {
                startMovieDetailsActivity(bean.movieId)
            }
        } else if (view.id == R.id.tv_film_comment) {//长影评
            getProvider(IUgcProvider::class.java) {
                launchDetail(bean.longInteractiveObj?.contentId ?: 0, CONTENT_TYPE_FILM_COMMENT)
            }
        } else if (view.id == R.id.tv_film_des) {//短影评
            getProvider(IUgcProvider::class.java) {
                launchDetail(bean.shortInteractiveObj?.contentId ?: 0, CONTENT_TYPE_FILM_COMMENT)
            }
        } else {
            super.onClick(view)
        }
    }

}