package com.kotlin.android.publish.component.widget.article.view.item

/**
 * 编辑器状态
 *
 * Created on 2022/4/1.
 *
 * @author o.s
 */
interface IEditorEvent {

//    var state: EditorState// = EditorState.NORMAL
//        set(value) {
//            field = value
//            when (value) {
//                EditorState.NORMAL -> normalState()
//                EditorState.EDIT -> editState()
//                EditorState.MOVE -> moveState()
//            }
//        }

    /**
     * 关闭
     */
    fun onClose()

    /**
     * 移动
     */
    fun onMove()

    /**
     * 图片描述
     */
    fun onImageDesc()

    /**
     * 超链接
     */
    fun onLink()

}