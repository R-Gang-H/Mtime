package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.Activitys
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.time.TimeExt.millis2String
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemContributeSubjectBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/25
 * 描述:本期主题
 **/
class ContributeSubjectBinder(val bean: Activitys) :
    MultiTypeBinder<ItemContributeSubjectBinding>() {
    private val mPattern = "yyyy/MM/dd"
    override fun layoutId() = R.layout.item_contribute_subject

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is ContributeSubjectBinder
    }

    override fun onBindViewHolder(binding: ItemContributeSubjectBinding, position: Int) {
        binding.apply {
            mSubjectCoverIv.loadImage(
                bean.coverUrl,
                screenWidth - 30.dp,
                96.dp,
                roundedRadius = 10.dpF
            )
            mTimeTv.text = String.format(
                getString(R.string.tablet_film_list_time_scope),
                millis2String(bean.startTime ?: 0L, mPattern),
                millis2String(bean.endTime ?: 0L, mPattern)
            )
        }
    }


}