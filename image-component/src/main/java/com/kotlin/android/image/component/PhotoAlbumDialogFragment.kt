package com.kotlin.android.image.component

import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.kotlin.android.app.data.entity.image.PhotoInfo
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.core.ITitleBarOfFragment
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.core.ext.showDialogFragment
import com.kotlin.android.image.component.camera.onActivityResultCameraData
import com.kotlin.android.image.component.databinding.DialogPhotoAlbumBinding
import com.kotlin.android.image.component.ext.getAllPhotoMap
import com.kotlin.android.image.component.photo.PhotoBucket
import com.kotlin.android.image.component.photo.PhotoData
import com.kotlin.android.image.component.ui.adapter.AllPhotoAdapter
import com.kotlin.android.image.component.ui.adapter.PhotoBucketAdapter
import com.kotlin.android.image.component.ui.decoration.AverItemDecoration
import com.kotlin.android.ktx.ext.core.getActivity
import com.kotlin.android.ktx.ext.core.getCompoundDrawable
import com.kotlin.android.ktx.ext.core.getShapeDrawable
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.statelist.getColorStateList
import com.kotlin.android.ktx.ext.statelist.getDrawableStateList
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back
import kotlinx.android.synthetic.main.layout_photo_album.*
import java.lang.Exception

/**
 * 相册簿
 *
 * Created on 2022/5/12.
 *
 * @author o.s
 */
@DialogFragmentTag(tag = "tag_dialog_photo_album_dialog")
class PhotoAlbumDialogFragment : BaseVMDialogFragment<BaseViewModel, DialogPhotoAlbumBinding>() {

    private val span = 3

    private var titleView: View? = null

    private val mAdapter by lazy { AllPhotoAdapter() }

    private val mBucketAdapter by lazy {
        PhotoBucketAdapter {
            mAdapter.selectPhotoBucket = it
            showMenu(isShow = false)
        }
    }

    /**
     * 限制照片选中数量
     */
    var limitPhotoSelectCount = 9
        set(value) {
            field = value
            mAdapter.limitPhotoSelectCount = value
        }

    /**
     * 是否需要上传图片
     */
    var needUpload: Boolean = false
        set(value) {
            field = value
            mAdapter.needUpload = value
        }

    /**
     * 上传成功的照片列表
     */
    var actionSuccessPhotos: ((ArrayList<PhotoInfo>) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.actionSuccessPhotos = {
                value?.invoke(it)
                dismiss()
            }
        }

    /**
     * 选中的照片列表
     */
    var selectedPhotos: ArrayList<PhotoInfo>
        set(value) {
            mAdapter.selectedPhotos = value
        }
        get() = mAdapter.selectedPhotos

    override fun initEnv() {
//        dialogStyle = DialogStyle.FULL
        window = {
            setWindowAnimations(R.style.BottomDialogAnimation)
        }
        theme = {
            setStyle(STYLE_NO_TITLE, R.style.ImmersiveDialog)
        }
        immersive = {
            dialog?.apply {
                activity?.immersive(this)?.statusBarColor(getColor(R.color.color_ffffff))
                    ?.statusBarDarkFont(true)
            }
        }
    }

    override fun initTitleBar(): ITitleBarOfFragment {
        return TitleBarManager.with(this, themeStyle = ThemeStyle.STANDARD)
            .back {
                dismiss()
            }
            .setTitle(
                titleRes = R.string.photo_album,
                endDrawable = getDrawableStateList(
                    normal = getCompoundDrawable(R.drawable.ic_label_22_triangle_down),
                    selected = getCompoundDrawable(R.drawable.ic_label_22_triangle_up),
                ).apply {
                    setBounds(0, 0, 22.dp, 22.dp)
                }
            ) {
                titleView = it
                // 切换状态
                showMenu(isShow = !it.isSelected)
            }.apply {
                setBackgroundResource(R.color.color_ffffff)
            }
    }

    override fun initView() {
        initPhotoView()
        initMenuView()
        initFooterView()
    }

