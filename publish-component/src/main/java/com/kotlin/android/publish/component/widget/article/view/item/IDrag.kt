package com.kotlin.android.publish.component.widget.article.view.item

import android.view.View

/**
 *
 * Created on 2022/4/1.
 *
 * @author o.s
 */
interface IDrag {
    var dragChange: ((v: View, dy: Float) -> Unit)?
}