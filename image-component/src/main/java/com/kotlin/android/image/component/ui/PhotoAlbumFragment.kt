package com.kotlin.android.image.component.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.app.data.annotation.UploadImageType
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.image.component.R
import com.kotlin.android.image.component.camera.onActivityResultCameraData
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.image.component.photo.getAllPhotosWithPermissions
import com.kotlin.android.image.component.ui.adapter.PhotoAlbumAdapter
import com.kotlin.android.image.component.ui.decoration.PhotoAlbumItemDecoration
import com.kotlin.android.image.component.viewmodel.PhotoAlbumViewModel
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import kotlinx.android.synthetic.main.layout_photo_album.*
import java.lang.Exception

/**
 * 相册：
 *
 * Created on 2020/7/23.
 *
 * @author o.s
 */
class PhotoAlbumFragment : DialogFragment {
    companion object {
        const val KEY_PHOTO_LIMITED_COUNT = "key_photo_limited_count"
        const val KEY_PHOTO_UPLOAD_IMAGE_TYPE = "key_photo_upload_image_type"
        const val KEY_PHOTO_IS_UP_LOAD_IN_IMAGE = "key_photo_is_up_load_in_image"
        const val MAX_LIMITED = 10L
    }

    constructor(isUploadImageInComponent: Boolean = false, imageFileType: Long = CommConstant.IMAGE_UPLOAD_COMMON, limitedCount: Long = MAX_LIMITED) : super() {
        Bundle().apply {
            putBoolean(KEY_PHOTO_IS_UP_LOAD_IN_IMAGE, isUploadImageInComponent)
            putLong(KEY_PHOTO_UPLOAD_IMAGE_TYPE, imageFileType)
            putLong(KEY_PHOTO_LIMITED_COUNT, limitedCount)
        }.also {
            arguments = it
        }
    }

    constructor() : super()

    private var isUploadImageInComponent: Boolean = false // 是否在本fragment中点击确认后直接上传图片
    @UploadImageType
    private var uploadImageType: Long = CommConstant.IMAGE_UPLOAD_COMMON // 上传图片业务类型
    private var limitedCount: Long = MAX_LIMITED // 选中图片最多张数
    private val spanCount = 4
    private val mAdapter: PhotoAlbumAdapter by lazy { PhotoAlbumAdapter(activity) }
    private var viewModel: PhotoAlbumViewModel? = null
    private var selectCount = 0 // 选中图片数量
    private var successCount = 0 // 上传成功的张数
    private var uploadCount = 0 // 正在上传张数
    private var completedCount = 0 // 上传完成的张数

    var actionSelectPhotos: ((photos: ArrayList<PhotoInfo>) -> Unit)? = null
    var dismissEvent: (() -> Unit)? = null
    var cancelEvent: (() -> Unit)? = null

