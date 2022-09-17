package com.kotlin.android.community.ui.person.center.photo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.observe
import com.kotlin.android.community.R
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import kotlinx.android.synthetic.main.frag_create_album_dialog.*
import org.jetbrains.anko.find


/**
 * @author: WangWei
 * @date: 2020/9/28
 * 创建相册dialog
 */
class CreateAlbumDialogFragment : DialogFragment() {

    var createAlbumViewModel = CreateAlbumViewModel()

    companion object {
        fun newInstance() = CreateAlbumDialogFragment()
        const val TAG_CREATE_DIALOG_FRAGMENT = "tag_create_dialog_fragment"
    }

    /**
     * 添加成功回调
     */
    interface AddOkOnClickListener {
        fun onAddOK()
    }

    private var addOkOnClickListener: AddOkOnClickListener? = null

    fun setAddOkOnClickListener(listener: AddOkOnClickListener) {
        this.addOkOnClickListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ViewsBottomDialog)
    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        ShapeExt.setShapeCornerWithColor(dialogView, R.color.color_ffffff, 20, 20, 0, 0)

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.BOTTOM
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = screenHeight - 45.dp
        win.attributes = params
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_create_album_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val back = getView()?.find<ImageView>(R.id.iv_back)
        val confirm = getView()?.find<TextView>(R.id.confirm)
        val name = getView()?.find<EditText>(R.id.tv_album_name)
        back?.setOnClickListener { dismiss() }
        confirm?.setOnClickListener {//发送更新信息
            var content = name?.text

            if (content?.trim()?.isEmpty() == true) showToast("相册名不可为空哦")

            createAlbumViewModel.loadData(content.toString())
        }
        createAlbumViewModel.uiState.observe(this) {

            it.apply {
                success?.run {
                    if (this.result) {
                        dismiss()
                        if (addOkOnClickListener != null) addOkOnClickListener?.onAddOK()
                    }else showToast("添加失败")
                }
                error?.run {
                    showToast(this)
                }
            }
        }
    }

}