package com.kotlin.android.mine.ui.widgets.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.CONTENT_TYPE_AUDIO
import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.ContentsViewBean
import com.kotlin.android.widget.dialog.showDialog
import org.jetbrains.anko.find

class OperationBottomDialog(
    private val isDrafts: Boolean,
    private val creatorContentStatus: Long? = 0L,
    private val type: Long,
    private val editCallBack: (() -> Unit)? = null,
    private val revokeLlCallBack: (() -> Unit)? = null,
    private val deleteCallBack: (() -> Unit)? = null
) : DialogFragment() {

    companion object {
        const val TAG_OPERATION_BOTTOM_DIALOG_FRAGMENT = "tag_operation_bottom_dialog_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ViewsBottomDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_operation_bottom_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        val win: Window = dialog!!.window ?: return
        win.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val params: WindowManager.LayoutParams = win.attributes
        params.gravity = Gravity.BOTTOM
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        win.attributes = params
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            find<LinearLayout>(R.id.dialogView).setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            find<RelativeLayout>(R.id.editLl).apply {
                visibility = when (creatorContentStatus) {
                    ContentsViewBean.CREATOR_CONTENT_STATUS_DRAFT, ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED, ContentsViewBean.CREATOR_CONTENT_STATUS_REVIEW_FAIL, ContentsViewBean.CREATOR_CONTENT_STATUS_RELEASED_REVIEW_FAIL -> {
                        if (type == CONTENT_TYPE_AUDIO) View.GONE else View.VISIBLE
                    }
                    else -> View.GONE
                }
                setOnClickListener {
                    editCallBack?.invoke()
                    dismiss()
                }
            }
            find<RelativeLayout>(R.id.revokeLl).apply {
                visibility =
                    if (!isDrafts && creatorContentStatus == ContentsViewBean.CREATOR_CONTENT_STATUS_WAIT_REVIEW) View.VISIBLE else View.GONE
                setOnClickListener {
                    showDialog(
                        context?.getString(R.string.mine_content_revoke_dialog_title) ?: "",
                        context?.getString(R.string.mine_content_revoke_dialog_content) ?: "",
                        revokeLlCallBack
                    )
                }
            }
            find<RelativeLayout>(R.id.deleteLl).apply {
                visibility = when (creatorContentStatus) {
                    ContentsViewBean.CREATOR_CONTENT_STATUS_WAIT_REVIEW -> View.GONE
                    else -> View.VISIBLE
                }
                setOnClickListener {
                    showDialog(
                        context?.getString(R.string.delete) ?: "",
                        context?.getString(R.string.mine_content_delete_dialog_content) ?: "",
                        deleteCallBack
                    )
                }
            }
            find<RelativeLayout>(R.id.cancelLl).setOnClickListener {
                dismiss()
            }
        }
    }

    private fun showDialog(
        dialogTitle: String,
        dialogContent: String,
        callBack: (() -> Unit)? = null
    ) {
        showDialog(
            context = context,
            title = dialogTitle,
            content = dialogContent,
            positiveAction = {
                callBack?.invoke()
                dismiss()
            },
            negativeAction = {

            })
    }

}