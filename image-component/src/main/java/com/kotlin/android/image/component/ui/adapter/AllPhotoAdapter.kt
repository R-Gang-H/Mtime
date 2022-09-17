package com.kotlin.android.image.component.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.component.*
import com.kotlin.android.image.component.camera.openCameraWithPermissions
import com.kotlin.android.image.component.databinding.ItemPhotoBinding
import com.kotlin.android.image.component.ext.loadThumbnail
import com.kotlin.android.image.component.photo.PhotoBucket
import com.kotlin.android.image.component.selectPhoto
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList

/**
 * 手机相册适配器（全相册，默认"最近项目"）
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
class AllPhotoAdapter : RecyclerView.Adapter<AllPhotoAdapter.ViewHolder>() {

    /**
     * 限制照片选中数量
     */
    var limitPhotoSelectCount = 9

    /**
     * 是否需要上传图片
     */
    var needUpload: Boolean = false

    /**
     * 上传成功的照片列表
     */
    var actionSuccessPhotos: ((ArrayList<PhotoInfo>) -> Unit)? = null

    /**
     * 是否显示照相机
     */
    private var hasCamera = false

    @SuppressLint("NotifyDataSetChanged")
    fun addPhoto(photo: PhotoInfo) {
        photoMap?.onEach { (key, value) ->
            if (key.name == PHOTO_RECENT || key.name == PHOTO_CAMERA) {
                value.add(0, photo)
            }
        }
        notifyDataSetChanged()
        selectPhoto(
            photo = photo,
            count = selectedPhotos.size,
            limitCount = limitPhotoSelectCount
        )
    }

    /**
     * 所有相册数据
     */
    var photoMap: LinkedHashMap<PhotoBucket, ArrayList<PhotoInfo>>? = null
        set(value) {
            field = value
            value?.onEach { (key, _) ->
                key.isCheck = false
                if (key.name == PHOTO_RECENT) {
                    key.isCheck = true
                    selectPhotoBucket = key
                }
            }
        }

    /**
     * 当前选中的相册
     */
    var selectPhotoBucket: PhotoBucket? = null
        set(value) {
            field = value
            value?.apply {
                photoMap?.forEach { (key, value) ->
                    if (key.id == id) {
                        key.isCheck = true
                        hasCamera = (key.name == PHOTO_CAMERA || key.name == PHOTO_RECENT)
                        photos = value
                    }
                }
            }
        }

    /**
     * 当前相册下的照片列表
     */
    var photos: ArrayList<PhotoInfo>? = null
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged() // 不可用动画通知，有越界问题
        }

    /**
     * 缓存上一次选中的照片列表
     */
    private var preSelectedPhotos: ArrayList<PhotoInfo> = ArrayList()

    /**
     * 当前选中的照片列表
     */
    var selectedPhotos: ArrayList<PhotoInfo> = ArrayList()
        set(value) {
            field = value
            // 处理取消选中的照片
            preSelectedPhotos.forEach {
                "preSelectedPhotos $it".e()
                if (!it.isCheck) {
                    notifyItemChanged(photo = it)
                }
            }
            // 处理新添加选中的照片
            value.forEach {
                "value $it".w()
                if (!preSelectedPhotos.contains(it)) {
                    notifyItemChanged(photo = it)
                }
            }

            // 同步选中数据
            preSelectedPhotos.clear()
            preSelectedPhotos.addAll(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            action = { photo ->
                val activity = parent.getActivity() as? FragmentActivity
                if (photo == null) {
                    activity?.openCameraWithPermissions()
                } else {
                    // 点击进入大图选择器
                    activity?.showDialogFragment(
                        PhotoGalleryDialogFragment::class.java
                    )?.apply {
                        limitPhotoSelectCount = this@AllPhotoAdapter.limitPhotoSelectCount
                        needUpload = this@AllPhotoAdapter.needUpload
                        actionSuccessPhotos = this@AllPhotoAdapter.actionSuccessPhotos
                        currentPhoto = photo
                        this@AllPhotoAdapter.photos?.apply {
                            val temp = ArrayList<PhotoInfo>()
                            val list = ArrayList<PhotoInfo>()
                            // 将所有选中的没有在photos列表中的照片，添加到开头
                            this@AllPhotoAdapter.selectedPhotos.forEach {
                                if (!contains(it)) {
                                    temp.add(it)
                                }
                            }

                            list.addAll(temp)
                            list.addAll(this)
                            photos = list
                        }
                    }
                }
            },
            selectAction = { photo ->
                selectPhoto(
                    photo = photo,
                    count = selectedPhotos.size,
                    limitCount = limitPhotoSelectCount
                )
            }
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position in 0 until itemCount) {
            val index = getItemIndex(position)
            if (index < 0) {
                holder.bindData(null)
            } else {
                holder.bindData(photos?.get(index))
            }
        }
    }

    override fun getItemCount(): Int {
        val offset = if (hasCamera) {
            1
        } else {
            0
        }
        return (photos?.size ?: 0) + offset
    }

    /**
     * 通知照片改变（选中/取消）
     */
    private fun notifyItemChanged(photo: PhotoInfo) {
        val index = photos?.indexOf(photo) ?: -1
        if (index >= 0) {
            notifyItemChanged(getItemPosition(index))
        }
    }

    /**
     * 获取通知的合理位置，首位为照相机，data数据列表下标对应ItemPosition做相应的后移
     */
    private fun getItemPosition(index: Int): Int {
        val offset = if (hasCamera) {
            1
        } else {
            0
        }
        return index + offset
    }

    /**
     * 获取通知的合理位置，首位为照相机，ItemPosition对应data数据列表下标做相应的前移
     */
    private fun getItemIndex(position: Int): Int {
        val offset = if (hasCamera) {
            1
        } else {
            0
        }
        return position - offset
    }

    class ViewHolder(
        val binding: ItemPhotoBinding,
        val action: (PhotoInfo?) -> Unit,
        val selectAction: (PhotoInfo) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.action.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_check_26_normal,
                        checkedRes = R.drawable.ic_check_26_selected,
                        selectedRes = R.drawable.ic_check_26_selected,
                    ).apply {
                        setBounds(0, 0, 26.dp, 26.dp)
                    }
                )
            }
        }

        fun bindData(data: PhotoInfo?) {
            if (data != null) {
                binding.camera.isVisible = false
                binding.cover.isVisible = data.isCheck
                binding.icon.apply {
                    if (data != tag) {
                        setImageDrawable(null)
                    }
                    tag = data
                    context.loadThumbnail(data) { bitmap ->
                        if (data == tag) {
                            bitmap?.let {
                                setImageBitmap(it)
                            }
                        }
                    }
                }
                binding.action.apply {
                    isVisible = true
                    isSelected = data.isCheck
                    setOnClickListener {
                        selectAction(data)
                    }
                }
            } else {
                binding.camera.isVisible = true
                binding.cover.isVisible = false
                binding.icon.setImageDrawable(null)
                binding.action.isVisible = false
            }
            itemView.setOnClickListener {
                action(data)
            }
        }
    }
}