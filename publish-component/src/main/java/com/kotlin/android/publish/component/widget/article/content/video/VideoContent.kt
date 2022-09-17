package com.kotlin.android.publish.component.widget.article.content.video

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.P
import com.kotlin.android.publish.component.widget.article.content.element.Source
import com.kotlin.android.publish.component.widget.article.content.element.Video

/**
 * 视频（内容）
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class VideoContent : IContent {
    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {

            // <p>：视频段落
            append(P.start())

            // <video>：视频
            append(
                Video.start(
                    url = url,
                    posterUrl = posterUrl,
                    width = width,
                    height = height,
                    videoId = videoId,
                    videoType = videoType,
                    mceFragment = mceFragment
                )
            )

            // <source>：资源
            append(Source.start(url = url, type = type))
            append(Source.end)

            append(Video.end)

            append(P.end)
        }

    /**
     * 视频url
     */
    var url: CharSequence = ""

    /**
     * 海报url
     */
    var posterUrl: CharSequence = ""

    /**
     * 视频宽
     */
    var width: Int = 0

    /**
     * 视频高
     */
    var height: Int = 0

    /**
     * 视频ID
     */
    var videoId: CharSequence = ""

    /**
     * 视频类型
     */
    var videoType: CharSequence = ""

    /**
     * 资源类型
     */
    var type: CharSequence = "audio/ogg" // audio/ogg

    /**
     * data-mce-fragment="1"
     */
    var mceFragment: CharSequence = ""
}

/**

<p>
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
</p>
 */