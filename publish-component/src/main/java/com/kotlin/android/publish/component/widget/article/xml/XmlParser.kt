package com.kotlin.android.publish.component.widget.article.xml

import com.google.gson.Gson
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.ktx.ext.log.w
import com.kotlin.android.mtime.ktx.htmlSimpleDecode
import com.kotlin.android.publish.component.widget.article.Article
import com.kotlin.android.publish.component.widget.article.xml.element.*
import com.kotlin.android.publish.component.widget.article.xml.entity.Body
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader

/**
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
object XmlParser {

    fun toJson() {
        val body = Gson().fromJson(TestData.jsonData, Body::class.java)
        body.i()
    }

    fun parseXml(body: String, compete: (Body) -> Unit) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(StringReader("<body>${htmlSimpleDecode(body)}</body>"))
            var event = parser.eventType
            val sb: StringBuilder = StringBuilder()
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_DOCUMENT -> {
                        val name = parser.name
                        "开始解析 >>> <$name>".e()
                    }
                    XmlPullParser.START_TAG -> {
                        val name = parser.name
                        "开始标签 >>> <$name>".w()
                        startTag(parser, sb)
//                        tag(parser, body)
//                        val name = parser.name
//                        val sb = StringBuilder()
//                        (0 until parser.attributeCount).forEach {
//                            val aName = parser.getAttributeName(it)
//                            val aValue = parser.getAttributeValue(it)
//                            if (sb.isNotEmpty()) {
//                                sb.append(" ")
//                            }
//                            sb.append(aName)
//                            sb.append("=")
//                            sb.append(aValue)
//                        }
//                        "开始标签 >>> <$name $sb>".i()
                    }
                    XmlPullParser.END_TAG -> {
                        endTag(parser, sb)
                        val name = parser.name
                        "标签完成 <<< </$name>".w()
                    }
                    XmlPullParser.END_DOCUMENT -> {
                        val name = parser.name
                        "解析完成 <<< </$name>".e()
                    }
                    XmlPullParser.TEXT -> {
                        Text.appendJson(parser, sb)
                        val text = parser.text
                        "TEXT >>> $text".i()
                    }
                }
                event = parser.next()
            }
            "while end >>> $event".e()
            "end json -> $sb".e()
            var json = sb.toString()
//            json = json.replace("\\", "&#92;")
            json = json.replace("\\", "")
            val body = Gson().fromJson(json, Body::class.java)
            // 去掉非法的字符
            body.elements?.removeIf { it.tag == null }
            compete.invoke(body)
//            body.e()
//            body.body.w()
//            val json = Gson().toJson(body)
//            json.i()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun endTag(parser: XmlPullParser, sb: StringBuilder) {
        when (parser.name) {
            "a" -> {
                sb.append("]}")
            }
            "body" -> {
                sb.append("]}")
            }
            "br" -> {
                sb.append("]}")
            }
            "div" -> {
                sb.append("]}")
            }
            "em" -> {
                sb.append("]}")
            }
            "figcaption" -> {
                sb.append("]}")
            }
            "figure" -> {
                sb.append("]}")
            }
            "img" -> {
                sb.append("]}")
            }
            "p" -> {
                sb.append("]}")
            }
            "source" -> {
                sb.append("]}")
            }
            "span" -> {
                sb.append("]}")
            }
            "strong" -> {
                sb.append("]}")
            }
            "text" -> {
//                sb.append("}")
            }
            "video" -> {
                sb.append("]}")
            }
        }
    }
    private fun startTag(parser: XmlPullParser, sb: StringBuilder) {
        when (parser.name) {
            "a" -> {
                A.appendJson(parser, sb)
            }
            "body" -> {
                sb.append("""{"elements":[""")
            }
            "br" -> {
                Br.appendJson(parser, sb)
            }
            "div" -> {
                Div.appendJson(parser, sb)
            }
            "em" -> {
                Em.appendJson(parser, sb)
            }
            "figcaption" -> {
                FigCaption.appendJson(parser, sb)
            }
            "figure" -> {
                Figure.appendJson(parser, sb)
            }
            "img" -> {
                Img.appendJson(parser, sb)
            }
            "p" -> {
                P.appendJson(parser, sb)
            }
            "source" -> {
                Source.appendJson(parser, sb)
            }
            "span" -> {
                Span.appendJson(parser, sb)
            }
            "strong" -> {
                Strong.appendJson(parser, sb)
            }
            "text" -> {
                Text.appendJson(parser, sb)
            }
            "video" -> {
                Video.appendJson(parser, sb)
            }
        }
    }

    fun parse2() {
        var article: Article = Article()
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(StringReader(TestData.xmlData))
            var event = parser.eventType
            while (event != XmlPullParser.END_DOCUMENT) {
                when (event) {
                    XmlPullParser.START_DOCUMENT -> {
                        val name = parser.name
                        "开始解析 >>> <$name>".e()
                    }
                    XmlPullParser.START_TAG -> {
                        val name = parser.name
                        val sb = StringBuilder()
                        (0 until parser.attributeCount).forEach {
                            val aName = parser.getAttributeName(it)
                            val aValue = parser.getAttributeValue(it)
                            if (sb.isNotEmpty()) {
                                sb.append(" ")
                            }
                            sb.append(aName)
                            sb.append("=")
                            sb.append(aValue)
                        }
                        "开始标签 >>> <$name $sb>".i()
                    }
                    XmlPullParser.END_TAG -> {
                        val name = parser.name
                        "标签完成 <<< </$name>".w()
                    }
                    XmlPullParser.END_DOCUMENT -> {
                        val name = parser.name
                        "解析完成 <<< </$name>".e()
                    }
                    XmlPullParser.TEXT -> {
                        val text = parser.text
                        "TEXT >>> $text".i()
                    }
                    XmlPullParser.CDSECT -> {
                        val name = parser.name
                        "CDSECT <<< </$name>".e()
                    }
                    XmlPullParser.ENTITY_REF -> {
                        val name = parser.name
                        "ENTITY_REF <<< </$name>".e()
                    }
                    XmlPullParser.IGNORABLE_WHITESPACE -> {
                        val name = parser.name
                        "IGNORABLE_WHITESPACE <<< </$name>".e()
                    }
                    XmlPullParser.PROCESSING_INSTRUCTION -> {
                        val name = parser.name
                        "PROCESSING_INSTRUCTION <<< </$name>".e()
                    }
                    XmlPullParser.COMMENT -> {
                        val name = parser.name
                        "COMMENT <<< </$name>".e()
                    }
                    XmlPullParser.DOCDECL -> {
                        val name = parser.name
                        "DOCDECL <<< </$name>".e()
                    }
                }
                event = parser.next()
            }
            "while end >>> $event".e()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun safeNextText(parser: XmlPullParser): String? {
        var result: String? = null
        try {
            result = parser.nextText()
//            if (parser.eventType != XmlPullParser.END_TAG) {
//                parser.nextTag()
//            }
        } catch (e: Exception) {
//            e.printStackTrace()
        }

        return result
    }
}

/**
 *     String [] TYPES = {
"START_DOCUMENT",
"END_DOCUMENT",
"START_TAG",
"END_TAG",
"TEXT",
"CDSECT",
"ENTITY_REF",
"IGNORABLE_WHITESPACE",
"PROCESSING_INSTRUCTION",
"COMMENT",
"DOCDECL"
};

 */