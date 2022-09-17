package com.kotlin.android.image.component.ui.adapter

import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.R

/**
 * 相册大图（画廊）适配器
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
class PhotoGalleryAdapter(val action: (data: PhotoInfo, position: Int) -> Unit) : RecyclerView.Adapter<PhotoGalleryAdapter.ViewHolder>() {

    var photos: ArrayList<PhotoInfo>? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AppCompatImageView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                adjustViewBounds = true
            },
            action = action
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            photos?.apply {
                holder.bindData(this[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return photos?.size ?: 0
    }

    class ViewHolder(
        val icon: AppCompatImageView,
        val action: (data: PhotoInfo, position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(icon) {

        fun bindData(data: PhotoInfo, position: Int) {
            icon.loadImage(data = data.uri, defaultImgRes = R.drawable.default_image_black)
            itemView.setOnClickListener {
                action(data, position)
            }
        }
    }
}