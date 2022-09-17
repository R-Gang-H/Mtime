package com.kotlin.android.message.ui.center.viewBean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.message.UnreadCountResult
import com.kotlin.android.message.R
import com.kotlin.android.mtime.ktx.getString


/**
 * Created by zhaoninglongfei on 2022/3/7
 *
 */
data class MessageCenterMovieNotifyViewBean(
    val hasNotify: Boolean = false,
    val movieNotify: String = getString(R.string.message_no_movie_notify),
    var hasMore: Boolean = false
) : ProguardRule {
    companion object {
        fun build(result: UnreadCountResult): MessageCenterMovieNotifyViewBean? {
            return MessageCenterMovieNotifyViewBean(
                hasNotify = result.movieRelease != 0L,
                movieNotify =
                if (result.movieName.isNullOrEmpty()) {
                    getString(R.string.message_no_movie_notify)
                } else {
                    getString(R.string.message_movie_notify).format(
                        result.movieName
                    )
                },
                hasMore = !result.movieName.isNullOrEmpty()
            )
        }
    }
}
