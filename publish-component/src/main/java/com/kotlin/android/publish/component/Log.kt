package com.kotlin.android.publish.component

import com.kotlin.android.ktx.ext.log.d
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.publish.component.widget.article.view.item.IEditorState
import com.kotlin.android.publish.component.widget.article.view.item.TextCard

/**
 *
 * Created on 2022/4/12.
 *
 * @author o.s
 */
internal fun Any.logE(log: String) {
    "${formatName()} $log".e()
}

internal fun Any.logW(log: String) {
    "${formatName()} $log".w()
}

internal fun Any.logI(log: String) {
    "${formatName()} $log".i()
}

internal fun Any.logD(log: String) {
    "${formatName()} $log".d()
}

internal fun Any.logE(methodName: String, log: String) {
    "${formatName()} ${methodName.formatLength()} :: $log".e()
}

internal fun Any.logW(methodName: String, log: String) {
    "${formatName()} ${methodName.formatLength()} :: $log".w()
}

internal fun Any.logI(methodName: String, log: String) {
    "${formatName()} ${methodName.formatLength()} :: $log".i()
}

internal fun Any.logD(methodName: String, log: String) {
    "${formatName()} ${methodName.formatLength()} :: $log".d()
}

internal fun IEditorState.logH(methodName: String, old: Int, new: Int, isD: Boolean = false) {
    if (isD) {
        "${formatName()} ${methodName.formatLength()} :: height($old -> $new)".d()
    } else {
        "${formatName()} ${methodName.formatLength()} :: height($old -> $new)".i()
    }
}

internal fun IEditorState.logH(methodName: String, new: Int) {
    "${formatName()} ${methodName.formatLength()} :: height($new)".i()
}

internal fun Any.logF(gainFocus: Boolean) {
    "${formatName()} ${"onFocusChanged".formatLength()} :: $gainFocus".w()
}

internal fun TextCard.logT(
    methodName: String,
    text: CharSequence?,
    start: Int? = null,
    count: Int? = null,
    before: Int? = null,
    after: Int? = null,
) {
    val s = if (start != null) {
        "s:$start"
    } else {
        ""
    }
    val c = if (count != null) {
        "c:$count"
    } else {
        ""
    }
    val x = when {
        after != null -> {
            "a:$after"
        }
        before != null -> {
            "b:$before"
        }
        else -> {
            ""
        }
    }
    "${formatName()} ${methodName.formatLength()} :: [$s, $c, $x] text:[$text]".i()
}

internal fun Any.formatName(): String {
    return String.format("%-20s", javaClass.simpleName)
}

internal fun String.formatLength(): String {
    return String.format("%20s", this)
}
