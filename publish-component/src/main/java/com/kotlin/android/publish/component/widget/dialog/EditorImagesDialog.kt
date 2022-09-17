package com.kotlin.android.publish.component.widget.dialog

import android.view.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.image.component.PhotoAlbumDialogFragment
import com.kotlin.android.ktx.ext.core.*
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.publish.component.Publish.MAX_IMAGES_LIMIT
import com.kotlin.android.publish.component.Publish.MAX_UPLOAD_IMAGES_LIMIT
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.DialogEditorImagesBinding
import com.kotlin.android.publish.component.databinding.ItemEdtiorImagesBinding

/**
 * 图片集合对话框
 *
 * Created on 2022/4/15.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_fragment_editor_images_dialog")
class EditorImagesDialog : BaseVMDialogFragment<BaseViewModel, DialogEditorImagesBinding>() {

    private val mAdapter by lazy {
        ImageAdapter().apply {
            action = {
                if (it == null) {
                    // 添加
                    val offset = MAX_IMAGES_LIMIT - photos.size
                    val limit = if (offset > MAX_UPLOAD_IMAGES_LIMIT) {
                        MAX_UPLOAD_IMAGES_LIMIT
                    } else {
                        offset
                    }
                    showDialogFragment(
                        clazz = PhotoAlbumDialogFragment::class.java
                    )?.apply {
                        needUpload = true
                        limitPhotoSelectCount = limit
                        actionSuccessPhotos = { photos ->
                            addPhotos(photos)
                            syncTitle()
                        }
                    }
                } else {
                    // 删除
                    syncTitle()
                }
            }
        }
    }

    var event: (() -> Unit)? = null

    var title: CharSequence = ""
        set(value) {
            field = value
            mBinding?.titleView?.text = value
        }

    var desc: CharSequence = ""
        set(value) {
            field = value
            mBinding?.descView?.text = value
        }

    var photos: List<PhotoInfo>
        get() = mAdapter.photos
        set(value) {
            mAdapter.addPhotos(value)
            syncTitle()
        }

    override fun initEnv() {
        window = {
            decorView.setPadding(0, 0, 0, 0)
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
    }

    override fun initView() {
        mBinding?.apply {
            rootLayout.setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            rootLayout.heightValue = screenHeight - 100.dp
            cancelView.setBackground(
                colorRes = R.color.color_00000000,
                strokeColorRes = R.color.color_20a0da,
                strokeWidth = 1.dp,
                cornerRadius = 20.dpF
            )
            okView.setBackground(
                colorRes = R.color.color_20a0da,
                cornerRadius = 20.dpF
            )
            okView.apply {
                background = getDrawableStateList(
                    normal = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 20.dpF
                    ),
                    pressed = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 20.dpF
                    ),
                    disable = getShapeDrawable(
                        colorRes = R.color.color_9920a0da,
                        cornerRadius = 20.dpF
                    ),
                )
            }
            okView.isEnabled = true
            okView.setOnClickListener {
                event?.invoke()
                dismiss()
            }
            cancelView.setOnClickListener {
                dismiss()
            }
            rootLayout.setOnClickListener {
            }
//            bgLayout.setOnClickListener {
//                dismiss()
//            }
            recyclerView.layoutManager = GridLayoutManager(context, 3)
            recyclerView.adapter = mAdapter
        }
    }

    override fun initData() {
    }

    override fun startObserve() {
    }

    private fun syncTitle() {
        title = "${photos.size}张照片"
    }

}

class ImageAdapter : RecyclerView.Adapter<ImageAdapter.Holder>() {

    var action: ((Int?) -> Unit)? = null

    val photos by lazy {
        ArrayList<PhotoInfo>()
    }

    fun addPhotos(photos: List<PhotoInfo>) {
        this.photos.addAll(photos)
//        notifyDataSetChanged()
        notifyItemRangeChanged(0, itemCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(ItemEdtiorImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
            it?.apply {
                "删除:$this".e()
                if (this >= 0 && this < photos.size) {
                    photos.removeAt(this)
                    notifyItemRemoved(this)
                    notifyItemRangeChanged(this, itemCount)
                }
            }
            action?.invoke(it)
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        if (position in 0 until photos.size) {
            holder.bindData(photos[position], position)
        } else {
            if (position == photos.size) {
                holder.showAdd()
            }
        }
    }

    override fun getItemCount(): Int {
        val size = photos.size
        return if (size < MAX_IMAGES_LIMIT) {
            size + 1 // add
        } else {
            size
        }
    }

    class Holder(
        val binding: ItemEdtiorImagesBinding,
        val action: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                delCoverView.apply {
                    setBackground(
                        colorRes = R.color.color_80000000,
                        cornerRadius = 8.dpF,
                        direction = Direction.LEFT_BOTTOM or Direction.RIGHT_TOP
                    )
                }

                coverView.apply {
                    setBackground(
                        strokeColorRes = R.color.color_e1e3ea,
                        strokeWidth = 1.dp,
                        dashWidth = 4.dpF,
                        dashGap = 4.dpF,
                        cornerRadius = 8.dpF,
                    )
                    setOnClickListener {
                        if (addView.isVisible) {
                            action(null)
                        }
                    }
                }
            }
        }

        fun bindData(photoInfo: PhotoInfo, position: Int) {
            binding.apply {
                addView.isVisible = false
                delCoverView.isVisible = true
                val tempTag = photoInfo.uri?.toString() ?: photoInfo.url
                coverView.tag = tempTag
                loadImage(data = photoInfo.uri ?: photoInfo.url) {
                    val tag = coverView.tag as? String
                    if (tag == tempTag) {
                        coverView.setImageDrawable(it)
                    }
                }
                delCoverView.setOnClickListener {
                    action(position)
                }
            }
        }

        fun showAdd() {
            binding.apply {
                addView.isVisible = true
                delCoverView.isVisible = false
                // 防图片错乱
                coverView.tag = "add"
                coverView.setImageDrawable(null)
                coverView.postDelayed({
                    coverView.setImageDrawable(null)
                }, 200)
            }
        }
    }
}