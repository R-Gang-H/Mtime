package com.kotlin.android.community.family.component.ui.manage.widget

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kotlin.android.community.family.component.R
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.screenHeight
import com.kotlin.android.ktx.ext.dimension.screenWidth
import kotlinx.android.synthetic.main.dialog_action_sheet.*
import java.util.*

/**
 * @author vivian.wei
 * @date 2020/9/9
 * @desc 底部弹出选择组件
 */
class ActionSheetDialog(context: Context, itemBeans: List<SheetItemBean>, callback: (which: Int) -> Unit): DialogFragment() {

    private var mContext = context
    private var mItemBeans = itemBeans
    private var mCallback = callback
//    private var showTitle = false
    private var sheetItemList: MutableList<SheetItem>? = null

    companion object {
        const val TAG_ACTION_SHEET_DIALOG_FRAGMENT = "tag_action_sheet_dialog_fragment"

        fun newInstance(context: Context, itemBeans: List<SheetItemBean>, callback: (which: Int) -> Unit)
                = ActionSheetDialog(context, itemBeans, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.ActionSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        // 获取Dialog布局
        var view = inflater.inflate(R.layout.dialog_action_sheet, container, true)
        // 设置Dialog最小宽度为屏幕宽度
        view.minimumWidth = screenWidth
        var dialogWindow = dialog!!.window
        dialogWindow?.setGravity(Gravity.LEFT or Gravity.BOTTOM)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mItemBeans.let {
            it.forEach {
                addSheetItem(it.strItem, it.color, mCallback)
            }
            setSheetItems()
        }

        txt_cancel?.onClick {
            dismiss()
        }
    }

//    fun setTitle(title: String?): ActionSheetDialog {
//        showTitle = true
//        txt_title!!.isVisible = true
//        txt_title!!.text = title
//        return this
//    }

    /**
     * @param strItem  条目名称
     * @param color    条目字体颜色，设置null则默认蓝色
     * @param listener
     * @return
     */
    fun addSheetItem(strItem: String?, color: SheetItemColor?, callback: (which: Int) -> Unit) {
        if (sheetItemList == null) {
            sheetItemList = ArrayList()
        }
        sheetItemList!!.add(SheetItem(strItem!!, color!!, callback))
    }

    /**
     * @param strItem  条目名称
     * @param listener
     * @return
     */
//    fun addSheetItem(strItem: Array<String?>, callback: (which: Int) -> Unit): ActionSheetDialog {
//        if (sheetItemList == null) {
//            sheetItemList = ArrayList()
//        }
//        for (i in strItem.indices) {
//            sheetItemList!!.add(SheetItem(strItem[i]!!, null, callback!!))
//        }
//        return this
//    }

    /**
     * 设置条目布局
     */
     private fun setSheetItems() {
        if (sheetItemList == null || sheetItemList!!.size <= 0) {
            return
        }
        val size = sheetItemList!!.size

        // TODO 高度控制，非最佳解决办法
        // 添加条目过多的时候控制高度
        if (size >= 7) {
            val params = sLayout_content!!.layoutParams as LinearLayout.LayoutParams
            params.height = screenHeight / 2
            sLayout_content!!.layoutParams = params
        }

        // 循环添加条目
        (1..size).forEach {
            var index = it - 1
            val sheetItem = sheetItemList!![index]
            val strItem = sheetItem.name
            val color = sheetItem.color
            val textView = TextView(mContext)
            textView.text = strItem
            textView.textSize = 18f
            textView.gravity = Gravity.CENTER
            textView.setBackgroundResource(R.drawable.selector_action_sheet_content)

            // 字体颜色
            if (color == null) {
                textView.setTextColor(Color.parseColor(SheetItemColor.Black.name))
            } else {
                textView.setTextColor(Color.parseColor(color.name))
            }

            // 高度
            val height = mContext.resources?.getDimensionPixelSize(R.dimen.offset_50dp)
            textView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height!!)

            // 点击事件
            textView.setOnClickListener {
                sheetItem.callback.invoke(index)
                dismiss()
            }
            lLayout_content!!.addView(textView)
        }
    }


    inner class SheetItem(var name: String, var color: SheetItemColor?, var callback: (which: Int) -> Unit)

    enum class SheetItemColor(name: String) {
        Blue("#037BFF"), Red("#F15353"), Gray("#929292"), Black("#333333");

    }
}

data class SheetItemBean(
        var strItem: String ?= "",
        var color: ActionSheetDialog.SheetItemColor?= null
): ProguardRule
