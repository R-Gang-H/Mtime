package com.kotlin.android.publish.component.widget

import android.content.Context
import android.util.AttributeSet
import com.kotlin.android.ktx.ext.log.e

/**
 * 链接事件视图
 *
 * Created on 2020/7/16.
 *
 * @author o.s
 */
class ActionLinkView : androidx.appcompat.widget.AppCompatImageView {

    private val tag = "    5 ->"

    private var itemView: PublishItemView? = null
    private var action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)? = null

    fun setAction(itemView: PublishItemView?,
                  action: ((v: PublishItemView, actionEvent: ActionEvent) -> Unit)?) {
        this.itemView = itemView
        this.action = action
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {

        setOnClickListener{
            "$tag click link".e()
            itemView?.run {
                action?.invoke(this, ActionEvent.EVENT_LINK)
            }
        }

    }
}