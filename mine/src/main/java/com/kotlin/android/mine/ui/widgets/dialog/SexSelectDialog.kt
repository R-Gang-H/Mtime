package com.kotlin.android.mine.ui.widgets.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mine.R
import org.jetbrains.anko.find

class SexSelectDialog(
    private var maleCallBack: (() -> Unit)? = null,
    private var femaleCallBack: (() -> Unit)? = null,
    private var secretCallBack: (() -> Unit)? = null
) : DialogFragment() {

    companion object {
        const val TAGx_SEX_SELECT_FRAGMENT = "tag_sex_select_fragment"
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
        return inflater.inflate(R.layout.frag_sex_select_dialog, container, false)
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
            find<RelativeLayout>(R.id.dialogView).setBackground(
                colorRes = R.color.color_ffffff,
                cornerRadius = 12.dpF,
                direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            find<RelativeLayout>(R.id.maleRl).setOnClickListener {
                maleCallBack?.invoke()
            }
            find<RelativeLayout>(R.id.femaleRl).setOnClickListener {
                femaleCallBack?.invoke()
            }
            find<RelativeLayout>(R.id.secretRl).setOnClickListener {
                secretCallBack?.invoke()
            }
            find<RelativeLayout>(R.id.cancelLl).setOnClickListener {
                dismiss()
            }
        }
    }

}