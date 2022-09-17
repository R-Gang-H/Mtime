package com.kotlin.android.image.component.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.PHOTO_CAMERA
import com.kotlin.android.image.component.PHOTO_RECENT
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.databinding.ItemPhotoBucketBinding
import com.kotlin.android.image.component.ext.loadThumbnail
import com.kotlin.android.image.component.photo.PhotoBucket
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 相册分组适配器
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
class PhotoBucketAdapter(
    val action: (PhotoBucket) -> Unit
) : RecyclerView.Adapter<PhotoBucketAdapter.ViewHolder>() {

    /**
     * 同步相机拍照照片（最新）
     */
    fun syncCamera(photo: PhotoInfo) {
        data?.forEachIndexed { index, photoBucket ->
            if (photoBucket.name == PHOTO_CAMERA
                || photoBucket.name == PHOTO_RECENT
            ) {
                photoBucket.photoInfo = photo
            }
            notifyItemChanged(index)
        }
    }

    var data: ArrayList<PhotoBucket>? = null
        set(value) {
            field = value
            notifyItemRangeChanged(0, itemCount)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemPhotoBucketBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            selectAction = { photoBucket, position ->
                photoBucket.apply {
                    data?.forEachIndexed { index, bucket ->
                        if (bucket.isCheck) {
                            bucket.isCheck = false
                            notifyItemChanged(index)
                        }
                    }
                    photoBucket.isCheck = true
                    action(photoBucket)
                    notifyItemChanged(position)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            data?.apply {
                holder.bindData(this[position], position)
            }
        }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    class ViewHolder(
        val binding: ItemPhotoBucketBinding,
        val selectAction: (PhotoBucket, Int) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.apply {
                background = getDrawableStateList(
                    normal = getShapeDrawable(colorRes = R.color.color_ffffff),
                    checked = getShapeDrawable(colorRes = R.color.color_f2f3f6),
                    selected = getShapeDrawable(colorRes = R.color.color_f2f3f6),
                )
            }
        }

        fun bindData(data: PhotoBucket, position: Int) {
            binding.icon.apply {
                if (data != tag) {
                    setImageDrawable(null)
                }
                tag = data
                data.photoInfo?.apply {
                    context.loadThumbnail(data = this) { bitmap ->
                        if (data == tag) {
                            bitmap?.let {
                                setImageBitmap(it)
                            }
                        }
                    }
                }
            }
            binding.title.text = "${data.name} (${data.count})"
            binding.action.isVisible = data.isCheck
            itemView.isSelected = data.isCheck
            itemView.setOnClickListener {
                selectAction(data, position)
            }
        }
    }
}