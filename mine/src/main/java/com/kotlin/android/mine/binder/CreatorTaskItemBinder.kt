package com.kotlin.android.mine.binder

import android.os.CountDownTimer
import com.kotlin.android.app.data.entity.mine.NoviceTaskInfo
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.date.timeInterval
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mine.R
import com.kotlin.android.mine.databinding.ItemCreatorTaskLayoutBinding
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 任务中心binder
 */
class CreatorTaskItemBinder(
    val taskInfos: NoviceTaskInfo,
    val isShow: Boolean = false
) :
    MultiTypeBinder<ItemCreatorTaskLayoutBinding>() {
    private var timer: CountDownTimer? = null
    override fun layoutId(): Int {
        return R.layout.item_creator_task_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CreatorTaskItemBinder
    }

    override fun onBindViewHolder(binding: ItemCreatorTaskLayoutBinding, position: Int) {
        binding.bean = taskInfos
        binding.binder = this
        when (taskInfos.status) {
            1L -> {
                binding.tvStatus.let {
                    it.text = getString(R.string.mine_creator_task_noStatus)
                    it.setBackground(
                        colorRes = R.color.white,
                        strokeWidth = 1.dp,
                        strokeColorRes = R.color.color_20a0da,
                        cornerRadius = 14.dpF
                    )
                    it.setTextColorRes(R.color.color_20a0da)
                }
            }
            2L -> {
                binding.tvStatus.let {
                    it.text = getString(R.string.mine_creator_task_status)
                    it.setBackground(
                        colorRes = R.color.color_20a0da,
                        endColorRes = R.color.color_1bafe0,
                        cornerRadius = 14.dpF
                    )
                    it.setTextColorRes(R.color.white)
                }
            }
            3L -> {
                binding.tvStatus.let {
                    it.text = getString(R.string.mine_creator_task_noStart)
                    it.setBackground(
                        colorRes = R.color.white,
                        strokeWidth = 1.dp,
                        strokeColorRes = R.color.color_20a0da,
                        cornerRadius = 14.dpF
                    )
                    it.setTextColorRes(R.color.color_20a0da)
                }
            }
            4L -> {
                binding.tvStatus.let {
                    it.text = getString(R.string.mine_creator_task_finish)
                    it.setBackground(
                        colorRes = R.color.color_20a0da,
                        endColorRes = R.color.color_1bafe0,
                        cornerRadius = 14.dpF
                    )
                    it.setTextColorRes(R.color.white)
                }
            }
        }
        if (isShow) {
            //倒计时
            if (taskInfos.type == 22L) {
                binding.tvTime.visible()
                val end = taskInfos.endTime?.stamp//结束时间
                if (end != null) {
                    if (taskInfos.status == 1L) {
                        val addTimeInterval = end.timeInterval(TimeExt.getNowMills())
                        timer = getCountDownTime(addTimeInterval, binding)
                        if (addTimeInterval > 0L) {
                            timer?.start()
                        } else {
                            timer?.cancel()
                        }
                    } else {
                        binding.tvTime.gone()
                    }
                }
            } else {
                binding.tvTime.gone()
            }
            // 任务描述
            if (taskInfos.details != "") {
                binding.tvDetails.visible()
            } else {
                binding.tvDetails.gone()
            }
        } else {
            // 创作中心-任务中心 不显示 倒计时、任务描述
            binding.tvTime.gone()
            binding.tvDetails.gone()
        }
    }

    /**
     * 倒计时展示
     */
    private fun getCountDownTime(
        time: Long,
        binding: ItemCreatorTaskLayoutBinding
    ): CountDownTimer {
        return object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTime.text = String.format(
                    getString(R.string.mine_creator_timer),
                    ms2HMS(millisUntilFinished)
                )
            }

            override fun onFinish() {
                binding.tvTime.text = getString(R.string.mine_creator_timer_finish)
            }
        }
    }

    fun ms2HMS(time: Long): String {
        var _ms = time
        val HMStime: String
        _ms /= 1000
        val hour = _ms / 3600
        val mint = _ms % 3600 / 60
        val sed = _ms % 60
        var hourStr = hour.toString()
        if (hour < 10) {
            hourStr = "0$hourStr"
        }
        var mintStr = mint.toString()
        if (mint < 10) {
            mintStr = "0$mintStr"
        }
        var sedStr = sed.toString()
        if (sed < 10) {
            sedStr = "0$sedStr"
        }
        HMStime = "$hourStr:$mintStr:$sedStr"
        return HMStime
    }

    override fun onUnBindViewHolder(binding: ItemCreatorTaskLayoutBinding?) {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
        super.onUnBindViewHolder(binding)
    }
}