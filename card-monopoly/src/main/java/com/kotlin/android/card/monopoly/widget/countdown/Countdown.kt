package com.kotlin.android.card.monopoly.widget.countdown

import android.os.CountDownTimer

/**
 * 倒计时：
 *
 * Created on 2020/10/19.
 *
 * @author o.s
 */
class Countdown(
        remainingTime: Long,
        countDownInterval: Long = 500L,
        private val complete: () -> Unit,
        private val tick: (time: Time) -> Unit
) : CountDownTimer(remainingTime, countDownInterval) {

    private val time by lazy { Time() }

    /**
     * Callback fired when the time is up.
     */
    override fun onFinish() {
        complete.invoke()
        cancel()
    }

    /**
     * Callback fired on regular interval.
     * @param millisUntilFinished The amount of time until finished.
     */
    override fun onTick(millisUntilFinished: Long) {
        handleMillisUntil(millisUntilFinished)
        tick.invoke(time)
    }

    private fun handleMillisUntil(millisUntilFinished: Long) {
        val ss = millisUntilFinished / 1000L // 总秒
        val mm = ss / 60L // 总分
        val h = mm / 60L // 时
        val m = mm % 60L // 分
        val s = ss % 60L // 秒

        time.apply {
            totalMinute = mm
            totalSecond = ss

            hour = if (h < 10) {
                "0$h"
            } else {
                h.toString()
            }
            minute = if (m < 10) {
                "0$m"
            } else {
                m.toString()
            }
            second = if (s < 10) {
                "0$s"
            } else {
                s.toString()
            }
        }
    }

    data class Time(
            var hour: String = "00",
            var minute: String = "00",
            var second: String = "00",
            var totalMinute: Long = 0,
            var totalSecond: Long = 0,
    )
}