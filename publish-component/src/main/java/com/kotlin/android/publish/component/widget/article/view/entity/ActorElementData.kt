//package com.kotlin.android.publish.component.widget.article.view.entity
//
//import com.kotlin.android.app.data.entity.search.Movie
//import com.kotlin.android.publish.component.widget.article.sytle.MovieClass
//import com.kotlin.android.publish.component.widget.article.view.safeGet
//import com.kotlin.android.publish.component.widget.article.xml.entity.Element
//import java.lang.StringBuilder
//
///**
// * 影人卡片的数据模型，关联影人相关 Element 元素
// *
//<figure class="movieCard" contenteditable="false" data-mtime-movie-id="225947">
//    <div class="DRE-subject-wrapper">
//        <div class="DRE-subject-item">
//            <div class="DRE-subject-cover">
//                <img src="http://img5.mtime.cn/mt/2022/02/17/165014.65576216_o.jpg" data-mtime-img="225947"></img>
//            </div>
//            <div class="DRE-subject-info">
//                <div class="DRE-subject-title">     新蝙蝠侠    </div>
//                <div class="DRE-subject-rating">
//                    <span>时光评分</span>
//                    <span class="DRE-rating-score">7.7</span>
//                </div>
//                <div class="DRE-subject-desc">
//                    <span> 175分钟 - 动作 / 犯罪 / 剧情 </span>
//                    <span> 2022年3月18日美国上映 </span>
//                </div>
//            </div>
//        </div>
//    </div>
//</figure>
// *
// * Created on 2022/3/30.
// *
// * @author o.s
// */
//class ActorElementData : IElementData {
//
//    /**
//     * <span> 2022年3月18日美国上映 </span>
//     */
//    private var desc2TextElement: Element = Element()
//
//    /**
//     * <span> 175分钟 - 动作 / 犯罪 / 剧情 </span>
//     */
//    private var desc1TextElement: Element = Element()
//
//    /**
//     * <span class="DRE-rating-score">7.7</span>
//     */
//    private var ratingScoreTextElement: Element = Element()
//
//    /**
//     * <span>时光评分</span>
//     */
//    private var ratingLabelTextElement: Element = Element(text = "时光评分")
//
//    /**
//     * <div class="DRE-subject-title">     新蝙蝠侠    </div>
//     */
//    private var titleTextElement: Element = Element()
//
//    private var desc2SpanElement: Element = Element(
//        tag = "span",
//        items = arrayListOf(desc2TextElement)
//    )
//
//    private var desc1SpanElement: Element = Element(
//        tag = "span",
//        items = arrayListOf(desc1TextElement)
//    )
//
//    private var ratingScoreSpanElement: Element = Element(
//        tag = "span",
//        clazz = MovieClass.SCORE.clazz,
//        items = arrayListOf(ratingScoreTextElement)
//    )
//
//    private var ratingLabelSpanElement: Element = Element(
//        tag = "span",
//        items = arrayListOf(ratingLabelTextElement)
//    )
//
//    private var descElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.DESC.clazz,
//        items = arrayListOf(desc1SpanElement, desc2SpanElement)
//    )
//
//    private var ratingElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.RATING.clazz,
//        items = arrayListOf(ratingLabelSpanElement, ratingScoreSpanElement)
//    )
//
//    private var titleElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.TITLE.clazz,
//        items = arrayListOf(titleTextElement)
//    )
//
//    private var imgElement: Element = Element(
//        tag = "img",
//    )
//
//    private var infoElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.INFO.clazz,
//        items = arrayListOf(titleElement, ratingElement, descElement)
//    )
//
//    private var coverElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.COVER.clazz,
//        items = arrayListOf(imgElement)
//    )
//
//    private var itemElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.ITEM.clazz,
//        items = arrayListOf(coverElement, infoElement)
//    )
//
//    private var wrapperElement: Element = Element(
//        tag = "div",
//        clazz = MovieClass.WRAPPER.clazz,
//        items = arrayListOf(itemElement)
//    )
//
//    /**
//     * 段落根元素
//     */
//    override var element: Element = Element(
//        tag = "figure",
//        clazz = "movieCard",
//        editable = "false",
//        items = arrayListOf(wrapperElement),
//    )
//        set(value) {
//            field = value
//            value.apply {
//                items.safeGet(0)?.apply {
//                    if (tag == "div") {
//                        wrapperElement = this
//                        items.safeGet(0)?.apply {
//                            if (tag == "div") {
//                                itemElement = this
//                                items.safeGet(0)?.apply {
//                                    if (tag == "div") {
//                                        coverElement = this
//                                        items.safeGet(0)?.apply {
//                                            if (tag == "img") {
//                                                imgElement = this
//                                            }
//                                        }
//                                    }
//                                }
//                                items.safeGet(1)?.apply {
//                                    if (tag == "div") {
//                                        infoElement = this
//                                        items.safeGet(0)?.apply {
//                                            if (tag == "div") {
//                                                titleElement = this
//                                                items.safeGet(0)?.apply {
//                                                    titleTextElement = this
//                                                }
//                                            }
//                                        }
//                                        items.safeGet(1)?.apply {
//                                            if (tag == "div") {
//                                                ratingElement = this
//                                                items.safeGet(0)?.apply {
//                                                    if (tag == "span") {
//                                                        ratingLabelSpanElement = this
//                                                        items.safeGet(0)?.apply {
//                                                            ratingLabelTextElement = this
//                                                        }
//                                                    }
//                                                }
//                                                items.safeGet(1)?.apply {
//                                                    if (tag == "span") {
//                                                        ratingScoreSpanElement = this
//                                                        items.safeGet(0)?.apply {
//                                                            ratingScoreTextElement = this
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                        items.safeGet(2)?.apply {
//                                            if (tag == "div") {
//                                                descElement = this
//                                                items.safeGet(0)?.apply {
//                                                    if (tag == "span") {
//                                                        desc1SpanElement = this
//                                                        items.safeGet(0)?.apply {
//                                                            desc1TextElement = this
//                                                        }
//                                                    }
//                                                }
//                                                items.safeGet(1)?.apply {
//                                                    if (tag == "span") {
//                                                        desc2SpanElement = this
//                                                        items.safeGet(0)?.apply {
//                                                            desc2TextElement = this
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    /**
//     * 设置电影对象
//     */
//    var movie: Movie? = null
//        get() {
//            if (field == null) {
//                field = Movie(
//                    movieId = element.movieId?.toLong(),
//                    img = imgElement.src,
//                    name = titleTextElement.text,
////                    rating = (ratingScoreTextElement.text ?: "0").toDouble()
//                )
//            }
//            return field
//        }
//        set(value) {
//            field = value
//            value?.apply {
//                element.movieId = movieId.toString()
//                imgElement.src = img
//                imgElement.mTimeImg = movieId.toString()
//                titleTextElement.text = name
//                if ((movie?.rating ?: 0.0) > 0.0) {
//                    ratingElement.style = null
//                    ratingScoreTextElement.text = rating.toString()
//                } else {
//                    ratingElement.style = "display: none;"
//                }
//
//                desc1TextElement.text = timeAndMovieType
//                desc2TextElement.text = realTimeAndLocation
//            }
//        }
//
//    /**
//     * 181分钟-动作/冒险/奇幻/家庭/奇幻/家庭/奇幻/家庭/奇幻/家庭
//     */
//    private val timeAndMovieType: String
//        get() {
//            val sb = StringBuilder()
//            movie?.run {
//                if ((length ?: 0) > 0) {
//                    sb.append(length)
//                    sb.append("分钟-")
//                }
//                if (!movieType.isNullOrEmpty()) {
//                    sb.append(movieType)
//                }
//            }
//            return sb.toString()
//        }
//
//    /**
//     * 2019年4月24日中国上映
//     */
//    private val realTimeAndLocation: String
//        get() {
//            val sb = StringBuilder()
//            movie?.run {
//                if (!realTime.isNullOrEmpty()) {
//                    sb.append(realTime)
//                }
//                if (!rLocation.isNullOrEmpty()) {
//                    sb.append(rLocation)
//                    sb.append("上映")
//                }
//            }
//            return sb.toString()
//        }
//}