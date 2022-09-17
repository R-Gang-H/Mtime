package com.kotlin.android.card.monopoly.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.Nullable
import com.kotlin.android.card.monopoly.R
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_CARD
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_SUIT
import com.kotlin.android.card.monopoly.constants.Constants.TYPE_TOOLS
import com.kotlin.android.ktx.ext.dimension.dp
import org.jetbrains.anko.alignParentRight

/**
 * @desc 拍卖行选择卡,套装,道具卡view
 * @author zhangjian
 * @date 2021-05-12 15:14:33
 */
class AuctionCardFilterView : LinearLayout {

    private var ivCard: ImageView? = null
    private var ivSuit: ImageView? = null
    private var ivTools: ImageView? = null

    var action: ((type: Long) -> Unit)? = null

    var type:Long? = TYPE_CARD


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
        val view = LayoutInflater.from(context).inflate(R.layout.view_auc_filtert, null, false)

        ivCard = view.findViewById(R.id.ivCard)
        ivSuit = view.findViewById(R.id.ivSuit)
        ivTools = view.findViewById(R.id.ivTools)

        addView(view)
        setState(TYPE_CARD)

        ivCard?.setOnClickListener {
            setState(TYPE_CARD)
        }

        ivSuit?.setOnClickListener {
            setState(TYPE_SUIT)
        }

        ivTools?.setOnClickListener {
            setState(TYPE_TOOLS)
        }

    }

    /**
     * @param type 选择的类型
     * @param flag 是否只是切换样式
     */
    fun setState(type: Long,flag:Boolean? = true) {
        if(flag == true){
            action?.invoke(type)
        }
        this.type = type
        when (type) {
            //卡
            TYPE_CARD -> {
                setImageResourceByType(isSelectCard = true)
            }
            TYPE_SUIT -> {
                setImageResourceByType(isSelectSuit = true)
            }
            TYPE_TOOLS -> {
                setImageResourceByType(isSelectTools = true)
            }
        }
    }

    fun setImgShowType(type: Long, show: Boolean? = true) {
        when (type) {
            //卡
            TYPE_CARD -> {
                ivCard?.visibility = if (show == true) {
                    VISIBLE
                } else {
                    GONE
                }
            }
            TYPE_SUIT -> {
                ivSuit?.visibility = if (show == true) {
                    VISIBLE
                } else {
                    GONE
                }
            }
            TYPE_TOOLS -> {
                ivTools?.visibility = if (show == true) {
                    VISIBLE
                } else {
                    GONE
                }
                //道具卡隐藏修改下布局,套装居右
                if(show == false){
                    val param = RelativeLayout.LayoutParams(23.dp,23.dp)
                    param.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE)
                    param.alignParentRight()
                    ivSuit?.layoutParams = param
                }

            }
        }
    }


    /**
     * 设置image的资源文件
     */
    private fun setImageResourceByType(
        isSelectCard: Boolean? = false,
        isSelectSuit: Boolean? = false,
        isSelectTools: Boolean? = false
    ) {
        //卡
        if (isSelectCard == true) {
            ivCard?.setImageResource(R.drawable.ic_auc_card_selected)
        } else {
            ivCard?.setImageResource(R.drawable.ic_auc_card_unselect)
        }
        //套装
        if (isSelectSuit == true) {
            ivSuit?.setImageResource(R.drawable.ic_auc_suit_selected)
        } else {
            ivSuit?.setImageResource(R.drawable.ic_auc_suit_unselect)
        }
        //道具
        if (isSelectTools == true) {
            ivTools?.setImageResource(R.drawable.ic_auc_tools_selected)
        } else {
            ivTools?.setImageResource(R.drawable.ic_auc_tools_unselect)
        }


    }


}