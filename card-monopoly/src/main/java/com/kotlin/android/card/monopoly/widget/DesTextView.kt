package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.Nullable
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.toSPF
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.user.UserManager
import java.text.DecimalFormat

/**
 * @desc 拍卖行信息展示view(左边描述,右边信息)
 * @author zhangjian
 * @date 2021-05-11 16:01:19
 */
class DesTextView : LinearLayout {

    private var tvLeft: TextView? = null
    private var ivLeft: ImageView? = null
    private var tvRight: TextView? = null


    constructor(context: Context) : this(context, null)

    constructor(context: Context, @Nullable attr: AttributeSet?) : this(context, attr, 0)

    constructor(context: Context, @Nullable attr: AttributeSet?, def: Int) : super(
        context,
        attr,
        def
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = LayoutInflater.from(context).inflate(R.layout.view_des_textview, null)
        tvLeft = view.findViewById(R.id.tvLeft)
        ivLeft = view.findViewById(R.id.ivLeft)
        tvRight = view.findViewById(R.id.tvRight)
        val param = LayoutParams(LayoutParams.MATCH_PARENT, 20.dp)
        view.layoutParams = param
        addView(view)
    }


    /**
     * 设置内容
     */
    fun setContentData(data: DesTextBean) {
        //设置左边的描述信息
        if (data.leftImageShow) {
            tvLeft?.visibility = View.GONE
            ivLeft?.visibility = View.VISIBLE
            setIvLeftRes(data)
        } else {
            tvLeft?.visibility = View.VISIBLE
            ivLeft?.visibility = View.GONE
            setTvLeftContent(data)
        }
        //设置右边的说明信息
        setTvRightContent(data)
    }


    /**
     * 设置左边的文案样式
     */
    private fun setTvLeftContent(data: DesTextBean) {
        tvLeft?.apply {
            if (data.leftBoldStyle) {
                this.setTypeface(null, Typeface.BOLD)
            } else {
                this.setTypeface(null, Typeface.NORMAL)
            }
            this.text = data.leftString
            this.setTextColor(resources.getColor(data.leftColor, null))
        }
    }

    /**
     * 设置左边的图片
     */
    private fun setIvLeftRes(data: DesTextBean) {
        ivLeft?.apply {
            if (data.isOwner) {
                this.setImageResource(R.drawable.ic_flag)
            } else {
                this.setImageResource(R.drawable.ic_hammer)
            }

        }
    }

    /**
     * 设置右边的说明信息
     */
    private fun setTvRightContent(data: DesTextBean) {
        tvRight?.apply {
            if (data.leftBoldStyle) {
                this.setTypeface(null, Typeface.BOLD)
            } else {
                this.setTypeface(null, Typeface.NORMAL)
            }
            this.text = data.rightString
            this.textSize = data.rightStrSize
        }
        tvRight?.setTextColor(resources.getColor(data.rightColor, null))
    }


    data class DesTextBean(
        @ColorRes
        var leftColor: Int = 0,
        @DrawableRes
        var leftImageRes: Int = 0,
        @ColorRes
        var rightColor: Int = 0,
        var leftString: String? = null,
        var leftBoldStyle: Boolean = false,
        var leftImageShow: Boolean = false,
        var rightString: String? = null,
        var rightStrSize: Float = 42.toSPF,
        var isOwner: Boolean = false
    )


    //设置名称的dataBean
    fun setNameDataBean(type: Long?, cardName: String?): DesTextBean {
        val strType = when (type) {
            1L -> getString(R.string.type_card)
            2L -> getString(R.string.type_tools)
            else -> getString(R.string.type_suit)
        }
        val nameDes = DesTextBean()
        nameDes.apply {
            rightColor = R.color.color_1d2736
            leftColor = R.color.color_1d2736
            leftBoldStyle = true
            leftString = strType
            rightStrSize = 36.toSPF
            rightString = cardName ?: ""
        }
        return nameDes
    }

    //设置起拍价的dataBean
    fun setStartDataBean(bidPrice: Long, startPrice: Long, userId: Long): DesTextBean {
        val data = DesTextBean()
        data.apply {
            rightColor = R.color.color_feb12a
            leftColor = R.color.color_4e5e73
            leftString = getString(R.string.start_price)
            rightString = if (bidPrice != 0L) {
                formatPrice(bidPrice)
            } else {
                formatPrice(startPrice)
            }
            leftImageShow = userId != 0L
            isOwner = userId == UserManager.instance.userId
        }
        return data
    }

    //设置一口价的dataBean
    fun setFixDataBean(fixPrice: Long): DesTextBean {
        val data = DesTextBean()
        data.apply {
            rightColor = R.color.color_36c096
            leftColor = R.color.color_4e5e73
            leftString = getString(R.string.fix_price)
            rightString = formatPrice(fixPrice)
        }
        return data
    }

    /**
     * 格式化价格
     */
    private fun formatPrice(price: Long): String {
        val df = DecimalFormat("#,###")
        return df.format(price)
    }

}