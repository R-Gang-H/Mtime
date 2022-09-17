package com.kotlin.android.community.family.component.ui.manage.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.kotlin.android.community.family.component.R
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.getString

/**
 * @author zhangjian
 * @date 2022/4/6 17:01
 */
class EditDialog(context: Context, themeResId: Int, var content: String?) :
    Dialog(context, themeResId) {

    val MAX_CONTENT_COUNT = 5
    var allContainer: ConstraintLayout? = null
    var llContent: ConstraintLayout? = null
    var et: EditText? = null
    var tvCancel: TextView? = null
    var tvAdd: TextView? = null
    var tvTitle: TextView? = null

    var cancelClick: (() -> Unit)? = null
    var addClick: ((str: String) -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_section)

        allContainer = findViewById(R.id.container)
        tvTitle = findViewById(R.id.tvTitle)
        llContent = findViewById(R.id.ctlContent)
        et = findViewById(R.id.etContent)
        tvCancel = findViewById(R.id.tvCancel)
        tvAdd = findViewById(R.id.tvAdd)

        //设置最大字数
        et?.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_CONTENT_COUNT))

        //content不为空的话,代表修改分类
        if(content?.isNotEmpty() == true){
            et?.setText(content)
            tvAdd?.text = getString(R.string.family_editor_dialog_modify)
            tvTitle?.text = getString(R.string.family_editor_dialog_title_modify)
        }else{
            tvAdd?.text = getString(R.string.family_editor_dialog_add)
            tvTitle?.text = getString(R.string.family_editor_dialog_title_add)
        }

        //设置圆角
        setCorner()
        //设置点击事件
        tvCancel?.onClick {
            cancelClick?.invoke()
        }
        tvAdd?.onClick {
            val str = et?.text?.toString()?.trim().orEmpty()
            if (str.isNotEmpty()) {
                addClick?.invoke(str)
            } else {
                showToast(getString(R.string.family_editor_dialog_content_hint))
            }

        }
    }

    /**
     * 设置圆角
     */
    private fun setCorner() {
        //整体背景圆角
        allContainer?.setBackground(colorRes = R.color.color_ffffff, cornerRadius = 12.dpF)
        //文本框圆角
        llContent?.setBackground(colorRes = R.color.color_f2f3f6, cornerRadius = 17.dpF)
        et?.setBackground(colorRes = R.color.color_f2f3f6)
        //取消按钮圆角
        tvCancel?.setBackground(
            colorRes = R.color.color_ffffff,
            cornerRadius = 18.dpF,
            strokeColorRes = R.color.color_d8d8d8,
            strokeWidth = 1
        )
        //添加按钮圆角
        tvAdd?.setBackground(colorRes = R.color.color_20a0da, cornerRadius = 18.dpF)
    }

}