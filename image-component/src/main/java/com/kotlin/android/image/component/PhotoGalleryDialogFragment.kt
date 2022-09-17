package com.kotlin.android.image.component

import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.DialogStyle
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.component.databinding.DialogPhotoGalleryBinding
import com.kotlin.android.image.component.photo.PhotoData
import com.kotlin.android.image.component.ui.adapter.PhotoSelectedAdapter
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor

/**
 * 相册库（画廊）
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_photo_gallery_dialog")
class PhotoGalleryDialogFragment : BaseVMDialogFragment<BaseViewModel, DialogPhotoGalleryBinding>() {

    private val mLayoutManager by lazy { LinearLayoutManager(context, RecyclerView.HORIZONTAL, false) }

    private val mSelectedAdapter by lazy {
        PhotoSelectedAdapter { photo, _ ->
            val position = photos?.indexOf(photo) ?: 0
            currentPosition = position
            syncPosition()
            if (position >= 0) {
                mBinding?.photoGalleryView?.scrollToPosition(position)
            }
        }
    }

    private var isReady = false

    /**
     * 当前照片位置 index
     */
    private var currentPosition = 0

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
     * 是否编辑模式
     */
    var isEditStyle = true
        set(value) {
            field = value
            mBinding?.apply {
                headerLayout.isVisible = isEditStyle
                selectRecyclerView.isVisible = isEditStyle
                footerLayout.isVisible = isEditStyle
            }
        }

    /**
     * 当前可见照片（大图）
     *
     * 1，点击照片进入预览的，不一定是选中的
     * 2，为null时，默认取选中照片列表的第一个图片
     */
    var currentPhoto: PhotoInfo? = null
        get() {
            if (field == null) {
                field = selectedPhotos.firstOrNull()
            }
            return field
        }
        set(value) {
            field = value
            // 同步当前获取焦点的照片
            syncFocusPhoto()
        }

    /**
     * 选中的照片
     */
    var selectedPhotos: ArrayList<PhotoInfo>
        get() = mSelectedAdapter.selectedPhotos
        set(value) {
            mSelectedAdapter.selectedPhotos = value
            if (currentPhoto == null) {
                currentPhoto = selectedPhotos.firstOrNull()
            }
        }

    /**
     * 照片集合
     */
    var photos: ArrayList<PhotoInfo>? = null
        set(value) {
            field = value
            fillData()
        }

    override fun initEnv() {
        window = {
            setWindowAnimations(R.style.RightDialogAnimation)
        }
        theme = {
            setStyle(STYLE_NO_TITLE, R.style.ImmersiveDialog)
        }
        immersive = {
            dialog?.apply {
                activity?.immersive(this)?.fullscreen()
            }
//            immersive().fullscreen()
        }
    }

    override fun initView() {
        initPhotoView()
        initHeaderView()
        initFooterView()

        isReady = true
        fillData()
    }

    override fun initData() {
    }

    override fun startObserve() {
        PhotoData.instance.selectedLiveData.observe(this) {
            it?.apply {
                selectedPhotos = this
                syncUIState(selectedPhotos = this)
            }
        }
        PhotoData.instance.originalLiveData.observe(this) {
            it?.apply {
                mBinding?.original?.isSelected = it
                showSize(isShow = it)
            }
        }
    }

    private fun initPhotoView() {
        mBinding?.apply {
            photoGalleryView.apply {
                itemChange = { position, _ ->
                    currentPosition = position
                    syncPosition()
                }
                action = { _, _ ->
                    isEditStyle = !isEditStyle
                }
            }
            selectRecyclerView.apply {
                layoutManager = mLayoutManager
//                PagerSnapHelper().attachToRecyclerView(this)
                adapter = mSelectedAdapter
                mSelectedAdapter.recyclerView = this
            }
        }
    }

    private fun initHeaderView() {
        mBinding?.apply {
            back.setOnClickListener {
                dismiss()
            }
            select.apply {
                setImageDrawable(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_check_26_normal,
                        checkedRes = R.drawable.ic_check_26_selected,
                        selectedRes = R.drawable.ic_check_26_selected,
                    ).apply {
                        setBounds(0, 0, 26.dp, 26.dp)
                    }
                )
                setOnClickListener {
                    clickSelect()
                }
            }
        }
    }

    private fun initFooterView() {
        mBinding?.apply {
            edit.apply {
                setOnClickListener {
                    showDialogFragment(
                        clazz = PhotoCropDialogFragment::class.java
                    )?.apply {
                        photo = currentPhoto
                    }
                }
            }
            original.apply {
                setCompoundDrawables(
                    getDrawableStateList(
                        normalRes = R.drawable.ic_check_26_normal_2,
                        checkedRes = R.drawable.ic_check_26_selected,
                        selectedRes = R.drawable.ic_check_26_selected,
                    ).apply {
                        setBounds(0, 0, 16.dp, 16.dp)
                    },
                    null,
                    null,
                    null
                )
                setOnClickListener {
                    PhotoData.instance.syncOriginalState(!it.isSelected)
                }
            }
            confirm.apply {
                background = getDrawableStateList(
                    normal = getShapeDrawable(
                        colorRes = R.color.color_20a0da,
                        cornerRadius = 14.dpF,
                    ),
                    checked = getShapeDrawable(
                        colorRes = R.color.color_0084ef,
                        cornerRadius = 14.dpF,
                    ),
                    disable = getShapeDrawable(
                        colorRes = R.color.color_6620a0da,
                        cornerRadius = 14.dpF,
                    ),
                )
                setTextColor(
                    getColorStateList(
                        normalColorRes = R.color.color_ffffff,
                        disableColorRes = R.color.color_f2f3f6
                    )
                )
                isEnabled = false
                setOnClickListener {
                    if (needUpload) {
                        uploadPhotos(
                            photos = selectedPhotos,
                            progressBar = {
                                (getActivity() as? FragmentActivity)?.showProgressDialog(isShow = it)
                            },
                            error = {
                                if (it.size == selectedPhotos.size) {
                                    showToast(R.string.upload_image_fail_please_retry)
                                }
                            },
                        ) {
                            actionSuccessPhotos?.invoke(it)
                            dismiss()
                        }
                    } else {
                        actionSuccessPhotos?.invoke(selectedPhotos)
                        dismiss()
                    }
                }
            }
        }
    }

    private fun fillData() {
        if (isReady) {
            mBinding?.apply {
                photoGalleryView.photos = photos
                var position = photos?.indexOf(currentPhoto) ?: 0
                if (position < 0) {
                    position = 0
                }
                photoGalleryView.scrollToPosition(position)
                syncFocusPhoto()
            }
        }
    }

    /**
     * 点击（切换）选中状态
     */
    private fun clickSelect() {
        mBinding?.apply {
            currentPhoto?.let { photo ->
                if (
                    selectPhoto(
                        photo = photo,
                        count = selectedPhotos.size,
                        limitCount = limitPhotoSelectCount
                    )
                ) {
                    select.postDelayed({
                        syncFocusPhoto()
                    }, 50)
                }
            }
        }
    }

    /**
     * 同步焦点照片
     */
    private fun syncFocusPhoto() {
        currentPhoto?.apply {
            mBinding?.select?.isSelected = isCheck
            mSelectedAdapter.focusPhoto(this)
        }
    }

    /**
     * 同步当前位置的照片情况
     */
    private fun syncPosition() {
        currentPhoto = photos?.get(currentPosition)
    }

    /**
     * 同步UI状态：按钮可选状态，计数等
     */
    private fun syncUIState(selectedPhotos: ArrayList<PhotoInfo>) {
        mBinding?.apply {
            val size = selectedPhotos.size
            syncConfirm(size = size)
            showSize(isShow = original.isSelected)
        }
    }

    /**
     * 同步确认按钮
     */
    private fun syncConfirm(size: Int) {
        mBinding?.confirm?.apply {
            isEnabled = size > 0
            text = if (isEnabled) "确定($size)" else "确定"
        }
    }

    /**
     * 显示原图大小
     */
    private fun showSize(isShow: Boolean) {
        mBinding?.apply {
            size.isVisible = isShow
            if (isShow) {
                size.text = selectOriginalTotalSize(selectedPhotos)
            }
        }
    }
}