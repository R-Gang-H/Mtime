package com.kotlin.android.ugc.detail.component.binder

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import com.kotlin.android.app.router.provider.main.IMainProvider
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.LinkActorViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemUgcLinkPersonBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by lushan on 2022/3/15
 * des:关联影人
 **/
class UgcLinkActorBinder(var bean:LinkActorViewBean):
    MultiTypeBinder<ItemUgcLinkPersonBinding>() {
    override fun layoutId(): Int  = R.layout.item_ugc_link_person

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is UgcLinkActorBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemUgcLinkPersonBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        if (bean.score.isNotEmpty()) {
            val score =
                getString(R.string.ugc_detail_actor_score_format).format(bean.score)
            SpannableStringBuilder(score).apply {
                var indexOf = score.indexOf(bean.score)
                setSpan(
                    ForegroundColorSpan(getColor(R.color.color_20a0da)),
                    indexOf,
                    indexOf + bean.score.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
                setSpan(
                    StyleSpan(Typeface.BOLD),
                    indexOf,
                    indexOf + bean.score.length,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }.also {
                binding.personScoreTv.text = it
            }
        } else {
            binding.personScoreTv.text = ""
        }
    }

    override fun onClick(view: View) {
        when(view.id){
            R.id.personContentCl->{
                getProvider(IMainProvider::class.java){
                    startActorViewActivity(bean.personId,bean.name)
                }
            }
            else->{
                super.onClick(view)
            }
        }

    }
}