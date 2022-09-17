package com.kotlin.android.publish.component.widget.article.content.element

/**
 * <source>：资源
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Source {

    fun start(
        url: CharSequence,
        type: CharSequence,
    ): CharSequence {
        return """<source src="$url" type="$type">"""
    }

    val end: CharSequence
        get() = "</source>"
}

/**
<video
    controls="controls"
    width="300"
    height="150"
    data-video-type="1"
    data-video-id="82616"
    data-mce-fragment="1"
    src="http://vfx.mtime.cn/Video/2022/03/21/mp4/220321103931188136.mp4"
    poster="http://img5.mtime.cn/mg/2022/03/21/103928.66679330.jpg">

        <source
            src="http://vfx.mtime.cn/Video/2022/03/21/mp4/220321103931188136.mp4"
            type="audio/ogg">

</video>
 */