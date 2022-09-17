package com.kotlin.android.ugc.detail.component.binder

import android.graphics.drawable.GradientDrawable
import android.view.View
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.comment.component.helper.setExpandVisibility
import com.kotlin.android.comment.component.helper.setTvUnderline
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.UgcMediaInfoViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcMediaInfoBinding
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/16
 * des:视频、音频详情
 **/
class UgcMediaInfoBinder(var bean: UgcMediaInfoViewBean) :
    MultiTypeBinder<ItemUgcMediaInfoBinding>() {
    private val MAX_LINE = 3
    override fun layoutId(): Int = R.layout.item_ugc_media_info

    fun updateFollow() {
        bean.isFollow = !bean.isFollow
        if (bean.isFollow) {
            bean.fansCount++
        } else {
            bean.fansCount--
        }
        notifyAdapterSelfChanged()
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcMediaInfoBinder && other.bean.isFollow == bean.isFollow && other.bean.playCount == bean.playCount
    }

    override fun onBindViewHolder(binding: ItemUgcMediaInfoBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.moreTv.setTvUnderline(R.string.ugc_detail_component_more)
        binding.contentTv.setExpandVisibility(binding.moreTv, MAX_LINE)

        initAttention(binding)
    }

    private fun initAttention(binding: ItemUgcMediaInfoBinding) {
        binding.attentionFL.apply {
            if (bean.isFollow || bean.userId == UserManager.instance.userId || bean.userId == 0L) {//关注或者是自己的都不展示
                gone()
                ShapeExt.setShapeCorner2Color2Stroke(
                    this,
                    R.color.color_ffffff,
                    30,
                    R.color.color_feb12a,
                    1
                )

            } else {
                visible()
                ShapeExt.setGradientColor(
                    this,
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    R.color.color_20a0da,
                    R.color.color_1bafe0,
                    30
                )
            }
        }

        binding.attentionBtn?.apply {
            if (bean.isFollow) {
                setTextColorRes(R.color.color_feb12a)
                setCompoundDrawablesAndPadding(
                    leftResId = R.drawable.ic_checkb,
                    padding = 3
                )
            } else {
                setTextColorRes(R.color.color_ffffff)
                setCompoundDrawables(null, null, null, null)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.userCardView -> {//跳转到个人主页
                getProvider(ICommunityPersonProvider::class.java) {
                    startPerson(bean.userId)
                }

            }
            R.id.moreTv -> {//更多、收起
                if (binding?.moreTv?.text?.toString() == getString(R.string.ugc_detail_component_more)) {
                    binding?.moreTv?.setTvUnderline(R.string.ugc_detail_component_pack_up)
                    binding?.contentTv?.maxLines = Int.MAX_VALUE
                } else {
                    binding?.moreTv?.setTvUnderline(R.string.ugc_detail_component_more)
                    binding?.contentTv?.maxLines = MAX_LINE
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}