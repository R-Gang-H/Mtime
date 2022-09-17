package com.kotlin.android.image.component.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.databinding.ItemPhotoSelectedBinding
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.getColor

/**
 * 相册选中照片列表适配器
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
class PhotoSelectedAdapter(
    val action: (PhotoInfo, Int) -> Unit
) : RecyclerView.Adapter<PhotoSelectedAdapter.ViewHolder>() {

    var recyclerView: RecyclerView? = null

    /**
     * 同步照片的取消/选中
     */
//    fun syncPhoto(photo: PhotoInfo) {
//        if (selectedPhotos.contains(photo)) {
//            if ((!photo.isCheck)) {
//                val position = selectedPhotos.indexOf(photo)
//                selectedPhotos.removeAt(position)
//                notifyItemRemoved(position)
//                notifyItemRangeChanged(position, itemCount - 1)
//            }
//        } else {
//            if (photo.isCheck) {
//                selectedPhotos.add(photo)
//                notifyItemChanged(itemCount - 1)
//            }
//        }
//    }

    /**
     * 获取焦点的照片
     */
    fun focusPhoto(photo: PhotoInfo): Int {
        photo.isFocus = true
        val position = photos.indexOf(photo)
        notifyFocusChanged(photo, position)
        return position
    }

    /**
     * 通知焦点改变。
     *
     * 注意：没有选中的照片获取焦点也不会高亮
     */
    private fun notifyFocusChanged(photo: PhotoInfo, position: Int) {
        photos.forEachIndexed { index, info ->
            if (info.isFocus && photo.id != info.id) {
                info.isFocus = false
                notifyItemChanged(index)
            }
        }
        if (position >= 0) {
            notifyItemChanged(position)
            recyclerView?.smoothScrollToPosition(position)
        }
    }

    /**
     * 缓存上一次选中的照片列表
     */
    private var photos: ArrayList<PhotoInfo> = ArrayList()

    /**
     * 当前选中的照片列表
     */
    var selectedPhotos: ArrayList<PhotoInfo> = ArrayList()
        set(value) {
            field = value
            // 处理取消选中的照片
            removeItem()

            // 处理新添加选中的照片
            value.forEach {
                "value $it".w()
                if (!photos.contains(it)) {
                    photos.add(it)
                    notifyItemInserted(itemCount)
                }
            }
        }

    private fun removeItem() {
        val photo = photos.findLast { !it.isCheck }
        if (photo != null) {
            val position = photos.indexOf(photo)
            if (position >= 0) {
                photos.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, itemCount)
                removeItem()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPhotoSelectedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//            AppCompatImageView(parent.context).apply {
//                layoutParams = ViewGroup.MarginLayoutParams(50.dp, 50.dp).apply {
//                    setMargins(3.dp, 0, 3.dp, 0)
//                }
//                setPadding(2.dp)
//                scaleType = ImageView.ScaleType.CENTER_CROP
//            }
        ) { photo, position ->
            notifyFocusChanged(photo = photo, position = position)
            action(photo, position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            holder.bindData(photos[position], position)
        }
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    class ViewHolder(
        val binding: ItemPhotoSelectedBinding,
        val action: (PhotoInfo, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.content.foreground = getDrawableStateList(
                normal = getGradientDrawable(
                    color = getColor(R.color.transparent),
                ),
                checked = getGradientDrawable(
                    color = getColor(R.color.transparent),
                    strokeColor = getColor(R.color.color_20a0da),
                    strokeWidth = 4.dp,
                    cornerRadius = 2.dpF,
                ),
                selected = getGradientDrawable(
                    color = getColor(R.color.transparent),
                    strokeColor = getColor(R.color.color_20a0da),
                    strokeWidth = 4.dp,
                    cornerRadius = 2.dpF,
                ),
            )
        }

        fun bindData(data: PhotoInfo, position: Int) {
            binding.icon.apply {
                loadImage(data = data.uri, defaultImgRes = R.drawable.default_image_black)
                // 获取焦点，并且是选中的照片
                binding.content.isSelected = data.isFocus && data.isCheck
                setOnClickListener {
                    data.isFocus = true
                    action(data, position)
                }
            }
        }
    }
}