    override fun initData() {
        getAllPhotoMap { photoMap ->
            mAdapter.photoMap = photoMap
            val keys = ArrayList<PhotoBucket>().apply {
                photoMap.forEach { (key, _) ->
                    add(key)
                }
            }
            mBucketAdapter.data = keys
        }
    }

    override fun startObserve() {
        PhotoData.instance.clear()
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
                        mAdapter.addPhoto(photoInfo)
                        mBucketAdapter.syncCamera(photoInfo)
                    }, 300)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initPhotoView() {
        mBinding?.apply {
            shadow.setBackground(
                colorRes = R.color.color_f9f9f9,
                endColorRes = R.color.color_00000000
            )
            recyclerView.layoutManager = GridLayoutManager(context, span)
            recyclerView.adapter = mAdapter
            recyclerView.addItemDecoration(
                AverItemDecoration(spanCount = span, edge = 3.dp)
            )
        }
    }

    /**
     * 下啦菜单
     */
    private fun initMenuView() {
        mBinding?.apply {
            coverLayout.setOnClickListener {
                showMenu(isShow = false)
            }

            coverRecyclerView.layoutManager = LinearLayoutManager(context)
            coverRecyclerView.adapter = mBucketAdapter
        }
    }

    private fun initFooterView() {
        mBinding?.apply {
            preview.apply {
                setTextColor(
                    getColorStateList(
                        normalColorRes = R.color.color_1d2736,
                        disableColorRes = R.color.color_8798af,
                    )
                )
                isEnabled = false
                setOnClickListener {
                    showDialogFragment(
                        clazz = PhotoGalleryDialogFragment::class.java
                    )?.let { galleryDialog ->
                        galleryDialog.limitPhotoSelectCount = limitPhotoSelectCount
                        galleryDialog.needUpload = needUpload
                        galleryDialog.actionSuccessPhotos = {
                            actionSuccessPhotos?.invoke(it)
                            dismiss()
                        }
                        mAdapter.photos?.apply {
                            val temp = ArrayList<PhotoInfo>()
                            val list = ArrayList<PhotoInfo>()
                            // 将所有选中的没有在photos列表中的照片，添加到开头
                            selectedPhotos.forEach {
                                if (!contains(it)) {
                                    temp.add(it)
                                }
                            }

                            list.addAll(temp)
                            list.addAll(this)
                            galleryDialog.photos = list
                        }
                    }
                }
            }
            edit.apply {
                setTextColor(
                    getColorStateList(
                        normalColorRes = R.color.color_1d2736,
                        disableColorRes = R.color.color_8798af,
                    )
                )
                isEnabled = false
                setOnClickListener {
                    showDialogFragment(
                        clazz = PhotoCropDialogFragment::class.java
                    )?.apply {
                        photo = selectedPhotos.firstOrNull()
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

    private fun showMenu(isShow: Boolean) {
        titleView?.isSelected = isShow
        if (isShow) {
            mBinding?.coverLayout?.startAnimation(animIn)
            mBinding?.coverRecyclerView?.startAnimation(animEnter)
            mBinding?.coverLayout?.isVisible = true
        } else {
            mBinding?.coverLayout?.startAnimation(animOut)
            mBinding?.coverRecyclerView?.startAnimation(animExit)
        }
    }

    private val animEnter by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.dialog_top_enter_200
        )
    }
    private val animExit by lazy {
        AnimationUtils.loadAnimation(
            context,
            R.anim.dialog_top_exit_200
        )
    }
    private val animIn by lazy { AnimationUtils.loadAnimation(context, R.anim.fade_in_200) }

    private val animOut by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fade_out_200).apply {
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    mBinding?.coverLayout?.isVisible = false
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }

            })
        }
    }

    /**
     * 同步UI状态：按钮可选状态，计数等
     */
    private fun syncUIState(selectedPhotos: ArrayList<PhotoInfo>) {
        mBinding?.apply {
            val size = selectedPhotos.size
            edit.isEnabled = size == 1
            preview.isEnabled = size > 0
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