package com.mtime.widgets;

import com.mtime.common.utils.LogWriter;

import java.lang.reflect.Field;

public abstract class TimerCountDown extends TimerCount {
    public Long                CLOCK_PAY_END_TIME;
    // public static final String COLON = ":";
    public static final String COLONT_TO       = "0";
    private final long         TWO_MINUTES     = 2 * 60 * 1000L; // 2分钟
    private final long         FIVE_MINUTES    = 5 * 60 * 1000L; // 5分钟
    private boolean            colorFlag       = true;          // 闪动，变换标记
    private boolean            hasChangedSpeed = false;         // 改变回调速度的flag，控制只执行一次
    private static final long  CALLBACK_SPEED  = 500L;          // 回调速度（0.5秒回调一次）
    private Object             tag             = null;

    public Object getTag() {
        return this.tag;
    }

    public void setTag(final Object tag) {
        this.tag = tag;
    }

    public TimerCountDown(final long millisInFuture) {
        super(millisInFuture, 1000);
    }

    @Override
    public void onFinish() {
        this.onTimeFinish();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void onTick(final long millisUntilFinished) {
        this.CLOCK_PAY_END_TIME = millisUntilFinished;
        final long s = millisUntilFinished / 60000L; // 分;

        final String minute;
        if (s < 10) {
            minute = "0" + s;
        }
        else {
            minute = String.valueOf(s);
        }
        final long m = (millisUntilFinished / 1000L) % 60L; // 秒
        final String sec;
        final String time;
        if (m < 10) {
            sec = TimerCountDown.COLONT_TO + m;
        }
        else {
            sec = String.valueOf(m);
        }
        time = minute + ":" + sec;

        // 开始闪烁
        if (millisUntilFinished <= this.FIVE_MINUTES) {
            // 如果剩余时间小于5分钟则开始闪动（红白颜色交替变化）
            this.onTickCallBackTo(time, minute, sec, this.colorFlag);
            this.colorFlag = !this.colorFlag;
        }
        else {
            this.onTickCallBack(time, minute, sec);
        }

        if (!this.hasChangedSpeed && (millisUntilFinished <= this.TWO_MINUTES)) {
            // 如果剩余时间小于2分钟则闪动频率加快（每0.5秒回调一次）
            this.hasChangedSpeed = true; // 只执行一次
            setCountdownInterval(TimerCountDown.CALLBACK_SPEED);
        }
    }

    /**
     * 显示倒计时的时间
     * 
     * @param value
     *            时间值
     */
    public abstract void onTickCallBack(final String value, final String min, final String sec);

    /**
     * 显示倒计时的时间
     * 
     * @param value
     *            时间值
     */
    public abstract void onTickCallBackTo(final String value, final String min, final String sec, boolean colorFlag);

    /**
     * 时间到了
     */
    public abstract void onTimeFinish();
}
