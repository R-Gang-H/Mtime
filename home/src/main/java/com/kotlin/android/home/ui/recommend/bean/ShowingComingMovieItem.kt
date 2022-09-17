package com.kotlin.android.home.ui.recommend.bean

import android.text.Html
import android.text.Spanned
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.home.R
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.mtime.ktx.getStringOrDefault

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/10
 *
 * 首页-正在热映/待映推荐 影片UI实体
 */
data class ShowingComingMovieItem(
    var id: Long,
    var name: String = "",
    var mtimeScore: String,
    var pic: String = "",
    var tag: String = "",
    var btnState: Long, //1:预售, 2:购票 3:想看, 4:已想看
    var wantSeeCount: Long = 0,
    var releaseDate: String = ""
) : ProguardRule {

    fun getReleaseDateOrWantSee(): String {
        return if (releaseDate.isNotEmpty()) {
            releaseDate
        } else {
            formatCount(wantSeeCount)+"人想看"
        }
    }

    fun getFormatScoreOrWantSee(): Spanned {
        return if (mtimeScore.isNotEmpty()) {
            Html.fromHtml(
                getStringOrDefault(R.string.home_mtime_score, "", mtimeScore)
                    .toString(),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            Html.fromHtml(
                getStringOrDefault(R.string.home_movie_wantsee_count, "", formatCount(wantSeeCount))
                    .toString(),
                Html.FROM_HTML_MODE_LEGACY
            )
        }
    }

    fun showScoreOrWantSee(): Boolean {
        return if (releaseDate.isNotEmpty()) {
            //待映-显示评分和想看人数
            mtimeScore.isNotEmpty() || wantSeeCount > 0
        } else {
            //热映-只显示评分
            mtimeScore.isNotEmpty()
        }
    }
}