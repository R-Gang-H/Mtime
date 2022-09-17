package com.kotlin.android.publish.component.widget.article.view.item

import com.kotlin.android.publish.component.widget.article.view.EditorState

/**
 * 编辑器状态
 *
 * Created on 2022/4/1.
 *
 * @author o.s
 */
interface IEditorState {

    /**
     * 编辑状态
     */
    var state: EditorState

    /**
     * 正常状态
     */
    fun normalState()

    /**
     * 编辑状态 修改/删除
     */
    fun editState()

    /**
     * 移动状态
     */
    fun moveState()


    /**
     * 分发状态
     */
    fun dispatchState(state: EditorState) {
        when (state) {
            EditorState.NORMAL -> normalState()
            EditorState.EDIT -> editState()
            EditorState.MOVE -> moveState()
        }
    }
}