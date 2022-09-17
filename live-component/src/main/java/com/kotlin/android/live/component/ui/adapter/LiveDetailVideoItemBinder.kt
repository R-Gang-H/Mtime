package com.kotlin.android.live.component.ui.adapter

import android.view.View
import androidx.core.view.isVisible
import com.kk.taurus.playerbase.utils.TimeUtil
import com.kotlin.android.app.data.entity.live.LiveDetail
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.time.TimeExt.millis2String
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.databinding.ItemLiveDetailVideoBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 直播详情页_相关视频
 */
class LiveDetailVideoItemBinder(val bean: LiveDetail.Video) :
        MultiTypeBinder<ItemLiveDetailVideoBinding>() {

    companion object {
        const val CORNER = 4        // dp
        const val STROKE_WIDTH = 8  // px
        const val PADDING_5 = 5     // dp
        const val PADDING_15 = 15   // dp
    }

    var mClickCallBack: ILiveDetailClickCallBack? = null

    override fun layoutId(): Int {
        return R.layout.item_live_detail_video
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveDetailVideoItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemLiveDetailVideoBinding, position: Int) {
        // item padding
        var lastPosition = getItemCount() - 1
        when(position) {
            0 -> {
                binding.mItemLiveDetailVideoCl.setPadding(PADDING_15.dp, 0, PADDING_5.dp, 0)
            }
            lastPosition -> {
                binding.mItemLiveDetailVideoCl.setPadding(PADDING_5.dp, 0, PADDING_15.dp, 0)
            }
            else -> {
                binding.mItemLiveDetailVideoCl.setPadding(PADDING_5.dp, 0, PADDING_5.dp, 0)
            }
        }
        // 选中状态边框
        binding.mItemLiveDetailVideoBorderView?.let {
            it.isVisible = bean.isSelect
            if(bean.isSelect) {
                ShapeExt.setShapeCorner2Color2Stroke(it, R.color.transparent,
                        CORNER,                 // dp
                        R.color.color_feb12a,
                        STROKE_WIDTH,           // px
                        false)
            }
        }
        // 播放按钮
        binding.mItemLiveDetailVideoPlayIconIv?.let {
            it.isVisible = bean.isSelect
        }
        // 时长
        binding.mItemLiveDetailVideoLengthTv?.let {
            it.isVisible = bean.isSelect
            it.text = if(bean.isSelect && bean.length > 0) {
                val millis = bean.length * 1000L
                TimeUtil.getTime(TimeUtil.getFormat(millis), millis)
            } else ""
        }
    }

    override fun onClick(view: View) {
        mClickCallBack?.let {
            it.clickCallBack(bean, mPosition)
        }
    }

    interface ILiveDetailClickCallBack {
        fun clickCallBack(bean: LiveDetail.Video, position: Int)
    }
}