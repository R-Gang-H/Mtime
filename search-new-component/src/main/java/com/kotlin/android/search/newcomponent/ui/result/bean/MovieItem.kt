package com.kotlin.android.search.newcomponent.ui.result.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.search.newcomponent.ui.publish.adapter.PublishSearchMovieItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 搜索结果页显示用影片数据
 */
data class MovieItem(
    var movieId: Long = 0L,        // 影片Id
    var name: String = "",         // 中文名称
    var nameEn: String = "",       // 英文名
    var year: String = "",         // 年代
    var img: String = "",          // 影片封面图
    var rating: Double = 0.0,      // 加权最终分
    var movieType: String = "",    // 影片类型 "惊悚/剧情"
    var locationName: String = "", // 影片产地
    var releaseStatus: Long = 0L,  // 地区影片的上映状态 0表示下线、1表示正在热映、 2 表示即将上映 (使用该字段必须传城市ID参数)
    var canPlay: Long = 0L,        // 是否可播放 0不可播放 1可播放
    var saleType: Long = 0L,       // 是否可售票 0不可售票 1可预售 2可售票
    var rLocation: String = "",    // 上映地区
): ProguardRule {
    companion object {
        // 是否可播放
        const val CAN_PLAY = 1L             // 可播放

        // 地区影片的上映状态
        const val RELEASE_STATUS_HOT = 1L    // 正在热映

        // 是否可售票
        const val SALE_TYPE_PRE_SELL = 1L   // 可预售
        const val SALE_TYPE_TICKET = 2L     // 可售票

        /**
         * 转换ViewBean
         */
        fun objectToViewBean(bean: Movie): MovieItem {
            return MovieItem(
                    movieId = bean.movieId.orZero(),
                    name = bean.name.orEmpty(),
                    nameEn = bean.nameEn.orEmpty(),
                    year = bean.year.orEmpty(),
                    img = bean.img.orEmpty(),
                    rating = bean.rating.orZero(),
                    movieType = bean.movieType.orEmpty(),
                    locationName = bean.locationName.orEmpty(),
                    releaseStatus = bean.releaseStatus.orZero(),
                    canPlay = bean.canPlay.orZero(),
                    saleType = bean.saleType.orZero(),
                    rLocation = bean.rLocation.orEmpty(),
            )
        }

        /**
         * 发布组件-关联电影-搜索结果binders
         */
        fun buildPublishSearch(beans: List<Movie>?) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans?.let { list ->
                list.map {
                    val viewBean = objectToViewBean(it)
                    binderList.add(
                            PublishSearchMovieItemBinder(
                                movie = it,
                                viewBean = viewBean
                            )
                    )
                }
            }
            return binderList
        }
    }
}
