package com.kotlin.android.publish.component.widget.article.content.movie

import android.text.SpannableStringBuilder
import com.kotlin.android.publish.component.widget.article.content.IContent
import com.kotlin.android.publish.component.widget.article.content.element.Div
import com.kotlin.android.publish.component.widget.article.content.element.Figure
import com.kotlin.android.publish.component.widget.article.content.element.Img
import com.kotlin.android.publish.component.widget.article.content.element.Span
import com.kotlin.android.publish.component.widget.article.sytle.MovieClass
import com.kotlin.android.publish.component.widget.article.sytle.FigureClass

/**
 * 电影卡片（内容）
 *
 * Created on 2022/3/22.
 *
 * @author o.s
 */
class MovieContent : IContent {
    override val body: SpannableStringBuilder
        get() = SpannableStringBuilder().apply {
            if (movieId.isNotEmpty()) {
                // <figure>:图形（电影）
                append(Figure.start(figureClass = FigureClass.MOVIE_CARD, movieId = movieId))

                    append(Div.start(clazz = MovieClass.WRAPPER))
                        append(Div.start(clazz = MovieClass.ITEM))

                            append(Div.start(clazz = MovieClass.COVER))

                                // <img>:图片
                                append(
                                    Img.start(
                                        url = coverUrl,
                                        mTimeImg = mTimeImg,
                                    )
                                )
                                append(Img.end)

                            append(Div.end) // cover

                            append(Div.start(clazz = MovieClass.INFO))

                                append(Div.start(clazz = MovieClass.TITLE))
                                append(movieName)
                                append(Div.end) // title

                                // 时光评级
                                append(Div.start(clazz = MovieClass.RATING, isDisplayNone = !isShowRating))

                                    // 时光评分Label
                                    append(Span.start())
                                    append(scoreLabelText)
                                    append(Span.end)

                                    // 时光评分
                                    append(Span.start(clazz = MovieClass.SCORE))
                                    append(scoreDesc)
                                    append(Span.end) // score

                                append(Div.end) // rating

                                // 影片描述
                                append(Div.start(clazz = MovieClass.DESC))

                                    // 时长 - 影片类型
                                    append(Span.start())
                                    append(timeAndMovieType)
                                    append(Span.end)

                                    // 上映日期 上映地点
                                    append(Span.start())
                                    append(realTimeAndLocation)
                                    append(Span.end) // score

                                append(Div.end) // desc

                            append(Div.end) // info

                        append(Div.end) // item

                    append(Div.end) // wrapper

                append(Figure.end)
            }
        }

    /**
     * 电影ID
     */
    var movieId: CharSequence = ""

    /**
     * 电影名称
     */
    var movieName: CharSequence = ""

    /**
     * 电影海报封面
     */
    var coverUrl: CharSequence = ""

    /**
     * 电影海报封面
     */
    var mTimeImg: CharSequence = ""

    /**
     * 时光评分Label
     */
    var scoreLabelText = "时光评分"

    /**
     * 时光评分
     */
    var score: Float = 0F // 7.7

    /**
     * 时光评分
     */
    val scoreDesc: CharSequence  // 7.7
        get() = score.toString()

    /**
     * 是否显示评级模块
     */
    val isShowRating: Boolean
        get() = score > 0

    /**
     * 时长 - 影片类型
     */
    var timeAndMovieType: CharSequence = "" // 175分钟 - 动作 / 犯罪 / 剧情

    /**
     * 上映日期 上映地点
     */
    var realTimeAndLocation: CharSequence = "" // 2022年3月18日美国上映
}

/**
<!-- 电影 -->
<figure class="movieCard" contenteditable="false" data-mtime-movie-id="225947"> \n
<div class="DRE-subject-wrapper"> \n
<div class="DRE-subject-item" data-v-6d079e13=""> \n
<div class="DRE-subject-cover" data-v-6d079e13="">\n
<img src="http://img5.mtime.cn/mt/2022/02/17/165014.65576216_o.jpg" data-v-6d079e13="" data-mtime-img="225947">\n
</div> \n
<div class="DRE-subject-info" data-v-6d079e13=""> \n
<div class="DRE-subject-title" data-v-6d079e13="">\n     新蝙蝠侠\n    </div> \n
<div class="DRE-subject-rating" data-v-6d079e13="">\n
<span data-v-6d079e13="">时光评分</span>
<span class="DRE-rating-score" data-v-6d079e13="">7.7</span>\n
</div> \n
<div class="DRE-subject-desc" data-v-6d079e13="">\n
<span data-v-6d079e13=""> 175分钟 - 动作 / 犯罪 / 剧情 </span>
<span data-v-6d079e13=""> 2022年3月18日美国上映 </span>\n
</div> \n
</div> \n
</div> \n
</div> \n
</figure> \n
 */

/**
"""
<figure class="movieCard" contenteditable="false" data-mtime-movie-id="${movie?.movieId}">
<div class="DRE-subject-wrapper">
<div class="DRE-subject-item" data-v-6d079e13="">
<div class="DRE-subject-cover" data-v-6d079e13="">
<img src="${movie?.img}" data-mtime-img="${movie?.movieId}"/>
</div>
<div class="DRE-subject-info" data-v-6d079e13="">
<div class="DRE-subject-title" data-v-6d079e13="">${movie?.name}</div>
<div class="DRE-subject-rating" data-v-6d079e13="" $ratingEmptyStyle>
$ratingSpan
</div>
<div class="DRE-subject-desc" data-v-6d079e13="">
<span data-v-6d079e13=""> $timeAndMovieType</span>
<span data-v-6d079e13=""> $realTimeAndLocation </span>
</div>
</div>
</div>
</div>
</figure>
""".trimIndent()
 */