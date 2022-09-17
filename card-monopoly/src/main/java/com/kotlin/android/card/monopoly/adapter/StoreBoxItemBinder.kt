package com.kotlin.android.card.monopoly.adapter

import android.os.CountDownTimer
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants
import com.kotlin.android.card.monopoly.databinding.ItemStoreBoxBinding
import com.kotlin.android.card.monopoly.ext.getCountDownTime
import com.kotlin.android.app.data.entity.monopoly.Box
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.time.TimeExt
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @desc 卡片商店宝箱列表页面
 * @author zhangjian
 * @date 2020/9/19 17:14
 */
class StoreBoxItemBinder(val data: Box, onBuyClick: ((data: Box, flag: Boolean) -> Unit)) :
    MultiTypeBinder<ItemStoreBoxBinding>() {

    var mOnBuyClick: ((data: Box, flag: Boolean) -> Unit)? = onBuyClick
    override fun layoutId(): Int = R.layout.item_store_box

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is StoreBoxItemBinder && other.data == data
    }

    override fun onBindViewHolder(binding: ItemStoreBoxBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        when (data.type) {
            Constants.STORE_BOX_ACTIVITY -> {
                showActivityBox(binding)
                binding.tvBuy.onClick {
                    mOnBuyClick?.invoke(data, false)
                }
            }
            Constants.STORE_BOX_NORMAL -> {
                showNormalBox(binding)
                binding.tvBuy.onClick {
                    mOnBuyClick?.invoke(data, false)
                }
            }
            Constants.STORE_BOX_LIMIT -> {
                showLimitBox(binding)
            }
            Constants.STORE_BOX_REWARD -> {
                showBoughtBox(binding)
                binding.tvBuy.onClick {
                    mOnBuyClick?.invoke(data, true)
                }
            }
        }
        //购买
        ShapeExt.setShapeCorner2ColorWithColor(binding.tvBuy, getColor(R.color.color_feb12a), 45)
    }

    //展示未打开的宝箱
    private fun showBoughtBox(binding: ItemStoreBoxBinding) {
        setItemMargin(binding)
        binding.boxItemContainer.setPadding(0, 0, 0, 0)
        //购买按钮展示文案改变
        setBuyBtnStyle(binding, true, getString(R.string.str_open))
        //名称单独处理
        setBoxName(binding, String.format(getString(R.string.str_unrevice_box), data.cardBoxName))
        //背景展示
        binding.boxItemContainer.background = null
        //倒计时展示
        binding.ctlHeader.visibility = View.GONE
        //金币
        binding.tvPrice.text = data.price.toString()
        //宝箱的图片
        binding.boxImageView.data = data
    }

    private fun showLimitBox(binding: ItemStoreBoxBinding) {
        setItemMargin(binding)
        binding.boxItemContainer.setPadding(0, 0, 0, 0)
        //背景展示
        binding.boxItemContainer.background = null
        //名称单独处理
        setBoxName(binding, data.cardBoxName)
        //购买按钮不展示
        setBuyBtnStyle(binding = binding, visiable = false)
        //倒计时不展示
        binding.ctlHeader.visibility = View.GONE
        //提示信息展示
        binding.tvInfo.visibility = View.VISIBLE
        //金币
        binding.tvPrice.visibility = View.GONE
        //宝箱的图片
        binding.boxImageView.data = data
    }

    private fun showNormalBox(binding: ItemStoreBoxBinding) {
        setItemMargin(binding)
        binding.boxItemContainer.setPadding(0, 0, 0, 0)
        //名称单独处理
        setBoxName(binding, data.cardBoxName)
        //按钮展示
        setBuyBtnStyle(binding, true, getString(R.string.str_buy))
        //背景展示
        binding.boxItemContainer.background = null
        //倒计时展示
        binding.ctlHeader.visibility = View.GONE
        //金币
        binding.tvPrice.text = data.price.toString()
        //提示信息展示
        binding.tvInfo.visibility = View.GONE
        //金币
        binding.tvPrice.visibility = View.VISIBLE
        //宝箱的图片
        binding.boxImageView.data = data
    }


    private fun showActivityBox(binding: ItemStoreBoxBinding) {
        setItemMargin(binding, 10.dp)
        //背景展示
        ShapeExt.setShapeCorner2Color(binding.boxItemContainer, R.color.color_dbf0f9, 12)
        //活动宝箱左右边距减少
        binding.ctlHeader.setPadding(15.dp, 0, 15.dp, 0)
        binding.boxContentArea.setPadding(15.dp, 0, 15.dp, 0)
        binding.tvDesption.setPadding(15.dp, 0, 15.dp, 15.dp)
        //倒计时展示
        binding.ctlHeader.visibility = View.VISIBLE
        //倒计时
        val mTime = (data.activityEndTime ?: 0) - (TimeExt.getNowMills() / 1000)
        val timer: CountDownTimer = binding.countdown.getCountDownTime(mTime, binding.countdown)
        if (mTime > 0L) {
            timer.start()
        } else {
            timer.cancel()
        }
        //按钮展示
        setBuyBtnStyle(binding, true, getString(R.string.str_buy))
        //宝箱名称
        if (data.remainCount != 0L) {
            setBoxName(binding, "${data.cardBoxName}(${data.remainCount}个)")
        } else {
            setBoxName(binding, data.cardBoxName)
        }
        //宝箱的图片
        binding.boxImageView.data = data
        //金币
        binding.tvPrice.text = data.price.toString()
        //提示信息展示
        binding.tvInfo.visibility = View.GONE
        //金币
        binding.tvPrice.visibility = View.VISIBLE
        //底部的分割线隐藏
        binding.bottomLine.visibility = View.GONE
    }

    private fun setItemMargin(binding: ItemStoreBoxBinding, marginTop: Int = 0) {
        val param = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
        param.topMargin = marginTop
        binding.boxItemContainer.layoutParams = param
    }

    /**
     * 宝箱名称样式
     * @param visiable 是否显示
     * @param str 按钮的文案
     */
    private fun setBoxName(binding: ItemStoreBoxBinding, str: String?) {
        binding.tvName.text = str ?: ""
    }

    /**
     * 购买按钮的样式
     * @param visiable 是否显示
     * @param str 按钮的文案
     */
    private fun setBuyBtnStyle(
        binding: ItemStoreBoxBinding,
        visiable: Boolean? = true,
        str: String? = ""
    ) {
        if (visiable == true) {
            binding.tvBuy.visibility = View.VISIBLE
            binding.tvBuy.text = str
        } else {
            binding.tvBuy.visibility = View.GONE
        }
    }
}