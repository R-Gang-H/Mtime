package com.kotlin.android.publish.component.widget.article.view.item

import android.graphics.Rect
import android.view.View
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.publish.component.widget.article.view.entity.IElementData
import com.kotlin.android.publish.component.widget.article.xml.entity.Element

/**
 *
 * Created on 2022/3/30.
 *
 * @author o.s
 */
interface IItemView : IEditorState, IFocusChanged {
    val logTag: String
        get() = javaClass.simpleName

    val parentItemView: IItemView?
        get() = null

    val view: View

    var element: Element

    val elementData: IElementData

    /**
     * 文本长度
     */
    val count: Int
        get() = 0

    /**
     * 内容是否就绪（图片/视频等上传是否完成）
     */
    val isReady: Boolean
        get() = true

    /**
     * 内容是否错误（图片/视频等上传是否错误）
     */
    val isError: Boolean
        get() = false

    /**
     * 是否为空
     */
    val isEmpty: Boolean
        get() = false

    /**
     * 是否有删除标识
     */
    val hasDelete: Boolean
        get() = false

    /**
     * 是否有移动标识
     */
    val hasMove: Boolean
        get() = false

    /**
     * 是否有图注
     */
    val hasDesc: Boolean
        get() = false

    /**
     * 是否有超链接标识
     */
    val hasLink: Boolean
        get() = false

    /**
     * item 主体 margin
     */
    val itemMargin: Int
        get() = defItemMargin

    /**
     * 高亮边框偏移量
     */
    val borderMarginOffset: Int
        get() = defBorderMarginOffset

    /**
     * 底部描述视图占据高度（包含底部view的margin，但不包含底部 [itemMargin]）
     */
    val footerHeight: Int
        get() = 0

    /**
     * 移动状态item高度
     */
    val moveStateHeight: Int
        get() = 74.dp

    /**
     * 操作标识 marginBottom （包含底部 [itemMargin]）
     */
    val actionMarginBottom: Int
        get() = itemMargin + defActionMarginBottom

    /**
     * 主体编辑区，在编辑状态时的高亮外框 margin
     */
    val editBorderMarginRect: Rect
        get() = defEditBorderMarginRect

    /**
     * 主体圆角
     */
    val cornerRadius: Float
        get() = defCornerRadius

    companion object {
        val defCornerRadius = 5.dpF
        val defItemMargin = 10.dp
        val defBorderMarginOffset = 1.dp
        val defActionMarginBottom = 15.dp
        val defBorderMargin = defItemMargin - defBorderMarginOffset
        val defEditBorderMarginRect = Rect(defBorderMargin, defBorderMargin, defBorderMargin, defBorderMargin)
    }
}