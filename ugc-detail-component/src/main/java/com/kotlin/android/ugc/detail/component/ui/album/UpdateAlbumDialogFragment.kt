package com.kotlin.android.ugc.detail.component.ui.album

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.router.ext.put
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.event.ALBUM_UPDATE
import com.kotlin.android.ugc.detail.component.event.UpdateAlbumEvent
import com.kotlin.android.ugc.detail.component.ui.album.UgcAlbumActivity.Companion.KEY_ALBUM_ID
import kotlinx.android.synthetic.main.frag_update_album_dialog.*
import org.jetbrains.anko.find

/**
 * create by lushan on 2020/10/12
 * description:更新相册名称
 */
class UpdateAlbumDialogFragment : DialogFragment() {

    var updateAlbumViewModel = UpdateAlbumViewModel()
    private var albumId: Long = 0L//相册id
    private var albumName: String = ""//相册名称

    companion object {
        fun newInstance(albumId: Long, albumName: String): UpdateAlbumDialogFragment {
            val updateAlbumDialogFragment = UpdateAlbumDialogFragment()
            val bundle = Bundle()
            bundle.put(KEY_ALBUM_ID, albumId)
            bundle.put(ALBUM_NAME, albumName)
            updateAlbumDialogFragment.arguments = bundle
            return updateAlbumDialogFragment
        }

        const val ALBUM_NAME = "album_name"
        const val TAG_UPDATE_DIALOG_FRAGMENT = "tag_update_dialog_fragment"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        albumId = args?.getLong(KEY_ALBUM_ID) ?: 0L
        albumName = args?.getString(ALBUM_NAME, "").orEmpty()
    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog?.window ?: return
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        ShapeExt.setShapeCornerWithColor(dialogView, R.color.color_ffffff, 20, 20, 0, 0)

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.BOTTOM
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = screenHeight - 45.dp
        win.attributes = params
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_update_album_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = getView()?.find<ImageView>(R.id.iv_back)
        val confirm = getView()?.find<TextView>(R.id.confirm)
        val name = getView()?.find<EditText>(R.id.tv_album_name)

        name?.setText(albumName)
        back?.setOnClickListener { dismiss() }
        confirm?.setOnClickListener {//发送更新信息
            var content = name?.text

            if (content?.trim()?.isEmpty() == true) showToast(R.string.ugc_album_cant_empty)

            updateAlbumViewModel.updateAlbum(albumId, content.toString())
        }

        updateAlbumViewModel.updateState.observe(this) {

            it?.apply {
                activity?.showOrHideProgressDialog(showLoading)
                success?.run {
                    if (this.result) {//修改成功
                        LiveEventBus.get(ALBUM_UPDATE).post(UpdateAlbumEvent(true, name?.text?.toString().orEmpty()))
                        dismiss()
                    } else {//修改失败
                        showToast(R.string.ugc_album_update_failed)
                    }

                }
                error?.run {
                    showToast(this)
                }
            }
        }

    }
}