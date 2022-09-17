package com.kotlin.android.publish.component.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatTime
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ItemVideoSelectBinding
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.router.ext.getProvider

/**
 * create by lushan on 2022/4/6
 * des:
 **/
class VideoListAdapter(var ctx: Activity?, var list: ArrayList<LocalMedia> = arrayListOf(),var toVideoPublish:Boolean = true) :
    RecyclerView.Adapter<VideoListAdapter.VideoListViewHolder>() {
    private val layoutInflater = LayoutInflater.from(ctx)


    inner class VideoListViewHolder(var binding: ItemVideoSelectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val span = 3
        private val edge = 15.dp
        private val gap = 7.dp
        private val mItemWidth = (screenWidth - edge * 2 - gap * (span - 1)) / span
        private val mItemHeight = mItemWidth
        fun bindData(bean: LocalMedia?, index: Int) {
            bean ?: return
            with(bean) {
                binding.root.layoutParams =
                    RecyclerView.LayoutParams(mItemWidth, mItemHeight).apply {
                        marginStart = gap
                        bottomMargin = gap
                    }
                binding.durationTv.text = formatTime(duration.orZero() / 1000)
                binding.fgView.setBackground(endColorRes = R.color.color_80000000)
                binding.action.setBackgroundResource(if (isChecked) R.drawable.ic_check_orange else R.drawable.ic_un_check)
                binding.root.onClick {
                    var lastIndex = list.indexOfFirst { it.isChecked }
                    list.forEach {
                        it.isChecked = false
                    }
                    isChecked = true

                    notifyItemChanged(index)
                    if (lastIndex>=0) {
                        notifyItemChanged(lastIndex)
                    }
                    //跳转到预览页面
                    getProvider(IPublishProvider::class.java){
                        startPreviewVideoActivity(ctx,bean,toVideoPublish = toVideoPublish)
                    }
                }
                //加载视频缩略图
                binding.videoIv.loadImage(realPath, isLoadVideo = true, useProxy = false)

            }
        }
    }

    fun setData(list: ArrayList<LocalMedia>,isToVideoPublish:Boolean = true) {
        this.toVideoPublish = isToVideoPublish
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addData(list: ArrayList<LocalMedia>,isToVideoPublish:Boolean = true){
        this.toVideoPublish = isToVideoPublish
        this.list.addAll(list)
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoListViewHolder {
        return VideoListViewHolder(ItemVideoSelectBinding.inflate(layoutInflater))
    }

    override fun onBindViewHolder(holder: VideoListViewHolder, position: Int) {
        holder.bindData(list[position], position)
    }

    override fun getItemCount(): Int = list.size
}