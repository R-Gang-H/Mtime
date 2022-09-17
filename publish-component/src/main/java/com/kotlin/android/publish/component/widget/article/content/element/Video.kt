package com.kotlin.android.publish.component.widget.article.content.element

/**
 * <video>：视频
 *
 * Created on 2022/3/23.
 *
 * @author o.s
 */
object Video {

    fun start(
        url: CharSequence,
        posterUrl: CharSequence,
        width: Int,
        height: Int,
        videoId: CharSequence,
        videoType: CharSequence,
        mceFragment: CharSequence,
    ): CharSequence {
        return """<video controls="controls" width="$width" height="$height" data-video-type="$videoType" data-video-id="$videoId" data-mce-fragment="$mceFragment" src="$url" poster="$posterUrl">"""
    }

    val end: CharSequence
        get() = "</video>"
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