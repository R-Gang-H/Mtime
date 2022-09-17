package com.kotlin.android.publish.component.widget.article.xml

import org.xmlpull.v1.XmlPullParser

/**
 *
 * Created on 2022/3/25.
 *
 * @author o.s
 */

fun StringBuilder.appendKeyValue(key: String, value: String?) {
    append("\"")
    append(key)
    append("\"")
    append(":")
    if (value != null) {
        append("\"")
        append(value)
        append("\"")
    } else {
        append("")
    }
}

fun StringBuilder.appendKey(key: String) {
    append("{\"")
    append(key)
    append("\"")
    append(":")
}

//fun StringBuilder.appendValue(value: String?) {
//    if (value != null) {
//        append("\"")
//        append(value)
//        append("\"")
//    } else {
//        append("null")
//    }
//}


fun appendJsonFromXml(
    parser: XmlPullParser,
    sb: StringBuilder,
    tag: String,
    vararg keys: Pair<String, String>
) {
    if (!sb.endsWith("{") && !sb.endsWith("[") && !sb.endsWith(",")) {
        sb.append(",")
    }
    sb.append(""""$tag":{""")
    (0 until parser.attributeCount).forEach {
        val name = parser.getAttributeName(it)
        val value = parser.getAttributeValue(it)
        keys.forEach { key ->
            if (key.first == name) {
                if (it > 0) {
                    sb.append(",")
                }
                sb.appendKeyValue(key.second, value)
            }
        }
    }
}

fun appendTextJsonFromXml(
    parser: XmlPullParser,
    sb: StringBuilder,
    tag: String,
    vararg keys: Pair<String, String>
) {
    if (!sb.endsWith("{") && !sb.endsWith("[") && !sb.endsWith(",")) {
        sb.append(",")
    }
    sb.append("{")
    sb.appendKeyValue("tag", tag)
    (0 until parser.attributeCount).forEach {
        val name = parser.getAttributeName(it)
        val value = parser.getAttributeValue(it)
        keys.forEach { key ->
            if (key.first == name) {
//                if (it > 0) {
//                    sb.append(",")
//                }
                if (!sb.endsWith("{") && !sb.endsWith("[") && !sb.endsWith(",")) {
                    sb.append(",")
                }
                sb.appendKeyValue(key.second, value)
            }
        }
    }

    if (!sb.endsWith("{") && !sb.endsWith("[") && !sb.endsWith(",")) {
        sb.append(",")
    }
    sb.append(""""items":[""")
}