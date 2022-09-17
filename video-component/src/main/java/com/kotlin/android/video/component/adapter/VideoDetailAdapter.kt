package com.kotlin.android.video.component.adapter

import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.video.component.R

/**
 * create by lushan on 2020/10/22
 * description:
 */

const val VIDEO_DETAIL_HAD_SEEN = 0L//已看过
const val VIDEO_DETAIL_HAD_WANNA = 1L//已想看
const val VIDEO_DETAIL_UN_ATTITUDE = -1L//当前用户对电影未表态

const val VIDEO_DETAIL_TICKET = 1L//购票
const val VIDEO_DETAIL_PRESELL = 2L//预售

/**
 * 预告片想看卡片想看按钮背景
 * @param buyStatus  购票状态：1正常购票 2预售 3不可购票
 * @param   attitudeStatus    当前用户对电影的态度：-1未表态 0看过 1想看
 * 1.2 else         0已看过 1已看过
 */
@BindingAdapter(value = ["video_detail_movie_btn_buy_status","video_detail_movie_btn_attuid_status"],requireAll = true)
fun bindMovieBtnBg(view: View, buyStatus: Long, attitudeStatus:Long) {
//buyStatus和其他购票状态是反着的
    when(buyStatus) {
        VIDEO_DETAIL_TICKET -> { //购票
            ShapeExt.setGradientColor(
                    view,
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    R.color.color_20a0da,
                    R.color.color_1bafe0,
                    30)
        }
        VIDEO_DETAIL_PRESELL -> { //预售
            ShapeExt.setGradientColor(
                    view,
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    R.color.color_afd956,
                    R.color.color_c0dc4d,
                    30)
        }
        else->{//不能购票判断当前用户对电影的态度
            when(attitudeStatus){
                VIDEO_DETAIL_HAD_SEEN,VIDEO_DETAIL_HAD_WANNA->{//已看过和已想看
                    ShapeExt.setShapeCorner2Color2Stroke(
                            view,
                            R.color.color_00ffffff,
                            30,
                            R.color.color_20a0da,
                            1
                    )
                }
                else->{
                    ShapeExt.setGradientColor(
                            view,
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            R.color.color_20a0da,
                            R.color.color_1bafe0,
                            30)
                }
            }

        }
    }
}

/**
 * 影片想看按钮文本
 */
@BindingAdapter(value = ["video_detail_movie_text_buy_status","video_detail_movie_text_attuid_status"],requireAll = true)
fun bindMovieBtnText(view: TextView, buyStatus: Long, attitudeStatus:Long) {
    when(buyStatus){
        VIDEO_DETAIL_TICKET -> {//购票
            view.setText(R.string.comm_movie_btn_ticket)
            view.setTextColorRes(R.color.color_ffffff)
            view.setCompoundDrawablesAndPadding()
        }
        VIDEO_DETAIL_PRESELL ->{//预售
            view.setText(R.string.comm_movie_btn_presell)
            view.setTextColorRes(R.color.color_ffffff)
            view.setCompoundDrawablesAndPadding()
        }
        else ->{
            when(attitudeStatus){
                VIDEO_DETAIL_HAD_SEEN->{//已看过
                    view.setText(R.string.video_had_seen)
                    view.setTextColorRes(R.color.color_20a0da)
                    view.setCompoundDrawablesAndPadding(
                            leftResId = R.drawable.ic_checkb,
                            padding = 3
                    )
                }
                VIDEO_DETAIL_HAD_WANNA->{//已想看
                    view.setText(R.string.comm_movie_btn_want_see)
                    view.setTextColorRes(R.color.color_20a0da)
                    view.setCompoundDrawablesAndPadding(
                            leftResId = R.drawable.ic_checkb,
                            padding = 3
                    )
                }
                else->{
                    view.setText(R.string.comm_movie_btn_want_see)
                    view.setTextColorRes(R.color.color_ffffff)
                    view.setCompoundDrawablesAndPadding()
                }

            }
        }
    }

}
