package com.kotlin.android.ticket.order.component

import android.os.CountDownTimer

/**
 * create by lushan on 2020/9/16
 * description: 倒计时
 */
class TicketCountDownTimer(remainTime: Long, countDownInterval: Long = 1000L, var listener: ((String, String, Boolean) -> Unit)? = null) : CountDownTimer(remainTime, countDownInterval) {

    override fun onTick(remain: Long) {
        val min = remain / (60L * 1000L)
        val sec = (remain / 1000L) % 60
        listener?.invoke(getTimeWithZero(min), getTimeWithZero(sec), false)

    }

    private fun getTimeWithZero(time: Long): String {
        return if (time < 10L) {
            "0$time"
        } else {
            time.toString()
        }
    }

    override fun onFinish() {
        listener?.invoke("00", "00", true)
    }
}