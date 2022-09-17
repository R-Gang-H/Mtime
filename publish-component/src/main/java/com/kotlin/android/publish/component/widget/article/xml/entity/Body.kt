package com.kotlin.android.publish.component.widget.article.xml.entity

/**
 *
 * Created on 2022/3/25.
 *
 * @author o.s
 */
data class Body(
    var elements: ArrayList<Element>? = null
) {
    /**
     * 获取 xml 标签文本，用于提交发布内容
     */
    val body: String
        get() {
            val sb = StringBuilder()
//            sb.append("<body>")
            var len = elements?.size ?: 0
            if (elements?.last()?.isEmpty == true) {
                len -= 1
            }
            if (len > 0) {
                (0 until len).forEach {
                    elements?.get(it)?.apply {
                        if (view?.isReady == true && view?.isError == false) {
                            sb.append(body)
                        }
                    }
                }
            }
//            elements?.forEach {
//                sb.append(it.body)
//            }
//            sb.append("</body>")
            return sb.toString()
        }

    /**
     * body（xml）是否为空
     */
    val isEmpty: Boolean
        get() {
            if (elements.isNullOrEmpty()) {
                return true
            }
            var empty = true
            elements?.forEach {
                if (!it.isEmpty) {
                    empty = false
                    return@forEach
                }
            }
            return empty
        }

    /**
     * 添加段落到指定位置，默认 [position] = -1 添加到最后
     */
    fun add(element: Element, position: Int = -1) {
        if (elements == null) {
            elements = ArrayList()
        }
        if (position in 0 until (elements?.size ?: 0)) {
            elements?.add(position, element)
        } else {
            elements?.add(element)
        }
    }
}
