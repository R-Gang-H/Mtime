package com.kotlin.android.image.component.ui.adapter

import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.photo.PhotoCropStyle
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.getColor

/**
 * 照片裁剪样式适配器
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
class PhotoCropStyleAdapter : RecyclerView.Adapter<PhotoCropStyleAdapter.ViewHolder>() {

    var data = ArrayList<PhotoCropStyle>().apply {
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_add, title = "自由"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_back, title = "1:1"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_scan, title = "3:2"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_filter, title = "2:3"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_help, title = "4:3"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_message, title = "3:4"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_info, title = "16:9"))
        add(PhotoCropStyle(icon = R.drawable.ic_title_bar_36_fans, title = "9:16"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            AppCompatTextView(parent.context).apply {
                layoutParams = ViewGroup.LayoutParams(60.dp, 60.dp)
            }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            holder.bindData(data[position], position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(
        val view: AppCompatTextView,
    ) : RecyclerView.ViewHolder(view) {

        init {
            view.apply {
                setTextColor(getColor(R.color.color_ffffff))
                textSize = 12F
                compoundDrawablePadding = 2.dp
                gravity = Gravity.CENTER
            }
        }

        fun bindData(data: PhotoCropStyle, position: Int) {
            view.apply {
                view.setCompoundDrawables(null, getCompoundDrawable(data.icon), null, null)
                text = data.title
            }
        }
    }
}