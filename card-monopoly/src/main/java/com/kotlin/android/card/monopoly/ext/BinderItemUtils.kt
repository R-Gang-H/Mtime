package com.kotlin.android.card.monopoly.ext

import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.mtime.ktx.getString
import java.text.DecimalFormat
import java.util.*

/**
 * 拍卖行格式化 价格
 */
fun View.formatPrice(price: Long): String {
    val df = DecimalFormat("#,###")
    return df.format(price)
}

/**
 * 倒计时展示
 */
fun View.getCountDownTime(time: Long, view: TextView): CountDownTimer {
    return object : CountDownTimer(time * 1000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            view.text = String.format(getString(R.string.count_down_time), getStringTime(millisUntilFinished / 1000))
        }

        override fun onFinish() {
            view.text = getString(R.string.str_finish)
        }
    }
}

/**
 * 倒计时时间格式化
 */
fun getStringTime(time: Long): String {
    val hour:Int = (time / 3600).toInt()
    val min = (time % 3600 / 60).toInt()
    val second = (time % 60).toInt()
    return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, min, second)
}

/**
 * 通过判断类型展示文案
 */
fun setTextBySuitType(type:Long?):String{
    return if(type == Constants.TYPE_SUIT){
        getString(R.string.str_retrieve_suit)
    }else{
        getString(R.string.str_retrieve_card)
    }
}
