package com.kotlin.android.home.ui.recommend.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.home.HomeShowingComingMovies
import com.kotlin.android.home.ui.recommend.adapter.ShowingComingBinder
import com.kotlin.android.home.ui.recommend.adapter.ShowingComingMovieItemBinder
import com.kotlin.android.ktx.ext.orZero

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/10
 *
 * 首页-正在热映/待映推荐 模块UI实体
 */
data class ShowingComingCategoryItem(
    var showingList: List<ShowingComingMovieItemBinder>?,
    var comingList: List<ShowingComingMovieItemBinder>?
) : ProguardRule {
    companion object {
        fun converter2Binder(data: HomeShowingComingMovies): ShowingComingBinder? {
            data.apply {
                //正在热映
                val showingList = hotPlayMovies?.map { movie ->
                    ShowingComingMovieItemBinder(
                        ShowingComingMovieItem(
                            id = movie.movieId,
                            name = movie.title.orEmpty(),
                            mtimeScore = movie.score.orEmpty(),
                            pic = movie.imgUrl.orEmpty(),
                            tag = movie.getTag(),
                            btnState = movie.btnShow,
                            wantSeeCount = movie.wantedCount.orZero()
                        )
                    )
                }
                //即将上映
                val comingList = mobilemoviecoming?.moviecomings?.map { movie ->
                    ShowingComingMovieItemBinder(
                        ShowingComingMovieItem(
                            id = movie.movieId,
                            name = movie.title.orEmpty(),
                            mtimeScore = movie.score.orEmpty(),
                            pic = movie.imgUrl.orEmpty(),
                            tag = movie.getTag(),
                            btnState = movie.btnShow,
                            wantSeeCount = movie.wantedCount.orZero(),
                            releaseDate = movie.releaseDateStr.orEmpty() + "上映"
                        )
                    )
                }

                return if (showingList.isNullOrEmpty().not() || comingList.isNullOrEmpty().not()) {
                    ShowingComingBinder(
                        ShowingComingCategoryItem(
                            showingList,
                            comingList
                        )
                    )
                } else {
                    null
                }
            }
        }
    }
}