    /**
     * 选中的照片
     */
    var selectedPhotos: ArrayList<PhotoInfo>
        set(value) {
            mAdapter.outSelectedPhotos = value
        }
        get() = mAdapter.selectedPhotos

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        isUploadImageInComponent = args?.getBoolean(KEY_PHOTO_IS_UP_LOAD_IN_IMAGE, false) ?: false
        uploadImageType = args?.getLong(KEY_PHOTO_UPLOAD_IMAGE_TYPE, CommConstant.IMAGE_UPLOAD_COMMON)
                ?: CommConstant.IMAGE_UPLOAD_COMMON
        limitedCount = args?.getLong(KEY_PHOTO_LIMITED_COUNT, MAX_LIMITED) ?: MAX_LIMITED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ImmersiveDialogKeep)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setWindowAnimations(R.style.BottomDialogAnimation)
        dialog?.window?.decorView?.apply {
            systemUiVisibility = systemUiVisibility or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_photo_album, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.apply {
            activity?.immersive(this)
                ?.statusBarColor(getColor(R.color.color_ffffff))
                ?.statusBarDarkFont(true)
        }

        viewModel = viewModels<PhotoAlbumViewModel>().value

        initTitleView()
        initView()

        loadPhotos()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissEvent?.invoke()
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD)
            setState(State.NORMAL)
            setTitle(
                    title = getString(R.string.camera_roll),
                    gravity = Gravity.CENTER
            )
            addItem(
                drawableRes = R.drawable.ic_title_bar_close,
                reverseDrawableRes = R.drawable.ic_title_bar_close
            ) {
                cancelEvent?.invoke()
                dismissAllowingStateLoss()
            }
        }
    }

    private fun initView() {
        initRecyclerView()

        shadow?.setBackground(
                colorRes = R.color.color_f9f9f9,
                endColorRes = R.color.color_ffffff
        )
        confirm?.apply {
            text = getString(R.string.confirm_photo, limitedCount)
            setBackground(colorRes = R.color.color_20a0da, cornerRadius = 20.dpF)
            mAdapter.actionSelect = {
                text = getString(R.string.confirm_photo_, it, limitedCount)
            }
            setOnClickListener {
                selectCount = selectedPhotos.size
                successCount = 0
                uploadCount = 0
                completedCount = 0

                if (selectedPhotos.isNotEmpty()) {
                    if (isUploadImageInComponent) {
                        uploadImage()
                    } else {
                        callback()
                    }
                } else {
                    showToast(R.string.please_select_photo)
                }
            }
        }

        photoUploadStartObserve()
    }

    private fun initRecyclerView() {
        recyclerView?.apply {
            layoutManager = GridLayoutManager(context, spanCount, RecyclerView.VERTICAL, false)
            addItemDecoration(PhotoAlbumItemDecoration(spanCount))
            mAdapter.setLimitPhotoSelectCount(limitedCount)
            adapter = mAdapter
        }
    }

    private fun loadPhotos() {
        activity?.getAllPhotosWithPermissions {
            mAdapter.setData(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResultCameraData(requestCode, resultCode, data) { path, uri ->
            try {
                uri.lastPathSegment?.apply {
                    val id = if (this.contains(":")) {
                        substring(indexOf(":") + 1).toLong()
                    } else {
                        toLong()
                    }
                    val photoInfo = PhotoInfo(id = id, uri = uri, path = path, uploadPath = path)
                    "添加照片信息 $photoInfo".i()
                    recyclerView?.postDelayed({
                        mAdapter.addItem(photoInfo)
                    }, 300)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun photoUploadStartObserve() {
        viewModel?.uploadImageState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                    completedCount++
                }
                success?.apply {
                    this.e()
                    if (success) {
                        successCount++
                    }
                }

                if (isUploadFinished()){
                    if (isUploadPerfect()) {
                        callback()
                    } else {
                        if (isAllFailed()) {
                            showToast(R.string.upload_image_fail_please_retry)
                        } else {
                            selectedPhotos.removeIf { photoInfo ->
                                !photoInfo.success
                            }
                            callback()
                        }
                    }
                }
            }
        }
    }

    /**
     * 只要有一个成功就是返回 false
     */
    private fun isAllFailed(): Boolean {
        selectedPhotos.forEach {
            if (it.success) {
                return false
            }
        }
        return true
    }
    /**
     * 同步上传图片全部失败的UI
     */
    private fun syncUploadAllFailedUI() {
        mAdapter.notifyDataSetChanged()
        confirm?.text = getString(R.string.confirm_photo_, 0, limitedCount)
    }

    /**
     * 上传图片
     */
    private fun uploadImage() {
        selectedPhotos.forEach {
            if (it.success) {
                successCount++
            } else {
                uploadCount++
                viewModel?.uploadImage(uploadImageType, it)
            }
        }
        if (isUploadPerfect()) {
            callback()
        }
    }

    /**
     * 回调选中的照片列表
     */
    private fun callback() {
        actionSelectPhotos?.invoke(selectedPhotos)
        dismissAllowingStateLoss()
    }

    /**
     * 是否上传成功
     */
    private fun isUploadFinished():Boolean = completedCount == uploadCount

    /**
     * 是否完美上传
     */
    private fun isUploadPerfect(): Boolean = successCount == selectCount

}