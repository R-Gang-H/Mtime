package com.kotlin.android.image.component.ui.adapter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.camera.openCameraWithPermissions
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.photo.setThumbnail
import com.kotlin.android.image.component.ui.PhotoAlbumFragment
import com.kotlin.android.ktx.ext.core.getGradientDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import kotlinx.android.synthetic.main.item_photo_album.view.*


/**
 * 相册适配器
 *
 * Created on 2020/7/23.
 *
 * @author o.s
 */
class PhotoAlbumAdapter(private val activity: FragmentActivity?) : RecyclerView.Adapter<PhotoAlbumAdapter.ViewHolder>() {

    /**
     * 照片选中数量的限制
     */
    private var limitPhotoSelectCount = PhotoAlbumFragment.MAX_LIMITED

    private val span = 4
    private val edge = 15.dp
    private val gap = 10.dp
    private val mItemWidth = (screenWidth - edge * 2 - gap * (span - 1)) / span
    private val mItemHeight = mItemWidth
    private val mCameraPadding = 28.dp

    private val data: ArrayList<PhotoInfo> = ArrayList()

    var actionSelect: ((selectCount: Int) -> Unit)? = null

    /**
     * 外部选中的照片
     */
    var outSelectedPhotos: ArrayList<PhotoInfo> = ArrayList()

    /**
     * 选中的照片
     */
    val selectedPhotos: ArrayList<PhotoInfo>
        get() {
            return selected
        }

    fun setLimitPhotoSelectCount(limitedCount:Long = PhotoAlbumFragment.MAX_LIMITED){
        this.limitPhotoSelectCount = limitedCount
    }
    /**
     * 内部处理选中的照片
     */
    private val selected = ArrayList<PhotoInfo>()

    fun setData(data: ArrayList<PhotoInfo>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
        syncSelected()
    }

    fun addData(data: ArrayList<PhotoInfo>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun addItem(photoInfo: PhotoInfo) {
        data.add(0, photoInfo)
        selectedItem(photoInfo)
        if (data.size == span) {
            notifyDataSetChanged()
        } else {
            notifyItemInserted(1)
        }
    }

    private fun updateCount() {
        actionSelect?.invoke(selected.size)
    }

    private val defDrawable = getGradientDrawable(color = getColor(R.color.color_f2f3f6), cornerRadius = 4.dpF)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = if (viewType == 1) {
            ImageView(parent.context).apply {
                layoutParams = RecyclerView.LayoutParams(mItemWidth, mItemHeight)
                setPadding(mCameraPadding, mCameraPadding, mCameraPadding, mCameraPadding)
                background = defDrawable
                setImageResource(R.drawable.ic_camera)
                setOnClickListener {
                    activity?.openCameraWithPermissions()
                }
            }
        } else {
            View.inflate(parent.context, R.layout.item_photo_album, null).apply {
                layoutParams = RecyclerView.LayoutParams(mItemWidth, mItemHeight)
                icon?.setOnClickListener {
                    itemClick(it)
                }
//                background = defDrawable
            }
        }
        return ViewHolder(view, defDrawable)
    }

    override fun getItemCount(): Int = data.size + 1

    override fun getItemViewType(position: Int): Int = if (position == 0) {
        1
    } else {
        0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 1 until itemCount) {
            holder.bindData(data[position - 1], position)
        }
    }

    /**
     * 同步外部传入的照片列表选中的状态
     */
    private fun syncSelected() {
        "sync outSelectedPhotos = $outSelectedPhotos".i()
        outSelectedPhotos.forEach { selectedPhoto ->
            data.forEachIndexed continuing@{ index, photo ->
                if (selectedPhoto.id == photo.id) {
                    photo.isCheck = true
                    photo.update(selectedPhoto)
                    selected.add(photo)
                    notifyItemChanged(getItemPosition(index))
                    return@continuing
                }
            }
        }
        updateCount()
    }

    /**
     * 获取通知的合理位置，首位为照相机，data数据列表下标对应ItemPosition做相应的后移
     */
    private fun getItemPosition(index: Int) = index + 1

    /**
     * 处理点击事件
     */
    private fun itemClick(view: View) {
        val photo = view.tag as? PhotoInfo
        photo?.apply {
            val position = getItemPosition(data.indexOf(this)) // view.getTag(R.string.all) as? Int ?: 0
            if (isCheck) {
                unSelectedItem(this)
                notifyItemChanged(position)
            } else {
                val count = selected.size
                if (count < limitPhotoSelectCount) {
                    selectedItem(this)
                    notifyItemChanged(position)
                } else {
                    showToast(getString(R.string.select_photo_limit).format(limitPhotoSelectCount))
                }
            }
        }
    }

    /**
     * 选择照片
     */
    private fun selectedItem(photo: PhotoInfo) {
        photo.isCheck = true
        selected.add(photo)
        updateCount()
    }

    /**
     * 取消选择照片
     */
    private fun unSelectedItem(photo: PhotoInfo) {
        photo.isCheck = false
        selected.remove(photo)
        updateCount()
    }

    class ViewHolder(view: View, private val defDrawable: Drawable) : RecyclerView.ViewHolder(view) {

        fun bindData(data: PhotoInfo, position: Int) {
            itemView.icon?.apply {
                if (data != tag) {
                    clearAnimation()
                    setImageDrawable(defDrawable)
                }
                tag = data
                setThumbnail(data, playAnim = true)
            }
            itemView.action?.visibility = if (data.isCheck) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }
}