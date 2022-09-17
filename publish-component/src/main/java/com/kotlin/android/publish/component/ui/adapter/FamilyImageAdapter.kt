package com.kotlin.android.publish.component.ui.adapter

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.community.record.Image
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.showPhotoAlbumFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.R
import kotlinx.android.synthetic.main.item_publish_family.view.*

/**
 * 日志/家族帖子封面图集适配器
 *
 * Created on 2020/7/22.
 *
 * @author o.s
 */
class FamilyImageAdapter(private val activity: FragmentActivity?, private val maxSize: Long = 10) : RecyclerView.Adapter<FamilyImageAdapter.ViewHolder>() {
    private val data: ArrayList<PhotoInfo> = ArrayList()

    /**
     * 条目变化监听
     */
    var itemChange: ((dx: Int) -> Unit)? = null

    /**
     * 封面变化监听
     */
    var coverChange: ((hasCover: Boolean) -> Unit)? = null

    fun setData(data: ArrayList<PhotoInfo>) {
        coverChange?.invoke(data.size > 0)
        this.data.apply {
            clear()
            addAll(data)
        }
        notifyDataSetChanged()
    }

    /**
     * 封面合集图片列表
     */
    fun getImages(): List<Image> {
        return data.map {
            Image(
                    imageId = it.fileID,
                    imageUrl = it.url,
                    imageFormat = it.imageFormat,
                    isGif = false
            )
        }
    }

    fun addData(itemData: PhotoInfo) {
        data.add(itemData)
        val position = data.indexOf(itemData)
        notifyItemInserted(position)
        itemChange?.invoke(90.dp)
    }

    fun removeItemAt(itemData: PhotoInfo) {
        val position = data.indexOf(itemData)
        if (position in 0 until data.size) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(View.inflate(parent.context, R.layout.item_publish_family, null)).apply {
            actionDelete = {
                removeItemAt(it)
            }
            actionAdd = {
                takePhotos(data) {
                    setData(it)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (data.size >= maxSize) {
            data.size
        } else {
            data.size + 1
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until data.size) {
            holder.bindData(data[position], position)
        } else {
            holder.addView()
        }
    }

    private fun takePhotos(selectedData: ArrayList<PhotoInfo>, completed: (ArrayList<PhotoInfo>) -> Unit) {
        activity?.showPhotoAlbumFragment(
                isUploadImageInComponent = true,
                limitedCount = maxSize
        )?.apply {
            selectedPhotos = selectedData
            actionSelectPhotos = { photos ->
                completed.invoke(photos)
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var actionDelete: ((data: PhotoInfo) -> Unit)? = null
        var actionAdd: (() -> Unit)? = null

        fun bindData(data: PhotoInfo, position: Int) {
            itemView.icon?.apply {
                tag = data
                loadImage(data = data.url, width = 80.dp, height = 80.dp)
//                setThumbnail(data)
                onClick {
                }
            }
            itemView.delete?.apply {
                if (position == 0) {
                    gone()
                } else {
                    visible()
                }
                onClick {
//                    toast("删除图片")
                    actionDelete?.invoke(data)
                }
            }
            itemView.titleView?.apply {
                setBackground(
                        colorRes = R.color.color_6620a0da,
                        cornerRadius = 4.dpF,
                        direction = Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
                )
                if (position == 0) {
                    visible()
                } else {
                    gone()
                }
            }
        }

        fun addView() {
            itemView.icon?.apply {
                loadImage(
                    data = R.drawable.ic_publish_family_add,
                    defaultImgRes = R.drawable.ic_publish_family_add)
                onClick {
                    actionAdd?.invoke()
                }
            }
            itemView.delete?.apply {
                gone()
            }
        }
    }
}